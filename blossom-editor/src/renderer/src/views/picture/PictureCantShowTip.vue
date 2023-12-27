<template>
  <div class="picture-cant-show-tip-root">
    <div class="info-title">
      <div class="iconbl bl-a-picturecracked-line"></div>
      图片无法显示的解决方式
    </div>
    <div class="content">
      <div class="choise-type">你通过何种方式部署？</div>
      <el-tabs type="border-card" v-model="activeName">
        <el-tab-pane label="Docker run 命令" name="docker">
          <p>你需要将启动命令中的如下配置修改为当前<span class="server-url">登录地址</span>，并在末尾增加<code>/pic</code></p>
          <div class="command">--project.iaas.blos.domain=登录地址/pic</div>
          <p>
            当前登录地址：<code>{{ serverStore.serverUrl }} </code>
          </p>
          <p>
            你所填的内容：<code>{{ userStore.userinfo.osRes.domain }}</code>
          </p>
          <p>
            正确地址应为：<code>{{ serverStore.serverUrl + '/pic' }}</code
            ><i class="iconbl bl-copy-line" @click="copyServerUrl"></i>
          </p>
        </el-tab-pane>
        <el-tab-pane label="Docker Compose 脚本" name="compose">
          <p>你需要将<code>yaml</code>脚本中的如下配置修改为当前<span class="server-url">登录地址</span>，并在末尾增加<code>/pic</code></p>
          <pre class="command">
services:
  blossom:
    environment:
      PROJECT_IAAS_BLOS_DOMAIN: 登录地址/pic/</pre
          >
          <p>
            当前登录地址：<code>{{ serverStore.serverUrl }}</code>
          </p>
          <p>
            你所填的内容：<code>{{ userStore.userinfo.osRes.domain }}</code>
          </p>
          <p>
            正确地址应为：<code>{{ serverStore.serverUrl + '/pic' }}</code
            ><i class="iconbl bl-copy-line" @click="copyServerUrl"></i>
          </p>
        </el-tab-pane>
        <el-tab-pane label="其他" name="other">
          <p>如果你使用 Jar 包部署，修改方式与 <span class="totab" @click="toTab('docker')">Docker run</span> 命令相同。</p>
          <p>
            如果你使用源码部署，请<a href="https://www.wangyunf.com/blossom-doc/guide/deploy/backend-props.html#props-iaas-blos" target="_blank"
              >查阅文档</a
            >。
          </p>
        </el-tab-pane>
      </el-tabs>
      <div class="tips">如果你在上传图片后变更过IP或域名，那么旧图片也将无法访问，请谨慎修改。</div>
      <div class="tips">
        如果以上方式无法解决你的问题，可以前往<a href="https://www.wangyunf.com/blossom-doc/guide/deploy/faq.html#cant-shwo-pic" target="_blank"
          >文档</a
        >查看更多信息。
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useServerStore } from '@renderer/stores/server'
import { useUserStore } from '@renderer/stores/user'
import { writeText } from '@renderer/assets/utils/electron'

const activeName = ref('docker')

const serverStore = useServerStore()
const userStore = useUserStore()

const toTab = (name: string) => {
  activeName.value = name
}

const copyServerUrl = () => {
  writeText(serverStore.serverUrl + '/pic')
}
</script>

<style scoped lang="scss">
@import '@renderer/assets/styles/bl-dialog-info';

.picture-cant-show-tip-root {
  @include box(100%, 100%);
  border-radius: 10px;

  .content {
    padding: 20px;
    font-weight: 300;
    transition: height 0.4s;
    position: relative;

    .choise-type {
      margin-bottom: 10px;
    }

    .server-url {
      font-weight: bold;
      color: var(--el-color-primary);
    }

    .command {
      font-size: 13px;
      padding: 10px;
      border-radius: 4px;
      color: var(--bl-preview-blockquote-color);
      background-color: var(--bl-preview-blockquote-bg-color);
      margin: 15px 0;
    }

    .totab {
      color: var(--el-color-primary);
      cursor: pointer;
    }

    .tips {
      margin-top: 10px;
    }

    .bl-copy-line {
      cursor: pointer;
      &:hover {
        color: var(--el-color-primary);
      }
    }

    code {
      font-size: 13px;
      background-color: var(--bl-preview-code-bg-color);
      color: var(--bl-preview-blockquote-color);
      padding: 0px 4px;
      border-radius: 3px;
      margin: 0 5px;
    }

    a {
      color: var(--el-color-primary);
    }
  }
}
</style>
