package com.blossom.backend.server.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import com.blossom.backend.server.todo.TaskStatusEnum;
import com.blossom.backend.server.todo.TodoTypeEnum;
import com.blossom.backend.server.todo.pojo.TodoEntity;
import com.blossom.backend.server.todo.pojo.TodoExportReq;
import com.blossom.common.base.util.DateUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TodoUtil {

    /**
     * 添加时间分割节点, 只有每日待办事项具有分割节点
     *
     * @param divideDate 分割的日期, 注意
     * @param tasks      任务列表
     */
    public static void addDateDivider(Date divideDate, List<TodoEntity> tasks, TaskStatusEnum taskStatus) {
        if (CollUtil.isEmpty(tasks)) {
            return;
        }
        for (int i = 0; i < tasks.size(); i++) {
            Date date;
            if (taskStatus == TaskStatusEnum.PROCESSING) {
                date = tasks.get(i).getStartTime();
            } else if (taskStatus == TaskStatusEnum.COMPLETED) {
                date = tasks.get(i).getEndTime();
            } else {
                continue;
            }
            if (date == null) {
                continue;
            }
            if (DateUtils.compare(divideDate, date) < 0) {
                TodoEntity placeHolder = new TodoEntity();
                placeHolder.setTodoType(TodoTypeEnum.NOON_AM_12.getType());
                tasks.add(i, placeHolder);
                break;
            }
        }
    }

    /**
     * 导出
     *
     * @param todos 事项列表
     * @return 返回 Markdown 格式的字符串
     */
    public static String export(List<TodoEntity> todos, TodoExportReq req) {
        if (CollUtil.isEmpty(todos)) {
            return "无内容";
        }

        List<Map.Entry<String, List<TodoEntity>>> entryList = todos.stream()
                .collect(Collectors.groupingBy(TodoEntity::getTodoName))
                // 增加根据Todo name的排序
                .entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        entryList.forEach(entry -> {
            String todoName = entry.getKey();
            List<TodoEntity> tasks = entry.getValue();
            sb.append(String.format("# %s \n\n", todoName));

            for (TodoEntity task : tasks) {

                String status = "未开始";
                if (task.getTaskStatus().equals(TaskStatusEnum.PROCESSING.name())) {
                    status = "进行中";
                } else if (task.getTaskStatus().equals(TaskStatusEnum.COMPLETED.name())) {
                    status = "已完成";
                }

                // 在标题增加进度
                if (req.getExportProcess() && task.getProcess() != null && task.getProcess() > 0) {
                    sb.append(String.format("### [%s] %s (%d%%)\n\n", status, task.getTaskName(), task.getProcess()));
                } else {
                    sb.append(String.format("### [%s] %s \n\n", status, task.getTaskName()));
                }

                // 导出时间
                if (req.getExportDate()) {
                    String creTime = task.getCreTime() == null ? "" : DateUtils.format(task.getCreTime(), DateUtils.PATTERN_YYYYMMDDHHMMSS);
                    String starTime = task.getStartTime() == null ? "" : DateUtils.format(task.getStartTime(), DateUtils.PATTERN_YYYYMMDDHHMMSS);
                    String endTime = task.getEndTime() == null ? "" : DateUtils.format(task.getEndTime(), DateUtils.PATTERN_YYYYMMDDHHMMSS);

                    sb.append(String.format("- 创建日期: %s\n", creTime));
                    sb.append(String.format("- 开始日期: %s\n", starTime));
                    sb.append(String.format("- 完成日期: %s\n\n", endTime));
                }
                if (StrUtil.isNotBlank(task.getTaskContent())) {
                    sb.append(String.format("%s\n\n", task.getTaskContent()));
                }
            }
        });
        return sb.toString();
    }

    public static void main(String[] args) {
//        TodoEntity cs = new TodoEntity();
//        cs.setTodoName("2023-09-15");
//        cs.setTaskName("测试测试");
//        cs.setProcess(35);
//        cs.setTaskContent("");
//        cs.setCreTime(new Date());
//        cs.setStartTime(new Date());
//        cs.setEndTime(new Date());
//        TodoService.export(CollUtil.newArrayList(cs, cs), true, true);

        for (DateTime dateTime : DateUtils.range(DateUtils.parse("2023-09-01"), DateUtils.parse("2023-09-11"), DateField.DAY_OF_MONTH)) {
            System.out.println(dateTime.toString(DateUtils.PATTERN_YYYYMMDD));
        }
    }

}
