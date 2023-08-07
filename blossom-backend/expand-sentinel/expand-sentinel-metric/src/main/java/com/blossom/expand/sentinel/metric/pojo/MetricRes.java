package com.blossom.expand.sentinel.metric.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 应用流量统计
 *
 * @author xzzz
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MetricRes {
    /**
     * 指标时间
     */
    private String datetime;
    /**
     * 最大QPS
     */
    private Integer maxQps;
    /**
     * 平均QPS
     */
    private Integer avgQps;
    /**
     * 成功的请求数，success = pass + block
     */
    private Integer success;
    /**
     * 异常的请求数
     */
    private Integer exception;
    /**
     * 通过的请求数
     */
    private Integer pass;
    /**
     * 阻塞的请求数
     */
    private Integer block;
    /**
     * 最小响应时间 ms
     */
    private Integer minRt;
    /**
     * 最大响应时间 ms
     */
    private Integer maxRt;
    /**
     * 平均响应时间 ms
     */
    private Double avgRt;

    public void setMaxQps(Integer maxQps) {
        if (invalid(maxQps)) {
            this.maxQps = 0;
        } else {
            this.maxQps = maxQps;
        }
    }

    public void setAvgQps(Integer avgQps) {
        if (invalid(avgQps)) {
            this.avgQps = 0;
        } else {
            this.avgQps = avgQps;
        }
    }

    public void setSuccess(Integer success) {
        if (invalid(success)) {
            this.success = 0;
        } else {
            this.success = success;
        }
    }

    public void setException(Integer exception) {
        if (invalid(exception)) {
            this.exception = 0;
        } else {
            this.exception = exception;
        }
    }

    public void setPass(Integer pass) {
        if (invalid(pass)) {
            this.pass = 0;
        } else {
            this.pass = pass;
        }
    }

    public void setBlock(Integer block) {
        if (invalid(block)) {
            this.block = 0;
        } else {
            this.block = block;
        }
    }

    public void setMinRt(Integer minRt) {
        if (invalid(minRt)) {
            this.minRt = 0;
        } else {
            this.minRt = minRt;
        }
    }

    public void setMaxRt(Integer maxRt) {
        if (invalid(maxRt)) {
            this.maxRt = 0;
        } else {
            this.maxRt = maxRt;
        }
    }

    public void setAvgRt(Double avgRt) {
//        if (invalid(avgRt)) {
//            this.avgRt = 0;
//        } else {
            this.avgRt = avgRt;
//        }
    }

    /**
     * 如果参数为 MAX_VALUE/MIN_VALUE, 则数据是无效的
     */
    private boolean invalid(Integer i) {
        if (i == null) {
            return true;
        }
        return (i == Integer.MAX_VALUE || i == Integer.MIN_VALUE);
    }

    public static MetricRes empty() {
        MetricRes metricRes = new MetricRes();
        metricRes.setSuccess(0);
        metricRes.setException(0);
        metricRes.setPass(0);
        metricRes.setBlock(0);
        metricRes.setMinRt(0);
        metricRes.setMaxRt(0);
        metricRes.setAvgRt(0.00);
        metricRes.setMaxQps(0);
        return metricRes;
    }
}

