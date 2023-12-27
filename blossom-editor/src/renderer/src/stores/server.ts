import { defineStore } from 'pinia'
import { Local } from '@renderer/assets/utils/storage'
import SYSTEM from '@renderer/assets/constants/system'

const isDemo = import.meta.env.MODE === 'tryuse'

export const storeKey = 'serverUrl'
export const usernameKey = 'username'

const initServerUrl = (): string => {
  const defaultUrl = isDemo ? SYSTEM.TRY_USE.serverUrl : ''
  Local.set(storeKey, defaultUrl)
  return defaultUrl
}

export const useServerStore = defineStore('serverStore', {
  state: () => ({
    serverUrl: (Local.get(storeKey) as string) || initServerUrl(),
    serverUsername: Local.get(usernameKey) || ''
  }),
  actions: {
    async setServerUrl(url: string) {
      this.serverUrl = url
      Local.set(storeKey, url)
    },
    setServerUsername(username: string) {
      this.serverUsername = username
      Local.set(usernameKey, username)
    }
  }
})
