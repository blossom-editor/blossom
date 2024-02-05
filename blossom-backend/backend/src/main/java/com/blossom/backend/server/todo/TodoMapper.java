package com.blossom.backend.server.todo;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blossom.backend.server.todo.pojo.TodoEntity;
import com.blossom.backend.server.todo.pojo.TodoPhasedUpdReq;
import com.blossom.backend.server.todo.pojo.TodoStatisticRes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 待办事项
 *
 * @since 1.4.0
 */
@Mapper
public interface TodoMapper extends BaseMapper<TodoEntity> {

    /**
     * 查询列表
     */
    List<TodoEntity> listAll(TodoEntity todo);

    /**
     * 查询某事项的全部任务
     *
     * @param userId 用户ID
     */
    List<TodoEntity> listTodo(@Param("userId") Long userId);

    /**
     * 统计事项下的任务
     *
     * @param todoId 事项ID
     */
    Integer count(@Param("todoId") String todoId);

    /**
     * 查询待办信息
     */
    TodoEntity selectByTodoId(@Param("todoId") String todoId);

    /**
     * 修改事项名称
     */
    void updTodoName(TodoPhasedUpdReq req);

    /**
     * 修改阶段性事项的状态
     *
     * @param todoId     事项ID
     * @param todoStatus 事项状态
     */
    void updTodoStatus(@Param("todoId") String todoId, @Param("todoStatus") Integer todoStatus);

    /**
     * 查询全部任务
     *
     * @param todoId todoid
     */
    List<TodoEntity> listTask(@Param("userId") Long userId, @Param("todoId") String todoId);

    /**
     * 事项统计信息
     *
     * @param todoId 事项ID
     */
    TodoStatisticRes statisticTodo(@Param("todoId") String todoId);

    /**
     * 全局任务统计信息, 只返回 taskStatus/ creTime 字段
     *
     * @param beginCreTime 开始时间
     * @param endCreTime   结束时间
     * @param userId       用户ID
     */
    List<TodoEntity> statisticTask(@Param("beginCreTime") String beginCreTime,
                                   @Param("endCreTime") String endCreTime,
                                   @Param("userId") Long userId);

    /**
     * 统计所有待办事项数量
     *
     * @param userId 用户ID
     */
    List<TodoEntity> statisticTaskCount(@Param("userId") Long userId);

    /**
     * 根据ID集合删除
     */
    void delByIds(@Param("ids") List<Long> ids, @Param("userId") Long userId);

    /**
     * 删除待办
     */
    void delByUserId(@Param("userId") Long userId);
}