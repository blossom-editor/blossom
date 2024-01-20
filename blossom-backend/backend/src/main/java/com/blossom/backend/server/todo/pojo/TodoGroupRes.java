package com.blossom.backend.server.todo.pojo;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 待办事项分组列表
 *
 * @since 1.4.0
 */
@Data
public class TodoGroupRes {

    /**
     * 每日待办事项
     */
    private Map<String, TodoGroup> todoDays;
    /**
     * 开启的阶段性事项
     */
    private List<TodoGroup> taskPhased;
    /**
     * 关闭的阶段性事项
     */
    private List<TodoGroup> taskPhasedClose;

    public static TodoGroupRes build() {
        TodoGroupRes res = new TodoGroupRes();
        res.setTodoDays(new HashMap<>());
        res.setTaskPhased(new ArrayList<>());
        res.setTaskPhasedClose(new ArrayList<>());
        return res;
    }

    @Data
    public static class TodoGroup {

        /**
         * 事项ID
         */
        private String todoId;
        /**
         * 事项名称
         */
        private String todoName;
        /**
         * 事项类型 10:每日待办事项 | 20:阶段性事项
         */
        private Integer todoType;
        /**
         * 任务数量
         */
        private Integer taskCount;
        /**
         * 任务数量说明
         */
        private String taskCountStat;
        /**
         * 事项状态 1:完成 | 2:未完成
         */
        private Integer todoStatus;
    }
}
