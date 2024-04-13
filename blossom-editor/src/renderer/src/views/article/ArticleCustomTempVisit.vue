<template>
  <div class="article-custom-temp-visit-root">
    <!-- 标题 -->
    <div class="info-title">
      <div class="iconbl bl-visit"></div>
      <div class="article-name">{{ articleInfo.articleName }}</div>
    </div>

    <div class="content">
      <div class="duration">有效时长(分钟) <el-input-number v-model="duration" :min="1" controls-position="right"></el-input-number></div>
      <div class="expire">将在 {{ expire }} 后失效。</div>
      <div class="btns">
        <el-button size="default" type="primary" @click="create">创建访问链接</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useServerStore } from '@renderer/stores/server'
import { articleTempKey, articleTempH } from '@renderer/api/blossom'
import { writeText } from '@renderer/assets/utils/electron'
import dayjs from 'dayjs'

const server = useServerStore()

const duration = ref(180)
const expire = computed(() => {
  return dayjs().add(duration.value, 'minutes').format('YYYY-MM-DD HH:mm')
})

const articleInfo = ref({
  articleId: '',
  articleName: ''
})

const create = () => {
  articleTempKey({ id: articleInfo.value.articleId, duration: duration.value }).then((resp) => {
    let url = server.serverUrl + articleTempH + resp.data
    writeText(url)
    ElMessage.info({ type: 'info', message: '已复制链接' })
    emits('created')
  })
}

const reload = (articleName: string, articleId: string) => {
  articleInfo.value = {
    articleId: articleId,
    articleName: articleName
  }
}

defineExpose({ reload })
const emits = defineEmits(['created'])
</script>

<style scoped lang="scss">
@import '@renderer/assets/styles/bl-dialog-info';

.article-custom-temp-visit-root {
  border-radius: 10px;
  @include box(100%, 100%);

  .article-name {
    width: 320px;
    @include font(16px, 500);
    @include ellipsis();
  }

  .content {
    padding: 10px;
    text-align: center;

    .duration {
    }

    .expire {
      @include font(14px, 300);
      width: 100%;
      padding: 10px;
      border-radius: 8px;
      background-color: var(--bl-preview-code-bg-color);
      margin: 13px 0;
    }

    .btns {
    }
  }
}
</style>
