package com.blossom.expand.tracker.core.adapter.aspect;

import cn.hutool.core.util.StrUtil;
import com.blossom.expand.tracker.core.SpanContext;
import com.blossom.expand.tracker.core.Tracker;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 创建追踪切面
 *
 * @author xzzz
 * @since 1.2.0
 */
@Slf4j
@Aspect
@Component
public class TrackerStartAspect {

    /**
     * 通过注解 start 或 fork 到追踪中
     * @param point 方法
     * @param trackerStart 注解
     * @return 返回值
     */
    @Around("@annotation(trackerStart)")
    public Object around(ProceedingJoinPoint point, TrackerStart trackerStart) throws Throwable {
        SpanContext span = null;
        try {
            String spanName = trackerStart.spanName();
            if (StrUtil.isBlank(spanName)) {
                // 取方法名称
                spanName = point.getSignature().getDeclaringTypeName();
            }
            span = Tracker.start(spanName, trackerStart.spanType());
            return point.proceed(point.getArgs());
        } finally {
            if (span != null) {
                Tracker.end();
            }
        }
    }
}
