package com.blossom.backend.server.todo;

import lombok.Getter;

/**
 * 待办事项状态
 *
 * @since 1.4.0
 */
public enum TodoStatusEnum {
    /**
     * 正常
     */
    OPEN(1),
    /**
     * 完成
     */
    COMPLETED(2);

    @Getter
    private final Integer type;

    TodoStatusEnum(Integer type) {
        this.type = type;
    }
}
