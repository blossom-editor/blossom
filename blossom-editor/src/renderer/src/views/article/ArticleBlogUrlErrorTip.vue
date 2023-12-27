<template>
  <div class="article-backup-root">
    <!-- 标题 -->
    <div class="info-title">
      <div class="iconbl bl-dizhicuowu"></div>
      博客地址配置方式
    </div>

    <div class="content">
      
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { formatFileSize } from '@renderer/assets/utils/util'
import { articleBackupListApi, articleBackupApi, articleBackupDownloadFragmentApi, articleBackupDownloadFragmentHeadApi } from '@renderer/api/blossom'
import type { BackupFile } from '@renderer/api/blossom'
import Notify from '@renderer/scripts/notify'
</script>

<style scoped lang="scss">
@import '@renderer/assets/styles/bl-dialog-info';

.article-backup-root {
  border-radius: 10px;
  @include box(100%, 100%);

  .content {
    @include box(100%, calc(100% - 50px));
    padding: 20px;
  }
}
</style>
