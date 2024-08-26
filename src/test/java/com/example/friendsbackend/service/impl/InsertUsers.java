package com.example.friendsbackend.service.impl;
import java.util.Date;

import com.example.friendsbackend.mapper.UserMapper;
import com.example.friendsbackend.modal.domain.User;
import com.example.friendsbackend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 本测试用于生成数据库数据，在打包时需要删除本测试或者跳过测试打包
 */
@SpringBootTest
public class InsertUsers {

    @Resource
    private UserService userService;

    //线程设置
    private ExecutorService executorService = new ThreadPoolExecutor(16,1000,10000, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000));
    @Test
    public void doInsertUser(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 50000;
        int batchSize = 10000;
        int j = 0;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i = 0; i < INSERT_NUM / batchSize; i++){
            List<User> userList = new ArrayList<>();
            while (true){
                j++;
                User user = new User();
                user.setUserAccount("xiaobai");
                user.setUserName("xiaobai");
                user.setUserUrl("https://picx.zhimg.com/80/v2-a67f86b7702594cc75899f23615aef1d_720w.webp?source=1def8aca");
                user.setProfile("熟练掌握 java, Spring, SpringMVC, MyBatis, MyBatisPlus, SpringBoot 等主流框架\n熟练掌握 JUC 并发编程，熟悉 JVM 原理与操作系统\n熟悉 Linux 环境， 熟练使用 Docker 进行 web 项目部署\n" +
                        "熟练掌握关系型数据库如 MySQL 的使用\n熟悉 Redis 的使用与缓存穿透、雪崩、击穿解决方案\n善于总结反思");
                user.setGender(0);
                user.setUserPassword("a1a6c667b32d27a7a8e09f189ba7bba9");
                user.setPhone("969900860@qq.com");
                user.setEmail("969900860@qq.com");

                user.setVipState("0");
                user.setTags("[\"男\",\"java\",\"python\"]");
                userList.add(user);
                if (j % batchSize == 0){
                    break;
                }
            }
            //异步执行
            CompletableFuture<Void> future = CompletableFuture.runAsync(()->{
                System.out.println("ThreadName" + Thread.currentThread().getName());
                userService.saveBatch(userList);
            },executorService);
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        stopWatch.stop();
        System.out.println(stopWatch.getLastTaskTimeMillis());
    }

}
