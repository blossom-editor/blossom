package com.blossom.backend.server.todo.pojo;

import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 任务新增
 *
 * @since 1.4.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskAddReq extends AbstractPOJO {

    /**
     * todoId
     */
    @NotBlank(message = "事项ID为必填项")
    private String todoId;
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 任务内容
     */
    private String taskContent;
    /**
     * 截止日期
     */
    private String deadLine;
    /**
     * 颜色
     */
    private String color;
    /**
     * 标签集合
     */
    private List<String> taskTags;
}
