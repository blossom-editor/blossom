<template>
  <div class="template-root"></div>
</template>

<script setup lang="ts">
import { ref, shallowRef, watch, onMounted, onActivated, provide, inject, nextTick } from "vue";
import { useDark } from '@vueuse/core';


</script>

<style scoped lang="scss">
.template-root {
  @include box(100%, 100%);
}
</style>