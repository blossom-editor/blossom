import { defineStore } from 'pinia';
import { Local } from '@renderer/assets/utils/storage';

export const editorStyleKey = 'editorStyle'
export const keymapKey = 'keymapConfig'

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
 * Blossom 设置
 */
export interface BlConfig {

  editorStyle: EditorStyle,
  // 快捷键
  keymapConfig: {

  }
}

export const useConfigStore = defineStore('configStore', {
  state: (): BlConfig => ({
    // 编辑器配置
    editorStyle: Local.get(editorStyleKey) || {
      fontFamily: "'Jetbrains Mono', sans-serif",
      fontSize: '14px'
    },
    // 快捷键
    keymapConfig: {

    }
  }),
  actions: {
    setEditorStyle(editorStyle: EditorStyle) {
      this.editorStyle = editorStyle
      Local.set(editorStyleKey, this.editorStyle)
    }
    // setEditorFontFamily(fontFamily: string) {
    //   this.editorStyle.fontFamily = fontFamily
    //   Local.set(editorStyleKey, this.editorStyle)
    // },
    // setEditorFontSize(fontSize: string) {
    //   this.editorStyle.fontSize = fontSize
    //   Local.set(editorStyleKey, this.editorStyle)
    // }
  }
});