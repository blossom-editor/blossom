<template>
  <div class="server-config-root" v-loading="auth.status !== '已登录'" element-loading-spinner="none" element-loading-text="请登录后使用设置...">
    <el-tabs tab-position="left" type="card" style="height: 100%" class="config-tabs">
      <el-tab-pane label="服务器配置">
        <div class="tab-content">
          <div class="title">服务器配置信</div>
          <div class="desc">Blossom 服务器配置</div>
          <ConfigServer></ConfigServer>
        </div>
      </el-tab-pane>
      <el-tab-pane label="客户端配置" :lazy="true">
        <div class="tab-content">
          <div class="title">客户端配置</div>
          <div class="desc">Blossom 桌面客户端配置</div>
          <ConfigClient></ConfigClient>
        </div>
      </el-tab-pane>
      <el-tab-pane label="修改个人信息" :lazy="true">
        <div class="tab-content">
          <div class="title">修改用户信息</div>
          <div class="desc">
            天气预报使用<a style="color: var(--el-color-primary)" target="_blank" href="https://dev.qweather.com/">和风天气API</a
            >，您可以在其上免费创建您的令牌以使用天气预报功能。
          </div>
          <ConfigUserinfo></ConfigUserinfo>
        </div>
      </el-tab-pane>
      <el-tab-pane label="修改登录密码" :lazy="true">
        <div class="tab-content">
          <div class="title">修改登录密码</div>
          <div class="desc">修改后下次登录将使用新密码。</div>
          <ConfigUpdPwd></ConfigUpdPwd>
        </div>
      </el-tab-pane>
      <el-tab-pane label="添加使用账号" :lazy="true">
        <div class="tab-content">
          <div class="title">添加使用账号</div>
          <div class="desc">您可以添加用户与您共用同一个后台服务，添加账号后，可登录新账号修改个人信息。</div>
          <ConfigAddUser></ConfigAddUser>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { storeToRefs } from 'pinia'
import { useUserStore } from '@renderer/stores/user'
import ConfigUserinfo from './SettingConfigUserinfo.vue'
import ConfigUpdPwd from './SettingConfigUpdPwd.vue'
import ConfigAddUser from './SettingConfigAddUser.vue'
import ConfigClient from './SettingConfigClient.vue'
import ConfigServer from './SettingConfigServer.vue'

const userStore = useUserStore()
const { auth } = storeToRefs(userStore)
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

    .title {
      color: var(--el-color-primary);
      margin-bottom: 10px;
      font-weight: 700;
    }

    .desc {
      @include font(12px, 300);
      margin-bottom: 30px;
      color: var(--bl-text-color-light);
    }
  }

  .form {
    padding: 20px;
    border: 1px solid var(--el-border-color);
    margin-top: 20px;
  }
}
</style>
