package com.lmw.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class Product implements Serializable {
    private Integer productId;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private Timestamp createdAt;
}
