import { useDark } from '@vueuse/core'
import { Local } from '@renderer/assets/utils/storage'
import Notify from './notify'

const THEME_LIGHT_KEY = 'theme_light'
const THEME_DARK_KEY = 'theme_dark'

const DEFAULT_LIGHT = {
  '--el-color-primary': '#AD8CF2',
  '--el-color-primary-dark': 'rgba(173, 140, 242, 0.8)',
  '--el-color-primary-dark-2': 'rgba(173, 140, 242, 0.8)',
  '--el-color-primary-light-1': 'rgba(173, 140, 242, 0.9)',
  '--el-color-primary-light-2': 'rgba(173, 140, 242, 0.8)',
  '--el-color-primary-light-3': 'rgba(173, 140, 242, 0.7)',
  '--el-color-primary-light-4': 'rgba(173, 140, 242, 0.6)',
  '--el-color-primary-light-5': 'rgba(173, 140, 242, 0.5)',
  '--el-color-primary-light-6': 'rgba(173, 140, 242, 0.4)',
  '--el-color-primary-light-7': 'rgba(173, 140, 242, 0.3)',
  '--el-color-primary-light-8': 'rgba(173, 140, 242, 0.2)',
  '--el-color-primary-light-9': 'rgba(173, 140, 242, 0.1)',
  '--bl-tag-color-open': '#7AC20C',
  '--bl-tag-color-subject': '#FA8072',
  '--bl-tag-color-toc': '#7274FA',
  '--bl-todo-wait-color': '#d22d3b6b',
  '--bl-todo-proc-color': '#fba85f6b',
  '--bl-todo-comp-color': '#9974fe6b'
}

const DEFAULT_DARK = {
  '--el-color-primary': ' #899911',
  '--el-color-primary-dark': 'rgb(136, 154, 2)',
  '--el-color-primary-dark-2': 'rgba(165, 184, 20, 0.8)',
  '--el-color-primary-light-1': 'rgba(165, 184, 20, 0.9)',
  '--el-color-primary-light-2': 'rgba(165, 184, 20, 0.8)',
  '--el-color-primary-light-3': 'rgba(165, 184, 20, 0.7)',
  '--el-color-primary-light-4': 'rgba(165, 184, 20, 0.6)',
  '--el-color-primary-light-5': 'rgba(165, 184, 20, 0.5)',
  '--el-color-primary-light-6': 'rgba(165, 184, 20, 0.4)',
  '--el-color-primary-light-7': 'rgba(165, 184, 20, 0.3)',
  '--el-color-primary-light-8': 'rgba(165, 184, 20, 0.2)',
  '--el-color-primary-light-9': 'rgba(165, 184, 20, 0.1)',
  '--bl-tag-color-open': '#7AC20C',
  '--bl-tag-color-subject': '#FA8072',
  '--bl-tag-color-toc': '#7274FA',
  '--bl-todo-wait-color': '#d22d3b6b',
  '--bl-todo-proc-color': '#fba85f6b',
  '--bl-todo-comp-color': '#9974fe6b'
}

const isDark = useDark()

/**
 * 将主题封装为对象, 主要用于 echart 图标
 */
const primaryColor = {
  color: '',
  color1: '',
  color2: '',
  color3: '',
  color4: '',
  color5: '',
  color6: '',
  color7: '',
  color8: '',
  color9: ''
}

const body = document.getElementsByTagName('body')[0]

/**
 * 从 localStorage 获取全部样式
 * @param themeDark
 */
const getTheme = (themeDark: boolean) => {
  if (themeDark) {
    return { ...DEFAULT_DARK, ...Local.get(THEME_DARK_KEY) }
  }
  return { ...DEFAULT_LIGHT, ...Local.get(THEME_LIGHT_KEY) }
}
/**
 * 合并样式, 并保存到 locaLstorage
 * @param style
 * @param themeDark
 */
const setTheme = (style: any, themeDark: boolean) => {
  if (themeDark) {
    let theme = Local.get(THEME_DARK_KEY)
    Local.set(THEME_DARK_KEY, { ...theme, ...style })
  } else {
    let theme = Local.get(THEME_LIGHT_KEY)
    console.log({ ...theme, ...style })
    Local.set(THEME_LIGHT_KEY, { ...theme, ...style })
  }
}

/**
 * 根据主题模式变更主题色
 * @param themeDark 主题模式 true:夜间模式 false:日间模式
 */
const changeTheme = async (themeDark: boolean) => {
  let text = ''
  let colors = getTheme(themeDark)
  for (const key in colors) {
    text += `${key}:${colors[key]};`
  }
  // 1. 相关的所有样式颜色设置到 body 下
  body.style.cssText = text

  // 2. 设置到变量中, 用于无法通过 css var 设置的样式中, 例如 echart
  primaryColor.color = colors['--el-color-primary']
  primaryColor.color1 = colors['--el-color-primary-light-1']
  primaryColor.color2 = colors['--el-color-primary-light-2']
  primaryColor.color3 = colors['--el-color-primary-light-3']
  primaryColor.color4 = colors['--el-color-primary-light-4']
  primaryColor.color5 = colors['--el-color-primary-light-5']
  primaryColor.color6 = colors['--el-color-primary-light-6']
  primaryColor.color7 = colors['--el-color-primary-light-7']
  primaryColor.color8 = colors['--el-color-primary-light-8']
  primaryColor.color9 = colors['--el-color-primary-light-9']
}

/**
 * 初始化主题色
 */
const initTheme = () => {
  console.log('Blossom => 初始化主题')
  changeTheme(isDark.value)
}
initTheme()

/**
 * 获取主题色
 */
const getPrimaryColor = () => {
  return primaryColor
}

/**
 * 设置主题色
 * @param rgb 主题颜色: rgb
 * @param themeDark 主题模式 true:夜间模式 false:日间模式
 */
const setPrimaryColor = (rgb: string, themeDark: boolean) => {
  if (!rgb.toLowerCase().startsWith('rgb(')) {
    Notify.error('请使用 RGB 格式颜色', '错误颜色')
    return
  }
  const prefix = rgb.substring(4, rgb.length - 1)
  const colors = {
    '--el-color-primary': rgb,
    '--el-color-primary-dark': rgb,
    '--el-color-primary-dark-2': `rgba(${prefix}, 0.8)`,
    '--el-color-primary-light-1': `rgba(${prefix}, 0.9)`,
    '--el-color-primary-light-2': `rgba(${prefix}, 0.8)`,
    '--el-color-primary-light-3': `rgba(${prefix}, 0.7)`,
    '--el-color-primary-light-4': `rgba(${prefix}, 0.6)`,
    '--el-color-primary-light-5': `rgba(${prefix}, 0.5)`,
    '--el-color-primary-light-6': `rgba(${prefix}, 0.4)`,
    '--el-color-primary-light-7': `rgba(${prefix}, 0.3)`,
    '--el-color-primary-light-8': `rgba(${prefix}, 0.2)`,
    '--el-color-primary-light-9': `rgba(${prefix}, 0.1)`
  }
  setTheme(colors, themeDark)
  changeTheme(isDark.value)
}

/**
 * 设置主题中某个的值, 用于单个设置
 * @param name
 * @param value
 * @param themeDark
 */
const setStyleItem = (name: string, value: string, themeDark: boolean) => {
  let item = {}
  item[name.toString()] = value
  setTheme(item, themeDark)
  if (themeDark === isDark.value) {
    body.style.setProperty(name, value)
  }
}

export { isDark, getTheme, changeTheme, getPrimaryColor, setPrimaryColor, setStyleItem }
