declare interface DocTree {
  /** ID */
  i: string
  p: string
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
  likes?: number
  words?: number
  openVersion?: number
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
