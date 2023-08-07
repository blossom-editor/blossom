<template>
  <div class="bl-col-root" :style="{
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
    type: String
  },
  height: {
    type: String,
    default: '100%'
  }
})
</script>
<style scoped lang="scss">
.bl-col-root {
  display: flex;
  flex-direction: column;
}
</style>