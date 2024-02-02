package com.blossom.backend.base.user.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class UserListRes {
    /**
     * 用户ID
     */
    private Long id;
    /**
     * 用户头像
     */
    private String avatar;
    /**
     * 用户名
     */
    private String username;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 用户类型
     */
    private Integer type;
    /**
     * 创建日期
     */
    private Date creTime;
    /**
     * 逻辑删除, 目前用于禁用用户, 而不是删除
     */
    private Long delTime;
}
