import { defineStore } from 'pinia'
import { Local } from '@renderer/assets/utils/storage'

export const VIEW_STYLE_KEY = 'viewStyle'
export const PIC_STYLE_KEY = 'picStyle'
export const EDITOR_STYLE_KEY = 'editorStyle'
export const KEYMAP_KEY = 'keymapConfig'

/**
 * 页面样式
 */
export interface ViewStyle {
  // 树状列表的字体大小
  treeDocsFontSize: string
  // 展开收起待办事项的统计
  todoStatExpand: boolean
  // 展开收起首页网页收藏
  webCollectExpand: boolean
  // 是否显示文件夹收藏图标
  isShowFolderStarTag: boolean
  // 是否显示专题样式
  isShowSubjectStyle: boolean
  // 是否以卡片方式显示文章收藏
  isHomeStarCard: boolean
  // 是否以卡片方式显示专题
  isHomeSubjectCard: boolean
  // 是否以卡片方式显示网页收藏
  isWebCollectCard: boolean
  // 是否开启全局阴影, 在 ThemeSetting.vue#changeGlobalShadow 中设置
  isGlobalShadow: boolean
  // 是否显示试用按钮
  isShowTryuseBtn: boolean
  // 只展开一项子菜单
  isMenuUniqueOpened: boolean
  // 点击登录后自动进入首页
  isLoginToHomePage: boolean
  // 显示公开收藏等状态标识
  isShowArticleType: boolean
  // 显示文章图标
  isShowArticleIcon: boolean
  // 显示专题目录标签
  isShowArticleTocTag: boolean
  // 显示自定义标签
  isShowArticleCustomTag: boolean
  // 显示公开文件夹标签
  isShowFolderOpenTag: boolean
  // 显示左下角上传入口
  isShowAsideUpload: boolean
  // 显示左上角 LOGO
  isShowAsideLogo: boolean
  // 显示简易左侧菜单
  isShowAsideSimple: boolean
}

/**
 * 编辑器样式配置
 */
export interface EditorStyle {
  // 编辑器的字体样式
  fontFamily: string
  // 编辑器的字体大小
  fontSize: string
  // 默认的代码块语言
  defaultPreLanguage: string
  // 编辑器是否超出自动换行
  isAutoBreakLine: boolean
  // 是否显示代码块行数
  isShowPreLineNumber: boolean
}

/**
 * 图片配置
 */
export interface PicStyle {
  // 最大上传限制
  maxSize: number
  // 是否自动添加文件后缀
  isAddSuffix: boolean
}

/**
 * 快捷键配置
 */
export interface KeymapConfig {}

/**
 * Blossom 设置
 */
export interface BlConfig {
  viewStyle: ViewStyle
  picStyle: PicStyle
  editorStyle: EditorStyle
  keymapConfig: KeymapConfig
}

/**
 * 客户端的配置项
 * 客户端配置会缓存在 localStorage 中, 由于版本变更会新增配置项, 所以需要通过扩展运算符将已缓存的数据和新增配置进行合并
 */
export const useConfigStore = defineStore('configStore', {
  state: (): BlConfig => ({
    // 编辑器配置
    editorStyle: {
      ...{
        fontFamily: "'Jetbrains Mono', sans-serif",
        fontSize: '14px',
        defaultPreLanguage: '',
        isAutoBreakLine: true,
        isShowPreLineNumber: false
      },
      ...Local.get(EDITOR_STYLE_KEY)
    },
    // 图片配置
    picStyle: {
      ...{
        maxSize: 50
      },
      ...Local.get(PIC_STYLE_KEY)
    },
    // 样式配置
    viewStyle: {
      ...{
        treeDocsFontSize: '13px',
        todoStatExpand: true,
        webCollectExpand: true,
        isShowFolderStarTag: true,
        isShowSubjectStyle: false,
        isHomeStarCard: true,
        isHomeSubjectCard: false,
        isWebCollectCard: true,
        isGlobalShadow: false,
        isShowTryuseBtn: true,
        isMenuUniqueOpened: true,
        isLoginToHomePage: false,
        isShowArticleType: true,
        isShowArticleIcon: true,
        isShowArticleTocTag: true,
        isShowArticleCustomTag: true,
        isShowFolderOpenTag: true,
        isShowAsideUpload: true,
        isShowAsideLogo: true,
        isShowAsideSimple: false
      },
      ...Local.get(VIEW_STYLE_KEY)
    },
    // 快捷键
    keymapConfig: {}
  }),
  /**
   * 因为配置涉及到 LOCAL_STORAGE 的持久化, 所以无法通过响应式进行修改
   */
  actions: {
    setEditorStyle(editorStyle: EditorStyle) {
      this.editorStyle = editorStyle
      Local.set(EDITOR_STYLE_KEY, this.editorStyle)
    },
    setViewStyle(_viewStyle: ViewStyle) {
      // this.viewStyle = viewStyle
      Local.set(VIEW_STYLE_KEY, this.viewStyle)
    },
    setPicStyle(picStyle: PicStyle) {
      this.picStyle = picStyle
      Local.set(PIC_STYLE_KEY, this.picStyle)
    }
  }
})
