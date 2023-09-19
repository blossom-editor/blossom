<template>
  <div class="index-picture-root">

    <!-- folder menu -->
    <div class="doc-container">

      <div class="doc-tree-menu-container">
        <PictureTreeDocs @click-doc="clickCurFolder"></PictureTreeDocs>
      </div>

      <div class="doc-upload">
        <PictureUpload></PictureUpload>
      </div>
    </div>

    <!-- editor -->
    <div class="picture-container">
      <div class="picutre-workbench">
        <div class="workbench-group">
          <div class="star">
            <div v-if="picuturePageParam.starStatus == undefined" class="iconbl bl-star-line" @click="changeStarStatus">
            </div>
            <div v-else class="iconbl bl-star-fill" @click="changeStarStatus"></div>
          </div>

          <!-- 显式收藏 -->
          <div class="radio">
            <div>卡片大小</div>
            <el-radio-group v-model="cardSize">
              <el-radio-button label="mini">小</el-radio-button>
              <el-radio-button label="large">大</el-radio-button>
            </el-radio-group>
          </div>

          <!-- <div class="radio">
            <div>图片清晰度</div>
            <el-radio-group v-model="resolution">
              <el-radio-button label="low">低</el-radio-button>
              <el-radio-button label="hight">高</el-radio-button>
              <el-radio-button label="orgin">原始</el-radio-button>
            </el-radio-group>
          </div> -->
        </div>

        <div class="workbench-group">
          <div class="statistic">
            <div>《{{ curFolder?.name }}》</div>
            <div>{{ pictureStat.cur.picCount }} P / {{ pictureStat.cur.picSize }}</div>
          </div>
          <div class="statistic">
            <div>图片总览</div>
            <div>{{ pictureStat.global.picCount }} P / {{ pictureStat.global.picSize }}</div>
          </div>
        </div>
      </div>

      <div class="picture-card-container">
        <div :class="['picture-card', cardClass]" v-for="pic in picturePages">
          <el-image style="width: 100%;height: 100%;" :src="pic.url + pictureCompressParam" fit="cover"
            :preview-src-list="[pic.url]" :preview-teleported="true">
            <template #error>
              <div class="img-error" style="">
                {{ pic.delTime == 2 ? '已删除' : pic.delTime == 1 ? '删除中' : '无法查看' }}
              </div>
            </template>
          </el-image>
          <div class="picuter-card-workbench">
            <el-tooltip placement="bottom" trigger="click" :hide-after="0">
              <template #content>
                <div style="max-width: 300px;white-space:break-spaces;word-wrap: break-word;word-break:break-all;">
                  <bl-row style="word-wrap: break-word;">图片名称: {{ pic.name }}</bl-row>
                  <bl-row>图片大小: {{ formatFileSize(pic.size) }}</bl-row>
                  <bl-row style="word-wrap: break-word;">上传时间: {{ pic.creTime }}</bl-row>
                  <bl-row style="word-wrap: break-word;">图片路径: {{ pic.pathName }}</bl-row>
                  <bl-row v-if="!isEmpty(pic.articleNames)" align="flex-start">引用文章:
                    <div>
                      <div v-for="aname in articleNamesToArray(pic.articleNames)" style="margin-left: -13px;">
                        《{{ aname }}》
                      </div>
                    </div>
                  </bl-row>
                </div>
              </template>
              <div class="item iconbl bl-problem-line"></div>
            </el-tooltip>
            <div class="item iconbl bl-copy-line" @click="writeTextTo(pic.url)"></div>
            <div class="item iconbl bl-a-clouddownload-line" @click="download(pic.url)"></div>
            <div v-if="pic.starStatus == 0" class="item iconbl bl-star-line" @click="starPicture(pic)"></div>
            <div v-else-if="pic.starStatus == 1" class="item iconbl bl-star-fill" @click="starPicture(pic)"></div>
            <div class="item iconbl bl-delete-line" @click="deletePicture(pic)"></div>
          </div>
        </div>

        <div class="picuter-card-next">
          <el-button type="info" plain style="width: 100px;" @click="nextPage">下一页</el-button>
        </div>
      </div>
    </div>

  </div>
</template>
<script setup lang="ts">
// vue
import { ref, provide, computed, onActivated } from "vue"
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus'
import { CopyDocument } from '@element-plus/icons-vue'
import { picturePageApi, pictureStarApi, pictureDelApi, pictureStatApi } from '@renderer/api/blossom'
import { treeToInfo, provideKeyDocInfo } from '@renderer/views/doc/doc'
import { isEmpty } from 'lodash'
import { formatFileSize } from '@renderer/assets/utils/util'
import { writeText, download } from '@renderer/assets/utils/electron'

// component
import { Picture } from './scripts/picture'
import PictureTreeDocs from "./PictureTreeDocs.vue"
import PictureUpload from "./PictureUpload.vue"
import { isBlank, isNotBlank, isNotNull, isNull } from "@renderer/assets/utils/obj"

onActivated(() => {
  getPictureStat()
})

const cardSize = ref('mini');
const resolution = ref('hight')
const cardClass = computed(() => {
  if (cardSize.value == 'large') {
    return 'picutre-card-large'
  }
  return 'picutre-card-mini'
})
// 图片列表图片请求地址的参数
const pictureCompressParam = computed(() => {
  if (resolution.value == 'low') {
    return '?scale=0.2&quality=0.2'
  }
  if (resolution.value == 'hight') {
    return '?scale=0.5&quality=0.5'
  }
  return ''
})
//#region ----------------------------------------< 当前文件当前文件 >----------------------------
const curFolder = ref<DocInfo>()       // 当前选中的文档, 包含文件夹和文章, 如果选中是文件夹, 则不会重置编辑器中的文章
type PageParam = { pageNum: number, pageSize: number, pid: number, name: string, starStatus: number | undefined } // 分页对象类型
// 列表参数
const picuturePageParam = ref<PageParam>({
  pageNum: 1,
  pageSize: 10,
  pid: 0,
  name: '',
  starStatus: undefined
})
// 图片列表
const picturePages = ref<Picture[]>([])
const pictureStat = ref<any>({
  cur: { picCount: 0, picSize: '0 MB' },
  global: { picCount: 0, picSize: '0 MB' }
})
// 依赖注入
provide(provideKeyDocInfo, curFolder)

const curIsFolder = () => {
  if (isNull(curFolder)) { return false }
  if (isNull(curFolder.value)) { return false }
  return true
}

const getPictureStat = (pid?: number) => {
  pictureStatApi({ pid: pid }).then(resp => {
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
 * @param tree
 */
const clickCurFolder = (tree: DocTree) => {
  curFolder.value = treeToInfo(tree)
  picuturePageParam.value.pageNum = 1
  picuturePageParam.value.pid = curFolder.value.id
  picturePageApi(picuturePageParam.value).then(resp => {
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
  picuturePageParam.value.pageNum += 1
  picturePageApi(picuturePageParam.value).then(resp => {
    if (resp.data.pageNum < picuturePageParam.value.pageNum) {
      picuturePageParam.value.pageNum = resp.data.pageNum
      return
    }
    resp.data.datas.forEach((pic: Picture) => {
      picturePages.value.push(pic)
    });
  })
}

/**
 * 只查询星标图片
 */
const changeStarStatus = () => {
  if (picuturePageParam.value.starStatus == undefined) {
    picuturePageParam.value.starStatus = 1
  } else if (picuturePageParam.value.starStatus == 1) {
    picuturePageParam.value.starStatus = undefined
  }
  if (curIsFolder()) {
    picuturePageParam.value.pageNum = 1
    picuturePageParam.value.pid = curFolder.value?.id as number
    picturePageApi(picuturePageParam.value).then(resp => {
      picturePages.value = resp.data.datas
    })
  }
}

//#region ----------------------------------------< 图片卡片操作 >------------------------------------------

const writeTextTo = (url: string) => {
  writeText(url)
  ElMessage.info({ message: '已复制', duration: 3000, offset: 10, grouping: true, icon: CopyDocument, customClass: 'bl-message' })
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
  pictureStarApi(param).then(_resp => {
    pic.starStatus = param.starStatus
  })
}

/**
 * 删除图片
 * @param pic 当前选中图片
 */
const deletePicture = (pic: Picture) => {
  ElMessageBox.confirm('删除图片可能会造成公网访问失效, 是否确定删除?', { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning' })
    .then(() => {
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
      pic.url = "1"
      pic.delTime = 1

      pictureDelApi({ id: pic.id })
        .then(_resp => {
          pic.delTime = 2
          getPictureStat(curFolder.value?.id)
          getPictureStat()
        }).catch(_ => {
          pic.delTime = 0
          pic.url = urlBak
        })
    })
}

const articleNamesToArray = (names: string): string[] => {
  if (isBlank(names)) {
    return []
  }
  let result = names.split(',').filter(name => isNotBlank(name))
  return result
}

//#endregion
</script>

<style scoped lang="scss">
@import './styles/picture-index.scss';
@import '@renderer/assets/styles/bl-loading-spinner.scss';

:deep(.el-loading-spinner) {
  @extend .bl-loading-spinner;
}
</style>