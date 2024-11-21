package com.lmw;

import com.lmw.entity.OrderItem;
import com.lmw.mapper.OrderItemMapper;
import com.lmw.vo.OrderItemVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.util.AssertionErrors.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderItemMapperTest {

    @Autowired
    private OrderItemMapper orderItemMapper;

    @BeforeEach
    void setUp() {
        // Set up test context if necessary
    }

    @Test
    void testGetOrderItemById_ItemExists_ItemReturned() {
        // Arrange
        Integer itemId = 1; // Assuming item ID 1 exists in the database

        // Act
        OrderItem orderItem = orderItemMapper.getOrderItemById(itemId);

        // Assert
        assertNotNull("OrderItem should not be null", orderItem);
        assertEquals("Item ID should match", itemId, orderItem.getItemId());
    }

    @Test
    void testGetOrderItemsByOrderId_OrderIdExists_ItemsReturned() {
        // Arrange
        Integer orderId = 1; // Assuming order ID 1 exists in the database

        // Act
        List<OrderItem> orderItems = orderItemMapper.getOrderItemsByOrderId(orderId);

        // Assert
        assertNotNull("OrderItems list should not be null", orderItems);
        assertFalse("OrderItems list should not be empty", orderItems.isEmpty());
    }

    @Test
    void testListOrderItemVOByOrderId_OrderIdExists_VOListReturned() {
        // Arrange
        int orderId = 1; // Assuming order ID 1 exists in the database

        // Act
        List<OrderItemVO> orderItemVOs = orderItemMapper.listOrderItemVOByOrderId(orderId);

        // Assert
        assertNotNull("OrderItemVO list should not be null", orderItemVOs);
        assertFalse("OrderItemVO list should not be empty", orderItemVOs.isEmpty());
    }

    @Test
    void testCreateOrderItem_ItemInserted_Success() {
        // Arrange
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(1);
        orderItem.setProductId(1);
        orderItem.setQuantity(2);
        orderItem.setPrice(new BigDecimal("100.0"));

        // Act
        int result = orderItemMapper.createOrderItem(orderItem);

        // Assert
        assertEquals("Insert operation should return 1", 1, result);
    }
}

