<template>
  <div class="index-root">
    <div class="main">
      <router-view v-slot="{ Component }">
        <keep-alive>
          <component :is="Component" />
        </keep-alive>
      </router-view>
    </div>
  </div>
</template>
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