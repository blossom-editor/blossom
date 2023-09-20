package com.blossom.backend.server.todo;

import cn.hutool.core.util.BooleanUtil;
import com.blossom.backend.server.todo.pojo.*;
import com.blossom.common.base.pojo.R;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @since 1.4.0
 */
@RestController
@AllArgsConstructor
@RequestMapping("/todo/task")
public class TaskController {

    private final TodoService baseService;

    /**
     * 任务列表
     *
     * @param todoId todoId
     */
    @GetMapping("/list")
    public R<TaskRes> list(@RequestParam("todoId") String todoId) {
        return R.ok(baseService.listTask(todoId));
    }

    /**
     * 任务详情
     *
     * @param id 任务ID
     */
    @GetMapping("/info")
    public R<TaskInfoRes> info(@RequestParam("id") Long id) {
        return R.ok(baseService.selectById(id));
    }

    /**
     * 任务数量
     *
     * @param todoId todoId
     */
    @GetMapping("/count")
    public R<Integer> count(@RequestParam("todoId") String todoId) {
        return R.ok(baseService.count(todoId));
    }

    /**
     * 统计
     */
    @GetMapping("/stat")
    public R<TaskStatisticRes> stat() {
        return R.ok(baseService.statistic());
    }

    /**
     * 修改任务
     *
     * @param req 请求对象
     * @return 任务列表
     */
    @PostMapping("/add")
    public R<TaskRes> add(@RequestBody @Validated TaskAddReq req) {
        baseService.insert(req);
        return R.ok(baseService.listTask(req.getTodoId()));
    }

    /**
     * 修改任务
     *
     * @param req 请求对象
     * @return 当 req.getReturnTasks 为 true 时, 返回列表, 否则返回空列表, 用于提高响应效率
     */
    @PostMapping("/upd")
    public R<TaskRes> upd(@RequestBody @Validated TaskUpdReq req) {
        baseService.updById(req);
        if (BooleanUtil.isTrue(req.getReturnTasks())) {
            return R.ok(baseService.listTask(req.getTodoId()));
        }
        return R.ok(TaskRes.build());
    }

    @PostMapping("/waiting")
    public R<TaskRes> toWaiting(@RequestBody @Validated TaskUpdStatusReq req) {
        baseService.toWaiting(req);
        return R.ok(baseService.listTask(req.getTodoId()));
    }

    @PostMapping("/processing")
    public R<TaskRes> toProcessing(@RequestBody @Validated TaskUpdStatusReq req) {
        baseService.toProcessing(req);
        return R.ok(baseService.listTask(req.getTodoId()));
    }

    @PostMapping("/completed")
    public R<TaskRes> toCompleted(@RequestBody @Validated TaskUpdStatusReq req) {
        baseService.toCompleted(req);
        return R.ok(baseService.listTask(req.getTodoId()));
    }

    /**
     * 删除节点
     */
    @PostMapping("/del")
    public R<TaskRes> toProcessing(@RequestBody @Validated TaskDelReq req) {
        baseService.delById(req.getId());
        return R.ok(baseService.listTask(req.getTodoId()));
    }
}
