<template>
  <!-- 图表 -->
  <div class="yq-user-info-heatmap" ref="ChartHeatmapRef" />
  <!-- 图表数据切换 -->
  <div class="heatmap-type-choice-btns">
    <div class="heatmap-type-content">编辑热力图</div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { articleHeatmapApi } from '@/api/blossom'
// echarts
import * as echarts from 'echarts/core'
import { TitleComponent, CalendarComponent, TooltipComponent, VisualMapComponent, LegendComponent } from 'echarts/components'
import { HeatmapChart } from 'echarts/charts'
import { CanvasRenderer } from 'echarts/renderers'
import { ScatterChart, EffectScatterChart } from 'echarts/charts'
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
  EffectScatterChart,
  UniversalTransition
])

const ChartHeatmapRef = ref()
let chartHeatmap: any
let chartData: any = []
let maxUpdateNum = 0
let dateBegin = ''
let dateEnd = ''

let articleHeatmap = { init: false, chartData: [], maxUpdateNum: 0, dateBegin: '', dateEnd: '' }

const getBlossomHeatmap = () => {
  let handle = () => {
    maxUpdateNum = articleHeatmap.maxUpdateNum
    dateBegin = articleHeatmap.dateBegin
    dateEnd = articleHeatmap.dateEnd
    chartData = articleHeatmap.chartData
    renderChart()
  }

  if (!articleHeatmap.init) {
    articleHeatmapApi().then((res) => {
      articleHeatmap.init = true
      articleHeatmap.maxUpdateNum = res.data.maxStatValues
      articleHeatmap.dateBegin = res.data.dateBegin
      articleHeatmap.dateEnd = res.data.dateEnd
      articleHeatmap.chartData = res.data.data
      handle()
    })
  } else {
    handle()
  }
}

const renderChart = (callback?: any) => {
  chartHeatmap.setOption({
    // 越靠下的数值越高
    gradientColor: [
      // '#494949',
      // '#b0be83',
      // '#698f14',
      '#494949',
      '#9b9b9b',
      '#b3b3b3'
      // '#494949',
      // '#835947',
      // '#aa725a',
    ],
    tooltip: {
      backgroundColor: '#2d3239',
      borderColor: '#1f1f1f',
      textStyle: {
        color: '#7a7a7a'
      },
      formatter: (params: any) => {
        let date = params.data[0]
        let num = params.data[1]
        return "<div style='text-align: left'>" + date + ' 日 <br/>' + '编辑 [ ' + num + ' ] 篇文章' + '</div>'
      }
    },
    calendar: [
      {
        top: 40,
        bottom: 5,
        left: 30,
        right: 30,
        cellSize: ['auto', 30],
        range: [dateBegin, dateEnd],
        splitLine: {
          show: true,
          lineStyle: {
            color: '#3F3F3F',
            width: 2,
            type: 'solid'
          }
        },
        itemStyle: {
          color: '#31363a',
          borderWidth: 1,
          borderColor: '#3F3F3F'
        },
        yearLabel: { show: false },
        // 月份轴
        monthLabel: { nameMap: 'ZH', color: '#747375' },
        // 天份轴
        dayLabel: { nameMap: 'ZH', firstDay: 1, color: '#747375' }
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
        type: 'scatter',
        coordinateSystem: 'calendar',
        animation: true,
        animationDuration: 200,
        animationDurationUpdate: 100,
        data: chartData === undefined ? 0 : chartData,
        symbolSize: (val: any) => {
          if (val[1] === 0) {
            return 0
          } else {
            return 10
          }
        }
      },
      {
        type: 'effectScatter',
        coordinateSystem: 'calendar',
        // 排序后选最多的5个
        data:
          chartData === undefined
            ? 0
            : chartData
                .sort((a: any, b: any) => {
                  return b[1] - a[1]
                })
                .slice(0, 5),
        // 何时显示特效
        showEffectOn: 'render',
        // 涟漪特效的配置
        rippleEffect: {
          number: 3,
          brushType: 'stroke'
        },
        zlevel: 1
      }
    ]
  })
}
const windowResize = () => {
  chartHeatmap.resize()
}

onMounted(() => {
  chartHeatmap = echarts.init(ChartHeatmapRef.value)
  windowResize()
  getBlossomHeatmap()
  window.addEventListener('resize', windowResize)
})
</script>

<style lang="scss">
// 整体左边距
$info-margin-left: 130px;

.yq-user-info-heatmap {
  @include box(330px, 175px);
}

.heatmap-type-choice-btns {
  @include flex(row, flex-start, flex-start);
  @include box(100%, 30px);
  margin-top: 8px;

  .heatmap-type-content {
    @include box(85px, 30px);
    @include font(12px, 700);
    color: #616060;
  }

  .heatmap-type-choice-btn {
    @include box(30px, 30px);
    @include border(1px, #3f3f3f, 3px);
    margin: 0 20px 0 25px;
    padding: 5px 3px;
    transition: box-shadow 0.4s;
    cursor: pointer;

    &:hover {
      box-shadow: 0 0 10px 1px rgba(24, 18, 18, 0.5);
    }

    svg {
      width: 20px;
      height: 20px;
    }
  }
}
</style>
