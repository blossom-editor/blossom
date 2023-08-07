package com.blossom.backend.base.param.pojo;

import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 系统参数响应实体，会排除敏感字段
 *
 * @author xzzz
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ParamRes extends AbstractPOJO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 参数名称 */
    private String paramName;
    /** 参数值 */
    private String paramValue;
}
