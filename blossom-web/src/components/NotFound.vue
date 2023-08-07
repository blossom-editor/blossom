<template>
  <div class="notfound-root">
    <div class="notfount">
      404
    </div>
    <div class="desc">
      未找到该页面, 您可能想前往
    </div>
    <div class="router">
      <div class="item" v-for="link in SYSTEM.LINKS" @click="toView(link.URL)">
        <img :src="getImg(link.LOGO)" style="width: 25px;">{{ link.NAME }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import SYSTEM from '@/assets/constants/blossom'
import { toView } from '@/assets/utils/util'

const getImg = (img: string) => {
  return new URL(`../assets/imgs/linklogo/${img}`, import.meta.url).href
}
</script>

<style scoped lang="scss">
.notfound-root {
  @include box(100%, 100%);
  @include flex(column, center, center);
  background-image: linear-gradient(to bottom right, #3e464e, #0c0c11);


  .notfount {
    @include font(260px, 700);
    color: #fff;
  }

  .desc {
    @include font(30px, 300);
    color: rgb(88, 88, 88);
  }

  .router {
    color: rgb(88, 88, 88);

    img {
      margin-right: 5px;
    }

    .item {
      @include flex(row, flex-start, center);
      margin-top: 10px;
      cursor: pointer;
    }
  }
}
</style>
