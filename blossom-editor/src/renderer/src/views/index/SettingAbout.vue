<template>
  <div class="about-root">
    <div class="project-name">Blossom</div>
    <bl-row class="repository" just="center">
      <bl-col @click="toView(CONFIG.SYS.GITHUB_REPO)">
        <svg class="github" aria-hidden="true"><use xlink:href="#wl-github2"></use></svg>
      </bl-col>

      <bl-col @click="toView(CONFIG.SYS.GITEE_REPO)" style="margin-left: 20px">
        <svg class="gitee" aria-hidden="true"><use xlink:href="#wl-gitee"></use></svg>
      </bl-col>
    </bl-row>

    <div class="bl-preview">
      <p class="paragraph">
        Blossom 是一个支持<span class="blod">私有部署</span
        >的云端存储双链笔记软件，你可以将你所有的笔记，图片，个人计划安排保存在自己的服务器中，并在任意设备之间实时同步，且基于MIT协议完全开源。
      </p>
      <p class="paragraph" style="margin-bottom: 0"><span class="blod">Blossom 具有以下优势:</span></p>
      <ol>
        <li><strong>不再为同步设备数量付费。</strong></li>
        <li><strong>不再为公网访问付费。</strong></li>
        <li>
          <strong>不再为软件付费</strong>，基于<a
            class="bl-tip bl-tip-bottom"
            data-tip="什么是 MIT 协议?"
            target="_blank"
            href="https://choosealicense.com/licenses/mit/"
            >MIT 协议</a
          >完全开源。
        </li>
        <li><strong>自带截图功能</strong><span class="blod">(仅windows)</span>，你不再需要使用其他截图工具截图后保存在本地再上传到文章了。</li>
        <li><strong>文章与图片都在你的服务器存储</strong>，不需要再使用任何三方图床，不需要购买任何对象存储。</li>
        <li><strong>没有任何私有协议</strong>，基于 Markdown 语法，采用约定格式拓展样式。迁移到其他软件可以正常显示。</li>
        <li><strong>多账号权限</strong>，可以和你的朋友一起使用。</li>
        <li><strong>一键导出</strong>，可以一键导出所有文章和图片，并将图片链接转换为图片路径，轻松转为本地笔记。</li>
        <li><strong>网页转换</strong>，可以一键将指定或全部文章转换为网页，方便分享。</li>
        <li><strong>丰富的功能拓展</strong>，包含日历计划，待办事项清单，一个完善的个人管理工具。</li>
        <li>你甚至可以只把 Blossom <strong>当做个人图床</strong>，并使用你自己的域名。</li>
      </ol>
      <p class="paragraph" style="margin-bottom: 0">
        <span class="blod">Blossom 具有以下功能:</span>
      </p>
      <ol>
        <li><strong>文章编辑：</strong>Markdown 文章编写，文章公网访问权限，文章分类管理。</li>
        <li><strong>双链笔记：</strong>内部文章与外部链接引用形成的双链笔记知识网络。</li>
        <li><strong>全量导出：</strong>每日全量备份，备份一键下载。</li>
        <li><strong>网页转换：</strong>将 Markdown 文章转换成网页，一键打包分享。</li>
        <li><strong>番茄时钟：</strong>编辑器包含一个番茄钟功能。</li>
        <li><strong>图片存储：</strong>按文章目录分类你的图片，或自定义图片目录，图片与文章双向查询。</li>
        <li><strong>自带截图：</strong>(仅windows)，你不再需要使用其他截图工具截图后保存在本地再上传到云端了。</li>
        <li><strong>待办清单：</strong>以 Todo List 的方式管理你的每日事项或阶段性计划。</li>
        <li><strong>计划安排：</strong>阶段性的长期计划，或者日历中的短期计划安排。</li>
        <li><strong>快捷便签：</strong>方便你记录随意的日常信息。</li>
        <li><strong>网站收藏：</strong>常用网站快捷访问，或者说是一个书签功能。</li>
        <li>笔记总字数折线图，笔记编辑数量热力图，服务器请求量折线图展示。</li>
      </ol>

      <p class="paragraph">
        更多内容，可前往<a :href="CONFIG.SYS.GITHUB_REPO" target="_blank">源码仓库</a>、<a :href="CONFIG.SYS.DOC" target="_blank">查看文档</a>、或<a
          :href="CONFIG.SYS.CONTACT"
          target="_blank"
          >联系作者</a
        >。
      </p>
    </div>

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

    <div class="blod" style="margin: 30px 0 10px 0">开发者列表</div>
    <div class="developer">
      <bl-row class="item" v-for="dever in developer" width="250px" height="70px">
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
  </div>
</template>

<script setup lang="ts">
import CONFIG from '@renderer/assets/constants/system'
import { toView } from '@renderer/assets/utils/util'

const references = [
  { name: '基础icon(作者:宗伟)', url: 'https://www.iconfont.cn/collections/detail?cid=35578' },
  { name: 'Microsoft Fluentui Emoji', url: 'https://github.com/microsoft/fluentui-emoji' },
  { name: 'JetBrains Mono', url: 'https://www.jetbrains.com/lp/mono/' }
]

const developer = [
  {
    name: '小贼贼子',
    desc: '一个后端程序员，也是一个半吊子前端。',
    avatar: 'https://www.wangyunf.com/bl/pic/home/bl/img/U1/head/luban.png'
  }
]
</script>

<style scoped lang="scss">
.about-root {
  @include box(100%, 100%);
  padding: 0 30px 130px 30px;
  overflow: scroll;

  .project-name {
    @include font(50px, 500);
    @include themeText(2px 2px 5px #878787, 2px 2px 5px #000000);
    color: var(--el-color-primary);
    width: 100%;
    text-align: center;
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
    .shields {
      margin-top: 10px;
    }
  }

  .project-link {
    width: 100%;
    text-align: center;
    padding-top: 5px;
  }

  .bl-preview {
    background-color: transparent;

    ol {
      @include font(14px, 300);
      @include themeText(1px 1px 5px #b5b5b5, 1px 1px 5px #000000);
      padding-left: 55px;
      margin: 0;

      li {
        margin-bottom: 3px;
      }
    }
  }

  .blod {
    color: var(--el-color-primary);
    font-weight: bold;
  }

  .paragraph {
    @include font(14px, 300);
    @include themeText(1px 1px 5px #b5b5b5, 1px 1px 5px #000000);
    text-indent: 30px;
    line-height: 25px;

    a {
      text-indent: 0;
    }
  }

  .developer {
    width: 100%;
    @include flex(row, flex-start, flex-start);
    align-content: flex-start;
    flex-wrap: wrap;

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
        height: 25px;
      }

      .desc {
        @include font(12px, 300);
        overflow: auto;
      }
    }
  }

  .reference {
    font-size: 12px;
    padding: 10px;
    margin-top: 30px;
    border: 1px dashed #8b8b8b;
    border-radius: 5px;
    color: #8b8b8b;
  }
}
</style>
