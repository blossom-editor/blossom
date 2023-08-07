package com.blossom.common.base.exception;

import com.blossom.common.base.BaseProperties;
import com.blossom.common.base.pojo.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 系统自定义异常处理
 *
 * @author xzzz
 */
@Slf4j
@Order(0)
@RestControllerAdvice
public class ExceptionAdviceByXz extends AbstractExceptionAdvice {

    public ExceptionAdviceByXz(BaseProperties baseProperties) {
        super(baseProperties);
    }

    @ExceptionHandler(XzException.class)
    public R<?> xzExceptionHandler(XzException exception) {
        printExLog(exception, exception.getMessage(), true);
        return R.fault(exception.getCode(), exception.getMessage());
    }

    /**
     * 返回 400 响应码
     *
     * @param exception 异常信息
     * @return 返回 R.fault(), Http 响应码为 400
     */
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(XzException400HTTP.class)
    public R<?> xzException400HttpCodeHandler(XzException400HTTP exception) {
        printExLog(exception, exception.getMessage());
        return R.fault(exception.getCode(), exception.getMessage());
    }


    @ExceptionHandler(XzException400.class)
    public R<?> xzException400Handler(XzException400 exception) {
        printExLog(exception, exception.getMessage(), true);
        return R.fault(exception.getCode(), exception.getMessage());
    }

    @ExceptionHandler(XzException404.class)
    public R<?> xzException404Handler(XzException404 exception) {
        printExLog(exception, exception.getMessage(), true);
        return R.fault(exception.getCode(), exception.getMessage());
    }

    @ExceptionHandler(XzException500.class)
    public R<?> xzException500Handler(XzException500 exception) {
        printExLog(exception, exception.getMessage());
        return R.fault(exception.getCode(), exception.getMessage());
    }
}
