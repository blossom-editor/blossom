package com.blossom.expand.sentinel.metric.pojo;

import lombok.Data;

/**
 * 资源信息
 *
 * @author xzzz
 */
@Data
public class ResourcesRes {
    /**
     * 资源名
     */
    private String resource;
    /**
     * 统计该资源所用的文档数，相当于多少秒的指标信息
     */
    private Long docCount;
    /**
     * 最小响应时间 毫秒
     */
    private Integer minRt;
    /**
     * 最大响应时间 毫秒
     */
    private Integer maxRt;
    /**
     * 平均响应时间 毫秒
     */
    private Double avgRt;
    /**
     * 请求成功数
     */
    private Integer success;
}
