package com.blossom.common.cache.caffeine;

import com.blossom.common.cache.CommonCacheProperties;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

/**
 * caffeine 配置
 *
 * @author xzzz
 */
@Slf4j
@EnableCaching
@ConditionalOnMissingClass("org.springframework.data.redis.core.RedisTemplate")
public class CaffeineCacheConfig {

    /**
     * 配置缓存管理器
     *
     * @param properties 公共缓存配置
     * @return 缓存管理器
     */
    @Bean("caffeineCacheManager")
    public CacheManager cacheManager(CommonCacheProperties properties) {
        log.info("[   CACHE] 缓存注解使用 : Caffeine");
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        // 默认的过期策略
        cacheManager.setCaffeine(Caffeine.newBuilder()
                // 设置最后一次写入或访问后经过固定时间过期
                .expireAfterAccess(120, TimeUnit.MINUTES)
                // 初始的缓存空间大小
                .initialCapacity(2000)
                // 缓存的最大条数
                .maximumSize(10000));

        for (CommonCacheProperties.CacheNamesConfig cacheNamesConfig : properties.getNamesConfig()) {
            Cache<Object, Object> cache = Caffeine.newBuilder()
                    .maximumSize(cacheNamesConfig.getMaximumSize())
                    .expireAfterAccess(cacheNamesConfig.getSeconds(), TimeUnit.SECONDS)
                    .removalListener((Object key, Object value, RemovalCause cause) ->
                            log.info("缓存被删除: {}:{}", key, value))
                    .build();

            cacheManager.registerCustomCache(cacheNamesConfig.getName(), cache);
        }
        return cacheManager;
    }
}
