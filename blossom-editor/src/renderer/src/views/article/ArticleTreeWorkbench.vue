<template>
  <div class="doc-workbench-root">
    <bl-col class="workbench-name" just="flex-start" align="flex-end" height="46px" v-show="curArticle !== undefined">
      <span>《{{ curArticle?.name }}》</span>
      <span style="font-size: 9px; padding-right: 5px">{{ curArticle?.id }}</span>
    </bl-col>

    <bl-row class="wb-page-container">
      <bl-row class="wb-page-item" just="flex-start" align="flex-end" width="calc(100% - 16px)" height="44px">
        <el-tooltip
          content="文章引用网络"
          effect="light"
          popper-class="is-small"
          transition="none"
          placement="top"
          :show-arrow="false"
          :offset="-5"
          :hide-after="0">
          <div class="iconbl bl-correlation-line" @click="openArticleReferenceWindow()"></div>
        </el-tooltip>
        <el-tooltip
          content="新增文件夹或文章"
          effect="light"
          popper-class="is-small"
          transition="none"
          placement="top"
          :show-arrow="false"
          :offset="8"
          :hide-after="0">
          <div class="iconbl bl-a-fileadd-line" @click="handleShowAddDocInfoDialog()"></div>
        </el-tooltip>
        <el-tooltip
          content="刷新"
          effect="light"
          popper-class="is-small"
          transition="none"
          placement="top"
          :show-arrow="false"
          :offset="8"
          :hide-after="0">
          <div class="iconbl bl-a-cloudrefresh-line" @click="refreshDocTree()"></div>
        </el-tooltip>
        <el-tooltip
          content="全文搜索"
          effect="light"
          popper-class="is-small"
          transition="none"
          placement="top"
          :show-arrow="false"
          :offset="9"
          :hide-after="0">
          <div class="iconbl bl-search-line" @click="showSearch()"></div>
        </el-tooltip>
        <el-tooltip effect="light" popper-class="is-small" transition="none" placement="top" :show-arrow="false" :offset="5" :hide-after="0">
          <div class="iconbl bl-a-leftdirection-line" @click="emits('show-sort')"></div>
          <template #content>
            显示排序<br />
            <bl-row>
              <bl-tag :bgColor="SortLevelColor.ONE">一级</bl-tag>
              <bl-tag :bgColor="SortLevelColor.TWO">二级</bl-tag>
            </bl-row>
            <bl-row style="padding-bottom: 5px">
              <bl-tag :bgColor="SortLevelColor.THREE">三级</bl-tag>
              <bl-tag :bgColor="SortLevelColor.FOUR">四级</bl-tag>
            </bl-row>
          </template>
        </el-tooltip>
        <el-tooltip
          content="备份记录"
          effect="light"
          popper-class="is-small"
          transition="none"
          placement="top"
          :show-arrow="false"
          :offset="8"
          :hide-after="0">
          <div class="iconbl bl-a-cloudstorage-line" @click="handleShowBackupDialog"></div>
        </el-tooltip>
        <el-tooltip
          content="文章回收站"
          effect="light"
          popper-class="is-small"
          transition="none"
          placement="top"
          :show-arrow="false"
          :offset="8"
          :hide-after="0">
          <div class="iconbl bl-delete-line" @click="handleShowRecycleDialog"></div>
        </el-tooltip>
        <el-tooltip
          content="查看收藏"
          effect="light"
          popper-class="is-small"
          transition="none"
          placement="top"
          :show-arrow="false"
          :offset="8"
          :hide-after="0">
          <div v-if="props.showStar">
            <div v-if="onlyStars" class="iconbl bl-star-fill" @click="changeOnlyStar()"></div>
            <div v-else class="iconbl bl-star-line" @click="changeOnlyStar()"></div>
          </div>
        </el-tooltip>
        <el-tooltip
          content="查看专题"
          effect="light"
          popper-class="is-small"
          transition="none"
          placement="top"
          :show-arrow="false"
          :offset="8"
          :hide-after="0">
          <div v-if="props.showSubject">
            <div v-if="onlySubject" class="iconbl bl-a-lowerrightpage-fill" @click="changeOnlySubject()"></div>
            <div v-else class="iconbl bl-a-lowerrightpage-line" @click="changeOnlySubject()"></div>
          </div>
        </el-tooltip>
        <el-tooltip
          content="查看公开"
          effect="light"
          popper-class="is-small"
          transition="none"
          placement="top"
          :show-arrow="false"
          :offset="8"
          :hide-after="0">
          <div v-if="props.showOpen">
            <div v-if="onlyOpen" class="iconbl bl-cloud-fill" @click="changeOnlyOpen()"></div>
            <div v-else class="iconbl bl-cloud-line" @click="changeOnlyOpen()"></div>
          </div>
        </el-tooltip>
      </bl-row>

      <bl-col width="12px" height="30px" just="end" class="workbench-more" style="">
        <div class="iconbl bl-a-morevertical-line" @click="showMoreMenu"></div>
      </bl-col>
    </bl-row>
  </div>

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

  <el-dialog
    class="bl-dialog-fixed-body"
    v-model="isShowBackupDialog"
    width="80%"
    style="height: 80%"
    :align-center="true"
    :append-to-body="true"
    :destroy-on-close="true"
    :close-on-click-modal="false"
    draggable>
    <ArticleBackup ref="ArticleBackupRef"></ArticleBackup>
  </el-dialog>

  <el-dialog
    class="bl-dialog-fixed-body"
    v-model="isShowRecycleDialog"
    width="80%"
    style="height: 80%"
    :align-center="true"
    :append-to-body="true"
    :destroy-on-close="true"
    :close-on-click-modal="false"
    draggable>
    <ArticleRecycle ref="ArticleRecycleRef"></ArticleRecycle>
  </el-dialog>

  <Teleport to="body">
    <div v-show="moreMenu.show" class="doc-tree-right-menu" :style="{ left: moreMenu.clientX + 'px', top: moreMenu.clientY + 'px', width: '120px' }">
      <div class="menu-content">
        <div @click="changeOnlyOpen"><span class="iconbl bl-cloud-line"></span>查看公开</div>
        <div @click="changeOnlySubject"><span class="iconbl bl-a-lowerrightpage-line"></span>查看专题</div>
        <div @click="changeOnlyStar"><span class="iconbl bl-star-line"></span>查看收藏</div>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, nextTick, inject, onDeactivated } from 'vue'
import { provideKeyCurArticleInfo, SortLevelColor } from '@renderer/views/doc/doc'
import { openNewArticleReferenceWindow } from '@renderer/assets/utils/electron'
import { keymaps } from './scripts/editor-tools'
import { useLifecycle } from '@renderer/scripts/lifecycle'
import hotkeys from 'hotkeys-js'
import ArticleInfo from './ArticleInfo.vue'
import ArticleBackup from './ArticleBackup.vue'
import ArticleRecycle from './ArticleRecycle.vue'

useLifecycle(
  () => bindKeys(),
  () => bindKeys()
)

onDeactivated(() => {
  unbindKeys()
})

const props = defineProps({
  showOpen: {
    type: Boolean,
    default: true
  },
  showSubject: {
    type: Boolean,
    default: true
  },
  showStar: {
    type: Boolean,
    default: true
  }
})

//#region --------------------------------------------------< 控制台更多选项 >--------------------------------------------------
const moreMenu = ref<RightMenu>({ show: false, clientX: 0, clientY: 0 })

/**
 * 显示右键菜单
 * @param doc 文档
 * @param event 事件
 */
const showMoreMenu = (event: MouseEvent) => {
  moreMenu.value.show = true
  nextTick(() => {
    let y = event.clientY
    if (document.body.clientHeight - event.clientY < 50) {
      y = event.clientY - 50
    }
    moreMenu.value = { show: true, clientX: event.clientX, clientY: y }
    setTimeout(() => {
      document.body.addEventListener('click', closeMoreMenu)
    }, 100)
  })
}

const closeMoreMenu = (event: MouseEvent) => {
  if (event.target) {
    let isPrevent = (event.target as HTMLElement).getAttribute('data-bl-prevet')
    if (isPrevent === 'true') {
      event.preventDefault()
      return
    }
  }

  document.body.removeEventListener('click', closeMoreMenu)
  moreMenu.value.show = false
}

//#endregion

//#region --------------------------------------------------< 查询 >--------------------------------------------------
const curArticle = inject(provideKeyCurArticleInfo)

const onlyOpen = ref<boolean>(false) // 只显示公开
const onlySubject = ref<boolean>(false) // 只显示专题
const onlyStars = ref<boolean>(false) // 只显示 star

const changeOnlyOpen = () => {
  onlyOpen.value = !onlyOpen.value
  onlySubject.value = false
  onlyStars.value = false
  refreshDocTree()
}

const changeOnlySubject = () => {
  onlyOpen.value = false
  onlySubject.value = !onlySubject.value
  onlyStars.value = false
  refreshDocTree()
}

const changeOnlyStar = () => {
  onlyOpen.value = false
  onlySubject.value = false
  onlyStars.value = !onlyStars.value
  refreshDocTree()
}

//#endregion

//#region --------------------------------------------------< 新增窗口 >--------------------------------------------------
const ArticleInfoRef = ref()
const isShowDocInfoDialog = ref<boolean>(false)

const handleShowAddDocInfoDialog = () => {
  isShowDocInfoDialog.value = true
  nextTick(() => {
    ArticleInfoRef.value.reload('add')
  })
}

const openArticleReferenceWindow = () => {
  openNewArticleReferenceWindow()
}

/**
 * 控制台刷新文档列表
 */
const refreshDocTree = () => {
  emits('refreshDocTree', onlyOpen.value, onlySubject.value, onlyStars.value)
}

/**
 * 保存后的回调
 * 1. 刷新菜单列表
 * 2. 关闭 dialog 页面
 */
const savedCallback = () => {
  isShowDocInfoDialog.value = false
  emits('refreshDocTree', onlyOpen.value, onlySubject.value, onlyStars.value)
}

const showSearch = () => {
  emits('show-search')
}

//#endregion

//#region --------------------------------------------------< 备份记录 >--------------------------------------------------
const ArticleBackupRef = ref()
const isShowBackupDialog = ref<boolean>(false)

const handleShowBackupDialog = () => {
  isShowBackupDialog.value = true
}
//#endregion

//#region --------------------------------------------------< 备份记录 >--------------------------------------------------
const ArticleRecycleRef = ref()
const isShowRecycleDialog = ref<boolean>(false)

const handleShowRecycleDialog = () => {
  isShowRecycleDialog.value = true
}
//#endregion

//#region --------------------------------------------------< 绑定快捷键 >--------------------------------------------------
const bindKeys = () => {
  hotkeys('ctrl+shift+f, command+shift+f', (keyboardEvent: KeyboardEvent) => {
    showSearch()
    keyboardEvent.preventDefault()
    return false
  })
  hotkeys('ctrl+n, command+n', (keyboardEvent: KeyboardEvent) => {
    handleShowAddDocInfoDialog()
    keyboardEvent.preventDefault()
    return false
  })
}

const unbindKeys = () => {
  hotkeys.unbind('ctrl+shift+f, command+shift+f')
  hotkeys.unbind('ctrl+n, command+n')
}

//#endregion
const emits = defineEmits(['refreshDocTree', 'show-sort', 'show-search'])
defineExpose({ handleShowBackupDialog })
</script>

<style scoped lang="scss">
@import '../doc/tree-workbench.scss';
@import '../doc/tree-docs-right-menu.scss';

.wb-page-container {
  position: relative;
  height: 44px;

  .wb-page-item {
    flex-direction: row-reverse !important;
    align-content: space-between;
    flex-wrap: wrap;
    overflow: hidden;
    position: absolute;
    left: 0;

    &::-webkit-scrollbar {
      width: 0px;
      height: 0px;
    }

    // 排序
    .bl-a-leftdirection-line {
      font-size: 27px;
      padding-bottom: 3px;
      padding-right: 0;
      padding-left: 0;
    }

    // 搜索
    .bl-search-line {
      font-size: 23px;
      padding-bottom: 4px;
    }

    // 收藏
    .bl-star-line,
    .bl-star-fill {
      font-size: 23px;
      padding-bottom: 5px;
    }

    // 刷新图标
    .bl-a-cloudrefresh-line,
    .bl-a-fileadd-line {
      &:active {
        color: #ffffff;
      }
    }

    .bl-correlation-line {
      color: var(--el-color-primary) !important;
      font-size: 40px;
      padding-bottom: 0px;
    }
  }
}

.wbpage-one-enter-from,
.wbpage-one-leave-to {
  opacity: 0;
  transform: translateY(-30%);
}

.wbpage-two-enter-from,
.wbpage-two-leave-to {
  opacity: 0;
  transform: translateY(30%);
}

.wbpage-one-enter-active,
.wbpage-one-leave-active,
.wbpage-two-enter-active,
.wbpage-two-leave-active {
  transition: all 0.2s ease;
}
</style>
