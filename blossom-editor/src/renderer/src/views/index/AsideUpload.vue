<template>
  <div class="aside-upload-root">
    <el-tooltip effect="light" placement="right" :show-after="1000" :hide-after="0">
      <template #content>从这里上传, 会上传至<br />《🌌 默认文件夹》</template>
      <el-upload
        name="file"
        :action="serverStore.serverUrl + uploadFileApiUrl"
        :data="(f: UploadRawFile) => uploadDate(f, '-1')"
        :headers="{ Authorization: 'Bearer ' + userStore.auth.token }"
        :show-file-list="false"
        :before-upload="beforeUpload"
        :on-success="onUploadSeccess"
        :on-error="onError"
        drag
        multiple>
        <upload-filled />
      </el-upload>
    </el-tooltip>
  </div>
</template>

<script setup lang="ts">
import { UploadRawFile } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import { uploadFileApiUrl } from '@renderer/api/blossom'
import { useUserStore } from '@renderer/stores/user'
import { useServerStore } from '@renderer/stores/server'
import { beforeUpload, onUploadSeccess, onError, uploadDate } from '@renderer/views/picture/scripts/picture'

//#region ----------------------------------------< panin store >--------------------------------------
const userStore = useUserStore()
const serverStore = useServerStore()
//#endregion
</script>

<style scoped lang="scss">
.aside-upload-root {
  @include box(100%, 100px);
  color: var(--el-color-primary);
  padding: 5px;

  :deep(.el-upload) {
    @include box(100%, 100%);
    --el-upload-dragger-padding-horizontal: 20px;
    --el-upload-dragger-padding-vertical: 3px;
    --el-fill-color-blank: var(--el-color-primary-light-9);
    --el-border-color: var(--el-color-primary-light-3);
    color: var(--el-color-primary-light-5);

    :deep(svg) {
      transition: 0.3s;
    }

    :hover {
      color: var(--el-color-primary);
    }
  }
}
</style>
