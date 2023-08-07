package com.blossom.expand.tracker.core.id;


import cn.hutool.core.lang.UUID;

/**
 * trace 生成类
 *
 * @author xzzz
 * @since 1.2.0
 */
public class TraceIdGeneratorUUID implements TraceIdGenerator {

    /**
     * 生成 traceId
     * @return traceId
     */
    @Override
    public String traceId () {
        return generator();
    }

    /**
     * 生成 spanId
     * @return spanId
     */
    @Override
    public String spanId () {
        return generator();
    }

    /**
     * 生成ID
     * @return ID
     */
    private String generator() {
        return UUID.fastUUID().toString(true).toUpperCase();
    }

}
