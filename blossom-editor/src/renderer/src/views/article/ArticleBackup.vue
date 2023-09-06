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
        <el-tooltip content="将文章以 Markdown 格式进行备份" :hide-after="0">
          <el-button @click="backupNow('MARKDOWN', 'NO')">
            <span class="iconbl bl-file-markdown" style="margin-right: 7px;"></span>备份 Markdown
          </el-button>
        </el-tooltip>
        <el-tooltip content="将文章以 Markdown 格式进行备份，同时备份所有图片" :hide-after="0">
          <el-button @click="backupNow('MARKDOWN', 'YES')">
            <span class="iconbl bl-file-markdown" style="margin-right: 7px;"></span>备份本地 Markdown
          </el-button>
        </el-tooltip>

        <el-tooltip content="将文章以 Html 格式进行备份" :hide-after="0">
          <el-button @click="backupNow('HTML', 'NO')">
            <span class="iconbl bl-HTML" style="margin-right: 7px;"></span>备份 Html
          </el-button>
        </el-tooltip>

        <el-tooltip content="将文章以 Html 格式进行备份，同时备份所有图片" :hide-after="0">
          <el-button @click="backupNow('HTML', 'YES')">
            <span class="iconbl bl-HTML" style="margin-right: 7px;"></span>备份本地 Html
          </el-button>
        </el-tooltip>
        <div class="backup-tip">
          服务器将于每日早上 7 点备份 Markdown 数据。
        </div>
        <div class="backup-tip">
          当前仅支持下载最大 10MB 的文件, 过大时请您自行从服务器中下载。若您的服务器带宽较小，也建议您自行从服务器下载。
        </div>
      </div>


      <div class="bak-container">
        <div v-for="bak in backupList" :key="bak.filename" class="bak-item">
          <bl-row>
            <span class="iconbl bl-file-zip-line"></span>
            <span class="name">{{ bak.filename }}</span>
            <span class="size">{{ formatFileSize(bak.fileLength) }}</span>
          </bl-row>
          <div>
            <span class="desc">{{ bak.desc }}</span>
          </div>
          <el-button class="download-btn" text bg style="margin-left: 5px;" @click="download(bak)"><span
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
    backupList.value = resp.data?.map((bak) => {
      let desc = ''
      let type = bak.filename.charAt(1)
      let toLocal = bak.filename.charAt(2)


      if (toLocal === 'L') {
        desc += '本地路径'
      } else if (toLocal === 'N') {
        desc += '网络地址'
      }

      if (type === 'M') {
        desc += ', Markdown'
      } else if (type === 'H') {
        desc += ', Html'
      }
      bak.desc = desc
      return bak
    })
  })
}

const backupNow = (type: 'MARKDOWN' | 'HTML', toLocal: 'YES' | 'NO') => {
  articleBackupApi({ type: type, toLocal: toLocal }).then(_ => {
    Notify.info('后台正在备份中, 请稍后刷新列表查看最新备份', '备份中')
  })
}

const download = (file: BackupFile) => {
  if (file.fileLength > 10485760) {
    Notify.error('暂不支持下载超过 [10MB] 的文件', '下载失败')
    return;
  }

  articleBackupDownloadApi({ filename: file.filename + '.zip' }).then(resp => {
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
    a.remove()
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
      @include box(100%, 90px);
      border-bottom: 1px solid var(--el-border-color);
      margin-bottom: 10px;

      .backup-tip {
        margin-top: 10px;
        font-size: 12px;
        color: var(--bl-text-color-light);
      }
    }

    .bak-container {
      @include box(100%, calc(100% - 100px));
      @include flex(column, flex-start, flex-start);
      align-content: flex-start;
      flex-wrap: wrap;
      overflow-x: overlay;
      padding: 10px;

      .bak-item {
        width: 350px;
        margin-bottom: 5px;
        margin-right: 5px;
        font-size: 14px;
        padding: 5px 10px;
        border-radius: 4px;
        transition: box-shadow 0.1s;
        position: relative;

        &:hover {
          box-shadow: var(--bl-box-shadow);

          .download-btn {
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


        .size,
        .desc {
          font-size: 12px;
          color: var(--bl-text-color-light);
        }


        .download-btn {
          position: absolute;
          right: 5px;
          top: calc(50% - 12px);
          opacity: 0;
        }
      }
    }
  }

}
</style>