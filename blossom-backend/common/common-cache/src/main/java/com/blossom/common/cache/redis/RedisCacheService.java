package com.blossom.common.cache.redis;

import com.blossom.common.base.util.json.JsonUtil;
import com.blossom.common.cache.CacheService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Redis 缓存操作
 * @param <K>
 * @param <V>
 */
@Component
@Slf4j
@ConditionalOnProperty(value = "project.cache.type", havingValue = "redis")
public class RedisCacheService<K, V> implements CacheService<K, V> {
    public RedisCacheService() {
        log.info("[   CACHE] 业务缓存使用 : Redis");
    }

    @Autowired
    private RedisTemplate<K, String> redisTemplate;

    @Override
    public void put(K key, V value) {
        redisTemplate.opsForValue().set(key, JsonUtil.toJson(value));
    }

    @Override
    public void put(K key, V value, Long expirationInSeconds) {
        redisTemplate.opsForValue().set(key, JsonUtil.toJson(value));
        redisTemplate.expire(key, expirationInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public Optional<V> get(K key, Class<V> valueType) {
        return Optional.ofNullable(JsonUtil.toObj(redisTemplate.opsForValue().get(key), valueType));
    }


    @Override
    public void remove(K key) {
        redisTemplate.delete(key);
    }

    @Override
    public void removeAll() {
        redisTemplate.execute((RedisCallback<Object>) connection -> {
            connection.flushDb();
            return null;
        });
    }
}
