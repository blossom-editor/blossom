package com.blossom.backend.base.auth.annotation;

import com.blossom.backend.base.user.UserTypeEnum;

import java.lang.annotation.*;

/**
 * 接口校验用户类型
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthUserType {

    /**
     * 用户的类型, 接口只允许该类型用户调用
     */
    UserTypeEnum value();
}
