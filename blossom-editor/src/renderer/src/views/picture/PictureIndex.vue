<template>
  <div class="index-picture-root">
    <!-- folder menu -->
    <div class="doc-container" ref="DocsRef">
      <div class="doc-tree-menu-container">
        <PictureTreeDocs @click-doc="clickCurFolder"></PictureTreeDocs>
      </div>

      <div class="doc-upload">
        <PictureUpload :repeat-upload="isReplaceUpload"></PictureUpload>
      </div>
    </div>

    <div class="resize-divider-vertical" ref="ResizeDividerRef"></div>

    <div class="picture-container" ref="PictureContainerRef">
      <!-- 工作台 -->
      <div class="picutre-workbench" :style="workbencStyle.workbench1">
        <div class="workbenchs">
          <div class="workbench-level1">
            <div class="star">
              <div v-if="picturePageParam.starStatus == undefined" class="iconbl bl-star-line" @click="changeStarStatus"></div>
              <div v-else class="iconbl bl-star-fill" @click="changeStarStatus"></div>
            </div>

            <!-- 显式收藏 -->
            <div class="btn-wrapper radio">
              <div>卡片大小</div>
              <el-radio-group v-model="cardSize">
                <el-radio-button value="mini">小</el-radio-button>
                <el-radio-button value="large">大</el-radio-button>
              </el-radio-group>
            </div>

            <div class="btn-wrapper radio">
              <el-tooltip effect="light" placement="top" popper-class="is-small" :offset="8" :hide-after="0">
                <template #content> 开启重复上传后，重名的图片将会被覆盖 </template>
                <div>重复上传<span class="iconbl bl-admonish-line"></span></div>
              </el-tooltip>
              <el-switch
                width="70"
                class="replace-upload-switch"
                inline-prompt
                size="large"
                v-model="isReplaceUpload"
                active-text="开启"
                inactive-text="关闭" />
            </div>

            <div class="btn-wrapper">
              <el-button plain @click="refresh">刷新</el-button>
            </div>

            <div class="btn-wrapper">
              <el-button @click="picCacheRefresh">
                清空图片缓存
                <el-tooltip effect="light" placement="top" popper-class="is-small" :hide-after="0">
                  <template #content> 重复上传图片后，如果图片无变化可刷新缓存 </template>
                  <div><span class="iconbl bl-admonish-line"></span></div>
                </el-tooltip>
              </el-button>
            </div>

            <div class="btn-wrapper">
              <el-button type="primary" plain @click="handleBenchworkStyle">
                批量管理
                <el-tooltip effect="light" placement="top" popper-class="is-small" :hide-after="0">
                  <template #content> 批量删除，或转移至其他文件夹<br />右键点击卡片可快捷选中 </template>
                  <div><span class="iconbl bl-admonish-line"></span></div>
                </el-tooltip>
              </el-button>
            </div>
          </div>
          <div class="workbench-level2" :style="workbencStyle.workbench2 as StyleValue">
            <el-checkbox v-model="checkedAll" @change="handlCheckedAll">全选</el-checkbox>
            <el-button type="primary" text bg @click="transfer" style="margin-left: 11px">移动</el-button>
            <el-button type="primary" text bg @click="delBatch">删除</el-button>
            <el-button type="danger" text bg @click="delBatchIgnoreCheck">强制删除</el-button>
          </div>
        </div>

        <div class="statistic">
          <bl-col just="center" :style="{ ...workbencStyle.workbench2, ...{ height: '100%' } }">
            <div style="font-size: 40px; font-weight: 300; font-style: italic; padding: 0 10px">{{ picChecks.size }}</div>
          </bl-col>
        </div>
      </div>

      <div class="picture-card-container" :style="workbencStyle.cards">
        <div :class="['picture-card', cardClass]" v-for="pic in picturePages" @click.right="picCheckRightClick(pic, $event)">
          <el-checkbox
            v-show="isExpandWorkbench"
            class="picture-card-check"
            size="large"
            v-model="pic.checked"
            @change="(check: boolean) => picCheckChange(check, pic.id)">
          </el-checkbox>

          <div v-if="pic.delTime" class="img-deleted">
            {{ pic.delTime == 2 ? '已删除' : pic.delTime == 1 ? '删除中' : '无法查看' }}
          </div>
          <div v-else-if="!isImage(pic.url)" class="other-file">
            <div class="other-filename">{{ getFilePrefix(pic.name) }}</div>
            <div class="other-suffix">{{ getFileSuffix(pic.url) }}</div>
          </div>
          <div v-else class="img-wrapper" @click="showPicInfo(pic.url)">
            <img :src="picCacheWrapper(pic.url)" @error="onErrorImg" />
          </div>

          <div class="picuter-card-workbench">
            <el-tooltip placement="bottom" trigger="click" :hide-after="0">
              <template #content>
                <div style="max-width: 300px; white-space: break-spaces; word-wrap: break-word; word-break: break-all">
                  <bl-row>图片名称: {{ pic.name }}</bl-row>
                  <bl-row>图片大小: {{ formatFileSize(pic.size) }}</bl-row>
                  <bl-row>上传时间: {{ pic.creTime }}</bl-row>
                  <bl-row>图片路径: {{ pic.pathName }}</bl-row>
                  <bl-row v-if="!isEmpty(pic.articleNames)" align="flex-start"
                    >引用文章:
                    <div>
                      <div v-for="aname in articleNamesToArray(pic.articleNames)" style="margin-left: -13px">《{{ aname }}》</div>
                    </div>
                  </bl-row>
                </div>
              </template>
              <div class="item iconbl bl-problem-line"></div>
            </el-tooltip>
            <div class="item iconbl bl-copy-line" @click="copyUrl(pic.url)" @click.right="copyMarkdownUrl(pic.url, pic.name, $event)"></div>
            <div class="item iconbl bl-a-clouddownload-line" @click="download(pic.url)"></div>
            <div v-if="pic.starStatus == 0" class="item iconbl bl-star-line" @click="starPicture(pic)"></div>
            <div v-else-if="pic.starStatus == 1" class="item iconbl bl-star-fill" @click="starPicture(pic)"></div>
            <div class="item iconbl bl-delete-line" @click="deletePicture(pic)"></div>
          </div>
        </div>

        <div class="picuter-card-next">
          <el-button type="info" plain style="width: 100px" @click="nextPage">下一页</el-button>
        </div>
      </div>
      <div class="picture-status">
        <bl-row width="calc(100% - 240px)" height="100%" class="status-item-container">
          <div>{{ curFolder?.name }}: {{ pictureStat.cur.picCount }} P / {{ pictureStat.cur.picSize }}</div>
          <div>存储路径: {{ storePath }}</div>
        </bl-row>
        <div class="status-item-container">
          <div>文件总览: {{ pictureStat.global.picCount }} P / {{ pictureStat.global.picSize }}</div>
        </div>
      </div>
    </div>

    <PictureViewerInfo ref="PictureViewerInfoRef"></PictureViewerInfo>
  </div>

  <el-dialog
    v-model="isShowTransferDialog"
    width="400px"
    style="height: fit-content"
    :align-center="true"
    :append-to-body="true"
    :destroy-on-close="true"
    :close-on-click-modal="false"
    draggable>
    <PictureTransfer :ids="picChecks" @transferred="transferred"></PictureTransfer>
  </el-dialog>

  <el-dialog
    v-model="isShowBatchDelDialog"
    width="500px"
    style="height: fit-content"
    :align-center="true"
    :append-to-body="true"
    :destroy-on-close="true"
    :close-on-click-modal="false"
    draggable>
    <PictureBatchDel :ids="picChecks" :ignore-check="delIgnoreCheck" @deleted="deleted"></PictureBatchDel>
  </el-dialog>
</template>
<script setup lang="ts">
// vue
import { ref, provide, computed, StyleValue } from 'vue'
import { useUserStore } from '@renderer/stores/user'
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus'
import { CopyDocument } from '@element-plus/icons-vue'
import { picturePageApi, pictureStarApi, pictureDelApi, pictureStatApi } from '@renderer/api/blossom'
import { treeToInfo, provideKeyDocInfo } from '@renderer/views/doc/doc'
import { isEmpty } from 'lodash'
import { isNotNull, isNull } from '@renderer/assets/utils/obj'
import { formatFileSize, getFilePrefix, getFileSuffix, isImage } from '@renderer/assets/utils/util'
import { writeText, download } from '@renderer/assets/utils/electron'
import { useLifecycle } from '@renderer/scripts/lifecycle'
import { useResizeVertical } from '@renderer/scripts/resize-devider-vertical'

// component
import { articleNamesToArray, picCacheWrapper, Picture, picCacheRefresh } from './scripts/picture'
import PictureTreeDocs from './PictureTreeDocs.vue'
import PictureUpload from './PictureUpload.vue'
import PictureViewerInfo from './PictureViewerInfo.vue'
import PictureBatchDel from './PictureBatchDel.vue'
import PictureTransfer from './PictureTransfer.vue'
import errorImg from '@renderer/assets/imgs/img_error.png'
import Notify from '@renderer/scripts/notify'

const userStore = useUserStore()

useLifecycle(
  () => getPictureStat(),
  () => getPictureStat()
)
// 是否替换上传
const isReplaceUpload = ref(false)
const cardSize = ref('mini')

const cardClass = computed(() => {
  if (cardSize.value == 'large') {
    return 'picutre-card-large'
  }
  return 'picutre-card-mini'
})

//#region ----------------------------------------< 当前文件当前文件 >----------------------------
type PageParam = { pageNum: number; pageSize: number; pid: string; name: string; starStatus: number | undefined } // 分页对象类型
const curFolder = ref<DocInfo>() // 当前选中的文档, 包含文件夹和文章, 如果选中是文件夹, 则不会重置编辑器中的文章
const picturePageParam = ref<PageParam>({ pageNum: 1, pageSize: 10, pid: '0', name: '', starStatus: undefined }) // 列表参数
const picturePages = ref<Picture[]>([]) // 图片列表
const pictureStat = ref<any>({ cur: { picCount: 0, picSize: '0MB' }, global: { picCount: 0, picSize: '0MB' } })
// 依赖注入
provide(provideKeyDocInfo, curFolder)

// storePath 拼接服务器配置的根目录
const storePath = computed(() => {
  if (curFolder.value && curFolder.value.storePath) {
    return userStore.userinfo.osRes.defaultPath + '/U' + userStore.userinfo.id + curFolder.value.storePath
  }
  return userStore.userinfo.osRes.defaultPath + '/U' + userStore.userinfo.id
})

const curIsFolder = () => {
  if (isNull(curFolder)) {
    return false
  }
  if (isNull(curFolder.value)) {
    return false
  }
  return true
}

const getPictureStat = (pid?: string) => {
  pictureStatApi({ pid: pid }).then((resp) => {
    if (isNotNull(pid)) {
      pictureStat.value.cur.picCount = resp.data.pictureCount
      pictureStat.value.cur.picSize = formatFileSize(resp.data.pictureSize)
    } else {
      pictureStat.value.global.picCount = resp.data.pictureCount
      pictureStat.value.global.picSize = formatFileSize(resp.data.pictureSize)
    }
  })
}

/**
 * 点击 doc title 的回调, 用于选中某个文档
 *
 * @param tree
 */
const clickCurFolder = (tree: DocTree) => {
  const clickFolder = treeToInfo(tree)
  if (isNotNull(curFolder.value) && clickFolder.id === curFolder.value!.id) {
    return
  }
  curFolder.value = clickFolder
  picChecks.value.clear()
  checkedAll.value = false
  picturePageParam.value.pageNum = 1
  picturePageParam.value.pid = curFolder.value.id
  picturePages.value = [] // 在重新加载前清空，防止因加载慢而残留显示其他文件夹的图片
  picturePageApi(picturePageParam.value).then((resp) => {
    picturePages.value = resp.data.datas
  })
  getPictureStat(curFolder.value.id)
}

/**
 * 刷新
 */
const refresh = () => {
  if (!curFolder.value) {
    return
  }
  picturePageParam.value.pageNum = 1
  picturePages.value = []
  picturePageApi(picturePageParam.value).then((resp) => {
    picturePages.value = resp.data.datas
  })
  getPictureStat(curFolder.value.id)
}

/**
 * 下一页图片
 */
const nextPage = () => {
  if (!curIsFolder()) {
    return
  }
  picturePageParam.value.pageNum += 1
  picturePageApi(picturePageParam.value).then((resp) => {
    if (resp.data.pageNum < picturePageParam.value.pageNum) {
      picturePageParam.value.pageNum = resp.data.pageNum
      return
    }
    resp.data.datas.forEach((pic: Picture) => {
      picturePages.value.push(pic)
    })
  })
}

/**
 * 只查询星标图片
 */
const changeStarStatus = () => {
  if (picturePageParam.value.starStatus == undefined) {
    picturePageParam.value.starStatus = 1
  } else if (picturePageParam.value.starStatus == 1) {
    picturePageParam.value.starStatus = undefined
  }
  if (curIsFolder()) {
    picturePageParam.value.pageNum = 1
    picturePageParam.value.pid = curFolder.value!.id
    picturePageApi(picturePageParam.value).then((resp) => {
      picturePages.value = resp.data.datas
    })
  }
}

//#endregion

//#region ----------------------------------------< 图片卡片操作 >--------------------------------
const PictureViewerInfoRef = ref()

const showPicInfo = (url: string) => {
  PictureViewerInfoRef.value.showPicInfo(url)
}

/**
 * 图片查询失败时的处理
 * @param a
 */
const onErrorImg = (a: Event) => {
  let imgEle = a.target as HTMLImageElement
  if (imgEle) {
    imgEle.src = errorImg
    imgEle.classList.add('img-error')
    imgEle.style.width = '32px'
    imgEle.style.height = 'auto'
    if (imgEle.parentNode) {
      let imgWrapper: HTMLElement = imgEle.parentNode as HTMLElement
      imgWrapper.style.backgroundColor = 'var(--bl-bg-color)'
    }
  }
}

/**
 * 复制文章链接
 * @param url
 */
const copyUrl = (url: string) => {
  writeText(url)
  ElMessage.info({ message: '已复制链接', duration: 3000, offset: 10, grouping: true, icon: CopyDocument, customClass: 'bl-message' })
}

/**
 * 复制文章 Markdown 链接
 * @param url 路径
 * @param picName 图片名称
 * @param event event
 */
const copyMarkdownUrl = (url: string, picName: string, event: MouseEvent) => {
  event.preventDefault()
  writeText(`![${picName}](${url})`)
  ElMessage.info({ message: '已复制 MD 格式链接', duration: 3000, offset: 10, grouping: true, icon: CopyDocument, customClass: 'bl-message' })
}

/**
 * 星标或取消星标
 * @param pic 当前选中图片
 */
const starPicture = (pic: Picture) => {
  let param = {
    id: pic.id,
    starStatus: pic.starStatus == 1 ? 0 : 1
  }
  pictureStarApi(param).then((_resp) => {
    pic.starStatus = param.starStatus
  })
}

/**
 * 删除图片
 * @param pic 当前选中图片
 */
const deletePicture = (pic: Picture) => {
  ElMessageBox.confirm('删除图片可能会造成公网访问失效, 是否确定删除?', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    let articleCount = articleNamesToArray(pic.articleNames).length
    if (articleCount > 0) {
      ElNotification.error({
        title: '删除失败',
        dangerouslyUseHTMLString: true,
        message: `尚有[${articleCount}]篇文章正在引用该图片, 请先将文章中的图片引用删除后, 再删除图片! <br/>
          <span style="color:red">注意: 引用文章篇数[${articleCount}]为引用了该图片的草稿数, 并不会校验公开文章是否引用.</span>`,
        offset: 30,
        position: 'bottom-right'
      })
      return
    }
    let urlBak = pic.url
    pic.url = '1'
    pic.delTime = 1

    pictureDelApi({ id: pic.id })
      .then((_resp) => {
        pic.delTime = 2
        getPictureStat(curFolder.value?.id)
        getPictureStat()
      })
      .catch((_) => {
        pic.delTime = 0
        pic.url = urlBak
      })
  })
}

//#endregion

//#region ----------------------------------------< 二级操作台 >----------------------------------
const workbencStyle = ref({
  workbench1: { height: '50px' },
  workbench2: { height: '0', visibility: 'hidden', opacity: 0 },
  cards: { height: 'calc(100% - 50px - 28px - 15px)' }
})
const isExpandWorkbench = ref(false)

const handleBenchworkStyle = () => {
  isExpandWorkbench.value = !isExpandWorkbench.value
  if (isExpandWorkbench.value) {
    // 展开
    workbencStyle.value = {
      workbench1: { height: '90px' },
      workbench2: { height: '40px', visibility: 'visible', opacity: 1 },
      cards: { height: 'calc(100% - 90px - 28px - 15px)' }
    }
  } else {
    // 收起
    workbencStyle.value = {
      workbench1: { height: '50px' },
      workbench2: { height: '0', visibility: 'hidden', opacity: 0 },
      cards: { height: 'calc(100% - 50px - 28px - 15px)' }
    }
  }
}

// 图片多选
const picChecks = ref<Set<string>>(new Set())
const delIgnoreCheck = ref(false)

/** 选中全部图片 */
const checkedAll = ref(false)
const handlCheckedAll = (checked: boolean) => {
  if (checked) {
    picturePages.value.forEach((ele) => {
      ele.checked = true
      picChecks.value.add(ele.id)
    })
  } else {
    picturePages.value.forEach((ele) => {
      ele.checked = false
      picChecks.value.delete(ele.id)
    })
  }
}

/** 图片选中 */
const picCheckChange = (check: boolean, id: string) => {
  if (check) {
    picChecks.value.add(id)
  } else {
    picChecks.value.delete(id)
  }
}

const picCheckRightClick = (doc: Picture, event: MouseEvent) => {
  if (!isExpandWorkbench.value) {
    return
  }
  if (doc.checked) {
    doc.checked = false
    picChecks.value.delete(doc.id)
  } else {
    doc.checked = true
    picChecks.value.add(doc.id)
  }
  event.preventDefault()
}

//#endregion

//#region ----------------------------------------< 批量删除 >----------------------------------
const isShowBatchDelDialog = ref(false)

const delBatch = () => {
  if (picChecks.value.size == 0) {
    Notify.info('请先选中图片', '提示')
    return
  }
  delIgnoreCheck.value = false
  isShowBatchDelDialog.value = true
}

const delBatchIgnoreCheck = () => {
  if (picChecks.value.size == 0) {
    Notify.info('请先选中图片', '提示')
    return
  }
  delIgnoreCheck.value = true
  isShowBatchDelDialog.value = true
}

const deleted = (ids: Array<string>) => {
  picChecks.value.clear()
  checkedAll.value = false
  for (let i = 0; i < picturePages.value.length; i++) {
    const pic = picturePages.value[i]
    if (ids.includes(pic.id)) {
      pic.url = '1'
      pic.delTime = 2
    }
    pic.checked = false
  }
}

//#endregion

//#region ----------------------------------------< 移动至其他文件夹 >----------------------------------

const isShowTransferDialog = ref(false)

const transfer = () => {
  if (picChecks.value.size == 0) {
    Notify.info('请先选中图片', '提示')
    return
  }
  isShowTransferDialog.value = true
}

const transferred = () => {
  picChecks.value.clear()
  checkedAll.value = false
  picturePageParam.value.pageNum = 1
  picturePageParam.value.pid = curFolder.value!.id
  picturePageApi(picturePageParam.value).then((resp) => {
    picturePages.value = resp.data.datas
  })
  getPictureStat(curFolder.value!.id)
  isShowTransferDialog.value = false
}

//#endregion

//#region ----------------------------------------< 左右拖拽 >----------------------------------
const DocsRef = ref()
const ResizeDividerRef = ref()
const PictureContainerRef = ref()
useResizeVertical(DocsRef, PictureContainerRef, ResizeDividerRef, undefined, {
  persistent: true,
  keyOne: 'picture_docs_width',
  keyTwo: 'picture_continer_width',
  defaultOne: '250px',
  defaultTwo: 'calc(100% - 250px)',
  maxOne: 700,
  minOne: 250
})
//#endregion
</script>

<style scoped lang="scss">
@import './styles/picture-index.scss';
@import '@renderer/assets/styles/bl-resize-divider.scss';
@import '@renderer/assets/styles/bl-loading-spinner.scss';

.replace-upload-switch {
  --el-switch-off-color: var(--el-color-primary-light-7);
  height: 24px;

  :deep(.el-switch__core) {
    border-radius: 4px;
  }

  :deep(.is-text) {
    color: #a9a9a9;
  }

  :deep(.el-switch__action) {
    @include themeBg(#ffffff, #a9a9a9);
    border-radius: 4px;
  }
}

.is-checked {
  :deep(.is-text) {
    color: #fff;
  }

  :deep(.el-switch__action) {
    background-color: #ffffff !important;
  }
}
</style>
