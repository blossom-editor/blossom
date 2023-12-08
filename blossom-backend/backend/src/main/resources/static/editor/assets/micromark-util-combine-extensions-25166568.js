import { s as splice } from "./micromark-util-chunked-b50d5f7c.js";
const hasOwnProperty = {}.hasOwnProperty;
function combineExtensions(extensions) {
  const all = {};
  let index = -1;
  while (++index < extensions.length) {
    syntaxExtension(all, extensions[index]);
  }
  return all;
}
function syntaxExtension(all, extension) {
  let hook;
  for (hook in extension) {
    const maybe = hasOwnProperty.call(all, hook) ? all[hook] : void 0;
    const left = maybe || (all[hook] = {});
    const right = extension[hook];
    let code;
    if (right) {
      for (code in right) {
        if (!hasOwnProperty.call(left, code))
          left[code] = [];
        const value = right[code];
        constructs(
          // @ts-expect-error Looks like a list.
          left[code],
          Array.isArray(value) ? value : value ? [value] : []
        );
      }
    }
  }
}
function constructs(existing, list) {
  let index = -1;
  const before = [];
  while (++index < list.length) {
    (list[index].add === "after" ? existing : before).push(list[index]);
  }
  splice(existing, 0, 0, before);
}
export {
  combineExtensions as c
};
