package com.blossom.expand.tracker.core.adapter.redis;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 链路追踪对 redis 的拓展
 *
 * @author xzzz
 */
@Configuration
@ConditionalOnClass(value = RedisTemplate.class)
public class TrackerRedisConfiguration {

    @Bean
    public TrackerRedisProperties trackerRedisProperties() {
        return new TrackerRedisProperties();
    }

    @Bean
    @ConditionalOnProperty(value = "tracker.redis.enabled", havingValue = "true", matchIfMissing = true)
    public RedisFactoryBeanPostProcessor redisFactoryBeanPostProcessor(TrackerRedisProperties trackerRedisProperties) {
        return new RedisFactoryBeanPostProcessor(trackerRedisProperties);
    }
}
