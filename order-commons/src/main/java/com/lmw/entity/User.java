package com.lmw.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class User implements Serializable {
    private Integer userId;
    private String username;
    private String email;
    private BigDecimal balance;
    private Timestamp createdAt;
}
