import { ref } from 'vue'
import { zhCn } from './zh-cn'
import { en } from './en'
import { Temp } from './types'

const locale = ref('zhCn')

export const useI18n = () => {
  const getValue = (key: string): string => {
    let k: any = key.split('.').reduce((o, i) => {
      if (o) {
        return o[i]
      }
    }, getLocale())
    return k as string
  }
  const getLocale = (): Temp => {
    let l = locale.value
    if (l === 'zh-cn') {
      return zhCn
    }
    if (l === 'en') {
      return en
    }
    return zhCn
  }

  return {
    locale,
    getValue,
    getLocale
  }
}
