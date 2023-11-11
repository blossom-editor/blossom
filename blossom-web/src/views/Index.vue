<template>
  <div class="index-root">
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
import { ref, watch, onMounted } from 'vue'
import router from '@/router'
import { checkToken } from '@/scripts/auth'

const includeRouter = ref<any>(['home'])

onMounted(() => {
  checkToken()
})

watch(
  () => router.currentRoute.value,
  (newRoute) => {
    if (newRoute.meta.keepAlive && includeRouter.value.indexOf(newRoute.name) === -1) {
      includeRouter.value.push(newRoute.name)
    }
  }
)

onMounted(() => {})
</script>
<style scoped lang="scss">
.index-root {
  @include box(100%, 100%);
  position: relative;

  .header {
    @include box(100%, 40px);
    height: 40px;
  }

  .main {
    @include box(100%, 100%);
  }
}
</style>
