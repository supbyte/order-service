package com.lmw.vo;

import com.lmw.enums.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderVO {

    private Integer orderId;
    private BigDecimal totalPrice;
    private OrderStatus status;

    private List<OrderItemVO> orderItems;
}
