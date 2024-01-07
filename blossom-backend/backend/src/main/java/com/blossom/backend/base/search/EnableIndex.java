package com.blossom.backend.base.search;

import com.blossom.backend.base.search.message.IndexMsgTypeEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EnableIndex {

    /**
     * 索引操作类型， 默认值为追加
     */
    IndexMsgTypeEnum type();

    /**
     * ID 字段表达式
     */
    String id();

}
