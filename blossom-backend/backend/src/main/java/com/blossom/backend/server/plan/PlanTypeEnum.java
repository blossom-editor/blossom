package com.blossom.backend.server.plan;

import lombok.Getter;

/**
 * 计划类型
 *
 * @author xzzz
 */
public enum PlanTypeEnum {
    /**
     * 每日计划
     */
    DAY(1),
    /**
     * 日常计划
     */
    DAILY(2),
    ;

    @Getter
    private final Integer type;

    PlanTypeEnum(Integer type) {
        this.type = type;
    }
}
