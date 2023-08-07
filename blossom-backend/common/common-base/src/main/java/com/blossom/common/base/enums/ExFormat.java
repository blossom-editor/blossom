package com.blossom.common.base.enums;

import com.blossom.common.base.exception.AbstractExceptionAdvice;

/**
 * {@link AbstractExceptionAdvice} 中是否将异常信息进行格式化打印;
 * <p>如果是 line, 则异常堆栈信息按照一行记录在日志中;
 * <p>如果是 format, 则异常堆栈信息按照格式化方式记录在日志中
 *
 * @author xzzz
 */
public enum ExFormat {
    /**
     * 则异常堆栈信息按照一行记录在日志中, 通常可以方便进行日志收集, 例如有结构化的数据, 可以避免使用某分隔符进行分割, 以提升效率
     */
    line,
    /**
     * 异常堆栈信息按照格式化方式记录在日志中
     */
    format
}
