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

const DEFAULT_USER_INFO = {
  id: '',
  username: '暂未登录',
  nickName: '暂未登录',
  avatar: '',
  remark: '',
  articleCount: 0,
  articleWords: 0,
  osRes: {
    osType: '',
    bucketName: '',
    domain: '',
    defaultPath: ''
  },
  params: {
    /**
     * @deprecated 该字段已不使用, 博客地址改用 userParams.WEB_ARTICLE_URL, 使用该地址会报错
     */
    WEB_ARTICLE_URL: '',
    BACKUP_PATH: '',
    BACKUP_EXP_DAYS: '',
    ARTICLE_LOG_EXP_DAYS: '',
    ARTICLE_RECYCLE_EXP_DAYS: '',
    HEFENG_KEY: '',
    BLOSSOM_OBJECT_STORAGE_DOMAIN: '',
    SERVER_MACHINE_EXPIRE: '',
    SERVER_DATABASE_EXPIRE: '',
    SERVER_HTTPS_EXPIRE: '',
    SERVER_DOMAIN_EXPIRE: ''
  },
  userParams: {
    WEB_ARTICLE_URL: '',
    WEB_IPC_BEI_AN_HAO: '',
    WEB_LOGO_NAME: '',
    WEB_LOGO_URL: '',
    WEB_GONG_WANG_AN_BEI: '',
    WEB_BLOG_URL_ERROR_TIP_SHOW: ''
  }
}

export type Userinfo = typeof DEFAULT_USER_INFO

/**
 * 初始化用户默认值
 */
const initUserinfo = (): Userinfo => {
  // let userinfo = {
  //   id: '',
  //   username: '暂未登录',
  //   nickName: '暂未登录',
  //   avatar: '',
  //   remark: '',
  //   articleCount: 0,
  //   articleWords: 0,
  //   osRes: {
  //     osType: '',
  //     bucketName: '',
  //     domain: '',
  //     defaultPath: ''
  //   },
  //   params: {
  //     /**
  //      * @deprecated 该字段已不使用, 博客地址改用 userParams.WEB_ARTICLE_URL, 使用该地址会报错
  //      */
  //     WEB_ARTICLE_URL: '',
  //     BACKUP_PATH: '',
  //     BACKUP_EXP_DAYS: '',
  //     ARTICLE_LOG_EXP_DAYS: '',
  //     ARTICLE_RECYCLE_EXP_DAYS: '',
  //     HEFENG_KEY: '',
  //     BLOSSOM_OBJECT_STORAGE_DOMAIN: '',
  //     SERVER_MACHINE_EXPIRE: '',
  //     SERVER_DATABASE_EXPIRE: '',
  //     SERVER_HTTPS_EXPIRE: '',
  //     SERVER_DOMAIN_EXPIRE: ''
  //   },
  //   userParams: {
  //     WEB_ARTICLE_URL: '',
  //     WEB_IPC_BEI_AN_HAO: '',
  //     WEB_LOGO_NAME: '',
  //     WEB_LOGO_URL: '',
  //     WEB_GONG_WANG_AN_BEI: '',
  //     WEB_BLOG_URL_ERROR_TIP_SHOW: ''
  //   }
  // }
  Local.set(userinfoKey, { ...DEFAULT_USER_INFO })
  return { ...DEFAULT_USER_INFO }
}
export const useUserStore = defineStore('userStore', {
  state: () => ({
    auth: Local.get(storeKey) || initAuth(),
    /** @type { Userinfo } */
    userinfo: (Local.get(userinfoKey) as Userinfo) || initUserinfo()
  }),
  getters: {
    /** 获取系统个人配置信息 */
    sysParams(state) {
      return state.userinfo.params
    },
    /** 获取用户个人配置信息 */
    userParams(state) {
      return state.userinfo.userParams
    }
  },
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
          let auth = { token: resp.data.token, status: AuthStatus.Succ }
          this.auth = auth
          Local.set(storeKey, auth)
          this.getUserinfo()
        })
        .catch((_e) => {
          this.reset()
          // 登录失败的状态需要特别更改
          let auth = { token: '', status: AuthStatus.Fail }
          this.auth = auth
        })
    },
    async logout() {
      await logoutApi().then((_) => {
        this.reset()
      })
    },
    /**
     * 检查登录状态
     */
    async checkToken(succ: any, fail: any) {
      this.auth.status = AuthStatus.Checking
      await checkApi()
        .then((resp) => {
          let auth = { token: resp.data.token, status: AuthStatus.Succ }
          this.auth = auth
          Local.set(storeKey, auth)
          this.getUserinfo()
          succ()
        })
        .catch((_error) => {
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
      userinfoApi().then((resp) => {
        this.userinfo = resp.data
        Local.set(userinfoKey, resp.data)
        setUserinfo(resp.data)
      })
    },
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
