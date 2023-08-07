<template>
  <div class="logo-root">
    <img v-if="isDark" class="logo-img" src="@renderer/assets/imgs/blossom_logo_dark.png" />
    <img v-else class="logo-img" src="@renderer/assets/imgs/blossom_logo.png" />
    <div class="project-name">{{ BLOSSOM.SYS.NAME }}</div>
  </div>
</template>

<script setup lang="ts">
import BLOSSOM from '@renderer/assets/constants/system'
import { useDark } from '@vueuse/core'

const isDark = useDark();
</script>

<style scoped lang="scss">
.logo-root {
  @include flex(column, flex-start, flex-start);
  @include box(100%, 80px);

  .logo-img {
    @include box(60px, 60px);
    padding: 5px;
  }

  .project-name {
    @include font(12px, 700);
    @include themeText(3px 3px 5px rgb(212, 212, 212), 3px 3px 5px rgb(0, 0, 0));
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