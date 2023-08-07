## 前言
聊天软件需要截图功能。我们找到了一个方法，QQ dll，包装一下生成 exe 文件，我用 Node.js 去调用完成截图。

1. 第一步先用 Node 执行微信封装的 exe，然后会把截图复制到剪切板
2. 然后调用浏览把剪切板的内容复制出来
```
    var screen_window = execFile(__dirname + '/screen/PrintScr.exe')
    screen_window.on('exit', function (code) {
      // 执行成功返回 1，返回 0 没有截图
      if (code) mainWindow.webContents.paste()
    })
```
