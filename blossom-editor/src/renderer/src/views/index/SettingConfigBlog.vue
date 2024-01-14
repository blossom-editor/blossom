<template>
  <div class="config-root" v-loading="!userStore.isLogin" element-loading-spinner="none" element-loading-text="请登录后查看...">
    <div class="title">博客配置</div>
    <div class="desc">博客各项参数配置，若无内容请点击右侧刷新。<el-button @click="refreshParam" text bg>刷新</el-button></div>

    <el-form :model="userParamForm" label-position="right" label-width="130px" style="max-width: 800px">
      <el-form-item label="文章查看地址" :required="true">
        <el-input
          size="default"
          style="width: calc(100% - 100px)"
          v-model="userParamForm.WEB_ARTICLE_URL"
          @change="(cur: any) => updParam('WEB_ARTICLE_URL', cur)">
          <template #prefix>
            <div class="iconbl bl-blog" style="font-size: 20px"></div>
          </template>
        </el-input>
        <el-button size="default" style="width: 90px; margin-left: 10px" @click="toBlog">点击访问博客</el-button>
        <div class="conf-tip">
          博客的文章访问地址，即使不使用博客，也推荐配置为后台自带的博客地址。<span style="color: var(--el-color-danger)"
            >必须以<code style="color: var(--el-color-danger)">/#/articles?articleId=</code>结尾。 </span
          >你可以点击右上角的<span class="iconbl bl-caution-line" style="padding: 0 3px"></span>图标进行快捷配置。
        </div>
      </el-form-item>

      <el-form-item label="博客名称">
        <el-input size="default" v-model="userParamForm.WEB_LOGO_NAME" @change="(cur: any) => updParam('WEB_LOGO_NAME', cur)"></el-input>
        <div class="conf-tip">博客左上角名称，以及在浏览器标签中的名称。</div>
      </el-form-item>

      <el-form-item label="博客LOGO地址">
        <el-input size="default" v-model="userParamForm.WEB_LOGO_URL" @change="(cur: any) => updParam('WEB_LOGO_URL', cur)"></el-input>
        <div class="conf-tip">博客左上角 Logo 的访问地址，以及在浏览器标签中的 Logo。</div>
      </el-form-item>

      <el-form-item label="ICP备案号">
        <el-input size="default" v-model="userParamForm.WEB_IPC_BEI_AN_HAO" @change="(cur: any) => updParam('WEB_IPC_BEI_AN_HAO', cur)"></el-input>
        <div class="conf-tip">如果博客作为你的域名首页，你可能需要配置 ICP 备案号</div>
      </el-form-item>

      <el-form-item label="公网安备号">
        <el-input
          size="default"
          v-model="userParamForm.WEB_GONG_WANG_AN_BEI"
          @change="(cur: any) => updParam('WEB_GONG_WANG_AN_BEI', cur)"></el-input>
        <div class="conf-tip">如果博客作为你的域名首页，你可能需要配置公网安备号</div>
      </el-form-item>

      <el-form-item label="外部链接">
        <div class="conf-tip">
          可以填写其他网站链接，该链接会展示在博客右上角的【更多】按钮，以及首页的【所有文章】下。<span style="color: var(--el-color-danger)"
            >你需要严格遵循模板中的 Json 格式。</span
          >
        </div>
        <el-button style="margin-bottom: 5px" @click="genWebLinksTemplate">生成链接配置模板</el-button>
        <el-input
          size="default"
          type="textarea"
          :rows="11"
          v-model="userParamForm.WEB_BLOG_LINKS"
          @change="(cur: any) => updParam('WEB_BLOG_LINKS', cur)"></el-input>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useUserStore } from '@renderer/stores/user'
import { userParamListApi, userParamUpdApi, userParamRefreshApi } from '@renderer/api/blossom'
import { isNotBlank } from '@renderer/assets/utils/obj'
import { openExtenal } from '@renderer/assets/utils/electron'
import Notify from '@renderer/scripts/notify'

const userStore = useUserStore()

const userParamForm = ref({
  WEB_ARTICLE_URL: '',
  WEB_IPC_BEI_AN_HAO: '',
  WEB_LOGO_NAME: '',
  WEB_LOGO_URL: '',
  WEB_GONG_WANG_AN_BEI: '',
  WEB_BLOG_URL_ERROR_TIP_SHOW: '',
  WEB_BLOG_LINKS: ''
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

const toBlog = () => {
  let url = userStore.userParams.WEB_ARTICLE_URL.replaceAll('/#/articles?articleId=', '/#/home')
  openExtenal(url)
}

const genWebLinksTemplate = () => {
  if (isNotBlank(userParamForm.value.WEB_BLOG_LINKS)) {
    Notify.info('你需要将内容清空后再生成模板', '提示')
  } else {
    userParamForm.value.WEB_BLOG_LINKS = `[
  {
    "NAME": "链接 A 名称(必填)",
    "URL": "链接 A 的地址(必填)",
    "LOGO": "链接 A 的 Logo"
  },{
    "NAME": "链接 B 名称",
    "URL": "链接 B 的地址",
    "LOGO": "链接 B 的 Logo"
  }
]`
    updParam('WEB_BLOG_LINKS', userParamForm.value.WEB_BLOG_LINKS)
  }
}

const reload = () => {
  getParamList()
}

defineExpose({ reload })
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
