import { defaultRequest as rq } from './request'
import type { R } from './request'

/**
 * 登录
 * @param params
 * @returns
 */
export const loginApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>('/login', data)
}

export const logoutApi = (): Promise<R<any>> => {
  return rq.post<R<any>>('/logout')
}

export const checkApi = (): Promise<R<any>> => {
  return rq.get<R<any>>('/check', {})
}

export const userListApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/user/list', { params })
}

export const userinfoApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/user/info', { params })
}

export const userinfoAdminApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/user/info/admin', { params })
}

export const userUpdApi = (data?: object): Promise<R<any>> => {
  return rq.post<any>('/user/upd', data)
}

export const userUpdPwdApi = (data?: object): Promise<R<any>> => {
  return rq.post<any>('/user/upd/pwd', data)
}

export const userUpdAdminApi = (data?: object): Promise<R<any>> => {
  return rq.post<any>('/user/upd/admin', data)
}

export const userAddApi = (data?: object): Promise<R<any>> => {
  return rq.post<any>('/user/add', data)
}

export const userDisabledApi = (data?: object): Promise<R<any>> => {
  return rq.post<any>('/user/disabled', data)
}

export const userDelReq = (data?: object): Promise<R<any>> => {
  return rq.post<any>('/user/del', data)
}