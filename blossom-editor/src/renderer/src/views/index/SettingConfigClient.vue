<template>
  <div class="config-client-root">
    <el-form label-position="right" label-width="110px" style="max-width: 800px">
      <bl-row just="center" class="config-module-titile"><span class="iconbl bl-a-texteditorhighlightcolor-line"></span>文章设置</bl-row>
      <el-form-item label="编辑器字体">
        <el-input v-model="configEditorStyleForm.fontFamily" size="default" @input="changeEditorStyle"></el-input>
        <div class="conf-tip">
          会影响 Markdown 编辑器、预览页面、新窗口预览、编辑历史中的正文字体样式。中英文等宽字体在表格中会有更好的样式表现，如:
          <a href="https://github.com/be5invis/Sarasa-Gothic" target="_blank">更纱黑体(Sarasa Fixed CL)</a>。
        </div>
      </el-form-item>

      <el-form-item label="编辑器字体大小">
        <el-input v-model="configEditorStyleForm.fontSize" size="default" @input="changeEditorStyle">
          <template #append>单位 px</template>
        </el-input>
        <div class="conf-tip">会影响 Markdown 编辑器、预览页面、新窗口预览、编辑历史中的正文字体大小。</div>
      </el-form-item>

      <el-form-item label="文档菜单字体大小">
        <el-input v-model="configViewStyleForm.treeDocsFontSize" size="default" @input="changeViewStyle">
          <template #append>单位 px</template>
        </el-input>
        <div class="conf-tip">会影响编辑器，照片墙中左侧树状菜单的字体大小。</div>
      </el-form-item>

      <el-form-item label="代码块默认语言">
        <el-input v-model="configEditorStyleForm.defaultPreLanguage" size="default" @input="changeEditorStyle"> </el-input>
        <div class="conf-tip">通过快捷键或者操作按钮生成多行代码块<code>```</code>时的默认语言。</div>
      </el-form-item>

      <el-form-item label="显示代码块行数">
        <el-switch v-model="configEditorStyleForm.isShowPreLineNumber" size="default" style="margin-right: 10px" @change="changeEditorStyle" />
        <div class="conf-tip">是否在代码块中显示代码行数。</div>
      </el-form-item>

      <el-form-item label="专题以特殊样式显示">
        <el-switch v-model="configViewStyleForm.isShowSubjectStyle" size="default" style="margin-right: 10px" @change="changeViewStyle" />
        <div class="conf-tip">是否以特殊的样式显示专题。</div>
      </el-form-item>
    </el-form>

    <el-form label-position="right" label-width="110px" style="max-width: 800px">
      <bl-row just="center" class="config-module-titile">照片墙设置</bl-row>
      <el-form-item label="图片上传大小限制">
        <el-input-number v-model="configPicStyleForm.maxSize" :min="0" controls-position="right" size="default" @change="changePicStyle">
          <template #append>单位 MB</template>
        </el-input-number>
        <div class="conf-tip">
          只控制客户端的上传文件大小限制，并不会影响服务器，在客户端限制上传大小会有更好的体验，推荐与服务端相同，单位<code>MB</code>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useConfigStore } from '@renderer/stores/config'
import type { EditorStyle, ViewStyle, PicStyle } from '@renderer/stores/config'

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
.config-client-root {
  @include box(100%, 100%);
  overflow: scroll;
  padding-right: 10px;
  padding-bottom: 150px;

  .config-module-titile {
    font-size: 20px;
    padding-bottom: 10px;
    margin-bottom: 20px;
    color: var(--bl-text-color);
    border-bottom: 1px solid var(--el-border-color);
    .iconbl {
      font-size: 25px;
      margin-right: 10px;
    }
  }

  code {
    @include themeColor(#909399, #909399);
    background-color: var(--bl-preview-code-bg-color);
    border-radius: var(--bl-preview-border-radius);
    padding: 0px 4px;
    border-radius: 3px;
    margin: 0 5px;
  }

  .conf-tip {
    color: var(--bl-text-color-light);
  }

  .el-form {
    margin-bottom: 100px;
  }
}
</style>
