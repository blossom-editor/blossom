package com.blossom.backend.base.auth.annotation;

import java.lang.annotation.*;

/**
 * 拦截器中为接口参数添加用户ID
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AssignUser {
}
