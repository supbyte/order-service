package com.lmw;

import com.lmw.entity.Order;
import com.lmw.enums.OrderStatus;
import com.lmw.mapper.OrderMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;

@SpringBootTest
public class OrderTest {

    @Resource
    private OrderMapper orderMapper;

    @Test
    public void insert(){
        Order order = new Order();
        order.setOrderItems(new ArrayList<>());
        order.setStatus(OrderStatus.PAID);
        order.setUserId(1);
        order.setTotalPrice(new BigDecimal(100));

        System.out.println(orderMapper.createOrder(order));
    }
}
