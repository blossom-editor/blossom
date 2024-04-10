package com.blossom.backend.base.paramu.pojo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 修改参数
 *
 * @since 1.12.0
 */
@Data
public class UserParamUpdReq {

    /**
     * 参数名称
     */
    @NotBlank(message = "参数名称为必填项")
    private String paramName;

    /**
     * 参数值
     */
    @NotNull(message = "参数值为必填项")
    private String paramValue;

    /**
     * 用户ID
     */
    private Long userId;
}
