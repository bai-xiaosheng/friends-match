package com.example.friendsbackend.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.friendsbackend.mapper.UserMapper;
import com.example.friendsbackend.modal.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class RedisUtilTest {
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private UserMapper userMapper;

    @Test
    void test(){
//        RedisUtil redisUtil = new RedisUtil();
        String key = "xiaobai:user:recentUser:id:zSet";
        // 查询最近登录的1000个用户，并将id保存到最近登录缓存中
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id","lastTime");
        queryWrapper.orderByDesc("lastTime");
        queryWrapper.last("limit 0,1000");
        List<User> userList = userMapper.selectList(queryWrapper);
        for (User user : userList){
            redisUtil.zsetSet(key,user.getId(),user.getLastTime().getTime());
        }
        // 查询svip和vip用户，将结果保存到最近登录表中
        queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id","lastTime");
        queryWrapper.eq("vipState",'2');
        queryWrapper.eq("vipState",'1');
        userList = userMapper.selectList(queryWrapper);
        for (User user : userList){
            redisUtil.zsetSet(key,user.getId(),user.getLastTime().getTime());
        }

        // 修改用户最近登录时间
        Date date = new Date();
        System.out.println(date);
        userList.get(1).setLastTime(date);
        userMapper.updateById(userList.get(1));

        // 计算最近登录表缓存中用户数量
        redisUtil.zsetCount(key);
//        System.out.println(System.currentTimeMillis());
//        redisUtil.zsetRemove(key,0L,1722653838000L);
        // 删除得分（时间戳）0到当前时刻的用户id
//        redisUtil.zsetRemove(key,0L,System.currentTimeMillis());
        // 查询一个月前到现在的登录用户
        redisUtil.zsetAllQuery(key,System.currentTimeMillis() - 2629800000L,System.currentTimeMillis());
    }
}