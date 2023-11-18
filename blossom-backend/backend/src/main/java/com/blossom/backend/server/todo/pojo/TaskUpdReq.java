package com.blossom.backend.server.todo.pojo;

import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;


/**
 * 任务修改
 *
 * @since 1.4.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskUpdReq extends AbstractPOJO {

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
    /**
     * 任务名称
     */
    @NotBlank(message = "待办任务标题为必填项")
    private String taskName;
    /**
     * 任务内容
     */
    private String taskContent;
    /**
     * 标签集合
     */
    private List<String> taskTags;
    /**
     * 截止日期
     */
    private String deadLine;
    /**
     * 开始日期
     */
    private Date startTime;
    /**
     * 结束日期
     */
    private Date endTime;
    /**
     * 进度 0 ~ 100
     */
    private Integer process;
    /**
     * 颜色
     */
    private String color;
    /**
     * 接口是否返回列表
     */
    private Boolean returnTasks;
}
