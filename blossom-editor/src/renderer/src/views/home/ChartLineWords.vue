<template>
  <div class="chart-line-word-root" v-loading="rqLoading" element-loading-text="正在查询字数统计, 请稍后...">
    <div style="width: 100%;height: 100%;" ref="ChartLineLogRef" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, nextTick } from "vue";
import { articleWordLineApi } from "@renderer/api/blossom";
import { useDark } from '@vueuse/core'
import { formartNumber } from '@renderer/assets/utils/util'
// echarts
import * as echartTheme from '@renderer/assets/styles/chartTheme'
import * as echarts from 'echarts/core';
import { TitleComponent, TooltipComponent, GridComponent, LegendComponent } from 'echarts/components';
import { LineChart } from 'echarts/charts';
import { UniversalTransition } from 'echarts/features';
import { LabelLayout } from 'echarts/features';
import { CanvasRenderer } from 'echarts/renderers';
echarts.use([TitleComponent, TooltipComponent, LegendComponent, LabelLayout, GridComponent, LegendComponent, LineChart, CanvasRenderer, UniversalTransition]);

const isDark = useDark();

onMounted(() => {
  chartLineLog = echarts.init(ChartLineLogRef.value)
  reload()
  windowResize()
})

watch(() => isDark.value, (_newValue: any, _oldValue: any) => {
  renderChart()
})

const ChartLineLogRef = ref<any>(null)
const rqLoading = ref<boolean>(true)
let chartLineLog: any;
let chartData: any = { statDates: [], statValues: [] };

const getWords = () => {
  rqLoading.value = true
  articleWordLineApi().then((resp) => {
    chartData = resp.data
    renderChart(
      setTimeout(() => {
        rqLoading.value = false
      }, 300)
    )
  })
}

const renderChart = (callback?: any) => {
  let dark: boolean = isDark.value
  chartLineLog.setOption({
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
        data: chartData.statDates,
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
          width: 3, cap: 'round',
          color: dark ? '#899911' : '#ad8cf2',
          shadowColor: dark ? '#000000' : '#cebdf0',
          shadowOffsetY: 5,
          shadowBlur: 10
        },
        itemStyle: {
          color: '#CDCDCD',
        },
        areaStyle: {
          color: {
            type: 'linear', x: 0, y: 0, x2: 0, y2: 0.5,
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
    callback;
  }
}

const reload = () => { nextTick(() => { getWords(); }) }
const windowResize = () => { chartLineLog.resize(); }
defineExpose({ reload, windowResize })

</script>

<style scoped lang="scss">
.chart-line-word-root {
  @include box(100%, 100%);
}
</style>

<style lang="scss">
.chart-line-word-tooltip {
  @include box(130px, 60px);
  @include themeBorder(1px, #C5AEF67F, #899911A0, 'around', 4px);
  @include themeShadow(3px 3px 10px 1px #EBEBEB, 3px 3px 10px 1px #1A1A1A);
  position: relative;
  z-index: 9999999;
  background: #FFFFFF00;
  padding: 10px;
  box-shadow: none;
  backdrop-filter: blur(5px);


  .xaxis-title {
    @include font(15px, 700);
    color: var(--el-color-primary);
  }

  .data {
    @include flex(row, space-between, center);
    @include font(13px, 300);
  }

  &::before {
    @include box(100%, 100%);
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    z-index: -1;
  }
}
</style>