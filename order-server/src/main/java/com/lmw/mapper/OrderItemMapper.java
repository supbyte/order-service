package com.lmw.mapper;

import com.lmw.entity.OrderItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderItemMapper {

    //@Select("SELECT * FROM order_items WHERE item_id = #{itemId}")
    OrderItem getOrderItemById(Integer itemId);

    //@Insert("INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (#{orderId}, #{productId}, #{quantity}, #{price})")
    int createOrderItem(OrderItem orderItem);

    //@Select("SELECT * FROM order_items WHERE order_id = #{orderId}")
    List<OrderItem> getOrderItemsByOrderId(Integer orderId);
}
