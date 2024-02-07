<template>
  <div class="quick-setting-root" :style="{ height: rootHeight }">
    <el-steps :active="activeSetp" align-center>
      <el-step title="文件" description="文件访问地址配置">
        <template #icon>
          <div class="iconbl bl-image--line"></div>
        </template>
      </el-step>
      <el-step title="博客" description="博客地址配置">
        <template #icon>
          <div class="iconbl bl-blog"></div>
        </template>
      </el-step>
      <el-step title="完成" description="配置完成">
        <template #icon>
          <div class="iconbl bl-check"></div>
        </template>
      </el-step>
    </el-steps>
    <!--


     -->
    <Transition name="fade" mode="out-in">
      <div class="step-detail" v-if="activeSetp === 1">
        <div class="row">下方是否为您在登录页填写的地址。</div>
        <div class="row">若不是，请重新登录。</div>
        <div class="row server-url">{{ serverStore.serverUrl }}</div>

        <div class="row">
          <el-button size="default" type="primary" plain @click="savePicture">是</el-button>
        </div>
      </div>
      <!--


      -->
      <div class="step-detail blog-detail" v-else-if="activeSetp === 2">
        <div>
          <div class="row" style="margin-bottom: 30px">您选择使用后台自带博客, 还是自行独立部署博客？</div>

          <el-tabs v-model="blogType" type="border-card" class="blog-tabs" stretch>
            <el-tab-pane label="自带博客" name="backend" style="line-height: 30px">
              <div>自带博客默认只提供给用户ID为 "1" 的用户使用。</div>
              <div>
                您的ID为:<code class="user-id">{{ userStore.userinfo.id }}</code>
              </div>
              <div>
                如需修改，请查看<a href="https://www.wangyunf.com/blossom-doc/guide/deploy/blog.html#update-config" target="_blank"
                  >《如何修改默认配置》<span class="iconbl bl-sendmail-line"></span></a
                >。
              </div>
            </el-tab-pane>

            <el-tab-pane label="独立部署" name="custom">
              <bl-row>
                1. 请确保博客的<code>config.js</code>中的<code>USER_ID</code>配置为<code class="user-id">{{ userStore.userinfo.id }}</code
                >。
              </bl-row>
              <bl-row height="40px">
                2. 填写博客地址，以<code>/#/articles?articleId=</code>结尾
                <el-tooltip effect="light" content="复制地址结尾" placement="top">
                  <span class="iconbl bl-copy-line" @click="writeText(URL_SUFFIX)"></span>
                </el-tooltip>
              </bl-row>
              <bl-row>
                <el-input
                  type="textarea"
                  :rows="2"
                  resize="none"
                  v-model="customBlogUrl"
                  placeholder="示例: http://www.google.com//#/articles?articleId="
                  @input="customBlogUrlChange"></el-input>
              </bl-row>
              <div class="blog-url-error" style="margin-top: 10px">
                <span v-show="customBlogUrlError">博客地址格式错误!</span>
              </div>
            </el-tab-pane>
          </el-tabs>

          <div class="row" style="margin-top: 30px">
            <el-button v-if="userStore.userinfo.type === 1" size="default" text @click="last(1)">上一步</el-button>
            <el-button size="default" type="primary" @click="saveBlog" plain>
              确认使用{{ blogType === 'backend' ? '自带博客' : '独立部署' }}
            </el-button>
            <el-button size="default" @click="saveBlog" text>我不使用博客</el-button>
          </div>
        </div>

        <div class="iframe-container">
          <bl-row v-if="isBlank(blogUrlPreview)" class="iframe-placeholder" just="center">请填写博客地址</bl-row>
          <iframe v-else :src="blogUrlPreview" width="100%"></iframe>
        </div>

        <div class="document" @click="openExtenal('http://localhost:5175/blossom-doc/guide/deploy/blog.html')">有疑问？查看文档。</div>
      </div>
      <!--


      -->
      <div class="step-detail" v-else-if="activeSetp === 3">
        <bl-row just="center">
          <img style="width: 80px" src="@renderer/assets/imgs/plan/base-celebrate.png" />
        </bl-row>
        <div class="row">配置完成, 欢迎使用 Blossom!</div>
        <div class="row">
          <el-button size="default" type="primary" plain @click="completed">开始!</el-button>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, ref } from 'vue'
import { useServerStore } from '@renderer/stores/server'
import { useUserStore } from '@renderer/stores/user'
import { KEY_BLOSSOM_OBJECT_STORAGE_DOMAIN, KEY_WEB_ARTICLE_URL } from '@renderer/stores/user'
import { isBlank } from '@renderer/assets/utils/obj'
import { openExtenal, writeText } from '@renderer/assets/utils/electron'
import { paramUpdApi, userParamUpdApi } from '@renderer/api/blossom'

const userStore = useUserStore()
const serverStore = useServerStore()
const activeSetp = ref(userStore.userinfo.type === 1 ? 1 : 2)

const rootHeight = computed(() => {
  if (activeSetp.value === 1) {
    return '310px'
  }
  if (activeSetp.value === 2) {
    return '600px'
  }
  return '310px'
})

//#region --- 保存图片路径

const last = (active: number) => {
  activeSetp.value = active
}

const savePicture = () => {
  let url = serverStore.serverUrl + '/pic'
  paramUpdApi({ paramName: KEY_BLOSSOM_OBJECT_STORAGE_DOMAIN, paramValue: url }).then((_resp) => {
    activeSetp.value = 2
    userStore.getUserinfo()
  })
}

//#endregion

//#region --- 保存博客链接
type BlogType = 'backend' | 'custom'
const URL_SUFFIX_SERVER = '/blog/#/articles?articleId='
const URL_SUFFIX = '/#/articles?articleId='
const previewId = -999
const blogType = ref<BlogType>('backend')
const customBlogUrl = ref(userStore.userParams.WEB_ARTICLE_URL)
const customBlogUrlError = ref(false)

/**
 * 预览页面
 */
const blogUrlPreview = computed(() => {
  if (blogType.value == 'backend') {
    return serverStore.serverUrl + URL_SUFFIX_SERVER + previewId
  } else if (isBlank(customBlogUrl.value) || (!customBlogUrl.value.startsWith('http://') && !customBlogUrl.value.startsWith('https://'))) {
    return ''
  } else if (customBlogUrl.value.endsWith(URL_SUFFIX)) {
    return customBlogUrl.value + previewId
  }

  return customBlogUrl.value
})

/**
 * 修改博客地址时, 将博客校验重置, 重新加载博客页面
 * @param url 博客地址
 */
const customBlogUrlChange = (url: string) => {
  customBlogUrl.value = ''
  customBlogUrlError.value = false
  nextTick(() => {
    customBlogUrl.value = url
  })
}

/**
 * 保存博客链接
 */
const saveBlog = () => {
  if (blogType.value === 'backend') {
    let blogUrl = serverStore.serverUrl + URL_SUFFIX_SERVER
    userParamUpdApi({ paramName: KEY_WEB_ARTICLE_URL, paramValue: blogUrl }).then((_resp) => {
      activeSetp.value = 3
      userStore.getUserinfo()
    })
  }
  if (blogType.value === 'custom') {
    // 如果链接不是以规范后缀结束, 则报错
    if (!customBlogUrl.value.endsWith(URL_SUFFIX)) {
      customBlogUrlError.value = true
      return
    }
    userParamUpdApi({ paramName: KEY_WEB_ARTICLE_URL, paramValue: customBlogUrl.value }).then((_resp) => {
      activeSetp.value = 3
      userStore.getUserinfo()
    })
  }
}

const completed = () => {
  emits('completed')
}

const emits = defineEmits(['completed'])
//#endregion
</script>

<style lang="scss" scoped>
.quick-setting-root {
  widows: 100%;
  padding: 20px 10px 0 10px;
  transition: height 0.7s ease;

  .fade-enter-active,
  .fade-leave-active {
    transition: opacity 0.3s ease;
  }

  .fade-enter-from,
  .fade-leave-to {
    opacity: 0;
  }

  .el-steps {
    --el-bg-color: var(--bl-dialog-bg-color);
    .iconbl {
      font-size: 30px;
    }
  }

  .row {
    margin-bottom: 15px;
    &:last-child {
      margin-bottom: 5px;
    }
  }

  .step-detail {
    @include font(14px, 300);
    padding: 35px 20px 20px 20px;
    text-align: center;
    text-decoration: dashed;

    .server-url {
      padding: 10px;
      border-radius: 8px;
      background-color: var(--bl-preview-code-bg-color);
      color: var(--bl-preview-code-color);
    }

    .bl-sendmail-line {
      font-size: 14px;
    }

    .bl-copy-line {
      font-size: 15px;
      cursor: pointer;
    }

    code {
      color: var(--el-color-primary);
      font-size: 13px;
      margin: 0 4px;
    }

    a {
      color: var(--el-color-primary);
    }
  }

  .blog-detail {
    @include flex(row, space-around, center);
    padding-top: 0;
    transition: height 0.4s;

    .blog-tabs {
      width: 410px;
      height: 200px;
      border-radius: 6px;
      overflow: hidden;
      font-size: 13px;
      --el-font-size-base: 14px;
      .el-tabs__item {
        transition: none;
      }
    }

    .iframe-container {
      @include box(240px, 480px);
      position: relative;

      .iframe-placeholder {
        @include box(240px, 480px);
        color: var(--bl-text-color-light);
        border: 1px solid var(--el-border-color);
        border-radius: 6px;
      }

      iframe {
        height: 800px;
        width: 400px;
        transform: scale(0.6);
        position: absolute;
        left: -80px;
        top: -160px;
        border: none;
        border-radius: 6px;
      }
    }

    .blog-url-error {
      height: 16px;
      font-size: 12px;
      color: var(--el-color-danger);
    }

    .user-id {
      color: #e3a300;
      border-bottom: 3px solid #e3a300;
      border-radius: 3px;
    }

    .document {
      @include font(12px, 300);
      font-style: italic;
      position: absolute;
      bottom: 5px;
      left: 5px;
      color: var(--bl-text-color-light);
      cursor: pointer;
    }
  }
}
</style>
