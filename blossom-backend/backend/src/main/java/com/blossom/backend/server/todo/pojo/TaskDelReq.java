package com.blossom.backend.server.todo.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 任务删除
 *
 * @since 1.4.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskDelReq extends TaskUpdStatusReq {
}
