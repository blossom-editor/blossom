package com.blossom.backend.server.note.pojo;

import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * 保存便签
 *
 * @author xzzz
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NoteSaveReq extends AbstractPOJO {

    /**
     * 便签内容
     */
    @NotBlank(message = "不能保存空的便签")
    private String content;

}
