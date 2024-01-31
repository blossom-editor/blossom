package com.blossom.common.cache.caffeine;

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
            .expireAfter(new DynamicExpiry())
            .initialCapacity(100)
            .removalListener((String key, String value, RemovalCause cause) -> log.info(key + " 被删除"))
            .build();

    private static final ScheduledExecutorService clearUpScheduled = Executors.newScheduledThreadPool(1);

    public Test() {
        clearUpScheduled.scheduleWithFixedDelay(this::clear, 0, 1, TimeUnit.SECONDS);
    }

    private void clear() {
        log.info("删除");
        cache.cleanUp();
    }

    public static void main(String[] args) throws InterruptedException {
        Test t = new Test();
        cache.policy().expireVariably().ifPresent(e -> {
            e.put("A1", "1", 7, TimeUnit.SECONDS);
        });
        cache.policy().expireVariably().ifPresent(e -> {
            e.put("A2", "2", 3, TimeUnit.SECONDS);
        });
        Thread.sleep(20 * 1000);
    }

}
