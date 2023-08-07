<template>
  <div class="bl-row-root" :style="{
    justifyContent: props.just,
    alignItems: props.align,
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
  just: {
    type: String,
    default: 'flex-start'
  },
  /**
   * align-items
   */
  align: {
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