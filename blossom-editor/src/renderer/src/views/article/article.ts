// codemirror
import { EditorView } from "codemirror"
import { EditorSelection, SelectionRange } from "@codemirror/state"



/**
 * 
 */
export const TempTextareaKey = 'editor_temp_textarea_value'

//#region ----------------------------------------< codemirror >--------------------------------------

/**
 * codemirror 样式配置
 * https://codemirror.net/examples/styling/#themes
 */
export const codemirrorTheme: any = {
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

/**
 * 行内格式的替换命令
 * @param editor 编辑器
 * @param range 范围
 * @param target 添加的前后缀字符, 如加粗是 **, 行内代码块是 `
 */
const replaceInlineCommand = (editor: EditorView, range: SelectionRange, target: string): any => {
  let targetLength = target.length

  const prefixFrom: number = range.from - targetLength
  const prefixTo: number = range.from
  const prefix = editor.state.sliceDoc(prefixFrom, prefixTo)

  const suffixFrom: number = range.to
  const suffixTo: number = range.to + targetLength
  const suffix = editor.state.sliceDoc(suffixFrom, suffixTo)
  // 判断是取消还是添加, 如果被选中的文本前后已经是 target 字符, 则删除前后字符
  if (prefix == target && suffix == target) {
    return {
      changes: [
        { from: prefixFrom, to: prefixTo, insert: "" },
        { from: suffixFrom, to: suffixTo, insert: "" }
      ],
      range: EditorSelection.range(prefixFrom, suffixFrom - targetLength)
    }
  } else {
    return {
      changes: [
        { from: range.from, insert: target },
        { from: range.to, insert: target }
      ],
      range: EditorSelection.range(range.from + targetLength, range.to + targetLength)
    }
  }
}
const replaceDifInlineCommand = (editor: EditorView, range: SelectionRange, prefixTarget: string, suffixTarget: string): any => {
  let prefixLength = prefixTarget.length
  let suffixLength = suffixTarget.length

  const prefixFrom: number = range.from - prefixLength
  const prefixTo: number = range.from
  const prefix = editor.state.sliceDoc(prefixFrom, prefixTo)

  const suffixFrom: number = range.to
  const suffixTo: number = range.to + suffixLength
  const suffix = editor.state.sliceDoc(suffixFrom, suffixTo)

  console.log(prefix, suffix);


  // 判断是取消还是添加, 如果被选中的文本前后已经是 target 字符, 则删除前后字符
  if (prefix == prefixTarget && suffix == suffixTarget) {
    return {
      changes: [
        { from: prefixFrom, to: prefixTo, insert: "" },
        { from: suffixFrom, to: suffixTo, insert: "" }
      ],
      range: EditorSelection.range(prefixFrom, range.to - prefixLength)
    }
  } else {
    return {
      changes: [
        { from: range.from, insert: prefixTarget },
        { from: range.to, insert: suffixTarget }
      ],
      range: EditorSelection.range(range.from + prefixLength, range.to + prefixLength)
    }
  }
}
/**
 * 将选中内容替换为 content, 如果没有选中, 则在光标位置插入
 * @param editor 编辑器
 * @param content 插入的内容
 */
export const insertBlockCommand = (editor: EditorView, content: string) => {
  editor.dispatch(editor.state.replaceSelection(content))
}
/**
 * 选中内容加粗
 */
export const commandInlineBold = (editor: EditorView) => {
  editor.dispatch(editor.state.changeByRange((range: SelectionRange) => replaceInlineCommand(editor, range, '**')))
}
/**
 * 选中内容斜体
 */
export const commandInlineItalic = (editor: EditorView) => {
  editor.dispatch(editor.state.changeByRange((range: SelectionRange) => replaceInlineCommand(editor, range, '*')))
}
/**
 * 选中内容增加删除线
 */
export const commandInlineStrike = (editor: EditorView) => {
  editor.dispatch(editor.state.changeByRange((range: SelectionRange) => replaceInlineCommand(editor, range, '~~')))
}
/**
 * 选择内容设置为行内代码块
 */
export const commandInlineCode = (editor: EditorView) => {
  editor.dispatch(editor.state.changeByRange((range: SelectionRange) => replaceInlineCommand(editor, range, '`')))
}
/**
 * 选择内容设置为上标
 */
export const commandInlineSup = (editor: EditorView) => {
  editor.dispatch(editor.state.changeByRange((range: SelectionRange) => replaceDifInlineCommand(editor, range, '<sup>', '</sup>')))
}
/**
 * 选择内容设置为下标
 */
export const commandInlineSub = (editor: EditorView) => {
  editor.dispatch(editor.state.changeByRange((range: SelectionRange) => replaceDifInlineCommand(editor, range, '<sub>', '</sub>')))
}
/**
 * 在当前位置增加表格
 */
export const commandBlockTable = (editor: EditorView) => {
  insertBlockCommand(editor, `\n|||\n|---|---|\n|||\n`)
}
/**
 * 在当前位置增加多行代码块
 */
export const commandBlockPre = (editor: EditorView) => {
  insertBlockCommand(editor, `\n\`\`\`java\n\n\`\`\`\n`)
}
/**
 * 在当前位置增加单选框
 */
export const commandBlockCheckBox = (editor: EditorView) => {
  insertBlockCommand(editor, `\n- [ ] \n`)
}
/**
 * 在当前位置增加分割线 
 */
export const commandBlockSeparator = (editor: EditorView) => {
  insertBlockCommand(editor, `\n---\n`)
}
/**
 * 在当前位置增加引用
 */
export const commandBlockquote = (editor: EditorView) => {
  insertBlockCommand(editor, `\n>\n>\n`)
}
export const commandBlockquoteBlack = (editor: EditorView) => {
  insertBlockCommand(editor, `\n> $$black$$\n> ⚫\n`)
}
export const commandBlockquoteGreen = (editor: EditorView) => {
  insertBlockCommand(editor, `\n> $$green$$\n> 🟢\n`)
}
export const commandBlockquoteYellow = (editor: EditorView) => {
  insertBlockCommand(editor, `\n> $$yellow$$\n> 🟡\n`)
}
export const commandBlockquoteRed = (editor: EditorView) => {
  insertBlockCommand(editor, `\n> $$red$$\n> 🔴\n`)
}
export const commandBlockquoteBlue = (editor: EditorView) => {
  insertBlockCommand(editor, `\n> $$blue$$\n> 🔵\n`)
}
export const commandBlockquotePurple = (editor: EditorView) => {
  insertBlockCommand(editor, `\n> $$purple$$\n> 🟣\n`)
}
/**
 * 在当前位置增加无序列表
 */
export const commandBlockUnordered = (editor: EditorView) => {
  insertBlockCommand(editor, `\n- \n`)
}
/**
 * 在当前位置增加有序列表
 */
export const commandBlockOrdered = (editor: EditorView) => {
  insertBlockCommand(editor, `\n1. \n`)
}
/**
 * 在当前位置增加图片
 */
export const commandBlockImg = (editor: EditorView) => {
  insertBlockCommand(editor, `\n![]()\n`)
}
/**
 * 在当前位置增加链接
 */
export const commandBlockLink = (editor: EditorView) => {
  insertBlockCommand(editor, `\n[]()\n`)
}
//#endregion
