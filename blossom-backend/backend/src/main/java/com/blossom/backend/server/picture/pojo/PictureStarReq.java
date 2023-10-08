package com.blossom.backend.server.picture.pojo;


import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 图片 star
 *
 * @author xzzz
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PictureStarReq extends AbstractPOJO {

    /**
     * 文章ID
     */
    @Min(value = 0, message = "[文章ID] 不能小于0")
    @NotNull(message = "[图片ID] 为必填项")
    private Long id;

    /**
     * 是否收藏 {@link YesNo}
     */
    @Min(value = 0, message = "[star 状态] 不能小于0")
    @Max(value = 1, message = "[star 状态] 不能大于1")
    @NotNull(message = "[star 状态] 为必填项")
    private Integer starStatus;
}
