package com.blossom.common.base.exception;

import com.blossom.common.base.BaseProperties;
import com.blossom.common.base.enums.ExFormat;
import com.blossom.common.base.enums.ExStackTrace;
import com.blossom.common.base.util.ExceptionUtil;
import com.blossom.common.base.util.ServletUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * 所有的该抽象类的子类在实现异常捕获时，必须调用 printExLog 方法
 *
 * @author xzzz
 */
@Slf4j
public abstract class AbstractExceptionAdvice {

    /**
     * 基础功能配置
     */
    private final BaseProperties baseProperties;

    public AbstractExceptionAdvice(BaseProperties baseProperties) {
        this.baseProperties = baseProperties;
    }

    /**
     * 对 sentinel 的增强, 在 request 中增加异常标记, 供 sentinel 拦截器捕获并记录
     */
    private static final String REQUEST_ATTRIBUTE_SENTINEL_EXCEPTION = "request_attribute_sentinel_exception";

    /**
     * 打印日志信息, 显示错误堆栈
     *
     * @param ex  异常
     * @param msg 错误信息
     */
    protected void printExLog(Exception ex, String msg) {
        printExLog(ex, msg, true);
    }

    /**
     * 打印错误信息
     *
     * @param ex             异常类
     * @param msg            异常信息
     * @param showStackTrace 是否显示异常堆栈信息
     */
    protected void printExLog(Exception ex, String msg, boolean showStackTrace) {
        HttpServletRequest request = ServletUtil.getRequest();
        String url = "";
        if (request != null) {
            // 对 sentinel 的增强方法
            request.setAttribute(REQUEST_ATTRIBUTE_SENTINEL_EXCEPTION, ex);
            url = request.getRequestURL().toString();
        }

        boolean filterStackTrace = baseProperties.getEx().getStackTrace().equals(ExStackTrace.project);
        boolean onLine = baseProperties.getEx().getFormat().equals(ExFormat.line);

        log.error("{} | {}({})", url, ex.getClass().getSimpleName(), Optional.ofNullable(msg).orElse(""));
        if (showStackTrace) {
            log.error(ExceptionUtil.printStackTrace(ex, filterStackTrace, onLine));
        }
    }

}
