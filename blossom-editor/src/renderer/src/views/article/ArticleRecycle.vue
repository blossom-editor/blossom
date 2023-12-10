<template>
  <div class="article-recycle-root">
    <!-- 标题 -->
    <div class="info-title">
      <div class="iconbl bl-delete-line"></div>
      文章回收站
    </div>

    <div class="content">
      <bl-row class="workbench">
        <el-button @click="getBackupList"> <span class="iconbl bl-refresh-line" style="margin-right: 7px"></span>刷新 </el-button>
        <el-input style="width: 150px; margin-left: 10px" placeholder="查询文章"></el-input>
        <div class="tips">回收站只保存 {{ userinfo.params.ARTICLE_RECYCLE_EXP_DAYS }} 天内的文章。</div>
      </bl-row>

      <div class="recycle-container">
        <div v-if="recycleList.length == 0" class="tips">回收站内无内容...</div>
        <div v-else v-for="recycle in recycleList" :key="recycle.id" class="recycle-item">
          <div class="name">{{ recycle.name }}</div>
          <div class="size">{{ recycle.delTime }}|{{ remainingDays(recycle.delTime) }}天后删除</div>
          <el-button class="restore-btn" @click="restore(recycle.id)"><span class="iconbl bl-a-cloudupload-line"></span></el-button>
          <el-button class="download-btn" @click=""><span class="iconbl bl-folder-download-line"></span></el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useUserStore } from '@renderer/stores/user'
import { articleRecycleListApi, articleRecycleRestoreApi } from '@renderer/api/blossom'
import dayjs from 'dayjs'

const userStore = useUserStore()
const { userinfo } = userStore

const deadLine = dayjs(new Date()).subtract(Number(userinfo.params.ARTICLE_RECYCLE_EXP_DAYS), 'day').toDate()

onMounted(() => {
  getBackupList()
})

const recycleList = ref<any[]>([])

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
</script>

<style scoped lang="scss">
@import '@renderer/assets/styles/bl-dialog-info';

.article-recycle-root {
  @include box(100%, 100%);

  .content {
    @include box(100%, calc(100% - 50px));
    padding: 0 20px;

    .tips {
      @include font(12px, 300);
      padding: 0 10px;
    }

    .workbench {
      @include box(100%, 45px);
    }

    .recycle-container {
      @include box(100%, calc(100% - 45px));
      @include flex(column, flex-start, flex-start);
      align-content: flex-start;
      flex-wrap: wrap;
      overflow-x: overlay;
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
