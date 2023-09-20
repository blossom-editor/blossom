<template>
  <div class="doc-workbench-root">
    <bl-row just="flex-end" align="flex-end">
      <div v-show="curDoc !== undefined" style="font-size:12px;text-align: right;color: var(--bl-text-color-light);">
        <span>《{{ curDoc?.name }}》</span>
        <br />
        <span style="font-size: 9px;padding-right: 5px;">{{ curDoc?.id }}</span>
      </div>
    </bl-row>
    <bl-row class="wb-page-container">
      <Transition name="wbpage-one">
        <bl-row class="wb-page-item" just="flex-end" align="flex-end" v-if="workbenchPage == 1">
          <el-tooltip content="显示排序" effect="blossomt" placement="top" :hide-after="0" :auto-close="2000">
            <div class="iconbl bl-a-leftdirection-line" @click="emits('show-sort')"></div>
          </el-tooltip>
          <el-tooltip content="只显示公开" effect="blossomt" placement="top" :hide-after="0" :auto-close="2000">
            <div v-if="props.showOpen">
              <div v-if="onlyOpen" class="iconbl bl-cloud-fill" @click="changeOnlyOpen()"></div>
              <div v-else class="iconbl bl-cloud-line" @click="changeOnlyOpen()"></div>
            </div>
          </el-tooltip>
          <el-tooltip content="只显示专题" effect="blossomt" placement="top" :hide-after="0" :auto-close="2000">
            <div v-if="props.showSubject">
              <div v-if="onlySubject" class="iconbl bl-a-lowerrightpage-fill" @click="changeOnlySubject()"></div>
              <div v-else class="iconbl bl-a-lowerrightpage-line" @click="changeOnlySubject()"></div>
            </div>
          </el-tooltip>
          <el-tooltip content="只显示 Star 文章" effect="blossomt" placement="top" :hide-after="0" :auto-close="2000">
            <div v-if="props.showStar">
              <div v-if="onlyStars" class="iconbl bl-star-fill" @click="changeOnlyStar()"></div>
              <div v-else class="iconbl bl-star-line" @click="changeOnlyStar()"></div>
            </div>
          </el-tooltip>
          <el-tooltip content="刷新列表" effect="blossomt" placement="top" :hide-after="0" :auto-close="2000">
            <div class="iconbl bl-a-cloudrefresh-line" @click="refreshDocTree()"></div>
          </el-tooltip>
          <el-tooltip content="新增文件夹或文档" effect="blossomb" :hide-after="0" :auto-close="2000">
            <div class="iconbl bl-a-fileadd-line" @click="handleShowAddDocInfoDialog()"></div>
          </el-tooltip>
          <el-tooltip content="文章引用网络" effect="blossomb" :hide-after="0" :auto-close="2000">
            <div class="iconbl bl-correlation-line" @click="openArticleReferenceWindow()"></div>
          </el-tooltip>
        </bl-row>
      </Transition>

      <Transition name="wbpage-two">
        <bl-row class="wb-page-item" just="flex-end" align="flex-end" v-if="workbenchPage == 2">
          <el-tooltip content="查看备份记录" effect="blossomt" placement="top" :hide-after="0" :auto-close="2000">
            <div class="iconbl bl-a-cloudstorage-line" @click="handleShowBackupDialog"></div>
          </el-tooltip>
        </bl-row>
      </Transition>

      <bl-col width="25px" just="end" class="workbench-page" style="position: absolute;right: 0;">
        <el-icon size="13px" class="up" @click="toWorkbenchPage(1)">
          <ArrowUp />
        </el-icon>
        <el-icon size="13px" class="down" @click="toWorkbenchPage(2)">
          <ArrowDown />
        </el-icon>
      </bl-col>

    </bl-row>
  </div>

  <el-dialog v-model="isShowDocInfoDialog" width="535" top="100px" style="margin-left: 320px;" :append-to-body="true"
    :destroy-on-close="true" :close-on-click-modal="false" draggable>
    <ArticleInfo ref="ArticleInfoRef" @saved="savedCallback"></ArticleInfo>
  </el-dialog>

  <el-dialog class="backup-dialog" v-model="isShowBackupDialog" width="80%" top="100px" style="height: 80%;"
    :append-to-body="true" :destroy-on-close="true" :close-on-click-modal="false" draggable>
    <ArticleBackup ref="ArticleBackupRef"></ArticleBackup>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, nextTick, inject } from "vue"
import { ArrowUp, ArrowDown } from '@element-plus/icons-vue'
import { provideKeyDocInfo } from '@renderer/views/doc/doc'
import { openNewArticleReferenceWindow } from "@renderer/assets/utils/electron"
import ArticleInfo from './ArticleInfo.vue'
import ArticleBackup from './ArticleBackup.vue'

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

//#region 控制台翻页

const workbenchPage = ref(1)

const toWorkbenchPage = (page: number) => {
  workbenchPage.value = page
}

//#endregion


//#region 查询
const curDoc = inject(provideKeyDocInfo)

const onlyOpen = ref<boolean>(false)// 只显示公开
const onlySubject = ref<boolean>(false)// 只显示专题
const onlyStars = ref<boolean>(false)// 只显示 star

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

//#region 新增窗口
const ArticleInfoRef = ref()
const isShowDocInfoDialog = ref<boolean>(false);

const handleShowAddDocInfoDialog = () => {
  isShowDocInfoDialog.value = true
  nextTick(() => { ArticleInfoRef.value.reload('add') })
}

const openArticleReferenceWindow = () => {
  openNewArticleReferenceWindow()
}

const refreshDocTree = () => {
  emits('refreshDocTree', onlyOpen.value, onlySubject.value, onlyStars.value)
}

/**
 * 保存后的回调
 * 1. [暂无] 刷新菜单列表
 * 2. 关闭 dialog 页面
 */
const savedCallback = () => {
  isShowDocInfoDialog.value = false;
}

//#endregion

//#region 备份记录
const ArticleBackupRef = ref()
const isShowBackupDialog = ref<boolean>(false);

const handleShowBackupDialog = () => {
  isShowBackupDialog.value = true
}

//#endregion

const emits = defineEmits(['refreshDocTree', 'show-sort'])
defineExpose({ handleShowBackupDialog })
</script>

<style scoped lang="scss">
@import '../doc/tree-workbench.scss';

.wb-page-container {
  position: relative;
  height: 44px;

  .wb-page-item {
    width: 225px !important;
    height: 44px;
    position: absolute;
    left: 0;
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

<style lang="scss">
.backup-dialog {

  .el-dialog__body {
    height: calc(100% - 10px);
  }
}
</style>