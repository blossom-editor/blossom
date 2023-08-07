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

export default router