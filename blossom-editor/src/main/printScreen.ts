import { BrowserWindow, Notification } from 'electron'
import { platform } from '@electron-toolkit/utils'
import cp from "child_process"
import { join } from 'path'

/**
 * 截图功能
 * @param win 窗口
 * @returns 
 */
export const printScreen = (win: BrowserWindow) => {
  // 非 window 平台不支持截图
  if (!platform.isWindows) {
    if (Notification.isSupported()) {
      new Notification({
        title: '抱歉, 当前平台暂不支持截图功能'
      }).show()
    }
    return
  }
  let filePath = join(__dirname, '../../resources/screenshot/PrintScr.exe')
  // 截屏时将页面隐藏
  win.hide()
  const screen_window = cp.execFile(filePath)
  let psCode = 0
  screen_window.on('exit', (code) => {
    // 截图结果: 1:截图成功; 0:没有截图;
    if (code) {
      psCode = code
      win.webContents.send('printScreenAfter', psCode)
    }
    win.show()
    win.moveTop()
  })
}

export default printScreen