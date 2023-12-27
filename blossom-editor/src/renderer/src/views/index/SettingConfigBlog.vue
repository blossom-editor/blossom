<template>
  <div class="config-root" v-loading="auth.status !== '已登录'" element-loading-spinner="none" element-loading-text="请登录后使用博客设置...">
    <div class="title">博客配置</div>
    <div class="desc">博客配置，若无内容请点击右侧刷新。<el-button @click="refreshParam" text bg>刷新</el-button></div>

    <el-form v-if="auth.status == '已登录'" :model="userParamForm" label-position="right" label-width="130px" style="max-width: 800px">
      <el-form-item label="文章查看地址" :required="true">
        <el-input
          size="default"
          v-model="userParamForm.WEB_ARTICLE_URL"
          @change="(cur: any) => updParam('WEB_ARTICLE_URL', cur)"
          style="width: calc(100% - 100px)"></el-input>
        <el-button size="default" style="width: 90px; margin-left: 10px">访问测试</el-button>
        <div class="conf-tip">网页端博客的访问地址，如果不使用博客可不配置。需以<code>/#/articles?articleId=</code>结尾。</div>
      </el-form-item>

      <el-form-item label="博客名称">
        <el-input size="default" v-model="userParamForm.WEB_LOGO_NAME" @change="(cur: any) => updParam('WEB_LOGO_NAME', cur)"></el-input>
        <div class="conf-tip">博客左上角名称。</div>
      </el-form-item>

      <el-form-item label="博客LOGO地址">
        <el-input size="default" v-model="userParamForm.WEB_LOGO_URL" @change="(cur: any) => updParam('WEB_LOGO_URL', cur)"></el-input>
        <div class="conf-tip">博客左上角 Logo 的访问地址。</div>
      </el-form-item>

      <el-form-item label="IPC备案号">
        <el-input size="default" v-model="userParamForm.WEB_IPC_BEI_AN_HAO" @change="(cur: any) => updParam('WEB_IPC_BEI_AN_HAO', cur)"></el-input>
        <div class="conf-tip">如果博客作为你的域名首页，你可能需要配置 IPC 备案号</div>
      </el-form-item>

      <el-form-item label="公网安备号">
        <el-input
          size="default"
          v-model="userParamForm.WEB_GONG_WANG_AN_BEI"
          @change="(cur: any) => updParam('WEB_GONG_WANG_AN_BEI', cur)"></el-input>
        <div class="conf-tip">如果博客作为你的域名首页，你可能需要配置公网安备号</div>
      </el-form-item>
    </el-form>
    <div class="server-config">
      {{ userinfoJson }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onActivated, onMounted, ref } from 'vue'
import { storeToRefs } from 'pinia'
import { useUserStore } from '@renderer/stores/user'
import { userParamListApi, userParamUpdApi, userParamRefreshApi } from '@renderer/api/blossom'
import { formatJson } from '@renderer/assets/utils/util'
import Notify from '@renderer/scripts/notify'

onMounted(() => {
  getParamList()
})

onActivated(() => {
  getParamList()
})

const userStore = useUserStore()
const { userinfo, auth } = storeToRefs(userStore)

const userParamForm = ref({
  WEB_ARTICLE_URL: '',
  WEB_IPC_BEI_AN_HAO: '',
  WEB_LOGO_NAME: '',
  WEB_LOGO_URL: '',
  WEB_GONG_WANG_AN_BEI: '',
  WEB_BLOG_URL_ERROR_TIP_SHOW: ''
})

/**
 * 获取参数列表
 */
const getParamList = () => {
  userParamListApi().then((resp) => {
    userParamForm.value = resp.data
  })
}

const refreshParam = () => {
  userParamRefreshApi().then((_) => {
    Notify.success('刷新参数成功', '刷新成功')
    getParamList()
    userStore.getUserinfo()
  })
}

const updParam = (paramName: string, paramValue: string) => {
  userParamUpdApi({ paramName: paramName, paramValue: paramValue }).then((_resp) => {
    userStore.getUserinfo()
  })
}

const userinfoJson = computed(() => {
  return formatJson(userinfo.value)
})
</script>

<style scoped lang="scss">
@import './styles/config-root.scss';

.server-config {
  padding: 10px;
  font-size: 12px;
  white-space: pre;
  color: var(--bl-text-color-light);
}
</style>
