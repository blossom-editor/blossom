<template>
  <!-- 文件夹操作 -->
  <div class="doc-workbench">
    <ArticleTreeWorkbench
      @refresh-doc-tree="getDocTree"
      @show-sort="handleShowSort"
      @show-search="handleShowArticleSearchDialog"
      ref="ArticleTreeWorkbenchRef">
    </ArticleTreeWorkbench>
  </div>
  <!--   -->
  <div
    class="doc-trees-container"
    v-loading="docTreeLoading"
    element-loading-text="正在读取文档..."
    :style="{ fontSize: viewStyle.treeDocsFontSize }">
    <el-menu
      v-if="!isEmpty(docTreeData)"
      ref="DocTreeRef"
      class="doc-trees"
      :unique-opened="true"
      :default-active="docTreeActiveArticleId"
      @open="openMenu">
      <!-- ================================================ L1 ================================================ -->
      <div v-for="L1 in docTreeData" :key="L1.i">
        <!-- L1无下级 -->
        <el-menu-item v-if="isEmpty(L1.children)" :index="L1.i">
          <template #title>
            <div class="menu-item-wrapper" @click="clickCurDoc(L1)" @click.right="handleClickRightMenu(L1, $event)">
              <ArticleTreeTitle :trees="L1" :level="1" />
            </div>
          </template>
        </el-menu-item>

        <!-- L1有下级 -->
        <el-sub-menu v-else :expand-open-icon="ArrowDownBold" :expand-close-icon="ArrowRightBold" :index="L1.i">
          <template #title>
            <div class="menu-item-wrapper" @click.right="handleClickRightMenu(L1, $event)">
              <ArticleTreeTitle :trees="L1" :level="1" />
            </div>
          </template>

          <!-- ================================================ L2 ================================================ -->
          <div v-for="L2 in L1.children" :key="L2.i">
            <!-- L2无下级 -->
            <el-menu-item v-if="isEmpty(L2.children)" :index="L2.i">
              <template #title>
                <div class="menu-item-wrapper" @click="clickCurDoc(L2)" @click.right="handleClickRightMenu(L2, $event)">
                  <ArticleTreeTitle :trees="L2" :level="2" />
                </div>
              </template>
            </el-menu-item>

            <!-- L2有下级 -->
            <el-sub-menu v-else :expand-open-icon="ArrowDownBold" :expand-close-icon="ArrowRightBold" :index="L2.i">
              <template #title>
                <div class="menu-item-wrapper" @click.right="handleClickRightMenu(L2, $event)">
                  <ArticleTreeTitle :trees="L2" :level="2" />
                </div>
              </template>

              <!-- ================================================ L3 ================================================ -->
              <div v-for="L3 in L2.children" :key="L3.i">
                <!-- L3无下级 -->
                <el-menu-item v-if="isEmpty(L3.children)" :index="L3.i">
                  <template #title>
                    <div class="menu-item-wrapper" @click="clickCurDoc(L3)" @click.right="handleClickRightMenu(L3, $event)">
                      <ArticleTreeTitle :trees="L3" :level="3" />
                    </div>
                  </template>
                </el-menu-item>

                <!-- L3有下级 -->
                <el-sub-menu v-else :expand-open-icon="ArrowDownBold" :expand-close-icon="ArrowRightBold" :index="L3.i">
                  <template #title>
                    <div class="menu-item-wrapper" @click.right="handleClickRightMenu(L3, $event)">
                      <ArticleTreeTitle :trees="L3" :level="3" />
                    </div>
                  </template>

                  <!-- ================================================ L4 ================================================ -->
                  <div v-for="L4 in L3.children" :key="L4.i">
                    <!-- L4 不允许有下级, 只允许4级 -->
                    <el-menu-item v-if="isEmpty(L4.children)" :index="L4.i">
                      <template #title>
                        <div class="menu-item-wrapper" @click="clickCurDoc(L4)" style="width: 100%" @click.right="handleClickRightMenu(L4, $event)">
                          <ArticleTreeTitle :trees="L4" :level="4" />
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
    <div v-else class="doc-trees-placeholder">暂无文档，可点击上方 ↑ 添加</div>
  </div>

  <Teleport to="body">
    <div
      v-show="rMenu.show"
      class="doc-tree-right-menu"
      :style="{ left: rMenu.clientX + 'px', top: rMenu.clientY + 'px' }"
      ref="ArticleDocTreeRightMenuRef">
      <div class="doc-name">{{ curDoc.n }}</div>
      <div class="menu-content">
        <div @click="rename"><span class="iconbl bl-pen"></span>重命名</div>
        <div @click="handleShowDocInfoDialog('upd')"><span class="iconbl bl-a-fileedit-line"></span>编辑详情</div>
        <div v-if="curDoc.ty === 3" @click="syncDoc()"><span class="iconbl bl-a-cloudrefresh-line"></span>同步文章</div>
        <div v-if="curDoc.ty !== 3" @click="addFolder"><span class="iconbl bl-a-fileadd-line"></span>新增文件夹</div>
        <div v-if="curDoc.ty !== 3" @click="addArticle"><span class="iconbl bl-a-fileadd-fill"></span>新增笔记</div>
        <div v-if="curDoc.ty === 3" @click="createUrl('link')"><span class="iconbl bl-correlation-line"></span>复制双链引用</div>
        <div v-if="curDoc.ty !== 3" @click="handleShowArticleImportDialog()"><span class="iconbl bl-file-upload-line"></span>导入文章</div>

        <div @mouseenter="handleHoverRightMenuLevel2($event, 2)" data-bl-prevet="true">
          <span class="iconbl bl-a-rightsmallline-line"></span>
          <span class="iconbl bl-apps-line"></span>更多
          <div class="menu-content-level2" :style="rMenuLevel2">
            <div v-if="curDoc.o === 0" @click="open(1)"><span class="iconbl bl-a-cloudupload-line"></span>公开</div>
            <div v-if="curDoc.o === 1" @click="open(0)"><span class="iconbl bl-a-clouddownload-line"></span>取消公开</div>
            <div v-if="curDoc.star === 0 && curDoc.ty === 3" @click="star(1)"><span class="iconbl bl-star-fill"></span>收藏</div>
            <div v-if="curDoc.star === 1 && curDoc.ty === 3" @click="star(0)"><span class="iconbl bl-star-line"></span>取消收藏</div>
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
          </div>
        </div>

        <div v-if="curDoc.ty === 3" class="menu-item-divider"></div>
        <div v-if="curDoc.ty === 3" @click="openArticleWindow"><span class="iconbl bl-a-computerend-line"></span>新窗口查看</div>
        <div v-if="curDoc.ty === 3" @click="createUrl('tempVisit', true)"><span class="iconbl bl-visit"></span>浏览器临时访问</div>

        <div v-if="curDoc.ty === 3" @mouseenter="handleHoverRightMenuLevel2($event, 4)" data-bl-prevet="true">
          <span class="iconbl bl-a-rightsmallline-line"></span>
          <span class="iconbl bl-file-download-line"></span>导出文章
          <div class="menu-content-level2" :style="rMenuLevel2">
            <div @click="articleDownload"><span class="iconbl bl-file-markdown"></span>导出为 MD</div>
            <div @click="articleBackup('MARKDOWN')"><span class="iconbl bl-file-markdown"></span>导出为本地 MD</div>
            <div @click="articleDownloadHtml"><span class="iconbl bl-HTML"></span>导出为 HTML</div>
            <div @click="articleBackup('HTML')"><span class="iconbl bl-HTML"></span>导出为本地 HTML</div>
          </div>
        </div>
        <div v-if="curDoc.ty === 3" @mouseenter="handleHoverRightMenuLevel2($event, 2)" data-bl-prevet="true">
          <span class="iconbl bl-a-rightsmallline-line"></span>
          <span class="iconbl bl-a-linkspread-line"></span>创建链接
          <div class="menu-content-level2" :style="rMenuLevel2">
            <div v-if="curDoc.o === 1" @click="createUrl('copy')"><span class="iconbl bl-planet-line"></span>复制博客地址</div>
            <div @click="createUrl('tempVisit')"><span class="iconbl bl-visit"></span>创建临时访问(3h)</div>
            <div @click="handleShowACustomTempVisitDialog"><span class="iconbl bl-visit"></span>创建临时访问(自定义)</div>
          </div>
        </div>
        <div v-if="curDoc.ty === 3 && curDoc.o === 1" @click="createUrl('open')"><span class="iconbl bl-planet-line"></span>博客中查看</div>
        <div v-if="curDoc.ty === 3 && curDoc.o === 1" @click="handleArticleQrCodeDialog()">
          <span class="iconbl bl-qr-code-line"></span>博客二维码
        </div>
        <div class="menu-item-divider"></div>
        <div @click="delDoc()"><span class="iconbl bl-a-fileprohibit-line"></span>删除{{ curDocType }}</div>
      </div>
    </div>
  </Teleport>

  <!-- 详情 -->
  <el-dialog
    v-model="isShowDocInfoDialog"
    width="535"
    top="100px"
    style="margin-left: 320px"
    :append-to-body="true"
    :destroy-on-close="true"
    :close-on-click-modal="false"
    draggable>
    <ArticleInfo ref="ArticleInfoRef" @saved="savedCallback"></ArticleInfo>
  </el-dialog>

  <!-- 二维码 -->
  <el-dialog
    v-model="isShowQrCodeDialog"
    width="335"
    :align-center="true"
    :append-to-body="true"
    :destroy-on-close="true"
    :close-on-click-modal="false"
    draggable>
    <ArticleQrCode ref="ArticleQrCodeRef"></ArticleQrCode>
  </el-dialog>

  <!-- 导入 -->
  <el-dialog
    v-model="isShowArticleImportDialog"
    width="335"
    top="80px"
    :append-to-body="true"
    :destroy-on-close="true"
    :close-on-click-modal="false"
    draggable>
    <ArticleImport ref="ArticleImportRef" :doc="curDoc"></ArticleImport>
  </el-dialog>

  <!-- 搜索 -->
  <el-dialog
    v-model="isShowArticleSearchDialog"
    class="bl-dialog-hidden-header-fixed-body"
    width="705"
    style="height: 80%"
    :align-center="true"
    :append-to-body="true"
    :destroy-on-close="true"
    :close-on-click-modal="true">
    <ArticleSearch @open-article="openArticle" @create-link="createUrlLink"></ArticleSearch>
  </el-dialog>

  <!-- 自定义临时访问链接 -->
  <el-dialog
    v-model="isShowACustomTempVisitDialog"
    width="400"
    style="height: 200px"
    :align-center="true"
    :append-to-body="true"
    :destroy-on-close="true"
    :close-on-click-modal="true">
    <ArticleCustomTempVisit ref="ArticleCustomTempVisitRef" @created="tempVisitCreated"></ArticleCustomTempVisit>
  </el-dialog>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'
import { useServerStore } from '@renderer/stores/server'
import { useUserStore } from '@renderer/stores/user'
import { useConfigStore } from '@renderer/stores/config'
import { ref, provide, onBeforeUnmount, nextTick, computed } from 'vue'
import { ElMessageBox } from 'element-plus'
import type { MenuInstance } from 'element-plus'
import { ArrowDownBold, ArrowRightBold } from '@element-plus/icons-vue'
import {
  articleAddApi,
  articleUpdTagApi,
  articleDownloadHtmlApi,
  articleOpenApi,
  articleStarApi,
  docTreeApi,
  folderAddApi,
  folderUpdTagApi,
  folderOpenApi
} from '@renderer/api/blossom'
import { isNotNull } from '@renderer/assets/utils/obj'
import { isEmpty } from 'lodash'
import { checkLevel, provideKeyDocTree } from '@renderer/views/doc/doc'
import { useLifecycle } from '@renderer/scripts/lifecycle'
import { grammar } from './scripts/markedjs'
import {
  folderDelApi,
  articleDownloadApi,
  articleSyncApi,
  articleDelApi,
  articleBackupApi,
  articleTempKey,
  articleTempH
} from '@renderer/api/blossom'
import { openExtenal, writeText, openNewArticleWindow } from '@renderer/assets/utils/electron'
import Notify from '@renderer/scripts/notify'

// components
import ArticleTreeTitle from './ArticleTreeTitle.vue'
import ArticleTreeWorkbench from './ArticleTreeWorkbench.vue'
import ArticleQrCode from './ArticleQrCode.vue'
import ArticleInfo from './ArticleInfo.vue'
import ArticleImport from './ArticleImport.vue'
import ArticleSearch from './ArticleSearch.vue'
import ArticleCustomTempVisit from './ArticleCustomTempVisit.vue'

const server = useServerStore()
const user = useUserStore()
const { viewStyle } = useConfigStore()
const route = useRoute()

useLifecycle(
  () => {
    getDocTree(false, false, false)
    getRouteQueryParams()
  },
  () => {
    getDocTree(false, false, false)
    getRouteQueryParams()
  }
)

onBeforeUnmount(() => {
  document.body.removeEventListener('click', closeTreeDocsMenuShow)
  document.body.removeEventListener('contextmenu', closeTreeDocsMenuShow)
})

//#region ----------------------------------------< 菜单 >--------------------------------------
let editorLoadingTimeout: NodeJS.Timeout
const DocTreeRef = ref<MenuInstance>()
const docTreeLoading = ref(true) // 文档菜单的加载动画
const showSort = ref(false) // 是否显示文档排序
const docTreeActiveArticleId = ref('') // 文档的默认选中项, 用于外部跳转后选中菜单
const docTreeData = ref<DocTree[]>([]) // 文档菜单
provide(provideKeyDocTree, docTreeData) // 提供菜单列表依赖注入, 主要用于在详情中选择上级文件夹, 避免二次查询

/**
 * 获取路由参数
 */
const getRouteQueryParams = () => {
  let articleId = route.query.articleId
  if (isNotNull(articleId)) {
    docTreeActiveArticleId.value = articleId as string
    let treeParam: any = { ty: 3, i: articleId }
    clickCurDoc(treeParam)
    nextTick(() => {
      docTreeActiveArticleId.value = articleId as string
    })
  }
}
/**
 * 获取文档树状列表
 * 1. 初始化是全否调用
 * 2. 在 workbench 中点击按钮调用, 每个按钮是单选的
 */
const getDocTree = (isOnlyOpen: boolean, isOnlySubject: boolean, isOnlyStar: boolean) => {
  startLoading()
  docTreeApi({ onlyPicture: false, onlyOpen: isOnlyOpen, onlySubject: isOnlySubject, onlyStar: isOnlyStar })
    .then((resp) => {
      docTreeData.value = resp.data
      concatSort(docTreeData.value)
      endLoading()
    })
    .finally(() => endLoading())
}

/**
 * 在名称中显式排序
 * @param trees
 */
const concatSort = (trees: DocTree[]) => {
  for (let i = 0; i < trees.length; i++) {
    if (!isEmpty(trees[i].children)) {
      concatSort(trees[i].children as DocTree[])
    }
    trees[i].showSort = showSort.value
  }
}

const getDocTreeData = (): DocTree[] => {
  return docTreeData.value
}

/**
 * 菜单中选中文章
 * @param index 文章ID
 */
const openMenu = (index: string) => {
  docTreeActiveArticleId.value = index
}

/**
 * 是否显示
 */
const handleShowSort = () => {
  showSort.value = !showSort.value
  concatSort(docTreeData.value)
}

/**
 * 开始加载
 */
const startLoading = () => {
  if (!editorLoadingTimeout) {
    editorLoadingTimeout = setTimeout(() => (docTreeLoading.value = true), 100)
  }
}

/**
 * 结束加载
 */
const endLoading = () => {
  if (editorLoadingTimeout) {
    clearTimeout(editorLoadingTimeout)
  }
  docTreeLoading.value = false
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
const handleClickRightMenu = (doc: DocTree, event: MouseEvent) => {
  event.preventDefault()
  if (!doc) {
    return
  }
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

const closeTreeDocsMenuShow = (event: MouseEvent) => {
  if (event.target) {
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

/** 重命名文章 */
const rename = () => {
  curDoc.value.updn = true
  nextTick(() => {
    let ele = document.getElementById('article-doc-name-' + curDoc.value.i)
    if (ele) ele.focus()
  })
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

/** 下载文章 */
const articleDownload = () => {
  articleDownloadApi({ id: curDoc.value.i }).then((resp) => {
    let filename: string = resp.headers.get('content-disposition')
    let filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/
    let matches = filenameRegex.exec(filename)
    if (matches != null && matches[1]) {
      filename = decodeURI(matches[1].replace(/['"]/g, ''))
    }
    filename = decodeURI(filename)
    let a = document.createElement('a')
    let blob = new Blob([resp.data], { type: 'text/plain' })
    let objectUrl = URL.createObjectURL(blob)
    a.setAttribute('href', objectUrl)
    a.setAttribute('download', filename)
    a.click()
    URL.revokeObjectURL(a.href)
    a.remove()
  })
}

/** 下载HTML文章 */
const articleDownloadHtml = () => {
  articleDownloadHtmlApi({ id: curDoc.value.i }).then((resp) => {
    let filename: string = resp.headers.get('content-disposition')
    console.log(decodeURI(filename))

    let filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/
    let matches = filenameRegex.exec(filename)
    if (matches != null && matches[1]) {
      filename = decodeURI(matches[1].replace(/['"]/g, ''))
    }
    filename = decodeURI(filename)
    let a = document.createElement('a')
    let blob = new Blob([resp.data], { type: 'text/plain' })
    let objectUrl = URL.createObjectURL(blob)
    a.setAttribute('href', objectUrl)
    a.setAttribute('download', filename)
    a.click()
    URL.revokeObjectURL(a.href)
    a.remove()
  })
}

/**
 * 导出文章
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
    getDocTree(false, false, false)
  })
}

/** 删除文档 */
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
      // type: 'warning',
      draggable: true,
      dangerouslyUseHTMLString: true
    }
  ).then(() => {
    if (curDoc.value.ty === 3) {
      articleDelApi({ id: curDoc.value.i }).then((_resp) => {
        Notify.success(`删除文章成功`)
        getDocTree(false, false, false)
      })
    } else {
      folderDelApi({ id: curDoc.value.i }).then((_resp) => {
        Notify.success(`删除文件夹成功`)
        getDocTree(false, false, false)
      })
    }
  })
}

/** 在末尾新增文件夹 */
const addFolder = () => {
  if (!checkLevel(curDoc.value.i, docTreeData.value)) {
    return
  }
  // 将文件夹新增至尾部
  folderAddApi({ pid: curDoc.value.i, name: '新文件夹', storePath: '/', type: 1, icon: 'wl-folder', sort: 0, addToLast: true }).then((resp) => {
    let doc: DocTree = {
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
      showSort: curDoc.value.showSort
    }
    if (isEmpty(curDoc.value.children)) {
      curDoc.value.children = [doc]
    } else {
      curDoc.value.children!.push(doc)
    }
    nextTick(() => {
      DocTreeRef.value!.open(curDoc.value.i.toString())
      nextTick(() => {
        let ele = document.getElementById('article-doc-name-' + doc.i)
        if (ele) ele.focus()
      })
    })
  })
}

/** 在末尾新增文章 */
const addArticle = () => {
  if (!checkLevel(curDoc.value.i, docTreeData.value)) {
    return
  }
  // 将文章新增至尾部
  articleAddApi({ pid: curDoc.value.i, name: '新文章', addToLast: true }).then((resp) => {
    let doc: DocTree = {
      i: resp.data.id,
      p: resp.data.pid,
      n: resp.data.name,
      updn: true,
      o: 0,
      t: [],
      s: resp.data.sort,
      icon: '',
      ty: 3,
      star: 0,
      showSort: curDoc.value.showSort
    }
    if (isEmpty(curDoc.value.children)) {
      curDoc.value.children = [doc]
    } else {
      curDoc.value.children!.push(doc)
    }
    nextTick(() => {
      DocTreeRef.value!.open(curDoc.value.i.toString())
      nextTick(() => {
        let ele = document.getElementById('article-doc-name-' + doc.i)
        if (ele) ele.focus()
      })
    })
  })
}

/** 公开/取消公开 */
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

/** 收藏/取消收藏 */
const star = (starStatus: 0 | 1) => {
  articleStarApi({ id: curDoc.value.i, starStatus: starStatus }).then(() => {
    curDoc.value.star = starStatus
    Notify.success(starStatus === 0 ? '取消 Star 成功' : 'Star 成功')
  })
}

/** 设为专题目录 */
const addArticleTag = (tag: string) => {
  articleUpdTagApi({ id: curDoc.value.i, tag: tag }).then((resp) => {
    curDoc.value.t = resp.data
  })
}

/** 设为专题 */
const addFolderTag = (tag: string) => {
  folderUpdTagApi({ id: curDoc.value.i, tag: tag }).then((resp) => {
    curDoc.value.t = resp.data
  })
}
//#endregion

//#region ----------------------------------------< 二维码 >--------------------------------------

const isShowQrCodeDialog = ref<boolean>(false)
const ArticleQrCodeRef = ref()

const handleArticleQrCodeDialog = () => {
  isShowQrCodeDialog.value = true
  nextTick(() => {
    ArticleQrCodeRef.value.getArticleQrCode(curDoc.value.n, curDoc.value.i)
  })
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
  if (dialogType === 'add') {
    nextTick(() => {
      ArticleInfoRef.value.reload(dialogType, undefined, undefined, pid)
    })
  }
  if (dialogType === 'upd') {
    nextTick(() => {
      ArticleInfoRef.value.reload(dialogType, curDoc.value.i, curDoc.value.ty)
    })
  }
}

/**
 * 保存后回调
 * @param dialogType
 */
const savedCallback = (_dialogType: DocDialogType) => {
  getDocTree(false, false, false)
  isShowDocInfoDialog.value = false
}

//#endregion

//#region ----------------------------------------< 导入文章 >--------------------------------------
const ArticleImportRef = ref()
const isShowArticleImportDialog = ref<boolean>(false)

const handleShowArticleImportDialog = () => {
  if (!checkLevel(curDoc.value.i, docTreeData.value)) {
    return
  }
  isShowArticleImportDialog.value = true
}

//#endregion

//#region ----------------------------------------< 搜索文章 >--------------------------------------
const isShowArticleSearchDialog = ref<boolean>(false)

const handleShowArticleSearchDialog = () => {
  isShowArticleSearchDialog.value = true
}

const openArticle = (article: DocTree) => {
  openMenu(article.i)
  emits('clickDoc', article)
  isShowArticleSearchDialog.value = false
  nextTick(() => {
    docTreeActiveArticleId.value = article.i
  })
}
//#endregion

//#region ----------------------------------------< 临时访问时长 >--------------------------------------
const isShowACustomTempVisitDialog = ref(false)
const ArticleCustomTempVisitRef = ref()
const handleShowACustomTempVisitDialog = () => {
  isShowACustomTempVisitDialog.value = true
  nextTick(() => {
    ArticleCustomTempVisitRef.value.reload(curDoc.value.n, curDoc.value.i)
  })
}

const tempVisitCreated = () => {
  isShowACustomTempVisitDialog.value = false
}
//#endregion

const clickCurDoc = (tree: DocTree) => {
  emits('clickDoc', tree)
}

const emits = defineEmits(['clickDoc'])

defineExpose({ getDocTreeData })
</script>

<style scoped lang="scss">
@import '../doc/tree-docs.scss';
@import '../doc/tree-docs-right-menu.scss';
</style>
