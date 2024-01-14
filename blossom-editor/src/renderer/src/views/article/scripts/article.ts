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
