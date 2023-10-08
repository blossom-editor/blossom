package com.blossom.backend.server.folder.pojo;

import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 文件夹公开关闭请求
 *
 * @author xzzz
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FolderOpenCloseReq extends AbstractPOJO implements Serializable {

    private static final long serialVersionUID = 1L;
    /** id */
    @NotNull(message = "[文件夹ID] 为必填项")
    private Long id;

    /**
     * open 状态 {@link YesNo}
     * @see com.blossom.common.base.enums.YesNo
     */
    @Min(value = 0, message = "[open 状态] 不能小于0")
    @Max(value = 1, message = "[open 状态] 不能大于1")
    @NotNull(message = "[open 状态] 为必填项")
    private Integer openStatus;
}
