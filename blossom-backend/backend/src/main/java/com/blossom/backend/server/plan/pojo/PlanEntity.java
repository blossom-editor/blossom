package com.blossom.backend.server.plan.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 计划实体
 *
 * @author xzzz
 */
@Data
@TableName("blossom_plan")
@EqualsAndHashCode(callSuper = true)
public class PlanEntity extends AbstractPOJO {
    /**
     * ID
     */
    private Long id;
    /**
     * 分组ID
     */
    private Long groupId;
    /**
     * 计划类型
     */
    private Integer type;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;
    /**
     * 日期月份
     */
    private String planMonth;
    /**
     * 日期
     */
    private Date planDate;
    /**
     * 开始时间
     */
    private String planStartTime;
    /**
     * 结束
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

    private Long userId;
}
