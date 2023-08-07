package com.blossom.common.cache.redis;

import com.blossom.common.base.util.json.JsonUtil;
import com.blossom.common.cache.CommonCacheProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis 缓存设置了key缓存名后加 "::"
 *
 * @author : xzzz
 * @see org.springframework.data.redis.cache.CacheKeyPrefix#simple
 */
@Slf4j
@EnableCaching
@ConditionalOnClass(RedisTemplate.class)
public class RedisCacheConfig {

    private static final Integer DEFAULT_TIME_OUT = 60 * 60 * 4;

    /**
     * 配置
     *
     * @param redisConnectionFactory redis配置
     * @return redis管理器
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory, CommonCacheProperties properties) {
        log.info("[   CACHE] 缓存注解使用 : Redis");
        return new RedisCacheManager(
                RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory),
                // 默认策略
                this.getDefaultCacheConfiguration(DEFAULT_TIME_OUT),
                // 自定义Key缓存策略
                this.initCacheConfigurations(properties)
        );
    }

    /**
     * redis缓存默认配置
     * 修改缓存序列化方式,修改过期时间
     *
     * @return RedisCacheConfiguration Redis配置类
     */
    private RedisCacheConfiguration getDefaultCacheConfiguration(Integer seconds) {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        // 修改序列化方式
        jackson2JsonRedisSerializer.setObjectMapper(JsonUtil.typeObjectMapper());

        return RedisCacheConfiguration
                .defaultCacheConfig()
                .computePrefixWith(name -> RedisConstant.CACHE_PREFIX_WITH + name + RedisConstant.CACHE_SUFFIX_WITH)
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                .entryTtl(Duration.ofSeconds(seconds));
    }

    /**
     * 自定义配置, 不使用公共的缓存有效时间
     *
     * @return 配置对象
     */
    private Map<String, RedisCacheConfiguration> initCacheConfigurations(CommonCacheProperties properties) {
        Map<String, RedisCacheConfiguration> configMap = new HashMap<>(1);
        for (CommonCacheProperties.CacheNamesConfig cacheNamesConfig : properties.getNamesConfig()) {
            configMap.put(cacheNamesConfig.getName(), this.getDefaultCacheConfiguration(cacheNamesConfig.getSeconds()));
        }
        // ========== 自定义设置缓存时间 ===========
        return configMap;
    }


}
