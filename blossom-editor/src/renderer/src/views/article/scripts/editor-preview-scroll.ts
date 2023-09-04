import { CmWrapper } from "./codemirror"
import { simpleMarked } from "./markedjs"

const marginTop = 75
const matchHtmlTags = 'p, h1, h2, h3, h4, h5, h6, ul, ol, li, pre, blockquote, hr, table, tr, iframe, span'

/**
 * 双屏滚动封装
 */
export class EPScroll {

  private _editor: HTMLElement
  private _preview: HTMLElement
  private _cmw: CmWrapper | undefined

  constructor(editor: HTMLElement, previre: HTMLElement, cmw: CmWrapper | undefined) {
    this._editor = editor
    this._preview = previre
    this._cmw = cmw
  }

  public sycnScroll(_event: Event | string, _source?: string, _lineno?: number, _colno?: number, _error?: Error): any {
    if (this._editor == undefined) {
      return
    }
    // console.log(this._editor?.scrollHeight,
    //   this._editor?.clientHeight,
    //   this._editor?.scrollTop)

    // 如果在头部附近
    if (this._editor?.scrollTop < 20) {
      (this._preview.firstChild as HTMLElement).scrollIntoView()
    }
    // 如果在尾部附近
    else if (this._editor?.clientHeight + this._editor?.scrollTop > this._editor?.scrollHeight - 20) {
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
      //@ts-ignore
      simpleMarked!.parse(invisibleMarkdown, { async: true }).then((html: string) => {
        const invisibleHtml = html
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
        const tagetDom: Element = previewDoms[targetIndex]
        tagetDom.scrollIntoView({ behavior: "smooth", block: "start", inline: "nearest" })
      })
    }
  }
}