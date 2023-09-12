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
  Loging = '登录中...',
  Checking = '检查登录...',
  Succ = '已登录',
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
      'WEB_ARTICLE_URL': '',
      'BACKUP_PATH': '',
      'BACKUP_EXP_DAYS': '',
      'ARTICLE_LOG_EXP_DAYS': '',
      'SERVER_MACHINE_EXPIRE': '',
      'SERVER_DATABASE_EXPIRE': '',
      'SERVER_HTTPS_EXPIRE': '',
      'SERVER_DOMAIN_EXPIRE': '',
    }
  }
  Local.set(userinfoKey, userinfo)
  return userinfo
}
const timeoutMs = 800
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
    async loginByPassword(username: string, password: string) {
      this.auth.status = AuthStatus.Loging
      /*
       * 客户端ID, 见服务器配置 project.auth.clients.clientid
       * 登录模式, 见服务器配置 project.auth.clients.grantType
       */
      await loginApi({ username: username, password: password, clientId: 'blossom', grantType: 'password' })
        .then((resp: any) => {
          setTimeout(() => {
            let auth = { token: resp.data.token, status: AuthStatus.Succ };
            this.auth = auth;
            Local.set(storeKey, auth);
            this.getUserinfo()
          }, timeoutMs);
        }).catch((_e) => {
          setTimeout(() => {
            this.reset()
            // 登录失败的状态需要特别更改
            let auth = { token: '', status: AuthStatus.Fail }
            this.auth = auth
          }, timeoutMs);
        })
    },
    async logout() {
      await logoutApi().then(_ => {
        this.reset();
      })
    },
    /**
     * 检查登录状态
     */
    async checkToken(succ: any, fail: any) {
      this.auth.status = AuthStatus.Checking
      await checkApi().then(resp => {
        setTimeout(() => {
          let auth = { token: resp.data.token, status: AuthStatus.Succ }
          this.auth = auth
          Local.set(storeKey, auth)
          this.getUserinfo()
          succ()
        }, timeoutMs);
      }).catch(_error => {
        setTimeout(() => {
          this.reset()
          // 登录失败的状态需要特别更改
          let auth = { token: '', status: AuthStatus.Wait }
          this.auth = auth
          fail()
        }, timeoutMs);
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