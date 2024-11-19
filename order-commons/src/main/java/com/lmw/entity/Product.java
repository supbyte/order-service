package com.lmw.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class Product {
    private Integer productId;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private Timestamp createdAt;
}
