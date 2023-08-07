package com.blossom.backend.base.auth.annotation;

import java.lang.annotation.*;

/**
 * 白名单注解标识
 *
 * @author xzzz
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthIgnore {
}
