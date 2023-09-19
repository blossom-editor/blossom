package com.blossom.backend.server.todo;

import lombok.Getter;

public enum TodoStatusEnum {
    /**
     * 正常
     */
    OPEN(1),
    /**
     * 完成
     */
    COMPLETED(2)
    ;

    @Getter
    private final Integer type;

    TodoStatusEnum(Integer type) {
        this.type = type;
    }
}
