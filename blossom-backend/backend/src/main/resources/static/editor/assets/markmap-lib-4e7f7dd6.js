import { _ as _extends } from "./@babel-d9a14db7.js";
import { R as Remarkable } from "./remarkable-9ef756fc.js";
import { U as UrlBuilder, w as wrapFunction, b as buildJSItem, p as persistCSS, a as persistJS, H as Hook, l as loadJS, c as buildCSSItem, n as noop } from "./markmap-common-e19b15e2.js";
import { r as remarkableKatex } from "./remarkable-katex-9c6f6947.js";
import { j as jsYaml } from "./js-yaml-54b37f1b.js";
/*! markmap-lib v0.15.4 | MIT License */
const template = '<!DOCTYPE html>\n<html>\n<head>\n<meta charset="UTF-8">\n<meta name="viewport" content="width=device-width, initial-scale=1.0">\n<meta http-equiv="X-UA-Compatible" content="ie=edge">\n<title>Markmap</title>\n<style>\n* {\n  margin: 0;\n  padding: 0;\n}\n#mindmap {\n  display: block;\n  width: 100vw;\n  height: 100vh;\n}\n</style>\n<!--CSS-->\n</head>\n<body>\n<svg id="mindmap"></svg>\n<!--JS-->\n</body>\n</html>\n';
const baseJsPaths = [`d3@${"7.8.5"}/dist/d3.min.js`, `markmap-view@${"0.15.4"}/dist/browser/index.js`];
const name$3 = "katex";
const preloadScripts$1 = [`katex@${"0.16.8"}/dist/katex.min.js`].map((path) => buildJSItem(path));
const webfontloader = buildJSItem(`webfontloader@${"1.6.28"}/webfontloader.js`);
webfontloader.data.defer = true;
const styles$1 = [`katex@${"0.16.8"}/dist/katex.min.css`].map((path) => buildCSSItem(path));
const config$1 = {
  versions: {
    katex: "0.16.8",
    webfontloader: "1.6.28"
  },
  preloadScripts: preloadScripts$1,
  scripts: [{
    type: "iife",
    data: {
      fn: (getMarkmap) => {
        window.WebFontConfig = {
          custom: {
            families: ["KaTeX_AMS", "KaTeX_Caligraphic:n4,n7", "KaTeX_Fraktur:n4,n7", "KaTeX_Main:n4,n7,i4,i7", "KaTeX_Math:i4,i7", "KaTeX_Script", "KaTeX_SansSerif:n4,n7,i4", "KaTeX_Size1", "KaTeX_Size2", "KaTeX_Size3", "KaTeX_Size4", "KaTeX_Typewriter"]
          },
          active: () => {
            getMarkmap().refreshHook.call();
          }
        };
      },
      getParams({
        getMarkmap
      }) {
        return [getMarkmap];
      }
    }
  }, webfontloader],
  styles: styles$1
};
function addDefaultVersions(paths, name2, version) {
  return paths.map((path) => {
    if (typeof path === "string" && !path.includes("://")) {
      if (!path.startsWith("npm:")) {
        path = `npm:${path}`;
      }
      const prefixLength = 4 + name2.length;
      if (path.startsWith(`npm:${name2}/`)) {
        path = `${path.slice(0, prefixLength)}@${version}${path.slice(prefixLength)}`;
      }
    }
    return path;
  });
}
function patchJSItem(transformer, item) {
  if (item.type === "script" && item.data.src) {
    return _extends({}, item, {
      data: _extends({}, item.data, {
        src: transformer.urlBuilder.getFullUrl(item.data.src)
      })
    });
  }
  return item;
}
function patchCSSItem(transformer, item) {
  if (item.type === "stylesheet" && item.data.href) {
    return _extends({}, item, {
      data: _extends({}, item.data, {
        href: transformer.urlBuilder.getFullUrl(item.data.href)
      })
    });
  }
  return item;
}
function createTransformHooks(transformer) {
  return {
    transformer,
    parser: new Hook(),
    beforeParse: new Hook(),
    afterParse: new Hook(),
    htmltag: new Hook(),
    retransform: new Hook()
  };
}
function definePlugin(plugin2) {
  return plugin2;
}
const plugin$1 = definePlugin({
  name: name$3,
  config: config$1,
  transform(transformHooks) {
    var _plugin$config, _plugin$config3, _plugin$config4;
    let loading;
    const preloadScripts2 = ((_plugin$config = plugin$1.config) == null || (_plugin$config = _plugin$config.preloadScripts) == null ? void 0 : _plugin$config.map((item) => patchJSItem(transformHooks.transformer, item))) || [];
    const autoload = () => {
      loading || (loading = loadJS(preloadScripts2));
      return loading;
    };
    const renderKatex = (source, displayMode) => {
      const {
        katex
      } = window;
      if (katex) {
        return katex.renderToString(source, {
          displayMode,
          throwOnError: false
        });
      }
      autoload().then(() => {
        transformHooks.retransform.call();
      });
      return source;
    };
    let enableFeature = noop;
    transformHooks.parser.tap((md) => {
      md.use(remarkableKatex);
      md.renderer.rules.katex = (tokens, idx) => {
        enableFeature();
        const result = renderKatex(tokens[idx].content, !!tokens[idx].block);
        return result;
      };
    });
    transformHooks.beforeParse.tap((_, context) => {
      enableFeature = () => {
        context.features[name$3] = true;
      };
    });
    transformHooks.afterParse.tap((_, context) => {
      var _context$frontmatter;
      const markmap = (_context$frontmatter = context.frontmatter) == null ? void 0 : _context$frontmatter.markmap;
      if (markmap) {
        ["extraJs", "extraCss"].forEach((key) => {
          const value = markmap[key];
          if (value) {
            var _plugin$config2;
            markmap[key] = addDefaultVersions(value, name$3, ((_plugin$config2 = plugin$1.config) == null || (_plugin$config2 = _plugin$config2.versions) == null ? void 0 : _plugin$config2.katex) || "");
          }
        });
      }
    });
    return {
      styles: (_plugin$config3 = plugin$1.config) == null ? void 0 : _plugin$config3.styles,
      scripts: (_plugin$config4 = plugin$1.config) == null ? void 0 : _plugin$config4.scripts
    };
  }
});
const name$2 = "frontmatter";
var frontmatter = definePlugin({
  name: name$2,
  transform(transformHooks) {
    transformHooks.beforeParse.tap((md, context) => {
      const {
        content
      } = context;
      if (!content.startsWith("---\n"))
        return;
      const endOffset = content.indexOf("\n---\n");
      if (endOffset < 0)
        return;
      const raw = content.slice(4, endOffset);
      let frontmatter2;
      try {
        var _frontmatter;
        frontmatter2 = jsYaml.load(raw);
        if ((_frontmatter = frontmatter2) != null && _frontmatter.markmap) {
          frontmatter2.markmap = normalizeMarkmapJsonOptions(frontmatter2.markmap);
        }
      } catch (_unused) {
        return;
      }
      context.frontmatter = frontmatter2;
      context.content = content.slice(endOffset + 5);
      context.contentLineOffset = content.slice(0, endOffset).split("\n").length + 1;
    });
    return {};
  }
});
function normalizeMarkmapJsonOptions(options) {
  if (!options)
    return;
  ["color", "extraJs", "extraCss"].forEach((key) => {
    if (options[key] != null)
      options[key] = normalizeStringArray(options[key]);
  });
  ["duration", "maxWidth", "initialExpandLevel"].forEach((key) => {
    if (options[key] != null)
      options[key] = normalizeNumber(options[key]);
  });
  return options;
}
function normalizeStringArray(value) {
  var _result;
  let result;
  if (typeof value === "string")
    result = [value];
  else if (Array.isArray(value))
    result = value.filter((item) => item && typeof item === "string");
  return (_result = result) != null && _result.length ? result : void 0;
}
function normalizeNumber(value) {
  if (isNaN(+value))
    return;
  return +value;
}
const name$1 = "npmUrl";
var npmUrl = definePlugin({
  name: name$1,
  transform(transformHooks) {
    transformHooks.afterParse.tap((_, context) => {
      const {
        frontmatter: frontmatter2
      } = context;
      const markmap = frontmatter2 == null ? void 0 : frontmatter2.markmap;
      if (markmap) {
        ["extraJs", "extraCss"].forEach((key) => {
          const value = markmap[key];
          if (value) {
            markmap[key] = value.map((path) => {
              if (path.startsWith("npm:")) {
                return transformHooks.transformer.urlBuilder.getFullUrl(path.slice(4));
              }
              return path;
            });
          }
        });
      }
    });
    return {};
  }
});
const name = "hljs";
const preloadScripts = [`@highlightjs/cdn-assets@${"11.8.0"}/highlight.min.js`].map((path) => buildJSItem(path));
const styles = [
  `@highlightjs/cdn-assets@${"11.8.0"}/styles/default.min.css`
  // `highlight.js@${"11.8.0"}/styles/default.css`,
].map((path) => buildCSSItem(path));
const config = {
  versions: {
    hljs: "11.8.0"
  },
  preloadScripts,
  styles
};
const plugin = definePlugin({
  name,
  config,
  transform(transformHooks) {
    var _plugin$config, _plugin$config2;
    let loading;
    const preloadScripts2 = ((_plugin$config = plugin.config) == null || (_plugin$config = _plugin$config.preloadScripts) == null ? void 0 : _plugin$config.map((item) => patchJSItem(transformHooks.transformer, item))) || [];
    const autoload = () => {
      loading || (loading = loadJS(preloadScripts2));
      return loading;
    };
    let enableFeature = noop;
    transformHooks.parser.tap((md) => {
      md.set({
        highlight: (str, language) => {
          enableFeature();
          const {
            hljs
          } = window;
          if (hljs) {
            return hljs.highlightAuto(str, language ? [language] : void 0).value;
          }
          autoload().then(() => {
            transformHooks.retransform.call();
          });
          return str;
        }
      });
    });
    transformHooks.beforeParse.tap((_, context) => {
      enableFeature = () => {
        context.features[name] = true;
      };
    });
    return {
      styles: (_plugin$config2 = plugin.config) == null ? void 0 : _plugin$config2.styles
    };
  }
});
const plugins = [frontmatter, plugin$1, plugin, npmUrl];
function cleanNode(node) {
  if (node.type === "heading") {
    node.children = node.children.filter((item) => item.type !== "paragraph");
  } else if (node.type === "list_item") {
    var _node$payload;
    node.children = node.children.filter((item) => {
      if (["paragraph", "fence"].includes(item.type)) {
        if (!node.content) {
          node.content = item.content;
          node.payload = _extends({}, node.payload, item.payload);
        }
        return false;
      }
      return true;
    });
    if (((_node$payload = node.payload) == null ? void 0 : _node$payload.index) != null) {
      node.content = `${node.payload.index}. ${node.content}`;
    }
  } else if (node.type === "ordered_list") {
    var _node$payload$startIn, _node$payload2;
    let index = (_node$payload$startIn = (_node$payload2 = node.payload) == null ? void 0 : _node$payload2.startIndex) != null ? _node$payload$startIn : 1;
    node.children.forEach((item) => {
      if (item.type === "list_item") {
        item.payload = _extends({}, item.payload, {
          index
        });
        index += 1;
      }
    });
  }
  if (node.children.length > 0) {
    node.children.forEach((child) => cleanNode(child));
    if (node.children.length === 1 && !node.children[0].content) {
      node.children = node.children[0].children;
    }
  }
}
function resetDepth(node, depth = 0) {
  node.depth = depth;
  node.children.forEach((child) => {
    resetDepth(child, depth + 1);
  });
}
class Transformer {
  constructor(plugins$1 = plugins) {
    this.assetsMap = {};
    this.urlBuilder = new UrlBuilder();
    this.hooks = createTransformHooks(this);
    this.plugins = plugins$1.map((plugin2) => typeof plugin2 === "function" ? plugin2() : plugin2);
    const assetsMap = {};
    for (const {
      name: name2,
      transform
    } of this.plugins) {
      assetsMap[name2] = transform(this.hooks);
    }
    this.assetsMap = assetsMap;
    const md = new Remarkable("full", {
      html: true,
      breaks: true,
      maxNesting: Infinity
    });
    md.renderer.rules.htmltag = wrapFunction(md.renderer.rules.htmltag, (render, ...args) => {
      const result = render(...args);
      this.hooks.htmltag.call({
        args,
        result
      });
      return result;
    });
    this.md = md;
    this.hooks.parser.call(md);
  }
  buildTree(tokens) {
    const {
      md
    } = this;
    const root = {
      type: "root",
      depth: 0,
      content: "",
      children: [],
      payload: {}
    };
    const stack = [root];
    let depth = 0;
    for (const token of tokens) {
      const payload = {};
      if (token.lines) {
        payload.lines = token.lines;
      }
      let current = stack[stack.length - 1];
      if (token.type.endsWith("_open")) {
        const type = token.type.slice(0, -5);
        if (type === "heading") {
          depth = token.hLevel;
          while (((_current = current) == null ? void 0 : _current.depth) >= depth) {
            var _current;
            stack.pop();
            current = stack[stack.length - 1];
          }
        } else {
          var _current2;
          depth = Math.max(depth, ((_current2 = current) == null ? void 0 : _current2.depth) || 0) + 1;
          if (type === "ordered_list") {
            payload.startIndex = token.order;
          }
        }
        const item = {
          type,
          depth,
          payload,
          content: "",
          children: []
        };
        current.children.push(item);
        stack.push(item);
      } else if (!current) {
        continue;
      } else if (token.type === `${current.type}_close`) {
        if (current.type === "heading") {
          depth = current.depth;
        } else {
          stack.pop();
          depth = 0;
        }
      } else if (token.type === "inline") {
        const revoke = this.hooks.htmltag.tap((ctx) => {
          var _ctx$result;
          const comment = (_ctx$result = ctx.result) == null ? void 0 : _ctx$result.match(/^<!--([\s\S]*?)-->$/);
          const data = comment == null ? void 0 : comment[1].trim().split(" ");
          if ((data == null ? void 0 : data[0]) === "fold") {
            current.payload = _extends({}, current.payload, {
              fold: ["all", "recursively"].includes(data[1]) ? 2 : 1
            });
            ctx.result = "";
          }
        });
        const text = md.renderer.render([token], md.options, {});
        revoke();
        current.content = `${current.content || ""}${text}`;
      } else if (token.type === "fence") {
        const result = md.renderer.render([token], md.options, {});
        current.children.push({
          type: token.type,
          depth: depth + 1,
          content: result,
          children: [],
          payload
        });
      } else
        ;
    }
    return root;
  }
  transform(content) {
    var _root$children;
    const context = {
      content,
      features: {},
      contentLineOffset: 0
    };
    this.hooks.beforeParse.call(this.md, context);
    const tokens = this.md.parse(context.content, {});
    this.hooks.afterParse.call(this.md, context);
    let root = this.buildTree(tokens);
    cleanNode(root);
    if (((_root$children = root.children) == null ? void 0 : _root$children.length) === 1)
      root = root.children[0];
    resetDepth(root);
    return _extends({}, context, {
      root
    });
  }
  /**
   * Get all assets from enabled plugins or filter them by plugin names as keys.
   */
  getAssets(keys) {
    var _keys;
    const styles2 = [];
    const scripts = [];
    (_keys = keys) != null ? _keys : keys = this.plugins.map((plugin2) => plugin2.name);
    for (const assets of keys.map((key) => this.assetsMap[key])) {
      if (assets) {
        if (assets.styles)
          styles2.push(...assets.styles);
        if (assets.scripts)
          scripts.push(...assets.scripts);
      }
    }
    return {
      styles: styles2.map((item) => patchCSSItem(this, item)),
      scripts: scripts.map((item) => patchJSItem(this, item))
    };
  }
  /**
   * Get used assets by features object returned by `transform`.
   */
  getUsedAssets(features) {
    const keys = this.plugins.map((plugin2) => plugin2.name).filter((name2) => features[name2]);
    return this.getAssets(keys);
  }
  fillTemplate(root, assets, extra) {
    var _extra, _extra$baseJs;
    extra = _extends({}, extra);
    (_extra$baseJs = (_extra = extra).baseJs) != null ? _extra$baseJs : _extra.baseJs = baseJsPaths.map((path) => this.urlBuilder.getFullUrl(path)).map((path) => buildJSItem(path));
    const {
      scripts,
      styles: styles2
    } = assets;
    const cssList = [...styles2 ? persistCSS(styles2) : []];
    const context = {
      getMarkmap: () => window.markmap,
      getOptions: extra.getOptions,
      jsonOptions: extra.jsonOptions,
      root
    };
    const jsList = [...persistJS([...extra.baseJs, ...scripts || [], {
      type: "iife",
      data: {
        fn: (getMarkmap, getOptions, root2, jsonOptions) => {
          const markmap = getMarkmap();
          window.mm = markmap.Markmap.create("svg#mindmap", (getOptions || markmap.deriveOptions)(jsonOptions), root2);
        },
        getParams: ({
          getMarkmap,
          getOptions,
          root: root2,
          jsonOptions
        }) => {
          return [getMarkmap, getOptions, root2, jsonOptions];
        }
      }
    }], context)];
    const html = template.replace("<!--CSS-->", () => cssList.join("")).replace("<!--JS-->", () => jsList.join(""));
    return html;
  }
}
export {
  Transformer as T
};
