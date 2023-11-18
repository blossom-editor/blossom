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
    private Integer waiting;
    /**
     * 进行中数
     */
    private Integer processing;
    /**
     * 完成数
     */
    private Integer completed;
    /**
     * 总数
     */
    private Integer total;

    public static TaskStatisticRes build() {
        TaskStatisticRes res = new TaskStatisticRes();
        res.setDates(new ArrayList<>());
        res.setRates(new ArrayList<>());
        res.setWaiting(0);
        res.setProcessing(0);
        res.setCompleted(0);
        res.setTotal(0);
        return res;
    }
}
