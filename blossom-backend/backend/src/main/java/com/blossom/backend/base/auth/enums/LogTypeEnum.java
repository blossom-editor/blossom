package com.blossom.backend.base.auth.enums;

/**
 * 打印请求日志的类型
 *
 * @author xzzz
 */
public enum LogTypeEnum {
    /**
     * 不打印请求日志
     */
    none,
    /**
     * 打印简单的请求日志, 只有请求地址
     */
    simple,
    /**
     * 答应详细的请求日志, 包含请求头, 请求参数, application/json 类型的请求体
     */
    detail;
}
