<template>
  <div class="login-root">
    <div class="login-form">
      <bl-col just="center" class="logo" height="auto">
        <img src="/blog-logo.png" :style="getThemeLogoStyle()" />
        <br />
        <bl-row class="title" just="center">{{ getSysName() }}</bl-row>
      </bl-col>
      <el-input class="login-input" size="default" v-model="formLogin.username">
        <template #prepend>用户名</template>
      </el-input>
      <el-input class="login-input" size="default" v-model="formLogin.password" type="password">
        <template #prepend>密　码</template>
      </el-input>
      <bl-row just="flex-end">
        <el-button size="default" text bg @click="toRoute('/home')">返　回</el-button>
        <el-button size="default" text bg @click="handleLogin">登　录</el-button>
      </bl-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { toRoute } from '@/router'
import { login } from '@/scripts/auth'
import { getSysName, getThemeLogoStyle } from '@/scripts/env'

const formLogin = ref({ username: '', password: '' })

const handleLogin = () => {
  login(formLogin.value.username, formLogin.value.password)
}
</script>

<style scoped lang="scss">
.login-root {
  @include flex(column, flex-start, center);
  @include box(100%, 100%);
  padding: 20px;

  .login-form {
    width: 300px;
    margin-top: 5vh;
    .logo {
      color: var(--el-color-primary);
      padding: 20px 0;
      cursor: pointer;
      .title {
        text-align: center;
      }

      img {
        @include box(80px, 80px);
      }
    }
    .login-input {
      padding-bottom: 20px;
    }
  }
}
</style>
