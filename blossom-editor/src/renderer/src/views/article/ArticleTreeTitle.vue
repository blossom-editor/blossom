<template>
  <div :class="[viewStyle.isShowSubjectStyle ? (isSubject ? 'subject-title' : 'doc-title') : 'doc-title']">
    <bl-tag class="sort" v-show="props.trees.showSort" :bgColor="levelColor">
      {{ props.trees.s }}
    </bl-tag>
    <div class="doc-name">
      <img
        class="menu-icon-img"
        v-if="isNotBlank(props.trees.icon) && (props.trees.icon.startsWith('http') || props.trees.icon.startsWith('https')) && !props.trees?.updn"
        :src="props.trees.icon" />
      <svg v-else-if="isNotBlank(props.trees.icon) && !props.trees?.updn" class="icon menu-icon" aria-hidden="true">
        <use :xlink:href="'#' + props.trees.icon"></use>
      </svg>
      <el-input
        v-if="props.trees?.updn"
        v-model="props.trees.n"
        :id="'article-doc-name-' + props.trees.i"
        @blur="blurArticleNameInput"
        @keyup.enter="blurArticleNameInput"
        style="width: 95%"></el-input>
      <div v-else class="name-wrapper" :style="nameWrapperStyle">
        {{ props.trees.n }}
      </div>
      <bl-tag v-for="tag in tags" style="margin-top: 5px" :bg-color="tag.bgColor" :icon="tag.icon">{{ tag.content }}</bl-tag>
    </div>
    <div v-if="level >= 2" class="folder-level-line" style="left: -20px"></div>
    <div v-if="level >= 3" class="folder-level-line" style="left: -30px"></div>
    <div v-if="level >= 4" class="folder-level-line" style="left: -40px"></div>
    <div v-for="(line, index) in tagLins" :key="line" :class="[line]" :style="{ left: -1 * (index + 1.5) * 4 + 'px' }"></div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { PropType } from 'vue'
import { useConfigStore } from '@renderer/stores/config'
import { isNotBlank } from '@renderer/assets/utils/obj'
import { computedDocTitleColor } from '@renderer/views/doc/doc'
import { articleUpdNameApi, folderUpdNameApi } from '@renderer/api/blossom'

const { viewStyle } = useConfigStore()

const props = defineProps({
  trees: { type: Object as PropType<DocTree>, default: {} },
  size: { type: Number, default: 14 },
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
 * 是否是专题
 */
const isSubject = computed(() => {
  return props.trees.t?.includes('subject')
})

/**
 * 计算标签, 并返回便签对象集合
 */
const tags = computed(() => {
  let icons: any = []
  props.trees.t?.forEach((tag) => {
    if (tag.toLocaleLowerCase() === 'subject') {
      icons.unshift({ content: '专题', bgColor: 'var(--bl-tag-color-subject)', icon: 'bl-a-lowerrightpage-line' })
    } else if (tag.toLocaleLowerCase() === 'toc') {
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

/**
 * 重命名文章
 */
const blurArticleNameInput = () => {
  if (props.trees.ty === 3) {
    articleUpdNameApi({ id: props.trees.i, name: props.trees.n }).then((_resp) => {
      props.trees.updn = false
    })
  } else {
    folderUpdNameApi({ id: props.trees.i, name: props.trees.n }).then((_resp) => {
      props.trees.updn = false
    })
  }
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
    padding: 0 2px;
    right: 0px;
    top: 2px;
    z-index: 10;
  }
  .folder-level-line {
    @include box(1px, 100%);
  }
}

.folder-level-line {
  background-color: var(--el-border-color);
  position: absolute;
  opacity: 0;
  transition: opacity 0.5s;
}

// 专题样式, 包括边框和文字样式
.subject-title {
  @include flex(row, flex-start, flex-start);
  @include themeShadow(2px 2px 10px 1px var(--el-color-primary-light-8), 2px 2px 10px 1px #131313);
  background: linear-gradient(135deg, var(--el-color-primary-light-7), var(--el-color-primary-light-8), var(--bl-html-color));
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
    @include box(1px, calc(100% + 15px));
    top: -5px;
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
