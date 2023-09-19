package com.blossom.backend.server.todo.pojo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TaskRes {
    private List<TodoEntity> waiting;
    private List<TodoEntity> processing;
    private List<TodoEntity> completed;

    public static TaskRes build() {
        TaskRes res = new TaskRes();
        res.setWaiting(new ArrayList<>());
        res.setProcessing(new ArrayList<>());
        res.setCompleted(new ArrayList<>());
        return res;
    }
}
