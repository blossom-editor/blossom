<template>
  <!-- 文件夹操作 -->
  <div class="doc-workbench">
    <Workbench @refresh-doc-tree="getDocTree" @show-sort="handleShowSort"></Workbench>
  </div>

  <div class="doc-trees-container" v-loading="docTreeLoading" element-loading-text="正在读取文档...">
    <!-- 文件夹 -->
    <el-menu v-if="!isEmpty(docTreeData)" class="doc-trees" :unique-opened="true">

      <!-- ================================================ L1 ================================================ -->
      <div v-for="L1 in docTreeData" :key="L1.i">


        <div v-if="L1.ty == 11" class="menu-divider" />

        <!-- L1无下级 -->
        <el-menu-item v-else-if="isEmpty(L1.children)" :index="L1.i">
          <template #title>
            <div class="menu-item-wrapper" @click="clickCurDoc(L1)" @click.right="handleClickRight(L1, $event)">
              <PictureTitle :trees="L1" />
            </div>
          </template>
        </el-menu-item>

        <!-- L1有下级 -->
        <el-sub-menu v-else :expand-open-icon="ArrowDownBold" :expand-close-icon="ArrowRightBold" :index="L1.i">
          <template #title>
            <div class="menu-item-wrapper" @click.right="handleClickRight(L1, $event)">
              <PictureTitle :trees="L1" />
            </div>
          </template>

          <!-- ================================================ L2 ================================================ -->
          <div v-for="L2 in L1.children" :key="L2.i">
            <!-- L2无下级 -->
            <el-menu-item v-if="isEmpty(L2.children)" :index="L2.i">
              <template #title>
                <div class="menu-item-wrapper" @click="clickCurDoc(L2)" @click.right="handleClickRight(L2, $event)">
                  <PictureTitle :trees="L2" />
                </div>
              </template>
            </el-menu-item>

            <!-- L2有下级 -->
            <el-sub-menu v-else :expand-open-icon="ArrowDownBold" :expand-close-icon="ArrowRightBold" :index="L2.i">
              <template #title>
                <div class="menu-item-wrapper" @click.right="handleClickRight(L2, $event)">
                  <PictureTitle :trees="L2" />
                </div>
              </template>

              <!-- ================================================ L3 ================================================ -->
              <div v-for="L3 in L2.children" :key="L3.i">
                <!-- L3无下级 -->
                <el-menu-item v-if="isEmpty(L3.children)" :index="L3.i">
                  <template #title>
                    <div class="menu-item-wrapper" @click="clickCurDoc(L3)" @click.right="handleClickRight(L3, $event)">
                      <PictureTitle :trees="L3" />
                    </div>
                  </template>
                </el-menu-item>

                <!-- L3有下级 -->
                <el-sub-menu v-else :expand-open-icon="ArrowDownBold" :expand-close-icon="ArrowRightBold" :index="L3.i">
                  <template #title>
                    <div class="menu-item-wrapper" @click.right="handleClickRight(L3, $event)">
                      <PictureTitle :trees="L3" />
                    </div>
                  </template>

                  <!-- ================================================ L4 ================================================ -->
                  <div v-for="L4 in L3.children" :key="L4.i">
                    <!-- L4 不允许有下级, 只允许4级 -->
                    <el-menu-item v-if="isEmpty(L4.children)" :index="L4.i">
                      <template #title>
                        <div class="menu-item-wrapper" @click="clickCurDoc(L4)" style="width: 100%;"
                          @click.right="handleClickRight(L4, $event)">
                          <PictureTitle :trees="L4" />
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
        <div :class="['menu-item', curDoc.i < 0 ? 'disabled' : '']" @click="handleShowDocInfoDialog('upd')">
          <span class="iconbl bl-a-fileedit-line"></span>编辑文档
        </div>
        <div :class="['menu-item', curDoc.ty != 2 ? 'disabled' : '']" @click="handleShowDocInfoDialog('add', curDoc.p)">
          <span class="iconbl bl-a-fileadd-line"></span>新增<strong>同级</strong>文档
        </div>
        <!-- 只有文件夹才有子文档 -->
        <div :class="['menu-item', curDoc.ty != 2 ? 'disabled' : '']" @click="handleShowDocInfoDialog('add', curDoc.i)">
          <span class="iconbl bl-a-fileadd-fill"></span>新增<strong>子级</strong>文档
        </div>
        <div :class="['menu-item', curDoc.i < 0 ? 'disabled' : '']" @click="delDoc()">
          <span class="iconbl bl-a-fileprohibit-line"></span>删除文档
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
    <PictureInfo ref="PictureInfoRef"></PictureInfo>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, provide, onActivated, nextTick } from "vue"
import { ArrowDownBold, ArrowRightBold } from '@element-plus/icons-vue'
import Workbench from "./PictureTreeWorkbench.vue"
import { docTreeApi, folderDelApi } from '@renderer/api/blossom'
import { provideKeyDocTree } from '@renderer/views/doc/doc'
import { isEmpty } from 'lodash'
import PictureTitle from './PictureTreeTitle.vue'
import PictureInfo from '@renderer/views/picture/PictureInfo.vue'
import Notify from "@renderer/scripts/notify"
import { ElMessageBox } from "element-plus"

onActivated(() => {
  getDocTree()
})

const docTreeLoading = ref(true)       // 文档菜单的加载动画
const showSort = ref(false)            // 是否显示文档排序    
const docTreeData = ref<DocTree[]>([]) // 文档菜单

// 依赖注入
provide(provideKeyDocTree, docTreeData)

/**
 * 获取文档树状列表
 * 1. 初始化是全否调用
 * 2. 在 workbench 中点击按钮调用, 每个按钮是单选的
 */
const getDocTree = () => {
  docTreeLoading.value = true;
  docTreeData.value = []
  docTreeApi({ onlyPicture: true }).then(resp => {
    const docTree: DocTree[] = resp.data
    // 两种类型的交界位置
    let lastPicIndex: number = docTree.length - 1
    for (let i = 0; i < docTree.length; i++) {
      let doc = docTree[i]
      if (doc.ty === 1) {
        lastPicIndex = i
        break
      }
    }

    docTree.splice(lastPicIndex, 0, {
      i: docTree[0].i - 100000,
      p: 0,
      n: '───────────────────────',
      o: 0,
      t: [],
      s: 0,
      icon: '',
      ty: 11,
      star: 0
    })

    docTreeData.value = docTree
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
/**
 * 是否显示排序
 */
const handleShowSort = () => {
  showSort.value = !showSort.value
  concatSort(docTreeData.value)
}

//#region ----------------------------------------< 右键菜单 >--------------------------------------
const curDoc = ref<DocTree>({ i: 0, p: 0, n: '选择菜单', o: 0, t: [], s: 0, icon: '', ty: 1, star: 0 })
const rMenu = ref<RightMenu>({ show: false, clientX: 0, clientY: 0 })
const rMenuHeight = 151 // 固定的菜单高度, 每次增加右键菜单项时需要修改该值

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

//#region 右键菜单功能

/**
 * 删除文档
 */
const delDoc = () => {
  ElMessageBox.confirm(
    `是否确定删除文件夹: <span style="color:#C02B2B;text-decoration: underline;">${curDoc.value.n}</span>？删除后将不可恢复！`, {
    confirmButtonText: '确定删除', cancelButtonText: '我再想想', type: 'info', draggable: true, dangerouslyUseHTMLString: true,
  }
  ).then(() => {
    folderDelApi({ id: curDoc.value.i }).then(_resp => {
      Notify.success(`删除文件夹成功`)
      getDocTree()
    })
  })
}

//#region ----------------------------------------< 新增修改详情弹框 >--------------------------------------

const PictureInfoRef = ref()
const isShowDocInfoDialog = ref<boolean>(false);

/**
 * 显示弹框
 * @param dialogType 弹框的类型, 新增, 修改
 * @param pid 父级ID, 新增同级或子集文档时使用
 */
const handleShowDocInfoDialog = (dialogType: DocDialogType, pid?: number) => {
  if (curDoc.value.i < 0) {
    Notify.info('当前文档为系统默认文档, 无法操作', '操作无效')
    return
  }

  if (dialogType === 'upd' && (curDoc.value == undefined || curDoc.value?.i == undefined)) {
    Notify.info('请先选则要修改的文件夹或文档')
    return
  }
  isShowDocInfoDialog.value = true
  if (dialogType === 'add') {
    nextTick(() => { PictureInfoRef.value.reload(dialogType, undefined, pid) })
  }
  if (dialogType === 'upd') {
    nextTick(() => {
      PictureInfoRef.value.reload(dialogType, curDoc.value.i)
    })
  }
}

//#endregion 右键菜单

const clickCurDoc = (tree: DocTree) => {
  emits('clickDoc', tree)
}

const emits = defineEmits(['clickDoc'])

</script>

<style scoped lang="scss">
@import '../doc/tree-docs.scss';
</style>