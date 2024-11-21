package com.lmw;

import com.lmw.entity.Product;
import com.lmw.mapper.ProductMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProductMapperTest {

    @Autowired
    private ProductMapper productMapper;

    private Integer newProductId;

    @BeforeEach
    public void setUp() {
        // 清理数据库或设置初始数据
    }

    @Test
    @Rollback
    public void testCreateProduct() {
        Product newProduct = new Product();
        newProduct.setName("Test Product");
        newProduct.setPrice(new BigDecimal("100"));
        newProduct.setStock(10);

        int result = productMapper.createProduct(newProduct);
        Assertions.assertTrue(result > 0, "Insert operation should return a result greater than 0");
        newProductId = newProduct.getProductId();
    }

    @Test
    @Rollback
    public void testGetProductById_ProductExists() {
        Product product = productMapper.getProductById(newProductId);
        Assertions.assertNotNull(product);
        assertEquals(newProductId, product.getProductId());
    }

    @Test
    @Rollback
    public void testGetProductById_ProductNotExists() {
        Product product = productMapper.getProductById(-1);
        Assertions.assertNotNull(product);
    }

    @Test
    @Rollback
    public void testUpdateProductStock() {
        Product product = new Product();
        product.setProductId(newProductId);
        product.setStock(20);

        int result = productMapper.updateProductStock(product);
        Assertions.assertTrue(result > 0, "Update operation should return a result greater than 0");

        Product updatedProduct = productMapper.getProductById(newProductId);
        assertEquals(20, updatedProduct.getStock());
    }

    @Test
    @Rollback
    public void testGetAllProducts() {
        List<Product> products = productMapper.getAllProducts();
        assertFalse(products.isEmpty(), "Product list should not be empty");
    }
}
