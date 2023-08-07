package com.blossom.common.cache.redis;

import com.blossom.common.base.config.JacksonConfig;
import com.blossom.common.base.util.json.JsonUtil;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * RedisTemplate 配置, 在 RedisTemplate 自动装载之前配置
 *
 * @author : xzzz
 */
@Configuration
@ConditionalOnClass(RedisTemplate.class)
@AutoConfigureBefore({RedisAutoConfiguration.class, JacksonConfig.class})
public class RedisConfiguration {

    private final RedisConnectionFactory factory;

    public RedisConfiguration(RedisConnectionFactory factory) {
        this.factory = factory;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        // 修改序列化方式
        jackson2JsonRedisSerializer.setObjectMapper(JsonUtil.typeObjectMapper());

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // 序列化 key
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setConnectionFactory(factory);
        return redisTemplate;
    }

    /**
     * 将 RedisMessageListenerContainer 注入到IOC, 方便使用
     */
    @Bean
    public RedisMessageListenerContainer listenerContainer() {
        RedisMessageListenerContainer listenerContainer = new RedisMessageListenerContainer();
        listenerContainer.setConnectionFactory(factory);
        return listenerContainer;
    }

//    @Bean(name = "dynamicLogRepositoryRedis")
//    public DynamicLogRepository dynamicLogRepositoryRedis(LoggingSystem loggingSystem,
//                                                     StringRedisTemplate redisTemplate,
//                                                     RedisMessageListenerContainer listenerContainer,
//                                                     BaseProperties properties) {
//        return new DynamicLogRepositoryRedis(loggingSystem, redisTemplate, listenerContainer, properties);
//    }

    @Bean
    public SimpleLock simpleLock(StringRedisTemplate stringRedisTemplate) {
        return new SimpleLock(stringRedisTemplate);
    }
}
