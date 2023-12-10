import * as echartTheme from '@renderer/assets/styles/chartTheme'
import { formartNumber } from '@renderer/assets/utils/util'
import { getPrimaryColor } from '@renderer/scripts/global-theme'
import { useDark } from '@vueuse/core'

const isDark = useDark()

export type ChartLineWordsData = {
  statDates: any[]
  statValues: any[]
  statValuesMom: any[]
}

/**
 * 设置折线图内容
 * @param chart 图表对象
 * @param chartData 图表数据
 * @param callback 渲染后回调
 */
export const renderChart = async (chart: any, chartData: ChartLineWordsData, callback?: any) => {
  let primaryColor = getPrimaryColor()
  getMom(chartData)
  chart.setOption({
    grid: { top: 30, left: 60, right: 20, bottom: 35 },
    legend: {
      ...echartTheme.legend(),
      ...{
        top: 15,
        left: 65,
        selected: { Words: true, 'Month On Month': true }
      }
    },
    tooltip: {
      ...echartTheme.tooltip(),
      ...{
        borderWidth: 0,
        borderColor: 'none',
        backgroundColor: 'none',
        extraCssText: 'box-shadow: none;',
        padding: 0,
        appendToBody: false,
        formatter: (params: any) => {
          let tooltip = `<div class="chart-line-word-tooltip" style="width:240px;height:80px">
            <div class="xaxis-title">${params[0].axisValue}</div>`
          for (let i = 0; i < params.length; i++) {
            const series = params[i]
            tooltip += `<div class="data" style="color:${series.color}">
              <div>
                <span class="iconbl bl-pen-line"></span>
                ${series.seriesName}
              </div>
              <span style="font-weight: 700;">
                ${formartNumber(series.data)}
              </span>
            </div>`
          }
          return (tooltip += `</div>`)
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
          color: primaryColor.color,
          shadowColor: isDark.value ? '#000000' : primaryColor.color,
          shadowOffsetY: 5,
          shadowBlur: 10
        },
        itemStyle: {
          color: primaryColor.color
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 0.5,
            colorStops: [
              { offset: 0, color: primaryColor.color5 },
              { offset: 0.5, color: primaryColor.color9 },
              { offset: 1, color: 'transparent' }
            ]
          },
          shadowColor: '#00000000',
          shadowOffsetY: 5,
          shadowBlur: 10
        }
      },
      {
        z: 2,
        name: 'Month on Month',
        type: 'line',
        sampling: 'lttb',
        smooth: true, // 平滑曲线
        showSymbol: false,
        data: chartData.statValuesMom,
        lineStyle: {
          width: 2,
          color: '#e3a300',
          shadowColor: isDark.value ? '#000000' : '#e3a300',
          shadowOffsetY: 5,
          shadowBlur: 10
        },
        itemStyle: {
          color: '#e3a300'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: '#e3a300' },
              { offset: 1, color: '#E3A30000' }
            ]
          }
        }
      }
    ]
  })
  if (callback !== undefined) {
    callback
  }
}

const getMom = (chartData: ChartLineWordsData) => {
  let sameMonth: any[] = []
  let lastMonth = 0
  for (let i = 0; i < chartData.statValues.length; i++) {
    const word = chartData.statValues[i]
    if (i === 0) {
      sameMonth.push(word)
    } else {
      sameMonth.push(word - lastMonth)
    }
    lastMonth = word
  }
  chartData.statValuesMom = sameMonth
}