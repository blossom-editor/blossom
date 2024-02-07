package com.blossom.common.base;

import com.blossom.common.base.enums.ExStackTrace;
import com.blossom.common.base.enums.ExFormat;
import com.blossom.common.base.exception.AbstractExceptionAdvice;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * base 基础功能配置
 *
 * @author xzzz
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "project.base")
public class BaseProperties {

    /**
     * 指定项目的时区
     */
    private String timeZone = "GMT+8";

    /**
     * 系统版本, 可以使用 @project.version@ 获取 pom 中版本
     */
    private String version = "xyz";

    /**
     * 异常处理信息配置
     */
    private Ex ex = new Ex();

    /**
     * 日志配置
     */
    private Log log = new Log();

    @Data
    public static class Ex {
        /**
         * {@link AbstractExceptionAdvice} 中记录异常堆栈信息的内容;
         * <p>如果是 all ，则记录全部堆栈信息;
         * <p>如果是 project, 堆栈信息只包含项目包下的类;
         */
        private ExStackTrace stackTrace = ExStackTrace.project;

        /**
         * {@link AbstractExceptionAdvice} 中是否将异常信息进行格式化打印;
         * <p>如果是 line, 则异常堆栈信息按照一行记录在日志中;
         * <p>如果是 project, 则异常堆栈信息按照格式化方式记录在日志中
         */
        private ExFormat format = ExFormat.format;
    }

    @Data
    public static class Log {
        /**
         * 日志级别的缓存时间, 单位毫秒, 超过该时间将会重置为 INFO, 用来防止日志过大。
         * <p>仅针对通过动态日志配置的日志路径, 如果日志级别已在配置文件中配置, 则会
         * 被覆盖并在超时后重置为 INFO, 但项目重启后会重置为
         */
        private Long duration = 10 * 60 * 1000L;

        /**
         * 如果以 redis 存储日志级别, 则日志级别失效后回滚需要监听 key 过期, 可能有性能隐患
         * <p>如果以 caffeine 存储日志级别, 则日志级别失效后 caffeine 会惰性删除, 可能存在很久的延迟.
         * <p>所以由于动态日志的回滚是每个应用定时去完成的
         * <p>每 restoreDuration 秒, 会重新刷新日志级别。如果日志级别设置的时间已超时, 则会重置为 INFO
         */
        private Long restoreDuration = 30 * 1000L;
    }

}
