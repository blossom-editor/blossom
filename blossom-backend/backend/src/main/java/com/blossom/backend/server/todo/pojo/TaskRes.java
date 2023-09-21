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
    private List<TaskInfoRes> waiting;
    private List<TaskInfoRes> processing;
    private List<TaskInfoRes> completed;

    public static TaskRes build() {
        TaskRes res = new TaskRes();
        res.setWaiting(new ArrayList<>());
        res.setProcessing(new ArrayList<>());
        res.setCompleted(new ArrayList<>());
        return res;
    }

}
