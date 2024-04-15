package com.blossom.common.cache.caffeine;

import com.blossom.common.cache.CacheService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Caffeine 缓存操作
 * @param <K>
 * @param <V>
 */
@Component
@Slf4j
@ConditionalOnProperty(value = "project.cache.type", havingValue = "caffeine")
public class CaffeineCacheService<K, V> implements CacheService<K, V> {
    private final Cache<K, V> cache;

    @Autowired
    public CaffeineCacheService() {
        log.info("[   CACHE] 业务缓存使用 : Caffeine");
        cache = Caffeine.newBuilder().build();
    }

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
    }

    @Override
    public void put(K key, V value, Long expirationInSeconds) {
        cache.policy().expireVariably().ifPresent(e -> e.put(key, value, expirationInSeconds, TimeUnit.SECONDS));
    }

    @Override
    public Optional<V> get(K key, Class<V> valueType) {
        return Optional.ofNullable(cache.getIfPresent(key));
    }

    @Override
    public void remove(K key) {
        cache.invalidate(key);
    }

    @Override
    public void removeAll() {
        cache.invalidateAll();
    }
}
