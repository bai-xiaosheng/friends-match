package com.example.friendsbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.friendsbackend.mapper.UserMapper;
import com.example.friendsbackend.modal.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class UserControllerTest {

    @Resource
    private UserMapper userMapper;
    @Test
    void Test(){

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id","tags");
        queryWrapper.eq("id",1);
        User user = userMapper.selectOne(queryWrapper);

//        user = userMapper.selectById(1);
        String loginUserTags = user.getTags();

        System.out.println(loginUserTags);
    }

}