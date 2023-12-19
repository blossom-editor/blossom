// @ts-ignore (define in dts)
import { contextBridge, ipcRenderer, clipboard, shell, OpenExternalOptions, NativeImage } from 'electron'
import { electronAPI } from '@electron-toolkit/preload'

// Custom APIs for renderer
const api = {}

/**
 * 主进程调用渲染进程方法
 */
const ipcToRender = {
  /**
   * 截屏结束后, 主进程会调用该方法
   * @param callback 截屏回调
   * @returns
   */
  printScreenAfter: (callback: any) => ipcRenderer.on('printScreenAfter', callback)
}

/**
 * 渲染进程调用主进程方法
 */
const rednerToIpc = {
  /**
   * 窗口操作
   */
  windowMin: () => ipcRenderer.send('window-min'),
  windowMax: () => ipcRenderer.send('window-max'),
  windowHide: () => ipcRenderer.send('window-hide'),
  windowClose: () => ipcRenderer.send('window-close'),
  openDevTools: () => ipcRenderer.send('openDevTools'),
  setBestSize: () => ipcRenderer.send('set-best-size'),
  setZoomLevel: (level: number) => ipcRenderer.send('set-zoom-level', level),
  resetZoomLevel: () => ipcRenderer.send('reset-zoom-level'),
  /**
   * 设置用户信息
   * @param userinfo 用户信息
   * @returns
   */
  setUserinfo: (userinfo: any) => ipcRenderer.send('set-userinfo', userinfo),
  /**
   * 下载文件
   * @param url 文件下载路径
   */
  printScreen: (): void => ipcRenderer.send('printScreen'),
  /**
   * 下载文件
   * @param url 文件下载路径
   */
  download: (url: string): void => ipcRenderer.send('download', url),
  /**
   * 在新窗口查看文章
   * @param article 新窗口查看文章
   */
  openNewArticleWindow: (article: any): void => ipcRenderer.send('open-new-article-window', article),
  /**
   * 新窗口查看图标
   * @returns
   */
  openNewIconWindow: (): void => ipcRenderer.send('open-new-icon-window'),
  /**
   * 新窗口查看文章引用
   * @param article 查看指定文章的引用关系
   * @returns
   */
  openNewArticleReferenceWindow: (article?: any): void => {
    ipcRenderer.send('open-new-article-referece-window', article)
  },
  /**
   * 新窗口查看文章编辑历史记录
   * @param article 文章信息
   * @returns
   */
  openNewArticleLogWindow: (article: any): void => {
    ipcRenderer.send('open-new-article-log-window', article)
  },
  /**
   * 剪贴板, 读取文本
   * @returns 剪切板的文本
   */
  readText: (type?: 'selection' | 'clipboard'): string => clipboard.readText(type),
  /**
   * 剪切板, 读取图片, 并转换成 base64 字符串
   * @returns base64 字符串
   */
  readImageToDataUrl: (type?: 'selection' | 'clipboard'): string => clipboard.readImage(type).toDataURL(),
  /**
   * 剪切板, 读取图片, 并转换成 jpeg 图片 buffer
   * @returns jpeg 图片 buffer
   */
  readImageToJPEG: (type?: 'selection' | 'clipboard'): Buffer => clipboard.readImage(type).toJPEG(1),
  /**
   * 剪切板, 读取图片, 并转换成 jpeg 图片 buffer
   * @returns jpeg 图片 buffer
   */
  readImageToPNG: (type?: 'selection' | 'clipboard'): Buffer => clipboard.readImage(type).toPNG(),
  /**
   * 剪贴板, 写入文本
   * @param text 文本
   * @param type 类型
   * @returns
   */
  writeText: (text: string, type?: 'selection' | 'clipboard') => clipboard.writeText(text, type),
  /**
   * 默认浏览器打开链接
   * @param url 链接路径
   * @param options
   * @returns
   */
  openExtenal: (url: string, options?: OpenExternalOptions) => shell.openExternal(url, options)
}

// Use `contextBridge` APIs to expose Electron APIs to
// renderer only if context isolation is enabled, otherwise
// just add to the DOM global.
if (process.contextIsolated) {
  try {
    contextBridge.exposeInMainWorld('electronAPI', { ...rednerToIpc, ...ipcToRender })
    // contextBridge.exposeInMainWorld('electron', electronAPI)
    // contextBridge.exposeInMainWorld('api', api)
  } catch (error) {
    console.error(error)
  }
} else {
  // @ts-ignore (define in dts)
  // window.electron = electronAPI
  // @ts-ignore (define in dts)
  window.api = api
  // @ts-ignore (define in dts)
  window.electronAPI = electronAPI
}
