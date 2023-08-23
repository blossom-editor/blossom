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
  box-shadow: 3px 3px 10px 3px rgba(107, 104, 104, 0.3);
  border: 1px solid rgb(31, 31, 31);

  .app-header {
    @include box(100%, 30px);
  }

  .app-main {
    @include box(100%, calc(100% - 30px))
  }
}
</style>
