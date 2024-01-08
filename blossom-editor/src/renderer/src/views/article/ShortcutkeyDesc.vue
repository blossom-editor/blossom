<template>
  <div class="key-desc-root">
    <div class="info-title">
      <div class="iconbl bl-jianpan-xianxing"></div>
      文章编辑快捷键
    </div>

    <div class="content">
      <div class="kind">页面快捷键</div>
      <bl-row v-for="key in keymapsView" :key="key.name">
        <div>
          <span :class="['iconbl', key.icon]"></span>
          <span class="label">{{ key.name }}</span>
        </div>
        <div class="keyboard">{{ key.key }}</div>
      </bl-row>

      <div class="kind">工具栏快捷键</div>
      <bl-row v-for="key in keymapsTool" :key="key.name">
        <div>
          <span :class="['iconbl', key.icon]"></span>
          <span class="label">{{ key.name }}</span>
        </div>
        <div class="keyboard">{{ key.key }}</div>
      </bl-row>

      <div class="kind">编辑器快捷键</div>
      <bl-row v-for="key in keymapsEditor" :key="key.name">
        <div>
          <span :class="['iconbl', key.icon]"></span>
          <span class="label">{{ key.name }}</span>
        </div>
        <div class="keyboard">{{ key.key }}</div>
      </bl-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { keymaps } from './scripts/editor-tools'

const keymapsView = [
  { icon: 'bl-a-eyeclose-line', name: '隐藏左侧菜单', key: keymaps.hideDocs },
  { icon: 'bl-a-filetext-line', name: '折叠浮动目录', key: keymaps.hideToc },
  { icon: 'bl-search-line', name: '全文搜索', key: keymaps.fullSearch },
  { icon: 'bl-and', name: '全文搜索时匹配所有关键字', key: keymaps.fullSearchOperatorAnd },
  { icon: 'bl-transcript-line', name: '格式化 Markdown 文章', key: keymaps.formatAll },
  { icon: 'bl-a-fileadd-line', name: '打开新增文件夹或文章窗口', key: keymaps.newDoc }
]

const keymapsTool = [
  { icon: 'bl-a-texteditorsave-line', name: '保存内容', key: keymaps.save },
  { icon: 'bl-eye-line', name: '全屏预览', key: keymaps.fullViewer },
  { icon: 'bl-expansion-line', name: '全屏编辑', key: keymaps.fullEditor },

  { icon: 'bl-bold', name: '加粗', key: keymaps.blod },
  { icon: 'bl-italic', name: '斜体', key: keymaps.italic },
  { icon: 'bl-strikethrough', name: '删除线', key: keymaps.striket },
  { icon: 'bl-a-texteditorsuperscript-line', name: '上标(sup)', key: keymaps.sup },
  { icon: 'bl-a-texteditorsubscript-line', name: '下标(sub)', key: keymaps.sub },
  { icon: 'bl-separator', name: '分割线', key: keymaps.separator },

  { icon: 'bl-a-rightsmallline-line', name: '引用', key: '>' },
  { icon: 'bl-single-quotes-r', name: '行内代码', key: keymaps.code },
  { icon: 'bl-double-quotes-r', name: '多行代码', key: keymaps.pre },
  { icon: 'bl-a-underbox-line', name: '单选框', key: '- []' },
  { icon: 'bl-list-unordered', name: '无序列表', key: '-' },
  { icon: 'bl-list-ordered', name: '有序列表', key: '1.' },

  { icon: 'bl-table-', name: '插入表格', key: keymaps.table },
  { icon: 'bl-image--line', name: '添加图片，也可拖入或黏贴', key: keymaps.image },
  { icon: 'bl-link-m', name: '添加链接', key: keymaps.link }
]

const keymapsEditor = [
  { icon: 'bl-a-listview-line', name: '列模式', key: keymaps.listView },
  { icon: 'bl-article-line', name: '光标聚焦多行', key: keymaps.selectMultLine },
  { icon: 'bl-search-line', name: '查找', key: keymaps.search },
  // { icon: 'bl-switch-line', name: '替换', key: keymaps.replace },
  { icon: 'bl-a-icon_jiandaojianqie', name: '剪切整行', key: keymaps.cut },
  { icon: 'bl-a-texteditorforward-line', name: '恢复', key: keymaps.redo },
  { icon: 'bl-a-texteditorback-line', name: '撤销', key: keymaps.undo },

  { icon: 'bl-transcript-line', name: '注释', key: keymaps.comment },
  { icon: 'bl-indent-decrease', name: '向前缩进', key: keymaps.retractionFront },
  { icon: 'bl-indent-increase', name: '向后缩进', key: keymaps.retractionBack },

  { icon: 'bl-a-radiochoose-line', name: '选中该行', key: keymaps.selectLine },
  { icon: 'bl-a-rightto-line', name: '前往行数', key: keymaps.toLine },
  { icon: 'bl-a-online-line', name: '行上移', key: keymaps.moveLineUp },
  { icon: 'bl-a-underline-line', name: '行下移', key: keymaps.moveLineDown },
  { icon: 'bl-a-doubleonline-line', name: '复制到上方', key: keymaps.copyLineUp },
  { icon: 'bl-a-doubleunderline-line', name: '复制到下方', key: keymaps.copyLineDown }
]
</script>

<style scoped lang="scss">
@import '@renderer/assets/styles/bl-dialog-info';

.key-desc-root {
  @include box(100%, 100%);
  border-radius: 10px;

  .content {
    height: calc(100% - 60px);
    padding: 0 10px;
    overflow: scroll;
    font-weight: 300;

    .kind {
      font-size: 16px;
      margin: 10px 0;
      color: var(--el-color-primary);
      text-align: center;
    }

    .bl-row-root {
      justify-content: space-between !important;
      border-radius: 4px;
      padding: 0 10px;

      &:hover {
        @include themeBg(#ececec, #171717);
      }
    }

    .iconbl {
      padding-right: 10px;
    }

    .label {
      width: 270px;
    }

    .keyboard {
      margin-top: 3px;
      font-size: 12px;
    }
  }
}
</style>
