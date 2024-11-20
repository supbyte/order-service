package com.lmw.vo;

import com.lmw.enums.OrderStatus;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderVO implements Serializable {

    private Integer orderId;
    private BigDecimal totalPrice;
    private OrderStatus status;

    private List<OrderItemVO> orderItems;
}
