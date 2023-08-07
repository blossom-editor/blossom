import { resolve } from 'path'
import { defineConfig, externalizeDepsPlugin } from 'electron-vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  main: {
    plugins: [externalizeDepsPlugin()]
  },
  preload: {
    plugins: [externalizeDepsPlugin()]
  },
  renderer: {
    plugins: [vue()],
    resolve: {
      alias: {
        '@renderer': resolve('src/renderer/src')
      }
    },
    css: {
      preprocessorOptions: {
        scss: {
          /**
           * 如果引入多个文件，可以使用
           * '@import "@/assets/scss/globalVariable1.scss";@import"@/assets/scss/globalVariable2.scss";'
           */
          additionalData: '@import "@renderer/assets/styles/config.scss";',
        }
      }
    },
    build: {
      // 警告大小, 单位kb
      // chunkSizeWarningLimit: 1000,
      rollupOptions: {
        output: {
          //@ts-ignore
          manualChunks(id) {
            if (id.includes('node_modules')) {
              return id.toString().split('node_modules/')[1].split('/')[0].toString();
            }
          }
        }
      }
    }
  }
})
