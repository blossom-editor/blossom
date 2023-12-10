<template>
  <div class="chart-line-word-root" v-loading="rqLoading" element-loading-text="正在查询字数统计, 请稍后...">
    <div style="width: 100%; height: 100%" ref="ChartLineRef" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, nextTick } from 'vue'
import { useDark } from '@vueuse/core'
import { ChartLineWordsData, renderChart } from './scripts/chart-line-words'
import { articleWordLineApi } from '@renderer/api/blossom'
// echarts
import * as echarts from 'echarts/core'
import { TitleComponent, TooltipComponent, GridComponent, LegendComponent } from 'echarts/components'
import { LineChart } from 'echarts/charts'
import { UniversalTransition } from 'echarts/features'
import { LabelLayout } from 'echarts/features'
import { CanvasRenderer } from 'echarts/renderers'
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
  chartLine = echarts.init(ChartLineRef.value)
  reload()
  windowResize()
})

watch(
  () => isDark.value,
  () => {
    renderChart(chartLine, chartData)
  }
)

const ChartLineRef = ref<any>(null)
const rqLoading = ref<boolean>(true)
let chartLine: any
let chartData: ChartLineWordsData = { statDates: [], statValues: [], statValuesMom: [] }

const getWords = () => {
  rqLoading.value = true
  articleWordLineApi().then((resp) => {
    chartData = resp.data
    

    renderChart(
      chartLine,
      chartData,
      setTimeout(() => {
        rqLoading.value = false
      }, 300)
    )
  })
}

const reload = () => {
  nextTick(() => {
    getWords()
  })
}
const windowResize = () => {
  chartLine.resize()
}
defineExpose({ reload, windowResize })
</script>

<style scoped lang="scss">
.chart-line-word-root {
  @include box(100%, 100%);
}
</style>

<style lang="scss">
.chart-line-word-tooltip {
  @include themeShadow(3px 3px 10px 1px #ebebeb, 3px 3px 10px 1px #1a1a1a);
  border: 1px solid var(--el-color-primary-light-5);
  border-radius: 4px;
  position: relative;
  z-index: 9999999;
  background: #ffffff00;
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
