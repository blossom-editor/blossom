<template>
  <!-- 文件夹操作 -->
  <div class="doc-workbench">
    <Workbench></Workbench>
  </div>
  <div class="doc-tree-operator">
    <el-tooltip effect="light" popper-class="is-small" placement="top" :offset="4" :hide-after="0" content="显示排序">
      <div class="iconbl bl-a-leftdirection-line" @click="handleShowSort"></div>
    </el-tooltip>
    <el-tooltip effect="light" popper-class="is-small" placement="top" :offset="4" :hide-after="0" content="根目录下新建图片文件夹">
      <div class="iconbl bl-folderadd-line" @click="addFolderToRoot()"></div>
    </el-tooltip>
    <el-tooltip effect="light" popper-class="is-small" placement="top" :offset="4" :hide-after="0" :show-after="1000" content="搜索">
      <div class="iconbl bl-search-item" @click="showTreeFilter()"></div>
    </el-tooltip>
    <el-tooltip effect="light" popper-class="is-small" placement="top" :offset="4" :hide-after="0" :show-after="1000" content="刷新">
      <div class="iconbl bl-refresh-line" @click="refreshDocTree()"></div>
    </el-tooltip>
    <el-tooltip effect="light" popper-class="is-small" placement="top" :offset="4" :hide-after="0" :show-after="1000" content="折叠所有文件夹">
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
        <div v-if="data.ty !== 11 && isShowSort" class="sort-tag" :style="{ backgroundColor: getColor(node) }">{{ data.s }}</div>
        <div v-if="data.ty === 11" class="menu-divider"></div>
        <div v-else class="menu-item-wrapper" @click.right="handleClickRightMenu($event, data)">
          <div class="doc-title">
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
          </div>
        </div>
      </template>
    </el-tree>
  </div>

  <!-- 右键菜单, 添加到 body 下 -->
  <Teleport to="body">
    <div v-if="rMenu.show" class="tree-menu" :style="{ left: rMenu.clientX + 'px', top: rMenu.clientY + 'px' }">
      <div class="doc-name">{{ curDoc.n }}</div>
      <div class="menu-content">
        <div :class="['menu-item', Number(curDoc.i) <= 0 ? 'disabled' : '']" @click="rename"><span class="iconbl bl-pen"></span>重命名</div>
        <div :class="['menu-item', Number(curDoc.i) <= 0 ? 'disabled' : '']" @click="handleShowDocInfoDialog('upd')">
          <span class="iconbl bl-a-fileedit-line"></span>编辑详情
        </div>
        <div :class="['menu-item', Number(curDoc.i) <= 0 ? 'disabled' : '']" @click="addFolderToDoc()">
          <span class="iconbl bl-folderadd-line"></span>新增文件夹
        </div>
        <div class="menu-item-divider"></div>
        <div :class="['menu-item', Number(curDoc.i) <= 0 ? 'disabled' : '']" @click="delDoc()">
          <span class="iconbl bl-a-fileprohibit-line"></span>删除文件夹
        </div>
      </div>
    </div>
  </Teleport>

  <!-- 详情的弹框 -->
  <el-dialog
    class="bl-dialog-draggable-header"
    v-model="isShowDocInfoDialog"
    width="535"
    :append-to-body="true"
    :destroy-on-close="true"
    :close-on-click-modal="false"
    align-center
    draggable>
    <PictureInfo ref="PictureInfoRef" @saved="savedCallback"></PictureInfo>
  </el-dialog>

  <!-- <div class="doc-tree-debug">
    <div>所有展开：{{ Array.from(docTreeCurrentExpandId) + '' }}</div>
    <div>当前选中：{{ docTreeCurrentId }}</div>
  </div> -->
</template>

<script setup lang="ts">
import { ref, provide, nextTick, watch } from 'vue'
import { useConfigStore } from '@renderer/stores/config'
import { useUserStore } from '@renderer/stores/user'
// element plus
import { ElMessageBox, TreeNode } from 'element-plus'
import { NodeDropType } from 'element-plus/es/components/tree/src/tree.type'
import { DragEvents } from 'element-plus/es/components/tree/src/model/useDragNode'
import { ArrowRightBold, Rank, Close } from '@element-plus/icons-vue'
import Node from 'element-plus/es/components/tree/src/model/node'
// ts
import { docTreeApi, docUpdSortApi, folderAddApi, folderDelApi, folderUpdNameApi } from '@renderer/api/blossom'
import { provideKeyDocTree } from '@renderer/views/doc/doc'
import { isShowImg, isShowSvg, tags } from '@renderer/views/doc/doc-tree-detail'
import { getColor, handleTreeDrop } from '@renderer/views/doc/doc-tree'
import { useDraggable } from '@renderer/scripts/draggable'
import { useLifecycle } from '@renderer/scripts/lifecycle'
// util
import { isEmpty } from 'lodash'
import { isBlank, isNotBlank } from '@renderer/assets/utils/obj'
// components
import Notify from '@renderer/scripts/notify'
import Workbench from './PictureTreeWorkbench.vue'
import PictureInfo from '@renderer/views/picture/PictureInfo.vue'

const user = useUserStore()
const { viewStyle } = useConfigStore()

useLifecycle(
  () => getDocTree(),
  () => getDocTree()
)
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
provide(provideKeyDocTree, docTreeData)

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
 * @param callback 获取文档后的自定义回调
 */
const getDocTree = (callback?: () => void) => {
  startLoading()
  docTreeApi({ onlyPicture: true })
    .then((resp) => {
      addTreeDivider(resp.data)
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
 * 是否显示排序
 */
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

/**
 * 为树状列表插入图片文件夹和文章文件夹的分割线
 */
const addTreeDivider = (data: any) => {
  const docTree: DocTree[] = data
  // 两种类型的交界位置
  let lastPicIndex: number = -1
  // 循环一级文件夹，第一个文章文件夹即是最后一个图片文件夹的位置
  for (let i = 0; i < docTree.length; i++) {
    let doc = docTree[i]
    if (doc.ty === 1) {
      lastPicIndex = i
      break
    }
  }

  console.log(lastPicIndex, docTree.length)

  const divider: DocTree = {
    i: (Number(docTree[0].i) - 100000).toString(),
    p: '0',
    n: '',
    o: 0,
    t: [],
    s: 0,
    icon: '',
    ty: 11,
    star: 0
  }

  if (lastPicIndex > -1) {
    // 插入分割符
    docTree.splice(Math.max(lastPicIndex, 1), 0, divider)
  } else {
    // docTree.push(divider)
  }

  docTreeData.value = docTree
}
//#endregion

//#region ----------------------------------------< 树状列表管理 >--------------------------------------
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

watch(treeFilterText, (val) => DocTreeRef.value!.filter(val))

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
 * @param value 搜索内容
 * @param data 列表
 * @return 返回节点是否保留
 */
const filterNode = (value: string, data: DocTree): boolean => {
  if (!value) return true
  return data.n.includes(value) || data.t.toString().includes(value)
}

/**
 * 判断是否允许被拖拽
 * 1. 文章文件夹不允许拖拽
 * 2. 正在重命名的节点不允许被拖拽
 *
 * @param node 拖动的节点
 * @return boolean 节点是否允许被拖动
 */
const handleAllowDrag = (node: Node): boolean => {
  return node.data.ty === 2 && notAllowDragKey !== node.data.i
}

/**
 * 判断是否允许被节点放置
 *
 * @param _draggingNode 拖动的节点
 * @param dropNode 被防止的节点
 * @param type 放置的类型
 * @return boolean 是否允许被放置
 */
const handleAllowDrop = (_draggingNode: Node, dropNode: Node, _type: NodeDropType): boolean => {
  if (dropNode.data.ty !== 2) {
    return false
  }
  if (dropNode.data.i < 0) {
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
 * 处理节点缩起, 同时清除所有子节点的展开状态
 */
const handleNodeCollapse = (tree: DocTree, node: Node) => {
  docTreeCurrentExpandId.value.delete(tree.i)
  collapseChilds(node)
}

/**
 * 递归缩起所有子节点
 */
const collapseChilds = (node: Node) => {
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
  handleTreeDrop(drag, enter, dropType, _event, DocTreeRef, docTreeData, 2, (needUpd) => {
    docUpdSortApi({ docs: needUpd, folderType: 2, onlyPicture: true })
      .then((resp) => {
        addTreeDivider(resp.data)
        collapseNoChild()
      })
      .catch(() => getDocTree())
  })
}

//#endregion

//#region ----------------------------------------< 右键菜单 >--------------------------------------
const curDoc = ref<DocTree>({ i: '0', p: '0', n: '选择菜单', o: 0, t: [], s: 0, icon: '', ty: 1, star: 0 })
const rMenu = ref<RightMenu>({ show: false, clientX: 0, clientY: 0 })
const rMenuHeight = 151 // 固定的菜单高度, 每次增加右键菜单项时需要修改该值

/**
 * 显示右键菜单
 * 文章文件夹不显示右键菜单, 文章文件夹的管理一律在文章编辑功能中
 *
 * @param doc 文档
 * @param event 事件
 */
const handleClickRightMenu = (event: MouseEvent, doc: DocTree) => {
  event.preventDefault()
  docTreeCurrentExpandId.value.add(doc.p)
  if (!doc) return
  if (doc.ty !== 2) return

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
  rMenu.value.show = false
  document.body.removeEventListener('click', closeTreeDocsMenuShow)
}

/**
 * 删除文档, 删除后将文档从树状节点中删除
 */
const delDoc = () => {
  ElMessageBox.confirm(`是否确定删除文件夹: <span style="color:#C02B2B;text-decoration: underline;">${curDoc.value.n}</span>？删除后将不可恢复！`, {
    confirmButtonText: '确定删除',
    cancelButtonText: '我再想想',
    type: 'info',
    draggable: true,
    dangerouslyUseHTMLString: true
  }).then(() => {
    folderDelApi({ id: curDoc.value.i }).then((_resp) => {
      Notify.success(`删除文件夹成功`)
      DocTreeRef.value.remove(curDoc.value.i)
      closeParentIfNoChild(curDoc.value.p)
    })
  })
}

/**
 * 重命名文章, 重命名时该节点无法拖拽
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
  folderUpdNameApi({ id: doc.i, name: doc.n }).then((_resp) => {
    doc.updn = false
    notAllowDragKey = ''
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
  folderAddApi({ pid: pid, name: '新文件夹', storePath: '/', type: 2, icon: 'wl-folder', sort: 0, addToLast: true }).then((resp) => {
    let newFolder: DocTree = {
      i: resp.data.id,
      p: resp.data.pid,
      n: resp.data.name,
      updn: true,
      s: resp.data.sort,
      icon: resp.data.icon,
      o: 0,
      t: [],
      ty: 2,
      star: 0,
      showSort: curDoc.value.showSort
    }
    addDocToTail(newFolder)
  })
}

/**
 * 将文档添加至末尾并选中
 */
const addDocToTail = (doc: DocTree) => {
  if (doc.p !== '0') {
    // 插入到根目录
    DocTreeRef.value.append(doc, DocTreeRef.value.getNode(doc.p))
    docTreeCurrentExpandId.value.add(doc.p)
  } else {
    const picRootFolder: DocTree[] = docTreeData.value.filter((n) => n.ty === 2)
    const lastFolder = picRootFolder[picRootFolder.length - 1]
    console.log(lastFolder)
    DocTreeRef.value.insertAfter(doc, lastFolder.i)
  }
  nextTick(() => {
    let ele = document.getElementById('article-doc-name-' + doc.i) as HTMLInputElement
    setTimeout(() => {
      if (ele) ele.select()
    }, 100)
  })
}

//#endregion

//#region ----------------------------------------< 新增修改详情弹框 >--------------------------------------
const PictureInfoRef = ref()
const isShowDocInfoDialog = ref<boolean>(false)

/**
 * 显示弹框
 * @param dialogType 弹框的类型, 新增, 修改
 * @param pid 父级ID, 新增同级或子集文档时使用
 */
const handleShowDocInfoDialog = (dialogType: DocDialogType, pid?: number) => {
  if (Number(curDoc.value.i) < 0) {
    Notify.info('当前文档为系统默认文档, 无法操作', '操作无效')
    return
  }
  if (dialogType === 'upd' && (curDoc.value == undefined || curDoc.value?.i == undefined)) {
    Notify.info('请先选则要修改的文件夹或文档')
    return
  }
  isShowDocInfoDialog.value = true
  if (dialogType === 'add') nextTick(() => PictureInfoRef.value.reload(dialogType, undefined, pid))
  if (dialogType === 'upd') nextTick(() => PictureInfoRef.value.reload(dialogType, curDoc.value.i))
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
  if (oldDoc.s !== doc.sort || oldDoc.p !== doc.pid) {
    getDocTree(() => {
      collapseNoChild()
    })
  }
}

//#endregion

const emits = defineEmits(['clickDoc'])
</script>

<style scoped lang="scss">
@import '../doc/doc-tree.scss';
@import '../doc/doc-tree-detail.scss';
@import '../doc/doc-tree-right-menu.scss';
</style>
