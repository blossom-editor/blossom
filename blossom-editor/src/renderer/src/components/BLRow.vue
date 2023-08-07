<template>
  <div class="bl-row-root" :style="{
    justifyContent: props.justifyContent,
    alignItems: props.alignItems,
    width: props.width,
    height: props.height,
  }">
    <slot />
  </div>
</template>

<script setup lang="ts">
const props = defineProps({
  /**
   * justify-content
   */
  justifyContent: {
    type: String,
    default: 'flex-start'
  },
  /**
   * align-items
   */
  alignItems: {
    type: String,
    default: 'center'
  },
  width: {
    type: String,
    default: '100%'
  },
  height: {
    type: String
  }
})
</script>
<style scoped lang="scss">
.bl-row-root {
  // width: 100%;
  display: flex;
}
</style>