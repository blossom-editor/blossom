<template>
  <div class="editor-tools-root">
    <div class="tomato-bell" ref="TomatoBellRef"></div>
    <div class="tools-container">
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
              <bl-col width="230px">
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
                <bl-row>
                  <div class="iconbl bl-transcript-fill"></div>
                  <div class="label">格式化</div>
                  <div class="keyboard">Shift + Alt + F</div>
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
                <bl-row>
                  <div class="iconbl bl-fanqiezhong"></div>
                  <div class="label">番茄时钟</div>
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
      <el-popover placement="bottom" :width="220" trigger="click" popper-style="padding:0;">
        <template #reference>
          <div class="iconbl bl-fanqiezhong"></div>
        </template>
        <template #default>
          <div
            style="padding: 5px 10px;font-size: 18px;margin-bottom: 15px;border-bottom: 1px solid var(--el-border-color);">
            <span class="iconbl bl-fanqiezhong" style="font-size: 18px;padding-right: 6px;color: #EC7259;"></span>番茄时钟
          </div>
          <bl-row style="padding: 0 10px;">
            <span>时长分钟：</span>
            <el-input-number v-model="duration"></el-input-number>
          </bl-row>
          <bl-row style="padding: 0 10px;margin-top: 15px;">
            <span>结束时间：</span>
            <span style="font-size: 11px;">{{ endTime }}</span>
          </bl-row>
          <bl-row just="space-between"
            style="padding: 8px 10px;margin-top: 15px;border-top: 1px solid var(--el-border-color);">
            <el-button @click="stop">停止番茄钟</el-button>
            <el-button @click="start" type="primary">开始</el-button>
          </bl-row>
        </template>
      </el-popover>
      <div style="font-size: 12px;padding: 4px 5px;">{{ remainStr }}</div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { secondsToDatetime, formateMs } from '@renderer/assets/utils/util';
import { ElNotification } from 'element-plus';

const emits = defineEmits([
  'save', 'editorFullScreen', 'previewFullScreen',
  'bold', 'italic', 'strike', 'sup', 'sub',
  'separator', 'blockquote', 'blockquoteBlock', 'blockquoteGreen', 'blockquoteYellow', 'blockquoteRed', 'blockquoteBlue', 'blockquotePurple',
  'code', 'pre', 'unordered', 'ordered', 'checkbox',
  'table', 'image', 'link'
])

onMounted(() => {
})

onUnmounted(() => {
  stop()
})

/**
 * 预览番茄钟到期时间
 */
const endTime = computed(() => {
  return secondsToDatetime((Date.now() / 1000) + (duration.value * 60))
})

const TomatoBellRef = ref()
// 番茄钟时长(分钟)
const duration = ref(30)
// 番茄钟的展示信息, 
const remainStr = ref('00:00:00')
// 番茄钟参数, 开始时间, 时长(毫秒)
let param = { start: 0, duration: 0 }
let bell: NodeJS.Timer

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
    TomatoBellRef.value.style.transform = `translateX(${100 - ((remain / param.duration) * 100)}%)`
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
  console.log('清除番茄钟')
  clearInterval(bell)
  remainStr.value = formateMs(0)
  if (TomatoBellRef.value) {
    TomatoBellRef.value.style.transform = `translateX(100%)`
  }
}

</script>

<style scoped lang="scss">
.editor-tools-root {
  @include box(calc(100% - 20px), 35px);
  @include themeShadow(0 1px 5px 1px #d3d3d3, 0 1px 5px 1px rgb(20, 20, 20));
  margin: 5px 10px 10px 10px;
  border-radius: 5px;
  position: relative;
  overflow: hidden;

  .tomato-bell {
    @include box(100%, 100%);
    @include themeBg(#B593FDC7, #354F00);
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
    @include themeBg(#EFEFEF80, #8A8A8A4A);
    padding: 0 5px;
    position: absolute;
    z-index: 2;
    overflow-x: overlay;

    &>div {
      @include themeColor(#5B5B5B, #0D0D0D);
      @include themeBorder(1px, #A7A7A7, #1C1C1C);
      margin: 0 5px;
      padding: 3px;
      border-radius: 3px;
      transition: 0.3s;
      cursor: pointer;

      &:hover {
        background-color: var(--el-color-primary);
      }
    }

    .divider {
      @include box(2px, 60%);
      @include themeBg(#ad8cf280, #1C1C1C);
      padding: 0;
      border: 0;
    }
  }
}
</style>
<!-- 
快捷键说明为弹出框 需要设置全局的样式
-->
<style lang=scss>
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
    width: 100%;
    @include font(15px, 500);
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