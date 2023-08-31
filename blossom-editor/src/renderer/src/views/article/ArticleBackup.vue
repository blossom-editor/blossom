<template>
  <div class="article-backup-root">

    <!-- 标题 -->
    <div class="title-wrapper">
      <div class="title">文章全量备份记录</div>
    </div>

    <div class="content">
      <div class="workbench">
        <el-button @click="getBackupList">
          <span class="iconbl bl-refresh-line" style="margin-right: 7px;"></span>刷新
        </el-button>
        <el-button @click="backupNow">
          <span class="iconbl bl-archive-line" style="margin-right: 7px;"></span>立即备份
        </el-button>
        <div class="backup-tip">当前仅支持下载最大 10MB 的文件, 如果备份文件查过 10MB, 请您自行从服务器中下载。若您的服务器带宽较小，也建议您自行从服务器下载。</div>
      </div>
      <div class="bak-container">
        <div v-for="bak in backupList" :key="bak.filename" class="bak-item">
          <span class="iconbl bl-file-zip-line"></span>
          <span class="name">{{ bak.filename }}</span>
          <span class="size">{{ formatFileSize(bak.fileLength) }}</span>
          <el-button class="visible" text bg style="margin-left: 5px;" @click="download(bak)"><span
              class="iconbl bl-folder-download-line"></span></el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { formatFileSize } from '@renderer/assets/utils/util'
import { articleBackupListApi, articleBackupApi, articleBackupDownloadApi } from '@renderer/api/blossom'
import type { BackupFile } from '@renderer/api/blossom'
import Notify from '@renderer/scripts/notify'

onMounted(() => {
  getBackupList()
})

const backupList = ref<BackupFile[]>()

const getBackupList = () => {
  articleBackupListApi().then(resp => {
    backupList.value = resp.data
  })
}

const backupNow = () => {
  articleBackupApi().then(_ => {
    Notify.info('后台正在备份中, 请稍后刷新列表查看最新备份', '备份中')
  })
}

const download = (file: BackupFile) => {
  if (file.fileLength > 10485760) {
    Notify.error('暂不支持下载超过 [10MB] 的文件', '下载失败')
    return;
  }

  articleBackupDownloadApi({ filename: file.filename }).then(resp => {
    let filename: string = resp.headers.get('content-disposition')
    let filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
    let matches = filenameRegex.exec(filename);
    if (matches != null && matches[1]) {
      filename = decodeURI(matches[1].replace(/['"]/g, ''));
    }
    let a = document.createElement('a')
    let blob = new Blob([resp.data], { type: "text/plain" })
    let objectUrl = URL.createObjectURL(blob)
    a.setAttribute("href", objectUrl)
    a.setAttribute("download", filename)
    a.click()
  })
}

</script>

<style scoped lang="scss">
$height-title: 50px;

.article-backup-root {
  border-radius: 10px;
  @include box(100%, 100%);

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
    @include box(100%, calc(100% - #{$height-title}));
    padding: 20px;

    .workbench {
      @include box(100%, 60px);
      border-bottom: 1px solid var(--el-border-color);
      margin-bottom: 10px;

      .backup-tip {
        margin-top: 10px;
        font-size: 12px;
        color: var(--bl-text-color-light);
      }
    }

    .bak-container {
      @include box(100%, calc(100% - 70px));
      @include flex(column, flex-start, flex-start);
      align-content: flex-start;
      flex-wrap: wrap;
      overflow-x: overlay;

      .bak-item {
        width: 400px;
        // border: 1px solid red;
        margin-bottom: 5px;
        margin-right: 5px;
        font-size: 14px;

        &:hover {
          .visible {
            opacity: 1;
          }
        }

        .bl-file-zip-line {
          font-size: 15px;
        }

        .name {
          padding: 0 10px;
          font-size: 13px;
        }


        .size {
          font-size: 12px;
          color: var(--bl-text-color-light);
        }

        .visible {
          opacity: 0;
        }
      }
    }
  }

}
</style>