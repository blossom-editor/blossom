import { i as markdownLineEndingOrSpace, u as unicodeWhitespace, j as unicodePunctuation } from "./micromark-util-character-89d8b557.js";
function classifyCharacter(code) {
  if (code === null || markdownLineEndingOrSpace(code) || unicodeWhitespace(code)) {
    return 1;
  }
  if (unicodePunctuation(code)) {
    return 2;
  }
}
export {
  classifyCharacter as c
};
