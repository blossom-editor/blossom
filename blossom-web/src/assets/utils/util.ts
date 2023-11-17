import { isNull } from './obj'

/**
 * 跳转新开页面
 * @param url 地址
 */
export const toView = (url: string): void => {
  window.open(url)
}

/**
 * 获取当前时间的 yyyy-MM-dd HH:mm:ss
 * @returns {string}
 */
export const getNowTimeFormat = (): string => {
  const now = new Date()
  let y = now.getFullYear()
  let m = formatNum(now.getMonth() + 1)
  let d = formatNum(now.getDate())
  let h = formatNum(now.getHours())
  let min = formatNum(now.getMinutes())
  let s = formatNum(now.getSeconds())
  return '' + y + '-' + m + '-' + d + ' ' + h + ':' + min + ':' + s
}

/**
 * 将 秒级时间戳 转为 yyyy-MM-dd HH:mm:ss
 * @param seconds 秒级时间戳
 */
export const secondsToDatetime = (seconds: number | string | Date): string => {
  const now = new Date(Number(seconds) * 1000)
  let y = now.getFullYear()
  let m = formatNum(now.getMonth() + 1)
  let d = formatNum(now.getDate())
  let h = formatNum(now.getHours())
  let min = formatNum(now.getMinutes())
  let s = formatNum(now.getSeconds())
  return '' + y + '-' + m + '-' + d + ' ' + h + ':' + min + ':' + s
}

/**
 * 将 毫秒时间戳 转为 yyyy-MM-dd HH:mm:ss.sss
 * @param timestamp 毫秒时间戳
 */
export const timestampToDatetime = (timestamp: number | string | Date): string => {
  const now = new Date(timestamp)
  let y = now.getFullYear()
  let m = formatNum(now.getMonth() + 1)
  let d = formatNum(now.getDate())
  let h = formatNum(now.getHours())
  let min = formatNum(now.getMinutes())
  let s = formatNum(now.getSeconds())
  let SSS = formatNum(now.getMilliseconds())
  return '' + y + '-' + m + '-' + d + ' ' + h + ':' + min + ':' + s + '.' + SSS
}

/**
 * 获取当前时间的 yyyy-MM-dd HH:mm:ss
 * @returns {string}
 */
export const getDateTimeFormat = (): string => {
  const now = new Date()
  let y = now.getFullYear()
  let m = formatNum(now.getMonth() + 1)
  let d = formatNum(now.getDate())
  let h = formatNum(now.getHours())
  let min = formatNum(now.getMinutes())
  let s = formatNum(now.getSeconds())
  return '' + y + '-' + m + '-' + d + ' ' + h + ':' + min + ':' + s
}

/**
 * 获取下一天
 * @param date
 * @param next
 * @param format
 * @returns
 */
export const getNextDay = (date: string, next: number = 1, format = '{y}-{m}-{d}'): string => {
  if (!date) {
    return '日期错误'
  }
  date = date.match(/\d+/g)!.join('-') // 格式为2022年09月19日处理
  const nextDay = new Date(date)
  nextDay.setDate(nextDay.getDate() + next)

  const formatObj: any = {
    y: nextDay.getFullYear(),
    m: nextDay.getMonth() + 1,
    d: nextDay.getDate()
  }
  return format.replace(/{([ymd])+}/g, (_result, key) => {
    const value = formatObj[key]
    return value.toString().padStart(2, '0')
  })
}

const formatNum = (num: number) => {
  if (num < 10) {
    return '0' + num
  }
  return num
}

/**
 * 格式化JSON字符串
 * @param msg
 * @param customRetract 缩进
 * @returns {string}
 */
export const formatJson = (msg: string, customRetract?: string): string => {
  // 格式化缩进为2个空格
  const retract = isNull(customRetract) ? '  ' : customRetract
  let rep = '~'
  let jsonStr = JSON.stringify(msg, null, rep)
  let str = ''
  for (let i = 0; i < jsonStr.length; i++) {
    let text2 = jsonStr.charAt(i)
    if (i > 1) {
      let text = jsonStr.charAt(i - 1)
      if (rep !== text && rep === text2) {
        // str += '\n'
      }
    }
    str += text2
  }
  jsonStr = ''
  for (let i = 0; i < str.length; i++) {
    let text = str.charAt(i)
    if (rep === text) {
      jsonStr += retract
    } else {
      jsonStr += text
    }
    if (i === str.length - 2) {
      jsonStr += '\n'
    }
  }
  return jsonStr
}
