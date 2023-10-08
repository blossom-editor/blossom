package com.blossom.backend.server.todo.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 任务信息
 *
 * @since 1.4.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskInfoRes {

    /**
     * 任务ID
     */
    private Long id;
    /**
     * 事项ID
     */
    private String todoId;
    /**
     * 待办类型
     **/
    private Integer todoType;
    /**
     * 任务名称
     */
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
     * 任务状态 WAITING | PROCESSING | COMPLETED
     */
    private String taskStatus;
    /**
     * 截止至, 可填写任意内容
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
     * 创建时间
     */
    private Date creTime;
}
