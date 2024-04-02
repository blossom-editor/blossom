import { onBeforeUnmount, onMounted, watchEffect } from 'vue'
import type { Ref } from 'vue'

/**
 * 元素拖拽
 * @param targetRef 拖拽时移动的元素
 * @param dragRef 拖拽时拖动的元素
 * @param regionRef 元素可以拖拽的范围
 */
export const useDraggable = (
  targetRef: Ref<HTMLElement | undefined>,
  dragRef: Ref<HTMLElement | undefined>,
  regionRef?: Ref<HTMLElement | undefined>
  // draggable: ComputedRef<boolean>
) => {
  let transform = {
    offsetX: 0,
    offsetY: 0
  }

  const onMousedown = (e: MouseEvent) => {
    const downX = e.clientX
    const downY = e.clientY
    const { offsetX, offsetY } = transform

    const targetRect = targetRef.value!.getBoundingClientRect()
    const targetLeft = targetRect.left
    const targetTop = targetRect.top
    const targetWidth = targetRect.width
    const targetHeight = targetRect.height

    let clientWidth = document.documentElement.clientWidth
    let clientHeight = document.documentElement.clientHeight
    let minLeft = -targetLeft + offsetX
    let minTop = -targetTop + offsetY

    if (regionRef) {
      console.log(regionRef.value!.getBoundingClientRect())
      const rect = regionRef.value!.getBoundingClientRect()
      clientWidth = rect.width + rect.x
      clientHeight = rect.height + rect.y
      minLeft += rect.x
      minTop += rect.y
    } else {
      clientWidth = document.documentElement.clientWidth
      clientHeight = document.documentElement.clientHeight
    }

    const maxLeft = clientWidth - targetLeft - targetWidth + offsetX
    const maxTop = clientHeight - targetTop - targetHeight + offsetY

    const onMousemove = (e: MouseEvent) => {
      const moveX = Math.min(Math.max(offsetX + e.clientX - downX, minLeft), maxLeft)
      const moveY = Math.min(Math.max(offsetY + e.clientY - downY, minTop), maxTop)

      transform = {
        offsetX: moveX,
        offsetY: moveY
      }
      targetRef.value!.style.transform = `translate(${moveX}px, ${moveY}px)`
    }

    const onMouseup = () => {
      document.removeEventListener('mousemove', onMousemove)
      document.removeEventListener('mouseup', onMouseup)
    }

    document.addEventListener('mousemove', onMousemove)
    document.addEventListener('mouseup', onMouseup)
  }

  const onDraggable = () => {
    if (dragRef.value && targetRef.value) {
      dragRef.value.addEventListener('mousedown', onMousedown)
    }
  }

  const offDraggable = () => {
    if (dragRef.value && targetRef.value) {
      dragRef.value.removeEventListener('mousedown', onMousedown)
    }
  }

  onMounted(() => {
    watchEffect(() => {
      // if (draggable.value) {
      onDraggable()
      // } else {
      //   offDraggable()
      // }
    })
  })

  onBeforeUnmount(() => {
    offDraggable()
  })
}
