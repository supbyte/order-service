package com.lmw.entity;

import com.lmw.enums.PaymentMethod;
import com.lmw.enums.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class Payment {
    private Integer paymentId;
    private Integer orderId;
    private BigDecimal amount;
    private PaymentStatus status;
    private PaymentMethod paymentMethod;
    private Timestamp createdAt;
}
