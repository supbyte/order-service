package com.lmw.service.impl;

import com.lmw.entity.Order;
import com.lmw.entity.OrderItem;
import com.lmw.entity.Product;
import com.lmw.entity.User;
import com.lmw.enums.OrderStatus;
import com.lmw.exception.InsufficientStockException;
import com.lmw.exception.ResourceNotFoundException;
import com.lmw.mapper.OrderItemMapper;
import com.lmw.mapper.OrderMapper;
import com.lmw.mapper.ProductMapper;
import com.lmw.mapper.UserMapper;
import com.lmw.service.OrderService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
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

    @Override
    @Transactional
    public Order createOrder(int userId, List<OrderItem> items) throws InsufficientStockException, ResourceNotFoundException {
        String lockKey = "order_lock_" + userId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            lock.lock(5, TimeUnit.SECONDS); // 获取锁，超时时间为5秒

            // 检查商品库存
            for (OrderItem item : items) {
                Product product = productMapper.getProductById(item.getProductId());
                if (product == null) {
                    throw new ResourceNotFoundException("Product not found");
                }
                if (product.getStock() < item.getQuantity()) {
                    throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
                }
            }

            // 创建订单
            Order order = new Order();
            order.setUserId(userId);
            order.setTotalPrice(calculateTotalPrice(items));
            order.setStatus(OrderStatus.PENDING);
            orderMapper.createOrder(order);

            // 创建订单详情
            for (OrderItem item : items) {
                item.setOrderId(order.getOrderId());
                orderItemMapper.createOrderItem(item);
            }

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
    @Transactional
    public void payOrder(int orderId, String paymentMethod) throws InsufficientStockException, ResourceNotFoundException {
        String lockKey = "order_lock_" + orderId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            lock.lock(5, TimeUnit.SECONDS); // 获取锁，超时时间为5秒

            Order order = orderMapper.getOrderById(orderId);
            if (order == null) {
                throw new ResourceNotFoundException("Order not found");
            }

            if (!order.getStatus().equals(OrderStatus.PENDING)) {
                throw new InsufficientStockException("Order is not in pending state");
            }

            User user = userMapper.getUserById(order.getUserId());
            if (user == null) {
                throw new ResourceNotFoundException("User not found");
            }

            if (paymentMethod.equals("BALANCE") && user.getBalance().compareTo(order.getTotalPrice()) < 0) {
                throw new InsufficientStockException("Insufficient balance");
            }

            // 更新用户余额
            if (paymentMethod.equals("BALANCE")) {
                user.setBalance(user.getBalance().subtract(order.getTotalPrice()));
                userMapper.updateUserBalance(user);
            }

            // 更新订单状态
            order.setStatus(OrderStatus.PAID);
            orderMapper.updateOrderStatus(order);

            // 减少商品库存
            for (OrderItem orderItem : orderItemMapper.getOrderItemsByOrderId(order.getOrderId())) {
                Product product = productMapper.getProductById(orderItem.getProductId());
                product.setStock(product.getStock() - orderItem.getQuantity());
                productMapper.updateProductStock(product);
            }
        } finally {
            lock.unlock(); // 释放锁
        }
    }

    @Override
    @Transactional
    public void cancelOrder(int orderId) throws ResourceNotFoundException {
        Order order = orderMapper.getOrderById(orderId);
        if (order == null) {
            throw new ResourceNotFoundException("Order not found");
        }

        if (!order.getStatus().equals(OrderStatus.PENDING)) {
            throw new InsufficientStockException("Order is not in pending state");
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
    public Order getOrderById(int orderId) throws ResourceNotFoundException {
        Order order = orderMapper.getOrderById(orderId);
        if (order == null) {
            throw new ResourceNotFoundException("Order not found");
        }
        return order;
    }

    @Override
    public List<Order> getOrdersByUserId(int userId) throws ResourceNotFoundException {
        List<Order> orders = orderMapper.getOrdersByUserId(userId);
        if (orders.isEmpty()) {
            throw new ResourceNotFoundException("No orders found for user");
        }
        return orders;
    }
}
