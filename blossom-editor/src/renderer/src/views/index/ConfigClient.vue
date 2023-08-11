<template>
  <el-form :model="configClientForm" label-position="right" label-width="90px" style="width: 580px;">
    <el-form-item label="编辑器字体">
      <el-input v-model="configClientForm.fontFamily" size="default" @input="changeFontFamily">
        <template #prefix>
          <div class="iconbl bl-a-texteditorhighlightcolor-line" style="font-size: 20px;"></div>
        </template>
      </el-input>
      <div style="color: var(--bl-text-color-light);">
        中英文等宽字体在 Markdown 表格中会有更好的样式表现，如：Sarasa Fixed CL
      </div>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useConfigStore } from '@renderer/stores/config'

const configStore = useConfigStore()

onMounted(() => {
  configClientForm.value = configStore.editorConfig
})

const configClientForm = ref<any>({})

const changeFontFamily = () => {
  configStore.setEditorFontFamily(configClientForm.value.fontFamily)
}

</script>