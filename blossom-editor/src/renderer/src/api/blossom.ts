import { defaultRequest as rq } from './request'
import type { R } from './request'
import { AxiosRequestConfig } from 'axios'

//#region ====================================================< sys >=======================================================

/**
 * 获取系统参数列表
 * @returns
 */
export const paramListApi = (): Promise<R<any>> => {
  return rq.get<R<any>>('/sys/param/list', {})
}

/**
 * 修改系统参数
 * @returns
 */
export const paramUpdApi = (data: object): Promise<R<any>> => {
  return rq.post<R<any>>('/sys/param/upd', data)
}

/**
 * 刷新系统参数缓存
 * @param data 文件 form
 * @returns
 */
export const paramRefreshApi = (): Promise<R<any>> => {
  return rq.post<R<any>>('/sys/param/refresh', {})
}

// 用户参数

/**
 * 获取用户参数列表
 * @returns
 */
export const userParamListApi = (): Promise<R<any>> => {
  return rq.get<R<any>>('/user/param/list', {})
}

/**
 * 修改用户参数
 * @returns
 */
export const userParamUpdApi = (data: object): Promise<R<any>> => {
  return rq.post<R<any>>('/user/param/upd', data)
}

/**
 * 管理员修改用户参数
 * @returns
 */
export const userParamUpdAdminApi = (data: object): Promise<R<any>> => {
  return rq.post<R<any>>('/user/param/upd/admin', data)
}

/**
 * 刷新系统参数缓存
 * @param data 文件 form
 * @returns
 */
export const userParamRefreshApi = (): Promise<R<any>> => {
  return rq.post<R<any>>('/user/param/refresh', {})
}

/**
 * 上传文件
 * @param data 文件 form
 * @returns
 */
export const uploadFileApiUrl = '/picture/file/upload'
export const uploadFileApi = (data?: object): Promise<R<any>> => {
  let config: object = { contentType: 'multipart/form-data;' }
  return rq.post<R<any>>(uploadFileApiUrl, data, config)
}

//#endregion

//#region ====================================================< doc >=======================================================

/**
 *
 * @param params {
 *  onlyFolder:  [Folder] 只查询文件夹, 包含文章文件夹和图片文件夹
 *  onlyPicture: [Picture + Article] 只查询图片文件夹, 以及含有图片的文章文件夹
 *  onlyOpen:    [Article] 只查询公开的文章文件夹
 *  onlySubject: [Article] 只查询专题文件夹
 *  onlyStar:    [Article] 问查询有 star 文章的文件夹
 * }
 * @returns
 */
export const docTreeApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/doc/trees', { params })
}

/**
 * 修改文档的排序
 * @param data
 * @returns
 */
export const docUpdSortApi = (data: object): Promise<R<any>> => {
  return rq.post<R<any>>('/doc/upd/sort', data)
}

//#endregion

//#region ====================================================< folder >====================================================

/**
 * 查询文件夹详情
 * @param params
 * @returns
 */
export const folderInfoApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/folder/info', { params })
}

/**
 * 新增文件夹
 * @param data
 * @returns
 */
export const folderAddApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>('/folder/add', data)
}

/**
 * 修改文件夹
 * @param data
 * @returns
 */
export const folderUpdApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>('/folder/upd', data)
}

/**
 * 修改文件夹
 * @param data
 * @returns
 */
export const folderUpdNameApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>('/folder/upd/name', data)
}

/**
 * 快捷增加标签
 * @param data
 * @returns
 */
export const folderUpdTagApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>('/folder/upd/tag', data)
}

/**
 * 删除文件夹
 * @param data
 * @returns
 */
export const folderDelApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>('/folder/del', data)
}

/**
 * 公开文件夹
 * @param data
 * @returns
 */
export const folderOpenApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>('/folder/open', data)
}

/**
 * 查看专题文章
 * @param params
 * @returns
 */
export const subjectsApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/folder/subjects', { params })
}

//#endregion

//#region ====================================================< article >===================================================

/**
 * 文章列表
 * @param params {
 *  starStatus: 0/1
 *  openStatus: 0/1
 * }
 * @returns
 */
export const articleListApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/article/list', { params })
}

/**
 * 查询文章详情, 如果文章为公开文章, 则会返回对应的公开信息, 如 openVersion, openTime 等
 * <p>注意: 返回的正文信息永远是草稿正文, 公开版本的正文信息需要通过公开文章查询 {@link articleOpenApi}
 * @param params {
 *  id: 文章ID,
 *  showToc: 返回 toc 目录,
 *  showMarkdown: 返回 markdown 正文,
 *  showHtml: 返回 html 正文
 * }
 * @returns
 */
export const articleInfoApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/article/info', { params })
}

/**
 * 新增文章正文
 * @param data { pid: 0, name: "", icon: "", tags: "", sort: 0, cover: "", describes: ""}
 * @returns
 */
export const articleAddApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>('/article/add', data)
}

/**
 * 修改文章正文
 * @param data { id: 0, pid: 0, name: "", icon: "", tags: "", sort: 0, cover: "", describes: ""}
 * @returns
 */
export const articleUpdApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>('/article/upd', data)
}

/**
 * 修改文章正文
 * @param data {
 *  id: curDoc.value?.id,
 *  markdown: editor.getMarkdown(),
 *  html: editor.getHTML()
 * }
 * @returns
 */
export const articleUpdContentApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>('/article/upd/content', data)
}

/**
 * 修改文章名称
 * @param data {
 *  id: curDoc.value?.id,
 *  name: curDoc.value.name
 * }
 * @returns
 */
export const articleUpdNameApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>('/article/upd/name', data)
}

/**
 * 快捷增加标签
 * @param data {
 *  id: curDoc.value?.id,
 *  tag: string
 * }
 * @returns
 */
export const articleUpdTagApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>('/article/upd/tag', data)
}

/**
 * 删除文章
 * @param data {id:文章ID}
 * @returns
 */
export const articleDelApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>('/article/del', data)
}

/**
 * star 或取消 star
 * @param data
 * @returns
 */
export const articleStarApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>('/article/star', data)
}

/**
 * star 或取消 star
 * @param data
 * @returns
 */
export const folderStarApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>('/folder/star', data)
}

/**
 * 下载文章 markdown
 * @param params
 * @returns
 */
export const articleDownloadApi = (params?: object): Promise<any> => {
  let config = { params: params, responseType: 'blob' }
  return rq.get('/article/download', config)
}

/**
 * 下载文章 html
 * @param params
 * @returns
 */
export const articleDownloadHtmlApi = (params?: object): Promise<any> => {
  let config = { params: params, responseType: 'blob' }
  return rq.get('/article/download/html', config)
}

/**
 * 文章字数折线图
 * @param params
 * @returns
 */
export const articleWordLineApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/article/stat/line', { params })
}

/**
 * 文章数和文章字数统计
 * @param params
 * @returns
 */
export const articleWordsApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/article/stat/words', { params })
}

/**
 * 指定用户的文章数和文章字数统计
 * @param params
 * @returns
 */
export const articleWordsUserApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/article/stat/words/user', { params })
}

/**
 * 文章数和文章字数统计
 * @param params
 * @returns
 */
export const articleWordsListApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/article/stat/words/list', { params })
}

/**
 * 保存字数统计信息
 */
export const articleWordsSaveApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>('/article/stat/words/save', data)
}

/**
 * 文章编辑热力图
 * @param params
 * @returns
 */
export const articleHeatmapApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/article/stat/heatmap', { params })
}

/**
 * 文章引用关系
 * @param params
 * @returns
 */
export const articleRefListApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/article/ref/list', { params })
}

/**
 * 文章公开或取消公开
 * @param data
 * @returns
 */
export const articleOpenApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>('/article/open', data)
}

/**
 * 文章公开或取消公开
 * @param data
 * @returns
 */
export const articleOpenBatchApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>('/article/open/batch', data)
}

/**
 * 文章同步
 * @param data
 * @returns
 */
export const articleSyncApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>('/article/open/sync', data)
}

/**
 * 生成文章的二维码
 * @param params
 * @returns
 */
export const articleQrCodeApi = (params?: object): Promise<any> => {
  let config = { params: params, responseType: 'blob' }
  return rq.get('/article/open/qrcode', config)
}

/**
 * 文章导入
 */
export const articleImportApiUrl = '/article/import'

/**
 * 文章历史记录
 * @param params { articleId: articleId }
 * @returns
 */
export const articleLogsApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/article/log', { params })
}

/**
 * 历史记录的 markdown 的正文信息
 * @param params
 * @returns
 */
export const articleLogContentApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/article/log/content', { params })
}

/**
 * 文章全量备份
 * @param params
 * @returns
 */
export const articleBackupApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/article/backup', { params })
}

/**
 * 备份列表
 * @param params
 * @returns
 */
export interface BackupFile {
  date: string
  time: string
  filename: string
  fileLength: number
  desc?: string
}
export const articleBackupListApi = (): Promise<R<BackupFile[]>> => {
  return rq.get<BackupFile[]>('/article/backup/list')
}

/**
 * 获取下载文件信息
 * @param data
 * @returns
 */
export const articleBackupDownloadFragmentHeadApi = (params: object): Promise<any> => {
  let config = { params: params, responseType: 'blob' }
  return rq.head('/article/backup/download/fragment', config)
}
/**
 * 分片下载备份文件
 * @param params
 * @returns
 */
export const articleBackupDownloadFragmentApi = (data: object, range: string): Promise<any> => {
  let config: AxiosRequestConfig = { responseType: 'blob', headers: { Range: range } }
  return rq.post('/article/backup/download/fragment', data, config)
}

/**
 * 获取临时访问链接的 key
 * @param params {id:id}
 * @returns
 */
export const articleTempKey = (params?: object): Promise<any> => {
  return rq.get('/article/temp/key', { params })
}

/**
 * 文章临时访问链接
 */
export const articleTempH = '/article/temp/h?k='

/**
 * 文章回收站列表
 * @returns
 */
export const articleRecycleListApi = (): Promise<any> => {
  return rq.get('/article/recycle/list')
}

/**
 * 文章回收站列表
 * @returns
 */
export const articleRecycleRestoreApi = (data?: object): Promise<any> => {
  return rq.post('/article/recycle/restore', data)
}

export const articleRecycleDownloadApi = (params?: object): Promise<any> => {
  let config = { params: params, responseType: 'blob' }
  return rq.get('/article/recycle/download', config)
}

/**
 * 文章全文搜索
 */
export const articleSearchApi = (params?: object): Promise<any> => {
  return rq.get('/search', { params })
}
//#endregion

//#region ====================================================< picture >===================================================

/**
 * 图片分页
 * @param params
 * @returns
 */
export const picturePageApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/picture/page', { params })
}

/**
 * 图片详情
 * @param params
 * @returns
 */
export const pictureInfoApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/picture/info', { params })
}

/**
 * 星标图片
 * @param params {id:id}
 * @returns
 */
export const pictureStarApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>('/picture/star', data)
}

/**
 * 删除图片
 * @param params {id:id}
 * @returns
 */
export const pictureDelApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>('/picture/del', data)
}

/**
 * 批量删除图片
 * @param params {ids:id}
 * @returns
 */
export type PictureDelBatchRes = { success: number; fault: number; inuse: number; successIds: Array<string> }
export const pictureDelBatchApi = (data?: object): Promise<R<PictureDelBatchRes>> => {
  return rq.post<PictureDelBatchRes>('/picture/del/batch', data)
}

/**
 * 删除图片
 * @param params {id:id}
 * @returns
 */
export const pictureStatApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/picture/stat', { params })
}

/**
 * 删除图片
 * @param params {id:id}
 * @returns
 */
export const pictureStatUserApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>('/picture/stat/user', { params })
}

/**
 * 转移文件
 * @param data
 * @returns
 */
export const pictureTransferApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>('/picture/transfer', data)
}
//#endregion
