package com.lmw.dto;

import com.lmw.enums.PaymentMethod;
import lombok.Data;

@Data
public class PaymentDTO {

    private Integer userId;

    private PaymentMethod paymentMethod;
}
