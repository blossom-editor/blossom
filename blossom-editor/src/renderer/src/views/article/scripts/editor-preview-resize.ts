import { onBeforeUnmount, onMounted, watchEffect } from 'vue'
import type { Ref } from 'vue'

/**
 * 用与拖拽调整调整编辑器和预览部分的宽度
 * @param editorRef 编辑器
 * @param previewRef 预览
 * @param resizeDividerRef 拖动条
 */
export const useResize = (
  editorRef: Ref<HTMLElement | undefined>,
  previewRef: Ref<HTMLElement | undefined>,
  resizeDividerRef: Ref<HTMLElement | undefined>,
  operatorRef: Ref<HTMLElement | undefined>
) => {
  const onMousedown = (_e: MouseEvent) => {
    const targetRect = editorRef.value!.getBoundingClientRect()
    // editor 距离应用左侧的距离
    const targetLeft = targetRect.left

    resizeDividerRef.value!.style.borderLeft = '2px dashed var(--el-color-primary)'
    document.body.style.cursor = 'ew-resize'

    const onMousemove = (e: MouseEvent) => {
      const x = Math.max(0, e.clientX - targetLeft)
      editorRef.value!.style.width = `${x}px`
      operatorRef.value!.style.left = `${x + 1}px`
      previewRef.value!.style.width = `calc(100% - ${x}px - 3px)`
    }

    const onMouseup = () => {
      document.body.style.cursor = 'auto'
      resizeDividerRef.value!.style.borderLeft = '2px solid var(--el-border-color)'
      document.removeEventListener('mousemove', onMousemove)
      document.removeEventListener('mouseup', onMouseup)
    }

    document.addEventListener('mousemove', onMousemove)
    document.addEventListener('mouseup', onMouseup)
  }

  const onResize = () => {
    if (previewRef.value && editorRef.value && resizeDividerRef.value) {
      resizeDividerRef.value.addEventListener('mousedown', onMousedown)
    }
  }

  const offResize = () => {
    if (previewRef.value && editorRef.value && resizeDividerRef.value) {
      resizeDividerRef.value.removeEventListener('mousedown', onMousedown)
    }
  }

  onMounted(() => {
    watchEffect(() => {
      onResize()
    })
  })

  onBeforeUnmount(() => {
    offResize()
  })
}
