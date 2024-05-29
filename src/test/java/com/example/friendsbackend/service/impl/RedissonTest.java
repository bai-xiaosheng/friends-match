package com.example.friendsbackend.service.impl;

import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class RedissonTest {

    @Resource
    private RedissonClient redissonClient;

    @Test
    public void doRedissonTest(){
        //列表
        List<String> list = new ArrayList<>();
        list.add("1");
        System.out.println(list.get(0));
//        list.remove(0);

        RList<String> test = redissonClient.getList("test");
        test.add("test");
        System.out.println("test:" + test.get(0));

        //map
        //set
    }
}
