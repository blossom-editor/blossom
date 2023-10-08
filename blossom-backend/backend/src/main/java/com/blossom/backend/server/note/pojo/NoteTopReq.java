package com.blossom.backend.server.note.pojo;

import com.blossom.common.base.enums.YesNo;
import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 便签置顶
 *
 * @author xzzz
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NoteTopReq extends AbstractPOJO {

    /**
     * ID
     */
    @NotNull(message = "便签ID为必填项")
    private Long id;

    /**
     * 是否置顶 {@link YesNo}
     */
    @NotNull(message = "是否指定为必填项")
    private Integer top;
}
