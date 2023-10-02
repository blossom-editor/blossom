<template>
  <div class="doc-title">
    <bl-tag class="sort" v-show="props.trees.showSort" :bgColor="levelColor">
      {{ props.trees.s }}
    </bl-tag>
    <svg v-if="isNotBlank(props.trees.icon)" class="icon menu-icon" aria-hidden="true">
      <use :xlink:href="'#' + props.trees.icon"></use>
    </svg>
    <span class="doc-name">
      {{ props.trees.n }}
    </span>
    <!-- 如果专题是公开的, 则单独显示公开标签 -->
    <bl-tag v-if="props.trees.o === 1 && isSubjectDoc" :bg-color="'#7AC20C'" :icon="'bl-cloud-line'"></bl-tag>
    <div v-for="tag in tags">
      <bl-tag v-if="tag.content" :bg-color="tag.bgColor" :icon="tag.icon">{{ tag.content }}</bl-tag>
      <bl-tag v-else :bg-color="tag.bgColor" :icon="tag.icon" />
    </div>
    <div v-if="props.trees.o === 1 && !isSubjectDoc" class="open-line"></div>
    <div v-if="props.trees.star === 1" class="star-line"></div>
  </div>
</template>
<script setup lang="ts">
import { computed } from 'vue'
import type { PropType } from 'vue'
import { isNotBlank } from '@renderer/assets/utils/obj'
import { computedDocTitleColor } from '@renderer/views/doc/doc'

//#region ----------------------------------------< 标题信息 >--------------------------------------

const props = defineProps({
  trees: { type: Object as PropType<DocTree>, default: {} },
  level: { type: Number, required: true },
})

const isSubjectDoc = computed(() => {
  return props.trees.t?.includes('subject')
})

const levelColor = computed(() => {
  return computedDocTitleColor(props.level)
})

/**
 * 计算标签, 并返回便签对象集合
 */
const tags = computed(() => {
  let icons: any = []
  props.trees.t?.forEach(tag => {
    if (tag === 'subject') {
      icons.unshift({ content: '专题', bgColor: 'salmon', icon: 'bl-a-lowerrightpage-line' })
    } else if (tag === 'toc') {
      icons.push({ content: 'TOC', bgColor: '#7274fa' })
    } else {
      icons.push({ content: tag })
    }
  });
  return icons
})

</script>

<style scoped lang="scss">
$icon-size: 17px;

.menu-icon {
  @include box($icon-size, $icon-size, $icon-size, $icon-size, $icon-size, $icon-size);
  margin-right: 9px;
}

.doc-title {
  @include flex(row, flex-start, center);
  align-content: flex-start;
  flex-wrap: wrap;
  font-size: 14px;
  max-width: calc(100% - 10px);
  min-width: calc(100% - 10px);
  padding-bottom: 1px;
  position: relative;
  .sort {
    position: absolute;
    right: -10px;
  }
}

.open-line,
.star-line {
  position: absolute;
  width: 2px;
  height: 60%;
  top: 20%;
  border-radius: 20px;
  font-size: 10px;
}

.open-line {
  left: -5px;
  background: #79C20C71;
}

.star-line {
  left: -10px;
  background: rgb(237, 204, 11);
}
</style>