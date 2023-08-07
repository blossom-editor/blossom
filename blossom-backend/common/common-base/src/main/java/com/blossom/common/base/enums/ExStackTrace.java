package com.blossom.common.base.enums;

import com.blossom.common.base.exception.AbstractExceptionAdvice;
import com.blossom.common.base.util.ExceptionUtil;

/**
 * {@link AbstractExceptionAdvice} 中记录异常堆栈信息的内容;
 * <p>如果是 all ，则记录全部堆栈信息;
 * <p>如果是 project, 堆栈信息只包含项目包下的类;
 *
 * @author xzzz
 */
public enum ExStackTrace {

    /**
     * 打印全部堆栈异常
     */
    all,

    /**
     * 只显式指定包下的堆栈信息, {@link ExceptionUtil#FILTER_PACKAGE}
     */
    project
}
