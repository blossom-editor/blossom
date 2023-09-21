package com.blossom.backend.server.todo.pojo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 任务统计列表
 *
 * @since 1.4.0
 */
@Data
public class TaskStatisticRes {

    private List<String> dates;

    private List<Long> rates;

    private Long waiting;
    private Long processing;
    private Long completed;
    private Long total;

    public static TaskStatisticRes build() {
        TaskStatisticRes res = new TaskStatisticRes();
        res.setDates(new ArrayList<>());
        res.setRates(new ArrayList<>());
        res.setWaiting(0L);
        res.setProcessing(0L);
        res.setCompleted(0L);
        res.setTotal(0L);
        return res;
    }
}
