import { ref } from 'vue'
import { Local } from '@/assets/utils/storage'

const FONT_SIZE_KEY = 'BLOSSOM-ARTICLE-FONT-SZIE'

const fontSize = ref<number>(Local.get(FONT_SIZE_KEY) || 0.9)

const increase = () => {
  fontSize.value += 0.1
  Local.set(FONT_SIZE_KEY, fontSize.value)
}

const decrease = () => {
  fontSize.value -= 0.1
  Local.set(FONT_SIZE_KEY, fontSize.value)
}

const getFontSizeValue = () => {
  return fontSize.value
}

const getFontSize = () => {
  return fontSize.value + 'rem'
}
export { increase, decrease, getFontSizeValue, getFontSize }
