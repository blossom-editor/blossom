const prefix = 'blossom_web_'

/**
 * window.localStorage 浏览器永久缓存
 * @method set 设置永久缓存
 * @method get 获取永久缓存
 * @method remove 移除永久缓存
 * @method clear 移除全部永久缓存
 */
export const Local = {
  // 设置永久缓存
  set(key: string, val: any) {
    window.localStorage.setItem(wrapKey(key), JSON.stringify(val))
  },
  // 获取永久缓存
  get(key: string): any {
    let json = <string>window.localStorage.getItem(wrapKey(key))
    return JSON.parse(json)
  },
  // 移除永久缓存
  remove(key: string) {
    window.localStorage.removeItem(wrapKey(key))
  },
  // 移除全部永久缓存
  clear() {
    window.localStorage.clear()
  }
}

/**
 * window.sessionStorage 浏览器临时缓存
 * @method set 设置临时缓存
 * @method get 获取临时缓存
 * @method remove 移除临时缓存
 * @method clear 移除全部临时缓存
 */
export const Session = {
  // 设置临时缓存
  set(key: string, val: any) {
    window.sessionStorage.setItem(wrapKey(key), JSON.stringify(val))
  },
  // 获取临时缓存
  get(key: string) {
    let json = <string>window.sessionStorage.getItem(wrapKey(key))
    return JSON.parse(json)
  },
  // 移除临时缓存
  remove(key: string) {
    window.sessionStorage.removeItem(wrapKey(key))
  },
  // 移除全部临时缓存
  clear() {
    window.sessionStorage.clear()
  }
}

const wrapKey = (key: string) => {
  return prefix + key
}
