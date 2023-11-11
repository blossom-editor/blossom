<template>
  <div class="chart-heatmap-root" ref="ChartHeatmapRef" v-loading="rqLoading" element-loading-text="正在查询热力图统计, 请稍后..." />
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, watch } from 'vue'
import { articleHeatmapApi } from '@renderer/api/blossom'
import { useDark } from '@vueuse/core'
import { getPrimaryColor } from '@renderer/scripts/global-theme'
// echarts
import * as echartTheme from '@renderer/assets/styles/chartTheme'
import * as echarts from 'echarts/core'
import { TitleComponent, CalendarComponent, TooltipComponent, VisualMapComponent, LegendComponent } from 'echarts/components'
import { HeatmapChart } from 'echarts/charts'
import { CanvasRenderer } from 'echarts/renderers'
import { ScatterChart } from 'echarts/charts'
import { UniversalTransition } from 'echarts/features'
echarts.use([
  TitleComponent,
  CalendarComponent,
  TooltipComponent,
  VisualMapComponent,
  HeatmapChart,
  CanvasRenderer,
  LegendComponent,
  ScatterChart,
  UniversalTransition
])

const isDark = useDark()

watch(
  () => isDark.value,
  (_newValue: any, _oldValue: any) => {
    renderChart()
  }
)

type Heatmap = {
  chartData: any[]
  maxUpdateNum: number
  dateBegin: ''
  dateEnd: ''
}
const ChartHeatmapRef = ref()
const rqLoading = ref<boolean>(true)
let chartHeatmap: any
let chartData: any = []
let maxUpdateNum = 0
let dateBegin = ''
let dateEnd = ''
let articleHeatmap: Heatmap = { chartData: [], maxUpdateNum: 0, dateBegin: '', dateEnd: '' }

const getBlossomHeatmap = () => {
  rqLoading.value = true
  let handle = () => {
    maxUpdateNum = articleHeatmap.maxUpdateNum
    dateBegin = articleHeatmap.dateBegin
    dateEnd = articleHeatmap.dateEnd
    chartData = articleHeatmap.chartData.filter((item) => item[1] !== 0)
    renderChart(setTimeout(() => (rqLoading.value = false), 300))
  }
  // 查6个月内的记录
  articleHeatmapApi({ offsetMonth: -5 }).then((res) => {
    articleHeatmap.maxUpdateNum = res.data.maxStatValues
    articleHeatmap.dateBegin = res.data.dateBegin
    articleHeatmap.dateEnd = res.data.dateEnd
    articleHeatmap.chartData = res.data.data
    handle()
  })
}

const renderChart = async (callback?: any) => {
  let { color, color2, color4, color6 } = getPrimaryColor()
  let dark: boolean = isDark.value
  chartHeatmap.setOption({
    gradientColor: dark ? ['#000000A2', color6, color4, color2, color] : ['#FFFFFFA2', color6, color4, color2, color],
    tooltip: {
      ...echartTheme.tooltip(),
      ...{
        trigger: 'item',
        borderWidth: 0,
        borderColor: 'none',
        backgroundColor: 'none',
        extraCssText: 'box-shadow: none',
        padding: 0,
        appendToBody: false,
        formatter: (params: any) => {
          let date = params.data[0]
          let num = params.data[1]
          return `
          <div class="chart-line-word-tooltip">
            <div class="xaxis-title">${date}</div>
            <div class="data">
              <span class="iconbl bl-Heatmap"></span>
              <span style="padding-left:10px;">编辑 [${num}] 篇文章</span>
            </div>
          </div>
          `
        }
      }
    },
    calendar: [
      {
        top: 35,
        bottom: 12,
        left: 30,
        right: 2,
        cellSize: ['auto', 30],
        range: [dateBegin, dateEnd],
        // 月的分隔线
        splitLine: {
          show: true,
          lineStyle: { color: dark ? '#474747' : '#DCDCDC', width: 2 }
        },
        // 日历中的每一个方格
        itemStyle: {
          color: dark ? '#1e1e1e00' : '#ffffff00',
          borderWidth: 1,
          borderColor: dark ? '#262626' : '#F4F4F4'
        },
        yearLabel: { show: false },
        // 月份轴
        monthLabel: { color: dark ? '#474747' : '#E5E5E5', nameMap: 'ZH' },
        // 天份轴
        dayLabel: { color: dark ? '#474747' : '#E5E5E5', nameMap: 'ZH', firstDay: 1 }
      }
    ],
    visualMap: [
      {
        top: 10,
        left: 300,
        min: 0,
        max: maxUpdateNum,
        show: false
      }
    ],
    series: [
      {
        // scatter | heatmap
        type: 'scatter',
        symbol: 'roundRect',
        coordinateSystem: 'calendar',
        animation: true,
        animationDuration: 200,
        animationDurationUpdate: 100,
        itemStyle: {
          shadowColor: dark ? '#00000064' : '#9B9B9B46',
          shadowOffsetX: 2,
          shadowOffsetY: 2,
          shadowBlur: 3
        },
        data: chartData === undefined ? 0 : chartData,
        symbolSize: 20
      }
    ]
  })
  if (callback !== undefined) {
    callback
  }
}

const windowResize = () => {
  chartHeatmap.resize()
}

onMounted(() => {
  chartHeatmap = echarts.init(ChartHeatmapRef.value)
  reload()
  windowResize()
})

const reload = () => {
  nextTick(() => {
    getBlossomHeatmap()
  })
}

defineExpose({
  reload,
  windowResize
})
</script>

<style scoped lang="scss">
.chart-heatmap-root {
  @include box(100%, 100%);
}
</style>
