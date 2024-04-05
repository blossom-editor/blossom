<template>
  <div class="theme-setting-root" ref="ThemeSettingRef">
    <div class="title" ref="ThemeSettingTitleRef">
      <div>
        🎨 主题样式
        <el-switch
          class="setting-switch"
          size="default"
          v-model="isDark"
          :active-icon="Moon"
          :inactive-icon="Sunny"
          inline-prompt
          @change="changeTheme" />
      </div>
      <div class="iconbl bl-a-closeline-line" @click="themeStore.close()"></div>
    </div>

    <bl-col class="content" align="flex-start">
      <el-tabs tab-position="right" class="tabs" v-model="activeTab">
        <el-tab-pane label="主题" name="theme">
          <bl-row class="prop-name">日间</bl-row>
          <bl-row class="colors" align="flex-start">
            <el-color-picker
              popper-class="theme-color-picker"
              v-model="customLight"
              color-format="rgb"
              @change="changePrimaryColor(customLight, false)" />
            <div
              class="color-item"
              v-for="preset in presetsLight"
              :key="preset.color"
              :style="{ backgroundColor: preset.color }"
              @click="changePrimaryColor(preset.color, false)">
              <div class="name">{{ preset.name }}</div>
            </div>
          </bl-row>

          <!--  -->
          <bl-row class="prop-name">夜间</bl-row>
          <bl-row class="colors" align="flex-start">
            <el-color-picker
              popper-class="theme-color-picker"
              v-model="customDark"
              color-format="rgb"
              @change="changePrimaryColor(customDark, true)" />
            <div
              class="color-item"
              v-for="preset in presetsDark"
              :key="preset.color"
              :style="{ backgroundColor: preset.color }"
              @click="changePrimaryColor(preset.color, true)">
              <div class="name">{{ preset.name }}</div>
            </div>
          </bl-row>

          <bl-row class="prop-row" just="space-between">
            <div class="prop">
              <div class="prop-name">增强阴影效果</div>
            </div>
            <el-switch v-model="viewStyle.isGlobalShadow" size="default" @change="changeGlobalShadow" />
          </bl-row>

          <bl-row class="prop-row" just="space-between">
            <div class="prop">
              <div class="prop-name">显示试用按钮</div>
            </div>
            <el-switch v-model="viewStyle.isShowTryuseBtn" size="default" @change="changeViewStype" />
          </bl-row>

          <bl-row class="prop-row" just="space-between">
            <div class="prop">
              <div class="prop-name">显示左上角 LOGO</div>
            </div>
            <el-switch v-model="viewStyle.isShowAsideLogo" size="default" @change="changeViewStype" />
          </bl-row>

          <bl-row class="prop-row" just="space-between">
            <div class="prop">
              <div class="prop-name">显示左下角上传入口</div>
            </div>
            <el-switch v-model="viewStyle.isShowAsideUpload" size="default" @change="changeViewStype" />
          </bl-row>

          <bl-row class="prop-row" just="space-between">
            <div class="prop">
              <div class="prop-name">简约的左侧菜单</div>
            </div>
            <el-switch v-model="viewStyle.isShowAsideSimple" size="default" @change="changeViewStype" />
          </bl-row>

          <bl-row v-if="isElectron()" class="prop-row" just="space-between">
            <div class="prop">
              <div class="prop-name">窗口缩放</div>
            </div>
            <el-button-group>
              <el-button @click="zoomOut">缩小</el-button>
              <el-button @click="zoomIn">放大</el-button>
              <el-button @click="zoomReset">还原</el-button>
            </el-button-group>
          </bl-row>

          <bl-col class="desc" align="flex-end"><div>修改主题后, 再次切换日间/夜间模式可查看完整效果。</div></bl-col>
        </el-tab-pane>
        <!--  



        -->
        <el-tab-pane label="文章" name="article">
          <bl-row class="prop-row" just="flex-end">
            <div class="prop-name" style="width: 80px; text-align: center">日间</div>
            <el-divider direction="vertical" />
            <div class="prop-name" style="width: 80px; text-align: center">夜间</div>
          </bl-row>
          <bl-row class="prop-row" just="space-between">
            <div class="prop">
              <div class="prop-name">公开标签</div>
              <bl-tag bg-color="var(--bl-tag-color-open)" icon="bl-cloud-line"></bl-tag>
            </div>
            <el-input v-model="themeLight['--bl-tag-color-open']" @input="(v: string) => setStyle('--bl-tag-color-open', v, false)"></el-input>
            <el-divider direction="vertical" />
            <el-input v-model="themeDark['--bl-tag-color-open']" @input="(v: string) => setStyle('--bl-tag-color-open', v, true)"></el-input>
          </bl-row>
          <bl-row class="prop-row" just="space-between">
            <div class="prop">
              <span class="prop-name">专题标签</span>
              <bl-tag bg-color="var(--bl-tag-color-subject)" icon="bl-a-lowerrightpage-line">专题</bl-tag>
            </div>
            <el-input v-model="themeLight['--bl-tag-color-subject']" @input="(v: string) => setStyle('--bl-tag-color-subject', v, false)"></el-input>
            <el-divider direction="vertical" />
            <el-input v-model="themeDark['--bl-tag-color-subject']" @input="(v: string) => setStyle('--bl-tag-color-subject', v, true)"></el-input>
          </bl-row>
          <bl-row class="prop-row" just="space-between">
            <div class="prop">
              <div class="prop-name">目录标签</div>
              <bl-tag bg-color="var(--bl-tag-color-toc)">TOC</bl-tag>
            </div>
            <el-input v-model="themeLight['--bl-tag-color-toc']" @input="(v: string) => setStyle('--bl-tag-color-toc', v, false)"></el-input>
            <el-divider direction="vertical" />
            <el-input v-model="themeDark['--bl-tag-color-toc']" @input="(v: string) => setStyle('--bl-tag-color-toc', v, true)"></el-input>
          </bl-row>

          <bl-row class="prop-row" just="space-between">
            <div class="prop">
              <div class="prop-name">开启专题特殊样式</div>
            </div>
            <el-switch v-model="viewStyle.isShowSubjectStyle" size="default" @change="changeViewStype" />
          </bl-row>

          <bl-row class="prop-row" just="space-between">
            <div class="prop">
              <div class="prop-name">显示文件夹收藏标签</div>
            </div>
            <el-switch v-model="viewStyle.isShowFolderStarTag" size="default" @change="changeViewStype" />
          </bl-row>

          <bl-row class="prop-row" just="space-between">
            <div class="prop">
              <div class="prop-name">显示专题目录标签</div>
            </div>
            <el-switch v-model="viewStyle.isShowArticleTocTag" size="default" @change="changeViewStype" />
          </bl-row>

          <bl-row class="prop-row" just="space-between">
            <div class="prop">
              <div class="prop-name">显示公开文件夹标签</div>
            </div>
            <el-switch v-model="viewStyle.isShowFolderOpenTag" size="default" @change="changeViewStype" />
          </bl-row>

          <bl-row class="prop-row" just="space-between">
            <div class="prop">
              <div class="prop-name">显示自定义标签</div>
            </div>
            <el-switch v-model="viewStyle.isShowArticleCustomTag" size="default" @change="changeViewStype" />
          </bl-row>

          <bl-row class="prop-row" just="space-between">
            <div class="prop" style="width: 200px">
              <div class="prop-name">显示文章名前的竖型状态标识</div>
            </div>
            <el-switch v-model="viewStyle.isShowArticleType" size="default" @change="changeViewStype" />
          </bl-row>

          <bl-row class="prop-row" just="space-between">
            <div class="prop">
              <div class="prop-name">显示文章图标</div>
            </div>
            <el-switch v-model="viewStyle.isShowArticleIcon" size="default" @change="changeViewStype" />
          </bl-row>
        </el-tab-pane>
        <!--  



        -->
        <el-tab-pane label="待办" name="todo">
          <bl-row class="prop-row" just="flex-end">
            <div class="prop-name" style="width: 80px; text-align: center">日间</div>
            <el-divider direction="vertical" />
            <div class="prop-name" style="width: 80px; text-align: center">夜间</div>
          </bl-row>
          <bl-row class="prop-row" just="space-between">
            <div class="prop">
              <div class="prop-name">未开始</div>
              <bl-tag bg-color="var(--bl-todo-wait-color)" style="width: 30px"></bl-tag>
            </div>
            <!-- prettier-ignore -->
            <el-input v-model="themeLight['--bl-todo-wait-color']" @input="(v: string) => setStyle('--bl-todo-wait-color', v, false)"></el-input>
            <el-divider direction="vertical" />
            <!-- prettier-ignore -->
            <el-input v-model="themeDark['--bl-todo-wait-color']" @input="(v: string) => setStyle('--bl-todo-wait-color', v, true)"></el-input>
          </bl-row>
          <bl-row class="prop-row" just="space-between">
            <div class="prop">
              <div class="prop-name">进行中</div>
              <bl-tag bg-color="var(--bl-todo-proc-color)" style="width: 30px"></bl-tag>
            </div>
            <!-- prettier-ignore -->
            <el-input v-model="themeLight['--bl-todo-proc-color']" @input="(v: string) => setStyle('--bl-todo-proc-color', v, false)"></el-input>
            <el-divider direction="vertical" />
            <!-- prettier-ignore -->
            <el-input v-model="themeDark['--bl-todo-proc-color']" @input="(v: string) => setStyle('--bl-todo-proc-color', v, true)"></el-input>
          </bl-row>
          <bl-row class="prop-row" just="space-between">
            <div class="prop">
              <div class="prop-name">已完成</div>
              <bl-tag bg-color="var(--bl-todo-comp-color)" style="width: 30px"></bl-tag>
            </div>
            <!-- prettier-ignore -->
            <el-input v-model="themeLight['--bl-todo-comp-color']" @input="(v: string) => setStyle('--bl-todo-comp-color', v, false)"></el-input>
            <el-divider direction="vertical" />
            <!-- prettier-ignore -->
            <el-input v-model="themeDark['--bl-todo-comp-color']" @input="(v: string) => setStyle('--bl-todo-comp-color', v, true)"></el-input>
          </bl-row>
        </el-tab-pane>
      </el-tabs>
      <!--  -->
    </bl-col>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useConfigStore } from '@renderer/stores/config'
import { Sunny, Moon } from '@element-plus/icons-vue'
import { useDraggable } from '@renderer/scripts/draggable'
import { useThemeStore } from '@renderer/stores/theme'
import { isDark, getTheme, changeTheme, setPrimaryColor, setStyleItem, setStyleItemObj, resetStyleItems } from '@renderer/scripts/global-theme'
import { setZoomLevel, resetZoomLevel } from '@renderer/assets/utils/electron'
import { isElectron } from '@renderer/assets/utils/util'

const config = useConfigStore()
const { viewStyle } = config

//#region 主题设置

const themeStore = useThemeStore()
const ThemeSettingRef = ref()
const ThemeSettingTitleRef = ref()
useDraggable(ThemeSettingRef, ThemeSettingTitleRef)
const activeTab = ref('theme')

const presetsLight = [
  { color: 'rgb(186, 91, 73)', name: '赤缇' },
  { color: 'rgb(136, 118, 87)', name: '茶色' },
  { color: 'rgb(119, 150, 73)', name: '碧山' },
  { color: 'rgb(128, 164, 146)', name: '缈碧' },
  { color: 'rgb(110, 155, 197)', name: '挼蓝' },
  // { color: 'rgb(173, 140, 242)', name: '亮紫' },
  { color: 'rgb(97, 94, 168)', name: '优昙瑞' },
  { color: 'rgb(178, 182, 182)', name: '月魄' },
  { color: 'rgb(199, 198, 183)', name: '霜地' },
  { color: 'rgb(104, 104, 104)', name: '深灰' }
]
const presetsDark = [
  { color: 'rgb(178, 119, 119)', name: '绛纱' },
  { color: 'rgb(173, 146, 49)', name: '立秋' },
  { color: 'rgb(137, 153, 17)', name: '水龙吟' },
  { color: 'rgb(93, 131, 81)', name: '漆姑' },
  { color: 'rgb(84, 118, 137)', name: '太师青' },
  { color: 'rgb(107, 121, 142)', name: '菘蓝' },
  { color: 'rgb(179, 173, 160)', name: '利休白茶' },
  { color: 'rgb(122, 123, 120)', name: '雷雨垂' },
  { color: 'rgb(82, 84, 84)', name: '深灰' }
]

const customLight = ref('')
const customDark = ref('')

const changePrimaryColor = (color: string, themeDark: boolean) => {
  setPrimaryColor(color, themeDark)
}

/**
 * 设置全局阴影
 * @param open
 */
const changeGlobalShadow = (open: boolean) => {
  if (open) {
    resetStyleItems(['--bl-text-shadow', '--bl-text-shadow-light', '--bl-box-shadow-subject', '--bl-drop-shadow-star'], true)
    resetStyleItems(['--bl-text-shadow', '--bl-text-shadow-light', '--bl-box-shadow-subject', '--bl-drop-shadow-star'], false)
  } else {
    let style = {
      '--bl-text-shadow': 'none',
      '--bl-text-shadow-light': 'none',
      '--bl-box-shadow-subject': 'none',
      '--bl-drop-shadow-star': 'none'
    }
    setStyleItemObj(style, true)
    setStyleItemObj(style, false)
  }
  config.setViewStyle(viewStyle)
}

const changeViewStype = () => {
  config.setViewStyle(viewStyle)
}

//#endregion

//#region
let zoomLevel = 0
const zoomIn = () => {
  zoomLevel = zoomLevel + 0.2
  setZoomLevel(0.2)
}
const zoomOut = () => {
  zoomLevel = zoomLevel - 0.2
  setZoomLevel(-0.2)
}

const zoomReset = () => {
  resetZoomLevel()
}
//#endregion

//#region 文档设置

const themeLight = ref(getTheme(false))
const themeDark = ref(getTheme(true))

const setStyle = (name: string, value: string, themeDark: boolean) => {
  setStyleItem(name, value, themeDark)
}

//#endregion
</script>

<style lang="scss">
.theme-setting-root {
  @include box(440px, auto);
  background-color: var(--bl-dialog-bg-color);
  box-shadow: var(--bl-dialog-box-shadow);
  border-radius: 8px;
  position: absolute;
  right: 100px;
  top: 100px;
  z-index: 3000;
  overflow: hidden;

  .title {
    @include flex(row, space-between, center);
    padding: 10px 10px;
    border-bottom: 1px solid var(--el-border-color);
    color: var(--bl-text-title-color);
    cursor: move;

    .bl-a-closeline-line {
      cursor: pointer;
      &:hover {
        color: var(--el-primary0);
      }
    }
  }

  .content {
    padding: 10px 0 10px 10px;

    .tabs {
      width: 100%;
    }

    .prop-name {
      @include font(14px, 300);
      color: var(--bl-text-color);
    }
    .colors {
      padding: 10px 0 20px 10px;
      align-content: flex-start;
      flex-wrap: wrap;

      .el-color-picker--small {
        margin: 0 10px 10px 0;
      }

      .color-item {
        @include box(24px, 24px);
        margin: 0 10px 10px 0;
        border-radius: 4px;
        position: relative;
        transition: transform 0.3s;
        cursor: pointer;
        text-align: center;

        .name {
          @include font(13px, 300);
          @include themeColor(#323232, #fff);
          word-break: keep-all;
          bottom: -20px;
          position: absolute;
          top: 50%;
          left: 50%;
          transform: translate(-50%, 20px);
          transition: opacity 0.4s;
          opacity: 0;
          pointer-events: none;
        }

        &:hover {
          .name {
            opacity: 1;
          }
        }
      }
    }

    .desc {
      @include font(13px, 300);
      color: var(--bl-text-color);
      margin-top: 30px;
      div {
        margin-bottom: 3px;
      }

      a {
        color: var(--el-color-primary);
      }
    }
  }

  .prop-row {
    margin-bottom: 10px;
    .prop {
      @include flex(row, flex-start, center);
      width: 180px;
    }
    .el-input {
      width: 80px;
    }
    .el-switch {
      height: 24px;
    }
  }
}

.theme-color-picker {
  z-index: 3001 !important;
  margin: 0 10px 10px 0;
}
</style>
