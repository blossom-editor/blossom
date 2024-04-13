<template>
  <div class="login-root">
    <bl-row style="margin-bottom: 10px" just="center">
      <img
        v-if="userinfo.avatar == ''"
        class="avatar-img"
        src="data:image/jpeg;base64,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" />
      <img v-if="userinfo.avatar != ''" class="avatar-img" :src="userinfo.avatar" />
    </bl-row>
    <div style="width: 100%; text-align: center">
      <bl-row just="center">
        <div class="input-wrapper" style="width: 500px">
          <input type="text" class="form__input" placeholder="https://..." v-model="formLogin.serverUrl" @input="handleServerUrl" />
          <div class="iconbl bl-a-servercloud-line"></div>
          <div v-if="serverUrlIsInValid" class="server-url-invalid">
            <el-tooltip effect="light" placement="top">
              <template #content>
                登录地址可能存在错误<br />地址中不应包含以下内容：
                <li>/#/</li>
              </template>
              <svg style="height: 20px; width: 20px" aria-hidden="true">
                <use xlink:href="#wl-jinggao"></use>
              </svg>
            </el-tooltip>
          </div>
        </div>
      </bl-row>
      <bl-row just="center">
        <div class="input-wrapper" style="width: 235px; margin-right: 30px">
          <input
            type="text"
            class="form__input"
            placeholder="username"
            v-model="formLogin.username"
            @input="handleServerUsername"
            @keyup.enter="login" />
          <div class="iconbl bl-user-line"></div>
        </div>
        <div class="input-wrapper" style="width: 235px">
          <input type="password" class="form__input" placeholder="password" v-model="formLogin.password" @keyup.enter="login" />
          <div class="iconbl bl-a-Securitypermissions-line"></div>
        </div>
      </bl-row>
      <button class="custom-btn btn-logout" style="margin-right: 30px" @click="logout"><span>Logout</span></button>
      <button :class="['custom-btn btn-login', logingIn ? 'loging-in' : '']" @click="login"><span>Login</span></button>
      <bl-col height="80px" just="center">
        <div class="login-progress">
          <el-progress
            :text-inside="true"
            :stroke-width="20"
            :percentage="loginDesc.percentage"
            striped
            striped-flow
            :duration="30"
            :color="loginDesc.color">
            <div :style="{ color: loginDesc.textColor, fontWeight: 700, textShadow: loginDesc.textShadow }">
              {{ loginDesc.status }}
            </div>
          </el-progress>
        </div>
      </bl-col>
      <bl-row just="center" v-if="configStore.viewStyle.isShowTryuseBtn">
        <span class="try-use" @click="showTryUse">我想试用!</span>
      </bl-row>
    </div>
  </div>

  <el-dialog
    draggable
    v-model="isShowTryUse"
    :align-center="true"
    :append-to-body="true"
    :destroy-on-close="true"
    :close-on-click-modal="true"
    width="550">
    <TryUse ref="TryUseRef" @help-me-login="helpMeLogin"></TryUse>
  </el-dialog>
</template>

<script setup lang="ts">
import router from '@renderer/router'
import { computed, nextTick, onMounted, ref } from 'vue'
import { storeToRefs } from 'pinia'
import { useDark } from '@vueuse/core'
import { useServerStore } from '@renderer/stores/server'
import { useConfigStore } from '@renderer/stores/config'
import { useUserStore, AuthStatus } from '@renderer/stores/user'
import { isBlank } from '@renderer/assets/utils/obj'
import SYSTEM from '@renderer/assets/constants/system'
import TryUse from './setting/TryUse.vue'

onMounted(() => {
  formLogin.value.serverUrl = serverUrl.value
  formLogin.value.username = serverUsername.value
  checkLogin()
})

const isDark = useDark()
const configStore = useConfigStore()
const userStore = useUserStore()
const { auth, userinfo } = storeToRefs(userStore)

//#region --------------------------------------------------< 授权 >--------------------------------------------------
// 是否登录中
const logingIn = ref(false)

/**
 * 登录
 */
const login = async () => {
  // 未填写地址时, 不校验用户是否登录
  if (isBlank(formLogin.value.serverUrl)) {
    return
  }
  if (logingIn.value) {
    return
  }
  logingIn.value = true
  await userStore.loginByPassword(formLogin.value.username, formLogin.value.password)
  if (userStore.isLogin && configStore.viewStyle.isLoginToHomePage) {
    router.push('/home')
  }
  logingIn.value = false
}

/**
 * 退出登录
 */
const logout = async () => {
  // 未填写地址时, 不校验用户是否登录
  if (isBlank(formLogin.value.serverUrl)) {
    return
  }
  await userStore.logout()
}

/**
 * 检查用户的登录状态
 */
const checkLogin = async () => {
  // 未填写地址时, 不校验用户是否登录
  if (isBlank(formLogin.value.serverUrl)) {
    return
  }
  await userStore.checkToken(
    () => {
      console.log('%c已登录', 'background-color:#09A113;color:#fff;padding:10px 40px;border-radius:5px;font-size:17px;')
    },
    () => {
      console.log('%c未登录', 'background-color:#C9BB1F;color:#fff;padding:10px 40px;border-radius:5px;font-size:17px;')
    }
  )
}

interface LoginStyle {
  status: string
  textColor: string
  textShadow: string
  color?: string
  percentage?: number
}

const loginDesc = computed(() => {
  let loginStyle: LoginStyle = {
    status: auth.value.status,
    textColor: isDark.value ? '#2F2F2F' : '#FFFFFF',
    textShadow: isDark.value ? '0 0 5px #3D3D3D' : '0 0 5px #000000'
  }
  if (auth.value.status === AuthStatus.Loging) {
    loginStyle.color = '#EFC75E'
    loginStyle.percentage = 50
  } else if (auth.value.status === AuthStatus.Checking) {
    loginStyle.color = '#EFC75E'
    loginStyle.percentage = 50
  } else if (auth.value.status === AuthStatus.Succ) {
    loginStyle.color = '#2BB532'
    loginStyle.percentage = 100
  } else if (auth.value.status === AuthStatus.Fail) {
    loginStyle.color = '#E8564F'
    loginStyle.percentage = 100
  } else if (auth.value.status === AuthStatus.Wait) {
    loginStyle.color = '#EFC75E'
    loginStyle.percentage = 1
    loginStyle.textColor = isDark.value ? '#7E7E7E' : '#A8ABB2'
    loginStyle.textShadow = isDark.value ? '0 0 5px #000000' : '0 0 5px #CBCBCB'
  }
  return loginStyle
})

const formLogin = ref({
  /** 兼容末尾带 / 或不带 / 的写法, 最终会去除末尾的 / */
  serverUrl: '',
  username: '',
  password: ''
})

//#endregion

//#region --------------------------------------------------< 服务器地址 >--------------------------------------------------
const serverStore = useServerStore()
const { serverUrl, serverUsername } = storeToRefs(serverStore)

const handleServerUrl = () => {
  let orginServerUrl = formLogin.value.serverUrl
  // 去除末尾的 /
  if (orginServerUrl.lastIndexOf('/') == orginServerUrl.length - 1) {
    orginServerUrl = orginServerUrl.substring(0, orginServerUrl.length - 1)
  }
  serverStore.setServerUrl(orginServerUrl)
}

const handleServerUsername = () => {
  serverStore.setServerUsername(formLogin.value.username)
}

/**
 * 登录地址可能不合法
 */
const serverUrlIsInValid = computed(() => {
  let url = formLogin.value.serverUrl.toLocaleLowerCase()
  if (url.includes('/#/')) {
    return true
  }
  return false
})

//#endregion

//#region --------------------------------------------------< 试用 >--------------------------------------------------
const isShowTryUse = ref(false)

const showTryUse = () => {
  isShowTryUse.value = true
}

const helpMeLogin = () => {
  formLogin.value = SYSTEM.TRY_USE
  handleServerUrl()
  isShowTryUse.value = false
  nextTick(() => {
    login()
  })
}
//#endregion
</script>

<style scoped lang="scss">
.login-root {
  @include box(100%, 100%);
  @include flex(column, center, center);
  padding: 40px;

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
      box-shadow:
        inset 0.2rem 0.2rem 0.5rem #c8d0e7,
        inset -0.2rem -0.2rem 0.5rem #ffffff;

      [class='dark'] & {
        box-shadow:
          inset 0.2rem 0.2rem 0.5rem #232323dd,
          inset -0.1rem -0.1rem 0.5rem #4e4e4e;
      }

      &::placeholder {
        @include themeColor(#9baacf, #606060);
      }

      &:focus {
        outline: none;
        box-shadow:
          0.3rem 0.3rem 0.6rem #c8d0e7,
          -0.2rem -0.2rem 0.5rem #fff;

        [class='dark'] & {
          box-shadow:
            0.3rem 0.3rem 0.6rem #000000,
            -0.1rem -0.1rem 0.5rem #4b4b4b;
        }

        ~ .iconbl {
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
      transition: 0.3s ease;
    }

    .server-url-invalid {
      @include themeFilter(drop-shadow(0 0 3px rgb(197, 197, 197)), drop-shadow(0 0 3px #000000));
      position: absolute;
      bottom: 5px;
      right: 10px;
      cursor: pointer;
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

    box-shadow:
      inset 2px 2px 2px 0px #ffffff80,
      7px 7px 20px 0px rgba(0, 0, 0, 0.1),
      4px 4px 5px 0px rgba(0, 0, 0, 0.1);

    [class='dark'] & {
      box-shadow:
        inset 2px 2px 2px 0px #59595980,
        7px 7px 20px 0px rgba(0, 0, 0, 0.1),
        4px 4px 5px 0px rgba(0, 0, 0, 0.1);
    }

    &:after {
      @include box(0, 100%);
      @include absolute(0, '', '', 0);
      content: '';
      direction: rtl;
      z-index: -1;
      transition: all 0.3s ease;
      border-radius: 7px;
      box-shadow:
        -7px -7px 20px 0px #fff9,
        -4px -4px 5px 0px #fff9,
        7px 7px 20px 0px #0002,
        4px 4px 5px 0px #0001;

      [class='dark'] & {
        box-shadow:
          -7px -7px 20px 0px rgba(0, 0, 0, 0.2),
          -4px -4px 5px 0px rgba(0, 0, 0, 0.1),
          7px 7px 20px 0px #0002,
          4px 4px 5px 0px #0001;
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
    background: radial-gradient(circle, #ba8a10b1 0%, var(--el-color-primary) 70%, var(--el-color-primary) 100%);
    line-height: 42px;
    padding: 0;
    border: none;
    box-shadow:
      inset 2px 2px 2px 0px rgba(255, 255, 255, 0.5),
      7px 7px 20px 0px rgba(0, 0, 0, 0.1),
      4px 4px 5px 0px rgba(0, 0, 0, 0.1);
    transition: background 2s;

    &:before,
    &:after {
      content: '';
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

      [class='dark'] & {
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
        content: '';
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
    color: #09a113;
  }

  .login-wait {
    color: #a18809;
  }

  .login-fail {
    color: #a11109;
  }

  .login-progress {
    width: 200px;

    :deep(.el-progress-bar__outer) {
      border-radius: 5px;
    }

    :deep(.el-progress-bar__inner) {
      @include themeShadow(inset 0 0 5px 0px rgb(226, 226, 226), inset 0 0 5px 2px #000);
      text-align: center;
      border-radius: 5px;
    }
  }

  .try-use {
    margin-top: 10px;
    color: var(--bl-text-color-light);
    font-weight: 300;
    font-style: italic;
    border-radius: 5px;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      color: var(--el-color-primary);
    }
  }
}
</style>
