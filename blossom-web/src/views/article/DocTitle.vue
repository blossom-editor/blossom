<template>
  <div :class="['doc-title', props.trees.t?.includes('subject') ? 'subject-title' : '']" @click="handlClick">
    <svg v-if="isNotBlank(props.trees.icon)" class="icon menu-icon" aria-hidden="true">
      <use :xlink:href="'#' + props.trees.icon"></use>
    </svg>
    <span class="doc-name">{{ props.trees.n }}</span>
    <!-- 如果专题是公开的, 则单独显示公开标签 -->
    <div v-for="tag in tags">
      <BLTag v-if="tag.content" :bg-color="tag.bgColor" :icon="tag.icon">{{ tag.content }}</BLTag>
      <BLTag v-else :bg-color="tag.bgColor" :icon="tag.icon" />
    </div>
  </div>
</template>

<!-- 
文档树状列表的标题封装
┌──────┬──────────┐
|      |          |
├──────┼──────────┤
| this |          |
|title |          |
|      |          |
└──────┴──────────┘
 -->
<script setup lang="ts">
import { computed } from 'vue'
import type { PropType } from 'vue'
import { isNotBlank } from '@/assets/utils/obj'
import BLTag from '@/components/BLTag.vue';

//#region ----------------------------------------< 标题信息 >--------------------------------------

const props = defineProps({
  trees: { type: Object as PropType<DocTree>, default: {} }
})

/**
 * 计算标签, 并返回便签对象集合
 */
const tags = computed(() => {
  let icons: any = []
  props.trees.t?.forEach(tag => {
    if (tag === 'subject') {
      icons.unshift({ content: '专题', bgColor: '#C0C0C0', icon: 'bl-a-lowerrightpage-line' })
    } else if (tag === 'toc') {
      icons.push({ content: 'TOC', bgColor: '#c1992f' })
    } else {
      icons.push({ content: tag })
    }
  });
  return icons;
})

/**
 * 点击文档菜单标题后的回调
 */
const handlClick = () => {
  emits('clickDoc', props.trees)
}


//#endregion

const emits = defineEmits(['clickDoc'])
</script>

<style scoped lang="scss">
$icon-size: 17px;

.menu-icon {
  @include box($icon-size, $icon-size, $icon-size, $icon-size, $icon-size, $icon-size);
  margin-right: 5px;
}

.doc-title {
  @include flex(row, flex-start, center);
  align-content: flex-start;
  flex-wrap: wrap;
  max-width: calc(100% - 10px);
  min-width: calc(100% - 10px);
  padding-bottom: 1px;
  position: relative;

  .doc-name {
    // @include flex(row, flex-start, center);
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }

}

// 专题样式, 包括边框和文字样式
.subject-title {
  position: relative;
  padding: 2px 5px;
  margin: 5px 0 10px 0;
  border-radius: 4px;
  box-shadow: 1px 1px 5px #A2A2A2;
  background: linear-gradient(135deg, #ffffff, #F0F0F0, #CACACA);

  .doc-name {
    min-width: 145px;
    max-width: 145px;
    color: #4A545E;
    text-shadow: 2px 2px 3px #9393939D;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }
}
</style>