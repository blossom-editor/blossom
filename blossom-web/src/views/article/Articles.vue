<template>
  <div class="articles-root">
    <div class="header">
      <IndexHeader :bg="true"></IndexHeader>
    </div>
    <div class="main">
      <div class="menu">
        <el-menu v-if="docTreeData != undefined && docTreeData.length > 0" class="doc-trees"
          :default-active="docTreeDefaultActive">

          <!-- ================================================ L1 ================================================ -->
          <div v-for="L1 in docTreeData" :key="L1.i">

            <!-- L1无下级 -->
            <el-menu-item v-if="isEmpty(L1.children)" :index="L1.i">
              <template #title>
                <DocTitle :trees="L1" @click-doc="clickCurDoc" />
              </template>
            </el-menu-item>

            <!-- L1有下级 -->
            <el-sub-menu v-else :expand-open-icon="ArrowDownBold" :expand-close-icon="ArrowRightBold" :index="L1.i">
              <template #title>
                <DocTitle :trees="L1" @click-doc="clickCurDoc" style="font-size: 15px;font-weight: bold;" />
              </template>

              <!-- ================================================ L2 ================================================ -->
              <div v-for="L2 in L1.children" :key="L2.i">
                <!-- 级别1无下级 -->
                <el-menu-item v-if="isEmpty(L2.children)" :index="L2.i">
                  <template #title>
                    <DocTitle :trees="L2" @click-doc="clickCurDoc" />
                  </template>
                </el-menu-item>

                <!-- 级别1有下级 -->
                <el-sub-menu v-else :expand-open-icon="ArrowDownBold" :expand-close-icon="ArrowRightBold" :index="L2.i">
                  <template #title>
                    <DocTitle :trees="L2" @click-doc="clickCurDoc" />
                  </template>

                  <!-- ================================================ L3 ================================================ -->
                  <div v-for="L3 in L2.children" :key="L3.i">
                    <!-- 级别2无下级 -->
                    <el-menu-item v-if="isEmpty(L3.children)" :index="L3.i">
                      <template #title>
                        <DocTitle :trees="L3" @click-doc="clickCurDoc" />
                      </template>
                    </el-menu-item>

                    <!-- 级别2有下级 -->
                    <el-sub-menu v-else :expand-open-icon="ArrowDownBold" :expand-close-icon="ArrowRightBold"
                      :index="L3.i">
                      <template #title>
                        <DocTitle :trees="L3" @click-doc="clickCurDoc" />
                      </template>

                      <!-- ================================================ L4 ================================================ -->
                      <div v-for="L4 in L3.children" :key="L4.i">
                        <!-- 级别3无下级 -->
                        <el-menu-item v-if="isEmpty(L4.children)" :index="L4.i">
                          <template #title>
                            <DocTitle :trees="L4" @click-doc="clickCurDoc" />
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

      <div class="article">
        <div class="bl-preview" v-html="article?.html"></div>
        <!-- <div v-else class="bl-preview-placeholder">请选择一篇文章查看</div> -->
      </div>

      <div class="toc">
        <div class="viewer-toc">
          <div class="toc-subtitle" style="font-size: 15px;">《{{ article?.name }}》</div>
          <div class="toc-subtitle">
            <span class="iconbl bl-pen-line"></span> {{ article?.words }} 字 |
            <span class="iconbl bl-read-line"></span> {{ article?.uv }} |
            <span class="iconbl bl-like-line"></span> {{ article?.likes }}
          </div>
          <div class="toc-subtitle"><span class="iconbl bl-a-clock3-line"></span> 公开 {{ article?.openTime }}
          </div>
          <div class="toc-subtitle"><span class="iconbl bl-a-clock3-line"></span> 修改 {{ article?.syncTime }}
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
import { ref, onActivated } from "vue";
import { ArrowDownBold, ArrowRightBold } from '@element-plus/icons-vue'
import { articleInfoOpenApi, docTreeApi } from '@/api/blossom'
import { isNull, isEmpty, isNotNull } from '@/assets/utils/obj'
import DocTitle from './DocTitle.vue'
import IndexHeader from "../index/IndexHeader.vue"
import 'katex/dist/katex.min.css'

onActivated(() => {
  getRouteQueryParams();
})

/**
 * 路由中获取ID参数
 */
const getRouteQueryParams = () => {
  let articleId = route.query.articleId;
  getDocTree()
  if (isNotNull(articleId)) {
    docTreeDefaultActive.value = articleId as string
    let treeParam: any = { ty: 3, i: articleId }
    clickCurDoc(treeParam)
  }
}

const route = useRoute();
// 文档菜单的加载动画
const previewLoading = ref(false)
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
  html: '<h4 style="color:#AAAAAA">请在左侧选择一篇文章</h4>'
})
const tocList = ref<any>([])

/**
 * 获取文档树状列表
 * 1. 初始化是全否调用
 * 2. 在 workbench 中点击按钮调用, 每个按钮是单选的
 */
const getDocTree = () => {
  docTreeLoading.value = true;
  docTreeData.value = []
  docTreeApi({ onlyOpen: true }).then(resp => {
    docTreeData.value = resp.data
  }).finally(() => {
    docTreeLoading.value = false
  })
}

const clickCurDoc = async (tree: DocTree) => {
  // 如果选中的是文章, 则查询文章详情, 用于在编辑器中显示以及注入
  if (tree.ty == 3) {
    await getCurEditArticle(tree.i)
  }
}


/**
 * 如果点击的是文章, 则查询文章信息和正文, 并在编辑器中显示.
 */
const getCurEditArticle = async (id: number) => {
  // previewLoading.value = true
  // tocList.value = []
  await articleInfoOpenApi({ id: id, showToc: true, showMarkdown: false, showHtml: true }).then(resp => {
    if (isNull(resp.data)) {
      return
    }
    article.value = resp.data
    tocList.value = JSON.parse(resp.data.toc)
  }).finally(() => {
    // setTimeout(() => { previewLoading.value = false }, 500);
  })
}

const toScroll = (level: number, content: string) => {
  let id = level + '-' + content
  let elm = document.getElementById(id)
  elm?.scrollIntoView(true)
}
</script>

<style scoped lang="scss">
.articles-root {
  @include box(100%, 100%);
  @include flex(column, flex-start, center);
  box-sizing: border-box;
  background: #ffffff;

  // 屏幕宽度在 1100 以内时使用以下样式
  @media screen and (max-width: 1100px) {
    .toc {
      position: absolute;
      right: 10px;
      top: 10px;
      max-height: 400px;
      background-color: #4D4D4DE2;
      border-radius: 10px;
      border: 0;
    }

    .viewer-toc {
      color: #fff !important;
    }
  }

  .header {
    @include box(100%, 60px);
  }

  .main {
    @include box(100%, calc(100% - 60px));
    @include flex(row, center, center);
    padding: 10px;
    position: relative;

    .menu {
      @include box(240px, 100%, 240px, 240px);
      border-right: 1px solid var(--el-border-color);
      font-weight: 200;
      transition: 0.1s;

      .doc-trees {
        @include box(100%, 100%);
        font-weight: 200;
        padding-right: 0;
        border: 0;
        overflow-y: scroll;
        padding-right: 6px;
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
        --el-color-primary-light-9: #FFFFFF00;
        --el-menu-hover-bg-color: #FFFFFF00;

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
              right: calc(215px - var(--el-menu-level) * 10px);
              font-size: 12px;
            }
          }
        }

        :deep(.el-menu-item) {
          --el-menu-hover-bg-color: #FFFFFF00 !important;
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
          background: linear-gradient(90deg,
              #3D454D00 0%,
              #C3C3C3 40%,
              #C3C3C3 60%,
              #3D454D00 100%);
          color: #ffffff;
          font-weight: 700;
        }

        :deep(.el-badge__content) {
          top: 7px;
          transform: translateY(-50%) translateX(100%) scale(0.8);
        }

      }
    }

    .toc {
      @include box(270px, 100%, 270px, 270px);
      border-left: 1px solid #EEEEEE;
      overflow: auto;
      transition: 0.3s;

      .viewer-toc {
        @include box(100%, 100%);
        color: #5E5E5E;
        padding: 10px;
        z-index: 1000;
        transition: 0.3s;

        .toc-title {
          @include box(100%, 40px);
          @include font(30px, 700);
          line-height: 40px;
          margin-top: 10px;
          padding-top: 10px;
          border-top: 3px solid #BCBCBC;
        }

        .toc-subtitle {
          width: 100%;
          @include flex(row, flex-start, center);
          @include font(12px);
          color: #ABABAB;
          overflow: hidden;
          white-space: nowrap;
          text-overflow: ellipsis;
          white-space: pre;
          margin-top: 5px;
        }

        .toc-content {
          // @include box(100%, calc(100% - 20px - 20px));
          widows: 100%;
          @include font(14px);
          // border-top: 3px solid #BCBCBC;
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
            overflow: hidden;
            white-space: nowrap;
            text-overflow: ellipsis;
            white-space: pre;

            &:hover {
              font-weight: bold;
            }
          }

          .toc-1 {
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
            &::before {
              content: '  ';
            }

          }

          .toc-3 {
            &::before {
              content: '    ';
            }
          }

          .toc-4 {
            &::before {
              content: '      ';
            }
          }

          .toc-5 {
            &::before {
              content: '        ';
            }
          }

          .toc-6 {
            &::before {
              content: '          ';
            }
          }
        }
      }
    }

    .article {
      height: 100%;
      width: 1260px;
      max-width: 1260px;
      overflow-y: scroll;
      padding: 0 30px;


      .bl-preview {
        $borderRadius: 4px;
        color: #4b4b4b;
        font-size: 14px;
        line-height: 1.4;

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
          // text-decoration: underline;
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
          // background-color: #eaeaea;
          padding: 10px 0;
          margin-top: 70px;
          border-bottom: 2px solid #bebebe;
          // // border-radius: $borderRadius;
          // box-shadow: 3px 3px 10px 3px #e5e5e5;
          text-align: left;
          // text-shadow: 3px 3px 3px rgb(148, 148, 148);
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

        :deep(h5,
          h6) {
          font-size: 16px;
        }

        :deep(h1:first-child) {
          margin-top: 20px;
        }

        :deep(li::marker) {
          color: #989898;
        }

        // 有序列表
        :deep(ol) {
          padding-left: 30px;
        }

        // 无序列表
        :deep(ul) {
          padding-left: 20px;

          ul {
            padding-left: 20px;
          }
        }

        // checkbox
        :deep(ul:has(input)) {
          padding-left: 0px;

          ul {
            padding-left: 15px;

          }

          li::marker {
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
          table-layout: fixed;
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
          background-color: #DEDEDE;
          padding: 0px 4px;
          border-radius: 3px;
          margin: 0 3px;
          font-size: 13px;
        }


        // 代码块
        :deep(pre) {
          padding: 10px 10px 10px 10px;
          background-color: #2b2b2b;
          overflow: auto;
          border-radius: $borderRadius;
          box-shadow: 2px 2px 5px rgb(76, 76, 76);
          font-size: 13px;
          max-height: 1100px;


          code {
            background-color: inherit;
            padding: 0;
            border-radius: 0;
            margin: 0;
            font-size: 13px;
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
            background: #2b2b2b
          }

          .hljs ::selection,
          .hljs::selection {
            background-color: #323232;
            color: #a9b7c6
          }

          .hljs-comment {
            color: #606366
          }

          .hljs-tag {
            color: #a4a3a3
          }

          .hljs-operator,
          .hljs-punctuation,
          .hljs-subst {
            color: #a9b7c6
          }

          .hljs-operator {
            opacity: .7
          }

          .hljs-bullet,
          .hljs-deletion,
          .hljs-name,
          .hljs-selector-tag,
          .hljs-template-variable,
          .hljs-variable {
            color: #4eade5
          }

          .hljs-attr {
            color: #cc7832;
          }

          .hljs-link,
          .hljs-literal,
          .hljs-number,
          .hljs-symbol,
          .hljs-variable.constant_ {
            color: #689757
          }

          .hljs-class .hljs-title,
          .hljs-title,
          .hljs-title.class_ {
            color: #e4b568
          }

          .hljs-strong {
            font-weight: 700;
            color: #bbb529
          }

          .hljs-addition,
          .hljs-code,
          .hljs-string,
          .hljs-title.class_.inherited__ {
            color: #6a8759
          }

          .hljs-built_in,
          .hljs-doctag,
          .hljs-keyword.hljs-atrule,
          .hljs-quote,
          .hljs-regexp {
            color: #629755
          }

          .hljs-attribute,
          .hljs-function .hljs-title,
          .hljs-section,
          .hljs-title.function_,
          .ruby .hljs-property {
            color: #9876aa
          }

          .diff .hljs-meta,
          .hljs-keyword,
          .hljs-template-tag,
          .hljs-type {
            color: #cc7832
          }

          .hljs-emphasis {
            color: #cc7832;
            font-style: italic
          }

          .hljs-meta,
          .hljs-meta .hljs-keyword,
          .hljs-meta .hljs-string {
            color: #b4b428;
          }

          .hljs-meta .hljs-keyword,
          .hljs-meta-keyword {
            font-weight: 700
          }
        }
      }
    }
  }


}
</style>