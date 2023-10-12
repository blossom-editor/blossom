<template>
  <div :class="[viewStyle.isShowSubjectStyle ? (props.trees.t?.includes('subject') ? 'subject-title' : 'doc-title') : 'doc-title']">
    <bl-tag class="sort" v-show="props.trees.showSort" :bgColor="levelColor" style="padding: 0 2px">
      {{ props.trees.s }}
    </bl-tag>
    <div class="doc-name">
      <svg v-if="isNotBlank(props.trees.icon)" class="icon menu-icon" aria-hidden="true">
        <use :xlink:href="'#' + props.trees.icon"></use>
      </svg>
      <div class="name-wrapper">
        {{ props.trees.n }}
      </div>
      <bl-tag v-if="props.trees.o === 1 && props.trees.ty != 3" style="margin-top: 5px" :bg-color="'#7AC20C'" :icon="'bl-cloud-line'"></bl-tag>
      <bl-tag v-for="tag in tags" style="margin-top: 5px" :bg-color="tag.bgColor" :icon="tag.icon">{{ tag.content }}</bl-tag>
    </div>
    <div v-for="(line, index) in tagLins" :key="line" :class="[line]" :style="{ left: -1 * (index + 1) * 5 + 'px' }"></div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { PropType } from 'vue'
import { useConfigStore } from '@renderer/stores/config'
import { isNotBlank } from '@renderer/assets/utils/obj'
import { computedDocTitleColor } from '@renderer/views/doc/doc'

const { viewStyle } = useConfigStore()

const props = defineProps({
  trees: { type: Object as PropType<DocTree>, default: {} },
  size: { type: Number, default: 14 },
  level: { type: Number, required: true }
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

// 竖条状态标识
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

    .menu-icon {
      @include box($icon-size, $icon-size, $icon-size, $icon-size, $icon-size, $icon-size);
      margin-top: 5px;
      margin-right: 8px;
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

// 专题样式, 包括边框和文字样式
.subject-title {
  @include flex(row, flex-start, flex-start);
  @include themeShadow(2px 2px 10px 1px #fad7d7, 1px 2px 10px 1px #0a0a0a);
  @include themeBg(linear-gradient(135deg, #fad7d7, #fae7e7, #ffffff), linear-gradient(135deg, #594a23, #453d28, #33302b));
  max-width: calc(100% - 15px);
  min-width: calc(100% - 15px);
  padding: 2px 5px;
  margin: 5px 0 10px 0;
  border-radius: 7px;
  position: relative;

  .doc-name {
    @include flex(row, flex-start, flex-start);
    @include themeBrightness(100%, 100%);
    color: var(--el-color-primary);
    align-content: flex-start;
    flex-wrap: wrap;
    width: 100%;

    .menu-icon {
      @include box($icon-size, $icon-size, $icon-size, $icon-size, $icon-size, $icon-size);
      margin-top: 5px;
      margin-right: 8px;
    }

    .name-wrapper {
      @include ellipsis();
      max-width: calc(100% - 25px);
      min-width: calc(100% - 25px);
    }
  }

  .sort {
    position: absolute;
    right: -15px;
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
  @include themeBg(rgb(237, 204, 11), rgba(228, 195, 5, 0.724));
}

.open-line {
  background: #79c20c71;
}

.sync-line {
  background: #e8122479;
}
</style>
