<template>
  <div class="home-userinfo-root">
    <div class="userinfo-desc">
      <div class="avatar" v-if="userinfo.avatar !== ''">
        <img :src="userinfo.avatar" alt="头像" />
      </div>
      <div class="info">
        <div class="name">{{ userinfo.nickName }}</div>
        <div class="desc">{{ userinfo.remark }}</div>
        <div class="desc">Doc:{{ userinfo.articleCount }} | Words:{{ userinfo.articleWords }}</div>
      </div>
    </div>

    <div class="userinfo-content">

      <!-- 操作按钮 -->
      <div class="userinfo-content-btns">
        <ul>
          <li @click="() => { toRoute('/articles') }">专题文章</li>
          <li v-for="link in SYSTEM.LINKS" @click="toView(link.URL)">
            {{ link.NAME }}
          </li>
        </ul>
      </div>

      <!-- 操作按钮及图标 -->
      <div class="userinfo-content-charts">
        <ChartHeatmap></ChartHeatmap>
      </div>

    </div>

    <div class="userinfo-footer">
      <div class="about-us">
        <span>{{ '版本：' + blossom.SYS.VERSION + ' | 邮箱：' + blossom.SYS.EMAIL }}</span>
      </div>
      <div class="icp">
        <div style="cursor: pointer" @click="openNew('http://www.beian.gov.cn/portal/index.do')">
          <img style="height: 14px;width: 14px;" src="@/assets/imgs/common/gong_wang_an_bei_img.png">
          {{ blossom.SYS.GONG_WANG_AN_BEI }}
        </div>
        <div>|</div>
        <div style="cursor: pointer" @click="openNew('https://beian.miit.gov.cn/')">
          {{ blossom.SYS.ICP_BEI_AN_HAO }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { toRoute } from '@/router'
import { onMounted, ref } from "vue"
import { userinfoApi } from '@/api/blossom'
import blossom from '@/assets/constants/blossom'
import ChartHeatmap from "./ChartHeatmap.vue"
import SYSTEM from '@/assets/constants/blossom'
import { toView } from '@/assets/utils/util'

const userinfo = ref({
  avatar: '',
  nickName: '小贼贼子',
  remark: 'Java | Js | Ts',
  articleCount: '200',
  articleWords: '20000'
})

const openNew = (url: string) => {
  window.open(url)
}

onMounted(() => {
  userinfoApi().then(resp => {
    userinfo.value = resp.data
  })
})

</script>

<style scoped lang="scss">
.home-userinfo-root {
  @include box(100%, 100%);
  @include flex(column, flex-start, center);


  .userinfo-desc {
    height: 40%;
    @include flex(row, flex-start, flex-end);

    .avatar {
      img {
        @include box(100px, 100px);
        box-shadow:
          -3px -3px 5px #585858,
          5px 5px 10px 1px #000000,
          9px 9px 10px 1px #2B2B2B,
          15px 15px 10px 1px #414141;
        border-radius: 10px;
        object-fit: cover;
      }
    }

    .info {
      text-align: left;
      padding-left: 20px;

      .name {
        @include font(38px, 200);
        height: 65px;
        color: #ffffff;
        letter-spacing: 5px;
        text-shadow: 3px 3px 5px rgb(24, 24, 24);
      }

      .desc {
        @include font(13px, 500);
        height: 20px;
        color: #7a7a7a;
        font-family: monospace, sans-serif;
        letter-spacing: 1px;
      }
    }
  }

  .userinfo-content {
    height: 60%;
    padding-top: 5px;
    text-align: center;

    // 操作按钮
    .userinfo-content-btns {
      @include flex(row, flex-start, center);
      margin-left: 30px;
      margin-top: 10px;

      ol,
      ul {
        @include font(15px, 500);
        color: #7a7a7a;
        text-align: left;

        li {
          transition: color 1s;
          cursor: pointer;
          line-height: 25px;

          &:hover {
            color: #F3F3F3;
            text-shadow: 3px 3px 10px #cccccc;
          }
        }

      }
    }

    &-charts {
      margin-left: -10px;
    }

  }

  .userinfo-footer {
    @include box(100%, 40px);

    .icp,
    .about-us {
      @include flex(row, center, center);
    }

    div {
      @include flex(row, center, center);
      color: #6d6d6d;
      font-size: 9px;
      transform: scale(0.8, 0.8);
      white-space: pre;
    }
  }

}
</style>