<template>
  <ul class="versions">
    <li class="electron-version">Electron v{{ versions.electron }}</li>
    <li class="chrome-version">Chromium v{{ versions.chrome }}</li>
    <li class="node-version">Node v{{ versions.node }}</li>
    <li class="v8-version">V8 v{{ versions.v8 }}</li>
  </ul>
</template>
<script setup lang="ts">
import { reactive } from 'vue'

// version
const versions = reactive({ ...window.electron.process.versions })


</script>

<style scoped lang=scss>
.versions {
  color: red;

  .electron-version {
    color: green;
  }
}
</style>
