import { useDark } from '@vueuse/core'
import * as echartTheme from '@renderer/assets/styles/chartTheme'
import { formartNumber } from '@renderer/assets/utils/util'

const isDark = useDark()

export type ChartLineWordsData = {
  statDates: any[]
  statValues: any[]
}

/**
 * 设置折线图内容
 * @param chart 图表对象
 * @param chartData 图表数据
 * @param callback 渲染后回调
 */
export const renderChart = (chart: any, chartData: ChartLineWordsData, callback?: any) => {
  let dark: boolean = isDark.value
  chart.setOption({
    grid: { top: 30, left: 60, right: 20, bottom: 35 },
    legend: {
      ...echartTheme.legend(),
      ...{ top: 15, left: 65 }
    },
    tooltip: {
      ...echartTheme.tooltip(),
      ...{
        borderWidth: 0,
        borderColor: 'none',
        backgroundColor: 'none',
        extraCssText: 'box-shadow: none;',
        padding: 0,
        // alwaysShowContent: true,
        appendToBody: false,
        formatter: (params: any) => {
          return `
          <div class="chart-line-word-tooltip">
            <div class="xaxis-title">${params[0].axisValue}</div>
            <div class="data">
              <div>
                <span class="iconbl bl-pen-line"></span>
                ${formartNumber(params[0].data)}
              </div>
            </div>
          </div>
          `
        }
      }
    },
    xAxis: {
      ...echartTheme.xAxis(),
      ...{
        type: 'category',
        offset: 10,
        nameGap: 30,
        data: chartData.statDates
      }
    },
    yAxis: echartTheme.yAxis(),
    series: [
      {
        name: 'Words',
        type: 'line',
        sampling: 'lttb',
        smooth: true,
        showSymbol: false,
        data: chartData.statValues,
        lineStyle: {
          width: 3,
          cap: 'round',
          color: dark ? '#899911' : '#ad8cf2',
          shadowColor: dark ? '#000000' : '#cebdf0',
          shadowOffsetY: 5,
          shadowBlur: 10
        },
        itemStyle: {
          color: '#CDCDCD'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 0.5,
            colorStops: [
              { offset: 0, color: dark ? '#899911B3' : '#CEBDF0AD' },
              { offset: 1, color: dark ? '#00000000' : '#FFFFFF3E' }
            ]
          },
          shadowColor: dark ? '#666666' : '#CEBDF0AD',
          shadowOffsetY: 5,
          shadowBlur: 10
        }
      }
    ]
  })
  if (callback !== undefined) {
    callback
  }
}
