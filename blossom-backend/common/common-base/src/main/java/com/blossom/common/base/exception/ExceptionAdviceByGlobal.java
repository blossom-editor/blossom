package com.blossom.common.base.exception;

import cn.hutool.core.collection.CollectionUtil;
import com.blossom.common.base.BaseProperties;
import com.blossom.common.base.pojo.R;
import com.blossom.common.base.pojo.RCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.ConnectException;
import java.util.List;

/**
 * 全局异常处理器
 * <p>
 * 统一以 R<?> 对象格式返回给客服端
 *
 * @authoR<?> xzzz
 * @since 0.0.1
 */
@Slf4j
@Order(1)
@RestControllerAdvice
public class ExceptionAdviceByGlobal extends AbstractExceptionAdvice {

    public ExceptionAdviceByGlobal(BaseProperties baseProperties) {
        super(baseProperties);
    }

    /**
     * 业务参数异常
     */
    @ExceptionHandler(Exception.class)
    public R<?> handleException(Exception exception) {
        exception.printStackTrace();
        return R.fault(RCode.INTERNAL_SERVER_ERROR.getCode(), exception.getMessage());
    }

    /**
     * 参数处理错误
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public R<?> illegalArgumentExceptionHandler(Exception exception) {
        exception.printStackTrace();
        return R.fault(RCode.BAD_REQUEST.getCode(), exception.getMessage());
    }

    /**
     * 数据资源访问失败异常
     */
    @ExceptionHandler(ConnectException.class)
    public R<?> handleDataAccessResourceFaultException(ConnectException exception) {
        exception.printStackTrace();
        return R.fault("99999", "连接失败", exception.getMessage());
    }

    /**
     * ServletRequest 绑定异常。 属于 HTTP 中的 400 错误
     * <p>
     * 示例：返回的 pojo 在序列化成 json 过程失败了，那么抛该异常；
     */
    @ExceptionHandler(ServletRequestBindingException.class)
    public R<?> handleServletRequestBindingException(ServletRequestBindingException exception) {
        String msg = "缺少请求参数";
        printExLog(exception, msg);
        return R.fault(RCode.BAD_REQUEST.getCode(), msg, exception.getMessage());
    }

    /**
     * 缺少请求参数 file
     */
    @ExceptionHandler(MissingServletRequestPartException.class)
    public R<?> illegalArgumentExceptionHandler(MissingServletRequestPartException exception) {
        String msg = String.format("缺少请求参数 [%s]", exception.getRequestPartName());
        printExLog(exception, msg);
        return R.fault(RCode.BAD_REQUEST.getCode(), msg, exception.getMessage());
    }

    /**
     * 缺少 ServletRequest 请求参数。 属于 HTTP 中的 400 错误
     * <p>
     * 1、@RequestParam("licenceId") String licenceId，但发起请求时，未携带该参数，则会抛该异常；
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public R<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException exception) {
        String msg = String.format("缺少请求参数 [%s]", exception.getParameterName());
        printExLog(exception, msg);
        return R.fault(RCode.BAD_REQUEST.getCode(), msg, exception.getMessage());
    }

    /**
     * 未检测到路径参数异常。 属于 HTTP 中的 500 错误
     * <p>
     * 1、url为：/licence/{licenceId}，参数签名包含 @PathVariable("licenceId")，当请求的 url 为 /licence 时。
     */
    @ExceptionHandler(MissingPathVariableException.class)
    public R<?> handleMissingPathVariableException(MissingPathVariableException exception) {
        printExLog(exception, exception.getMessage());
        return R.fault(RCode.BAD_REQUEST.getCode(), "缺少请求参数" + exception.getMessage(), exception.getMessage());
    }

    /**
     * 参数解析异常。 属于 HTTP 中的 400 错误
     * <p>1、JSON parse error： 使用 @RequestBody 接收参数时，参数是 json 字符串，数据是 Long 类型的，json 字符串中却传入了 String 类型。
     * <p>2、Required request body is missing： 使用 @RequestBody 接收参数时，不传递参数。
     * <p>3、Required request body is missing： 使用 get 请求去请求使用 @RequestBody 接收参数的接口时。
     * <p>4、JSON parse error: Numeric value (9999999999999999999) out of range of int (-2147483648 - 2147483647);
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public R<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        String msg = "请检查 RequestBody 格式是否正确。";
        printExLog(exception, msg);
        return R.fault(RCode.BAD_REQUEST.getCode(), msg, exception.getMessage());
    }

    /**
     * Spring 参数绑定异常
     * <p>1、org.springframework.validation.BindException： 参数上添加了 @Validated 注解，不传递参数。
     * <p>2、org.springframework.validation.BindException： 参数上添加了 @Validated 注解，不使用 @RequestBody 接收参数且参数值超出了校验范围。
     */
    @ExceptionHandler(BindException.class)
    public R<?> handleBindException(BindException exception) {
        printExLog(exception, exception.getMessage());
        return R.fault(RCode.BAD_REQUEST.getCode(), "参数绑定异常:" + exception.getMessage(), exception.getMessage());
    }

    /**
     * 方法参数校验异常。 属于 HTTP 中的 400 错误
     * <p>
     * 使用 @Validated 注解的参数校验
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        List<FieldError> fieldErrorList = exception.getFieldErrors();
        StringBuilder buffer = new StringBuilder();
        if (CollectionUtil.isNotEmpty(fieldErrorList)) {
            for (FieldError fieldError : fieldErrorList) {
                fieldError.getField();
                buffer.append(fieldError.getDefaultMessage()).append(";");
            }
        }
        printExLog(exception, buffer.toString());
        return R.fault(RCode.BAD_REQUEST.getCode(), buffer.toString(), exception.getMessage());
    }


    /**
     * 405 错误
     * <p>
     * 1、请求对应的 ControlleR<?> 方法支持 Post 请求，此时，用 GET 请求来访问。
     */
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public R<?> request405(HttpRequestMethodNotSupportedException exception) {
        printExLog(exception, exception.getMessage());
        return R.fault(RCode.BAD_REQUEST.getCode(), String.format("当前访问路径不支持 %s 请求", exception.getMethod()), exception.getMessage());
    }

    /**
     * 空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public R<?> nullPointerExceptionHandler(NullPointerException exception) {
        printExLog(exception, exception.getMessage());
        return R.fault(RCode.INTERNAL_SERVER_ERROR.getCode(), "空引用异常", exception.getMessage());
    }


    /**
     * 类型转换异常
     */
    @ExceptionHandler(ClassCastException.class)
    public R<?> classCastExceptionHandler(ClassCastException exception) {
        printExLog(exception, exception.getMessage());
        return R.fault(RCode.INTERNAL_SERVER_ERROR.getCode(), "类型转换错误", exception.getMessage());
    }

    /**
     * 远程主机强迫关闭了一个现有的连接
     */
    @ExceptionHandler(ClientAbortException.class)
    public R<?> clientAbortExceptionHandler(ClientAbortException exception, HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        return R.fault(RCode.INTERNAL_SERVER_ERROR.getCode(), "IO 异常", exception.getMessage());
    }

    /**
     * IO 异常
     */
    @ExceptionHandler(IOException.class)
    public R<?> ioExceptionHandler(IOException exception, HttpServletResponse response) {
        printExLog(exception, exception.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        return R.fault(RCode.INTERNAL_SERVER_ERROR.getCode(), "IO 异常", exception.getMessage());
    }

    /**
     * 方法不存在异常
     */
    @ExceptionHandler(NoSuchMethodException.class)
    public R<?> noSuchMethodExceptionHandler(NoSuchMethodException exception) {
        printExLog(exception, exception.getMessage());
        return R.fault(RCode.INTERNAL_SERVER_ERROR.getCode(), "方法不存在", exception.getMessage());
    }

    /**
     * 下标越界异常
     */
    @ExceptionHandler(IndexOutOfBoundsException.class)
    public R<?> indexOutOfBoundsExceptionHandler(IndexOutOfBoundsException exception) {
        printExLog(exception, exception.getMessage());
        return R.fault(RCode.INTERNAL_SERVER_ERROR.getCode(), "下标越界", exception.getMessage());
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * 503 错误
     */
    @ExceptionHandler({AsyncRequestTimeoutException.class})
    public R<?> request503(AsyncRequestTimeoutException exception) {
        printExLog(exception, exception.getMessage());
        return R.fault(RCode.INTERNAL_SERVER_ERROR.getCode(), "异步请求超时", exception.getMessage());
    }

    /**
     * 500 错误
     * <p>
     * ConversionNotSupportedException：转换不支持
     * HttpMessageNotWritableException：返回的 pojo 在序列化成 json 过程失败了，那么抛该异常；
     * MissingPathVariableException：未检测到路径参数：url为：/licence/{licenceId}，参数签名包含 @PathVariable("licenceId")
     * ，当请求的url为/licence，在没有明确定义url为/licence的情况下，会被判定为：缺少路径参数；
     */
    @ExceptionHandler({ConversionNotSupportedException.class, HttpMessageNotWritableException.class})
    public R<?> server500(RuntimeException exception) {
        printExLog(exception, exception.getMessage());
        return R.fault(RCode.INTERNAL_SERVER_ERROR.getCode(), RCode.INTERNAL_SERVER_ERROR.getMsg(), exception.getMessage());
    }

    /**
     * 404 错误
     * <p>
     * NoHandlerFoundException：请求没找到 首先根据请求Url查找有没有对应的控制器，若没有则会抛该异常
     */
    @ExceptionHandler({NoHandlerFoundException.class})
    public R<?> request404(NoHandlerFoundException exception) {
        printExLog(exception, exception.getMessage());
        return R.fault(RCode.NOT_FOUND.getCode(), RCode.NOT_FOUND.getMsg(), exception.getMessage());
    }

    /**
     * 上传文件大小的错误
     *
     * @param exception 异常
     */
    @ExceptionHandler(value = MaxUploadSizeExceededException.class)
    public R<?> missingServletRequestParameterExceptionHandler(MaxUploadSizeExceededException exception) {
        printExLog(exception, exception.getMessage());
        if (exception.getCause() != null
                && exception.getCause() instanceof IllegalStateException
                && exception.getCause().getCause() != null
                && exception.getCause().getCause() instanceof SizeLimitExceededException) {
            SizeLimitExceededException sizeEx = (SizeLimitExceededException) exception.getCause().getCause();
            return R.fault(RCode.BAD_REQUEST.getCode(),
                    String.format("上传文件过大, 文件大小不能超过 %s MB ", (sizeEx.getPermittedSize()) / 1024 / 1024));
        }
        return R.fault(RCode.BAD_REQUEST.getCode(), String.format("文件上传错误, %s", exception.getMessage()));
    }


}
