<template>
  <div class="server-config-root">
    <el-tabs v-model="curTab" style="height: 100%" class="config-tabs" tab-position="left" type="card" @tab-change="handleChange">
      <el-tab-pane label="客户端配置" name="client">
        <div class="tab-content"><ConfigClient></ConfigClient></div>
      </el-tab-pane>
      <el-tab-pane label="服务器配置" name="server" :lazy="true" v-if="userStore.userinfo.type === 1 && userStore.isLogin">
        <div class="tab-content"><ConfigServer ref="ConfigServerRef"></ConfigServer></div>
      </el-tab-pane>
      <el-tab-pane label="博客配置" name="blog" :lazy="true" v-if="userStore.isLogin">
        <div class="tab-content"><ConfigBlog ref="ConfigBlogRef"></ConfigBlog></div>
      </el-tab-pane>
      <el-tab-pane label="修改个人信息" name="userinfo" :lazy="true">
        <div class="tab-content"><ConfigUserinfo ref="ConfigUserinfoRef"></ConfigUserinfo></div>
      </el-tab-pane>
      <el-tab-pane label="修改登录密码" name="password" :lazy="true" v-if="userStore.isLogin">
        <div class="tab-content"><ConfigUpdPwd></ConfigUpdPwd></div>
      </el-tab-pane>
      <el-tab-pane label="添加使用账号" name="adduser" :lazy="true" v-if="userStore.userinfo.type === 1 && userStore.isLogin">
        <div class="tab-content">
          <ConfigAddUser></ConfigAddUser>
        </div>
      </el-tab-pane>
      <el-tab-pane label="实时访问流量" name="flow" :lazy="true" v-if="userStore.isLogin && userStore.userinfo.type === 1 && userStore.isLogin">
        <SentinelResources></SentinelResources>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { nextTick, ref } from 'vue'
import { useUserStore } from '@renderer/stores/user'
import ConfigUserinfo from './SettingConfigUserinfo.vue'
import ConfigUpdPwd from './SettingConfigUpdPwd.vue'
import ConfigAddUser from './SettingConfigAddUser.vue'
import ConfigClient from './SettingConfigClient.vue'
import ConfigServer from './SettingConfigServer.vue'
import ConfigBlog from './SettingConfigBlog.vue'
import SentinelResources from '@renderer/views/statistic/SentinelResources.vue'

const userStore = useUserStore()

const curTab = ref('client')
const ConfigServerRef = ref()
const ConfigBlogRef = ref()
const ConfigUserinfoRef = ref()

const handleChange = (name: string) => {
  if (name === 'server') {
    nextTick(() => ConfigServerRef.value.reload())
  }
  if (name === 'blog') {
    nextTick(() => ConfigBlogRef.value.reload())
  }
  if (name === 'userinfo') {
    nextTick(() => ConfigUserinfoRef.value.reload())
  }
}
</script>

<style scoped lang="scss">
.server-config-root {
  @include box(100%, 100%);

  .config-tabs {
    width: 100%;

    :deep(.el-tabs__item.is-left.is-active) {
      border-right-color: #ffffff00;
      font-weight: bold;
    }

    :deep(.el-tabs__item) {
      font-weight: 300;
    }
  }

  .tab-content {
    width: 100%;
    height: 100%;
    padding-left: 30px;
  }
}
</style>
