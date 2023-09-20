<template>
  <div class="try-use-root">
    <div class="info-title">
      <Logo :show-name="false" style="width: 70px;padding-top:5px;"></Logo>如何试用 Blossom
    </div>

    <div class="info-form">
      <div class="try-use-content">
        <p>如果你想要在自己部署 Blossom 前先进行试用, 你可以在登录页面中填入如下的信息</p>
        <div>
          <img src="@renderer/assets/imgs/docs/tryuse.png">
        </div>
        <p>然后使用账号，密码直接登录使用: blos / blos。<span style="color: var(--el-color-primary);">您也可以点击下方按钮直接登录。</span></p>
        <el-button class="login-btn" type="primary" size="large" @click="helpMeLogin">直接登录</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import Logo from '@renderer/components/Logo.vue'

const helpMeLogin = () => {
  emits('helpMeLogin')
}

const emits = defineEmits(['helpMeLogin'])
</script>

<style scoped lang="scss">
@import '@renderer/assets/styles/bl-dialog-info.scss';

.try-use-root {
  @include box(100%, 100%);

  .try-use-content {
    @include box(100%, 100%);
    @include flex(column, flex-start, center);
    padding: 10px 20px;

    img {
      width: 500px;
      border: 1px solid var(--el-border-color);
      border-radius: 10px;
    }

    .login-btn {
      @include font(20px, 700);
    }
  }
}
</style>