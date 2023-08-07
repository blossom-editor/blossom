package com.blossom.backend.base.auth.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * token 对象
 *
 * @author xzzz
 */
@Data
public class AccessToken implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 登录令牌
     */
    private String token;

    /**
     * 到期时间
     */
    private Long expire;

    /**
     * 授权方式
     */
    private String grantType;

    /**
     * 登录平台
     */
    private String clientId;

    /**
     * 请求是否刷新Token
     */
    private Boolean requestRefresh;

    /**
     * 用户 Token 唯一:
     * <p>为 false  则每次登录返回的 token 是一样的;
     * <p>为 true   则每次登录 token 会替换前一个 token
     */
    private Boolean multiPlaceLogin;

    /**
     * 授权时长, 单位为秒
     */
    private Integer duration;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 登录日期
     */
    private String loginTime;

    /**
     * 权限列表
     */
    private List<String> permissions;

    /**
     * 用户元信息, 由用户自定义各类信息
     */
    private Map<String, String> metadata;

}
