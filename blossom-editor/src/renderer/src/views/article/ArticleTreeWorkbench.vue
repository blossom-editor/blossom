<template>
  <div class="doc-workbench-root">
    <bl-row justify-content="flex-end" align-items="flex-end">
      <div v-show="curDoc !== undefined"
        style="font-size:12px;text-align: right;color: var(--el-text-color-placeholder);">
        <span>《{{ curDoc?.name }}》</span>
        <br />
        <span style="font-size: 9px;padding-right: 5px;">{{ curDoc?.id }}</span>
      </div>
    </bl-row>
    <bl-row justify-content="flex-end" align-items="flex-end">
      <el-tooltip content="显示排序" effect="blossomt" placement="top" :hide-after="0" :auto-close="2000">
        <div>
          <div class="iconbl bl-a-leftdirection-line" @click="emits('show-sort')"></div>
        </div>
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
  </div>

  <el-dialog v-model="isShowDocInfoDialog" width="535" top="100px" style="margin-left: 65px;
    --el-dialog-padding-primary:0;
    --el-dialog-border-radius:10px;
    --el-dialog-box-shadow:var(--xz-box-shadow-dialog)" :append-to-body="true" :destroy-on-close="true"
    :close-on-click-modal="false" draggable>
    <ArticleInfo ref="ArticleInfoRef" @saved="savedCallback"></ArticleInfo>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, nextTick, inject } from "vue"
import { provideKeyDocInfo } from '@renderer/views/doc/doc'
import ArticleInfo from '@renderer/views/article/ArticleInfo.vue'
import { openNewArticleReferenceWindow } from "@renderer/assets/utils/electron";

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

// 当前菜单中选择的文档
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
// ---------- 新增修改按钮 ----------
const ArticleInfoRef = ref()
// 显示编辑 dialog
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

const emits = defineEmits(['refreshDocTree', 'show-sort'])

</script>

<style scoped lang="scss">
.doc-workbench-root {
  @include box(100%, 100%);
  @include flex(column, flex-end, flex-end);
  border-bottom: 1px solid var(--el-border-color);

  .iconbl {
    text-shadow: var(--xz-iconbl-text-shadow);
    color: var(--el-text-color-secondary);
    font-size: 25px;
    padding: 3px;
    cursor: pointer;
    transition: 0.3s;

    &:hover {
      color: var(--el-color-primary);
    }
  }

  .bl-a-leftdirection-line {
    padding-bottom: 5px;
    padding-right: 0px;
  }

  // 公开图标
  .bl-cloud-fill {
    color: #7AC20C;
    @include themeText(0px 5px 5px #79C20CA4, 2px 3px 5px rgba(183, 183, 183, 0.8));
  }

  .bl-cloud-fill,
  .bl-cloud-line {
    &:hover {
      color: #7AC20C;
      @include themeText(5px 5px 5px #79C20CA4, 2px 3px 5px rgba(183, 183, 183, 0.8));
    }
  }

  // 专题图标
  .bl-a-lowerrightpage-fill {
    color: salmon;
    @include themeText(0px 5px 5px rgba(250, 128, 114, 0.546), 2px 3px 5px rgba(183, 183, 183, 0.8));
  }

  // 专题图标
  .bl-a-lowerrightpage-fill,
  .bl-a-lowerrightpage-line {
    &:hover {
      color: salmon;
      @include themeText(5px 5px 5px rgba(250, 128, 114, 0.546), 2px 3px 5px rgba(183, 183, 183, 0.8));
    }
  }

  //  star 图标
  .bl-star-fill {
    color: rgb(237, 204, 11);
    @include themeText(0px 5px 5px rgba(237, 204, 11, 0.546), 2px 3px 5px rgba(183, 183, 183, 0.8));
  }

  .bl-star-line,
  .bl-star-fill {
    font-size: 23px;
    padding-bottom: 5px;

    &:hover {
      color: rgb(237, 204, 11);
      @include themeText(5px 5px 5px rgba(237, 203, 11, 0.756), 2px 3px 5px rgba(183, 183, 183, 0.8));
    }
  }

  // 刷新图标
  .bl-a-cloudrefresh-line,
  .bl-a-fileadd-line {
    &:active {
      color: #ffffff;
    }
  }

  .bl-a-fileadd-line {}

  .bl-correlation-line {
    color: var(--el-color-primary);
    font-size: 40px;
    padding-bottom: 0px;
  }
}
</style>