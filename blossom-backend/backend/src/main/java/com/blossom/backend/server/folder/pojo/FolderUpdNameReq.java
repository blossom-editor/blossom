package com.blossom.backend.server.folder.pojo;

import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 修改文章名称
 *
 * @since 1.10.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FolderUpdNameReq extends AbstractPOJO {

    /**
     * ID
     */
    @Min(value = 0, message = "[文件夹ID] 不能小于0")
    @NotNull(message = "[文件夹ID] 为必填项")
    private Long id;
    /**
     * 名称
     */
    @NotBlank(message = "文件夹名称为必填项")
    private String name;
}
