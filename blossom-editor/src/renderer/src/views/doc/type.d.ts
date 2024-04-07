/**
 * 文件夹和文档的树状结构, 简短的字段名有助于减少传输大小
 */
declare interface DocTree {
  /** id */
  i: string
  /** pid */
  p: string
  /** name */
  n: string
  /** 是否修改名称 */
  updn?: boolean
  /** open: 0:否;1:是; */
  o: number
  /** 版本有差异, 公开文章才会有此项 */
  vd?: number | null
  /** Tags */
  t: string[]
  /** 排序 */
  s: number
  /** 图标 */
  icon: string
  /** 文档类型: 1:文章文件夹|2:图片文件夹|3:文章; */
  ty: DocType
  /** 是否 star: 0:否;1:是; */
  star: number
  /** 是否显示排序 */
  showSort?: boolean
  /** 子集 */
  children?: DocTree[]
  /** 存储路径 */
  sp?: string
}

/**
 * 文章详情
 */
declare interface DocInfo {
  id: string
  pid: string
  name: string
  icon?: string
  tags: string[]
  sort: number
  cover?: string
  color?: string
  describes?: string
  starStatus: number
  pv?: number
  uv?: number
  likes?: number
  words?: number
  version?: number
  storePath?: string
  subjectWords?: string
  subjectUpdTime?: string
  type: DocType
  creTime?: string
  updTime?: string
  toc?: string
  // 文章正文
  markdown?: string
  html?: string
  // 公开文章的信息
  openStatus: number
  openTime?: string
  openVersion?: number
  syncTime?: string
}

/** 文件夹类型: 1:文章文件夹|2:图片文件夹 */
declare type FolderType = 1 | 2

/** 文档类型: 1:文章文件夹|2:图片文件夹|3:文章|11:分隔文档; */
declare type DocType = 1 | 2 | 3 | 11

/** 文档弹框类型: 增|删|查 */
declare type DocDialogType = 'add' | 'upd' | 'info'

/** 右键菜单对象的显示位置 */
declare type RightMenu = { show: boolean; clientX?: number; clientY?: number }

declare type RightMenuLevel2 = { top: string }

/** 快捷标签 */
declare type QuickTag = { name: string; selected: boolean }

/**
 * 在 window 注册内联事件
 */
declare interface Window {
  onHtmlEventDispatch: any
}
