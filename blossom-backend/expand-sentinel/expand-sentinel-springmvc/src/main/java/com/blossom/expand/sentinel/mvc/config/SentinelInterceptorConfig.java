package com.blossom.expand.sentinel.mvc.config;

import com.blossom.expand.sentinel.mvc.SentinelWebInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;

/**
 * @author : xzzz
 */
@Slf4j
@Configuration
public class SentinelInterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sentinelWebInterceptor()).addPathPatterns("/**");
    }

    @Bean
    public SentinelWebInterceptor sentinelWebInterceptor() {
        return new SentinelWebInterceptor();
    }

    @PostConstruct
    public void init() {
        log.debug("[SENTINEL] 已适配 SPRING_MVC BY INTERCEPTOR");
    }

}
