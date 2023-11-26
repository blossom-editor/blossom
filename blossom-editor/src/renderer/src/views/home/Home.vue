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

      <!-- 统计 -->
      <div class="chart-container">
        <bl-row class="container-name">字数统计</bl-row>
        <bl-row height="270px">
          <!-- 字数图表 -->
          <bl-col width="670px">
            <bl-row class="container-sub-name">
              The last 36 months
              <span class="iconbl bl-refresh-smile container-refresh" @click="loadWordLine"></span>
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
          <span class="iconbl bl-refresh-smile container-refresh" @click="loadSentinlLine"></span>
        </bl-row>
        <bl-row width="880px" height="250px">
          <SentinelChartLine ref="SentinelChartLineRef"></SentinelChartLine>
        </bl-row>

        <!-- 热力图 -->
        <bl-row class="container-name">编辑热力图</bl-row>
        <bl-row class="container-sub-name">
          每日编辑文章数 (每5分钟更新)
          <span class="iconbl bl-refresh-smile container-refresh" @click="loadArticleHeapmap"></span>
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
    <div class="middle">
      <div style="height: 45px"></div>

      <bl-col width="100%" height="270px">
        <ArticleStars></ArticleStars>
      </bl-col>

      <bl-col width="100%" height="330px">
        <ArticleSubjects></ArticleSubjects>
      </bl-col>

      <bl-col width="100%" height="calc(100% - 45px - 270px - 330px)">
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
    <div class="web-container gradient-linear">
      <WebCollect></WebCollect>
    </div>
  </div>

  <el-dialog
    v-model="isShowWordsInfoDialog"
    width="80%"
    top="100px"
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

onActivated(() => {
  now.value = nowWhen()
})

const loadWordLine = () => {
  ChartLineWordsRef.value.reload()
}

const loadArticleHeapmap = () => {
  ChartHeatmapRef.value.reload()
}

const loadSentinlLine = () => {
  SentinelChartLineRef.value.reload()
}

const now = ref(nowWhen())

//#region ----------------------------------------< panin store >--------------------------------------
const isShowWordsInfoDialog = ref(false)

const showWordsInfo = () => {
  isShowWordsInfoDialog.value = !isShowWordsInfoDialog.value
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
  $width-middle: calc(
    100% - /* border */ #{$border-middle} - /* margin : $margin-web */ 0px - /* margin : $margin-middle */ 10px - /* width */ #{$width-main} - #{$width-web}
  );

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
      text-shadow: var(--bl-text-shadow);
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
      overflow-y: overlay;
    }
  }

  .middle {
    @include box($width-middle, 100%, $width-middle, $width-middle);
    position: relative;
    display: none;
    z-index: 2;
  }

  .web-container {
    @include flex(column, space-between, center);
    @include box($width-web, calc(100% - 60px));
    margin: $margin-web;
    position: relative;
    border-top-left-radius: 10px;
    border-bottom-left-radius: 10px;
    box-shadow: var(--bl-box-shadow);
    overflow: hidden;
    z-index: 2;
  }

  /** 小于1440时 */
  @media screen and (max-width: 1140px) {
    .web-container {
      opacity: 0;
    }
  }

  @media screen and (min-width: 1600px) {
    .middle {
      @include themeBorder(1px, #e6e6e6, #171717, 'left');
      display: block;
      padding: 0 10px 10px 20px;
    }
  }

  @keyframes rotation {
    0% {
      transform: rotate(0deg);
    }

    100% {
      transform: rotate(-360deg);
    }
  }

  .gradient-linear {
    --color1: #f6f6f6;
    --color2: #ffffff5a;

    [class='dark'] & {
      --color1: #151515c1;
      --color2: #00000014;
    }

    background: linear-gradient(135deg, var(--color1) 25%, var(--color2) 0, var(--color2) 50%, var(--color1) 0, var(--color1) 75%, var(--color2) 0);
    background-size: 60px 60px;
    animation: alwaysToLeftBottom 120s linear infinite;
  }

  @keyframes alwaysToLeftBottom {
    to {
      background-position: 300%;
    }
  }
}
</style>
