import pinia from '@renderer/stores/store-config'
import { useUserStore } from '@renderer/stores/user'
import { useConfigStore } from '@renderer/stores/config'
import type { UploadProps, UploadRawFile } from 'element-plus'
import { isBlank, isNotBlank } from '@renderer/assets/utils/obj'
import Notify from '@renderer/scripts/notify'
import { uploadFileApi } from '@renderer/api/blossom'
import { getFilePrefix, getFileSuffix, getNowTime, randomInt } from '@renderer/assets/utils/util'

const { picStyle } = useConfigStore(pinia)
const userStore = useUserStore(pinia)

/**
 * Picture Object
 */
export interface Picture {
  creTime: string
  id: string
  name: string
  pathName: string
  pid: string
  size: number
  sourceName: string
  starStatus: number
  url: string
  articleNames: string
  delTime: number
  checked: boolean
}

/**
 * 获取一个默认的图片实现
 * @returns
 */
export const buildDefaultPicture = (): Picture => {
  return {
    id: '0',
    pid: '0',
    sourceName: '',
    starStatus: 0,
    name: '',
    size: 0,
    pathName: '',
    creTime: '',
    url: '',
    articleNames: '',
    delTime: 0,
    checked: false
  }
}

/**
 * 图片上传的回调事件
 */
export type UploadCallback = (url: string) => void

/**
 * 判断是否为图片名称增加时间后缀
 * @param name 图片名称
 * @returns
 */
export const wrapperFilename = (name: string): string => {
  if (picStyle.isAddSuffix) {
    let prefix = getFilePrefix(name)
    let suffix = getFileSuffix(name)
    return prefix + `_${getNowTime()}_${randomInt(1, 999)}.${suffix}`
  }
  return name
}

/**
 * form 表单上传图片
 * @param file 文件
 * @param pid 文件所属文件夹
 * @param callback 上传回调
 * @returns 返回文件路径
 */
export const uploadForm = (file: File, pid: string, callback: UploadCallback) => {
  if (file.size / 1024 / 1024 > picStyle.maxSize) {
    Notify.error(`文件大小不能超过 ${picStyle.maxSize}MB!`, '上传失败')
  } else {
    const form = new FormData()
    form.append('file', file)
    form.append('filename', wrapperFilename(file.name))
    form.append('pid', pid)
    uploadFileApi(form).then((resp) => {
      callback(resp.data)
    })
  }
}

/**
 * uoload 组件的 data 数据获取
 * @param rawFile
 * @param pid
 * @returns
 */
export const uploadDate = (rawFile: UploadRawFile, pid: string, repeatUpload: boolean = false) => {
  return {
    pid: pid,
    filename: wrapperFilename(rawFile.name),
    repeatUpload: repeatUpload
  }
}

/**
 *
 * @param rawFile
 * @returns
 */
export const beforeUpload: UploadProps['beforeUpload'] = (rawFile) => {
  if (rawFile.size / 1024 / 1024 > picStyle.maxSize) {
    Notify.error(`文件大小不能超过 ${picStyle.maxSize}MB!`, '上传失败')
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
 * 上传文件结果处理, 失败是根据
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
    if (error.message.indexOf('fail to post') > -1 && error.message.indexOf('/picture/file/upload 0') > -1) {
      Notify.error('可能是由于您上传的文件过大, 请检查服务端上传大小限制。', '上传失败')
    } else {
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

/**
 * 图片路径包装
 * 如果是 blossom 存储的图片, 会增加 picCache 参数, 用来清理缓存
 */
export const picCacheWrapper = (url: string) => {
  if (url.includes(userStore.sysParams.BLOSSOM_OBJECT_STORAGE_DOMAIN)) {
    return url + '?picCache=' + picCache
  } else {
    return url
  }
}

//#endregion
