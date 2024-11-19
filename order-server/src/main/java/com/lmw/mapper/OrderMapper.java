package com.lmw.mapper;

import com.lmw.entity.Order;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderMapper {

    @Select("SELECT * FROM orders WHERE order_id = #{orderId}")
    Order getOrderById(Integer orderId);

    @Insert("INSERT INTO orders (user_id, total_price, status, created_at) VALUES (#{userId}, #{totalPrice}, #{status}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "orderId")
    int createOrder(Order order);

    @Update("UPDATE orders SET status = #{status} WHERE order_id = #{orderId}")
    int updateOrderStatus(Order order);

    @Select("SELECT * FROM orders WHERE user_id = #{userId}")
    List<Order> getOrdersByUserId(Integer userId);
}
