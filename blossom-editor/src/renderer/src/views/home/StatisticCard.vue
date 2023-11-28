<template>
  <div>
    <div class="statistic-article-root">
      <div class="statistic">
        <div class="main-stat"><span style="font-size: 20px">A</span>{{ formartNumber(article.articleCount) }}</div>
        <div class="sub-stat"><span class="iconbl bl-pen-line"></span> Words {{ formartNumber(article.articleWords) }}</div>
      </div>
      <div class="iconbl bl-a-texteditorhighlightcolor-line icon"></div>
      <div class="iconbl bl-a-texteditorhighlightcolor-line icon-shadow"></div>
    </div>

    <div class="statistic-picture-root">
      <div class="statistic">
        <div class="main-stat"><span style="font-size: 20px">P</span>{{ formartNumber(picture.pictureCount) }}</div>
        <div class="sub-stat"><span class="iconbl bl-a-cloudstorage-line"></span> Size {{ formatFileSize(picture.pictureSize) }}</div>
      </div>
      <div class="iconbl bl-image--line icon"></div>
      <div class="iconbl bl-image--line icon-shadow"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { articleWordsApi, pictureStatApi } from '@renderer/api/blossom'
import { ref } from 'vue'
import { formatFileSize, formartNumber } from '@renderer/assets/utils/util'
import { useLifecycle } from '@renderer/scripts/lifecycle'

useLifecycle(
  () => {
    getArticleWords()
    getPictureStat()
  },
  () => {
    getArticleWords()
    getPictureStat()
  }
)

let article = ref({ articleCount: 0, articleWords: 0 })
let picture = ref({ pictureCount: 0, pictureSize: 0 })

const getArticleWords = () => {
  articleWordsApi().then((resp) => {
    article.value = resp.data
  })
}

const getPictureStat = () => {
  pictureStatApi().then((resp) => {
    picture.value = resp.data
  })
}
</script>

<style scoped lang="scss">
.statistic-article-root,
.statistic-picture-root {
  @include box(200px, 95px);
  @include flex(row, flex-start, center);
  @include themeColor(#727272, #929292);
  text-shadow: var(--bl-text-shadow);
  border: 3px solid var(--el-color-primary-light-7);
  padding: 10px 0;
  margin-top: 20px;
  border-radius: 10px;
  overflow: hidden;
  position: relative;
  transition: box-shadow 0.3s;

  &:hover {
    @include themeShadow(2px 3px 7px 0 rgba(58, 47, 47, 0.5), 3px 3px 10px 0 rgba(0, 0, 0, 1));

    .icon-shadow {
      transform: translateX(-20px) translateY(10px) scale(150%);
    }
  }

  &::before {
    content: '';
    width: 100%;
    height: 100%;
    position: absolute;
  }

  .statistic {
    @include box(calc(100% - 70px), 100%);
    text-align: right;
    z-index: 2;

    .main-stat {
      @include box(100%, 53px);
      @include font(42px, 700);
    }

    .sub-stat {
      @include box(100%, 20px);
      @include font(12px, 700);

      .iconbl {
        font-size: 13px;
      }
    }
  }

  .icon {
    font-size: 80px;
    font-weight: 500;
    z-index: 2;
  }

  .icon-shadow {
    @include font(100px, 500);
    color: var(--el-color-primary-light-5);
    position: absolute;
    right: -30px;
    bottom: 0px;
    z-index: 1;
    transition: transform 0.3s;
    filter: blur(3px);
  }
}
</style>
