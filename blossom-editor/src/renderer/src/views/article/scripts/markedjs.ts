import { useUserStore } from '@renderer/stores/user'
import { useConfigStore } from '@renderer/stores/config'
import { isBlank, isNotBlank } from '@renderer/assets/utils/obj'
import { escape2Html, randomInt, sleep } from '@renderer/assets/utils/util'
import { marked, Marked } from 'marked'
import markedKatex from 'marked-katex-extension'
// highlight
import { markedHighlight } from 'marked-highlight'
import hljs from 'highlight.js'
// katex
import katex, { KatexOptions } from 'katex'
import 'katex/dist/katex.min.css'
// mermaid
import mermaid from 'mermaid'
// markmap
import { Transformer } from 'markmap-lib'
import { Markmap, deriveOptions } from 'markmap-view'
import { ArticleReference } from './article'
import { picCacheWrapper } from '@renderer/views/picture/scripts/picture'
import { getDocById } from '@renderer/views/doc/doc'
// import 'highlight.js/styles/atom-one-light.css';
// import 'highlight.js/styles/base16/darcula.css';

const userStore = useUserStore()
const { editorStyle } = useConfigStore()

//#region ----------------------------------------< 组件配置 >--------------------------------------

/**
 * markmap 配置
 */
const transformer = new Transformer()
const markmapOptions = deriveOptions({
  colorFreezeLevel: 2, // 颜色冻结级别
  duration: 0, // 展开缩起动画
  maxWidth: 160, // 每个节点最大宽度
  zoom: true, // 缩放
  pan: true // 拖动
})

/**
 * mermaid 配置
 */
mermaid.initialize({
  startOnLoad: false,
  securityLevel: 'loose'
})
mermaid.parseError = (_err, _hash) => {}

/**
 * 标记标识
 */
export const grammar = '##'
/** 匹配一个 $ 符, 来自于 markedjs 的官方例子 */
export const singleDollar = /^\$+([^\$\n]+?)\$+/
export const doubleDollar = /(?<=\$\$).*?(?=\$\$)/
export const doubleWell = /(?<=\#\#).*?(?=\#\#)/

const katexOptions: KatexOptions = {
  throwOnError: false,
  displayMode: true,
  // 生成 katex-mathml 时会出现错误, mathml 绝对定位没有定到 katex-display 元素, 而是找到上级导致页面出现错误
  output: 'html'
}

/**
 * markedjs 配置
 */
marked.use(
  {
    async: true,
    pedantic: false,
    gfm: true,
    mangle: false,
    headerIds: false
  },
  markedKatex(katexOptions)
)

let hljsConfig = {
  langPrefix: 'hljs language-',
  highlight(code: string, lang: string) {
    if (lang === 'katex' || lang === 'mermaid' || lang.startsWith('markmap') || lang.startsWith('bilibili')) return code
    const language = hljs.getLanguage(lang) ? lang : 'shell'
    return hljs.highlight(code, { language }).value
  }
}
marked.use(markedHighlight(hljsConfig))
//#endregion

//#region ----------------------------------------< renderer >--------------------------------------
const domParser = new DOMParser()
/**
 * 标题解析为 TOC 集合, 增加锚点跳转
 * @param text  标题内容
 * @param level 标题级别
 * @param raw   原内容
 */
export const renderHeading = (text: string, level: number, raw: string) => {
  let id: string = randomInt(1000000, 9999999).toString()
  try {
    if (raw.indexOf('<') > -1 && raw.indexOf('>') > -1) {
      let dom = domParser.parseFromString(raw, 'text/html')
      if (dom) {
        id += dom.body.innerText
      } else {
        id += raw
      }
    } else {
      id += raw
    }
  } catch {
    id += raw
  }
  return `<h${level} id="${id}">${text}</h${level}>`
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

  /**
   * 支持了新的语义化引用
   *
   * https://github.com/orgs/community/discussions/16925#discussioncomment-3192118
   * https://learn.microsoft.com/en-us/contribute/content/markdown-reference#alerts-note-tip-important-caution-warning
   */
  if (quote.startsWith('<p>[!NOTE]')) {
    clazz = 'bl-blockquote-blue'
    finalQuote = quote.replaceAll('<p>[!NOTE]', '<p>')
  } else if (quote.startsWith('<p>[!TIP]')) {
    clazz = 'bl-blockquote-blue'
    finalQuote = quote.replaceAll('<p>[!TIP]', '<p>')
  } else if (quote.startsWith('<p>[!IMPORTANT]')) {
    clazz = 'bl-blockquote-purple'
    finalQuote = quote.replaceAll('<p>[!IMPORTANT]', '<p>')
  } else if (quote.startsWith('<p>[!WARNING]')) {
    clazz = 'bl-blockquote-yellow'
    finalQuote = quote.replaceAll('<p>[!WARNING]', '<p>')
  } else if (quote.startsWith('<p>[!CAUTION]')) {
    clazz = 'bl-blockquote-red'
    finalQuote = quote.replaceAll('<p>[!CAUTION]', '<p>')
  }
  return `<blockquote class="${clazz}">${finalQuote}</blockquote>`
}

/**
 * 自定义代码块内容解析:
 * 1. mermaid  (async)
 * 2. katex
 * 3. markmap  (async)
 * 4. bilibili (iframe), 文档: https://player.bilibili.com/
 *
 * @param code      解析后的 HTML 代码
 * @param language  语言
 * @param isEscaped
 */
export const renderCode = (code: string, language: string | undefined, _isEscaped: boolean, asyncStat: { need: number; done: number }) => {
  if (language == undefined) language = 'text'

  /** ==========================================================================================
   * 渲染 mermaid
   * ```mermaid${grammar}h300
   * ========================================================================================== */
  if (language.startsWith('mermaid') && isNotBlank(code)) {
    asyncStat.need++
    const eleid = 'mermaid-' + Date.now() + '-' + randomInt(1, 10000)
    const escape = escape2Html(code) as string

    let height = 'auto'
    let tags: string[] = language.split(grammar)
    if (tags.length >= 2) {
      let tag = tags[1]
      if (tag.startsWith('h')) {
        height = tag.substring(1)
        if (!height.endsWith('%')) {
          height += 'px'
        }
      }
    }

    mermaid
      .parse(escape)
      .then((syntax) => {
        let canSyntax: boolean | void = syntax
        if (canSyntax) {
          mermaid.render(eleid + '-svg', escape).then(async (resp) => {
            const { svg } = resp
            let element = document.getElementById(eleid)
            let retry = 0
            while (!element || element == null) {
              if (retry > 30) break
              await sleep(5)
              element = document.querySelector(`#${eleid}`)
              retry++
            }
            if (element) {
              element.innerHTML = svg
            }
            asyncStat.done++
          })
        }
      })
      .catch((error) => {
        console.error('mermaid 格式校验失败:错误信息如下:\n', error)
        let html = `<p class='bl-preview-analysis-fail-block'>
          <div class="fail-title">Mermaid 语法解析失败!</div><br/>
          ${error}<br/><br/>
          你可以尝试前往 Mermaid 官网来校验你的内容, 或者查看相关文档: <a href='https://mermaid.live/edit' target='_blank'>https://mermaid.live/edit</a>
          </p>`
        let element = document.getElementById(eleid)
        if (element) element!.innerHTML = html
        asyncStat.done++
      })
    return `<p class="mermaid-container" style="height:${height}" id="${eleid}"></p>`
  }

  /**
   * 渲染 katex
   */
  if (language === 'katex') {
    try {
      return katex.renderToString(escape2Html(code), { throwOnError: true, displayMode: true, output: 'html' })
    } catch (error) {
      return `<p class='bl-preview-analysis-fail-block'>
          <div class="fail-title">Katex 语法解析失败!</div><br/>
          ${error}<br/><br/>
          你可以尝试前往 Katex 官网来校验你的公式, 或者查看相关文档: <a href='https://katex.org/#demo' target='_blank'>https://katex.org/#demo</a>
          </p>`
    }
  }

  /** ==========================================================================================
   * 渲染 markmap
   * ```markmap${grammar}h300
   * ========================================================================================== */
  if (language.startsWith('markmap')) {
    asyncStat.need++
    let height = '300px'
    let tags: string[] = language.split(grammar)
    if (tags.length >= 2) {
      let tag = tags[1]
      if (tag.startsWith('h')) {
        height = tag.substring(1)
        if (!height.endsWith('%')) {
          height += 'px'
        }
      }
    }

    const eleid = 'markmap-' + Date.now() + '-' + randomInt(1, 10000)
    const escape = escape2Html(code) as string
    const { root } = transformer.transform(escape)
    new Promise<SVGElement>(async (_resolve, _reject) => {
      let svg: SVGElement | null = document.querySelector(`#${eleid}`)
      let retry = 0
      while (!svg || svg == null) {
        if (retry > 30) break
        await sleep(5)
        svg = document.querySelector(`#${eleid}`)
        retry++
      }
      if (svg) {
        Markmap.create(svg, markmapOptions, root)
      }
      asyncStat.done++
    })
    return `<p class="markmap-container"><svg id=${eleid} xmlns="http://www.w3.org/2000/svg" style="width:100%;height:${height}"></svg></p>`
  }

  /** ==========================================================================================
   * 渲染 bilibili
   * ```bilibili${grammar}bvid${grammar}w100${grammar}h100
   * ========================================================================================== */
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
              height += 'px'
            }
          }
        }
      }
    }

    if (isBlank(bvid)) {
      return `<p class='bl-preview-analysis-fail-block'>
      <span style="color:#00aeec">bilibili</span> 视频解析失败, 未获取到 <span style="color:#00aeec">BVID</span>，请检查你的配置
      </p>`
    }

    return `<iframe width="${width}" height="${height}" style="margin: 10px 0"
      scrolling="no" border="0" frameborder="no" framespacing="0" loading="lazy" 
      src="https://player.bilibili.com/player.html?bvid=${bvid}&page=1&autoplay=0" ></iframe>`
  }

  const id = 'pre-' + Date.now() + '-' + randomInt(1, 1000000)
  const lines: string[] = code.split(/\n|\r\n?|\n\n+/g)
  let result = '<ol>'
  for (let i = 0; i < lines.length; i++) {
    if (editorStyle.isShowPreLineNumber) {
      result += `<li><span class="line-num">${i + 1}</span></li>`
    } else {
      result += `<li><span class="line-num"></span></li>`
    }
  }
  let lineNumbers = result + '</ol>'
  return `<pre><code id="${id}" class="hljs language-${language}">${code}</code>${lineNumbers}<div class="pre-copy" onclick="onHtmlEventDispatch(this,'click',event,'copyPreCode','${id}')">${language}</div></pre>`
}

/**
 * 单行代码块的解析拓展
 * @param src
 * @returns
 */
export const renderCodespan = (src: string) => {
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
export const renderImage = (href: string | null, title: string | null, text: string) => {
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
  // 为图片增加缓存标识
  return `<img width="${width}" style="${style}" src="${picCacheWrapper(href as string)}" alt="${title}">`
  // return `<img width="${width}" style="${style}" src="${href}" alt="${title}">`
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
export const renderLink = (
  href: string,
  title: string | null | undefined,
  text: string,
  docTrees: DocTree[]
): { link: string; ref: ArticleReference } => {
  let link: string
  let ref: ArticleReference = { targetId: '0', targetName: text, targetUrl: href as string, type: 21 }
  if (isBlank(title)) {
    link = `<a target="_blank" href=${href} target="_blank">${text}</a>`
  } else if (!href.startsWith(userStore.userParams.WEB_ARTICLE_URL)) {
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
      // 内部引用不会使用 Markdown 中的链接名, 而是用内部文章名
      let article = getDocById(articleId.toString(), docTrees)
      if (article != undefined) {
        ref.targetId = article.i
        ref.targetName = article.n
        ref.type = 11
      } else {
        ref.targetId = articleId.toString()
        ref.targetName = '未知文章-' + articleId.toString()
        ref.type = 12
      }

      link = `<a target="_blank" href=${href} class="inner-link"
      onclick="onHtmlEventDispatch(this,'',event,'showArticleReferenceView','${ref.targetId}')">${text}</a>`
    } else {
      link = `<a target="_blank" href=${href} title=${title} >${text}</a>`
    }
  }
  return { link: link, ref: ref }
}

//#endregion

//#region ----------------------------------------< simpleMarked >--------------------------------------
const simpleMarked = new Marked({ mangle: false, headerIds: false }, markedKatex(katexOptions))
simpleMarked.use(markedHighlight(hljsConfig))

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
        return `<p></p>`
      }
    } else if (language === 'mermaid') {
      return '<p></p>'
    } else if (language?.startsWith('markmap')) {
      return `<p><svg></svg></p>`
    }
    const lines: string[] = code.split(/\n|\r\n?|\n\n+/g)
    let result = '<ol>'
    for (let i = 0; i < lines.length; i++) {
      result += `<li></li>`
    }
    let lineNumbers = result + '</ol>'
    return `<pre><code class="hljs language-${language}"></code>${lineNumbers}<div class="pre-copy">${language}</div></pre>`
  },
  codespan(src: string): string {
    return renderCodespan(src)
  }
}

//@ts-ignore
simpleMarked.use({ renderer: simpleRenderer })

//#endregion

export default marked

export { simpleMarked }
