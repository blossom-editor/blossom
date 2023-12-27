<template>
  <div class="quick-setting-root">
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
      <el-step title="完成">
        <template #icon>
          <div class="iconbl bl-check"></div>
        </template>
      </el-step>
    </el-steps>
    <!-- 




     -->
    <div class="step-detail" v-if="activeSetp === 1">
      <div class="row">下方是否为您在登录页面添加的地址。</div>
      <div class="row">若不是，请在登录页重新登录。</div>
      <div class="row server-url">{{ server.serverUrl }}</div>

      <div class="row">
        <el-button size="default" type="primary" plain @click="savePicture">是</el-button>
      </div>
    </div>
    <!-- 




     -->
    <div class="step-detail blog-detail" v-if="activeSetp === 2">
      <div class="detail">
        <div class="row">您选择使用后台自带博客, 还是自己独立部署博客？</div>
        <div class="row" just="center">
          <el-button size="default" :type="blogType == 1 ? 'primary' : ''" @click="setBlogType(1)">自带博客</el-button>
          <el-button size="default" :type="blogType == 2 ? 'primary' : ''" @click="setBlogType(2)">独立部署</el-button>
        </div>
        <div v-if="blogType == 1" class="row blog-type">
          <div>
            自带博客只能作为用户ID为1的用户使用，若想修改，请查看<a
              href="https://www.wangyunf.com/blossom-doc/guide/deploy/blog.html#update-config"
              target="_blank"
              >修改方法<span class="iconbl bl-sendmail-line"></span></a
            >。
          </div>
        </div>
        <div v-if="blogType == 2" class="row blog-type">
          <div style="font-size: 13px">
            填写博客地址，并以<code>/#/articles?articleId=</code>结尾
            <el-tooltip effect="light" content="复制地址结尾" placement="top">
              <span class="iconbl bl-copy-line" @click="writeText(URL_SUFFIX)"></span>
            </el-tooltip>
          </div>
          <bl-row>
            <el-input type="textarea" :rows="2" resize="none" v-model="blogUrl" placeholder="请填写博客地址..." @input="blogUrlChange"></el-input>
          </bl-row>
          <div class="blog-url-error">
            <span v-show="blogUrlError">博客地址格式错误!</span>
          </div>
        </div>
        <div class="row" style="margin-top: 20px">
          <el-button size="default" text @click="last(1)">上一步</el-button>
          <el-button size="default" type="primary" @click="saveBlog" plain>确认使用{{ blogType === 1 ? '自带博客' : '独立部署' }}</el-button>
        </div>
      </div>
      <div class="iframe-container">
        <bl-row v-if="isBlank(blogUrlPreview)" class="iframe-placeholder" just="center">请填写博客地址</bl-row>
        <iframe v-else :src="blogUrlPreview" width="100%"></iframe>
      </div>
    </div>

    <div class="step-detail" v-if="activeSetp === 3">
      <img style="width: 200px" src="@renderer/assets/imgs/plant/杨桃.svg" />
      <div class="row">配置完成,</div>
      <div class="row">
        <el-button size="default" type="primary" plain @click="savePicture">开始！</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, ref } from 'vue'
import { useServerStore } from '@renderer/stores/server'
import { isBlank } from '@renderer/assets/utils/obj'
import { writeText } from '@renderer/assets/utils/electron'

const server = useServerStore()
const activeSetp = ref(1)

const last = (active: number) => {
  activeSetp.value = active
}

const savePicture = () => {
  activeSetp.value = 2
}

const URL_SUFFIX_SERVER = '/blog/#/articles?articleId='
const URL_SUFFIX = '/#/articles?articleId='
const previewId = -123
const blogType = ref<1 | 2>(1)
const blogUrl = ref('')
const blogUrlError = ref(false)

/**
 * 预览页面
 */
const blogUrlPreview = computed(() => {
  console.log(blogUrl.value)

  if (blogType.value == 1) {
    return server.serverUrl + URL_SUFFIX_SERVER + previewId
  } else if (isBlank(blogUrl.value) || (!blogUrl.value.startsWith('http://') && !blogUrl.value.startsWith('https://'))) {
    return ''
  } else if (blogUrl.value.endsWith(URL_SUFFIX)) {
    return blogUrl.value + previewId
  }

  return blogUrl.value
})

const setBlogType = (type: 1 | 2) => {
  blogType.value = type
}

const blogUrlChange = (val) => {
  blogUrl.value = ''
  blogUrlError.value = false
  nextTick(() => {
    blogUrl.value = val
  })
}

const saveBlog = () => {
  if (blogType.value === 2 && !blogUrl.value.endsWith(URL_SUFFIX)) {
    blogUrlError.value = true
    return
  }
  activeSetp.value = 3
}
</script>

<style lang="scss">
.quick-setting-root {
  padding: 20px 10px 0 10px;
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

    .server-url {
      padding: 10px;
      border-radius: 8px;
      background-color: var(--bl-preview-code-bg-color);
      color: var(--bl-preview-code-color);
    }

    .blog-type {
      @include flex(column, space-around, center);
      @include box(100%, 100px);
      padding: 5px;
      border-radius: 6px;
      border: 1px dashed var(--el-border-color);
      background-color: var(--bl-preview-code-bg-color);
    }

    .blog-url-error {
      height: 16px;
      font-size: 12px;
      color: var(--el-color-danger);
    }

    .bl-sendmail-line {
      font-size: 14px;
    }

    .bl-copy-line {
      font-size: 15px;
      cursor: pointer;
    }

    .bl-refresh-smile {
      font-size: 50px;
    }

    code {
      color: var(--el-color-primary);
      font-size: 13px;
      margin: 0 5px;
    }
  }

  .blog-detail {
    @include flex(row, space-around, center);
    padding-top: 0;

    .detail {
      @include flex(column, flex-start, center);
      @include box(400px, auto, 400px, 400px);
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
  }
}
</style>
