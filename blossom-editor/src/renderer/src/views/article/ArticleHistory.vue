<template>
  <div class="header">
    <AppHeader simple></AppHeader>
  </div>
  <div class="article-history-root">
    <div class="left bl-preview-toc-block">
      <div class="desc">
        <div class="toc-subtitle">《{{ article?.name }}》</div>
        <div class="toc-subtitle">
          <span class="iconbl bl-pen-line"></span> {{ article?.words }} 字 | <span class="iconbl bl-read-line"></span> {{ article?.uv }} |
          <span class="iconbl bl-like-line"></span> {{ article?.likes }}
        </div>
        <div class="toc-subtitle"><span class="iconbl bl-a-clock3-line"></span> 公开 {{ article?.openTime }}</div>
        <div class="toc-subtitle"><span class="iconbl bl-a-clock3-line"></span> 修改 {{ article?.updTime }}</div>
      </div>
      <el-divider style="margin: 10px 0"></el-divider>
      <div class="history-list">
        <div v-for="log in historyList" class="history-item">
          <div class="dt">{{ log.dt }}</div>
          <bl-row just="flex-end">
            <el-button text bg @click="getLogContent(log.i, log.dt)">查看</el-button>
          </bl-row>
        </div>
      </div>
    </div>
    <div class="main" :style="editorStyle">
      <div class="cur-log">当前为 [{{ historyDt }}] 的记录信息</div>
      <div class="history-editor-codemirror" ref="HistoryEditorRef"></div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { useRoute } from 'vue-router'
import { storeToRefs } from 'pinia'
import { ref, onMounted } from 'vue'
import { articleLogsApi, articleLogContentApi, articleInfoApi } from '@renderer/api/blossom'
import { CmWrapper } from './scripts/codemirror'
import { useConfigStore } from '@renderer/stores/config'
import AppHeader from '@renderer/components/AppHeader.vue'

const configStore = useConfigStore()
const { editorStyle } = storeToRefs(configStore)
const route = useRoute()

const article = ref<DocInfo>()
const historyList = ref<any>([])
const historyDt = ref('')
const HistoryEditorRef = ref()
let cmw: CmWrapper

const initEditor = (_doc?: string) => {
  cmw = new CmWrapper(
    CmWrapper.newEditor(
      CmWrapper.newState(
        () => {},
        () => {},
        () => {}
      ),
      HistoryEditorRef.value
    )
  )
}

const getLogs = (articleId: string | number) => {
  articleInfoApi({ id: articleId, showToc: false, showMarkdown: true, showHtml: false }).then((resp) => {
    article.value = resp.data
    document.title = `《${resp.data.name}》编辑历史`
  })
  articleLogsApi({ articleId: articleId }).then((resp) => {
    historyList.value = resp.data
  })
}

const getLogContent = (id: number, dt: string) => {
  historyDt.value = dt
  articleLogContentApi({ id: id }).then((resp) => {
    let maxLen = cmw.getDocLength()
    cmw.insert(0, maxLen, resp.data, 0, 0)
  })
}

onMounted(() => {
  initEditor()
  getLogs(route.query.articleId as string)
})
</script>

<style scoped lang="scss">
@import './styles/bl-preview-toc.scss';
@import './styles/article-index.scss';

.header {
  @include box(100%, 30px);
}
.article-history-root {
  @include box(100%, calc(100% - 30px));
  @include flex(row, flex-start, center);
  padding: 20px;

  .left {
    @include box(230px, 100%);
    border-right: 1px solid var(--el-border-color);

    .desc {
      @include box(100%, 80px);
    }

    .history-list {
      @include box(100%, calc(100% - 80px - 21px));
      padding: 10px 15px 10px 10px;
      overflow-y: scroll;

      .history-item {
        @include box(100%, 55px);
        border: 1px solid var(--el-border-color);
        margin-bottom: 10px;
        padding: 3px 5px;
        font-size: 13px;
        color: #ababab;
        border-radius: 5px;
        transition: box-shadow 0.1s;

        .dt {
          @include box(100%, 20px);
          text-align: center;
        }

        &:hover {
          box-shadow: var(--bl-box-shadow-hover);
        }
      }
    }
  }

  .main {
    @include box(calc(100% - 230px), 100%);

    .cur-log {
      @include box(100%, 30px);
      color: var(--bl-text-color);
      font-size: 15px;
      padding: 0 10px;
      font-family: inherit;
    }

    .history-editor-codemirror {
      @include box(100%, calc(100% - 30px));
      overflow: auto;
      border-top: 1px solid var(--el-border-color);
      border-right: 1px solid var(--el-border-color);
      border-bottom: 1px solid var(--el-border-color);
      font-size: inherit;
      font-family: inherit;

      :deep(*) {
        font-size: inherit;
        font-family: inherit;
      }

      :deep(.cm-line) {
        caret-color: var(--bl-editor-caret-color) !important;
      }

      :deep(.cm-cursor) {
        border-color: var(--bl-editor-caret-color) !important;
      }

      :deep(.cm-focused) {
        outline: none;
      }

      // 选中内容的颜色
      :deep(.cm-selectionBackground) {
        background: var(--bl-editor-selection-bg-color);
      }
    }
  }
}
</style>
