import type { UploadProps } from 'element-plus'
import Notify from '@renderer/components/Notify'

/**
 * Picture Object
 */
export interface Picture {
  creTime: string,
  id: string | number,
  name: string,
  pathName: string,
  pid: string | number,
  size: number,
  sourceName: string,
  starStatus: number,
  url: string,
  articleNames: string,
  delTime: number
}

/**
 * 
 * @param rawFile 
 * @returns 
 */
export const beforeUpload: UploadProps['beforeUpload'] = (rawFile) => {
  if (rawFile.size / 1024 / 1024 > 10) {
    Notify.error('文件大小不能超过 10MB!', '上传失败')
    return false
  }
  return true
}

/**
 * 
 * @param resp 
 * @param _file 
 */
export const onUploadSeccess: UploadProps['onSuccess'] = (resp, _file?) => {
  handleUploadSeccess(resp)
}

/**
 * 上传文件结果处理
 * @param resp 接口响应
 * @returns 是否成功
 */
export const handleUploadSeccess = (resp: any): boolean => {
  if (resp.code === '20000') {
    Notify.success('上传成功')
    return true
  } else {
    Notify.error(resp.msg, '上传失败')
    return false
  }
}

/**
 * 
 * @param error 
 * @param _file 
 * @param _files 
 */
export const onError: UploadProps['onError'] = (error, _file, _files) => {
  handleUploadError(error)
}

/**
 * 
 * @param error 
 */
export const handleUploadError = (error: Error) => {
  if (error.message != undefined) {
    try {
      let resp = JSON.parse(error.message);
      if (resp != undefined) {
        Notify.error(resp.msg, '上传失败')
      }
    } catch (e) {
      Notify.error(error.message, '上传失败')
    }
  }
}