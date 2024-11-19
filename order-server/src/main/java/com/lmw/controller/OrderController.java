package com.lmw.controller;


import com.lmw.entity.Order;
import com.lmw.entity.OrderItem;
import com.lmw.exception.InsufficientStockException;
import com.lmw.exception.ResourceNotFoundException;
import com.lmw.result.Result;
import com.lmw.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/orders")
@Api(tags = "订单服务接口")
public class OrderController {

    @Resource
    private OrderService orderService;

    @PostMapping
    @ApiOperation("⽤户请求下单")
    public Result<Order> createOrder(@RequestParam int userId, @RequestBody List<OrderItem> items) {
        try {
            Order order = orderService.createOrder(userId, items);
            return Result.success(order);
        } catch (InsufficientStockException | ResourceNotFoundException e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{orderId}/pay")
    @ApiOperation("⽤户请求⽀付订单")
    public Result<String> payOrder(@PathVariable int orderId, @RequestParam String paymentMethod) {
        try {
            orderService.payOrder(orderId, paymentMethod);
            return Result.success();
        } catch (InsufficientStockException | ResourceNotFoundException e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{orderId}/cancel")
    @ApiOperation("⽤户请求取消订单")
    public Result<String> cancelOrder(@PathVariable int orderId) {
        try {
            orderService.cancelOrder(orderId);
            return Result.success();
        } catch (ResourceNotFoundException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{orderId}")
    @ApiOperation("查询订单")
    public Result<Order> getOrderById(@PathVariable int orderId) {
        try {
            Order order = orderService.getOrderById(orderId);
            return Result.success(order);
        } catch (ResourceNotFoundException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    @ApiOperation("⽤户请求查询订单")
    public Result<List<Order>> getOrdersByUserId(@PathVariable int userId) {
        try {
            List<Order> orders = orderService.getOrdersByUserId(userId);
            return Result.success(orders);
        } catch (ResourceNotFoundException e) {
            return Result.error(e.getMessage());
        }
    }
}
