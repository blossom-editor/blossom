package com.blossom.backend.server.picture.pojo;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 删除图片
 *
 * @since 1.6.0 增加忽略图片引用参数
 */
@Data
public class PictureDelReq {

    /**
     * ID
     */
    @NotNull(message = "ID为必填项")
    @Min(value = 0, message = "ID不能小于0")
    private Long id;

    /**
     * 忽略图片引用检查, 强制进行删除
     */
    private Boolean ignoreCheck;
}
