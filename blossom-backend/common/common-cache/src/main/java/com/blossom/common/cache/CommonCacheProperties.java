package com.blossom.common.cache;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 缓存配置
 *
 * @author xzzz
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "project.cache")
public class CommonCacheProperties {

    /**
     * 缓存的 key 配置
     */
    private List<CacheNamesConfig> namesConfig = new ArrayList<>();

    @Data
    public static class CacheNamesConfig {
        /**
         * 缓存的 key
         */
        private String name;
        /**
         * 缓存的超时时间, 单位毫秒
         */
        private Integer seconds;
        /**
         * 最大缓存数量, 通常是指 caffeine
         */
        private Integer maximumSize = 1000;
    }
}
