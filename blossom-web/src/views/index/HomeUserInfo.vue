<template>
  <div class="home-userinfo-root">
    <div class="userinfo-desc">
      <div class="avatar" v-if="userStore.userinfo.avatar !== ''">
        <img :src="userStore.userinfo.avatar" alt="头像" />
      </div>
      <div class="info">
        <div class="name">{{ userStore.userinfo.nickName }}</div>
        <div class="desc">{{ userStore.userinfo.remark }}</div>
        <div class="desc">Doc:{{ userStore.userinfo.articleCount }} | Words:{{ userStore.userinfo.articleWords }}</div>
      </div>
    </div>

    <div class="userinfo-content">
      <div class="userinfo-content-btns">
        <ul>
          <li @click="toRoute('/articles')">所有文章 <span class="iconbl bl-sendmail-line"></span></li>
          <li v-for="link in links()" @click="toView(link.URL)">
            {{ link.NAME }}
          </li>
        </ul>
      </div>

      <!-- 操作按钮及图标 -->
      <div class="userinfo-content-charts">
        <ChartHeatmap></ChartHeatmap>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { toRoute } from '@/router'
import { useUserStore } from '@/stores/user'
import { toView } from '@/assets/utils/util'
import { getLinks } from '@/scripts/env'
import { isNotBlank } from '@/assets/utils/obj'
import ChartHeatmap from './ChartHeatmap.vue'

const userStore = useUserStore()

const links = () => {
  if (isNotBlank(userStore.links)) {
    return JSON.parse(userStore.links)
  } else {
    getLinks()
  }
}
</script>

<style scoped lang="scss">
.home-userinfo-root {
  @include box(100%, 100%);
  @include flex(column, center, center);
  font-family: 'Jetbrains Mono';

  .userinfo-desc {
    @include flex(row, flex-start, flex-end);

    .avatar {
      img {
        @include box(100px, 100px);
        box-shadow: -3px -3px 5px #585858, 5px 5px 10px 1px #000000, 9px 9px 10px 1px #2b2b2b, 15px 15px 10px 1px #414141;
        border-radius: 10px;
        object-fit: cover;
      }
    }

    .info {
      text-align: left;
      padding-left: 20px;

      .name {
        @include font(38px, 300);
        height: 65px;
        color: #ffffff;
        letter-spacing: 5px;
        text-shadow: 3px 3px 5px rgb(24, 24, 24);
      }

      .desc {
        @include font(13px, 300);
        height: 20px;
        color: #7a7a7a;
        letter-spacing: 1px;
      }
    }
  }

  .userinfo-content {
    padding-top: 5px;
    text-align: center;

    // 操作按钮
    .userinfo-content-btns {
      @include flex(row, flex-start, center);
      margin-left: 30px;
      margin-top: 10px;

      ol,
      ul {
        @include font(15px, 300);
        color: #7a7a7a;
        text-align: left;

        li {
          transition: color 1s;
          cursor: pointer;
          line-height: 25px;

          &:hover {
            color: #f3f3f3;
            text-shadow: 3px 3px 10px #cccccc;
          }
        }
      }
    }

    &-charts {
      margin-left: -10px;
    }
  }
}
</style>
