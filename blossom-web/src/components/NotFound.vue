<template>
  <div class="notfound-root">
    <div class="notfount">404</div>
    <div class="desc">您可能想前往....</div>
    <div class="router">
      <div class="item" @click="toRoute('/home')"><span class="iconbl bl-a-home1-line"></span>回到首页</div>
      <div class="item" v-for="link in getLinks()" @click="toView(link.URL)"><img :src="link.LOGO" style="width: 25px" />{{ link.NAME }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { toView } from '@/assets/utils/util'
import { toRoute } from '@/router'
import { getLinks } from '@/scripts/env'
</script>

<style scoped lang="scss">
.notfound-root {
  @include box(100%, 100%);
  @include flex(column, center, center);
  background-image: linear-gradient(to right bottom, rgb(62, 70, 78), rgb(33, 33, 33));

  .notfount {
    @include font(260px, 700);
    color: #fff;
  }

  .desc {
    @include font(30px, 300);
    color: #9b9b9b;
    padding-bottom: 10px;
  }

  .router {
    font-weight: 300;
    color: #9b9b9b;

    img {
      margin-right: 5px;
    }

    .item {
      @include flex(row, flex-start, center);
      margin-top: 10px;
      height: 35px;
      cursor: pointer;

      img {
        margin: 5px 10px 5px 0;
      }

      .iconbl {
        font-size: 30px;
        padding-right: 5px;
        color: #9b9b9b;
      }
    }
  }
}
</style>
