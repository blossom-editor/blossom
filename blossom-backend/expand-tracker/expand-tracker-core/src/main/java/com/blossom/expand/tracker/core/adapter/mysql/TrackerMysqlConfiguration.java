package com.blossom.expand.tracker.core.adapter.mysql;

import org.apache.ibatis.session.SqlSession;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 链路追踪 mysql 配置
 *
 * @author xzzz
 */
@Configuration
@ConditionalOnClass(value = SqlSession.class)
public class TrackerMysqlConfiguration {

    @Bean
    public TrackerMysqlInterceptor trackerMysqlInterceptor() {
        return new TrackerMysqlInterceptor();
    }
}
