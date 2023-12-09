<template>
  <div class="index-user-root">
    <img v-if="userinfo.avatar != ''" class="avatar-img" :src="userinfo.avatar" />
    <img v-else class="avatar-img" src="@renderer/assets/imgs/default_user_avatar.jpg" />
  </div>
</template>

<script setup lang="ts">
import { storeToRefs } from 'pinia'
import { useUserStore } from '@renderer/stores/user'
const userStore = useUserStore()
const { userinfo } = storeToRefs(userStore)
</script>

<style scoped lang="scss">
.index-user-root {
  @include flex(row, center, center);
  @include box(100%, 60px);
  padding: 5px;
  cursor: pointer;
  transition: background-color 0.3s;

  &:hover {
    background-color: var(--el-color-primary-light-9);
  }

  .avatar-img {
    @include box(50px, 50px);
    @include themeShadow(2px 4px 7px rgba(49, 49, 49, 0.4), 2px 2px 5px rgb(0, 0, 0));
    @include themeBorder(2px, var(--el-color-primary), var(--el-color-primary-light-5), 'around', 5px);
    @include themeBrightness();
    object-fit: cover;
  }
}
</style>
