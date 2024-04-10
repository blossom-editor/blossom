import { defineStore } from 'pinia'
import { Local } from '@/assets/utils/storage'
import { userinfoOpenApi } from '@/api/blossom'

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

const DEFAULT_USER_INFO = {
  id: '',
  username: '',
  nickName: '',
  avatar: '',
  remark: '',
  articleCount: 0,
  articleWords: 0,
  userParams: {
    WEB_ARTICLE_URL: '',
    WEB_IPC_BEI_AN_HAO: '',
    WEB_LOGO_NAME: '',
    WEB_LOGO_URL: '',
    WEB_GONG_WANG_AN_BEI: '',
    WEB_BLOG_URL_ERROR_TIP_SHOW: 1,
    WEB_BLOG_LINKS: '',
    WEB_BLOG_SUBJECT_TITLE: '1',
    WEB_BLOG_SHOW_ARTICLE_NAME: '1',
    WEB_BLOG_COLOR: 'rgb(104, 104, 104)',
    WEB_BLOG_WATERMARK_ENABLED: '0',
    WEB_BLOG_WATERMARK_CONTENT: '',
    WEB_BLOG_WATERMARK_FONTSIZE: 15,
    WEB_BLOG_WATERMARK_COLOR: '',
    WEB_BLOG_WATERMARK_GAP: 100
  }
}

export type UserParams = typeof DEFAULT_USER_INFO.userParams
export type Userinfo = typeof DEFAULT_USER_INFO
/**
 * 初始化用户信息
 */
const initUserinfo = () => {
  return { ...DEFAULT_USER_INFO }
}

/**
 *
 */
export const useUserStore = defineStore('userStore', {
  state: () => ({
    auth: Local.get(storeKey) || initAuth(),
    userinfo: initUserinfo()
  }),
  getters: {
    isLogin: (state): boolean => {
      if (!state.auth) {
        return false
      }
      return state.auth.status === AuthStatus.Succ
    },
    userParams: (state): UserParams => {
      return state.userinfo.userParams
    },
    links: (state): any => {
      return state.userinfo.userParams.WEB_BLOG_LINKS
    }
  },
  actions: {
    async getUserinfoOpen() {
      await userinfoOpenApi().then((resp) => {
        this.userinfo = resp.data
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
