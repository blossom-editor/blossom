package com.blossom.backend.server.todo;

import cn.hutool.core.util.BooleanUtil;
import com.blossom.backend.base.auth.AuthContext;
import com.blossom.backend.server.todo.pojo.*;
import com.blossom.common.base.exception.XzException404;
import com.blossom.common.base.pojo.R;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * 待办事项 [Todo#Task]
 *
 * @order 41
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
     * 标签列表
     *
     * @param todoType 待办事项类型 {@link TodoTypeEnum}
     * @param todoId   待办事项ID, 当待办事项为阶段性事项时传入, 如未传入, 则返回空集合
     * @apiNote 不会存在重复的标签
     */
    @GetMapping("/tags")
    public R<Set<String>> tags(@RequestParam("todoType") Integer todoType,
                               @RequestParam(value = "todoId", required = false) String todoId) {
        TodoTypeEnum todoTypeEnum = TodoTypeEnum.getByType(todoType);
        XzException404.throwBy(todoType == null, "待办事项类型错误");
        return R.ok(baseService.tags(todoTypeEnum, todoId, AuthContext.getUserId()));
    }


    /**
     * 统计
     */
    @GetMapping("/stat")
    public R<TaskStatisticRes> stat() {
        return R.ok(baseService.statistic());
    }

    /**
     * 新增任务
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

    /**
     * 事项移动到待办
     */
    @PostMapping("/waiting")
    public R<TaskRes> toWaiting(@RequestBody @Validated TaskUpdStatusReq req) {
        baseService.toWaiting(req);
        return R.ok(baseService.listTask(req.getTodoId()));
    }

    /**
     * 事项移动到进行中
     */
    @PostMapping("/processing")
    public R<TaskRes> toProcessing(@RequestBody @Validated TaskUpdStatusReq req) {
        baseService.toProcessing(req);
        return R.ok(baseService.listTask(req.getTodoId()));
    }

    /**
     * 事项移动到完成
     */
    @PostMapping("/completed")
    public R<TaskRes> toCompleted(@RequestBody @Validated TaskUpdStatusReq req) {
        baseService.toCompleted(req);
        return R.ok(baseService.listTask(req.getTodoId()));
    }

    /**
     * 删除事项
     */
    @PostMapping("/del")
    public R<TaskRes> toProcessing(@RequestBody @Validated TaskDelReq req) {
        baseService.delById(req.getId());
        return R.ok(baseService.listTask(req.getTodoId()));
    }

    /**
     * 转移事项
     *
     * @since 1.8.0
     */
    @PostMapping("/transfer")
    public R<TaskRes> transfer(@RequestBody @Validated TaskTransferReq req) {
        if (req.getDelSource() == null) {
            req.setDelSource(false);
        }
        baseService.transfer(req);
        return R.ok(baseService.listTask(req.getCurTodoId()));
    }
}
