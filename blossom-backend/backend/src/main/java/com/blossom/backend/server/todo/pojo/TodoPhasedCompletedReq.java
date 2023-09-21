package com.blossom.backend.server.todo.pojo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 阶段性事项完成
 *
 * @since 1.4.0
 */
@Data
public class TodoPhasedCompletedReq {

    /**
     * 阶段性事项ID
     */
    @NotBlank(message = "阶段性事项ID为必填项")
    private String todoId;
}
