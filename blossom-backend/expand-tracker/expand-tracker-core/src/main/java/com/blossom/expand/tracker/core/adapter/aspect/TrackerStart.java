package com.blossom.expand.tracker.core.adapter.aspect;


import com.blossom.expand.tracker.core.common.TrackerConstants;

import java.lang.annotation.*;

/**
 *
 * @author xzzz
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TrackerStart {

    /**
     * 是否创建新的Trace
     *
     * false: 如果当前存在跟踪链路, 则以当前为准
     * true : 忽略当前已存在的路径, 会替换后续全部链路
     *
     * @return 默认false
     */
    String spanName() default "";

    String spanType() default TrackerConstants.SPAN_TYPE_ANNOTATION;

}
