package com.blossom.backend.base.auth.pojo;

import com.blossom.backend.base.auth.enums.GrantTypeEnum;
import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

/**
 * 登录请求
 *
 * @author xzzz
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LoginReq extends AbstractPOJO {

    /**
     * 登录方式，可见数据字典中[GrantType]部分, 必须为小写
     *
     * @see GrantTypeEnum
     */
    @NotEmpty(message = "授权方式[GrantType]为必填项")
    private String grantType;
    /**
     * 客户端ID
     */
    @NotEmpty(message = "客户端ID[ClientId]为必填项")
    private String clientId;

    /**
     * 用户名, grantType = password 时必填
     */
    private String username;
    /**
     * 密码, grantType = password 时必填
     */
    private String password;
}
