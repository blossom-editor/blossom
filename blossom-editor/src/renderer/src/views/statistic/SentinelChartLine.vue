<template>
  <div ref="ChartLineMetricRef" v-loading="rqLoading" element-loading-text="正在查询流量统计, 请稍后..." style="height: 100%; width: 100%"></div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, watch } from 'vue'
import { metricLineApi } from '@renderer/api/sentinel'
import { useDark } from '@vueuse/core'
import { getPrimaryColor } from '@renderer/scripts/global-theme'
// echarts
import * as echartTheme from '@renderer/assets/styles/chartTheme'
import * as echarts from 'echarts/core'
import { TitleComponent, TooltipComponent, GridComponent, LegendComponent } from 'echarts/components'
import { LineChart } from 'echarts/charts'
import { UniversalTransition } from 'echarts/features'
import { CanvasRenderer } from 'echarts/renderers'

echarts.use([TitleComponent, TooltipComponent, LegendComponent, GridComponent, LineChart, CanvasRenderer, UniversalTransition])

const isDark = useDark()

watch(
  () => isDark.value,
  (_newValue: any, _oldValue: any) => {
    renderChart()
  }
)

const props = defineProps({
  showTitle: {
    type: Boolean,
    default: false
  }
})

const rqLoading = ref<boolean>(true)
const ChartLineMetricRef = ref<any>(null)
let chartLineMetric: any
let chartData = {
  title: '',
  subTitle: '',
  x: <any>[], // 折线图的X轴
  success: <any>[],
  avgRt: <any>[],
  maxVisual: 0
}

/**
 * 查询指标数据
 * @param resource 资源
 * @param interval 数据时间区间枚举
 * @param cumtomInterval 自定义聚合时间范围
 */
const getChartLineMetric = (resource: string, interval: string, customInterval: number) => {
  rqLoading.value = true
  let params = {
    resource: resource,
    interval: interval,
    startTime: '',
    endTime: '',
    customInterval: customInterval,
    customIntervalUnit: 'MINUTES'
  }
  metricLineApi(params).then((resp) => {
    if (props.showTitle) {
      chartData.title = resp.data.title
      chartData.subTitle = resp.data.subTitle
    }
    chartData.x = []
    chartData.success = []
    chartData.avgRt = []
    chartData.x = resp.data.x
    chartData.success = resp.data.s
    chartData.avgRt = resp.data.avgRt
    renderChart(() => {
      setTimeout(() => {
        rqLoading.value = false
      }, 150)
    })
  })
}

const renderChart = async (callback?: any) => {
  let primaryColor = getPrimaryColor()
  chartLineMetric.setOption({
    grid: { top: 30, left: 40, right: 15, bottom: 35 },
    title: {
      ...echartTheme.title(),
      ...{ top: 30, right: 5, text: chartData.title, subtext: chartData.subTitle, subtextStyle: { lineHeight: 18 } }
    },
    legend: {
      ...echartTheme.legend(),
      ...{ top: 15, left: 35 }
    },
    xAxis: {
      ...echartTheme.xAxis(),
      ...{
        type: 'category',
        nameGap: 30,
        data: chartData.x
      }
    },
    yAxis: echartTheme.yAxis(),
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
          let tooltip = `<div class="chart-line-word-tooltip" style="width:240px;height:80px">
            <div class="xaxis-title">${params[0].axisValue}</div>`
          for (let i = 0; i < params.length; i++) {
            const series = params[i]
            tooltip += `<div class="data" style="color:${series.color}">
              <div>
                <span class="iconbl bl-sendmail-line"></span>
                ${series.seriesName}
              </div>
              <span style="font-weight: 700;">
                ${series.data}
              </span>
            </div>`
          }
          return (tooltip += `</div>`)
        }
      }
    },
    series: [
      {
        type: 'line',
        name: 'Requests',
        sampling: 'lttb',
        smooth: true, // 平滑曲线
        showSymbol: false,
        data: chartData.success,
        lineStyle: {
          width: 2,
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
            y2: 1,
            colorStops: [
              { offset: 0, color: primaryColor.color },
              { offset: 1, color: primaryColor.color9 }
            ]
          }
        }
      },
      {
        z: 2,
        type: 'line',
        name: 'Average Response Time',
        sampling: 'lttb',
        smooth: true, // 平滑曲线
        showSymbol: false,
        data: chartData.avgRt,
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
    callback()
  }
}

onMounted(() => {
  chartLineMetric = echarts.init(ChartLineMetricRef.value)
  reload()
  windowResize()
})

const reload = (resource: string = '__total_inbound_traffic__', interval: string = '1d', customInterval: number = 30) => {
  nextTick(() => {
    getChartLineMetric(resource, interval, customInterval)
  })
}

const windowResize = () => {
  chartLineMetric.resize()
}

defineExpose({
  reload,
  windowResize
})
</script>

<style scoped lang="scss">
.chart-resource-histogram-root {
  @include box(100%, 100%);
}
</style>
