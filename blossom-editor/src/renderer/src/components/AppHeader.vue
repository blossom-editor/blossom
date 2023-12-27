<template>
  <div class="app-header-root">
    <div class="drag">{{ tryuseComment }}</div>
    <div class="window-workbench">
      <el-popover
        popper-class="header-popover"
        ref="PopoverRef"
        trigger="click"
        width="350px"
        :virtual-ref="ButtonRef"
        :hide-after="10"
        :tabindex="100"
        :offset="5"
        :persistent="false"
        virtual-triggering>
        <div class="header-config-popover">
          <bl-row class="url-container" just="space-between">
            <bl-col width="60px" height="60px" class="iconbl bl-blog" just="center"></bl-col>
            <bl-col width="calc(100% - 70px)" height="60px" align="flex-start" just="flex-start">
              <div class="name"><span>博客地址</span> <span class="iconbl bl-sendmail-line"></span></div>
              <div class="url">
                {{ userStore.userParams.WEB_ARTICLE_URL }}123123123123123123123123123123123123123123123123123123123123123123123123123123123123
              </div>
            </bl-col>
          </bl-row>
          <bl-row class="url-container" height="100%" style="margin-bottom: 6px">
            <bl-col width="60px" height="60px" class="iconbl bl-image--line" just="center"></bl-col>
            <bl-col width="calc(100% - 70px)" height="60px" align="flex-start" just="flex-start">
              <div class="name">图片地址</div>
              <div class="url">{{ userStore.sysParams.BLOSSOM_OBJECT_STORAGE_DOMAIN }}</div>
            </bl-col>
          </bl-row>
          <bl-row just="space-between">
            <el-button text>关闭闪烁提示</el-button>
            <el-button type="primary" plain @click="showQuickSetting">快捷配置</el-button>
          </bl-row>
        </div>
      </el-popover>

      <div class="iconbl bl-blog warn-heightlight" ref="ButtonRef" v-click-outside="onClickOutside"></div>

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

  <el-dialog
    v-model="isShowQuickSetting"
    width="80%"
    style="height: fit-content; max-width: 800px"
    :align-center="true"
    :append-to-body="false"
    :destroy-on-close="true"
    :close-on-click-modal="false"
    draggable>
    <QuickSetting ref="PlanDayInfoRef"></QuickSetting>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref, unref } from 'vue'
import { ClickOutside as vClickOutside } from 'element-plus'
import { toRoute } from '@renderer/router'
import { useThemeStore } from '@renderer/stores/theme'
import { windowMin, windowMax, windowHide, setBestSize, openExtenal } from '@renderer/assets/utils/electron'
import WebCollect from './WebCollect.vue'
import { isWindows, isElectron } from '@renderer/assets/utils/util'
import { isTryuse } from '@renderer/scripts/env'
import SYSTEM from '@renderer/assets/constants/system'
import { useUserStore } from '@renderer/stores/user'
import QuickSetting from '@renderer/views/index/setting/QuickSetting.vue'

const themeStrore = useThemeStore()
const userStore = useUserStore()

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

//#region 快捷配置

const ButtonRef = ref()
const PopoverRef = ref()
const isShowQuickSetting = ref(false)

const onClickOutside = () => {
  unref(PopoverRef).popperRef?.delayHide?.()
}

const showQuickSetting = () => {
  unref(PopoverRef).popperRef?.delayHide?.()
  isShowQuickSetting.value = true
}
//#endregion
</script>

<style lang="scss" scoped>
.app-header-root {
  @include box(100%, 100%);
  @include flex(row, space-between, center);

  $width-workbench: 240px;
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
    @include flex(row, flex-end, flex-end);
    color: var(--el-color-primary);
    padding-right: 4px;

    div {
      @include box(40px, 90%);
      @include flex(row, center, center);
      cursor: pointer;
      transition: 0.3s;
      border-radius: 4px;

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

  .warn-heightlight {
    animation: animated-border 1.5s infinite;
    text-shadow: 0 0 3px #e3a300;
    background-color: #e3a300;
    color: var(--bl-html-color);
  }

  @keyframes animated-border {
    0% {
      // box-shadow: 0 0 0 0 rgba(227, 163, 0, 0.7);
      filter: drop-shadow(0 0 0 #e3a300);
    }

    100% {
      // box-shadow: 0 0 0 10px rgba(227, 163, 0, 0);
      filter: drop-shadow(0 0 30px #e3a300);
    }
  }
}

.header-config-popover {
  @include font(13px, 300);
  margin: 6px;

  .url-container {
    padding: 6px;
    border: 1px solid transparent;
    border-radius: 4px;

    .bl-blog,
    .bl-image--line {
      @include themeBg(#f5f5f5, #060404);
      font-size: 28px;
      border-radius: 4px;
      margin-right: 10px;
    }

    .bl-image--line {
      font-size: 33px;
    }

    .bl-sendmail-line {
      font-size: 14px;
    }

    &:hover {
      border: 1px solid var(--el-border-color);
    }
  }

  .name {
    width: 100%;
    min-height: 20px;
    text-decoration: none;
    color: var(--el-text-color);
  }

  .url {
    @include box(100%, 40px);
    font-size: 12px;
    padding: 2px 5px 0 5px;
    overflow-y: scroll;
    border-radius: 4px;
    color: var(--bl-text-color-light);
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
.header-popover {
  /* inset: 35.3333px 10px auto auto !important; */
  padding: 0 !important;
  .el-popper__arrow {
    /* left: 240.333px !important; */
  }
}
</style>
