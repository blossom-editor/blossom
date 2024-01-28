import { isNull } from '@renderer/assets/utils/obj'

/**
 * 临时内容的 localStorage key
 */
export const TempTextareaKey = 'editor_temp_textarea_value'

/**
 * doc tree and editor width
 */
export interface DocEditorStyle {
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