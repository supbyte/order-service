package com.lmw.service;


import com.lmw.entity.Order;
import com.lmw.entity.OrderItem;
import com.lmw.exception.InsufficientStockException;
import com.lmw.exception.ResourceNotFoundException;

import java.util.List;

public interface OrderService {

    Order createOrder(int userId, List<OrderItem> items) throws InsufficientStockException, ResourceNotFoundException;

    void payOrder(int orderId, String paymentMethod) throws InsufficientStockException, ResourceNotFoundException;

    void cancelOrder(int orderId) throws ResourceNotFoundException;

    Order getOrderById(int orderId) throws ResourceNotFoundException;

    List<Order> getOrdersByUserId(int userId) throws ResourceNotFoundException;
}
