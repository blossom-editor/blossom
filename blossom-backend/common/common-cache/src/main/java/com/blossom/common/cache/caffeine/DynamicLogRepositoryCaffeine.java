package com.blossom.common.cache.caffeine;

import com.blossom.common.base.BaseProperties;
import com.blossom.common.base.log.DynamicLogRepository;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 动态日志级别配置, 使用 caffeine 作为缓存实现。无法在集群中同步日志级别, 只能单机使用。
 *
 * @author xzzz
 */
@Slf4j
@Component("dynamicLogRepositoryCaffeine")
@ConditionalOnProperty(value = "project.cache.type", havingValue = "caffeine")
public class DynamicLogRepositoryCaffeine implements DynamicLogRepository {

    private final LoggingSystem loggingSystem;
    private final Cache<String, String> cache;
    private final ScheduledExecutorService clearUpScheduled = Executors.newScheduledThreadPool(1);

    public DynamicLogRepositoryCaffeine(LoggingSystem loggingSystem, BaseProperties properties) {
        log.info("[    BASE] 日志级别存储 : Caffeine, 配置持续时长[{}ms], 刷新间隔[{}ms]",
                properties.getLog().getDuration(), properties.getLog().getRestoreDuration());
        cache = Caffeine.newBuilder()
                .initialCapacity(100)
                .expireAfterWrite(properties.getLog().getDuration(), TimeUnit.SECONDS)
                .removalListener((String key, String value, RemovalCause cause) -> this.restore(key))
                .build();
        clearUpScheduled.scheduleWithFixedDelay(this::clear, 10, properties.getLog().getRestoreDuration(), TimeUnit.SECONDS);
        this.loggingSystem = loggingSystem;
    }

    private void restore(String path) {
        loggingSystem.setLogLevel(path, LogLevel.INFO);
    }

    private void clear() {
        cache.cleanUp();
    }

    /**
     * @param levelWrapper 日志级别封装
     */
    @Override
    public void save(LevelWrapper levelWrapper) {
        String path = levelWrapper.getPath();
        LogLevel level = LogLevel.valueOf(levelWrapper.getLevel().toUpperCase());
        loggingSystem.setLogLevel(path, level);
        cache.put(path, level.name());
    }
}
