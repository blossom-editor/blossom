<template>
  <div class="header">
    <AppHeader simple></AppHeader>
  </div>
  <div class="article-view-window-root">
    <div class="preview bl-preview" :style="editorStyle" v-html="article?.html" ref="WindowPreviewRef" style="margin-right: 5px"></div>
    <el-backtop target=".preview" :right="350" :bottom="50">
      <div class="iconbl bl-a-doubleonline-line backtop"></div>
    </el-backtop>

    <!-- the toc -->
    <div class="bl-preview-toc-block">
      <div class="doc-info">
        <div class="doc-name">《{{ article?.name }}》</div>
        <div class="doc-subtitle">
          <span class="iconbl bl-pen-line"></span> {{ article?.words }} 字 | <span class="iconbl bl-read-line"></span> {{ article?.uv }} |
          <span class="iconbl bl-like-line"></span> {{ article?.likes }}
        </div>
        <div class="doc-subtitle"><span class="iconbl bl-a-clock3-line"></span> 公开 {{ article?.openTime }}</div>
        <div class="doc-subtitle"><span class="iconbl bl-a-clock3-line"></span> 修改 {{ article?.updTime }}</div>
        <div class="toc-title">目录</div>
      </div>
      <div class="toc-content">
        <div v-for="toc in tocs" :key="toc.id" :class="['toc-item', toc.clazz]" @click="toScroll(toc.id)">
          {{ toc.content }}
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { storeToRefs } from 'pinia'
import { ref, onMounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { articleInfoApi } from '@renderer/api/blossom'
import { useConfigStore } from '@renderer/stores/config'
import { parseTocAsync } from './scripts/article'
import type { Toc } from './scripts/article'
import AppHeader from '@renderer/components/AppHeader.vue'

const configStore = useConfigStore()
const { editorStyle } = storeToRefs(configStore)

const route = useRoute()
const article = ref<DocInfo>()
const tocs = ref<Toc[]>([])
const WindowPreviewRef = ref()

/**
 * 跳转至指定ID位置,ID为 标题级别-标题内容
 * @param level 标题级别
 * @param content 标题内容
 */
const toScroll = (id: string) => {
  let elm: HTMLElement = document.getElementById(id) as HTMLElement
  ;(elm.parentNode as Element).scrollTop = elm.offsetTop - 40
}

const initPreview = (articleId: string) => {
  articleInfoApi({ id: articleId, showToc: false, showMarkdown: false, showHtml: true }).then((resp) => {
    article.value = resp.data
    document.title = `《${resp.data.name}》`
    nextTick(() => initToc())
  })
}

const initToc = () => {
  parseTocAsync(WindowPreviewRef.value).then((toc) => {
    tocs.value = toc
  })
}

onMounted(() => {
  initPreview(route.query.articleId as string)
})
</script>
<style scoped lang="scss">
@import './styles/bl-preview-toc.scss';
@import './styles/article-backtop.scss';

.header {
  @include box(100%, 30px);
}

.article-view-window-root {
  @include box(100%, calc(100% - 30px));
  @include flex(row, center, center);

  .preview {
    @include box(100%, 100%);
    font-size: 15px;
    padding: 30px;
    overflow-y: scroll;
    overflow-x: hidden;
    line-height: 23px;

    :deep(*) {
      font-size: inherit;
      font-family: inherit;
    }

    :deep(.katex > *) {
      font-size: 1.2em !important;
      font-family: 'KaTeX_Size1', sans-serif !important;
    }
  }
}
</style>
