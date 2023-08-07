package com.blossom.common.base.log.pojo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * 动态日志级别
 *
 * @author xzzz
 */
@Data
public class DynamicLog {

    /**
     * 日志路径
     */
    @NotEmpty(message = "日志路径为必填项")
    private String path;

    /**
     * 日志级别
     */
    @NotEmpty(message = "日志级别为必填项")
    private String level;
}
