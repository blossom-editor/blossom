import { StyleHTMLAttributes } from 'vue'
import { useDark } from '@vueuse/core'
import { Local } from '@renderer/assets/utils/storage'
import Notify from './notify'

const THEME_LIGHT_KEY = 'theme_light'
const THEME_DARK_KEY = 'theme_dark'
const THEME_STYLE_TAG_ID = 'blossom-theme-css'

const DEFAULT_LIGHT = {
  '--el-color-primary': 'rgb(119, 150, 73)',
  '--el-color-primary-dark': 'rgba(119, 150, 73, 0.8)',
  '--el-color-primary-dark-2': 'rgba(119, 150, 73, 0.8)',
  '--el-color-primary-light-1': 'rgba(119, 150, 73, 0.9)',
  '--el-color-primary-light-2': 'rgba(119, 150, 73, 0.8)',
  '--el-color-primary-light-3': 'rgba(119, 150, 73, 0.7)',
  '--el-color-primary-light-4': 'rgba(119, 150, 73, 0.6)',
  '--el-color-primary-light-5': 'rgba(119, 150, 73, 0.5)',
  '--el-color-primary-light-6': 'rgba(119, 150, 73, 0.4)',
  '--el-color-primary-light-7': 'rgba(119, 150, 73, 0.3)',
  '--el-color-primary-light-8': 'rgba(119, 150, 73, 0.2)',
  '--el-color-primary-light-9': 'rgba(119, 150, 73, 0.1)',
  '--bl-tag-color-open': '#bdc3c7',
  '--bl-tag-color-subject': '#7f8c8d',
  '--bl-tag-color-toc': '#c5c5c5',
  '--bl-todo-wait-color': '#C7D0D6',
  '--bl-todo-proc-color': '#ecd452',
  '--bl-todo-comp-color': '#c0d695',
  '--bl-text-shadow': '2px 3px 4px rgba(107, 104, 104, 0.5)',
  '--bl-text-shadow-light': '2px 3px 4px rgba(107, 104, 104, 0.4)',
  '--bl-box-shadow-subject': '0 3px 5px 0 #cacaca',
  '--bl-drop-shadow-star': 'drop-shadow(2px 2px 2px rgb(180, 180, 180))'
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
  '--bl-tag-color-open': '#B3ADA0',
  '--bl-tag-color-subject': '#B3ADA0',
  '--bl-tag-color-toc': '#8E8C8E',
  '--bl-todo-wait-color': '#505050A5',
  '--bl-todo-proc-color': '#fba85f6b',
  '--bl-todo-comp-color': '#476716',
  '--bl-text-shadow': '2px 3px 5px rgb(0, 0, 0)',
  '--bl-text-shadow-light': '2px 3px 4px rgba(39, 39, 39, 0.5)',
  '--bl-box-shadow-subject': '0 3px 3px #000000',
  '--bl-drop-shadow-star': ' drop-shadow(2px 2px 2px rgb(0, 0, 0))'
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

/**
 * 从 localStorage 获取全部样式
 *
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
 *
 * @param style
 * @param themeDark
 */
const setTheme = (style: any, themeDark: boolean) => {
  if (themeDark) {
    let theme = Local.get(THEME_DARK_KEY)
    Local.set(THEME_DARK_KEY, { ...theme, ...style })
  } else {
    let theme = Local.get(THEME_LIGHT_KEY)
    Local.set(THEME_LIGHT_KEY, { ...theme, ...style })
  }
}

/**
 * 根据主题模式变更主题色
 *
 * @param themeDark 主题模式 true:夜间模式 false:日间模式
 */
const changeTheme = async (themeDark: boolean) => {
  let text = ''
  let colors = getTheme(themeDark)
  for (const key in colors) {
    text += `${key}:${colors[key]};`
  }

  if (themeDark) {
    text = `:root.dark {${text}}`
  } else {
    text = `:root {${text}}`
  }

  // 1. 新建 style 标签, 设置到 head 默认覆盖全局样式
  let themeStyleTag: HTMLElement | StyleHTMLAttributes | null = document.getElementById('blossom-theme-css')
  if (themeStyleTag) {
    themeStyleTag.innerHTML = text
  } else {
    themeStyleTag = document.createElement('style') as unknown as StyleHTMLAttributes
    themeStyleTag.type = 'text/css'
    themeStyleTag.id = THEME_STYLE_TAG_ID
    themeStyleTag.innerHTML = text
    document
      .getElementsByTagName('head')
      .item(0)!
      .appendChild(themeStyleTag as Node)
  }

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
 *
 * @param rgb       主题颜色: rgb
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
 * 设置主题中的单个值
 *
 * @param name      样式的名称, 例如 --el-color-primary
 * @param value     样式值
 * @param themeDark 主题模式 true:夜间模式 false:日间模式
 */
const setStyleItem = (name: string, value: string, themeDark: boolean) => {
  let item = {}
  item[name.toString()] = value
  setTheme(item, themeDark)
  if (themeDark === isDark.value) {
    changeTheme(themeDark)
  }
}

/**
 * 设置主题中的多个值
 *
 * @param obj       样式对象
 * @param themeDark 主题模式 true:夜间模式 false:日间模式
 */
const setStyleItemObj = (styleObj: any, themeDark: boolean) => {
  setTheme(styleObj, themeDark)
  if (themeDark === isDark.value) {
    changeTheme(themeDark)
  }
}

/**
 * 重置主题中的多个值
 *
 * @param names     一个或多个
 * @param themeDark 主题模式 true:夜间模式 false:日间模式
 */
const resetStyleItems = (names: string[], themeDark: boolean) => {
  if (names.length === 0) {
    return
  }
  const DEFAULT_THEME = themeDark ? DEFAULT_DARK : DEFAULT_LIGHT
  let item = {}
  names.forEach((name) => {
    item[name] = DEFAULT_THEME[name]
  })
  setTheme(item, themeDark)
  if (themeDark === isDark.value) {
    changeTheme(themeDark)
  }
}

export { isDark, getTheme, changeTheme, getPrimaryColor, setPrimaryColor, setStyleItem, setStyleItemObj, resetStyleItems }
