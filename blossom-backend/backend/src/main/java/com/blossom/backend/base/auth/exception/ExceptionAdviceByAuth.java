package com.blossom.backend.base.auth.exception;

import com.blossom.common.base.BaseProperties;
import com.blossom.common.base.exception.AbstractExceptionAdvice;
import com.blossom.common.base.exception.XzAbstractException;
import com.blossom.common.base.pojo.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 授权异常处理
 *
 * <p>并不是所有授权阶段发生的异常都在此捕获, 例如有些异常在过滤器无法通过 Advice 捕获, 该种异常不需要继承 {@link XzAbstractException}
 *
 * @author xzzz
 */
@Slf4j
@Order(-1)
@RestControllerAdvice
public class ExceptionAdviceByAuth extends AbstractExceptionAdvice {

    public ExceptionAdviceByAuth(BaseProperties baseProperties) {
        super(baseProperties);
    }

    @ExceptionHandler(AuthException.class)
    public R<?> authExceptionHandler(XzAbstractException exception) {
//        printExLog(exception, exception.getMessage());
        return R.fault(exception.getCode(), exception.getMessage());
    }
}
