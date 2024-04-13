<template>
  <div class="doc-workbench-root">
    <bl-row class="wb-page-container">
      <bl-row class="wb-page-item" just="flex-start" align="flex-end" height="44px">
        <el-tooltip content="文章引用网络" effect="light" popper-class="is-small" placement="top" :offset="-5" :hide-after="0">
          <div class="iconbl bl-correlation-line" @click="openArticleReferenceWindow()"></div>
        </el-tooltip>
        <el-tooltip content="全文搜索" effect="light" popper-class="is-small" placement="top" :offset="9" :hide-after="0">
          <div class="iconbl bl-search-line" @click="showSearch()"></div>
        </el-tooltip>
        <el-tooltip content="备份记录" effect="light" popper-class="is-small" placement="top" :offset="8" :hide-after="0">
          <div class="iconbl bl-a-cloudstorage-line" @click="handleShowBackupDialog"></div>
        </el-tooltip>
        <el-tooltip content="文章回收站" effect="light" popper-class="is-small" placement="top" :offset="8" :hide-after="0">
          <div class="iconbl bl-delete-line" @click="handleShowRecycleDialog"></div>
        </el-tooltip>
        <!-- <el-tooltip content="文档快速编辑" effect="light" popper-class="is-small" placement="top" :offset="8" :hide-after="0">
          <div class="iconbl bl-article-line" @click=""></div>
        </el-tooltip> -->
      </bl-row>
    </bl-row>
  </div>

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
</template>

<script setup lang="ts">
import { ref, nextTick, onDeactivated } from 'vue'
import { openNewArticleReferenceWindow } from '@renderer/assets/utils/electron'
import { useLifecycle } from '@renderer/scripts/lifecycle'
import hotkeys from 'hotkeys-js'
import ArticleBackup from './ArticleBackup.vue'
import ArticleRecycle from './ArticleRecycle.vue'

useLifecycle(
  () => bindKeys(),
  () => bindKeys()
)

onDeactivated(() => {
  unbindKeys()
})

//#region --------------------------------------------------< 控制台更多选项 >--------------------------------------------------
const moreMenu = ref<RightMenu>({ show: false, clientX: 0, clientY: 0 })

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
const emits = defineEmits(['show-sort', 'show-search'])
defineExpose({ handleShowBackupDialog })
</script>

<style scoped lang="scss">
@import '../doc/tree-workbench.scss';

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

    .bl-correlation-line {
      font-size: 40px;
      padding-bottom: 0px;
    }
  }
}
</style>
