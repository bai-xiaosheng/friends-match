package com.example.friendsbackend.config;


import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.*;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import java.time.Duration;
import java.util.Random;


@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
@EnableCaching
public class RedissonConfig {

    private String host;

    private String port;

    private String password;

    @Bean(destroyMethod = "shutdown")  // bean销毁时关闭Redisson实例，但不关闭Redis服务
    public RedissonClient redisson() {
        //创建配置
        Config config = new Config();
        /**
         *  连接哨兵：config.useSentinelServers().setMasterName("myMaster").addSentinelAddress()
         *  连接集群： config.useClusterServers().addNodeAddress()
         */
        config.useSingleServer()
                .setAddress("redis://" + host + ":" + port)
                .setPassword(password)
                .setTimeout(5000);
        //根据config创建出RedissonClient实例
        return Redisson.create(config);
    }


    @Bean
    public CacheManager RedisCacheManager(RedisConnectionFactory factory){

//        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
//        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        // 解决查询缓存转换异常的问题
//        ObjectMapper om = new ObjectMapper();
//        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        /*
          新版本中om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL)已经被废弃
          建议替换为om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL)
         */
//        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
//        jackson2JsonRedisSerializer.setObjectMapper(om);
        // 配置序列化解决乱码的问题
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                // 设置缓存过期时间  为解决缓存雪崩,所以将过期时间加随机值
                .entryTtl(Duration.ofSeconds(60 * 60 + new Random().nextInt(60 * 10)))
                // 设置key的序列化方式
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                // 设置value的序列化方式
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        // .disableCachingNullValues(); //为防止缓存击穿，所以允许缓存null值
        return RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                // 启用RedisCache以将缓存 put/evict 操作与正在进行的 Spring 管理的事务同步
                .transactionAware()
                .build();
    }

}
