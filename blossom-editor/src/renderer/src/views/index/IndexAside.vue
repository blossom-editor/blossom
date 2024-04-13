<template>
  <div class="index-aside-root">
    <div class="item-logo" v-if="!viewStyle.isShowAsideSimple && viewStyle.isShowAsideLogo">
      <logo :name="true"></logo>
    </div>
    <div
      :class="[
        isMacOS() ? 'mac' : '',
        viewStyle.isShowAsideSimple ? 'aside-item-container-simple' : 'aside-item-container',
        viewStyle.isGlobalShadow ? 'aside-item-container-heavy' : 'aside-item-container-light',
        viewStyle.isShowAsideLogo ? '' : 'no-logo'
      ]">
      <!-- 模块菜单 -->
      <div class="aside-item item-menu-container">
        <div class="aside-item item-user top-user" v-if="viewStyle.isShowAsideSimple || !viewStyle.isShowAsideLogo">
          <user></user>
        </div>
        <div v-for="menu in menus" :key="menu.name" :class="['item-menu iconbl', menu.icon, isActive(menu.path)]" @click="toRoute(menu)">
          <span>{{ menu.name }}</span>
        </div>
      </div>
      <div>
        <div class="aside-item item-user" v-if="!viewStyle.isShowAsideSimple && viewStyle.isShowAsideLogo">
          <user></user>
        </div>
        <div class="aside-item item-upload" v-if="!viewStyle.isShowAsideSimple && viewStyle.isShowAsideUpload">
          <upload></upload>
        </div>
        <div class="aside-item item-setting">
          <setting></setting>
        </div>
      </div>
    </div>
  </div>
  <Teleport to="body">
    <ThemeSetting v-if="themeStrore.isShow"></ThemeSetting>
  </Teleport>
</template>

<script setup lang="ts">
import router from '@renderer/router'
import { ref, watch } from 'vue'
import { storeToRefs } from 'pinia'
import { useThemeStore } from '@renderer/stores/theme'
import { useConfigStore } from '@renderer/stores/config'
import { useUserStore, AuthStatus } from '@renderer/stores/user'
import { ElNotification } from 'element-plus'
import user from './AsideUser.vue'
import upload from './AsideUpload.vue'
import setting from './AsideSetting.vue'
import logo from '@renderer/components/Logo.vue'
import ThemeSetting from './setting/ThemeSetting.vue'
import { isMacOS } from '@renderer/assets/utils/util'

const themeStrore = useThemeStore()
const userStore = useUserStore()
const configStore = useConfigStore()
const { viewStyle } = configStore
const { auth } = storeToRefs(userStore)

interface AsideMenu {
  login: boolean
  name: string
  path: string
  icon: string
}
const menus = ref<AsideMenu[]>([
  { login: true, name: 'Home', path: '/home', icon: 'bl-a-home1-line' },
  { login: true, name: 'Editor', path: '/articleIndex', icon: 'bl-a-texteditorhighlightcolor-line' },
  { login: true, name: 'Pic', path: '/pictureIndex', icon: 'bl-picture-line' },
  { login: true, name: 'Todo', path: '/todoIndex', icon: 'bl-a-labellist-line' },
  { login: true, name: 'Plan', path: '/planIndex', icon: 'bl-calendar-line' },
  { login: true, name: 'Note', path: '/noteIndex', icon: 'bl-note-line' }
])

const activeMenuPath = ref<string>('home')

watch(
  () => router.currentRoute.value,
  (newRoute) => {
    activeMenuPath.value = newRoute.path
  },
  { immediate: true }
)

/**
 * 跳转页面, 非登录状态无法跳转
 */
const toRoute = (menu: AsideMenu) => {
  if (menu.login && !isLogin()) {
    ElNotification.error({
      title: '未登录',
      message: `你的登录状态已失效, 请在左下角登录 ↙`,
      offset: 30,
      position: 'bottom-left'
    })
    return
  }
  activeMenuPath.value = menu.path
  router.push(menu.path)
}

const isActive = (path: string): string => {
  if (activeMenuPath.value === path) {
    if (viewStyle.isGlobalShadow) {
      return 'is-active active-shadow-heavy'
    } else {
      return 'is-active active-shadow-light'
    }
  }
  return ''
}

const isLogin = () => {
  return auth.value.status === AuthStatus.Succ
}
</script>

<style scoped lang="scss">
.index-aside-root {
  @include box(100%, 100%);
  @include flex(column, flex-end, center);

  .item-logo {
    @include box(100%, 95px);
    @include flex(column, flex-end, center);
    background-color: var(--bl-html-color);
  }

  .aside-item-container {
    @include box(100%, calc(100% - 105px));
    @include flex(column, space-between, center);
    border-top-right-radius: 10px;
    background-color: var(--bl-bg-color);

    .aside-item {
      width: 100%;
      overflow: hidden;
    }

    .item-menu-container {
      @include box(100%, calc(100% - 100px - 65px - 68px));
      @include flex(column, flex-start, center);
      @include themeColor(#909399, #a3a6ad);
      padding-top: 7px;
      border-top-right-radius: 10px;

      .is-active {
        background: var(--el-color-primary-light-5);
        color: #ffffff;
      }

      .active-shadow-heavy {
        @include themeShadow(2px 4px 7px 2px rgba(49, 49, 49, 0.3), 2px 4px 7px 2px rgb(28, 28, 28));
        @include themeText(2px 4px 5px rgba(107, 104, 104, 1), 2px 4px 5px rgba(183, 183, 183, 1));
      }

      .active-shadow-light {
        @include themeShadow(2px 2px 7px rgba(49, 49, 49, 0.3), 2px 4px 7px rgb(28, 28, 28));
        @include themeText(2px 3px 4px rgba(107, 104, 104, 1), 2px 4px 5px rgba(183, 183, 183, 1));
      }

      .item-menu {
        @include flex(column, center, center);
        @include font(25px);
        width: 48px;
        padding: 5px 0;
        margin: 0 1px 5px 0;
        border-radius: 10px;
        transition:
          background-color 0.3s,
          box-shadow 0.3s;
        cursor: pointer;

        span {
          @include font(12px);
          transform: scale(0.9);
        }

        &:hover {
          background-color: var(--el-color-primary-light-5);
          box-shadow: 2px 4px 7px 2px rgba(0, 0, 0, 0.2);
        }

        &:active {
          background-color: var(--el-color-primary-light-5);
        }
      }
    }

    .item-user {
      @include box(100%, 65px);
    }

    .top-user {
      margin-top: 15px;
      margin-bottom: 25px;
    }

    .item-upload {
      @include box(100%, 100px);
      border-top: 1px var(--el-border-color) var(--el-border-style);
    }

    .item-setting {
      @include box(100%, 68px);
      border-top: 1px var(--el-border-color) var(--el-border-style);
      box-sizing: border-box;
    }
  }

  .aside-item-container.no-logo {
    @include box(100%, 100%);
    border-top-right-radius: 0px;
  }

  .aside-item-container-heavy {
    @include themeShadow(2px 3px 10px rgba(49, 49, 49, 0.3), 2px 2px 7px rgb(0, 0, 0));
  }

  .aside-item-container-light {
    @include themeShadow(2px 0 6px rgba(49, 49, 49, 0.3), 2px 2px 3px rgb(21, 21, 21));
  }

  .aside-item-container-simple {
    @include box(100%, 100%);
    @include flex(column, space-between, center);
    background-color: var(--bl-bg-color);
    box-shadow: none !important;
    border-right: 1px solid var(--el-border-color);

    .aside-item {
      width: 100%;
      overflow: hidden;
    }

    .item-menu-container {
      @include box(100%, calc(100% - 65px - 68px));
      @include flex(column, flex-start, center);
      @include themeColor(#909399, #a3a6ad);
      padding-top: 5px;

      .is-active {
        color: var(--el-color-primary);
      }

      .item-menu {
        @include flex(column, center, center);
        font-size: 22px;
        padding: 6px 0;
        cursor: pointer;

        span {
          display: none;
        }
        .top-user {
          margin-bottom: 10px;
        }
      }

      .bl-a-home1-line,
      .bl-a-texteditorhighlightcolor-line {
        font-size: 25px;
      }

      .item-user {
        @include box(100%, 65px);
        margin-top: 10px;
      }

      .item-upload {
        display: none !important;
      }

      .item-setting {
        @include box(100%, 68px);
        border-top: 1px var(--el-border-color) var(--el-border-style);
        box-sizing: border-box;
      }
    }
  }

  .aside-item-container-simple.mac {
    @include box(100%, calc(100% - 30px));
    border-top-right-radius: 10px;
    border-top: 1px solid var(--el-border-color);

    .item-user {
      height: 60px;
      margin-top: 0;
    }
  }

  :deep(.el-divider) {
    margin: 0;
  }
}
</style>
