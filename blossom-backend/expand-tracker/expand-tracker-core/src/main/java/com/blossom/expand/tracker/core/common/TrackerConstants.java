package com.blossom.expand.tracker.core.common;

import com.blossom.expand.tracker.core.adapter.aspect.TrackerStart;
import com.blossom.expand.tracker.core.adapter.spring.TrackerTaskDecorator;

import java.util.HashSet;
import java.util.Set;

/**
 * 静态参数
 *
 * @author xzzz
 * @since 1.2.0
 */
public class TrackerConstants {

    public static final String ROOT_SPAN = "ROOT_SPAN";

    /**
     * 应用启动
     */
    public static final String SPAN_TYPE_APPLICATION_RUN = "APPLICATION_RUN";

    /**
     * 通过注解的形式 {@link TrackerStart}
     */
    public static final String SPAN_TYPE_ANNOTATION       = "ANNOTATION";

    /**
     * Spring 异步线程池装饰器类型 {@link TrackerTaskDecorator}
     */
    public static final String SPAN_TYPE_SPRING_ASYNC     = "SPRING_ASYNC";

    /**
     * Spring 定时线程池装饰器类型 {@link TrackerTaskDecorator}
     */
    public static final String SPAN_TYPE_SPRING_SCHEDULED = "SPRING_SCHEDULED";

    /**
     * 调用 ES
     */
    public static final String SPAN_TYPE_ELASTICSEARCH = "ELASTIC_SEARCH";

    /**
     * 服务被外部直接调用网关接口
     */
    public static final String SPAN_TYPE_HTTP_GATEWAY = "HTTP_GATEWAY";

    /**
     * 内部服务被网关调用
     */
    public static final String SPAN_TYPE_HTTP_GATEWAY_INVOKE = "HTTP_GATEWAY_INVOKE";

    /**
     * 服务被外部直接调用 HTTP 接口
     */
    public static final String SPAN_TYPE_HTTP_MVC = "HTTP_MVC";

    /**
     * 内部服务被 FEIGN 调用
     */
    public static final String SPAN_TYPE_HTTP_FEIGN_INVOKE = "HTTP_FEIGN_INVOKE";

    /**
     * OKHTTP 调用他方
     */
    public static final String SPAN_TYPE_HTTP_OKHTTP  = "HTTP_OKHTTP";

    /**
     * 被内部 OKHTTP 工具调用
     */
    public static final String SPAN_TYPE_HTTP_OKHTTP_INVOKE  = "HTTP_OKHTTP_INVOKE";

    /**
     * 追踪到请求 REDIS
     */
    public static final String SPAN_TYPE_REDIS = "REDIS";

    /**
     * 追踪到请求 MySQL
     */
    public static final String SPAN_TYPE_MYSQL = "MYSQL";

    /**
     * 通过HTTP传递参数时使用的请求头
     * <p>格式: traceId|spanId|spanType
     */
    public static final String HTTP_HEADERS = "XZ_TRACE";

    /**
     * MDC 中 Trace 的 key
     */
    public static final String MDC_TRACE_ID_KEY = "traceId";

    /**
     * MDC 中 Span 的 key
     */
    public static final String MDC_SPAN_ID_KEY = "spanId";

    public static final Integer DEFAULT_COLLECTOR_LOCAL_CACHE = 5000;
    public static final Integer DEFAULT_COLLECTOR_RATE = 1;
    public static final Integer COLLECTOR_MAX_RATE = 1000;

    /**
     * 忽略的追踪信息
     */
    public static final Set<String> ignoreSpanName = new HashSet<String>(){{
        this.add("/tracker/metric");
    }};

    public static final Set<String> ignoreInnerApi = new HashSet<String>(){{
        this.add("/tracker/metric");
    }};

    public static final Set<String> ignoreInnerSql = new HashSet<String>(){{
        this.add("com.blossomexpand.tracker.server.mappers.TrackerMetricMySqlMapper.save");
    }};

    /**
     * Trace 的API
     */
    interface API {
        String SERVER_SAVE_SPAN = "/tracker/metric";
    }

}

