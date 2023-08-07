<template>
  <div class="app">
    <div class="app-header">
      <AppHeader></AppHeader>
    </div>
    <div class="app-main">
      <RouterView />
    </div>
  </div>
</template>

<script setup lang="ts">
import AppHeader from './components/AppHeader.vue';
</script>


<style lang="scss">
.app {
  @include box(100%, 100%);
  overflow: hidden;
  box-shadow: 3px 3px 10px 3px rgba(107, 104, 104, 0.3);

  .app-header {
    @include box(100%, 30px);
  }

  .app-main {
    @include box(100%, calc(100% - 30px))
  }
}
</style>
