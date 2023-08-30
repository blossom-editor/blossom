import { defaultRequest as rq } from "./request";
import type { R } from "./request";

//#region ====================================================< sys >=======================================================

/**
 * 上传文件
 * @param data 文件 form
 * @returns 
 */
export const osconfigApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>("/sys/osconfig", { params });
}

/**
 * 上传文件
 * @param data 文件 form
 * @returns 
 */
export const uploadFileApiUrl = '/picture/file/upload'
export const uploadFileApi = (data?: object): Promise<R<any>> => {
  let config: object = { contentType: 'multipart/form-data;', }
  return rq.post<R<any>>(uploadFileApiUrl, data, config);
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
  return rq.get<R<any>>("/doc/trees", { params });
}

//#endregion

//#region ====================================================< folder >====================================================

/**
 * 查询文件夹详情
 * @param params 
 * @returns 
 */
export const folderInfoApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>("/folder/info", { params });
}

export const folderAddApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>("/folder/add", data);
}

export const folderUpdApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>("/folder/upd", data);
}

export const folderDelApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>("/folder/del", data);
}

export const folderOpenApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>("/folder/open", data);
}

/**
 * 查看专题文章
 * @param params 
 * @returns 
 */
export const subjectsApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>("/folder/subjects", { params });
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
  return rq.get<R<any>>("/article/list", { params });
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
  return rq.get<R<any>>("/article/info", { params });
}

/**
 * 新增文章正文
 * @param data { pid: 0, name: "", icon: "", tags: "", sort: 0, cover: "", describes: ""}
 * @returns 
 */
export const articleAddApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>("/article/add", data);
}

/**
 * 修改文章正文
 * @param data { id: 0, pid: 0, name: "", icon: "", tags: "", sort: 0, cover: "", describes: ""}
 * @returns 
 */
export const articleUpdApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>("/article/upd", data);
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
  return rq.post<R<any>>("/article/upd/content", data);
}

/**
 * 删除文章
 * @param data {id:文章ID}
 * @returns 
 */
export const articleDelApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>("/article/del", data);
}

/**
 * star 或取消 star
 * @param data 
 * @returns 
 */
export const articleStarApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>("/article/star", data);
}

/**
 * 下载文章 markdown
 * @param params 
 * @returns 
 */
export const articleDownloadApi = (params?: object): Promise<any> => {
  let config = { params: params, responseType: 'blob' }
  return rq.get("/article/download", config);
}

export const articleWordsApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>("/article/stat/words", { params });
}

export const articleWordLineApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>("/article/stat/line", { params });
}

export const articleHeatmapApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>("/article/stat/heatmap", { params });
}

export const articleRefListApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>("/article/ref/list", { params });
}

export const articleOpenApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>("/article/open", data);
}

export const articleSyncApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>("/article/open/sync", data);
}

export const articleQrCodeApi = (params?: object): Promise<any> => {
  let config = { params: params, responseType: 'blob' }
  return rq.get("/article/open/qrcode", config);
}

export const articleImportApiUrl = '/article/import'


//#endregion

//#region ====================================================< picture >===================================================

/**
 * 图片分页
 * @param params 
 * @returns 
 */
export const picturePageApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>("/picture/page", { params });
}

/**
 * 星标图片
 * @param params {id:id}
 * @returns 
 */
export const pictureStarApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>("/picture/star", data);
}

/**
 * 删除图片
 * @param params {id:id}
 * @returns 
 */
export const pictureDelApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>("/picture/del", data);
}

/**
 * 删除图片
 * @param params {id:id}
 * @returns 
 */
export const pictureStatApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>("/picture/stat", { params });
}
//#endregion
