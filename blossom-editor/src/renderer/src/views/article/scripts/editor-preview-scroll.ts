import { CmWrapper } from './codemirror'
import { simpleMarked } from './markedjs'

const marginTop = 85
/**
 * 不能增加 div 标签, 只能是 preview 元素下的一级标签, 而 div 会出现在 markmap 的内部节点中
 */
const matchHtmlTags = 'p, h1, h2, h3, h4, h5, h6, ul, ol, li, blockquote, hr, table, tr, iframe'

/**
 * 双屏滚动封装
 */
export class EPScroll {
  /**
   * 编辑器
   */
  private _editor: HTMLElement
  /**
   * 预览
   */
  private _preview: HTMLElement
  /**
   * codemirror 封装
   */
  private _cmw: CmWrapper | undefined
  /**
   * 保存编辑器滚动条的最后位置
   */
  private _scrollTop: number = 0
  /**
   * 是否启用同步刷新
   */
  private _open: boolean = true

  constructor(editor: HTMLElement, previre: HTMLElement, cmw: CmWrapper | undefined) {
    this._editor = editor
    this._preview = previre
    this._cmw = cmw
    this._scrollTop = 0
  }

  /**
   * 返回滚动条位置
   */
  public get scrollTop() {
    return this._scrollTop
  }

  /**
   * 重置滚动条
   */
  public scrollTopReset() {
    this._scrollTop = 0
    this.toTop()
  }

  /**
   * 滚动到最近一次
   */
  public scrollTopLast() {
    this._editor.scrollTo({ top: this._scrollTop })
  }

  /**
   * 编辑器滚动条置顶
   */
  public toTop() {
    this._editor.scrollTo({ top: 0 })
  }

  /**
   * 编辑器滚动条置底
   */
  public toBottom() {
    this._editor.scrollTo({ top: 999999999 })
  }

  /**
   * 切换同步滚动开启/关闭
   * @returns 是否同步滚动
   */
  public open(): boolean {
    this._open = !this._open
    return this._open
  }

  public sycnScroll(_event: Event | string, _source?: string, _lineno?: number, _colno?: number, _error?: Error): void {
    if (this._editor == undefined) {
      return
    }
    // console.log(this._editor?.scrollHeight, this._editor?.clientHeight, this._editor?.scrollTop)

    this._scrollTop = this._editor.scrollTop

    // 如果不开启同步滚动, 则返回
    if (!this._open) {
      return
    }
    // 如果在头部附近
    if (this._editor.scrollTop < 5) {
      this._preview.scrollTo({ top: 0 })
    }
    // 如果在尾部附近
    else if (this._editor.clientHeight + this._editor.scrollTop > this._editor.scrollHeight - 20) {
      this._preview.scrollTop = this._preview.scrollHeight
    }
    // 其他
    else {
      if (!this._cmw) {
        return
      }

      // 文档头部, 距离整个浏览器的距离
      const top = this._cmw.getDocumentTop()
      // 获取可见位置最顶部的内容
      const topBlock = this._cmw.getLineBlockAtHeight(Math.abs(top) + marginTop)
      // 从0开始获取全部不可见的内容的 markdown 原文档
      const invisibleMarkdown: string = this._cmw.sliceDoc(0, topBlock.from)

      // 将不可见的内容全部转换为 html
      // @ts-ignore
      simpleMarked.parse(invisibleMarkdown, { async: true }).then((invisibleHtml: string) => {
        // let logLine = { markdown: invisibleMarkdown, html: html }
        // console.table([logLine])

        // 将不可见的的 html 转换为 dom 对象, 是一个从 <html> 标签开始的 dom 对象
        const invisibleDomAll = new DOMParser().parseFromString(invisibleHtml, 'text/html')

        // body 下的内容才是由 markdown 转换而来的, 不可见内容转换的 dom 集合
        const editorDoms = invisibleDomAll.body.querySelectorAll(matchHtmlTags)
        // 预览页面的 dom 集合
        const previewDoms: NodeListOf<Element> = this._preview.querySelectorAll(matchHtmlTags)
        let targetIndex: number = editorDoms.length
        // 预览页面的 dom 数小于 markdown 转换的 dom 数, 处理数组边界
        if (targetIndex > previewDoms.length) {
          targetIndex = previewDoms.length
        }
        const targetDom: Element = previewDoms[targetIndex]
        if (targetDom) {
          targetDom.scrollIntoView()
        }
      })
    }
  }
}
