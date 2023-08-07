package com.blossom.backend.server.plan;

import lombok.Getter;

/**
 * 计划位置, 用于前端处理不同样式
 *
 * @author xzzz
 */
public enum PlanPositionEnum {
    /**
     * 每日计划的第一天
     */
    HEAD("head"),
    /**
     * 每日计划的中间日
     */
    MID("mid"),
    /**
     * 每日计划的最后一天
     */
    TAIL("tail"),
    /**
     * 仅有一天的计划
     */
    ALL("all")
    ;

    @Getter
    private final String position;

    PlanPositionEnum(String position) {
        this.position = position;
    }
}
