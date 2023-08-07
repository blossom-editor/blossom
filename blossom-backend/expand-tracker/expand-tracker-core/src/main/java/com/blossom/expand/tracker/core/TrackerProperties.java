package com.blossom.expand.tracker.core;

import com.blossom.expand.tracker.core.common.TrackerConstants;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 链路追踪配置
 *
 * @author xzzz
 */
@Data
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "project.tracker")
public class TrackerProperties {

    /**
     * 链路追踪收集器配置, 收集器会将 Trace 信息发送发指定的存储方式中, 存储方式需要另外
     * 配置 {@link TrackerProperties#repository}
     */
    private Collector collector;

    /**
     * Trace 信息的存储方式配置
     */
    private Repository repository;

    @PostConstruct
    public void init() {
        if (collector != null) {
            if (collector.getRate() / Double.valueOf(TrackerConstants.COLLECTOR_MAX_RATE) > 0.5) {
                log.warn("[TRACKERS] 当前采样率[{}/{}], 生成环境建议调小该值",
                        collector.getRate(), TrackerConstants.COLLECTOR_MAX_RATE);
            }
        }
    }

    @Data
    public static class Collector {

        /**
         * 是否开启本地收集, 需要开启收集
         */
        private boolean enabled = false;

        /**
         * 收集器本地缓存最大大小, 默认5000
         * <p>本地缓存超过该配置后, 将舍弃新的收集信息, 这是用于在远程服务无法连接时, 或本地磁盘无法写入时, 防止本地内存过大
         */
        private Integer maxCache = TrackerConstants.DEFAULT_COLLECTOR_LOCAL_CACHE;

        /**
         * 采样率, 0 - 1000 之间的整数, 为 1000 则全部收集, 为 0 则不收集. 默认采样率为1/1000
         * <p>每个 span 会取 traceId 进行 hash, hash % 1000 后与该值比较, 小于该值则进行采集。
         * <p>服务集群需要有相同的采样率, 否则数据将出现不完整的情况，
         * <h3>注意：</h3>
         * <ol>
         *  <li>该配置仅在大量数据情况下保证整体采样率近似于该值, 并不能保证每 1000 个请求就有 rate 数的请求被采集.</li>
         *  <li>较高的采样率并不会对吞吐量直接造成太大影响, 但较高的采样率会使用更多内存, 这在较低配置的机器时会造成更频繁的GC, 从而影响响应rt指标</li>
         *  <li>出于性能考虑, 采样率过高时 tracker 选择丢弃 span 信息来保证系统稳定运行.</li>
         * </ol>
         */
        private Integer rate = TrackerConstants.DEFAULT_COLLECTOR_RATE;

        /**
         * 忽略的追踪
         */
        private List<IgnoreTracker> ignoreTrackers;
    }

    @Data
    public static class IgnoreTracker {
        /**
         * 追踪名称
         */
        private String spanName;
        /**
         * 忽略的追踪类型
         */
        private String spanType;
        /**
         * 是否只有该 span 为 rootSpan 时才忽略
         * <p>例如未被监控的定时任务调用了SQL, 此时会出现一条单独的SQL追踪信息, 在整个链路中仅此一条,
         * 如果此时希望忽略这种追踪, 可以将 rootSpan 设置为 true
         */
        private Boolean rootSpan;
    }

    @Data
    public static class Repository {

        /**
         * 收集方式, 在对象注入时会进行判断 {@link TrackerConfiguration#trackerRepository(TrackerProperties)}
         * <p>1. disk: 将 span 信息保存在磁盘中, 目前仅支持该种方式, 但不允许开启.
         */
        private String type;

        /**
         * trace 信息持久化在本地磁盘时的配置
         */
        private Disk disk;

    }

    @Data
    public static class Disk {
        /**
         * 追踪文件名增加 pid, 为了在本地启动多个项目时进行区分
         */
        Boolean usePid = false;
        /**
         * 记录的路径
         */
        String logDir = "/tracker/";
        /**
         * 单个文件大小, byte
         * <p>10MB: 83886080
         * <p>20MB: 167772160
         * <p>30MB: 251658240
         */
        Long singleFileSize = 251658240L;
        /**
         * 保留的文件总数
         */
        Integer totalFileCount = 30;

    }



}
