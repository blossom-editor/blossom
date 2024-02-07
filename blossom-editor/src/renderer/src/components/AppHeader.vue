<template>
  <div class="app-header-root">
    <div class="drag">{{ tryuseComment }}</div>
    <div class="window-workbench">
      <el-popover
        popper-class="caution-popover"
        ref="PopoverRef"
        trigger="click"
        width="350px"
        :virtual-ref="ButtonRef"
        :hide-after="10"
        :tabindex="100"
        :offset="10"
        :persistent="false"
        virtual-triggering>
        <div v-if="!userStore.isLogin" class="caution-popover-placeholder">登录后查看</div>
        <div v-else class="caution-content">
          <bl-row class="caution-row caution-row-warn" v-if="!userStore.paramIsCorrect" @click="showQuickSetting">
            <bl-col width="40px" height="40px" class="iconbl bl-blog" just="center"></bl-col>
            <bl-col just="center">您有一些设置需要修改，点击快速设置。</bl-col>
          </bl-row>

          <!-- <bl-row class="caution-row">
            <bl-col width="40px" height="40px" class="iconbl bl-a-cloudrefresh-line" just="center"></bl-col>
            <bl-col just="center">发现新版本，点击查看更新内容。</bl-col>
          </bl-row> -->

          <bl-row class="no-more" just="center"> 无更多内容 </bl-row>
        </div>
        <bl-row v-if="userStore.isLogin" class="caution-footer" just="flex-end">
          <div @click="showQuickSetting">快速配置</div>
        </bl-row>
      </el-popover>
      <div
        v-if="!props.simple"
        :class="['iconbl', 'bl-caution-line', userStore.paramIsCorrect ? '' : 'warn-heightlight']"
        ref="ButtonRef"
        v-click-outside="onClickOutside"></div>

      <el-tooltip content="主题配置" popper-class="is-small" transition="none" effect="light" placement="top" :show-after="0" :hide-after="0">
        <div v-if="!props.simple" class="iconbl bl-a-colorpalette-line" @click="themeStrore.show()"></div>
      </el-tooltip>

      <el-tooltip content="所有图标" popper-class="is-small" transition="none" effect="light" placement="top" :show-after="0" :hide-after="0">
        <div v-if="!props.simple" class="iconbl bl-a-radiochoose-line" @click="toRoute('/iconListIndex')"></div>
      </el-tooltip>

      <el-tooltip content="网页收藏" popper-class="is-small" transition="none" effect="light" placement="top" :show-after="0" :hide-after="0">
        <div v-if="!props.simple" class="iconbl bl-folding-line" @click="isShowWebDrawer = !isShowWebDrawer"></div>
      </el-tooltip>

      <el-tooltip content="最佳窗口大小" popper-class="is-small" transition="none" effect="light" placement="top" :show-after="0" :hide-after="0">
        <div v-if="isElectron()" :class="['iconbl bl-computer-line', isWindows() ? '' : 'electron-mac-last']" @click="setBestSize"></div>
      </el-tooltip>

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
    class="bl-dialog-bigger-headerbtn"
    width="750px"
    :align-center="true"
    :append-to-body="true"
    :lock-scroll="false"
    :destroy-on-close="true"
    :close-on-click-modal="false"
    draggable>
    <QuickSetting ref="PlanDayInfoRef" @completed="quickSettingComplete"></QuickSetting>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref, unref } from 'vue'
import { ClickOutside as vClickOutside } from 'element-plus'
import { toRoute } from '@renderer/router'
import { useUserStore } from '@renderer/stores/user'
import { useThemeStore } from '@renderer/stores/theme'
import { windowMin, windowMax, windowHide, setBestSize } from '@renderer/assets/utils/electron'
import { isWindows, isElectron } from '@renderer/assets/utils/util'
import { isTryuse } from '@renderer/scripts/env'
import SYSTEM from '@renderer/assets/constants/system'
import WebCollect from './WebCollect.vue'
import QuickSetting from '@renderer/views/index/setting/QuickSetting.vue'

const themeStrore = useThemeStore()
const userStore = useUserStore()

onMounted(() => {
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})

const props = defineProps({
  simple: {
    default: false,
    type: Boolean
  }
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

/**
 * 点击外部时关闭
 */
const onClickOutside = () => {
  unref(PopoverRef).popperRef?.delayHide?.()
}

const showQuickSetting = () => {
  unref(PopoverRef).popperRef?.delayHide?.()
  isShowQuickSetting.value = true
}

const quickSettingComplete = () => {
  isShowQuickSetting.value = false
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
    @include flex(row, flex-end, center);
    color: var(--el-color-primary);
    padding-right: 2px;

    div {
      @include box(26px, 26px);
      @include flex(row, center, center);
      cursor: pointer;
      transition: 0.3s;
      border-radius: 4px;

      &:hover {
        color: #fff;
        background-color: var(--el-color-primary-light-5);
      }
    }

    .electron-mac-last {
      border-top-right-radius: 9px;
    }

    .divider {
      width: 20px;
      cursor: auto;
      &:hover {
        background-color: #00000000;
      }
    }

    .bl-subtract-line {
      margin-left: 10px;
    }

    .close {
      &:hover {
        background-color: var(--el-color-danger);
      }
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
    filter: drop-shadow(0 0 0 #e3a300);
  }

  100% {
    filter: drop-shadow(0 0 30px #e3a300);
  }
}

.caution-popover-placeholder {
  @include box(350px, 190px);
  @include flex(row, center, center);
  font-weight: 300;
  font-size: 14px;
}

.caution-content {
  @include box(350px, 250px);
  @include font(13px, 300);
  padding: 10px;
  height: 250px;

  .caution-row {
    height: 50px;
    border: 1px solid var(--el-border-color);
    border-radius: 10px;
    padding: 0 5px;
    transition: border 0.3s;
    margin-bottom: 10px;
    cursor: pointer;

    &:hover {
      border: 1px solid var(--el-color-primary);

      .iconbl {
        background-color: var(--el-color-primary-light-7) !important;
      }
    }

    .iconbl {
      @include themeBg(#f5f5f5, #414141);
      font-size: 20px;
      border-radius: 10px;
      margin-right: 5px;
      transition: background-color 0.3s;
    }
  }

  .caution-row-warn {
    border: 1px solid #e3a300;
    color: #ca9100;
  }

  .no-more {
    font-size: 12px;
    margin: 10px 0;
    color: var(--bl-text-color-light);
  }
}

.caution-footer {
  padding: 5px;
  color: var(--bl-text-color-light);
  div {
    padding: 0 10px;
    cursor: pointer;
  }
}
</style>

<style>
.web-collect-drawer {
  --el-drawer-bg-color: var(--bl-html-color);
  --el-drawer-padding-primary: 10px 10px 0 0;
  --color: var(--bl-text-color-light);
  .el-drawer__header {
    margin-bottom: 0;
  }

  .el-drawer__body {
    --el-drawer-padding-primary: 0;
  }
}
.caution-popover {
  padding: 0 !important;
  border-radius: 10px !important;
}
</style>
