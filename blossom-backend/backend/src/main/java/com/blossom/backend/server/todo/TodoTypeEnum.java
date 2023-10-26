package com.blossom.backend.server.todo;

import lombok.Getter;

/**
 * 待办事项类型
 *
 * @since 1.4.0
 */
public enum TodoTypeEnum {

    /**
     * 每日待办事项
     */
    DAY(10),
    /**
     * 阶段性事项
     */
    PHASED(20),
    /**
     * 中午12点, 用于在任务列表中进行分割
     */
    NOON_AM_12(99);

    @Getter
    private final Integer type;

    TodoTypeEnum(Integer type) {
        this.type = type;
    }

    public static TodoTypeEnum getByType(Integer type) {
        for (TodoTypeEnum value : TodoTypeEnum.values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return null;
    }
}
