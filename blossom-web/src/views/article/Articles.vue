<template>
  <div class="articles-root">
    <div class="mask" :style="maskStyle" @click="closeAll"></div>

    <div class="headmenu">
      <bl-row @click="handleMenu(!menuShow)">
        <div class="iconbl bl-model-line"></div>
        <div style="font-size: 0.8rem">菜单</div>
      </bl-row>
      <bl-row just="flex-end" @click="handleToc(!tocShow)">
        <div style="font-size: 0.8rem">目录</div>
        <div class="iconbl bl-list-ordered"></div>
      </bl-row>
    </div>
    <div class="main">
      <div class="menu" :style="menuStyle">
        <el-menu
          v-if="docTreeData != undefined && docTreeData.length > 0"
          class="doc-trees"
          :default-active="docTreeDefaultActive"
          :default-openeds="defaultOpeneds"
          :unique-opened="true">
          <!-- ================================================ L1 ================================================ -->
          <div v-for="L1 in docTreeData" :key="L1.i" class="menu-level-one">
            <el-menu-item v-if="isEmpty(L1.children)" :index="L1.i">
              <template #title>
                <div class="menu-item-wrapper" @click="clickCurDoc(L1)">
                  <DocTitle :trees="L1" :level="1" />
                </div>
              </template>
            </el-menu-item>

            <el-sub-menu v-else :expand-open-icon="ArrowDownBold" :expand-close-icon="ArrowRightBold" :index="L1.i">
              <template #title>
                <div class="menu-item-wrapper">
                  <DocTitle :trees="L1" :level="1" style="font-size: 15px" />
                </div>
              </template>

              <!-- ================================================ L2 ================================================ -->
              <div v-for="L2 in L1.children" :key="L2.i">
                <el-menu-item v-if="isEmpty(L2.children)" :index="L2.i">
                  <template #title>
                    <div class="menu-item-wrapper" @click="clickCurDoc(L2)">
                      <DocTitle :trees="L2" :level="2" />
                    </div>
                  </template>
                </el-menu-item>

                <el-sub-menu v-else :expand-open-icon="ArrowDownBold" :expand-close-icon="ArrowRightBold" :index="L2.i">
                  <template #title>
                    <div class="menu-item-wrapper">
                      <DocTitle :trees="L2" :level="2" />
                    </div>
                  </template>

                  <!-- ================================================ L3 ================================================ -->
                  <div v-for="L3 in L2.children" :key="L3.i">
                    <el-menu-item v-if="isEmpty(L3.children)" :index="L3.i">
                      <template #title>
                        <div class="menu-item-wrapper" @click="clickCurDoc(L3)">
                          <DocTitle :trees="L3" :level="3" />
                        </div>
                      </template>
                    </el-menu-item>

                    <el-sub-menu v-else :expand-open-icon="ArrowDownBold" :expand-close-icon="ArrowRightBold" :index="L3.i">
                      <template #title>
                        <div class="menu-item-wrapper">
                          <DocTitle :trees="L3" :level="3" />
                        </div>
                      </template>

                      <!-- ================================================ L4 ================================================ -->
                      <div v-for="L4 in L3.children" :key="L4.i">
                        <el-menu-item v-if="isEmpty(L4.children)" :index="L4.i">
                          <template #title>
                            <div class="menu-item-wrapper" @click="clickCurDoc(L4)">
                              <DocTitle :trees="L4" :level="4" />
                            </div>
                          </template>
                        </el-menu-item>
                      </div>
                    </el-sub-menu>
                  </div>
                </el-sub-menu>
              </div>
            </el-sub-menu>
          </div>
        </el-menu>
      </div>

      <div class="article" ref="PreviewRef">
        <div class="bl-preview" v-html="article.html"></div>
      </div>

      <div class="toc-container" :style="tocStyle">
        <div class="viewer-toc">
          <div v-if="article.id != 0">
            <div class="toc-subtitle" style="font-size: 15px">《{{ article.name }}》</div>
            <div class="toc-subtitle">
              <span class="iconbl bl-pen-line"></span> {{ article.words }} 字 | <span class="iconbl bl-read-line"></span> {{ article.uv }} |
              <span class="iconbl bl-like-line"></span> {{ article.likes }}
            </div>
            <div class="toc-subtitle">
              <span class="iconbl bl-a-clock3-line"></span> 公开
              {{ article.openTime }}
            </div>
            <div class="toc-subtitle">
              <span class="iconbl bl-a-clock3-line"></span> 修改
              {{ article.syncTime }}
            </div>
          </div>
          <div class="toc-title">目录</div>
          <div class="toc-content">
            <div v-for="toc in tocList" :key="toc.index" :class="[toc.clazz]" @click="toScroll(toc.level, toc.content)">
              {{ toc.content }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'
import { ref, onUnmounted, nextTick } from 'vue'
import { ArrowDownBold, ArrowRightBold } from '@element-plus/icons-vue'
import { articleInfoOpenApi, articleInfoApi, docTreeOpenApi, docTreeApi } from '@/api/blossom'
import { useUserStore } from '@/stores/user'
import { isNull, isEmpty, isNotNull } from '@/assets/utils/obj'
import DocTitle from './DocTitle.vue'
import { useLifecycle } from '@/scripts/lifecycle'
import 'katex/dist/katex.min.css'

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

/**
 * 路由中获取ID参数
 */
const getRouteQueryParams = () => {
  let articleId = route.query.articleId
  getDocTree()
  if (isNotNull(articleId)) {
    docTreeDefaultActive.value = articleId as string
    let treeParam: any = { ty: 3, i: articleId }
    clickCurDoc(treeParam)
  }
}

const route = useRoute()
// 文档菜单的加载动画
const docTreeLoading = ref(true)
// 文档菜单
const docTreeDefaultActive = ref('')
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
const tocList = ref<any>([])
const defaultOpeneds = ref<string[]>([])
const PreviewRef = ref()

/**
 * 获取文档树状列表
 * 1. 初始化是全否调用
 * 2. 在 workbench 中点击按钮调用, 每个按钮是单选的
 */
const getDocTree = () => {
  docTreeLoading.value = true
  docTreeData.value = []
  defaultOpeneds.value = []

  const then = (resp: any) => {
    docTreeData.value = resp.data
    docTreeData.value.forEach((l1: DocTree) => {
      defaultOpeneds.value.push(l1.i.toString())
    })
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

const clickCurDoc = async (tree: DocTree) => {
  // 如果选中的是文章, 则查询文章详情, 用于在编辑器中显示以及注入
  if (tree.ty == 3) {
    await getCurEditArticle(tree.i)
    window.history.replaceState('', '', '#/articles?articleId=' + tree.i)
    nextTick(() => {
      PreviewRef.value.scrollTo({ top: 0 })
    })
  }
}

/**
 * 如果点击的是文章, 则查询文章信息和正文, 并在编辑器中显示.
 */
const getCurEditArticle = async (id: number) => {
  if (id == -123) {
    article.value.html = `<div style="color:#C6C6C6;font-weight: 300;width:100%;height:300px;padding:0 20px;display:flex;justify-content: center;
    align-items: center;text-align:center;font-size:25px;">当您看到这句话, 证明博客地址配置正确</div>`
    return
  }

  const then = (resp: any) => {
    if (isNull(resp.data)) return
    article.value = resp.data
    tocList.value = JSON.parse(resp.data.toc)
  }
  if (userStore.isLogin) {
    await articleInfoApi({ id: id, showToc: true, showMarkdown: false, showHtml: true }).then((resp) => then(resp))
  } else {
    await articleInfoOpenApi({ id: id, showToc: true, showMarkdown: false, showHtml: true }).then((resp) => then(resp))
  }
}

const toScroll = (level: number, content: string) => {
  let id = level + '-' + content
  let elm = document.getElementById(id)
  elm?.scrollIntoView(true)
}

/**
 * 监听 html 的内联事件
 */
type ArticleHtmlEvent = 'copyPreCode' | 'showArticleReferenceView'
const onHtmlEventDispatch = (_t: any, _ty: any, _event: any, type: ArticleHtmlEvent, data: any) => {
  if (type === 'copyPreCode') {
    let code = document.getElementById(data)
    if (code) {
      // navigator.clipboard.writeText(code.innerText)
      // navigator clipboard 需要https等安全上下文
      if (navigator.clipboard && window.isSecureContext) {
        // navigator clipboard 向剪贴板写文本
        return navigator.clipboard.writeText(code.innerText)
      } else {
        // 创建text area
        let textArea = document.createElement('textarea')
        textArea.value = code.innerText
        // 使text area不在viewport，同时设置不可见
        textArea.style.position = 'absolute'
        textArea.style.opacity = '0'
        textArea.style.left = '-999999px'
        textArea.style.top = '-999999px'
        document.body.appendChild(textArea)
        textArea.focus()
        textArea.select()
        return new Promise<void>((res, rej) => {
          // 执行复制命令并移除文本框
          document.execCommand('copy') ? res() : rej()
          textArea.remove()
        })
      }
    }
  }
}
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

    .menu {
      @include box(240px, 100%, 240px, 240px);
      border-right: 1px solid var(--el-border-color);
      font-weight: 200;
      transition: 0.1s;
      &:hover {
        :deep(.folder-level-line) {
          opacity: 1;
        }
      }

      .menu-level-one {
        margin-top: 8px;
        border-bottom: 1px solid #f0f0f0;
        padding-bottom: 8px;

        &:first-child {
          margin-top: 0px;
        }
      }

      .menu-item-wrapper {
        width: 100%;
      }

      .doc-trees {
        @include box(100%, 100%);
        font-weight: 200;
        padding-right: 0;
        border: 0;
        overflow-y: scroll;
        // padding-right: 6px;
        // 基础的 padding
        --el-menu-base-level-padding: 25px;
        // 每级别的的缩进
        --el-menu-level-padding: 10px;
        // ------------------- sub-item 的样式
        // sub-item 菜单的高度
        --el-menu-sub-item-height: 25px;

        // ------------------- item 的样式
        // 菜单每个 item 的高度
        --el-menu-item-height: 25px;
        // item 的 字体大小
        --el-menu-item-font-size: 13px;
        --el-color-primary-light-9: #ffffff00;
        --el-menu-hover-bg-color: #ffffff00;

        :deep(.el-menu) {
          transition: 0.1s !important;
        }

        :deep(.el-sub-menu) {
          .el-sub-menu__title {
            height: auto;
            min-height: 25px;
            padding-right: 0;
            overflow: hidden;
            white-space: nowrap;
            text-overflow: ellipsis;
            border-radius: 5px;

            .el-sub-menu__icon-arrow {
              right: calc(220px - var(--el-menu-level) * 14px);
              color: #b3b3b3;
              width: 0.8em;
              height: 0.8em;
            }
          }
        }

        :deep(.el-menu-item) {
          --el-menu-hover-bg-color: #ffffff00 !important;
          height: auto;
          min-height: 25px;
          padding-right: 0;
          border-radius: 5px;
          overflow: hidden;
          white-space: nowrap;
          text-overflow: ellipsis;
          transition: 0.1s;
        }

        :deep(.el-menu-item.is-active) {
          text-shadow: 0px 4px 5px rgba(107, 104, 104, 1);
          background: linear-gradient(90deg, #3d454d00 0%, #c3c3c3 40%, #c3c3c3 60%, #3d454d00 100%);
          color: #ffffff;
          font-weight: 700;
        }

        :deep(.el-badge__content) {
          top: 7px;
          transform: translateY(-50%) translateX(100%) scale(0.8);
        }
      }
    }

    .toc-container {
      @include box(270px, 100%, 270px, 270px);
      border-left: 1px solid #eeeeee;
      overflow: auto;
      transition: 0.3s;

      .viewer-toc {
        @include box(100%, 100%);
        color: #5e5e5e;
        padding: 10px;
        z-index: 1000;
        transition: 0.3s;

        .toc-title {
          @include box(100%, 40px);
          @include font(30px, 700);
          line-height: 40px;
          margin-top: 10px;
          padding-top: 10px;
          border-top: 3px solid #bcbcbc;
        }

        .toc-subtitle {
          width: 100%;
          @include flex(row, flex-start, center);
          @include font(12px);
          color: #ababab;
          overflow: hidden;
          white-space: nowrap;
          text-overflow: ellipsis;
          white-space: pre;
          margin-top: 5px;
        }

        .toc-content {
          @include font(14px);
          width: 100%;
          overflow-y: auto;
          margin-top: 10px;
          padding-top: 10px;

          .toc-1,
          .toc-2,
          .toc-3,
          .toc-4,
          .toc-5,
          .toc-6 {
            cursor: pointer;
            // overflow: hidden;
            // white-space: nowrap;
            // text-overflow: ellipsis;
            // white-space: pre;

            &:hover {
              font-weight: bold;
            }
          }

          .toc-1 {
            font-size: 1.1em;
            border-top: 2px solid #eeeeee;
            margin-top: 5px;
            padding-top: 5px;

            &:first-child {
              margin: 0;
              padding: 0;
              border: 0;
            }
          }

          .toc-2 {
            padding-left: 10px;
          }

          .toc-3 {
            padding-left: 20px;
          }

          .toc-4 {
            padding-left: 30px;
          }

          .toc-5 {
            padding-left: 40px;
          }

          .toc-6 {
            padding-left: 50px;
          }
        }
      }
    }

    .article {
      height: 100%;
      width: 1260px;
      max-width: 1260px;
      overflow-y: overlay;
      padding: 0 30px;

      .bl-preview {
        $borderRadius: 4px;
        color: #4b4b4b;
        font-size: 0.9rem;
        // font-size: 14px;
        line-height: 1.6;

        :deep(.katex > *) {
          font-size: 1.2em !important;
          // font-family: 'KaTeX_Size1', sans-serif !important;
          // font-size: 1.3em !important;
          // font-family: 'KaTeX_Math', sans-serif !important;
        }

        ::-webkit-scrollbar {
          /* 滚动条里槽的背景色 */
          background-color: transparent;
        }

        /* 定义滑块 内阴影+圆角 */
        ::-webkit-scrollbar-thumb {
          border-radius: 4px;
          background-color: #717171;
        }

        :deep(a) {
          color: var(--el-color-primary);
          font-weight: bold;
        }

        :deep(a.inner-link) {
          border-bottom: 2px dashed var(--el-color-primary);
          box-sizing: border-box;
          padding: 0 4px;
          text-decoration: none;
        }

        :deep(img) {
          border-radius: $borderRadius;
          max-width: 100%;
        }

        // 列表
        :deep(h1) {
          padding: 10px 0;
          margin-top: 70px;
          border-bottom: 2px solid #bebebe;
          text-align: left;
          position: relative;
        }

        :deep(h2) {
          font-size: 25px;
        }

        :deep(h3) {
          font-size: 22px;
        }

        :deep(h4) {
          font-size: 19px;
        }

        :deep(h5, h6) {
          font-size: 16px;
        }

        :deep(h1:first-child) {
          margin-top: 20px;
        }

        :deep(li::marker) {
          color: #989898;
        }

        /* 有序列表 */
        :deep(ol) {
          padding-left: 2em;
        }

        /* 无序列表 */
        :deep(ul) {
          padding-left: 2em;
        }

        /* checkbox */
        :deep(li) {
          input {
            margin: 0 0 0 -1.4em;
          }

          &:has(> input)::marker {
            content: none;
          }
          & :has(> p > input)::marker {
            content: none;
          }
        }

        :deep(hr) {
          border-color: var(--el-color-primary-light-7);
        }

        // 表格
        :deep(table) {
          border: 1px solid #939393;
          box-sizing: border-box;
          padding: 0;
          border-spacing: 0;
          margin: 10px 0;
          max-width: 100%;
          // table-layout: fixed;
          table-layout: auto;
          width: 100%;

          thead {
            background-color: #2b2b2b;
            color: #d4d4d4;

            tr {
              th {
                font-size: 16px;
                padding: 10px;
                border-right: 1px solid #6e6e6e;
              }

              th:last-child {
                border: 0;
              }
            }
          }

          tbody {
            tr {
              td {
                padding: 5px;
                border-right: 1px solid #939393;
                border-bottom: 1px solid #939393;
                word-wrap: break-word;
                width: auto;
              }

              td:last-child {
                border-right: 0;
              }
            }

            tr:last-child {
              td {
                border-bottom: 0;
              }
            }
          }
        }

        :deep(.bl-table-container) {
          border: 0;

          thead {
            display: none;
          }

          tbody {
            td {
              border: 0;
            }
          }
        }

        // 引用
        :deep(blockquote) {
          padding: 1px 10px;
          margin: 10px 0;
          color: #7d7d7d;
          background-color: #f0f0f0;
          border-left: 3px solid #bebebe;
          border-radius: $borderRadius;

          blockquote {
            border: 1px solid #dedede;
            border-left: 3px solid #bebebe;
          }
        }

        :deep(.bl-blockquote-green) {
          background-color: #edf8db;
          border-left: 3px solid #bed609;
        }

        :deep(.bl-blockquote-yellow) {
          background-color: #faf0d5;
          border-left: 3px solid #efc75e;
        }

        :deep(.bl-blockquote-red) {
          background-color: #fbe6e9;
          border-left: 3px solid #ff9090;
        }

        :deep(.bl-blockquote-blue) {
          background-color: #dfeefd;
          border-left: 3px solid #81bbf8;
        }

        :deep(.bl-blockquote-purple) {
          background-color: #ece4fb;
          border-left: 3px solid #ba9bf2;
        }

        :deep(.bl-blockquote-black) {
          background-color: rgba(0, 0, 0, 0.7);
          border-left: 3px solid #000000;
        }

        // 单行代码块
        :deep(code) {
          background-color: #dedede;
          padding: 0px 4px;
          border-radius: 3px;
          margin: 0 3px;
          word-wrap: break-word;
          word-break: break-all;
        }

        // 代码块
        :deep(pre) {
          padding: 0 0 0 30px;
          background-color: #2b2b2b;
          border-radius: $borderRadius;
          box-shadow: 2px 2px 5px rgb(76, 76, 76);
          position: relative;
          overflow: hidden;

          .pre-copy {
            position: absolute;
            top: 5px;
            right: 5px;
            text-align: right;
            z-index: 10;
            color: #5c5c5c;
            padding: 1px 8px;
            border-radius: 4px;
            cursor: pointer;
            user-select: none;
          }

          .pre-copy:hover {
            background-color: #1a1a1a;
            color: #9d9d9d;
          }
          .pre-copy:active {
            color: #e2e2e2;
          }

          ol {
            margin: 0;
            padding-left: 0;
            position: absolute;
            top: 15px;
            left: 3px;
            user-select: none;
            li {
              list-style: none;
              .line-num {
                width: 30px;
                display: inline-block;
                text-align: right;
                padding-right: 10px;
                color: #6a6a6a;
                user-select: none;
              }
            }
          }

          code {
            background-color: inherit;
            border-radius: 0;
            margin: 0;

            height: 100%;
            width: 100%;
            display: block;
            padding: 15px 0 15px 0;
            overflow: auto;
          }

          pre code.hljs {
            display: block;
            overflow-x: auto;
          }

          code.hljs {
            text-shadow: none;
          }

          .hljs {
            color: #a9b7c6;
            background: #2b2b2b;
          }

          .hljs ::selection,
          .hljs::selection {
            // background-color: #323232;
            // color: #a9b7c6;
          }

          .hljs-comment {
            color: #606366;
          }

          .hljs-tag {
            color: #a4a3a3;
          }

          .hljs-operator,
          .hljs-punctuation,
          .hljs-subst {
            color: #a9b7c6;
          }

          .hljs-operator {
            opacity: 0.7;
          }

          .hljs-bullet,
          .hljs-deletion,
          .hljs-name,
          .hljs-selector-tag,
          .hljs-template-variable,
          .hljs-variable {
            color: #4eade5;
          }

          .hljs-attr {
            color: #cc7832;
          }

          .hljs-link,
          .hljs-literal,
          .hljs-number,
          .hljs-symbol,
          .hljs-variable.constant_ {
            color: #689757;
          }

          .hljs-class .hljs-title,
          .hljs-title,
          .hljs-title.class_ {
            color: #e4b568;
          }

          .hljs-strong {
            font-weight: 700;
            color: #bbb529;
          }

          .hljs-addition,
          .hljs-code,
          .hljs-string,
          .hljs-title.class_.inherited__ {
            color: #6a8759;
          }

          .hljs-built_in,
          .hljs-doctag,
          .hljs-keyword.hljs-atrule,
          .hljs-quote,
          .hljs-regexp {
            color: #629755;
          }

          .hljs-attribute,
          .hljs-function .hljs-title,
          .hljs-section,
          .hljs-title.function_,
          .ruby .hljs-property {
            color: #9876aa;
          }

          .diff .hljs-meta,
          .hljs-keyword,
          .hljs-template-tag,
          .hljs-type {
            color: #cc7832;
          }

          .hljs-emphasis {
            color: #cc7832;
            font-style: italic;
          }

          .hljs-meta,
          .hljs-meta .hljs-keyword,
          .hljs-meta .hljs-string {
            color: #b4b428;
          }

          .hljs-meta .hljs-keyword,
          .hljs-meta-keyword {
            font-weight: 700;
          }
        }
      }
    }
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

      .menu {
        height: calc(100% - 20px) !important;
        position: absolute;
        left: 10px;
        top: 10px;
        border-radius: 10px;
        background-color: #ffffff;
        padding-top: 10px;
        z-index: 9999;
        overflow: hidden;

        // .doc-trees {
        //   overflow: overlay;
        // }
      }

      .article {
        padding: 0 10px;
        overflow-x: hidden;

        .bl-preview {
          font-size: 0.8rem;

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
