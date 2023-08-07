<template>
  <div class="doc-title" @click="handlClick" @click.right="handleClickRight" :style="{ fontSize: size + 'px' }">
    <svg v-if="isNotBlank(props.trees.icon)" class="icon menu-icon" aria-hidden="true">
      <use :xlink:href="'#' + props.trees.icon"></use>
    </svg>
    <span class="doc-name">{{ props.trees.n }}</span>
    <!-- 如果专题是公开的, 则单独显示公开标签 -->
    <bl-tag v-if="props.trees.o === 1 && isSubjectDoc" :bg-color="'#7AC20C'" :icon="'bl-cloud-line'"></bl-tag>
    <div v-for="tag in tags">
      <bl-tag v-if="tag.content" :bg-color="tag.bgColor" :icon="tag.icon">{{ tag.content }}</bl-tag>
      <bl-tag v-else :bg-color="tag.bgColor" :icon="tag.icon" />
    </div>
    <div v-if="isOpen && !isSubjectDoc" class="open-line"></div>
    <div v-if="isStar" class="star-line"></div>

    <!-- 右键菜单, 添加到 body 下 -->
    <Teleport to="body">
      <div v-if="rMenu.show" class="right-menu" :style="{ left: rMenu.clientX + 'px', top: rMenu.clientY + 'px' }">
        <div class="doc-name">{{ props.trees.n }}</div>
        <div class="menu-content">
          <div :class="['menu-item', props.trees.i < 0 ? 'disabled' : '']" @click="handleShowDocInfoDialog('upd')">
            <span class="iconbl bl-a-fileedit-line"></span>编辑文档
          </div>
          <div :class="['menu-item', !isPictureFolder ? 'disabled' : '']"
            @click="handleShowDocInfoDialog('add', props.trees.p)">
            <span class="iconbl bl-a-fileadd-line"></span>新增<strong>同级</strong>文档
          </div>
          <!-- 只有文件夹才有子文档 -->
          <div :class="['menu-item', !isPictureFolder ? 'disabled' : '']"
            @click="handleShowDocInfoDialog('add', props.trees.i)">
            <span class="iconbl bl-a-fileadd-fill"></span>新增<strong>子级</strong>文档
          </div>
        </div>
      </div>
    </Teleport>
  </div>

  <!-- 详情的弹框 -->
  <el-dialog v-model="isShowDocInfoDialog" width="535" top="100px" style="margin-left: 65px;
    --el-dialog-padding-primary:0;
    --el-dialog-border-radius:10px;
    --el-dialog-box-shadow:var(--xz-box-shadow-dialog)" :append-to-body="true" :destroy-on-close="true"
    :close-on-click-modal="false" draggable>
    <PictureInfo ref="PictureInfoRef"></PictureInfo>
  </el-dialog>
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
import { ref, computed, onBeforeUnmount, nextTick } from 'vue'
import type { PropType } from 'vue'
import { isNotBlank } from '@renderer/assets/utils/obj'
import PictureInfo from '@renderer/views/picture/PictureInfo.vue'
import Notify from '@renderer/components/Notify'


//#region ----------------------------------------< 标题信息 >--------------------------------------

const props = defineProps({
  trees: { type: Object as PropType<DocTree>, default: {} },
  size: {
    type: Number,
    default: 14
  }
})

const isSubjectDoc = computed(() => {
  return props.trees.t?.includes('subject')
})

const isOpen = computed(() => {
  return props.trees.o === 1;
})

const isStar = computed(() => {
  return props.trees.star === 1;
})

const isPictureFolder = computed(() => {
  return props.trees.ty === 2;
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

/**
 * 点击文档菜单标题后的回调
 */
const handlClick = () => {
  emits('clickDoc', props.trees)
}

//#endregion


//#region ----------------------------------------< 右键菜单 >--------------------------------------
const rMenu = ref<RightMenu>({ show: false, clientX: 0, clientY: 0 })

onBeforeUnmount(() => {
  document.body.removeEventListener('click', closeMenuShow)
  document.body.removeEventListener('contextmenu', closeMenuShow)
})

const closeMenuShow = () => {
  // console.log('由监听关闭菜单', props.trees.n);
  rMenu.value.show = false
  document.body.removeEventListener('click', closeMenuShow)
  document.body.removeEventListener('contextmenu', closeMenuShow)
}

const handleClickRight = (event: Event) => {
  // console.log('右键点击', props.trees.n);
  //@ts-ignore
  rMenu.value = { show: true, clientX: event.clientX, clientY: event.clientY }
  setTimeout(() => {
    document.body.addEventListener('click', closeMenuShow)
    document.body.addEventListener('contextmenu', closeMenuShow)
  }, 100);
}

//#endregion 右键菜单

//#region ----------------------------------------< 新增修改详情弹框 >--------------------------------------

const PictureInfoRef = ref()
const isShowDocInfoDialog = ref<boolean>(false);

/**
 * 显示弹框
 * @param dialogType 弹框的类型, 新增, 修改
 * @param pid 父级ID, 新增同级或子集文档时使用
 */
const handleShowDocInfoDialog = (dialogType: DocDialogType, pid?: number) => {
  if (props.trees.i < 0) {
    Notify.info('当前文档为系统默认文档, 无法操作', '操作无效')
    return
  }

  if (dialogType === 'upd' && (props.trees == undefined || props.trees?.i == undefined)) {
    Notify.info('请先选则要修改的文件夹或文档')
    return
  }
  isShowDocInfoDialog.value = true
  if (dialogType === 'add') {
    nextTick(() => { PictureInfoRef.value.reload(dialogType, undefined, pid) })
  }
  if (dialogType === 'upd') {
    nextTick(() => {
      PictureInfoRef.value.reload(dialogType, props.trees.i)
    })
  }
}
//#endregion
const emits = defineEmits(['clickDoc'])
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
  max-width: calc(100% - 10px);
  min-width: calc(100% - 10px);
  padding-bottom: 1px;
  position: relative;
  font-size: 14px;
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

<style lang="scss">
.right-menu {
  @include box(170px, auto);
  @include themeShadow(3px 3px 7px 2px rgba(49, 49, 49, 0.3), 2px 3px 7px 2px rgb(0, 0, 0));
  position: fixed;
  background: var(--xz-html-color);
  z-index: 2002;
  border-radius: 5px;
  font-size: 12px;

  .doc-name {
    font-size: 13px;
    padding: 5px 10px;
    color: var(--el-color-primary);
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }

  .menu-content {
    margin: 0;
    // padding: 5px;
    color: var(--el-text-color-regular);
    border-top: 1px solid var(--el-border-color);
    padding: 5px 0;

    .menu-item-divider {
      height: 1px;
      border-bottom: 1px solid var(--el-border-color);
      margin-bottom: 5px;
    }

    .menu-item {
      @include flex(row, flex-start, center);
      line-height: 0.9;
      padding: 5px 5px 5px 10px;
      margin: 0 5px 5px 5px;
      // margin-bottom: 5px;
      border-radius: 5px;
      cursor: pointer;
      // transition: 0.3s;

      .iconbl {
        margin-right: 5px;
      }

      &:hover {
        background: var(--el-color-primary-light-8);
      }

      &:last-child {
        margin-bottom: 0;
      }
    }

    .disabled {
      cursor: not-allowed;
      pointer-events: none;
      color: var(--el-color-info-light-5);

      &:hover {
        color: var(--el-color-info-light-5);
      }
    }
  }

}
</style>