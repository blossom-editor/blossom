package com.blossom.common.base.enums;


import cn.hutool.core.util.ClassLoaderUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xzzz
 */
@Slf4j
@SuppressWarnings("all")
public enum FrameworkEnum {
    /**
     * 各类框架的索引类, 用来查询是否使用了此框架
     */
    SPRINGBOOT     ("org.springframework.boot.SpringApplication"),
    DUBBO          ("org.apache.dubbo.rpc.protocol.dubbo.DubboProtocolServer"),
    ROCKET_MQ      ("org.apache.rocketmq.common.Configuration"),
    APOLLO         ("com.ctrip.framework.apollo.core.utils.ApolloThreadFactory"),
    MYSQL          ("com.mysql.cj.jdbc.ConnectionImpl"),
    REDIS          ("org.springframework.data.redis.connection.lettuce.DefaultLettucePoolingClientConfiguration"),
    ELASTIC_SEARCH ("org.elasticsearch.client.RestClientBuilder"),
    SENTRY         ("io.sentry.protocol.SentryThread"),
    XXL_JOB        ("com.xxl.job.core.thread.ExecutorRegistryThread"),
    SENTINEL       ("com.alibaba.csp.sentinel.Env"),
    ZOOKEEPER      ("org.apache.zookeeper.ZooKeeper"),

    /**
     * 自研模块
     */
    TRACE          ("com.blossom.expand.tracker.core.Tracker"),
    ;

    @Getter
    private final String clazz;

    FrameworkEnum(String clazz) {
        this.clazz = clazz;
    }

    public static List<FrameworkEnum> getExistFramework() {
        List<FrameworkEnum> frameworkEnums = new ArrayList<>();

        for (FrameworkEnum framework : FrameworkEnum.values()) {
            try {
                if (ClassLoaderUtil.isPresent(framework.getClazz())) {
                    frameworkEnums.add(framework);
                }
            } catch (Exception e) {
                log.warn("检查框架错误:{}", e.getMessage());
            }
        }
        return frameworkEnums;
    }

    public static String getExistFrameworkStr() {
        List<FrameworkEnum> frameworkEnums = getExistFramework();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < frameworkEnums.size(); i++) {
            sb.append(frameworkEnums.get(i).name());
            if ((i + 1) < frameworkEnums.size()) {
                sb.append(", ");
            }
        }
        for (FrameworkEnum frameworkEnum : frameworkEnums) {

        }
        return sb.toString();
    }

}

