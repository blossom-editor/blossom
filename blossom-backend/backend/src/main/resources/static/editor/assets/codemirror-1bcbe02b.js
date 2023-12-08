import { l as lineNumbers, h as highlightActiveLineGutter, a as highlightSpecialChars, b as history, f as foldGutter, d as drawSelection, c as dropCursor, E as EditorState, i as indentOnInput, s as syntaxHighlighting, e as bracketMatching, g as closeBrackets, j as autocompletion, r as rectangularSelection, k as crosshairCursor, m as highlightActiveLine, n as highlightSelectionMatches, o as keymap, p as closeBracketsKeymap, q as defaultKeymap, t as searchKeymap, u as historyKeymap, v as foldKeymap, w as completionKeymap, x as lintKeymap, y as defaultHighlightStyle } from "./@codemirror-1642cad3.js";
const basicSetup = /* @__PURE__ */ (() => [
  lineNumbers(),
  highlightActiveLineGutter(),
  highlightSpecialChars(),
  history(),
  foldGutter(),
  drawSelection(),
  dropCursor(),
  EditorState.allowMultipleSelections.of(true),
  indentOnInput(),
  syntaxHighlighting(defaultHighlightStyle, { fallback: true }),
  bracketMatching(),
  closeBrackets(),
  autocompletion(),
  rectangularSelection(),
  crosshairCursor(),
  highlightActiveLine(),
  highlightSelectionMatches(),
  keymap.of([
    ...closeBracketsKeymap,
    ...defaultKeymap,
    ...searchKeymap,
    ...historyKeymap,
    ...foldKeymap,
    ...completionKeymap,
    ...lintKeymap
  ])
])();
export {
  basicSetup as b
};
