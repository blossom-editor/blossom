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

export const userinfoApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/user/info', { params })
}

export const userUpdApi = (data?: object): Promise<R<any>> => {
  return rq.post<any>('/user/upd', data)
}

export const userUpdPwdApi = (data?: object): Promise<R<any>> => {
  return rq.post<any>('/user/upd/pwd', data)
}

export const userAddApi = (data?: object): Promise<R<any>> => {
  return rq.post<any>('/user/add', data)
}
