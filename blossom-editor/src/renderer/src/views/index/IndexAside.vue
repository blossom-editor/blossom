<template>
  <div class="index-aside-root">
    <div class="item-logo">
      <logo></logo>
    </div>
    <div class="aside-item-container">
      <!-- 模块菜单 -->
      <div class="aside-item item-menu-container">
        <div v-for="menu in menus" :key="menu.name" :class="['item-menu iconbl', menu.icon, isActive(menu.path)]"
          @click="toRoute(menu)">
          <span>{{ menu.name }}</span>
        </div>
      </div>
      <div class="aside-item item-user">
        <user></user>
      </div>
      <div class="aside-item item-upload">
        <upload></upload>
      </div>
      <div class="aside-item item-setting">
        <setting></setting>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import router from '@renderer/router'
import { ref, watch } from 'vue'
import { storeToRefs } from 'pinia'
import { useUserStore, AuthStatus } from '@renderer/stores/user'
import { ElNotification } from 'element-plus'
import user from './AsideUser.vue'
import upload from './AsideUpload.vue'
import setting from './AsideSetting.vue'
import logo from '@renderer/components/Logo.vue'

//#region ----------------------------------------< panin store >--------------------------------------
const userStore = useUserStore()
const { auth } = storeToRefs(userStore)

interface AsideMenu { login: boolean, name: string, path: string, icon: string }
const menus = ref<AsideMenu[]>([
  { login: true, name: 'Home', path: '/home', icon: 'bl-a-home1-line' },
  { login: true, name: 'Editor', path: '/articleIndex', icon: 'bl-a-texteditorhighlightcolor-line' },
  { login: true, name: 'Pic', path: '/pictureIndex', icon: 'bl-picture-line' },
  { login: true, name: 'Plan', path: '/planIndex', icon: 'bl-calendar-line' },
  { login: true, name: 'Note', path: '/noteIndex', icon: 'bl-note-line' },
  { login: false, name: 'Icon', path: '/iconListIndex', icon: 'bl-a-radiochoose-line' },
])

const activeMenuPath = ref<string>('home');

watch(() => router.currentRoute.value, (newRoute) => {
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
  activeMenuPath.value = menu.path;
  router.push(menu.path)
}

const isActive = (path: string): string => {
  if (activeMenuPath.value === path) {
    return 'is-active'
  }
  return ''
}

const isLogin = () => {
  return auth.value.status === AuthStatus.Succ
}

</script>

<style scoped lang="scss">
$height-logo: 85px;
$height-upload: 100px;
$height-user: 65px;
$height-setting: 68px;
$height-menu: calc(100% - #{$height-upload} - #{$height-user} - #{$height-setting});

.index-aside-root {
  @include box(100%, 100%);
  @include flex(column, flex-start, center);

  .item-logo {
    @include box(100%, $height-logo);
    background-color: var(--bl-html-color);
  }

  .aside-item-container {
    @include box(100%, calc(100% - #{$height-logo}));
    @include themeShadow(2px 3px 7px 2px rgba(49, 49, 49, 0.3), 2px 3px 7px 2px rgb(0, 0, 0));
    border-top-right-radius: 10px;

    .aside-item {
      width: 100%;
      overflow: hidden;
      background-color: var(--bl-bg-color);
    }

    .item-menu-container {
      @include box(100%, $height-menu);
      @include flex(column, flex-start, center);
      @include themeColor(#909399, #A3A6AD);
      padding-top: 5px;
      border-top-right-radius: 10px;

      .is-active {
        @include themeShadow(2px 4px 7px 2px rgba(49, 49, 49, 0.3), 2px 4px 7px 2px rgb(28, 28, 28));
        @include themeText(2px 4px 5px rgba(107, 104, 104, 1), 2px 4px 5px rgba(183, 183, 183, 1));
        background: var(--el-color-primary-light-5);
        color: #ffffff;
      }

      .item-menu {
        @include flex(column, center, center);
        @include font(25px);
        width: 48px;
        padding: 5px 0;
        margin: 0 1px 5px 0;
        border-radius: 10px;
        transition: 0.3s;
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
      @include box(100%, $height-user);
    }

    .item-upload {
      @include box(100%, $height-upload);
      border-top: 1px var(--el-border-color) var(--el-border-style);
    }

    .item-setting {
      @include box(100%, $height-setting);
      border-top: 1px var(--el-border-color) var(--el-border-style);
      box-sizing: border-box;
    }
  }


  :deep(.el-divider) {
    margin: 0;
  }

}
</style>