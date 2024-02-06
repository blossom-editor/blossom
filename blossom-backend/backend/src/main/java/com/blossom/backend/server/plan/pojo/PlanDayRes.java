package com.blossom.backend.server.plan.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * 每日计划
 *
 * @author xzzz
 */
@Data
public class PlanDayRes implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private Long id;
    /**
     * 分组ID
     */
    private Long groupId;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;
    /**
     * 日期
     */
    private String planDate;
    /**
     * 计划开始时间
     */
    private String planStartTime;
    /**
     * 计划结束时间
     */
    private String planEndTime;
    /**
     * 颜色
     */
    private String color;
    /**
     * 位置
     */
    private String position;
    /**
     * 图片
     */
    private String img;
    /**
     * 排序
     */
    private Integer sort;
}
