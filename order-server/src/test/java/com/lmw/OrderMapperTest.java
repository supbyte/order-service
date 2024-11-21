package com.lmw;

import com.lmw.entity.Order;
import com.lmw.enums.OrderStatus;
import com.lmw.mapper.OrderMapper;
import com.lmw.vo.OrderVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OrderMapperTest {

    @Autowired
    private OrderMapper orderMapper;

    @BeforeEach
    void setUp() {
        // 可以在这里进行一些初始化操作，例如清理数据库或插入测试数据
    }

    @Test
    void testGetOrderById_OrderExists_ReturnsOrder() {
        // Arrange
        Integer orderId = 1; // 假设数据库中有订单ID为1的记录

        // Act
        Order order = orderMapper.getOrderById(orderId);

        // Assert
        assertNotNull(order, "Order should not be null");
        assertEquals(orderId, order.getOrderId(), "Order ID should match");
    }

    @Test
    void testGetOrderById_OrderDoesNotExist_ReturnsNull() {
        // Arrange
        Integer orderId = 999; // 假设数据库中没有订单ID为999的记录

        // Act
        Order order = orderMapper.getOrderById(orderId);

        // Assert
        assertNull(order, "Order should be null");
    }

    @Test
    void testCreateOrder_Success_ReturnsOrderId() {
        // Arrange
        Order order = new Order();
        order.setUserId(1);
        order.setTotalPrice(new BigDecimal("100.00"));
        order.setStatus(OrderStatus.PENDING);

        // Act
        int result = orderMapper.createOrder(order);

        // Assert
        assertNotNull(order.getOrderId(), "Order should not be null after creation");
        assertTrue(order.getOrderId() > 0, "Order ID should be greater than 0");
    }

    @Test
    void testUpdateOrderStatus_Success_ReturnsOne() {
        // Arrange
        Order order = new Order();
        order.setOrderId(1); // 假设数据库中有订单ID为1的记录
        order.setStatus(OrderStatus.PAID);

        // Act
        int result = orderMapper.updateOrderStatus(order);

        // Assert
        assertEquals(1, result, "Update should affect one row");
    }

    @Test
    void testGetOrdersByUserId_OrdersExist_ReturnsOrderList() {
        // Arrange
        Integer userId = 1; // 假设用户ID为1有订单记录

        // Act
        List<Order> orders = orderMapper.getOrdersByUserId(userId);

        // Assert
        assertNotNull(orders, "Order list should not be null");
        assertFalse(orders.isEmpty(), "Order list should not be empty");
    }

    @Test
    void testGetOrdersByUserId_NoOrders_ReturnsEmptyList() {
        // Arrange
        Integer userId = 999; // 假设用户ID为999没有订单记录

        // Act
        List<Order> orders = orderMapper.getOrdersByUserId(userId);

        // Assert
        assertNotNull(orders, "Order list should not be null");
        assertTrue(orders.isEmpty(), "Order list should be empty");
    }

    @Test
    void testListOrderVOByUserId_OrdersExist_ReturnsOrderVOList() {
        // Arrange
        int userId = 1; // 假设用户ID为1有订单记录

        // Act
        List<OrderVO> orderVOs = orderMapper.listOrderVOByUserId(userId);

        // Assert
        assertNotNull(orderVOs, "OrderVO list should not be null");
        assertFalse(orderVOs.isEmpty(), "OrderVO list should not be empty");
    }

    @Test
    void testListOrderVOByUserId_NoOrders_ReturnsEmptyList() {
        // Arrange
        int userId = 999; // 假设用户ID为999没有订单记录

        // Act
        List<OrderVO> orderVOs = orderMapper.listOrderVOByUserId(userId);

        // Assert
        assertNotNull(orderVOs, "OrderVO list should not be null");
        assertTrue(orderVOs.isEmpty(), "OrderVO list should be empty");
    }
}
