package com.blossom.backend.config;

import com.blossom.backend.base.auth.interceptor.UserTypeInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * @author jasmineXz
 */
@Slf4j
@Configuration
public class WebConfigurer implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 拦截所有请求，通过判断是否有 @myInterceptor 注解 决定是否需要登录
        registry.addInterceptor(myInterceptor()).addPathPatterns("/**");
    }

    @Bean
    public UserTypeInterceptor myInterceptor() {
        return new UserTypeInterceptor();
    }
}
