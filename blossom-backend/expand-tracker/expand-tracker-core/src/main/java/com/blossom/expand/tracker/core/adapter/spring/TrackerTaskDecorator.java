package com.blossom.expand.tracker.core.adapter.spring;

import cn.hutool.core.util.StrUtil;
import com.blossom.expand.tracker.core.Tracker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskDecorator;

/**
 * Spring task 线程池装饰器
 *
 * @author xzzz
 * @since 1.2.0
 */
@Slf4j
public class TrackerTaskDecorator implements TaskDecorator {

    /**
     * 装饰器名称
     */
    private String decoratorName;

    private String spanType;

    /**
     * 装饰器名称, 将作为异步任务的 spanName
     *
     * @param decoratorName 装饰器名称
     */
    public TrackerTaskDecorator(String decoratorName, String spanType) {
        this.decoratorName = decoratorName;
        this.spanType = spanType;
    }

    /**
     * 嵌套异步线程,只需要设
     * @param runnable 异步线程
     * @return runnable
     */
    @Override
    public Runnable decorate(Runnable runnable) {
        String traceId = Tracker.getTraceId();
        String spanParentId = Tracker.getSpanId();
        return () -> {
            try {
                if (StrUtil.isBlank(traceId) || StrUtil.isBlank(spanParentId)) {
                    Tracker.start(decoratorName, spanType);
                } else {
                    Tracker.fork(decoratorName, spanType, traceId, spanParentId);
                }
                runnable.run();
            } finally {
                Tracker.end();
            }
        };
    }
}
