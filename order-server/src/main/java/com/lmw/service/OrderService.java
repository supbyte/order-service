package com.lmw.service;


import com.lmw.dto.OrderItemDTO;
import com.lmw.dto.PaymentDTO;
import com.lmw.entity.Order;
import com.lmw.entity.OrderItem;
import com.lmw.entity.Payment;
import com.lmw.exception.InsufficientStockException;
import com.lmw.exception.ResourceNotFoundException;
import com.lmw.vo.OrderVO;

import java.util.List;

public interface OrderService {

    Order createOrder(int userId, List<OrderItemDTO> items) throws InsufficientStockException, ResourceNotFoundException;

    void payOrder(int orderId, PaymentDTO paymentDTO) throws InsufficientStockException, ResourceNotFoundException;

    void cancelOrder(int orderId, int userId) throws ResourceNotFoundException;

    OrderVO getOrderById(int orderId) throws ResourceNotFoundException;

    List<OrderVO> getOrdersByUserId(int userId) throws ResourceNotFoundException;

    void handlePaymentCallback(int orderId, Payment payment) throws InsufficientStockException, ResourceNotFoundException;
}
