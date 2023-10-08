package com.blossom.backend.server.todo;

import com.blossom.backend.server.todo.pojo.*;
import com.blossom.common.base.pojo.R;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 待办事项 [Todo]
 *
 * @order 40
 * @since 1.4.0
 */
@RestController
@AllArgsConstructor
@RequestMapping("/todo")
public class TodoController {

    private final TodoService baseService;

    /**
     * 待办事项列表
     *
     * @return 待办事项列表
     */
    @GetMapping("/list")
    public R<TodoGroupRes> list() {
        return R.ok(baseService.listTodo());
    }

    /**
     * 新增阶段性事项
     */
    @PostMapping("/add/phased")
    public R<?> addPhased(@RequestBody @Validated TodoPhasedAddReq req) {
        baseService.addPhased(req);
        return R.ok();
    }

    /**
     * 修改阶段性事项名称
     */
    @PostMapping("/upd/name")
    public R<?> updTodoName(@RequestBody @Validated TodoPhasedUpdReq req) {
        baseService.updTodoName(req);
        return R.ok();
    }

    /**
     * 开启阶段性事项
     */
    @PostMapping("/open")
    public R<?> open(@RequestBody @Validated TodoPhasedCompletedReq req) {
        baseService.openPhased(req.getTodoId());
        return R.ok();
    }

    /**
     * 完成阶段性事项
     */
    @PostMapping("/completed")
    public R<?> completed(@RequestBody @Validated TodoPhasedCompletedReq req) {
        baseService.completedPhased(req.getTodoId());
        return R.ok();
    }

    /**
     * 待办事项列表
     *
     * @param todoId todoId 待办事项ID
     * @return 待办事项列表
     */
    @GetMapping("/stat")
    public R<TodoStatisticRes> stat(@RequestParam("todoId") String todoId) {
        return R.ok(baseService.statistic(todoId));
    }

    /**
     * 任务导出
     */
    @GetMapping("/export")
    public R<String> export(@ModelAttribute TodoExportReq req) {
        if (req.getExportDate() == null) {
            req.setExportDate(false);
        }
        if (req.getExportProcess() == null) {
            req.setExportProcess(false);
        }
        return R.ok(baseService.export(req));
    }
}
