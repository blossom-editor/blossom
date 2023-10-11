import type { UploadProps } from 'element-plus'
import Notify from '@renderer/scripts/notify'
import { isBlank, isNotBlank } from '@renderer/assets/utils/obj'

/**
 * Picture Object
 */
export interface Picture {
  creTime: string
  id: string | number
  name: string
  pathName: string
  pid: string | number
  size: number
  sourceName: string
  starStatus: number
  url: string
  articleNames: string
  delTime: number
}

/**
 * 获取一个默认的图片实现
 * @returns
 */
export const buildDefaultPicture = (): Picture => {
  return {
    id: 0,
    pid: 0,
    sourceName: '',
    starStatus: 0,
    name: '',
    size: 0,
    pathName: '',
    creTime: '',
    url: '',
    articleNames: '',
    delTime: 0
  }
}

/**
 *
 * @param rawFile
 * @returns
 */
export const beforeUpload: UploadProps['beforeUpload'] = (rawFile) => {
  if (rawFile.size / 1024 / 1024 > 50) {
    Notify.error('文件大小不能超过 50MB!', '上传失败')
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
      let resp = JSON.parse(error.message)
      if (resp != undefined) {
        Notify.error(resp.msg, '上传失败')
      }
    } catch (e) {
      Notify.error(error.message, '上传失败')
    }
  }
}
/**
 * 图片引用文章转为数组拼接转
 *
 * @param names 文章名称拼接的字符串
 * @returns
 */
export const articleNamesToArray = (names: string): string[] => {
  if (isBlank(names)) {
    return []
  }
  let result = names.split(',').filter((name) => isNotBlank(name))
  return result
}

//#region 图片缓存控制

// 图片缓存
let picCache = new Date().getTime()

// 刷新图片缓存
export const picCacheRefresh = () => {
  picCache = new Date().getTime()
}

// 图片路径包装
export const picCacheWrapper = (url: string) => {
  return url + '?picCache=' + picCache
}

//#endregion
