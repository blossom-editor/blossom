<template>
  <div class="blossom-home-root">
    <div class="home-main">
      <div class="home-main-userinfo">
        <UserInfo></UserInfo>
      </div>
      <div class="home-main-right">
        <div class="home-main-right-chart">
          <ChartLineWords ref="ChartLineWordsRef"></ChartLineWords>
        </div>
        <div class="home-main-right-subject">
          <HomeSubject></HomeSubject>
        </div>
      </div>
    </div>
    <div class="home-footer">
      <div class="custom-info">
        <div v-if="isNotBlank(gwab())" v-html="gwab()"></div>
        <div v-if="isNotBlank(ipc())" style="cursor: pointer" @click="openNew('https://beian.miit.gov.cn/')">
          {{ ipc() }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { isNotBlank } from '@/assets/utils/obj'
import { getGwab, getIpc, getEmail } from '@/scripts/env'

import UserInfo from './HomeUserInfo.vue'
import ChartLineWords from './ChartLineWords.vue'
import HomeSubject from './HomeSubject.vue'
import blossom from '@/assets/constants/blossom'

const userStore = useUserStore()
const ChartLineWordsRef = ref()
onMounted(() => {
  ChartLineWordsRef.value.reload()
})

const openNew = (url: string) => {
  window.open(url)
}

const gwab = () => {
  if (userStore.userParams.WEB_GONG_WANG_AN_BEI) {
    return userStore.userParams.WEB_GONG_WANG_AN_BEI
  }
  return getGwab()
}

const ipc = () => {
  if (userStore.userParams.WEB_IPC_BEI_AN_HAO) {
    return userStore.userParams.WEB_IPC_BEI_AN_HAO
  }
  return getIpc()
}
</script>

<style scoped lang="scss">
.blossom-home-root {
  @include box(100%, 100%);
  @include flex(column, center, center);
  position: relative;
  overflow: hidden;

  .home-header {
    @include box(100%, 60px);
    position: absolute;
    top: 0;
    left: 0;
  }

  .home-main {
    @include box(100%, calc(100% - 40px));
    padding-top: 60px;
    @include flex(row, flex-start, center);

    .home-main-userinfo {
      @include box(600px, 100%);
    }

    .home-main-right {
      @include box(calc(100% - 600px), 100%);
      @include flex(column, flex-start, center);
      padding-right: 20px;

      .home-main-right-chart {
        @include box(100%, 40%);
      }

      .home-main-right-subject {
        @include box(100%, 60%);
      }
    }
  }

  .home-footer {
    @include box(100%, 40px);

    // icp
    .custom-info {
      @include flex(column, flex-end, center);
      height: 100%;
      padding-bottom: 5px;
    }

    .about-us {
      @include flex(row, center, center);
      position: absolute;
      right: 10px;
      bottom: 10px;
    }

    div {
      @include flex(row, center, center);
      color: #6d6d6d;
      font-size: 13px;
      white-space: pre;
    }
  }

  @media screen and (max-width: 1100px) {
    .home-main {
      .home-main-userinfo {
        width: 100%;
      }

      .home-main-right {
        display: none;
      }
    }
  }
}
</style>
