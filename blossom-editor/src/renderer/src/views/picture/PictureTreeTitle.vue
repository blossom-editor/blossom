<template>
  <div class="doc-title">
    <bl-tag class="sort" v-show="props.trees.showSort" :bgColor="levelColor" style="padding: 0 2px">
      {{ props.trees.s }}
    </bl-tag>
    <span class="doc-name">
      <img
        class="menu-icon-img"
        v-if="isNotBlank(props.trees.icon) && (props.trees.icon.startsWith('http') || props.trees.icon.startsWith('https')) && !props.trees.updn"
        :src="props.trees.icon" />
      <svg v-else-if="isNotBlank(props.trees.icon) && !props.trees.updn" class="icon menu-icon" aria-hidden="true">
        <use :xlink:href="'#' + props.trees.icon"></use>
      </svg>

      <el-input
        v-if="$props.trees?.updn"
        v-model="$props.trees.n"
        :id="'article-doc-name-' + props.trees.i"
        @blur="blurArticleNameInput"
        @keyup.enter="blurArticleNameInput"
        style="width: 95%"></el-input>
      <div v-else class="name-wrapper" :style="nameWrapperStyle">
        {{ props.trees.n }}
      </div>
      <bl-tag v-for="tag in tags" :bg-color="tag.bgColor" style="margin-top: 5px" :icon="tag.icon">{{ tag.content }}</bl-tag>
    </span>
    <div v-if="level >= 2" class="folder-level-line" style="left: -20px"></div>
    <div v-if="level >= 3" class="folder-level-line" style="left: -30px"></div>
    <div v-if="level >= 4" class="folder-level-line" style="left: -40px"></div>
  </div>
</template>
<script setup lang="ts">
import { computed } from 'vue'
import type { PropType } from 'vue'
import { isNotBlank } from '@renderer/assets/utils/obj'
import { computedDocTitleColor } from '@renderer/views/doc/doc'
import { folderUpdNameApi } from '@renderer/api/blossom'

//#region ----------------------------------------< 标题信息 >--------------------------------------

const props = defineProps({
  trees: { type: Object as PropType<DocTree>, default: {} },
  level: { type: Number, required: true }
})

const levelColor = computed(() => {
  return computedDocTitleColor(props.level)
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
      icons.unshift({ content: '专题', bgColor: 'var(--bl-tag-color-subject)', icon: 'bl-a-lowerrightpage-line' })
    } else if (tag === 'toc') {
      icons.push({ content: 'TOC', bgColor: 'var(--bl-tag-color-toc)' })
    } else {
      icons.push({ content: tag })
    }
  })
  if (props.trees.o === 1 && props.trees.ty != 3) {
    icons.unshift({ bgColor: 'var(--bl-tag-color-open)', icon: 'bl-cloud-line' })
  }
  return icons
})

/**
 * 重命名文章
 */
const blurArticleNameInput = () => {
  folderUpdNameApi({ id: props.trees.i, name: props.trees.n }).then((_resp) => {
    props.trees.updn = false
  })
}
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
    }
  }

  .sort {
    position: absolute;
    right: 0;
    top: 2px;
  }

  .folder-level-line {
    @include box(1px, 100%);
    background-color: var(--el-border-color);
    position: absolute;
    opacity: 0;
    transition: opacity 0.5s;
  }
}
</style>
