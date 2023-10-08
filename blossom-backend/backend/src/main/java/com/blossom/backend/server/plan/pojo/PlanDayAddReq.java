package com.blossom.backend.server.plan.pojo;

import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 新增每日计划
 *
 * @author xzzz
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PlanDayAddReq extends AbstractPOJO {

    /**
     * 标题
     */
    @NotBlank(message = "标题为必填项")
    private String title;
    /**
     * 内容
     */
    private String content;
    /**
     * 日期
     */
    @NotNull(message = "日期为必填项")
    private Date planDate;
    /**
     * 开始时间
     */
    @NotBlank(message = "开始时间为必填项")
    private String planStartTime;
    /**
     * 结束
     */
    @NotBlank(message = "结束时间为必填项")
    private String planEndTime;
    /**
     * 颜色
     */
    private String color;
    /**
     * 是否全天
     */
    private Boolean allDay;
    /**
     * 是否重复
     */
    private Boolean repeat;
    /**
     * 重复天数
     */
    private Integer repeatDay;
    /**
     * 用户ID
     */
    private Long userId;
}
