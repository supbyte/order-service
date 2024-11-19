package com.lmw.entity;

import com.lmw.enums.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
public class Order {
    private Integer orderId;
    private Integer userId;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private Timestamp createdAt;
    private List<OrderItem> orderItems;
}
