<template>
  <div class="doc-workbench-root">
    <bl-row just="flex-end" align="flex-end">
      <div v-show="curFolder !== undefined"
        style="font-size:12px;text-align: right;color: var(--el-text-color-placeholder);">
        <span>《{{ curFolder?.name }}》</span>
        <br />
        <span style="font-size: 9px;padding-right: 5px;">{{ curFolder?.id }}</span>
      </div>
    </bl-row>

    <!--  -->
    <bl-row just="flex-end" align="flex-end">
      <el-tooltip content="刷新列表" effect="blossomt" placement="top" :hide-after="0" :auto-close="2000">
        <div class="iconbl bl-a-cloudrefresh-line" @click="refreshDocTree()"></div>
      </el-tooltip>
      <el-tooltip content="新增文件夹或文档" effect="blossomb" :hide-after="0" :auto-close="2000">
        <div class="iconbl bl-a-fileadd-line" @click="handleShowAddDocInfoDialog()"></div>
      </el-tooltip>
    </bl-row>
  </div>

  <el-dialog v-model="isShowDocInfoDialog" width="535" top="100px" style="margin-left: 65px;
    --el-dialog-padding-primary:0;
    --el-dialog-border-radius:10px;
    --el-dialog-box-shadow:var(--xz-box-shadow-dialog)" :append-to-body="true" :destroy-on-close="true"
    :close-on-click-modal="false" draggable>
    <PictureInfo ref="PictureInfoRef" @saved="savedCallback"></PictureInfo>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, nextTick, inject } from "vue"
import { provideKeyDocInfo } from '@renderer/views/doc/doc'
import PictureInfo from '@renderer/views/picture/PictureInfo.vue'

// 当前菜单中选择的文档
const curFolder = inject(provideKeyDocInfo)


// ---------- 新增修改按钮 ----------
const PictureInfoRef = ref()
// 显示编辑 dialog
const isShowDocInfoDialog = ref<boolean>(false);
const handleShowAddDocInfoDialog = () => {
  isShowDocInfoDialog.value = true
  nextTick(() => {
    PictureInfoRef.value.reload('add')
  })
}

const refreshDocTree = () => {
  emits('refreshDocTree')
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

const emits = defineEmits(['refreshDocTree'])

</script>

<style scoped lang="scss">
.doc-workbench-root {
  @include box(100%, 100%);
  @include flex(column, flex-end, flex-end);

  .iconbl {
    color: var(--el-text-color-secondary);
    text-shadow: var(--xz-iconbl-text-shadow);
    font-size: 25px;
    padding: 5px;
    cursor: pointer;
    transition: 0.3s;

    &:hover {
      color: var(--el-color-primary);
    }
  }

  // 刷新图标
  .bl-a-cloudrefresh-line,
  .bl-a-fileadd-line {
    &:active {
      color: #ffffff;
    }
  }

  .bl-a-fileadd-line {
    font-size: 35px;
    color: var(--el-color-primary);
    padding-bottom: 2px;
  }
}
</style>