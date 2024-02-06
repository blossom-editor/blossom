import { storeToRefs } from 'pinia'
import { AuthStatus, storeKey, userinfoKey, useUserStore } from '@/stores/user'
import { checkApi, loginApi, userinfoApi } from '@/api/auth'
import { Local } from '@/assets/utils/storage'
import { toRoute } from '@/router'

const userStore = useUserStore()
const { auth } = storeToRefs(userStore)

/**
 * 登录
 * @param username
 * @param password
 */
export const login = async (username: string, password: string) => {
  auth.value = { token: '', status: AuthStatus.Loging }
  await loginApi({ username: username, password: password, clientId: 'blossom', grantType: 'password' })
    .then((resp: any) => {
      auth.value = { token: resp.data.token, status: AuthStatus.Succ }
      Local.set(storeKey, auth.value)
      toRoute('/home')
    })
    .catch((_e) => {
      userStore.reset()
      // 登录失败的状态需要特别更改
      auth.value = { token: '', status: AuthStatus.Fail }
    })
}

/**
 * 退出登录
 */
export const logout = () => {
  auth.value = { token: '', status: AuthStatus.Wait }
  Local.set(storeKey, { token: '', status: AuthStatus.Wait })
}

/**
 * 检查 token
 */
export const checkToken = () => {
  checkApi()
    .then((resp) => {
      auth.value = { token: resp.data.token, status: AuthStatus.Succ }
      Local.set(storeKey, auth.value)
    })
    .catch((_error) => {
      userStore.reset()
    })
}
