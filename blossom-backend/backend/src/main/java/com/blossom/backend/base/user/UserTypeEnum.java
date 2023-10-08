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
    ADMIN(1,"管理员"),
    /**
     * 普通用户
     */
    NORMAL(2,"普通用户"),
    /**
     * 只读用户
     */
    READONLY(3,"只读用户"),
    ;

    @Getter
    private final Integer type;

    @Getter
    private final String desc;

    UserTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
