import { f as factorySpace } from "./micromark-factory-space-6db398ce.js";
import { m as markdownLineEnding, e as markdownSpace } from "./micromark-util-character-89d8b557.js";
function factoryWhitespace(effects, ok) {
  let seen;
  return start;
  function start(code) {
    if (markdownLineEnding(code)) {
      effects.enter("lineEnding");
      effects.consume(code);
      effects.exit("lineEnding");
      seen = true;
      return start;
    }
    if (markdownSpace(code)) {
      return factorySpace(
        effects,
        start,
        seen ? "linePrefix" : "lineSuffix"
      )(code);
    }
    return ok(code);
  }
}
export {
  factoryWhitespace as f
};
