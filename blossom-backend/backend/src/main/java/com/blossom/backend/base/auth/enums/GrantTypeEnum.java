package com.blossom.backend.base.auth.enums;

import lombok.Getter;

/**
 * 授权方式
 *
 * @author xzzz
 */
public enum GrantTypeEnum {

    /**
     * 暂时只支持 password
     */
    PASSWORD("password", "密码登录, 需要传入用户名和密码");

    @Getter
    private final String type;

    @Getter
    private final String desc;

    GrantTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
