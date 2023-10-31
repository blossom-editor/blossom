package com.blossom.backend.server.todo.pojo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 任务转移
 *
 * @since 1.8.0
 */
@Data
public class TaskTransferReq {

    /**
     * 任务ID集合
     */
    private List<Long> taskIds;

    /**
     * 被转移的各项任务所在的待办事项ID
     */
    @NotBlank(message = "被转移任务所在的待办事项ID为必填项")
    private String curTodoId;

    /**
     * 转移到的事项ID
     */
    @NotBlank(message = "待办事项ID为必填项")
    private String todoId;

    /**
     * 是否删除原事项
     */
    private Boolean delSource;
}
