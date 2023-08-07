import axios from "axios"
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse } from "axios"
import SYSTEM from '@/assets/constants/blossom'
import { ElNotification } from "element-plus"
import { isNotNull } from "@/assets/utils/obj"

export class Request {
  /** axios 实例 */
  instance: AxiosInstance
  /** 基础配置，url和超时时间 */
  baseConfig: AxiosRequestConfig = {
    baseURL: SYSTEM.DOMAIN.PRD,
    timeout: 60000
  };
  /**
   * 构造方法中会设置拦截器逻辑，配置文件为可选项
   * @param config 配置文件内容，为可选项
   */
  constructor(config?: AxiosRequestConfig) {
    this.instance = axios.create(Object.assign(this.baseConfig, config));
    /**
     * 请求拦截
     */
    this.instance.interceptors.request.use(
      //@ts-ignore
      (config: AxiosRequestConfig) => {
        return {
          ...config,
          headers: {
            "Blossom-User-Id": SYSTEM.DOMAIN.USER_ID
          }
        }
      },
      (err: any) => Promise.reject(err)
    );
    /**
     * 响应拦截
     */
    this.instance.interceptors.response.use(
      (res: AxiosResponse) => {
        const status = res.status;
        if (status !== 200) {
          Promise.reject(res);
        }
        // 本次响应是否正确
        let isSuccess = false;
        // 返回文件流的, 可能没有JSON返回体, 但如果接口报错还是有返回体的
        if (res.config.responseType === 'blob' || res.headers['content-type'] === 'application/force-download') {
          if (isNotNull(res.data.code)) {
            if (!isSuccessRCode(res.data.code)) {
              isSuccess = false;
            }
          } else {
            isSuccess = true;
          }
        }
        // 响应码为正确的直接返回
        if (isSuccessRCode(res.data.code)) {
          isSuccess = true;
        }

        if (isSuccess) {
          return res.data;
        }
        /*
         * 授权被拦截, 则需要退回登录页请求 
         */
        else if (res.data.code === 'AUTH-40101') {
          ElNotification({ message: res.data.msg, type: "error", position: 'bottom-right', title: '请求错误' });
          return Promise.reject(res);
        }
        /*
         * 其他接口报错, 直接拒绝并提示错误信息 
         */
        else {
          let errorResponse = res.data;
          errorResponse['url'] = res.config.url;
          // 其他情况拒绝
          ElNotification({ message: res.data.msg, type: "error", position: 'bottom-right', title: '请求错误' });
          return Promise.reject(res);
        }
        // 直接返回res，当然你也可以只返回res.data
        return res;
      },
      (err: any) => {
        let errorMsg = err.message;
        let code = err.code;
        if (code === "ERR_NETWORK") {
          errorMsg = "网络错误,请检查您的网络是否通畅";
        }
        // 字符串和变量拼接：请求失败(${err.response.status})
        ElNotification({ message: errorMsg, type: "error", position: 'bottom-right', title: '请求错误' });
        return Promise.reject(err);
      }
    );
  }

  // 定义请求方法
  public request(config: AxiosRequestConfig): Promise<any> {
    return this.instance.request(config);
  }

  public get<T>(url: string, params?: object): Promise<R<T>> {
    return this.instance.get(url, params);
  }

  public post<T>(url: string, data?: object, config?: object): Promise<R<T>> {
    return this.instance.post(url, data, config);
  }

  public delete<T>(url: string, params?: object): Promise<R<T>> {
    return this.instance.delete(url, params);
  }
}

export interface R<T = any> {
  ok: boolean;
  code: number;
  msg: string;
  traceId: string,
  data?: T;
};

/**
 * 判断接口响应码是否正确
 * @param code 接口响应码
 * @returns 
 */
const isSuccessRCode = (code: string | number): boolean => {
  return code === 0 || code === '0' || code === 20000 || code === '20000';
}

export const defaultRequest = new Request();