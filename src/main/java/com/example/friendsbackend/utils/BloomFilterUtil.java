package com.example.friendsbackend.utils;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class BloomFilterUtil {
    @Resource
    private RedissonClient redissonClient;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 创建布隆过滤器
     *
     * @param filterName           过滤器名称
     * @param expectedInsertions   预测插入数量
     * @param falsePositiveRate    误判率
     * @return  布隆过滤器
     */
    public <T> RBloomFilter<T> create(String filterName, long expectedInsertions, double falsePositiveRate){
        RBloomFilter<T> bloomFilter = redissonClient.getBloomFilter(filterName);
        bloomFilter.tryInit(expectedInsertions,falsePositiveRate);
        return bloomFilter;
    }

    public <T> void deleteBloomFilter(String filterName) {
        RBloomFilter<T> bloomFilter = redissonClient.getBloomFilter(filterName);
        bloomFilter.delete();
    }
}
