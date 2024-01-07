export interface IColorObj {
  r: number
  g: number
  b: number
  a?: number
}

/**
 * 255颜色值转16进制颜色值
 * @param n 255颜色值
 * @returns hex 16进制颜色值
 */
export const toHex = (n: number) => `${n > 15 ? '' : 0}${n.toString(16)}`

/**
 * 颜色对象转化为16进制颜色字符串
 * @param colorObj 颜色对象
 */
export const toHexString = (colorObj: IColorObj) => {
  const { r, g, b, a = 1 } = colorObj
  return `#${toHex(r)}${toHex(g)}${toHex(b)}${a === 1 ? '' : toHex(Math.floor(a * 255))}`
}

/**
 * 颜色对象转化为rgb颜色字符串
 * @param colorObj 颜色对象
 */
export const toRgbString = (colorObj: IColorObj) => {
  const { r, g, b } = colorObj
  return `rgb(${r},${g},${b})`
}

/**
 * 颜色对象转化为rgba颜色字符串
 * @param colorObj 颜色对象
 */
export const toRgbaString = (colorObj: IColorObj, n = 10000) => {
  const { r, g, b, a = 1 } = colorObj
  return `rgba(${r},${g},${b},${Math.floor(a * n) / n})`
}

/**
 * 16进制颜色字符串解析为颜色对象
 * @param color 颜色字符串
 * @returns IColorObj
 */
export const parseHexColor = (color: string) => {
  let hex = color.slice(1)
  let a = 1
  if (hex.length === 3) {
    hex = `${hex[0]}${hex[0]}${hex[1]}${hex[1]}${hex[2]}${hex[2]}`
  }
  if (hex.length === 8) {
    a = parseInt(hex.slice(6), 16) / 255
    hex = hex.slice(0, 6)
  }
  const bigint = parseInt(hex, 16)
  return {
    r: (bigint >> 16) & 255,
    g: (bigint >> 8) & 255,
    b: bigint & 255,
    a
  } as IColorObj
}

/**
 * rgba颜色字符串解析为颜色对象
 * @param color 颜色字符串
 * @returns IColorObj
 */
export const parseRgbaColor = (color: string) => {
  const arr = color.match(/(\d(\.\d+)?)+/g) || []
  const res = arr.map((s: string) => parseInt(s, 10))
  return {
    r: res[0],
    g: res[1],
    b: res[2],
    a: parseFloat(arr[3]) || 1
  } as IColorObj
}

/**
 * 颜色字符串解析为颜色对象
 * @param color 颜色字符串
 * @returns IColorObj
 */
export const parseColorString = (color: string) => {
  if (color.startsWith('#')) {
    return parseHexColor(color)
  } else if (color.startsWith('rgb')) {
    return parseRgbaColor(color)
  } else if (color === 'transparent') {
    return parseHexColor('#00000000')
  }
  throw new Error(`color string error: ${color}`)
}

/**
 * 颜色字符串解析为各种颜色表达方式
 * @param color 颜色字符串
 * @returns IColorObj
 */
export const getColorInfo = (color: string) => {
  const colorObj = parseColorString(color)
  const hex = toHexString(colorObj)
  const rgba = toRgbaString(colorObj)
  const rgb = toRgbString(colorObj)
  return {
    hex,
    rgba,
    rgb,
    rgbaObj: colorObj
  }
}

/**
 * 16进制颜色字符串转化为rgba颜色字符串
 * @param hex 16进制颜色字符串
 * @returns rgba颜色字符串
 */
export const hexToRgba = (hex: string) => {
  const colorObj = parseColorString(hex)
  return toRgbaString(colorObj)
}

/**
 * rgb颜色字符串转化为16进制颜色字符串
 * @param rgb rgb颜色字符串
 * @returns 16进制颜色字符串
 */
export const rgbToHex = (rgb: string) => {
  const colorObj = parseColorString(rgb)
  return toHexString(colorObj)
}

/**
 * rgba颜色字符串转化为16进制颜色字符串
 * @param rgba rgba颜色字符串
 * @returns 16进制颜色字符串
 */
export const rgbaToHex = (rgba: string) => {
  const colorObj = parseColorString(rgba)
  return toHexString(colorObj)
}
