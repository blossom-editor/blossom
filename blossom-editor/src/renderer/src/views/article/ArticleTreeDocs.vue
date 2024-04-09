<template>
  <!-- 文件夹操作 -->
  <div class="doc-workbench">
    <ArticleTreeWorkbench @show-sort="handleShowSort" @show-search="handleShowArticleSearchDialog" ref="ArticleTreeWorkbenchRef">
    </ArticleTreeWorkbench>
  </div>
  <!--   -->
  <div class="doc-tree-operator">
    <el-tooltip effect="light" popper-class="is-small" placement="top" :hide-after="0" content="显示排序">
      <div class="iconbl bl-a-leftdirection-line" @click="handleShowSort"></div>
    </el-tooltip>
    <el-tooltip effect="light" popper-class="is-small" placement="top" :hide-after="0" content="根目录下新建文章">
      <div class="iconbl bl-fileadd-line" @click="addArticleToRoot()"></div>
    </el-tooltip>
    <el-tooltip effect="light" popper-class="is-small" placement="top" :hide-after="0" content="根目录下新建文件夹">
      <div class="iconbl bl-folderadd-line" @click="addFolderToRoot()"></div>
    </el-tooltip>
    <el-tooltip effect="light" popper-class="is-small" placement="top" :hide-after="0" :show-after="1000" content="搜索">
      <div class="iconbl bl-search-item" @click="showTreeFilter()"></div>
    </el-tooltip>
    <el-tooltip effect="light" popper-class="is-small" placement="top" :hide-after="0" :show-after="1000" content="刷新">
      <div class="iconbl bl-refresh-line" @click="refreshDocTree()"></div>
    </el-tooltip>
    <el-tooltip effect="light" popper-class="is-small" placement="top" :hide-after="0" :show-after="1000" content="选中当前文章">
      <div class="iconbl bl-collimation" @click="collimationCurrentArticle"></div>
    </el-tooltip>
    <el-tooltip effect="light" popper-class="is-small" placement="top" :hide-after="0" :show-after="1000" content="折叠所有文件夹">
      <div class="iconbl bl-collapse" @click="collapseAll"></div>
    </el-tooltip>
    <div class="doc-tree-search" ref="DocTreeSearch" v-show="isShowTreeFilter">
      <el-input v-model="treeFilterText" style="width: 180px" ref="DocTreeSearchInput">
        <template #append>
          <div ref="DocTreeSearchMove" style="cursor: move; border-right: 1px solid var(--el-border-color)">
            <el-icon><Rank /></el-icon>
          </div>
          <div style="cursor: pointer" @click="showTreeFilter()">
            <el-icon size="14"><Close /></el-icon>
          </div>
        </template>
      </el-input>
    </div>
  </div>
  <div
    ref="DocTreeContainer"
    class="doc-trees-container"
    v-loading="docTreeLoading"
    element-loading-text="正在读取文档..."
    :style="{ fontSize: viewStyle.treeDocsFontSize }">
    <el-tree
      v-if="docTreeData.length > 0"
      ref="DocTreeRef"
      class="doc-tree"
      :data="docTreeData"
      :allow-drag="handleAllowDrag"
      :allow-drop="handleAllowDrop"
      :highlight-current="true"
      :indent="14"
      :icon="ArrowRightBold"
      :accordion="false"
      :default-expanded-keys="Array.from(docTreeCurrentExpandId)"
      :filter-node-method="filterNode"
      :draggable="isBlank(treeFilterText)"
      node-key="i"
      @nodeClick="clickCurDoc"
      @nodeExpand="handleNodeExpand"
      @nodeCollapse="handleNodeCollapse"
      @nodeDrop="handleDrop">
      <template #default="{ node, data }">
        <div v-if="isShowSort" class="sort-tag" :style="{ backgroundColor: getColor(node) }">{{ data.s }}</div>
        <div class="menu-item-wrapper" :id="'article-doc-wrapper-' + data.i" @click.right="handleClickRightMenu($event, data)">
          <div :class="[viewStyle.isShowSubjectStyle ? (data.t?.includes('subject') ? 'subject-title' : 'doc-title') : 'doc-title']">
            <div class="doc-name">
              <img class="menu-icon-img" v-if="isShowImg(data, viewStyle)" :src="data.icon" />
              <svg v-else-if="isShowSvg(data, viewStyle)" class="icon menu-icon" aria-hidden="true">
                <use :xlink:href="'#' + data.icon"></use>
              </svg>
              <el-input
                v-if="data?.updn"
                v-model="data.n"
                :id="'article-doc-name-' + data.i"
                @blur="blurArticleNameInput(data)"
                @keyup.enter="blurArticleNameInput(data)"
                style="width: 95%"></el-input>
              <div v-else class="name-wrapper" :style="{ maxWidth: isNotBlank(data.icon) ? 'calc(100% - 25px)' : '100%' }">
                {{ data.n }}
              </div>
              <bl-tag v-for="tag in tags(data, viewStyle)" style="margin-top: 4px" :bg-color="tag.bgColor" :icon="tag.icon">
                {{ tag.content }}
              </bl-tag>
            </div>
            <div
              v-if="viewStyle.isShowArticleType"
              v-for="(line, index) in tagLins(data)"
              :key="line"
              :class="[line]"
              :style="{ left: -1 * (index + 1.5) * 4 + 'px' }"></div>
          </div>
        </div>
      </template>
    </el-tree>
    <bl-row v-else class="doc-trees-placeholder" just="center">
      无文档，点击
      <div class="iconbl bl-fileadd-line" @click="addArticleToRoot()"></div>
      /
      <div class="iconbl bl-folderadd-line" @click="addFolderToRoot()"></div>
      添加
    </bl-row>
  </div>

  <Teleport to="body">
    <div v-show="rMenu.show" class="tree-menu" :style="{ left: rMenu.clientX + 'px', top: rMenu.clientY + 'px' }" ref="ArticleDocTreeRightMenuRef">
      <div class="doc-name">{{ curDoc.n }}</div>
      <div class="menu-content">
        <div @click="rename"><span class="iconbl bl-pen"></span>重命名</div>
        <div @click="handleShowDocInfoDialog('upd')"><span class="iconbl bl-a-fileedit-line"></span>编辑详情</div>
        <div v-if="curDoc.ty === 3" @click="syncDoc()"><span class="iconbl bl-a-cloudrefresh-line"></span>同步文章</div>
        <div v-if="curDoc.ty !== 3" @click="addFolderToDoc()"><span class="iconbl bl-folderadd-line"></span>新增文件夹</div>
        <div v-if="curDoc.ty !== 3" @click="addArticleToDoc()"><span class="iconbl bl-fileadd-line"></span>新增笔记</div>
        <div v-if="curDoc.ty === 3" @click="createUrl('link')"><span class="iconbl bl-correlation-line"></span>复制双链引用</div>
        <div v-if="curDoc.ty !== 3" @click="handleShowArticleImportDialog()"><span class="iconbl bl-file-upload-line"></span>导入文章</div>

        <div @mouseenter="handleHoverRightMenuLevel2($event, 2)" data-bl-prevet="true">
          <span class="iconbl bl-a-rightsmallline-line"></span>
          <span class="iconbl bl-apps-line"></span>更多
          <div class="tree-menu-level2" :style="rMenuLevel2">
            <div v-if="curDoc.o === 0" @click="open(1)"><span class="iconbl bl-a-cloudupload-line"></span>公开{{ curDocType }}</div>
            <div v-if="curDoc.o === 1" @click="open(0)"><span class="iconbl bl-a-clouddownload-line"></span>取消{{ curDocType }}公开</div>
            <div v-if="curDoc.star === 0" @click="star(1)"><span class="iconbl bl-star-line"></span>收藏{{ curDocType }}</div>
            <div v-if="curDoc.star === 1" @click="star(0)"><span class="iconbl bl-star-line"></span>取消收藏{{ curDocType }}</div>
            <div v-if="curDoc.ty === 3 && !curDoc.t.includes('toc')" @click="addArticleTag('toc')">
              <span class="iconbl bl-list-ordered"></span>设为专题目录
            </div>
            <div v-if="curDoc.ty === 3 && curDoc.t.includes('toc')" @click="addArticleTag('toc')">
              <span class="iconbl bl-list-ordered"></span>取消专题目录
            </div>

            <div v-if="curDoc.ty !== 3 && !curDoc.t.includes('subject')" @click="addFolderTag('subject')">
              <span class="iconbl bl-a-lowerrightpage-line"></span>设为专题
            </div>
            <div v-if="curDoc.ty !== 3 && curDoc.t.includes('subject')" @click="addFolderTag('subject')">
              <span class="iconbl bl-a-lowerrightpage-line"></span>取消专题
            </div>
            <div v-if="curDoc.ty === 1" class="menu-item-divider"></div>
            <div v-if="curDoc.ty === 1" @click="openBactch(1)"><span class="iconbl bl-a-cloudupload-line"></span>所有文章公开</div>
            <div v-if="curDoc.ty === 1" @click="openBactch(0)"><span class="iconbl bl-a-clouddownload-line"></span>所有文章取消公开</div>
          </div>
        </div>

        <div v-if="curDoc.ty === 3" class="menu-item-divider"></div>
        <div v-if="curDoc.ty === 3" @click="openArticleWindow"><span class="iconbl bl-a-computerend-line"></span>新窗口查看</div>
        <div v-if="curDoc.ty === 3" @click="createUrl('tempVisit', true)"><span class="iconbl bl-visit"></span>浏览器临时访问</div>

        <div v-if="curDoc.ty === 3" @mouseenter="handleHoverRightMenuLevel2($event, 4)" data-bl-prevet="true">
          <span class="iconbl bl-a-rightsmallline-line"></span>
          <span class="iconbl bl-file-download-line"></span>导出文章
          <div class="tree-menu-level2" :style="rMenuLevel2">
            <div @click="articleDownload"><span class="iconbl bl-file-markdown"></span>导出为 MD</div>
            <div @click="articleBackup('MARKDOWN')"><span class="iconbl bl-file-markdown"></span>导出为本地 MD</div>
            <div @click="articleDownloadHtml"><span class="iconbl bl-HTML"></span>导出为 HTML</div>
            <div @click="articleBackup('HTML')"><span class="iconbl bl-HTML"></span>导出为本地 HTML</div>
          </div>
        </div>
        <div v-if="curDoc.ty === 3" @mouseenter="handleHoverRightMenuLevel2($event, 2)" data-bl-prevet="true">
          <span class="iconbl bl-a-rightsmallline-line"></span>
          <span class="iconbl bl-a-linkspread-line"></span>创建链接
          <div class="tree-menu-level2" :style="rMenuLevel2">
            <div v-if="curDoc.o === 1" @click="createUrl('copy')"><span class="iconbl bl-planet-line"></span>复制博客地址</div>
            <div @click="createUrl('tempVisit')"><span class="iconbl bl-visit"></span>创建临时访问(3h)</div>
            <div @click="handleShowTempVisitDialog"><span class="iconbl bl-visit"></span>创建临时访问(自定义)</div>
          </div>
        </div>
        <div v-if="curDoc.ty === 3 && curDoc.o === 1" @click="createUrl('open')"><span class="iconbl bl-planet-line"></span>博客中查看</div>
        <div v-if="curDoc.ty === 3 && curDoc.o === 1" @click="handleArticleQrCodeDialog"><span class="iconbl bl-qr-code-line"></span>博客二维码</div>
        <div class="menu-item-divider"></div>
        <div @click="delDoc()"><span class="iconbl bl-delete-line"></span>删除{{ curDocType }}</div>
      </div>
    </div>
  </Teleport>

  <!-- 详情 -->
  <el-dialog
    class="bl-dialog-draggable-header"
    v-model="isShowDocInfoDialog"
    width="535"
    :append-to-body="true"
    :destroy-on-close="true"
    :close-on-click-modal="false"
    align-center
    draggable>
    <ArticleInfo ref="ArticleInfoRef" @saved="savedCallback"></ArticleInfo>
  </el-dialog>

  <!-- 二维码 -->
  <el-dialog v-model="isShowQrCodeDialog" width="335" :append-to-body="true" :destroy-on-close="true" :close-on-click-modal="false" align-center>
    <ArticleQrCode ref="ArticleQrCodeRef"></ArticleQrCode>
  </el-dialog>

  <!-- 导入 -->
  <el-dialog v-model="isShowArticleImportDialog" width="335" top="80px" :append-to-body="true" :destroy-on-close="true" :close-on-click-modal="false">
    <ArticleImport ref="ArticleImportRef" :doc="curDoc"></ArticleImport>
  </el-dialog>

  <!-- 自定义临时访问链接 -->
  <el-dialog v-model="isShowTempVisitDialog" width="400" :append-to-body="true" :destroy-on-close="true" :close-on-click-modal="true" align-center>
    <ArticleCustomTempVisit ref="ArticleTempVisitRef" @created="tempVisitCreated"></ArticleCustomTempVisit>
  </el-dialog>

  <!-- 搜索 -->
  <el-dialog
    v-model="isShowArticleSearchDialog"
    class="bl-dialog-hidden-header-fixed-body"
    width="705"
    style="height: 80%"
    :append-to-body="true"
    :destroy-on-close="true"
    :close-on-click-modal="true"
    align-center>
    <ArticleSearch @open-article="openArticle" @create-link="createUrlLink"></ArticleSearch>
  </el-dialog>

  <!-- <div class="doc-tree-debug">
    <div>所有展开：{{ Array.from(docTreeCurrentExpandId) + '' }}</div>
    <div>当前选中：{{ docTreeCurrentId }}</div>
    <div>当前文章：{{ articleCurrnetId }}</div>
  </div> -->
</template>

<script setup lang="ts">
import { ref, provide, onBeforeUnmount, nextTick, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useServerStore } from '@renderer/stores/server'
import { useUserStore } from '@renderer/stores/user'
import { useConfigStore } from '@renderer/stores/config'
// element plus
import { ElMessageBox, TreeNode } from 'element-plus'
import type { DragEvents } from 'element-plus/es/components/tree/src/model/useDragNode'
import type { NodeDropType } from 'element-plus/es/components/tree/src/tree.type'
import Node from 'element-plus/es/components/tree/src/model/node'
import { ArrowRightBold, Rank, Close } from '@element-plus/icons-vue'
// ts
import {
  articleAddApi,
  articleUpdTagApi,
  articleDownloadHtmlApi,
  articleOpenApi,
  articleOpenBatchApi,
  articleStarApi,
  folderStarApi,
  folderAddApi,
  folderUpdTagApi,
  folderDelApi,
  folderOpenApi,
  folderUpdNameApi,
  articleDownloadApi,
  articleSyncApi,
  articleDelApi,
  articleBackupApi,
  articleTempKey,
  articleTempH,
  articleUpdNameApi,
  docTreeApi,
  docUpdSortApi
} from '@renderer/api/blossom'
import { grammar } from './scripts/markedjs'
import { provideKeyDocTree } from '@renderer/views/doc/doc'
import { getColor, handleTreeDrop } from '@renderer/views/doc/doc-tree'
import { tags, tagLins, isShowImg, isShowSvg } from '@renderer/views/doc/doc-tree-detail'
import { useLifecycle } from '@renderer/scripts/lifecycle'
import { useDraggable } from '@renderer/scripts/draggable'
// util
import { isEmpty } from 'lodash'
import { isNotNull, isNotBlank, isBlank } from '@renderer/assets/utils/obj'
import { openExtenal, writeText, openNewArticleWindow } from '@renderer/assets/utils/electron'
// components
import Notify from '@renderer/scripts/notify'
import ArticleTreeWorkbench from './ArticleTreeWorkbench.vue'
import ArticleQrCode from './ArticleQrCode.vue'
import ArticleInfo from './ArticleInfo.vue'
import ArticleImport from './ArticleImport.vue'
import ArticleSearch from './ArticleSearch.vue'
import ArticleCustomTempVisit from './ArticleCustomTempVisit.vue'
import { downloadTextPlain } from './scripts/article'

const route = useRoute()
const server = useServerStore()
const user = useUserStore()
const { viewStyle } = useConfigStore()

useLifecycle(
  () => getDocTree(getRouteQueryParams),
  () => getRouteQueryParams()
)
onBeforeUnmount(() => {
  document.body.removeEventListener('click', closeTreeDocsMenuShow)
  document.body.removeEventListener('contextmenu', closeTreeDocsMenuShow)
})
watch(
  () => user.currentUserId,
  (_newVal, _oldVal) => getDocTree()
)

//#region ----------------------------------------< 树状列表 >--------------------------------------
let editorLoadingTimeout: NodeJS.Timeout
const DocTreeRef = ref()
const docTreeLoading = ref(true) // 文档菜单的加载动画
const isShowSort = ref(false) // 是否显示文档排序
const docTreeData = ref<DocTree[]>([]) // 文档菜单
provide(provideKeyDocTree, docTreeData) // 提供菜单列表依赖注入, 主要用于在详情中选择上级文件夹, 避免二次查询

/** 获取路由参数 */
const getRouteQueryParams = () => {
  let routeArticleId = route.query.articleId
  if (isNotNull(routeArticleId)) {
    const articleId = routeArticleId as string
    emits('clickDoc', { ty: 3, i: articleId })
    nextTick(() => {
      docTreeCurrentId.value = articleId
      const parentNode = DocTreeRef.value.getNode(articleId)
      setCurrentKey({ i: articleId, p: parentNode.data.p, ty: 3 })
      const ele = document.getElementById('article-doc-wrapper-' + articleId)
      if (ele) {
        ;(DocTreeContainer.value as Element).scrollTop = ele.offsetTop
      }
    })
  }
}

/**
 * 聚焦当前打开的文章
 */
const collimationCurrentArticle = () => {
  if (!isEmpty(docTreeData.value) && isNotBlank(articleCurrnetId.value)) {
    DocTreeRef.value.setCurrentKey(articleCurrnetId.value)
    nextTick(() => {
      const ele = document.getElementById('article-doc-wrapper-' + articleCurrnetId.value)
      if (ele) {
        ;(DocTreeContainer.value as Element).scrollTop = ele.offsetTop
      }
    })
  }
}

/**
 * 刷新文档, 并在渲染结束后选中最后一次选中项
 */
const refreshDocTree = () => {
  getDocTree(() => {
    nextTick(() => {
      if (!isEmpty(docTreeData.value) && isNotBlank(docTreeCurrentId.value)) {
        DocTreeRef.value.setCurrentKey(docTreeCurrentId.value)
      }
    })
  })
}

/**
 * 获取文档树状列表
 *
 * @param callback 查询后的回调方法
 */
const getDocTree = (callback?: () => void) => {
  startLoading()
  docTreeApi({ onlyPicture: false, onlyOpen: false, onlySubject: false, onlyStar: false })
    .then((resp) => {
      docTreeData.value = resp.data
      endLoading()
      if (callback) callback()
    })
    .finally(() => endLoading())
}

/**
 * 点击菜单
 *
 * @param tree 点击的菜单节点数据
 * @param node 树状菜单 node
 * @param treeNode TreeNode
 * @param event 点击事件
 */
const clickCurDoc = (tree: DocTree, node: Node, treeNode: TreeNode, event: MouseEvent) => {
  closeTreeDocsMenuShow(event)
  setCurrentKey(tree, node, treeNode, event)
  emits('clickDoc', tree)
}

/**
 * 暴露给其他组件
 */
const getDocTreeData = (): DocTree[] => {
  return docTreeData.value
}

/** 是否显示排序 */
const handleShowSort = () => (isShowSort.value = !isShowSort.value)

/** 开始加载 */
const startLoading = () => {
  if (!editorLoadingTimeout) {
    editorLoadingTimeout = setTimeout(() => (docTreeLoading.value = true), 100)
  }
}

/** 结束加载 */
const endLoading = () => {
  if (editorLoadingTimeout) {
    clearTimeout(editorLoadingTimeout)
  }
  docTreeLoading.value = false
}

//#endregion

//#region ----------------------------------------< 树状列表管理 >--------------------------------------
const articleCurrnetId = ref('')
// 文档的最后选中项, 用于外部跳转后选中菜单
const docTreeCurrentId = ref('')
// 所有展开的节点
const docTreeCurrentExpandId = ref<Set<string>>(new Set())
// 搜索内容
const treeFilterText = ref('')
const isShowTreeFilter = ref(false)
// 搜索框
const DocTreeContainer = ref()
const DocTreeSearch = ref()
const DocTreeSearchMove = ref()
const DocTreeSearchInput = ref()
// 禁止拖拽的节点, 正在重命名的节点不允许进行拖拽
let notAllowDragKey: string = ''

useDraggable(DocTreeSearch, DocTreeSearchMove, DocTreeContainer)

watch(treeFilterText, (val) => {
  DocTreeRef.value!.filter(val)
})

/**
 * 设置选中项, 并展开所有上级
 *
 * @param tree 当前选中的文档
 */
const setCurrentKey = (tree: { i: string; p: string; ty: DocType }, node?: Node, _treeNode?: any, _event?: MouseEvent) => {
  if (tree.ty === 1 || tree.ty === 2) {
    docTreeCurrentId.value = tree.i
    if (node && node.expanded) {
      docTreeCurrentExpandId.value.add(tree.i)
    }
  } else if (tree.ty === 3) {
    docTreeCurrentId.value = tree.i
    docTreeCurrentExpandId.value.add(tree.p)
    articleCurrnetId.value = tree.i
  }
  DocTreeRef.value.setCurrentKey(tree.i)
}

/**
 * 显示过滤框
 */
const showTreeFilter = () => {
  isShowTreeFilter.value = !isShowTreeFilter.value
  if (isShowTreeFilter.value) {
    DocTreeSearchInput.value.focus()
  }
}

/**
 * 过滤节点名称和标签
 *
 * @param value 搜索内容
 * @param data 列表
 * @return 返回节点是否保留
 */
const filterNode = (value: string, data: DocTree) => {
  if (!value) return true
  return data.n.includes(value) || data.t.toString().includes(value)
}

/**
 * 判断是否允许被拖拽
 * 1. 正在重命名的节点不允许被拖拽
 *
 * @param node 拖动的节点
 * @return boolean 节点是否允许被拖动
 */
const handleAllowDrag = (node: Node) => {
  return notAllowDragKey !== node.data.i
}

/**
 * 判断是否允许被节点放置
 *
 * @param _draggingNode 拖动的节点
 * @param dropNode 被防止的节点
 * @param type 放置的类型
 * @return boolean 是否允许被放置
 */
const handleAllowDrop = (_draggingNode: Node, dropNode: Node, type: NodeDropType) => {
  if (dropNode.data.ty === 3 && type === 'inner') {
    return false
  }
  return true
}

/**
 * 折叠全部, 清空当前选中状态, 并刷新列表
 */
const collapseAll = () => {
  docTreeCurrentExpandId.value.clear()
  getDocTree()
}

/**
 * 折叠所有无子菜单的文件夹
 */
const collapseNoChild = () => {
  nextTick(() => {
    for (let i = 0; i < docTreeData.value.length; i++) {
      const doc = docTreeData.value[i]
      collapseChild(doc)
    }
  })
}

/**
 * 递归折叠所有子文件夹
 *
 * @param doc
 */
const collapseChild = (doc: DocTree) => {
  if (doc.ty === 1 || doc.ty === 2) {
    if (isEmpty(doc.children)) {
      docTreeCurrentExpandId.value.delete(doc.i)
    } else {
      for (let i = 0; i < doc.children!.length; i++) {
        const cdoc = doc.children![i]
        collapseChild(cdoc)
      }
    }
  }
}

/**
 * 处理节点展开
 */
const handleNodeExpand = (tree: DocTree, _node: Node) => {
  docTreeCurrentExpandId.value.add(tree.i)
}

/**
 * 处理节点折叠, 同时清除所有子节点的展开状态
 */
const handleNodeCollapse = async (tree: DocTree, node: Node) => {
  docTreeCurrentExpandId.value.delete(tree.i)
  collapseChilds(node)
}

/**
 * 递归缩起所有子节点
 */
const collapseChilds = async (node: Node) => {
  for (let i = 0; i < node.childNodes.length; i++) {
    const child = node.childNodes[i]
    if (child.isLeaf) {
    } else {
      child.expanded = false
      docTreeCurrentExpandId.value.delete(child.data.i)
      collapseChilds(child)
    }
  }
}

/**
 * 如果父节点没有子节点时, 关闭父节点的展开状态
 * @param pid 父ID
 */
const closeParentIfNoChild = (pid: string) => {
  let node: Node = DocTreeRef.value.getNode(pid)
  if (node && isEmpty(node.childNodes)) {
    docTreeCurrentExpandId.value.delete(pid)
  }
}

/**
 * 拖拽后处理各个节点排序
 */
const handleDrop = (drag: Node, enter: Node, dropType: NodeDropType, _event: DragEvents) => {
  handleTreeDrop(drag, enter, dropType, _event, DocTreeRef, docTreeData, 1, (needUpd) => {
    docUpdSortApi({ docs: needUpd, folderType: 1 })
      .then((resp) => {
        docTreeData.value = resp.data
        collapseNoChild()
      })
      .catch(() => getDocTree())
  })
}

//#endregion

//#region ----------------------------------------< 右键菜单 >--------------------------------------
const curDoc = ref<DocTree>({ i: '0', p: '0', n: '选择菜单', o: 0, t: [], s: 0, icon: '', ty: 1, star: 0, updn: false })
const rMenu = ref<RightMenu>({ show: false, clientX: 0, clientY: 0 })
const rMenuLevel2 = ref<RightMenuLevel2>({ top: '0px' })
const ArticleDocTreeRightMenuRef = ref()
const ArticleTreeWorkbenchRef = ref()
const curDocType = computed(() => {
  if (curDoc.value.ty === 1 || curDoc.value.ty === 2) {
    return '文件夹'
  } else {
    return '文章'
  }
})

/**
 * 显示右键菜单
 * @param doc 文档
 * @param event 事件
 */
const handleClickRightMenu = (event: MouseEvent, doc: DocTree) => {
  event.preventDefault()
  docTreeCurrentExpandId.value.add(doc.p)
  if (!doc) return

  curDoc.value = doc
  rMenu.value.show = false
  rMenu.value.show = true
  nextTick(() => {
    let domHeight = ArticleDocTreeRightMenuRef.value.offsetHeight
    let y = event.clientY
    if (document.body.clientHeight - event.clientY < domHeight) {
      y = event.clientY - domHeight
    }
    rMenu.value = { show: true, clientX: event.clientX, clientY: y }
    setTimeout(() => {
      document.body.addEventListener('click', closeTreeDocsMenuShow)
    }, 100)
  })
}

/**
 * 关闭右键菜单, 有子菜单时, 点击菜单不会关闭
 *
 * @param event 点击事件
 */
const closeTreeDocsMenuShow = (event?: MouseEvent) => {
  if (event && event?.target) {
    let isPrevent = (event.target as HTMLElement).getAttribute('data-bl-prevet')
    if (isPrevent === 'true') {
      event.preventDefault()
      return
    }
  }

  document.body.removeEventListener('click', closeTreeDocsMenuShow)
  rMenu.value.show = false
}

/**
 * 显示右键二级菜单
 * @param event
 * @param childMenuCount 二级菜单的个数, 用于处理二级菜单显示位置
 */
const handleHoverRightMenuLevel2 = (event: MouseEvent, childMenuCount: number = 1) => {
  const domHeight = 30 * childMenuCount + 10
  if (document.body.clientHeight - event.clientY <= domHeight) {
    rMenuLevel2.value.top = domHeight * -1 + 30 + 'px'
  } else {
    rMenuLevel2.value.top = '0px'
  }
}

/**
 * 重命名文章
 */
const rename = () => {
  curDoc.value.updn = true
  notAllowDragKey = curDoc.value.i
  nextTick(() => {
    let ele = document.getElementById('article-doc-name-' + curDoc.value.i)
    if (ele) ele.focus()
  })
}

/**
 * 重命名文章失去焦点
 */
const blurArticleNameInput = (doc: DocTree) => {
  if (doc.ty === 3) {
    articleUpdNameApi({ id: doc.i, name: doc.n }).then((_resp) => {
      doc.updn = false
      notAllowDragKey = ''
    })
  } else {
    folderUpdNameApi({ id: doc.i, name: doc.n }).then((_resp) => {
      doc.updn = false
      notAllowDragKey = ''
    })
  }
}

/** 打开新页面, 文件夹(curDoc.value.ty == 1)无法使用新页面打开 */
const openArticleWindow = () => {
  if (curDoc.value.ty === 1) return
  openNewArticleWindow(curDoc.value.n, curDoc.value.i)
}

/**
 * 使用浏览器打开公开链接, 或复制公开链接
 * @param type:
 *  open      : 网页端查看
 *  copy      : 复制网页端链接
 *  link      : 复制双链引用
 *  tempVisit : 临时访问链接
 * @param open: 生成链接后是否直接打开
 */
const createUrl = (type: 'open' | 'copy' | 'link' | 'tempVisit', open: boolean = false) => {
  let url: string = user.userParams.WEB_ARTICLE_URL + curDoc.value.i
  if (type === 'open') {
    openExtenal(url)
  } else if (type === 'copy') {
    writeText(url)
  } else if (type === 'link') {
    createUrlLink(curDoc.value.n, curDoc.value.i)
  } else if (type === 'tempVisit') {
    articleTempKey({ id: curDoc.value.i }).then((resp) => {
      url = server.serverUrl + articleTempH + resp.data
      writeText(url)
      if (open) {
        openExtenal(url)
      }
    })
  }
}

/**
 * 复制双链引用
 * @param name : 文章名称
 * @param id   : 文章ID
 */
const createUrlLink = (name: string, id: string) => {
  let url = `[${name}](${user.userParams.WEB_ARTICLE_URL + id} "${grammar}${id}${grammar}")`
  writeText(url)
}

/**
 * 下载文章
 */
const articleDownload = () => articleDownloadApi({ id: curDoc.value.i }).then((resp) => downloadTextPlain(resp))

/**
 * 下载HTML文章
 */
const articleDownloadHtml = () => articleDownloadHtmlApi({ id: curDoc.value.i }).then((resp) => downloadTextPlain(resp))

/**
 * 批量导出文章
 * @param type 导出类型
 */
const articleBackup = (type: 'MARKDOWN' | 'HTML') => {
  articleBackupApi({ type: type, articleId: curDoc.value.i, toLocal: 'YES' }).then((resp) => {
    ElMessageBox.confirm(
      `由于导出为本地文章时需要导出图片等信息，所以文章将会以
    <span style="color:#C02B2B;text-decoration: underline;">备份压缩包</span>
    的形式存储在服务器上，文件名为：「${resp.data.filename}」，你可以前往备份页面查看导出进度和导出文件压缩包。`,
      {
        confirmButtonText: '立即查看',
        cancelButtonText: '稍后再说',
        type: 'info',
        draggable: true,
        dangerouslyUseHTMLString: true
      }
    ).then(() => {
      ArticleTreeWorkbenchRef.value.handleShowBackupDialog()
    })
  })
}

/** 同步文档 */
const syncDoc = () => {
  articleSyncApi({ id: curDoc.value.i }).then((_resp) => {
    Notify.success('同步成功')
    curDoc.value.vd = 0
  })
}

/**
 * 删除文档, 删除后将文档从树状节点中删除
 */
const delDoc = () => {
  let type = curDoc.value.ty === 3 ? '文章' : '文件夹'
  ElMessageBox.confirm(
    `<strong>注意：</strong><br/>
    1. 公开访问记录将永久删除。<br/>
    2. 双链引用将永久删除，还原后续重新编辑才可再次生成。<br/>
    是否继续删除${type}: <span style="color:#C02B2B;text-decoration: underline;">${curDoc.value.n}</span>？`,
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '我再想想',
      type: 'info',
      draggable: true,
      dangerouslyUseHTMLString: true
    }
  ).then(() => {
    if (curDoc.value.ty === 3) {
      articleDelApi({ id: curDoc.value.i }).then((_resp) => {
        Notify.success(`删除文章成功`)
        DocTreeRef.value.remove(curDoc.value.i)
        closeParentIfNoChild(curDoc.value.p)
      })
    } else {
      folderDelApi({ id: curDoc.value.i }).then((_resp) => {
        Notify.success(`删除文件夹成功`)
        DocTreeRef.value.remove(curDoc.value.i)
        closeParentIfNoChild(curDoc.value.p)
      })
    }
  })
}

/**
 * 在文件夹下新增文件夹
 */
const addFolderToDoc = () => addFolder(curDoc.value.i)

/**
 * 在根目录添加文件夹
 */
const addFolderToRoot = () => addFolder('0')

/**
 * 在指定 pid 的末尾新增文件夹
 *
 * @param pid 父ID
 */
const addFolder = (pid: string) => {
  // 将文件夹新增至尾部
  folderAddApi({ pid: pid, name: '新文件夹', storePath: '/', type: 1, icon: 'wl-folder', sort: 0, addToLast: true }).then((resp) => {
    const newFolder: DocTree = {
      i: resp.data.id,
      p: resp.data.pid,
      n: resp.data.name,
      updn: true,
      s: resp.data.sort,
      icon: resp.data.icon,
      o: 0,
      t: [],
      ty: 1,
      star: 0,
      showSort: isShowSort.value
    }
    addDocToTail(newFolder)
  })
}

/**
 * 在文件夹下新增文章
 */
const addArticleToDoc = () => addArticle(curDoc.value.i)

/**
 * 在根目录添加文章
 */
const addArticleToRoot = () => addArticle('0')

/**
 * 在指定 pid 的末尾新增文章
 *
 * @param pid 父ID
 */
const addArticle = (pid: string) => {
  // 将文章新增至尾部
  articleAddApi({ pid: pid, name: '新文章', addToLast: true }).then((resp) => {
    const newArticle: DocTree = {
      i: resp.data.id,
      p: resp.data.pid,
      n: resp.data.name,
      updn: true,
      o: 0,
      t: [],
      s: resp.data.sort,
      icon: '',
      ty: 3,
      star: 0
    }
    addDocToTail(newArticle)
  })
}

/**
 * 将文档添加至末尾并重命名
 */
const addDocToTail = (doc: DocTree) => {
  if (doc.p !== '0') {
    // 插入到根目录
    DocTreeRef.value.append(doc, DocTreeRef.value.getNode(doc.p))
    docTreeCurrentExpandId.value.add(doc.p)
  } else {
    docTreeData.value.push(doc)
  }
  nextTick(() => {
    let ele = document.getElementById('article-doc-name-' + doc.i) as HTMLInputElement
    setTimeout(() => {
      if (ele) ele.select()
    }, 100)
  })
}

/**
 * 公开/取消公开
 */
const open = (openStatus: 0 | 1) => {
  if (curDoc.value.ty === 3) {
    articleOpenApi({ id: curDoc.value.i, openStatus: openStatus }).then((_) => {
      curDoc.value.o = openStatus
      Notify.success(openStatus === 0 ? '取消公开成功' : '公开成功')
    })
  } else {
    folderOpenApi({ id: curDoc.value.i, openStatus: openStatus }).then((_) => {
      curDoc.value.o = openStatus
      Notify.success(openStatus === 0 ? '取消公开成功' : '公开成功')
    })
  }
}

/**
 * 公开/取消公开
 */
const openBactch = (openStatus: 0 | 1) => {
  const callback = () => {
    const parent: Node = DocTreeRef.value.getNode(curDoc.value.i)
    if (parent && !isEmpty(parent.childNodes)) {
      for (const doc of parent.childNodes) {
        if (doc.data.ty === 3) {
          doc.data.o = openStatus
        }
      }
    }
  }

  if (curDoc.value.ty === 1) {
    articleOpenBatchApi({ pid: curDoc.value.i, openStatus: openStatus }).then((_) => {
      callback()
      Notify.success(openStatus === 0 ? '取消公开成功' : '公开成功')
    })
  } else {
    articleOpenBatchApi({ pid: curDoc.value.i, openStatus: openStatus }).then((_) => {
      callback()
      Notify.success(openStatus === 0 ? '取消公开成功' : '公开成功')
    })
  }
}

/**
 * 收藏/取消收藏
 */
const star = (starStatus: 0 | 1) => {
  if (curDoc.value.ty === 3) {
    articleStarApi({ id: curDoc.value.i, starStatus: starStatus }).then(() => {
      curDoc.value.star = starStatus
      Notify.success(starStatus === 0 ? '取消 Star 成功' : 'Star 成功')
    })
  } else if (curDoc.value.ty === 1) {
    folderStarApi({ id: curDoc.value.i, starStatus: starStatus }).then(() => {
      curDoc.value.star = starStatus
      Notify.success(starStatus === 0 ? '取消文件夹 Star 成功' : '文件夹 Star 成功')
    })
  }
}

/**
 * 设为专题目录
 */
const addArticleTag = (tag: string) => {
  articleUpdTagApi({ id: curDoc.value.i, tag: tag }).then((resp) => {
    curDoc.value.t = resp.data
  })
}

/**
 * 设为专题
 */
const addFolderTag = (tag: string) => {
  folderUpdTagApi({ id: curDoc.value.i, tag: tag }).then((resp) => {
    curDoc.value.t = resp.data
  })
}
//#endregion

//#region ----------------------------------------< 二维码 >--------------------------------------
const ArticleQrCodeRef = ref()
const isShowQrCodeDialog = ref<boolean>(false)

const handleArticleQrCodeDialog = () => {
  isShowQrCodeDialog.value = true
  nextTick(() => ArticleQrCodeRef.value.getArticleQrCode(curDoc.value.n, curDoc.value.i))
}

//#endregion

//#region ----------------------------------------< 文章详情 >--------------------------------------
const ArticleInfoRef = ref()
const isShowDocInfoDialog = ref<boolean>(false)

/**
 * 显示弹框
 * @param dialogType 弹框的类型, 新增, 修改
 * @param pid 父级ID, 新增同级或子集文档时使用
 */
const handleShowDocInfoDialog = (dialogType: DocDialogType, pid?: number) => {
  if (Number(curDoc.value.i) < 0) {
    Notify.info('当前文档为系统默认文档, 无法操作')
    return
  }
  if (dialogType === 'upd' && (curDoc.value == undefined || curDoc.value.i == undefined)) {
    Notify.info('请先选则要修改的文件夹或文档')
    return
  }
  isShowDocInfoDialog.value = true
  if (dialogType === 'add') nextTick(() => ArticleInfoRef.value.reload(dialogType, undefined, undefined, pid))
  if (dialogType === 'upd') nextTick(() => ArticleInfoRef.value.reload(dialogType, curDoc.value.i, curDoc.value.ty))
}

/**
 * 保存后回调
 *
 * @param dialogType 保存类型
 * @param doc 文档
 */
const savedCallback = (_dialogType: DocDialogType, doc: DocInfo) => {
  isShowDocInfoDialog.value = false
  const oldDoc: DocTree = DocTreeRef.value.getNode(doc.id).data
  oldDoc.n = doc.name
  oldDoc.t = doc.tags
  oldDoc.icon = doc.icon!
  oldDoc.o = doc.openStatus
  oldDoc.star = doc.starStatus
  if (oldDoc.s !== doc.sort || oldDoc.p !== doc.pid) {
    getDocTree(() => {
      collapseNoChild()
    })
    // oldDoc.s = doc.sort
    // oldDoc.p = doc.pid
  }
}

//#endregion

//#region ----------------------------------------< 导入文章 >--------------------------------------
const ArticleImportRef = ref()
const isShowArticleImportDialog = ref<boolean>(false)

const handleShowArticleImportDialog = () => {
  isShowArticleImportDialog.value = true
}

//#endregion

//#region ----------------------------------------< 全文搜索 >--------------------------------------
const isShowArticleSearchDialog = ref<boolean>(false)

const handleShowArticleSearchDialog = () => {
  isShowArticleSearchDialog.value = true
}

const openArticle = (article: DocTree) => {
  const articleId = article.i
  emits('clickDoc', { ty: 3, i: articleId })
  nextTick(() => {
    docTreeCurrentId.value = articleId
    const parentNode = DocTreeRef.value.getNode(articleId)
    setCurrentKey({ i: articleId, p: parentNode.data.p, ty: 3 })
    isShowArticleSearchDialog.value = false
  })
}
//#endregion

//#region ----------------------------------------< 临时访问时长 >--------------------------------------
const isShowTempVisitDialog = ref(false)
const ArticleTempVisitRef = ref()
const handleShowTempVisitDialog = () => {
  isShowTempVisitDialog.value = true
  nextTick(() => {
    ArticleTempVisitRef.value.reload(curDoc.value.n, curDoc.value.i)
  })
}

const tempVisitCreated = () => {
  isShowTempVisitDialog.value = false
}
//#endregion

const emits = defineEmits(['clickDoc'])
defineExpose({ getDocTreeData })
</script>

<style scoped lang="scss">
@import '../doc/doc-tree.scss';
@import '../doc/doc-tree-detail.scss';
</style>
