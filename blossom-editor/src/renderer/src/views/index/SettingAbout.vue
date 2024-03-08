<template>
  <div class="about-root">
    <div class="project-name">
      <div>Blossom</div>
      <div class="version">Client {{ CONFIG.SYS.VERSION + getServerVersion() }}</div>
    </div>

    <bl-col height="fit-content">
      <bl-row just="center" class="about-item" height="50px" width="700px">
        <div class="iconbl bl-books-line"></div>
        <bl-col just="center" align="flex-start" height="100%" width="400px">
          <div class="title">官方帮助文档</div>
          <div class="desc">查看 Blossom 功能介绍，了解关于项目的更新信息，开发进度以及未来规划的功能。</div>
        </bl-col>
        <div class="btns">
          <el-button size="default" style="width: 55px" @click="openExtenal(CONFIG.SYS.DOC)"> 浏览 </el-button>
        </div>
      </bl-row>

      <bl-row just="center" class="about-item" height="50px" width="700px">
        <div class="iconbl bl-visit"></div>
        <bl-col just="center" align="flex-start" height="100%" width="400px">
          <div class="title">加入群聊</div>
          <div class="desc">和更多 Blossom 用户一起沟通交流，了解最新更新内容，说出你想要的功能！</div>
        </bl-col>
        <div class="btns">
          <el-button size="default" style="width: 55px" @click="openExtenal(CONFIG.SYS.CONTACT)"> 前往 </el-button>
        </div>
      </bl-row>

      <bl-row just="center" class="about-item" height="50px" width="700px">
        <div class="iconbl bl-github"></div>
        <bl-col just="center" align="flex-start" height="100%" width="400px">
          <div class="title">查看源码</div>
          <div class="desc">Blossom 是一个开源项目，公开所有源代码。你可以加入社区反馈问题，参与开发。</div>
        </bl-col>
        <div class="btns">
          <el-button style="width: 55px" @click="openExtenal(CONFIG.SYS.GITHUB_REPO)">Github</el-button>
          <el-button style="width: 55px; margin: 3px 0 0 0" @click="openExtenal(CONFIG.SYS.GITEE_REPO)">Gitee</el-button>
        </div>
      </bl-row>

      <bl-row just="center" class="about-item" height="50px" width="700px">
        <div class="iconbl">❤️</div>
        <bl-col just="center" align="flex-start" height="100%" width="400px">
          <div class="title">赞助项目</div>
          <div class="desc">
            Blossom 的设计、开发、测试等工作需要耗费大量的精力。开源项目难以维持生计，如果你觉得项目还不错，可以赞助开发者来支持该项目。
          </div>
        </bl-col>
        <div class="btns">
          <el-button size="default" style="width: 55px" @click="openExtenal(CONFIG.SYS.SPONSOR)"> 前往 </el-button>
        </div>
      </bl-row>
    </bl-col>

    <bl-col just="center" height="fit-content">
      <div class="blod" style="margin: 80px 0 10px 0">开发者列表</div>
      <div class="developer">
        <bl-row class="item" v-for="dever in developer" width="250px" height="70px" @click="toView(dever.github)">
          <div>
            <img :src="dever.avatar" />
          </div>
          <bl-col just="flex-start" align="flex-start">
            <div class="name">
              {{ dever.name }}
            </div>
            <div class="desc">
              {{ dever.desc }}
            </div>
          </bl-col>
        </bl-row>
      </div>
    </bl-col>

    <bl-row just="center">
      <div class="reference">
        <div class="blod">三方引用:</div>
        <ol>
          <li v-for="ref in references">
            <bl-row>
              <span style="width: 180px">{{ ref.name }}</span
              >: <a :href="ref.url">{{ ref.url }}</a>
            </bl-row>
          </li>
        </ol>
      </div>
    </bl-row>

    <bl-row just="center" class="statement"> 本应用完全免费并开源全部源代码，如果你从付费渠道获取本应用，谨防上当受骗。 </bl-row>
  </div>
</template>

<script setup lang="ts">
import CONFIG from '@renderer/assets/constants/system'
import { openExtenal } from '@renderer/assets/utils/electron'
import { toView } from '@renderer/assets/utils/util'
import { useUserStore } from '@renderer/stores/user'

const userStore = useUserStore()

const references = [
  { name: '基础icon(作者:宗伟)', url: 'https://www.iconfont.cn/collections/detail?cid=35578' },
  { name: 'Microsoft Fluentui Emoji', url: 'https://github.com/microsoft/fluentui-emoji' },
  { name: 'JetBrains Mono', url: 'https://www.jetbrains.com/lp/mono/' }
]

const developer = [
  {
    name: '小贼贼子',
    desc: '创建者',
    github: 'https://github.com/xiaozzzi',
    avatar: 'https://www.wangyunf.com/bl/pic/home/bl/img/U1/head/luban.png'
  },
  {
    name: 'Tianjiu',
    desc: '项目成员、英文译者',
    github: 'https://github.com/T1anjiu',
    avatar: 'https://www.wangyunf.com/bl/pic/home/bl/img/U1/pic/blosteam/T1anjiu.jpg'
  }
]

const getServerVersion = () => {
  if (userStore.sysParams && userStore.sysParams.SERVER_VERSION) {
    return ' | Server v' + userStore.sysParams.SERVER_VERSION.replaceAll('-SNAPSHOT', '')
  }
  return ''
}
</script>

<style scoped lang="scss">
.about-root {
  @include box(100%, 100%);
  padding: 0 30px 130px 30px;
  overflow: scroll;
  color: var(--bl-text-color);

  .project-name {
    @include font(50px, 500);
    @include themeText(2px 2px 5px #878787, 2px 2px 5px #000000);
    color: var(--el-color-primary);
    width: 100%;
    text-align: center;

    .version {
      @include font(12px, 300);
      color: var(--bl-text-color-light);
      text-shadow: none;
    }
  }

  .repository {
    margin-top: 10px;
    @include themeFilter(drop-shadow(0 0 3px rgb(184, 184, 184)), drop-shadow(0 0 3px rgb(5, 5, 5)));

    .github,
    .gitee {
      width: 25px;
      height: 25px;
      cursor: pointer;
    }
  }

  .about-item {
    margin-top: 40px;
    .iconbl {
      width: 80px;
      font-size: 40px;
      color: var(--bl-text-color-light);
      text-align: center;
    }

    .title {
      font-size: 16px;
    }

    .desc {
      @include font(13px, 300);
      color: var(--bl-text-color-light);
    }

    .btns {
      @include flex(column, center, center);
      width: 70px;
    }
  }

  .blod {
    color: var(--el-color-primary);
    font-weight: bold;
  }

  .developer {
    @include flex(row, flex-start, flex-start);
    width: 630px;
    align-content: flex-start;
    flex-wrap: wrap;
    cursor: pointer;

    .item {
      background-color: var(--bl-bg-color);
      margin: 0 15px 15px 0;
      padding: 5px;
      border-radius: 10px;

      img {
        @include box(50px, 50px);
        border-radius: 50%;
        margin-right: 10px;
      }

      .name {
        font-size: 14px;
        height: 25px;
      }

      .desc {
        @include font(12px, 300);
        overflow: auto;
      }
    }
  }

  .reference {
    width: 630px;
    font-size: 12px;
    padding: 10px;
    margin-top: 50px;
    border: 1px dashed #8b8b8b;
    border-radius: 5px;
    color: #8b8b8b;
  }

  .statement {
    font-size: 13px;
    margin-top: 20px;
    color: var(--bl-text-color-light);
  }
}
</style>
