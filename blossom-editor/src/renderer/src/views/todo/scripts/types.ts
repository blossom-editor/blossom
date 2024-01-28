/**
 * 事项类型
 * 10: 每日待办事项
 * 20: 阶段性事项
 * 99: 中午12点分割线
 */
export type TodoType = 10 | 20 | 99

/**
 * 事项的状态, 专用于阶段性事项的分组, 所以当 TodoType = 1 时, 只有 1
 * 1: 未完成
 * 2: 完成
 */
export type TodoStatus = 1 | 2

/**
 * 任务状态
 */
export type TaskStatus = 'WAITING' | 'PROCESSING' | 'COMPLETED'

/**
 *
 * 事项 todo 列表
 */
export interface TodoList {
  id?: string
  /**
   * type = 1 时, todoId 为日期
   * type = 2 时, todoId 为雪花ID
   */
  todoId: string
  /**
   * type = 1 时, todoName 为日期
   * type = 2 时, todoName 为标题
   */
  todoName: string
  /**
   * 是否修改名称
   */
  updTodoName: boolean
  /**
   * 事项状态
   */
  todoStatus: TodoStatus
  /**
   * 事项类型
   */
  todoType: TodoType
  /**
   * 是否当天, 用于 type = 1
   */
  today: boolean
  /**
   * 任务数量
   */
  taskCount: number
  /**
   * 任务数量统计
   */
  taskCountStat: string
}

/**
 * 任务 task
 */
export interface TaskInfo {
  id?: string
  todoId: string
  todoName: string
  todoType: TodoType
  taskName: string
  taskContent: string
  taskTags: string[]
  deadLine: string
  creTime: string
  startTime?: string
  endTime?: string
  /**
   * 处理百分比, 0 ~ 100
   */
  process: number
  color: string
  taskStatus: TaskStatus

  //
  updTaskName: boolean
  updTaskContent: boolean
}
