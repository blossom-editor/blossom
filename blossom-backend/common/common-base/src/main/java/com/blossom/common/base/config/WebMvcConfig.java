package com.blossom.common.base.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

/**
 * MVC 配置
 *
 * @author xzzz
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 静态资源
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/favicon.ico")
                .addResourceLocations("classpath:/static/favicon.png")
                .setCacheControl(CacheControl.maxAge(10, TimeUnit.SECONDS).cachePublic());
    }

    /**
     * 过滤器方式配置跨域
     *
     * @return 跨域过滤器
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> filterFilterRegistrationBean() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowCredentials(true);
        // 允许的请求来源
        corsConfig.addAllowedOriginPattern("*");
        // 允许的请求头
        corsConfig.addAllowedHeader("*");
        // 暴露头信息
        corsConfig.addExposedHeader(HttpHeaders.SET_COOKIE);
        // 允许的请求方式
        corsConfig.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        // 过滤器优先级
        bean.setOrder(Integer.MIN_VALUE);
        return bean;
    }
}
