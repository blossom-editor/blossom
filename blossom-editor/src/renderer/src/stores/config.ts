import { defineStore } from 'pinia'
import { Local } from '@renderer/assets/utils/storage'

export const VIEW_STYLE_KEY = 'viewStyle'
export const EDITOR_STYLE_KEY = 'editorStyle'
export const KEYMAP_KEY = 'keymapConfig'

/**
 * 页面样式
 */
export interface ViewStyle {
  treeDocsFontSize: string
  todoStatExpand: boolean
}

/**
 * 编辑器样式配置
 */
export interface EditorStyle {
  // 编辑器的字体样式
  fontFamily: string
  // 编辑器的字体大小
  fontSize: string
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
  editorStyle: EditorStyle
  keymapConfig: KeymapConfig
}

export const useConfigStore = defineStore('configStore', {
  state: (): BlConfig => ({
    viewStyle: {
      ...{
        treeDocsFontSize: '14px',
        todoStatExpand: true
      },
      ...Local.get(VIEW_STYLE_KEY)
    },
    // 编辑器配置
    editorStyle: {
      ...{
        fontFamily: "'Jetbrains Mono', sans-serif",
        fontSize: '14px'
      },
      ...Local.get(EDITOR_STYLE_KEY)
    },
    // 快捷键
    keymapConfig: {}
  }),
  actions: {
    setEditorStyle(editorStyle: EditorStyle) {
      this.editorStyle = editorStyle
      Local.set(EDITOR_STYLE_KEY, this.editorStyle)
    },

    setViewStyle(viewStyle: ViewStyle) {
      this.viewStyle = viewStyle
      Local.set(VIEW_STYLE_KEY, this.viewStyle)
    }
  }
})
