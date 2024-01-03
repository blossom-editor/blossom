<template>
  <el-config-provider :locale="zhCn">
    <RouterView />
  </el-config-provider>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElConfigProvider } from 'element-plus'
import { isNotBlank } from './assets/utils/obj'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'

const userStore = useUserStore()

onMounted(async () => {
  await userStore.getUserinfoOpen()

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
</script>
