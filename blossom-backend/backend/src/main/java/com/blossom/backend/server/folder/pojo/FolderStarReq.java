package com.blossom.backend.server.folder.pojo;


import com.blossom.common.base.enums.YesNo;
import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 文章 star
 *
 * @author xzzz
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FolderStarReq extends AbstractPOJO {

    /**
     * 目录ID
     */
    @Min(value = 0, message = "[目录ID] 不能小于0")
    @NotNull(message = "[目录ID] 为必填项")
    private Long id;

    /**
     * star 状态 {@link YesNo}
     * @see YesNo
     */
    @Min(value = 0, message = "[star 状态] 不能小于0")
    @Max(value = 1, message = "[star 状态] 不能大于1")
    @NotNull(message = "[star 状态] 为必填项")
    private Integer starStatus;
}
