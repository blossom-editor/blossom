<template>
  <div class="login-root">
    <bl-row style="margin-bottom: 10px;" just="center">
      <img v-if="userinfo.avatar != ''" class="avatar-img" :src="userinfo.avatar" />
      <img v-else class="avatar-img" src="@renderer/assets/imgs/default_user_avatar.jpg">
    </bl-row>
    <div style="width: 100%;text-align: center;">
      <bl-row just="center">
        <div class="input-wrapper" style="width: 500px;">
          <input type="text" class="form__input" placeholder="https://..." v-model="formLogin.serverUrl"
            @input="handleServerUrl">
          <div class="iconbl bl-a-servercloud-line"></div>
        </div>
      </bl-row>
      <bl-row just="center">
        <div class="input-wrapper" style="width:235px;margin-right:30px;">
          <input type="text" class="form__input" placeholder="username" v-model="formLogin.username"
            @input="handleServerUsername" @keyup.enter="login">
          <div class="iconbl bl-user-line"></div>
        </div>
        <div class="input-wrapper" style="width:235px;">
          <input type="password" class="form__input" placeholder="password" v-model="formLogin.password"
            @keyup.enter="login">
          <div class="iconbl bl-a-Securitypermissions-line"></div>
        </div>
      </bl-row>
      <button class="custom-btn btn-logout" style="margin-right: 30px;" @click="logout"><span>Logout</span></button>
      <button :class="['custom-btn btn-login', logingIn ? 'loging-in' : '']" @click="login"><span>Login</span></button>
      <div :class="[loginStatClass]" style="margin-top: 20px;">{{ loginResult }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { storeToRefs } from 'pinia'
import { useServerStore } from '@renderer/stores/server'
import { useUserStore, AuthStatus } from '@renderer/stores/user'

onMounted(() => {
  formLogin.value.serverUrl = serverUrl.value
  formLogin.value.username = serverUsername.value
  checkLogin()
})

// --------------------------------------------------< 授权 >--------------------------------------------------
const userStore = useUserStore()
const { auth, userinfo } = storeToRefs(userStore)

const loginResult = ref<string>(AuthStatus.Wait)
const logingIn = ref(false)

const login = () => {
  if (logingIn.value) {
    return
  }
  logingIn.value = true
  loginResult.value = '正在登录...'
  setTimeout(() => {
    userStore.loginByPassword(formLogin.value.username, formLogin.value.password)
    logingIn.value = false
  }, 500);
}

const logout = () => {
  userStore.logout();
}

const checkLogin = () => {
  loginResult.value = '正在检查登录状态...'
  setTimeout(() => {
    userStore.checkToken(
      () => {
        console.warn('已登录');
      },
      () => {
        console.warn('未登录')
      })
  }, 500);
}

const loginStatClass = computed(() => {
  if (auth.value.status === AuthStatus.Succ) {
    loginResult.value = AuthStatus.Succ
    return 'login-succ'
  }
  if (auth.value.status === AuthStatus.Fail) {
    loginResult.value = AuthStatus.Fail
    return 'login-fail'
  }
  loginResult.value = AuthStatus.Wait
  return 'login-wait'
})

const formLogin = ref({
  /**
   * 兼容末尾带 / 或不带 / 的写法, 最终会去除末尾的 /
   */
  serverUrl: '',
  username: '',
  password: ''
})

// --------------------------------------------------< 服务器地址 >--------------------------------------------------
const serverStore = useServerStore();
const { serverUrl, serverUsername } = storeToRefs(serverStore);

const handleServerUrl = () => {
  let orginServerUrl = formLogin.value.serverUrl
  // 去除末尾的 / 
  if (orginServerUrl.lastIndexOf('/') == orginServerUrl.length - 1) {
    orginServerUrl = orginServerUrl.substring(0, orginServerUrl.length - 1);
  }
  serverStore.setServerUrl(orginServerUrl)
}

const handleServerUsername = () => {
  serverStore.setServerUsername(formLogin.value.username)
}
</script>

<style scoped lang="scss">
.login-root {
  @include box(100%, 100%);
  @include flex(column, center, center);

  .avatar-img {
    height: 150px;
    @include themeShadow(2px 4px 7px 2px rgba(134, 134, 134, 0.3), 2px 4px 7px 2px #000000);
    @include themeBorder(2px, #a8abb2, #707070);
    border-radius: 10px;
    margin-bottom: 20px;
  }

  .input-wrapper {
    height: 100%;
    margin-bottom: 15px;
    position: relative;

    input {
      @include box(100%, 40px);
      @include font(16px, 200);
      color: var(--el-color-primary);
      border: none;
      border-radius: 10px;
      padding-left: 40px;
      background: none;
      transition: 0.3s;
      box-shadow: inset .2rem .2rem .5rem #c8d0e7, inset -.2rem -.2rem .5rem #FFFFFF;

      [class="dark"] & {
        box-shadow: inset .2rem .2rem .5rem #232323DD, inset -.1rem -.1rem .5rem #4E4E4E;
      }

      &::placeholder {
        @include themeColor(#9baacf, #606060);
      }

      &:focus {
        outline: none;
        box-shadow: .3rem .3rem .6rem #c8d0e7, -.2rem -.2rem .5rem #fff;

        [class="dark"] & {
          box-shadow: .3rem .3rem .6rem #000000, -.1rem -.1rem .5rem #848484;
        }

        ~.iconbl {
          color: var(--el-color-primary);
        }
      }
    }

    .iconbl {
      @include themeColor(#9baacf, rgb(126, 126, 126));
      position: absolute;
      left: 10px;
      top: 8px;
      font-size: 25px;
      display: flex;
      transition: .3s ease;
    }

  }

  .custom-btn {
    @include box(120px, 40px);
    @include font(14px 300);
    color: #fff;
    border-radius: 5px;
    padding: 10px 25px;
    background: transparent;
    transition: all 0.3s ease;
    position: relative;
    display: inline-block;
    outline: none;
    cursor: pointer;
    z-index: 1;
  }

  .btn-logout {
    @include themeColor(#9baacf, #7e7e7e);
    border: none;

    box-shadow: inset 2px 2px 2px 0px #ffffff80,
      7px 7px 20px 0px rgba(0, 0, 0, .1),
      4px 4px 5px 0px rgba(0, 0, 0, .1);

    [class="dark"] & {
      box-shadow: inset 2px 2px 2px 0px #59595980,
        7px 7px 20px 0px rgba(0, 0, 0, .1),
        4px 4px 5px 0px rgba(0, 0, 0, .1);
    }

    &:after {
      @include box(0, 100%);
      @include absolute(0, '', '', 0);
      content: "";
      direction: rtl;
      z-index: -1;
      transition: all 0.3s ease;
      border-radius: 7px;
      box-shadow: -7px -7px 20px 0px #fff9, -4px -4px 5px 0px #fff9, 7px 7px 20px 0px #0002, 4px 4px 5px 0px #0001;

      [class="dark"] & {
        box-shadow: -7px -7px 20px 0px rgba(0, 0, 0, 0.2), -4px -4px 5px 0px rgba(0, 0, 0, 0.1), 7px 7px 20px 0px #0002, 4px 4px 5px 0px #0001;
      }
    }

    &:hover {
      color: var(--el-color-primary);
    }

    &:hover:after {
      left: auto;
      right: 0;
      width: 100%;
    }

    &:active {
      top: 2px;
    }
  }

  .btn-login {
    background: radial-gradient(circle, #f796c0 0%, #76aef1 100%);
    line-height: 42px;
    padding: 0;
    border: none;
    box-shadow: inset 2px 2px 2px 0px rgba(255, 255, 255, .5),
      7px 7px 20px 0px rgba(0, 0, 0, .1),
      4px 4px 5px 0px rgba(0, 0, 0, .1);
    transition: background 2s;

    [class="dark"] & {
      background: radial-gradient(circle, #BA8A10 0%, #6F7C0D 100%);
    }

    &:before,
    &:after {
      content: "";
      @include absolute('', 0, 0, '');
      background: var(--el-color-primary);
      transition: all 0.3s ease;
    }

    &:before {
      @include box(2px, 0%);
    }

    &:after {
      @include box(0%, 2px);
    }

    &:hover {
      background: var(--el-color-primary-light-6);
      border-radius: 0;

      [class="dark"] & {
        box-shadow: none;
      }

      &:before {
        height: 100%;
      }

      &:after {
        width: 100%;
      }
    }

    span {
      @include box(100%, 100%);
      position: relative;
      display: block;

      &:hover {
        color: var(--el-color-primary);
      }

      &:before {
        @include box(2px, 0%);
      }

      &:hover:before {
        height: 100%;
      }

      &:after {
        @include box(0%, 2px);
      }

      &:hover:after {
        width: 100%;
      }

      &:before,
      &:after {
        content: "";
        @include absolute(0, '', '', 0);
        background: var(--el-color-primary);
        transition: all 0.3s ease;
      }
    }
  }

  .loging-in {
    cursor: no-drop !important;
  }


  .login-succ {
    color: #09A113;
  }

  .login-wait {
    color: #A18809
  }

  .login-fail {
    color: #A11109
  }
}
</style>