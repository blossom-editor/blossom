package com.blossom.backend.base.param.pojo;

import com.blossom.common.db.pojo.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统参数分页请求类
 *
 * @author xzzz
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ParamPageReq extends PageReq implements Serializable {

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
