package com.lmw.controller;

import com.lmw.constant.MessageConstant;
import com.lmw.exception.ResourceNotFoundException;
import com.lmw.result.Result;
import com.lmw.service.OrderService;
import com.lmw.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "用户服务接口")
@RestController
@RequestMapping("/users")
public class UserController {

    @Resource
    private OrderService orderService;

    @ApiOperation("查询用户订单")
    @GetMapping("/{userId}/orders")
    @Cacheable(cacheNames = "userOrderCache", key = "#userId")
    public Result<List<OrderVO>> getOrdersByUserId(@PathVariable int userId) {
        try {
            List<OrderVO> orderVOList = orderService.getOrdersByUserId(userId);
            return Result.success(orderVOList);
        } catch (ResourceNotFoundException e) {
            return Result.error(MessageConstant.USER_NOT_FOUND + e.getMessage());
        }
    }
}
