<template>
  <div class="blossom-home-root">
    <div class="home-header">
      <IndexHeader></IndexHeader>
    </div>
    <div class="home-main">
      <div class="home-main-userinfo">
        <UserInfo></UserInfo>
      </div>
      <div class="home-main-right">
        <div class="home-main-right-chart">
          <ChartLineWords ref="ChartLineWordsRef"></ChartLineWords>
        </div>
        <div class="home-main-right-subject">
          <HomeSubject></HomeSubject>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue"
import IndexHeader from './IndexHeader.vue'
import UserInfo from './HomeUserInfo.vue'
import ChartLineWords from "./ChartLineWords.vue"
import HomeSubject from "./HomeSubject.vue"

const ChartLineWordsRef = ref()
onMounted(() => {
  ChartLineWordsRef.value.reload()
})
</script>

<style scoped lang="scss">
.blossom-home-root {
  @include box(100%, 100%);
  @include flex(row, center, center);
  box-shadow: 0 0 10px #24272c;
  background-image: linear-gradient(to bottom right, #3e464e, #212121);
  position: relative;
  overflow: hidden;

  .home-header {
    @include box(100%, 60px);
    position: absolute;
    top: 0;
    left: 0;
  }

  .home-main {
    @include box(100%, 100%);
    padding-top: 60px;
    @include flex(row, flex-start, center);

    .home-main-userinfo {
      @include box(600px, 100%);
    }

    .home-main-right {
      @include box(calc(100% - 600px), 100%);
      @include flex(column, flex-start, center);
      padding-right: 20px;

      .home-main-right-chart {
        @include box(100%, 40%);
      }

      .home-main-right-subject {
        @include box(100%, 60%);
      }
    }
  }


  @media screen and (max-width: 1100px) {
    .home-main {

      .home-main-userinfo {
        width: 100%;
      }

      .home-main-right {
        display: none;
      }
    }
  }

}
</style>