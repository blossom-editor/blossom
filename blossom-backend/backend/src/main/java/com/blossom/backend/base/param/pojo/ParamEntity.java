package com.blossom.backend.base.param.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统参数信息的实体类
 *
 * @author xzzz
 */
@Data
@TableName("base_sys_param")
@EqualsAndHashCode(callSuper = true)
public class ParamEntity extends AbstractPOJO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 参数ID */
    @TableId
    private Long id;
    /** 参数名称 */
    private String paramName;
    /** 参数值 */
    private String paramValue;
    /** 参数说明 */
    private String paramDesc;
    /** 创建时间 */
    private Date creTime;
    /** 修改时间 */
    private Date updTime;
}
