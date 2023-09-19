package com.blossom.backend.server.todo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blossom.backend.base.auth.AuthContext;
import com.blossom.backend.server.todo.pojo.*;
import com.blossom.common.base.exception.XzException404;
import com.blossom.common.base.util.DateUtils;
import com.blossom.common.base.util.PrimaryKeyUtil;
import com.blossom.common.base.util.SortUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 待办事项
 */
@Slf4j
@Service
@AllArgsConstructor
public class TodoService extends ServiceImpl<TodoMapper, TodoEntity> {

    // region 待办事项

    /**
     * 全部列表
     */
    public TodoGroupRes listTodo(String todoName) {
        List<TodoEntity> todos = baseMapper.listTodo(AuthContext.getUserId(), todoName);
        TodoGroupRes res = TodoGroupRes.build();
        for (TodoEntity todo : todos) {
            TodoGroupRes.TodoGroup group = todo.to(TodoGroupRes.TodoGroup.class);
            if (TodoTypeEnum.DAY.getType().equals(todo.getTodoType())) {
                res.getTodoDays().put(group.getTodoId(), group);
            } else {
                // 未完成的阶段性事项
                if (TodoStatusEnum.OPEN.getType().equals(todo.getTodoStatus())) {
                    res.getTaskPhased().add(group);
                } else {
                    res.getTaskPhasedClose().add(group);
                }
            }
        }
        return res;
    }


    /**
     * 新增阶段性事项
     */
    @Transactional(rollbackFor = Exception.class)
    public void addPhased(TodoPhasedAddReq req) {
        TodoEntity todo = new TodoEntity();
        todo.setTodoId(String.valueOf(PrimaryKeyUtil.nextId()));
        todo.setTodoName(req.getTodoName());
        todo.setTodoType(TodoTypeEnum.PHASED.getType());
        todo.setTaskName(req.getTodoName());
        todo.setTaskStatus(TaskStatusEnum.WAITING.name());
        todo.setUserId(AuthContext.getUserId());
        baseMapper.insert(todo);
    }

    /**
     * 开启阶段性事项
     *
     * @param todoId 事项ID
     */
    public void openPhased(String todoId) {
        baseMapper.updTodoStatus(todoId, TodoStatusEnum.OPEN.getType());
    }

    /**
     * 完成阶段性事项
     *
     * @param todoId 事项ID
     */
    public void completedPhased(String todoId) {
        baseMapper.updTodoStatus(todoId, TodoStatusEnum.COMPLETED.getType());
    }

    /**
     * 修改todo名称
     */
    @Transactional(rollbackFor = Exception.class)
    public void updTodoName(TodoPhasedUpdReq req) {
        baseMapper.updTodoName(req);
    }

    /**
     * 查询待办信息
     */
    public TodoEntity selectByTodoId(String todoId) {
        return baseMapper.selectByTodoId(todoId);
    }

    // endregion


    // region 任务

    /**
     * 获取待办任务列表
     *
     * @param todoId todoId, 每日待办事项, 则 todoId 为 yyyy-MM-dd 格式, 如果是阶段性事项, 则为字符串ID
     */
    public TaskRes listTask(String todoId) {
        List<TodoEntity> tasks = baseMapper.listTask(todoId);
        if (CollUtil.isEmpty(tasks)) {
            return TaskRes.build();
        }

        Map<String, List<TodoEntity>> maps = tasks.stream().collect(Collectors.groupingBy(TodoEntity::getTaskStatus));
        TaskRes res = new TaskRes();
        res.setWaiting(maps.getOrDefault(TaskStatusEnum.WAITING.name(), new ArrayList<>())
                .stream()
                .sorted((t1, t2) -> SortUtil.dateSort.compare(t1.getCreTime(), t2.getCreTime()))
                .collect(Collectors.toList())
        );

        res.setProcessing(maps.getOrDefault(TaskStatusEnum.PROCESSING.name(), new ArrayList<>())
                .stream()
                .sorted((t1, t2) -> SortUtil.dateSort.compare(t1.getStartTime(), t2.getStartTime()))
                .collect(Collectors.toList())
        );

        res.setCompleted(maps.getOrDefault(TaskStatusEnum.COMPLETED.name(), new ArrayList<>())
                .stream()
                .sorted((t1, t2) -> SortUtil.dateSort.compare(t1.getEndTime(), t2.getEndTime()))
                .collect(Collectors.toList())
        );

        // 如果是阶段性事项, 则直接返回
        if (tasks.get(0).getTodoType().equals(TodoTypeEnum.PHASED.getType())) {
            return res;
        }

        // 今日中午12点
        Date noon = DateUtils.parse(todoId + " 12:00:00", DateUtils.PATTERN_YYYYMMDDHHMMSS);
        List<TodoEntity> processing = res.getProcessing();
        addNoon(noon, processing);
        List<TodoEntity> completed = res.getCompleted();
        addNoon(noon, completed);
        return res;
    }

    /**
     * 查看事项的任务数量
     *
     * @param todoId 事项ID
     */
    public Integer count(String todoId) {
        return baseMapper.count(todoId);
    }

    /**
     * 添加中午12点的分割节点
     *
     * @param noon  中午的日期
     * @param tasks 任务列表
     */
    private void addNoon(Date noon, List<TodoEntity> tasks) {
        if (CollUtil.isEmpty(tasks)) {
            return;
        }
        for (int i = 0; i < tasks.size(); i++) {
            Date start = tasks.get(i).getStartTime();
            if (DateUtils.compare(noon, start) < 0) {
                TodoEntity placeHolder = new TodoEntity();
                placeHolder.setTodoType(TodoTypeEnum.NOON_AM_12.getType());
                tasks.add(i, placeHolder);
                break;
            }
        }
    }

    /**
     * 查询任务信息
     *
     * @param id 任务ID
     */
    public TodoEntity selectById(Long id) {
        return baseMapper.selectById(id);
    }

    /**
     * 新增
     */
    @Transactional(rollbackFor = Exception.class)
    public void insert(TaskAddReq req) {

        TodoEntity todo = selectByTodoId(req.getTodoId());
        TodoEntity ist = req.to(TodoEntity.class);

        if (todo == null) {
            ist.setTodoName(req.getTodoId());
            ist.setTodoType(TodoTypeEnum.DAY.getType());
            ist.setTodoStatus(TodoStatusEnum.OPEN.getType());
        } else {
            ist.setTodoName(todo.getTodoName());
            ist.setTodoStatus(todo.getTodoStatus());
            ist.setTodoType(todo.getTodoType());
        }

        ist.setTaskStatus(TaskStatusEnum.WAITING.name());
        ist.setUserId(AuthContext.getUserId());
        baseMapper.insert(ist);
    }

    /**
     * 修改
     */
    @Transactional(rollbackFor = Exception.class)
    public void updById(TaskUpdReq req) {
        TodoEntity task = req.to(TodoEntity.class);
        baseMapper.updateById(task);
    }

    /**
     * 移动到待办
     */
    @Transactional(rollbackFor = Exception.class)
    public void toWaiting(TaskUpdStatusReq req) {
        TodoEntity task = req.to(TodoEntity.class);
        task.setTaskStatus(TaskStatusEnum.WAITING.name());
        baseMapper.updateById(task);
    }

    /**
     * 移动到进行中
     */
    @Transactional(rollbackFor = Exception.class)
    public void toProcessing(TaskUpdStatusReq req) {
        TodoEntity task = req.to(TodoEntity.class);
        task.setTaskStatus(TaskStatusEnum.PROCESSING.name());
        task.setStartTime(DateUtils.date());
        baseMapper.updateById(task);
    }

    /**
     * 移动到完成
     */
    @Transactional(rollbackFor = Exception.class)
    public void toCompleted(TaskUpdStatusReq req) {
        TodoEntity task = req.to(TodoEntity.class);
        task.setTaskStatus(TaskStatusEnum.COMPLETED.name());
        task.setEndTime(DateUtils.date());
        baseMapper.updateById(task);
    }

    /**
     * 删除
     */
    @Transactional(rollbackFor = Exception.class)
    public void delById(Long id) {
        baseMapper.deleteById(id);
    }

    // endregion


    //region 统计与导出

    /**
     * 全局任务统计, 包含任务折线图, 各阶段任务数量, 该统计数据包含每日待办事项和阶段性事项
     */
    public TaskStatisticRes statistic() {
        String now = DateUtils.now();
        // 30 天前
        String nowOffset_30Day = DateUtils.format(DateUtils.lastMonth(), DateUtils.PATTERN_YYYYMMDDHHMMSS);
        List<TodoEntity> all = baseMapper.statisticTask(nowOffset_30Day, now, AuthContext.getUserId());
        if (CollUtil.isEmpty(all)) {
            return TaskStatisticRes.build();
        }

        Map<String, List<TodoEntity>> dateMap = new HashMap<>();
        for (TodoEntity todo : all) {
            String date = DateUtils.format(todo.getCreTime(), DateUtils.PATTERN_YYYYMMDD);
            List<TodoEntity> todos = dateMap.getOrDefault(date, new ArrayList<>());
            todos.add(todo);
            dateMap.put(date, todos);
        }


        List<String> dates = new ArrayList<>();
        List<Long> rates = new ArrayList<>();

        Date today = DateUtils.date();
        Date lastMonth = DateUtils.lastMonth();
        while (DateUtils.compare(lastMonth, today, DateUtils.PATTERN_YYYYMMDD) <= 0) {
            String dayStr = DateUtils.format(lastMonth, DateUtils.PATTERN_YYYYMMDD);
            lastMonth = DateUtils.offsetDay(lastMonth, 1);

            List<TodoEntity> tasks = dateMap.get(dayStr);
            dates.add(dayStr.substring(5));
            if (CollUtil.isEmpty(tasks)) {
                rates.add(0L);
                continue;
            }
            long taskCount = tasks.size();
            long completed = tasks.stream().filter(t -> t.getTaskStatus().equals(TaskStatusEnum.COMPLETED.name())).count();
            if (completed == 0) {
                rates.add(0L);
            } else {
                rates.add(Math.round((completed * 1.0 / taskCount * 1.0) * 100));
            }
        }

        TaskStatisticRes res = new TaskStatisticRes();
        res.setDates(dates);
        res.setRates(rates);
        res.setTotal((long) all.size());
        res.setWaiting(all.stream().filter(t -> t.getTaskStatus().equals(TaskStatusEnum.WAITING.name())).count());
        res.setProcessing(all.stream().filter(t -> t.getTaskStatus().equals(TaskStatusEnum.PROCESSING.name())).count());
        res.setCompleted(all.stream().filter(t -> t.getTaskStatus().equals(TaskStatusEnum.COMPLETED.name())).count());
        return res;
    }


    /**
     * 指定事项统计信息
     *
     * @param todoId 事项ID
     */
    public TodoStatisticRes statistic(String todoId) {
        TodoStatisticRes res = baseMapper.statisticTodo(todoId);
        if (res == null) {
            return TodoStatisticRes.buildDay(todoId);
        }
        if (res.getTodoType().equals(TodoTypeEnum.DAY.getType())) {
            res.setTodoName("每日待办事项: " + res.getTodoName());
        } else {
            res.setTodoName("阶段性事项: " + res.getTodoName());
        }
        if (StrUtil.isBlank(res.getFirstCreTime())) {
            res.setFirstCreTime("无任务");
        }
        if (StrUtil.isBlank(res.getFirstStartTime())) {
            res.setFirstStartTime("无任务开始");
        }
        if (StrUtil.isBlank(res.getLastEndTime())) {
            res.setLastEndTime("无任务完成");
        }
        return res;
    }

    /**
     * 导出为 Markdown
     */
    public String export(TodoExportReq req) {
        List<TodoEntity> todos = new ArrayList<>();
        if (StrUtil.isNotBlank(req.getTodoId())) {
            TodoEntity query = new TodoEntity();
            query.setTodoId(req.getTodoId());
            todos.addAll(baseMapper.listAll(query));
        } else {
            TodoEntity query = new TodoEntity();
            query.setUserId(AuthContext.getUserId());
            query.setTodoType(TodoTypeEnum.DAY.getType());
            List<String> todoIds = new ArrayList<>();
            for (DateTime dateTime : DateUtils.range(DateUtils.parse(req.getBeginTodoId()), DateUtils.parse(req.getEndTodoId()), DateField.DAY_OF_MONTH)) {
                todoIds.add(dateTime.toString(DateUtils.PATTERN_YYYYMMDD));
            }
            query.setTodoIds(todoIds);
            todos.addAll(baseMapper.listAll(query));
        }
        XzException404.throwBy(CollUtil.isEmpty(todos), "未查询到任何待办任务, 无法导出");
        return export(todos, req);
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
        Map<String, List<TodoEntity>> maps = todos.stream().collect(Collectors.groupingBy(TodoEntity::getTodoName));
        StringBuilder sb = new StringBuilder();
        maps.forEach((todoName, tasks) -> {
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


    //endregion
}