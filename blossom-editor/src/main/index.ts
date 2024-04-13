import { app, shell, ipcMain, BrowserWindow, Menu, IpcMainEvent, Tray, HandlerDetails } from 'electron'
import { join } from 'path'
import { electronApp, optimizer, is, platform } from '@electron-toolkit/utils'
import icon from '../../resources/imgs/icon.ico?asset'
import printScreen from './printScreen'
import ShortcutRegistrant from './shortcut'

// 主窗口
let mainWindow: BrowserWindow | undefined
let tray: Tray
let blossomUserinfo: any

const additionalData = { blossomSingle: 'blossomSingle' }
const gotTheLock = app.requestSingleInstanceLock(additionalData)

if (!gotTheLock) {
  app.quit()
} else {
  app.on('second-instance', (_event, _commandLine, _workingDirectory, _additionalData) => {
    // 输出从第二个实例中接收到的数据
    // 有人试图运行第二个实例，我们应该关注我们的窗口
    if (mainWindow) {
      if (mainWindow.isMinimized()) mainWindow.restore()
      mainWindow.focus()
    }
  })

  // This method will be called when Electron has finished initialization and is ready to create browser windows. Some APIs can only be used after this event occurs.
  /**
   * =========================================================================================================================
   * APP 启动完成
   * =========================================================================================================================
   */
  app.whenReady().then(() => {
    // Set app user model id for windows
    electronApp.setAppUserModelId('com.electron')
    /*
     * Default open or close DevTools by F12 in development and ignore CommandOrControl + R in production.
     * see https://github.com/alex8088/electron-toolkit/tree/master/packages/utils
     */
    app.on('browser-window-created', (_, window) => {
      optimizer.watchWindowShortcuts(window)
    })

    if (platform.isMacOS) {
      // Mac 平台下要设置 dock 栏图标
      // app.dock.setIcon(iconPng)
    }

    setTimeout(() => {
      createMainWindow()
    }, 300)

    // session.defaultSession.webRequest.onHeadersReceived((details, callback) => {
    //   callback({
    //     responseHeaders: {
    //       ...details.responseHeaders,
    //       'Content-Security-Policy': ['frame-ancestors *']
    //     }
    //   })
    // })

    app.on('activate', function () {
      // On macOS it's common to re-create a window in the app when the
      // dock icon is clicked and there are no other windows open.
      if (BrowserWindow.getAllWindows().length === 0) {
        if (mainWindow) {
          mainWindow.show()
        } else {
          createMainWindow()
        }
      } else if (mainWindow) {
        mainWindow.show()
      }
    })

    // Quit when all windows are closed, except on macOS. There, it's common
    // for applications and their menu bar to stay active until the user quits
    // explicitly with Cmd + Q.
    app.on('window-all-closed', () => {
      if (process.platform !== 'darwin') {
        app.quit()
      } else {
        if (mainWindow?.isDestroyed()) {
          mainWindow = undefined
        }
      }
    })
  })
}

/**
 * =========================================================================================================================
 * 创建窗口
 * =========================================================================================================================
 */
const buildWindow = (_title: string): BrowserWindow => {
  Menu.setApplicationMenu(null)
  let win = new BrowserWindow({
    show: true,
    width: 1400,
    height: 1022,
    minWidth: 972,
    minHeight: 700,
    icon: icon,
    frame: false, // 无边框
    transparent: false, // 透明窗口
    titleBarStyle: 'hidden',
    opacity: 1,
    hasShadow: true,
    autoHideMenuBar: false,
    ...(platform.isLinux ? { icon } : {}),
    webPreferences: {
      preload: join(__dirname, '../preload/index.js'),
      // 是否沙盒模式, 沙盒模式下, 渲染进程可以直接与主进程通信
      sandbox: false
    }
  })
  return win
}

/**
 * =========================================================================================================================
 * 创建主窗口
 * 官网API
 * https://www.electronjs.org/zh/docs/latest/api/browser-window#new-browserwindowoptions
 * =========================================================================================================================
 */
function createMainWindow(): void {
  if (mainWindow != undefined) {
    return
  }
  mainWindow = buildWindow('Blossom')
  mainWindow.setMenu(null)
  mainWindow.webContents.setWindowOpenHandler((details) => {
    shell.openExternal(details.url)
    return { action: 'deny' }
  })
  // HMR for renderer base on electron-vite cli. Load the remote URL for development or the local html file for production.
  if (is.dev && process.env['ELECTRON_RENDERER_URL']) {
    mainWindow.loadURL(process.env['ELECTRON_RENDERER_URL'])
  } else {
    mainWindow.loadFile(join(__dirname, '../renderer/index.html'))
  }
  console.log('============================================================')
  // 开发环境自动打开控制台
  openDevToos(mainWindow)
  // 创建 Tray
  initTray()
  // 创建焦点窗口事件
  initOnFocusedWindow()
  // 主窗口监听事件
  initOnMainWindow(mainWindow)
  // 注册全局快捷键 printScreen:截屏快捷键
  new ShortcutRegistrant(mainWindow).printScreen()
  console.log('============================================================')
}

/**
 * =========================================================================================================================
 * 托盘图标 Tray
 * =========================================================================================================================
 */
const initTray = () => {
  if (platform.isMacOS) {
    return
  }
  console.log('1. 创建托盘 Tray')
  tray = new Tray(icon)
  const contextMenu = Menu.buildFromTemplate([
    { label: 'Blossom 官网 ', click: () => shell.openExternal('https://www.wangyunf.com/blossom-doc/index') },
    { type: 'separator' },
    { label: '显示', click: () => mainWindow!.show() },
    { label: '退出', click: () => app.quit() }
  ])
  tray.setToolTip('Blossom\n未登录')
  tray.setContextMenu(contextMenu)
  tray.addListener('double-click', () => {
    mainWindow!.show()
  })
}

/**
 * =========================================================================================================================
 * 新的窗口
 * =========================================================================================================================
 */
/**
 * article: 新窗口
 * wlIcon: weblogo 图标窗口
 * articleReference: 文章引用网络
 * articleLog: 文章编辑记录
 */
type WindowType = 'article' | 'wlIcon' | 'articleReference' | 'articleLog'
const newWindowMaps = new Map<string, BrowserWindow | undefined>()

/**
 * 创建新窗口
 * @param windowType 窗口类型
 * @param title      窗口标题, 窗口类型_窗口标题会保存在 newWindowMaps 中保存唯一
 * @param id         id
 * @returns
 */
function createNewWindow(windowType: WindowType, title: string, id?: number) {
  console.log('打开新窗口, 窗口标题:', title)
  let newWindow: BrowserWindow | undefined = newWindowMaps.get(windowType + '_' + title)
  if (newWindow != undefined) {
    newWindow.show()
    return
  }
  let path: string = ''
  newWindow = buildWindow(title)

  if (windowType === 'article') {
    path = '/articleViewWindow?articleId=' + id
  } else if (windowType === 'wlIcon') {
    path = '/iconListIndexWindow'
  } else if (windowType === 'articleReference') {
    path = '/articleReferenceWindow?articleId=' + id
  } else if (windowType === 'articleLog') {
    path = '/articleHistory?articleId=' + id
  }

  if (is.dev && process.env['ELECTRON_RENDERER_URL']) {
    newWindow.loadURL(process.env['ELECTRON_RENDERER_URL'] + '/#' + path)
  } else {
    newWindow.loadFile(join(__dirname, '../renderer/index.html'), { hash: path })
  }

  // 开发环境自动打开控制台
  openDevToos(newWindow)
  initOnWindow(newWindow)
  newWindow.on('closed', () => {
    console.log('关闭新窗口, 窗口标题:', title)
    newWindowMaps.delete(windowType + '_' + title)
  })
  newWindowMaps.set(windowType + '_' + title, newWindow)
}

// In this file you can include the rest of your app"s specific main process
// code. You can also put them in separate files and require them here.
/**
 * 打开开发者工具
 * @param win 窗口
 */
const openDevToos = (win: BrowserWindow) => {
  if (is.dev) {
    win.webContents.openDevTools()
  } else {
    // win.webContents.openDevTools({ mode: 'right' })
  }
}

/**
 * =========================================================================================================================
 * 主进程监听的事件 ipcMain
 * =========================================================================================================================
 */
const initOnFocusedWindow = (): void => {
  console.log('2. 监听焦点窗口事件')
  /**
   * 窗口最小化
   */
  ipcMain.on('window-min', () => {
    BrowserWindow.getFocusedWindow()?.minimize()
  })
  /**
   * 窗口最大化, 或变为原窗口大小
   */
  ipcMain.on('window-max', () => {
    if (BrowserWindow.getFocusedWindow()?.isMaximized()) {
      BrowserWindow.getFocusedWindow()?.unmaximize()
    } else {
      BrowserWindow.getFocusedWindow()?.maximize()
    }
  })
  /**
   * 隐藏窗口
   */
  ipcMain.on('window-hide', () => {
    if (BrowserWindow.getFocusedWindow()?.id != mainWindow!.id) {
      BrowserWindow.getFocusedWindow()?.close()
    } else {
      BrowserWindow.getFocusedWindow()?.hide()
    }
  })
  /**
   * 关闭窗口
   */
  ipcMain.on('window-close', () => {
    BrowserWindow.getFocusedWindow()?.close()
  })
  /**
   * 打开控制台
   */
  ipcMain.on('openDevTools', () => {
    if (BrowserWindow.getFocusedWindow()?.webContents.isDevToolsOpened()) {
      BrowserWindow.getFocusedWindow()?.webContents.closeDevTools()
    } else {
      BrowserWindow.getFocusedWindow()?.webContents.openDevTools({ mode: 'right' })
    }
  })
  /**
   * 将窗口大小设置为最佳显示效果
   */
  ipcMain.on('set-best-size', () => {
    BrowserWindow.getFocusedWindow()?.setSize(1905, 1022)
  })
  /**
   * 设置窗口缩放
   * @param level: 变更的缩放等级, 在当前缩放上加减
   */
  ipcMain.on('set-zoom-level', (_event: IpcMainEvent, level: number) => {
    mainWindow?.webContents.setZoomLevel(mainWindow!.webContents.getZoomLevel() + level)
  })
  /**
   * 重置窗口缩放
   */
  ipcMain.on('reset-zoom-level', (_event: IpcMainEvent) => {
    mainWindow?.webContents.setZoomLevel(0)
  })
}

/**
 * =========================================================================================================================
 * 主窗口监听的事件 mainWindow
 * =========================================================================================================================
 */
const initOnMainWindow = (mainWindow: BrowserWindow): void => {
  console.log('3. 监听主窗口事件')
  initOnWindow(mainWindow)
  /**
   * 窗口准备好后, 立即显示
   */
  mainWindow.on('ready-to-show', () => {
    mainWindow.show()
  })
  /**
   * 下载URL, 使用 mainWin.webContents.downloadURL(url) 触发
   * @param event
   * @param item
   * @param webContents
   */
  mainWindow.webContents.session.on('will-download', (_event, item, _webContents) => {
    console.log('准备下载文件')
    // 无参数时弹出文件选择框
    item.setSavePath('')
    item.on('updated', (_event, state) => {
      if (state === 'interrupted') {
        console.log('Download is interrupted but can be resumed')
      } else if (state === 'progressing') {
        if (item.isPaused()) {
          console.log('Download is paused')
        } else {
          console.log(`Received bytes: ${item.getReceivedBytes()}`)
        }
      }
    })
    item.once('done', (_event, state) => {
      if (state === 'completed') {
        console.log('Download successfully')
      } else {
        console.log(`Download failed: ${state}`)
      }
    })
  })
  /*
   * =========================================================================================================================
   * 主进程监听事件
   * =========================================================================================================================
   */
  /**
   * 截屏
   */
  ipcMain.on('printScreen', () => {
    printScreen(mainWindow)
  })
  /**
   * 登录后设置用户信息
   */
  ipcMain.on('set-userinfo', (_: IpcMainEvent, userinfo: any): void => {
    blossomUserinfo = userinfo
    console.log('当前登录用户:', userinfo)
    if (platform.isWindows) {
      tray.setToolTip(`Blossom\n用户: ${userinfo.username}\n昵称: ${userinfo.nickName}`)
    }
  })
  /**
   * 新文章窗口
   */
  ipcMain.on('open-new-article-window', (_: IpcMainEvent, article: any): void => {
    createNewWindow('article', article.name, article.id)
  })
  /**
   * 图标窗口
   */
  ipcMain.on('open-new-icon-window', (_: IpcMainEvent): void => {
    createNewWindow('wlIcon', '图标')
  })
  /**
   * 文章引用网络
   */
  ipcMain.on('open-new-article-referece-window', (_: IpcMainEvent, article?: any): void => {
    let name = '文章引用网络'
    let id: number | undefined
    if (article) {
      name = article.name + '引用网络'
      id = article.id
    }
    createNewWindow('articleReference', name, id)
  })
  /**
   * 文章编辑记录
   */
  ipcMain.on('open-new-article-log-window', (_: IpcMainEvent, article: any): void => {
    createNewWindow('articleLog', article.name + '编辑记录', article.id)
  })
  /**
   * 下载, 最终会调用
   * mainWin.webContents.session.on('will-download', (_event, item, _webContents) => {}
   */
  ipcMain.on('download', (_event, url: string) => {
    mainWindow.webContents.downloadURL(url)
  })
}

/**
 * =========================================================================================================================
 * 非主窗口的事件
 * =========================================================================================================================
 */
export const initOnWindow = (window: BrowserWindow) => {
  console.log('4. 监听非主窗口事件')
  /**
   * 拦截页面链接
   */
  // window.webContents.on('will-navigate', (event: Event, url: string) => {
  //   interceptorATag(event, url)
  // })

  /**
   * 打开链接, 如果打开链接于服务器域名相同, 则在新窗口中打开
   */
  window.webContents.setWindowOpenHandler((details: HandlerDetails): any => {
    let url = details.url as string
    if (blossomUserinfo && url.startsWith(blossomUserinfo.userParams.WEB_ARTICLE_URL)) {
      let articleId: string = url.replaceAll(blossomUserinfo.userParams.WEB_ARTICLE_URL, '')
      createNewWindow('article', articleId, Number(articleId))
    } else {
      shell.openExternal(url)
    }
    // return { action: "deny" }
  })
}

// /**
//  * 拦截 a 标签
//  * @param e
//  * @param url
//  */
// const interceptorATag = (e: Event, url: string): boolean => {
//   e.preventDefault()
//   let innerUrl = url
//   if (blossomUserinfo && innerUrl.startsWith(blossomUserinfo.userParams.WEB_ARTICLE_URL)) {
//     let articleId: string = innerUrl.replaceAll(blossomUserinfo.userParams.WEB_ARTICLE_URL, '')
//     createNewWindow('article', articleId, Number(articleId))
//   } else if (!is.dev) {
//     shell.openExternal(url)
//   }
//   return true
// }
