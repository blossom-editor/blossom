package com.blossom.common.base.util;

import cn.hutool.core.util.StrUtil;
import com.blossom.common.base.exception.XzException500;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

/**
 * 异常工具包
 *
 * @author xzzz
 */
@Slf4j
public class ExceptionUtil {

    /**
     * 过滤的基础包。只输出与该包相关的异常信息
     */
    private static final String FILTER_PACKAGE = "com.blossom";

    /**
     * 过滤的堆栈信息
     *
     * @param throwable 异常信息
     * @return String 堆栈信息
     */
    public static String printStackTrace(Throwable throwable) {
        return printStackTrace(throwable, false, false);
    }

    /**
     * 不过滤的堆栈信息
     *
     * @param throwable 异常信息
     * @return String 堆栈信息
     */
    public static String printStackTrace(Throwable throwable, boolean oneLine) {
        return printStackTrace(throwable, false, oneLine);
    }

    /**
     * 堆栈信息追踪
     *
     * @param throwable 异常信息
     * @param filter    是否过滤【true：只输出与 FILTER_PACKAGE 相关的异常信息, false:输出所有的异常信息】
     * @param oneLine   是否将堆栈输出为一行
     * @return String 堆栈信息
     */
    public static String printStackTrace(Throwable throwable, boolean filter, boolean oneLine) {
        if (throwable == null) {
            return "";
        }
        if (throwable.getStackTrace() == null || throwable.getStackTrace().length == 0) {
            return throwable.toString();
        }
        if (throwable instanceof InvocationTargetException) {
            return printStackTrace(((InvocationTargetException) throwable).getTargetException(), filter, oneLine);
        }
        List<StackTraceElement> stackTraceElements;
        if (filter) {
            stackTraceElements = Arrays.stream(throwable.getStackTrace())
                    .filter(element -> element.getClassName().startsWith(FILTER_PACKAGE))
                    .collect(Collectors.toList());
        } else {
            stackTraceElements = Arrays.stream(throwable.getStackTrace()).collect(Collectors.toList());
        }

        // 对堆栈信息各项格式化
        StringBuilder sb = new StringBuilder();
        if (StrUtil.isNotEmpty(throwable.getMessage())) {
            sb.append(oneLine ? "" : "\n");
            sb.append("异常类型: ").append(throwable.getClass().getCanonicalName());
            sb.append(oneLine ? "。" : "\n");
            // 去除换行符
            sb.append("异常内容: ").append(removeTNRF(throwable.getMessage()));
            sb.append(oneLine ? "。" : "\n");
        }
        sb.append("堆栈信息: ");
        sb.append(oneLine ? "" : "\n");
        stackTraceElements.forEach(element -> {
                    sb.append(" > ")
                            .append(element.getClassName()).append(".").append(element.getMethodName())// (类名.方法名)
                            .append("(").append(element.getFileName()).append(":").append(element.getLineNumber()).append(")");// (文件名:行数)
                    if (!oneLine) {
                        sb.append("\n");
                    }
                }
        );
        return sb.toString();
    }

    /**
     * 拼接堆栈信息
     *
     * @param stackTraceElements 堆栈追踪元素列表
     * @param parameterJson      参数的 Json 信息
     * @return String 堆栈信息
     */
    private static String getStackMessage(String detailMessage, List<StackTraceElement> stackTraceElements,
                                          String parameterJson) {
        StringBuilder sb = new StringBuilder("--error message detail：");
        sb.append(detailMessage);
        if (null != parameterJson && parameterJson.trim().length() > 0) {
            sb.append("\r\nParameters:").append(parameterJson).append("\n");
        }
        stackTraceElements.forEach(
                element -> sb.append("\tat ").append(element.getClassName()).append(".").append(element.getMethodName())
                        .append("(").append(element.getFileName()).append(":").append(element.getLineNumber())
                        .append(")\n"));
        return sb.toString();
    }

    /**
     * 获取由指定异常类引起的异常
     *
     * @param throwable    异常
     * @param causeClasses 定义的引起异常的类
     * @return 是否由指定异常类引起
     */
    public static Throwable getCausedBy(Throwable throwable, Class<? extends Exception>... causeClasses) {
        Throwable cause = throwable;
        while (cause != null) {
            for (Class<? extends Exception> causeClass : causeClasses) {
                if (causeClass.isInstance(cause)) {
                    return cause;
                }
            }
            cause = cause.getCause();
        }
        return null;
    }

    /**
     * 去除多余的换行, 空格【"\t\n\r\f"】
     *
     * @param original 原始字符串
     * @return String 去除换行后的字符串
     */
    public static String removeTNRF(String original) {
        StringTokenizer tokenizer = new StringTokenizer(original, "\t\n\r\f");
        StringBuilder builder = new StringBuilder();
        boolean hasMoreTokens = tokenizer.hasMoreTokens();
        while (hasMoreTokens) {
            builder.append(tokenizer.nextToken());
            hasMoreTokens = tokenizer.hasMoreTokens();
            if (hasMoreTokens) {
                builder.append(' ');
            }
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        try {
            throw new XzException500("123123");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("发生异常, {}", ExceptionUtil.printStackTrace(e, false));
        }
    }
}
