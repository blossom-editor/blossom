package com.blossom.backend.base.param.pojo;

import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统参数请求实体
 *
 * @author xzzz
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ParamReq extends AbstractPOJO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 参数ID */
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
