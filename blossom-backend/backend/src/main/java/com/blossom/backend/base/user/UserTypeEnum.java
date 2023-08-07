package com.blossom.backend.base.user;

import lombok.Getter;

/**
 * 用户类型
 *
 * @author xzzz
 */
public enum UserTypeEnum {
    /**
     * 管理员
     */
    ADMIN(1),
    /**
     * 普通用户
     */
    NORMAL(2),
    /**
     * 只读用户
     */
    READONLY(3),
    ;

    @Getter
    private final Integer type;

    UserTypeEnum(Integer type) {
        this.type = type;
    }
}
