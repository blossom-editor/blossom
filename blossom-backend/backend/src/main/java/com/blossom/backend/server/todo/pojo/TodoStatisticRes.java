package com.blossom.backend.server.todo.pojo;

import com.blossom.backend.server.todo.TodoStatusEnum;
import com.blossom.backend.server.todo.TodoTypeEnum;
import lombok.Data;

/**
 * 待办事项统计
 *
 * @since 1.4.0
 */
@Data
public class TodoStatisticRes {

    private String todoId;
    /**
     * 事项名称
     */
    private String todoName;
    /**
     * 事项类型
     */
    private Integer todoType;
    /**
     * 事项状态
     */
    private Integer todoStatus;
    /**
     * 最早创建日期
     */
    private String firstCreTime;
    /**
     * 最早开始日期
     */
    private String firstStartTime;
    /**
     * 最后完成日期
     */
    private String lastEndTime;

    public static TodoStatisticRes buildDay(String todoId) {
        TodoStatisticRes res = new TodoStatisticRes();
        res.setTodoId(todoId);
        res.setTodoName("每日待办事项: " + todoId);
        res.setTodoType(TodoTypeEnum.DAY.getType());
        res.setTodoStatus(TodoStatusEnum.OPEN.getType());
        res.setFirstCreTime("无任务");
        res.setFirstStartTime("无任务开始");
        res.setLastEndTime("无任务完成");
        return res;
    }
}
