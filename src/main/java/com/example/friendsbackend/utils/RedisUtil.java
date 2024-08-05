package com.example.friendsbackend.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class RedisUtil {
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * @param key Redis key
     * @param value Redis value
     * @param score Redis score
     * @return 添加是否成功
     */
    public Boolean zsetSet(String key, Object value, long score){
//        redisTemplate.opsForZSet().add(key,value,score);
        try {
//            按照score值由小到大进行排列
            return redisTemplate.opsForZSet().add(key,value,score);
        }catch (Exception e){
            log.error("zsetSet redis error" + e);
            return false;
        }
    }

    /**
     * @param key Redis key
     * @return 最近登录人数
     */
    public Long zsetCount(String key){
        try {
            return redisTemplate.opsForZSet().size(key);
        }catch (Exception e){
            log.error("zsetCount error" + e);
            return 0L;
        }
    }

    /**
     * @param key Redis key
     * @param minScore 最小得分
     * @param maxScore 最大得分
     * @return 删除个数
     */
    public Long zsetRemove(String key, Long minScore, Long maxScore){
        try {
            return redisTemplate.opsForZSet().removeRangeByScore(key,minScore,maxScore);
        }catch (Exception e){
            log.error("zset Remove error" + e);
            return 0L;
        }
    }

    /**
     * 查询当前时间范围内的用户id
     * @param key Redis key
     * @param minScore 起始时间
     * @param maxScore 当前时间
     * @return 在时间范围内的用户id
     */
    public Set<Object> zsetAllQuery(String key,Long minScore, Long maxScore){
        try {
            //            System.out.println(userId);
            return redisTemplate.opsForZSet().rangeByScore(key, minScore, maxScore);
        }catch (Exception e){
            log.error("zsetAllQuery error"+e);
            return null;
        }
    }
}
