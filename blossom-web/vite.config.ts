import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import { visualizer } from 'rollup-plugin-visualizer';
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'

// 为 Element Plus 按需引入样式。
import ElementPlus from 'unplugin-element-plus/vite'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

// https://vitejs.dev/config/
export default defineConfig({
  base: './',
  server: {
    host: "0.0.0.0",
    // 开发环境代理
    proxy: {
      '/dev': {
        target:'http://127.0.0.1:9999/',
      },
    },
    port: 5174,
    //vue3 vite配置热更新不用手动刷新
    hmr: true
  },
  plugins: [
    vue(),
    vueJsx(),
    visualizer({
      emitFile: false,
      filename: "stats.html", //分析图生成的文件名
      open: true //如果存在本地服务端口，将在打包后自动展示
    }),
    // ElementPlus 按需引入的插件
    AutoImport({
      resolvers: [ElementPlusResolver()],
    }),
    Components({
      resolvers: [ElementPlusResolver()],
    }),
    ElementPlus({
      // options
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  css: {
    preprocessorOptions: {
      scss: {
        /**如果引入多个文件，可以使用
         * '@import "@/assets/scss/globalVariable1.scss";
         * @import"@/assets/scss/globalVariable2.scss";'
         **/
        additionalData: '@import "@/assets/styles/config.scss";',
      }
    }
  },
  build: {
    // 警告大小, 单位kb
    // chunkSizeWarningLimit: 1000,
    rollupOptions: {
      output: {
        manualChunks(id) {
          if (id.includes('node_modules')) {
            return id.toString().split('node_modules/')[1].split('/')[0].toString();
          }
        }
      }
    }
  }
})
