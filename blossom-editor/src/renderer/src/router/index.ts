import { createRouter, createWebHashHistory } from 'vue-router'

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes: []
})

export const toRoute = (path: string) => {
  router.push(path)
}

export const toLogin = () => {
  router.push('/settingIndex')
}

/**
 * 前往设置页面, 指定激活的 tab
 * @param tabName
 */
export const toSetting = (tabName: 'setting' | 'about') => {
  router.push('/settingIndex?activeTab=' + tabName)
}

export default router
