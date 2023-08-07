package com.blossom.common.base.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 删除参数
 *
 * @author xzzz
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DelReq extends AbstractPOJO {

    /**
     * ID
     */
    @NotNull(message = "ID为必填项")
    @Min(value = 0, message = "ID不能小于0")
    private Long id;
}
