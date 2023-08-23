package com.blossom.common.base.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Test {

    private static final Cache<String, String> cache = Caffeine.newBuilder()
            .initialCapacity(100)
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .removalListener((String key, String value, RemovalCause cause) -> System.out.println(key + " 被删除"))
            .build();
    private static final ScheduledExecutorService clearUpScheduled = Executors.newScheduledThreadPool(1);

    public Test() {
        clearUpScheduled.scheduleWithFixedDelay(this::clear, 5, 1, TimeUnit.SECONDS);
    }

    private void clear() {
        log.info("尝试过期缓存");
        cache.cleanUp();
    }

    public static void main(String[] args) {
        Test test = new Test();
        cache.policy().expireVariably().ifPresent(e -> {
        });
    }

}
