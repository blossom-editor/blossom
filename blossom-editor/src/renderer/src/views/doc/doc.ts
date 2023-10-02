import { Ref } from 'vue'
import type { InjectionKey } from 'vue'

/**
 * 依赖注入的 key, 用于限制类型
 */
export const provideKeyDocTree = Symbol() as InjectionKey<Ref<DocTree[]>>
export const provideKeyDocInfo = Symbol() as InjectionKey<Ref<DocInfo | undefined>>
export const provideKeyCurArticleInfo = Symbol() as InjectionKey<Ref<DocInfo | undefined>>

/**
 * 将 docTree 对象转换为 docInfo
 * @param tree DocTree
 * @returns DocInfo
 */
export const treeToInfo = (tree: DocTree): DocInfo => {
  return {
    id: tree.i,
    pid: 0,
    name: tree.n,
    tags: tree.t,
    sort: 1,
    openStatus: 0,
    starStatus: 0,
    type: tree.ty
  }
}

export enum TitleColor {
  ONE = '#C9515193',
  TWO = '#E6981293',
  THREE = '#127EA993',
  FOUR = '#4AA40E93',
}

/**
 * 通过标题等级计算颜色
 * @param level 标题等级
 * @returns 
 */
export const computedDocTitleColor = (level: number) => {
  if (level === 1) {
    return TitleColor.ONE
  }
  if (level === 2) {
    return TitleColor.TWO
  }
  if (level === 3) {
    return TitleColor.THREE
  }
  return TitleColor.FOUR
}