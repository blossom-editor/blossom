<template>
  <div
    :class="['doc-title', props.trees.t?.includes('subject') ? 'subject-title' : '']">

    <div class="doc-name">
      <svg v-if="isNotBlank(props.trees.icon)" class="icon menu-icon" aria-hidden="true">
        <use :xlink:href="'#' + props.trees.icon"></use>
      </svg>
      {{ props.trees.n }}
    </div>

    <bl-tag v-if="props.trees.o === 1 && props.trees.ty != 3" :bg-color="'#7AC20C'" :icon="'bl-cloud-line'"></bl-tag>
    <div v-for="tag in tags">
      <bl-tag v-if="tag.content" :bg-color="tag.bgColor" :icon="tag.icon">{{ tag.content }}</bl-tag>
      <bl-tag v-else :bg-color="tag.bgColor" :icon="tag.icon" />
    </div>

    <div v-for="line, index in tagLins" :key="line" :class="[line]" :style="{ left: (-1 * (index + 1) * 5) + 'px' }">
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { PropType } from 'vue'
import { isNotBlank } from '@renderer/assets/utils/obj'

const props = defineProps({
  trees: { type: Object as PropType<DocTree>, default: {} },
  size: { type: Number, default: 14 }
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
  return icons;
})

const tagLins = computed(() => {
  let lines: string[] = []
  if (props.trees.star === 1) {
    lines.push('star-line')
  }
  if (props.trees.o === 1 && props.trees.ty === 3) {
    lines.push('open-line')
  }
  if (props.trees.vd === 1) {
    lines.push('sync-line')
  }
  return lines
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
  max-width: calc(100% - 15px);
  min-width: calc(100% - 15px);
  padding-bottom: 1px;
  position: relative;

  .doc-name {
    @include flex(row, flex-start, center);
    @include themeBrightness(100%, 80%);
    @include ellipsis();
  }
}

// 专题样式, 包括边框和文字样式
.subject-title {
  @include themeShadow(2px 2px 10px 1px #fad7d7, 1px 2px 10px 1px #0A0A0A);
  @include themeBg(linear-gradient(135deg, #fad7d7, #fae7e7, #ffffff), linear-gradient(135deg, #594A23, #453D28, #33302B));
  padding: 2px 5px;
  margin: 5px 0 10px 0;
  border-radius: 7px;
  position: relative;


  .doc-name {
    @include font(13px, 300);
    @include themeText(2px 2px 2px #D8D8D8, 2px 2px 2px #0A0A0A);
    @include ellipsis();
    color: var(--el-color-primary);
    min-width: 180px;
    max-width: 180px;
  }
}

// 在左侧显示
.open-line,
.star-line,
.sync-line {
  position: absolute;
  width: 2px;
  height: 60%;
  top: 20%;
  border-radius: 10px;
}

.star-line {
  background: rgb(237, 204, 11);
}

.open-line {
  background: #79C20C71;
}

.sync-line {
  background: #E8122479;
}
</style>