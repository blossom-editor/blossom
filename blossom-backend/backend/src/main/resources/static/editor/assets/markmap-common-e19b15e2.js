/*! markmap-common v0.15.3 | MIT License */
function _extends() {
  _extends = Object.assign ? Object.assign.bind() : function(target) {
    for (var i = 1; i < arguments.length; i++) {
      var source = arguments[i];
      for (var key in source) {
        if (Object.prototype.hasOwnProperty.call(source, key)) {
          target[key] = source[key];
        }
      }
    }
    return target;
  };
  return _extends.apply(this, arguments);
}
const testPath = "npm2url/dist/index.cjs";
const defaultProviders = {
  jsdelivr: (path) => `https://cdn.jsdelivr.net/npm/${path}`,
  unpkg: (path) => `https://unpkg.com/${path}`
};
class UrlBuilder {
  constructor() {
    this.providers = _extends({}, defaultProviders);
    this.provider = "jsdelivr";
  }
  getFastestProvider(timeout = 5e3, path = testPath) {
    return new Promise((resolve, reject) => {
      Promise.all(Object.entries(this.providers).map(async ([name, factory]) => {
        try {
          const res = await fetch(factory(path));
          if (!res.ok) {
            throw res;
          }
          await res.text();
          resolve(name);
        } catch (_unused) {
        }
      })).then(() => reject(new Error("All providers failed")));
      setTimeout(reject, timeout, new Error("Timed out"));
    });
  }
  async findFastestProvider(timeout) {
    this.provider = await this.getFastestProvider(timeout);
    return this.provider;
  }
  setProvider(name, factory) {
    if (factory) {
      this.providers[name] = factory;
    } else {
      delete this.providers[name];
    }
  }
  getFullUrl(path, provider = this.provider) {
    if (path.includes("://")) {
      return path;
    }
    const factory = this.providers[provider];
    if (!factory) {
      throw new Error(`Provider ${provider} not found`);
    }
    return factory(path);
  }
}
new UrlBuilder();
class Hook {
  constructor() {
    this.listeners = [];
  }
  tap(fn) {
    this.listeners.push(fn);
    return () => this.revoke(fn);
  }
  revoke(fn) {
    const i = this.listeners.indexOf(fn);
    if (i >= 0)
      this.listeners.splice(i, 1);
  }
  revokeAll() {
    this.listeners.splice(0);
  }
  call(...args) {
    for (const fn of this.listeners) {
      fn(...args);
    }
  }
}
function _objectWithoutPropertiesLoose(source, excluded) {
  if (source == null)
    return {};
  var target = {};
  var sourceKeys = Object.keys(source);
  var key, i;
  for (i = 0; i < sourceKeys.length; i++) {
    key = sourceKeys[i];
    if (excluded.indexOf(key) >= 0)
      continue;
    target[key] = source[key];
  }
  return target;
}
const _excluded = ["textContent"];
const escapeChars = {
  "&": "&amp;",
  "<": "&lt;",
  '"': "&quot;"
};
function escapeHtml(html) {
  return html.replace(/[&<"]/g, (m) => escapeChars[m]);
}
function escapeScript(content) {
  return content.replace(/<(\/script>)/g, "\\x3c$2");
}
function htmlOpen(tagName, attrs) {
  const attrStr = attrs ? Object.entries(attrs).map(([key, value]) => {
    if (value == null || value === false)
      return;
    key = ` ${escapeHtml(key)}`;
    if (value === true)
      return key;
    return `${key}="${escapeHtml(value)}"`;
  }).filter(Boolean).join("") : "";
  return `<${tagName}${attrStr}>`;
}
function htmlClose(tagName) {
  return `</${tagName}>`;
}
function wrapHtml(tagName, content, attrs) {
  if (content == null)
    return htmlOpen(tagName, attrs);
  return htmlOpen(tagName, attrs) + (content || "") + htmlClose(tagName);
}
function buildCode(fn, args) {
  const params = args.map((arg) => {
    if (typeof arg === "function")
      return arg.toString();
    return JSON.stringify(arg != null ? arg : null);
  }).join(",");
  return `(${fn.toString()})(${params})`;
}
function persistJS(items, context) {
  return items.map((item) => {
    if (item.type === "script") {
      const _item$data = item.data, {
        textContent
      } = _item$data, rest = _objectWithoutPropertiesLoose(_item$data, _excluded);
      return wrapHtml("script", textContent || "", rest);
    }
    if (item.type === "iife") {
      const {
        fn,
        getParams
      } = item.data;
      return wrapHtml("script", escapeScript(buildCode(fn, (getParams == null ? void 0 : getParams(context)) || [])));
    }
    return "";
  });
}
function persistCSS(items) {
  return items.map((item) => {
    if (item.type === "stylesheet") {
      return wrapHtml("link", null, _extends({
        rel: "stylesheet"
      }, item.data));
    }
    return wrapHtml("style", item.data);
  });
}
const uniqId = Math.random().toString(36).slice(2, 8);
let globalIndex = 0;
function getId() {
  globalIndex += 1;
  return `mm-${uniqId}-${globalIndex}`;
}
function noop() {
}
function walkTree(tree, callback) {
  const walk = (item, parent) => callback(item, () => {
    var _item$children;
    (_item$children = item.children) == null ? void 0 : _item$children.forEach((child) => {
      walk(child, item);
    });
  }, parent);
  walk(tree);
}
function addClass(className, ...rest) {
  const classList = (className || "").split(" ").filter(Boolean);
  rest.forEach((item) => {
    if (item && classList.indexOf(item) < 0)
      classList.push(item);
  });
  return classList.join(" ");
}
function childSelector(filter) {
  if (typeof filter === "string") {
    const tagName = filter;
    filter = (el) => el.tagName === tagName;
  }
  const filterFn = filter;
  return function selector() {
    let nodes = Array.from(this.childNodes);
    if (filterFn)
      nodes = nodes.filter((node) => filterFn(node));
    return nodes;
  };
}
function wrapFunction(fn, wrapper) {
  return (...args) => wrapper(fn, ...args);
}
function memoize(fn) {
  const cache = {};
  return function memoized(...args) {
    const key = `${args[0]}`;
    let data = cache[key];
    if (!data) {
      data = {
        value: fn(...args)
      };
      cache[key] = data;
    }
    return data.value;
  };
}
/*! @gera2ld/jsx-dom v2.2.2 | ISC License */
const VTYPE_ELEMENT = 1;
const VTYPE_FUNCTION = 2;
const SVG_NS = "http://www.w3.org/2000/svg";
const XLINK_NS = "http://www.w3.org/1999/xlink";
const NS_ATTRS = {
  show: XLINK_NS,
  actuate: XLINK_NS,
  href: XLINK_NS
};
const isLeaf = (c) => typeof c === "string" || typeof c === "number";
const isElement = (c) => (c == null ? void 0 : c.vtype) === VTYPE_ELEMENT;
const isRenderFunction = (c) => (c == null ? void 0 : c.vtype) === VTYPE_FUNCTION;
function h(type, props, ...children) {
  props = Object.assign({}, props, {
    children: children.length === 1 ? children[0] : children
  });
  return jsx(type, props);
}
function jsx(type, props) {
  let vtype;
  if (typeof type === "string")
    vtype = VTYPE_ELEMENT;
  else if (typeof type === "function")
    vtype = VTYPE_FUNCTION;
  else
    throw new Error("Invalid VNode type");
  return {
    vtype,
    type,
    props
  };
}
function Fragment(props) {
  return props.children;
}
const DEFAULT_ENV = {
  isSvg: false
};
function insertDom(parent, nodes) {
  if (!Array.isArray(nodes))
    nodes = [nodes];
  nodes = nodes.filter(Boolean);
  if (nodes.length)
    parent.append(...nodes);
}
function mountAttributes(domElement, props, env) {
  for (const key in props) {
    if (key === "key" || key === "children" || key === "ref")
      continue;
    if (key === "dangerouslySetInnerHTML") {
      domElement.innerHTML = props[key].__html;
    } else if (key === "innerHTML" || key === "textContent" || key === "innerText" || key === "value" && ["textarea", "select"].includes(domElement.tagName)) {
      const value = props[key];
      if (value != null)
        domElement[key] = value;
    } else if (key.startsWith("on")) {
      domElement[key.toLowerCase()] = props[key];
    } else {
      setDOMAttribute(domElement, key, props[key], env.isSvg);
    }
  }
}
const attrMap = {
  className: "class",
  labelFor: "for"
};
function setDOMAttribute(el, attr, value, isSVG) {
  attr = attrMap[attr] || attr;
  if (value === true) {
    el.setAttribute(attr, "");
  } else if (value === false) {
    el.removeAttribute(attr);
  } else {
    const namespace = isSVG ? NS_ATTRS[attr] : void 0;
    if (namespace !== void 0) {
      el.setAttributeNS(namespace, attr, value);
    } else {
      el.setAttribute(attr, value);
    }
  }
}
function flatten(arr) {
  return arr.reduce((prev, item) => prev.concat(item), []);
}
function mountChildren(children, env) {
  return Array.isArray(children) ? flatten(children.map((child) => mountChildren(child, env))) : mount(children, env);
}
function mount(vnode, env = DEFAULT_ENV) {
  if (vnode == null || typeof vnode === "boolean") {
    return null;
  }
  if (vnode instanceof Node) {
    return vnode;
  }
  if (isRenderFunction(vnode)) {
    const {
      type,
      props
    } = vnode;
    if (type === Fragment) {
      const node = document.createDocumentFragment();
      if (props.children) {
        const children = mountChildren(props.children, env);
        insertDom(node, children);
      }
      return node;
    }
    const childVNode = type(props);
    return mount(childVNode, env);
  }
  if (isLeaf(vnode)) {
    return document.createTextNode(`${vnode}`);
  }
  if (isElement(vnode)) {
    let node;
    const {
      type,
      props
    } = vnode;
    if (!env.isSvg && type === "svg") {
      env = Object.assign({}, env, {
        isSvg: true
      });
    }
    if (!env.isSvg) {
      node = document.createElement(type);
    } else {
      node = document.createElementNS(SVG_NS, type);
    }
    mountAttributes(node, props, env);
    if (props.children) {
      let childEnv = env;
      if (env.isSvg && type === "foreignObject") {
        childEnv = Object.assign({}, childEnv, {
          isSvg: false
        });
      }
      const children = mountChildren(props.children, childEnv);
      if (children != null)
        insertDom(node, children);
    }
    const {
      ref
    } = props;
    if (typeof ref === "function")
      ref(node);
    return node;
  }
  throw new Error("mount: Invalid Vnode!");
}
function mountDom(vnode) {
  return mount(vnode);
}
function hm(...args) {
  return mountDom(h(...args));
}
const memoizedPreloadJS = memoize((url) => {
  document.head.append(hm("link", {
    rel: "preload",
    as: "script",
    href: url
  }));
});
const jsCache = {};
async function loadJSItem(item, context) {
  var _item$data;
  const src = item.type === "script" && ((_item$data = item.data) == null ? void 0 : _item$data.src) || "";
  item.loaded || (item.loaded = jsCache[src]);
  if (!item.loaded) {
    if (item.type === "script") {
      item.loaded = new Promise((resolve, reject) => {
        document.head.append(hm("script", _extends({}, item.data, {
          onLoad: resolve,
          onError: reject
        })));
        if (!src) {
          resolve(void 0);
        }
      }).then(() => {
        item.loaded = true;
      });
      if (src)
        jsCache[src] = item.loaded;
    }
    if (item.type === "iife") {
      const {
        fn,
        getParams
      } = item.data;
      fn(...(getParams == null ? void 0 : getParams(context)) || []);
      item.loaded = true;
    }
  }
  await item.loaded;
}
async function loadJS(items, context) {
  items.forEach((item) => {
    var _item$data2;
    if (item.type === "script" && (_item$data2 = item.data) != null && _item$data2.src) {
      memoizedPreloadJS(item.data.src);
    }
  });
  context = _extends({
    getMarkmap: () => window.markmap
  }, context);
  for (const item of items) {
    await loadJSItem(item, context);
  }
}
function buildJSItem(path) {
  return {
    type: "script",
    data: {
      src: path
    }
  };
}
function buildCSSItem(path) {
  return {
    type: "stylesheet",
    data: {
      href: path
    }
  };
}
export {
  Hook as H,
  UrlBuilder as U,
  persistJS as a,
  buildJSItem as b,
  buildCSSItem as c,
  addClass as d,
  walkTree as e,
  childSelector as f,
  getId as g,
  loadJS as l,
  noop as n,
  persistCSS as p,
  wrapFunction as w
};
