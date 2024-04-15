package com.blossom.backend.config;

import com.blossom.common.base.exception.XzExceptionSys;
import com.blossom.common.cache.CacheTypeEnum;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * 系统参数初始化
 */
public class ContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        boolean enabled = Boolean.parseBoolean(environment.getProperty("project.base.enabled.redis"));
        boolean status = CacheTypeEnum.redis.name().equals(environment.getProperty("project.cache.type")) && !enabled;
        XzExceptionSys.throwBy(status, "请在配置文件中开启 redis 【project.base.enabled.redis】");
    }
}
