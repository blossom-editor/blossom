package com.blossom.backend.config;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.blossom.backend.base.auth.interceptor.UserTypeInterceptor;
import com.blossom.common.base.util.spring.SpringUtil;
import com.blossom.expand.tracker.core.adapter.spring.TrackerTaskDecorator;
import com.blossom.expand.tracker.core.common.TrackerConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.CacheControl;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.servlet.config.annotation.*;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @author jasmineXz
 */
@Slf4j
@Configuration
public class WebConfigurer implements WebMvcConfigurer {

    private ThreadPoolTaskExecutor executor;

    @Bean("taskExecutor")
    public Executor taskExecutor(Environment env) {
        log.debug("[TRACKERS] 已经适配框架 : Spring Task");
        executor = new ThreadPoolTaskExecutor();
        // 核心线程池10
        executor.setCorePoolSize(10);
        // 最大线程池20
        executor.setMaxPoolSize(20);
        // 队列容量
        executor.setQueueCapacity(200);
        // 当线程空闲时间达到keepAliveTime，该线程会退出，直到线程数量等于corePoolSize。
        executor.setKeepAliveSeconds(60);
        // 线程名称前缀
        executor.setThreadNamePrefix(env.getProperty(SpringUtil.APP_NAME) + "-task-");
        // 用来设置线程池关闭的时候等待所有任务都完成再继续销毁其他的Bean
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 等待时间
        executor.setAwaitTerminationSeconds(60);
        // 增加 TaskDecorator 属性的配置
        executor.setTaskDecorator(new TrackerTaskDecorator(
                "SPRING_ASYNC_TASK", TrackerConstants.SPAN_TYPE_SPRING_ASYNC));
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // 处理未捕获异常日志打印
        final Thread.UncaughtExceptionHandler uncaughtExceptionHandler = (t, e) ->
                log.error("异步线程执行失败。异常信息 => {} : ", e.getMessage(), e);
        ThreadFactoryBuilder threadFactoryBuilder = new ThreadFactoryBuilder();
        threadFactoryBuilder.setNamePrefix(env.getProperty(SpringUtil.APP_NAME) + "-task-");
        threadFactoryBuilder.setUncaughtExceptionHandler(uncaughtExceptionHandler);
        executor.setThreadFactory(threadFactoryBuilder.build());
        executor.initialize();
        return executor;
    }

    /**
     * 定时任务线程池
     */
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(10);
        taskScheduler.setThreadNamePrefix("bl-sche-");
        final Thread.UncaughtExceptionHandler uncaughtExceptionHandler = (t, e) -> log.error("定时任务执行失败。异常信息 => {} : ", e.getMessage(), e);
        ThreadFactoryBuilder threadFactoryBuilder = new ThreadFactoryBuilder();
        threadFactoryBuilder.setNamePrefix("bl-sche-");
        threadFactoryBuilder.setUncaughtExceptionHandler(uncaughtExceptionHandler);
        taskScheduler.setThreadFactory(threadFactoryBuilder.build());
        taskScheduler.initialize();
        return taskScheduler;
    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setTaskExecutor(executor);
    }

    @Bean
    public UserTypeInterceptor userTypeInterceptor() {
        return new UserTypeInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userTypeInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/blog/**",
                        "/editor/**",
                        "/error"
                );

    }

    /**
     * 页面映射
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/blog/").setViewName("/blog/index.html");
        registry.addViewController("/editor/").setViewName("/editor/index.html");
    }

    /**
     * 静态文件处理
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/editor/**")
                .addResourceLocations("classpath:/static/editor/")
                .setCacheControl(CacheControl.maxAge(48, TimeUnit.HOURS).cachePublic());
        registry.addResourceHandler("/blog/**")
                .addResourceLocations("classpath:/static/blog/")
                .setCacheControl(CacheControl.maxAge(48, TimeUnit.HOURS).cachePublic());
    }
}
