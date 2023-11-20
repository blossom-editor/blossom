import { defaultRequest as rq } from './request'
import type { R } from './request'

export const planListDayApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/plan/list/day', { params })
}

export const planListDailyApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/plan/list/daily', { params })
}

export const planAddDailyApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>('/plan/add/daily', data)
}

export const planAddDayApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>('/plan/add/day', data)
}

export const planDelApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>('/plan/del', data)
}

export const planUpdDayApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>('/plan/upd/day', data)
}
