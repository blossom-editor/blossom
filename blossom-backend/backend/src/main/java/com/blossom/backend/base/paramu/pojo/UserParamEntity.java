package com.blossom.backend.base.paramu.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 用户参数
 *
 * @since 1.12.0
 */
@Data
@TableName("base_user_param")
public class UserParamEntity {
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 参数名称
     */
    private String paramName;
    /**
     * 参数值
     */
    private String paramValue;
    /**
     * 修改时间
     */
    private Date updTime;
}
