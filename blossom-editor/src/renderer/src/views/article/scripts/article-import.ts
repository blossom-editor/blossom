import type { UploadProps, UploadUserFile, UploadFile, UploadFiles } from 'element-plus'
import Notify from '@renderer/scripts/notify'

/**
 * 添加文件的检查, 当前支持如下格式:
 * .txt
 * .md
 *
 * @param _file
 * @param files
 */
export const onChange: UploadProps['onChange'] = (_file: UploadUserFile, files: UploadUserFile[]) => {
  for (let i = 0; i < files.length; i++) {
    let file: UploadUserFile = files[i]
    let suffix = file.name.substring(file.name.lastIndexOf('.'))
    if (suffix !== '.txt' && suffix !== '.md') {
      files.splice(i, 1)
    }
  }
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
 * 文件上传成功时的钩子, 成功代表 http 成功, 不代表后台处理成功, 后台处理成功的返回 20000 之外的状态的, 并在这里重置为其他状态
 * 'ready' | 'uploading' | 'success' | 'fail'
 *
 * @param resp
 * @param _file
 */
export const onUploadSeccess: UploadProps['onSuccess'] = (resp, _file: UploadFile, _files: UploadFiles) => {
  const result = handleUploadSeccess(resp)
  console.log(_file)
  if (result) {
    _file.status = 'success'
  } else {
    _file.status = 'fail'
  }
}

/**
 * 上传文件结果处理
 * @param resp 接口响应
 * @returns 是否成功
 */
export const handleUploadSeccess = (resp: any): boolean => {
  if (resp.code === '20000') {
    // Notify.success('上传成功')
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
 * 上传失败时的处理, 失败是根据 http 响应码决定, 通常是网络失败等原因
 * @param error
 */
export const handleUploadError = (error: Error) => {
  console.log('🚀 ~ handleUploadError ~ error:', error)
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
