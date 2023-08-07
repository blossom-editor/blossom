<template>
  <div class="editor-tools-root">
    <!-- 文章的操作 -->
    <div class="iconbl bl-a-texteditorsave-line" @click="emits('save')"></div>
    <div class="iconbl bl-eye-line" @click="emits('previewFullScreen')"></div>
    <div class="iconbl bl-expansion-line" @click="emits('editorFullScreen')"></div>

    <!--  -->
    <div class="divider"></div>
    <div class="iconbl bl-bold" @click="emits('bold')"></div>
    <div class="iconbl bl-italic" @click="emits('italic')"></div>
    <div class="iconbl bl-strikethrough" @click="emits('strike')"></div>
    <div class="iconbl bl-a-texteditorsuperscript-line" @click="emits('sup')"></div>
    <div class="iconbl bl-a-texteditorsubscript-line" @click="emits('sub')"></div>
    <div class="iconbl bl-separator" @click="emits('separator')"></div>

    <!--  -->
    <div class="divider"></div>
    <!-- <div class="iconbl bl-a-rightsmallline-line" @click="emits('blockquote')"> -->
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
    <!-- </div> -->
    <div class="iconbl bl-single-quotes-r" @click="emits('code')"></div>
    <div class="iconbl bl-double-quotes-r" @click="emits('pre')"></div>
    <div class="iconbl bl-a-underbox-line" @click="emits('checkbox')"></div>
    <div class="iconbl bl-list-unordered" @click="emits('unordered')"></div>
    <div class="iconbl bl-list-ordered" @click="emits('ordered')"></div>


    <!-- 表格及其他 -->
    <div class="divider"></div>
    <div class="iconbl bl-table-" @click="emits('table')"></div>
    <div class="iconbl bl-image--line" @click="emits('image')"></div>
    <div class="iconbl bl-link-m" @click="emits('link')"></div>

    <!-- 其他工具 -->
    <div class="divider"></div>
    <el-tooltip placement="top" effect="light" :hide-after="0" trigger="click">
      <template #content>
        <div class="editor-tools-content">
          <bl-row>
            <div class="info-title">编辑器工具栏</div>
          </bl-row>
          <bl-row align="flex-start">
            <bl-col width="190px">
              <bl-row>
                <div class="iconbl bl-a-texteditorsave-line"></div>
                <div class="label">保存内容</div>
                <div class="keyboard">Ctrl + S</div>
              </bl-row>
              <bl-row>
                <div class="iconbl bl-a-eyeclose-line"></div>
                <div class="label">隐藏菜单</div>
                <div class="keyboard">Alt + 1</div>
              </bl-row>
              <bl-row>
                <div class="iconbl bl-a-eyeclose-line"></div>
                <div class="label">隐藏目录</div>
                <div class="keyboard">Alt + 2</div>
              </bl-row>
              <bl-row>
                <div class="iconbl bl-eye-line"></div>
                <div class="label">全屏预览</div>
                <div class="keyboard">Alt + 3</div>
              </bl-row>
              <bl-row>
                <div class="iconbl bl-expansion-line"></div>
                <div class="label">全屏编辑</div>
                <div class="keyboard">Alt + 4</div>
              </bl-row>
            </bl-col>
            <bl-col width="230px">
              <bl-row>
                <div class="iconbl bl-bold"></div>
                <div class="label">加粗</div>
                <div class="keyboard">Alt + B</div>
              </bl-row>
              <bl-row>
                <div class="iconbl bl-italic"></div>
                <div class="label">斜体</div>
                <div class="keyboard">Alt + I</div>
              </bl-row>
              <bl-row>
                <div class="iconbl bl-strikethrough"></div>
                <div class="label">删除</div>
                <div class="keyboard">Alt + S</div>
              </bl-row>
              <bl-row>
                <div class="iconbl bl-a-texteditorsuperscript-line"></div>
                <div class="label">上标</div>
                <div class="keyboard">Ctrl + Alt + P</div>
              </bl-row>
              <bl-row>
                <div class="iconbl bl-a-texteditorsubscript-line"></div>
                <div class="label">下标</div>
                <div class="keyboard">Ctrl + Alt + B</div>
              </bl-row>
              <bl-row>
                <div class="iconbl bl-separator"></div>
                <div class="label">分割线</div>
                <div class="keyboard">Ctrl + Alt + S</div>
              </bl-row>
            </bl-col>
            <!--  -->
            <bl-col width="230px">
              <bl-row>
                <div class="iconbl bl-a-rightsmallline-line"></div>
                <div class="label">引用</div>
                <div class="keyboard">></div>
              </bl-row>
              <bl-row>
                <div class="iconbl bl-single-quotes-r"></div>
                <div class="label">行内代码</div>
                <div class="keyboard">Alt + E</div>
              </bl-row>
              <bl-row>
                <div class="iconbl bl-double-quotes-r"></div>
                <div class="label">多行代码</div>
                <div class="keyboard">Ctrl + Alt + E</div>
              </bl-row>
              <bl-row>
                <div class="iconbl bl-a-underbox-line"></div>
                <div class="label">单选框</div>
                <div class="keyboard">Ctrl + Alt + C</div>
              </bl-row>
              <bl-row>
                <div class="iconbl bl-list-unordered"></div>
                <div class="label">无序列表</div>
                <div class="keyboard">- </div>
              </bl-row>
              <bl-row>
                <div class="iconbl bl-list-ordered"></div>
                <div class="label">有序列表</div>
                <div class="keyboard">1. </div>
              </bl-row>
            </bl-col>

            <bl-col>
              <!--  -->
              <el-divider style="margin:5px 0;border: 0;"></el-divider>
              <bl-row>
                <div class="iconbl bl-table-"></div>
                <div class="label">插入表格</div>
                <div class="keyboard">Alt + T</div>
              </bl-row>
              <bl-row>
                <div class="iconbl bl-image--line"></div>
                <div class="label">添加图片</div>
                <div class="keyboard">Alt + M</div>
              </bl-row>
              <bl-row>
                <div class="iconbl bl-link-m"></div>
                <div class="label">添加链接</div>
                <div class="keyboard">Alt + K</div>
              </bl-row>
              <bl-row>
                <div class="iconbl bl-jianpan-xianxing"></div>
                <div class="label">按键说明</div>
                <div class="keyboard">暂无</div>
              </bl-row>
            </bl-col>
          </bl-row>

          <el-divider style="margin:5px 0;border: 0;"></el-divider>
          <bl-row>
            <div class="info-title">编辑器快捷键</div>
          </bl-row>
          <bl-row align="flex-start">
            <bl-col width="190px">
              <bl-row>
                <div class="iconbl bl-a-icon_jiandaojianqie"></div>
                <div class="label">剪切整行</div>
                <div class="keyboard">Ctrl + X</div>
              </bl-row>
              <bl-row>
                <div class="iconbl bl-a-texteditorback-line"></div>
                <div class="label">撤销</div>
                <div class="keyboard">Ctrl + Z</div>
              </bl-row>
              <bl-row>
                <div class="iconbl bl-a-texteditorforward-line"></div>
                <div class="label">恢复</div>
                <div class="keyboard">Ctrl + Y</div>
              </bl-row>
              <bl-row>
                <div class="iconbl bl-search-line"></div>
                <div class="label">查找</div>
                <div class="keyboard">Ctrl + F</div>
              </bl-row>
              <bl-row>
                <div class="iconbl bl-switch-line"></div>
                <div class="label">替换</div>
                <div class="keyboard">Ctrl + G</div>
              </bl-row>
            </bl-col>
            <bl-col width="190px">
              <bl-row>
                <div class="iconbl bl-problem-line"></div>
                <div class="label">注释</div>
                <div class="keyboard">Ctrl + /</div>
              </bl-row>
              <bl-row>
                <div class="iconbl bl-indent-decrease"></div>
                <div class="label">向前缩进</div>
                <div class="keyboard">Ctrl + [</div>
              </bl-row>
              <bl-row>
                <div class="iconbl bl-indent-increase"></div>
                <div class="label">向后缩进</div>
                <div class="keyboard">Ctrl + ]</div>
              </bl-row>
            </bl-col>
            <bl-col width="190px">
              <bl-row>
                <div class="iconbl bl-a-listview-line"></div>
                <div class="label">列模式</div>
                <div class="keyboard">Alt</div>
              </bl-row>
              <bl-row>
                <div class="iconbl bl-a-radiochoose-line"></div>
                <div class="label">选中该行</div>
                <div class="keyboard">Alt + L</div>
              </bl-row>
              <bl-row>
                <div class="iconbl bl-a-rightto-line"></div>
                <div class="label">前往行数</div>
                <div class="keyboard">Alt + G</div>
              </bl-row>
              <bl-row>
                <div class="iconbl bl-a-online-line"></div>
                <div class="label">行上移</div>
                <div class="keyboard">Alt + ↑</div>
              </bl-row>
              <bl-row>
                <div class="iconbl bl-a-underline-line"></div>
                <div class="label">行下移</div>
                <div class="keyboard">Alt + ↓</div>
              </bl-row>
            </bl-col>
            <bl-col width="230px">
              <bl-row>
                <div class="iconbl bl-a-doubleonline-line"></div>
                <div class="label">上方复制</div>
                <div class="keyboard">Shift + Alt + ↑</div>
              </bl-row>
              <bl-row>
                <div class="iconbl bl-a-doubleunderline-line"></div>
                <div class="label">下方复制</div>
                <div class="keyboard">Shift + Alt + ↓</div>
              </bl-row>
            </bl-col>
          </bl-row>
        </div>
      </template>
      <div class="iconbl bl-jianpan-xianxing"></div>
    </el-tooltip>
  </div>
</template>

<script lang="ts" setup>
const emits = defineEmits([
  'save', 'editorFullScreen', 'previewFullScreen',
  'bold', 'italic', 'strike', 'sup', 'sub',
  'separator', 'blockquote', 'blockquoteBlock', 'blockquoteGreen', 'blockquoteYellow', 'blockquoteRed', 'blockquoteBlue', 'blockquotePurple',
  'code', 'pre', 'unordered', 'ordered', 'checkbox',
  'table', 'image', 'link'
])
</script>

<style scoped lang="scss">
.editor-tools-root {
  @include box(calc(100% - 20px), 35px);
  @include flex(row, flex-start, center);
  @include themeShadow(0 3px 5px 1px #d3d3d3, 0 3px 5px 1px #000);
  margin: 5px 10px 10px 10px;
  padding: 0 5px;
  border-radius: 5px;
  background-color: var(--el-color-primary-light-5);
  overflow-x: overlay;

  &>div {
    margin: 0 5px;
    padding: 3px;
    border: 1px solid var(--el-color-primary-light-5);
    border-radius: 3px;
    transition: 0.3s;
    cursor: pointer;

    &:hover {
      background-color: var(--el-color-primary);
    }
  }

  .divider {
    @include box(2px, 60%);
    background-color: var(--el-color-primary-light-1);
    padding: 0;
    border: 0;
  }
}
</style>

<style lang=scss>
.editor-tools-content {
  @include flex(column, flex-start, flex-start);

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
    width: 100%;
    @include font(15px, 500);
    border-bottom: 1px solid #c9c9c9;
    padding: 5px;
    margin-bottom: 10px;
  }

  .iconbl {
    margin-right: 10px;
    border: 1px solid #c9c9c9;
    padding: 1px 3px;
    border-radius: 3px;
  }

  .label {
    height: 20px;
    width: 48px;
    text-align: justify;
    margin-right: 10px;

    &:after {
      display: inline-block;
      width: 100%;
      content: "";
    }
  }
}
</style>