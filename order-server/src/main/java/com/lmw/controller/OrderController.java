package com.lmw.controller;


import com.lmw.constant.MessageConstant;
import com.lmw.dto.OrderItemDTO;
import com.lmw.dto.PaymentDTO;
import com.lmw.entity.Order;
import com.lmw.enums.PaymentStatus;
import com.lmw.exception.InsufficientStockException;
import com.lmw.exception.PaymentFailureException;
import com.lmw.exception.ResourceNotFoundException;
import com.lmw.result.Result;
import com.lmw.service.OrderService;
import com.lmw.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/orders")
public class OrderController {

    @Resource
    private OrderService orderService;

    @PostMapping("/create")
    public Result<Map<String, Object>> createOrder(@RequestParam int userId, @RequestBody List<OrderItemDTO> items) {
        try {
            Order order = orderService.createOrder(userId, items);
            // 数据脱敏
            Map<String, Object> map = new HashMap<>();
            map.put("orderId", order.getOrderId());
            return Result.success(MessageConstant.ORDER_CREATE_SUCCESS,map);
        } catch (InsufficientStockException | ResourceNotFoundException e) {
            log.error("订单创建失败：{}", e.getMessage());
            return Result.error(MessageConstant.ORDER_CREATE_FAILURE + e.getMessage());
        }
    }

    @PutMapping("/{orderId}/pay")
    public Result<Map<String, Object>> payOrder(@PathVariable int orderId, @RequestBody PaymentDTO paymentDTO) {
        Map<String, Object> map = new HashMap<>();
        map.put("orderId", orderId);
        try {
            orderService.payOrder(orderId, paymentDTO);
            map.put("payStatus", PaymentStatus.SUCCESS);
            return Result.success(MessageConstant.PAYMENT_SUCCESS, map);
        } catch (InsufficientStockException | ResourceNotFoundException | PaymentFailureException e) {
            log.error("支付失败：{}", e.getMessage());
            map.put("payStatus", PaymentStatus.FAILED);
            return Result.error(MessageConstant.PAYMENT_FAILURE, map);
        }
    }

    @PutMapping("/{orderId}/cancel")
    public Result<Map<String, Object>> cancelOrder(@PathVariable int orderId, @RequestParam Integer userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("orderId", orderId);
        try {
            orderService.cancelOrder(orderId, userId);
            return Result.success(MessageConstant.ORDER_CANCEL_SUCCESS, map);
        } catch (ResourceNotFoundException e) {
            log.error("订单取消失败：{}", e.getMessage());
            return Result.error(MessageConstant.ORDER_CANCEL_FAILURE + e.getMessage());
        }
    }

    @GetMapping("/{orderId}")
    public Result<OrderVO> getOrderById(@PathVariable int orderId) {
        try {
            OrderVO orderVO = orderService.getOrderById(orderId);
            return Result.success(orderVO);
        } catch (ResourceNotFoundException e) {
            return Result.error(MessageConstant.ORDER_NOT_FOUND + e.getMessage());
        }
    }

}
