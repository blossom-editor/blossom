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
  //1.首先动态创建一个容器标签元素，如DIV
  let temp = document.createElement("div");
  //2.然后将要转换的字符串设置为这个元素的innerHTML(ie，火狐，google都支持)
  temp.innerHTML = str;
  //3.最后返回这个元素的innerText或者textContent，即得到经过HTML解码的字符串了。
  let output = temp.innerText || temp.textContent;
  // console.log('output', output);
  if (output == null) {
    return ''
  }
  return output;
}

export const getSolar = (): string => {
  let today = new Date();
  let solar = getSolarTermByYearMonthDay(today.getMonth() + 1, today.getDate());
  // console.log('当前日期[%s-%s]的最后一次节气: %s', today.getMonth() + 1, today.getDate(), solar);
  return solar;
}

export const getSolarTermByYearMonthDay = (month: number, day: any): string => {
  // return '冬至';
  if (month == 1) {
    if (day >= 5) return "小寒";
    if (day >= 19) return "大寒"
  }
  if (month == 2) {
    if (day < 3) return "大寒";
    if (day >= 3) return "立春";
    if (day >= 18) return "雨水"
  }
  if (month == 3) {
    if (day < 5) return "雨水";
    if (day >= 5) return "惊蛰";
    if (day >= 20) return "春分"
  }
  if (month == 4) {
    if (day < 4) return "春分";
    if (day >= 4) return "清明";
    if (day >= 19) return "谷雨"
  }
  if (month == 5) {
    if (day < 5) return "谷雨";
    if (day >= 5) return "立夏";
    if (day >= 20) return "小满"
  }
  if (month == 6) {
    if (day < 5) return "小满";
    if (day >= 5) return "芒种";
    if (day >= 21) return "夏至"
  }
  if (month == 7) {
    if (day < 7) return "夏至";
    if (day >= 7) return "小暑";
    if (day >= 22) return "大暑"
  }
  if (month == 8) {
    if (day < 6) return "大暑";
    if (day >= 6) return "立秋";
    if (day >= 22) return "处暑"
  }
  if (month == 9) {
    if (day < 7) return "处暑";
    if (day >= 7) return "白露";
    if (day >= 22) return "秋分"
  }
  if (month == 10) {
    if (day < 7) return "秋分";
    if (day >= 7) return "寒露";
    if (day >= 23) return "霜降"
  }
  if (month == 11) {
    if (day < 7) return "霜降";
    if (day >= 7) return "立冬";
    if (day >= 22) return "小雪"
  }
  if (month == 12) {
    if (day < 5) return "小雪";
    if (day >= 5) return "大雪";
    if (day >= 21) return "冬至"
  }
  return '';
}

export interface SolarColor {
  firstColor: string;
  firstColorFlip: string;
  secondColor: string;
  secondColorFlip: string,
}

export const getSolorColor = (): SolarColor => {
  const currentSolar = getSolar();

  if (currentSolar == '小寒') {
    return { firstColor: '#283233', secondColor: '#973131', firstColorFlip: '#42494a', secondColorFlip: '#a65050' }
  }
  if (currentSolar == '大寒') {
    return { firstColor: '#c6c0ba', secondColor: '#576975', firstColorFlip: '#b5b0aa', secondColorFlip: '#4c5c66' }
  }
  if (currentSolar == '立春') {
    return { firstColor: '#f6da8c', secondColor: '#c4beb9', firstColorFlip: '#e6cb83', secondColorFlip: '#ada8a4' }
  }
  if (currentSolar == '雨水') {
    return { firstColor: '#ccaf9b', secondColor: '#463936', firstColorFlip: '#b59b8a', secondColorFlip: '#a68e7e' }
  }
  if (currentSolar == '惊蛰') {
    return { firstColor: '#c9c1ba', secondColor: '#508e69', firstColorFlip: '#a9c2b3', secondColorFlip: '#c9c1ba' }
  }
  if (currentSolar == '春分') {
    return { firstColor: '#c3bfba', secondColor: '#f5d791', firstColorFlip: '#d9c793', secondColorFlip: '#d6d2cc' }
  }
  if (currentSolar == '清明') {
    return { firstColor: '#ded2c3', secondColor: '#b7bc4b', firstColorFlip: '#c1c2a1', secondColorFlip: '#f4e7d7' }
  }
  if (currentSolar == '谷雨') {
    return { firstColor: '#d3e0ea', secondColor: '#529e43', firstColorFlip: '#b6d4b0', secondColorFlip: '#d3e0ea' }
  }
  if (currentSolar == '立夏') {
    return { firstColor: '#efd58e', secondColor: '#6cc1ff', firstColorFlip: '#f7e7ba', secondColorFlip: '#83cbff' }
  }
  if (currentSolar == '小满') {
    return { firstColor: '#d19c46', secondColor: '#53915a', firstColorFlip: '#edaf54', secondColorFlip: '#50a356' }
  }
  if (currentSolar == '芒种') {
    return { firstColor: '#87c0f9', secondColor: '#b08738', firstColorFlip: '#7294ad', secondColorFlip: '#edaf54' }
  }
  if (currentSolar == '夏至') {
    return { firstColor: '#dfeaf2', secondColor: '#8ed2b1', firstColorFlip: '#a2cfe5', secondColorFlip: '#b8d3da' }
  }
  if (currentSolar == '小暑') {
    return { firstColor: '#60d7a0', secondColor: '#83bd6f', firstColorFlip: '#83bd6f', secondColorFlip: '#65ab63' }
  }
  if (currentSolar == '大暑') {
    return { firstColor: '#05aae4', secondColor: '#9de7ed', firstColorFlip: '#3c9ee8', secondColorFlip: '#96e0e2' }
  }
  if (currentSolar == '立秋') {
    return { firstColor: '#dbbc30', secondColor: '#a08373', firstColorFlip: '#fcd42b', secondColorFlip: '#885629' }
  }
  if (currentSolar == '处暑') {
    return { firstColor: '#b6e2e3', secondColor: '#8ed2af', firstColorFlip: '#8dd0d1', secondColorFlip: '#35b578' }
  }
  if (currentSolar == '白露') {
    return { firstColor: '#bdd8a5', secondColor: '#406b5e', firstColorFlip: '#9dd4bc', secondColorFlip: '#274238' }
  }
  if (currentSolar == '秋分') {
    return { firstColor: '#f0ce8f', secondColor: '#c1ac9a', firstColorFlip: '#d9ba81', secondColorFlip: '#ab9888' }
  }
  if (currentSolar == '寒露') {
    return { firstColor: '#edcbae', secondColor: '#c3beba', firstColorFlip: '#d6b79d', secondColorFlip: '#b0aba8' }
  }
  if (currentSolar == '霜降') {
    return { firstColor: '#f2e6e2', secondColor: '#997b50', firstColorFlip: '#ded3cf', secondColorFlip: '#826944' }
  }
  if (currentSolar == '立冬') {
    return { firstColor: '#3d6989', secondColor: '#abc9df', firstColorFlip: '#335873', secondColorFlip: '#8fa8ba' }
  }
  if (currentSolar == '小雪') {
    return { firstColor: '#c3dcf4', secondColor: '#b0cced', firstColorFlip: '#b1c8de', secondColorFlip: '#9bb4d1' }
  }
  if (currentSolar == '大雪') {
    return { firstColor: '#afc8e7', secondColor: '#84a8d2', firstColorFlip: '#a0b7d4', secondColorFlip: '#7797bd' }
  }
  if (currentSolar == '冬至') {
    return { firstColor: '#ede1cc', secondColor: '#a3c1ce', firstColorFlip: '#d4c9b6', secondColorFlip: '#8da7b3' }
  }
  return { firstColor: '#ede1cc', secondColor: '#a3c1ce', firstColorFlip: '#d4c9b6', secondColorFlip: '#8da7b3' }
}