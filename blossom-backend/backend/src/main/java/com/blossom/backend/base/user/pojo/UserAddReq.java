package com.blossom.backend.base.user.pojo;

import com.blossom.backend.base.user.UserTypeEnum;
import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 新增用户
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserAddReq extends AbstractPOJO {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名为必填项")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码为必填项")
    private String password;

    /**
     * 用户类型 {@link UserTypeEnum}
     */
    @NotNull(message = "用户类型为必填项")
    private Integer type;
}
