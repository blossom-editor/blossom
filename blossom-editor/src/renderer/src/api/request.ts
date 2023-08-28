// index.ts
import axios from "axios"
import pinia from "@renderer/stores/store-config"
import { toLogin } from "@renderer/router"
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse } from "axios"
import { Local } from "@renderer/assets/utils/storage"
import { isNotNull } from "@renderer/assets/utils/obj"
import { storeKey as authKey, useUserStore } from '@renderer/stores/user'
import { storeKey as serverUrlKey } from '@renderer/stores/server'
import Notify from '@renderer/scripts/notify'

const userStore = useUserStore(pinia)

export class Request {
  /** axios 实例 */
  instance: AxiosInstance
  /** 基础配置，url和超时时间 */
  baseConfig: AxiosRequestConfig = {
    baseURL: Local.get(serverUrlKey),
    timeout: 60000
  }
  /**
   * 构造方法中会设置拦截器逻辑，配置文件为可选项
   * @param config 配置文件内容，为可选项
   */
  constructor(config?: AxiosRequestConfig) {
    this.instance = axios.create(Object.assign(this.baseConfig, config))
    /**
     * 请求拦截
     */
    this.instance.interceptors.request.use(
      (config: AxiosRequestConfig): any => {
        let token: string = ''
        let tokenCache = Local.get(authKey)
        if (isNotNull(tokenCache) && isNotNull(tokenCache.token)) {
          token = tokenCache.token
        }
        config.url = Local.get(serverUrlKey) + config.url
        return {
          ...config,
          headers: {
            "Authorization": 'Bearer ' + token
          }
        }
      },
      (err: any) => {
        return Promise.reject(err)
      }
    )
    /**
     * 响应拦截
     */
    this.instance.interceptors.response.use(
      (res: AxiosResponse) => {
        const status = res.status
        if (status !== 200) {
          Promise.reject(res)
        }
        let data = res.data
        // 本次响应是否正确
        let isSuccess = false

        // 返回文件流
        if (res.config.responseType === 'blob' || res.headers['content-type'] === 'application/force-download') {
          // 返回文件流但仍然有JSON返回体(例如接口报错时), 仍要判断返回码
          if (isNotNull(data.code) && !isSuccessRCode(data.code)) {
            isSuccess = false
          } else {
            isSuccess = true
            return res
          }
        }
        // 响应码为正确的直接返回
        if (isSuccessRCode(data.code)) {
          isSuccess = true
        }

        if (isSuccess) {
          return data
        } else if (data.code === 'AUTH-40101') {
          /* 授权被拦截, 则需要退回登录页请求 */
          console.log('授权失败, 重置登录状态');
          userStore.reset()
          toLogin()
          return Promise.reject(res)
        } else {
          /* 其他接口报错, 直接拒绝并提示错误信息 */
          let errorResponse = data
          errorResponse['url'] = res.config.url
          Notify.error(data.msg, '请求失败')
          return Promise.reject(res)
        }
      },
      (err: any) => {
        let errorMsg = err.message
        let code = err.code
        if (code === "ERR_NETWORK") {
          errorMsg = "网络错误,请检查您的网络是否通畅"
        }
        Notify.error(errorMsg, '请求失败')
        return Promise.reject(err)
      }
    );
  }

  // 定义请求方法
  public request(config: AxiosRequestConfig): Promise<any> {
    return this.instance.request(config)
  }

  public get<T>(url: string, params?: object): Promise<R<T>> {
    return this.instance.get(url, params)
  }

  public post<T>(url: string, data?: object, config?: object): Promise<R<T>> {
    return this.instance.post(url, data, config)
  }

  public delete<T>(url: string, params?: object): Promise<R<T>> {
    return this.instance.delete(url, params)
  }
}

export interface R<T = any> {
  ok: boolean
  code: number
  msg: string
  traceId: string
  data?: T
}

/**
 * 判断接口响应码是否正确
 * @param code 接口响应码
 * @returns 
 */
const isSuccessRCode = (code: string | number): boolean => {
  return code === 0 || code === '0' || code === 20000 || code === '20000'
}

export const defaultRequest = new Request()