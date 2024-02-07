package com.blossom.backend.base.user.pojo;

import com.blossom.common.base.pojo.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户请求
 *
 * @author xzzz
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserUpdAdminReq extends AbstractEntity {
    /**
     * 用户ID
     */
    private Long id;
    /**
     * 和风天气的位置
     */
    private String location;
    /**
     * 类型
     */
    private Integer type;
}
