<template>
  <div :class="titleClass">
    <div class="doc-name">
      <img
        class="menu-icon-img"
        v-if="isNotBlank(props.trees.icon) && (props.trees.icon.startsWith('http') || props.trees.icon.startsWith('https'))"
        :src="props.trees.icon" />
      <svg v-else-if="isNotBlank(props.trees.icon)" class="icon menu-icon" aria-hidden="true">
        <use :xlink:href="'#' + props.trees.icon"></use>
      </svg>
      <div class="name-wrapper" :style="nameWrapperStyle">
        {{ props.trees.n }}
      </div>
      <bl-tag v-for="tag in tags" style="margin-top: 5px" :bg-color="tag.bgColor" :icon="tag.icon">{{ tag.content }}</bl-tag>
    </div>
    <div v-if="level === 2" class="folder-level-line" style="left: -26px"></div>
    <div v-if="level === 3" class="folder-level-line" style="left: -36px"></div>
    <div v-if="level === 3" class="folder-level-line" style="left: -22px"></div>
    <!--  -->
    <div v-if="level === 4" class="folder-level-line" style="left: -46px"></div>
    <div v-if="level === 4" class="folder-level-line" style="left: -32px"></div>
    <div v-if="level === 4" class="folder-level-line" style="left: -18px"></div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { PropType } from 'vue'
import { isNotBlank } from '@/assets/utils/obj'
import { getThemeSubjecTitle } from '@/scripts/env'

//#region ----------------------------------------< 标题信息 >--------------------------------------

const props = defineProps({
  trees: { type: Object as PropType<DocTree>, default: {} },
  level: { type: Number, required: true }
})

const nameWrapperStyle = computed(() => {
  return {
    maxWidth: isNotBlank(props.trees.icon) ? 'calc(100% - 25px)' : '100%'
  }
})

/**
 * 计算标签, 并返回便签对象集合
 */
const tags = computed(() => {
  let icons: any = []
  props.trees.t?.forEach((tag) => {
    if (tag === 'subject') {
      icons.unshift({ content: '专题', bgColor: '#C0C0C0', icon: 'bl-a-lowerrightpage-line' })
    } else if (tag === 'toc') {
      icons.push({ content: 'TOC', bgColor: '#c1992f' })
    } else {
      icons.push({ content: tag })
    }
  })
  return icons
})

const titleClass = computed(() => {
  if (!getThemeSubjecTitle()) {
    return 'doc-title'
  }

  if (props.trees.t.includes('subject')) {
    return 'subject-title'
  }

  return 'doc-title'
})

//#endregion
</script>

<style scoped lang="scss">
$icon-size: 17px;

.doc-title {
  @include flex(row, flex-start, flex-start);
  width: 100%;
  padding-bottom: 1px;
  position: relative;

  .doc-name {
    @include flex(row, flex-start, flex-start);
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
    }
  }

  .sort {
    position: absolute;
    padding: 0 2px;
    right: 0px;
    top: 2px;
    z-index: 10;
  }
  .folder-level-line {
    height: 100%;
  }
}

.folder-level-line {
  width: 1.5px;
  background-color: var(--el-border-color);
  box-sizing: border-box;
  position: absolute;
  opacity: 0;
  transition: opacity 0.5s;
}

// 专题样式, 包括边框和文字样式
.subject-title {
  @include flex(row, flex-start, flex-start);
  background: linear-gradient(135deg, #ffffff, #f0f0f0, #cacaca);
  box-shadow: 1px 1px 5px #a2a2a2;
  max-width: calc(100% - 15px);
  min-width: calc(100% - 15px);
  padding: 2px 5px;
  margin: 5px 0 10px 0;
  border-radius: 7px;
  position: relative;

  .doc-name {
    @include flex(row, flex-start, flex-start);
    color: var(--el-color-primary);
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
      min-width: calc(100% - 25px);
    }
  }

  .sort {
    position: absolute;
    right: -15px;
  }

  .folder-level-line {
    height: calc(100% + 25px);
    top: -5px;
  }
}
</style>
