import { defineStore } from 'pinia'
import { Local } from '@renderer/assets/utils/storage'
import { loginApi, logoutApi, checkApi, userinfoApi } from '@renderer/api/auth'
import { setUserinfo } from '@renderer/assets/utils/electron'

export const storeKey = 'token'
export const userinfoKey = 'userinfo'

/* ======================================================================
import { storeToRefs } from 'pinia'
import { useUserStore } from '@renderer/stores/user'

const userStore = useUserStore()
const { userinfo } = storeToRefs(userStore)
 * ====================================================================== */

/**
 * 登录状态枚举
 */
export const enum AuthStatus {
  Wait = '请登录',
  Succ = '登录成功',
  Fail = '登录失败'
}

const initAuth = () => {
  let auth = {
    token: '',
    status: AuthStatus.Wait
  }
  Local.set(storeKey, auth)
  return auth
}

const initUserinfo = () => {
  let userinfo = {
    id: '',
    username: '暂未登录',
    nickName: '暂未登录',
    avatar: '',
    remark: '',
    articleCount: 0,
    articleWords: 0,
    osRes: {
      osType: "",
      bucketName: "",
      domain: "",
      defaultPath: ""
    },
    params: {
      'WEB_ARTICLE_URL': ''
    }
  }
  Local.set(userinfoKey, userinfo)
  return userinfo
}

export const useUserStore = defineStore('userStore', {
  state: () => ({
    auth: Local.get(storeKey) || initAuth(),
    userinfo: Local.get(userinfoKey) || initUserinfo()
  }),
  actions: {
    /**
     * 根据用户名密码登录
     * @param username 用户名
     * @param password 密码
     */
    loginByPassword(username: string, password: string) {
      /*
       * 客户端ID, 见服务器配置 project.auth.clients.clientid
       * 登录模式, 见服务器配置 project.auth.clients.grantType
       */
      loginApi({ username: username, password: password, clientId: 'blossom', grantType: 'password' })
        .then((resp: any) => {
          let auth = { token: resp.data.token, status: AuthStatus.Succ };
          this.auth = auth;
          Local.set(storeKey, auth);
          this.getUserinfo()
        }).catch((_e) => {
          this.reset()
          // 登录失败的状态需要特别更改
          let auth = { token: '', status: AuthStatus.Fail }
          this.auth = auth
        })
    },
    logout() {
      logoutApi().then(_ => {
        this.reset();
      })
    },
    /**
     * 检查登录状态
     */
    checkToken(succ: any, fail: any) {
      checkApi().then(resp => {
        let auth = { token: resp.data.token, status: AuthStatus.Succ }
        this.auth = auth
        Local.set(storeKey, auth)
        this.getUserinfo()
        succ()
      }).catch(_error => {
        this.reset()
        // 登录失败的状态需要特别更改
        let auth = { token: '', status: AuthStatus.Wait }
        this.auth = auth
        fail()
      })
    },
    /**
     * 获取用户信息
     */
    getUserinfo() {
      userinfoApi().then(resp => {
        this.userinfo = resp.data
        Local.set(userinfoKey, resp.data)
        setUserinfo(resp.data)
      })
    },
    /**
     * 重置登录状态和用户信息
     */
    reset() {
      Local.remove(storeKey);
      Local.remove(userinfoKey);
      this.auth = initAuth()
      this.userinfo = initUserinfo()
    }
  }
});