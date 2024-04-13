<template>
  <div :class="['index-setting-root', viewStyle.isShowAsideSimple ? 'simple' : '']">
    <el-switch
      :class="['setting-switch', viewStyle.isShowAsideSimple ? 'simple' : '']"
      inline-prompt
      :size="viewStyle.isShowAsideSimple ? 'default' : 'large'"
      v-model="isDark"
      :active-icon="Moon"
      :inactive-icon="Sunny"
      @change="changeTheme" />
    <div v-if="viewStyle.isShowAsideSimple" class="button-group-simple">
      <el-button type="primary" text @click="handlePrintScreenUpload()">
        <el-icon :size="18"><Crop /></el-icon>
      </el-button>
      <el-button type="primary" text @click="toSettingTab" style="margin: 0; margin-top: 3px">
        <el-icon :size="18"><Setting /></el-icon>
      </el-button>
    </div>
    <el-button-group v-else>
      <el-button class="setting-button" type="primary" :icon="Setting" @click="toSettingTab" />
      <el-button class="setting-button" type="primary" :icon="Crop" @click="handlePrintScreenUpload()" />
    </el-button-group>
  </div>

  <!-- 截图上传弹框 -->
  <el-dialog
    title="设置上传目录"
    class="dialog-ps-upload"
    v-model="isShowPrintScreenUpload"
    height="400"
    width="300"
    style="border-radius: 4px"
    :append-to-body="true"
    :destroy-on-close="false"
    :close-on-click-modal="true">
    <div class="ps-upload-root">
      <el-image fit="cover" :src="printScreenImgUrl">
        <template #error>
          <div class="img-placeholder">
            请点击截图, 或使用快捷键
            <div class="keyboard">Ctrl+Alt+Q</div>
          </div>
        </template>
      </el-image>
      <bl-row>
        <el-tree-select
          style="width: 290px"
          v-model="pid"
          :data="docTreeData"
          node-key="i"
          :props="{ label: 'n' }"
          :indent="10"
          clearable
          accordion
          show-checkbox
          check-strictly
          @clear="clearPid"
          @check="selectCheck"
          placeholder="请选择上传目录">
        </el-tree-select>
      </bl-row>
      <bl-row>
        <el-input v-model="printScreenName" placeholder="图片名称"></el-input>
      </bl-row>
      <bl-row>
        <span
          >当前截图将上传至《{{ docTreeChecked?.n }}》目录, 上传后可粘贴为
          <span class="copy-type-desc">
            {{ copyType == 'http' ? '图片链接' : copyType == 'markdown' ? '图片链接(Markdown 格式)' : '图片文件' }}
          </span>
        </span>
      </bl-row>
      <bl-row just="space-between" style="border-top: 1px solid var(--el-border-color)">
        <el-radio-group v-model="copyType">
          <el-radio-button value="http">HT</el-radio-button>
          <el-radio-button value="markdown">MD</el-radio-button>
        </el-radio-group>
        <div>
          <el-button type="primary" @click="invokePrintScreen">截图</el-button>
          <el-button type="primary" plain @click="printscreenUpload">上传</el-button>
        </div>
      </bl-row>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useConfigStore } from '@renderer/stores/config'
import { useUserStore } from '@renderer/stores/user'
import { toLogin, toSetting } from '@renderer/router'
import { Sunny, Moon, Setting, Crop } from '@element-plus/icons-vue'
import { docTreeApi, uploadFileApi } from '@renderer/api/blossom'
import { handleUploadSeccess, handleUploadError } from '@renderer/views/picture/scripts/picture'
import { isEmpty } from 'lodash'
import { printScreen, readImageToDataUrl, readImageToPNG, writeText } from '@renderer/assets/utils/electron'
import { isBlank } from '@renderer/assets/utils/obj'
import { getNowTime, isMacOS, isElectron } from '@renderer/assets/utils/util'
import { isDark, changeTheme } from '@renderer/scripts/global-theme'
import Notify from '@renderer/scripts/notify'

const userStore = useUserStore()
const { viewStyle } = useConfigStore()

onMounted(() => {
  printscreenAfter()
})

const toSettingTab = () => {
  if (userStore.isLogin) {
    toSetting('setting')
  } else {
    toLogin()
  }
}

//#region ----------------------------------------< 截屏 >----------------------------------------
const isShowPrintScreenUpload = ref<boolean>(false)
const docTreeData = ref<DocTree[]>([])
const docTreeChecked = ref<DocTree>()
const pid = ref<number>(-1)
const printScreenName = ref('')

// 截屏图片预览
const printScreenImgUrl = ref('')
let docTreeInit: boolean = false

// 图片上传结果
const copyType = ref('markdown')

/**
 * 显示截屏结果页面
 * @param isShow 是否显示, 传入否时, 将会根据当前状态取反, 否则设置为 isShow
 */
const handlePrintScreenUpload = (isShow?: boolean) => {
  if (isMacOS() || !isElectron()) {
    Notify.warning('您所在的平台暂不支持截图功能', '抱歉')
    return
  }
  if (!docTreeInit) {
    docTreeApi({ onlyFolder: true }).then((resp) => {
      docTreeData.value = resp.data
      pid.value = resp.data[0].i
      docTreeChecked.value = resp.data[0]
      docTreeInit = true
    })
  }
  if (isShow !== undefined) {
    isShowPrintScreenUpload.value = isShow
  } else {
    isShowPrintScreenUpload.value = !isShowPrintScreenUpload.value
  }
}

/**
 * 处理选中事件
 * @param treeNode 选中的节点
 * @param checked  选中的集合, 集合 checkedKeys 为空时, 则
 */
const selectCheck = (treeNode: DocTree, checked: any) => {
  docTreeChecked.value = treeNode
  if (isEmpty(checked.checkedKeys)) {
    pid.value = -1
    docTreeChecked.value = docTreeData.value[0]
  }
}

/**
 * 清空选中下拉列表, 并重置为默认文件夹(-1)
 */
const clearPid = () => {
  pid.value = -1
  docTreeChecked.value = docTreeData.value[0]
}

/**
 * 调用截图
 */
const invokePrintScreen = () => {
  printScreen()
}

/**
 * 截图后处理
 */
const printscreenAfter = () => {
  if (isElectron()) {
    //@ts-ignore
    window.electronAPI.printScreenAfter((_event: any, psCode: any): void => {
      if (psCode == 1) {
        handlePrintScreenUpload(true)
        setTimeout(() => {
          printScreenImgUrl.value = readImageToDataUrl()
        }, 0)
      }
    })
  }
}

/**
 * 上传截图
 */
const printscreenUpload = () => {
  const buffer = readImageToPNG()
  const blob = new Blob([buffer])
  const formData = new FormData()
  let filename: string | Blob
  if (isBlank(printScreenName.value)) {
    filename = `PRINTSCREEN_${getNowTime()}.png`
  } else {
    filename = printScreenName.value + '.png'
  }
  formData.append('file', blob, filename)
  formData.append('pid', pid.value.toString())
  formData.append('filename', filename)
  uploadFileApi(formData)
    .then((resp) => {
      /**
       * 上传成功
       */
      if (handleUploadSeccess(resp)) {
        let url: string = resp.data
        // 1. 复制上传结果
        if (copyType.value == 'http') {
          writeText(url)
        } else if (copyType.value == 'markdown') {
          writeText(`![${filename}](${resp.data})`)
        } else {
        }
        // 2. 重置截图预览
        printScreenImgUrl.value = ''
        // 3. 重置截图名称
        printScreenName.value = ''
      }
    })
    .catch((error) => {
      handleUploadError(error)
    })
}

//#endregion
</script>

<style scoped lang="scss">
.index-setting-root {
  @include flex(column, space-between, center);
  @include box(100%, 65px);
  padding: 5px;

  .setting-switch {
    margin-bottom: 5px;
    --el-switch-off-color: var(--el-color-primary-light-9);

    :deep(.el-switch__core) {
      border-radius: 4px;
    }

    :deep(.el-switch__action) {
      border-radius: 4px;
    }
  }

  .setting-button {
    padding: 5px;
    font-size: 14px;

    :deep(.el-button--small) {
      margin: 0;
    }
  }
}

.index-setting-root.simple {
  @include flex(column, flex-end, center);
  @include box(100%, 145px);

  .setting-switch.simple {
    transform: rotate(90deg);
    margin-bottom: 16px;
  }

  .button-group-simple {
    @include flex(column, flex-end, center);
    padding: 0;
    margin-bottom: 5px;

    .el-button--small {
      padding: 15px 5px;
    }
  }
}
</style>

<style lang="scss">
.dialog-system-setting {
  .el-dialog__header {
    border-bottom: 1px solid var(--el-border-color);
    padding: 15px;
    margin: 0;
  }

  .el-dialog__body {
    padding: 10px 0 0 0;
  }
}

.dialog-ps-upload {
  position: absolute !important;
  left: 80px;
  bottom: 80px;
  margin: 0 !important;

  .el-dialog__header {
    border-bottom: 1px solid var(--el-border-color);
    padding: 5px 10px;
    margin: 0;

    .el-dialog__headerbtn {
      height: 25px;
      width: 30px;
    }
  }

  .el-dialog__body {
    padding: 0;

    .ps-upload-root {
      @include box(100%, 100%);
      @include flex(column, flex-start, flex-start);
      @include font(12px);

      .el-image {
        @include box(100%, 150px);
        border: 10px solid var(--el-color-primary-light-5);

        .img-placeholder {
          @include box(100%, 100%);
          @include flex(column, center, center);
          color: var(--bl-text-color-light);

          div {
            margin-top: 5px;
          }
        }
      }

      .copy-type-desc {
        color: var(--el-color-primary);
        font-style: italic;
        text-decoration: underline;
      }

      .bl-row-root {
        margin-top: 5px;
        padding: 5px;
      }
    }
  }
}
</style>
