<template>
  <div class="server-config-root">
    <el-tabs tab-position="left" type="card" style="height: 100%" class="config-tabs">
      <el-tab-pane label="客户端配置">
        <div class="tab-content"><ConfigClient></ConfigClient></div>
      </el-tab-pane>
      <el-tab-pane label="服务器配置" :lazy="true" v-if="userStore.userinfo.type === 1">
        <div class="tab-content"><ConfigServer></ConfigServer></div>
      </el-tab-pane>
      <el-tab-pane label="博客配置" :lazy="true">
        <div class="tab-content"><ConfigBlog></ConfigBlog></div>
      </el-tab-pane>
      <el-tab-pane label="修改个人信息" :lazy="true">
        <div class="tab-content"><ConfigUserinfo></ConfigUserinfo></div>
      </el-tab-pane>
      <el-tab-pane label="修改登录密码" :lazy="true">
        <div class="tab-content"><ConfigUpdPwd></ConfigUpdPwd></div>
      </el-tab-pane>
      <el-tab-pane label="添加使用账号" :lazy="true" v-if="userStore.userinfo.type === 1">
        <div class="tab-content">
          <ConfigAddUser></ConfigAddUser>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import ConfigUserinfo from './SettingConfigUserinfo.vue'
import ConfigUpdPwd from './SettingConfigUpdPwd.vue'
import ConfigAddUser from './SettingConfigAddUser.vue'
import ConfigClient from './SettingConfigClient.vue'
import ConfigServer from './SettingConfigServer.vue'
import ConfigBlog from './SettingConfigBlog.vue'
import { useUserStore } from '@renderer/stores/user'
const userStore = useUserStore()
</script>

<style scoped lang="scss">
.server-config-root {
  @include box(100%, 100%);

  .config-tabs {
    width: 100%;

    :deep(.el-tabs__item.is-left.is-active) {
      border-right-color: #ffffff00;
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
