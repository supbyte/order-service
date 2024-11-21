package com.lmw.service.impl;

import com.lmw.constant.MessageConstant;
import com.lmw.dto.OrderItemDTO;
import com.lmw.dto.PaymentDTO;
import com.lmw.entity.*;
import com.lmw.enums.OrderStatus;
import com.lmw.enums.PaymentMethod;
import com.lmw.enums.PaymentStatus;
import com.lmw.exception.InsufficientStockException;
import com.lmw.exception.InvalidOrderStateException;
import com.lmw.exception.PaymentFailureException;
import com.lmw.exception.ResourceNotFoundException;
import com.lmw.mapper.*;
import com.lmw.service.OrderService;
import com.lmw.vo.OrderItemVO;
import com.lmw.vo.OrderVO;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderItemMapper orderItemMapper;

    @Resource
    private ProductMapper productMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private PaymentMapper paymentMapper;

    @Override
    @Transactional
    public Order createOrder(int userId, List<OrderItemDTO> items) throws InsufficientStockException, ResourceNotFoundException {
        String lockKey = "order_lock_" + userId;    // 只能防止重复下单
        RLock lock = redissonClient.getLock(lockKey);

        try {
            lock.lock(5, TimeUnit.SECONDS); // 获取锁，超时时间为5秒

            List<OrderItem> orderItems = new ArrayList<>();
            for (OrderItemDTO item : items) {
                Product product = productMapper.getProductById(item.getProductId());
                // 检查商品是否存在
                if (product == null) {
                    throw new ResourceNotFoundException(MessageConstant.PRODUCT_NOT_FOUND);
                }
                // 检查商品库存
                if (product.getStock() < item.getQuantity()) {
                    throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
                }

                // 组装OrderItem
                OrderItem orderItem = new OrderItem();
                BeanUtils.copyProperties(item, orderItem);
                orderItem.setPrice(product.getPrice());

                orderItems.add(orderItem);
            }

            // 创建订单
            Order order = new Order();
            order.setUserId(userId);
            order.setTotalPrice(calculateTotalPrice(orderItems));   // 计算订单总金额
            order.setStatus(OrderStatus.PENDING);
            order.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            orderMapper.createOrder(order);

            // 创建订单详情
            for (OrderItem item : orderItems) {
                item.setOrderId(order.getOrderId());
                orderItemMapper.createOrderItem(item);
            }

            order.setOrderItems(orderItems);

            return order;
        } finally {
            lock.unlock(); // 释放锁
        }
    }

    private BigDecimal calculateTotalPrice(List<OrderItem> items) {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : items) {
            total = total.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        return total;
    }

    @Override
    @Transactional(noRollbackFor = PaymentFailureException.class)
    public void payOrder(int orderId, PaymentDTO paymentDTO) throws InsufficientStockException, ResourceNotFoundException {
        String lockKey = "order_lock_" + orderId;   // 只能防止重复支付
        RLock lock = redissonClient.getLock(lockKey);

        try {
            lock.lock(5, TimeUnit.SECONDS); // 获取锁，超时时间为5秒

            Order order = orderMapper.getOrderById(orderId);
            // 订单是否存在 || 订单是否属于该用户
            if (order == null || !Objects.equals(order.getUserId(), paymentDTO.getUserId())) {
                throw new ResourceNotFoundException(MessageConstant.ORDER_NOT_FOUND);
            }

            User user = userMapper.getUserById(order.getUserId());
            // 检查用户是否存在
            if (user == null) {
                throw new ResourceNotFoundException(MessageConstant.USER_NOT_FOUND);
            }

            // 检查订单状态
            if (!order.getStatus().equals(OrderStatus.PENDING)) {
                throw new InvalidOrderStateException(MessageConstant.ORDER_STATUS_ERROR);
            }

            // 模拟发起支付
            Payment payment = new Payment();
            payment.setOrderId(orderId);
            payment.setAmount(order.getTotalPrice());
            payment.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            payment.setPaymentMethod(PaymentMethod.BALANCE);

            // 检查余额是否足够
            if (paymentDTO.getPaymentMethod() == PaymentMethod.BALANCE && user.getBalance().compareTo(order.getTotalPrice()) < 0) {
                throw new InsufficientStockException("Insufficient balance");
            }

            // 模拟支付成功
            payment.setStatus(PaymentStatus.SUCCESS);
            // 模拟支付失败
            //payment.setStatus(PaymentStatus.FAILED);

            // 保存支付记录
            paymentMapper.createPayment(payment);

            // 模拟支付回调
            handlePaymentCallback(orderId, payment);
        } finally {
            lock.unlock(); // 释放锁
        }
    }

    @Override
    @Transactional
    public void handlePaymentCallback(int orderId, Payment payment) throws InsufficientStockException, ResourceNotFoundException {
        Order order = orderMapper.getOrderById(orderId);

        User user = userMapper.getUserById(order.getUserId());

        PaymentStatus paymentStatus = payment.getStatus();

        if (paymentStatus == PaymentStatus.SUCCESS) {
            // 支付成功，更新订单状态
            order.setStatus(OrderStatus.PAID);
            orderMapper.updateOrderStatus(order);

            // 如果是余额支付则更新用户余额
            if (payment.getPaymentMethod() == PaymentMethod.BALANCE) {
                user.setBalance(user.getBalance().subtract(order.getTotalPrice()));
                userMapper.updateUserBalance(user);
            }

            // 减少商品库存
            for (OrderItem orderItem : orderItemMapper.getOrderItemsByOrderId(order.getOrderId())) {
                Product product = productMapper.getProductById(orderItem.getProductId());
                product.setStock(product.getStock() - orderItem.getQuantity());
                productMapper.updateProductStock(product);
            }
        } else if (paymentStatus == PaymentStatus.FAILED) {
            // 支付失败则取消订单
            cancelOrder(orderId, user.getUserId());
            throw new PaymentFailureException(MessageConstant.PAYMENT_FAILURE);
        } else {
            throw new InsufficientStockException("Unknown payment status: " + paymentStatus);
        }
    }

    @Override
    @Transactional
    public void cancelOrder(int orderId, int userId) throws ResourceNotFoundException {
        Order order = orderMapper.getOrderById(orderId);
        // 订单是否存在 || 订单是否属于该用户
        if (order == null || order.getUserId() != userId) {
            throw new ResourceNotFoundException(MessageConstant.ORDER_NOT_FOUND);
        }

        // 检查订单状态
        if (!order.getStatus().equals(OrderStatus.PENDING)) {
            throw new InvalidOrderStateException(MessageConstant.ORDER_STATUS_ERROR);
        }

        // 更新订单状态
        order.setStatus(OrderStatus.CANCELLED);
        orderMapper.updateOrderStatus(order);

        // 恢复商品库存
        for (OrderItem orderItem : orderItemMapper.getOrderItemsByOrderId(order.getOrderId())) {
            Product product = productMapper.getProductById(orderItem.getProductId());
            product.setStock(product.getStock() + orderItem.getQuantity());
            productMapper.updateProductStock(product);
        }
    }

    @Override
    public OrderVO getOrderById(int orderId) throws ResourceNotFoundException {
        Order order = orderMapper.getOrderById(orderId);
        if (order == null) {
            throw new ResourceNotFoundException(MessageConstant.ORDER_NOT_FOUND);
        }

        // 封装VO对象
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        List<OrderItemVO> items = orderItemMapper.listOrderItemVOByOrderId(orderId);
        orderVO.setOrderItems(items);

        return orderVO;
    }

    @Override
    public List<OrderVO> getOrdersByUserId(int userId) throws ResourceNotFoundException {
        User user = userMapper.getUserById(userId);
        // 检查用户是否存在
        if (user == null) {
            throw new ResourceNotFoundException(MessageConstant.USER_NOT_FOUND);
        }

        List<OrderVO> orderVOList = orderMapper.listOrderVOByUserId(userId);
        // 封装VO对象
        orderVOList.forEach(orderVO -> {
            List<OrderItemVO> items = orderItemMapper.listOrderItemVOByOrderId(orderVO.getOrderId());
            orderVO.setOrderItems(items);
        });

        return orderVOList;
    }
}
