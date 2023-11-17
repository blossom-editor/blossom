import { defineStore } from 'pinia'
import { Local } from '@/assets/utils/storage'
// import { loginApi, logoutApi, checkApi, userinfoApi } from '@/api/auth'

export const storeKey = 'token'
export const userinfoKey = 'userinfo'

/**
 * 登录状态枚举
 */
export const enum AuthStatus {
  Wait = '请登录',
  Loging = '登录中...',
  Checking = '检查登录...',
  Succ = '已登录',
  Fail = '登录失败'
}

export interface Auth {
  token: string
  status: AuthStatus
}

/**
 * 初始化授权状态
 */
const initAuth = () => {
  let auth: Auth = { token: '', status: AuthStatus.Wait }
  Local.set(storeKey, auth)
  return auth
}

/**
 * 初始化用户信息
 */
const initUserinfo = () => {
  let userinfo = { id: '', username: '暂未登录', nickName: '暂未登录', avatar: '', remark: '', articleCount: 0, articleWords: 0 }
  Local.set(userinfoKey, userinfo)
  return userinfo
}

/**
 *
 */
export const useUserStore = defineStore('userStore', {
  state: () => ({
    auth: Local.get(storeKey) || initAuth(),
    userinfo: Local.get(userinfoKey) || initUserinfo()
  }),
  getters: {
    isLogin: (state): boolean => {
      if (!state.auth) {
        return false
      }
      return state.auth.status === AuthStatus.Succ
    }
  },
  actions: {
    //   /**
    //    * 根据用户名密码登录
    //    * @param username 用户名
    //    * @param password 密码
    //    */
    //   async loginByPassword(username: string, password: string) {
    //     this.auth.status = AuthStatus.Loging
    //     /*
    //      * 客户端ID, 见服务器配置 project.auth.clients.clientid
    //      * 登录模式, 见服务器配置 project.auth.clients.grantType
    //      */
    //     await loginApi({ username: username, password: password, clientId: 'blossom', grantType: 'password' })
    //       .then((resp: any) => {
    //         let auth = { token: resp.data.token, status: AuthStatus.Succ }
    //         this.auth = auth
    //         Local.set(storeKey, auth)
    //         this.getUserinfo()
    //       })
    //       .catch((_e) => {
    //         this.reset()
    //         // 登录失败的状态需要特别更改
    //         let auth = { token: '', status: AuthStatus.Fail }
    //         this.auth = auth
    //       })
    //   },
    //   async logout() {
    //     await logoutApi().then((_) => {
    //       this.reset()
    //     })
    //   },
    //   /**
    //    * 检查登录状态
    //    */
    //   async checkToken(succ: any, fail: any) {
    //     this.auth.status = AuthStatus.Checking
    //     await checkApi()
    //       .then((resp) => {
    //         let auth = { token: resp.data.token, status: AuthStatus.Succ }
    //         this.auth = auth
    //         Local.set(storeKey, auth)
    //         this.getUserinfo()
    //         succ()
    //       })
    //       .catch((_error) => {
    //         this.reset()
    //         // 登录失败的状态需要特别更改
    //         let auth = { token: '', status: AuthStatus.Wait }
    //         this.auth = auth
    //         fail()
    //       })
    //   },
    //   /**
    //    * 获取用户信息
    //    */
    //   getUserinfo() {
    //     userinfoApi().then((resp) => {
    //       this.userinfo = resp.data
    //       Local.set(userinfoKey, resp.data)
    //     })
    //   },
    /**
     * 重置登录状态和用户信息
     */
    reset() {
      Local.remove(storeKey)
      Local.remove(userinfoKey)
      this.auth = initAuth()
      this.userinfo = initUserinfo()
    }
  }
})
