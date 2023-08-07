package com.blossom.expand.tracker.core;

import com.blossom.expand.tracker.core.repository.TrackerRepository;
import com.blossom.expand.tracker.core.adapter.aspect.TrackerStartAspect;
import com.blossom.expand.tracker.core.collector.TrackerCollector;
import com.blossom.expand.tracker.core.collector.TrackerLocalCacheCollector;
import com.blossom.expand.tracker.core.repository.LocalDiskFileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 链路追踪配置
 *
 * @author xzzz
 */
@Slf4j
@Configuration
public class TrackerConfiguration {

    @Bean
    public TrackerStartAspect trackerBeginAspect() {
        return new TrackerStartAspect();
    }

    /**
     * havingValue 为一个不存在的值, 永不开启trace收集
     */
    @Bean(name = "trackerRepository")
    @ConditionalOnProperty(value = "project.tracker.repository.type", havingValue = "disk")
    public TrackerRepository trackerRepository(TrackerProperties trackerProperties) {
        return new LocalDiskFileRepository(trackerProperties);
    }

    /**
     * Tracker span 收集器, 开启收集器才会生效
     *
     * @param trackerProperties 配置
     * @return Tracker span 收集器
     */
    @Bean
    @ConditionalOnBean(name = "trackerRepository")
    @ConditionalOnProperty(value = "project.tracker.collector.enabled", havingValue = "true")
    public TrackerCollector traceCollector(TrackerProperties trackerProperties) {
        return new TrackerLocalCacheCollector(trackerProperties);
    }


}
