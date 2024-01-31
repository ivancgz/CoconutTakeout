package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfiguration {

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        log.info("开始创建 redis 模版对象...");
        RedisTemplate redisTemplate = new RedisTemplate();
        // 设置 redis 的连接工厂对象
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 设置 redis key 的序列号器
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // 设置 redis value 的序列号器
        // redisTemplate.setValueSerializer(new StringRedisSerializer());
        // 设置 redis hash 的序列号器
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());


        return redisTemplate;
    }
}
