import { platform } from '@renderer/assets/utils/util'

const isMac = platform() === 'darwin'

export const keymaps = {
  save: isMac ? '⌘ S' : 'Ctrl + S',
  hideDocs: isMac ? '⌘ 1' : 'Alt + 1',
  hideToc: isMac ? '⌘ 2' : 'Alt + 2',
  fullViewer: isMac ? '⌘ 3' : 'Alt + 3',
  fullEditor: isMac ? '⌘ 4' : 'Alt + 4',
  formatAll: isMac ? '⌥ ⇧ F' : 'Slift + Alt + F',
  fullSearch: isMac ? '⌘ ⇧ F' : 'Ctrl + Shift + F',
  fullSearchOperatorAnd: isMac ? '⌘ G' : 'Alt + G',
  newDoc: isMac ? '⌘ N' : 'Ctrl + N',

  blod: isMac ? '⌘ B' : 'Alt + B',
  italic: isMac ? '⌥ ⌘ B' : 'Ctrl + Alt + B',
  striket: isMac ? '⌥ S' : 'Alt + S',
  sup: isMac ? '⌃ ⌘ P' : 'Ctrl + Alt + P',
  sub: isMac ? '⌃ ⌘ B' : 'Ctrl + Alt + B',
  separator: isMac ? '⌃ ⌘ S' : 'Ctrl + Alt + S',

  blockquote: isMac ? '>' : '>',
  code: isMac ? '⌘ E' : 'Alt + E',
  pre: isMac ? '⌃ ⌘ E' : 'Ctrl + Alt + E',

  table: isMac ? '⌘ T' : 'Alt + T',
  image: isMac ? '⌘ M' : 'Alt + M',
  link: isMac ? '⌘ K' : 'Alt + K',

  // 列模式
  listView: isMac ? '⌥' : 'Alt',
  // 光标多行
  selectMultLine: isMac ? '⌘' : 'Ctrl',
  search: isMac ? '⌘ F' : 'Ctrl + F',
  replace: isMac ? '⌘ G' : 'Ctrl + G',
  cut: isMac ? '⌘ X' : 'Ctrl + X',
  // 恢复
  redo: isMac ? '⌘ ⇧ Z' : 'Ctrl + Y',
  // 撤销
  undo: isMac ? '⌘ Z' : 'Ctrl + Z',

  comment: isMac ? '⌘ /' : 'Ctrl + /',
  retractionFront: isMac ? '⌘ [' : 'Ctrl + [',
  retractionBack: isMac ? '⌘ ]' : 'Ctrl + ]',

  // https://codemirror.net/docs/ref/#commands.defaultKeymap
  selectLine: isMac ? '⌃ L' : 'Alt + L',
  // https://codemirror.net/docs/ref/#search
  toLine: isMac ? '⌥ ⌘ G' : 'Ctrl + Alt + G',
  moveLineUp: isMac ? '⌥ ↑' : 'Alt + ↑',
  moveLineDown: isMac ? '⌥ ↓' : 'Alt + ↓',
  copyLineUp: isMac ? '⌥ ⇧ ↑' : 'Shift + Alt + ↑',
  copyLineDown: isMac ? '⌥ ⇧ ↓' : 'Shift + Alt + ↓'
}
