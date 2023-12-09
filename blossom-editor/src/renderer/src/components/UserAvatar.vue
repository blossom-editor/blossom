<template>
  <div ref="AvatorWrapperRef" class="avator-wrapper" @mouseleave="mouseLevel" @mouseenter="mouseEnter($event)" @mousemove="mouseMove($event)">
    <img
      v-if="userinfo.avatar != ''"
      ref="AcatorInnerRef"
      :class="['avatar-img', viewStyle.isGlobalShadow ? 'img-shadow' : '']"
      :src="userinfo.avatar"
      :style="innerStyle" />
    <img
      v-else
      :class="['avatar-img', viewStyle.isGlobalShadow ? 'img-shadow' : '']"
      ref="AcatorInnerRef"
      src="@renderer/assets/imgs/default_user_avatar.jpg"
      :style="innerStyle" />
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useUserStore } from '@renderer/stores/user'
import { useConfigStore } from '@renderer/stores/config'
import { storeToRefs } from 'pinia'

const configStore = useConfigStore()
const userStore = useUserStore()
const { userinfo } = storeToRefs(userStore)
const { viewStyle } = configStore

onMounted(() => {
  mouse.setOrigin(AvatorWrapperRef.value)
})

const innerStyle = ref<any>({})
const AvatorWrapperRef = ref()
const AcatorInnerRef = ref()
var counter = 0
var updateRate = 10

const isTimeToUpdate = () => {
  return counter++ % updateRate === 0
}

// Mouse
const mouse = {
  // 该值越大, 图片变形越大
  rotateBase: 2,
  centerX: 0,
  centerY: 0,
  x: 0,
  y: 0,
  /**
   * 修改位置
   * @param event
   */
  updatePosition: function (event: any) {
    var e = event || window.event
    this.x = e.offsetX - this.centerX
    this.y = (e.offsetY - this.centerY) * -1
  },
  setOrigin: function (e?: any) {
    /**
     * 设置元素的中心点, 相对于父元素
     * offsetLeft wrapperr 距离父元素左侧的距离
     * offsetTop  wrapperr 距离父元素顶部的距离
     * offsetWidth wrapperr 的宽度, 包含 border 和 padding
     * offsetWidth wrapperr 的高度, 包含 border 和 padding
     */
    this.centerX = Math.floor(e.offsetWidth / 2)
    this.centerY = Math.floor(e.offsetHeight / 2)
  },
  show: function () {
    return '(' + this.x + ', ' + this.y + ')'
  }
}

const mouseEnter = (event: any) => {
  update(event)
}

const mouseLevel = () => {
  innerStyle.value = {}
}

const mouseMove = (event: any) => {
  if (isTimeToUpdate()) {
    update(event)
  }
}

const update = (event: any) => {
  mouse.updatePosition(event)
  updateTransformStyle(
    ((mouse.y / AcatorInnerRef.value.offsetHeight) * mouse.rotateBase).toFixed(2),
    ((mouse.x / AcatorInnerRef.value.offsetWidth) * mouse.rotateBase).toFixed(2)
  )
}

/**
 * 修改
 * @param x
 * @param y
 */
const updateTransformStyle = (x: string, y: string) => {
  var style = 'rotateX(' + x + 'deg) rotateY(' + y + 'deg)'
  innerStyle.value.transform = style
}
</script>

<style scoped lang="scss">
.avator-wrapper {
  @include box(200px, 200px, 200px, 200px, 200px, 200px);
  perspective: 40px;

  .avatar-img {
    @include box(100%, 100%);
    @include themeBrightness();
    object-fit: cover;
    border-radius: 10px;
    transition: transform 0.2s;
  }

  .img-shadow {
    box-shadow: var(--bl-box-shadow);
  }
}
</style>
