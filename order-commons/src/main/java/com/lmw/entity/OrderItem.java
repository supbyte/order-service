package com.lmw.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderItem implements Serializable {
    private Integer itemId;
    private Integer orderId;
    private Integer productId;
    private Integer quantity;
    private BigDecimal price;
}
