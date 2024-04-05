import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import { visualizer } from 'rollup-plugin-visualizer'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'

// 为 Element Plus 按需引入样式。
import ElementPlus from 'unplugin-element-plus/vite'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import { resolve } from 'path'

const npm_lifecycle_event = process.env.npm_lifecycle_event

const isSpring = () => {
  return npm_lifecycle_event && npm_lifecycle_event === 'build:spring'
}

console.log('当前运行脚本: ', npm_lifecycle_event)

// https://vitejs.dev/config/
export default defineConfig({
  base: './',
  envDir: resolve('env'),
  server: {
    host: '0.0.0.0',
    port: 5174,
    hmr: true
  },
  plugins: [
    vue(),
    vueJsx(),
    visualizer({
      emitFile: false,
      filename: 'stats.html', // 分析图生成的文件名
      open: true //如果存在本地服务端口，将在打包后自动展示
    }),
    // ElementPlus 按需引入的插件
    AutoImport({
      resolvers: [ElementPlusResolver()]
    }),
    Components({
      resolvers: [ElementPlusResolver()]
    }),
    ElementPlus({
      // options
    })
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  css: {
    preprocessorOptions: {
      scss: {
        /**
         * 如果引入多个文件，可以使用
         * '@import "@/assets/scss/globalVariable1.scss";
         * @import"@/assets/scss/globalVariable2.scss";'
         */
        additionalData: '@import "@/assets/styles/config.scss";'
      }
    }
  },
  build: {
    outDir: isSpring() ? '../blossom-backend/backend/src/main/resources/static/blog' : 'dist',
    // 警告大小, 单位kb
    // chunkSizeWarningLimit: 1000,
    rollupOptions: {
      output: {
        manualChunks(id) {
          if (id.includes('node_modules')) {
            return id.toString().split('node_modules/')[1].split('/')[0].toString()
          }
        }
      }
    }
  }
})
