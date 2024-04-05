<template>
  <div class="editor-tools-root">
    <div class="tomato-bell" ref="TomatoBellRef"></div>
    <div class="tools-container">
      <!-- 文章的操作 -->
      <el-tooltip
        content="保存"
        popper-class="is-small"
        effect="light"
        placement="top"
        transition="none"
        :hide-after="0"
        :show-arrow="false"
        :offset="10">
        <div class="iconbl bl-a-texteditorsave-line" @click="emits('save')"></div>
      </el-tooltip>
      <el-tooltip
        content="开启/关闭全屏预览"
        popper-class="is-small"
        effect="light"
        placement="top"
        transition="none"
        :hide-after="0"
        :show-arrow="false"
        :offset="10">
        <div class="iconbl bl-eye-line" @click="emits('previewFullScreen')"></div>
      </el-tooltip>
      <el-tooltip
        content="开启/关闭全屏编辑"
        popper-class="is-small"
        effect="light"
        placement="top"
        transition="none"
        :hide-after="0"
        :show-arrow="false"
        :offset="10">
        <div class="iconbl bl-expansion-line" @click="emits('editorFullScreen')"></div>
      </el-tooltip>

      <!--  -->
      <div class="divider"></div>
      <el-tooltip
        content="加粗"
        popper-class="is-small"
        effect="light"
        placement="top"
        transition="none"
        :hide-after="0"
        :show-arrow="false"
        :offset="10">
        <div class="iconbl bl-bold" @click="emits('bold')"></div>
      </el-tooltip>
      <el-tooltip
        content="斜体"
        popper-class="is-small"
        effect="light"
        placement="top"
        transition="none"
        :hide-after="0"
        :show-arrow="false"
        :offset="10">
        <div class="iconbl bl-italic" @click="emits('italic')"></div>
      </el-tooltip>
      <el-tooltip
        content="删除线"
        popper-class="is-small"
        effect="light"
        placement="top"
        transition="none"
        :hide-after="0"
        :show-arrow="false"
        :offset="10">
        <div class="iconbl bl-strikethrough" @click="emits('strike')"></div>
      </el-tooltip>
      <el-tooltip
        content="上标"
        popper-class="is-small"
        effect="light"
        placement="top"
        transition="none"
        :hide-after="0"
        :show-arrow="false"
        :offset="10">
        <div class="iconbl bl-a-texteditorsuperscript-line" @click="emits('sup')"></div>
      </el-tooltip>
      <el-tooltip
        content="下标"
        popper-class="is-small"
        effect="light"
        placement="top"
        transition="none"
        :hide-after="0"
        :show-arrow="false"
        :offset="10">
        <div class="iconbl bl-a-texteditorsubscript-line" @click="emits('sub')"></div>
      </el-tooltip>
      <el-tooltip
        content="删除线"
        popper-class="is-small"
        effect="light"
        placement="top"
        transition="none"
        :hide-after="0"
        :show-arrow="false"
        :offset="10">
        <div class="iconbl bl-separator" @click="emits('separator')"></div>
      </el-tooltip>

      <!--  -->
      <div class="divider"></div>
      <el-tooltip
        content="引用"
        popper-class="is-small"
        effect="light"
        placement="top"
        transition="none"
        :hide-after="0"
        :show-arrow="false"
        :offset="10">
        <el-dropdown>
          <div class="iconbl bl-a-rightsmallline-line"></div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="emits('blockquote')">⚪ Normal</el-dropdown-item>
              <el-dropdown-item @click="emits('blockquoteBlock')">⚫ Black</el-dropdown-item>
              <el-dropdown-item @click="emits('blockquoteGreen')">🟢 Green</el-dropdown-item>
              <el-dropdown-item @click="emits('blockquoteYellow')">🟡 Yellow</el-dropdown-item>
              <el-dropdown-item @click="emits('blockquoteRed')">🔴 Red</el-dropdown-item>
              <el-dropdown-item @click="emits('blockquoteBlue')">🔵 Blue</el-dropdown-item>
              <el-dropdown-item @click="emits('blockquotePurple')">🟣 Purple</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-tooltip>
      <el-tooltip
        content="行内代码块"
        popper-class="is-small"
        effect="light"
        placement="top"
        transition="none"
        :hide-after="0"
        :show-arrow="false"
        :offset="10">
        <div class="iconbl bl-single-quotes-r" @click="emits('code')"></div>
      </el-tooltip>
      <el-tooltip
        content="多行代码块"
        popper-class="is-small"
        effect="light"
        placement="top"
        transition="none"
        :hide-after="0"
        :show-arrow="false"
        :offset="10">
        <div class="iconbl bl-double-quotes-r" @click="emits('pre')"></div>
      </el-tooltip>
      <el-tooltip
        content="单选框"
        popper-class="is-small"
        effect="light"
        placement="top"
        transition="none"
        :hide-after="0"
        :show-arrow="false"
        :offset="10">
        <div class="iconbl bl-a-underbox-line" @click="emits('checkbox')"></div>
      </el-tooltip>
      <el-tooltip
        content="无序列表"
        popper-class="is-small"
        effect="light"
        placement="top"
        transition="none"
        :hide-after="0"
        :show-arrow="false"
        :offset="10">
        <div class="iconbl bl-list-unordered" @click="emits('unordered')"></div>
      </el-tooltip>
      <el-tooltip
        content="有序列表"
        popper-class="is-small"
        effect="light"
        placement="top"
        transition="none"
        :hide-after="0"
        :show-arrow="false"
        :offset="10">
        <div class="iconbl bl-list-ordered" @click="emits('ordered')"></div>
      </el-tooltip>

      <!-- 表格及其他 -->
      <div class="divider"></div>
      <el-tooltip
        content="表格"
        popper-class="is-small"
        effect="light"
        placement="top"
        transition="none"
        :hide-after="0"
        :show-arrow="false"
        :offset="10">
        <div class="iconbl bl-table-" @click="emits('table')"></div>
      </el-tooltip>
      <el-tooltip
        content="图片链接"
        popper-class="is-small"
        effect="light"
        placement="top"
        transition="none"
        :hide-after="0"
        :show-arrow="false"
        :offset="10">
        <div class="iconbl bl-image--line" @click="emits('image')"></div>
      </el-tooltip>
      <el-tooltip
        content="链接"
        popper-class="is-small"
        effect="light"
        placement="top"
        transition="none"
        :hide-after="0"
        :show-arrow="false"
        :offset="10">
        <div class="iconbl bl-link-m" @click="emits('link')"></div>
      </el-tooltip>

      <!-- 其他工具 -->
      <div class="divider"></div>
      <el-tooltip
        content="快捷键说明"
        popper-class="is-small"
        effect="light"
        placement="top"
        transition="none"
        :hide-after="0"
        :show-arrow="false"
        :offset="10">
        <div class="iconbl bl-jianpan-xianxing" @click="handleShowHotKeyDialog"></div>
      </el-tooltip>

      <!-- 番茄 -->
      <el-popover placement="bottom" :width="220" trigger="click" popper-style="padding:0;">
        <template #reference>
          <div class="iconbl bl-fanqiezhong"></div>
        </template>
        <template #default>
          <div style="padding: 5px 10px; font-size: 18px; margin-bottom: 15px; border-bottom: 1px solid var(--el-border-color)">
            <span class="iconbl bl-fanqiezhong" style="font-size: 18px; padding-right: 6px; color: #ec7259"></span>番茄时钟
          </div>
          <bl-row style="padding: 0 10px">
            <span>时长分钟：</span>
            <el-input-number v-model="duration"></el-input-number>
          </bl-row>
          <bl-row style="padding: 0 10px; margin-top: 15px">
            <span>结束时间：</span>
            <span style="font-size: 11px">{{ endTime }}</span>
          </bl-row>
          <bl-row just="space-between" style="padding: 8px 10px; margin-top: 15px; border-top: 1px solid var(--el-border-color)">
            <el-button @click="stop">停止番茄钟</el-button>
            <el-button @click="start" type="primary">开始</el-button>
          </bl-row>
        </template>
      </el-popover>
      <div style="font-size: 12px; padding: 4px 5px">{{ remainStr }}</div>
    </div>
  </div>

  <!-- 导入 -->
  <el-dialog
    v-model="isShowHotKeyDialog"
    class="bl-dialog-fixed-body"
    width="500"
    style="height: 80%"
    :align-center="true"
    :destroy-on-close="true"
    :close-on-click-modal="true">
    <ShortcutKeyDesc></ShortcutKeyDesc>
  </el-dialog>
</template>

<script lang="ts" setup>
import { computed, onUnmounted, ref } from 'vue'
import { secondsToDatetime, formateMs } from '@renderer/assets/utils/util'
import { ElNotification } from 'element-plus'
import ShortcutKeyDesc from './ShortcutkeyDesc.vue'

const emits = defineEmits([
  'save',
  'editorFullScreen',
  'previewFullScreen',
  'bold',
  'italic',
  'strike',
  'sup',
  'sub',
  'separator',
  'blockquote',
  'blockquoteBlock',
  'blockquoteGreen',
  'blockquoteYellow',
  'blockquoteRed',
  'blockquoteBlue',
  'blockquotePurple',
  'code',
  'pre',
  'unordered',
  'ordered',
  'checkbox',
  'table',
  'image',
  'link'
])

onUnmounted(() => {
  stop()
})

/**
 * 预览番茄钟到期时间
 */
const endTime = computed(() => {
  return secondsToDatetime(Date.now() / 1000 + duration.value * 60)
})

const TomatoBellRef = ref()
// 番茄钟时长(分钟)
const duration = ref(30)
// 番茄钟的展示信息,
const remainStr = ref('00:00:00')
// 番茄钟参数, 开始时间, 时长(毫秒)
let param = { start: 0, duration: 0 }
let bell: NodeJS.Timeout

/**
 * 番茄钟开始
 */
const start = () => {
  clearInterval(bell)
  param.start = Date.now()
  param.duration = duration.value * 60 * 1000
  bell = setInterval(() => {
    const now = Date.now()
    const remain = param.start + param.duration - now
    remainStr.value = formateMs(Math.max(remain, 0))
    TomatoBellRef.value.style.transform = `translateX(${100 - (remain / param.duration) * 100}%)`
    if (remain <= 0) {
      stop()
      ElNotification.success({
        title: '番茄时钟',
        dangerouslyUseHTMLString: true,
        message: `<span class="iconbl bl-fanqiezhong" style="font-size: 16px;padding-right: 6px;color: #EC7259;"></span>番茄时钟已结束`,
        offset: 30,
        position: 'bottom-right'
      })
    }
  }, 1000)
}

/**
 * 番茄钟结束
 */
const stop = () => {
  clearInterval(bell)
  remainStr.value = formateMs(0)
  if (TomatoBellRef.value) {
    TomatoBellRef.value.style.transform = `translateX(100%)`
  }
}

const isShowHotKeyDialog = ref<boolean>(false)

const handleShowHotKeyDialog = () => {
  isShowHotKeyDialog.value = true
}
</script>

<style scoped lang="scss">
.editor-tools-root {
  @include box(calc(100% - 20px), 35px);
  @include themeShadow(0 1px 4px 1px #d3d3d3, 0 1px 4px 1px rgb(20, 20, 20));
  border-radius: 5px;
  position: relative;
  overflow: hidden;

  .tomato-bell {
    @include box(100%, 100%);
    background-color: var(--el-color-primary-light-5);
    transform: translateX(100%);
    position: absolute;
    right: 0;
    border-radius: 5px;
    transition: 0.3s;
    z-index: 1;
  }

  .tools-container {
    @include box(100%, 100%);
    @include flex(row, flex-start, center);
    @include themeBg(#efefef80, #8a8a8a4a);
    padding: 0 5px;
    position: absolute;
    z-index: 2;
    overflow: hidden;

    & > div {
      @include themeColor(#5b5b5b, #909090);
      @include themeBorder(1px, #a7a7a7, #1c1c1c);
      margin: 0 5px;
      padding: 3px;
      border-radius: 3px;
      transition: 0.3s;
      cursor: pointer;

      &:hover {
        background-color: var(--el-color-primary);
        color: #ffffff;
      }
    }

    .divider {
      @include box(2px, 60%);
      padding: 0;
      border: 0;
    }
  }
}
</style>
<!--
快捷键说明为弹出框 需要设置全局的样式
-->
<style lang="scss" scoped>
.editor-tools-content {
  @include flex(column, flex-start, flex-start);
  color: var(--bl-text-color);

  .editor-tools-col {
    @include flex(column, flex-start, center);
  }

  .editor-tools-col.left {
    padding-right: 10px;
    border-right: 1px solid var(--el-border-color);
  }

  .editor-tools-col.right {
    margin-left: 10px;
  }

  .info-title {
    @include font(15px, 500);
    width: 100%;
    border-bottom: 1px solid var(--el-border-color);
    padding: 5px;
    margin-bottom: 10px;
  }

  .iconbl {
    margin-right: 10px;
    border: 1px solid var(--el-border-color);
    padding: 1px 3px;
    border-radius: 3px;
  }

  .label {
    @include box(48px, 20px);
    text-align: justify;
    margin-right: 10px;

    &:after {
      display: inline-block;
      width: 100%;
      content: '';
    }
  }
}
</style>
