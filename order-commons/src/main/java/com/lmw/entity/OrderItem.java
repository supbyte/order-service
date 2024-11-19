package com.lmw.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItem {
    private Integer itemId;
    private Integer orderId;
    private Integer productId;
    private Integer quantity;
    private BigDecimal price;
}
