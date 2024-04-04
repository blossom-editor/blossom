import { isNull } from '@renderer/assets/utils/obj'
import { randomInt } from '@renderer/assets/utils/util'

/**
 * 临时内容的 localStorage key
 */
export const TempTextareaKey = 'editor_temp_textarea_value'

/**
 * doc tree and editor width
 */
export interface DocsEditorStyle {
  docs: string
  editor: string
}

/**
 * article reference
 */
export interface ArticleReference {
  /**
   * reference targetid
   *
   * if type === 10 | 21, targetId is 0
   * else targetId is articleId
   */
  targetId: string
  /**
   * Target name
   *
   * if type === 10 | 21, targetName is ''
   * else targetId is articleName
   */
  targetName: string
  /**
   * Target name
   *
   * if type === 10 | 21, targetName is ''
   * else targetId is articleName
   */
  targetUrl: string
  /**
   * 文章的引用类型
   *
   * 10 : picture
   * 11 : inner article
   * 12 : unknown inner article
   * 21 : public article
   */
  type: 10 | 11 | 12 | 21
}

/**
 * 目录结构
 */
export interface Toc {
  content: string
  clazz: string
  id: string
}

/**
 * 解析文章中的标题, 返回目录对象集合
 *
 * @param ele
 * @returns
 */
export const parseTocAsync = async (ele: HTMLElement): Promise<Toc[]> => {
  let heads = ele.querySelectorAll('h1, h2, h3, h4, h5, h6')
  let tocs: Toc[] = []
  for (let i = 0; i < heads.length; i++) {
    let head: Element = heads[i]
    let level = 1
    let content = (head as HTMLElement).innerText
    let id = head.id
    if (isNull(id)) {
      id = randomInt(1000000, 9999999).toString() + content
      head.id = id
    }
    switch (head.localName) {
      case 'h1':
        level = 1
        break
      case 'h2':
        level = 2
        break
      case 'h3':
        level = 3
        break
      case 'h4':
        level = 4
        break
      case 'h5':
        level = 5
        break
      case 'h6':
        level = 6
        break
    }
    let toc: Toc = { content: content, clazz: 'toc-' + level, id: id }
    tocs.push(toc)
  }
  return tocs
}

/**
 * 下载返回对象
 *
 * @param resp
 */
export const downloadTextPlain = (resp: any) => {
  let filename: string = resp.headers.get('content-disposition')
  let filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/
  let matches = filenameRegex.exec(filename)
  if (matches != null && matches[1]) {
    filename = decodeURI(matches[1].replace(/['"]/g, ''))
  }
  filename = decodeURI(filename)
  let a = document.createElement('a')
  let blob = new Blob([resp.data], { type: 'text/plain' })
  let objectUrl = URL.createObjectURL(blob)
  a.setAttribute('href', objectUrl)
  a.setAttribute('download', filename)
  a.click()
  URL.revokeObjectURL(a.href)
  a.remove()
}
