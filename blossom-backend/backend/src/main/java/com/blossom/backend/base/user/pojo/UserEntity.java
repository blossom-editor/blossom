package com.blossom.backend.base.user.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户
 *
 * @author xzzz
 */
@Data
@TableName("base_user")
@EqualsAndHashCode(callSuper = true)
public class UserEntity extends AbstractPOJO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 用户类型
     */
    private Integer type;
    /**
     * 用户名
     */
    private String username;
    /**
     * 用户手机号
     */
    private String phone;
    /**
     * 用户密码
     */
    private String password;
    /**
     * 密码加盐
     */
    private String salt;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 真实姓名
     */
    private String realName;
    /**
     * 用户头像
     */
    private String avatar;
    /**
     * 备注
     */
    private String remark;
    /**
     * 用户的位置
     */
    private String location;
    /**
     * 创建日期
     */
    private Date creTime;
    /**
     * 逻辑删除, 目前用于禁用用户, 而不是删除
     */
    private Long delTime;

}
