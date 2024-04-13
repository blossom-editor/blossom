<template>
  <div class="index-root">
    <div class="index-aside" :class="[viewStyle.isShowAsideSimple ? 'simple' : '']">
      <indexAside></indexAside>
    </div>
    <div class="index-main-container" :class="[viewStyle.isShowAsideSimple ? 'simple' : '']">
      <div class="app-header">
        <AppHeader></AppHeader>
      </div>
      <div class="index-main">
        <router-view v-slot="{ Component }">
          <keep-alive :include="[includeRouter]">
            <component :is="Component" />
          </keep-alive>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import router from '@renderer/router'
import { isElectron } from '@renderer/assets/utils/util'
import indexAside from './index/IndexAside.vue'
import AppHeader from '@renderer/components/AppHeader.vue'
import { useConfigStore } from '@renderer/stores/config'

const { viewStyle } = useConfigStore()

onMounted(() => {
  console.log(`blossom => 是否 Electron 容器：${isElectron()}`)
  console.log(`blossom => 当前运行环境：${import.meta.env.MODE}`)
})

const includeRouter = ref<any>(['settingIndex'])

watch(
  () => router.currentRoute.value,
  (newRoute) => {
    document.title = newRoute.meta.title as string
    if (newRoute.meta.keepAlive && includeRouter.value.indexOf(newRoute.name) === -1) {
      includeRouter.value.push(newRoute.name)
    }
  }
)
</script>

<style scoped lang="scss">
$zindex-aside: 2001;
$zindex-header: 2001;

.index-root {
  @include box(100%, 100%);
  @include flex(row, flex-start, center);
  position: relative;

  .index-aside {
    @include box(62px, 100%);
    z-index: $zindex-aside;
  }

  .index-main-container {
    @include box(calc(100% - 62px), 100%);
    position: relative;

    .app-header {
      @include box(100%, 30px);
    }

    .index-main {
      @include box(100%, calc(100% - 30px));
    }

    .index-header {
      @include box(180px, 35px);
      @include flex(row, center, center);
      @include themeShadow(0 0 7px 2px rgba(49, 49, 49, 0.3), 0 0 7px 2px rgb(0, 0, 0));
      background-color: var(--bl-html-color);
      position: absolute;
      bottom: 0;
      left: 20px;
      z-index: $zindex-header;
      border-top-left-radius: 10px;
      border-top-right-radius: 10px;
    }
  }

  .index-aside.simple {
    @include box(42px, 100%);
  }

  .index-main-container.simple {
    @include box(calc(100% - 43px), 100%);
  }
}
</style>
