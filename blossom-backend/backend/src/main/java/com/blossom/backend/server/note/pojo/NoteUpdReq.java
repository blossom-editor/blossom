package com.blossom.backend.server.note.pojo;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 修改便签
 *
 * @since 1.9.0
 */
@Data
public class NoteUpdReq {

    /**
     * ID
     */
    @NotNull(message = "便签ID为必填项")
    @Min(value = 0)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 便签内容
     */
    @NotBlank(message = "不能保存空的便签")
    private String content;
}
