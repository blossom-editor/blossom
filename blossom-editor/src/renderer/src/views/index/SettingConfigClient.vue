<template>
  <div class="config-root">
    <div class="title">客户端配置</div>
    <div class="desc">客户端配置</div>

    <el-form label-position="right" label-width="130px" style="max-width: 800px">
      <bl-row just="flex-start" class="config-module-titile"><span class="iconbl bl-a-texteditorhighlightcolor-line"></span>文章设置</bl-row>
      <el-form-item label="编辑器字体">
        <el-input v-model="configEditorStyleForm.fontFamily" size="default" @input="changeEditorStyle"></el-input>
        <div class="conf-tip">
          Markdown 编辑器、预览页面、新窗口预览、编辑历史中的正文字体样式。中英文等宽字体在表格中会有更好的样式表现，如:
          <a href="https://github.com/be5invis/Sarasa-Gothic" target="_blank">更纱黑体(Sarasa Fixed CL)</a>。
        </div>
      </el-form-item>

      <el-form-item label="编辑器字体大小">
        <el-input v-model="configEditorStyleForm.fontSize" size="default" @input="changeEditorStyle">
          <template #append>单位 px</template>
        </el-input>
        <div class="conf-tip">Markdown 编辑器、预览页面、新窗口预览、编辑历史中的正文字体大小。</div>
      </el-form-item>

      <el-form-item label="文档菜单字体大小">
        <el-input v-model="configViewStyleForm.treeDocsFontSize" size="default" @input="changeViewStyle">
          <template #append>单位 px</template>
        </el-input>
        <div class="conf-tip">文章、照片墙功能中左侧树状菜单的字体大小。</div>
      </el-form-item>

      <el-form-item label="代码块默认语言">
        <el-input v-model="configEditorStyleForm.defaultPreLanguage" size="default" @input="changeEditorStyle"> </el-input>
        <div class="conf-tip">通过快捷键或者工具栏按钮生成多行代码块<code>```</code>时的默认语言。</div>
      </el-form-item>

      <el-form-item label="显示代码块行数">
        <bl-row>
          <el-switch v-model="configEditorStyleForm.isShowPreLineNumber" size="default" style="margin-right: 10px" @change="changeEditorStyle" />
        </bl-row>
        <div class="conf-tip">是否在代码块中显示代码行数，已公开的文章需要修改后重新同步才会生效。</div>
      </el-form-item>

      <el-form-item label="专题以特殊样式显示">
        <bl-row>
          <el-switch v-model="configViewStyleForm.isShowSubjectStyle" size="default" style="margin-right: 10px" @change="changeViewStyle" />
        </bl-row>
        <div class="conf-tip">是否在文档列表中以特殊的样式显示专题。</div>
      </el-form-item>

      <el-form-item label="只展开一项子菜单">
        <bl-row>
          <el-switch v-model="configViewStyleForm.isMenuUniqueOpened" size="default" style="margin-right: 10px" @change="changeViewStyle" />
        </bl-row>
        <div class="conf-tip">展开子菜单时，收起其他子菜单。</div>
      </el-form-item>
    </el-form>

    <el-form label-position="right" label-width="130px" style="max-width: 800px">
      <bl-row just="flex-start" class="config-module-titile"><span class="iconbl bl-picture-line"></span>照片墙设置</bl-row>
      <el-form-item label="图片上传大小限制">
        <el-input-number v-model="configPicStyleForm.maxSize" :min="0" controls-position="right" size="default" @change="changePicStyle" />
        <div class="conf-tip">
          控制客户端上传文件时的大小限制检查，单位<code>MB</code>。该配置仅作用于客户端，并不会影响服务器的文件大小限制，在客户端进行大小限制会有更好的体验，通常推荐与服务端相同。
          <span class="blod">注意：服务端控制需要在修改服务端参数，详情可见</span>
          <a href="https://www.wangyunf.com/blossom-doc/guide/deploy/backend-props.html" target="_blank">《文档》</a>。
        </div>
      </el-form-item>

      <el-form-item label="自动添加图片名后缀">
        <bl-row>
          <el-switch v-model="configPicStyleForm.isAddSuffix" size="default" style="margin-right: 10px" @change="changePicStyle" />
        </bl-row>
        <div class="conf-tip">
          当开启时，会自动为所有上传的图片增加后缀，如:image_20230101_123015_000.png (截图功能不受此影响)。
          <span class="blod">注意：开启后将无法校验图片是否已上传，同时照片墙顶部的「重复上传」控制将会失效。</span>
        </div>
      </el-form-item>
    </el-form>

    <el-form label-position="right" label-width="130px" style="max-width: 800px">
      <bl-row just="flex-start" class="config-module-titile"><span class="iconbl bl-apps-line"></span>其他</bl-row>
      <el-form-item label="登录后进入首页">
        <bl-row>
          <el-switch v-model="configViewStyleForm.isLoginToHomePage" size="default" style="margin-right: 10px" @change="changeViewStyle" />
        </bl-row>
        <div class="conf-tip">点击登录按钮后，是否自动跳转至首页。</div>
      </el-form-item>
      <el-form-item label="开发者工具">
        <bl-row>
          <el-button @click="openDevTools"><span class="iconbl bl-bug-line" @click="openDevTools"></span></el-button>
        </bl-row>
        <div class="conf-tip">打开开发者工具。</div>
      </el-form-item>
      <el-form-item label="主题设置">
        <bl-row>
          <el-button @click="themeStrore.show()">设置</el-button>
        </bl-row>
        <div class="conf-tip">设置日间/夜间主题颜色。</div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useConfigStore } from '@renderer/stores/config'
import type { EditorStyle, ViewStyle, PicStyle } from '@renderer/stores/config'
import { openDevTools } from '@renderer/assets/utils/electron'
import { useThemeStore } from '@renderer/stores/theme'

const themeStrore = useThemeStore()
const configStore = useConfigStore()
const configEditorStyleForm = ref<EditorStyle>(configStore.editorStyle)
const configViewStyleForm = ref<ViewStyle>(configStore.viewStyle)
const configPicStyleForm = ref<PicStyle>(configStore.picStyle)

const changeEditorStyle = () => {
  configStore.setEditorStyle(configEditorStyleForm.value)
}

const changeViewStyle = () => {
  configStore.setViewStyle(configViewStyleForm.value)
}

const changePicStyle = () => {
  configStore.setPicStyle(configPicStyleForm.value)
}
</script>

<style scoped lang="scss">
@import './styles/config-root.scss';
</style>
