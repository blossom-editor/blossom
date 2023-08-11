import { EditorView } from "codemirror"
import { EditorSelection, SelectionRange } from "@codemirror/state"
import { isBlank } from "@renderer/assets/utils/obj"

/**
 * codemirror 样式配置
 * https://codemirror.net/examples/styling/#themes
 */
export const cwTheme: any = {
  "&": {
    color: "var(--bl-editor-color)",
    backgroundColor: "var(--bl-editor-bg-color)",
    fontSize: '14px'
  },
  ".cm-gutters": {
    backgroundColor: 'var(--bl-editor-gutters-bg-color)',
    borderColor: 'var(--bl-editor-gutters-border-color)',
    fontSize: '12px'
  },
  ".cm-activeLineGutter": {
    backgroundColor: 'var(--bl-editor-gutters-bg-color)',
    color: 'var(--el-color-primary)'
  },
  ".cm-lineNumbers": {
    width: '40px'
  },
  ".cm-foldGutter": {
    // paddingRight: '3px'
  },
  ".cm-content": {
    whiteSpace: "break-spaces",
    wordWrap: "break-word",
    width: "calc(100% - 55px)",
    overflow: 'auto',
    padding: '0',
    caretColor: "#707070"
  },
  ".cm-line": {
    // color: '#707070'
    // caretColor: 'var(--bl-editor-caret-color) !important',
    wordWrap: 'break-word',
    wordBreak: 'break-all',
    padding: '0'
  },
  ".cm-activeLine": {
    backgroundColor: 'var(--bl-editor-active-line-gutter-bg-color)',
  },
  ".cm-selectionMatch": {
    backgroundColor: 'var(--bl-editor-selection-match-bg-color)'
  },
  ".ͼ1.cm-focused": {
    outline: 'none'
  },
  ".ͼ2 .cm-activeLine": {
    backgroundColor: 'var(--bl-editor-active-line-gutter-bg-color)',
  },
  ".ͼ5": {
    color: 'var(--bl-editor-c5-color)',
    fontWeight: '700'
  },
  ".ͼ6": {
    color: '#707070',
    fontWeight: '500'
  },
  ".ͼ7": {
    backgroundColor: 'var(--bl-editor-c7-bg-color)',
    color: 'var(--bl-editor-c7-color)'
  },
  ".ͼc": {
    color: 'var(--bl-editor-cc-color)',
  },
  // ͼm: 注释   #940
  ".ͼm": {
    color: 'var(--bl-editor-cm-color)'
  },
  // ͼb: 关键字 #708
  ".ͼb": {
    color: 'var(--bl-editor-cb-color)'
  },
  // ͼd: 数字 #708
  ".ͼd": {
    color: 'var(--bl-editor-cd-color)'
  },
  // ͼe: 字符串 #a11
  ".ͼe": {
    color: 'var(--bl-editor-ce-color)'
  },
  //ͼi: 类名: 
  ".ͼi": {
    color: 'var(--bl-editor-ci-color)'
  },
  //ͼg: 方法名和参数
  ".ͼg": {
    color: 'var(--bl-editor-cg-color)'
  }
}

export class CmWrapper {
  // codemirror 的原生方法
  /**
   * 获取指定范围的内容
   * @param editor 
   * @param from 开始位置
   * @param to 结束位置
   * @returns 范围内的内容
   */
  static sliceDoc = (editor: EditorView, from?: number, to?: number): string => {
    return editor.state.sliceDoc(from, to)
  }
  /**
   * 在指定位置(istFrom -> istTo)插入 content, 或将内容替换为 content, 随后选中 (selectFrom -> selectTo)
   * istFrom 与 istTo 相同即为插入，不同即为替换
   * 如果要将光标移动到某处, selectFrom 与 selectTo 相同即可
   * 
   * @param istFrom 插入的开始位置
   * @param istTo 插入的结束位置
   * @param content 插入的内容
   * @param selectFrom 
   * @param selectTo 
   */
  static insert = (editor: EditorView, istFrom: number, istTo: number, content: string, selectFrom: number, selectTo: number) => {
    let changeByRange = {
      /* 创建变更的内容, 可以是个数组, 说明同时修改多个部分 */
      changes: [
        { from: istFrom, to: istTo, insert: content }
      ],
      /* 修改之后光标移动到的位置 */
      range: EditorSelection.range(selectFrom, selectTo)
    }
    editor.dispatch(
      /**
       * @param _range 当前选中的位置
       */
      editor.state.changeByRange((_range: SelectionRange) => {
        return changeByRange
      })
    )
  }
  /**
   * 行内格式的替换命令, 用于前后缀相同的格式, 如 `**` / `~~` 等
   * 
   * @param editor 编辑器
   * @param range 范围
   * @param target 添加的前后缀字符, 如加粗是 **, 行内代码块是 `
   */
  static replaceInlineCommand = (editor: EditorView, range: SelectionRange, target: string): any => {
    let len = target.length

    const prefixFrom: number = range.from - len
    const prefixTo: number = range.from
    const prefix = this.sliceDoc(editor, prefixFrom, prefixTo)

    const suffixFrom: number = range.to
    const suffixTo: number = range.to + len
    const suffix = this.sliceDoc(editor, suffixFrom, suffixTo)
    // 判断是取消还是添加, 如果被选中的文本前后已经是 target 字符, 则删除前后字符
    if (prefix == target && suffix == target) {
      return {
        changes: [
          { from: prefixFrom, to: prefixTo, insert: "" },
          { from: suffixFrom, to: suffixTo, insert: "" }
        ],
        range: EditorSelection.range(prefixFrom, suffixFrom - len)
      }
    } else {
      return {
        changes: [
          { from: range.from, insert: target },
          { from: range.to, insert: target }
        ],
        range: EditorSelection.range(range.from + len, range.to + len)
      }
    }
  }
  /**
   * 行内格式的替换命令, 用于前后缀不同的格式, 如 `<sup></sup>`等
   * 
   * @param editor 编辑器
   * @param range  范围
   * @param prefixTarget 前缀
   * @param suffixTarget 后缀
   * @returns 
   */
  static replaceDifInlineCommand = (editor: EditorView, range: SelectionRange, prefixTarget: string, suffixTarget: string): any => {
    let prefixLen = prefixTarget.length
    let suffixLen = suffixTarget.length

    const prefixFrom: number = range.from - prefixLen
    const prefixTo: number = range.from
    const prefix = this.sliceDoc(editor, prefixFrom, prefixTo)

    const suffixFrom: number = range.to
    const suffixTo: number = range.to + suffixLen
    const suffix = this.sliceDoc(editor, suffixFrom, suffixTo)

    console.log(prefix, suffix);


    // 判断是取消还是添加, 如果被选中的文本前后已经是 target 字符, 则删除前后字符
    if (prefix == prefixTarget && suffix == suffixTarget) {
      return {
        changes: [
          { from: prefixFrom, to: prefixTo, insert: "" },
          { from: suffixFrom, to: suffixTo, insert: "" }
        ],
        range: EditorSelection.range(prefixFrom, range.to - prefixLen)
      }
    } else {
      return {
        changes: [
          { from: range.from, insert: prefixTarget },
          { from: range.to, insert: suffixTarget }
        ],
        range: EditorSelection.range(range.from + prefixLen, range.to + prefixLen)
      }
    }
  }
  /**
   * 将选中内容替换为 content, 如果没有选中, 则在光标位置插入
   * @param editor 编辑器
   * @param content 插入的内容
   */
  static insertBlockCommand = (editor: EditorView, content: string) => {
    editor.dispatch(editor.state.replaceSelection(content))
  }
  /**
   * 选中内容加粗
   */
  static commandInlineBold = (editor: EditorView) => {
    editor.dispatch(editor.state.changeByRange((range: SelectionRange) => this.replaceInlineCommand(editor, range, '**')))
  }
  /**
   * 选中内容斜体
   */
  static commandInlineItalic = (editor: EditorView) => {
    editor.dispatch(editor.state.changeByRange((range: SelectionRange) => this.replaceInlineCommand(editor, range, '*')))
  }
  /**
   * 选中内容增加删除线
   */
  static commandInlineStrike = (editor: EditorView) => {
    editor.dispatch(editor.state.changeByRange((range: SelectionRange) => this.replaceInlineCommand(editor, range, '~~')))
  }
  /**
   * 选择内容设置为行内代码块
   */
  static commandInlineCode = (editor: EditorView) => {
    editor.dispatch(editor.state.changeByRange((range: SelectionRange) => this.replaceInlineCommand(editor, range, '`')))
  }
  /**
   * 选择内容设置为上标
   */
  static commandInlineSup = (editor: EditorView) => {
    editor.dispatch(editor.state.changeByRange((range: SelectionRange) => this.replaceDifInlineCommand(editor, range, '<sup>', '</sup>')))
  }
  /**
   * 选择内容设置为下标
   */
  static commandInlineSub = (editor: EditorView) => {
    editor.dispatch(editor.state.changeByRange((range: SelectionRange) => this.replaceDifInlineCommand(editor, range, '<sub>', '</sub>')))
  }
  /**
   * 在当前位置增加表格
   */
  static commandBlockTable = (editor: EditorView) => {
    this.insertBlockCommand(editor, `\n|||\n|---|---|\n|||\n`)
  }
  /**
   * 在当前位置增加多行代码块
   */
  static commandBlockPre = (editor: EditorView) => {
    this.insertBlockCommand(editor, `\n\`\`\`java\n\n\`\`\`\n`)
  }
  /**
   * 在当前位置增加单选框
   */
  static commandBlockCheckBox = (editor: EditorView) => {
    this.insertBlockCommand(editor, `\n- [ ] \n`)
  }
  /**
   * 在当前位置增加分割线 
   */
  static commandBlockSeparator = (editor: EditorView) => {
    this.insertBlockCommand(editor, `\n---\n`)
  }
  /**
   * 在当前位置增加引用
   */
  static commandBlockquote = (editor: EditorView) => {
    this.insertBlockCommand(editor, `\n>\n>\n`)
  }
  /**
   * 在当前位置增加引用
   */
  static commandBlockquoteBlack = (editor: EditorView) => {
    this.insertBlockCommand(editor, `\n> $$black$$\n> ⚫\n`)
  }
  /**
   * 在当前位置增加引用
   */
  static commandBlockquoteGreen = (editor: EditorView) => {
    this.insertBlockCommand(editor, `\n> $$green$$\n> 🟢\n`)
  }
  /**
   * 在当前位置增加引用
   */
  static commandBlockquoteYellow = (editor: EditorView) => {
    this.insertBlockCommand(editor, `\n> $$yellow$$\n> 🟡\n`)
  }
  /**
   * 在当前位置增加引用
   */
  static commandBlockquoteRed = (editor: EditorView) => {
    this.insertBlockCommand(editor, `\n> $$red$$\n> 🔴\n`)
  }
  /**
   * 在当前位置增加引用
   */
  static commandBlockquoteBlue = (editor: EditorView) => {
    this.insertBlockCommand(editor, `\n> $$blue$$\n> 🔵\n`)
  }
  /**
   * 在当前位置增加引用
   */
  static commandBlockquotePurple = (editor: EditorView) => {
    this.insertBlockCommand(editor, `\n> $$purple$$\n> 🟣\n`)
  }
  /**
   * 在当前位置增加无序列表
   */
  static commandBlockUnordered = (editor: EditorView) => {
    this.insertBlockCommand(editor, `\n- \n`)
  }
  /**
   * 在当前位置增加有序列表
   */
  static commandBlockOrdered = (editor: EditorView) => {
    this.insertBlockCommand(editor, `\n1. \n`)
  }
  /**
   * 在当前位置增加图片
   */
  static commandBlockImg = (editor: EditorView) => {
    this.insertBlockCommand(editor, `\n![]()\n`)
  }
  /**
   * 在当前位置增加链接
   */
  static commandBlockLink = (editor: EditorView) => {
    this.insertBlockCommand(editor, `\n[]()\n`)
  }
  /**
   * 获取当前选中内容, 并返回选中的文本内容, 可以选中多个不同的段落, 多个段落之间会以 \n 换行
   * @param editor 
   * @returns 文本内容, 多个选中之间会换行
   */
  static getSelectionRangesText = (editor: EditorView): string => {
    let ranges = editor.state.selection.ranges;
    let text = ''
    if (ranges.length > 0) {
      for (let i = 0; i < ranges.length; i++) {
        let range = ranges[i]
        if (range != undefined) {
          let rangeText = editor.state.sliceDoc(range.from, range.to)
          if (isBlank(rangeText)) {
            continue;
          }
          if (i != 0) {
            text += '\n'
          }
          text += rangeText
        }
      }
    }
    return text
  }
  /**
   * 获取文档内容
   * @param editor 
   * @returns 内容
   */
  static getDocString = (editor: EditorView): string => {
    return editor.state.doc.toString()
  }
  /**
   * 获取文档长度
   * @param editor 
   * @returns 长度
   */
  static getDocLength = (editor: EditorView): number => {
    return editor.state.doc.length
  }
}

// export default CmWrapper