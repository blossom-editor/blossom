package com.blossom.backend.base.user.pojo;

import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserAddReq extends AbstractPOJO {

    @NotBlank(message = "用户名为必填项")
    private String username;

    @NotBlank(message = "密码为必填项")
    private String password;

    @NotNull(message = "用户类型为必填项")
    private Integer type;
}
