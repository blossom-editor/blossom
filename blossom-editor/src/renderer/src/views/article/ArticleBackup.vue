<template>
  <div class="article-backup-root">
    <!-- 标题 -->
    <div class="info-title">
      <div class="iconbl bl-a-cloudstorage-line"></div>
      文章全量备份记录
    </div>

    <div class="content">
      <div class="workbench">
        <bl-row just="space-between">
          <div>
            <el-tooltip content="将文章以 Markdown 格式进行备份" :hide-after="0">
              <el-button @click="backupNow('MARKDOWN', 'NO')">
                <span class="iconbl bl-file-markdown" style="margin-right: 7px"></span>备份 Markdown
              </el-button>
            </el-tooltip>
            <el-tooltip content="将文章以 Markdown 格式进行备份，同时备份所有图片" :hide-after="0">
              <el-button @click="backupNow('MARKDOWN', 'YES')">
                <span class="iconbl bl-file-markdown" style="margin-right: 7px"></span>备份本地 Markdown
              </el-button>
            </el-tooltip>

            <el-tooltip content="将文章以 Html 格式进行备份" :hide-after="0">
              <el-button @click="backupNow('HTML', 'NO')"> <span class="iconbl bl-HTML" style="margin-right: 7px"></span>备份 Html </el-button>
            </el-tooltip>

            <el-tooltip content="将文章以 Html 格式进行备份，同时备份所有图片" :hide-after="0">
              <el-button @click="backupNow('HTML', 'YES')"> <span class="iconbl bl-HTML" style="margin-right: 7px"></span>备份本地 Html </el-button>
            </el-tooltip>
            <el-button @click="cancelDownload" type="danger" plain>
              <span class="iconbl bl-a-closeline-line" style="margin-right: 7px"></span>取消下载
            </el-button>
          </div>
          <el-button @click="getBackupList" text> <span class="iconbl bl-refresh-line"></span></el-button>
        </bl-row>
        <div class="tips">服务器将于每日早上 7 点备份 Markdown 数据。</div>
        <div class="download-process">
          <el-progress :text-inside="true" :stroke-width="20" :percentage="downloadProgress" striped striped-flow :duration="200" />
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
          <el-button class="download-btn" text bg style="margin-left: 5px" @click="downloadFragment(bak)"
            ><span class="iconbl bl-folder-download-line"></span
          ></el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { formatFileSize } from '@renderer/assets/utils/util'
import { articleBackupListApi, articleBackupApi, articleBackupDownloadFragmentApi, articleBackupDownloadFragmentHeadApi } from '@renderer/api/blossom'
import type { BackupFile } from '@renderer/api/blossom'
import Notify from '@renderer/scripts/notify'

onMounted(() => {
  getBackupList()
})

const backupList = ref<BackupFile[]>()

const getBackupList = () => {
  articleBackupListApi().then((resp) => {
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
  articleBackupApi({ type: type, toLocal: toLocal }).then((_) => {
    Notify.info('后台正在备份中, 请稍后刷新列表查看最新备份', '备份中')
  })
}

let downloadState: 'IDLE' | 'LOADING' | 'CANCEL' = 'IDLE'
const downloadProgress = ref(0)
const fragmentSize = 32768

const downloadFragment = async (file: BackupFile) => {
  const filename = file.filename + '.zip'

  let start = 0
  let end = fragmentSize - 1
  let fileSize = 0
  let all: any[] = []
  downloadProgress.value = 0
  downloadState = 'LOADING'

  let headResp = await articleBackupDownloadFragmentHeadApi({ filename: filename })
  fileSize = headResp.headers.get('Content-Length')
  const fragmentCount = Math.ceil(fileSize / fragmentSize)
  if (fileSize < fragmentCount) {
    end = fileSize - 1
  }

  for (let i = 0; i < fragmentCount; i++) {
    const range = `bytes=${start}-${end}`
    const resp = await articleBackupDownloadFragmentApi({ filename: filename }, range)
    all.push(resp.data)
    start = end + 1
    end = Math.min(end + fragmentSize, fileSize - 1)
    downloadProgress.value = Math.round((end / fileSize) * 100)
    //@ts-ignore
    if (downloadState == 'CANCEL') {
      break
    }
  }

  //@ts-ignore
  if (downloadState == 'CANCEL') {
    downloadProgress.value = 0
    Notify.warning('下载任务已取消', '已取消')
    return
  }

  let a: HTMLAnchorElement = document.createElement('a')
  let blob: Blob = new Blob(all, { type: 'application/octet-stream' })
  let objectUrl = URL.createObjectURL(blob)
  a.setAttribute('href', objectUrl)
  a.setAttribute('download', filename)
  a.click()
  URL.revokeObjectURL(a.href)
  a.remove()
  downloadState = 'IDLE'
}

const cancelDownload = async () => {
  downloadState = 'CANCEL'
}
</script>

<style scoped lang="scss">
@import '@renderer/assets/styles/bl-dialog-info';

.article-backup-root {
  border-radius: 10px;
  @include box(100%, 100%);

  .content {
    @include box(100%, calc(100% - 50px));
    padding: 20px;

    .workbench {
      @include box(100%, 90px);
      border-bottom: 1px solid var(--el-border-color);
      margin-bottom: 10px;

      .download-process {
        padding: 3px 10px;
        :deep(.el-progress-bar__outer) {
          border-radius: 5px;
        }

        :deep(.el-progress-bar__inner) {
          border-radius: 5px;
        }
      }
    }

    .bak-container {
      @include box(100%, calc(100% - 100px));
      @include flex(column, flex-start, flex-start);
      align-content: flex-start;
      flex-wrap: wrap;
      overflow-x: scroll;
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
          box-shadow: var(--bl-box-shadow-hover);

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
