package com.blossom.common.cache;

import java.util.Optional;

/**
 * 抽象缓存接口
 *
 * @param <K> 缓存Key
 * @param <V> 缓存值
 */
public interface CacheService<K, V> {
    void put(K key, V value);

    /**
     *
     * @param key
     * @param value
     * @param expirationInSeconds 过期时间（秒）
     */
    void put(K key, V value, Long expirationInSeconds);
    Optional<V> get(K key, Class<V> valueType);
    void remove(K key);
    void removeAll();
}
