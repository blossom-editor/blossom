import { platform } from '@renderer/assets/utils/util'

const isMac = platform() === 'darwin'

export const keymaps = {
  save: isMac ? 'Cmd + S' : 'Ctrl + S',
  hideDocs: isMac ? 'Cmd + 1' : 'Alt + 1',
  hideToc: isMac ? 'Cmd + 2' : 'Alt + 2',
  fullViewer: isMac ? 'Cmd + 3' : 'Alt + 3',
  fullEditor: isMac ? 'Cmd + 4' : 'Alt + 4',
  formatAll: isMac ? 'Slift + Cmd + F' : 'Slift + Alt + F',

  blod: isMac ? 'Cmd + B' : 'Alt + B',
  italic: isMac ? 'Cmd + I' : 'Alt + I',
  striket: isMac ? 'Cmd + S' : 'Alt + S',
  sup: isMac ? 'Ctrl + Cmd + P' : 'Ctrl + Alt + P',
  sub: isMac ? 'Ctrl + Cmd + B' : 'Ctrl + Alt + B',
  separator: isMac ? 'Ctrl + Cmd + S' : 'Ctrl + Alt + S',

  blockquote: isMac ? '>' : '>',
  code: isMac ? 'Cmd + E' : 'Alt + E',
  pre: isMac ? 'Ctrl + Cmd + S' : 'Ctrl + Alt + E',

  table: isMac ? 'Cmd + T' : 'Alt + T',
  image: isMac ? 'Cmd + M' : 'Alt + M',
  link: isMac ? 'Cmd + K' : 'Alt + K'
}
