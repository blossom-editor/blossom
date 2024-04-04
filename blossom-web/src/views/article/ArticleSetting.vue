<template>
  <div class="detail">
    <bl-row>
      文章字体大小：
      <bl-tag style="margin-right: 15px">{{ getFontSizeValue().toFixed(1) }}</bl-tag>
      <el-button size="small" :icon="Plus" @click="increase()"></el-button>
      <el-button size="small" :icon="Minus" @click="decrease()"></el-button>
    </bl-row>
  </div>
</template>

<script setup lang="ts">
import { Plus, Minus } from '@element-plus/icons-vue'
import { increase, decrease, getFontSizeValue } from './scripts/article-setting'
</script>

<style scoped lang="scss">
.detail {
  @include flex(column, flex-start, flex-start);
  height: 100%;
  padding-top: 20px;
  padding-bottom: 30px;
  font-size: 13px;

  .btns {
    .iconbl {
      margin-right: 15px;
    }
  }
}
</style>
