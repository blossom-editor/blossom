import { onBeforeUnmount, onMounted, watchEffect } from 'vue'
import type { Ref } from 'vue'
import { Local } from '@renderer/assets/utils/storage'

/**
 * persistent: 是否持久化
 * keyOne: 组件1持久化 key,
 * keyTwo: 组件2持久化 key,
 * defaultOne: 组件1默认宽度,
 * defaultTwo: 组件2默认宽度,
 * maxOne: 组件1最大宽度,
 * minOne: 组件1最小宽度
 */
type Option = {
  persistent: boolean
  keyOne: string
  keyTwo: string
  defaultOne: string
  defaultTwo: string
  maxOne?: number
  minOne?: number
}

/**
 * 左右拖动改变布局的功能
 *
 * @param oneRef 拖动影响到组件1
 * @param twoRef 拖动影响到组件2
 * @param resizeDividerRef 拖动条对象
 * @param twoPendantRef 另一个组件可能存在的挂件
 * @param options 拖动配置
 */
export const useResizeVertical = (
  oneRef: Ref<HTMLElement | undefined>,
  twoRef: Ref<HTMLElement | undefined>,
  resizeDividerRef: Ref<HTMLElement | undefined>,
  twoPendantRef?: Ref<HTMLElement | undefined>,
  options: Option = {
    persistent: false,
    keyOne: '',
    keyTwo: '',
    defaultOne: '',
    defaultTwo: '',
    maxOne: -1,
    minOne: -1
  }
) => {
  let last = {
    persistent: options.persistent,
    defaultOne: options.defaultOne,
    defaultTwo: options.defaultTwo,
    lastOne: '',
    lastTwo: ''
  }

  let timer: NodeJS.Timeout | undefined
  const debounce = (fn: () => void, time = 200) => {
    if (timer != undefined) {
      clearTimeout(timer)
    }
    timer = setTimeout(fn, time)
  }

  const hideOne = () => {
    oneRef.value!.style.width = `0px`
    twoRef.value!.style.width = `calc(100% - 2px)`
  }

  const resotreOne = () => {
    oneRef.value!.style.width = last.lastOne
    twoRef.value!.style.width = last.lastTwo
  }

  const persistent = () => {
    if (options.persistent) {
      debounce(() => {
        last.lastOne = oneRef.value!.style.width
        last.lastTwo = twoRef.value!.style.width
        Local.set(options.keyOne, last.lastOne)
        Local.set(options.keyTwo, last.lastTwo)
      })
    }
  }

  const onMousedown = (_e: MouseEvent) => {
    const targetRect = oneRef.value!.getBoundingClientRect()
    // editor 距离应用左侧的距离
    const targetLeft = targetRect.left

    document.body.style.cursor = 'ew-resize'

    const onMousemove = (e: MouseEvent) => {
      let oneWidth = Math.max(0, e.clientX - targetLeft)
      if (options.maxOne && options.maxOne > 0) {
        oneWidth = Math.min(options.maxOne, oneWidth)
      }
      if (options.minOne && options.minOne > 0) {
        oneWidth = Math.max(options.minOne, oneWidth)
      }
      oneRef.value!.style.width = `${oneWidth}px`
      twoRef.value!.style.width = `calc(100% - ${oneWidth}px - 2px)`
      if (twoPendantRef) {
        twoPendantRef.value!.style.left = `${oneWidth}px`
      }
      persistent()
    }

    const onMouseup = () => {
      document.body.style.cursor = 'auto'
      document.removeEventListener('mousemove', onMousemove)
      document.removeEventListener('mouseup', onMouseup)
    }

    document.addEventListener('mousemove', onMousemove)
    document.addEventListener('mouseup', onMouseup)
  }

  const onResize = () => {
    if (twoRef.value && oneRef.value && resizeDividerRef.value) {
      resizeDividerRef.value.addEventListener('mousedown', onMousedown)
    }
  }

  const offResize = () => {
    if (twoRef.value && oneRef.value && resizeDividerRef.value) {
      resizeDividerRef.value.removeEventListener('mousedown', onMousedown)
    }
  }

  onMounted(() => {
    if (options.persistent) {
      oneRef.value!.style.width = Local.get(options.keyOne) || options.defaultOne
      twoRef.value!.style.width = Local.get(options.keyTwo) || options.defaultTwo
      last.lastOne = oneRef.value!.style.width
      last.lastTwo = twoRef.value!.style.width
    }
    watchEffect(() => {
      onResize()
    })
  })

  onBeforeUnmount(() => {
    offResize()
  })

  return { last, hideOne, resotreOne }
}
