<template>
  <div class="doc-workbench-root">
    <bl-row just="flex-end" align="flex-end"> </bl-row>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'

// ---------- 新增修改按钮 ----------
const refreshDocTree = () => {
  emits('refreshDocTree')
}
//#endregion
</script>

<style scoped lang="scss">
@import '../doc/tree-workbench.scss';
</style>
