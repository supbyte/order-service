package com.lmw.mapper;

import com.lmw.entity.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductMapper {

    @Select("SELECT * FROM products WHERE product_id = #{productId}")
    Product getProductById(Integer productId);

    //@Insert("INSERT INTO products (name, price, stock, created_at) VALUES (#{name}, #{price}, #{stock}, NOW())")
    //@Options(useGeneratedKeys = true, keyProperty = "productId")
    int createProduct(Product product);

    @Update("UPDATE products SET stock = #{stock} WHERE product_id = #{productId}")
    int updateProductStock(Product product);

    @Select("SELECT * FROM products")
    List<Product> getAllProducts();
}
