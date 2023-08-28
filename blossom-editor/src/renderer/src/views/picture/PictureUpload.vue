<template>
  <div class="picture-upload-root">
    <el-upload :action="serverStore.serverUrl + uploadFileApiUrl" name="file" :data="{ pid: curFolder?.id }"
      :headers="{ 'Authorization': 'Bearer ' + userStore.auth.token }" :show-file-list="true" list-type="text"
      :before-upload="beforeUpload" :on-success="onUploadSeccess" :on-error="onError" drag multiple>
      <span style="font-size: 12px;">
        点击或拖拽上传至<br />
        《{{ curFolder?.name }}》
      </span>
    </el-upload>
  </div>
</template>

<script setup lang="ts">
import { inject } from 'vue'
import { uploadFileApiUrl } from '@renderer/api/blossom'
import { useUserStore } from '@renderer/stores/user'
import { useServerStore } from '@renderer/stores/server'
import { provideKeyDocInfo } from '@renderer/views/doc/doc'
import { beforeUpload, onUploadSeccess, onError } from '@renderer/views/picture/scripts/picture'

// 当前菜单中选择的文档
const curFolder = inject(provideKeyDocInfo)

//#region ----------------------------------------< panin store >--------------------------------------
const userStore = useUserStore()
const serverStore = useServerStore()
//#endregion

</script>

<style scoped lang="scss">
.picture-upload-root {
  @include box(100%, 100%);
  padding: 10px 10px 5px 10px;

  &>div {
    @include box(100%, 100%);
  }

  :deep(.el-upload-dragger) {
    @include box(100%, 50px);
    padding: 3px;
    --el-border-color: var(--el-color-primary-light-3);
    color: var(--el-color-primary-light-5);

    :deep(svg) {
      transition: 0.3s;
    }

    :hover {
      color: var(--el-color-primary);
    }
  }

  :deep(.el-upload-list) {
    @include box(100%, calc(100% - 50px - 15px));
    border: 1px solid var(--el-border-color);
    padding: 5px 0;
    margin: 15px 0 0;
    border-radius: 5px;
    overflow-y: scroll;

    .el-upload-list__item {
      --el-font-size-base: 10px;
      font-size: 10px;
      margin-bottom: 0;
    }
  }
}
</style>