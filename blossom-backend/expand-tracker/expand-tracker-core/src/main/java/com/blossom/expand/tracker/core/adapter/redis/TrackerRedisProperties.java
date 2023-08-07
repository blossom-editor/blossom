package com.blossom.expand.tracker.core.adapter.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Redis 链路追踪配置
 *
 * @author xzzz
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "tracker.redis")
public class TrackerRedisProperties {

    /**
     * 开启 redis 追踪
     */
    private Boolean enabled = true;

    /**
     * redis 请求详情保存至 Tracker.Record 中
     */
    private Boolean detailToRecord = false;
}
