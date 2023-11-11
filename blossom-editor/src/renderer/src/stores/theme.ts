import { defineStore } from 'pinia'

export const useThemeStore = defineStore('themeStore', {
  state: () => ({
    isShow: false
  }),
  actions: {
    show() {
      this.isShow = true
    },
    close() {
      this.isShow = false
    }
  }
})
