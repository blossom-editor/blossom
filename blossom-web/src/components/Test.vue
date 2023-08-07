<template>
  <div class="template-root">

    <img src="@/assets/imgs/home_light.jpg" class="m1">
    <img src="@/assets/imgs/home_dark.jpg" class="m2">
  </div>
</template>

<script setup lang="ts">
import { ref, shallowRef, watch, onMounted, onActivated, provide, inject, nextTick } from "vue";
import { useDark } from '@vueuse/core';


</script>

<style scoped lang="scss">
.template-root {
  @include box(100%, 100%);

  .m1,
  .m2 {
    width: 1800px;
    height: 990px;
    position: absolute;
  }

  .m1 {
    clip-path: polygon(0 0, 70% 0, 30% 100%, 0 100%);
    z-index: 2;
  }

  .m2 {
    z-index: 1;
  }
}
</style>