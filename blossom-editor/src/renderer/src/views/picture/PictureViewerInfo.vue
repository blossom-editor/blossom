<template>
  <div class="picture-viewer-info-root">
    <el-image-viewer v-if="isShowPicInfo" :url-list="[picCacheWrapper(picUrl)]" @close="closePicInfo" :z-index="2002">
      <div class="bl-image-viewer-infos" v-if="isNotNull(picInfo)">
        <div class="container">
          <bl-row align="flex-start">
            <strong>图片名称：</strong>
            <div>{{ picInfo!.name }}<span class="iconbl bl-copy-line" @click="writeText(picInfo!.name)"></span></div>
          </bl-row>
          <bl-row align="flex-start"><strong>图片大小：</strong>{{ formatFileSize(picInfo!.size) }}</bl-row>
          <bl-row align="flex-start"><strong>上传时间：</strong>{{ picInfo!.creTime }}</bl-row>
          <bl-row align="flex-start"><strong>图片路径：</strong>{{ picInfo!.pathName }}</bl-row>
        </div>
        <el-divider></el-divider>
        <div class="container">
          <div><strong>使用该图片的文章：</strong></div>
          <bl-row v-if="!isEmpty(picInfo!.articleNames)" align="flex-start">
            <div>
              <div v-for="aname in articleNamesToArray(picInfo!.articleNames)">《{{ aname }}》</div>
            </div>
          </bl-row>
        </div>
        <el-divider></el-divider>

        <div class="container btns">
          <el-tooltip content="强制删除会使该图片链接失效" placement="top" :hide-after="0">
            <el-button type="primary" text style="--el-fill-color: #535353; --el-fill-color-light: #414141" @click="deletePicture">
              强制删除
            </el-button>
          </el-tooltip>

          <el-tooltip placement="top" :hide-after="0">
            <template #content>
              <bl-row>
                <svg style="height: 20px; width: 20px; margin-right: 10px" aria-hidden="true">
                  <use xlink:href="#wl-jinggao"></use>
                </svg>
                <div>
                  将该图片替换为其他图片<br />
                  <span style="color: #faad14">图片替换为立即生效，旧图片将无法找回</span>
                </div>
              </bl-row>
            </template>
            <el-upload
              :action="serverStore.serverUrl + uploadFileApiUrl"
              name="file"
              :data="{ pid: picInfo!.pid, filename: picInfo!.name, repeatUpload: true }"
              :headers="{ Authorization: 'Bearer ' + userStore.auth.token }"
              :show-file-list="false"
              :before-upload="beforeUpload"
              :on-success="onUploadSeccess"
              :on-error="onError">
              <el-button type="primary" text style="--el-fill-color: #535353; --el-fill-color-light: #414141">
                <svg style="height: 15px; width: 25px" aria-hidden="true">
                  <use xlink:href="#wl-jinggao"></use>
                </svg>
                替换图片
              </el-button>
            </el-upload>
          </el-tooltip>

          <el-button type="primary" text style="--el-fill-color: #535353; --el-fill-color-light: #414141" @click="download(picInfo!.url)"
            >下载图片</el-button
          >
        </div>
      </div>
    </el-image-viewer>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessageBox, UploadProps } from 'element-plus'
import { WarnTriangleFilled } from '@element-plus/icons-vue'
import { useUserStore } from '@renderer/stores/user'
import { useServerStore } from '@renderer/stores/server'
import { pictureDelApi, pictureInfoApi, uploadFileApiUrl } from '@renderer/api/blossom'
import { articleNamesToArray, Picture, buildDefaultPicture, onError, beforeUpload, picCacheWrapper, picCacheRefresh } from './scripts/picture'
import { formatFileSize, isHttp } from '@renderer/assets/utils/util'
import { isNotNull } from '@renderer/assets/utils/obj'
import { isEmpty } from 'lodash'
import { download, writeText } from '@renderer/assets/utils/electron'
import Notify from '@renderer/scripts/notify'

const userStore = useUserStore()
const serverStore = useServerStore()

// 是否显示图片 viewer
const isShowPicInfo = ref(false)
// 图片地址
const picUrl = ref('')
// 图片信息
const picInfo = ref<Picture | null>(buildDefaultPicture())

const showPicInfo = (url: string) => {
  picUrl.value = url
  isShowPicInfo.value = true
  if (!isHttp(url)) {
    picInfo.value = null
    return
  }
  pictureInfoApi({ url: url }).then((resp) => {
    picInfo.value = resp.data
  })
}

const closePicInfo = () => {
  isShowPicInfo.value = false
  picInfo.value = buildDefaultPicture()
}

/**
 * 删除图片
 * @param pic 当前选中图片
 */
const deletePicture = () => {
  ElMessageBox.confirm('强制删除图片后该图片访问链接将会失效, 是否继续删除?', {
    confirmButtonText: '我要删除',
    cancelButtonText: '取消',
    type: 'warning',
    icon: WarnTriangleFilled
  }).then(() => {
    pictureDelApi({ id: picInfo.value!.id, ignoreCheck: true }).then((_resp) => {
      picCacheRefresh()
      closePicInfo()
      emits('saved')
    })
  })
}

/**
 * 替换上传成功
 */
const onUploadSeccess: UploadProps['onSuccess'] = (resp, _file?) => {
  if (resp.code === '20000') {
    Notify.success('图片替换成功')
    emits('saved')
    closePicInfo()
    picCacheRefresh()
    return true
  } else {
    Notify.error(resp.msg, '图片替换失败')
    return false
  }
}

defineExpose({ showPicInfo })

const emits = defineEmits(['saved'])
</script>

<style scoped lang="scss">
.picture-viewer-info-root {
  .bl-image-viewer-infos {
    @include themeBg(#1b1b1bbf, #1e1e1ebf);
    @include themeColor(#a9a9a9, rgb(190, 190, 190));
    width: 320px;
    font-size: 13px;
    position: absolute;
    left: 30px;
    bottom: 30px;
    border-radius: 10px;
    line-height: 25px;

    .container {
      padding: 20px;
      white-space: break-spaces;
      word-wrap: break-word;
      word-break: break-all;

      strong {
        min-width: 70px;
      }

      .iconbl {
        margin-left: 10px;
        cursor: pointer;

        &:hover {
          color: var(--el-color-primary);
        }
      }
    }

    .el-divider {
      margin: 0;
      border-color: #474747;
    }

    .btns {
      @include flex(row, space-around);
      padding-top: 10px;
      height: 90px;
    }
  }

  :deep(.el-image-viewer__mask) {
    opacity: 0.8;
  }

  :deep(.el-image-viewer__actions) {
    left: 180px;
    background-color: transparent;
  }

  .test {
    color: #a9a9a9;
  }
}
</style>
