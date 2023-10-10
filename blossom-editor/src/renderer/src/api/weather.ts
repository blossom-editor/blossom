import { defaultRequest as rq } from './request'
import type { R } from './request'

export const getAll = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/weather', { params })
}

/**
 * 刷新天气缓存, 重新获取最新信息, 注意不要频繁调用该接口
 * @param params
 * @returns
 */
export const refreshApi = (): Promise<R<any>> => {
  return rq.post<R<any>>('/thirdparty/scheduled/weather')
}
