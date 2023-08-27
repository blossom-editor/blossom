<template>
  <div class="editor-status-root">
    <bl-row width="calc(100% - 80px)" height="100%" class="status-item-container">
      <div>
        《{{ curDoc?.name }}》
      </div>
      <div>
        版本:{{ curDoc?.version }}
      </div>
      <div>
        字数:{{ curDoc?.words }}
      </div>
      <div>
        最近修改:{{ curDoc?.updTime }}
      </div>
      <div v-if="curDoc?.openTime">
        发布:{{ curDoc?.openTime }}
      </div>
    </bl-row>
    <bl-row width="100px">
      渲染用时: {{ props.renderInterval }}ms
    </bl-row>
  </div>
</template>

<!-- 
文档编辑的首页, 主要包含4个主体区域
┌──────┬──────────┐
|      |   this   |
├──────┼──────────┤
|      |          |
|      |          |
|      |          |
└──────┴──────────┘
 -->
<script setup lang="ts">
import { inject } from 'vue';
import { provideKeyCurArticleInfo } from '@renderer/views/doc/doc'

const props = defineProps({
  renderInterval: {
    type: Number,
    default: 0
  }
})

const curDoc = inject<DocInfo | undefined>(provideKeyCurArticleInfo)
</script>

<style scoped lang="scss">
.editor-status-root {
  @include flex(row, space-between, center);
  @include box(100%, 100%);
  @include font(10px, 500);
  color: var(--bl-editor-color);
  background-color: var(--bl-editor-gutters-bg-color);

  .status-item-container {
    overflow-x: overlay;
    white-space: nowrap;

    &>div {
      height: 100%;
      padding: 0 5px;
      cursor: pointer;

      &:hover {
        background-color: var(--bl-editor-bg-color);
      }
    }
  }

  .tag-root {
    cursor: pointer;
  }

  div {
    @include flex(row, flex-start, center);
  }
}
</style>