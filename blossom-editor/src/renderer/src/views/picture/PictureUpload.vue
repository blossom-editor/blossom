<template>
  <div class="picture-upload-root">
    <!-- :data="{ pid: curFolder?.id, repeatUpload: porps.repeatUpload }" -->
    <el-upload
      name="file"
      list-type="text"
      drag
      multiple
      :disabled="!curFolder || !curFolder?.id"
      :action="serverStore.serverUrl + uploadFileApiUrl"
      :data="(f: UploadRawFile) => uploadDate(f, curFolder!.id, porps.repeatUpload)"
      :headers="{ Authorization: 'Bearer ' + userStore.auth.token }"
      :show-file-list="true"
      :before-upload="beforeUpload"
      :on-success="onUploadSeccess"
      :on-error="onError">
      <bl-row v-if="!curFolder || !curFolder?.id" just="center" align="center" height="100%" style="font-size: 12px"
        >请先选择文件夹，再上传文件</bl-row
      >
      <bl-row v-else just="center" align="center" height="100%" style="font-size: 12px">
        点击或拖拽上传至<br />
        《{{ curFolder?.name }}》
      </bl-row>
    </el-upload>
  </div>
</template>

<script setup lang="ts">
import { inject } from 'vue'
import { UploadRawFile } from 'element-plus'
import { uploadFileApiUrl } from '@renderer/api/blossom'
import { useUserStore } from '@renderer/stores/user'
import { useServerStore } from '@renderer/stores/server'
import { provideKeyDocInfo } from '@renderer/views/doc/doc'
import { beforeUpload, onUploadSeccess, onError, uploadDate } from '@renderer/views/picture/scripts/picture'

const porps = defineProps({
  repeatUpload: {
    type: Boolean,
    default: false
  }
})

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

  & > div {
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
