import { Cancel } from 'axios'

declare interface DocTree {
  /** ID */
  i: number
  /** Name */
  n: string
  /** open: 0:否;1:是; */
  o: number
  /** Tags */
  t: string[]
  icon: string
  /** 文档类型: 1:文件夹|2:文章; */
  ty: DocType
  star: number
  children?: DocTree[]
}

/**
 * 文章详情
 */
declare interface DocInfo {
  id: number
  pid: number
  name: string
  icon?: string
  tags: string[]
  sort: number
  cover?: string
  describes?: string
  starStatus: number
  pv?: number
  uv?: number
  likes: number
  dislikes: number,
  words?: number
  version?: number
  storePath?: string
  subjectWords?: string
  subjectUpdTime?: string
  type: DocType
  creTime?: string
  updTime?: string
  html?: string
  // 公开文章的信息
  openStatus: number
  openTime?: string
  openVersion?: number
  syncTime?: string
}

/** 文档类型: 1:文件夹|2:文章; */
declare type DocType = 1 | 2 | 3

declare interface Window {
  onHtmlEventDispatch: any
}

/**
 * 目录结构
 */
declare interface Toc {
  content: string
  clazz: string
  id: string
}


