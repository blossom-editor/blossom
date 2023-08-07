package com.blossom.backend.server.plan.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * 日常计划响应
 *
 * @author xzzz
 */
@Data
public class PlanDailyRes implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private Long id;
    /**
     * 内容
     */
    private String content;
    /**
     * 开始时间
     */
    private String planStartTime;
    /**
     * 结束日期
     */
    private String planEndTime;
    /**
     * 图片
     */
    private String img;
    /**
     * 是否当前计划
     */
    private Boolean current;
}
