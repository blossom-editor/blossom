<template>
  <div class="index-root">
    <div class="index-aside">
      <indexAside></indexAside>
    </div>
    <div class="index-main-container">
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
import { ref, watch, onMounted } from "vue";
import router from "@renderer/router";
import indexAside from './index/IndexAside.vue';

const includeRouter = ref<any>(['settingIndex']);

watch(() => router.currentRoute.value, (newRoute) => {
  if (newRoute.meta.keepAlive && includeRouter.value.indexOf(newRoute.name) === -1) {
    includeRouter.value.push(newRoute.name)
  }
}, { immediate: true, deep: true })

onMounted(() => {
})
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

    .index-main {
      @include box(100%, 100%);
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

    // .index-footer {
    //   @include box(100%, 37px);
    // }
  }
}
</style>