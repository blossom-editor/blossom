<template>
  <el-config-provider :size="'small'" :locale="zhCn">
    <div class="app">
      <div class="app-header">
        <AppHeader></AppHeader>
      </div>
      <div class="app-main">
        <RouterView />
      </div>
    </div>
  </el-config-provider>
</template>

<script setup lang="ts">
import { ElConfigProvider } from 'element-plus'
import AppHeader from './components/AppHeader.vue'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
</script>

<style lang="scss">
.app {
  @include box(100%, 100%);
  overflow: hidden;
  .app-header {
    @include box(100%, 30px);
  }

  .app-main {
    @include box(100%, calc(100% - 30px));
  }
}
</style>
