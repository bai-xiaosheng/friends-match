package com.example.friendsbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.friendsbackend.config.RedisConfig;
import com.example.friendsbackend.mapper.UserMapper;
import com.example.friendsbackend.modal.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import com.example.friendsbackend.service.UserService;


@SpringBootTest
@Slf4j
public class RedisTest {
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserService userService;
    @Resource
    private RedissonClient redissonClient;
    private List<Long> mainUserList = Arrays.asList(1l);

//    @Test
//    void Test() {
//        //增加
//        ValueOperations<String, Object> redis = redisTemplate.opsForValue();
//        redis.set("yupi","dog");
//        redis.set("1",1);
//        redis.set("2",2.0);
//        //查询
//        Object yupiString = redis.get("yupi");
//
//    }

    @Test
    void Test(){
        RLock lock = redissonClient.getLock("xiaobai:preRedis:docache:lock");
        try {
            if (lock.tryLock(0,-1, TimeUnit.MICROSECONDS)){
                System.out.println("get lock"+Thread.currentThread().getId());
                for (Long userid: mainUserList){
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userMapper.selectPage(new Page<>(1, 20), queryWrapper);
                    String rediasKey = String.format("xiaobai:user:recommend:%s",userid);
                    List<User> collect = userPage.getRecords().stream().map(
                            user -> userService.getSafeUser(user)).collect(Collectors.toList());
                    try {
                        redisTemplate.opsForValue().set(rediasKey,collect);
                    } catch (Exception e) {
                        log.error("redias set key error",e);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUser error",e);
        }finally {
            if (lock.isHeldByCurrentThread()){
                System.out.println("unlock" + Thread.currentThread().getId());
                lock.unlock();
            }
        }
//        for (long userId: mainUserList){
//            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//            Page<User> userPage = userMapper.selectPage(new Page<>(1, 20), queryWrapper);
//            String rediasKey = String.format("xiaobai:user:recommend:%s",userId);
//            List<User> collect = userPage.getRecords().stream().map(
//                    user -> {
//                        return userService.getSafeUser(user);
//                    }).collect(Collectors.toList());
//            try {
//                redisTemplate.opsForValue().set(rediasKey,collect);
//            } catch (Exception e) {
//                log.error("redias set key error",e);
//            }
//        }
    }
}
