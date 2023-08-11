import { isBlank } from '@renderer/assets/utils/obj'
import { marked } from 'marked'
import { markedHighlight } from "marked-highlight"
import hljs from 'highlight.js'
// import 'highlight.js/styles/atom-one-light.css';
// import 'highlight.js/styles/base16/darcula.css';

//#region ----------------------------------------< marked >--------------------------------------
marked.use({
  async: true,
  pedantic: false,
  gfm: true,
  mangle: false,
  headerIds: false
})

// 高亮拓展
marked.use(markedHighlight({
  langPrefix: 'hljs language-',
  highlight(code, lang) {
    const language = hljs.getLanguage(lang) ? lang : 'shell';
    return hljs.highlight(code, { language }).value;
  }
}))


/**
 * 标题解析为 TOC 集合, 增加锚点跳转
 * @param text  标题内容
 * @param level 标题级别
 */
export const renderHeading = (text: any, level: number) => {
  const realLevel = level
  return `<h${realLevel} id="${realLevel}-${text}">${text}</h${realLevel}>`;
}


/**
 * 表格 header/body
 */
export const renderTable = (header: string, body: string) => {
  let arr = header.match(/(?<=\$\$).*?(?=\$\$)/)
  let isContainer: boolean = arr != null && arr[0] === 'container'
  if (isContainer) {
    return `<table class="bl-table-container"><thead>${header}</thead><tbody>${body}</tbody></table>`
  }
  return `<table><thead>${header}</thead><tbody>${body}</tbody></table>`
}

/**
 * 引用拓展, 为引用指定颜色
 * @param quote 引用内部文字的内容
 */
export const renderBlockquote = (quote: string) => {
  let finalQuote = quote
  let clazz = 'bl-blockquote-'
  let colors = ['green', 'yellow', 'red', 'blue', 'purple', 'black']
  for (let i = 0; i < colors.length; i++) {
    let color = colors[i]
    let target = '<p>$$' + color + '$$'
    if (quote.startsWith(target)) {
      clazz = 'bl-blockquote-' + color
      finalQuote = quote.replaceAll(target, '<p>')
      break
    }
  }
  return `<blockquote class="${clazz}">${finalQuote}</blockquote>`
}

/**
 * 自定义代码块内容解析:
 * 1. bilibili
 * 格式为: ```bilibili$$bvid$$w100$$h100
 * 
 * 官方使用文档: https://player.bilibili.com/
 * 
 * @param code      解析后的 HTML 代码
 * @param language  语言
 * @param isEscaped 
 */
export const renderCode = (code: string, language: string | undefined, _isEscaped: boolean) => {
  if (language == undefined) {
    language = 'text'
  }

  if (language.startsWith('bilibili')) {
    let bvid = ''
    let width = '100%'
    let height = '300px'
    let tags: string[] = language.split('$$')
    if (tags.length > 1) {
      if (tags.length >= 2) {
        bvid = tags[1]
      }
      if (tags.length >= 3) {
        for (let i = 2; i < tags.length; i++) {
          let tag = tags[i]
          if (tag.startsWith('w')) {
            width = tags[i].substring(1)
            if (!width.endsWith('%')) {
              width += 'px'
            }
          }
          if (tag.startsWith('h')) {
            height = tags[i].substring(1)
            if (!height.endsWith('%')) {
              width += 'px'
            }
          }
        }
      }
    }

    if (isBlank(bvid)) {
      return `<div style="width:100%;padding:40px;background-color:#000000;color:#ffffff;">未获取到BVID，请检查你的配置</div>`
    }

    return `<iframe width="${width}" height="${height}" style="margin: 10px 0"
      scrolling="no" border="0" frameborder="no" framespacing="0"
      src="https://player.bilibili.com/player.html?bvid=${bvid}&page=1&autoplay=0" ></iframe>`
  }
  return `<pre><code class="hljs language-${language}">${code}</code></pre>`
}

/**
   * 拓展图片设置
   * ![照片A$$shadow$$w100]()
   * 上面格式解析为
   *  - 图片名称为 照片A
   *  - 图片包含阴影
   *  - 图片宽度为100px
   * 
   * @param href   图片路径
   * @param _title null
   * @param text   图片的名称
   */
export const renderImage = (href: string | null, _title: string | null, text: string) => {
  let width = 'auto';
  let style = ''
  let tags: string[] = text.split('$$')
  if (tags.length > 1) {
    for (let i = 0; i < tags.length; i++) {
      let tag = tags[i]
      if (tag === 'shadow') {
        style += 'box-shadow: var(--bl-preview-img-box-shadow);'
      }
      if (tag.startsWith('w')) {
        width = tags[i].substring(1)
        if (!width.endsWith('%')) {
          width += 'px'
          style += `min-width:${width};max-width:${width};`
        }
      }
    }
  }
  return `<p>
      <img width="${width}" style="${style}" src="${href}" alt="${text}">
      </p>`
}

export default marked