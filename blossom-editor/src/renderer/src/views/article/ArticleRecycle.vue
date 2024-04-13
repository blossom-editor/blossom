<template>
  <div class="article-recycle-root">
    <!-- 标题 -->
    <div class="info-title">
      <div class="iconbl bl-delete-line"></div>
      文章回收站
    </div>

    <div class="content">
      <bl-row class="workbench" just="space-between">
        <div style="width: auto">
          <el-input style="width: 150px; margin-left: 10px" placeholder="搜索文章名" v-model="recycleSearch"></el-input>
          <span class="tips">回收站只保存 {{ userinfo.params.ARTICLE_RECYCLE_EXP_DAYS }} 天内的文章。</span>
        </div>
        <el-button @click="getBackupList" text> <span class="iconbl bl-refresh-line"></span></el-button>
      </bl-row>

      <div class="recycle-container">
        <div v-if="isEmpty(recycleList)" class="tips">回收站内无内容...</div>
        <div v-else v-for="recycle in recycsFilter" :key="recycle.id" class="recycle-item">
          <div class="name">{{ recycle.name }}</div>
          <div class="size">{{ recycle.delTime }}|{{ remainingDays(recycle.delTime) }}天后删除</div>
          <el-button class="restore-btn" @click="restore(recycle.id)"><span class="iconbl bl-a-cloudupload-line"></span></el-button>
          <el-button class="download-btn" @click="download(recycle.id)"><span class="iconbl bl-folder-download-line"></span></el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useUserStore } from '@renderer/stores/user'
import { articleRecycleListApi, articleRecycleRestoreApi, articleRecycleDownloadApi } from '@renderer/api/blossom'
import { isNull } from '@renderer/assets/utils/obj'
import { isEmpty } from 'lodash'
import dayjs from 'dayjs'

const userStore = useUserStore()
const { userinfo } = userStore
const deadLine = dayjs(new Date()).subtract(Number(userinfo.params.ARTICLE_RECYCLE_EXP_DAYS), 'day').toDate()

onMounted(() => {
  getBackupList()
})

const recycleSearch = ref('')
const recycleList = ref<any[]>([])
const recycsFilter = computed(() => {
  return recycleList.value.filter((r: any) => {
    if (isNull(recycleSearch.value)) {
      return true
    }
    return r.name.toLowerCase().includes(recycleSearch.value.toLowerCase())
  })
})

const remainingDays = (day: string) => {
  return dayjs(day).diff(deadLine, 'day')
}

const getBackupList = () => {
  articleRecycleListApi().then((resp) => {
    recycleList.value = resp.data
  })
}

const restore = (id: string) => {
  articleRecycleRestoreApi({ id: id }).then((_resp) => {
    getBackupList()
  })
}

const download = (id: string) => {
  articleRecycleDownloadApi({ id: id }).then((resp) => {
    let filename: string = resp.headers.get('content-disposition')
    let filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/
    let matches = filenameRegex.exec(filename)
    if (matches != null && matches[1]) {
      filename = decodeURI(matches[1].replace(/['"]/g, ''))
    }
    filename = decodeURI(filename)
    let a = document.createElement('a')
    let blob = new Blob([resp.data], { type: 'text/plain' })
    let objectUrl = URL.createObjectURL(blob)
    a.setAttribute('href', objectUrl)
    a.setAttribute('download', filename)
    a.click()
    URL.revokeObjectURL(a.href)
    a.remove()
  })
}
</script>

<style scoped lang="scss">
@import '@renderer/assets/styles/bl-dialog-info';

.article-recycle-root {
  @include box(100%, 100%);

  .content {
    @include box(100%, calc(100% - 50px));
    padding: 0 20px;

    .workbench {
      @include box(100%, 45px);
    }

    .recycle-container {
      @include box(100%, calc(100% - 45px));
      @include flex(column, flex-start, flex-start);
      align-content: flex-start;
      flex-wrap: wrap;
      overflow-x: scroll;
      padding: 10px;

      .recycle-item {
        width: 270px;
        margin-bottom: 5px;
        margin-right: 5px;
        font-size: 14px;
        padding: 8px 10px;
        border-radius: 4px;
        transition: box-shadow 0.1s;
        position: relative;

        &:hover {
          box-shadow: var(--bl-box-shadow-hover);
          .restore-btn,
          .download-btn {
            opacity: 1;
          }
        }

        .name {
          @include ellipsis();
          font-size: 13px;
        }

        .size {
          font-size: 12px;
          color: var(--bl-text-color-light);
          margin-top: 5px;
        }

        .download-btn {
          position: absolute;
          right: 5px;
          bottom: 3px;
          opacity: 0;
        }

        .restore-btn {
          position: absolute;
          right: 5px;
          top: 3px;
          opacity: 0;
        }
      }
    }
  }
}
</style>
