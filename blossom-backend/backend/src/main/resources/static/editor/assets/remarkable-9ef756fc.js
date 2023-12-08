var textarea;
function decodeEntity(name) {
  textarea = textarea || document.createElement("textarea");
  textarea.innerHTML = "&" + name + ";";
  return textarea.value;
}
var hasOwn = Object.prototype.hasOwnProperty;
function has(object, key) {
  return object ? hasOwn.call(object, key) : false;
}
function assign(obj) {
  var sources = [].slice.call(arguments, 1);
  sources.forEach(function(source) {
    if (!source) {
      return;
    }
    if (typeof source !== "object") {
      throw new TypeError(source + "must be object");
    }
    Object.keys(source).forEach(function(key) {
      obj[key] = source[key];
    });
  });
  return obj;
}
var UNESCAPE_MD_RE = /\\([\\!"#$%&'()*+,.\/:;<=>?@[\]^_`{|}~-])/g;
function unescapeMd(str) {
  if (str.indexOf("\\") < 0) {
    return str;
  }
  return str.replace(UNESCAPE_MD_RE, "$1");
}
function isValidEntityCode(c) {
  if (c >= 55296 && c <= 57343) {
    return false;
  }
  if (c >= 64976 && c <= 65007) {
    return false;
  }
  if ((c & 65535) === 65535 || (c & 65535) === 65534) {
    return false;
  }
  if (c >= 0 && c <= 8) {
    return false;
  }
  if (c === 11) {
    return false;
  }
  if (c >= 14 && c <= 31) {
    return false;
  }
  if (c >= 127 && c <= 159) {
    return false;
  }
  if (c > 1114111) {
    return false;
  }
  return true;
}
function fromCodePoint(c) {
  if (c > 65535) {
    c -= 65536;
    var surrogate1 = 55296 + (c >> 10), surrogate2 = 56320 + (c & 1023);
    return String.fromCharCode(surrogate1, surrogate2);
  }
  return String.fromCharCode(c);
}
var NAMED_ENTITY_RE = /&([a-z#][a-z0-9]{1,31});/gi;
var DIGITAL_ENTITY_TEST_RE = /^#((?:x[a-f0-9]{1,8}|[0-9]{1,8}))/i;
function replaceEntityPattern(match, name) {
  var code2 = 0;
  var decoded = decodeEntity(name);
  if (name !== decoded) {
    return decoded;
  } else if (name.charCodeAt(0) === 35 && DIGITAL_ENTITY_TEST_RE.test(name)) {
    code2 = name[1].toLowerCase() === "x" ? parseInt(name.slice(2), 16) : parseInt(name.slice(1), 10);
    if (isValidEntityCode(code2)) {
      return fromCodePoint(code2);
    }
  }
  return match;
}
function replaceEntities(str) {
  if (str.indexOf("&") < 0) {
    return str;
  }
  return str.replace(NAMED_ENTITY_RE, replaceEntityPattern);
}
var HTML_ESCAPE_TEST_RE = /[&<>"]/;
var HTML_ESCAPE_REPLACE_RE = /[&<>"]/g;
var HTML_REPLACEMENTS = {
  "&": "&amp;",
  "<": "&lt;",
  ">": "&gt;",
  '"': "&quot;"
};
function replaceUnsafeChar(ch) {
  return HTML_REPLACEMENTS[ch];
}
function escapeHtml(str) {
  if (HTML_ESCAPE_TEST_RE.test(str)) {
    return str.replace(HTML_ESCAPE_REPLACE_RE, replaceUnsafeChar);
  }
  return str;
}
var rules = {};
rules.blockquote_open = function() {
  return "<blockquote>\n";
};
rules.blockquote_close = function(tokens, idx) {
  return "</blockquote>" + getBreak(tokens, idx);
};
rules.code = function(tokens, idx) {
  if (tokens[idx].block) {
    return "<pre><code>" + escapeHtml(tokens[idx].content) + "</code></pre>" + getBreak(tokens, idx);
  }
  return "<code>" + escapeHtml(tokens[idx].content) + "</code>";
};
rules.fence = function(tokens, idx, options, env, instance) {
  var token = tokens[idx];
  var langClass = "";
  var langPrefix = options.langPrefix;
  var langName = "", fences2, fenceName;
  var highlighted;
  if (token.params) {
    fences2 = token.params.split(/\s+/g);
    fenceName = fences2.join(" ");
    if (has(instance.rules.fence_custom, fences2[0])) {
      return instance.rules.fence_custom[fences2[0]](tokens, idx, options, env, instance);
    }
    langName = escapeHtml(replaceEntities(unescapeMd(fenceName)));
    langClass = ' class="' + langPrefix + langName + '"';
  }
  if (options.highlight) {
    highlighted = options.highlight.apply(options.highlight, [token.content].concat(fences2)) || escapeHtml(token.content);
  } else {
    highlighted = escapeHtml(token.content);
  }
  return "<pre><code" + langClass + ">" + highlighted + "</code></pre>" + getBreak(tokens, idx);
};
rules.fence_custom = {};
rules.heading_open = function(tokens, idx) {
  return "<h" + tokens[idx].hLevel + ">";
};
rules.heading_close = function(tokens, idx) {
  return "</h" + tokens[idx].hLevel + ">\n";
};
rules.hr = function(tokens, idx, options) {
  return (options.xhtmlOut ? "<hr />" : "<hr>") + getBreak(tokens, idx);
};
rules.bullet_list_open = function() {
  return "<ul>\n";
};
rules.bullet_list_close = function(tokens, idx) {
  return "</ul>" + getBreak(tokens, idx);
};
rules.list_item_open = function() {
  return "<li>";
};
rules.list_item_close = function() {
  return "</li>\n";
};
rules.ordered_list_open = function(tokens, idx) {
  var token = tokens[idx];
  var order = token.order > 1 ? ' start="' + token.order + '"' : "";
  return "<ol" + order + ">\n";
};
rules.ordered_list_close = function(tokens, idx) {
  return "</ol>" + getBreak(tokens, idx);
};
rules.paragraph_open = function(tokens, idx) {
  return tokens[idx].tight ? "" : "<p>";
};
rules.paragraph_close = function(tokens, idx) {
  var addBreak = !(tokens[idx].tight && idx && tokens[idx - 1].type === "inline" && !tokens[idx - 1].content);
  return (tokens[idx].tight ? "" : "</p>") + (addBreak ? getBreak(tokens, idx) : "");
};
rules.link_open = function(tokens, idx, options) {
  var title = tokens[idx].title ? ' title="' + escapeHtml(replaceEntities(tokens[idx].title)) + '"' : "";
  var target = options.linkTarget ? ' target="' + options.linkTarget + '"' : "";
  return '<a href="' + escapeHtml(tokens[idx].href) + '"' + title + target + ">";
};
rules.link_close = function() {
  return "</a>";
};
rules.image = function(tokens, idx, options) {
  var src = ' src="' + escapeHtml(tokens[idx].src) + '"';
  var title = tokens[idx].title ? ' title="' + escapeHtml(replaceEntities(tokens[idx].title)) + '"' : "";
  var alt = ' alt="' + (tokens[idx].alt ? escapeHtml(replaceEntities(unescapeMd(tokens[idx].alt))) : "") + '"';
  var suffix = options.xhtmlOut ? " /" : "";
  return "<img" + src + alt + title + suffix + ">";
};
rules.table_open = function() {
  return "<table>\n";
};
rules.table_close = function() {
  return "</table>\n";
};
rules.thead_open = function() {
  return "<thead>\n";
};
rules.thead_close = function() {
  return "</thead>\n";
};
rules.tbody_open = function() {
  return "<tbody>\n";
};
rules.tbody_close = function() {
  return "</tbody>\n";
};
rules.tr_open = function() {
  return "<tr>";
};
rules.tr_close = function() {
  return "</tr>\n";
};
rules.th_open = function(tokens, idx) {
  var token = tokens[idx];
  return "<th" + (token.align ? ' style="text-align:' + token.align + '"' : "") + ">";
};
rules.th_close = function() {
  return "</th>";
};
rules.td_open = function(tokens, idx) {
  var token = tokens[idx];
  return "<td" + (token.align ? ' style="text-align:' + token.align + '"' : "") + ">";
};
rules.td_close = function() {
  return "</td>";
};
rules.strong_open = function() {
  return "<strong>";
};
rules.strong_close = function() {
  return "</strong>";
};
rules.em_open = function() {
  return "<em>";
};
rules.em_close = function() {
  return "</em>";
};
rules.del_open = function() {
  return "<del>";
};
rules.del_close = function() {
  return "</del>";
};
rules.ins_open = function() {
  return "<ins>";
};
rules.ins_close = function() {
  return "</ins>";
};
rules.mark_open = function() {
  return "<mark>";
};
rules.mark_close = function() {
  return "</mark>";
};
rules.sub = function(tokens, idx) {
  return "<sub>" + escapeHtml(tokens[idx].content) + "</sub>";
};
rules.sup = function(tokens, idx) {
  return "<sup>" + escapeHtml(tokens[idx].content) + "</sup>";
};
rules.hardbreak = function(tokens, idx, options) {
  return options.xhtmlOut ? "<br />\n" : "<br>\n";
};
rules.softbreak = function(tokens, idx, options) {
  return options.breaks ? options.xhtmlOut ? "<br />\n" : "<br>\n" : "\n";
};
rules.text = function(tokens, idx) {
  return escapeHtml(tokens[idx].content);
};
rules.htmlblock = function(tokens, idx) {
  return tokens[idx].content;
};
rules.htmltag = function(tokens, idx) {
  return tokens[idx].content;
};
rules.abbr_open = function(tokens, idx) {
  return '<abbr title="' + escapeHtml(replaceEntities(tokens[idx].title)) + '">';
};
rules.abbr_close = function() {
  return "</abbr>";
};
rules.footnote_ref = function(tokens, idx) {
  var n = Number(tokens[idx].id + 1).toString();
  var id = "fnref" + n;
  if (tokens[idx].subId > 0) {
    id += ":" + tokens[idx].subId;
  }
  return '<sup class="footnote-ref"><a href="#fn' + n + '" id="' + id + '">[' + n + "]</a></sup>";
};
rules.footnote_block_open = function(tokens, idx, options) {
  var hr2 = options.xhtmlOut ? '<hr class="footnotes-sep" />\n' : '<hr class="footnotes-sep">\n';
  return hr2 + '<section class="footnotes">\n<ol class="footnotes-list">\n';
};
rules.footnote_block_close = function() {
  return "</ol>\n</section>\n";
};
rules.footnote_open = function(tokens, idx) {
  var id = Number(tokens[idx].id + 1).toString();
  return '<li id="fn' + id + '"  class="footnote-item">';
};
rules.footnote_close = function() {
  return "</li>\n";
};
rules.footnote_anchor = function(tokens, idx) {
  var n = Number(tokens[idx].id + 1).toString();
  var id = "fnref" + n;
  if (tokens[idx].subId > 0) {
    id += ":" + tokens[idx].subId;
  }
  return ' <a href="#' + id + '" class="footnote-backref">↩</a>';
};
rules.dl_open = function() {
  return "<dl>\n";
};
rules.dt_open = function() {
  return "<dt>";
};
rules.dd_open = function() {
  return "<dd>";
};
rules.dl_close = function() {
  return "</dl>\n";
};
rules.dt_close = function() {
  return "</dt>\n";
};
rules.dd_close = function() {
  return "</dd>\n";
};
function nextToken(tokens, idx) {
  if (++idx >= tokens.length - 2) {
    return idx;
  }
  if (tokens[idx].type === "paragraph_open" && tokens[idx].tight && (tokens[idx + 1].type === "inline" && tokens[idx + 1].content.length === 0) && (tokens[idx + 2].type === "paragraph_close" && tokens[idx + 2].tight)) {
    return nextToken(tokens, idx + 2);
  }
  return idx;
}
var getBreak = rules.getBreak = function getBreak2(tokens, idx) {
  idx = nextToken(tokens, idx);
  if (idx < tokens.length && tokens[idx].type === "list_item_close") {
    return "";
  }
  return "\n";
};
function Renderer() {
  this.rules = assign({}, rules);
  this.getBreak = rules.getBreak;
}
Renderer.prototype.renderInline = function(tokens, options, env) {
  var _rules2 = this.rules;
  var len = tokens.length, i = 0;
  var result = "";
  while (len--) {
    result += _rules2[tokens[i].type](tokens, i++, options, env, this);
  }
  return result;
};
Renderer.prototype.render = function(tokens, options, env) {
  var _rules2 = this.rules;
  var len = tokens.length, i = -1;
  var result = "";
  while (++i < len) {
    if (tokens[i].type === "inline") {
      result += this.renderInline(tokens[i].children, options, env);
    } else {
      result += _rules2[tokens[i].type](tokens, i, options, env, this);
    }
  }
  return result;
};
function Ruler() {
  this.__rules__ = [];
  this.__cache__ = null;
}
Ruler.prototype.__find__ = function(name) {
  var len = this.__rules__.length;
  var i = -1;
  while (len--) {
    if (this.__rules__[++i].name === name) {
      return i;
    }
  }
  return -1;
};
Ruler.prototype.__compile__ = function() {
  var self = this;
  var chains = [""];
  self.__rules__.forEach(function(rule) {
    if (!rule.enabled) {
      return;
    }
    rule.alt.forEach(function(altName) {
      if (chains.indexOf(altName) < 0) {
        chains.push(altName);
      }
    });
  });
  self.__cache__ = {};
  chains.forEach(function(chain) {
    self.__cache__[chain] = [];
    self.__rules__.forEach(function(rule) {
      if (!rule.enabled) {
        return;
      }
      if (chain && rule.alt.indexOf(chain) < 0) {
        return;
      }
      self.__cache__[chain].push(rule.fn);
    });
  });
};
Ruler.prototype.at = function(name, fn, options) {
  var idx = this.__find__(name);
  var opt = options || {};
  if (idx === -1) {
    throw new Error("Parser rule not found: " + name);
  }
  this.__rules__[idx].fn = fn;
  this.__rules__[idx].alt = opt.alt || [];
  this.__cache__ = null;
};
Ruler.prototype.before = function(beforeName, ruleName, fn, options) {
  var idx = this.__find__(beforeName);
  var opt = options || {};
  if (idx === -1) {
    throw new Error("Parser rule not found: " + beforeName);
  }
  this.__rules__.splice(idx, 0, {
    name: ruleName,
    enabled: true,
    fn,
    alt: opt.alt || []
  });
  this.__cache__ = null;
};
Ruler.prototype.after = function(afterName, ruleName, fn, options) {
  var idx = this.__find__(afterName);
  var opt = options || {};
  if (idx === -1) {
    throw new Error("Parser rule not found: " + afterName);
  }
  this.__rules__.splice(idx + 1, 0, {
    name: ruleName,
    enabled: true,
    fn,
    alt: opt.alt || []
  });
  this.__cache__ = null;
};
Ruler.prototype.push = function(ruleName, fn, options) {
  var opt = options || {};
  this.__rules__.push({
    name: ruleName,
    enabled: true,
    fn,
    alt: opt.alt || []
  });
  this.__cache__ = null;
};
Ruler.prototype.enable = function(list2, strict) {
  list2 = !Array.isArray(list2) ? [list2] : list2;
  if (strict) {
    this.__rules__.forEach(function(rule) {
      rule.enabled = false;
    });
  }
  list2.forEach(function(name) {
    var idx = this.__find__(name);
    if (idx < 0) {
      throw new Error("Rules manager: invalid rule name " + name);
    }
    this.__rules__[idx].enabled = true;
  }, this);
  this.__cache__ = null;
};
Ruler.prototype.disable = function(list2) {
  list2 = !Array.isArray(list2) ? [list2] : list2;
  list2.forEach(function(name) {
    var idx = this.__find__(name);
    if (idx < 0) {
      throw new Error("Rules manager: invalid rule name " + name);
    }
    this.__rules__[idx].enabled = false;
  }, this);
  this.__cache__ = null;
};
Ruler.prototype.getRules = function(chainName) {
  if (this.__cache__ === null) {
    this.__compile__();
  }
  return this.__cache__[chainName] || [];
};
function block(state) {
  if (state.inlineMode) {
    state.tokens.push({
      type: "inline",
      content: state.src.replace(/\n/g, " ").trim(),
      level: 0,
      lines: [0, 1],
      children: []
    });
  } else {
    state.block.parse(state.src, state.options, state.env, state.tokens);
  }
}
function StateInline(src, parserInline, options, env, outTokens) {
  this.src = src;
  this.env = env;
  this.options = options;
  this.parser = parserInline;
  this.tokens = outTokens;
  this.pos = 0;
  this.posMax = this.src.length;
  this.level = 0;
  this.pending = "";
  this.pendingLevel = 0;
  this.cache = [];
  this.isInLabel = false;
  this.linkLevel = 0;
  this.linkContent = "";
  this.labelUnmatchedScopes = 0;
}
StateInline.prototype.pushPending = function() {
  this.tokens.push({
    type: "text",
    content: this.pending,
    level: this.pendingLevel
  });
  this.pending = "";
};
StateInline.prototype.push = function(token) {
  if (this.pending) {
    this.pushPending();
  }
  this.tokens.push(token);
  this.pendingLevel = this.level;
};
StateInline.prototype.cacheSet = function(key, val) {
  for (var i = this.cache.length; i <= key; i++) {
    this.cache.push(0);
  }
  this.cache[key] = val;
};
StateInline.prototype.cacheGet = function(key) {
  return key < this.cache.length ? this.cache[key] : 0;
};
function parseLinkLabel(state, start) {
  var level, found, marker, labelEnd = -1, max = state.posMax, oldPos = state.pos, oldFlag = state.isInLabel;
  if (state.isInLabel) {
    return -1;
  }
  if (state.labelUnmatchedScopes) {
    state.labelUnmatchedScopes--;
    return -1;
  }
  state.pos = start + 1;
  state.isInLabel = true;
  level = 1;
  while (state.pos < max) {
    marker = state.src.charCodeAt(state.pos);
    if (marker === 91) {
      level++;
    } else if (marker === 93) {
      level--;
      if (level === 0) {
        found = true;
        break;
      }
    }
    state.parser.skipToken(state);
  }
  if (found) {
    labelEnd = state.pos;
    state.labelUnmatchedScopes = 0;
  } else {
    state.labelUnmatchedScopes = level - 1;
  }
  state.pos = oldPos;
  state.isInLabel = oldFlag;
  return labelEnd;
}
function parseAbbr(str, parserInline, options, env) {
  var state, labelEnd, pos, max, label, title;
  if (str.charCodeAt(0) !== 42) {
    return -1;
  }
  if (str.charCodeAt(1) !== 91) {
    return -1;
  }
  if (str.indexOf("]:") === -1) {
    return -1;
  }
  state = new StateInline(str, parserInline, options, env, []);
  labelEnd = parseLinkLabel(state, 1);
  if (labelEnd < 0 || str.charCodeAt(labelEnd + 1) !== 58) {
    return -1;
  }
  max = state.posMax;
  for (pos = labelEnd + 2; pos < max; pos++) {
    if (state.src.charCodeAt(pos) === 10) {
      break;
    }
  }
  label = str.slice(2, labelEnd);
  title = str.slice(labelEnd + 2, pos).trim();
  if (title.length === 0) {
    return -1;
  }
  if (!env.abbreviations) {
    env.abbreviations = {};
  }
  if (typeof env.abbreviations[":" + label] === "undefined") {
    env.abbreviations[":" + label] = title;
  }
  return pos;
}
function abbr(state) {
  var tokens = state.tokens, i, l, content, pos;
  if (state.inlineMode) {
    return;
  }
  for (i = 1, l = tokens.length - 1; i < l; i++) {
    if (tokens[i - 1].type === "paragraph_open" && tokens[i].type === "inline" && tokens[i + 1].type === "paragraph_close") {
      content = tokens[i].content;
      while (content.length) {
        pos = parseAbbr(content, state.inline, state.options, state.env);
        if (pos < 0) {
          break;
        }
        content = content.slice(pos).trim();
      }
      tokens[i].content = content;
      if (!content.length) {
        tokens[i - 1].tight = true;
        tokens[i + 1].tight = true;
      }
    }
  }
}
function normalizeLink(url) {
  var normalized = replaceEntities(url);
  try {
    normalized = decodeURI(normalized);
  } catch (err) {
  }
  return encodeURI(normalized);
}
function parseLinkDestination(state, pos) {
  var code2, level, link, start = pos, max = state.posMax;
  if (state.src.charCodeAt(pos) === 60) {
    pos++;
    while (pos < max) {
      code2 = state.src.charCodeAt(pos);
      if (code2 === 10) {
        return false;
      }
      if (code2 === 62) {
        link = normalizeLink(unescapeMd(state.src.slice(start + 1, pos)));
        if (!state.parser.validateLink(link)) {
          return false;
        }
        state.pos = pos + 1;
        state.linkContent = link;
        return true;
      }
      if (code2 === 92 && pos + 1 < max) {
        pos += 2;
        continue;
      }
      pos++;
    }
    return false;
  }
  level = 0;
  while (pos < max) {
    code2 = state.src.charCodeAt(pos);
    if (code2 === 32) {
      break;
    }
    if (code2 < 32 || code2 === 127) {
      break;
    }
    if (code2 === 92 && pos + 1 < max) {
      pos += 2;
      continue;
    }
    if (code2 === 40) {
      level++;
      if (level > 1) {
        break;
      }
    }
    if (code2 === 41) {
      level--;
      if (level < 0) {
        break;
      }
    }
    pos++;
  }
  if (start === pos) {
    return false;
  }
  link = unescapeMd(state.src.slice(start, pos));
  if (!state.parser.validateLink(link)) {
    return false;
  }
  state.linkContent = link;
  state.pos = pos;
  return true;
}
function parseLinkTitle(state, pos) {
  var code2, start = pos, max = state.posMax, marker = state.src.charCodeAt(pos);
  if (marker !== 34 && marker !== 39 && marker !== 40) {
    return false;
  }
  pos++;
  if (marker === 40) {
    marker = 41;
  }
  while (pos < max) {
    code2 = state.src.charCodeAt(pos);
    if (code2 === marker) {
      state.pos = pos + 1;
      state.linkContent = unescapeMd(state.src.slice(start + 1, pos));
      return true;
    }
    if (code2 === 92 && pos + 1 < max) {
      pos += 2;
      continue;
    }
    pos++;
  }
  return false;
}
function normalizeReference(str) {
  return str.trim().replace(/\s+/g, " ").toUpperCase();
}
function parseReference(str, parser, options, env) {
  var state, labelEnd, pos, max, code2, start, href, title, label;
  if (str.charCodeAt(0) !== 91) {
    return -1;
  }
  if (str.indexOf("]:") === -1) {
    return -1;
  }
  state = new StateInline(str, parser, options, env, []);
  labelEnd = parseLinkLabel(state, 0);
  if (labelEnd < 0 || str.charCodeAt(labelEnd + 1) !== 58) {
    return -1;
  }
  max = state.posMax;
  for (pos = labelEnd + 2; pos < max; pos++) {
    code2 = state.src.charCodeAt(pos);
    if (code2 !== 32 && code2 !== 10) {
      break;
    }
  }
  if (!parseLinkDestination(state, pos)) {
    return -1;
  }
  href = state.linkContent;
  pos = state.pos;
  start = pos;
  for (pos = pos + 1; pos < max; pos++) {
    code2 = state.src.charCodeAt(pos);
    if (code2 !== 32 && code2 !== 10) {
      break;
    }
  }
  if (pos < max && start !== pos && parseLinkTitle(state, pos)) {
    title = state.linkContent;
    pos = state.pos;
  } else {
    title = "";
    pos = start;
  }
  while (pos < max && state.src.charCodeAt(pos) === 32) {
    pos++;
  }
  if (pos < max && state.src.charCodeAt(pos) !== 10) {
    return -1;
  }
  label = normalizeReference(str.slice(1, labelEnd));
  if (typeof env.references[label] === "undefined") {
    env.references[label] = { title, href };
  }
  return pos;
}
function references(state) {
  var tokens = state.tokens, i, l, content, pos;
  state.env.references = state.env.references || {};
  if (state.inlineMode) {
    return;
  }
  for (i = 1, l = tokens.length - 1; i < l; i++) {
    if (tokens[i].type === "inline" && tokens[i - 1].type === "paragraph_open" && tokens[i + 1].type === "paragraph_close") {
      content = tokens[i].content;
      while (content.length) {
        pos = parseReference(content, state.inline, state.options, state.env);
        if (pos < 0) {
          break;
        }
        content = content.slice(pos).trim();
      }
      tokens[i].content = content;
      if (!content.length) {
        tokens[i - 1].tight = true;
        tokens[i + 1].tight = true;
      }
    }
  }
}
function inline(state) {
  var tokens = state.tokens, tok, i, l;
  for (i = 0, l = tokens.length; i < l; i++) {
    tok = tokens[i];
    if (tok.type === "inline") {
      state.inline.parse(tok.content, state.options, state.env, tok.children);
    }
  }
}
function footnote_block(state) {
  var i, l, j, t, lastParagraph, list2, tokens, current, currentLabel, level = 0, insideRef = false, refTokens = {};
  if (!state.env.footnotes) {
    return;
  }
  state.tokens = state.tokens.filter(function(tok) {
    if (tok.type === "footnote_reference_open") {
      insideRef = true;
      current = [];
      currentLabel = tok.label;
      return false;
    }
    if (tok.type === "footnote_reference_close") {
      insideRef = false;
      refTokens[":" + currentLabel] = current;
      return false;
    }
    if (insideRef) {
      current.push(tok);
    }
    return !insideRef;
  });
  if (!state.env.footnotes.list) {
    return;
  }
  list2 = state.env.footnotes.list;
  state.tokens.push({
    type: "footnote_block_open",
    level: level++
  });
  for (i = 0, l = list2.length; i < l; i++) {
    state.tokens.push({
      type: "footnote_open",
      id: i,
      level: level++
    });
    if (list2[i].tokens) {
      tokens = [];
      tokens.push({
        type: "paragraph_open",
        tight: false,
        level: level++
      });
      tokens.push({
        type: "inline",
        content: "",
        level,
        children: list2[i].tokens
      });
      tokens.push({
        type: "paragraph_close",
        tight: false,
        level: --level
      });
    } else if (list2[i].label) {
      tokens = refTokens[":" + list2[i].label];
    }
    state.tokens = state.tokens.concat(tokens);
    if (state.tokens[state.tokens.length - 1].type === "paragraph_close") {
      lastParagraph = state.tokens.pop();
    } else {
      lastParagraph = null;
    }
    t = list2[i].count > 0 ? list2[i].count : 1;
    for (j = 0; j < t; j++) {
      state.tokens.push({
        type: "footnote_anchor",
        id: i,
        subId: j,
        level
      });
    }
    if (lastParagraph) {
      state.tokens.push(lastParagraph);
    }
    state.tokens.push({
      type: "footnote_close",
      level: --level
    });
  }
  state.tokens.push({
    type: "footnote_block_close",
    level: --level
  });
}
var PUNCT_CHARS = ` 
()[]'".,!?-`;
function regEscape(s) {
  return s.replace(/([-()\[\]{}+?*.$\^|,:#<!\\])/g, "\\$1");
}
function abbr2(state) {
  var i, j, l, tokens, token, text2, nodes, pos, level, reg, m, regText, blockTokens = state.tokens;
  if (!state.env.abbreviations) {
    return;
  }
  if (!state.env.abbrRegExp) {
    regText = "(^|[" + PUNCT_CHARS.split("").map(regEscape).join("") + "])(" + Object.keys(state.env.abbreviations).map(function(x) {
      return x.substr(1);
    }).sort(function(a, b) {
      return b.length - a.length;
    }).map(regEscape).join("|") + ")($|[" + PUNCT_CHARS.split("").map(regEscape).join("") + "])";
    state.env.abbrRegExp = new RegExp(regText, "g");
  }
  reg = state.env.abbrRegExp;
  for (j = 0, l = blockTokens.length; j < l; j++) {
    if (blockTokens[j].type !== "inline") {
      continue;
    }
    tokens = blockTokens[j].children;
    for (i = tokens.length - 1; i >= 0; i--) {
      token = tokens[i];
      if (token.type !== "text") {
        continue;
      }
      pos = 0;
      text2 = token.content;
      reg.lastIndex = 0;
      level = token.level;
      nodes = [];
      while (m = reg.exec(text2)) {
        if (reg.lastIndex > pos) {
          nodes.push({
            type: "text",
            content: text2.slice(pos, m.index + m[1].length),
            level
          });
        }
        nodes.push({
          type: "abbr_open",
          title: state.env.abbreviations[":" + m[2]],
          level: level++
        });
        nodes.push({
          type: "text",
          content: m[2],
          level
        });
        nodes.push({
          type: "abbr_close",
          level: --level
        });
        pos = reg.lastIndex - m[3].length;
      }
      if (!nodes.length) {
        continue;
      }
      if (pos < text2.length) {
        nodes.push({
          type: "text",
          content: text2.slice(pos),
          level
        });
      }
      blockTokens[j].children = tokens = [].concat(tokens.slice(0, i), nodes, tokens.slice(i + 1));
    }
  }
}
var RARE_RE = /\+-|\.\.|\?\?\?\?|!!!!|,,|--/;
var SCOPED_ABBR_RE = /\((c|tm|r|p)\)/ig;
var SCOPED_ABBR = {
  "c": "©",
  "r": "®",
  "p": "§",
  "tm": "™"
};
function replaceScopedAbbr(str) {
  if (str.indexOf("(") < 0) {
    return str;
  }
  return str.replace(SCOPED_ABBR_RE, function(match, name) {
    return SCOPED_ABBR[name.toLowerCase()];
  });
}
function replace(state) {
  var i, token, text2, inlineTokens, blkIdx;
  if (!state.options.typographer) {
    return;
  }
  for (blkIdx = state.tokens.length - 1; blkIdx >= 0; blkIdx--) {
    if (state.tokens[blkIdx].type !== "inline") {
      continue;
    }
    inlineTokens = state.tokens[blkIdx].children;
    for (i = inlineTokens.length - 1; i >= 0; i--) {
      token = inlineTokens[i];
      if (token.type === "text") {
        text2 = token.content;
        text2 = replaceScopedAbbr(text2);
        if (RARE_RE.test(text2)) {
          text2 = text2.replace(/\+-/g, "±").replace(/\.{2,}/g, "…").replace(/([?!])…/g, "$1..").replace(/([?!]){4,}/g, "$1$1$1").replace(/,{2,}/g, ",").replace(/(^|[^-])---([^-]|$)/mg, "$1—$2").replace(/(^|\s)--(\s|$)/mg, "$1–$2").replace(/(^|[^-\s])--([^-\s]|$)/mg, "$1–$2");
        }
        token.content = text2;
      }
    }
  }
}
var QUOTE_TEST_RE = /['"]/;
var QUOTE_RE = /['"]/g;
var PUNCT_RE = /[-\s()\[\]]/;
var APOSTROPHE = "’";
function isLetter(str, pos) {
  if (pos < 0 || pos >= str.length) {
    return false;
  }
  return !PUNCT_RE.test(str[pos]);
}
function replaceAt(str, index, ch) {
  return str.substr(0, index) + ch + str.substr(index + 1);
}
function smartquotes(state) {
  var i, token, text2, t, pos, max, thisLevel, lastSpace, nextSpace, item, canOpen, canClose, j, isSingle, blkIdx, tokens, stack;
  if (!state.options.typographer) {
    return;
  }
  stack = [];
  for (blkIdx = state.tokens.length - 1; blkIdx >= 0; blkIdx--) {
    if (state.tokens[blkIdx].type !== "inline") {
      continue;
    }
    tokens = state.tokens[blkIdx].children;
    stack.length = 0;
    for (i = 0; i < tokens.length; i++) {
      token = tokens[i];
      if (token.type !== "text" || QUOTE_TEST_RE.test(token.text)) {
        continue;
      }
      thisLevel = tokens[i].level;
      for (j = stack.length - 1; j >= 0; j--) {
        if (stack[j].level <= thisLevel) {
          break;
        }
      }
      stack.length = j + 1;
      text2 = token.content;
      pos = 0;
      max = text2.length;
      OUTER:
        while (pos < max) {
          QUOTE_RE.lastIndex = pos;
          t = QUOTE_RE.exec(text2);
          if (!t) {
            break;
          }
          lastSpace = !isLetter(text2, t.index - 1);
          pos = t.index + 1;
          isSingle = t[0] === "'";
          nextSpace = !isLetter(text2, pos);
          if (!nextSpace && !lastSpace) {
            if (isSingle) {
              token.content = replaceAt(token.content, t.index, APOSTROPHE);
            }
            continue;
          }
          canOpen = !nextSpace;
          canClose = !lastSpace;
          if (canClose) {
            for (j = stack.length - 1; j >= 0; j--) {
              item = stack[j];
              if (stack[j].level < thisLevel) {
                break;
              }
              if (item.single === isSingle && stack[j].level === thisLevel) {
                item = stack[j];
                if (isSingle) {
                  tokens[item.token].content = replaceAt(tokens[item.token].content, item.pos, state.options.quotes[2]);
                  token.content = replaceAt(token.content, t.index, state.options.quotes[3]);
                } else {
                  tokens[item.token].content = replaceAt(tokens[item.token].content, item.pos, state.options.quotes[0]);
                  token.content = replaceAt(token.content, t.index, state.options.quotes[1]);
                }
                stack.length = j;
                continue OUTER;
              }
            }
          }
          if (canOpen) {
            stack.push({
              token: i,
              pos: t.index,
              single: isSingle,
              level: thisLevel
            });
          } else if (canClose && isSingle) {
            token.content = replaceAt(token.content, t.index, APOSTROPHE);
          }
        }
    }
  }
}
var _rules = [
  ["block", block],
  ["abbr", abbr],
  ["references", references],
  ["inline", inline],
  ["footnote_tail", footnote_block],
  ["abbr2", abbr2],
  ["replacements", replace],
  ["smartquotes", smartquotes]
];
function Core() {
  this.options = {};
  this.ruler = new Ruler();
  for (var i = 0; i < _rules.length; i++) {
    this.ruler.push(_rules[i][0], _rules[i][1]);
  }
}
Core.prototype.process = function(state) {
  var i, l, rules2;
  rules2 = this.ruler.getRules("");
  for (i = 0, l = rules2.length; i < l; i++) {
    rules2[i](state);
  }
};
function StateBlock(src, parser, options, env, tokens) {
  var ch, s, start, pos, len, indent, indent_found;
  this.src = src;
  this.parser = parser;
  this.options = options;
  this.env = env;
  this.tokens = tokens;
  this.bMarks = [];
  this.eMarks = [];
  this.tShift = [];
  this.blkIndent = 0;
  this.line = 0;
  this.lineMax = 0;
  this.tight = false;
  this.parentType = "root";
  this.ddIndent = -1;
  this.level = 0;
  this.result = "";
  s = this.src;
  indent = 0;
  indent_found = false;
  for (start = pos = indent = 0, len = s.length; pos < len; pos++) {
    ch = s.charCodeAt(pos);
    if (!indent_found) {
      if (ch === 32) {
        indent++;
        continue;
      } else {
        indent_found = true;
      }
    }
    if (ch === 10 || pos === len - 1) {
      if (ch !== 10) {
        pos++;
      }
      this.bMarks.push(start);
      this.eMarks.push(pos);
      this.tShift.push(indent);
      indent_found = false;
      indent = 0;
      start = pos + 1;
    }
  }
  this.bMarks.push(s.length);
  this.eMarks.push(s.length);
  this.tShift.push(0);
  this.lineMax = this.bMarks.length - 1;
}
StateBlock.prototype.isEmpty = function isEmpty(line) {
  return this.bMarks[line] + this.tShift[line] >= this.eMarks[line];
};
StateBlock.prototype.skipEmptyLines = function skipEmptyLines(from) {
  for (var max = this.lineMax; from < max; from++) {
    if (this.bMarks[from] + this.tShift[from] < this.eMarks[from]) {
      break;
    }
  }
  return from;
};
StateBlock.prototype.skipSpaces = function skipSpaces(pos) {
  for (var max = this.src.length; pos < max; pos++) {
    if (this.src.charCodeAt(pos) !== 32) {
      break;
    }
  }
  return pos;
};
StateBlock.prototype.skipChars = function skipChars(pos, code2) {
  for (var max = this.src.length; pos < max; pos++) {
    if (this.src.charCodeAt(pos) !== code2) {
      break;
    }
  }
  return pos;
};
StateBlock.prototype.skipCharsBack = function skipCharsBack(pos, code2, min) {
  if (pos <= min) {
    return pos;
  }
  while (pos > min) {
    if (code2 !== this.src.charCodeAt(--pos)) {
      return pos + 1;
    }
  }
  return pos;
};
StateBlock.prototype.getLines = function getLines(begin, end, indent, keepLastLF) {
  var i, first, last, queue, shift, line = begin;
  if (begin >= end) {
    return "";
  }
  if (line + 1 === end) {
    first = this.bMarks[line] + Math.min(this.tShift[line], indent);
    last = keepLastLF ? this.eMarks[line] + 1 : this.eMarks[line];
    return this.src.slice(first, last);
  }
  queue = new Array(end - begin);
  for (i = 0; line < end; line++, i++) {
    shift = this.tShift[line];
    if (shift > indent) {
      shift = indent;
    }
    if (shift < 0) {
      shift = 0;
    }
    first = this.bMarks[line] + shift;
    if (line + 1 < end || keepLastLF) {
      last = this.eMarks[line] + 1;
    } else {
      last = this.eMarks[line];
    }
    queue[i] = this.src.slice(first, last);
  }
  return queue.join("");
};
function code(state, startLine, endLine) {
  var nextLine, last;
  if (state.tShift[startLine] - state.blkIndent < 4) {
    return false;
  }
  last = nextLine = startLine + 1;
  while (nextLine < endLine) {
    if (state.isEmpty(nextLine)) {
      nextLine++;
      continue;
    }
    if (state.tShift[nextLine] - state.blkIndent >= 4) {
      nextLine++;
      last = nextLine;
      continue;
    }
    break;
  }
  state.line = nextLine;
  state.tokens.push({
    type: "code",
    content: state.getLines(startLine, last, 4 + state.blkIndent, true),
    block: true,
    lines: [startLine, state.line],
    level: state.level
  });
  return true;
}
function fences(state, startLine, endLine, silent) {
  var marker, len, params, nextLine, mem, haveEndMarker = false, pos = state.bMarks[startLine] + state.tShift[startLine], max = state.eMarks[startLine];
  if (pos + 3 > max) {
    return false;
  }
  marker = state.src.charCodeAt(pos);
  if (marker !== 126 && marker !== 96) {
    return false;
  }
  mem = pos;
  pos = state.skipChars(pos, marker);
  len = pos - mem;
  if (len < 3) {
    return false;
  }
  params = state.src.slice(pos, max).trim();
  if (params.indexOf("`") >= 0) {
    return false;
  }
  if (silent) {
    return true;
  }
  nextLine = startLine;
  for (; ; ) {
    nextLine++;
    if (nextLine >= endLine) {
      break;
    }
    pos = mem = state.bMarks[nextLine] + state.tShift[nextLine];
    max = state.eMarks[nextLine];
    if (pos < max && state.tShift[nextLine] < state.blkIndent) {
      break;
    }
    if (state.src.charCodeAt(pos) !== marker) {
      continue;
    }
    if (state.tShift[nextLine] - state.blkIndent >= 4) {
      continue;
    }
    pos = state.skipChars(pos, marker);
    if (pos - mem < len) {
      continue;
    }
    pos = state.skipSpaces(pos);
    if (pos < max) {
      continue;
    }
    haveEndMarker = true;
    break;
  }
  len = state.tShift[startLine];
  state.line = nextLine + (haveEndMarker ? 1 : 0);
  state.tokens.push({
    type: "fence",
    params,
    content: state.getLines(startLine + 1, nextLine, len, true),
    lines: [startLine, state.line],
    level: state.level
  });
  return true;
}
function blockquote(state, startLine, endLine, silent) {
  var nextLine, lastLineEmpty, oldTShift, oldBMarks, oldIndent, oldParentType, lines, terminatorRules, i, l, terminate, pos = state.bMarks[startLine] + state.tShift[startLine], max = state.eMarks[startLine];
  if (pos > max) {
    return false;
  }
  if (state.src.charCodeAt(pos++) !== 62) {
    return false;
  }
  if (state.level >= state.options.maxNesting) {
    return false;
  }
  if (silent) {
    return true;
  }
  if (state.src.charCodeAt(pos) === 32) {
    pos++;
  }
  oldIndent = state.blkIndent;
  state.blkIndent = 0;
  oldBMarks = [state.bMarks[startLine]];
  state.bMarks[startLine] = pos;
  pos = pos < max ? state.skipSpaces(pos) : pos;
  lastLineEmpty = pos >= max;
  oldTShift = [state.tShift[startLine]];
  state.tShift[startLine] = pos - state.bMarks[startLine];
  terminatorRules = state.parser.ruler.getRules("blockquote");
  for (nextLine = startLine + 1; nextLine < endLine; nextLine++) {
    pos = state.bMarks[nextLine] + state.tShift[nextLine];
    max = state.eMarks[nextLine];
    if (pos >= max) {
      break;
    }
    if (state.src.charCodeAt(pos++) === 62) {
      if (state.src.charCodeAt(pos) === 32) {
        pos++;
      }
      oldBMarks.push(state.bMarks[nextLine]);
      state.bMarks[nextLine] = pos;
      pos = pos < max ? state.skipSpaces(pos) : pos;
      lastLineEmpty = pos >= max;
      oldTShift.push(state.tShift[nextLine]);
      state.tShift[nextLine] = pos - state.bMarks[nextLine];
      continue;
    }
    if (lastLineEmpty) {
      break;
    }
    terminate = false;
    for (i = 0, l = terminatorRules.length; i < l; i++) {
      if (terminatorRules[i](state, nextLine, endLine, true)) {
        terminate = true;
        break;
      }
    }
    if (terminate) {
      break;
    }
    oldBMarks.push(state.bMarks[nextLine]);
    oldTShift.push(state.tShift[nextLine]);
    state.tShift[nextLine] = -1337;
  }
  oldParentType = state.parentType;
  state.parentType = "blockquote";
  state.tokens.push({
    type: "blockquote_open",
    lines: lines = [startLine, 0],
    level: state.level++
  });
  state.parser.tokenize(state, startLine, nextLine);
  state.tokens.push({
    type: "blockquote_close",
    level: --state.level
  });
  state.parentType = oldParentType;
  lines[1] = state.line;
  for (i = 0; i < oldTShift.length; i++) {
    state.bMarks[i + startLine] = oldBMarks[i];
    state.tShift[i + startLine] = oldTShift[i];
  }
  state.blkIndent = oldIndent;
  return true;
}
function hr(state, startLine, endLine, silent) {
  var marker, cnt, ch, pos = state.bMarks[startLine], max = state.eMarks[startLine];
  pos += state.tShift[startLine];
  if (pos > max) {
    return false;
  }
  marker = state.src.charCodeAt(pos++);
  if (marker !== 42 && marker !== 45 && marker !== 95) {
    return false;
  }
  cnt = 1;
  while (pos < max) {
    ch = state.src.charCodeAt(pos++);
    if (ch !== marker && ch !== 32) {
      return false;
    }
    if (ch === marker) {
      cnt++;
    }
  }
  if (cnt < 3) {
    return false;
  }
  if (silent) {
    return true;
  }
  state.line = startLine + 1;
  state.tokens.push({
    type: "hr",
    lines: [startLine, state.line],
    level: state.level
  });
  return true;
}
function skipBulletListMarker(state, startLine) {
  var marker, pos, max;
  pos = state.bMarks[startLine] + state.tShift[startLine];
  max = state.eMarks[startLine];
  if (pos >= max) {
    return -1;
  }
  marker = state.src.charCodeAt(pos++);
  if (marker !== 42 && marker !== 45 && marker !== 43) {
    return -1;
  }
  if (pos < max && state.src.charCodeAt(pos) !== 32) {
    return -1;
  }
  return pos;
}
function skipOrderedListMarker(state, startLine) {
  var ch, pos = state.bMarks[startLine] + state.tShift[startLine], max = state.eMarks[startLine];
  if (pos + 1 >= max) {
    return -1;
  }
  ch = state.src.charCodeAt(pos++);
  if (ch < 48 || ch > 57) {
    return -1;
  }
  for (; ; ) {
    if (pos >= max) {
      return -1;
    }
    ch = state.src.charCodeAt(pos++);
    if (ch >= 48 && ch <= 57) {
      continue;
    }
    if (ch === 41 || ch === 46) {
      break;
    }
    return -1;
  }
  if (pos < max && state.src.charCodeAt(pos) !== 32) {
    return -1;
  }
  return pos;
}
function markTightParagraphs(state, idx) {
  var i, l, level = state.level + 2;
  for (i = idx + 2, l = state.tokens.length - 2; i < l; i++) {
    if (state.tokens[i].level === level && state.tokens[i].type === "paragraph_open") {
      state.tokens[i + 2].tight = true;
      state.tokens[i].tight = true;
      i += 2;
    }
  }
}
function list(state, startLine, endLine, silent) {
  var nextLine, indent, oldTShift, oldIndent, oldTight, oldParentType, start, posAfterMarker, max, indentAfterMarker, markerValue, markerCharCode, isOrdered, contentStart, listTokIdx, prevEmptyEnd, listLines, itemLines, tight = true, terminatorRules, i, l, terminate;
  if ((posAfterMarker = skipOrderedListMarker(state, startLine)) >= 0) {
    isOrdered = true;
  } else if ((posAfterMarker = skipBulletListMarker(state, startLine)) >= 0) {
    isOrdered = false;
  } else {
    return false;
  }
  if (state.level >= state.options.maxNesting) {
    return false;
  }
  markerCharCode = state.src.charCodeAt(posAfterMarker - 1);
  if (silent) {
    return true;
  }
  listTokIdx = state.tokens.length;
  if (isOrdered) {
    start = state.bMarks[startLine] + state.tShift[startLine];
    markerValue = Number(state.src.substr(start, posAfterMarker - start - 1));
    state.tokens.push({
      type: "ordered_list_open",
      order: markerValue,
      lines: listLines = [startLine, 0],
      level: state.level++
    });
  } else {
    state.tokens.push({
      type: "bullet_list_open",
      lines: listLines = [startLine, 0],
      level: state.level++
    });
  }
  nextLine = startLine;
  prevEmptyEnd = false;
  terminatorRules = state.parser.ruler.getRules("list");
  while (nextLine < endLine) {
    contentStart = state.skipSpaces(posAfterMarker);
    max = state.eMarks[nextLine];
    if (contentStart >= max) {
      indentAfterMarker = 1;
    } else {
      indentAfterMarker = contentStart - posAfterMarker;
    }
    if (indentAfterMarker > 4) {
      indentAfterMarker = 1;
    }
    if (indentAfterMarker < 1) {
      indentAfterMarker = 1;
    }
    indent = posAfterMarker - state.bMarks[nextLine] + indentAfterMarker;
    state.tokens.push({
      type: "list_item_open",
      lines: itemLines = [startLine, 0],
      level: state.level++
    });
    oldIndent = state.blkIndent;
    oldTight = state.tight;
    oldTShift = state.tShift[startLine];
    oldParentType = state.parentType;
    state.tShift[startLine] = contentStart - state.bMarks[startLine];
    state.blkIndent = indent;
    state.tight = true;
    state.parentType = "list";
    state.parser.tokenize(state, startLine, endLine, true);
    if (!state.tight || prevEmptyEnd) {
      tight = false;
    }
    prevEmptyEnd = state.line - startLine > 1 && state.isEmpty(state.line - 1);
    state.blkIndent = oldIndent;
    state.tShift[startLine] = oldTShift;
    state.tight = oldTight;
    state.parentType = oldParentType;
    state.tokens.push({
      type: "list_item_close",
      level: --state.level
    });
    nextLine = startLine = state.line;
    itemLines[1] = nextLine;
    contentStart = state.bMarks[startLine];
    if (nextLine >= endLine) {
      break;
    }
    if (state.isEmpty(nextLine)) {
      break;
    }
    if (state.tShift[nextLine] < state.blkIndent) {
      break;
    }
    terminate = false;
    for (i = 0, l = terminatorRules.length; i < l; i++) {
      if (terminatorRules[i](state, nextLine, endLine, true)) {
        terminate = true;
        break;
      }
    }
    if (terminate) {
      break;
    }
    if (isOrdered) {
      posAfterMarker = skipOrderedListMarker(state, nextLine);
      if (posAfterMarker < 0) {
        break;
      }
    } else {
      posAfterMarker = skipBulletListMarker(state, nextLine);
      if (posAfterMarker < 0) {
        break;
      }
    }
    if (markerCharCode !== state.src.charCodeAt(posAfterMarker - 1)) {
      break;
    }
  }
  state.tokens.push({
    type: isOrdered ? "ordered_list_close" : "bullet_list_close",
    level: --state.level
  });
  listLines[1] = nextLine;
  state.line = nextLine;
  if (tight) {
    markTightParagraphs(state, listTokIdx);
  }
  return true;
}
function footnote(state, startLine, endLine, silent) {
  var oldBMark, oldTShift, oldParentType, pos, label, start = state.bMarks[startLine] + state.tShift[startLine], max = state.eMarks[startLine];
  if (start + 4 > max) {
    return false;
  }
  if (state.src.charCodeAt(start) !== 91) {
    return false;
  }
  if (state.src.charCodeAt(start + 1) !== 94) {
    return false;
  }
  if (state.level >= state.options.maxNesting) {
    return false;
  }
  for (pos = start + 2; pos < max; pos++) {
    if (state.src.charCodeAt(pos) === 32) {
      return false;
    }
    if (state.src.charCodeAt(pos) === 93) {
      break;
    }
  }
  if (pos === start + 2) {
    return false;
  }
  if (pos + 1 >= max || state.src.charCodeAt(++pos) !== 58) {
    return false;
  }
  if (silent) {
    return true;
  }
  pos++;
  if (!state.env.footnotes) {
    state.env.footnotes = {};
  }
  if (!state.env.footnotes.refs) {
    state.env.footnotes.refs = {};
  }
  label = state.src.slice(start + 2, pos - 2);
  state.env.footnotes.refs[":" + label] = -1;
  state.tokens.push({
    type: "footnote_reference_open",
    label,
    level: state.level++
  });
  oldBMark = state.bMarks[startLine];
  oldTShift = state.tShift[startLine];
  oldParentType = state.parentType;
  state.tShift[startLine] = state.skipSpaces(pos) - pos;
  state.bMarks[startLine] = pos;
  state.blkIndent += 4;
  state.parentType = "footnote";
  if (state.tShift[startLine] < state.blkIndent) {
    state.tShift[startLine] += state.blkIndent;
    state.bMarks[startLine] -= state.blkIndent;
  }
  state.parser.tokenize(state, startLine, endLine, true);
  state.parentType = oldParentType;
  state.blkIndent -= 4;
  state.tShift[startLine] = oldTShift;
  state.bMarks[startLine] = oldBMark;
  state.tokens.push({
    type: "footnote_reference_close",
    level: --state.level
  });
  return true;
}
function heading(state, startLine, endLine, silent) {
  var ch, level, tmp, pos = state.bMarks[startLine] + state.tShift[startLine], max = state.eMarks[startLine];
  if (pos >= max) {
    return false;
  }
  ch = state.src.charCodeAt(pos);
  if (ch !== 35 || pos >= max) {
    return false;
  }
  level = 1;
  ch = state.src.charCodeAt(++pos);
  while (ch === 35 && pos < max && level <= 6) {
    level++;
    ch = state.src.charCodeAt(++pos);
  }
  if (level > 6 || pos < max && ch !== 32) {
    return false;
  }
  if (silent) {
    return true;
  }
  max = state.skipCharsBack(max, 32, pos);
  tmp = state.skipCharsBack(max, 35, pos);
  if (tmp > pos && state.src.charCodeAt(tmp - 1) === 32) {
    max = tmp;
  }
  state.line = startLine + 1;
  state.tokens.push({
    type: "heading_open",
    hLevel: level,
    lines: [startLine, state.line],
    level: state.level
  });
  if (pos < max) {
    state.tokens.push({
      type: "inline",
      content: state.src.slice(pos, max).trim(),
      level: state.level + 1,
      lines: [startLine, state.line],
      children: []
    });
  }
  state.tokens.push({ type: "heading_close", hLevel: level, level: state.level });
  return true;
}
function lheading(state, startLine, endLine) {
  var marker, pos, max, next = startLine + 1;
  if (next >= endLine) {
    return false;
  }
  if (state.tShift[next] < state.blkIndent) {
    return false;
  }
  if (state.tShift[next] - state.blkIndent > 3) {
    return false;
  }
  pos = state.bMarks[next] + state.tShift[next];
  max = state.eMarks[next];
  if (pos >= max) {
    return false;
  }
  marker = state.src.charCodeAt(pos);
  if (marker !== 45 && marker !== 61) {
    return false;
  }
  pos = state.skipChars(pos, marker);
  pos = state.skipSpaces(pos);
  if (pos < max) {
    return false;
  }
  pos = state.bMarks[startLine] + state.tShift[startLine];
  state.line = next + 1;
  state.tokens.push({
    type: "heading_open",
    hLevel: marker === 61 ? 1 : 2,
    lines: [startLine, state.line],
    level: state.level
  });
  state.tokens.push({
    type: "inline",
    content: state.src.slice(pos, state.eMarks[startLine]).trim(),
    level: state.level + 1,
    lines: [startLine, state.line - 1],
    children: []
  });
  state.tokens.push({
    type: "heading_close",
    hLevel: marker === 61 ? 1 : 2,
    level: state.level
  });
  return true;
}
var html_blocks = {};
[
  "article",
  "aside",
  "button",
  "blockquote",
  "body",
  "canvas",
  "caption",
  "col",
  "colgroup",
  "dd",
  "div",
  "dl",
  "dt",
  "embed",
  "fieldset",
  "figcaption",
  "figure",
  "footer",
  "form",
  "h1",
  "h2",
  "h3",
  "h4",
  "h5",
  "h6",
  "header",
  "hgroup",
  "hr",
  "iframe",
  "li",
  "map",
  "object",
  "ol",
  "output",
  "p",
  "pre",
  "progress",
  "script",
  "section",
  "style",
  "table",
  "tbody",
  "td",
  "textarea",
  "tfoot",
  "th",
  "tr",
  "thead",
  "ul",
  "video"
].forEach(function(name) {
  html_blocks[name] = true;
});
var HTML_TAG_OPEN_RE = /^<([a-zA-Z]{1,15})[\s\/>]/;
var HTML_TAG_CLOSE_RE = /^<\/([a-zA-Z]{1,15})[\s>]/;
function isLetter$1(ch) {
  var lc = ch | 32;
  return lc >= 97 && lc <= 122;
}
function htmlblock(state, startLine, endLine, silent) {
  var ch, match, nextLine, pos = state.bMarks[startLine], max = state.eMarks[startLine], shift = state.tShift[startLine];
  pos += shift;
  if (!state.options.html) {
    return false;
  }
  if (shift > 3 || pos + 2 >= max) {
    return false;
  }
  if (state.src.charCodeAt(pos) !== 60) {
    return false;
  }
  ch = state.src.charCodeAt(pos + 1);
  if (ch === 33 || ch === 63) {
    if (silent) {
      return true;
    }
  } else if (ch === 47 || isLetter$1(ch)) {
    if (ch === 47) {
      match = state.src.slice(pos, max).match(HTML_TAG_CLOSE_RE);
      if (!match) {
        return false;
      }
    } else {
      match = state.src.slice(pos, max).match(HTML_TAG_OPEN_RE);
      if (!match) {
        return false;
      }
    }
    if (html_blocks[match[1].toLowerCase()] !== true) {
      return false;
    }
    if (silent) {
      return true;
    }
  } else {
    return false;
  }
  nextLine = startLine + 1;
  while (nextLine < state.lineMax && !state.isEmpty(nextLine)) {
    nextLine++;
  }
  state.line = nextLine;
  state.tokens.push({
    type: "htmlblock",
    level: state.level,
    lines: [startLine, state.line],
    content: state.getLines(startLine, nextLine, 0, true)
  });
  return true;
}
function getLine(state, line) {
  var pos = state.bMarks[line] + state.blkIndent, max = state.eMarks[line];
  return state.src.substr(pos, max - pos);
}
function table(state, startLine, endLine, silent) {
  var ch, lineText, pos, i, nextLine, rows, cell, aligns, t, tableLines, tbodyLines;
  if (startLine + 2 > endLine) {
    return false;
  }
  nextLine = startLine + 1;
  if (state.tShift[nextLine] < state.blkIndent) {
    return false;
  }
  pos = state.bMarks[nextLine] + state.tShift[nextLine];
  if (pos >= state.eMarks[nextLine]) {
    return false;
  }
  ch = state.src.charCodeAt(pos);
  if (ch !== 124 && ch !== 45 && ch !== 58) {
    return false;
  }
  lineText = getLine(state, startLine + 1);
  if (!/^[-:| ]+$/.test(lineText)) {
    return false;
  }
  rows = lineText.split("|");
  if (rows <= 2) {
    return false;
  }
  aligns = [];
  for (i = 0; i < rows.length; i++) {
    t = rows[i].trim();
    if (!t) {
      if (i === 0 || i === rows.length - 1) {
        continue;
      } else {
        return false;
      }
    }
    if (!/^:?-+:?$/.test(t)) {
      return false;
    }
    if (t.charCodeAt(t.length - 1) === 58) {
      aligns.push(t.charCodeAt(0) === 58 ? "center" : "right");
    } else if (t.charCodeAt(0) === 58) {
      aligns.push("left");
    } else {
      aligns.push("");
    }
  }
  lineText = getLine(state, startLine).trim();
  if (lineText.indexOf("|") === -1) {
    return false;
  }
  rows = lineText.replace(/^\||\|$/g, "").split("|");
  if (aligns.length !== rows.length) {
    return false;
  }
  if (silent) {
    return true;
  }
  state.tokens.push({
    type: "table_open",
    lines: tableLines = [startLine, 0],
    level: state.level++
  });
  state.tokens.push({
    type: "thead_open",
    lines: [startLine, startLine + 1],
    level: state.level++
  });
  state.tokens.push({
    type: "tr_open",
    lines: [startLine, startLine + 1],
    level: state.level++
  });
  for (i = 0; i < rows.length; i++) {
    state.tokens.push({
      type: "th_open",
      align: aligns[i],
      lines: [startLine, startLine + 1],
      level: state.level++
    });
    state.tokens.push({
      type: "inline",
      content: rows[i].trim(),
      lines: [startLine, startLine + 1],
      level: state.level,
      children: []
    });
    state.tokens.push({ type: "th_close", level: --state.level });
  }
  state.tokens.push({ type: "tr_close", level: --state.level });
  state.tokens.push({ type: "thead_close", level: --state.level });
  state.tokens.push({
    type: "tbody_open",
    lines: tbodyLines = [startLine + 2, 0],
    level: state.level++
  });
  for (nextLine = startLine + 2; nextLine < endLine; nextLine++) {
    if (state.tShift[nextLine] < state.blkIndent) {
      break;
    }
    lineText = getLine(state, nextLine).trim();
    if (lineText.indexOf("|") === -1) {
      break;
    }
    rows = lineText.replace(/^\||\|$/g, "").split("|");
    state.tokens.push({ type: "tr_open", level: state.level++ });
    for (i = 0; i < rows.length; i++) {
      state.tokens.push({ type: "td_open", align: aligns[i], level: state.level++ });
      cell = rows[i].substring(
        rows[i].charCodeAt(0) === 124 ? 1 : 0,
        rows[i].charCodeAt(rows[i].length - 1) === 124 ? rows[i].length - 1 : rows[i].length
      ).trim();
      state.tokens.push({
        type: "inline",
        content: cell,
        level: state.level,
        children: []
      });
      state.tokens.push({ type: "td_close", level: --state.level });
    }
    state.tokens.push({ type: "tr_close", level: --state.level });
  }
  state.tokens.push({ type: "tbody_close", level: --state.level });
  state.tokens.push({ type: "table_close", level: --state.level });
  tableLines[1] = tbodyLines[1] = nextLine;
  state.line = nextLine;
  return true;
}
function skipMarker(state, line) {
  var pos, marker, start = state.bMarks[line] + state.tShift[line], max = state.eMarks[line];
  if (start >= max) {
    return -1;
  }
  marker = state.src.charCodeAt(start++);
  if (marker !== 126 && marker !== 58) {
    return -1;
  }
  pos = state.skipSpaces(start);
  if (start === pos) {
    return -1;
  }
  if (pos >= max) {
    return -1;
  }
  return pos;
}
function markTightParagraphs$1(state, idx) {
  var i, l, level = state.level + 2;
  for (i = idx + 2, l = state.tokens.length - 2; i < l; i++) {
    if (state.tokens[i].level === level && state.tokens[i].type === "paragraph_open") {
      state.tokens[i + 2].tight = true;
      state.tokens[i].tight = true;
      i += 2;
    }
  }
}
function deflist(state, startLine, endLine, silent) {
  var contentStart, ddLine, dtLine, itemLines, listLines, listTokIdx, nextLine, oldIndent, oldDDIndent, oldParentType, oldTShift, oldTight, prevEmptyEnd, tight;
  if (silent) {
    if (state.ddIndent < 0) {
      return false;
    }
    return skipMarker(state, startLine) >= 0;
  }
  nextLine = startLine + 1;
  if (state.isEmpty(nextLine)) {
    if (++nextLine > endLine) {
      return false;
    }
  }
  if (state.tShift[nextLine] < state.blkIndent) {
    return false;
  }
  contentStart = skipMarker(state, nextLine);
  if (contentStart < 0) {
    return false;
  }
  if (state.level >= state.options.maxNesting) {
    return false;
  }
  listTokIdx = state.tokens.length;
  state.tokens.push({
    type: "dl_open",
    lines: listLines = [startLine, 0],
    level: state.level++
  });
  dtLine = startLine;
  ddLine = nextLine;
  OUTER:
    for (; ; ) {
      tight = true;
      prevEmptyEnd = false;
      state.tokens.push({
        type: "dt_open",
        lines: [dtLine, dtLine],
        level: state.level++
      });
      state.tokens.push({
        type: "inline",
        content: state.getLines(dtLine, dtLine + 1, state.blkIndent, false).trim(),
        level: state.level + 1,
        lines: [dtLine, dtLine],
        children: []
      });
      state.tokens.push({
        type: "dt_close",
        level: --state.level
      });
      for (; ; ) {
        state.tokens.push({
          type: "dd_open",
          lines: itemLines = [nextLine, 0],
          level: state.level++
        });
        oldTight = state.tight;
        oldDDIndent = state.ddIndent;
        oldIndent = state.blkIndent;
        oldTShift = state.tShift[ddLine];
        oldParentType = state.parentType;
        state.blkIndent = state.ddIndent = state.tShift[ddLine] + 2;
        state.tShift[ddLine] = contentStart - state.bMarks[ddLine];
        state.tight = true;
        state.parentType = "deflist";
        state.parser.tokenize(state, ddLine, endLine, true);
        if (!state.tight || prevEmptyEnd) {
          tight = false;
        }
        prevEmptyEnd = state.line - ddLine > 1 && state.isEmpty(state.line - 1);
        state.tShift[ddLine] = oldTShift;
        state.tight = oldTight;
        state.parentType = oldParentType;
        state.blkIndent = oldIndent;
        state.ddIndent = oldDDIndent;
        state.tokens.push({
          type: "dd_close",
          level: --state.level
        });
        itemLines[1] = nextLine = state.line;
        if (nextLine >= endLine) {
          break OUTER;
        }
        if (state.tShift[nextLine] < state.blkIndent) {
          break OUTER;
        }
        contentStart = skipMarker(state, nextLine);
        if (contentStart < 0) {
          break;
        }
        ddLine = nextLine;
      }
      if (nextLine >= endLine) {
        break;
      }
      dtLine = nextLine;
      if (state.isEmpty(dtLine)) {
        break;
      }
      if (state.tShift[dtLine] < state.blkIndent) {
        break;
      }
      ddLine = dtLine + 1;
      if (ddLine >= endLine) {
        break;
      }
      if (state.isEmpty(ddLine)) {
        ddLine++;
      }
      if (ddLine >= endLine) {
        break;
      }
      if (state.tShift[ddLine] < state.blkIndent) {
        break;
      }
      contentStart = skipMarker(state, ddLine);
      if (contentStart < 0) {
        break;
      }
    }
  state.tokens.push({
    type: "dl_close",
    level: --state.level
  });
  listLines[1] = nextLine;
  state.line = nextLine;
  if (tight) {
    markTightParagraphs$1(state, listTokIdx);
  }
  return true;
}
function paragraph(state, startLine) {
  var endLine, content, terminate, i, l, nextLine = startLine + 1, terminatorRules;
  endLine = state.lineMax;
  if (nextLine < endLine && !state.isEmpty(nextLine)) {
    terminatorRules = state.parser.ruler.getRules("paragraph");
    for (; nextLine < endLine && !state.isEmpty(nextLine); nextLine++) {
      if (state.tShift[nextLine] - state.blkIndent > 3) {
        continue;
      }
      terminate = false;
      for (i = 0, l = terminatorRules.length; i < l; i++) {
        if (terminatorRules[i](state, nextLine, endLine, true)) {
          terminate = true;
          break;
        }
      }
      if (terminate) {
        break;
      }
    }
  }
  content = state.getLines(startLine, nextLine, state.blkIndent, false).trim();
  state.line = nextLine;
  if (content.length) {
    state.tokens.push({
      type: "paragraph_open",
      tight: false,
      lines: [startLine, state.line],
      level: state.level
    });
    state.tokens.push({
      type: "inline",
      content,
      level: state.level + 1,
      lines: [startLine, state.line],
      children: []
    });
    state.tokens.push({
      type: "paragraph_close",
      tight: false,
      level: state.level
    });
  }
  return true;
}
var _rules$1 = [
  ["code", code],
  ["fences", fences, ["paragraph", "blockquote", "list"]],
  ["blockquote", blockquote, ["paragraph", "blockquote", "list"]],
  ["hr", hr, ["paragraph", "blockquote", "list"]],
  ["list", list, ["paragraph", "blockquote"]],
  ["footnote", footnote, ["paragraph"]],
  ["heading", heading, ["paragraph", "blockquote"]],
  ["lheading", lheading],
  ["htmlblock", htmlblock, ["paragraph", "blockquote"]],
  ["table", table, ["paragraph"]],
  ["deflist", deflist, ["paragraph"]],
  ["paragraph", paragraph]
];
function ParserBlock() {
  this.ruler = new Ruler();
  for (var i = 0; i < _rules$1.length; i++) {
    this.ruler.push(_rules$1[i][0], _rules$1[i][1], {
      alt: (_rules$1[i][2] || []).slice()
    });
  }
}
ParserBlock.prototype.tokenize = function(state, startLine, endLine) {
  var rules2 = this.ruler.getRules("");
  var len = rules2.length;
  var line = startLine;
  var hasEmptyLines = false;
  var ok, i;
  while (line < endLine) {
    state.line = line = state.skipEmptyLines(line);
    if (line >= endLine) {
      break;
    }
    if (state.tShift[line] < state.blkIndent) {
      break;
    }
    for (i = 0; i < len; i++) {
      ok = rules2[i](state, line, endLine, false);
      if (ok) {
        break;
      }
    }
    state.tight = !hasEmptyLines;
    if (state.isEmpty(state.line - 1)) {
      hasEmptyLines = true;
    }
    line = state.line;
    if (line < endLine && state.isEmpty(line)) {
      hasEmptyLines = true;
      line++;
      if (line < endLine && state.parentType === "list" && state.isEmpty(line)) {
        break;
      }
      state.line = line;
    }
  }
};
var TABS_SCAN_RE = /[\n\t]/g;
var NEWLINES_RE = /\r[\n\u0085]|[\u2424\u2028\u0085]/g;
var SPACES_RE = /\u00a0/g;
ParserBlock.prototype.parse = function(str, options, env, outTokens) {
  var state, lineStart = 0, lastTabPos = 0;
  if (!str) {
    return [];
  }
  str = str.replace(SPACES_RE, " ");
  str = str.replace(NEWLINES_RE, "\n");
  if (str.indexOf("	") >= 0) {
    str = str.replace(TABS_SCAN_RE, function(match, offset) {
      var result;
      if (str.charCodeAt(offset) === 10) {
        lineStart = offset + 1;
        lastTabPos = 0;
        return match;
      }
      result = "    ".slice((offset - lineStart - lastTabPos) % 4);
      lastTabPos = offset - lineStart + 1;
      return result;
    });
  }
  state = new StateBlock(str, this, options, env, outTokens);
  this.tokenize(state, state.line, state.lineMax);
};
function isTerminatorChar(ch) {
  switch (ch) {
    case 10:
    case 92:
    case 96:
    case 42:
    case 95:
    case 94:
    case 91:
    case 93:
    case 33:
    case 38:
    case 60:
    case 62:
    case 123:
    case 125:
    case 36:
    case 37:
    case 64:
    case 126:
    case 43:
    case 61:
    case 58:
      return true;
    default:
      return false;
  }
}
function text(state, silent) {
  var pos = state.pos;
  while (pos < state.posMax && !isTerminatorChar(state.src.charCodeAt(pos))) {
    pos++;
  }
  if (pos === state.pos) {
    return false;
  }
  if (!silent) {
    state.pending += state.src.slice(state.pos, pos);
  }
  state.pos = pos;
  return true;
}
function newline(state, silent) {
  var pmax, max, pos = state.pos;
  if (state.src.charCodeAt(pos) !== 10) {
    return false;
  }
  pmax = state.pending.length - 1;
  max = state.posMax;
  if (!silent) {
    if (pmax >= 0 && state.pending.charCodeAt(pmax) === 32) {
      if (pmax >= 1 && state.pending.charCodeAt(pmax - 1) === 32) {
        for (var i = pmax - 2; i >= 0; i--) {
          if (state.pending.charCodeAt(i) !== 32) {
            state.pending = state.pending.substring(0, i + 1);
            break;
          }
        }
        state.push({
          type: "hardbreak",
          level: state.level
        });
      } else {
        state.pending = state.pending.slice(0, -1);
        state.push({
          type: "softbreak",
          level: state.level
        });
      }
    } else {
      state.push({
        type: "softbreak",
        level: state.level
      });
    }
  }
  pos++;
  while (pos < max && state.src.charCodeAt(pos) === 32) {
    pos++;
  }
  state.pos = pos;
  return true;
}
var ESCAPED = [];
for (var i = 0; i < 256; i++) {
  ESCAPED.push(0);
}
"\\!\"#$%&'()*+,./:;<=>?@[]^_`{|}~-".split("").forEach(function(ch) {
  ESCAPED[ch.charCodeAt(0)] = 1;
});
function escape(state, silent) {
  var ch, pos = state.pos, max = state.posMax;
  if (state.src.charCodeAt(pos) !== 92) {
    return false;
  }
  pos++;
  if (pos < max) {
    ch = state.src.charCodeAt(pos);
    if (ch < 256 && ESCAPED[ch] !== 0) {
      if (!silent) {
        state.pending += state.src[pos];
      }
      state.pos += 2;
      return true;
    }
    if (ch === 10) {
      if (!silent) {
        state.push({
          type: "hardbreak",
          level: state.level
        });
      }
      pos++;
      while (pos < max && state.src.charCodeAt(pos) === 32) {
        pos++;
      }
      state.pos = pos;
      return true;
    }
  }
  if (!silent) {
    state.pending += "\\";
  }
  state.pos++;
  return true;
}
function backticks(state, silent) {
  var start, max, marker, matchStart, matchEnd, pos = state.pos, ch = state.src.charCodeAt(pos);
  if (ch !== 96) {
    return false;
  }
  start = pos;
  pos++;
  max = state.posMax;
  while (pos < max && state.src.charCodeAt(pos) === 96) {
    pos++;
  }
  marker = state.src.slice(start, pos);
  matchStart = matchEnd = pos;
  while ((matchStart = state.src.indexOf("`", matchEnd)) !== -1) {
    matchEnd = matchStart + 1;
    while (matchEnd < max && state.src.charCodeAt(matchEnd) === 96) {
      matchEnd++;
    }
    if (matchEnd - matchStart === marker.length) {
      if (!silent) {
        state.push({
          type: "code",
          content: state.src.slice(pos, matchStart).replace(/[ \n]+/g, " ").trim(),
          block: false,
          level: state.level
        });
      }
      state.pos = matchEnd;
      return true;
    }
  }
  if (!silent) {
    state.pending += marker;
  }
  state.pos += marker.length;
  return true;
}
function del(state, silent) {
  var found, pos, stack, max = state.posMax, start = state.pos, lastChar, nextChar;
  if (state.src.charCodeAt(start) !== 126) {
    return false;
  }
  if (silent) {
    return false;
  }
  if (start + 4 >= max) {
    return false;
  }
  if (state.src.charCodeAt(start + 1) !== 126) {
    return false;
  }
  if (state.level >= state.options.maxNesting) {
    return false;
  }
  lastChar = start > 0 ? state.src.charCodeAt(start - 1) : -1;
  nextChar = state.src.charCodeAt(start + 2);
  if (lastChar === 126) {
    return false;
  }
  if (nextChar === 126) {
    return false;
  }
  if (nextChar === 32 || nextChar === 10) {
    return false;
  }
  pos = start + 2;
  while (pos < max && state.src.charCodeAt(pos) === 126) {
    pos++;
  }
  if (pos > start + 3) {
    state.pos += pos - start;
    if (!silent) {
      state.pending += state.src.slice(start, pos);
    }
    return true;
  }
  state.pos = start + 2;
  stack = 1;
  while (state.pos + 1 < max) {
    if (state.src.charCodeAt(state.pos) === 126) {
      if (state.src.charCodeAt(state.pos + 1) === 126) {
        lastChar = state.src.charCodeAt(state.pos - 1);
        nextChar = state.pos + 2 < max ? state.src.charCodeAt(state.pos + 2) : -1;
        if (nextChar !== 126 && lastChar !== 126) {
          if (lastChar !== 32 && lastChar !== 10) {
            stack--;
          } else if (nextChar !== 32 && nextChar !== 10) {
            stack++;
          }
          if (stack <= 0) {
            found = true;
            break;
          }
        }
      }
    }
    state.parser.skipToken(state);
  }
  if (!found) {
    state.pos = start;
    return false;
  }
  state.posMax = state.pos;
  state.pos = start + 2;
  if (!silent) {
    state.push({ type: "del_open", level: state.level++ });
    state.parser.tokenize(state);
    state.push({ type: "del_close", level: --state.level });
  }
  state.pos = state.posMax + 2;
  state.posMax = max;
  return true;
}
function ins(state, silent) {
  var found, pos, stack, max = state.posMax, start = state.pos, lastChar, nextChar;
  if (state.src.charCodeAt(start) !== 43) {
    return false;
  }
  if (silent) {
    return false;
  }
  if (start + 4 >= max) {
    return false;
  }
  if (state.src.charCodeAt(start + 1) !== 43) {
    return false;
  }
  if (state.level >= state.options.maxNesting) {
    return false;
  }
  lastChar = start > 0 ? state.src.charCodeAt(start - 1) : -1;
  nextChar = state.src.charCodeAt(start + 2);
  if (lastChar === 43) {
    return false;
  }
  if (nextChar === 43) {
    return false;
  }
  if (nextChar === 32 || nextChar === 10) {
    return false;
  }
  pos = start + 2;
  while (pos < max && state.src.charCodeAt(pos) === 43) {
    pos++;
  }
  if (pos !== start + 2) {
    state.pos += pos - start;
    if (!silent) {
      state.pending += state.src.slice(start, pos);
    }
    return true;
  }
  state.pos = start + 2;
  stack = 1;
  while (state.pos + 1 < max) {
    if (state.src.charCodeAt(state.pos) === 43) {
      if (state.src.charCodeAt(state.pos + 1) === 43) {
        lastChar = state.src.charCodeAt(state.pos - 1);
        nextChar = state.pos + 2 < max ? state.src.charCodeAt(state.pos + 2) : -1;
        if (nextChar !== 43 && lastChar !== 43) {
          if (lastChar !== 32 && lastChar !== 10) {
            stack--;
          } else if (nextChar !== 32 && nextChar !== 10) {
            stack++;
          }
          if (stack <= 0) {
            found = true;
            break;
          }
        }
      }
    }
    state.parser.skipToken(state);
  }
  if (!found) {
    state.pos = start;
    return false;
  }
  state.posMax = state.pos;
  state.pos = start + 2;
  if (!silent) {
    state.push({ type: "ins_open", level: state.level++ });
    state.parser.tokenize(state);
    state.push({ type: "ins_close", level: --state.level });
  }
  state.pos = state.posMax + 2;
  state.posMax = max;
  return true;
}
function mark(state, silent) {
  var found, pos, stack, max = state.posMax, start = state.pos, lastChar, nextChar;
  if (state.src.charCodeAt(start) !== 61) {
    return false;
  }
  if (silent) {
    return false;
  }
  if (start + 4 >= max) {
    return false;
  }
  if (state.src.charCodeAt(start + 1) !== 61) {
    return false;
  }
  if (state.level >= state.options.maxNesting) {
    return false;
  }
  lastChar = start > 0 ? state.src.charCodeAt(start - 1) : -1;
  nextChar = state.src.charCodeAt(start + 2);
  if (lastChar === 61) {
    return false;
  }
  if (nextChar === 61) {
    return false;
  }
  if (nextChar === 32 || nextChar === 10) {
    return false;
  }
  pos = start + 2;
  while (pos < max && state.src.charCodeAt(pos) === 61) {
    pos++;
  }
  if (pos !== start + 2) {
    state.pos += pos - start;
    if (!silent) {
      state.pending += state.src.slice(start, pos);
    }
    return true;
  }
  state.pos = start + 2;
  stack = 1;
  while (state.pos + 1 < max) {
    if (state.src.charCodeAt(state.pos) === 61) {
      if (state.src.charCodeAt(state.pos + 1) === 61) {
        lastChar = state.src.charCodeAt(state.pos - 1);
        nextChar = state.pos + 2 < max ? state.src.charCodeAt(state.pos + 2) : -1;
        if (nextChar !== 61 && lastChar !== 61) {
          if (lastChar !== 32 && lastChar !== 10) {
            stack--;
          } else if (nextChar !== 32 && nextChar !== 10) {
            stack++;
          }
          if (stack <= 0) {
            found = true;
            break;
          }
        }
      }
    }
    state.parser.skipToken(state);
  }
  if (!found) {
    state.pos = start;
    return false;
  }
  state.posMax = state.pos;
  state.pos = start + 2;
  if (!silent) {
    state.push({ type: "mark_open", level: state.level++ });
    state.parser.tokenize(state);
    state.push({ type: "mark_close", level: --state.level });
  }
  state.pos = state.posMax + 2;
  state.posMax = max;
  return true;
}
function isAlphaNum(code2) {
  return code2 >= 48 && code2 <= 57 || code2 >= 65 && code2 <= 90 || code2 >= 97 && code2 <= 122;
}
function scanDelims(state, start) {
  var pos = start, lastChar, nextChar, count, can_open = true, can_close = true, max = state.posMax, marker = state.src.charCodeAt(start);
  lastChar = start > 0 ? state.src.charCodeAt(start - 1) : -1;
  while (pos < max && state.src.charCodeAt(pos) === marker) {
    pos++;
  }
  if (pos >= max) {
    can_open = false;
  }
  count = pos - start;
  if (count >= 4) {
    can_open = can_close = false;
  } else {
    nextChar = pos < max ? state.src.charCodeAt(pos) : -1;
    if (nextChar === 32 || nextChar === 10) {
      can_open = false;
    }
    if (lastChar === 32 || lastChar === 10) {
      can_close = false;
    }
    if (marker === 95) {
      if (isAlphaNum(lastChar)) {
        can_open = false;
      }
      if (isAlphaNum(nextChar)) {
        can_close = false;
      }
    }
  }
  return {
    can_open,
    can_close,
    delims: count
  };
}
function emphasis(state, silent) {
  var startCount, count, found, oldCount, newCount, stack, res, max = state.posMax, start = state.pos, marker = state.src.charCodeAt(start);
  if (marker !== 95 && marker !== 42) {
    return false;
  }
  if (silent) {
    return false;
  }
  res = scanDelims(state, start);
  startCount = res.delims;
  if (!res.can_open) {
    state.pos += startCount;
    if (!silent) {
      state.pending += state.src.slice(start, state.pos);
    }
    return true;
  }
  if (state.level >= state.options.maxNesting) {
    return false;
  }
  state.pos = start + startCount;
  stack = [startCount];
  while (state.pos < max) {
    if (state.src.charCodeAt(state.pos) === marker) {
      res = scanDelims(state, state.pos);
      count = res.delims;
      if (res.can_close) {
        oldCount = stack.pop();
        newCount = count;
        while (oldCount !== newCount) {
          if (newCount < oldCount) {
            stack.push(oldCount - newCount);
            break;
          }
          newCount -= oldCount;
          if (stack.length === 0) {
            break;
          }
          state.pos += oldCount;
          oldCount = stack.pop();
        }
        if (stack.length === 0) {
          startCount = oldCount;
          found = true;
          break;
        }
        state.pos += count;
        continue;
      }
      if (res.can_open) {
        stack.push(count);
      }
      state.pos += count;
      continue;
    }
    state.parser.skipToken(state);
  }
  if (!found) {
    state.pos = start;
    return false;
  }
  state.posMax = state.pos;
  state.pos = start + startCount;
  if (!silent) {
    if (startCount === 2 || startCount === 3) {
      state.push({ type: "strong_open", level: state.level++ });
    }
    if (startCount === 1 || startCount === 3) {
      state.push({ type: "em_open", level: state.level++ });
    }
    state.parser.tokenize(state);
    if (startCount === 1 || startCount === 3) {
      state.push({ type: "em_close", level: --state.level });
    }
    if (startCount === 2 || startCount === 3) {
      state.push({ type: "strong_close", level: --state.level });
    }
  }
  state.pos = state.posMax + startCount;
  state.posMax = max;
  return true;
}
var UNESCAPE_RE = /\\([ \\!"#$%&'()*+,.\/:;<=>?@[\]^_`{|}~-])/g;
function sub(state, silent) {
  var found, content, max = state.posMax, start = state.pos;
  if (state.src.charCodeAt(start) !== 126) {
    return false;
  }
  if (silent) {
    return false;
  }
  if (start + 2 >= max) {
    return false;
  }
  if (state.level >= state.options.maxNesting) {
    return false;
  }
  state.pos = start + 1;
  while (state.pos < max) {
    if (state.src.charCodeAt(state.pos) === 126) {
      found = true;
      break;
    }
    state.parser.skipToken(state);
  }
  if (!found || start + 1 === state.pos) {
    state.pos = start;
    return false;
  }
  content = state.src.slice(start + 1, state.pos);
  if (content.match(/(^|[^\\])(\\\\)*\s/)) {
    state.pos = start;
    return false;
  }
  state.posMax = state.pos;
  state.pos = start + 1;
  if (!silent) {
    state.push({
      type: "sub",
      level: state.level,
      content: content.replace(UNESCAPE_RE, "$1")
    });
  }
  state.pos = state.posMax + 1;
  state.posMax = max;
  return true;
}
var UNESCAPE_RE$1 = /\\([ \\!"#$%&'()*+,.\/:;<=>?@[\]^_`{|}~-])/g;
function sup(state, silent) {
  var found, content, max = state.posMax, start = state.pos;
  if (state.src.charCodeAt(start) !== 94) {
    return false;
  }
  if (silent) {
    return false;
  }
  if (start + 2 >= max) {
    return false;
  }
  if (state.level >= state.options.maxNesting) {
    return false;
  }
  state.pos = start + 1;
  while (state.pos < max) {
    if (state.src.charCodeAt(state.pos) === 94) {
      found = true;
      break;
    }
    state.parser.skipToken(state);
  }
  if (!found || start + 1 === state.pos) {
    state.pos = start;
    return false;
  }
  content = state.src.slice(start + 1, state.pos);
  if (content.match(/(^|[^\\])(\\\\)*\s/)) {
    state.pos = start;
    return false;
  }
  state.posMax = state.pos;
  state.pos = start + 1;
  if (!silent) {
    state.push({
      type: "sup",
      level: state.level,
      content: content.replace(UNESCAPE_RE$1, "$1")
    });
  }
  state.pos = state.posMax + 1;
  state.posMax = max;
  return true;
}
function links(state, silent) {
  var labelStart, labelEnd, label, href, title, pos, ref, code2, isImage = false, oldPos = state.pos, max = state.posMax, start = state.pos, marker = state.src.charCodeAt(start);
  if (marker === 33) {
    isImage = true;
    marker = state.src.charCodeAt(++start);
  }
  if (marker !== 91) {
    return false;
  }
  if (state.level >= state.options.maxNesting) {
    return false;
  }
  labelStart = start + 1;
  labelEnd = parseLinkLabel(state, start);
  if (labelEnd < 0) {
    return false;
  }
  pos = labelEnd + 1;
  if (pos < max && state.src.charCodeAt(pos) === 40) {
    pos++;
    for (; pos < max; pos++) {
      code2 = state.src.charCodeAt(pos);
      if (code2 !== 32 && code2 !== 10) {
        break;
      }
    }
    if (pos >= max) {
      return false;
    }
    start = pos;
    if (parseLinkDestination(state, pos)) {
      href = state.linkContent;
      pos = state.pos;
    } else {
      href = "";
    }
    start = pos;
    for (; pos < max; pos++) {
      code2 = state.src.charCodeAt(pos);
      if (code2 !== 32 && code2 !== 10) {
        break;
      }
    }
    if (pos < max && start !== pos && parseLinkTitle(state, pos)) {
      title = state.linkContent;
      pos = state.pos;
      for (; pos < max; pos++) {
        code2 = state.src.charCodeAt(pos);
        if (code2 !== 32 && code2 !== 10) {
          break;
        }
      }
    } else {
      title = "";
    }
    if (pos >= max || state.src.charCodeAt(pos) !== 41) {
      state.pos = oldPos;
      return false;
    }
    pos++;
  } else {
    if (state.linkLevel > 0) {
      return false;
    }
    for (; pos < max; pos++) {
      code2 = state.src.charCodeAt(pos);
      if (code2 !== 32 && code2 !== 10) {
        break;
      }
    }
    if (pos < max && state.src.charCodeAt(pos) === 91) {
      start = pos + 1;
      pos = parseLinkLabel(state, pos);
      if (pos >= 0) {
        label = state.src.slice(start, pos++);
      } else {
        pos = start - 1;
      }
    }
    if (!label) {
      if (typeof label === "undefined") {
        pos = labelEnd + 1;
      }
      label = state.src.slice(labelStart, labelEnd);
    }
    ref = state.env.references[normalizeReference(label)];
    if (!ref) {
      state.pos = oldPos;
      return false;
    }
    href = ref.href;
    title = ref.title;
  }
  if (!silent) {
    state.pos = labelStart;
    state.posMax = labelEnd;
    if (isImage) {
      state.push({
        type: "image",
        src: href,
        title,
        alt: state.src.substr(labelStart, labelEnd - labelStart),
        level: state.level
      });
    } else {
      state.push({
        type: "link_open",
        href,
        title,
        level: state.level++
      });
      state.linkLevel++;
      state.parser.tokenize(state);
      state.linkLevel--;
      state.push({ type: "link_close", level: --state.level });
    }
  }
  state.pos = pos;
  state.posMax = max;
  return true;
}
function footnote_inline(state, silent) {
  var labelStart, labelEnd, footnoteId, oldLength, max = state.posMax, start = state.pos;
  if (start + 2 >= max) {
    return false;
  }
  if (state.src.charCodeAt(start) !== 94) {
    return false;
  }
  if (state.src.charCodeAt(start + 1) !== 91) {
    return false;
  }
  if (state.level >= state.options.maxNesting) {
    return false;
  }
  labelStart = start + 2;
  labelEnd = parseLinkLabel(state, start + 1);
  if (labelEnd < 0) {
    return false;
  }
  if (!silent) {
    if (!state.env.footnotes) {
      state.env.footnotes = {};
    }
    if (!state.env.footnotes.list) {
      state.env.footnotes.list = [];
    }
    footnoteId = state.env.footnotes.list.length;
    state.pos = labelStart;
    state.posMax = labelEnd;
    state.push({
      type: "footnote_ref",
      id: footnoteId,
      level: state.level
    });
    state.linkLevel++;
    oldLength = state.tokens.length;
    state.parser.tokenize(state);
    state.env.footnotes.list[footnoteId] = { tokens: state.tokens.splice(oldLength) };
    state.linkLevel--;
  }
  state.pos = labelEnd + 1;
  state.posMax = max;
  return true;
}
function footnote_ref(state, silent) {
  var label, pos, footnoteId, footnoteSubId, max = state.posMax, start = state.pos;
  if (start + 3 > max) {
    return false;
  }
  if (!state.env.footnotes || !state.env.footnotes.refs) {
    return false;
  }
  if (state.src.charCodeAt(start) !== 91) {
    return false;
  }
  if (state.src.charCodeAt(start + 1) !== 94) {
    return false;
  }
  if (state.level >= state.options.maxNesting) {
    return false;
  }
  for (pos = start + 2; pos < max; pos++) {
    if (state.src.charCodeAt(pos) === 32) {
      return false;
    }
    if (state.src.charCodeAt(pos) === 10) {
      return false;
    }
    if (state.src.charCodeAt(pos) === 93) {
      break;
    }
  }
  if (pos === start + 2) {
    return false;
  }
  if (pos >= max) {
    return false;
  }
  pos++;
  label = state.src.slice(start + 2, pos - 1);
  if (typeof state.env.footnotes.refs[":" + label] === "undefined") {
    return false;
  }
  if (!silent) {
    if (!state.env.footnotes.list) {
      state.env.footnotes.list = [];
    }
    if (state.env.footnotes.refs[":" + label] < 0) {
      footnoteId = state.env.footnotes.list.length;
      state.env.footnotes.list[footnoteId] = { label, count: 0 };
      state.env.footnotes.refs[":" + label] = footnoteId;
    } else {
      footnoteId = state.env.footnotes.refs[":" + label];
    }
    footnoteSubId = state.env.footnotes.list[footnoteId].count;
    state.env.footnotes.list[footnoteId].count++;
    state.push({
      type: "footnote_ref",
      id: footnoteId,
      subId: footnoteSubId,
      level: state.level
    });
  }
  state.pos = pos;
  state.posMax = max;
  return true;
}
var url_schemas = [
  "coap",
  "doi",
  "javascript",
  "aaa",
  "aaas",
  "about",
  "acap",
  "cap",
  "cid",
  "crid",
  "data",
  "dav",
  "dict",
  "dns",
  "file",
  "ftp",
  "geo",
  "go",
  "gopher",
  "h323",
  "http",
  "https",
  "iax",
  "icap",
  "im",
  "imap",
  "info",
  "ipp",
  "iris",
  "iris.beep",
  "iris.xpc",
  "iris.xpcs",
  "iris.lwz",
  "ldap",
  "mailto",
  "mid",
  "msrp",
  "msrps",
  "mtqp",
  "mupdate",
  "news",
  "nfs",
  "ni",
  "nih",
  "nntp",
  "opaquelocktoken",
  "pop",
  "pres",
  "rtsp",
  "service",
  "session",
  "shttp",
  "sieve",
  "sip",
  "sips",
  "sms",
  "snmp",
  "soap.beep",
  "soap.beeps",
  "tag",
  "tel",
  "telnet",
  "tftp",
  "thismessage",
  "tn3270",
  "tip",
  "tv",
  "urn",
  "vemmi",
  "ws",
  "wss",
  "xcon",
  "xcon-userid",
  "xmlrpc.beep",
  "xmlrpc.beeps",
  "xmpp",
  "z39.50r",
  "z39.50s",
  "adiumxtra",
  "afp",
  "afs",
  "aim",
  "apt",
  "attachment",
  "aw",
  "beshare",
  "bitcoin",
  "bolo",
  "callto",
  "chrome",
  "chrome-extension",
  "com-eventbrite-attendee",
  "content",
  "cvs",
  "dlna-playsingle",
  "dlna-playcontainer",
  "dtn",
  "dvb",
  "ed2k",
  "facetime",
  "feed",
  "finger",
  "fish",
  "gg",
  "git",
  "gizmoproject",
  "gtalk",
  "hcp",
  "icon",
  "ipn",
  "irc",
  "irc6",
  "ircs",
  "itms",
  "jar",
  "jms",
  "keyparc",
  "lastfm",
  "ldaps",
  "magnet",
  "maps",
  "market",
  "message",
  "mms",
  "ms-help",
  "msnim",
  "mumble",
  "mvn",
  "notes",
  "oid",
  "palm",
  "paparazzi",
  "platform",
  "proxy",
  "psyc",
  "query",
  "res",
  "resource",
  "rmi",
  "rsync",
  "rtmp",
  "secondlife",
  "sftp",
  "sgn",
  "skype",
  "smb",
  "soldat",
  "spotify",
  "ssh",
  "steam",
  "svn",
  "teamspeak",
  "things",
  "udp",
  "unreal",
  "ut2004",
  "ventrilo",
  "view-source",
  "webcal",
  "wtai",
  "wyciwyg",
  "xfire",
  "xri",
  "ymsgr"
];
var EMAIL_RE = /^<([a-zA-Z0-9.!#$%&'*+\/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*)>/;
var AUTOLINK_RE = /^<([a-zA-Z.\-]{1,25}):([^<>\x00-\x20]*)>/;
function autolink(state, silent) {
  var tail, linkMatch, emailMatch, url, fullUrl, pos = state.pos;
  if (state.src.charCodeAt(pos) !== 60) {
    return false;
  }
  tail = state.src.slice(pos);
  if (tail.indexOf(">") < 0) {
    return false;
  }
  linkMatch = tail.match(AUTOLINK_RE);
  if (linkMatch) {
    if (url_schemas.indexOf(linkMatch[1].toLowerCase()) < 0) {
      return false;
    }
    url = linkMatch[0].slice(1, -1);
    fullUrl = normalizeLink(url);
    if (!state.parser.validateLink(url)) {
      return false;
    }
    if (!silent) {
      state.push({
        type: "link_open",
        href: fullUrl,
        level: state.level
      });
      state.push({
        type: "text",
        content: url,
        level: state.level + 1
      });
      state.push({ type: "link_close", level: state.level });
    }
    state.pos += linkMatch[0].length;
    return true;
  }
  emailMatch = tail.match(EMAIL_RE);
  if (emailMatch) {
    url = emailMatch[0].slice(1, -1);
    fullUrl = normalizeLink("mailto:" + url);
    if (!state.parser.validateLink(fullUrl)) {
      return false;
    }
    if (!silent) {
      state.push({
        type: "link_open",
        href: fullUrl,
        level: state.level
      });
      state.push({
        type: "text",
        content: url,
        level: state.level + 1
      });
      state.push({ type: "link_close", level: state.level });
    }
    state.pos += emailMatch[0].length;
    return true;
  }
  return false;
}
function replace$1(regex, options) {
  regex = regex.source;
  options = options || "";
  return function self(name, val) {
    if (!name) {
      return new RegExp(regex, options);
    }
    val = val.source || val;
    regex = regex.replace(name, val);
    return self;
  };
}
var attr_name = /[a-zA-Z_:][a-zA-Z0-9:._-]*/;
var unquoted = /[^"'=<>`\x00-\x20]+/;
var single_quoted = /'[^']*'/;
var double_quoted = /"[^"]*"/;
var attr_value = replace$1(/(?:unquoted|single_quoted|double_quoted)/)("unquoted", unquoted)("single_quoted", single_quoted)("double_quoted", double_quoted)();
var attribute = replace$1(/(?:\s+attr_name(?:\s*=\s*attr_value)?)/)("attr_name", attr_name)("attr_value", attr_value)();
var open_tag = replace$1(/<[A-Za-z][A-Za-z0-9]*attribute*\s*\/?>/)("attribute", attribute)();
var close_tag = /<\/[A-Za-z][A-Za-z0-9]*\s*>/;
var comment = /<!---->|<!--(?:-?[^>-])(?:-?[^-])*-->/;
var processing = /<[?].*?[?]>/;
var declaration = /<![A-Z]+\s+[^>]*>/;
var cdata = /<!\[CDATA\[[\s\S]*?\]\]>/;
var HTML_TAG_RE = replace$1(/^(?:open_tag|close_tag|comment|processing|declaration|cdata)/)("open_tag", open_tag)("close_tag", close_tag)("comment", comment)("processing", processing)("declaration", declaration)("cdata", cdata)();
function isLetter$2(ch) {
  var lc = ch | 32;
  return lc >= 97 && lc <= 122;
}
function htmltag(state, silent) {
  var ch, match, max, pos = state.pos;
  if (!state.options.html) {
    return false;
  }
  max = state.posMax;
  if (state.src.charCodeAt(pos) !== 60 || pos + 2 >= max) {
    return false;
  }
  ch = state.src.charCodeAt(pos + 1);
  if (ch !== 33 && ch !== 63 && ch !== 47 && !isLetter$2(ch)) {
    return false;
  }
  match = state.src.slice(pos).match(HTML_TAG_RE);
  if (!match) {
    return false;
  }
  if (!silent) {
    state.push({
      type: "htmltag",
      content: state.src.slice(pos, pos + match[0].length),
      level: state.level
    });
  }
  state.pos += match[0].length;
  return true;
}
var DIGITAL_RE = /^&#((?:x[a-f0-9]{1,8}|[0-9]{1,8}));/i;
var NAMED_RE = /^&([a-z][a-z0-9]{1,31});/i;
function entity(state, silent) {
  var ch, code2, match, pos = state.pos, max = state.posMax;
  if (state.src.charCodeAt(pos) !== 38) {
    return false;
  }
  if (pos + 1 < max) {
    ch = state.src.charCodeAt(pos + 1);
    if (ch === 35) {
      match = state.src.slice(pos).match(DIGITAL_RE);
      if (match) {
        if (!silent) {
          code2 = match[1][0].toLowerCase() === "x" ? parseInt(match[1].slice(1), 16) : parseInt(match[1], 10);
          state.pending += isValidEntityCode(code2) ? fromCodePoint(code2) : fromCodePoint(65533);
        }
        state.pos += match[0].length;
        return true;
      }
    } else {
      match = state.src.slice(pos).match(NAMED_RE);
      if (match) {
        var decoded = decodeEntity(match[1]);
        if (match[1] !== decoded) {
          if (!silent) {
            state.pending += decoded;
          }
          state.pos += match[0].length;
          return true;
        }
      }
    }
  }
  if (!silent) {
    state.pending += "&";
  }
  state.pos++;
  return true;
}
var _rules$2 = [
  ["text", text],
  ["newline", newline],
  ["escape", escape],
  ["backticks", backticks],
  ["del", del],
  ["ins", ins],
  ["mark", mark],
  ["emphasis", emphasis],
  ["sub", sub],
  ["sup", sup],
  ["links", links],
  ["footnote_inline", footnote_inline],
  ["footnote_ref", footnote_ref],
  ["autolink", autolink],
  ["htmltag", htmltag],
  ["entity", entity]
];
function ParserInline() {
  this.ruler = new Ruler();
  for (var i = 0; i < _rules$2.length; i++) {
    this.ruler.push(_rules$2[i][0], _rules$2[i][1]);
  }
  this.validateLink = validateLink;
}
ParserInline.prototype.skipToken = function(state) {
  var rules2 = this.ruler.getRules("");
  var len = rules2.length;
  var pos = state.pos;
  var i, cached_pos;
  if ((cached_pos = state.cacheGet(pos)) > 0) {
    state.pos = cached_pos;
    return;
  }
  for (i = 0; i < len; i++) {
    if (rules2[i](state, true)) {
      state.cacheSet(pos, state.pos);
      return;
    }
  }
  state.pos++;
  state.cacheSet(pos, state.pos);
};
ParserInline.prototype.tokenize = function(state) {
  var rules2 = this.ruler.getRules("");
  var len = rules2.length;
  var end = state.posMax;
  var ok, i;
  while (state.pos < end) {
    for (i = 0; i < len; i++) {
      ok = rules2[i](state, false);
      if (ok) {
        break;
      }
    }
    if (ok) {
      if (state.pos >= end) {
        break;
      }
      continue;
    }
    state.pending += state.src[state.pos++];
  }
  if (state.pending) {
    state.pushPending();
  }
};
ParserInline.prototype.parse = function(str, options, env, outTokens) {
  var state = new StateInline(str, this, options, env, outTokens);
  this.tokenize(state);
};
function validateLink(url) {
  var BAD_PROTOCOLS = ["vbscript", "javascript", "file", "data"];
  var str = url.trim().toLowerCase();
  str = replaceEntities(str);
  if (str.indexOf(":") !== -1 && BAD_PROTOCOLS.indexOf(str.split(":")[0]) !== -1) {
    return false;
  }
  return true;
}
var defaultConfig = {
  options: {
    html: false,
    // Enable HTML tags in source
    xhtmlOut: false,
    // Use '/' to close single tags (<br />)
    breaks: false,
    // Convert '\n' in paragraphs into <br>
    langPrefix: "language-",
    // CSS language prefix for fenced blocks
    linkTarget: "",
    // set target to open link in
    // Enable some language-neutral replacements + quotes beautification
    typographer: false,
    // Double + single quotes replacement pairs, when typographer enabled,
    // and smartquotes on. Set doubles to '«»' for Russian, '„“' for German.
    quotes: "“”‘’",
    // Highlighter function. Should return escaped HTML,
    // or '' if input not changed
    //
    // function (/*str, lang*/) { return ''; }
    //
    highlight: null,
    maxNesting: 20
    // Internal protection, recursion limit
  },
  components: {
    core: {
      rules: [
        "block",
        "inline",
        "references",
        "replacements",
        "smartquotes",
        "references",
        "abbr2",
        "footnote_tail"
      ]
    },
    block: {
      rules: [
        "blockquote",
        "code",
        "fences",
        "footnote",
        "heading",
        "hr",
        "htmlblock",
        "lheading",
        "list",
        "paragraph",
        "table"
      ]
    },
    inline: {
      rules: [
        "autolink",
        "backticks",
        "del",
        "emphasis",
        "entity",
        "escape",
        "footnote_ref",
        "htmltag",
        "links",
        "newline",
        "text"
      ]
    }
  }
};
var fullConfig = {
  options: {
    html: false,
    // Enable HTML tags in source
    xhtmlOut: false,
    // Use '/' to close single tags (<br />)
    breaks: false,
    // Convert '\n' in paragraphs into <br>
    langPrefix: "language-",
    // CSS language prefix for fenced blocks
    linkTarget: "",
    // set target to open link in
    // Enable some language-neutral replacements + quotes beautification
    typographer: false,
    // Double + single quotes replacement pairs, when typographer enabled,
    // and smartquotes on. Set doubles to '«»' for Russian, '„“' for German.
    quotes: "“”‘’",
    // Highlighter function. Should return escaped HTML,
    // or '' if input not changed
    //
    // function (/*str, lang*/) { return ''; }
    //
    highlight: null,
    maxNesting: 20
    // Internal protection, recursion limit
  },
  components: {
    // Don't restrict core/block/inline rules
    core: {},
    block: {},
    inline: {}
  }
};
var commonmarkConfig = {
  options: {
    html: true,
    // Enable HTML tags in source
    xhtmlOut: true,
    // Use '/' to close single tags (<br />)
    breaks: false,
    // Convert '\n' in paragraphs into <br>
    langPrefix: "language-",
    // CSS language prefix for fenced blocks
    linkTarget: "",
    // set target to open link in
    // Enable some language-neutral replacements + quotes beautification
    typographer: false,
    // Double + single quotes replacement pairs, when typographer enabled,
    // and smartquotes on. Set doubles to '«»' for Russian, '„“' for German.
    quotes: "“”‘’",
    // Highlighter function. Should return escaped HTML,
    // or '' if input not changed
    //
    // function (/*str, lang*/) { return ''; }
    //
    highlight: null,
    maxNesting: 20
    // Internal protection, recursion limit
  },
  components: {
    core: {
      rules: [
        "block",
        "inline",
        "references",
        "abbr2"
      ]
    },
    block: {
      rules: [
        "blockquote",
        "code",
        "fences",
        "heading",
        "hr",
        "htmlblock",
        "lheading",
        "list",
        "paragraph"
      ]
    },
    inline: {
      rules: [
        "autolink",
        "backticks",
        "emphasis",
        "entity",
        "escape",
        "htmltag",
        "links",
        "newline",
        "text"
      ]
    }
  }
};
var config = {
  "default": defaultConfig,
  "full": fullConfig,
  "commonmark": commonmarkConfig
};
function StateCore(instance, str, env) {
  this.src = str;
  this.env = env;
  this.options = instance.options;
  this.tokens = [];
  this.inlineMode = false;
  this.inline = instance.inline;
  this.block = instance.block;
  this.renderer = instance.renderer;
  this.typographer = instance.typographer;
}
function Remarkable(preset, options) {
  if (typeof preset !== "string") {
    options = preset;
    preset = "default";
  }
  if (options && options.linkify != null) {
    console.warn(
      "linkify option is removed. Use linkify plugin instead:\n\nimport Remarkable from 'remarkable';\nimport linkify from 'remarkable/linkify';\nnew Remarkable().use(linkify)\n"
    );
  }
  this.inline = new ParserInline();
  this.block = new ParserBlock();
  this.core = new Core();
  this.renderer = new Renderer();
  this.ruler = new Ruler();
  this.options = {};
  this.configure(config[preset]);
  this.set(options || {});
}
Remarkable.prototype.set = function(options) {
  assign(this.options, options);
};
Remarkable.prototype.configure = function(presets) {
  var self = this;
  if (!presets) {
    throw new Error("Wrong `remarkable` preset, check name/content");
  }
  if (presets.options) {
    self.set(presets.options);
  }
  if (presets.components) {
    Object.keys(presets.components).forEach(function(name) {
      if (presets.components[name].rules) {
        self[name].ruler.enable(presets.components[name].rules, true);
      }
    });
  }
};
Remarkable.prototype.use = function(plugin, options) {
  plugin(this, options);
  return this;
};
Remarkable.prototype.parse = function(str, env) {
  var state = new StateCore(this, str, env);
  this.core.process(state);
  return state.tokens;
};
Remarkable.prototype.render = function(str, env) {
  env = env || {};
  return this.renderer.render(this.parse(str, env), this.options, env);
};
Remarkable.prototype.parseInline = function(str, env) {
  var state = new StateCore(this, str, env);
  state.inlineMode = true;
  this.core.process(state);
  return state.tokens;
};
Remarkable.prototype.renderInline = function(str, env) {
  env = env || {};
  return this.renderer.render(this.parseInline(str, env), this.options, env);
};
export {
  Remarkable as R
};
