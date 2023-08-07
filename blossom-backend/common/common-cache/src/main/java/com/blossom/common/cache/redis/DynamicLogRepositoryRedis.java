package com.blossom.common.cache.redis;

import cn.hutool.core.util.StrUtil;
import com.blossom.common.base.BaseProperties;
import com.blossom.common.base.log.DynamicLogRepository;
import com.blossom.common.base.util.json.JsonUtil;
import com.blossom.common.base.util.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 监听 redis
 *
 * @author xzzz
 */
@Slf4j
@Component("DynamicLogRepositoryRedis")
@ConditionalOnClass(RedisTemplate.class)
public class DynamicLogRepositoryRedis implements DynamicLogRepository {

    private final StringRedisTemplate redisTemplate;
    private final LoggingSystem loggingSystem;
    private final ScheduledExecutorService restore = Executors.newScheduledThreadPool(1);
    private final String TOPIC_NAME = "topic_dynamic_log";

    @Autowired
    private SimpleLock simpleLock;

    public DynamicLogRepositoryRedis(LoggingSystem loggingSystem,
                                     StringRedisTemplate redisTemplate,
                                     RedisMessageListenerContainer listenerContainer,
                                     BaseProperties properties) {
        log.info("[    BASE] 日志级别存储 : Redis, 配置持续时长[{}ms], 刷新间隔[{}ms]",
                properties.getLog().getDuration(), properties.getLog().getRestoreDuration());
        this.redisTemplate = redisTemplate;
        this.loggingSystem = loggingSystem;
        listenerContainer.addMessageListener(new DynamicLogListener(this), new ChannelTopic(TOPIC_NAME));
        restore.scheduleWithFixedDelay(this::restore, 5000, properties.getLog().getRestoreDuration(), TimeUnit.MILLISECONDS);
    }

    /**
     * 保存日志级别
     */
    @Override
    public void save(LevelWrapper levelWrapper) {
        log.info("[    BASE] 刷新日志级别 {}:{}", levelWrapper.getPath(), levelWrapper.getLevel());
        LogLevel logLevel = LogLevel.valueOf(levelWrapper.getLevel().toUpperCase());
        // 1. 设置日志级别
        loggingSystem.setLogLevel(levelWrapper.getPath(), logLevel);
        // 2. 如果设置的不是INFO级别, 则需要发布消息让集群同步日志级别
        redisTemplate.opsForHash().put(SpringUtil.getAppName(), levelWrapper.getPath(), JsonUtil.toJson(levelWrapper));
        redisTemplate.convertAndSend(TOPIC_NAME, JsonUtil.toJson(levelWrapper));
    }

    /**
     * 重新加载, 集群中只有一台会从Redis获取数据
     * <p>1. 如果手动修改了 Redis 配置, 该方法会重新同步, 未发生改变的也会再次同步
     * <p>2. 如果级别过去, 该方法会将集群重置为 INFO
     */
    public void restore() {
        boolean isLock = simpleLock.tryLock(SpringUtil.getAppName());
        if (isLock) {
            try {
                Map<Object, Object> levelMap = redisTemplate.opsForHash().entries(SpringUtil.getAppName());
                List<LevelWrapper> expireLevels = new ArrayList<>();
                levelMap.forEach((pathObj, levelObj) -> {
                    LevelWrapper levelWrapper = JsonUtil.toObj(levelObj.toString(), LevelWrapper.class);
                    if (levelWrapper == null) {
                        return;
                    }
                    if (levelWrapper.getExpire() < System.currentTimeMillis()) {
                        expireLevels.add(levelWrapper);
                    } else {
                        this.save(levelWrapper);
                    }
                });

                // 重置为 INFO
                for (LevelWrapper levelWrapper : expireLevels) {
                    redisTemplate.opsForHash().delete(SpringUtil.getAppName(), levelWrapper.getPath());
                    levelWrapper.setLevel(LogLevel.INFO.name());
                    log.info("[    BASE] 重置日志级别 {}:{}", levelWrapper.getPath(), levelWrapper.getLevel());
                    redisTemplate.convertAndSend(TOPIC_NAME, JsonUtil.toJson(levelWrapper));
                }
            } finally {
                simpleLock.release(SpringUtil.getAppName());
            }
        }
    }

    /**
     * 监听事件
     *
     * @param levelWrapper 事件
     */
    public void listener(LevelWrapper levelWrapper) {
        if (levelWrapper == null) {
            return;
        }
        loggingSystem.setLogLevel(levelWrapper.getPath(), LogLevel.valueOf(levelWrapper.getLevel().toUpperCase()));
    }


    /**
     * 监听器
     */
    private static class DynamicLogListener implements MessageListener {
        private final DynamicLogRepositoryRedis repositoryRedis;

        public DynamicLogListener(DynamicLogRepositoryRedis repositoryRedis) {
            this.repositoryRedis = repositoryRedis;
        }

        @Override
        public void onMessage(Message message, byte[] pattern) {
            String body = new String(message.getBody());
            if (StrUtil.isBlank(body)) {
                return;
            }
            repositoryRedis.listener(JsonUtil.toObj(body, LevelWrapper.class));
        }
    }

}
