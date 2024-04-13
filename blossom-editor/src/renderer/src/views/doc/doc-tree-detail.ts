/* ======================================================================
 * 文章树状列表自定义节点
 * ====================================================================== */
import { isNotBlank } from '@renderer/assets/utils/obj'

/**
 * 计算标签, 并返回便签对象集合
 * @param doc 文章内容
 * @param viewStyle 页面样式
 */
export const tags = (
  doc: DocTree,
  viewStyle: {
    isShowFolderStarTag: boolean
    isShowArticleTocTag: boolean
    isShowArticleCustomTag: boolean
    isShowFolderOpenTag: boolean
  }
) => {
  let icons: any = []
  doc.t.forEach((tag) => {
    if (tag.toLocaleLowerCase() === 'subject') {
      icons.unshift({ content: '专题', bgColor: 'var(--bl-tag-color-subject)', icon: 'bl-a-lowerrightpage-line' })
    } else if (tag.toLocaleLowerCase() === 'toc') {
      if (!viewStyle.isShowArticleTocTag) {
        return
      }
      icons.push({ content: 'TOC', bgColor: 'var(--bl-tag-color-toc)' })
    } else if (viewStyle.isShowArticleCustomTag) {
      icons.push({ content: tag })
    }
  })
  if (doc.o === 1 && doc.ty != 3) {
    if (viewStyle.isShowFolderOpenTag) {
      icons.unshift({ bgColor: 'var(--bl-tag-color-open)', icon: 'bl-cloud-line' })
    }
  }
  if (doc.ty === 1 && doc.star === 1) {
    if (viewStyle.isShowFolderStarTag) {
      icons.unshift({ bgColor: 'rgb(220,192,36)', icon: 'bl-star-line' })
    }
  }
  return icons
}

/**
 * 竖条状态标识
 */
export const tagLins = (doc: DocTree) => {
  let lines: string[] = []
  if (doc.ty === 3) {
    if (doc.star === 1) {
      lines.push('star-line')
    }
    if (doc.o === 1) {
      lines.push('open-line')
    }
    if (doc.vd === 1) {
      lines.push('sync-line')
    }
  }
  return lines
}

/**
 * 文章图标是否是链接
 * @param doc
 * @param viewStyle
 * @returns
 */
export const isShowImg = (doc: DocTree, viewStyle: { isShowArticleIcon: boolean }) => {
  return viewStyle.isShowArticleIcon && isNotBlank(doc.icon) && (doc.icon.startsWith('http') || doc.icon.startsWith('https')) && !doc?.updn
}

/**
 * 文章图标是否是内置 svg
 * @param doc
 * @param viewStyle
 * @returns
 */
export const isShowSvg = (doc: DocTree, viewStyle: { isShowArticleIcon: boolean }) => {
  return viewStyle.isShowArticleIcon && isNotBlank(doc.icon) && !doc?.updn
}
