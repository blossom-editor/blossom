package com.blossom.backend.base.user.pojo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 删除用户
 */
@Data
public class UserDeleteReq {
    /**
     * 用户ID
     */
    @NotNull(message = "用户ID为必填项")
    private Long id;
}
