package com.blossom.backend.server.picture.pojo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 移动图片
 *
 * @since 1.10.0
 */
@Data
public class PictureTransferReq {

    /**
     * ID
     */
    @NotNull(message = "ID为必填项")
    private List<Long> ids;

    /**
     * 文件夹ID
     */
    @NotNull(message = "文件夹ID为必填项")
    private Long pid;
}
