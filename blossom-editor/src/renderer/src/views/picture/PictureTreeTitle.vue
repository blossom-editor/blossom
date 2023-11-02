<template>
  <div class="doc-title">
    <bl-tag class="sort" v-show="props.trees.showSort" :bgColor="levelColor" style="padding: 0 2px">
      {{ props.trees.s }}
    </bl-tag>
    <span class="doc-name">
      <img
        class="menu-icon-img"
        v-if="isNotBlank(props.trees.icon) && (props.trees.icon.startsWith('http') || props.trees.icon.startsWith('https'))"
        :src="props.trees.icon" />
      <svg v-else-if="isNotBlank(props.trees.icon)" class="icon menu-icon" aria-hidden="true">
        <use :xlink:href="'#' + props.trees.icon"></use>
      </svg>
      <div class="name-wrapper">
        {{ props.trees.n }}
      </div>
      <!-- 如果专题是公开的, 则单独显示公开标签 -->
      <bl-tag v-if="props.trees.o === 1 && isSubjectDoc" style="margin-top: 5px" :bg-color="'#7AC20C'" :icon="'bl-cloud-line'"></bl-tag>
      <bl-tag v-for="tag in tags" :bg-color="tag.bgColor" style="margin-top: 5px" :icon="tag.icon">{{ tag.content }}</bl-tag>
    </span>
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
  level: { type: Number, required: true }
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
  props.trees.t?.forEach((tag) => {
    if (tag === 'subject') {
      icons.unshift({ content: '专题', bgColor: 'salmon', icon: 'bl-a-lowerrightpage-line' })
    } else if (tag === 'toc') {
      icons.push({ content: 'TOC', bgColor: '#7274fa' })
    } else {
      icons.push({ content: tag })
    }
  })
  return icons
})
</script>

<style scoped lang="scss">
$icon-size: 17px;

// .menu-icon {
//   @include box($icon-size, $icon-size, $icon-size, $icon-size, $icon-size, $icon-size);
//   margin-right: 9px;
// }

.doc-title {
  @include flex(row, flex-start, flex-start);
  max-width: calc(100% - 15px);
  min-width: calc(100% - 15px);
  width: 100%;
  padding-bottom: 1px;
  position: relative;

  .doc-name {
    @include flex(row, flex-start, flex-start);
    @include themeBrightness(100%, 90%);
    @include ellipsis();
    font-size: inherit;
    align-content: flex-start;
    flex-wrap: wrap;
    width: 100%;

    .menu-icon,
    .menu-icon-img {
      @include box($icon-size, $icon-size, $icon-size, $icon-size, $icon-size, $icon-size);
      margin-top: 5px;
      margin-right: 8px;
    }

    .menu-icon-img {
      border-radius: 2px;
    }

    .name-wrapper {
      @include ellipsis();
      max-width: calc(100% - 25px);
    }
  }

  .sort {
    position: absolute;
    right: -15px;
    top: 2px;
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
  background: #79c20c71;
}

.star-line {
  left: -10px;
  background: rgb(237, 204, 11);
}
</style>
