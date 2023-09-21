package com.blossom.backend.server.todo.pojo;

import lombok.Data;

/**
 * 待办事项导出
 *
 * @since 1.4.0
 */
@Data
public class TodoExportReq {

    /**
     * 阶段性事项ID
     */
    private String todoId;
    /**
     * 每日事项开始日期
     */
    private String beginTodoId;
    /**
     * 每日事项结束日期
     */
    private String endTodoId;
    /**
     * 是否导出日期
     */
    private Boolean exportDate;
    /**
     * 是否导出进度
     */
    private Boolean exportProcess;
}
