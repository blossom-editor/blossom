<template>
  <div class="login-root">
    <div class="login-form">
      <bl-col just="center" class="logo" height="auto">
        <img src="@/assets/imgs/blossom/blossom_logo.png" />
        <br />
        <bl-row class="title" just="center">Blossom</bl-row>
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
// import { storeToRefs } from 'pinia'
// import { AuthStatus, storeKey, userinfoKey, useUserStore } from '@/stores/user'
// import { loginApi, userinfoApi } from '@/api/auth'
// import { Local } from '@/assets/utils/storage'
import { toRoute } from '@/router'
import { login } from '@/scripts/auth'

// const userStore = useUserStore()
// const { auth, userinfo } = storeToRefs(userStore)
const formLogin = ref({
  username: '',
  password: ''
})

const logingIn = ref(false)

const handleLogin = () => {
  login(formLogin.value.username, formLogin.value.password)
}

// const login = async () => {
//   if (logingIn.value) {
//     return
//   }
//   logingIn.value = true
//   auth.value.status = AuthStatus.Loging
//   await loginApi({ username: formLogin.value.username, password: formLogin.value.password, clientId: 'blossom', grantType: 'password' })
//     .then((resp: any) => {
//       auth.value = { token: resp.data.token, status: AuthStatus.Succ }
//       Local.set(storeKey, auth)
//       getUserinfo()
//       toRoute('/home')
//     })
//     .catch((_e) => {
//       userStore.reset()
//       // 登录失败的状态需要特别更改
//       auth.value = { token: '', status: AuthStatus.Fail }
//     })
//     .finally(() => (logingIn.value = false))
// }

// const getUserinfo = () => {
//   userinfoApi().then((resp) => {
//     userinfo.value = resp.data
//     Local.set(userinfoKey, resp.data)
//   })
// }
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
