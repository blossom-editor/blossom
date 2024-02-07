package com.blossom.backend.base.auth.pojo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 踢出用户
 */
@Data
public class KickOutReq {

    /**
     * 用户ID
     */
    @NotNull(message = "userId 为必填项")
    private Long userId;
}
