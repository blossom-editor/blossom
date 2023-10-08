package com.blossom.backend.server.plan.pojo;

import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * 新增日常计划
 *
 * @author xzzz
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PlanDailyAddReq extends AbstractPOJO {

    /**
     * 内容
     */
    private String content;
    /**
     * 开始时间
     */
    @NotBlank(message = "开始时间为必填项")
    private String planStartTime;
    /**
     * 结束日期
     */
    @NotBlank(message = "结束时间为必填项")
    private String planEndTime;
    /**
     * 图片
     */
    private String img;

    /**
     * 用户ID
     */
    private Long userId;

}
