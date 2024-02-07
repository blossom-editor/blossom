<template>
  <div class="picture-batch-del-root">
    <div class="info-title">
      <div class="iconbl bl-delete-line"></div>
      批量删除文件 <span v-if="ignoreCheck">【强制】</span>
    </div>
    <div class="content" :style="{ height: delResult.done ? (props.ignoreCheck ? '158px' : '187px') : '145px' }">
      <bl-row just="center" class="desc">
        已选择 <span class="file-count">{{ props.ids.size }}</span> 个文件。
      </bl-row>
      <div v-if="ignoreCheck" class="desc" style="margin-top: 5px; color: red">警告：正在文章中使用的图片也会被删除！文件删除后无法找回！</div>
      <div v-else class="desc" style="margin-top: 5px">提示：如果文件正被文章使用，将无法删除。</div>
      <Transition>
        <div v-show="delResult.done" class="result">{{ delResult.msg }}</div>
      </Transition>
      <el-button class="del-btn" size="large" type="primary" :loading="isLoading" @click="del">
        {{ isLoading ? '正在删除' : props.ignoreCheck ? '强制删除' : '开始删除' }}
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { pictureDelBatchApi } from '@renderer/api/blossom'
import type { PictureDelBatchRes } from '@renderer/api/blossom'
import type { R } from '@renderer/api/request'

const props = defineProps({
  // 被删除的ID集合
  ids: {
    type: Set<String>,
    required: true
  },
  // 是否强制删除
  ignoreCheck: {
    type: Boolean,
    default: false
  }
})

const isLoading = ref(false)
const delResult = ref({ done: false, msg: '' })

const del = () => {
  if (props.ids.size <= 0) {
    return
  }
  isLoading.value = true
  pictureDelBatchApi({ ids: Array.from(props.ids), ignoreCheck: props.ignoreCheck }).then((resp: R<PictureDelBatchRes>) => {
    if (props.ignoreCheck) {
      delResult.value.msg = `${resp.data?.success} 个文件成功删除。 \n${resp.data?.fault} 个文件删除失败。`
    } else {
      delResult.value.msg = `${resp.data?.success} 个文件成功删除。 \n${resp.data?.inuse} 文件正在使用中。 \n${resp.data?.fault} 个文件删除失败。`
    }
    setTimeout(() => {
      isLoading.value = false
      delResult.value.done = true
      emits('deleted', resp.data!.successIds)
    }, 1500)
  })
}

const emits = defineEmits(['deleted'])
</script>

<style scoped lang="scss">
@import '@renderer/assets/styles/bl-dialog-info';

.picture-batch-del-root {
  @include box(100%, 100%);
  border-radius: 10px;

  .content {
    padding: 10px;
    font-weight: 300;
    transition: height 0.4s;
    position: relative;

    .desc {
      text-align: center;
      line-height: 30px;
    }

    .file-count {
      @include font(30px, 700);
      padding: 0 10px;
    }

    .del-btn {
      margin: 15px 0 3px 0;
      font-size: 17px;
      font-weight: 300;
      transition: all 0.3s;
      position: absolute;
      bottom: 15px;
      right: 15px;
    }

    .result {
      @include themeBg(#e1e1e1, #1b1b1b);
      line-height: 30px;
      padding: 0 10px;
      border: 1px solid var(--el-border-color);
      margin-top: 10px;
      border-radius: 8px;
      white-space: pre;
    }
  }

  .v-enter-active,
  .v-leave-active {
    transition: opacity 0.5s ease;
  }

  .v-enter-from,
  .v-leave-to {
    opacity: 0;
  }
}
</style>
