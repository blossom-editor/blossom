<template>
  <div class="global-home-root">
    <div class="main">
      <bl-row class="greetings">
        Good {{ now }}.
      </bl-row>
      <!--  -->
      <bl-row align="flex-end" height="250px">
        <div class="image-container">
          <div class="now-time">
            <DateLine></DateLine>
          </div>
          <div class="user-name">{{ userinfo.nickName }}</div>
          <Laptop></Laptop>
        </div>
        <!-- 天气 -->
        <Weather></Weather>
        <!-- 头像 -->
        <UserAvatar style="margin-left: 20px;"></UserAvatar>
      </bl-row>

      <!-- 统计 -->
      <bl-row class="container-name">字数统计</bl-row>
      <bl-row width="100%" height="270px">
        <!-- 字数图表 -->
        <bl-col width="calc(100% - 200px)">
          <bl-row class="container-sub-name">
            Article Words
            <span class="iconbl bl-refresh-smile container-refresh" @click="loadWordLine"></span>
          </bl-row>
          <ChartLineWords ref="ChartLineWordsRef"></ChartLineWords>
        </bl-col>
        <!-- 统计卡片 -->
        <bl-col width="200px" style="margin-left: 20px;">
          <bl-row class="container-sub-name">Blossom Statistic</bl-row>
          <StatisticCard></StatisticCard>
        </bl-col>
      </bl-row>

      <!-- 流量统计 -->
      <bl-row class="container-name">流量</bl-row>
      <bl-row class="container-sub-name">
        Flow Statistic / Requests & Average Response Time(ms)
        <span class="iconbl bl-refresh-smile container-refresh" @click="loadSentinlLine"></span>
      </bl-row>
      <bl-row width="880px" height="250px">
        <ChartLineSentinel ref="ChartLineSentinelRef"></ChartLineSentinel>
      </bl-row>

      <!-- 热力图 -->
      <bl-col class="heatmap-container" width="100%" style="height: 330px;">
        <bl-row class="container-name">编辑热力图</bl-row>
        <bl-row class="container-sub-name">
          每日编辑文章数 (每5分钟更新)
          <span class="iconbl bl-refresh-smile container-refresh" @click="loadArticleHeapmap"></span>
        </bl-row>
        <bl-row style="height: calc(100% - 60px - 20px);">
          <ChartHeatmap ref="ChartHeatmapRef"></ChartHeatmap>
        </bl-row>
      </bl-col>

    </div>

    <!-- 
      =======================================================
      middle
      =======================================================
     -->

    <div class="middle">
      <div style="height: 45px;">
      </div>

      <bl-col width="100%" height="270px">
        <bl-row class="container-name">收藏/关注</bl-row>
        <bl-row class="container-sub-name">Article Star ⭐</bl-row>
        <ArticleStars></ArticleStars>
      </bl-col>

      <bl-col width="100%" height="330px">
        <bl-row class="container-name">专题</bl-row>
        <bl-row class="container-sub-name">Article Subjects</bl-row>
        <ArticleSubjects></ArticleSubjects>
      </bl-col>

      <bl-col width="100%" height="calc(100% - 45px - 270px - 330px)">
        <bl-row class="container-name">便签</bl-row>
        <bl-row class="container-sub-name">记录瞬间的灵感 (Ctrl+Enter 快速保存)</bl-row>
        <bl-row style="padding:20px 10px 30px 10px;height: calc(100% - 80px);text-align: right;">
          <NoteEditor></NoteEditor>

          <!-- <textarea type="textarea" style="height: 100%;width:  100%;;background-color: #ffffff00;"></textarea> -->
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


    <bl-row style="position: absolute;bottom: 0;z-index: 1;height: 100px;">
      <WaveFooter></WaveFooter>
    </bl-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onActivated } from "vue"
import { storeToRefs } from 'pinia'
import { useUserStore } from '@renderer/stores/user'
// components
import Laptop from '@renderer/components/Laptop.vue'
import DateLine from '@renderer/components/DateLine.vue'
import WebCollect from '@renderer/components/WebCollect.vue'
import UserAvatar from "@renderer/components/UserAvatar.vue"
import Weather from "@renderer/components/Weather.vue"
// charts
import ChartLineWords from "./ChartLineWords.vue"
import ChartLineSentinel from "./ChartLineSentinel.vue"
import ChartHeatmap from "./ChartHeatmap.vue"
// articles
import ArticleSubjects from "./ArticleSubjects.vue"
import ArticleStars from "./ArticleStars.vue"
import StatisticCard from './StatisticCard.vue'
import NoteEditor from '@renderer/views/note/NoteEditor.vue'
import WaveFooter from "@renderer/components/WaveFooter.vue"

import { nowWhen } from "@renderer/assets/utils/util"

const userStore = useUserStore()
const { userinfo } = storeToRefs(userStore)

const ChartLineWordsRef = ref()
const ChartLineSentinelRef = ref()
const ChartHeatmapRef = ref()

onActivated(() => {
  now = nowWhen()
})

const loadWordLine = () => {
  ChartLineWordsRef.value.reload()
}

const loadSentinlLine = () => {
  ChartLineSentinelRef.value.reload()
}

const loadArticleHeapmap = () => {
  ChartHeatmapRef.value.reload()
}

let now: string = nowWhen()

</script>

<style scoped lang="scss">
.global-home-root {
  @include box(100%, 100%);
  @include flex(row, space-between, center);
  background-image: linear-gradient(to bottom right,
      var(--xz-html-color),
      var(--xz-html-color),
      var(--el-color-primary-light-7));

  .container-name {
    @include font(20px, 700);
    @include themeColor(#5C5C5C, #a3a6ad);
    height: 60px;
    min-height: 60px;
    align-items: end !important;
    text-shadow: var(--xz-text-shadow);
  }

  .container-sub-name {
    height: 20px;
    min-height: 20px;
    @include font(13px, 300);
    @include themeColor(#ABABAB, #7E7E7E);
    text-shadow: var(--xz-text-shadow);
  }

  .container-refresh {
    font-size: 20px;
    margin-left: 10px;
    cursor: pointer;

    &:hover {
      animation: rotation 4s linear infinite;
      text-shadow: var(--xz-text-shadow);
    }
  }

  $border-middle: 1px;

  $margin-web: 30px 0 50px 0px;
  $margin-middle: 10px;

  $width-main: 900px;
  $width-web: 420px;
  $width-middle: calc(100% -
      /* border */
      #{$border-middle} -
      /* margin : $margin-web */
      0px -
      /* margin : $margin-middle */
      10px -
      /* width */
      #{$width-main} - #{$width-web});

  .main {
    @include box($width-main, 100%, $width-main, $width-main);
    padding: 0 10px 10px 20px;
    $height-greetings: 65px;
    z-index: 2;

    // 笔记本图片等
    .image-container {
      @include box(200px, 100%);

      .user-name {
        @include font(25px, 700);
        @include themeColor(#5C5C5C, var(--el-color-primary));
        text-shadow: var(--xz-text-shadow);
        height: 30px;
      }

      [class="dark"] & {
        filter: brightness(80%);
      }
    }

    .greetings {
      height: $height-greetings;
      @include font(50px, 700);
      @include themeColor(#5C5C5C, var(--el-color-primary));
      text-shadow: var(--xz-text-shadow);
    }

    .now-time {
      height: 20px;
      @include themeColor(#5C5C5C, var(--el-color-primary));
      text-shadow: var(--xz-text-shadow);
      font-size: 12px;
    }

    .heatmap-container {
      opacity: 0;
    }
  }


  .middle {
    @include box($width-middle, 100%, $width-middle, $width-middle);
    position: relative;
    transition: 0.2s;
    opacity: 0;
    z-index: 2;
  }

  .web-container {
    @include flex(column, space-between, center);
    @include box($width-web, calc(100% - 60px));
    margin: $margin-web;
    position: relative;
    border-top-left-radius: 10px;
    border-bottom-left-radius: 10px;
    box-shadow: var(--xz-box-shadow);
    overflow: hidden;
    z-index: 2;
  }


  /** 小于1440时 */
  @media screen and (max-width:1140px) {
    .web-container {
      opacity: 0;
    }
  }

  @media screen and (min-height: 1120px) {
    .main {
      .heatmap-container {
        opacity: 1 !important;
      }
    }
  }

  // 大于1600时使用以下样式
  @media screen and (min-width: 1600px) {

    .middle {
      @include themeBorder(1px, #E6E6E6, #171717, 'left');
      opacity: 1 !important;
      margin-left: $margin-middle;
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
    --color1: rgba(234, 224, 254, 0.1);
    --color2: #FFFFFF5A;

    [class="dark"] & {
      --color1: #89991109;
      --color2: #89991114;
    }

    background: linear-gradient(135deg,
      var(--color1) 25%,
      var(--color2) 0,
      var(--color2) 50%,
      var(--color1) 0,
      var(--color1) 75%,
      var(--color2) 0);
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