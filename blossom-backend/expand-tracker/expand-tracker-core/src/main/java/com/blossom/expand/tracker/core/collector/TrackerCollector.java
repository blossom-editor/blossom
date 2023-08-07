package com.blossom.expand.tracker.core.collector;

import com.blossom.expand.tracker.core.SpanNode;

import java.util.List;

/**
 * 追踪收集器
 *
 * @author xzzz
 */
public interface TrackerCollector {

    /**
     * 收集 span 信息
     *
     * @param spanNode span 信息
     */
    void collect(SpanNode spanNode);

    /**
     * 获取 span 列表
     *
     * @return span 列表
     */
    List<SpanNode> get();
}
