package com.example.friendsbackend.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.friendsbackend.mapper.UserMapper;
import com.example.friendsbackend.modal.domain.User;
import com.example.friendsbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class preRedisJob {
    //to do用户信息脱敏
    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private UserService userService;
    @Resource
    private RedissonClient redissonClient;
    private List<Long> userList = Arrays.asList(1L);


//    // 结合分页，按批次从数据库拉取数据出来跑批，例如从数据库获取10万记录，做数据处理
//    Page<H2User> page = new Page<>(1, 100000);
//baseMapper.selectList(page, Wrappers.emptyWrapper(), new ResultHandler<H2User>() {
//        int count = 0;
//        @Override
//        public void handleResult(ResultContext<? extends H2User> resultContext) {
//            H2User h2User = resultContext.getResultObject();
//            System.out.println("当前处理第" + (++count) + "条记录: " + h2User);
//            // 在这里进行你的业务处理，比如分发任务
//        }
//    });

    @Scheduled(cron = "0 0 0 * * *")
    public void doCacheRecommendUser(){
        RLock lock = redissonClient.getLock("xiaobai:preRedis:docache:lock");
        try {
            if (lock.tryLock(0,-1, TimeUnit.MICROSECONDS)){
                System.out.println("get lock"+Thread.currentThread().getId());
                for (Long userid: userList){
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userMapper.selectPage(new Page<>(1, 10), queryWrapper);
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
    }

}
