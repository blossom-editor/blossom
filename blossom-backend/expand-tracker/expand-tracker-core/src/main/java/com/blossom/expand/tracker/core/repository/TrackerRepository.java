package com.blossom.expand.tracker.core.repository;

import com.blossom.expand.tracker.core.SpanNode;

import java.util.List;

/**
 * tracker span 信息持久化接口
 * <p>tracker 客户端和服务端的实现不同
 * <p><h3>客户端的实现目前有</h3>
 * <ol>
 * <li>disk: 保存在本地日志中
 * </ol>
 *
 * <p><h3>服务端接收到应用自身，或其他应用的 span 信息，通过如下方式保存</h3>
 * 本项目是 xz.irda.tracker 框架的简单版本, 不提供服务方
 *
 * @author xzzz
 */
public interface TrackerRepository {

    /**
     * 保存 span 集合,
     *
     * @param spanNodes span 集合
     * @return true: 保存成功; false:保存失败
     */
    boolean save(List<SpanNode> spanNodes);

    /**
     * span 信息到期
     *
     * @return 删除的条数
     */
    default long expire() {
        return 0;
    }
}
