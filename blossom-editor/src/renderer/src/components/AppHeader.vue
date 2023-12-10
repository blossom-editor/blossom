<template>
  <div class="app-header-root">
    <div class="drag">{{ tryuseComment }}</div>
    <div class="window-workbench">
      <div class="iconbl bl-a-colorpalette-line" @click="themeStrore.show()"></div>

      <el-tooltip content="查看图标" effect="light" placement="top" :show-after="1000" :hide-after="0" :auto-close="2000">
        <div class="iconbl bl-a-radiochoose-line" @click="toRoute('/iconListIndex')"></div>
      </el-tooltip>

      <el-tooltip content="显示网页收藏" effect="light" placement="top" :show-after="1000" :hide-after="0" :auto-close="2000">
        <div class="iconbl bl-folding-line" @click="isShowWebDrawer = !isShowWebDrawer"></div>
      </el-tooltip>

      <el-tooltip content="最佳窗口大小" effect="light" placement="top" :show-after="1000" :hide-after="0" :auto-close="2000">
        <div v-if="isElectron()" class="iconbl bl-computer-line" @click="setBestSize"></div>
      </el-tooltip>

      <div class="divider"></div>

      <div v-if="isElectron() && isWindows()" class="iconbl bl-subtract-line" @click="windowMin"></div>
      <div v-if="isElectron() && isWindows()" :class="['iconbl', isFullScreen ? 'bl-win-reset' : 'bl-box-line']" @click="windowMax"></div>
      <div v-if="isElectron() && isWindows()" class="close iconbl bl-a-closeline-line" @click="windowHide"></div>
    </div>
  </div>

  <el-drawer class="web-collect-drawer" size="420" v-model="isShowWebDrawer">
    <WebCollect></WebCollect>
  </el-drawer>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { toRoute } from '@renderer/router'
import { useThemeStore } from '@renderer/stores/theme'
import { windowMin, windowMax, windowHide, setBestSize } from '@renderer/assets/utils/electron'
import WebCollect from './WebCollect.vue'
import { isWindows, isElectron } from '@renderer/assets/utils/util'
import { isTryuse } from '@renderer/scripts/env'
import SYSTEM from '@renderer/assets/constants/system'

const themeStrore = useThemeStore()

onMounted(() => {
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})

const tryuseComment = computed(() => {
  if (isTryuse()) {
    return `试用版本(${SYSTEM.SYS.VERSION})为最新的开发版本，包含当前正式版中没有的功能。`
  }
  return ``
})

/**
 * 判断是否全屏
 */
const checkFullScreen = () => {
  if (window.outerHeight === screen.availHeight && window.outerWidth === screen.availWidth) {
    return true
  }
  return false
}

const isShowWebDrawer = ref(false)
const isFullScreen = ref(checkFullScreen())

const handleResize = () => {
  isFullScreen.value = checkFullScreen()
}
</script>

<style lang="scss" scoped>
.app-header-root {
  @include box(100%, 100%);
  @include flex(row, space-between, center);

  $width-workbench: 220px;
  $width-drag: calc(100% - #{$width-workbench});

  .drag {
    @include flex(row, flex-start, center);
    @include box($width-drag, 100%);
    @include font(12px, 300);
    color: var(--bl-text-color-light);
    -webkit-app-region: drag;
    font-style: italic;
    padding-left: 10px;
  }

  .window-workbench {
    @include box($width-workbench, 100%);
    @include flex(row, flex-end, center);
    color: var(--el-color-primary);

    div {
      @include box(40px, 100%);
      @include flex(row, center, center);
      cursor: pointer;
      transition: 0.3s;

      &:hover {
        color: #fff;
        background-color: var(--el-color-primary-light-5);
      }
    }

    .divider {
      width: 20px;
      cursor: auto;
      &:hover {
        background-color: #00000000;
      }
    }

    .close {
      &:hover {
        background-color: var(--el-color-danger);
      }
    }
  }
}
</style>

<style>
.web-collect-drawer {
  --el-drawer-bg-color: var(--bl-html-color);
  .el-drawer__header {
    margin-bottom: 0;
  }

  .el-drawer__body {
    --el-drawer-padding-primary: 0;
  }
}
</style>
