<template>
  <div class="picture-batch-del-root">
    <div class="info-title">
      <div class="iconbl bl-delete-line"></div>
      批量删除文件
    </div>
    <div class="content">
      <div class="desc">
        已选择 <span class="file-count">{{ 42 }}</span> 个文件。
      </div>
      <div class="desc" style="margin-top: 5px">批量删除只会删除未被使用的文件，若文件正被文章引用，将无法删除。</div>
      <div class="desc">
        <el-button class="del-btn" size="large" type="primary" :loading="isLoading" @click="del">{{ isLoading ? '正在删除' : '开始删除' }}</el-button>
      </div>
      <div v-show="delDone.done" class="desc">已删除 31 个文件，10 个文件正在使用中。</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'

const isLoading = ref(false)
const delDone = ref({ done: false, msg: '' })

const del = () => {
  isLoading.value = true
  setTimeout(() => {
    isLoading.value = false
    delDone.value.done = true
  }, 1000)
}
</script>

<style scoped lang="scss">
@import '@renderer/assets/styles/bl-dialog-info';

.picture-batch-del-root {
  border-radius: 10px;
  @include box(100%, 100%);

  .content {
    padding: 10px;
    font-weight: 300;

    .desc {
      text-align: center;
      line-height: 30px;
    }

    .file-count {
      @include font(20px, 700);
      padding: 0 10px;
    }

    .del-btn {
      margin: 15px 0 3px 0;
      font-size: 17px;
      font-weight: 300;
      transition: all 0.3s;
    }
  }
}
</style>
