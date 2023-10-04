package com.blossom.expand.sentinel.metric.config;

import com.blossom.expand.sentinel.metric.controller.SentinelMetricController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author xzzz
 */
@Slf4j
@Configuration
public class SentinelMetricConfiguration {

    @Bean
    public SentinelMetricController sentinelMetricController() {
        return new SentinelMetricController();
    }

    @PostConstruct
    public void init() {
         log.debug("[SENTINEL] 指标接口初始化完成");
    }
}
