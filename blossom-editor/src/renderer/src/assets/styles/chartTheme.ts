import { useDark } from '@vueuse/core';
const isDark = useDark();

/**
 * 公共样式
 */
export const common = (): any => {
  if (isDark.value) {
    return {
      fontFamily: 'Consolas, Monaco',
      titleColor: '#8C8C8C',
      textColor: '#a7a7a7',
      legendColor: '#a7a7a7',
      axisLineColor: '#272727',
      // axisLineColor: '#000000',
      itemShadowColor: 'rgba(255, 255, 255, 0.3)',
      lineShadowColor: 'rgba(255, 255, 255, 0.5)',
      backgroundColor: '#131313'
    }
  } else {
    return {
      fontFamily: 'Consolas, Monaco',
      titleColor: '#8C8C8C',
      textColor: '#494949',
      legendColor: '#494949',
      axisLineColor: '#ececec',
      itemShadowColor: 'rgba(0, 0, 0, 0.7)',
      lineShadowColor: 'rgba(0, 0, 0, 0.5)',
      backgroundColor: '#ffffff'
    }
  }
}
/**
 * 标题
 */
export const title = (): any => {
  return {
    textStyle: {
      fontSize: 14,
      fontFamily: 'Jetbrains Mono',
      color: isDark.value ? '#5B5B5B' : '#ABABAB'
    }
  }
}

/**
 * 图例样式
 */
export const legend = (): any => {
  return {
    orient: 'vertical',
    textStyle: {
      fontFamily: 'Jetbrains Mono',
      color: isDark.value ? '#5B5B5B' : '#ABABAB'
    },
    itemStyle: { opacity: 0 }
  }
}

/**
 * 提示框样式
 */
export const tooltip = (): any => {
  if (isDark.value) {
    return {
      show: true,
      trigger: 'axis',
      confine: true,
      enterable: false,
      // backgroundColor: '#2B2B2B',
      // borderColor: '#252525',
      // borderWidth: 2,
      // textStyle: {
      //   color: '#A7A7A7'
      // },
      // extraCssText: 'box-shadow: 5px 5px 10px 3px rgba(255, 255, 255, 0.1);'
    }
  } else {
    return {
      show: true,
      trigger: 'axis',
      // 是否将 tooltip 框限制在图表的区域内。
      confine: true,
      // 鼠标是否可进入提示框浮层中，默认为false，如需详情内交互，如添加链接，按钮，可设置为 true。
      enterable: false,
      // backgroundColor: 
      // borderColor: '#ffffff',
      // borderWidth: 0,
      // extraCssText: 'box-shadow: 5px 5px 10px 3px rgba(0, 0, 0, 0.1);'
    }
  }
}

export const xAxis = (): any => {
  return {
    // X轴文字
    axisLabel: {
      color: isDark.value ? '#474747' : '#E5E5E5'
    },
    // X轴直通顶部的线
    splitLine: {
      show: true,
      lineStyle: {
        color: isDark.value ? '#2B2B2B' : '#F7F7F7'
      }
    },
    // X轴的坐标轴的刻度
    axisTick: {
      inside: true,
      length: 0,
      lineStyle: {
        color: isDark.value ? '#2B2B2B' : '#F7F7F7'
      }
    },
    // X轴横线
    axisLine: {
      show: true,
      lineStyle: {
        color: isDark.value ? '#2B2B2B' : '#CDCDCD',
        shadowColor: isDark.value ? '#000000' : '#41434B',
        shadowBlur: 10
      }
    },
    axisPointer: {
      lineStyle: {
        color: isDark.value ? '#353535' : '#C8C8C8'
      }
    }
  }
}

export const yAxis = (): any => {
  return {
    show: true,
    scale: true,
    type: 'value',
    splitLine: {
      lineStyle: {
        color: isDark.value ? '#2B2B2B' : '#F7F7F7'
      }
    },
    // Y轴文字
    axisLabel: {
      color: isDark.value ? '#474747' : '#E5E5E5'
    }
  }
}