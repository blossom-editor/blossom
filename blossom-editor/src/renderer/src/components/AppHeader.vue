<template>
  <div class="app-header-root">
    <div v-if="platform() === 'windows'">
      <img v-if="isDark" class="logo-img" src="@renderer/assets/imgs/blossom_logo_dark.png" />
      <img v-else class="logo-img" src="@renderer/assets/imgs/blossom_logo.png" />
    </div>
    <div class="drag"></div>
    <div class="window-workbench">
      <el-tooltip content="查看图标" effect="light" placement="top" :show-after="1000" :hide-after="0" :auto-close="2000">
        <div class="iconbl bl-a-radiochoose-line" @click="toRoute('/iconListIndex')"></div>
      </el-tooltip>

      <el-tooltip content="显示网页收藏" effect="light" placement="top" :show-after="1000" :hide-after="0" :auto-close="2000">
        <div class="iconbl bl-folding-line" @click="isShowWebDrawer = !isShowWebDrawer"></div>
      </el-tooltip>

      <el-tooltip content="最佳窗口大小" effect="light" placement="top" :show-after="1000" :hide-after="0" :auto-close="2000">
        <div class="iconbl bl-computer-line" @click="setBestSize"></div>
      </el-tooltip>

      <div v-if="platform() === 'windows'" class="iconbl bl-subtract-line" @click="windowMin"></div>
      <div v-if="platform() === 'windows'" :class="['iconbl', isFullScreen ? 'bl-win-reset' : 'bl-box-line']" @click="windowMax"></div>
      <div v-if="platform() === 'windows'" class="close iconbl bl-a-closeline-line" @click="windowHide"></div>
    </div>
  </div>

  <el-drawer class="web-collect-drawer" size="420" v-model="isShowWebDrawer">
    <WebCollect></WebCollect>
  </el-drawer>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'
import { toRoute } from '@renderer/router'
import { useDark } from '@vueuse/core'
import { windowMin, windowMax, windowHide, setBestSize } from '@renderer/assets/utils/electron'
import WebCollect from './WebCollect.vue'
import { platform } from '@renderer/assets/utils/util'

onMounted(() => {
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
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

const isDark = useDark()
const isShowWebDrawer = ref(false)
const isFullScreen = ref(checkFullScreen())

const handleResize = () => {
  isFullScreen.value = checkFullScreen()
}
</script>

<style lang="scss">
.app-header-root {
  @include box(100%, 100%);
  @include flex(row, space-between, center);

  $width-img: 30px;
  $width-workbench: 160px;
  $width-drag: calc(100% - #{$width-img} - #{$width-workbench});

  .logo-img {
    @include box(18px, 18px);
    margin: 0 6px;
  }

  .drag {
    @include box($width-drag, 100%);
    -webkit-app-region: drag;
  }

  .window-workbench {
    @include box($width-workbench, 100%);
    @include flex(row, flex-end, center);
    color: var(--el-color-primary);
    // border-bottom: 1px solid var(--el-border-color);

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
  /* background-color: #FFFFFFD1; */

  .el-drawer__header {
    margin-bottom: 0;
  }

  .el-drawer__body {
    --el-drawer-padding-primary: 0;
  }
}
</style>
