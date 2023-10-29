<template>
  <div class="words-info-root" v-loading="rqLoading" :element-loading-text="rqloadingText">
    <!-- 标题 -->
    <div class="info-title">
      <div class="iconbl bl-statistic-line"></div>
      字数统计编辑
    </div>
    <div class="workbench">
      <div class="form">
        <div>
          <span class="info-sub-title">可以修改字数统计折线图的过往统计数据(按月)。</span><br />
          <span class="info-sub-title">已添加的统计信息只能修改，无法删除。</span>
        </div>
        <div style="margin-top: 10px">
          月份：<el-date-picker v-model="wordsDate" type="month" style="width: 166px" placeholder="选择月份" value-format="YYYY-MM" />
          <el-button style="margin-left: 10px" @click="addWordsDate">添加月份</el-button>
        </div>
        <div class="btn-container">
          <el-button @click="resetWordsList">恢复默认</el-button>
          <el-button @click="render">预览图表</el-button>
          <el-button @click="saveWords" type="primary">确认修改</el-button>
        </div>
      </div>
      <el-divider style="margin: 0 10px" direction="vertical" />
      <div class="lines">
        <div style="width: 100%; height: 100%" ref="ChartLineRef"></div>
      </div>
    </div>
    <el-divider style="margin: 0" />
    <div class="words-item-container">
      <div class="words-item" v-for="data in wordsList?.sort((a, b) => a.date.localeCompare(b.date))">
        <span :class="[data.date.substring(4) == '-01' ? 'january' : '']">{{ data.date }}</span>
        <el-input-number v-model="data.value" style="width: 110px" controls-position="right" :step="10000" :min="0"></el-input-number>
        <el-button class="del-btn" text bg style="margin-left: 5px" @click="delWordsDate(data.date)">
          <span class="iconbl bl-delete-line"></span>
        </el-button>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ChartLineWordsData, renderChart } from './scripts/chart-line-words'
import { articleWordsListApi, articleWordsSaveApi } from '@renderer/api/blossom'
// echarts
import * as echarts from 'echarts/core'
import { TitleComponent, TooltipComponent, GridComponent, LegendComponent } from 'echarts/components'
import { LineChart } from 'echarts/charts'
import { UniversalTransition } from 'echarts/features'
import { LabelLayout } from 'echarts/features'
import { CanvasRenderer } from 'echarts/renderers'
import { isNull } from '@renderer/assets/utils/obj'
import Notify from '@renderer/scripts/notify'
import { ElMessageBox } from 'element-plus'

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

onMounted(() => {
  chartLine = echarts.init(ChartLineRef.value)
  getWordsList()
})

const rqLoading = ref(true)
const rqloadingText = ref('')

let chartLine: any
const ChartLineRef = ref<any>(null)
const wordsDate = ref('')
const wordsList = ref<{ date: string; value: number | string }[] | null>([])

const getWordsList = () => {
  rqLoading.value = true
  rqloadingText.value = '正在获取统计数据'
  articleWordsListApi()
    .then((resp) => {
      wordsList.value = resp.data
      render()
    })
    .finally(() => (rqLoading.value = false))
}

const resetWordsList = () => {
  ElMessageBox.confirm('未保存的数据将会丢失，是否重置?', '是否重置', {
    confirmButtonText: '我要重置',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    getWordsList()
  })
}

/**
 * 添加统计信息
 */
const addWordsDate = () => {
  if (wordsList.value == null) {
    wordsList.value = [{ date: wordsDate.value, value: 0 }]
    return
  }
  let exist = false
  for (let i = 0; i < wordsList.value.length; i++) {
    const stat = wordsList.value[i]
    if (stat.date === wordsDate.value) {
      exist = true
      break
    }
  }
  if (!exist) {
    wordsList.value.push({ date: wordsDate.value, value: 0 })
  } else {
    Notify.info(`日期 [${wordsDate.value}] 已存在`, '提示')
  }
}

/**
 * 保存统计信息
 */
const saveWords = () => {
  if (isNull(wordsList.value)) {
    Notify.warning('未添加统计数据', '无法保存')
    return
  }

  ElMessageBox.confirm('是否确定保存字数统计。', '是否保存', {
    confirmButtonText: '确认保存',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    rqLoading.value = true
    rqloadingText.value = '正在保存统计数据'
    articleWordsSaveApi({ wordsList: wordsList.value })
      .then((_resp) => {
        Notify.success('保存字数统计成功, 请在首页刷新后查看', '保存成功')
      })
      .finally(() => {
        render()
        rqLoading.value = false
      })
  })
}

/**
 * 删除统计信息
 * @param date 删除的日期 yyyy-MM
 */
const delWordsDate = (date: string) => {
  if (wordsList.value == null) {
    return
  }
  for (let i = 0; i < wordsList.value.length; i++) {
    const stat = wordsList.value[i]
    if (stat.date === date) {
      wordsList.value.splice(i, 1)
      break
    }
  }
}

const render = () => {
  windowResize()

  if (isNull(wordsList.value)) {
    return
  }

  let chartDate: ChartLineWordsData = {
    statDates: [],
    statValues: []
  }

  wordsList
    .value!.sort((a, b) => a.date.localeCompare(b.date))
    .forEach((e) => {
      chartDate.statDates.push(e.date)
      chartDate.statValues.push(e.value)
    })

  renderChart(chartLine, chartDate)
}

const windowResize = () => {
  chartLine.resize()
}
</script>
<style scoped lang="scss">
@import '@renderer/assets/styles/bl-dialog-info';

.words-info-root {
  @include box(100%, 100%);
  border-radius: 10px;
  overflow: hidden;

  .info-sub-title {
    padding-top: 5px;
    font-size: 12px;
    color: var(--bl-text-color-light);
    text-shadow: none;
  }

  .workbench {
    @include flex(row, flex-start, flex-start);
    @include box(100%, 200px);
    padding: 0 20px;
    .form {
      @include box(300px, 100%);
      padding: 10px 0;
      .btn-container {
        border-top: 1px solid var(--el-border-color);
        padding-top: 20px;
        margin-top: 20px;
        margin-right: 10px;
        text-align: right;
      }
    }
    .lines {
      @include box(calc(100% - 320px), 100%);
    }
    .el-divider {
      height: 100%;
    }
  }

  .words-item-container {
    @include box(100%, calc(100% - 250px));
    @include flex(row, flex-start, flex-start);
    flex-wrap: wrap;
    padding: 10px 10px;
    overflow-y: scroll;

    .words-item {
      width: 240px;
      margin-bottom: 5px;
      margin-right: 5px;
      font-size: 14px;
      padding: 5px 5px;
      border-radius: 4px;
      transition: box-shadow 0.1s;

      .january {
        color: var(--el-color-primary);
        font-weight: bold;
      }

      &:hover {
        box-shadow: var(--bl-box-shadow);
        .del-btn {
          opacity: 1;
        }
      }

      & > span {
        padding-right: 10px;
        font-size: 13px;
      }

      .del-btn {
        opacity: 0;
      }
    }
  }
}
</style>
