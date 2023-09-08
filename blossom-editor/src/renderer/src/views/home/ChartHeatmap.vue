<template>
  <div class="chart-heatmap-root" ref="ChartHeatmapRef" v-loading="rqLoading" element-loading-text="正在查询热力图统计, 请稍后..." />
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, watch } from "vue"
import { articleHeatmapApi } from '@renderer/api/blossom'
import { useDark } from '@vueuse/core'
// echarts
import * as echartTheme from '@renderer/assets/styles/chartTheme'
import * as echarts from 'echarts/core'
import { TitleComponent, CalendarComponent, TooltipComponent, VisualMapComponent, LegendComponent } from 'echarts/components'
import { HeatmapChart } from 'echarts/charts'
import { CanvasRenderer } from 'echarts/renderers'
import {
  ScatterChart,
  // EffectScatterChart
} from 'echarts/charts'
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
  // EffectScatterChart,
  UniversalTransition
])

const isDark = useDark()

watch(() => isDark.value, (_newValue: any, _oldValue: any) => {
  renderChart()
})

const ChartHeatmapRef = ref()
const rqLoading = ref<boolean>(true)
let chartHeatmap: any
let chartData: any = []
let maxUpdateNum = 0
let dateBegin = ''
let dateEnd = ''

let articleHeatmap = { chartData: [], maxUpdateNum: 0, dateBegin: '', dateEnd: '' }

const getBlossomHeatmap = () => {
  rqLoading.value = true
  let handle = () => {
    maxUpdateNum = articleHeatmap.maxUpdateNum
    dateBegin = articleHeatmap.dateBegin
    dateEnd = articleHeatmap.dateEnd
    chartData = articleHeatmap.chartData
    renderChart(
      setTimeout(() => {
        rqLoading.value = false
      }, 300)
    )
  }
  // 查6个月内的记录
  articleHeatmapApi({ offsetMonth: -5 }).then(res => {
    articleHeatmap.maxUpdateNum = res.data.maxStatValues
    articleHeatmap.dateBegin = res.data.dateBegin
    articleHeatmap.dateEnd = res.data.dateEnd
    articleHeatmap.chartData = res.data.data
    handle()
  })
}

const renderChart = (callback?: any) => {
  let dark: boolean = isDark.value
  chartHeatmap.setOption({
    // 越靠下的数值越高
    gradientColor: dark ?
      [
        '#1e1e1e',
        '#3C4400',
        '#5B6700',
        '#768600',
        '#e3a300',
      ] :
      [
        '#FFFFFF',
        '#D3BFFF',
        '#D2A4FF',
        '#A176FF',
        '#e3a300',
      ],
    tooltip: {
      ...echartTheme.tooltip(),
      ...{
        trigger: 'item',
        borderWidth: 0,
        borderColor: 'none',
        backgroundColor: 'none',
        extraCssText: 'box-shadow: none',
        padding: 0,
        // alwaysShowContent: true,
        appendToBody: true,
        formatter: (params: any) => {
          let date = params.data[0]
          let num = params.data[1]
          return `
          <div class="chart-line-word-tooltip">
            <div class="xaxis-title">${date}</div>
            <div class="data">
              <div>
               编辑 [${num}] 篇文章
              </div>
            </div>
          </div>
          `
        }
      }
    },
    /**
     * 热力图日历配置
     */
    calendar: [{
      top: 25,
      bottom: 30,
      left: 30,
      right: 2,
      cellSize: ['auto', 30],
      range: [dateBegin, dateEnd],
      // 月的分隔线
      splitLine: {
        show: true,
        lineStyle: {
          color: dark ? '#474747' : '#DCDCDC',
          width: 2,
          type: 'solid'
        }
      },
      // 日历中的每一个方格
      itemStyle: {
        color: dark ? '#1e1e1e00' : '#ffffff00',
        borderWidth: 1,
        borderColor: dark ? '#262626' : '#F4F4F4'
      },
      yearLabel: { show: false },
      // 月份轴
      monthLabel: { color: dark ? '#474747' : '#E5E5E5', nameMap: 'ZH', },
      // 天份轴
      dayLabel: { color: dark ? '#474747' : '#E5E5E5', nameMap: 'ZH', firstDay: 1 }
    }],
    visualMap: [{
      top: 10,
      left: 300,
      min: 0,
      max: maxUpdateNum,
      show: false
    }],
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
          color: dark ? '#89991180' : '#AC8CF2C3',
          shadowColor: dark ? '#00000064' : '#9B9B9B46',
          shadowOffsetX: 2,
          shadowOffsetY: 2,
          shadowBlur: 3
        },
        data: chartData === undefined ? 0 : chartData,
        symbolSize: 20,
        // symbolSize: (val: any) => {
        //   if (val[1] === 0) {
        //     return 0
        //   } else {
        //     return 13
        //   }
        // }
      },
      // {
      //   type: 'effectScatter',
      //   coordinateSystem: 'calendar',
      //   // 排序后选最多的5个
      //   data: chartData === undefined ? 0 : chartData.sort((a: any, b: any) => {
      //     return b[1] - a[1]
      //   }).slice(0, 10).map(item => {
      //     return item
      //   }),
      //   // 何时显示特效
      //   showEffectOn: 'render',
      //   symbolSize: 15,
      //   // 涟漪特效的配置
      //   rippleEffect: {
      //     number: 3,
      //     period: 10,
      //     scale: 2.5,
      //     brushType: 'stroke'
      //   },
      //   zlevel: 1
      // }
    ]
  })
  if (callback !== undefined) {
    callback;
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
  reload, windowResize
})

</script>


<style scoped lang="scss">
.chart-heatmap-root {
  @include box(100%, 100%)
}
</style>