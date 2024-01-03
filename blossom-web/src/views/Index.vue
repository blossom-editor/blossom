<template>
  <div class="index-root" :style="{ backgroundImage: isHome ? 'linear-gradient(to bottom right, #3e464e, #212121)' : '' }">
    <IndexHeader :bg="!isHome"></IndexHeader>
    <div class="main">
      <router-view v-slot="{ Component }">
        <keep-alive :include="[includeRouter]">
          <component :is="Component" />
        </keep-alive>
      </router-view>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import router from '@/router'
import IndexHeader from './index/IndexHeader.vue'
import type { RouteRecordName } from 'vue-router'

const includeRouter = ref<any>(['Home'])
const curRoute = ref<RouteRecordName>('Home')
const isHome = computed(() => {
  return curRoute.value === 'Home'
})

watch(
  () => router.currentRoute.value,
  (newRoute) => {
    if (newRoute.name) {
      curRoute.value = newRoute.name
    }
    if (newRoute.meta.keepAlive && includeRouter.value.indexOf(newRoute.name) === -1) {
      includeRouter.value.push(newRoute.name)
    }
  },
  { immediate: true }
)
</script>
<style scoped lang="scss">
.index-root {
  @include box(100%, 100%);
  position: relative;

  .main {
    @include box(100%, calc(100% - 60px));
    padding-top: 10px;
    overflow: hidden;
  }
}
</style>
