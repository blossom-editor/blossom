import { defaultRequest as rq } from './request'
import type { R } from './request'

/**
 * 用户信息
 * @param params
 * @returns
 */
export const userinfoOpenApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/user/info/open', { params })
}

/**
 * 文档树状列表
 * @param params
 * @returns
 */
export const docTreeOpenApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/doc/trees/open', { params })
}
export const docTreeApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/doc/trees', { params })
}

/**
 * 专题列表
 * @param params
 * @returns
 */
export const subjectsApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/folder/subjects/open', { params })
}

// --------------------------------------------------< 文章 >--------------------------------------------------

/**
 * 文章热力图
 * @param params
 * @returns
 */
export const articleHeatmapApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/article/stat/heatmap/open', { params })
}

/**
 * 字数折线图
 * @param params
 * @returns
 */
export const articleWordLineApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/article/stat/line/open', { params })
}

/**
 * 文章详情
 * @param params
 * @returns
 */
export const articleInfoOpenApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/article/open/info', { params })
}

export const articleInfoApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/article/info', { params })
}
