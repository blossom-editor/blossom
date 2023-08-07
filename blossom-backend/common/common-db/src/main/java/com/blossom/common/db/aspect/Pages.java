package com.blossom.common.db.aspect;

import java.lang.annotation.*;

/**
 * 分页注解, 仅对被注解方法的第一条sql语句生效, 多条语句分页时需显示调用
 *
 * @author xzzz
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Pages {

}
