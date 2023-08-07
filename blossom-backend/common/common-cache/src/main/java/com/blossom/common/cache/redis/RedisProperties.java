package com.blossom.common.cache.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * redis 配置
 *
 * @author xzzz
 */
@Configuration
@ConfigurationProperties(prefix = "project.redis")
public class RedisProperties {

}
