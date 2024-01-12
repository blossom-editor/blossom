import { resolve } from 'path'
import { defineConfig, externalizeDepsPlugin } from 'electron-vite'
import vue from '@vitejs/plugin-vue'
import { visualizer } from 'rollup-plugin-visualizer'

// 为 Element Plus 按需引入样式。
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

export default defineConfig({
  main: {
    plugins: [externalizeDepsPlugin()]
  },
  preload: {
    plugins: [externalizeDepsPlugin()]
  },
  renderer: {
    envDir: resolve('src/env'),
    server: {
      host: '0.0.0.0',
      port: 5173,
      hmr: true
    },
    plugins: [
      vue(),
      visualizer({
        emitFile: false,
        filename: 'stats.html'
      }),
      // ElementPlus 按需引入的插件
      AutoImport({
        resolvers: [ElementPlusResolver()]
      }),
      Components({
        resolvers: [ElementPlusResolver()]
      })
    ],
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
          additionalData: '@import "@renderer/assets/styles/config.scss";'
        }
      }
    },
    // electorn 应用不需要拆分文件打包
    build: {
      // 警告大小, 单位kb
      // chunkSizeWarningLimit: 1000,
      rollupOptions: {
        output: {
          //@ts-ignore
          manualChunks(id) {
            if (id.includes('node_modules')) {
              return id.toString().split('node_modules/')[1].split('/')[0].toString()
            }
          }
        }
      }
    }
  }
})
