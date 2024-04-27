<template>
  <div class="config-root" v-loading="!userStore.isLogin" element-loading-spinner="none" element-loading-text="请登录后查看...">
    <div class="title">博客配置</div>
    <div class="desc" style="margin-bottom: 0">博客各项参数配置，若无内容请点击右侧刷新。</div>
    <div class="desc">
      <el-button @click="refreshParam" text bg><span class="iconbl bl-refresh-line"></span>刷新参数</el-button>
    </div>

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

      <el-divider></el-divider>

      <el-form-item label="博客主题色">
        <bl-row class="colors">
          <el-input
            size="default"
            v-model="userParamForm.WEB_BLOG_COLOR"
            :style="{
              width: '180px',
              marginRight: '15px',
              '--el-input-text-color': userParamForm.WEB_BLOG_COLOR,
              '--el-input-border-color': userParamForm.WEB_BLOG_COLOR
            }"></el-input>
          <el-color-picker
            size="default"
            popper-class="theme-color-picker"
            v-model="userParamForm.WEB_BLOG_COLOR"
            color-format="rgb"
            @change="changeBlogColor" />
          <div class="color-item" v-for="color in colors" :key="color" :style="{ backgroundColor: color }" @click="changeBlogColor(color)"></div>
        </bl-row>
        <div class="conf-tip">博客主题色，主要影响专题样式颜色以及文章中的链接颜色，以及文章临时访问中的链接颜色。</div>
      </el-form-item>

      <el-form-item label="博客名称">
        <el-input size="default" v-model="userParamForm.WEB_LOGO_NAME" @change="(cur: any) => updParam('WEB_LOGO_NAME', cur)"></el-input>
        <div class="conf-tip">博客左上角名称，以及在浏览器标签中的名称。</div>
      </el-form-item>

      <el-form-item label="博客LOGO地址">
        <el-input size="default" v-model="userParamForm.WEB_LOGO_URL" @change="(cur: any) => updParam('WEB_LOGO_URL', cur)"></el-input>
        <div class="conf-tip">博客左上角 Logo 的访问地址，以及在浏览器标签中的 Logo。</div>
      </el-form-item>

      <el-form-item label="是否显示文章标题">
        <bl-row>
          <el-switch
            v-model="userParamForm.WEB_BLOG_SHOW_ARTICLE_NAME"
            size="default"
            style="margin-right: 10px"
            @change="(cur: boolean) => updParam('WEB_BLOG_SHOW_ARTICLE_NAME', cur ? '1' : '0')" />
        </bl-row>
        <div class="conf-tip">是否在文章内容的顶部显示文章的标题。</div>
      </el-form-item>

      <el-form-item label="开启专题特殊样式">
        <bl-row>
          <el-switch
            v-model="userParamForm.WEB_BLOG_SUBJECT_TITLE"
            size="default"
            style="margin-right: 10px"
            @change="(cur: boolean) => updParam('WEB_BLOG_SUBJECT_TITLE', cur ? '1' : '0')" />
        </bl-row>
        <div class="conf-tip">是否在博客文档列表中以特殊的样式显示专题。</div>
      </el-form-item>

      <el-form-item label="开启文章水印">
        <bl-row>
          <el-switch
            size="default"
            v-model="userParamForm.WEB_BLOG_WATERMARK_ENABLED"
            @change="(cur: boolean) => updParam('WEB_BLOG_WATERMARK_ENABLED', cur ? '1' : '0')" />
        </bl-row>
        <div class="conf-tip">是否开启博客文章，以及文章临时访问中的背景文字水印。</div>
        <div class="conf-watermark" v-if="userParamForm.WEB_BLOG_WATERMARK_ENABLED">
          <bl-row>
            <bl-row width="60%">
              <div class="label">水印内容</div>
              <el-input
                size="default"
                v-model="userParamForm.WEB_BLOG_WATERMARK_CONTENT"
                @change="(cur: any) => updParam('WEB_BLOG_WATERMARK_CONTENT', cur)"></el-input>
            </bl-row>
            <bl-row width="40%">
              <div class="label">水印大小</div>
              <el-input-number
                size="default"
                v-model="userParamForm.WEB_BLOG_WATERMARK_FONTSIZE"
                @change="(cur: any) => updParam('WEB_BLOG_WATERMARK_FONTSIZE', cur.toString())"></el-input-number>
            </bl-row>
          </bl-row>

          <bl-row style="margin-top: 10px">
            <bl-row width="60%">
              <div class="label">水印颜色</div>
              <el-input
                size="default"
                style="width: calc(100% - 120px); margin-right: 8px"
                v-model="userParamForm.WEB_BLOG_WATERMARK_COLOR"
                @change="(cur: any) => updParam('WEB_BLOG_WATERMARK_COLOR', cur)">
                <template #suffix>
                  <el-tooltip effect="light" placement="top" transition="none">
                    <template #content> 使用透明颜色会有更好的显示效果<br />如: rgba(157, 157, 157, 0.2)</template>
                    <span class="iconbl bl-admonish-line"></span>
                  </el-tooltip>
                </template>
              </el-input>
              <el-tooltip effect="light" placement="top" transition="none">
                <template #content> 使用透明颜色会有更好的效果<br />如: rgba(157, 157, 157, 0.2)</template>
                <el-color-picker
                  size="default"
                  popper-class="theme-color-picker"
                  v-model="userParamForm.WEB_BLOG_WATERMARK_COLOR"
                  color-format="rgba"
                  show-alpha
                  @change="changeWatermarkColor" />
              </el-tooltip>
            </bl-row>
            <bl-row width="40%">
              <div class="label">密集程度</div>
              <el-input-number
                size="default"
                v-model="userParamForm.WEB_BLOG_WATERMARK_GAP"
                :step="10"
                @change="(cur: any) => updParam('WEB_BLOG_WATERMARK_GAP', cur.toString())"></el-input-number>
            </bl-row>
          </bl-row>
        </div>
      </el-form-item>

      <el-form-item label="ICP备案号">
        <el-input size="default" v-model="userParamForm.WEB_IPC_BEI_AN_HAO" @change="(cur: any) => updParam('WEB_IPC_BEI_AN_HAO', cur)"></el-input>
        <div class="conf-tip">如果博客作为你的域名首页，你可能需要配置 ICP 备案号，该内容会跳转至中国大陆《ICP/IP地址/域名信息备案管理系统》。</div>
      </el-form-item>

      <el-form-item label="首页自定义信息">
        <el-input
          size="default"
          type="textarea"
          :rows="5"
          v-model="userParamForm.WEB_GONG_WANG_AN_BEI"
          @change="(cur: any) => updParam('WEB_GONG_WANG_AN_BEI', cur)"></el-input>
        <div class="conf-tip">首页底部的自定义信息，可填写 HTML 内容，也可在此自定义备案信息即跳转地址等信息。</div>
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
          :rows="13"
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
import { ElMessage } from 'element-plus'

const userStore = useUserStore()

const userParamForm = ref({
  WEB_ARTICLE_URL: '',
  WEB_IPC_BEI_AN_HAO: '',
  WEB_LOGO_NAME: '',
  WEB_LOGO_URL: '',
  WEB_GONG_WANG_AN_BEI: '',
  WEB_BLOG_URL_ERROR_TIP_SHOW: '',
  WEB_BLOG_LINKS: '',
  WEB_BLOG_SUBJECT_TITLE: false,
  WEB_BLOG_COLOR: '',
  WEB_BLOG_SHOW_ARTICLE_NAME: true,
  WEB_BLOG_WATERMARK_ENABLED: false,
  WEB_BLOG_WATERMARK_CONTENT: '',
  WEB_BLOG_WATERMARK_FONTSIZE: 15,
  WEB_BLOG_WATERMARK_COLOR: '',
  WEB_BLOG_WATERMARK_GAP: 100
})

const colors = ['rgb(136, 118, 87)', 'rgb(119, 150, 73)', 'rgb(128, 164, 146)', 'rgb(110, 155, 197)', 'rgb(97, 94, 168)', 'rgb(104, 104, 104)']

const changeBlogColor = (color: string) => {
  if (!color) {
    userParamForm.value.WEB_BLOG_COLOR = 'rgb(104, 104, 104)'
  } else {
    userParamForm.value.WEB_BLOG_COLOR = color
  }
  updParam('WEB_BLOG_COLOR', userParamForm.value.WEB_BLOG_COLOR)
}

const changeWatermarkColor = (color: string) => {
  if (!color) {
    userParamForm.value.WEB_BLOG_WATERMARK_COLOR = 'rgba(157, 157, 157, 0.2)'
  } else {
    userParamForm.value.WEB_BLOG_WATERMARK_COLOR = color
  }
  updParam('WEB_BLOG_WATERMARK_COLOR', userParamForm.value.WEB_BLOG_WATERMARK_COLOR)
}

/**
 * 获取参数列表
 */
const getParamList = () => {
  userParamListApi().then((resp) => {
    userParamForm.value = {
      ...resp.data,
      ...{
        WEB_BLOG_SUBJECT_TITLE: resp.data.WEB_BLOG_SUBJECT_TITLE === '1',
        WEB_BLOG_SHOW_ARTICLE_NAME: resp.data.WEB_BLOG_SHOW_ARTICLE_NAME === '1',
        WEB_BLOG_WATERMARK_ENABLED: resp.data.WEB_BLOG_WATERMARK_ENABLED === '1',
        WEB_BLOG_WATERMARK_FONTSIZE: Number(resp.data.WEB_BLOG_WATERMARK_FONTSIZE),
        WEB_BLOG_WATERMARK_GAP: Number(resp.data.WEB_BLOG_WATERMARK_GAP)
      }
    }
  })
}

/**
 * 刷新参数
 */
const refreshParam = () => {
  userParamRefreshApi().then((_) => {
    Notify.success('', '刷新成功')
    getParamList()
    userStore.getUserinfo()
  })
}

/**
 * 修改参数
 *
 * @param paramName 参数名
 * @param paramValue 参数值
 */
const updParam = (paramName: string, paramValue: string) => {
  userParamUpdApi({ paramName: paramName, paramValue: paramValue }).then((_resp) => {
    userStore.getUserinfo()
    ElMessage.info({ message: '保存成功', grouping: true })
  })
}

const toBlog = () => {
  let url = userStore.userParams.WEB_ARTICLE_URL.replaceAll('/#/articles?articleId=', '/#/home')
  openExtenal(url)
}

/**
 * 获取连接配置模板
 */
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

.colors {
  align-content: flex-start;
  flex-wrap: wrap;

  .el-color-picker--small {
    margin: 0 10px 10px 0;
  }

  .color-item {
    @include box(28px, 28px);
    margin: 0 0 0 15px;
    border-radius: 4px;
    position: relative;
    transition: transform 0.3s;
    cursor: pointer;
    text-align: center;

    &:hover {
      transform: scale(1.1);
    }
  }
}

.theme-color-picker {
  z-index: 3001 !important;
  margin: 0 10px 10px 0;
}
</style>
