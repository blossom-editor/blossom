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
  // 是否显示专题样式
  isShowSubjectStyle: boolean
  // 是否在首页显示收藏卡片
  isHomeStarCard: boolean
  // 是否在首页显示专题卡片
  isHomeSubjectCard: boolean
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
        treeDocsFontSize: '14px',
        todoStatExpand: true,
        webCollectExpand: true,
        isShowSubjectStyle: true,
        isHomeStarCard: true,
        isHomeSubjectCard: true
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
    setViewStyle(viewStyle: ViewStyle) {
      this.viewStyle = viewStyle
      Local.set(VIEW_STYLE_KEY, this.viewStyle)
    },
    setPicStyle(picStyle: PicStyle) {
      this.picStyle = picStyle
      Local.set(PIC_STYLE_KEY, this.picStyle)
    }
  }
})
