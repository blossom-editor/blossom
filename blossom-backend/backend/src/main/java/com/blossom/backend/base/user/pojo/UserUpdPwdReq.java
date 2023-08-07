package com.blossom.backend.base.user.pojo;

import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * 修改密码
 *
 * @author xzzz
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserUpdPwdReq extends AbstractPOJO {

    private Long userId;

    @NotBlank(message = "旧密码为必填项")
    private String password;

    @NotBlank(message = "新密码为必填项")
    private String newPassword;

    @NotBlank(message = "确认密码为必填项")
    private String confirmPassword;
}
