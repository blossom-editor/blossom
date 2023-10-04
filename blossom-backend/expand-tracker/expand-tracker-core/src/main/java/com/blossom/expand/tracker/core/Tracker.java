package com.blossom.expand.tracker.core;

import com.blossom.expand.tracker.core.collector.TrackerCollector;
import com.blossom.common.base.util.spring.SpringUtil;
import com.blossom.common.base.util.threadlocal.InternalThreadLocal;
import lombok.extern.slf4j.Slf4j;


/**
 * 链路追踪管理工具,只有此类会对外部暴露
 *
 * @author xzzz
 * @since 1.2.0
 */
@Slf4j
public class Tracker implements AutoCloseable {

    /**
     * <p>复制来自 Dubbo {@see org.apache.dubbo.common.threadlocal.InternalThreadLocal}
     * <p>代码源自 Netty {@see io.netty.util.concurrent.FastThreadLocal}
     */
    private static final InternalThreadLocal<SpanContext> TRACE_CONTEXT = new InternalThreadLocal<>();

    private static TrackerCollector trackerCollector;

    private static boolean needCollectSpan = true;

    /**
     * 开始追踪
     *
     * <p>如果当前线程已存在 span 信息，则本次 start 会成为一个 child span
     *
     * @param name 本次 span 名称
     * @param type 本次 span 类型
     * @return TraceNode 对象
     */
    public static SpanContext start(String name, String type) {
        try {
            SpanContext context = TRACE_CONTEXT.get();
            // 如果上下文不存在则新建，存在则保存到当前上下文链表
            if (context == null) {
                context = new SpanContext();
                SpanNode currentSpan = SpanNode.rootSpan(name, type);
                // 添加到上下文链表中
                context.add(currentSpan);
                TRACE_CONTEXT.set(context);
                TrackerMDCLog.traceId(currentSpan.getTraceId());
                TrackerMDCLog.spanId(currentSpan.getSpanId());
                return context;
            } else {
                String traceId = Tracker.getTraceId();
                String spanParentId = Tracker.getSpanId();
                return fork(name, type, traceId, spanParentId);
            }

        } catch (Exception e) {
            log.error("[TRACKERS] 追踪开始时发生错误: {}", e.getMessage());
            return null;
        }
    }

    /**
     * fork 与 start
     *
     * @param name
     * @param type
     * @param traceId
     * @param spanParentId
     * @return
     */
    public static SpanContext fork(String name, String type, String traceId, String spanParentId) {
        try {
            SpanContext context = TRACE_CONTEXT.get();
            if (context == null) {
                context = new SpanContext();
            }
            SpanNode currentSpan = SpanNode.forkSpan(name, type, traceId, spanParentId);
            context.add(currentSpan);
            TRACE_CONTEXT.set(context);
            TrackerMDCLog.traceId(currentSpan.getTraceId());
            TrackerMDCLog.spanId(currentSpan.getSpanId());
            return context;
        } catch (Exception e) {
            log.error("[TRACKERS] 追踪开始时发生错误: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 结束追踪
     */
    public static void end() {
        // 当前不存在山下文，则直接返回
        SpanContext context = TRACE_CONTEXT.get();
        if (context == null) {
            return;
        }

        // 获取当前 span
        SpanNode currentSpan = context.getCurSpan();

        SpanNode lastSpan = context.remove();
        if (null != lastSpan) {
            TrackerMDCLog.traceId(lastSpan.getTraceId());
            TrackerMDCLog.spanId(lastSpan.getSpanId());
        } else {
            TrackerMDCLog.removeAll();
            TRACE_CONTEXT.remove();
        }
        // 当一个 span 结束后, 发送到 span 收集器中
        sendSpanCollector(currentSpan);
    }

    public static void record(String key, String value) {
        SpanContext context = TRACE_CONTEXT.get();
        if (context != null && context.getCurSpan() != null) {
            context.getCurSpan().setRecord(key, value);
        }
    }

    /**
     * 获取到当前链路的 traceId
     *
     * @return traceId
     */
    public static String getTraceId() {
        SpanContext context = TRACE_CONTEXT.get();
        if (context != null && context.getCurSpan() != null) {
            return context.getCurSpan().getTraceId();
        }
        return "";
    }

    /**
     * 获取到当前链路的 spanId
     *
     * @return spanId
     */
    public static String getSpanId() {
        SpanContext context = TRACE_CONTEXT.get();
        if (context != null && context.getCurSpan() != null) {
            return context.getCurSpan().getSpanId();
        }
        return "";
    }

    /**
     * 获取到当前链路的 spanParentId
     *
     * @return spanId
     */
    public static String getSpanParentId() {
        SpanContext context = TRACE_CONTEXT.get();
        if (context != null && context.getCurSpan() != null) {
            return context.getCurSpan().getSpanParentId();
        }
        return "";
    }

    /**
     * 初始化 span 收集器
     */
    private synchronized static void initSpanQueue() {
        try {
            // 初始化收集器失败时, 则不需要收集
            trackerCollector = SpringUtil.getBean(TrackerCollector.class);
        } catch (Exception e) {
            log.debug("[TRACKERS] 无法发现任何 TrackerCollector 收集器, TRACE 将只在日志 MDC 中记录, 不会持久化...");
            needCollectSpan = false;
        }
    }

    /**
     * span 收集器, 当 span 结束时发送到 span 收集器
     *
     * @param spanNode span节点信息
     */
    private static void sendSpanCollector(SpanNode spanNode) {
        // 如果需要收集
        if (needCollectSpan) {
            // 如果为空则需要
            if (trackerCollector == null) {
                initSpanQueue();
            }
            if (trackerCollector != null) {
                trackerCollector.collect(spanNode);
            }
        }
    }

    @Override
    public void close() {
        Tracker.end();
    }
}
