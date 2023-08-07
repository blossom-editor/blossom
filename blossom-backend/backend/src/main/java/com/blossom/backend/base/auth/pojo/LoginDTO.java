package com.blossom.backend.base.auth.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 登录信息
 *
 * @author xzzz
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LoginDTO extends LoginReq {

    /**
     * 用户ID
     */
    private Long userId;
    /**
     * userAgent
     */
    private String userAgent;
    /**
     * ip
     */
    private String ip;
    /**
     * 登录时间
     */
    private String loginTime;
    /**
     * token 快照
     */
    private String accessTokenSnapshot;
}
