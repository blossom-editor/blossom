<template>
  <!-- 文件夹操作 -->
  <div class="doc-workbench">
    <ArticleTreeWorkbench @refresh-doc-tree="getDocTree" @show-sort="handleShowSort" ref="ArticleTreeWorkbenchRef"> </ArticleTreeWorkbench>
  </div>
  <!--   -->
  <div
    class="doc-trees-container"
    v-loading="docTreeLoading"
    element-loading-text="正在读取文档..."
    :style="{ fontSize: viewStyle.treeDocsFontSize }">
    <el-menu v-if="!isEmpty(docTreeData)" class="doc-trees" :unique-opened="true" :default-active="docTreeActiveArticleId">
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

  <!-- 右键菜单, 添加到 body 下 -->
  <Teleport to="body">
    <div
      v-show="rMenu.show"
      class="doc-tree-right-menu"
      :style="{ left: rMenu.clientX + 'px', top: rMenu.clientY + 'px' }"
      ref="ArticleDocTreeRightMenuRef">
      <div class="doc-name">{{ curDoc.n }}</div>
      <div class="menu-content">
        <div @click="handleShowDocInfoDialog('upd')"><span class="iconbl bl-a-fileedit-line"></span>编辑文档</div>
        <div @click="syncDoc()"><span class="iconbl bl-a-cloudrefresh-line"></span>同步文档</div>
        <div @click="handleShowDocInfoDialog('add', curDoc.p)"><span class="iconbl bl-a-fileadd-line"></span>新增同级文档</div>
        <div v-if="curDoc.ty != 3" @click="handleShowDocInfoDialog('add', curDoc.i)"><span class="iconbl bl-a-fileadd-fill"></span>新增子级文档</div>
        <div @click="delDoc()"><span class="iconbl bl-a-fileprohibit-line"></span>删除文档</div>
        <div v-if="curDoc.ty === 3" @click="createUrl('link')"><span class="iconbl bl-correlation-line"></span>复制引用</div>
        <div v-if="curDoc.ty != 3" @click="handleShowArticleImportDialog()"><span class="iconbl bl-file-upload-line"></span>导入文章</div>

        <div class="menu-item-divider" v-if="curDoc.ty === 3"></div>
        <div v-if="curDoc.ty === 3" @click="openArticleWindow"><span class="iconbl bl-a-computerend-line"></span>新窗口打开</div>
        <div v-if="curDoc.ty === 3 && curDoc.o === 1" @click="createUrl('open')"><span class="iconbl bl-planet-line"></span>浏览器打开</div>
        <div v-if="curDoc.ty === 3" @mouseenter="handleHoverRightMenuLevel2($event, 4)">
          <span class="iconbl bl-a-rightsmallline-line"></span>
          <span class="iconbl bl-file-download-line"></span>导出文章
          <div class="menu-content-level2" :style="rMenuLevel2">
            <div @click="articleDownload"><span class="iconbl bl-file-markdown"></span>导出为 MD</div>
            <div @click="articleBackup('MARKDOWN')"><span class="iconbl bl-file-markdown"></span>导出为本地 MD</div>
            <div @click="articleDownloadHtml"><span class="iconbl bl-HTML"></span>导出为 HTML</div>
            <div @click="articleBackup('HTML')"><span class="iconbl bl-HTML"></span>导出为本地 HTML</div>
          </div>
        </div>
        <div v-if="curDoc.ty === 3 && curDoc.o === 1" @click="createUrl('copy')"><span class="iconbl bl-a-linkspread-line"></span>复制链接</div>
        <div v-if="curDoc.ty === 3 && curDoc.o === 1" @click="handleArticleQrCodeDialog()">
          <span class="iconbl bl-qr-code-line"></span>查看二维码
        </div>
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
    top="100px"
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
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'
import { useUserStore } from '@renderer/stores/user'
import { useConfigStore } from '@renderer/stores/config'
import { ref, onActivated, provide, onBeforeUnmount, nextTick, onMounted } from 'vue'
import { ElMessageBox } from 'element-plus'
import { ArrowDownBold, ArrowRightBold } from '@element-plus/icons-vue'
import { articleDownloadHtmlApi, docTreeApi } from '@renderer/api/blossom'
import { isNotNull } from '@renderer/assets/utils/obj'
import { isEmpty } from 'lodash'
import { provideKeyDocTree } from '@renderer/views/doc/doc'
import { grammar } from './scripts/markedjs'
import { folderDelApi, articleDownloadApi, articleSyncApi, articleDelApi, articleBackupApi } from '@renderer/api/blossom'
import { openExtenal, writeText, openNewArticleWindow } from '@renderer/assets/utils/electron'
import Notify from '@renderer/scripts/notify'

// components
import ArticleTreeTitle from './ArticleTreeTitle.vue'
import ArticleTreeWorkbench from './ArticleTreeWorkbench.vue'
import ArticleQrCode from './ArticleQrCode.vue'
import ArticleInfo from './ArticleInfo.vue'
import ArticleImport from './ArticleImport.vue'

const { userinfo } = useUserStore()
const { viewStyle } = useConfigStore()
const route = useRoute()

onMounted(() => {
  getDocTree(false, false, false)
  getRouteQueryParams()
})

onActivated(() => {
  getDocTree(false, false, false)
  getRouteQueryParams()
})

onBeforeUnmount(() => {
  document.body.removeEventListener('click', closeTreeDocsMenuShow)
  document.body.removeEventListener('contextmenu', closeTreeDocsMenuShow)
})

//#region ----------------------------------------< 菜单 >--------------------------------------
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
  }
}
/**
 * 获取文档树状列表
 * 1. 初始化是全否调用
 * 2. 在 workbench 中点击按钮调用, 每个按钮是单选的
 */
const getDocTree = (isOnlyOpen: boolean, isOnlySubject: boolean, isOnlyStar: boolean) => {
  docTreeLoading.value = true
  docTreeApi({ onlyPicture: false, onlyOpen: isOnlyOpen, onlySubject: isOnlySubject, onlyStar: isOnlyStar })
    .then((resp) => {
      docTreeData.value = resp.data
      concatSort(docTreeData.value)
    })
    .finally(() => {
      docTreeLoading.value = false
    })
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
 * 是否显示
 */
const handleShowSort = () => {
  showSort.value = !showSort.value
  concatSort(docTreeData.value)
}

//#endregion

//#region ----------------------------------------< 右键菜单 >--------------------------------------
const curDoc = ref<DocTree>({ i: 0, p: 0, n: '选择菜单', o: 0, t: [], s: 0, icon: '', ty: 1, star: 0 })
const rMenu = ref<RightMenu>({ show: false, clientX: 0, clientY: 0 })
const rMenuLevel2 = ref<RightMenuLevel2>({ top: '0px' })
const ArticleDocTreeRightMenuRef = ref()
const ArticleTreeWorkbenchRef = ref()

/**
 * 显示有检查菜单
 * @param doc 文档
 * @param event 事件
 */
const handleClickRightMenu = (doc: DocTree, event: MouseEvent) => {
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

const closeTreeDocsMenuShow = () => {
  document.body.removeEventListener('click', closeTreeDocsMenuShow)
  rMenu.value.show = false
}

const handleHoverRightMenuLevel2 = (event: MouseEvent, childMenuCount: number = 1) => {
  const domHeight = 30 * childMenuCount + 10
  if (document.body.clientHeight - event.clientY <= domHeight) {
    rMenuLevel2.value.top = domHeight * -1 + 20 + 'px'
  } else {
    rMenuLevel2.value.top = '0px'
  }
}

/**
 * 打开新页面, 文件夹(curDoc.value.ty == 1)无法使用新页面打开
 */
const openArticleWindow = () => {
  if (curDoc.value.ty === 1) return
  openNewArticleWindow(curDoc.value.n, curDoc.value.i)
}

/**
 * 使用浏览器打开公开链接, 或复制公开链接
 */
const createUrl = (type: 'open' | 'copy' | 'link') => {
  let url: string = userinfo.params.WEB_ARTICLE_URL + curDoc.value.i
  if (type == 'open') {
    openExtenal(url)
  } else if (type == 'copy') {
    writeText(url)
  } else if (type == 'link') {
    url = `[${curDoc.value.n}](${userinfo.params.WEB_ARTICLE_URL + curDoc.value.i} "${grammar}${curDoc.value.i}${grammar}")`
    writeText(url)
  }
}

/**
 * 下载文章
 */
const articleDownload = () => {
  articleDownloadApi({ id: curDoc.value.i }).then((resp) => {
    let filename: string = resp.headers.get('content-disposition')
    let filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/
    let matches = filenameRegex.exec(filename)
    if (matches != null && matches[1]) {
      filename = decodeURI(matches[1].replace(/['"]/g, ''))
    }
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
 * 下载HTML文章
 */
const articleDownloadHtml = () => {
  articleDownloadHtmlApi({ id: curDoc.value.i }).then((resp) => {
    let filename: string = resp.headers.get('content-disposition')
    let filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/
    let matches = filenameRegex.exec(filename)
    if (matches != null && matches[1]) {
      filename = decodeURI(matches[1].replace(/['"]/g, ''))
    }
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

/**
 * 同步文档
 */
const syncDoc = () => {
  articleSyncApi({ id: curDoc.value.i }).then((_resp) => {
    Notify.success('同步成功')
    getDocTree(false, false, false)
  })
}

/**
 * 删除文档
 */
const delDoc = () => {
  let type = curDoc.value.ty === 3 ? '文章' : '文件夹'
  ElMessageBox.confirm(`是否确定删除${type}: <span style="color:#C02B2B;text-decoration: underline;">${curDoc.value.n}</span>？删除后将不可恢复！`, {
    confirmButtonText: '确定删除',
    cancelButtonText: '我再想想',
    type: 'info',
    draggable: true,
    dangerouslyUseHTMLString: true
  }).then(() => {
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
  if (curDoc.value.i < 0) {
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

//#region 导出

const ArticleImportRef = ref()
const isShowArticleImportDialog = ref<boolean>(false)

const handleShowArticleImportDialog = () => {
  isShowArticleImportDialog.value = true
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
