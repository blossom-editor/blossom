package com.blossom.backend.base.user.pojo;

import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserDisabledReq extends AbstractPOJO {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID为必填项")
    private Long id;
    /**
     * 禁用状态
     */
    private Long delTime;
}
