package com.blossom.backend.server.todo.pojo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 修改阶段性事项的名称
 *
 * @since 1.4.0
 */
@Data
public class TodoPhasedUpdReq {

    /**
     * 阶段性事项ID
     */
    @NotBlank(message = "阶段性事项ID为必填项")
    private String todoId;
    /**
     * 阶段性事项名称
     */
    @NotBlank(message = "阶段性事项名称为必填项")
    private String todoName;
}
