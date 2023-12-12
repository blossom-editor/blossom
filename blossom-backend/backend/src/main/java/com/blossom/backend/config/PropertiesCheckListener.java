package com.blossom.backend.config;

import cn.hutool.core.util.StrUtil;
import com.blossom.common.base.util.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * 启动检查配置文件内容, 用于检查配置项是否正确
 *
 * @since 1.9.0
 */
@Slf4j
public class PropertiesCheckListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment env = event.getEnvironment();
        log.warn("\n\n正在检查 Blossom 后台配置项\n\n" +
                        "\n[CHECK] ==========================================================================================================================" +
                        "\n[CHECK] 使用环境: [{}], 版本: [{}]" +
                        "\n[CHECK] ==========================================================================================================================" +
                        "\n[CHECK] 数据库配置: {}" +
                        "\n[CHECK] 数据库用户: {}" +
                        "\n[CHECK] 数据库密码: {}" +
                        "\n[CHECK] 图片前缀: {}" +
                        "\n[CHECK] 图片存储: {}" +
                        "\n[CHECK] ==========================================================================================================================\n\n",
                get(env, SpringUtil.PROFILE_ACTION), get(env, "project.base.version"),
                get(env, "spring.datasource.url"),
                get(env, "spring.datasource.username"),
                get(env, "spring.datasource.password"),
                get(env, "project.iaas.blos.domain"),
                get(env, "project.iaas.blos.default-path")
        );

        String defaultPath = get(env, "project.iaas.blos.default-path");

        if (StrUtil.isBlank(defaultPath)) {
            log.error("【配置错误】你需要填写图片的默认存储地址 project.iaas.blos.default-path");
            throw new IllegalArgumentException("你需要填写图片的默认存储地址 project.iaas.blos.default-path");
        }

        if ("/".equals(defaultPath)) {
            log.error("【配置错误】图片的默认存储地址(project.iaas.blos.default-path)不能是根目录 \"/\"");
            throw new IllegalArgumentException("图片的默认存储地址(project.iaas.blos.default-path)不能是根目录 \"/\"");
        }
    }

    private String get(ConfigurableEnvironment env, String prop) {
        return env.getProperty(prop);
    }
}
