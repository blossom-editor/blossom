import { isMap, isUndefined } from "lodash"

export type shortcutFunc = () => void

// 以下快捷键不会参与到监听中
const ignoreCodes: string[] = ['Backspace', 'Enter', 'ArrowUp', 'ArrowDown', 'ArrowLeft', 'ArrowRight']

/**
 * 局部快捷键注册
 */
class ShortcutRegistrant {
  shortcutMap: Map<string, Map<string, shortcutFunc | Map<string, shortcutFunc>>> = new Map()
  downIndex: number = 0
  downCodes: string[] = (['', '', ''])
  debug: boolean = false

  public setDebug(debug: boolean): ShortcutRegistrant {
    this.debug = debug
    return this
  }

  /**
   * 注册快捷键
   * @param key   快捷键的开始前缀
   * @param value 快捷键对应的值, 可以是嵌套的快捷键key, 也可以是一个函数 {@link shortcutFunc}, 如果是函数, 则执行函数中的内容
   */
  public register(key: string, value: Map<string, shortcutFunc | Map<string, shortcutFunc>>): void {
    this.shortcutMap.set(key, value)
  }

  /**
   * 清空按下的记录
   * 例如在按下一个键后切出到其他应用(比如使用 Alt + Tab 键切换), 由于 Alt 键是在应用内下, 随后切换到了其他窗口, 此时
   * 无法获得 Alt 键的松开事件, 导致切回后按下 1 键, 会认为按下的是 alt + 1 键, 并且 alt 永远都不会被松开, 导致快捷键出
   * 现问题.
   * 
   * 所以需要在离开应用窗口后删除记录按下的快捷键, 可以使用: 
   * 
   * <pre>
   * window.onblur = () => {
   *   shortcutRegistrant.clearDownCodes()
   * }
   * </pre>
   */
  public clearDownCodes(): void {
    this.downCodes[0] = ''
    this.downCodes[1] = ''
    this.downCodes[2] = ''
    this.downIndex = 0
  }

  /**
   * 键盘松开事件
   * @param event 
   */
  public keyup(event: KeyboardEvent): void {
    if (this.debug) {
      let row1 = { col1: '动作', col2: '松开' }
      let row2 = { col1: 'key', col2: event.key }
      let row3 = { col1: 'code', col2: event.code }
      console.table([row1, row2, row3])
    }

    let code: string = event.code
    // 主要针对 mac 下的按键逻辑
    if (code === 'MetaLeft') {
      this.clearDownCodes()
    }
    let upCodeIndex: number = this.downCodes.indexOf(code)

    if (upCodeIndex !== -1) {
      this.downCodes[upCodeIndex] = ''
      this.downIndex--
    }
  }

  /**
   * 键盘按下事件
   * @param event 
   */
  public keydown(event: KeyboardEvent): void {
    if (this.debug) {
      let row1 = { col1: '动作', col2: '按下' }
      let row2 = { col1: 'key', col2: event.key }
      let row3 = { col1: 'code', col2: event.code }
      console.table([row1, row2, row3])
    }
    if (ignoreCodes.indexOf(event.code) > -1) {
      return
    }
    if (this.downIndex >= 3) {
      return
    }
    let code: string = event.code
    if (this.downCodes.indexOf(code) != -1) {
      return
    }
    let lastdown = this.downCodes[this.downIndex]
    if (lastdown === '') {
      this.downCodes[this.downIndex] = code
      this.downIndex++
      // 如果命中快捷键, 则阻止浏览器默认行为
      if (this.choiceShortcut(this.downCodes)) {
        event.preventDefault()
      }
    }
  }

  private choiceShortcut(codeArray: string[]): boolean {
    let underfinedOrMap: undefined | Map<string, shortcutFunc | Map<string, shortcutFunc>> = this.shortcutMap.get(codeArray[0])
    if (isUndefined(underfinedOrMap)) {
      return false
    }
    let underfinedOrMap2: undefined | shortcutFunc | Map<string, shortcutFunc> = underfinedOrMap.get(codeArray[1])
    if (isUndefined(underfinedOrMap2)) {
      return false
    }
    if (isMap(underfinedOrMap2)) {
      let underfinedOrMap3: undefined | shortcutFunc = underfinedOrMap2.get(codeArray[2])
      if (isUndefined(underfinedOrMap3)) {
        return false
      }
      underfinedOrMap3()
      return true
    }

    underfinedOrMap2()
    return true
  }

}

export default ShortcutRegistrant