<template>
  <div ref="ChartLineMetricRef" v-loading="rqLoading" element-loading-text="正在查询流量信息, 请稍后..." :style="{
    'height': fixedBox.height,
    'width': fixedBox.width
  }"></div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, inject, watch } from 'vue'
import { metricLineApi } from '@renderer/api/sentinel'
import { useDark } from '@vueuse/core'
// echarts
import * as echartTheme from './chartTheme'
import * as echarts from 'echarts/core'
import { TitleComponent, TooltipComponent, GridComponent, LegendComponent } from 'echarts/components'
import { LineChart } from 'echarts/charts'
import { UniversalTransition } from 'echarts/features'
import { CanvasRenderer } from 'echarts/renderers'
echarts.use([TitleComponent, TooltipComponent, LegendComponent, GridComponent, LineChart, CanvasRenderer, UniversalTransition])

const isDark = useDark();

watch(() => isDark.value, (_newValue: any, _oldValue: any) => {
  renderChart()
})

/* ============================================================
 * inject
 * 用于多层嵌套中使用流量, 下列相关信息使用
 * ============================================================ */
const fixedBox: any = inject('chartLineResourceFixedBox', { height: '100%', width: '100%' });

/* ============================================================
 * props
 * 在单层嵌套中使用的简单的参数
 * ============================================================ */
const props = defineProps({
  resource: { type: String, default: '__total_inbound_traffic__' },
  interval: { type: String, default: '1d' },
  startTime: { type: String, default: '' },
  endTime: { type: String, default: '' }
})

// -------------------- ref
const rqLoading = ref<boolean>(true);
const ChartLineMetricRef = ref<any>(null);
let chartLineMetric: any;
let chartData = {
  title: '',
  subTitle: '',
  x: <any>[],// 折线图的X轴
  success: <any>[],
  avgRt: <any>[],
  maxVisual: 0
}

// -------------------- methods
/**
 * 查询指标数据
 */
const getChartLineMetric = () => {
  rqLoading.value = true;
  let params = {
    resource: '__total_inbound_traffic__',
    interval: props.interval,
    startTime: props.startTime,
    endTime: props.endTime,
    customInterval: 30,
    customIntervalUnit: 'MINUTES'
  }
  metricLineApi(params).then(resp => {
    chartData.title = resp.data.title;
    chartData.subTitle = resp.data.subTitle;
    chartData.x = [];
    chartData.success = [];
    chartData.avgRt = [];
    chartData.x = resp.data.x;
    chartData.success = resp.data.s;
    chartData.avgRt = resp.data.avgRt;
    renderChart(() => {
      setTimeout(() => {
        rqLoading.value = false;
      }, 150);
    });
  })
}

const renderChart = (callback?: any) => {
  let dark: boolean = isDark.value
  chartLineMetric.setOption({
    grid: { top: 30, left: 40, right: 15, bottom: 35 },
    title: {
      ...echartTheme.title(),
      ...{ top: 0, left: 0, text: '' }
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
        data: chartData.x,
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
        appendToBody: true,
        formatter: (params: any) => {
          return `
          <div class="chart-line-sentinel-tooltip">
            <div class="xaxis-title">${params[0].axisValue}</div>
            <div class="data" style="color:${params[0].color}">
              <div>
                <span class="iconbl bl-sendmail-line"></span>
                ${params[0].seriesName}
              </div>
              <span style="font-weight: 700;">
                ${params[0].data}
              </span>
            </div>
            <div class="data" style="color:${params[1].color}">
              <div>
                <span class="iconbl bl-a-historicalrecord-line"></span>
                ${params[1].seriesName}
              </div>
              <span style="font-weight: 700;">
                ${params[1].data}
              </span>
            </div>
          </div>
          `
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
          color: '#7d7ced',
          shadowColor: dark ? '#000000' : '#7d7ced',
          shadowOffsetY: 5,
          shadowBlur: 10
        },
        areaStyle: {
          color: {
            type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [{ offset: 0, color: '#7d7ced' }, { offset: 1, color: '#0066FF00' }]
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
          shadowColor: dark ? '#000000' : '#e3a300',
          shadowOffsetY: 5,
          shadowBlur: 10
        },
        itemStyle: {
          color: '#e3a300'
        },
        areaStyle: {
          color: {
            type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [{ offset: 0, color: '#e3a300' }, { offset: 1, color: '#E3A30000' }]
          }
        }
      }
    ]
  });
  if (callback !== undefined) {
    callback();
  }
}

onMounted(() => {
  chartLineMetric = echarts.init(ChartLineMetricRef.value);
  reload()
  windowResize();
})

const reload = () => {
  nextTick(() => {
    getChartLineMetric();
  })
}

const windowResize = () => {
  chartLineMetric.resize();
}

defineExpose({
  reload, windowResize
})
</script>

<style scoped lang="scss">
.chart-resource-histogram-root {
  @include box(100%, 100%);
}
</style>


<style lang="scss">
.chart-line-sentinel-tooltip {
  @include box(240px, 80px);
  @include themeBorder(1px, #C5AEF67F, #899911A0, 'around', 4px);
  @include themeShadow(3px 3px 10px 1px #EBEBEB, 3px 3px 10px 1px #1A1A1A);
  position: relative;
  z-index: 9999999;
  background: #FFFFFF00;
  padding: 10px;
  box-shadow: none;
  backdrop-filter: blur(4px);

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