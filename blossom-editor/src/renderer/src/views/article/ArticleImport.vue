<template>
  <div class="article-import-root">

    <!-- 标题 -->
    <div class="title-wrapper">
      <div class="title">导入文章</div>
    </div>

    <div class="content">

      <el-upload class="article-upload" ref="uploadRef" name="file" :action="serverStore.serverUrl + articleImportApiUrl"
        :data="{ pid: porps.doc.i }" :headers="{ 'Authorization': 'Bearer ' + userStore.auth.token }"
        :on-change="onChange" :before-upload="beforeUpload" :on-success="onUploadSeccess" :on-error="onError"
        :auto-upload="false" :limit="20" multiple>
        <template #trigger>
          <el-button size="default">选择文章</el-button>
        </template>
        <el-button style="margin-left: 10px;" size="default" type="primary" @click="submitUpload" plain>开始导入</el-button>
        <template #tip>
          <div class="el-upload__tip upload-tip">
            仅支持导入 [.md] 以及 [.txt] 后缀的文件，每次最多导入 20 个文件
          </div>
        </template>
      </el-upload>
    </div>
  </div>
</template>

<script setup lang="ts">
import { PropType, ref } from 'vue'
import type { UploadInstance } from 'element-plus'
import { articleImportApiUrl } from '@renderer/api/blossom'
import { useUserStore } from '@renderer/stores/user'
import { useServerStore } from '@renderer/stores/server'
import { onChange, beforeUpload, onUploadSeccess, onError } from './scripts/article-import'

const userStore = useUserStore()
const serverStore = useServerStore()

const porps = defineProps({
  doc: {
    type: Object as PropType<DocTree>,
    required: true
  }
})

const uploadRef = ref<UploadInstance>()

const submitUpload = () => {
  uploadRef.value!.submit()
}
</script>


<style scoped lang="scss">
$height-title: 50px;

.article-import-root {
  border-radius: 10px;

  .title-wrapper {
    @include box(100%, $height-title);
    @include flex(row, flex-start, center);
    border-bottom: 1px solid var(--el-border-color);

    .info-icon {
      @include box(50px, 100%);
      padding: 5px 0;
      text-align: center;
    }

    .title {
      @include font(16px);
      @include box(100%, 100%);
      height: 100%;
      padding-top: 10px;
      color: var(--el-color-primary);
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
      padding-left: 30px;
    }
  }

  .content {
    padding: 20px;

    .upload-tip {
      border: 1px solid var(--el-border-color);
      padding: 5px 10px;
      border-radius: 5px;
      color: rgb(188, 55, 55);
    }

    .article-upload {
      :deep(.el-upload-list) {
        li {
          transition: none;
          margin-bottom: 0;
        }
      }
    }

  }


}
</style>