package com.blossom.common.db;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 数据库配置
 *
 * @author xzzz
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "project.db")
public class DBProperties {

    /**
     * 慢SQL指标, 单位毫秒
     */
    private long slowInterval = 2000L;

}
