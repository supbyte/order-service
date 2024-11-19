package com.lmw.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class Payment {
    private Integer paymentId;
    private Integer orderId;
    private BigDecimal amount;
    private String status;
    private String paymentMethod;
    private Timestamp createdAt;
}
