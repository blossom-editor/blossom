package com.blossom.backend.server.plan.pojo;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 修改计划信息
 * @since 1.9.0
 */
@Data
public class PlanDayUpdReq {

    /**
     * 分组ID
     */
    @NotNull(message = "分组ID为必填项")
    @Min(value = 0)
    private Long groupId;
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
     * 用户ID
     */
    private Long userId;
}
