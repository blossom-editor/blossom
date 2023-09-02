import { isBlank, isNotBlank } from '@renderer/assets/utils/obj'
import { escape2Html } from '@renderer/assets/utils/util'
import { marked, Marked } from 'marked'
import { markedHighlight } from "marked-highlight"
import hljs from 'highlight.js'
import katex from 'katex'
import 'katex/dist/katex.min.css'
import mermaid from 'mermaid'
import { ArticleReference, getDocInfoFromTrees } from './article'
// import 'highlight.js/styles/atom-one-light.css';
// import 'highlight.js/styles/base16/darcula.css';


mermaid.initialize({
  theme: 'base',
  startOnLoad: false,
  securityLevel: 'loose',
  'themeVariables': {
    'fontFamily': 'inherit',
    // 主要配色
    'primaryColor': '#cfbef1',
    'primaryTextColor': '#606266',
    'primaryBorderColor': '#8143FF',
    // 第二颜色
    'secondaryColor': '#efc75e',
    'secondaryTextColor': '#606266',
    // 第三颜色
    'tertiaryColor': '#C4DFFF',
    'tertiaryTextColor': '#606266',
    // 连线的颜色
    'lineColor': '#A0A0A0',
  }
});
mermaid.parseError = (_err, _hash) => {
}

/**
 * 标记标识
 */
export const grammar = '##'
/** 匹配一个 $ 符, 来自于 markedjs 的官方例子 */
export const singleDollar = /^\$+([^\$\n]+?)\$+/
export const doubleDollar = /(?<=\$\$).*?(?=\$\$)/
export const doubleWell = /(?<=\#\#).*?(?=\#\#)/

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
    const language = hljs.getLanguage(lang) ? lang : 'shell'
    return hljs.highlight(code, { language }).value
  }
}))

//

//#region ----------------------------------------< tokenizer >--------------------------------------
export const tokenizerCodespan = (src: string): any => {
  const match = src.match(singleDollar)
  if (match) {
    let result = {
      type: 'codespan',
      raw: match[0],
      text: match[0]
    }
    return result
  }
  return false
}

//#endregion

//#region ----------------------------------------< renderer >--------------------------------------


/**
 * 标题解析为 TOC 集合, 增加锚点跳转
 * @param text  标题内容
 * @param level 标题级别
 */
export const renderHeading = (text: any, level: number) => {
  const realLevel = level
  return `<h${realLevel} id="${realLevel}-${text}">${text}</h${realLevel}>`
}


/**
 * 表格 header/body
 */
export const renderTable = (header: string, body: string) => {
  let arr = header.match(doubleWell)
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
    let target = `<p>${grammar}${color}${grammar}`
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
 *    格式为: ```bilibili${grammar}bvid${grammar}w100${grammar}h100
 *    官方使用文档: https://player.bilibili.com/
 * 
 * 2. katex 
 * 3. mermaid
 * 
 * @param code      解析后的 HTML 代码
 * @param language  语言
 * @param isEscaped 
 * @param mermaidCallback 替换 html 结果中的 mermaid 内容
 */
export const renderCode = (code: string, language: string | undefined, _isEscaped: boolean, mermaidCallback?: (eleid: string, svg: string) => void) => {
  if (language == undefined) {
    language = 'text'
  }
  if (language === 'mermaid' && isNotBlank(code)) {
    const eleid = 'mermaid-' + Date.now() + '-' + Math.round(Math.random() * 1000)
    const escape = escape2Html(code) as string
    mermaid.parse(escape).then(syntax => {
      let canSyntax: boolean | void = syntax
      if (canSyntax) {
        mermaid.render(eleid + '-svg', escape).then((resp) => {
          if (mermaidCallback != undefined) {
            const { svg } = resp
            let element = document.getElementById(eleid)
            element!.innerHTML = svg
            mermaidCallback(eleid, svg)
          }
        })
      }
    }).catch(error => {
      console.error('mermaid 格式校验失败:错误信息如下:\n', error)
      let html = `<div class='bl-preview-analysis-fail-block'>
          <div class="fail-title">Mermaid 语法解析失败!</div><br/>
          ${error}<br/><br/>
          你可以尝试前往 Mermaid 官网来校验你的内容, 或者查看相关文档: <a href='https://mermaid.live/edit' target='_blank'>https://mermaid.live/edit</a>
          </div>`
      if (mermaidCallback != undefined) {
        let element = document.getElementById(eleid)
        element!.innerHTML = html
        mermaidCallback(eleid, html)
      }
    })
    return `<p id="${eleid}">${eleid}</p>`
  }

  if (language === 'katex') {
    try {
      return katex.renderToString(escape2Html(code), {
        throwOnError: true,
        displayMode: true,
        output: 'html'
      })
    } catch (error) {
      console.error(error)
      return `<div class='bl-preview-analysis-fail-block'>
          <div class="fail-title">Katex 语法解析失败!</div><br/>
          ${error}<br/><br/>
          你可以尝试前往 Katex 官网来校验你的公式, 或者查看相关文档: <a href='https://katex.org/#demo' target='_blank'>https://katex.org/#demo</a>
          </div>`
    }
  }

  if (language.startsWith('bilibili')) {
    let bvid = ''
    let width = '100%'
    let height = '300px'
    let tags: string[] = language.split(grammar)
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
      return `<div class='bl-preview-analysis-fail-block'>
      <span style="color:#00aeec">bilibili</span> 视频解析失败, 未获取到 <span style="color:#00aeec">BVID</span>，请检查你的配置
      </div>`
    }

    return `<iframe width="${width}" height="${height}" style="margin: 10px 0"
      scrolling="no" border="0" frameborder="no" framespacing="0"
      src="https://player.bilibili.com/player.html?bvid=${bvid}&page=1&autoplay=0" ></iframe>`
  }

  return `<pre><code class="hljs language-${language}">${code}</code></pre>`
}

/**
 * 单行代码块的解析拓展
 * 1. katex `$内部写表达式$`
 * @param src 
 * @returns 
 */
export const renderCodespan = (src: string) => {
  let arr = src.match(singleDollar)
  if (arr != null && arr.length > 0) {
    try {
      return katex.renderToString(arr[1], {
        throwOnError: true,
        output: 'html'
      })
    } catch (error) {
      console.error(error)
      return `<div class='bl-preview-analysis-fail-inline'>
          Katex 语法解析失败! 你可以尝试前往<a href='https://katex.org/#demo' target='_blank'> Katex 官网</a> 来校验你的公式。
          </div>`
    }
  }
  return `<code>${src}</code>`
}

/**
   * 拓展图片设置
   * ![照片A${grammar}shadow${grammar}w100]()
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
  let width = 'auto'
  let style = ''
  let tags: string[] = text.split(grammar)
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
        }
      }
    }
  }
  return `<img width="${width}" style="${style}" src="${href}" alt="${text}">`
}

/**
 * 解析链接, 拓展双链功能
 * 
 * @param href 链接地址
 * @param title 链接标题 <a title="title">, 语法拓展内容在title中
 * @param text 链接的文字
 * @param docTrees 文档树状对象, 用于获取双链笔记的内容详情
 * @returns {
 *  link: link 标签
 *  ref: 双链内容
 * }
 */
export const renderLink = (href: string | null, title: string | null, text: string, docTrees: DocTree[]) => {
  let link: string
  let ref: ArticleReference = { targetId: 0, targetName: text, targetUrl: href as string, type: 21 }
  if (isBlank(title)) {
    link = `<a target="_blank" href=${href} target="_blank">${text}</a>`
  } else {
    let arr = title!.match(/(?<=\#\#).*?(?=\#\#)/)
    let isInnerArticle: boolean = arr != null && arr.length > 0 && !isBlank(arr[0])
    if (isInnerArticle) {
      let articleId = Number(arr![0])
      // 如果ID不是数字
      if (isNaN(articleId)) {
        link = `<a target="_blank" href=${href} title=${title}>${text}</a>`
      }

      // 从文章列表中获取文章, 如果找到则认为是内部引用, 否则即使是内部引用格式, 也认为是个外部文章.
      let article = getDocInfoFromTrees(articleId, docTrees)
      if (article != undefined) {
        ref.targetId = article.i
        ref.targetName = article.n
        ref.type = 11
      }

      link = `<a target="_blank" href=${href} class="inner-link bl-tip bl-tip-bottom" data-tip="双链引用: 《${text}》">${text}</a>`
    } else {
      link = `<a target="_blank" href=${href} title=${title} >${text}</a>`
    }
  }
  return { link: link, ref: ref }
}

//#endregion

//#region ----------------------------------------< simpleMarked >--------------------------------------
const simpleMarked = new Marked({ mangle: false, headerIds: false })
simpleMarked.use(markedHighlight({
  langPrefix: 'hljs language-',
  highlight(code, lang) {
    const language = hljs.getLanguage(lang) ? lang : 'shell'
    return hljs.highlight(code, { language }).value
  }
}))

const simpleRenderer = {
  code(code: string, language: string | undefined, _isEscaped: boolean): string {
    if (language === 'katex') {
      try {
        return katex.renderToString(escape2Html(code), {
          throwOnError: true,
          displayMode: true,
          output: 'html'
        })
      } catch (error) {
        return `<div></div>`
      }
    }
    return `<pre><code class="hljs language-${language}">${code}</code></pre>`
  },
  codespan(src: string): string { return renderCodespan(src) },
}

const tokenizer = {
  codespan(src: string): any { return tokenizerCodespan(src) }
}

//@ts-ignore
simpleMarked.use({ tokenizer: tokenizer, renderer: simpleRenderer })

//#endregion

export default marked

export {
  simpleMarked
}