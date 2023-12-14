package com.blossom.backend.server.picture.pojo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 批量删除图片
 *
 * @since 1.10.0
 */
@Data
public class PictureDelBatchReq {

    /**
     * ID
     */
    @NotNull(message = "ID为必填项")
    private List<Long> ids;

    /**
     * 忽略图片引用检查, 强制进行删除
     */
    private Boolean ignoreCheck;
}
