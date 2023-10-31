package com.blossom.backend.server.todo.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 待办事项
 *
 * @since 1.4.0
 */
@Data
@TableName("blossom_todo")
@EqualsAndHashCode(callSuper = true)
public class TodoEntity extends AbstractPOJO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 事项ID
     */
    private String todoId;
    /**
     * 事项名称
     */
    private String todoName;
    /**
     * 事项状态 1:未完成 | 2:完成 | 9:作废
     */
    private Integer todoStatus;
    /**
     * 事项类型 10:每日待办事项 | 20:阶段性事项
     */
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
    private String taskTags;
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
     * 用户ID
     */
    private Long userId;
    /**
     * 创建时间
     */
    private Date creTime;

    /**
     * 任务数量
     */
    @TableField(exist = false)
    private Integer taskCount;
    @TableField(exist = false)
    private String beginCreTime;
    @TableField(exist = false)
    private String endCreTime;
    @TableField(exist = false)
    private List<Long> ids;
    @TableField(exist = false)
    private List<String> todoIds;
}
