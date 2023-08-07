<template>
  <div class="chart-line-log-root">
    <div style="width: 100%;height: 100%;" ref="ChartLineLogRef" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, onUnmounted } from "vue";
import { articleWordLineApi } from "@/api/blossom";
// echarts
import * as echarts from 'echarts/core';
import { TitleComponent, TooltipComponent, GridComponent, LegendComponent } from 'echarts/components';
import { LineChart } from 'echarts/charts';
import { UniversalTransition } from 'echarts/features';
import { LabelLayout } from 'echarts/features';
import { CanvasRenderer } from 'echarts/renderers';
echarts.use([TitleComponent, TooltipComponent, LegendComponent, LabelLayout, GridComponent, LegendComponent, LineChart, CanvasRenderer, UniversalTransition]);

// -------------------- data
const ChartLineLogRef = ref<any>(null)
let chartLineLog: any;
let chartData: any = {
  statDates: [
    '2022/07/01', '2022/08/01', '2022/09/01', '2022/10/01',
    '2022/11/01', '2022/12/01', '2023/01/01', '2023/02/01',
    '2023/03/01', '2023/04/01', '2023/05/01', '2023/06/01',
    '2023/07/01', '2023/08/01', '2023/09/01', '2023/10/01',
    '2023/11/01', '2023/12/01', '2024/01/01', '2024/02/01',
    '2024/03/01', '2024/04/01', '2024/05/01', '2024/06/01'
  ],
  statValues: [
    370300, 380300, 380300, 383300,
    390000, 400000, 403200, 413200,
    410300, 420000, 430000, 432631,
    432631, 442631, 452631, 462631,
    452631, 472631, 472631, 472631,
    472631, 477631, 480631, 483631
  ]
};
const rqLoading = ref<boolean>(true);

const getWords = () => {
  // chartData = resp.data;
  articleWordLineApi().then((resp) => {
    chartData = resp.data
    renderChart(
      setTimeout(() => { }, 300)
    );
  })
}

/**
 * 将数字转为千分位的字符串
 * @param param 
 * @returns 
 */
const formartNumber = (param: number): string => {
  let num: string = (param || 0).toString(), result: string = '';
  while (num.length > 3) {
    result = ',' + num.slice(-3) + result;
    num = num.slice(0, num.length - 3);
  }
  if (num) { result = num + result; }
  return result;
}

const renderChart = (callback?: any) => {
  chartLineLog.setOption(
    {
      title: {
        bottom: 45, right: 25, text: '历史笔记字数', textStyle: {
          fontSize: 14,
          fontFamily: 'Consolas, Monaco',
          color: '#5B5B5B'
        }
      },
      grid: { top: 30, left: 40, right: 20, bottom: 35 },
      tooltip: {
        show: true,
        trigger: 'axis',
        confine: true,
        enterable: false,
        borderWidth: 0,
        borderColor: 'none',
        backgroundColor: 'none',
        extraCssText: 'box-shadow: none;',
        padding: 0,
        // alwaysShowContent: true,
        appendToBody: true,
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
      },
      xAxis: {
        type: 'category',
        offset: 10,
        nameGap: 30,
        data: chartData.statDates,
        // X轴文字
        axisLabel: {
          color: '#6D6D6D'
        },
        // X轴直通顶部的线
        splitLine: {
          show: false
        },
        // X轴的坐标轴的刻度
        axisTick: {
          inside: true,
          length: 0,
          lineStyle: {
            color: '#41434B'
          }
        },
        // X轴横线
        axisLine: {
          show: true,
          lineStyle: {
            color: '#41434B',
            shadowColor: '#000000',
            shadowBlur: 10
          }
        },
        axisPointer: {
          lineStyle: {
            color: '#6b6d76'
          }
        }
      },
      yAxis: {
        show: false,
        scale: true,
        type: 'value',
        splitLine: {
          lineStyle: {
            color: '#343A42'
          }
        }
      },
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
            color: '#CDCDCD',
            shadowColor: '#000000AC',
            shadowOffsetY: 5,
            shadowBlur: 10
          },
          itemStyle: {
            color: '#CDCDCD',
          },
          areaStyle: {
            color: {
              type: 'linear', x: 0, y: 0, x2: 0, y2: 0.5,
              colorStops: [{ offset: 0, color: '#666666B3' }, { offset: 1, color: '#00000000' }]
            },
            shadowColor: '#666666',
            shadowOffsetY: 5,
            shadowBlur: 10
          }
        }
      ]
    }
  )
  if (callback !== undefined) {
    callback;
  }
}

const reload = () => {
  nextTick(() => { getWords(); })
}

const windowResize = () => {
  chartLineLog.resize();
}

onMounted(() => {
  chartLineLog = echarts.init(ChartLineLogRef.value);
  windowResize();
  window.addEventListener("resize", windowResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', windowResize)
})
defineExpose({ reload, windowResize })

</script>

<style scoped lang="scss">
.chart-line-log-root {
  @include box(100%, 100%);
}
</style>

<style lang="scss">
.chart-line-word-tooltip {
  @include box(150px, 60px);
  border: 1px solid #CDCDCD19;
  position: relative;
  z-index: 9999999;
  background: #FFFFFF00;
  padding: 10px;
  box-shadow: none;
  backdrop-filter: blur(5px);
  border-radius: 5px;


  .xaxis-title {
    @include font(15px, 700);
    color: #cdcdcd;
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