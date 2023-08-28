<template>
  <!-- 文件夹操作 -->
  <div class="doc-workbench">
    <ArticleTreeWorkbench @refresh-doc-tree="getDocTree" @show-sort="handleShowSort"></ArticleTreeWorkbench>
  </div>

  <div class="doc-trees-container" v-loading="docTreeLoading" element-loading-text="正在读取文档...">

    <el-menu v-if="!isEmpty(docTreeData)" class="doc-trees" :unique-opened="true" :default-active="docTreeDefaultActive">
      <!-- ================================================ L1 ================================================ -->
      <div v-for="L1 in docTreeData" :key="L1.i">

        <!-- L1无下级 -->
        <el-menu-item v-if="isEmpty(L1.children)" :index="L1.i">
          <template #title>
            <div class="menu-item-wrapper" @click="clickCurDoc(L1)" @click.right="handleClickRight(L1, $event)">
              <ArticleTreeTitle :trees="L1" />
            </div>
          </template>
        </el-menu-item>

        <!-- L1有下级 -->
        <el-sub-menu v-else :expand-open-icon="ArrowDownBold" :expand-close-icon="ArrowRightBold" :index="L1.i">
          <template #title>
            <div class="menu-item-wrapper" @click.right="handleClickRight(L1, $event)">
              <ArticleTreeTitle :trees="L1" />
            </div>
          </template>

          <!-- ================================================ L2 ================================================ -->
          <div v-for="L2 in L1.children" :key="L2.i">
            <!-- L2无下级 -->
            <el-menu-item v-if="isEmpty(L2.children)" :index="L2.i">
              <template #title>
                <div class="menu-item-wrapper" @click="clickCurDoc(L2)" @click.right="handleClickRight(L2, $event)">
                  <ArticleTreeTitle :trees="L2" />
                </div>
              </template>
            </el-menu-item>

            <!-- L2有下级 -->
            <el-sub-menu v-else :expand-open-icon="ArrowDownBold" :expand-close-icon="ArrowRightBold" :index="L2.i">
              <template #title>
                <div class="menu-item-wrapper" @click.right="handleClickRight(L2, $event)">
                  <ArticleTreeTitle :trees="L2" />
                </div>
              </template>

              <!-- ================================================ L3 ================================================ -->
              <div v-for="L3 in L2.children" :key="L3.i">
                <!-- L3无下级 -->
                <el-menu-item v-if="isEmpty(L3.children)" :index="L3.i">
                  <template #title>
                    <div class="menu-item-wrapper" @click="clickCurDoc(L3)" @click.right="handleClickRight(L3, $event)">
                      <ArticleTreeTitle :trees="L3" />
                    </div>
                  </template>
                </el-menu-item>

                <!-- L3有下级 -->
                <el-sub-menu v-else :expand-open-icon="ArrowDownBold" :expand-close-icon="ArrowRightBold" :index="L3.i">
                  <template #title>
                    <div class="menu-item-wrapper" @click.right="handleClickRight(L3, $event)">
                      <ArticleTreeTitle :trees="L3" />
                    </div>
                  </template>

                  <!-- ================================================ L4 ================================================ -->
                  <div v-for="L4 in L3.children" :key="L4.i">
                    <!-- L4 不允许有下级, 只允许4级 -->
                    <el-menu-item v-if="isEmpty(L4.children)" :index="L4.i">
                      <template #title>
                        <div class="menu-item-wrapper" @click="clickCurDoc(L4)" style="width: 100%;"
                          @click.right="handleClickRight(L4, $event)">
                          <ArticleTreeTitle :trees="L4" />
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

  <!-- 右键菜单, 添加到 body 下 -->
  <Teleport to="body">
    <div v-if="rMenu.show" class="doc-tree-right-menu" :style="{ left: rMenu.clientX + 'px', top: rMenu.clientY + 'px' }">
      <div class="doc-name">{{ curDoc.n }}</div>
      <div class="menu-content">
        <div class="menu-item" @click="handleShowDocInfoDialog('upd')">
          <span class="iconbl bl-a-fileedit-line"></span>编辑文档
        </div>
        <div class="menu-item" @click="syncDoc()">
          <span class="iconbl bl-a-cloudrefresh-line"></span>同步文档
        </div>
        <div class="menu-item" @click="handleShowDocInfoDialog('add', curDoc.p)">
          <span class="iconbl bl-a-fileadd-line"></span>新增<strong>同级</strong>文档
        </div>
        <div :class="['menu-item', curDoc?.ty === 3 ? 'disabled' : '']" @click="handleShowDocInfoDialog('add', curDoc.i)">
          <span class="iconbl bl-a-fileadd-fill"></span>新增<strong>子级</strong>文档
        </div>
        <div class="menu-item" @click="delDoc()">
          <span class="iconbl bl-a-fileprohibit-line"></span>删除文档
        </div>
        <div :class="['menu-item', curDoc.ty === 1 ? 'disabled' : '']" @click="createUrl('link')">
          <span class="iconbl bl-correlation-line"></span>复制引用
        </div>

        <div class="menu-item-divider"></div>

        <div :class="['menu-item', curDoc.ty === 1 ? 'disabled' : '']" @click="openArticleWindow">
          <span class="iconbl bl-a-computerend-line"></span>新窗口打开
        </div>
        <div :class="['menu-item', curDoc.ty === 1 || curDoc.o != 1 ? 'disabled' : '']" @click="createUrl('open')">
          <span class="iconbl bl-planet-line"></span>浏览器打开
        </div>
        <div :class="['menu-item', curDoc.ty === 1 ? 'disabled' : '']" @click="articleDownload">
          <span class="iconbl bl-a-clouddownload-line"></span>下载文章
        </div>
        <div :class="['menu-item', curDoc.ty === 1 || curDoc.o != 1 ? 'disabled' : '']" @click="createUrl('copy')">
          <span class="iconbl bl-a-linkspread-line"></span>复制链接
        </div>
        <div :class="['menu-item', curDoc.ty === 1 || curDoc.o != 1 ? 'disabled' : '']"
          @click="handleArticleQrCodeDialog()">
          <span class="iconbl bl-a-qrcode1-line"></span>查看二维码
        </div>
      </div>
    </div>
  </Teleport>


  <!-- 详情的弹框 -->
  <el-dialog v-model="isShowDocInfoDialog" width="535" top="100px" style="margin-left: 65px;
    --el-dialog-padding-primary:0;
    --el-dialog-border-radius:10px;
    --el-dialog-box-shadow:var(--bl-box-shadow-dialog)" :append-to-body="true" :destroy-on-close="true"
    :close-on-click-modal="false" draggable>
    <ArticleInfo ref="ArticleInfoRef"></ArticleInfo>
  </el-dialog>

  <!-- 二维码的弹框 -->
  <el-dialog v-model="isShowQrCodeDialog" width="335" top="100px" style="
    --el-dialog-padding-primary:0;
    --el-dialog-border-radius:10px;
    --el-dialog-box-shadow:var(--bl-box-shadow-dialog)" :append-to-body="true" :destroy-on-close="true"
    :close-on-click-modal="false" draggable>
    <ArticleQrCode ref="ArticleQrCodeRef"></ArticleQrCode>
  </el-dialog>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'
import { useUserStore } from '@renderer/stores/user'
import { ref, onActivated, provide, onBeforeUnmount, nextTick, onMounted } from "vue"
import { ElMessageBox } from 'element-plus'
import { ArrowDownBold, ArrowRightBold } from '@element-plus/icons-vue'
import { docTreeApi } from '@renderer/api/blossom'
import { isNotNull } from "@renderer/assets/utils/obj"
import { isEmpty } from 'lodash'
import { provideKeyDocTree } from '@renderer/views/doc/doc'
import { grammar } from './scripts/markedjs'
import { folderDelApi, articleDownloadApi, articleSyncApi, articleDelApi } from '@renderer/api/blossom'
import { openExtenal, writeText, openNewArticleWindow } from '@renderer/assets/utils/electron'
import Notify from '@renderer/scripts/notify'

// components
import ArticleTreeTitle from './ArticleTreeTitle.vue'
import ArticleTreeWorkbench from "./ArticleTreeWorkbench.vue"
import ArticleQrCode from './ArticleQrCode.vue'
import ArticleInfo from './ArticleInfo.vue'

const userStore = useUserStore();
const route = useRoute()

onMounted(() => {
  getDocTree(false, false, false)
  getRouteQueryParams()
})

onActivated(() => {
  // getDocTree(false, false, false)
  getRouteQueryParams()
})

onBeforeUnmount(() => {
  document.body.removeEventListener('click', closeTreeDocsMenuShow)
  document.body.removeEventListener('contextmenu', closeTreeDocsMenuShow)
})

//#region 菜单

const docTreeLoading = ref(true)        // 文档菜单的加载动画
const showSort = ref(false)             // 是否显示文档排序
const docTreeDefaultActive = ref('')    // 文档的默认选中项, 用于外部跳转后选中菜单
const docTreeData = ref<DocTree[]>([])  // 文档菜单

// 注入的相关信息
provide(provideKeyDocTree, docTreeData)

/**
 * 获取路由参数
 */
const getRouteQueryParams = () => {
  let articleId = route.query.articleId
  if (isNotNull(articleId)) {
    docTreeDefaultActive.value = articleId as string
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
  docTreeApi({ onlyPicture: false, onlyOpen: isOnlyOpen, onlySubject: isOnlySubject, onlyStar: isOnlyStar }).then(resp => {
    docTreeData.value = resp.data
    concatSort(docTreeData.value)
  }).finally(() => {
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
    if (showSort.value) {
      trees[i].n = trees[i].s + '〉' + trees[i].n
    } else {
      trees[i].n = trees[i].n.substring(trees[i].n.indexOf('〉') + 1)
    }
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
const rMenuHeight = 363 // 固定的菜单高度, 每次增加右键菜单项时需要修改该值

/**
 * 显示有检查菜单
 * @param doc 文档
 * @param event 事件
 */
const handleClickRight = (doc: DocTree, event: MouseEvent) => {
  if (!doc) {
    return
  }
  curDoc.value = doc
  rMenu.value = { show: false, clientX: 0, clientY: 0 }
  let y = event.clientY
  if (document.body.clientHeight - event.clientY < rMenuHeight) {
    y = event.clientY - rMenuHeight
  }
  rMenu.value = { show: true, clientX: event.clientX, clientY: y }
  setTimeout(() => {
    document.body.addEventListener('click', closeTreeDocsMenuShow)
  }, 100)
}

const closeTreeDocsMenuShow = () => {
  removeListenerTreeDocsRightMenu()
  rMenu.value.show = false
}

const removeListenerTreeDocsRightMenu = () => {
  document.body.removeEventListener('click', closeTreeDocsMenuShow)
}

//#endregion

/**
 * 打开新页面, 文件夹(curDoc.value.ty == 1)无法使用新页面打开
 */
const openArticleWindow = () => {
  if (curDoc.value.ty === 1)
    return
  openNewArticleWindow(curDoc.value.n, curDoc.value.i);
}

/**
 * 使用浏览器打开公开链接, 或复制公开链接
 */
const createUrl = (type: 'open' | 'copy' | 'link') => {
  let url: string = userStore.userinfo.params.WEB_ARTICLE_URL + curDoc.value.i;
  if (type == 'open') {
    openExtenal(url)
  } else if (type == 'copy') {
    writeText(url)
  } else if (type == 'link') {
    url = `[${curDoc.value.n}](${userStore.userinfo.params.WEB_ARTICLE_URL + curDoc.value.i} "${grammar}${curDoc.value.i}${grammar}")`
    writeText(url)
  }
}

/**
 * 下载文章
 */
const articleDownload = () => {
  articleDownloadApi({ id: curDoc.value.i }).then(resp => {
    let filename: string = resp.headers.get('content-disposition')
    let filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
    let matches = filenameRegex.exec(filename);
    if (matches != null && matches[1]) {
      filename = decodeURI(matches[1].replace(/['"]/g, ''));
    }
    let a = document.createElement('a')
    let blob = new Blob([resp.data], { type: "text/plain" })
    let objectUrl = URL.createObjectURL(blob)
    a.setAttribute("href", objectUrl)
    a.setAttribute("download", filename)
    a.click()
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
  ElMessageBox.confirm(
    `是否确定删除${type}: <span style="color:#C02B2B;text-decoration: underline;">${curDoc.value.n}</span>？删除后将不可恢复！`, {
    confirmButtonText: '确定删除', cancelButtonText: '我再想想', type: 'info', draggable: true, dangerouslyUseHTMLString: true,
  }
  ).then(() => {
    if (curDoc.value.ty === 3) {
      articleDelApi({ id: curDoc.value.i }).then(_resp => {
        Notify.success(`删除文章成功`)
        getDocTree(false, false, false)
      })
    } else {
      folderDelApi({ id: curDoc.value.i }).then(_resp => {
        Notify.success(`删除文件夹成功`)
        getDocTree(false, false, false)
      })
    }
  })
}

const isShowQrCodeDialog = ref<boolean>(false);
const ArticleQrCodeRef = ref()

const handleArticleQrCodeDialog = () => {
  isShowQrCodeDialog.value = true
  nextTick(() => {
    ArticleQrCodeRef.value.getArticleQrCode(curDoc.value.n, curDoc.value.i)
  })
}

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
    nextTick(() => { ArticleInfoRef.value.reload(dialogType, undefined, undefined, pid) })
  }
  if (dialogType === 'upd') {
    nextTick(() => {
      ArticleInfoRef.value.reload(dialogType, curDoc.value.i, curDoc.value.ty)
    })
  }
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
</style>