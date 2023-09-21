package com.blossom.backend.server.todo.pojo;

import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 任务状态修改
 *
 * @since 1.4.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskUpdStatusReq extends AbstractPOJO {

    /**
     * ID
     */
    @NotNull(message = "任务ID为必填项")
    private Long id;
    /**
     * todoId
     */
    @NotBlank(message = "事项ID为必填项")
    private String todoId;
}
