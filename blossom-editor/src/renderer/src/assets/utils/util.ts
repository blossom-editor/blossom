import { isNull } from "./obj"

/**
 * 跳转新开页面
 * @param url 地址
 */
export const toView = (url: string): void => {
  window.open(url)
}

/**
 * 获取当前时间的 yyyyMMdd_HHmmss
 * @returns 
 */
export const getNowTime = (): string => {
  const now = new Date()
  let y = now.getFullYear()
  let m = formatNum(now.getMonth() + 1)
  let d = formatNum(now.getDate())
  let h = formatNum(now.getHours())
  let min = formatNum(now.getMinutes())
  let s = formatNum(now.getSeconds())
  return `${y}${m}${d}_${h}${min}${s}`
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
  return '' + y + '-' + m + '-' + d + ' ' + h + ':' + min + ':' + s;
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
  date = date.match(/\d+/g)!.join('-'); // 格式为2022年09月19日处理
  const nextDay = new Date(date);
  nextDay.setDate(nextDay.getDate() + next);

  const formatObj = {
    y: nextDay.getFullYear(),
    m: nextDay.getMonth() + 1,
    d: nextDay.getDate(),
  };
  return format.replace(/{([ymd])+}/g, (_result, key) => {
    const value = formatObj[key];
    return value.toString().padStart(2, '0');
  });
}

export const getDateZh = (): string => {
  const now = new Date()
  let y = now.getFullYear()
  let m = formatNum(now.getMonth() + 1)
  let d = formatNum(now.getDate())
  return `${y}年${m}月${d}日`
}


/**
 * 获取当前处于何时
 */
export const nowWhen = (): string => {
  let nowTime: number = new Date().getHours();
  if (0 <= nowTime && nowTime < 7) {
    return 'Dawning'
  }
  if (7 <= nowTime && nowTime < 12) {
    return 'Morning'
  }
  if (12 <= nowTime && nowTime < 19) {
    return 'Afternoon'
  }
  if (19 <= nowTime && nowTime < 24) {
    return 'Evening'
  }
  return 'Night'
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
  return '' + y + '-' + m + '-' + d + ' ' + h + ':' + min + ':' + s;
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
  return '' + y + '-' + m + '-' + d + ' ' + h + ':' + min + ':' + s + '.' + SSS;
}

const formatNum = (num: number) => {
  if (num < 10) {
    return '0' + num
  }
  return num
};

/**
 * 格式化JSON字符串
 * @param msg
 * @param customRetract 缩进
 * @returns {string}
 */
export const formatJson = (msg: string, customRetract?: string): string => {
  // 格式化缩进为2个空格
  const retract = isNull(customRetract) ? '  ' : customRetract;
  let rep = '~';
  let jsonStr = JSON.stringify(msg, null, rep);
  let str = '';
  for (let i = 0; i < jsonStr.length; i++) {
    let text2 = jsonStr.charAt(i);
    if (i > 1) {
      let text = jsonStr.charAt(i - 1);
      if (rep !== text && rep === text2) {
        // str += '\n'
      }
    }
    str += text2
  }
  jsonStr = '';
  for (let i = 0; i < str.length; i++) {
    let text = str.charAt(i);
    if (rep === text) {
      jsonStr += retract;
    } else {
      jsonStr += text
    }
    if (i === str.length - 2) {
      jsonStr += '\n'
    }
  }
  return jsonStr;
};

/**
 * 格式化文件大小
 * @param size
 * @returns 
 */
export const formatFileSize = (size: number): string => {
  if (!size)
    return "";

  var num = 1024.00; //byte

  if (size < num)
    return size + "B";
  if (size < Math.pow(num, 2))
    return (size / num).toFixed(2) + "KB"; //kb
  if (size < Math.pow(num, 3))
    return (size / Math.pow(num, 2)).toFixed(2) + "MB"; //M
  if (size < Math.pow(num, 4))
    return (size / Math.pow(num, 3)).toFixed(2) + "G"; //G
  return (size / Math.pow(num, 4)).toFixed(2) + "T"; //T
}

/**
 * 将数字转为千分位的字符串
 * @param param 
 * @returns 
 */
export const formartNumber = (param: number): string => {
  let num: string = (param || 0).toString(), result: string = '';
  while (num.length > 3) {
    result = ',' + num.slice(-3) + result;
    num = num.slice(0, num.length - 3);
  }
  if (num) { result = num + result; }
  return result;
}

export const randomBoolean = (): boolean => {
  return Math.random() >= 0.5
}
/**
 * html 反转义
 * @param str 
 * @returns 
 */
export const escape2Html = (str: string): string => {
  // 1.首先动态创建一个容器标签元素
  let temp = document.createElement("div");
  // 2.然后将要转换的字符串设置为这个元素的innerHTML(ie，火狐，google都支持)
  temp.innerHTML = str;
  // 3.最后返回这个元素的innerText或者textContent，即得到经过HTML解码的字符串了。
  let output = temp.innerText || temp.textContent;
  // console.log('output', output);
  if (output == null) {
    return ''
  }
  return output;
}