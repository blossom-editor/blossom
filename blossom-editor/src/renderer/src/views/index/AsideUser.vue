<template>
  <div class="index-user-root">
    <img
      v-if="userStore.userinfo.avatar != ''"
      :class="['avatar-img', viewStyle.isShowAsideSimple ? 'simple' : '']"
      :src="userStore.userinfo.avatar" />
    <img v-else :class="['avatar-img', viewStyle.isShowAsideSimple ? 'simple' : '']" src="@renderer/assets/imgs/default_user_avatar.jpg" />
  </div>
</template>

<script setup lang="ts">
import { useUserStore } from '@renderer/stores/user'
import { useConfigStore } from '@renderer/stores/config'

const userStore = useUserStore()
const { viewStyle } = useConfigStore()
</script>

<style scoped lang="scss">
.index-user-root {
  @include flex(row, center, center);
  @include box(100%, 60px);
  padding: 5px;
  cursor: pointer;

  .avatar-img {
    @include box(50px, 50px);
    @include themeShadow(2px 4px 7px rgba(49, 49, 49, 0.4), 2px 2px 5px rgb(0, 0, 0));
    @include themeBorder(2px, var(--el-color-primary), var(--el-color-primary-light-5), 'around', 5px);
    @include themeBrightness();
    object-fit: cover;
  }

  .avatar-img.simple {
    @include box(30px, 30px);
  }
}
</style>
