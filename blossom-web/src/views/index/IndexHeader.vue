<template>
  <div :class="['blossom-header-root', props.bg ? 'blossom-header-bg' : '']">
    <bl-row class="head-row" width="auto" height="100%">
      <div class="blossom-logo" @click="toLogin">
        <img :src="logo()" :style="getThemeLogoStyle()" />
      </div>
      <div class="project-name" @click="toRoute('/home')">{{ sysName() }}</div>
    </bl-row>

    <bl-row class="head-row" width="auto" height="100%">
      <el-popover
        popper-class="popper-dark"
        placement="bottom-start"
        trigger="click"
        :show-arrow="false"
        :hide-after="0"
        :offset="-5"
        transition="el-zoom-in-top">
        <template #reference>
          <div v-show="links() && links().length > 0" class="popper-target">更多</div>
        </template>
        <div class="popper-content">
          <div class="item" v-for="link in links()" @click="toView(link.URL)"><img :src="link.LOGO" style="width: 25px" />{{ link.NAME }}</div>
        </div>
      </el-popover>
      <el-popover
        popper-class="popper-dark"
        placement="bottom-start"
        trigger="click"
        :show-arrow="false"
        :hide-after="0"
        :offset="-5"
        transition="el-zoom-in-top">
        <template #reference>
          <div v-show="userStore.auth && userStore.auth.status === '已登录'" class="popper-target">
            <span class="iconbl bl-apps-line"></span>
          </div>
        </template>
        <div class="popper-content">
          <div class="item" @click="toRoute('/home')"><span class="iconbl bl-a-home1-line"></span>首页</div>
          <div class="item-divider"></div>
          <div class="item" @click="toRoute('/articles')"><span class="iconbl bl-a-texteditorhighlightcolor-line"></span>文章列表</div>
          <div class="item" @click="toRoute('/todo')"><span class="iconbl bl-a-labellist-line"></span>待办事项</div>
          <div class="item" @click="toRoute('/plan')"><span class="iconbl bl-calendar-line"></span>日历计划</div>
          <div class="item" @click="toRoute('/note')"><span class="iconbl bl-note-line"></span>便签</div>
          <div class="item-divider"></div>
          <div class="item" @click="handlLogout"><span class="iconbl bl-logout-circle-line"></span>退出登录</div>
        </div>
      </el-popover>
    </bl-row>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { toRoute } from '@/router'
import { toView } from '@/assets/utils/util'
import { useUserStore } from '@/stores/user'
import { logout } from '@/scripts/auth'
import { getLinks, getSysName, getThemeLogoStyle } from '@/scripts/env'
import { isNotBlank } from '@/assets/utils/obj'

const userStore = useUserStore()

const props = defineProps({
  bg: {
    type: Boolean,
    default: false
  }
})

let recount: NodeJS.Timeout | undefined
const tryLoginCount = ref(0)

const toLogin = () => {
  tryLoginCount.value += 1
  if (!recount) {
    recount = setTimeout(() => {
      tryLoginCount.value = 0
      clearTimeout(recount)
      recount = undefined
    }, 5000)
  }
  if (tryLoginCount.value === 7) {
    toRoute('/login')
  }
}

const sysName = () => {
  if (userStore.userParams.WEB_LOGO_NAME) {
    return userStore.userParams.WEB_LOGO_NAME
  }
  return getSysName()
}

const logo = () => {
  if (userStore.userParams.WEB_LOGO_URL && isNotBlank(userStore.userParams.WEB_LOGO_URL)) {
    return userStore.userParams.WEB_LOGO_URL
  }
  return 'blog-logo.png'
}

const links = () => {
  if (isNotBlank(userStore.links)) {
    return JSON.parse(userStore.links)
  } else {
    getLinks()
  }
}

const handlLogout = () => {
  logout()
  toRoute('/home')
}
</script>

<style lang="scss">
.blossom-header-root {
  @include box(100%, 60px);
  @include flex(row, space-between, center);
  padding: 10px 10px;
  z-index: 2003;

  .head-row {
    line-height: 40px;
  }

  .blossom-logo {
    @include box(40px, 40px);
    cursor: pointer;

    img {
      @include box(40px, 40px);
    }
  }

  .project-name {
    @include box(auto, 100%);
    margin-left: 10px;
    text-shadow: 3px 3px 5px #ababab;
    cursor: pointer;

    color: transparent;
    font-family: current, sans-serif;
    letter-spacing: 1px;
    background: linear-gradient(90deg, var(--bl-header-color), #fff3fc, var(--bl-header-color));
    -webkit-background-clip: text;
    animation: glow 10s linear infinite;
    transition: 1.5s;
    background-size: 300%;

    @keyframes glow {
      to {
        background-position: -300%;
      }
    }
  }
}

.popper-target {
  height: 100%;
  font-size: 15px;
  color: #909090;
  padding: 0 10px;
  text-shadow: 3px 3px 5px #000;
  user-select: none;
  transition: color 0.3s;

  &:hover {
    color: #fff3fc;
  }

  .iconbl {
    font-size: 18px;
  }
}

.popper-content {
  .item {
    @include flex(row, flex-start, center);
    padding: 0 10px;
    color: #aeaeae;
    border-radius: 5px;
    transition: 0.3s;
    white-space: pre-line;
    text-shadow: 3px 3px 5px #1d1d1d;
    cursor: pointer;

    img {
      margin: 5px 10px 5px 0;
      border-radius: 50%;
    }

    .iconbl {
      font-size: 20px;
      margin-right: 10px;
    }

    &:hover {
      color: #fff3fc;
    }
  }

  .item-divider {
    border-top: 1px solid #5c5c5c;
    margin-top: 5px;
    padding-bottom: 5px;
  }
}

.blossom-header-bg {
  background-image: linear-gradient(to bottom right, #3e464e, #212121);
  box-shadow: 0 1px 8px 1px #212121;
}
</style>
