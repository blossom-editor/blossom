package com.blossom.expand.tracker.core.id;

/**
 * 追踪ID生成
 *
 * @author xzzz
 */
public class TraceIdGeneratorUtil {
    /**
     * TraceGenerator traceId 生成器
     */
    private static final TraceIdGenerator traceGenerator = new TraceIdGeneratorUUID();

    public static String traceId() {
        return traceGenerator.traceId();
    }

    public static String spanId() {
        return traceGenerator.spanId();
    }
}
