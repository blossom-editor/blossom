<template>
  <el-scrollbar :onresize="false">
    <!-- <div class="article-stars-root"> -->
    <div v-if="isEmpty(articles)" class="placeholder">无收藏的文章</div>
    <div v-else class="star-item" v-for="article in articles" :key="article.id">
      <div class="counterfoil" @click="toWebview(article)">
        <div class="iconbl bl-a-barcode-line open-barcode" v-if="article.openStatus == 1"></div>
        <div class="iconbl bl-a-linkspread-line"></div>
      </div>
      <div class="content" @click="toRoute(article.id)">
        <div class="name">{{ article.name }}</div>
        <div class="infos">
          <span>U{{ article.uv }}</span>
          <span>L{{ article.likes }}</span>
          <span>W{{ article.words }}</span>
        </div>
      </div>
    </div>
    <div style="width: 100%; height: 10px"></div>
    <!-- </div> -->
  </el-scrollbar>
</template>

<!-- 
文章收藏
 -->
<script setup lang="ts">
import router from '@renderer/router'
import { nextTick, onActivated, ref } from 'vue'
import { articleListApi } from '@renderer/api/blossom'
import { useUserStore } from '@renderer/stores/user'
import { openExtenal } from '@renderer/assets/utils/electron'
import { isEmpty } from 'lodash'

onActivated(() => {
  getArticleListApi()
})

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

const toWebview = (article: any) => {
  if (article.openStatus === 1) {
    openExtenal(userStore.userinfo.params.WEB_ARTICLE_URL + article.id)
  }
}

const reload = () => {
  nextTick(() => {
    getArticleListApi()
  })
}
defineExpose({ reload })
</script>

<style scoped lang="scss">
.el-scrollbar {
  width: 100%;
  --el-scrollbar-opacity: 1;
  --el-scrollbar-hover-opacity: 1;
  --el-scrollbar-bg-color: var(--el-color-primary-light-8);
  --el-scrollbar-hover-bg-color: var(--el-color-primary-light-6);

  :deep(.el-scrollbar__bar) {
    width: 5px;
    right: 0px;
  }

  :deep(.el-scrollbar__view) {
    @include box(100%, 100%);
    @include flex(row, flex-start, flex-start);
    flex-wrap: wrap;
    align-content: flex-start;
  }
}

.placeholder {
  padding: 20px 0 0 20px;
  color: var(--bl-text-color-light);
}

.star-item {
  @include box(210px, 70px, 210px, 210px);
  position: relative;
  margin: 10px 10px;
  filter: drop-shadow(2px 2px 2px rgb(180, 180, 180));

  [class='dark'] & {
    filter: drop-shadow(2px 2px 2px rgb(0, 0, 0));
  }

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
    @include flex(row, center, center);
    @include absolute(0);
    color: #fff;
    transition: 0.3s;
    cursor: pointer;
    z-index: 2;

    &::before {
      @include box(100%, 100%);
      @include absolute('', 0, '', 0);
      content: '';
      background: var(--el-color-primary-light-3);
      border-radius: 10px;
      -webkit-mask-composite: source-in;
      mask-composite: source-in;
      z-index: -1;
    }
  }

  // 左侧存根
  .counterfoil {
    @include box(53px, 100%);
    padding: 5px;

    &::before {
      -webkit-mask:
        radial-gradient(circle at 10px 10px, transparent 10px, #fff 0) 43px -10px / 120%,
        radial-gradient(circle at 1px 1px, transparent 2px, #fff 0) 51px 0px / 110% 8%;
    }

    &:hover {
      transform: rotate(-10deg);
      transform-origin: 100% 100%;
    }

    .iconbl {
      font-size: 30px;
    }

    .bl-shuxingtiaoma {
      @include absolute('', '', -3px, 14px);
      font-size: 22px;
      transform: rotate(90deg);
    }

    .bl-tiaoxingma {
      position: absolute;
      bottom: -15px;
      left: 8px;
      font-size: 40px;
    }

    .open-barcode {
      @include absolute('', '', 0px, 17px);
      font-size: 15px;
    }
  }

  //
  .content {
    @include box(157px, 100%);
    right: 0;
    padding: 5px 5px 5px 10px;
    font-size: 12px;

    &::before {
      -webkit-mask:
        radial-gradient(circle at 10px 10px, transparent 10px, #000 0) -10px -10px / 120%,
        // radial-gradient(circle at 10px 10px, transparent 10px, #fff 0) -10px -10px,
        radial-gradient(circle at 1px 1px, transparent 2px, #000 0) -1px 0px / 110% 8%;
    }

    .name {
      @include box(100%, calc(100% - 12px));
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
</style>
