/**
 * 监听 html 的内联事件
 */
export type ArticleHtmlEvent =
  | 'copyPreCode' // 代码复制
  | 'showArticleReferenceView' // 显示引用文章内容

/**
 * 处理内联事件
 *
 * blog 只有代码复制(copyPreCode)事件
 *
 * @param _t
 * @param _ty
 * @param _event
 * @param type
 * @param data
 * @returns
 */
export const onHtmlEventDispatch = (_t: any, _ty: any, _event: any, type: ArticleHtmlEvent, data: any) => {
  if (type === 'copyPreCode') {
    let code = document.getElementById(data)
    if (code) {
      // navigator clipboard 需要https等安全上下文
      if (navigator.clipboard && window.isSecureContext) {
        // navigator clipboard 向剪贴板写文本
        return navigator.clipboard.writeText(code.innerText)
      } else {
        // 创建text area
        let textArea = document.createElement('textarea')
        textArea.value = code.innerText
        // 使text area不在viewport，同时设置不可见
        textArea.style.position = 'absolute'
        textArea.style.opacity = '0'
        textArea.style.left = '-999999px'
        textArea.style.top = '-999999px'
        document.body.appendChild(textArea)
        textArea.focus()
        textArea.select()
        return new Promise<void>((res, rej) => {
          // 执行复制命令并移除文本框
          document.execCommand('copy') ? res() : rej()
          textArea.remove()
        })
      }
    }
  }
}
