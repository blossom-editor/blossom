package com.blossom.common.cache.caffeine;

import com.github.benmanes.caffeine.cache.Expiry;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * key 使用不同的过期时间
 *
 * @since 1.13.0
 */
public class DynamicExpiry implements Expiry<String, Object> {

    @Override
    public long expireAfterCreate(@NonNull String key, @NonNull Object value, long currentTime) {
        return 0;
    }

    @Override
    public long expireAfterUpdate(@NonNull String key, @NonNull Object value, long currentTime, @NonNegative long currentDuration) {
        return currentDuration;
    }

    @Override
    public long expireAfterRead(@NonNull String key, @NonNull Object value, long currentTime, @NonNegative long currentDuration) {
        return currentDuration;
    }
}
