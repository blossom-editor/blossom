package com.blossom.backend.server.todo.pojo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 任务列表
 *
 * @since 1.4.0
 */
@Data
public class TaskRes {
    /**
     * 代办事项
     */
    private List<TaskInfoRes> waiting;
    /**
     * 进行中事项
     */
    private List<TaskInfoRes> processing;
    /**
     * 已完成事项
     */
    private List<TaskInfoRes> completed;

    public static TaskRes build() {
        TaskRes res = new TaskRes();
        res.setWaiting(new ArrayList<>());
        res.setProcessing(new ArrayList<>());
        res.setCompleted(new ArrayList<>());
        return res;
    }

}
