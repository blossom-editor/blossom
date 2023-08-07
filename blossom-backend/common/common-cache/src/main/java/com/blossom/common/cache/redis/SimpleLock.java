package com.blossom.common.cache.redis;

import cn.hutool.core.util.StrUtil;
import com.blossom.common.base.util.spring.SpringUtil;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * 最简单的分布式锁(setnx), 适用于非严格场景.
 * <p>不可重入锁
 * <p>不考虑锁续期
 * <p>锁超时由 redis 自动释放
 *
 * @author xzzz
 */
public class SimpleLock {

    private final String LOCK_PREFIX = ":simple_lock:";
    private final long TIMEOUT = 30 * 1000;
    private final StringRedisTemplate redisTemplate;

    public SimpleLock(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean tryLock(String key) {
        if (StrUtil.isBlank(key)) {
            return false;
        }
        Boolean result = redisTemplate.opsForValue()
                .setIfAbsent(buildKey(key), "lock", TIMEOUT, TimeUnit.MILLISECONDS);
        return result != null && result;
    }

    public boolean release(String key) {
        Boolean result = redisTemplate.delete(buildKey(key));
        return result != null && result;
    }

    private String buildKey(String key) {
        return SpringUtil.getAppName() + LOCK_PREFIX + key;
    }
}
