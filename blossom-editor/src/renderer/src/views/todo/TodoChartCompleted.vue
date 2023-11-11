<template>
  <div class="chart-line-completed-root" v-loading="rqLoading" element-loading-text="正在查询事项完成率, 请稍后...">
    <div style="width: 100%; height: 100%" ref="ChartLineLogRef" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, nextTick } from 'vue'
import { useDark } from '@vueuse/core'
import { formartNumber } from '@renderer/assets/utils/util'
import * as echartTheme from '@renderer/assets/styles/chartTheme'
import * as echarts from 'echarts/core'
import { TitleComponent, TooltipComponent, GridComponent, LegendComponent } from 'echarts/components'
import { LineChart } from 'echarts/charts'
import { UniversalTransition } from 'echarts/features'
import { LabelLayout } from 'echarts/features'
import { CanvasRenderer } from 'echarts/renderers'
import { getPrimaryColor } from '@renderer/scripts/global-theme'
echarts.use([
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  LabelLayout,
  GridComponent,
  LegendComponent,
  LineChart,
  CanvasRenderer,
  UniversalTransition
])

const isDark = useDark()

onMounted(() => {
  chartLineLog = echarts.init(ChartLineLogRef.value)
  windowResize()
})

watch(
  () => isDark.value,
  (_newValue: any, _oldValue: any) => {
    renderChart()
  }
)

const ChartLineLogRef = ref<any>(null)
const rqLoading = ref<boolean>(true)
let chartLineLog: any
let chartData: any = {
  statDates: [],
  statRates: []
}

const renderChart = async (callback?: any) => {
  let primaryColor = getPrimaryColor()
  chartLineLog.setOption({
    grid: { top: 20, left: 35, right: 10, bottom: 50 },
    legend: {
      ...echartTheme.legend(),
      ...{ top: 0, right: 10 }
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
          return `
          <div class="chart-line-word-tooltip">
            <div class="xaxis-title">${params[0].axisValue}</div>
            <div class="data">
              <div>
                <span class="iconbl bl-a-boxchoice-line"></span>
                ${formartNumber(params[0].data)}%
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
        axisTick: {
          length: 5,
          lineStyle: {
            color: isDark.value ? '#353535' : '#C8C8C8'
          }
        },
        // X轴直通顶部的线
        splitLine: {
          show: true,
          lineStyle: {
            color: isDark.value ? '#2B2B2B' : '#EDEDED'
          }
        },
        axisLabel: {
          rotate: 40
        },
        data: chartData.statDates
      }
    },
    yAxis: {
      ...echartTheme.yAxis(),
      ...{
        splitLine: {
          lineStyle: {
            color: isDark.value ? '#2B2B2B' : '#EDEDED'
          }
        }
      }
    },
    series: [
      {
        name: '完成率',
        type: 'line',
        sampling: 'lttb',
        smooth: true,
        showSymbol: false,
        data: chartData.statRates,
        lineStyle: {
          width: 2,
          cap: 'round',
          color: primaryColor.color,
          shadowColor: primaryColor.color5,
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
              { offset: 0, color: primaryColor.color5 },
              { offset: 0.5, color: primaryColor.color9 }
            ]
          },
          shadowColor: '#00000000',
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

const reload = (dates: any, rates: any) => {
  nextTick(() => {
    chartData.statDates = dates
    chartData.statRates = rates
    renderChart()
    rqLoading.value = false
  })
}
const windowResize = () => {
  chartLineLog.resize()
}
defineExpose({ reload, windowResize })
</script>

<style scoped lang="scss">
.chart-line-completed-root {
  @include box(100%, 100%);
}
</style>
