package com.blossom.common.base.pojo;

import lombok.Data;

import java.util.Date;

/**
 * 抽象 entity
 *
 * @author xzzz
 */
@Data
public class AbstractEntity extends AbstractPOJO {
    /**
     * 创建名称,创建人ID
     */
    private String creBy;
    /**
     * 创建时间
     */
    private Date creTime;
    /**
     * 修改人名称,修改人ID
     */
    private String updBy;
    /**
     * 修改时间
     */
    private Date updTime;
    /**
     * 删除人名称,删除人ID
     */
    private String delBy;
    /**
     * 删除时间，0 为未删除，非 0 为已删除
     */
    private Long delTime;
}
