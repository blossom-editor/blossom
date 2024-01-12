// 正常工作。
export {}

declare module '@renderer/assets/iconfont/iconfont.js'
declare module 'highlight.js'
declare module 'katex'

declare module 'vue' {
  interface ComponentCustomProperties {
    $t: (key: string) => string
  }
}
