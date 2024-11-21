package com.lmw;

import com.lmw.entity.User;
import com.lmw.mapper.UserMapper;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testGetUserById_UserExists_ReturnsUser() {
        // Given
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setBalance(new BigDecimal("1000"));
        userMapper.createUser(user);

        // When
        User retrievedUser = userMapper.getUserById(user.getUserId());

        // Then
        assertNotNull(retrievedUser);
        assertEquals(user.getUsername(), retrievedUser.getUsername());
        assertEquals(user.getEmail(), retrievedUser.getEmail());
        assertEquals(user.getBalance(), retrievedUser.getBalance());
    }

    @Test
    public void testGetUserById_UserDoesNotExist_ReturnsNull() {
        // Given
        Integer userId = 1;

        // When
        User retrievedUser = userMapper.getUserById(userId);

        // Then
        assertNull(retrievedUser);
    }

    @Test
    public void testCreateUser_Success_ReturnsUserId() {
        // Given
        User user = new User();
        user.setUsername("newUser");
        user.setEmail("newuser@example.com");
        user.setBalance(new BigDecimal("500"));

        // When
        int result = userMapper.createUser(user);

        // Then
        assertEquals(1, result);
        assertNotNull(user.getUserId());
    }

    @Test
    public void testUpdateUserBalance_Success_ReturnsOne() {
        // Given
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setBalance(new BigDecimal("1000"));
        userMapper.createUser(user);

        // Update the user's balance
        user.setBalance(new BigDecimal("1500"));

        // When
        int result = userMapper.updateUserBalance(user);

        // Then
        assertEquals(1, result);
    }

    @Test
    public void testGetAllUsers_UsersExist_ReturnsUserList() {
        // Given
        User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setBalance(new BigDecimal("2000"));
        userMapper.createUser(user1);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setBalance(new BigDecimal("2500"));
        userMapper.createUser(user2);

        // When
        List<User> users = userMapper.getAllUsers();

        // Then
        assertNotNull(users);
        assertEquals(2, users.size());
    }

    @Test
    public void testDeleteAllUsers_Success_ReturnsZeroUsers() {
        // Given
        User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setBalance(new BigDecimal(2000));
        userMapper.createUser(user1);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setBalance(new BigDecimal("2500"));
        userMapper.createUser(user2);

        // Then
        List<User> users = userMapper.getAllUsers();
        assertNotNull(users);
        assertTrue(users.isEmpty());
    }


}