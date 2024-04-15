<template>
  <div class="global-home-root">
    <div class="main">
      <bl-row class="greetings" height="65px"> Good {{ now }}. </bl-row>
      <!--  -->
      <bl-row align="flex-end" height="250px">
        <div class="image-container">
          <div class="now-time">
            <DateLine></DateLine>
          </div>
          <div class="user-name">{{ userinfo.nickName }}</div>
          <Laptop></Laptop>
        </div>
        <Weather></Weather>
        <UserAvatar style="margin-left: 20px"></UserAvatar>
      </bl-row>
      <div style="height: 10px"></div>
      <!-- 统计 -->
      <div class="chart-container">
        <bl-row class="container-name" style="height: 50px; min-height: 50px">字数统计</bl-row>
        <bl-row height="270px">
          <!-- 字数图表 -->
          <bl-col width="670px">
            <bl-row class="container-sub-name">
              The last 36 months
              <span class="iconbl bl-refresh-smile" @click="loadWordLine"></span>
              <span class="iconbl bl-statistic-line container-operator" @click="showWordsInfo"></span>
            </bl-row>
            <ChartLineWords ref="ChartLineWordsRef"></ChartLineWords>
          </bl-col>
          <!-- 统计卡片 -->
          <bl-col width="200px">
            <bl-row class="container-sub-name">Blossom Statistic</bl-row>
            <StatisticCard></StatisticCard>
          </bl-col>
        </bl-row>

        <!-- 流量统计 -->
        <bl-row class="container-name">请求流量</bl-row>
        <bl-row class="container-sub-name">
          Flow Statistic / Requests & Average Response Time(ms)
          <span class="iconbl bl-refresh-smile" @click="loadSentinlLine"></span>
        </bl-row>
        <bl-row width="880px" height="250px">
          <SentinelChartLine ref="SentinelChartLineRef"></SentinelChartLine>
        </bl-row>

        <!-- 热力图 -->
        <bl-row class="container-name">编辑热力图</bl-row>
        <bl-row class="container-sub-name">
          每日编辑文章数 (每5分钟更新)
          <span class="iconbl bl-refresh-smile" @click="loadArticleHeapmap"></span>
        </bl-row>
        <bl-row width="870px" height="270px">
          <ChartHeatmap ref="ChartHeatmapRef"></ChartHeatmap>
        </bl-row>
      </div>
    </div>

    <!-- 
      =======================================================
      middle
      =======================================================
     -->
    <div
      :class="['middle', viewStyle.webCollectExpand ? 'expand' : 'fold']"
      :style="{ width: viewStyle.webCollectExpand ? 'calc(100% - 1px - 0px - 10px - 910px - 420px)' : 'calc(100% - 1px - 0px - 10px - 910px)' }">
      <div v-if="!viewStyle.webCollectExpand" class="web-show iconbl bl-left-line" @click="expand"></div>
      <div style="height: 15px"></div>

      <bl-col width="100%" height="300px">
        <ArticleStars></ArticleStars>
      </bl-col>

      <bl-col width="100%" height="330px">
        <ArticleSubjects></ArticleSubjects>
      </bl-col>

      <bl-col width="100%" height="calc(100% - 33px - 270px - 330px)">
        <bl-row class="container-name">待办事项</bl-row>
        <bl-row class="container-sub-name">Todo List</bl-row>
        <bl-row style="padding-bottom: 10px; height: calc(100% - 80px)">
          <TaskProgressSimpleVue></TaskProgressSimpleVue>
        </bl-row>
      </bl-col>
    </div>

    <!-- 
      =======================================================
      right
      =======================================================
     -->
    <div
      :class="[
        'web-container',
        viewStyle.webCollectExpand ? 'expand' : 'fold',
        viewStyle.isGlobalShadow ? 'web-container-heavy' : 'web-container-light'
      ]"
      :style="{ width: viewStyle.webCollectExpand ? '420px' : '0px', opacity: viewStyle.webCollectExpand ? 1 : 0 }">
      <div v-if="viewStyle.webCollectExpand" class="web-hide iconbl bl-right-line" @click="fold"></div>
      <WebCollect></WebCollect>
    </div>
  </div>

  <el-dialog
    v-model="isShowWordsInfoDialog"
    width="80%"
    :align-center="true"
    :append-to-body="true"
    :destroy-on-close="true"
    :close-on-click-modal="false"
    draggable>
    <WordsInfo></WordsInfo>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, onActivated } from 'vue'
import { storeToRefs } from 'pinia'
import { useUserStore } from '@renderer/stores/user'
import { useConfigStore } from '@renderer/stores/config'
// components
import Laptop from '@renderer/components/Laptop.vue'
import DateLine from '@renderer/components/DateLine.vue'
import WebCollect from '@renderer/components/WebCollect.vue'
import UserAvatar from '@renderer/components/UserAvatar.vue'
import Weather from '@renderer/components/Weather.vue'
// charts
import ChartLineWords from './ChartLineWords.vue'
import ChartHeatmap from './ChartHeatmap.vue'
import SentinelChartLine from '@renderer/views/statistic/SentinelChartLine.vue'
// articles
import ArticleSubjects from './ArticleSubjects.vue'
import ArticleStars from './ArticleStars.vue'
import StatisticCard from './StatisticCard.vue'
import TaskProgressSimpleVue from '../todo/TaskProgressSimple.vue'
import WordsInfo from './WordsInfo.vue'

import { nowWhen } from '@renderer/assets/utils/util'

const userStore = useUserStore()
const { userinfo } = storeToRefs(userStore)

const ChartLineWordsRef = ref()
const SentinelChartLineRef = ref()
const ChartHeatmapRef = ref()

onActivated(() => (now.value = nowWhen()))
const loadWordLine = () => ChartLineWordsRef.value.reload()
const loadArticleHeapmap = () => ChartHeatmapRef.value.reload()
const loadSentinlLine = () => SentinelChartLineRef.value.reload()
const now = ref(nowWhen())
//#region ----------------------------------------< 字数编辑 >--------------------------------------
const isShowWordsInfoDialog = ref(false)

const showWordsInfo = () => {
  isShowWordsInfoDialog.value = !isShowWordsInfoDialog.value
}
//#endregion

//#region ----------------------------------------< 网页收藏 >--------------------------------------
const config = useConfigStore()
const { viewStyle } = config
const expand = () => {
  viewStyle.webCollectExpand = true
  config.setViewStyle(viewStyle)
}
const fold = () => {
  viewStyle.webCollectExpand = false
  config.setViewStyle(viewStyle)
}
//#endregion
</script>

<style scoped lang="scss">
@import './styles/container.scss';
.global-home-root {
  @include box(100%, 100%);
  @include flex(row, space-between, center);

  $border-middle: 1px;

  $margin-web: 30px 0 50px 0px;
  $margin-middle: 10px;

  $width-main: 910px;
  $width-web: 420px;
  $width-middle: calc(100% - #{$border-middle} - 0px - 10px - #{$width-main} - #{$width-web});

  .main {
    @include box($width-main, 100%, $width-main, $width-main);
    padding: 0 5px 10px 20px;
    z-index: 2;

    // 笔记本图片等
    .image-container {
      @include box(200px, 100%);
      @include themeBrightness(100%, 80%);

      .user-name {
        @include font(25px, 700);
        @include themeColor(#5c5c5c, var(--el-color-primary));
        text-shadow: var(--bl-text-shadow);
        height: 30px;
      }
    }

    .greetings {
      @include font(50px, 700);
      @include themeColor(#5c5c5c, var(--el-color-primary));
      @include themeText(2px 3px 4px rgba(107, 104, 104, 0.5), 2px 3px 5px rgb(0, 0, 0));
    }

    .now-time {
      @include themeColor(#5c5c5c, var(--el-color-primary));
      height: 20px;
      text-shadow: var(--bl-text-shadow);
      font-size: 12px;
    }

    .chart-container {
      @include box(100%, calc(100% - 315px));
      overflow-x: hidden;
      overflow-y: scroll;
    }
  }

  .middle {
    height: 100%;
    position: relative;
    display: none;
    z-index: 2;
  }

  .web-container {
    @include flex(column, space-between, center);
    height: calc(100% - 60px);
    margin: $margin-web;
    position: relative;
    border-top-left-radius: 10px;
    border-bottom-left-radius: 10px;
    overflow: hidden;
    z-index: 2;
  }

  .web-container-heavy {
    border: 1px solid #00000000;
    box-shadow: var(--bl-box-shadow);
  }

  .web-container-light {
    @include themeBorder(1px, #e6e6e6, #171717);
  }

  .web-show,
  .web-hide {
    @include themeColor(#ababab, #7e7e7e);
    text-shadow: var(--bl-text-shadow);
    position: absolute;
    font-size: 20px;
    z-index: 2;
    transition: color 0.2s;
    cursor: pointer;

    &:hover {
      color: var(--el-color-primary);
    }
  }

  .web-show {
    right: 0;
    top: 30px;
  }

  .web-hide {
    left: 10px;
    top: 10px;
    z-index: 99;
  }

  /** 小于1440时 */
  @media screen and (max-width: 1140px) {
    .web-container {
      opacity: 0 !important;
    }
  }

  @media screen and (min-width: 1140px) {
    .middle:not(.expand) {
      @include themeBorder(1px, #e6e6e6, #171717, 'left');
      display: block;
      padding: 0 10px 10px 20px;
    }
  }

  // 大于1600时显示 middle
  @media screen and (min-width: 1600px) {
    .middle:not(.fold) {
      @include themeBorder(1px, #e6e6e6, #171717, 'left');
      display: block;
      padding: 0 10px 10px 20px;
    }
  }
}
</style>
