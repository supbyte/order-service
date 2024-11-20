package com.lmw.dto;

import lombok.Data;

@Data
public class OrderItemDTO {

    // 产品ID
    private int productId;
    // 购买数量
    private int quantity;
}
