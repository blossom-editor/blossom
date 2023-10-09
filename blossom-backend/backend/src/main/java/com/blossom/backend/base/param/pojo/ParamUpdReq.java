package com.blossom.backend.base.param.pojo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 修改参数
 */
@Data
public class ParamUpdReq {

    /**
     * 参数名称
     */
    @NotBlank(message = "参数名称为必填项")
    private String paramName;

    /**
     * 参数值
     */
    @NotBlank(message = "参数值为必填项")
    private String paramValue;
}
