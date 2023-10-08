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
    /**
     * 日期
     */
    private List<String> dates;
    /**
     * 对应日期的完成率
     */
    private List<Long> rates;
    /**
     * 待完成数
     */
    private Long waiting;
    /**
     * 进行中数
     */
    private Long processing;
    /**
     * 完成数
     */
    private Long completed;
    /**
     * 总数
     */
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
