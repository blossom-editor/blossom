
import { isEmpty } from 'lodash'

/**
 * 临时内容的 localStorage key
 */
export const TempTextareaKey = 'editor_temp_textarea_value'

/**
 * doc tree and editor width
 */
export interface DocEditorStyle {
  docs: string;
  editor: string
}

/**
 * editor and preview styles
 */
export interface EditorPreviewStyle {
  gutter: any,
  editor: any,
  preview: any
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
  targetId: number,
  /**
   * Target name
   * 
   * if type === 10 | 21, targetName is ''
   * else targetId is articleName
   */
  targetName: string,
  /**
   * Target name
   * 
   * if type === 10 | 21, targetName is ''
   * else targetId is articleName
   */
  targetUrl: string,
  /**
   * 文章的引用类型
   * 
   * 10 : picture
   * 11 : inner article
   * 21 : public article
   */
  type: 10 | 11 | 21
}


/**
 * 递归从文档树状列表中获取指定ID的文章信息
 * 
 * @param articleId 文档DI, 通常是文章ID, 也兼容文件夹ID的获取
 * @param trees 文档树状列表
 */
export const getDocInfoFromTrees = (articleId: number, trees: DocTree[]): DocTree | undefined => {
  let target: DocTree | undefined
  for (let i = 0; i < trees.length; i++) {
    let tree = trees[i]
    if (tree.i == articleId) {
      target = tree
    } else if (!isEmpty(tree.children)) {
      target = getDocInfoFromTrees(articleId, tree.children!)
    }
    if (target != undefined) {
      break
    }
  }
  return target
}