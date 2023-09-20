<template>
  <div class="article-qrcode-root">

    <!-- 标题 -->
    <div class="info-title">
      <div class="iconbl bl-qr-code-line"></div>公网访问二维码
    </div>

    <div class="content" v-loading="isLoading">

      <bl-row just="center">
        <el-image fit="cover" :src="qrcodeImg" style="width: 200px;height: 200px;">
          <template #error>
            <bl-row just="center" height="200px" style="border:1px dashed var(--el-border-color);border-radius: 10px;">
              二维码生成失败
            </bl-row>
          </template>
        </el-image>
      </bl-row>
      <bl-row just="center" style="margin-top: 5px;color: var(--bl-text-color-light);">
        <div>《{{ articleName }}》</div>
      </bl-row>
      <bl-row just="center" style="margin-top: 10px;">
      </bl-row>

      <bl-row class="label" style="margin-top: 10px;">
        文章访问链接
        <div class="iconbl bl-copy-line" @click="copyUrl"></div>
        <div class="iconbl bl-a-clouddownload-line" @click="downloadImg"></div>
      </bl-row>

      <div style="word-break: break-all;font-size: 12px;margin-top: 10px;">
        <a :href="qrcodeUrl" target="_blank">{{ qrcodeUrl }}</a>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { articleQrCodeApi } from '@renderer/api/blossom'
import { writeText, download } from '@renderer/assets/utils/electron'

const isLoading = ref(true)

// 截屏图片预览
const qrcodeImg = ref('')
const qrcodeUrl = ref('')
const articleName = ref('')

const getArticleQrCode = (name: string, id: number) => {
  articleName.value = name
  articleQrCodeApi({ id: id }).then(resp => {
    let blob = new Blob([resp.data], { type: "image/png" })
    qrcodeImg.value = URL.createObjectURL(blob)
    qrcodeUrl.value = resp.headers.get('Article-Url')
  }).finally(() => {
    isLoading.value = false
  })
}

const copyUrl = () => {
  writeText(qrcodeUrl.value)
}

const downloadImg = () => {
  download(qrcodeImg.value)
}

defineExpose({
  getArticleQrCode
})
</script>

<style scoped lang="scss">
@import '@renderer/assets/styles/bl-dialog-info';
$height-title: 50px;

.article-qrcode-root {
  border-radius: 10px;

  .content {
    padding: 20px;

    .label {
      font-size: 16px;
      border-top: 1px solid var(--el-border-color);
      padding-top: 15px;

      .iconbl {
        font-size: 18px;
        margin-left: 10px;
        cursor: pointer;

        &:hover {
          color: var(--el-color-primary);
        }
      }
    }

    a {
      color: var(--el-color-primary);
    }
  }


}
</style>