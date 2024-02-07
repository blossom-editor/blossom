package com.blossom.backend.base.auth.exception;


import com.blossom.common.base.pojo.IRCode;
import lombok.Getter;

/**
 * 授权响应码
 *
 * @author xzzz
 */
public enum AuthRCode implements IRCode {

    /**
     * 400: 请求路径不合法
     */
    REQUEST_REJECTED      ("AUTH-40001", "请求不合法","请求不合法：请求路径中可能包含不规范的内容。"),

    /**
     * 400: 登录参数请求参数不合法
     */
    INVALID_GRANT_TYPE    ("AUTH-40002", "无效的授权方式","无效的授权方式：[GrantType] 字段错误, 请查看传入值是否在数据字典[GrantType]中。"),
    INVALID_CLIENT_ID     ("AUTH-40003", "无效的客户端","无效的客户端：[ClientId] 字段错误, 请求的客户端没有在服务器配置。"),

    /**
     * 400: 登录时发生错误
     */
    USERNAME_OR_PWD_FAULT ("AUTH-40004", "用户名或密码错误","用户名或密码错误, 或用户名不存在。"),
    CAPTCHA_FAULT         ("AUTH-40005", "验证码错误","验证码错误, 或手机号不存在。"),
    USER_NOT_ENABLED      ("AUTH-40010", "您的账户已被已禁用, 暂时无法登录","您的账户已被已禁用, 暂时无法登录。"),

    /**
     * 401: 未经过认证
     * <p>指身份验证是必需的, 没有提供身份验证或身份验证失败。如果请求已经包含授权凭据, 那么401状态码表示不接受这些凭据。
     */
    INVALID_TOKEN         ("AUTH-40101", "无效的授权信息",
            "无效的授权信息\n请求时的令牌错误, 可以通过 AUTH-40101 来判断登录超时来跳转至登录页等。"),

    ANOTHER_DEVICE_LOGIN  ("AUTH-40102", "账号已在其他设备登录",
            "账号已在其他设备登录。\n本账号在其他设备登录时, 本设备下次请求接口时会出现该错误, 该错误出现之后。\n再次使用该令牌访问时会响应 \"AUTH-40101\",  出现该错误通常需要提示用户后跳转至登录页。"),

    /**
     * 403: 被禁止
     * <p>指示尽管请求有效, 但服务器拒绝响应它。与401状态码不同, 提供身份验证不会改变结果。
     */
    PERMISSION_DENIED     ("AUTH-40302", "你没有权限访问该资源","你没有权限访问该资源。\n没有权限访问对应 API 接口或服务器资源。"),

    /**
     * 404:找不到请求
     *
     */
    USER_NOT_EXIST        ("AUTH-40401", "用户不存在","用户不存在。\n通常不会返回该信息, 而是根据 [GrantType] 提示相应参数错误"),
    ;

    @Getter
    final String code;
    @Getter
    final String msg;
    @Getter
    final String desc;

    AuthRCode(String code, String msg, String desc) {
        this.code = code;
        this.msg = msg;
        this.desc = desc;
    }

    public static AuthRCode getByCode(String code) {
        for (AuthRCode value : AuthRCode.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }

}
