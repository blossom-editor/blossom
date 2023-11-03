<template>
  <span
    class="tag-root"
    :style="{
      color: props.color,
      backgroundColor: props.bgColor,
      fontSize: props.size + 'px',
      fontWeight: props.weight
    }">
    <!-- {{ !!slots.default }}| -->
    <span v-if="props.icon" :class="['tag-iconfont iconbl', props.icon, !!slots.default ? 'tag-icon-margin' : '']" />
    <span class="tag-content">
      <slot />
    </span>
  </span>
</template>

<script setup lang="ts">
import { useSlots } from 'vue'

const slots = useSlots()

const props = defineProps({
  /**
   * background-color
   */
  bgColor: {
    type: String,
    default: 'var(--el-color-primary)'
  },
  color: {
    type: String,
    default: 'var(--bl-html-color)'
  },
  /**
   * font-size
   */
  size: {
    type: Number,
    default: 12
  },
  /**
   * font-weight
   */
  weight: {
    type: Number,
    default: 500
  },
  icon: {
    type: String
  }
})
</script>
<style scoped lang="scss">
.tag-root {
  @include flex(row, center, center);
  box-shadow: 2px 2px 3px 0 #999999;
  border-radius: 4px;
  padding: 1px 4px;
  margin: 3px;
  height: 15px;
  min-height: 15px;
  max-height: 15px;
  text-align: center;

  .tag-iconfont {
    font-size: 12px;
  }

  .tag-icon-margin {
    margin-right: 2px;
  }

  .tag-content {
    line-height: 12px;
    transform: scale(0.9);
  }
}
</style>
