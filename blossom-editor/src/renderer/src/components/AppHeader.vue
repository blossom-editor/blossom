<template>
  <div class="app-header-root">
    <div @click="openDevTools">
      <img v-if="isDark" class="logo-img" src="@renderer/assets/imgs/blossom_logo_dark.png" />
      <img v-else class="logo-img" src="@renderer/assets/imgs/blossom_logo.png" />
    </div>
    <div class="drag">
    </div>
    <div class="window-workbench">
      
      <div class="iconbl bl-a-radiochoose-line" @click="toRoute('/iconListIndex')"></div>
      <div class="iconbl bl-folding-line" @click="isShowWebDrawer = !isShowWebDrawer"></div>
      <div class="iconbl bl-computer-line" @click="setBestSize"></div>
      <div class="iconbl bl-subtract-line" @click="windowMin"></div>
      <div class="iconbl bl-box-line" @click="windowMax"></div>
      <div class="close iconbl bl-a-closeline-line" @click="windowHide"></div>
    </div>
  </div>

  <el-drawer class="web-collect-drawer" size="420" v-model="isShowWebDrawer">
    <WebCollect></WebCollect>
  </el-drawer>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useDark } from '@vueuse/core'
import { openDevTools, windowMin, windowMax, windowHide, setBestSize } from '@renderer/assets/utils/electron'
import WebCollect from './WebCollect.vue'
import { toRoute } from '@renderer/router';

const isDark = useDark();

const isShowWebDrawer = ref(false)

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