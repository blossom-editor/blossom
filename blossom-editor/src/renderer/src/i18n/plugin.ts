import { App } from 'vue'
import { useI18n } from './useI18n'

const i18n = useI18n()

export default {
  install: (app: App) => {
    app.config.globalProperties.$t = (key: string) => {
      return i18n.getValue(key)
    }
  }
}
