package com.lmw.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderItemVO implements Serializable {

    private String productName;

    private Integer quantity;

    private BigDecimal price;
}
