<template>
  <el-config-provider :locale="zhCn">
    <RouterView />
  </el-config-provider>
</template>

<script setup lang="ts">
import { onMounted, type StyleHTMLAttributes } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElConfigProvider } from 'element-plus'
import { isNotBlank } from './assets/utils/obj'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'

const userStore = useUserStore()

onMounted(async () => {
  await userStore.getUserinfoOpen()
  initCustomTheme()
  // 优先使用后台配置的博客名称, 否则使用默认
  if (isNotBlank(userStore.userParams.WEB_LOGO_NAME)) {
    document.title = userStore.userParams.WEB_LOGO_NAME
  } else {
    document.title = 'Blossom'
  }

  // 优先使用后台配置的博客名称, 否则使用默认的 favicon.png
  if (isNotBlank(userStore.userParams.WEB_LOGO_URL)) {
    let link: HTMLLinkElement = document.querySelector("link[rel*='icon']") || document.createElement('link')
    link.type = 'image/x-icon'
    link.rel = 'shortcut icon'
    link.href = userStore.userParams.WEB_LOGO_URL
    document.getElementsByTagName('head')[0].appendChild(link)
  }
})

const THEME_STYLE_TAG_ID = 'blossom-blog-theme-css'

/**
 * 初始化样式
 */
const initCustomTheme = () => {
  const rgb = userStore.userParams.WEB_BLOG_COLOR
  if (rgb && !rgb.toLowerCase().startsWith('rgb(')) {
    return
  }
  const prefix = rgb.substring(4, rgb.length - 1)
  let text = `:root {
    --el-color-primary: ${rgb};
    --el-color-primary-dark: ${rgb};
    --el-color-primary-dark-2: rgba(${prefix}, 0.8);
    --el-color-primary-light-1: rgba(${prefix}, 0.9);
    --el-color-primary-light-2: rgba(${prefix}, 0.8);
    --el-color-primary-light-3: rgba(${prefix}, 0.7);
    --el-color-primary-light-4: rgba(${prefix}, 0.6);
    --el-color-primary-light-5: rgba(${prefix}, 0.5);
    --el-color-primary-light-6: rgba(${prefix}, 0.4);
    --el-color-primary-light-7: rgba(${prefix}, 0.3);
    --el-color-primary-light-8: rgba(${prefix}, 0.2);
    --el-color-primary-light-9: rgba(${prefix}, 0.1);
  }`

  let themeStyleTag = document.createElement('style') as unknown as StyleHTMLAttributes
  themeStyleTag.type = 'text/css'
  themeStyleTag.id = THEME_STYLE_TAG_ID
  themeStyleTag.innerHTML = text
  document
    .getElementsByTagName('head')
    .item(0)!
    .appendChild(themeStyleTag as Node)
  // }
}
</script>
