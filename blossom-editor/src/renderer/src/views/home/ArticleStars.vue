<template>
  <bl-row class="container-name">文章收藏</bl-row>
  <bl-row class="container-sub-name" just="space-between">
    Article Star
    <span v-if="configViewStyle.isHomeStarCard" class="iconbl bl-array-line container-operator" @click="showStarCard(false)" />
    <span v-else class="iconbl bl-article-line container-operator" @click="showStarCard(true)" />
  </bl-row>
  <div class="article-stars-root">
    <div v-if="isEmpty(articles)" class="placeholder">无收藏文章</div>
    <div
      v-else
      v-for="article in articles"
      :key="article.id"
      :class="[configViewStyle.isHomeStarCard ? 'star-card' : 'star-list', configViewStyle.webCollectExpand ? '' : 'close']">
      <div class="counterfoil">
        <div :class="['iconbl', article.openStatus == 1 ? 'bl-cloud-line' : 'bl-a-linkspread-line']" @click="toWebOrRoute(article)"></div>
      </div>
      <div class="content" @click="toRoute(article.id)">
        <div class="name">{{ article.name }}</div>
        <div class="infos">
          <span style="padding-left: 5px">U{{ article.uv }}</span>
          <span style="padding-right: 5px">W{{ article.words }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import router from '@renderer/router'
import { ref, nextTick } from 'vue'
import { useConfigStore } from '@renderer/stores/config'
import type { ViewStyle } from '@renderer/stores/config'
import { articleListApi } from '@renderer/api/blossom'
import { useUserStore } from '@renderer/stores/user'
import { openExtenal } from '@renderer/assets/utils/electron'
import { isEmpty } from 'lodash'
import { useLifecycle } from '@renderer/scripts/lifecycle'

const configStore = useConfigStore()
const configViewStyle = ref<ViewStyle>(configStore.viewStyle)
useLifecycle(
  () => getArticleListApi(),
  () => getArticleListApi()
)

const userStore = useUserStore()
const articles = ref<any>([])

const getArticleListApi = () => {
  articles.value = []
  articleListApi({ starStatus: 1 }).then((resp) => {
    articles.value = resp.data
  })
}

const toRoute = (articleId: number) => {
  router.push({ path: '/articleIndex', query: { articleId: articleId } })
}

const toWebOrRoute = (article: any) => {
  if (article.openStatus === 1) {
    openExtenal(userStore.userinfo.userParams.WEB_ARTICLE_URL + article.id)
  } else {
    toRoute(article.id)
  }
}

const showStarCard = (card: boolean) => {
  configViewStyle.value.isHomeStarCard = card
  configStore.setViewStyle(configViewStyle.value)
}

const reload = () => {
  nextTick(() => {
    getArticleListApi()
  })
}
defineExpose({ reload })
</script>

<style scoped lang="scss">
@import './styles/container.scss';

.article-stars-root {
  @include box(100%, 100%);
  @include flex(row, flex-start, flex-start);
  flex-wrap: wrap;
  align-content: flex-start;
  overflow: hidden;
  overflow-y: scroll;
  padding-top: 10px;
}

.placeholder {
  @include font(15px, 300);
  padding: 20px 0 0 20px;
  color: var(--bl-text-color-light);
}

.star-card {
  @include box(210px, 50px, 210px, 210px);
  filter: var(--bl-drop-shadow-star);
  position: relative;
  margin: 10px 10px;

  &:hover {
    .counterfoil {
      transform: translateX(-5px);
    }

    .content {
      transform: translateX(5px);
    }
  }

  .counterfoil,
  .content {
    @include absolute(0);
    @include themeColor(#ffffff, #c9c9c9);
    @include themeBg(var(--el-color-primary-light-4), var(--el-color-primary-light-6));
    transition:
      transform-origin 0.3s,
      transform 0.3s;
    z-index: 2;
    cursor: pointer;
  }

  // 左侧存根
  .counterfoil {
    @include flex(row, center, center);
    @include box(43px, 100%);
    border-top-left-radius: 8px;
    border-bottom-left-radius: 8px;

    &:hover {
      transform: rotate(-10deg);
      transform-origin: 100% 100%;
    }

    .iconbl {
      font-size: 26px;
      &:hover {
        @include themeColor(#ffffff, #c9c9c9);
      }
    }

    .open-article {
      @include absolute('', '', 0px, 17px);
      font-size: 13px;
    }
  }

  //
  .content {
    @include flex(row, flex-start, center);
    @include box(167px, 100%);
    padding: 5px 5px 5px 5px;
    font-size: 12px;
    border-top-right-radius: 8px;
    border-bottom-right-radius: 8px;
    right: 0;

    .name {
      @include box(150px, 25px);
      @include absolute(7px, '', '', 8px);
      line-height: 13px;
      word-break: break-all;
      overflow: hidden;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
    }

    .infos {
      @include box(140px, 12px);
      @include flex(row, space-between, center);
      @include font(10px, 300);
      @include absolute('', '', 2px, 12px);
      border-top: 1px dashed #f1f1f1;
      padding-top: 4px;
    }
  }
}

.star-card.close {
  margin: 10px 5px;
}

.star-list {
  width: 210px;
  max-width: 210px;
  margin: 5px 10px 0px 10px;
  padding: 2px 3px 2px 10px;
  border-radius: 3px;
  transition: background-color 0.2s;
  cursor: pointer;

  &:hover {
    @include themeBg(#f5f5f5, #171717);

    .content {
      .name {
        color: var(--bl-text-color);
      }
    }
  }

  .counterfoil {
    display: none;
  }

  .content {
    @include box(100%, 100%);
    @include flex(column, flex-start, flex-start);

    .name {
      @include ellipsis();
      width: 100%;
      font-size: 13px;
      color: var(--bl-text-color-light);
      transition: color 0.2s;
    }

    .infos {
      display: none;
    }
  }
}

.star-list.close {
  margin: 5px 5px 0px 5px;
}
</style>
