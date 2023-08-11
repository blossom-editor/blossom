// @ts-nocheck
/** 打开控制台, 会在打开和关闭之间切换 */
export const openDevTools = () => { window.electronAPI.openDevTools() }

export const windowMin = () => { window.electronAPI.windowMin() }
/** 会在最大化和还原之间切换 */
export const windowMax = () => { window.electronAPI.windowMax() }
export const windowHide = () => { window.electronAPI.windowHide() }
export const windowClose = () => { window.electronAPI.windowClose() }

/**
 * 设置用户信息
 * @param userinfo 用户信息
 */
export const setUserinfo = (userinfo: any) => { window.electronAPI.setUserinfo(userinfo) }

/**
 * 打开文章窗口
 * @param articleName
 * @param articleId
 */
export const openNewArticleWindow = (articleName: string, articleId: number) => { window.electronAPI.openNewArticleWindow({ name: articleName, id: articleId }) }

/**
 * 打开新图标窗口
 */
export const openNewIconWindow = () => { window.electronAPI.openNewIconWindow() }

/**
 * 打开文章引用窗口
 */
export const openNewArticleReferenceWindow = () => { window.electronAPI.openNewArticleReferenceWindow() }

/**
 * 使用默认浏览器打开链接
 * @param url 访问链接
 */
export const openExtenal = (url: string): void => { window.electronAPI.openExtenal(url) }

/**
 * 截屏
 */
export const printScreen = (): void => { window.electronAPI.printScreen() }

/**
 * 剪切板, 读取图片, 并转换成 base64 字符串
 * @returns base64 字符串
 */
export const readImageToDataUrl = (): string => { return window.electronAPI.readImageToDataUrl('clipboard') }

/**
 * 剪切板, 读取图片, 并转换成 png 图片 buffer
 * @returns png 图片 buffer
 */
export const readImageToPNG = (): string => { return window.electronAPI.readImageToPNG('clipboard') }

/**
 * 剪切板, 读取图片, 并转换成 jpeg 图片 buffer
 * @returns jpeg 图片 buffer
 */
export const readImageToJPEG = (): string => { return window.electronAPI.readImageToJPEG('clipboard') }

/**
 * 向剪切板写入文本
 * @param text 文本内容
 */
export const writeText = (text: string): void => { window.electronAPI.writeText(text, 'clipboard') }

/**
 * 从剪切板读取文本
 * @returns text 文本内容
 */
export const readText = (): string => { return window.electronAPI.readText() }

/**
 * 下载
 * @param url 文件路径
 */
export const download = (url: string): void => { window.electronAPI.download(url) }