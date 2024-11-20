### 功能需求

1. **下单功能：**

   ⽤户选择商品和数量，下单后系统⽣成订单及订单详情。 下单时需要检查商品库存，只有库存⾜够的情况下才能创建订单。 订单创建时的状态为 PENDING ，并记录订单中的商品及其数量。

2. **⽀付功能：**

   实现订单⽀付功能，⽤户可以直接通过以下⽅式⽀付：

   ⅰ. 余额⽀付：⽤户使⽤⾃⼰的账户余额⽀付。 ⽀付成功后，更新订单状态为 PAID ，并减少对应商品的库存。

3. **库存管理：**

   在下单和⽀付过程中，需要保证库存的⼀致性（涉及并发控制）。 如果⽤户⽀付失败或取消订单，库存应恢复。

4. **订单取消功能：**

   ⽤户可以取消未⽀付的订单，取消后订单状态应更新为 CANCELLED ，并恢复库存。

5. **⽀付回调功能：**

   实现⼀个模拟的⽀付回调接⼝，当第三⽅⽀付成功后系统应⾃动接收回调并更新订单状态和⽀ 付记录。

6. **查询功能：**

   实现⽤户查询订单功能，能够查询到所有订单及其详情，包括订单状态和⽀付状态。

### 数据库设计

```sql
# 用户表
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) NOT NULL,
    balance DECIMAL(10, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
# 商品表
CREATE TABLE products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
# 订单表
CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    status ENUM('PENDING', 'PAID', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
# 订单详情表
CREATE TABLE order_items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL
);
# 支付记录表
CREATE TABLE payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    status ENUM('SUCCESS', 'FAILED') NOT NULL,
    payment_method ENUM('BALANCE', 'THIRD_PARTY') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```
### 整合Knife4j接口文档
启动项目后访问 http://localhost:8080/doc.html 即可查看接口文档并调试

### 项目环境
1. Idea 2024.3
2. MySQL 5.7
3. 浏览器 chrome
4. Maven 3.6.3
5. JDK 1.8
6. Redis 5.0.14
7. SpringBoot 2.7.3


