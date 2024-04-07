package com.blossom.backend.server.todo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blossom.backend.base.auth.AuthContext;
import com.blossom.backend.server.todo.pojo.*;
import com.blossom.backend.server.utils.DocUtil;
import com.blossom.backend.server.utils.TodoUtil;
import com.blossom.common.base.exception.XzException400;
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
 *
 * @since 1.4.0
 */
@Slf4j
@Service
@AllArgsConstructor
public class TodoService extends ServiceImpl<TodoMapper, TodoEntity> {

    // region 待办事项

    /**
     * 全部列表
     */
    public TodoGroupRes listTodo() {
        List<TodoEntity> todos = baseMapper.listTodo(AuthContext.getUserId());
        TodoGroupRes res = TodoGroupRes.build();

        Map<String, List<TodoEntity>> map = todos.stream().collect(Collectors.groupingBy(TodoEntity::getTodoId));

        // 遍历每个待办事项的所有任务
        map.forEach((todoId, data) -> {
            TodoGroupRes.TodoGroup group = data.get(0).to(TodoGroupRes.TodoGroup.class);
            // 根据状态分组
            Map<String, List<TodoEntity>> taskStatusMap = data.stream().collect(Collectors.groupingBy(TodoEntity::getTaskStatus));
            int w = 0, p = 0, c = 0;
            if (CollUtil.isNotEmpty(taskStatusMap.get(TaskStatusEnum.WAITING.name()))) {
                w = taskStatusMap.get(TaskStatusEnum.WAITING.name()).get(0).getTaskCount();
            }
            if (CollUtil.isNotEmpty(taskStatusMap.get(TaskStatusEnum.PROCESSING.name()))) {
                p = taskStatusMap.get(TaskStatusEnum.PROCESSING.name()).get(0).getTaskCount();
            }
            if (CollUtil.isNotEmpty(taskStatusMap.get(TaskStatusEnum.COMPLETED.name()))) {
                c = taskStatusMap.get(TaskStatusEnum.COMPLETED.name()).get(0).getTaskCount();
            }
            group.setTaskCountStat(String.format("%d/%d/%d", w, p, c));
            group.setTaskCount(w + p + c);

            if (TodoTypeEnum.DAY.getType().equals(data.get(0).getTodoType())) {
                res.getTodoDays().put(group.getTodoId(), group);
            } else {
                // 未完成的阶段性事项
                if (TodoStatusEnum.OPEN.getType().equals(data.get(0).getTodoStatus())) {
                    res.getTaskPhased().add(group);
                } else {
                    res.getTaskPhasedClose().add(group);
                }
            }
        });
        return res;
    }

    /**
     * 获取全部标签
     *
     * @param todoType 待办事项类型
     * @param todoId   待办事项ID
     * @param userId   用户ID
     * @return 标签集合
     */
    public Set<String> tags(TodoTypeEnum todoType, String todoId, Long userId) {
        List<String> todoIds = new ArrayList<>();
        if (todoType == TodoTypeEnum.DAY) {
            Date today = DateUtil.date();
            for (DateTime dateTime : DateUtils.range(today, DateUtil.offsetDay(today, 7), DateField.DAY_OF_MONTH)) {
                todoIds.add(DateUtil.formatDate(dateTime));
            }
            for (DateTime dateTime : DateUtils.range(DateUtil.offsetDay(today, -31), today, DateField.DAY_OF_MONTH)) {
                todoIds.add(DateUtil.formatDate(dateTime));
            }
        } else {
            if (StrUtil.isBlank(todoId)) {
                return new HashSet<>();
            }
            todoIds.add(todoId);
        }
        TodoEntity where = new TodoEntity();
        where.setTodoIds(todoIds);
        where.setUserId(userId);
        List<TodoEntity> todos = baseMapper.listAll(where);
        Set<String> tags = new HashSet<>();
        for (TodoEntity todo : todos) {
            tags.addAll(DocUtil.toTagList(todo.getTaskTags()));
        }
        return tags;
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
        List<TodoEntity> tasks = baseMapper.listTask(AuthContext.getUserId(), todoId);
        if (CollUtil.isEmpty(tasks)) {
            return TaskRes.build();
        }

        Map<String, List<TodoEntity>> maps = tasks.stream().collect(Collectors.groupingBy(TodoEntity::getTaskStatus));

        List<TodoEntity> waiting = maps.getOrDefault(TaskStatusEnum.WAITING.name(), new ArrayList<>()).stream()
                .sorted((t1, t2) -> SortUtil.dateSort.compare(t1.getCreTime(), t2.getCreTime()))
                .collect(Collectors.toList());

        List<TodoEntity> processing = maps.getOrDefault(TaskStatusEnum.PROCESSING.name(), new ArrayList<>()).stream()
                .sorted((t1, t2) -> SortUtil.dateSort.compare(t1.getStartTime(), t2.getStartTime()))
                .collect(Collectors.toList());

        List<TodoEntity> completed = maps.getOrDefault(TaskStatusEnum.COMPLETED.name(), new ArrayList<>()).stream()
                .sorted((t1, t2) -> SortUtil.dateSort.compare(t1.getEndTime(), t2.getEndTime()))
                .collect(Collectors.toList());

        // 如果每日任务, 则添加分割节点
        if (tasks.get(0).getTodoType().equals(TodoTypeEnum.DAY.getType())) {
            Date noon = DateUtils.parse(todoId + " 12:00:00", DateUtils.PATTERN_YYYYMMDDHHMMSS);
            TodoUtil.addDateDivider(noon, processing, TaskStatusEnum.PROCESSING);
            TodoUtil.addDateDivider(noon, completed, TaskStatusEnum.COMPLETED);
        }

        List<TaskInfoRes> wTasks = new ArrayList<>();
        for (TodoEntity task : waiting) {
            TaskInfoRes wTask = task.to(TaskInfoRes.class);
            wTask.setTaskTags(DocUtil.toTagList(task.getTaskTags()));
            wTasks.add(wTask);
        }

        List<TaskInfoRes> pTasks = new ArrayList<>();
        for (TodoEntity task : processing) {
            TaskInfoRes pTask = task.to(TaskInfoRes.class);
            pTask.setTaskTags(DocUtil.toTagList(task.getTaskTags()));
            pTasks.add(pTask);
        }

        List<TaskInfoRes> cTasks = new ArrayList<>();
        for (TodoEntity task : completed) {
            TaskInfoRes cTask = task.to(TaskInfoRes.class);
            cTask.setTaskTags(DocUtil.toTagList(task.getTaskTags()));
            cTasks.add(cTask);
        }

        TaskRes res = new TaskRes();
        res.setWaiting(wTasks);
        res.setProcessing(pTasks);
        res.setCompleted(cTasks);

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
     * 查询任务信息
     *
     * @param id 任务ID
     */
    public TaskInfoRes selectById(Long id) {
        TodoEntity task = baseMapper.selectById(id);
        TaskInfoRes res = task.to(TaskInfoRes.class);
        res.setTaskTags(DocUtil.toTagList(task.getTaskTags()));
        return res;
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

        ist.setTaskTags(DocUtil.toTagStr(req.getTaskTags()));
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
        if (req.getTaskTags() != null) {
            task.setTaskTags(DocUtil.toTagStr(req.getTaskTags()));
        }
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

    /**
     * 转移任务
     *
     * @since 1.8.0
     */
    @Transactional(rollbackFor = Exception.class)
    public void transfer(TaskTransferReq req) {
        if (CollUtil.isEmpty(req.getTaskIds())) {
            return;
        }
        if (req.getCurTodoId().equals(req.getTodoId())) {
            throw new XzException400("转移日期与原日期不能相同");
        }
        TodoEntity query = new TodoEntity();
        query.setIds(req.getTaskIds());
        query.setUserId(AuthContext.getUserId());
        List<TodoEntity> tasks = baseMapper.listAll(query);
        for (TodoEntity task : tasks) {
            // 任务已经在目前事项中, 则跳过
            if (task.getTodoId().equals(req.getTodoId())) {
                continue;
            }
            task.setId(null);
            task.setTodoId(req.getTodoId());
            task.setTodoName(req.getTodoId());
            baseMapper.insert(task);
        }
        if (req.getDelSource()) {
            baseMapper.delByIds(req.getTaskIds(), AuthContext.getUserId());
        }
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

        TaskStatisticRes res = TaskStatisticRes.build();
        res.setDates(dates);
        res.setRates(rates);

        List<TodoEntity> counts = baseMapper.statisticTaskCount(AuthContext.getUserId());
        for (TodoEntity count : counts) {
            if (count.getTaskStatus().equals(TaskStatusEnum.WAITING.name())) {
                res.setWaiting(count.getTaskCount());
            }
            if (count.getTaskStatus().equals(TaskStatusEnum.PROCESSING.name())) {
                res.setProcessing(count.getTaskCount());
            }
            if (count.getTaskStatus().equals(TaskStatusEnum.COMPLETED.name())) {
                res.setCompleted(count.getTaskCount());
            }
        }
        res.setTotal(res.getWaiting() + res.getProcessing() + res.getCompleted());
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
        return TodoUtil.export(todos, req);
    }


    //endregion
}