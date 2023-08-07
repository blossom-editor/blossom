import { globalShortcut, BrowserWindow } from 'electron'
import { printScreen } from './printScreen'

/**
 * 全局快捷键注册器
 */
class ShortcutRegistrant {
  win: BrowserWindow;

  constructor(win: BrowserWindow) {
    console.log('5. 注册快捷键');
    this.win = win;
  }

  /**
   * 注册截屏快捷键
   * CommandOrControl+Alt+Q
   * @returns 
   */
  printScreen(): ShortcutRegistrant {
    console.log('   5.1 注册全局截图快捷键 [CommandOrControl+Alt+Q]');
    globalShortcut.register('CommandOrControl+Alt+Q', () => {
      console.log('调用快捷键', 'CommandOrControl+Alt+Q');
      printScreen(this.win)
    })
    return this;
  }
}

export default ShortcutRegistrant