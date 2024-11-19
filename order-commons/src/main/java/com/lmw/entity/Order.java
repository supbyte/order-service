package com.lmw.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
public class Order {
    private Integer orderId;
    private Integer userId;
    private BigDecimal totalPrice;
    private String status;
    private Timestamp createdAt;
    private List<OrderItem> orderItems;
}
