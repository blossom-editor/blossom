<template>
  <div class="article-view-window-root">
    <!-- the toc -->
    <div class="bl-preview-toc-block">
      <div class="toc-subtitle">《{{ article?.name }}》</div>
      <div class="toc-subtitle">
        <span class="iconbl bl-pen-line"></span> {{ article?.words }} 字 |
        <span class="iconbl bl-read-line"></span> {{ article?.uv }} |
        <span class="iconbl bl-like-line"></span> {{ article?.likes }}
      </div>
      <div class="toc-subtitle">
        <span class="iconbl bl-a-clock3-line"></span> 公开 {{ article?.openTime }}
      </div>
      <div class="toc-subtitle">
        <span class="iconbl bl-a-clock3-line"></span> 修改 {{ article?.updTime }}
      </div>
      <div class="toc-title">目录</div>
      <div class="toc-content">
        <div v-for="toc in tocList" :key="toc.index" :class="[toc.clazz]" @click="toScroll(toc.level, toc.content)">
          {{ toc.content }}
        </div>
      </div>
    </div>

    <div class="preview bl-preview" v-html="article?.html"></div>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { articleInfoApi } from '@renderer/api/blossom'

const route = useRoute();
const article = ref<DocInfo>()
const tocList = ref<any>([])

/**
 * 跳转至指定ID位置,ID为 标题级别-标题内容
 * @param level 标题级别
 * @param content 标题内容
 */
const toScroll = (level: number, content: string) => {
  let id = level + '-' + content
  let elm: HTMLElement = document.getElementById(id) as HTMLElement
  (elm.parentNode as Element).scrollTop = elm.offsetTop
}

const initPreview = (articleId: string) => {
  articleInfoApi({ id: articleId, showToc: true, showMarkdown: false, showHtml: true }).then(resp => {
    article.value = resp.data
    tocList.value = JSON.parse(resp.data.toc)
  })
}

onMounted(() => {
  initPreview(route.query.articleId as string)
})
</script>
<style scoped lang=scss>
.article-view-window-root {
  @include box(100%, 100%);
  @include flex(row, center, center);

  .preview {
    @include box(100%, 100%);
    font-size: 15px;
    padding: 30px;
    overflow-y: scroll;
  }

}
</style>