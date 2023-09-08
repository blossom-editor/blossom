<template>
  <div class="setting-index-root">
    <el-tabs tab-position="left" class="setting-tabs">
      <el-tab-pane label="登录">
        <div class="setting-container">
          <div class="wrapper">
            <SettingLogin></SettingLogin>
          </div>
        </div>
      </el-tab-pane>
      <el-tab-pane label="设置">
        <SettingConfig></SettingConfig>
      </el-tab-pane>
      <el-tab-pane label="关于">
        <SettingAboutVue></SettingAboutVue>
      </el-tab-pane>
      <el-tab-pane label="流量监控" :lazy="true">
        <SentinelResources></SentinelResources>
      </el-tab-pane>
    </el-tabs>
  </div>
  <div class="version">
    <span>{{ CONFIG.SYS.NAME + ' | ' + CONFIG.SYS.VERSION + ' | ' + CONFIG.SYS.DATETIME }}</span>
  </div>
  <bl-row class="footer">
    <WaveFooter></WaveFooter>
  </bl-row>
</template>

<script setup lang="ts">
import SettingLogin from './SettingLogin.vue'
import SettingConfig from './SettingConfig.vue'
import SettingAboutVue from './SettingAbout.vue'
import SentinelResources from '@renderer/views/statistic/SentinelResources.vue'
import WaveFooter from '@renderer/components/WaveFooter.vue'
import CONFIG from '@renderer/assets/constants/system'

</script>

<style scoped lang="scss">
.setting-index-root {
  @include box(100%, 100%);
  background-image: linear-gradient(135deg,
      var(--bl-html-color),
      var(--bl-html-color),
      var(--el-color-primary-light-5));
  padding: 50px;
  z-index: 2;

  .setting-tabs {
    height: 100%;

    :deep(.el-tabs__nav-wrap::after) {
      background-color: var(--el-color-primary-light-8);
    }

    :deep(.el-tabs__content) {
      height: 100%;

      .el-tab-pane {
        height: 100%;
      }
    }
  }

  .setting-container {
    @include box(100%, 100%);
    @include flex(row, center, center);

    .wrapper {
      width: 600px;
      height: 600px;
    }
  }
}

.version {
  @include themeColor(#D0D0D0, #3A3A3A);
  @include font(13px, 300);
  @include absolute('', 10px, 10px, '');
  z-index: 2;
}

.footer {
  position: absolute;
  bottom: 0;
  z-index: 1;
  height: 100px;
}
</style>