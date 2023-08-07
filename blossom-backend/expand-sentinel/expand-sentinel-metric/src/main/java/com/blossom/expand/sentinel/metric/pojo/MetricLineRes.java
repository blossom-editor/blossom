package com.blossom.expand.sentinel.metric.pojo;

import com.blossom.expand.sentinel.metric.controller.AbstractSentinelController;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 资源直方图信息
 *
 * @author xzzz
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MetricLineRes {

    public MetricLineRes() {
    }

    public MetricLineRes(Integer size) {
        this.x = new String[size];
        this.s = new Integer[size];
        this.e = new Integer[size];
        this.p = new Integer[size];
        this.b = new Integer[size];
        this.minRt = new Integer[size];
        this.maxRt = new Integer[size];
        this.avgRt = new Double[size];
    }

    /**
     * 资源名称
     */
    private String resource;
    /**
     * 标题
     */
    private String title;
    /**
     * 副标题
     */
    private String subTitle;

    private String[] x;
    private Integer[] s;
    private Integer[] e;
    private Integer[] p;
    private Integer[] b;
    private Integer[] minRt;
    private Integer[] maxRt;
    private Double[] avgRt;

    public void addMetric(MetricRes metric) {
    }


    public void addMetric(String datetime, AbstractSentinelController.MetricValue v, Integer index) {
        x[index] = datetime.substring(5).replaceAll(" ", "\n");
        s[index] = v.getSuccess();
        e[index] = v.getException();
        p[index] = v.getPass();
        b[index] = v.getBlock();
        minRt[index] = v.getMinRt();
        maxRt[index] = v.getMaxRt();
        if (v.getSumRt() == 0 || v.getCount() == 0) {
            avgRt[index] = 0.0;
        } else if (v.getSumRt() != 0 && v.getCount() != 0) {
            avgRt[index] = (double) Math.round((double) v.getSumRt() / v.getCount());
        }
    }
}
