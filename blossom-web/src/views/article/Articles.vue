<template>
  <div class="articles-root">
    <div class="mask" :style="maskStyle" @click="closeAll"></div>

    <div class="headmenu">
      <bl-row @click="handleMenu(!menuShow)">
        <div class="iconbl bl-model-line"></div>
        <div style="font-size: 0.8rem">文章列表</div>
      </bl-row>
      <bl-row just="flex-end" @click="handleToc(!tocShow)">
        <div style="font-size: 0.8rem">目录</div>
        <div class="iconbl bl-list-ordered"></div>
      </bl-row>
    </div>
    <div class="main">
      <div class="doc-tree-container" :style="menuStyle">
        <el-tree
          ref="DocTreeRef"
          class="doc-tree"
          :data="docTreeData"
          :highlight-current="true"
          :indent="14"
          :icon="ArrowRightBold"
          node-key="i"
          @nodeClick="clickCurDoc">
          <template #default="{ _node, data }">
            <div
              class="menu-item-wrapper"
              :style="{
                marginTop: data.p === '0' ? '5px' : '1px',
                marginBottom: data.p === '0' ? '5px' : '1px'
              }">
              <div :class="[data.t.includes('subject') && userStore.userParams.WEB_BLOG_SUBJECT_TITLE === '1' ? 'subject-title' : 'doc-title']">
                <div class="doc-name">
                  <img class="menu-icon-img" v-if="isShowImg(data)" :src="data.icon" />
                  <svg v-else-if="isShowSvg(data)" class="icon menu-icon" aria-hidden="true">
                    <use :xlink:href="'#' + data.icon"></use>
                  </svg>
                  <div class="name-wrapper" :style="{ maxWidth: isNotBlank(data.icon) ? 'calc(100% - 25px)' : '100%' }">
                    {{ data.n }}
                  </div>
                  <bl-tag v-for="tag in tags(data)" :bg-color="tag.bgColor" :icon="tag.icon">
                    {{ tag.content }}
                  </bl-tag>
                </div>
              </div>
            </div>
          </template>
        </el-tree>
      </div>

      <div class="doc-content-container" ref="PreviewRef" :style="{ fontSize: getFontSize() }">
        <div class="article-name" v-if="userStore.userParams.WEB_BLOG_SHOW_ARTICLE_NAME === '1'">{{ article.name }}</div>

        <el-watermark
          :font="{
            color: userStore.userParams.WEB_BLOG_WATERMARK_COLOR,
            fontSize: userStore.userParams.WEB_BLOG_WATERMARK_FONTSIZE,
            textBaseline: 'hanging'
          }"
          :content="article.id > 0 && userStore.userParams.WEB_BLOG_WATERMARK_ENABLED === '1' ? userStore.userParams.WEB_BLOG_WATERMARK_CONTENT : ''"
          :gap="[userStore.userParams.WEB_BLOG_WATERMARK_GAP, userStore.userParams.WEB_BLOG_WATERMARK_GAP]">
          <div class="bl-preview" :style="{ fontSize: getFontSize() }" v-html="article.html"></div>
        </el-watermark>
      </div>

      <div class="toc-container" :style="tocStyle">
        <div class="viewer-toc">
          <div v-if="article.id != 0" class="doc-info">
            <div class="doc-name" style="font-size: 15px">{{ article.name }}</div>
            <div class="doc-subtitle">
              <span class="iconbl bl-pen-line"></span> {{ article.words }} 字 | <span class="iconbl bl-read-line"></span> {{ article.uv }} |
              <span class="iconbl bl-like-line"></span> {{ article.likes }}
            </div>
            <div class="doc-subtitle">
              <span class="iconbl bl-a-clock3-line"></span> 公开
              {{ article.openTime }}
            </div>
            <div class="doc-subtitle">
              <span class="iconbl bl-a-clock3-line"></span> 修改
              {{ article.syncTime }}
            </div>
          </div>
          <div class="toc-title">目录</div>
          <div class="toc-content">
            <div v-for="toc in tocList" :key="toc.id" :class="['toc-item', 'link', toc.clazz]" @click="toScroll(toc.id)">
              {{ toc.content }}
            </div>
          </div>
        </div>
      </div>

      <div class="module-workbench" @click="showSetting">
        <el-icon color="#7b7b7ba9"><Setting /></el-icon>
      </div>
    </div>
  </div>

  <el-drawer
    v-model="isShowSetting"
    class="center-drawer"
    direction="btt"
    :close-on-click-modal="true"
    :with-header="true"
    :destroy-on-close="true"
    size="70px">
    <ArticleSetting></ArticleSetting>
  </el-drawer>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'
import { ref, onUnmounted, nextTick } from 'vue'
import { useUserStore } from '@/stores/user'
import { useLifecycle } from '@/scripts/lifecycle'
// element plus
import { ArrowRightBold, Setting } from '@element-plus/icons-vue'
// ts
import 'katex/dist/katex.min.css'
import { getFontSize } from './scripts/article-setting'
import { parseToc, toScroll, type Toc } from './scripts/doc-toc'
import { isShowImg, isShowSvg, tags } from './scripts/doc-tree-detail'
import { onHtmlEventDispatch } from './scripts/doc-content-event-dispatch'
import { articleInfoOpenApi, articleInfoApi, docTreeOpenApi, docTreeApi } from '@/api/blossom'
// utils
import { isNull, isNotNull, isNotBlank } from '@/assets/utils/obj'
// components
import ArticleSetting from './ArticleSetting.vue'

const userStore = useUserStore()
useLifecycle(
  () => {
    window.onHtmlEventDispatch = onHtmlEventDispatch
    getRouteQueryParams()
    window.addEventListener('resize', onresize)
    initStyle()
  },
  () => {
    getRouteQueryParams()
    window.addEventListener('resize', onresize)
    initStyle()
  }
)

onUnmounted(() => {
  window.removeEventListener('resize', onresize)
})

const DocTreeRef = ref()

/**
 * 路由中获取ID参数
 */
const getRouteQueryParams = () => {
  let articleId = route.query.articleId
  getDocTree(() => {
    nextTick(() => {
      DocTreeRef.value.setCurrentKey(docTreeCurrentId.value)
    })
  })
  if (isNotNull(articleId)) {
    docTreeCurrentId.value = articleId as string
    let treeParam: any = { ty: 3, i: articleId }
    clickCurDoc(treeParam)
  }
}

const route = useRoute()
// 文档菜单的加载动画
const docTreeLoading = ref(true)
// 文档菜单
const docTreeCurrentId = ref('')
const docTreeData = ref<DocTree[]>([])
// 当前选中的文章, 用于在编辑器中展示
const article = ref<DocInfo>({
  id: 0,
  pid: 0,
  name: '',
  tags: [],
  sort: 0,
  starStatus: 0,
  openStatus: 0,
  type: 3,
  html: `<div style="color:#E3E3E3;width:100%;height:300px;display:flex;justify-content: center;
    align-items: center;font-size:25px;">请在左侧菜单选择文章</div>`
})
const tocList = ref<Toc[]>([])
const defaultOpeneds = ref<string[]>([])
const PreviewRef = ref()

/**
 * 获取文档树状列表
 * 1. 初始化是全否调用
 * 2. 在 workbench 中点击按钮调用, 每个按钮是单选的
 */
const getDocTree = (callback?: () => void) => {
  docTreeLoading.value = true
  docTreeData.value = []
  defaultOpeneds.value = []

  const then = (resp: any) => {
    docTreeData.value = resp.data
    docTreeData.value.forEach((l1: DocTree) => {
      defaultOpeneds.value.push(l1.i.toString())
    })
    if (callback) callback()
  }

  if (userStore.isLogin) {
    docTreeApi()
      .then((resp) => then(resp))
      .finally(() => (docTreeLoading.value = false))
  } else {
    docTreeOpenApi()
      .then((resp) => then(resp))
      .finally(() => (docTreeLoading.value = false))
  }
}

/**
 * 获取文章信息
 * @param tree
 */
const clickCurDoc = async (tree: DocTree) => {
  // 如果选中的是文章, 则查询文章详情, 用于在编辑器中显示以及注入
  if (tree.ty == 3) {
    await getCurEditArticle(tree.i)
    window.history.replaceState('', '', '#/articles?articleId=' + tree.i)
    nextTick(() => {
      PreviewRef.value.scrollTo({ top: 0 })
      parseTocAsync(PreviewRef.value)
    })
  }
}

/**
 * 如果点击的是文章, 则查询文章信息和正文, 并在编辑器中显示.
 */
const getCurEditArticle = async (id: string) => {
  if (id === '-999') {
    article.value.html = `<div style="color:#C6C6C6;font-weight: 300;width:100%;height:300px;padding:150px 20px;font-size:25px;text-align:center">
      该博客所配置的 USER_ID 为<br/><span style="color:#e3a300; border-bottom: 5px solid #e3a300;border-radius:5px">${window.blconfig.DOMAIN.USER_ID}</span>
      </div>`
    return
  }

  const then = (resp: any) => {
    if (isNull(resp.data)) {
      return
    }
    article.value = resp.data
  }
  if (userStore.isLogin) {
    await articleInfoApi({ id: id, showToc: false, showMarkdown: false, showHtml: true }).then((resp) => then(resp))
  } else {
    await articleInfoOpenApi({ id: id, showToc: false, showMarkdown: false, showHtml: true }).then((resp) => then(resp))
  }
}

/**
 * 解析目录
 */
const parseTocAsync = async (ele: HTMLElement) => {
  tocList.value = parseToc(ele)
}

//#region ----------------------------------------< setting >----------------------------------------
const isShowSetting = ref(false)

const showSetting = () => {
  isShowSetting.value = true
}
//#endregion

//#region ----------------------------------------< 响应式样式 >--------------------------------------
const maskStyle = ref({ display: 'none' })
const menuShow = ref(false)
const menuStyle = ref({ display: 'none', opacity: 0 })
const tocShow = ref(false)
const tocStyle = ref({ display: 'none', opacity: 0 })

const handleMenu = (show: boolean) => {
  menuShow.value = show
  if (show) {
    maskStyle.value = { display: 'block' }
    menuStyle.value = { display: 'block', opacity: 0 }
    setTimeout(() => {
      menuStyle.value = { display: 'block', opacity: 1 }
    }, 1)
  }
  if (!show) {
    menuStyle.value = { display: 'block', opacity: 0 }
    setTimeout(() => {
      menuStyle.value = { display: 'none', opacity: 0 }
    }, 300)
  }
}

const handleToc = (show: boolean) => {
  tocShow.value = show
  if (show) {
    maskStyle.value = { display: 'block' }
    tocStyle.value = { display: 'block', opacity: 0 }
    setTimeout(() => {
      tocStyle.value = { display: 'block', opacity: 1 }
    }, 1)
  }
  if (!show) {
    tocStyle.value = { display: 'block', opacity: 0 }
    setTimeout(() => {
      tocStyle.value = { display: 'none', opacity: 0 }
    }, 300)
  }
}

const initStyle = () => {
  let width = document.body.clientWidth
  if (width > 1100) {
    menuStyle.value = { display: 'block', opacity: 1 }
  }
  if (width > 1100) {
    maskStyle.value = { display: 'none' }
    tocStyle.value = { display: 'block', opacity: 1 }
  }
}

const closeAll = () => {
  handleMenu(false)
  handleToc(false)
  maskStyle.value = { display: 'none' }
}

/**
 *
 */
const onresize = () => {
  let width = document.body.clientWidth
  if (width < 1100) {
    menuShow.value = false
    menuStyle.value = { display: 'none', opacity: 0 }
  }
  if (width > 1100) {
    maskStyle.value = { display: 'none' }
    menuStyle.value = { display: 'block', opacity: 1 }
  }

  if (width < 1100) {
    tocShow.value = false
    tocStyle.value = { display: 'none', opacity: 0 }
  }
  if (width > 1100) {
    maskStyle.value = { display: 'none' }
    tocStyle.value = { display: 'block', opacity: 1 }
  }
}

//#endregion
</script>

<style scoped lang="scss">
@import './styles/doc-content.scss';
@import './styles/doc-toc.scss';
@import './styles/doc-tree.scss';
@import './styles/doc-tree-detail.scss';

.articles-root {
  @include box(100vw, 100%);
  @include flex(column, flex-start, center);
  box-sizing: border-box;
  background: #ffffff;
  position: relative;

  .mask {
    position: absolute;
    @include box(100%, calc(100% + 10px));
    left: 0;
    top: -10px;
    z-index: 9998;
    background-color: #00000067;
  }

  .headmenu {
    display: none;
    color: #bfbfbf;

    .iconbl {
      font-size: 20px;
      padding: 0 10px;
      cursor: pointer;
    }
  }

  .main {
    @include box(100%, 100%);
    @include flex(row, center, center);
    padding: 10px;
    overflow: hidden;
  }

  // 屏幕宽度在 1100 以内时使用以下样式
  @media screen and (max-width: 1100px) {
    .headmenu {
      @include box(100vw, 40px);
      @include flex(row, space-between, center);
      border-bottom: 1px solid #e2e2e2;
      padding-bottom: 10px;
    }

    .main {
      @include box(100%, calc(100% - 40px));
      padding: 0;

      .doc-tree-container {
        height: calc(100% - 20px) !important;
        position: absolute;
        left: 10px;
        top: 10px;
        border-radius: 10px;
        background-color: #ffffff;
        padding-top: 10px;
        z-index: 9999;
        overflow: hidden;
      }

      .doc-content-container {
        padding: 0 10px;
        overflow-x: hidden;

        .bl-preview {
          font-size: 0.9rem;

          :deep(.markmap) {
            & > g {
              transform: translateX(0) translateY(50%) !important ;
            }
          }

          pre {
            margin: -10px !important;
          }
        }
      }
    }

    .toc-container {
      height: calc(100% - 20px) !important;
      position: absolute;
      right: 10px;
      top: 10px;
      background-color: #ffffff;
      border-radius: 10px;
      border: 0;
      z-index: 9999;
    }
  }
}
</style>
