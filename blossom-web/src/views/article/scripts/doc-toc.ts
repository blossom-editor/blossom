/**
 * 目录结构
 */
export interface Toc {
  content: string
  clazz: string
  id: string
}

/**
 * 解析正文中的标题
 *
 * @param ele
 * @returns
 */
export const parseToc = (ele: HTMLElement): Toc[] => {
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
/**
 * 前往指定标题
 *
 * @param id
 */
export const toScroll = (id: string) => {
  let elm = document.getElementById(id)
  elm?.scrollIntoView(true)
}
