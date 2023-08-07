import { BrowserWindow } from 'electron'
// import { is } from '@electron-toolkit/utils'
import cp from "child_process"
import { join } from 'path';


export const printScreen = (win: BrowserWindow) => {
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