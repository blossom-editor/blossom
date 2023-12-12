/// <reference types="vite/client" />

interface ImportMetaEnv {
  /**
   * API 的基础 URL
   */
  readonly VITE_BL_API_BASE_URI: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}

declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  // eslint-disable-next-line @typescript-eslint/no-explicit-any, @typescript-eslint/ban-types
  const component: DefineComponent<{}, {}, any>

  export default component
}