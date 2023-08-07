
package com.blossom.expand.tracker.core.adapter.redis;

import cn.hutool.core.util.ArrayUtil;
import com.blossom.expand.tracker.core.SpanContext;
import com.blossom.expand.tracker.core.common.TrackerConstants;
import com.blossom.expand.tracker.core.Tracker;
import com.blossom.common.base.util.ProxyUtils;
import com.blossom.common.base.util.json.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * 使用自定义代理完成日志输出
 */
@Slf4j
class RedisConnectionFactoryProxy {

    /**
     * 追踪时忽略的方法
     */
    private static final String IGNORE_IS_PIPELINED = "isPipelined";
    private static final String IGNORE_IS_QUEUEING = "isQueueing";
    private static final String IGNORE_CLOSE = "close";

    /**
     * 被代理方法
     */
    private static final String PROXY_METHOD = "getConnection";

    private TrackerRedisProperties trackerRedisProperties;

    public RedisConnectionFactoryProxy(TrackerRedisProperties trackerRedisProperties) {
        this.trackerRedisProperties = trackerRedisProperties;
    }

    /**
     * 如果是 getConnection 方法，把返回结果进行代理包装
     *
     * @param invocation 方法
     * @return
     */
    public Object interceptorRedisFactory(MethodInvocation invocation) throws Throwable {
        Object ret = invocation.proceed();
        String methodName = invocation.getMethod().getName();
        if (PROXY_METHOD.equals(methodName)) {
            return ProxyUtils.getProxy(ret, this::getConnectionMethodProxy);
        }
        return ret;
    }

    /**
     * 拦截 redisConnectionFactory.getConnection 的方法, 来进行追踪 span 中的 redis 节点
     *
     * @param invocation 调用方法
     * @return 方法调用结果
     * @throws Throwable 异常
     */
    private Object getConnectionMethodProxy(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        String methodName = method.getName();

        // 忽略的REDIS命令
        if (IGNORE_IS_PIPELINED.equals(methodName) || IGNORE_IS_QUEUEING.equals(methodName) || IGNORE_CLOSE.equals(methodName)) {
            return invocation.proceed();
        }

        SpanContext spanContext = Tracker.start(methodName, TrackerConstants.SPAN_TYPE_REDIS);

        if (trackerRedisProperties.getDetailToRecord()) {
            paramToRecord(invocation.getArguments());
        }

        try {
            return invocation.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw throwable;
        } finally {
            if (spanContext != null) {
                Tracker.end();
            }
        }
    }


    private void paramToRecord(Object[] params) {
        if (ArrayUtil.isEmpty(params)) {
            return;
        }
        for (int i = 0; i < params.length; i++) {
            Tracker.record("key_" + (i + 1), deserialization(params[i]));
        }
    }

    private String deserialization(Object obj) {
        if (obj == null) {
            return "";
        }
        try {
            if (obj instanceof byte[]) {
                obj = new String((byte[]) obj);
            }
            if (obj instanceof String) {
                return obj.toString();
            }
            return JsonUtil.toJson(obj);
        } catch (Exception ex) {
            log.error(String.format("%s serialize error: %s", obj.getClass().getName(), ex.toString()));
            return obj.toString();
        }
    }
}
