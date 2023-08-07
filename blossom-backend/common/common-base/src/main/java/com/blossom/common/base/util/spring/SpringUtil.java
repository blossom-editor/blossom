package com.blossom.common.base.util.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

/**
 * Spring 环境配置工具类
 *
 * @author xzzz
 * @since 0.0.1
 */
@Slf4j
@Component
@Lazy(false)
public class SpringUtil implements EnvironmentAware, ApplicationContextAware, DisposableBean {

    private static ApplicationContext applicationContext = null;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 实现ApplicationContextAware接口, 注入Context到静态变量中.
     */
    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) {
        SpringUtil.applicationContext = applicationContext;
    }

    /**
     * 从静态变量 applicationContext 中取得 Bean, 自动转型为所赋值对象的类型.
     */
    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    /**
     * 从静态变量 applicationContext 中取得 Bean, 自动转型为所赋值对象的类型.
     */
    public static <T> T getBean(Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }

    /**
     * 发布事件
     */
    public static void publishEvent(ApplicationEvent event) {
        if (applicationContext == null) {
            return;
        }
        applicationContext.publishEvent(event);
    }

    /**
     * 实现DisposableBean接口, 在Context关闭时清理静态变量.
     */
    @Override
    public void destroy() {
        try {
            applicationContext = null;
        } catch (Exception ignored) {
        }
    }

    private static Environment environment;

    /**
     * 当前应用名称
     */
    public static final String APP_NAME = "spring.application.name";
    /**
     * 当前环境
     */
    public static final String PROFILE_ACTION = "spring.profiles.active";
    /**
     * 应用基础路径
     */
    public static final String SERVLET_CONTEXT_PATH = "server.servlet.context-path";
    /**
     * 端口
     */
    public static final String SERVER_PORT = "server.port";
    /**
     * 本项目说明
     */
    private static final String ENV_LEARNING_DESCRIPTION = "learning.description";
    /**
     * 本项目框架版本
     */
    private static final String ENV_LEARNING_FRAMEWORK_VERSION = "learning.frameworkVersion";

    @Override
    public void setEnvironment(@NotNull Environment environment) {
        SpringUtil.environment = environment;
    }

    public static String get(String key) {
        return environment.getProperty(key);
    }

    public static String getPort() {
        return environment.getProperty(SERVER_PORT);
    }

    public static String getDesc() {
        return environment.getProperty(ENV_LEARNING_DESCRIPTION);
    }

    public static String getFrameworkVersion() {
        return environment.getProperty(ENV_LEARNING_FRAMEWORK_VERSION);
    }

    public static String getAppName() {
        return getApplicationContext().getEnvironment().getProperty(APP_NAME);
    }

    public static String getProfileAction() {
        return getApplicationContext().getEnvironment().getProperty(PROFILE_ACTION);
    }

    public static String getServletContextPath() {
        return getApplicationContext().getEnvironment().getProperty(SERVLET_CONTEXT_PATH);
    }
}
