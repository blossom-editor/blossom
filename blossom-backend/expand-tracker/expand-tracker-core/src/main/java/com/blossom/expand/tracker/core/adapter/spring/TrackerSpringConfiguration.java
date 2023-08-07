package com.blossom.expand.tracker.core.adapter.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xzzz
 * @since 1.2.0
 */
@Slf4j
@Configuration
public class TrackerSpringConfiguration {

    @Bean
    public TrackerFilter trackerFilter() {
        return new TrackerFilter();
    }
}
