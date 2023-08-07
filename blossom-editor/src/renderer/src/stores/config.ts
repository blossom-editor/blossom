import { defineStore } from 'pinia';
import { Local } from '@renderer/assets/utils/storage';

export const editorKey = 'editorConfig'
export const keymapKey = 'keymapConfig'

export interface BlConfig {
  // 编辑器配置
  editorConfig: {
    fontFamily: string
  },
  // 快捷键
  keymapConfig: {

  }
}

export const useConfigStore = defineStore('configStore', {
  state: (): BlConfig => ({
    // 编辑器配置
    editorConfig: Local.get(editorKey) || {
      fontFamily: "'Jetbrains Mono', sans-serif"
    },
    // 快捷键
    keymapConfig: {

    }
  }),
  actions: {
    setEditorFontFamily(fontFamily: string) {
      this.editorConfig.fontFamily = fontFamily
      Local.set(editorKey, this.editorConfig)
    }
  }
});