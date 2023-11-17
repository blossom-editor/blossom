import { defaultRequest as rq } from './request'
import type { R } from './request'

/**
 * 任务统计
 */
export const taskStatApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/todo/task/stat', { params })
}

/**
 * 事项统计
 */
export const todoStatApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/todo/stat', { params })
}

//#region ---------------------------------------- todo ----------------------------------------

/**
 * 事项列表
 */
export const todosApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/todo/list', { params })
}

/**
 * 事项列表
 */
export const countTaskApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/todo/task/count', { params })
}

/**
 * 新增阶段性事项
 */
export const addPhasedApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>('/todo/add/phased', data)
}

/**
 * 修改阶段性事项的名称
 */
export const updTodoNameApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>('/todo/upd/name', data)
}

/**
 * 完成阶段性事项
 */
export const openTodoApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>('/todo/open', data)
}

/**
 * 完成阶段性事项
 */
export const completedTodoApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>('/todo/completed', data)
}

/**
 * 完成阶段性事项
 */
export const exportTodoApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/todo/export', { params })
}

//#endregion

//#region ---------------------------------------- task ----------------------------------------
/**
 * 某天或某个事项的任务列表
 */
export const tasksApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/todo/task/list', { params })
}

/**
 * 某天或某个事项的任务列表
 */
export const taskInfoApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/todo/task/info', { params })
}

/**
 * 新增任务
 */
export const addTaskApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>('/todo/task/add', data)
}

/**
 * 修改任务的主要信息
 */
export const updTaskApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>('/todo/task/upd', data)
}

/**
 * 移动到 waiting
 */
export const toWaitingApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>('/todo/task/waiting', data)
}

/**
 * 移动到 processing
 */
export const toProcessingApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>('/todo/task/processing', data)
}

/**
 * 移动到 completed
 */
export const toCompletedApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>('/todo/task/completed', data)
}

/**
 * 删除节点
 */
export const delTaskApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>('/todo/task/del', data)
}

/**
 * 标签集合
 */
export const taskTagsApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/todo/task/tags', { params })
}

export const taskTransferApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>('/todo/task/transfer', data)
}
//#endregion
