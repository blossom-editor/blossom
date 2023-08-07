package com.blossom.backend.base.auth.annotation;

import java.lang.annotation.*;

/**
 * 权限校验
 *
 * @author xzzz
 * @since 0.0.1
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface PermCheck {

    /*
    所需要的授权
     */
    String value();
}
