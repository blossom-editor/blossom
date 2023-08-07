<template>
  <div class="index-footer-root">
    <el-popover placement="top-start" :width="350" trigger="hover" :show-after="100" :hide-after="300">
      <template #reference>
        <div class="operator-buttons">
          <el-radio-group v-model="copyType" class="copy-type-radio">
            <el-radio-button label="http">HT</el-radio-button>
            <el-radio-button label="markdown">MD</el-radio-button>
            <el-radio-button label="binary">01</el-radio-button>
          </el-radio-group>
          <el-button class="iconbl bl-copy-line" />
        </div>
      </template>
      <el-input :suffix-icon="Link" placeholder="截图后可查看图片链接..." disabled>
        <template #prepend>
          <el-button class="iconbl bl-copy-line" />
        </template>
      </el-input>
    </el-popover>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Link } from '@element-plus/icons-vue';

const copyType = ref('markdown')
</script>

<style scoped lang="scss">
.index-footer-root {
  @include box(100%, 100%);
  @include flex(row, space-around, center);
  font-size: 12px;
  -webkit-app-region: drag;

  .operator-buttons {
    @include flex(row, flex-end, center);

    .copy-type-radio {
      margin-right: 10px;

      :deep(.el-radio-button__inner) {
        height: 24px;
      }
    }

    .copy-button {
      height: 24px;
    }

    .el-input {
      width: 300px;
    }
  }

}
</style>