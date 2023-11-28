import { onActivated, onMounted } from 'vue'

/**
 * 初始化时生命周期封装, 主要控制 mounted 和 activated 只执行一次
 * @param mounted
 * @param activated 
 */
export const useLifecycle = (mounted: Function, activated: Function) => {
  let isMounted = false

  onMounted(() => {
    if (!isMounted) {
      mounted()
    }
  })

  onActivated(() => {
    if (isMounted) {
      activated()
    }
    isMounted = true
  })
}
