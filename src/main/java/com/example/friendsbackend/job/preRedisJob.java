package com.example.friendsbackend.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.friendsbackend.mapper.UserMapper;
import com.example.friendsbackend.modal.domain.User;
import com.example.friendsbackend.service.UserService;
import com.example.friendsbackend.utils.RedisUtil;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.CacheAsync;
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

import static com.example.friendsbackend.constant.Constant.*;

@Component
@Slf4j
public class preRedisJob {
    //to do用户信息脱敏
    @Resource
    private UserMapper userMapper;
//    @Resource
//    private RedisTemplate<String,Object> redisTemplate;
//    @Resource
//    private UserService userService;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private RedisUtil redisUtil;
//    private List<Long> userList = Arrays.asList(1L);


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

//    @Scheduled(cron = "0 0 0 * * *")
//    public void doCacheRecommendUser(){
//        RLock lock = redissonClient.getLock("xiaobai:preRedis:docache:lock");
//        try {
//            if (lock.tryLock(0,-1, TimeUnit.MICROSECONDS)){
//                System.out.println("get lock"+Thread.currentThread().getId());
//                for (Long userid: userList){
//                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//                    Page<User> userPage = userMapper.selectPage(new Page<>(1, 10), queryWrapper);
//                    String rediasKey = String.format("xiaobai:user:recommend:%s",userid);
//                    List<User> collect = userPage.getRecords().stream().map(
//                            user -> userService.getSafeUser(user)).collect(Collectors.toList());
//                    try {
//                        redisTemplate.opsForValue().set(rediasKey,collect);
//                    } catch (Exception e) {
//                        log.error("redis set key error",e);
//                    }
//                }
//            }
//        } catch (InterruptedException e) {
//            log.error("doCacheRecommendUser error",e);
//        }finally {
//            if (lock.isHeldByCurrentThread()){
//                System.out.println("unlock" + Thread.currentThread().getId());
//                lock.unlock();
//            }
//        }
//    }

    /**
     * 每天凌晨4点更新当前用户缓存
     */
    @Scheduled(cron = "0 0 4 * * *")
    public void doCacheRecentUser(){
        RLock lock = redissonClient.getLock("xiaobai:user:recentUser:lock");
        try {
            if (lock.tryLock(0,-1,TimeUnit.MICROSECONDS)){
                // 查询最近登录的1000个用户，并将id保存到最近登录缓存中
                QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                queryWrapper.select("id","lastTime");
                queryWrapper.orderByDesc("lastTime");
                queryWrapper.last("limit 0,1000");
                List<User> userList = userMapper.selectList(queryWrapper);
                for (User user : userList){
                    redisUtil.zsetSet(RECENTUSER,user.getId(),user.getLastTime().getTime());
                }
                // 查询svip和vip用户，将结果保存到最近登录表中
                queryWrapper = new QueryWrapper<>();
                queryWrapper.select("id","lastTime");
                queryWrapper.eq("vipState",'2');
                queryWrapper.eq("vipState",'1');
                userList = userMapper.selectList(queryWrapper);
                for (User user : userList){
                    redisUtil.zsetSet(RECENTUSER,user.getId(),user.getLastTime().getTime());
                }
            }
        } catch (InterruptedException e) {
            log.error("doCacheRecentUser error" + e);
        }finally {
            if (lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
    }

    @Scheduled(cron = "0 3 5 * * *")
    public void deleteOverdueUser(){
        RLock lock = redissonClient.getLock("xiaobai:user:deleteOverdue:lock");
        try {
            if (lock.tryLock(0,-1,TimeUnit.MICROSECONDS)){
                // 删除缓存中一个月以前的用户
                redisUtil.zsetRemove(RECENTUSER,0L,System.currentTimeMillis() - RECENTTIME);
            }
        }catch (InterruptedException e){
            log.error("deleteOverdueUser error" + e);
        }finally {
            if (lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
    }
// cron:秒 分 时 日 月 周 年。*代表每一个 ？表示不关心，-表示从几到几
//    每小时执行一次
//    @Scheduled(cron = "0 0 * * * *")
//    public void doCacheRecentHourUser(){
//        RLock lock = redissonClient.getLock("xiaobai:preRedis:doCache:lock");
//        try {
//            if (lock.tryLock(0,-1,TimeUnit.MICROSECONDS)){
//                System.out.println("get lock" + Thread.currentThread().getId());
//                QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//                queryWrapper.select("id","tags","lastTime");
//                queryWrapper.orderByDesc("lastTime");
//                queryWrapper.last("limit 0,1000");
//                String redisKey = "xiaobai:user:recentHour:{id,lastTime}:String";
//                List<User> recentUserList = userMapper.selectList(queryWrapper);
//                try {
//                    redisTemplate.opsForValue().set(redisKey,recentUserList);
//                }catch (Exception e){
//                    log.error("redis set key error:",e);
//                }
//            }
//        }catch (InterruptedException e){
//            log.error("doCacheRecentHourUser error",e);
//        }finally {
//            if (lock.isHeldByCurrentThread()){
//                System.out.println("unlock" + Thread.currentThread().getId());
//                lock.unlock();
//            }
//        }
//    }


}
