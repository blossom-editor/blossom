<template>
  <el-config-provider :size="'small'" :locale="zhCn">
    <div class="app">
      <div class="app-main">
        <RouterView />
      </div>
    </div>
  </el-config-provider>
</template>

<script setup lang="ts">
import { ElConfigProvider } from 'element-plus'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
</script>

<style lang="scss">
.app {
  @include box(100%, 100%);
  overflow: hidden;

  .app-main {
    @include box(100%, 100%);
  }
}
</style>
