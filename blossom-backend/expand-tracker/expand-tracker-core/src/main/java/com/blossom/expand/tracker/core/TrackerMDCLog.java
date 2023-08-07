package com.blossom.expand.tracker.core;

import com.blossom.expand.tracker.core.common.TrackerConstants;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

/**
 * 日志MDC拓展
 *
 * @author xzzz
 * @since 1.2.0
 */
@Slf4j
public class TrackerMDCLog {

    /**
     * 设置 traceId 到日志 MDC
     * @param traceId traceId
     */
    protected static void traceId(String traceId) {
        try {
            MDC.put(TrackerConstants.MDC_TRACE_ID_KEY,traceId);
        } catch (IllegalArgumentException e) {
            log.error("[TRACKERS] 设置日志MDC拓展错误:{}",e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 设置 spanId 到日志 MDC
     * @param spanId spanId
     */
    protected static void spanId(String spanId) {
        try {
            MDC.put(TrackerConstants.MDC_SPAN_ID_KEY,spanId);
        } catch (IllegalArgumentException e) {
            log.error("[TRACKERS] 设置日志MDC拓展错误:{}",e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 删除日志MDC
     */
    protected static void removeAll() {
        try {
            MDC.remove(TrackerConstants.MDC_TRACE_ID_KEY);
            MDC.remove(TrackerConstants.MDC_SPAN_ID_KEY);
        } catch (IllegalArgumentException e) {
            log.error("[TRACKERS] 删除日志MDC拓展错误:{}",e.getMessage());
            e.printStackTrace();
        }
    }
}
