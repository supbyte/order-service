package com.lmw.mapper;

import com.lmw.entity.Payment;
import org.apache.ibatis.annotations.*;

@Mapper
public interface PaymentMapper {

    //@Select("SELECT * FROM payments WHERE payment_id = #{paymentId}")
    Payment getPaymentById(Integer paymentId);

    //@Insert("INSERT INTO payments (order_id, amount, status, payment_method, created_at) VALUES (#{orderId}, #{amount}, #{status}, #{paymentMethod}, NOW())")
    //@Options(useGeneratedKeys = true, keyProperty = "paymentId")
    int createPayment(Payment payment);

    //@Select("SELECT * FROM payments WHERE order_id = #{orderId}")
    Payment getPaymentByOrderId(Integer orderId);
}
