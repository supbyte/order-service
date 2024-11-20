package com.lmw.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemVO {

    private String productName;

    private Integer quantity;

    private BigDecimal price;
}
