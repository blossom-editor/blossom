package com.blossom.expand.tracker.core.id;

/**
 * ID生成接口
 *
 * @author xzzz
 * @since 1.2.0
 */
public interface TraceIdGenerator {


    /**
     * 生成 traceId
     * @return traceId
     */
    String traceId();


    /**
     * 生成 spanId
     * @return spanId
     */
    String spanId();

}
