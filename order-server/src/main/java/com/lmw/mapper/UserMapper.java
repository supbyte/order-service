package com.lmw.mapper;

import com.lmw.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    //@Select("SELECT * FROM users WHERE user_id = #{userId}")
    User getUserById(Integer userId);

    //@Insert("INSERT INTO users (username, email, balance, created_at) VALUES (#{username}, #{email}, #{balance}, NOW())")
    //@Options(useGeneratedKeys = true, keyProperty = "userId")
    int createUser(User user);

    //@Update("UPDATE users SET balance = #{balance} WHERE user_id = #{userId}")
    int updateUserBalance(User user);

    //@Select("SELECT * FROM users")
    List<User> getAllUsers();
}
