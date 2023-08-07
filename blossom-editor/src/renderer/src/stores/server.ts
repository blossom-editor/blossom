import { defineStore } from 'pinia';
import { Local } from '@renderer/assets/utils/storage';

export const storeKey = 'serverUrl';
export const usernameKey = 'username';


const initServerUrl = () => {
  const defaultUrl = 'http://127.0.0.1:9999'
  Local.set(storeKey, defaultUrl)
  return defaultUrl
}

export const useServerStore = defineStore('serverStore', {
  state: () => ({
    serverUrl: Local.get(storeKey) || initServerUrl(),
    serverUsername: Local.get(usernameKey) || '',
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
});