<template>
  <div :class="['doc-title', props.trees.t?.includes('subject') ? 'subject-title' : '']" @click="handlClick">
    <div class="doc-name">
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
      <bl-tag v-for="tag in tags" style="margin-top: 5px" :bg-color="tag.bgColor" :icon="tag.icon">{{ tag.content }}</bl-tag>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { PropType } from 'vue'
import { isNotBlank } from '@/assets/utils/obj'
import BLTag from '@/components/BLTag.vue'

//#region ----------------------------------------< 标题信息 >--------------------------------------

const props = defineProps({
  trees: { type: Object as PropType<DocTree>, default: {} }
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

.doc-title {
  @include flex(row, flex-start, flex-start);
  max-width: calc(100% - 15px);
  min-width: calc(100% - 15px);
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
  position: relative;
  padding: 2px 5px;
  margin: 5px 0 10px 0;
  border-radius: 4px;
  box-shadow: 1px 1px 5px #a2a2a2;
  background: linear-gradient(135deg, #ffffff, #f0f0f0, #cacaca);

  .doc-name {
    min-width: 145px;
    max-width: 145px;
    color: #4a545e;
    text-shadow: 2px 2px 3px #9393939d;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }
}

// 专题样式, 包括边框和文字样式
.subject-title {
  @include flex(row, flex-start, flex-start);
  box-shadow: 1px 1px 5px #a2a2a2;
  background: linear-gradient(135deg, #ffffff, #f0f0f0, #cacaca);
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
}
</style>
