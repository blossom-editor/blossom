<template>
  <div :class="['doc-title', props.trees.t?.includes('subject') ? 'subject-title' : '']"
    :style="{ fontSize: size + 'px' }" @click="handlClick" @click.right="handleClickRight">

    <div class="doc-name">
      <svg v-if="isNotBlank(props.trees.icon)" class="icon menu-icon" aria-hidden="true">
        <use :xlink:href="'#' + props.trees.icon"></use>
      </svg>
      {{ props.trees.n }}
    </div>
    <!-- 如果专题是公开的, 则单独显示公开标签 -->
    <bl-tag v-if="props.trees.o === 1 && isFolder" :bg-color="'#7AC20C'" :icon="'bl-cloud-line'"></bl-tag>
    <div v-for="tag in tags">
      <bl-tag v-if="tag.content" :bg-color="tag.bgColor" :icon="tag.icon">{{ tag.content }}</bl-tag>
      <bl-tag v-else :bg-color="tag.bgColor" :icon="tag.icon" />
    </div>
    <div v-for="line, index in tagLins" :key="line" :class="[line]" :style="{ left: (-1 * (index + 1) * 5) + 'px' }">
    </div>

    <!-- 右键菜单, 添加到 body 下 -->
    <Teleport to="body">
      <div v-if="rMenu.show" class="doc-tree-right-menu"
        :style="{ left: rMenu.clientX + 'px', top: rMenu.clientY + 'px' }">
        <div class="doc-name">{{ props.trees.n }}</div>
        <div class="menu-content">
          <div class="menu-item" @click="handleShowDocInfoDialog('upd')">
            <span class="iconbl bl-a-fileedit-line"></span>编辑文档
          </div>
          <div class="menu-item" @click="sync()">
            <span class="iconbl bl-a-cloudrefresh-line"></span>同步文档
          </div>
          <div class="menu-item" @click="handleShowDocInfoDialog('add', props.trees.p)">
            <span class="iconbl bl-a-fileadd-line"></span>新增<strong>同级</strong>文档
          </div>
          <!-- 只有文件夹才有子文档 -->
          <div :class="['menu-item', props.trees.ty === 3 ? 'disabled' : '']"
            @click="handleShowDocInfoDialog('add', props.trees.i)">
            <span class="iconbl bl-a-fileadd-fill"></span>新增<strong>子级</strong>文档
          </div>
          <!-- 只有文件夹才有子文档 -->
          <!-- <div class="menu-item" @click="handleShowDocInfoDialog('add', props.trees.i)">
            <span class="iconbl bl-a-fileprohibit-line"></span>删除文档
          </div> -->
          <div :class="['menu-item', props.trees.ty === 1 ? 'disabled' : '']" @click="createUrl('link')">
            <span class="iconbl bl-correlation-line"></span>复制引用
          </div>
          <div class="menu-item-divider"></div>
          <div :class="['menu-item', props.trees.ty === 1 ? 'disabled' : '']" @click="openArticleWindow">
            <span class="iconbl bl-a-computerend-line"></span>新窗口打开
          </div>
          <div :class="['menu-item', props.trees.ty === 1 || !isOpen ? 'disabled' : '']" @click="createUrl('open')">
            <span class="iconbl bl-planet-line"></span>浏览器打开
          </div>
          <div :class="['menu-item', props.trees.ty === 1 ? 'disabled' : '']" @click="articleDownload">
            <span class="iconbl bl-a-clouddownload-line"></span>下载文章
          </div>
          <div :class="['menu-item', props.trees.ty === 1 || !isOpen ? 'disabled' : '']" @click="createUrl('copy')">
            <span class="iconbl bl-a-linkspread-line"></span>复制链接
          </div>
          <div :class="['menu-item', props.trees.ty === 1 || !isOpen ? 'disabled' : '']"
            @click="handleArticleQrCodeDialog()">
            <span class="iconbl bl-a-qrcode1-line"></span>查看二维码
          </div>
        </div>
      </div>
    </Teleport>
  </div>

  <!-- 详情的弹框 -->
  <el-dialog v-model="isShowDocInfoDialog" width="535" top="100px" style="margin-left: 65px;
    --el-dialog-padding-primary:0;
    --el-dialog-border-radius:10px;
    --el-dialog-box-shadow:var(--bl-box-shadow-dialog)" :append-to-body="true" :destroy-on-close="true"
    :close-on-click-modal="false" draggable>
    <ArticleInfo ref="ArticleInfoRef"></ArticleInfo>
  </el-dialog>

  <!-- 详情的弹框 -->
  <el-dialog v-model="isShowQrCodeDialog" width="335" top="100px" style="
    --el-dialog-padding-primary:0;
    --el-dialog-border-radius:10px;
    --el-dialog-box-shadow:var(--bl-box-shadow-dialog)" :append-to-body="true" :destroy-on-close="true"
    :close-on-click-modal="false" draggable>
    <ArticleQrCode ref="ArticleQrCodeRef"></ArticleQrCode>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, onBeforeUnmount, nextTick } from 'vue'
import type { PropType } from 'vue'
import { useUserStore } from '@renderer/stores/user'

import { grammar } from './markedjs'
import { articleDownloadApi, articleSyncApi } from '@renderer/api/blossom'
import { openExtenal, writeText, openNewArticleWindow } from '@renderer/assets/utils/electron'
import { isNotBlank } from '@renderer/assets/utils/obj'
import ArticleInfo from '@renderer/views/article/ArticleInfo.vue'
import ArticleQrCode from './ArticleQrCode.vue'
import Notify from '@renderer/components/Notify'

const userStore = useUserStore();

//#region ----------------------------------------< 标题信息 >--------------------------------------

const props = defineProps({
  trees: { type: Object as PropType<DocTree>, default: {} },
  size: {
    type: Number,
    default: 14
  }
})

//@ts-ignore
const isSubjectDoc = computed(() => {
  return props.trees.t?.includes('subject')
})

const isOpen = computed(() => {
  return props.trees.o === 1;
})

const isFolder = computed(() => {
  return props.trees.ty === 1 || props.trees.ty === 2;
})

/**
 * 计算标签, 并返回便签对象集合
 */
const tags = computed(() => {
  let icons: any = []
  props.trees.t?.forEach(tag => {
    if (tag === 'subject') {
      icons.unshift({ content: '专题', bgColor: 'salmon', icon: 'bl-a-lowerrightpage-line' })
    } else if (tag === 'toc') {
      icons.push({ content: 'TOC', bgColor: '#7274fa' })
    } else {
      icons.push({ content: tag })
    }
  });
  return icons;
})

const tagLins = computed(() => {
  let lines: string[] = []
  if (props.trees.star === 1) {
    lines.push('star-line')
  }
  if (props.trees.o === 1 && props.trees.ty === 3) {
    lines.push('open-line')
  }
  if (props.trees.vd === 1) {
    lines.push('sync-line')
  }
  return lines
})

/**
 * 点击文档菜单标题后的回调
 */
const handlClick = () => {
  emits('clickDoc', props.trees)
}

//#endregion

//#region ----------------------------------------< 右键菜单 >--------------------------------------
const rMenu = ref<RightMenu>({ show: false, clientX: 0, clientY: 0 })
const rMenuHeight = 275

onBeforeUnmount(() => {
  document.body.removeEventListener('click', closeMenuShow)
  document.body.removeEventListener('contextmenu', closeMenuShow)
})

const closeMenuShow = () => {
  rMenu.value.show = false
  document.body.removeEventListener('click', closeMenuShow)
  document.body.removeEventListener('contextmenu', closeMenuShow)
}

const handleClickRight = (event: MouseEvent) => {
  // TODO 固定的菜单高度, 每次增加右键菜单项时需要修改该值
  let y = event.clientY
  if (document.body.clientHeight - event.clientY < rMenuHeight) {
    y = event.clientY - rMenuHeight
  }
  rMenu.value = { show: true, clientX: event.clientX, clientY: y }
  setTimeout(() => {
    document.body.addEventListener('click', closeMenuShow)
    document.body.addEventListener('contextmenu', closeMenuShow)
  }, 100);
}

/**
 * 打开新页面, 文件夹(props.trees.ty == 1)无法使用新页面打开
 */
const openArticleWindow = () => {
  if (props.trees.ty === 1)
    return
  openNewArticleWindow(props.trees.n, props.trees.i);
}

/**
 * 使用浏览器打开公开链接, 或复制公开链接
 */
const createUrl = (type: 'open' | 'copy' | 'link') => {
  let url: string = userStore.userinfo.params.WEB_ARTICLE_URL + props.trees.i;
  if (type == 'open') {
    openExtenal(url)
  } else if (type == 'copy') {
    writeText(url)
  } else if (type == 'link') {
    url = `[${props.trees.n}](${userStore.userinfo.params.WEB_ARTICLE_URL + props.trees.i} "${grammar}${props.trees.i}${grammar}")`
    writeText(url)
  }
}

/**
 * 下载文章
 */
const articleDownload = () => {
  articleDownloadApi({ id: props.trees.i }).then(resp => {
    let filename: string = resp.headers.get('content-disposition')
    let filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
    let matches = filenameRegex.exec(filename);
    if (matches != null && matches[1]) {
      filename = decodeURI(matches[1].replace(/['"]/g, ''));
    }
    let a = document.createElement('a')
    let blob = new Blob([resp.data], { type: "text/plain" })
    let objectUrl = URL.createObjectURL(blob)
    a.setAttribute("href", objectUrl)
    a.setAttribute("download", filename)
    a.click()
  })
}

const sync = () => {
  articleSyncApi({ id: props.trees.i }).then((_resp) => {
    Notify.success('同步成功')
  })
}

const isShowQrCodeDialog = ref<boolean>(false);
const ArticleQrCodeRef = ref()

const handleArticleQrCodeDialog = () => {
  isShowQrCodeDialog.value = true
  nextTick(() => {
    ArticleQrCodeRef.value.getArticleQrCode(props.trees.n, props.trees.i)
  })
}
//#endregion 右键菜单

//#region ----------------------------------------< 新增修改详情弹框 >--------------------------------------

const ArticleInfoRef = ref()
const isShowDocInfoDialog = ref<boolean>(false)
/**
 * 显示弹框
 * @param dialogType 弹框的类型, 新增, 修改
 * @param pid 父级ID, 新增同级或子集文档时使用
 */
const handleShowDocInfoDialog = (dialogType: DocDialogType, pid?: number) => {
  if (props.trees.i < 0) {
    Notify.info('当前文档为系统默认文档, 无法操作')
    return
  }
  if (dialogType === 'upd' && (props.trees == undefined || props.trees?.i == undefined)) {
    Notify.info('请先选则要修改的文件夹或文档')
    return
  }
  isShowDocInfoDialog.value = true
  if (dialogType === 'add') {
    nextTick(() => { ArticleInfoRef.value.reload(dialogType, undefined, undefined, pid) })
  }
  if (dialogType === 'upd') {
    nextTick(() => {
      ArticleInfoRef.value.reload(dialogType, props.trees.i, props.trees.ty)
    })
  }
}
//#endregion

const emits = defineEmits(['clickDoc'])
</script>

<style scoped lang="scss">
$icon-size: 17px;

.menu-icon {
  @include box($icon-size, $icon-size, $icon-size, $icon-size, $icon-size, $icon-size);
  margin-right: 9px;
}

.doc-title {
  @include flex(row, flex-start, center);
  align-content: flex-start;
  flex-wrap: wrap;
  max-width: calc(100% - 15px);
  min-width: calc(100% - 15px);
  padding-bottom: 1px;
  position: relative;

  .doc-name {
    @include flex(row, flex-start, center);
    @include themeBrightness(100%, 80%);
    @include ellipsis();
  }
}

// 专题样式, 包括边框和文字样式
.subject-title {
  @include themeShadow(2px 2px 10px 1px #fad7d7, 1px 2px 10px 1px #0A0A0A);
  @include themeBg(linear-gradient(135deg, #fad7d7, #fae7e7, #ffffff), linear-gradient(135deg, #594A23, #453D28, #33302B));
  padding: 2px 5px;
  margin: 5px 0 10px 0;
  border-radius: 7px;
  position: relative;

  .doc-name {
    @include font(13px, 300);
    @include themeText(2px 2px 2px #D8D8D8, 2px 2px 2px #0A0A0A);
    @include ellipsis();
    color: var(--el-color-primary);
    min-width: 180px;
    max-width: 180px;
  }
}

// 在左侧显示
.open-line,
.star-line,
.sync-line {
  position: absolute;
  width: 2px;
  height: 60%;
  top: 20%;
  border-radius: 10px;
}

.star-line {
  background: rgb(237, 204, 11);
}

.open-line {
  background: #79C20C71;
}

.sync-line {
  background: #E8122479;
}
</style>