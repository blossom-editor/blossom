<template>
  <div :class="['blossom-header-root', props.bg ? 'blossom-header-bg' : '']">
    <div class="blossom-logo" @click="() => { toRoute('/home') }">
      <img src="@/assets/imgs/blossom/blossom_logo.png" />
    </div>
    <div class="project-name" @click="() => { toRoute('/home') }">
      Blossom
    </div>
    <div class="more-menu">
      <button class="menu-dropdown">更多</button>
      <div class="dropdown-content">
        <div class="dropdown-item" v-for="link in SYSTEM.LINKS" @click="toView(link.URL)">
          <img :src="getImg(link.LOGO)" style="width: 25px;">{{ link.NAME }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { toRoute } from '@/router'
import SYSTEM from '@/assets/constants/blossom'
import { toView } from '@/assets/utils/util'

const props = defineProps({
  bg: {
    type: Boolean,
    default: false
  }
})

const getImg = (img: string) => {
  return new URL(`../../assets/imgs/linklogo/${img}`, import.meta.url).href
}
</script>

<style scoped lang="scss">
.blossom-header-root {
  @include box(100%, 60px);
  @include flex(row, flex-start, center);
  padding: 10px 10px;
  z-index: 2003;

  .blossom-logo {
    @include box(40px, 40px);
    cursor: pointer;

    img {
      @include box(40px, 40px);
    }
  }

  .project-name {
    @include box(100px, 100%);
    margin-left: 10px;
    padding: 10px 0;
    text-shadow: 3px 3px 5px #ABABAB;
    cursor: pointer;

    color: transparent;
    font-family: current, sans-serif;
    letter-spacing: 1px;
    background: linear-gradient(90deg,
        #313131,
        #fff3fc,
        #313131);
    -webkit-background-clip: text;
    animation: glow 10s linear infinite;
    transition: 1.5s;
    background-size: 300%;

    @keyframes glow {
      to {
        background-position: -300%;
      }
    }
  }

  .more-menu {
    @include box(50px, 100%);
    font-size: 15px;
    color: #909090;

    .menu-dropdown {
      @include box(100%, 100%);
      padding: 5px;
      margin-top: 3px;
      color: #ABABAB;
      border: 0;
      background-color: rgba(0, 0, 0, 0);
      text-align: center;
      overflow: hidden;
      transition: 0.3s;
      cursor: pointer;

      &:hover {
        font-weight: bold;
        border-radius: 5px;
        color: #E8E8E8;
        text-shadow: 3px 3px 10px #cccccc;
      }
    }

    .menu-dropdown:hover+.dropdown-content {
      display: block;
    }

    /* 为下拉内容设置样式（默认隐藏）*/
    .dropdown-content {
      @include flex(column, flex-start, flex-start);
      min-width: 160px;
      display: none;
      position: absolute;
      top: 50px;
      padding: 5px 0 10px 0;
      border-radius: 5px;
      box-shadow: 5px 8px 16px 0px rgba(0, 0, 0, 1);
      background-color: #353b40;
      transition: 0.3s;
      z-index: 1;

      &:hover {
        display: block;
      }

      .dropdown-item {
        @include flex(row, flex-start, center);
        padding: 7px 15px 7px 10px;
        border-radius: 5px;
        transition: 0.3s;
        white-space: pre-line;
        cursor: pointer;

        img {
          margin-right: 5px;
        }

        &:hover {
          font-weight: bold;
          color: #E8E8E8;
          text-shadow: 3px 3px 10px #cccccc;

          ~.dropdown-content {
            display: block;
          }
        }
      }

      .dropdown-item:first-child {
        margin-top: 0;
      }

    }

  }
}

.blossom-header-bg {
  background-image: linear-gradient(to bottom right, #3e464e, #212121);
  box-shadow: 0 1px 8px 1px #212121;
}
</style>