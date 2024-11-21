package com.lmw;

import com.lmw.entity.Payment;
import com.lmw.enums.OrderStatus;
import com.lmw.enums.PaymentMethod;
import com.lmw.enums.PaymentStatus;
import com.lmw.mapper.PaymentMapper;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class PaymentMapperTest {

    @Autowired
    private SqlSession sqlSession;

    @Autowired
    private PaymentMapper paymentMapper;

    @BeforeEach
    public void setUp() {
        // Enable the MyBatis specific logging
        System.setProperty("org.apache.ibatis.logging.log4j2.Log4j2Logging", "DEBUG");
    }


    @Test
    public void createPayment_PaymentIsValid_PaymentIsCreated() {
        Payment payment = new Payment();
        payment.setOrderId(1001);
        payment.setAmount(new BigDecimal("100.00"));
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaymentMethod(PaymentMethod.BALANCE);

        int result = paymentMapper.createPayment(payment);

        // Check that the payment was created successfully
        assertEquals(1, result);
        assertNotNull(payment.getPaymentId());
    }

    @Test
    public void getPaymentById_PaymentExists_PaymentReturned() {
        // Create a payment first
        Payment createdPayment = new Payment();
        createdPayment.setOrderId(1002);
        createdPayment.setAmount(new BigDecimal("200.00"));
        createdPayment.setStatus(PaymentStatus.SUCCESS);
        createdPayment.setPaymentMethod(PaymentMethod.BALANCE);
        paymentMapper.createPayment(createdPayment);

        // Retrieve the payment by ID
        Payment retrievedPayment = paymentMapper.getPaymentById(createdPayment.getPaymentId());

        // Check that the correct payment is returned
        assertNotNull(retrievedPayment);
        assertEquals(createdPayment.getPaymentId(), retrievedPayment.getPaymentId());
    }

    @Test
    public void getPaymentById_PaymentDoesNotExist_ReturnsNull() {
        // Retrieve a non-existent payment
        Payment retrievedPayment = paymentMapper.getPaymentById(999);

        // Check that null is returned
        assertNull(retrievedPayment);
    }

    @Test
    public void getPaymentByOrderId_PaymentExists_PaymentReturned() {
        // Create a payment first
        Payment createdPayment = new Payment();
        createdPayment.setOrderId(1003);
        createdPayment.setAmount(new BigDecimal("300.00"));
        createdPayment.setStatus(PaymentStatus.FAILED);
        createdPayment.setPaymentMethod(PaymentMethod.BALANCE);
        paymentMapper.createPayment(createdPayment);

        // Retrieve the payment by Order ID
        Payment retrievedPayment = paymentMapper.getPaymentByOrderId(createdPayment.getOrderId());

        // Check that the correct payment is returned
        assertNotNull(retrievedPayment);
        assertEquals(createdPayment.getOrderId(), retrievedPayment.getOrderId());
    }

    @Test
    public void getPaymentByOrderId_PaymentDoesNotExist_ReturnsNull() {
        // Retrieve a payment for a non-existent Order ID
        Payment retrievedPayment = paymentMapper.getPaymentByOrderId(999);

        // Check that null is returned
        assertNull(retrievedPayment);
    }
}
