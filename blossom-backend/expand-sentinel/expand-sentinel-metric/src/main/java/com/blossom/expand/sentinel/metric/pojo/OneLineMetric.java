package com.blossom.expand.sentinel.metric.pojo;

import lombok.Data;

/**
 * Sentinel 原生日志字段
 * <p>详细信息见: https://www.yuque.com/xiaozeizeizi/learning/vdxicg
 *
 * @author xzzz
 */
@Data
public class OneLineMetric {
    private Long timestamp;
    private String datetime;
    private String resource;
    private Integer p;
    private Integer b;
    private Integer s;
    private Integer e;
    private Integer rt;
    private Integer o;
    private Integer c;
    private Integer cf;
}
