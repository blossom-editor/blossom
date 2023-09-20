<template>
  <div class="logo-root">
    <img v-if="isDark" class="logo-img" src="@renderer/assets/imgs/blossom_logo_dark.png" />
    <img v-else class="logo-img" src="@renderer/assets/imgs/blossom_logo.png" />
    <div v-if="props.showName" class="project-name">{{ BLOSSOM.SYS.NAME }}</div>
  </div>
</template>

<script setup lang="ts">
import BLOSSOM from '@renderer/assets/constants/system'
import { useDark } from '@vueuse/core'

const isDark = useDark();

const props = defineProps({
  showName: {
    type: Boolean,
    default: true
  }
})
</script>

<style scoped lang="scss">
.logo-root {
  @include flex(column, flex-start, flex-start);
  @include box(100%, 80px);

  .logo-img {
    @include box(60px, 60px);
    @include themeFilter(drop-shadow(2px 2px 3px #a7a7a7), drop-shadow(0 0 3px #000000));
    padding: 5px;
  }

  .project-name {
    @include font(12px, 700);
    @include themeText(3px 3px 5px #979797, 3px 3px 5px #000000);
    width: 100%;
    color: var(--el-color-primary);
    text-align: center;
    letter-spacing: 0;
  }

  .project-version {
    @include font(12px, 700);
    width: 100%;
    text-align: right;
    color: var(--el-color-primary);
    transform: scale(0.9);
  }

  .logo-and-version {
    @include flex(column, space-around, flex-end);
    height: 100%;

  }
}
</style>