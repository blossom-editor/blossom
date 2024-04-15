import { isNotBlank } from '@/assets/utils/obj'

/**
 * 文章图标是否是链接
 * @param doc
 * @param viewStyle
 * @returns
 */
export const isShowImg = (doc: DocTree) => {
  return isNotBlank(doc.icon) && (doc.icon.startsWith('http') || doc.icon.startsWith('https'))
}

/**
 * 文章图标是否是内置 svg
 * @param doc
 * @param viewStyle
 * @returns
 */
export const isShowSvg = (doc: DocTree) => {
  return isNotBlank(doc.icon)
}

/**
 * 计算标签, 并返回便签对象集合
 * @param doc 文章内容
 * @param viewStyle 页面样式
 */
export const tags = (doc: DocTree) => {
  let icons: any = []
  doc.t.forEach((tag) => {
    if (tag.toLocaleLowerCase() === 'subject') {
      icons.unshift({ content: '专题', bgColor: '#565656', icon: 'bl-a-lowerrightpage-line' })
    } else if (tag.toLocaleLowerCase() === 'toc') {
      icons.push({ content: '目录', bgColor: '#939393' })
    } else {
      icons.push({ content: tag })
    }
  })
  return icons
}
