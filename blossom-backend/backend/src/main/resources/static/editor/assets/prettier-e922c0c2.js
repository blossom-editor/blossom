var au = Object.create;
var $e = Object.defineProperty;
var Du = Object.getOwnPropertyDescriptor;
var cu = Object.getOwnPropertyNames;
var lu = Object.getPrototypeOf, fu = Object.prototype.hasOwnProperty;
var pu = (t, e) => () => (t && (e = t(t = 0)), e);
var Ue = (t, e) => () => (e || t((e = { exports: {} }).exports, e), e.exports), Me = (t, e) => {
  for (var r in e)
    $e(t, r, { get: e[r], enumerable: true });
}, nr$1 = (t, e, r, n) => {
  if (e && typeof e == "object" || typeof e == "function")
    for (let o of cu(e))
      !fu.call(t, o) && o !== r && $e(t, o, { get: () => e[o], enumerable: !(n = Du(e, o)) || n.enumerable });
  return t;
};
var Ce$1 = (t, e, r) => (r = t != null ? au(lu(t)) : {}, nr$1(e || !t || !t.__esModule ? $e(r, "default", { value: t, enumerable: true }) : r, t)), du = (t) => nr$1($e({}, "__esModule", { value: true }), t);
var Fu = (t, e, r) => {
  if (!e.has(t))
    throw TypeError("Cannot " + r);
};
var Ct$1 = (t, e, r) => {
  if (e.has(t))
    throw TypeError("Cannot add the same private member more than once");
  e instanceof WeakSet ? e.add(t) : e.set(t, r);
};
var ce$1 = (t, e, r) => (Fu(t, e, "access private method"), r);
var or$1 = Ue((gt2) => {
  Object.defineProperty(gt2, "__esModule", { value: true });
  gt2.default = ur2;
  function ur2() {
  }
  ur2.prototype = { diff: function(e, r) {
    var n = arguments.length > 2 && arguments[2] !== void 0 ? arguments[2] : {}, o = n.callback;
    typeof n == "function" && (o = n, n = {}), this.options = n;
    var u = this;
    function i(p) {
      return o ? (setTimeout(function() {
        o(void 0, p);
      }, 0), true) : p;
    }
    e = this.castInput(e), r = this.castInput(r), e = this.removeEmpty(this.tokenize(e)), r = this.removeEmpty(this.tokenize(r));
    var s = r.length, a = e.length, D = 1, c = s + a;
    n.maxEditLength && (c = Math.min(c, n.maxEditLength));
    var F = [{ newPos: -1, components: [] }], f = this.extractCommon(F[0], r, e, 0);
    if (F[0].newPos + 1 >= s && f + 1 >= a)
      return i([{ value: this.join(r), count: r.length }]);
    function d() {
      for (var p = -1 * D; p <= D; p += 2) {
        var m = void 0, E = F[p - 1], h = F[p + 1], g = (h ? h.newPos : 0) - p;
        E && (F[p - 1] = void 0);
        var C2 = E && E.newPos + 1 < s, _ = h && 0 <= g && g < a;
        if (!C2 && !_) {
          F[p] = void 0;
          continue;
        }
        if (!C2 || _ && E.newPos < h.newPos ? (m = hu2(h), u.pushComponent(m.components, void 0, true)) : (m = E, m.newPos++, u.pushComponent(m.components, true, void 0)), g = u.extractCommon(m, r, e, p), m.newPos + 1 >= s && g + 1 >= a)
          return i(Eu(u, m.components, r, e, u.useLongestToken));
        F[p] = m;
      }
      D++;
    }
    if (o)
      (function p() {
        setTimeout(function() {
          if (D > c)
            return o();
          d() || p();
        }, 0);
      })();
    else
      for (; D <= c; ) {
        var l = d();
        if (l)
          return l;
      }
  }, pushComponent: function(e, r, n) {
    var o = e[e.length - 1];
    o && o.added === r && o.removed === n ? e[e.length - 1] = { count: o.count + 1, added: r, removed: n } : e.push({ count: 1, added: r, removed: n });
  }, extractCommon: function(e, r, n, o) {
    for (var u = r.length, i = n.length, s = e.newPos, a = s - o, D = 0; s + 1 < u && a + 1 < i && this.equals(r[s + 1], n[a + 1]); )
      s++, a++, D++;
    return D && e.components.push({ count: D }), e.newPos = s, a;
  }, equals: function(e, r) {
    return this.options.comparator ? this.options.comparator(e, r) : e === r || this.options.ignoreCase && e.toLowerCase() === r.toLowerCase();
  }, removeEmpty: function(e) {
    for (var r = [], n = 0; n < e.length; n++)
      e[n] && r.push(e[n]);
    return r;
  }, castInput: function(e) {
    return e;
  }, tokenize: function(e) {
    return e.split("");
  }, join: function(e) {
    return e.join("");
  } };
  function Eu(t, e, r, n, o) {
    for (var u = 0, i = e.length, s = 0, a = 0; u < i; u++) {
      var D = e[u];
      if (D.removed) {
        if (D.value = t.join(n.slice(a, a + D.count)), a += D.count, u && e[u - 1].added) {
          var F = e[u - 1];
          e[u - 1] = e[u], e[u] = F;
        }
      } else {
        if (!D.added && o) {
          var c = r.slice(s, s + D.count);
          c = c.map(function(d, l) {
            var p = n[a + l];
            return p.length > d.length ? p : d;
          }), D.value = t.join(c);
        } else
          D.value = t.join(r.slice(s, s + D.count));
        s += D.count, D.added || (a += D.count);
      }
    }
    var f = e[i - 1];
    return i > 1 && typeof f.value == "string" && (f.added || f.removed) && t.equals("", f.value) && (e[i - 2].value += f.value, e.pop()), e;
  }
  function hu2(t) {
    return { newPos: t.newPos, components: t.components.slice(0) };
  }
});
var ir$1 = Ue((ye) => {
  Object.defineProperty(ye, "__esModule", { value: true });
  ye.diffArrays = yu;
  ye.arrayDiff = void 0;
  var Cu2 = gu(or$1());
  function gu(t) {
    return t && t.__esModule ? t : { default: t };
  }
  var ge = new Cu2.default();
  ye.arrayDiff = ge;
  ge.tokenize = function(t) {
    return t.slice();
  };
  ge.join = ge.removeEmpty = function(t) {
    return t;
  };
  function yu(t, e, r) {
    return ge.diff(t, e, r);
  }
});
var Pe$1 = Ue((ss, Rr) => {
  var Ir2 = new Proxy(String, { get: () => Ir2 });
  Rr.exports = Ir2;
});
var ln = {};
Me(ln, { default: () => to, shouldHighlight: () => eo });
var eo, to, fn = pu(() => {
  eo = () => false, to = String;
});
var hn = Ue((pt2) => {
  Object.defineProperty(pt2, "__esModule", { value: true });
  pt2.codeFrameColumns = En;
  pt2.default = io2;
  var pn = (fn(), du(ln)), ro = Pe$1(), dn = ro, jt;
  function no(t) {
    if (t) {
      return jt != null || (jt = new dn.constructor({ enabled: true, level: 1 })), jt;
    }
    return dn;
  }
  var Fn = false;
  function uo(t) {
    return { gutter: t.grey, marker: t.red.bold, message: t.red.bold };
  }
  var mn = /\r\n|[\n\r\u2028\u2029]/;
  function oo(t, e, r) {
    let n = Object.assign({ column: 0, line: -1 }, t.start), o = Object.assign({}, n, t.end), { linesAbove: u = 2, linesBelow: i = 3 } = r || {}, s = n.line, a = n.column, D = o.line, c = o.column, F = Math.max(s - (u + 1), 0), f = Math.min(e.length, D + i);
    s === -1 && (F = 0), D === -1 && (f = e.length);
    let d = D - s, l = {};
    if (d)
      for (let p = 0; p <= d; p++) {
        let m = p + s;
        if (!a)
          l[m] = true;
        else if (p === 0) {
          let E = e[m - 1].length;
          l[m] = [a, E - a + 1];
        } else if (p === d)
          l[m] = [0, c];
        else {
          let E = e[m - p].length;
          l[m] = [0, E];
        }
      }
    else
      a === c ? a ? l[s] = [a, 0] : l[s] = true : l[s] = [a, c - a];
    return { start: F, end: f, markerLines: l };
  }
  function En(t, e, r = {}) {
    let n = (r.highlightCode || r.forceColor) && (0, pn.shouldHighlight)(r), o = no(r.forceColor), u = uo(o), i = (p, m) => n ? p(m) : m, s = t.split(mn), { start: a, end: D, markerLines: c } = oo(e, s, r), F = e.start && typeof e.start.column == "number", f = String(D).length, l = (n ? (0, pn.default)(t, r) : t).split(mn, D).slice(a, D).map((p, m) => {
      let E = a + 1 + m, g = ` ${` ${E}`.slice(-f)} |`, C2 = c[E], _ = !c[E + 1];
      if (C2) {
        let Z2 = "";
        if (Array.isArray(C2)) {
          let $2 = p.slice(0, Math.max(C2[0] - 1, 0)).replace(/[^\t]/g, " "), Q2 = C2[1] || 1;
          Z2 = [`
 `, i(u.gutter, g.replace(/\d/g, " ")), " ", $2, i(u.marker, "^").repeat(Q2)].join(""), _ && r.message && (Z2 += " " + i(u.message, r.message));
        }
        return [i(u.marker, ">"), i(u.gutter, g), p.length > 0 ? ` ${p}` : "", Z2].join("");
      } else
        return ` ${i(u.gutter, g)}${p.length > 0 ? ` ${p}` : ""}`;
    }).join(`
`);
    return r.message && !F && (l = `${" ".repeat(f + 1)}${r.message}
${l}`), n ? o.reset(l) : l;
  }
  function io2(t, e, r, n = {}) {
    if (!Fn) {
      Fn = true;
      let u = "Passing lineNumber and colNumber is deprecated to @babel/code-frame. Please use `codeFrameColumns`.";
      {
        let i = new Error(u);
        i.name = "DeprecationWarning", console.warn(new Error(u));
      }
    }
    return r = Math.max(r, 0), En(t, { start: { column: r, line: e } }, n);
  }
});
var tr$1 = {};
Me(tr$1, { __debug: () => Qo$1, check: () => Xo, doc: () => er$1, format: () => su, formatWithCursor: () => iu, getSupportInfo: () => Zo, util: () => Qt, version: () => ou });
var mu = (t, e, r, n) => {
  if (!(t && e == null))
    return e.replaceAll ? e.replaceAll(r, n) : r.global ? e.replace(r, n) : e.split(r).join(n);
}, ee$1 = mu;
var Wn$1 = Ce$1(ir$1(), 1);
var U$1 = "string", j = "array", M = "cursor", T = "indent", S = "align", v = "trim", A = "group", k = "fill", B = "if-break", P = "indent-if-break", L = "line-suffix", I = "line-suffix-boundary", x = "line", O$1 = "label", b = "break-parent", We = /* @__PURE__ */ new Set([M, T, S, v, A, k, B, P, L, I, x, O$1, b]);
function xu$1(t) {
  if (typeof t == "string")
    return U$1;
  if (Array.isArray(t))
    return j;
  if (!t)
    return;
  let { type: e } = t;
  if (We.has(e))
    return e;
}
var W$1 = xu$1;
var _u = (t) => new Intl.ListFormat("en-US", { type: "disjunction" }).format(t);
function Au(t) {
  let e = t === null ? "null" : typeof t;
  if (e !== "string" && e !== "object")
    return `Unexpected doc '${e}', 
Expected it to be 'string' or 'object'.`;
  if (W$1(t))
    throw new Error("doc is valid.");
  let r = Object.prototype.toString.call(t);
  if (r !== "[object Object]")
    return `Unexpected doc '${r}'.`;
  let n = _u([...We].map((o) => `'${o}'`));
  return `Unexpected doc.type '${t.type}'.
Expected it to be ${n}.`;
}
var yt = class extends Error {
  name = "InvalidDocError";
  constructor(e) {
    super(Au(e)), this.doc = e;
  }
}, q = yt;
var sr$1 = {};
function Bu(t, e, r, n) {
  let o = [t];
  for (; o.length > 0; ) {
    let u = o.pop();
    if (u === sr$1) {
      r(o.pop());
      continue;
    }
    r && o.push(u, sr$1);
    let i = W$1(u);
    if (!i)
      throw new q(u);
    if ((e == null ? void 0 : e(u)) !== false)
      switch (i) {
        case j:
        case k: {
          let s = i === j ? u : u.parts;
          for (let a = s.length, D = a - 1; D >= 0; --D)
            o.push(s[D]);
          break;
        }
        case B:
          o.push(u.flatContents, u.breakContents);
          break;
        case A:
          if (n && u.expandedStates)
            for (let s = u.expandedStates.length, a = s - 1; a >= 0; --a)
              o.push(u.expandedStates[a]);
          else
            o.push(u.contents);
          break;
        case S:
        case T:
        case P:
        case O$1:
        case L:
          o.push(u.contents);
          break;
        case U$1:
        case M:
        case v:
        case I:
        case x:
        case b:
          break;
        default:
          throw new q(u);
      }
  }
}
var xe = Bu;
var ar$1 = () => {
}, ze$1 = ar$1;
function ie(t) {
  return { type: T, contents: t };
}
function oe$1(t, e) {
  return { type: S, contents: e, n: t };
}
function xt$1(t, e = {}) {
  return ze$1(e.expandedStates), { type: A, id: e.id, contents: t, break: !!e.shouldBreak, expandedStates: e.expandedStates };
}
function Dr(t) {
  return oe$1(Number.NEGATIVE_INFINITY, t);
}
function cr$1(t) {
  return oe$1({ type: "root" }, t);
}
function lr$1(t) {
  return oe$1(-1, t);
}
function fr(t, e) {
  return xt$1(t[0], { ...e, expandedStates: t });
}
function Ge(t) {
  return { type: k, parts: t };
}
function pr(t, e = "", r = {}) {
  return { type: B, breakContents: t, flatContents: e, groupId: r.groupId };
}
function dr(t, e) {
  return { type: P, contents: t, groupId: e.groupId, negate: e.negate };
}
function _e$1(t) {
  return { type: L, contents: t };
}
var Fr = { type: I }, le$1 = { type: b }, mr = { type: v }, Ae$1 = { type: x, hard: true }, _t = { type: x, hard: true, literal: true }, Ke = { type: x }, Er = { type: x, soft: true }, G$1 = [Ae$1, le$1], He = [_t, le$1], Be = { type: M };
function ke(t, e) {
  let r = [];
  for (let n = 0; n < e.length; n++)
    n !== 0 && r.push(t), r.push(e[n]);
  return r;
}
function qe$1(t, e, r) {
  let n = t;
  if (e > 0) {
    for (let o = 0; o < Math.floor(e / r); ++o)
      n = ie(n);
    n = oe$1(e % r, n), n = oe$1(Number.NEGATIVE_INFINITY, n);
  }
  return n;
}
function hr(t, e) {
  return t ? { type: O$1, label: t, contents: e } : e;
}
var ku = (t, e, r) => {
  if (!(t && e == null))
    return Array.isArray(e) || typeof e == "string" ? e[r < 0 ? e.length + r : r] : e.at(r);
}, y = ku;
function Cr(t) {
  let e = t.indexOf("\r");
  return e >= 0 ? t.charAt(e + 1) === `
` ? "crlf" : "cr" : "lf";
}
function be$1(t) {
  switch (t) {
    case "cr":
      return "\r";
    case "crlf":
      return `\r
`;
    default:
      return `
`;
  }
}
function At(t, e) {
  let r;
  switch (e) {
    case `
`:
      r = /\n/g;
      break;
    case "\r":
      r = /\r/g;
      break;
    case `\r
`:
      r = /\r\n/g;
      break;
    default:
      throw new Error(`Unexpected "eol" ${JSON.stringify(e)}.`);
  }
  let n = t.match(r);
  return n ? n.length : 0;
}
function gr(t) {
  return ee$1(false, t, /\r\n?/g, `
`);
}
var yr = () => /[#*0-9]\uFE0F?\u20E3|[\xA9\xAE\u203C\u2049\u2122\u2139\u2194-\u2199\u21A9\u21AA\u231A\u231B\u2328\u23CF\u23ED-\u23EF\u23F1\u23F2\u23F8-\u23FA\u24C2\u25AA\u25AB\u25B6\u25C0\u25FB\u25FC\u25FE\u2600-\u2604\u260E\u2611\u2614\u2615\u2618\u2620\u2622\u2623\u2626\u262A\u262E\u262F\u2638-\u263A\u2640\u2642\u2648-\u2653\u265F\u2660\u2663\u2665\u2666\u2668\u267B\u267E\u267F\u2692\u2694-\u2697\u2699\u269B\u269C\u26A0\u26A7\u26AA\u26B0\u26B1\u26BD\u26BE\u26C4\u26C8\u26CF\u26D1\u26D3\u26E9\u26F0-\u26F5\u26F7\u26F8\u26FA\u2702\u2708\u2709\u270F\u2712\u2714\u2716\u271D\u2721\u2733\u2734\u2744\u2747\u2757\u2763\u27A1\u2934\u2935\u2B05-\u2B07\u2B1B\u2B1C\u2B55\u3030\u303D\u3297\u3299]\uFE0F?|[\u261D\u270C\u270D](?:\uFE0F|\uD83C[\uDFFB-\uDFFF])?|[\u270A\u270B](?:\uD83C[\uDFFB-\uDFFF])?|[\u23E9-\u23EC\u23F0\u23F3\u25FD\u2693\u26A1\u26AB\u26C5\u26CE\u26D4\u26EA\u26FD\u2705\u2728\u274C\u274E\u2753-\u2755\u2795-\u2797\u27B0\u27BF\u2B50]|\u26F9(?:\uFE0F|\uD83C[\uDFFB-\uDFFF])?(?:\u200D[\u2640\u2642]\uFE0F?)?|\u2764\uFE0F?(?:\u200D(?:\uD83D\uDD25|\uD83E\uDE79))?|\uD83C(?:[\uDC04\uDD70\uDD71\uDD7E\uDD7F\uDE02\uDE37\uDF21\uDF24-\uDF2C\uDF36\uDF7D\uDF96\uDF97\uDF99-\uDF9B\uDF9E\uDF9F\uDFCD\uDFCE\uDFD4-\uDFDF\uDFF5\uDFF7]\uFE0F?|[\uDF85\uDFC2\uDFC7](?:\uD83C[\uDFFB-\uDFFF])?|[\uDFC3\uDFC4\uDFCA](?:\uD83C[\uDFFB-\uDFFF])?(?:\u200D[\u2640\u2642]\uFE0F?)?|[\uDFCB\uDFCC](?:\uFE0F|\uD83C[\uDFFB-\uDFFF])?(?:\u200D[\u2640\u2642]\uFE0F?)?|[\uDCCF\uDD8E\uDD91-\uDD9A\uDE01\uDE1A\uDE2F\uDE32-\uDE36\uDE38-\uDE3A\uDE50\uDE51\uDF00-\uDF20\uDF2D-\uDF35\uDF37-\uDF7C\uDF7E-\uDF84\uDF86-\uDF93\uDFA0-\uDFC1\uDFC5\uDFC6\uDFC8\uDFC9\uDFCF-\uDFD3\uDFE0-\uDFF0\uDFF8-\uDFFF]|\uDDE6\uD83C[\uDDE8-\uDDEC\uDDEE\uDDF1\uDDF2\uDDF4\uDDF6-\uDDFA\uDDFC\uDDFD\uDDFF]|\uDDE7\uD83C[\uDDE6\uDDE7\uDDE9-\uDDEF\uDDF1-\uDDF4\uDDF6-\uDDF9\uDDFB\uDDFC\uDDFE\uDDFF]|\uDDE8\uD83C[\uDDE6\uDDE8\uDDE9\uDDEB-\uDDEE\uDDF0-\uDDF5\uDDF7\uDDFA-\uDDFF]|\uDDE9\uD83C[\uDDEA\uDDEC\uDDEF\uDDF0\uDDF2\uDDF4\uDDFF]|\uDDEA\uD83C[\uDDE6\uDDE8\uDDEA\uDDEC\uDDED\uDDF7-\uDDFA]|\uDDEB\uD83C[\uDDEE-\uDDF0\uDDF2\uDDF4\uDDF7]|\uDDEC\uD83C[\uDDE6\uDDE7\uDDE9-\uDDEE\uDDF1-\uDDF3\uDDF5-\uDDFA\uDDFC\uDDFE]|\uDDED\uD83C[\uDDF0\uDDF2\uDDF3\uDDF7\uDDF9\uDDFA]|\uDDEE\uD83C[\uDDE8-\uDDEA\uDDF1-\uDDF4\uDDF6-\uDDF9]|\uDDEF\uD83C[\uDDEA\uDDF2\uDDF4\uDDF5]|\uDDF0\uD83C[\uDDEA\uDDEC-\uDDEE\uDDF2\uDDF3\uDDF5\uDDF7\uDDFC\uDDFE\uDDFF]|\uDDF1\uD83C[\uDDE6-\uDDE8\uDDEE\uDDF0\uDDF7-\uDDFB\uDDFE]|\uDDF2\uD83C[\uDDE6\uDDE8-\uDDED\uDDF0-\uDDFF]|\uDDF3\uD83C[\uDDE6\uDDE8\uDDEA-\uDDEC\uDDEE\uDDF1\uDDF4\uDDF5\uDDF7\uDDFA\uDDFF]|\uDDF4\uD83C\uDDF2|\uDDF5\uD83C[\uDDE6\uDDEA-\uDDED\uDDF0-\uDDF3\uDDF7-\uDDF9\uDDFC\uDDFE]|\uDDF6\uD83C\uDDE6|\uDDF7\uD83C[\uDDEA\uDDF4\uDDF8\uDDFA\uDDFC]|\uDDF8\uD83C[\uDDE6-\uDDEA\uDDEC-\uDDF4\uDDF7-\uDDF9\uDDFB\uDDFD-\uDDFF]|\uDDF9\uD83C[\uDDE6\uDDE8\uDDE9\uDDEB-\uDDED\uDDEF-\uDDF4\uDDF7\uDDF9\uDDFB\uDDFC\uDDFF]|\uDDFA\uD83C[\uDDE6\uDDEC\uDDF2\uDDF3\uDDF8\uDDFE\uDDFF]|\uDDFB\uD83C[\uDDE6\uDDE8\uDDEA\uDDEC\uDDEE\uDDF3\uDDFA]|\uDDFC\uD83C[\uDDEB\uDDF8]|\uDDFD\uD83C\uDDF0|\uDDFE\uD83C[\uDDEA\uDDF9]|\uDDFF\uD83C[\uDDE6\uDDF2\uDDFC]|\uDFF3\uFE0F?(?:\u200D(?:\u26A7\uFE0F?|\uD83C\uDF08))?|\uDFF4(?:\u200D\u2620\uFE0F?|\uDB40\uDC67\uDB40\uDC62\uDB40(?:\uDC65\uDB40\uDC6E\uDB40\uDC67|\uDC73\uDB40\uDC63\uDB40\uDC74|\uDC77\uDB40\uDC6C\uDB40\uDC73)\uDB40\uDC7F)?)|\uD83D(?:[\uDC08\uDC26](?:\u200D\u2B1B)?|[\uDC3F\uDCFD\uDD49\uDD4A\uDD6F\uDD70\uDD73\uDD76-\uDD79\uDD87\uDD8A-\uDD8D\uDDA5\uDDA8\uDDB1\uDDB2\uDDBC\uDDC2-\uDDC4\uDDD1-\uDDD3\uDDDC-\uDDDE\uDDE1\uDDE3\uDDE8\uDDEF\uDDF3\uDDFA\uDECB\uDECD-\uDECF\uDEE0-\uDEE5\uDEE9\uDEF0\uDEF3]\uFE0F?|[\uDC42\uDC43\uDC46-\uDC50\uDC66\uDC67\uDC6B-\uDC6D\uDC72\uDC74-\uDC76\uDC78\uDC7C\uDC83\uDC85\uDC8F\uDC91\uDCAA\uDD7A\uDD95\uDD96\uDE4C\uDE4F\uDEC0\uDECC](?:\uD83C[\uDFFB-\uDFFF])?|[\uDC6E\uDC70\uDC71\uDC73\uDC77\uDC81\uDC82\uDC86\uDC87\uDE45-\uDE47\uDE4B\uDE4D\uDE4E\uDEA3\uDEB4-\uDEB6](?:\uD83C[\uDFFB-\uDFFF])?(?:\u200D[\u2640\u2642]\uFE0F?)?|[\uDD74\uDD90](?:\uFE0F|\uD83C[\uDFFB-\uDFFF])?|[\uDC00-\uDC07\uDC09-\uDC14\uDC16-\uDC25\uDC27-\uDC3A\uDC3C-\uDC3E\uDC40\uDC44\uDC45\uDC51-\uDC65\uDC6A\uDC79-\uDC7B\uDC7D-\uDC80\uDC84\uDC88-\uDC8E\uDC90\uDC92-\uDCA9\uDCAB-\uDCFC\uDCFF-\uDD3D\uDD4B-\uDD4E\uDD50-\uDD67\uDDA4\uDDFB-\uDE2D\uDE2F-\uDE34\uDE37-\uDE44\uDE48-\uDE4A\uDE80-\uDEA2\uDEA4-\uDEB3\uDEB7-\uDEBF\uDEC1-\uDEC5\uDED0-\uDED2\uDED5-\uDED7\uDEDC-\uDEDF\uDEEB\uDEEC\uDEF4-\uDEFC\uDFE0-\uDFEB\uDFF0]|\uDC15(?:\u200D\uD83E\uDDBA)?|\uDC3B(?:\u200D\u2744\uFE0F?)?|\uDC41\uFE0F?(?:\u200D\uD83D\uDDE8\uFE0F?)?|\uDC68(?:\u200D(?:[\u2695\u2696\u2708]\uFE0F?|\u2764\uFE0F?\u200D\uD83D(?:\uDC8B\u200D\uD83D)?\uDC68|\uD83C[\uDF3E\uDF73\uDF7C\uDF93\uDFA4\uDFA8\uDFEB\uDFED]|\uD83D(?:[\uDC68\uDC69]\u200D\uD83D(?:\uDC66(?:\u200D\uD83D\uDC66)?|\uDC67(?:\u200D\uD83D[\uDC66\uDC67])?)|[\uDCBB\uDCBC\uDD27\uDD2C\uDE80\uDE92]|\uDC66(?:\u200D\uD83D\uDC66)?|\uDC67(?:\u200D\uD83D[\uDC66\uDC67])?)|\uD83E[\uDDAF-\uDDB3\uDDBC\uDDBD])|\uD83C(?:\uDFFB(?:\u200D(?:[\u2695\u2696\u2708]\uFE0F?|\u2764\uFE0F?\u200D\uD83D(?:\uDC8B\u200D\uD83D)?\uDC68\uD83C[\uDFFB-\uDFFF]|\uD83C[\uDF3E\uDF73\uDF7C\uDF93\uDFA4\uDFA8\uDFEB\uDFED]|\uD83D[\uDCBB\uDCBC\uDD27\uDD2C\uDE80\uDE92]|\uD83E(?:[\uDDAF-\uDDB3\uDDBC\uDDBD]|\uDD1D\u200D\uD83D\uDC68\uD83C[\uDFFC-\uDFFF])))?|\uDFFC(?:\u200D(?:[\u2695\u2696\u2708]\uFE0F?|\u2764\uFE0F?\u200D\uD83D(?:\uDC8B\u200D\uD83D)?\uDC68\uD83C[\uDFFB-\uDFFF]|\uD83C[\uDF3E\uDF73\uDF7C\uDF93\uDFA4\uDFA8\uDFEB\uDFED]|\uD83D[\uDCBB\uDCBC\uDD27\uDD2C\uDE80\uDE92]|\uD83E(?:[\uDDAF-\uDDB3\uDDBC\uDDBD]|\uDD1D\u200D\uD83D\uDC68\uD83C[\uDFFB\uDFFD-\uDFFF])))?|\uDFFD(?:\u200D(?:[\u2695\u2696\u2708]\uFE0F?|\u2764\uFE0F?\u200D\uD83D(?:\uDC8B\u200D\uD83D)?\uDC68\uD83C[\uDFFB-\uDFFF]|\uD83C[\uDF3E\uDF73\uDF7C\uDF93\uDFA4\uDFA8\uDFEB\uDFED]|\uD83D[\uDCBB\uDCBC\uDD27\uDD2C\uDE80\uDE92]|\uD83E(?:[\uDDAF-\uDDB3\uDDBC\uDDBD]|\uDD1D\u200D\uD83D\uDC68\uD83C[\uDFFB\uDFFC\uDFFE\uDFFF])))?|\uDFFE(?:\u200D(?:[\u2695\u2696\u2708]\uFE0F?|\u2764\uFE0F?\u200D\uD83D(?:\uDC8B\u200D\uD83D)?\uDC68\uD83C[\uDFFB-\uDFFF]|\uD83C[\uDF3E\uDF73\uDF7C\uDF93\uDFA4\uDFA8\uDFEB\uDFED]|\uD83D[\uDCBB\uDCBC\uDD27\uDD2C\uDE80\uDE92]|\uD83E(?:[\uDDAF-\uDDB3\uDDBC\uDDBD]|\uDD1D\u200D\uD83D\uDC68\uD83C[\uDFFB-\uDFFD\uDFFF])))?|\uDFFF(?:\u200D(?:[\u2695\u2696\u2708]\uFE0F?|\u2764\uFE0F?\u200D\uD83D(?:\uDC8B\u200D\uD83D)?\uDC68\uD83C[\uDFFB-\uDFFF]|\uD83C[\uDF3E\uDF73\uDF7C\uDF93\uDFA4\uDFA8\uDFEB\uDFED]|\uD83D[\uDCBB\uDCBC\uDD27\uDD2C\uDE80\uDE92]|\uD83E(?:[\uDDAF-\uDDB3\uDDBC\uDDBD]|\uDD1D\u200D\uD83D\uDC68\uD83C[\uDFFB-\uDFFE])))?))?|\uDC69(?:\u200D(?:[\u2695\u2696\u2708]\uFE0F?|\u2764\uFE0F?\u200D\uD83D(?:\uDC8B\u200D\uD83D)?[\uDC68\uDC69]|\uD83C[\uDF3E\uDF73\uDF7C\uDF93\uDFA4\uDFA8\uDFEB\uDFED]|\uD83D(?:[\uDCBB\uDCBC\uDD27\uDD2C\uDE80\uDE92]|\uDC66(?:\u200D\uD83D\uDC66)?|\uDC67(?:\u200D\uD83D[\uDC66\uDC67])?|\uDC69\u200D\uD83D(?:\uDC66(?:\u200D\uD83D\uDC66)?|\uDC67(?:\u200D\uD83D[\uDC66\uDC67])?))|\uD83E[\uDDAF-\uDDB3\uDDBC\uDDBD])|\uD83C(?:\uDFFB(?:\u200D(?:[\u2695\u2696\u2708]\uFE0F?|\u2764\uFE0F?\u200D\uD83D(?:[\uDC68\uDC69]|\uDC8B\u200D\uD83D[\uDC68\uDC69])\uD83C[\uDFFB-\uDFFF]|\uD83C[\uDF3E\uDF73\uDF7C\uDF93\uDFA4\uDFA8\uDFEB\uDFED]|\uD83D[\uDCBB\uDCBC\uDD27\uDD2C\uDE80\uDE92]|\uD83E(?:[\uDDAF-\uDDB3\uDDBC\uDDBD]|\uDD1D\u200D\uD83D[\uDC68\uDC69]\uD83C[\uDFFC-\uDFFF])))?|\uDFFC(?:\u200D(?:[\u2695\u2696\u2708]\uFE0F?|\u2764\uFE0F?\u200D\uD83D(?:[\uDC68\uDC69]|\uDC8B\u200D\uD83D[\uDC68\uDC69])\uD83C[\uDFFB-\uDFFF]|\uD83C[\uDF3E\uDF73\uDF7C\uDF93\uDFA4\uDFA8\uDFEB\uDFED]|\uD83D[\uDCBB\uDCBC\uDD27\uDD2C\uDE80\uDE92]|\uD83E(?:[\uDDAF-\uDDB3\uDDBC\uDDBD]|\uDD1D\u200D\uD83D[\uDC68\uDC69]\uD83C[\uDFFB\uDFFD-\uDFFF])))?|\uDFFD(?:\u200D(?:[\u2695\u2696\u2708]\uFE0F?|\u2764\uFE0F?\u200D\uD83D(?:[\uDC68\uDC69]|\uDC8B\u200D\uD83D[\uDC68\uDC69])\uD83C[\uDFFB-\uDFFF]|\uD83C[\uDF3E\uDF73\uDF7C\uDF93\uDFA4\uDFA8\uDFEB\uDFED]|\uD83D[\uDCBB\uDCBC\uDD27\uDD2C\uDE80\uDE92]|\uD83E(?:[\uDDAF-\uDDB3\uDDBC\uDDBD]|\uDD1D\u200D\uD83D[\uDC68\uDC69]\uD83C[\uDFFB\uDFFC\uDFFE\uDFFF])))?|\uDFFE(?:\u200D(?:[\u2695\u2696\u2708]\uFE0F?|\u2764\uFE0F?\u200D\uD83D(?:[\uDC68\uDC69]|\uDC8B\u200D\uD83D[\uDC68\uDC69])\uD83C[\uDFFB-\uDFFF]|\uD83C[\uDF3E\uDF73\uDF7C\uDF93\uDFA4\uDFA8\uDFEB\uDFED]|\uD83D[\uDCBB\uDCBC\uDD27\uDD2C\uDE80\uDE92]|\uD83E(?:[\uDDAF-\uDDB3\uDDBC\uDDBD]|\uDD1D\u200D\uD83D[\uDC68\uDC69]\uD83C[\uDFFB-\uDFFD\uDFFF])))?|\uDFFF(?:\u200D(?:[\u2695\u2696\u2708]\uFE0F?|\u2764\uFE0F?\u200D\uD83D(?:[\uDC68\uDC69]|\uDC8B\u200D\uD83D[\uDC68\uDC69])\uD83C[\uDFFB-\uDFFF]|\uD83C[\uDF3E\uDF73\uDF7C\uDF93\uDFA4\uDFA8\uDFEB\uDFED]|\uD83D[\uDCBB\uDCBC\uDD27\uDD2C\uDE80\uDE92]|\uD83E(?:[\uDDAF-\uDDB3\uDDBC\uDDBD]|\uDD1D\u200D\uD83D[\uDC68\uDC69]\uD83C[\uDFFB-\uDFFE])))?))?|\uDC6F(?:\u200D[\u2640\u2642]\uFE0F?)?|\uDD75(?:\uFE0F|\uD83C[\uDFFB-\uDFFF])?(?:\u200D[\u2640\u2642]\uFE0F?)?|\uDE2E(?:\u200D\uD83D\uDCA8)?|\uDE35(?:\u200D\uD83D\uDCAB)?|\uDE36(?:\u200D\uD83C\uDF2B\uFE0F?)?)|\uD83E(?:[\uDD0C\uDD0F\uDD18-\uDD1F\uDD30-\uDD34\uDD36\uDD77\uDDB5\uDDB6\uDDBB\uDDD2\uDDD3\uDDD5\uDEC3-\uDEC5\uDEF0\uDEF2-\uDEF8](?:\uD83C[\uDFFB-\uDFFF])?|[\uDD26\uDD35\uDD37-\uDD39\uDD3D\uDD3E\uDDB8\uDDB9\uDDCD-\uDDCF\uDDD4\uDDD6-\uDDDD](?:\uD83C[\uDFFB-\uDFFF])?(?:\u200D[\u2640\u2642]\uFE0F?)?|[\uDDDE\uDDDF](?:\u200D[\u2640\u2642]\uFE0F?)?|[\uDD0D\uDD0E\uDD10-\uDD17\uDD20-\uDD25\uDD27-\uDD2F\uDD3A\uDD3F-\uDD45\uDD47-\uDD76\uDD78-\uDDB4\uDDB7\uDDBA\uDDBC-\uDDCC\uDDD0\uDDE0-\uDDFF\uDE70-\uDE7C\uDE80-\uDE88\uDE90-\uDEBD\uDEBF-\uDEC2\uDECE-\uDEDB\uDEE0-\uDEE8]|\uDD3C(?:\u200D[\u2640\u2642]\uFE0F?|\uD83C[\uDFFB-\uDFFF])?|\uDDD1(?:\u200D(?:[\u2695\u2696\u2708]\uFE0F?|\uD83C[\uDF3E\uDF73\uDF7C\uDF84\uDF93\uDFA4\uDFA8\uDFEB\uDFED]|\uD83D[\uDCBB\uDCBC\uDD27\uDD2C\uDE80\uDE92]|\uD83E(?:[\uDDAF-\uDDB3\uDDBC\uDDBD]|\uDD1D\u200D\uD83E\uDDD1))|\uD83C(?:\uDFFB(?:\u200D(?:[\u2695\u2696\u2708]\uFE0F?|\u2764\uFE0F?\u200D(?:\uD83D\uDC8B\u200D)?\uD83E\uDDD1\uD83C[\uDFFC-\uDFFF]|\uD83C[\uDF3E\uDF73\uDF7C\uDF84\uDF93\uDFA4\uDFA8\uDFEB\uDFED]|\uD83D[\uDCBB\uDCBC\uDD27\uDD2C\uDE80\uDE92]|\uD83E(?:[\uDDAF-\uDDB3\uDDBC\uDDBD]|\uDD1D\u200D\uD83E\uDDD1\uD83C[\uDFFB-\uDFFF])))?|\uDFFC(?:\u200D(?:[\u2695\u2696\u2708]\uFE0F?|\u2764\uFE0F?\u200D(?:\uD83D\uDC8B\u200D)?\uD83E\uDDD1\uD83C[\uDFFB\uDFFD-\uDFFF]|\uD83C[\uDF3E\uDF73\uDF7C\uDF84\uDF93\uDFA4\uDFA8\uDFEB\uDFED]|\uD83D[\uDCBB\uDCBC\uDD27\uDD2C\uDE80\uDE92]|\uD83E(?:[\uDDAF-\uDDB3\uDDBC\uDDBD]|\uDD1D\u200D\uD83E\uDDD1\uD83C[\uDFFB-\uDFFF])))?|\uDFFD(?:\u200D(?:[\u2695\u2696\u2708]\uFE0F?|\u2764\uFE0F?\u200D(?:\uD83D\uDC8B\u200D)?\uD83E\uDDD1\uD83C[\uDFFB\uDFFC\uDFFE\uDFFF]|\uD83C[\uDF3E\uDF73\uDF7C\uDF84\uDF93\uDFA4\uDFA8\uDFEB\uDFED]|\uD83D[\uDCBB\uDCBC\uDD27\uDD2C\uDE80\uDE92]|\uD83E(?:[\uDDAF-\uDDB3\uDDBC\uDDBD]|\uDD1D\u200D\uD83E\uDDD1\uD83C[\uDFFB-\uDFFF])))?|\uDFFE(?:\u200D(?:[\u2695\u2696\u2708]\uFE0F?|\u2764\uFE0F?\u200D(?:\uD83D\uDC8B\u200D)?\uD83E\uDDD1\uD83C[\uDFFB-\uDFFD\uDFFF]|\uD83C[\uDF3E\uDF73\uDF7C\uDF84\uDF93\uDFA4\uDFA8\uDFEB\uDFED]|\uD83D[\uDCBB\uDCBC\uDD27\uDD2C\uDE80\uDE92]|\uD83E(?:[\uDDAF-\uDDB3\uDDBC\uDDBD]|\uDD1D\u200D\uD83E\uDDD1\uD83C[\uDFFB-\uDFFF])))?|\uDFFF(?:\u200D(?:[\u2695\u2696\u2708]\uFE0F?|\u2764\uFE0F?\u200D(?:\uD83D\uDC8B\u200D)?\uD83E\uDDD1\uD83C[\uDFFB-\uDFFE]|\uD83C[\uDF3E\uDF73\uDF7C\uDF84\uDF93\uDFA4\uDFA8\uDFEB\uDFED]|\uD83D[\uDCBB\uDCBC\uDD27\uDD2C\uDE80\uDE92]|\uD83E(?:[\uDDAF-\uDDB3\uDDBC\uDDBD]|\uDD1D\u200D\uD83E\uDDD1\uD83C[\uDFFB-\uDFFF])))?))?|\uDEF1(?:\uD83C(?:\uDFFB(?:\u200D\uD83E\uDEF2\uD83C[\uDFFC-\uDFFF])?|\uDFFC(?:\u200D\uD83E\uDEF2\uD83C[\uDFFB\uDFFD-\uDFFF])?|\uDFFD(?:\u200D\uD83E\uDEF2\uD83C[\uDFFB\uDFFC\uDFFE\uDFFF])?|\uDFFE(?:\u200D\uD83E\uDEF2\uD83C[\uDFFB-\uDFFD\uDFFF])?|\uDFFF(?:\u200D\uD83E\uDEF2\uD83C[\uDFFB-\uDFFE])?))?)/g;
var xr = { eastAsianWidth(t) {
  var e = t.charCodeAt(0), r = t.length == 2 ? t.charCodeAt(1) : 0, n = e;
  return 55296 <= e && e <= 56319 && 56320 <= r && r <= 57343 && (e &= 1023, r &= 1023, n = e << 10 | r, n += 65536), n == 12288 || 65281 <= n && n <= 65376 || 65504 <= n && n <= 65510 ? "F" : 4352 <= n && n <= 4447 || 4515 <= n && n <= 4519 || 4602 <= n && n <= 4607 || 9001 <= n && n <= 9002 || 11904 <= n && n <= 11929 || 11931 <= n && n <= 12019 || 12032 <= n && n <= 12245 || 12272 <= n && n <= 12283 || 12289 <= n && n <= 12350 || 12353 <= n && n <= 12438 || 12441 <= n && n <= 12543 || 12549 <= n && n <= 12589 || 12593 <= n && n <= 12686 || 12688 <= n && n <= 12730 || 12736 <= n && n <= 12771 || 12784 <= n && n <= 12830 || 12832 <= n && n <= 12871 || 12880 <= n && n <= 13054 || 13056 <= n && n <= 19903 || 19968 <= n && n <= 42124 || 42128 <= n && n <= 42182 || 43360 <= n && n <= 43388 || 44032 <= n && n <= 55203 || 55216 <= n && n <= 55238 || 55243 <= n && n <= 55291 || 63744 <= n && n <= 64255 || 65040 <= n && n <= 65049 || 65072 <= n && n <= 65106 || 65108 <= n && n <= 65126 || 65128 <= n && n <= 65131 || 110592 <= n && n <= 110593 || 127488 <= n && n <= 127490 || 127504 <= n && n <= 127546 || 127552 <= n && n <= 127560 || 127568 <= n && n <= 127569 || 131072 <= n && n <= 194367 || 177984 <= n && n <= 196605 || 196608 <= n && n <= 262141 ? "W" : "N";
} };
var bu = /[^\x20-\x7F]/;
function wu$1(t) {
  if (!t)
    return 0;
  if (!bu.test(t))
    return t.length;
  t = t.replace(yr(), "  ");
  let e = 0;
  for (let r of t) {
    let n = r.codePointAt(0);
    if (n <= 31 || n >= 127 && n <= 159 || n >= 768 && n <= 879)
      continue;
    let o = xr.eastAsianWidth(r);
    e += o === "F" || o === "W" ? 2 : 1;
  }
  return e;
}
var we = wu$1;
var Br$1 = (t) => {
  if (Array.isArray(t))
    return t;
  if (t.type !== k)
    throw new Error(`Expect doc to be 'array' or '${k}'.`);
  return t.parts;
};
function Ne$1(t, e) {
  if (typeof t == "string")
    return e(t);
  let r = /* @__PURE__ */ new Map();
  return n(t);
  function n(u) {
    if (r.has(u))
      return r.get(u);
    let i = o(u);
    return r.set(u, i), i;
  }
  function o(u) {
    switch (W$1(u)) {
      case j:
        return e(u.map(n));
      case k:
        return e({ ...u, parts: u.parts.map(n) });
      case B:
        return e({ ...u, breakContents: n(u.breakContents), flatContents: n(u.flatContents) });
      case A: {
        let { expandedStates: i, contents: s } = u;
        return i ? (i = i.map(n), s = i[0]) : s = n(s), e({ ...u, contents: s, expandedStates: i });
      }
      case S:
      case T:
      case P:
      case O$1:
      case L:
        return e({ ...u, contents: n(u.contents) });
      case U$1:
      case M:
      case v:
      case I:
      case x:
      case b:
        return e(u);
      default:
        throw new q(u);
    }
  }
}
function Je$1(t, e, r) {
  let n = r, o = false;
  function u(i) {
    if (o)
      return false;
    let s = e(i);
    s !== void 0 && (o = true, n = s);
  }
  return xe(t, u), n;
}
function Ou$1(t) {
  if (t.type === A && t.break || t.type === x && t.hard || t.type === b)
    return true;
}
function kr$1(t) {
  return Je$1(t, Ou$1, false);
}
function _r$1(t) {
  if (t.length > 0) {
    let e = y(false, t, -1);
    !e.expandedStates && !e.break && (e.break = "propagated");
  }
  return null;
}
function br(t) {
  let e = /* @__PURE__ */ new Set(), r = [];
  function n(u) {
    if (u.type === b && _r$1(r), u.type === A) {
      if (r.push(u), e.has(u))
        return false;
      e.add(u);
    }
  }
  function o(u) {
    u.type === A && r.pop().break && _r$1(r);
  }
  xe(t, n, o, true);
}
function Nu$1(t) {
  return t.type === x && !t.hard ? t.soft ? "" : " " : t.type === B ? t.flatContents : t;
}
function wr$1(t) {
  return Ne$1(t, Nu$1);
}
function Ar$1(t) {
  for (t = [...t]; t.length >= 2 && y(false, t, -2).type === x && y(false, t, -1).type === b; )
    t.length -= 2;
  if (t.length > 0) {
    let e = Oe$1(y(false, t, -1));
    t[t.length - 1] = e;
  }
  return t;
}
function Oe$1(t) {
  switch (W$1(t)) {
    case S:
    case T:
    case P:
    case A:
    case L:
    case O$1: {
      let e = Oe$1(t.contents);
      return { ...t, contents: e };
    }
    case B:
      return { ...t, breakContents: Oe$1(t.breakContents), flatContents: Oe$1(t.flatContents) };
    case k:
      return { ...t, parts: Ar$1(t.parts) };
    case j:
      return Ar$1(t);
    case U$1:
      return t.replace(/[\n\r]*$/, "");
    case M:
    case v:
    case I:
    case x:
    case b:
      break;
    default:
      throw new q(t);
  }
  return t;
}
function Xe$1(t) {
  return Oe$1(Su$1(t));
}
function Tu(t) {
  switch (W$1(t)) {
    case k:
      if (t.parts.every((e) => e === ""))
        return "";
      break;
    case A:
      if (!t.contents && !t.id && !t.break && !t.expandedStates)
        return "";
      if (t.contents.type === A && t.contents.id === t.id && t.contents.break === t.break && t.contents.expandedStates === t.expandedStates)
        return t.contents;
      break;
    case S:
    case T:
    case P:
    case L:
      if (!t.contents)
        return "";
      break;
    case B:
      if (!t.flatContents && !t.breakContents)
        return "";
      break;
    case j: {
      let e = [];
      for (let r of t) {
        if (!r)
          continue;
        let [n, ...o] = Array.isArray(r) ? r : [r];
        typeof n == "string" && typeof y(false, e, -1) == "string" ? e[e.length - 1] += n : e.push(n), e.push(...o);
      }
      return e.length === 0 ? "" : e.length === 1 ? e[0] : e;
    }
    case U$1:
    case M:
    case v:
    case I:
    case x:
    case O$1:
    case b:
      break;
    default:
      throw new q(t);
  }
  return t;
}
function Su$1(t) {
  return Ne$1(t, (e) => Tu(e));
}
function Or$1(t, e = He) {
  return Ne$1(t, (r) => typeof r == "string" ? ke(e, r.split(`
`)) : r);
}
function vu$1(t) {
  if (t.type === x)
    return true;
}
function Nr(t) {
  return Je$1(t, vu$1, false);
}
function Ze$1(t, e) {
  return t.type === O$1 ? { ...t, contents: e(t.contents) } : e(t);
}
var R$1 = Symbol("MODE_BREAK"), K = Symbol("MODE_FLAT"), Te$1 = Symbol("cursor");
function Tr$1() {
  return { value: "", length: 0, queue: [] };
}
function Pu(t, e) {
  return Bt(t, { type: "indent" }, e);
}
function Lu(t, e, r) {
  return e === Number.NEGATIVE_INFINITY ? t.root || Tr$1() : e < 0 ? Bt(t, { type: "dedent" }, r) : e ? e.type === "root" ? { ...t, root: t } : Bt(t, { type: typeof e == "string" ? "stringAlign" : "numberAlign", n: e }, r) : t;
}
function Bt(t, e, r) {
  let n = e.type === "dedent" ? t.queue.slice(0, -1) : [...t.queue, e], o = "", u = 0, i = 0, s = 0;
  for (let l of n)
    switch (l.type) {
      case "indent":
        c(), r.useTabs ? a(1) : D(r.tabWidth);
        break;
      case "stringAlign":
        c(), o += l.n, u += l.n.length;
        break;
      case "numberAlign":
        i += 1, s += l.n;
        break;
      default:
        throw new Error(`Unexpected type '${l.type}'`);
    }
  return f(), { ...t, value: o, length: u, queue: n };
  function a(l) {
    o += "	".repeat(l), u += r.tabWidth * l;
  }
  function D(l) {
    o += " ".repeat(l), u += l;
  }
  function c() {
    r.useTabs ? F() : f();
  }
  function F() {
    i > 0 && a(i), d();
  }
  function f() {
    s > 0 && D(s), d();
  }
  function d() {
    i = 0, s = 0;
  }
}
function kt(t) {
  let e = 0, r = 0, n = t.length;
  e:
    for (; n--; ) {
      let o = t[n];
      if (o === Te$1) {
        r++;
        continue;
      }
      for (let u = o.length - 1; u >= 0; u--) {
        let i = o[u];
        if (i === " " || i === "	")
          e++;
        else {
          t[n] = o.slice(0, u + 1);
          break e;
        }
      }
    }
  if (e > 0 || r > 0)
    for (t.length = n + 1; r-- > 0; )
      t.push(Te$1);
  return e;
}
function Qe$1(t, e, r, n, o, u) {
  if (r === Number.POSITIVE_INFINITY)
    return true;
  let i = e.length, s = [t], a = [];
  for (; r >= 0; ) {
    if (s.length === 0) {
      if (i === 0)
        return true;
      s.push(e[--i]);
      continue;
    }
    let { mode: D, doc: c } = s.pop();
    switch (W$1(c)) {
      case U$1:
        a.push(c), r -= we(c);
        break;
      case j:
      case k: {
        let F = Br$1(c);
        for (let f = F.length - 1; f >= 0; f--)
          s.push({ mode: D, doc: F[f] });
        break;
      }
      case T:
      case S:
      case P:
      case O$1:
        s.push({ mode: D, doc: c.contents });
        break;
      case v:
        r += kt(a);
        break;
      case A: {
        if (u && c.break)
          return false;
        let F = c.break ? R$1 : D, f = c.expandedStates && F === R$1 ? y(false, c.expandedStates, -1) : c.contents;
        s.push({ mode: F, doc: f });
        break;
      }
      case B: {
        let f = (c.groupId ? o[c.groupId] || K : D) === R$1 ? c.breakContents : c.flatContents;
        f && s.push({ mode: D, doc: f });
        break;
      }
      case x:
        if (D === R$1 || c.hard)
          return true;
        c.soft || (a.push(" "), r--);
        break;
      case L:
        n = true;
        break;
      case I:
        if (n)
          return false;
        break;
    }
  }
  return false;
}
function fe$1(t, e) {
  let r = {}, n = e.printWidth, o = be$1(e.endOfLine), u = 0, i = [{ ind: Tr$1(), mode: R$1, doc: t }], s = [], a = false, D = [], c = 0;
  for (br(t); i.length > 0; ) {
    let { ind: f, mode: d, doc: l } = i.pop();
    switch (W$1(l)) {
      case U$1: {
        let p = o !== `
` ? ee$1(false, l, `
`, o) : l;
        s.push(p), i.length > 0 && (u += we(p));
        break;
      }
      case j:
        for (let p = l.length - 1; p >= 0; p--)
          i.push({ ind: f, mode: d, doc: l[p] });
        break;
      case M:
        if (c >= 2)
          throw new Error("There are too many 'cursor' in doc.");
        s.push(Te$1), c++;
        break;
      case T:
        i.push({ ind: Pu(f, e), mode: d, doc: l.contents });
        break;
      case S:
        i.push({ ind: Lu(f, l.n, e), mode: d, doc: l.contents });
        break;
      case v:
        u -= kt(s);
        break;
      case A:
        switch (d) {
          case K:
            if (!a) {
              i.push({ ind: f, mode: l.break ? R$1 : K, doc: l.contents });
              break;
            }
          case R$1: {
            a = false;
            let p = { ind: f, mode: K, doc: l.contents }, m = n - u, E = D.length > 0;
            if (!l.break && Qe$1(p, i, m, E, r))
              i.push(p);
            else if (l.expandedStates) {
              let h = y(false, l.expandedStates, -1);
              if (l.break) {
                i.push({ ind: f, mode: R$1, doc: h });
                break;
              } else
                for (let g = 1; g < l.expandedStates.length + 1; g++)
                  if (g >= l.expandedStates.length) {
                    i.push({ ind: f, mode: R$1, doc: h });
                    break;
                  } else {
                    let C2 = l.expandedStates[g], _ = { ind: f, mode: K, doc: C2 };
                    if (Qe$1(_, i, m, E, r)) {
                      i.push(_);
                      break;
                    }
                  }
            } else
              i.push({ ind: f, mode: R$1, doc: l.contents });
            break;
          }
        }
        l.id && (r[l.id] = y(false, i, -1).mode);
        break;
      case k: {
        let p = n - u, { parts: m } = l;
        if (m.length === 0)
          break;
        let [E, h] = m, g = { ind: f, mode: K, doc: E }, C2 = { ind: f, mode: R$1, doc: E }, _ = Qe$1(g, [], p, D.length > 0, r, true);
        if (m.length === 1) {
          _ ? i.push(g) : i.push(C2);
          break;
        }
        let Z2 = { ind: f, mode: K, doc: h }, $2 = { ind: f, mode: R$1, doc: h };
        if (m.length === 2) {
          _ ? i.push(Z2, g) : i.push($2, C2);
          break;
        }
        m.splice(0, 2);
        let Q2 = { ind: f, mode: d, doc: Ge(m) }, rr2 = m[0];
        Qe$1({ ind: f, mode: K, doc: [E, h, rr2] }, [], p, D.length > 0, r, true) ? i.push(Q2, Z2, g) : _ ? i.push(Q2, $2, g) : i.push(Q2, $2, C2);
        break;
      }
      case B:
      case P: {
        let p = l.groupId ? r[l.groupId] : d;
        if (p === R$1) {
          let m = l.type === B ? l.breakContents : l.negate ? l.contents : ie(l.contents);
          m && i.push({ ind: f, mode: d, doc: m });
        }
        if (p === K) {
          let m = l.type === B ? l.flatContents : l.negate ? ie(l.contents) : l.contents;
          m && i.push({ ind: f, mode: d, doc: m });
        }
        break;
      }
      case L:
        D.push({ ind: f, mode: d, doc: l.contents });
        break;
      case I:
        D.length > 0 && i.push({ ind: f, mode: d, doc: Ae$1 });
        break;
      case x:
        switch (d) {
          case K:
            if (l.hard)
              a = true;
            else {
              l.soft || (s.push(" "), u += 1);
              break;
            }
          case R$1:
            if (D.length > 0) {
              i.push({ ind: f, mode: d, doc: l }, ...D.reverse()), D.length = 0;
              break;
            }
            l.literal ? f.root ? (s.push(o, f.root.value), u = f.root.length) : (s.push(o), u = 0) : (u -= kt(s), s.push(o + f.value), u = f.length);
            break;
        }
        break;
      case O$1:
        i.push({ ind: f, mode: d, doc: l.contents });
        break;
      case b:
        break;
      default:
        throw new q(l);
    }
    i.length === 0 && D.length > 0 && (i.push(...D.reverse()), D.length = 0);
  }
  let F = s.indexOf(Te$1);
  if (F !== -1) {
    let f = s.indexOf(Te$1, F + 1), d = s.slice(0, F).join(""), l = s.slice(F + 1, f).join(""), p = s.slice(f + 1).join("");
    return { formatted: d + l + p, cursorNodeStart: d.length, cursorNodeText: l };
  }
  return { formatted: s.join("") };
}
function J(t) {
  var e;
  if (!t)
    return "";
  if (Array.isArray(t)) {
    let r = [];
    for (let n of t)
      if (Array.isArray(n))
        r.push(...J(n));
      else {
        let o = J(n);
        o !== "" && r.push(o);
      }
    return r;
  }
  return t.type === B ? { ...t, breakContents: J(t.breakContents), flatContents: J(t.flatContents) } : t.type === A ? { ...t, contents: J(t.contents), expandedStates: (e = t.expandedStates) == null ? void 0 : e.map(J) } : t.type === k ? { type: "fill", parts: t.parts.map(J) } : t.contents ? { ...t, contents: J(t.contents) } : t;
}
function Sr$1(t) {
  let e = /* @__PURE__ */ Object.create(null), r = /* @__PURE__ */ new Set();
  return n(J(t));
  function n(u, i, s) {
    var a, D;
    if (typeof u == "string")
      return JSON.stringify(u);
    if (Array.isArray(u)) {
      let c = u.map(n).filter(Boolean);
      return c.length === 1 ? c[0] : `[${c.join(", ")}]`;
    }
    if (u.type === x) {
      let c = ((a = s == null ? void 0 : s[i + 1]) == null ? void 0 : a.type) === b;
      return u.literal ? c ? "literalline" : "literallineWithoutBreakParent" : u.hard ? c ? "hardline" : "hardlineWithoutBreakParent" : u.soft ? "softline" : "line";
    }
    if (u.type === b)
      return ((D = s == null ? void 0 : s[i - 1]) == null ? void 0 : D.type) === x && s[i - 1].hard ? void 0 : "breakParent";
    if (u.type === v)
      return "trim";
    if (u.type === T)
      return "indent(" + n(u.contents) + ")";
    if (u.type === S)
      return u.n === Number.NEGATIVE_INFINITY ? "dedentToRoot(" + n(u.contents) + ")" : u.n < 0 ? "dedent(" + n(u.contents) + ")" : u.n.type === "root" ? "markAsRoot(" + n(u.contents) + ")" : "align(" + JSON.stringify(u.n) + ", " + n(u.contents) + ")";
    if (u.type === B)
      return "ifBreak(" + n(u.breakContents) + (u.flatContents ? ", " + n(u.flatContents) : "") + (u.groupId ? (u.flatContents ? "" : ', ""') + `, { groupId: ${o(u.groupId)} }` : "") + ")";
    if (u.type === P) {
      let c = [];
      u.negate && c.push("negate: true"), u.groupId && c.push(`groupId: ${o(u.groupId)}`);
      let F = c.length > 0 ? `, { ${c.join(", ")} }` : "";
      return `indentIfBreak(${n(u.contents)}${F})`;
    }
    if (u.type === A) {
      let c = [];
      u.break && u.break !== "propagated" && c.push("shouldBreak: true"), u.id && c.push(`id: ${o(u.id)}`);
      let F = c.length > 0 ? `, { ${c.join(", ")} }` : "";
      return u.expandedStates ? `conditionalGroup([${u.expandedStates.map((f) => n(f)).join(",")}]${F})` : `group(${n(u.contents)}${F})`;
    }
    if (u.type === k)
      return `fill([${u.parts.map((c) => n(c)).join(", ")}])`;
    if (u.type === L)
      return "lineSuffix(" + n(u.contents) + ")";
    if (u.type === I)
      return "lineSuffixBoundary";
    if (u.type === O$1)
      return `label(${JSON.stringify(u.label)}, ${n(u.contents)})`;
    throw new Error("Unknown doc type " + u.type);
  }
  function o(u) {
    if (typeof u != "symbol")
      return JSON.stringify(String(u));
    if (u in e)
      return e[u];
    let i = u.description || "symbol";
    for (let s = 0; ; s++) {
      let a = i + (s > 0 ? ` #${s}` : "");
      if (!r.has(a))
        return r.add(a), e[u] = `Symbol.for(${JSON.stringify(a)})`;
    }
  }
}
function Iu(t, e, r = 0) {
  let n = 0;
  for (let o = r; o < t.length; ++o)
    t[o] === "	" ? n = n + e - n % e : n++;
  return n;
}
var pe = Iu;
var Se$1 = class Se extends Error {
  name = "ConfigError";
}, ve$1 = class ve extends Error {
  name = "UndefinedParserError";
};
var vr = { cursorOffset: { category: "Special", type: "int", default: -1, range: { start: -1, end: 1 / 0, step: 1 }, description: `Print (to stderr) where a cursor at the given position would move to after formatting.
This option cannot be used with --range-start and --range-end.`, cliCategory: "Editor" }, endOfLine: { category: "Global", type: "choice", default: "lf", description: "Which end of line characters to apply.", choices: [{ value: "lf", description: "Line Feed only (\\n), common on Linux and macOS as well as inside git repos" }, { value: "crlf", description: "Carriage Return + Line Feed characters (\\r\\n), common on Windows" }, { value: "cr", description: "Carriage Return character only (\\r), used very rarely" }, { value: "auto", description: `Maintain existing
(mixed values within one file are normalised by looking at what's used after the first line)` }] }, filepath: { category: "Special", type: "path", description: "Specify the input filepath. This will be used to do parser inference.", cliName: "stdin-filepath", cliCategory: "Other", cliDescription: "Path to the file to pretend that stdin comes from." }, insertPragma: { category: "Special", type: "boolean", default: false, description: "Insert @format pragma into file's first docblock comment.", cliCategory: "Other" }, parser: { category: "Global", type: "choice", default: void 0, description: "Which parser to use.", exception: (t) => typeof t == "string" || typeof t == "function", choices: [{ value: "flow", description: "Flow" }, { value: "babel", description: "JavaScript" }, { value: "babel-flow", description: "Flow" }, { value: "babel-ts", description: "TypeScript" }, { value: "typescript", description: "TypeScript" }, { value: "acorn", description: "JavaScript" }, { value: "espree", description: "JavaScript" }, { value: "meriyah", description: "JavaScript" }, { value: "css", description: "CSS" }, { value: "less", description: "Less" }, { value: "scss", description: "SCSS" }, { value: "json", description: "JSON" }, { value: "json5", description: "JSON5" }, { value: "json-stringify", description: "JSON.stringify" }, { value: "graphql", description: "GraphQL" }, { value: "markdown", description: "Markdown" }, { value: "mdx", description: "MDX" }, { value: "vue", description: "Vue" }, { value: "yaml", description: "YAML" }, { value: "glimmer", description: "Ember / Handlebars" }, { value: "html", description: "HTML" }, { value: "angular", description: "Angular" }, { value: "lwc", description: "Lightning Web Components" }] }, plugins: { type: "path", array: true, default: [{ value: [] }], category: "Global", description: "Add a plugin. Multiple plugins can be passed as separate `--plugin`s.", exception: (t) => typeof t == "string" || typeof t == "object", cliName: "plugin", cliCategory: "Config" }, printWidth: { category: "Global", type: "int", default: 80, description: "The line length where Prettier will try wrap.", range: { start: 0, end: 1 / 0, step: 1 } }, rangeEnd: { category: "Special", type: "int", default: 1 / 0, range: { start: 0, end: 1 / 0, step: 1 }, description: `Format code ending at a given character offset (exclusive).
The range will extend forwards to the end of the selected statement.
This option cannot be used with --cursor-offset.`, cliCategory: "Editor" }, rangeStart: { category: "Special", type: "int", default: 0, range: { start: 0, end: 1 / 0, step: 1 }, description: `Format code starting at a given character offset.
The range will extend backwards to the start of the first line containing the selected statement.
This option cannot be used with --cursor-offset.`, cliCategory: "Editor" }, requirePragma: { category: "Special", type: "boolean", default: false, description: `Require either '@prettier' or '@format' to be present in the file's first docblock comment
in order for it to be formatted.`, cliCategory: "Other" }, tabWidth: { type: "int", category: "Global", default: 2, description: "Number of spaces per indentation level.", range: { start: 0, end: 1 / 0, step: 1 } }, useTabs: { category: "Global", type: "boolean", default: false, description: "Indent with tabs instead of spaces." }, embeddedLanguageFormatting: { category: "Global", type: "choice", default: "auto", description: "Control how Prettier formats quoted code embedded in the file.", choices: [{ value: "auto", description: "Format embedded code if Prettier can automatically identify it." }, { value: "off", description: "Never automatically format embedded code." }] } };
function et({ plugins: t = [], showDeprecated: e = false } = {}) {
  let r = t.flatMap((o) => o.languages ?? []), n = [];
  for (let o of Yu$1(Object.assign({}, ...t.map(({ options: u }) => u), vr)))
    !e && o.deprecated || (Array.isArray(o.choices) && (e || (o.choices = o.choices.filter((u) => !u.deprecated)), o.name === "parser" && (o.choices = [...o.choices, ...Ru(o.choices, r, t)])), o.pluginDefaults = Object.fromEntries(t.filter((u) => {
      var i;
      return ((i = u.defaultOptions) == null ? void 0 : i[o.name]) !== void 0;
    }).map((u) => [u.name, u.defaultOptions[o.name]])), n.push(o));
  return { languages: r, options: n };
}
function* Ru(t, e, r) {
  let n = new Set(t.map((o) => o.value));
  for (let o of e)
    if (o.parsers) {
      for (let u of o.parsers)
        if (!n.has(u)) {
          n.add(u);
          let i = r.find((a) => a.parsers && Object.prototype.hasOwnProperty.call(a.parsers, u)), s = o.name;
          i != null && i.name && (s += ` (plugin: ${i.name})`), yield { value: u, description: s };
        }
    }
}
function Yu$1(t) {
  let e = [];
  for (let [r, n] of Object.entries(t)) {
    let o = { name: r, ...n };
    Array.isArray(o.default) && (o.default = y(false, o.default, -1).value), e.push(o);
  }
  return e;
}
var ju$1 = (t) => t.split(/[/\\]/).pop();
function Pr$1(t, e) {
  if (!e)
    return;
  let r = ju$1(e).toLowerCase();
  return t.find((n) => {
    var o, u;
    return ((o = n.extensions) == null ? void 0 : o.some((i) => r.endsWith(i))) || ((u = n.filenames) == null ? void 0 : u.some((i) => i.toLowerCase() === r));
  });
}
function Vu(t, e) {
  if (e)
    return t.find(({ name: r }) => r.toLowerCase() === e) ?? t.find(({ aliases: r }) => r == null ? void 0 : r.includes(e)) ?? t.find(({ extensions: r }) => r == null ? void 0 : r.includes(`.${e}`));
}
function $u(t, e) {
  let r = t.plugins.flatMap((o) => o.languages ?? []), n = Vu(r, e.language) ?? Pr$1(r, e.physicalFile) ?? Pr$1(r, e.file) ?? (e.physicalFile, void 0);
  return n == null ? void 0 : n.parsers[0];
}
var Lr$1 = $u;
var te = { key: (t) => /^[$_a-zA-Z][$_a-zA-Z0-9]*$/.test(t) ? t : JSON.stringify(t), value(t) {
  if (t === null || typeof t != "object")
    return JSON.stringify(t);
  if (Array.isArray(t))
    return `[${t.map((r) => te.value(r)).join(", ")}]`;
  let e = Object.keys(t);
  return e.length === 0 ? "{}" : `{ ${e.map((r) => `${te.key(r)}: ${te.value(t[r])}`).join(", ")} }`;
}, pair: ({ key: t, value: e }) => te.value({ [t]: e }) };
var bt$1 = Ce$1(Pe$1(), 1), Yr$1 = (t, e, { descriptor: r }) => {
  let n = [`${bt$1.default.yellow(typeof t == "string" ? r.key(t) : r.pair(t))} is deprecated`];
  return e && n.push(`we now treat it as ${bt$1.default.blue(typeof e == "string" ? r.key(e) : r.pair(e))}`), n.join("; ") + ".";
};
var se$1 = Ce$1(Pe$1(), 1);
var tt = Symbol.for("vnopts.VALUE_NOT_EXIST"), de = Symbol.for("vnopts.VALUE_UNCHANGED");
var jr$1 = " ".repeat(2), $r = (t, e, r) => {
  let { text: n, list: o } = r.normalizeExpectedResult(r.schemas[t].expected(r)), u = [];
  return n && u.push(Vr(t, e, n, r.descriptor)), o && u.push([Vr(t, e, o.title, r.descriptor)].concat(o.values.map((i) => Ur(i, r.loggerPrintWidth))).join(`
`)), Mr(u, r.loggerPrintWidth);
};
function Vr(t, e, r, n) {
  return [`Invalid ${se$1.default.red(n.key(t))} value.`, `Expected ${se$1.default.blue(r)},`, `but received ${e === tt ? se$1.default.gray("nothing") : se$1.default.red(n.value(e))}.`].join(" ");
}
function Ur({ text: t, list: e }, r) {
  let n = [];
  return t && n.push(`- ${se$1.default.blue(t)}`), e && n.push([`- ${se$1.default.blue(e.title)}:`].concat(e.values.map((o) => Ur(o, r - jr$1.length).replace(/^|\n/g, `$&${jr$1}`))).join(`
`)), Mr(n, r);
}
function Mr(t, e) {
  if (t.length === 1)
    return t[0];
  let [r, n] = t, [o, u] = t.map((i) => i.split(`
`, 1)[0].length);
  return o > e && o > u ? n : r;
}
var Nt = Ce$1(Pe$1(), 1);
var wt = [], Wr = [];
function Ot$1(t, e) {
  if (t === e)
    return 0;
  let r = t;
  t.length > e.length && (t = e, e = r);
  let n = t.length, o = e.length;
  for (; n > 0 && t.charCodeAt(~-n) === e.charCodeAt(~-o); )
    n--, o--;
  let u = 0;
  for (; u < n && t.charCodeAt(u) === e.charCodeAt(u); )
    u++;
  if (n -= u, o -= u, n === 0)
    return o;
  let i, s, a, D, c = 0, F = 0;
  for (; c < n; )
    Wr[c] = t.charCodeAt(u + c), wt[c] = ++c;
  for (; F < o; )
    for (i = e.charCodeAt(u + F), a = F++, s = F, c = 0; c < n; c++)
      D = i === Wr[c] ? a : a + 1, a = wt[c], s = wt[c] = a > s ? D > s ? s + 1 : D : D > a ? a + 1 : D;
  return s;
}
var rt = (t, e, { descriptor: r, logger: n, schemas: o }) => {
  let u = [`Ignored unknown option ${Nt.default.yellow(r.pair({ key: t, value: e }))}.`], i = Object.keys(o).sort().find((s) => Ot$1(t, s) < 3);
  i && u.push(`Did you mean ${Nt.default.blue(r.key(i))}?`), n.warn(u.join(" "));
};
var Uu = ["default", "expected", "validate", "deprecated", "forward", "redirect", "overlap", "preprocess", "postprocess"];
function Mu(t, e) {
  let r = new t(e), n = Object.create(r);
  for (let o of Uu)
    o in e && (n[o] = Wu(e[o], r, w.prototype[o].length));
  return n;
}
var w = class {
  static create(e) {
    return Mu(this, e);
  }
  constructor(e) {
    this.name = e.name;
  }
  default(e) {
  }
  expected(e) {
    return "nothing";
  }
  validate(e, r) {
    return false;
  }
  deprecated(e, r) {
    return false;
  }
  forward(e, r) {
  }
  redirect(e, r) {
  }
  overlap(e, r, n) {
    return e;
  }
  preprocess(e, r) {
    return e;
  }
  postprocess(e, r) {
    return de;
  }
};
function Wu(t, e, r) {
  return typeof t == "function" ? (...n) => t(...n.slice(0, r - 1), e, ...n.slice(r - 1)) : () => t;
}
var nt = class extends w {
  constructor(e) {
    super(e), this._sourceName = e.sourceName;
  }
  expected(e) {
    return e.schemas[this._sourceName].expected(e);
  }
  validate(e, r) {
    return r.schemas[this._sourceName].validate(e, r);
  }
  redirect(e, r) {
    return this._sourceName;
  }
};
var ut = class extends w {
  expected() {
    return "anything";
  }
  validate() {
    return true;
  }
};
var ot = class extends w {
  constructor({ valueSchema: e, name: r = e.name, ...n }) {
    super({ ...n, name: r }), this._valueSchema = e;
  }
  expected(e) {
    let { text: r, list: n } = e.normalizeExpectedResult(this._valueSchema.expected(e));
    return { text: r && `an array of ${r}`, list: n && { title: "an array of the following values", values: [{ list: n }] } };
  }
  validate(e, r) {
    if (!Array.isArray(e))
      return false;
    let n = [];
    for (let o of e) {
      let u = r.normalizeValidateResult(this._valueSchema.validate(o, r), o);
      u !== true && n.push(u.value);
    }
    return n.length === 0 ? true : { value: n };
  }
  deprecated(e, r) {
    let n = [];
    for (let o of e) {
      let u = r.normalizeDeprecatedResult(this._valueSchema.deprecated(o, r), o);
      u !== false && n.push(...u.map(({ value: i }) => ({ value: [i] })));
    }
    return n;
  }
  forward(e, r) {
    let n = [];
    for (let o of e) {
      let u = r.normalizeForwardResult(this._valueSchema.forward(o, r), o);
      n.push(...u.map(zr));
    }
    return n;
  }
  redirect(e, r) {
    let n = [], o = [];
    for (let u of e) {
      let i = r.normalizeRedirectResult(this._valueSchema.redirect(u, r), u);
      "remain" in i && n.push(i.remain), o.push(...i.redirect.map(zr));
    }
    return n.length === 0 ? { redirect: o } : { redirect: o, remain: n };
  }
  overlap(e, r) {
    return e.concat(r);
  }
};
function zr({ from: t, to: e }) {
  return { from: [t], to: e };
}
var it = class extends w {
  expected() {
    return "true or false";
  }
  validate(e) {
    return typeof e == "boolean";
  }
};
function Kr(t, e) {
  let r = /* @__PURE__ */ Object.create(null);
  for (let n of t) {
    let o = n[e];
    if (r[o])
      throw new Error(`Duplicate ${e} ${JSON.stringify(o)}`);
    r[o] = n;
  }
  return r;
}
function Hr(t, e) {
  let r = /* @__PURE__ */ new Map();
  for (let n of t) {
    let o = n[e];
    if (r.has(o))
      throw new Error(`Duplicate ${e} ${JSON.stringify(o)}`);
    r.set(o, n);
  }
  return r;
}
function qr$1() {
  let t = /* @__PURE__ */ Object.create(null);
  return (e) => {
    let r = JSON.stringify(e);
    return t[r] ? true : (t[r] = true, false);
  };
}
function Jr(t, e) {
  let r = [], n = [];
  for (let o of t)
    e(o) ? r.push(o) : n.push(o);
  return [r, n];
}
function Xr(t) {
  return t === Math.floor(t);
}
function Zr(t, e) {
  if (t === e)
    return 0;
  let r = typeof t, n = typeof e, o = ["undefined", "object", "boolean", "number", "string"];
  return r !== n ? o.indexOf(r) - o.indexOf(n) : r !== "string" ? Number(t) - Number(e) : t.localeCompare(e);
}
function Qr(t) {
  return (...e) => {
    let r = t(...e);
    return typeof r == "string" ? new Error(r) : r;
  };
}
function Tt(t) {
  return t === void 0 ? {} : t;
}
function St(t) {
  if (typeof t == "string")
    return { text: t };
  let { text: e, list: r } = t;
  return zu$1((e || r) !== void 0, "Unexpected `expected` result, there should be at least one field."), r ? { text: e, list: { title: r.title, values: r.values.map(St) } } : { text: e };
}
function vt$1(t, e) {
  return t === true ? true : t === false ? { value: e } : t;
}
function Pt$1(t, e, r = false) {
  return t === false ? false : t === true ? r ? true : [{ value: e }] : "value" in t ? [t] : t.length === 0 ? false : t;
}
function Gr(t, e) {
  return typeof t == "string" || "key" in t ? { from: e, to: t } : "from" in t ? { from: t.from, to: t.to } : { from: e, to: t.to };
}
function st(t, e) {
  return t === void 0 ? [] : Array.isArray(t) ? t.map((r) => Gr(r, e)) : [Gr(t, e)];
}
function Lt(t, e) {
  let r = st(typeof t == "object" && "redirect" in t ? t.redirect : t, e);
  return r.length === 0 ? { remain: e, redirect: r } : typeof t == "object" && "remain" in t ? { remain: t.remain, redirect: r } : { redirect: r };
}
function zu$1(t, e) {
  if (!t)
    throw new Error(e);
}
var at = class extends w {
  constructor(e) {
    super(e), this._choices = Hr(e.choices.map((r) => r && typeof r == "object" ? r : { value: r }), "value");
  }
  expected({ descriptor: e }) {
    let r = Array.from(this._choices.keys()).map((i) => this._choices.get(i)).filter(({ hidden: i }) => !i).map((i) => i.value).sort(Zr).map(e.value), n = r.slice(0, -2), o = r.slice(-2);
    return { text: n.concat(o.join(" or ")).join(", "), list: { title: "one of the following values", values: r } };
  }
  validate(e) {
    return this._choices.has(e);
  }
  deprecated(e) {
    let r = this._choices.get(e);
    return r && r.deprecated ? { value: e } : false;
  }
  forward(e) {
    let r = this._choices.get(e);
    return r ? r.forward : void 0;
  }
  redirect(e) {
    let r = this._choices.get(e);
    return r ? r.redirect : void 0;
  }
};
var Dt$1 = class Dt extends w {
  expected() {
    return "a number";
  }
  validate(e, r) {
    return typeof e == "number";
  }
};
var ct = class extends Dt$1 {
  expected() {
    return "an integer";
  }
  validate(e, r) {
    return r.normalizeValidateResult(super.validate(e, r), e) === true && Xr(e);
  }
};
var Le$1 = class Le extends w {
  expected() {
    return "a string";
  }
  validate(e) {
    return typeof e == "string";
  }
};
var en = te, tn = rt, rn = $r, nn = Yr$1;
var lt$1 = class lt {
  constructor(e, r) {
    let { logger: n = console, loggerPrintWidth: o = 80, descriptor: u = en, unknown: i = tn, invalid: s = rn, deprecated: a = nn, missing: D = () => false, required: c = () => false, preprocess: F = (d) => d, postprocess: f = () => de } = r || {};
    this._utils = { descriptor: u, logger: n || { warn: () => {
    } }, loggerPrintWidth: o, schemas: Kr(e, "name"), normalizeDefaultResult: Tt, normalizeExpectedResult: St, normalizeDeprecatedResult: Pt$1, normalizeForwardResult: st, normalizeRedirectResult: Lt, normalizeValidateResult: vt$1 }, this._unknownHandler = i, this._invalidHandler = Qr(s), this._deprecatedHandler = a, this._identifyMissing = (d, l) => !(d in l) || D(d, l), this._identifyRequired = c, this._preprocess = F, this._postprocess = f, this.cleanHistory();
  }
  cleanHistory() {
    this._hasDeprecationWarned = qr$1();
  }
  normalize(e) {
    let r = {}, o = [this._preprocess(e, this._utils)], u = () => {
      for (; o.length !== 0; ) {
        let i = o.shift(), s = this._applyNormalization(i, r);
        o.push(...s);
      }
    };
    u();
    for (let i of Object.keys(this._utils.schemas)) {
      let s = this._utils.schemas[i];
      if (!(i in r)) {
        let a = Tt(s.default(this._utils));
        "value" in a && o.push({ [i]: a.value });
      }
    }
    u();
    for (let i of Object.keys(this._utils.schemas)) {
      if (!(i in r))
        continue;
      let s = this._utils.schemas[i], a = r[i], D = s.postprocess(a, this._utils);
      D !== de && (this._applyValidation(D, i, s), r[i] = D);
    }
    return this._applyPostprocess(r), this._applyRequiredCheck(r), r;
  }
  _applyNormalization(e, r) {
    let n = [], { knownKeys: o, unknownKeys: u } = this._partitionOptionKeys(e);
    for (let i of o) {
      let s = this._utils.schemas[i], a = s.preprocess(e[i], this._utils);
      this._applyValidation(a, i, s);
      let D = ({ from: d, to: l }) => {
        n.push(typeof l == "string" ? { [l]: d } : { [l.key]: l.value });
      }, c = ({ value: d, redirectTo: l }) => {
        let p = Pt$1(s.deprecated(d, this._utils), a, true);
        if (p !== false)
          if (p === true)
            this._hasDeprecationWarned(i) || this._utils.logger.warn(this._deprecatedHandler(i, l, this._utils));
          else
            for (let { value: m } of p) {
              let E = { key: i, value: m };
              if (!this._hasDeprecationWarned(E)) {
                let h = typeof l == "string" ? { key: l, value: m } : l;
                this._utils.logger.warn(this._deprecatedHandler(E, h, this._utils));
              }
            }
      };
      st(s.forward(a, this._utils), a).forEach(D);
      let f = Lt(s.redirect(a, this._utils), a);
      if (f.redirect.forEach(D), "remain" in f) {
        let d = f.remain;
        r[i] = i in r ? s.overlap(r[i], d, this._utils) : d, c({ value: d });
      }
      for (let { from: d, to: l } of f.redirect)
        c({ value: d, redirectTo: l });
    }
    for (let i of u) {
      let s = e[i];
      this._applyUnknownHandler(i, s, r, (a, D) => {
        n.push({ [a]: D });
      });
    }
    return n;
  }
  _applyRequiredCheck(e) {
    for (let r of Object.keys(this._utils.schemas))
      if (this._identifyMissing(r, e) && this._identifyRequired(r))
        throw this._invalidHandler(r, tt, this._utils);
  }
  _partitionOptionKeys(e) {
    let [r, n] = Jr(Object.keys(e).filter((o) => !this._identifyMissing(o, e)), (o) => o in this._utils.schemas);
    return { knownKeys: r, unknownKeys: n };
  }
  _applyValidation(e, r, n) {
    let o = vt$1(n.validate(e, this._utils), e);
    if (o !== true)
      throw this._invalidHandler(r, o.value, this._utils);
  }
  _applyUnknownHandler(e, r, n, o) {
    let u = this._unknownHandler(e, r, this._utils);
    if (u)
      for (let i of Object.keys(u)) {
        if (this._identifyMissing(i, u))
          continue;
        let s = u[i];
        i in this._utils.schemas ? o(i, s) : n[i] = s;
      }
  }
  _applyPostprocess(e) {
    let r = this._postprocess(e, this._utils);
    if (r !== de) {
      if (r.delete)
        for (let n of r.delete)
          delete e[n];
      if (r.override) {
        let { knownKeys: n, unknownKeys: o } = this._partitionOptionKeys(r.override);
        for (let u of n) {
          let i = r.override[u];
          this._applyValidation(i, u, this._utils.schemas[u]), e[u] = i;
        }
        for (let u of o) {
          let i = r.override[u];
          this._applyUnknownHandler(u, i, e, (s, a) => {
            let D = this._utils.schemas[s];
            this._applyValidation(a, s, D), e[s] = a;
          });
        }
      }
    }
  }
};
var It;
function Ku$1(t, e, { logger: r = false, isCLI: n = false, passThrough: o = false, FlagSchema: u, descriptor: i } = {}) {
  if (n) {
    if (!u)
      throw new Error("'FlagSchema' option is required.");
    if (!i)
      throw new Error("'descriptor' option is required.");
  } else
    i = te;
  let s = o ? Array.isArray(o) ? (f, d) => o.includes(f) ? { [f]: d } : void 0 : (f, d) => ({ [f]: d }) : (f, d, l) => {
    let { _: p, ...m } = l.schemas;
    return rt(f, d, { ...l, schemas: m });
  }, a = Hu(e, { isCLI: n, FlagSchema: u }), D = new lt$1(a, { logger: r, unknown: s, descriptor: i }), c = r !== false;
  c && It && (D._hasDeprecationWarned = It);
  let F = D.normalize(t);
  return c && (It = D._hasDeprecationWarned), F;
}
function Hu(t, { isCLI: e, FlagSchema: r }) {
  let n = [];
  e && n.push(ut.create({ name: "_" }));
  for (let o of t)
    n.push(qu$1(o, { isCLI: e, optionInfos: t, FlagSchema: r })), o.alias && e && n.push(nt.create({ name: o.alias, sourceName: o.name }));
  return n;
}
function qu$1(t, { isCLI: e, optionInfos: r, FlagSchema: n }) {
  let { name: o } = t, u = { name: o }, i, s = {};
  switch (t.type) {
    case "int":
      i = ct, e && (u.preprocess = Number);
      break;
    case "string":
      i = Le$1;
      break;
    case "choice":
      i = at, u.choices = t.choices.map((a) => a != null && a.redirect ? { ...a, redirect: { to: { key: t.name, value: a.redirect } } } : a);
      break;
    case "boolean":
      i = it;
      break;
    case "flag":
      i = n, u.flags = r.flatMap((a) => [a.alias, a.description && a.name, a.oppositeDescription && `no-${a.name}`].filter(Boolean));
      break;
    case "path":
      i = Le$1;
      break;
    default:
      throw new Error(`Unexpected type ${t.type}`);
  }
  if (t.exception ? u.validate = (a, D, c) => t.exception(a) || D.validate(a, c) : u.validate = (a, D, c) => a === void 0 || D.validate(a, c), t.redirect && (s.redirect = (a) => a ? { to: { key: t.redirect.option, value: t.redirect.value } } : void 0), t.deprecated && (s.deprecated = true), e && !t.array) {
    let a = u.preprocess || ((D) => D);
    u.preprocess = (D, c, F) => c.preprocess(a(Array.isArray(D) ? y(false, D, -1) : D), F);
  }
  return t.array ? ot.create({ ...e ? { preprocess: (a) => Array.isArray(a) ? a : [a] } : {}, ...s, valueSchema: i.create(u) }) : i.create({ ...u, ...s });
}
var un = Ku$1;
function Rt(t, e) {
  if (!e)
    throw new Error("parserName is required.");
  for (let n = t.length - 1; n >= 0; n--) {
    let o = t[n];
    if (o.parsers && Object.prototype.hasOwnProperty.call(o.parsers, e))
      return o;
  }
  let r = `Couldn't resolve parser "${e}".`;
  throw r += " Plugins must be explicitly added to the standalone bundle.", new Se$1(r);
}
function on(t, e) {
  if (!e)
    throw new Error("astFormat is required.");
  for (let n = t.length - 1; n >= 0; n--) {
    let o = t[n];
    if (o.printers && Object.prototype.hasOwnProperty.call(o.printers, e))
      return o;
  }
  let r = `Couldn't find plugin for AST format "${e}".`;
  throw r += " Plugins must be explicitly added to the standalone bundle.", new Se$1(r);
}
function ft$1({ plugins: t, parser: e }) {
  let r = Rt(t, e);
  return Yt(r, e);
}
function Yt(t, e) {
  let r = t.parsers[e];
  return typeof r == "function" ? r() : r;
}
function sn$1(t, e) {
  let r = t.printers[e];
  return typeof r == "function" ? r() : r;
}
var an = { astFormat: "estree", printer: {}, originalText: void 0, locStart: null, locEnd: null };
async function Ju(t, e = {}) {
  var F;
  let r = { ...t };
  if (!r.parser)
    if (r.filepath) {
      if (r.parser = Lr$1(r, { physicalFile: r.filepath }), !r.parser)
        throw new ve$1(`No parser could be inferred for file "${r.filepath}".`);
    } else
      throw new ve$1("No parser and no file path given, couldn't infer a parser.");
  let n = et({ plugins: t.plugins, showDeprecated: true }).options, o = { ...an, ...Object.fromEntries(n.filter((f) => f.default !== void 0).map((f) => [f.name, f.default])) }, u = Rt(r.plugins, r.parser), i = await Yt(u, r.parser);
  r.astFormat = i.astFormat, r.locEnd = i.locEnd, r.locStart = i.locStart;
  let s = (F = u.printers) != null && F[i.astFormat] ? u : on(r.plugins, i.astFormat), a = await sn$1(s, i.astFormat);
  r.printer = a;
  let D = s.defaultOptions ? Object.fromEntries(Object.entries(s.defaultOptions).filter(([, f]) => f !== void 0)) : {}, c = { ...o, ...D };
  for (let [f, d] of Object.entries(c))
    (r[f] === null || r[f] === void 0) && (r[f] = d);
  return r.parser === "json" && (r.trailingComma = "none"), un(r, n, { passThrough: Object.keys(an), ...e });
}
var re$1 = Ju;
var Dn = /* @__PURE__ */ new Set(["tokens", "comments", "parent", "enclosingNode", "precedingNode", "followingNode"]), Xu = (t) => Object.keys(t).filter((e) => !Dn.has(e));
function Zu(t) {
  return t ? (e) => t(e, Dn) : Xu;
}
var H$1 = Zu;
function Qu$1(t, e) {
  let { printer: { massageAstNode: r, getVisitorKeys: n } } = e;
  if (!r)
    return t;
  let o = H$1(n), u = r.ignoredProperties ?? /* @__PURE__ */ new Set();
  return i(t);
  function i(s, a) {
    if (!(s !== null && typeof s == "object"))
      return s;
    if (Array.isArray(s))
      return s.map((f) => i(f, a)).filter(Boolean);
    let D = {}, c = new Set(o(s));
    for (let f in s)
      !Object.prototype.hasOwnProperty.call(s, f) || u.has(f) || (c.has(f) ? D[f] = i(s[f], s) : D[f] = s[f]);
    let F = r(s, D, a);
    if (F !== null)
      return F ?? D;
  }
}
var cn = Qu$1;
var Cn = Ce$1(hn(), 1);
async function so(t, e) {
  let r = await ft$1(e), n = r.preprocess ? r.preprocess(t, e) : t;
  e.originalText = n;
  let o;
  try {
    o = await r.parse(n, e, e);
  } catch (u) {
    ao$1(u, t);
  }
  return { text: n, ast: o };
}
function ao$1(t, e) {
  let { loc: r } = t;
  if (r) {
    let n = (0, Cn.codeFrameColumns)(e, r, { highlightCode: true });
    throw t.message += `
` + n, t.codeFrame = n, t;
  }
  throw t;
}
var ae$1 = so;
var Ie$1, $t$1, Fe$1, dt$1, Vt = class {
  constructor(e) {
    Ct$1(this, Ie$1);
    Ct$1(this, Fe$1);
    this.stack = [e];
  }
  get key() {
    let { stack: e, siblings: r } = this;
    return y(false, e, r === null ? -2 : -4) ?? null;
  }
  get index() {
    return this.siblings === null ? null : y(false, this.stack, -2);
  }
  get node() {
    return y(false, this.stack, -1);
  }
  get parent() {
    return this.getNode(1);
  }
  get grandparent() {
    return this.getNode(2);
  }
  get isInArray() {
    return this.siblings !== null;
  }
  get siblings() {
    let { stack: e } = this, r = y(false, e, -3);
    return Array.isArray(r) ? r : null;
  }
  get next() {
    let { siblings: e } = this;
    return e === null ? null : e[this.index + 1];
  }
  get previous() {
    let { siblings: e } = this;
    return e === null ? null : e[this.index - 1];
  }
  get isFirst() {
    return this.index === 0;
  }
  get isLast() {
    let { siblings: e, index: r } = this;
    return e !== null && r === e.length - 1;
  }
  get isRoot() {
    return this.stack.length === 1;
  }
  get root() {
    return this.stack[0];
  }
  get ancestors() {
    return [...ce$1(this, Fe$1, dt$1).call(this)];
  }
  getName() {
    let { stack: e } = this, { length: r } = e;
    return r > 1 ? y(false, e, -2) : null;
  }
  getValue() {
    return y(false, this.stack, -1);
  }
  getNode(e = 0) {
    let r = ce$1(this, Ie$1, $t$1).call(this, e);
    return r === -1 ? null : this.stack[r];
  }
  getParentNode(e = 0) {
    return this.getNode(e + 1);
  }
  call(e, ...r) {
    let { stack: n } = this, { length: o } = n, u = y(false, n, -1);
    for (let i of r)
      u = u[i], n.push(i, u);
    try {
      return e(this);
    } finally {
      n.length = o;
    }
  }
  callParent(e, r = 0) {
    let n = ce$1(this, Ie$1, $t$1).call(this, r + 1), o = this.stack.splice(n + 1);
    try {
      return e(this);
    } finally {
      this.stack.push(...o);
    }
  }
  each(e, ...r) {
    let { stack: n } = this, { length: o } = n, u = y(false, n, -1);
    for (let i of r)
      u = u[i], n.push(i, u);
    try {
      for (let i = 0; i < u.length; ++i)
        n.push(i, u[i]), e(this, i, u), n.length -= 2;
    } finally {
      n.length = o;
    }
  }
  map(e, ...r) {
    let n = [];
    return this.each((o, u, i) => {
      n[u] = e(o, u, i);
    }, ...r), n;
  }
  match(...e) {
    let r = this.stack.length - 1, n = null, o = this.stack[r--];
    for (let u of e) {
      if (o === void 0)
        return false;
      let i = null;
      if (typeof n == "number" && (i = n, n = this.stack[r--], o = this.stack[r--]), u && !u(o, n, i))
        return false;
      n = this.stack[r--], o = this.stack[r--];
    }
    return true;
  }
  findAncestor(e) {
    for (let r of ce$1(this, Fe$1, dt$1).call(this))
      if (e(r))
        return r;
  }
  hasAncestor(e) {
    for (let r of ce$1(this, Fe$1, dt$1).call(this))
      if (e(r))
        return true;
    return false;
  }
};
Ie$1 = /* @__PURE__ */ new WeakSet(), $t$1 = function(e) {
  let { stack: r } = this;
  for (let n = r.length - 1; n >= 0; n -= 2)
    if (!Array.isArray(r[n]) && --e < 0)
      return n;
  return -1;
}, Fe$1 = /* @__PURE__ */ new WeakSet(), dt$1 = function* () {
  let { stack: e } = this;
  for (let r = e.length - 3; r >= 0; r -= 2) {
    let n = e[r];
    Array.isArray(n) || (yield n);
  }
};
var gn$1 = Vt;
var yn = new Proxy(() => {
}, { get: () => yn }), Re$1 = yn;
function me(t) {
  return (e, r, n) => {
    let o = !!(n != null && n.backwards);
    if (r === false)
      return false;
    let { length: u } = e, i = r;
    for (; i >= 0 && i < u; ) {
      let s = e.charAt(i);
      if (t instanceof RegExp) {
        if (!t.test(s))
          return i;
      } else if (!t.includes(s))
        return i;
      o ? i-- : i++;
    }
    return i === -1 || i === u ? i : false;
  };
}
var xn = me(/\s/), N = me(" 	"), Ft$1 = me(",; 	"), mt$1 = me(/[^\n\r]/);
function Do(t, e, r) {
  let n = !!(r != null && r.backwards);
  if (e === false)
    return false;
  let o = t.charAt(e);
  if (n) {
    if (t.charAt(e - 1) === "\r" && o === `
`)
      return e - 2;
    if (o === `
` || o === "\r" || o === "\u2028" || o === "\u2029")
      return e - 1;
  } else {
    if (o === "\r" && t.charAt(e + 1) === `
`)
      return e + 2;
    if (o === `
` || o === "\r" || o === "\u2028" || o === "\u2029")
      return e + 1;
  }
  return e;
}
var Y$1 = Do;
function co$1(t, e, r = {}) {
  let n = N(t, r.backwards ? e - 1 : e, r), o = Y$1(t, n, r);
  return n !== o;
}
var V$1 = co$1;
function lo(t) {
  return Array.isArray(t) && t.length > 0;
}
var Ut = lo;
function fo(t) {
  return t !== null && typeof t == "object";
}
var _n$1 = fo;
function* Mt$1(t, e) {
  let { getVisitorKeys: r, filter: n = () => true } = e, o = (u) => _n$1(u) && n(u);
  for (let u of r(t)) {
    let i = t[u];
    if (Array.isArray(i))
      for (let s of i)
        o(s) && (yield s);
    else
      o(i) && (yield i);
  }
}
function* An$1(t, e) {
  let r = [t];
  for (let n = 0; n < r.length; n++) {
    let o = r[n];
    for (let u of Mt$1(o, e))
      yield u, r.push(u);
  }
}
function po(t) {
  let e = t.type || t.kind || "(unknown type)", r = String(t.name || t.id && (typeof t.id == "object" ? t.id.name : t.id) || t.key && (typeof t.key == "object" ? t.key.name : t.key) || t.value && (typeof t.value == "object" ? "" : String(t.value)) || t.operator || "");
  return r.length > 20 && (r = r.slice(0, 19) + "…"), e + (r ? " " + r : "");
}
function Wt(t, e) {
  (t.comments ?? (t.comments = [])).push(e), e.printed = false, e.nodeDescription = po(t);
}
function ne$1(t, e) {
  e.leading = true, e.trailing = false, Wt(t, e);
}
function X$1(t, e, r) {
  e.leading = false, e.trailing = false, r && (e.marker = r), Wt(t, e);
}
function ue$1(t, e) {
  e.leading = false, e.trailing = true, Wt(t, e);
}
var zt = /* @__PURE__ */ new WeakMap();
function Et$1(t, e) {
  if (zt.has(t))
    return zt.get(t);
  let { printer: { getCommentChildNodes: r, canAttachComment: n, getVisitorKeys: o }, locStart: u, locEnd: i } = e;
  if (!n)
    return [];
  let s = ((r == null ? void 0 : r(t, e)) ?? [...Mt$1(t, { getVisitorKeys: H$1(o) })]).flatMap((a) => n(a) ? [a] : Et$1(a, e));
  return s.sort((a, D) => u(a) - u(D) || i(a) - i(D)), zt.set(t, s), s;
}
function kn(t, e, r, n) {
  let { locStart: o, locEnd: u } = r, i = o(e), s = u(e), a = Et$1(t, r), D, c, F = 0, f = a.length;
  for (; F < f; ) {
    let d = F + f >> 1, l = a[d], p = o(l), m = u(l);
    if (p <= i && s <= m)
      return kn(l, e, r, l);
    if (m <= i) {
      D = l, F = d + 1;
      continue;
    }
    if (s <= p) {
      c = l, f = d;
      continue;
    }
    throw new Error("Comment location overlaps with node location");
  }
  if ((n == null ? void 0 : n.type) === "TemplateLiteral") {
    let { quasis: d } = n, l = Kt(d, e, r);
    D && Kt(d, D, r) !== l && (D = null), c && Kt(d, c, r) !== l && (c = null);
  }
  return { enclosingNode: n, precedingNode: D, followingNode: c };
}
var Gt$1 = () => false;
function bn(t, e) {
  let { comments: r } = t;
  if (delete t.comments, !Ut(r) || !e.printer.canAttachComment)
    return;
  let n = [], { locStart: o, locEnd: u, printer: { experimentalFeatures: { avoidAstMutation: i = false } = {}, handleComments: s = {} }, originalText: a } = e, { ownLine: D = Gt$1, endOfLine: c = Gt$1, remaining: F = Gt$1 } = s, f = r.map((d, l) => ({ ...kn(t, d, e), comment: d, text: a, options: e, ast: t, isLastComment: r.length - 1 === l }));
  for (let [d, l] of f.entries()) {
    let { comment: p, precedingNode: m, enclosingNode: E, followingNode: h, text: g, options: C2, ast: _, isLastComment: Z2 } = l;
    if (C2.parser === "json" || C2.parser === "json5" || C2.parser === "__js_expression" || C2.parser === "__ts_expression" || C2.parser === "__vue_expression" || C2.parser === "__vue_ts_expression") {
      if (o(p) - o(_) <= 0) {
        ne$1(_, p);
        continue;
      }
      if (u(p) - u(_) >= 0) {
        ue$1(_, p);
        continue;
      }
    }
    let $2;
    if (i ? $2 = [l] : (p.enclosingNode = E, p.precedingNode = m, p.followingNode = h, $2 = [p, g, C2, _, Z2]), Fo$1(g, C2, f, d))
      p.placement = "ownLine", D(...$2) || (h ? ne$1(h, p) : m ? ue$1(m, p) : E ? X$1(E, p) : X$1(_, p));
    else if (mo(g, C2, f, d))
      p.placement = "endOfLine", c(...$2) || (m ? ue$1(m, p) : h ? ne$1(h, p) : E ? X$1(E, p) : X$1(_, p));
    else if (p.placement = "remaining", !F(...$2))
      if (m && h) {
        let Q2 = n.length;
        Q2 > 0 && n[Q2 - 1].followingNode !== h && Bn(n, C2), n.push(l);
      } else
        m ? ue$1(m, p) : h ? ne$1(h, p) : E ? X$1(E, p) : X$1(_, p);
  }
  if (Bn(n, e), !i)
    for (let d of r)
      delete d.precedingNode, delete d.enclosingNode, delete d.followingNode;
}
var wn = (t) => !/[\S\n\u2028\u2029]/.test(t);
function Fo$1(t, e, r, n) {
  let { comment: o, precedingNode: u } = r[n], { locStart: i, locEnd: s } = e, a = i(o);
  if (u)
    for (let D = n - 1; D >= 0; D--) {
      let { comment: c, precedingNode: F } = r[D];
      if (F !== u || !wn(t.slice(s(c), a)))
        break;
      a = i(c);
    }
  return V$1(t, a, { backwards: true });
}
function mo(t, e, r, n) {
  let { comment: o, followingNode: u } = r[n], { locStart: i, locEnd: s } = e, a = s(o);
  if (u)
    for (let D = n + 1; D < r.length; D++) {
      let { comment: c, followingNode: F } = r[D];
      if (F !== u || !wn(t.slice(a, i(c))))
        break;
      a = s(c);
    }
  return V$1(t, a);
}
function Bn(t, e) {
  var s, a;
  let r = t.length;
  if (r === 0)
    return;
  let { precedingNode: n, followingNode: o } = t[0], u = e.locStart(o), i;
  for (i = r; i > 0; --i) {
    let { comment: D, precedingNode: c, followingNode: F } = t[i - 1];
    Re$1.strictEqual(c, n), Re$1.strictEqual(F, o);
    let f = e.originalText.slice(e.locEnd(D), u);
    if (((a = (s = e.printer).isGap) == null ? void 0 : a.call(s, f, e)) ?? /^[\s(]*$/.test(f))
      u = e.locStart(D);
    else
      break;
  }
  for (let [D, { comment: c }] of t.entries())
    D < i ? ue$1(n, c) : ne$1(o, c);
  for (let D of [n, o])
    D.comments && D.comments.length > 1 && D.comments.sort((c, F) => e.locStart(c) - e.locStart(F));
  t.length = 0;
}
function Kt(t, e, r) {
  let n = r.locStart(e) - 1;
  for (let o = 1; o < t.length; ++o)
    if (n < r.locStart(t[o]))
      return o - 1;
  return 0;
}
function Eo(t, e) {
  let r = e - 1;
  r = N(t, r, { backwards: true }), r = Y$1(t, r, { backwards: true }), r = N(t, r, { backwards: true });
  let n = Y$1(t, r, { backwards: true });
  return r !== n;
}
var Ye = Eo;
function On$1(t, e) {
  let r = t.node;
  return r.printed = true, e.printer.printComment(t, e);
}
function ho$1(t, e) {
  var c;
  let r = t.node, n = [On$1(t, e)], { printer: o, originalText: u, locStart: i, locEnd: s } = e;
  if ((c = o.isBlockComment) == null ? void 0 : c.call(o, r)) {
    let F = V$1(u, s(r)) ? V$1(u, i(r), { backwards: true }) ? G$1 : Ke : " ";
    n.push(F);
  } else
    n.push(G$1);
  let D = Y$1(u, N(u, s(r)));
  return D !== false && V$1(u, D) && n.push(G$1), n;
}
function Co(t, e, r) {
  var D;
  let n = t.node, o = On$1(t, e), { printer: u, originalText: i, locStart: s } = e, a = (D = u.isBlockComment) == null ? void 0 : D.call(u, n);
  if (r != null && r.hasLineSuffix && !(r != null && r.isBlock) || V$1(i, s(n), { backwards: true })) {
    let c = Ye(i, s(n));
    return { doc: _e$1([G$1, c ? G$1 : "", o]), isBlock: a, hasLineSuffix: true };
  }
  return !a || r != null && r.hasLineSuffix ? { doc: [_e$1([" ", o]), le$1], isBlock: a, hasLineSuffix: true } : { doc: [" ", o], isBlock: a, hasLineSuffix: false };
}
function go(t, e) {
  let r = t.node;
  if (!r)
    return {};
  let n = e[Symbol.for("printedComments")];
  if ((r.comments || []).filter((a) => !n.has(a)).length === 0)
    return { leading: "", trailing: "" };
  let u = [], i = [], s;
  return t.each(() => {
    let a = t.node;
    if (n != null && n.has(a))
      return;
    let { leading: D, trailing: c } = a;
    D ? u.push(ho$1(t, e)) : c && (s = Co(t, e, s), i.push(s.doc));
  }, "comments"), { leading: u, trailing: i };
}
function Nn$1(t, e, r) {
  let { leading: n, trailing: o } = go(t, r);
  return !n && !o ? e : Ze$1(e, (u) => [n, u, o]);
}
function Tn(t) {
  let { [Symbol.for("comments")]: e, [Symbol.for("printedComments")]: r } = t;
  for (let n of e) {
    if (!n.printed && !r.has(n))
      throw new Error('Comment "' + n.value.trim() + '" was not printed. Please report this error!');
    delete n.printed;
  }
}
async function Sn$1(t, e, r, n, o) {
  let { embeddedLanguageFormatting: u, printer: { embed: i, hasPrettierIgnore: s = () => false, getVisitorKeys: a } } = r;
  if (!i || u !== "auto")
    return;
  if (i.length > 2)
    throw new Error("printer.embed has too many parameters. The API changed in Prettier v3. Please update your plugin. See https://prettier.io/docs/en/plugins.html#optional-embed");
  let D = H$1(i.getVisitorKeys ?? a), c = [];
  d();
  let F = t.stack;
  for (let { print: l, node: p, pathStack: m } of c)
    try {
      t.stack = m;
      let E = await l(f, e, t, r);
      E && o.set(p, E);
    } catch (E) {
      if (globalThis.PRETTIER_DEBUG)
        throw E;
    }
  t.stack = F;
  function f(l, p) {
    return yo(l, p, r, n);
  }
  function d() {
    let { node: l } = t;
    if (l === null || typeof l != "object" || s(t))
      return;
    for (let m of D(l))
      Array.isArray(l[m]) ? t.each(d, m) : t.call(d, m);
    let p = i(t, r);
    if (p) {
      if (typeof p == "function") {
        c.push({ print: p, node: l, pathStack: [...t.stack] });
        return;
      }
      o.set(l, p);
    }
  }
}
async function yo(t, e, r, n) {
  let o = await re$1({ ...r, ...e, parentParser: r.parser, originalText: t }, { passThrough: true }), { ast: u } = await ae$1(t, o), i = await n(u, o);
  return Xe$1(i);
}
function _o(t, e) {
  let { originalText: r, [Symbol.for("comments")]: n, locStart: o, locEnd: u, [Symbol.for("printedComments")]: i } = e, { node: s } = t, a = o(s), D = u(s);
  for (let c of n)
    o(c) >= a && u(c) <= D && i.add(c);
  return r.slice(a, D);
}
var Pn$1 = _o;
async function je(t, e) {
  ({ ast: t } = await Ht(t, e));
  let r = /* @__PURE__ */ new Map(), n = new gn$1(t), u = /* @__PURE__ */ new Map();
  await Sn$1(n, s, e, je, u);
  let i = await Ln$1(n, e, s, void 0, u);
  return Tn(e), i;
  function s(D, c) {
    return D === void 0 || D === n ? a(c) : Array.isArray(D) ? n.call(() => a(c), ...D) : n.call(() => a(c), D);
  }
  function a(D) {
    let c = n.node;
    if (c == null)
      return "";
    let F = c && typeof c == "object" && D === void 0;
    if (F && r.has(c))
      return r.get(c);
    let f = Ln$1(n, e, s, D, u);
    return F && r.set(c, f), f;
  }
}
function Ln$1(t, e, r, n, o) {
  var a;
  let { node: u } = t, { printer: i } = e, s;
  return (a = i.hasPrettierIgnore) != null && a.call(i, t) ? s = Pn$1(t, e) : o.has(u) ? s = o.get(u) : s = i.print(t, e, r, n), u === e.cursorNode && (s = Ze$1(s, (D) => [Be, D, Be])), i.printComment && (!i.willPrintOwnComments || !i.willPrintOwnComments(t, e)) && (s = Nn$1(t, s, e)), s;
}
async function Ht(t, e) {
  let r = t.comments ?? [];
  e[Symbol.for("comments")] = r, e[Symbol.for("tokens")] = t.tokens ?? [], e[Symbol.for("printedComments")] = /* @__PURE__ */ new Set(), bn(t, e);
  let { printer: { preprocess: n } } = e;
  return t = n ? await n(t, e) : t, { ast: t, comments: r };
}
var Ao = ({ parser: t }) => t === "json" || t === "json5" || t === "json-stringify";
function Bo(t, e) {
  let r = [t.node, ...t.parentNodes], n = /* @__PURE__ */ new Set([e.node, ...e.parentNodes]);
  return r.find((o) => Yn$1.has(o.type) && n.has(o));
}
function In(t) {
  let e = t.length - 1;
  for (; ; ) {
    let r = t[e];
    if ((r == null ? void 0 : r.type) === "Program" || (r == null ? void 0 : r.type) === "File")
      e--;
    else
      break;
  }
  return t.slice(0, e + 1);
}
function ko(t, e, { locStart: r, locEnd: n }) {
  let o = t.node, u = e.node;
  if (o === u)
    return { startNode: o, endNode: u };
  let i = r(t.node);
  for (let a of In(e.parentNodes))
    if (r(a) >= i)
      u = a;
    else
      break;
  let s = n(e.node);
  for (let a of In(t.parentNodes)) {
    if (n(a) <= s)
      o = a;
    else
      break;
    if (o === u)
      break;
  }
  return { startNode: o, endNode: u };
}
function qt(t, e, r, n, o = [], u) {
  let { locStart: i, locEnd: s } = r, a = i(t), D = s(t);
  if (!(e > D || e < a || u === "rangeEnd" && e === a || u === "rangeStart" && e === D)) {
    for (let c of Et$1(t, r)) {
      let F = qt(c, e, r, n, [t, ...o], u);
      if (F)
        return F;
    }
    if (!n || n(t, o[0]))
      return { node: t, parentNodes: o };
  }
}
function bo(t, e) {
  return e !== "DeclareExportDeclaration" && t !== "TypeParameterDeclaration" && (t === "Directive" || t === "TypeAlias" || t === "TSExportAssignment" || t.startsWith("Declare") || t.startsWith("TSDeclare") || t.endsWith("Statement") || t.endsWith("Declaration"));
}
var Yn$1 = /* @__PURE__ */ new Set(["JsonRoot", "ObjectExpression", "ArrayExpression", "StringLiteral", "NumericLiteral", "BooleanLiteral", "NullLiteral", "UnaryExpression", "TemplateLiteral"]), wo$1 = /* @__PURE__ */ new Set(["OperationDefinition", "FragmentDefinition", "VariableDefinition", "TypeExtensionDefinition", "ObjectTypeDefinition", "FieldDefinition", "DirectiveDefinition", "EnumTypeDefinition", "EnumValueDefinition", "InputValueDefinition", "InputObjectTypeDefinition", "SchemaDefinition", "OperationTypeDefinition", "InterfaceTypeDefinition", "UnionTypeDefinition", "ScalarTypeDefinition"]);
function Rn$1(t, e, r) {
  if (!e)
    return false;
  switch (t.parser) {
    case "flow":
    case "babel":
    case "babel-flow":
    case "babel-ts":
    case "typescript":
    case "acorn":
    case "espree":
    case "meriyah":
    case "__babel_estree":
      return bo(e.type, r == null ? void 0 : r.type);
    case "json":
    case "json5":
    case "json-stringify":
      return Yn$1.has(e.type);
    case "graphql":
      return wo$1.has(e.kind);
    case "vue":
      return e.tag !== "root";
  }
  return false;
}
function jn$1(t, e, r) {
  let { rangeStart: n, rangeEnd: o, locStart: u, locEnd: i } = e;
  Re$1.ok(o > n);
  let s = t.slice(n, o).search(/\S/), a = s === -1;
  if (!a)
    for (n += s; o > n && !/\S/.test(t[o - 1]); --o)
      ;
  let D = qt(r, n, e, (d, l) => Rn$1(e, d, l), [], "rangeStart"), c = a ? D : qt(r, o, e, (d) => Rn$1(e, d), [], "rangeEnd");
  if (!D || !c)
    return { rangeStart: 0, rangeEnd: 0 };
  let F, f;
  if (Ao(e)) {
    let d = Bo(D, c);
    F = d, f = d;
  } else
    ({ startNode: F, endNode: f } = ko(D, c, e));
  return { rangeStart: Math.min(u(F), u(f)), rangeEnd: Math.max(i(F), i(f)) };
}
function Oo(t, e) {
  let { cursorOffset: r, locStart: n, locEnd: o } = e, u = H$1(e.printer.getVisitorKeys), i = (a) => n(a) <= r && o(a) >= r, s = t;
  for (let a of An$1(t, { getVisitorKeys: u, filter: i }))
    s = a;
  return s;
}
var Vn$1 = Oo;
var zn$1 = "\uFEFF", $n$1 = Symbol("cursor");
async function Gn$1(t, e, r = 0) {
  if (!t || t.trim().length === 0)
    return { formatted: "", cursorOffset: -1, comments: [] };
  let { ast: n, text: o } = await ae$1(t, e);
  e.cursorOffset >= 0 && (e.cursorNode = Vn$1(n, e));
  let u = await je(n, e);
  r > 0 && (u = qe$1([G$1, u], r, e.tabWidth));
  let i = fe$1(u, e);
  if (r > 0) {
    let a = i.formatted.trim();
    i.cursorNodeStart !== void 0 && (i.cursorNodeStart -= i.formatted.indexOf(a)), i.formatted = a + be$1(e.endOfLine);
  }
  let s = e[Symbol.for("comments")];
  if (e.cursorOffset >= 0) {
    let a, D, c, F, f;
    if (e.cursorNode && i.cursorNodeText ? (a = e.locStart(e.cursorNode), D = o.slice(a, e.locEnd(e.cursorNode)), c = e.cursorOffset - a, F = i.cursorNodeStart, f = i.cursorNodeText) : (a = 0, D = o, c = e.cursorOffset, F = 0, f = i.formatted), D === f)
      return { formatted: i.formatted, cursorOffset: F + c, comments: s };
    let d = D.split("");
    d.splice(c, 0, $n$1);
    let l = f.split(""), p = (0, Wn$1.diffArrays)(d, l), m = F;
    for (let E of p)
      if (E.removed) {
        if (E.value.includes($n$1))
          break;
      } else
        m += E.count;
    return { formatted: i.formatted, cursorOffset: m, comments: s };
  }
  return { formatted: i.formatted, cursorOffset: -1, comments: s };
}
async function No$1(t, e) {
  let { ast: r, text: n } = await ae$1(t, e), { rangeStart: o, rangeEnd: u } = jn$1(n, e, r), i = n.slice(o, u), s = Math.min(o, n.lastIndexOf(`
`, o) + 1), a = n.slice(s, o).match(/^\s*/)[0], D = pe(a, e.tabWidth), c = await Gn$1(i, { ...e, rangeStart: 0, rangeEnd: Number.POSITIVE_INFINITY, cursorOffset: e.cursorOffset > o && e.cursorOffset <= u ? e.cursorOffset - o : -1, endOfLine: "lf" }, D), F = c.formatted.trimEnd(), { cursorOffset: f } = e;
  f > u ? f += F.length - i.length : c.cursorOffset >= 0 && (f = c.cursorOffset + o);
  let d = n.slice(0, o) + F + n.slice(u);
  if (e.endOfLine !== "lf") {
    let l = be$1(e.endOfLine);
    f >= 0 && l === `\r
` && (f += At(d.slice(0, f), `
`)), d = ee$1(false, d, `
`, l);
  }
  return { formatted: d, cursorOffset: f, comments: c.comments };
}
function Jt$1(t, e, r) {
  return typeof e != "number" || Number.isNaN(e) || e < 0 || e > t.length ? r : e;
}
function Un$1(t, e) {
  let { cursorOffset: r, rangeStart: n, rangeEnd: o } = e;
  return r = Jt$1(t, r, -1), n = Jt$1(t, n, 0), o = Jt$1(t, o, t.length), { ...e, cursorOffset: r, rangeStart: n, rangeEnd: o };
}
function Kn$1(t, e) {
  let { cursorOffset: r, rangeStart: n, rangeEnd: o, endOfLine: u } = Un$1(t, e), i = t.charAt(0) === zn$1;
  if (i && (t = t.slice(1), r--, n--, o--), u === "auto" && (u = Cr(t)), t.includes("\r")) {
    let s = (a) => At(t.slice(0, Math.max(a, 0)), `\r
`);
    r -= s(r), n -= s(n), o -= s(o), t = gr(t);
  }
  return { hasBOM: i, text: t, options: Un$1(t, { ...e, cursorOffset: r, rangeStart: n, rangeEnd: o, endOfLine: u }) };
}
async function Mn$1(t, e) {
  let r = await ft$1(e);
  return !r.hasPragma || r.hasPragma(t);
}
async function Xt(t, e) {
  let { hasBOM: r, text: n, options: o } = Kn$1(t, await re$1(e));
  if (o.rangeStart >= o.rangeEnd && n !== "" || o.requirePragma && !await Mn$1(n, o))
    return { formatted: t, cursorOffset: e.cursorOffset, comments: [] };
  let u;
  return o.rangeStart > 0 || o.rangeEnd < n.length ? u = await No$1(n, o) : (!o.requirePragma && o.insertPragma && o.printer.insertPragma && !await Mn$1(n, o) && (n = o.printer.insertPragma(n)), u = await Gn$1(n, o)), r && (u.formatted = zn$1 + u.formatted, u.cursorOffset >= 0 && u.cursorOffset++), u;
}
async function Hn$1(t, e, r) {
  let { text: n, options: o } = Kn$1(t, await re$1(e)), u = await ae$1(n, o);
  return r && (r.preprocessForPrint && (u.ast = await Ht(u.ast, o)), r.massage && (u.ast = cn(u.ast, o))), u;
}
async function qn(t, e) {
  e = await re$1(e);
  let r = await je(t, e);
  return fe$1(r, e);
}
async function Jn$1(t, e) {
  let r = Sr$1(t), { formatted: n } = await Xt(r, { ...e, parser: "__js_expression" });
  return n;
}
async function Xn$1(t, e) {
  e = await re$1(e);
  let { ast: r } = await ae$1(t, e);
  return je(r, e);
}
async function Zn$1(t, e) {
  return fe$1(t, await re$1(e));
}
var Qt = {};
Me(Qt, { addDanglingComment: () => X$1, addLeadingComment: () => ne$1, addTrailingComment: () => ue$1, getAlignmentSize: () => pe, getIndentSize: () => eu, getMaxContinuousCount: () => Qn$1, getNextNonSpaceNonCommentCharacter: () => nu, getNextNonSpaceNonCommentCharacterIndex: () => Mo, getStringWidth: () => we, hasNewline: () => V$1, hasNewlineInRange: () => tu, hasSpaces: () => ru, isNextLineEmpty: () => Ko, isNextLineEmptyAfterIndex: () => ht$1, isPreviousLineEmpty: () => zo$1, makeString: () => uu, skip: () => me, skipEverythingButNewLine: () => mt$1, skipInlineComment: () => Ee$1, skipNewline: () => Y$1, skipSpaces: () => N, skipToLineEnd: () => Ft$1, skipTrailingComment: () => he, skipWhitespace: () => xn });
function So(t, e) {
  if (e === false)
    return false;
  if (t.charAt(e) === "/" && t.charAt(e + 1) === "*") {
    for (let r = e + 2; r < t.length; ++r)
      if (t.charAt(r) === "*" && t.charAt(r + 1) === "/")
        return r + 2;
  }
  return e;
}
var Ee$1 = So;
function vo(t, e) {
  return e === false ? false : t.charAt(e) === "/" && t.charAt(e + 1) === "/" ? mt$1(t, e) : e;
}
var he = vo;
function Po(t, e) {
  let r = null, n = e;
  for (; n !== r; )
    r = n, n = N(t, n), n = Ee$1(t, n), n = he(t, n), n = Y$1(t, n);
  return n;
}
var Ve = Po;
function Lo$1(t, e) {
  let r = null, n = e;
  for (; n !== r; )
    r = n, n = Ft$1(t, n), n = Ee$1(t, n), n = N(t, n);
  return n = he(t, n), n = Y$1(t, n), n !== false && V$1(t, n);
}
var ht$1 = Lo$1;
function Zt(t) {
  if (typeof t != "string")
    throw new TypeError("Expected a string");
  return t.replace(/[|\\{}()[\]^$+*?.]/g, "\\$&").replace(/-/g, "\\x2d");
}
function Io(t, e) {
  let r = t.match(new RegExp(`(${Zt(e)})+`, "g"));
  return r === null ? 0 : r.reduce((n, o) => Math.max(n, o.length / e.length), 0);
}
var Qn$1 = Io;
function Ro(t, e) {
  let r = t.lastIndexOf(`
`);
  return r === -1 ? 0 : pe(t.slice(r + 1).match(/^[\t ]*/)[0], e);
}
var eu = Ro;
function Yo(t, e, r) {
  for (let n = e; n < r; ++n)
    if (t.charAt(n) === `
`)
      return true;
  return false;
}
var tu = Yo;
function jo(t, e, r = {}) {
  return N(t, r.backwards ? e - 1 : e, r) !== e;
}
var ru = jo;
function Vo$1(t, e) {
  let r = Ve(t, e);
  return r === false ? "" : t.charAt(r);
}
var nu = Vo$1;
function $o$1(t, e, r) {
  let n = e === '"' ? "'" : '"', u = ee$1(false, t, /\\(.)|(["'])/gs, (i, s, a) => s === n ? s : a === e ? "\\" + a : a || (r && /^[^\n\r"'0-7\\bfnrt-vx\u2028\u2029]$/.test(s) ? s : "\\" + s));
  return e + u + e;
}
var uu = $o$1;
function Uo(t, e, r) {
  return Ve(t, r(e));
}
function Mo(t, e) {
  return arguments.length === 2 || typeof e == "number" ? Ve(t, e) : Uo(...arguments);
}
function Wo(t, e, r) {
  return Ye(t, r(e));
}
function zo$1(t, e) {
  return arguments.length === 2 || typeof e == "number" ? Ye(t, e) : Wo(...arguments);
}
function Go(t, e, r) {
  return ht$1(t, r(e));
}
function Ko(t, e) {
  return arguments.length === 2 || typeof e == "number" ? ht$1(t, e) : Go(...arguments);
}
var er$1 = {};
Me(er$1, { builders: () => Ho$1, printer: () => qo, utils: () => Jo });
var Ho$1 = { join: ke, line: Ke, softline: Er, hardline: G$1, literalline: He, group: xt$1, conditionalGroup: fr, fill: Ge, lineSuffix: _e$1, lineSuffixBoundary: Fr, cursor: Be, breakParent: le$1, ifBreak: pr, trim: mr, indent: ie, indentIfBreak: dr, align: oe$1, addAlignmentToDoc: qe$1, markAsRoot: cr$1, dedentToRoot: Dr, dedent: lr$1, hardlineWithoutBreakParent: Ae$1, literallineWithoutBreakParent: _t, label: hr, concat: (t) => t }, qo = { printDocToString: fe$1 }, Jo = { willBreak: kr$1, traverseDoc: xe, findInDoc: Je$1, mapDoc: Ne$1, removeLines: wr$1, stripTrailingHardline: Xe$1, replaceEndOfLine: Or$1, canBreak: Nr };
var ou = "3.0.2";
function De$1(t, e = 1) {
  return async (...r) => {
    let n = r[e] ?? {}, o = n.plugins ?? [];
    return r[e] = { ...n, plugins: Array.isArray(o) ? o : Object.values(o) }, t(...r);
  };
}
var iu = De$1(Xt);
async function su(t, e) {
  let { formatted: r } = await iu(t, { ...e, cursorOffset: -1 });
  return r;
}
async function Xo(t, e) {
  return await su(t, e) === t;
}
var Zo = De$1(et, 0), Qo$1 = { parse: De$1(Hn$1), formatAST: De$1(qn), formatDoc: De$1(Jn$1), printToDoc: De$1(Xn$1), printDocToString: De$1(Zn$1) };
var el = Object.create;
var lt2 = Object.defineProperty;
var rl = Object.getOwnPropertyDescriptor;
var tl = Object.getOwnPropertyNames;
var nl = Object.getPrototypeOf, il = Object.prototype.hasOwnProperty;
var C = (e, r) => () => (r || e((r = { exports: {} }).exports, r), r.exports), On = (e, r) => {
  for (var n in r)
    lt2(e, n, { get: r[n], enumerable: true });
}, ul = (e, r, n, t) => {
  if (r && typeof r == "object" || typeof r == "function")
    for (let a of tl(r))
      !il.call(e, a) && a !== n && lt2(e, a, { get: () => r[a], enumerable: !(t = rl(r, a)) || t.enumerable });
  return e;
};
var Ie = (e, r, n) => (n = e != null ? el(nl(e)) : {}, ul(r || !e || !e.__esModule ? lt2(n, "default", { value: e, enumerable: true }) : n, e));
var kr = C((Tg, In2) => {
  In2.exports = sl;
  function sl(e) {
    return String(e).replace(/\s+/g, " ");
  }
});
var _e = C((_v, Si) => {
  Si.exports = cf;
  var sf = Object.prototype.hasOwnProperty;
  function cf() {
    for (var e = {}, r = 0; r < arguments.length; r++) {
      var n = arguments[r];
      for (var t in n)
        sf.call(n, t) && (e[t] = n[t]);
    }
    return e;
  }
});
var Pi = C((Lv, yt2) => {
  typeof Object.create == "function" ? yt2.exports = function(r, n) {
    n && (r.super_ = n, r.prototype = Object.create(n.prototype, { constructor: { value: r, enumerable: false, writable: true, configurable: true } }));
  } : yt2.exports = function(r, n) {
    if (n) {
      r.super_ = n;
      var t = function() {
      };
      t.prototype = n.prototype, r.prototype = new t(), r.prototype.constructor = r;
    }
  };
});
var Oi = C((Ov, Li) => {
  var lf = _e(), _i = Pi();
  Li.exports = ff;
  function ff(e) {
    var r, n, t;
    _i(i, e), _i(a, i), r = i.prototype;
    for (n in r)
      t = r[n], t && typeof t == "object" && (r[n] = "concat" in t ? t.concat() : lf(t));
    return i;
    function a(u) {
      return e.apply(this, u);
    }
    function i() {
      return this instanceof i ? e.apply(this, arguments) : new a(arguments);
    }
  }
});
var Ni = C((Iv, Ii) => {
  Ii.exports = Df;
  function Df(e, r, n) {
    return t;
    function t() {
      var a = n || this, i = a[e];
      return a[e] = !r, u;
      function u() {
        a[e] = i;
      }
    }
  }
});
var zi = C((Nv, Ri) => {
  Ri.exports = pf;
  function pf(e) {
    for (var r = String(e), n = [], t = /\r?\n|\r/g; t.exec(r); )
      n.push(t.lastIndex);
    return n.push(r.length + 1), { toPoint: a, toPosition: a, toOffset: i };
    function a(u) {
      var o = -1;
      if (u > -1 && u < n[n.length - 1]) {
        for (; ++o < n.length; )
          if (n[o] > u)
            return { line: o + 1, column: u - (n[o - 1] || 0) + 1, offset: u };
      }
      return {};
    }
    function i(u) {
      var o = u && u.line, s = u && u.column, l;
      return !isNaN(o) && !isNaN(s) && o - 1 in n && (l = (n[o - 2] || 0) + s - 1 || 0), l > -1 && l < n[n.length - 1] ? l : -1;
    }
  }
});
var Ui = C((Rv, Mi) => {
  Mi.exports = df;
  var kt2 = "\\";
  function df(e, r) {
    return n;
    function n(t) {
      for (var a = 0, i = t.indexOf(kt2), u = e[r], o = [], s; i !== -1; )
        o.push(t.slice(a, i)), a = i + 1, s = t.charAt(a), (!s || u.indexOf(s) === -1) && o.push(kt2), i = t.indexOf(kt2, a + 1);
      return o.push(t.slice(a)), o.join("");
    }
  }
});
var Yi = C((zv, hf) => {
  hf.exports = { AElig: "Æ", AMP: "&", Aacute: "Á", Acirc: "Â", Agrave: "À", Aring: "Å", Atilde: "Ã", Auml: "Ä", COPY: "©", Ccedil: "Ç", ETH: "Ð", Eacute: "É", Ecirc: "Ê", Egrave: "È", Euml: "Ë", GT: ">", Iacute: "Í", Icirc: "Î", Igrave: "Ì", Iuml: "Ï", LT: "<", Ntilde: "Ñ", Oacute: "Ó", Ocirc: "Ô", Ograve: "Ò", Oslash: "Ø", Otilde: "Õ", Ouml: "Ö", QUOT: '"', REG: "®", THORN: "Þ", Uacute: "Ú", Ucirc: "Û", Ugrave: "Ù", Uuml: "Ü", Yacute: "Ý", aacute: "á", acirc: "â", acute: "´", aelig: "æ", agrave: "à", amp: "&", aring: "å", atilde: "ã", auml: "ä", brvbar: "¦", ccedil: "ç", cedil: "¸", cent: "¢", copy: "©", curren: "¤", deg: "°", divide: "÷", eacute: "é", ecirc: "ê", egrave: "è", eth: "ð", euml: "ë", frac12: "½", frac14: "¼", frac34: "¾", gt: ">", iacute: "í", icirc: "î", iexcl: "¡", igrave: "ì", iquest: "¿", iuml: "ï", laquo: "«", lt: "<", macr: "¯", micro: "µ", middot: "·", nbsp: " ", not: "¬", ntilde: "ñ", oacute: "ó", ocirc: "ô", ograve: "ò", ordf: "ª", ordm: "º", oslash: "ø", otilde: "õ", ouml: "ö", para: "¶", plusmn: "±", pound: "£", quot: '"', raquo: "»", reg: "®", sect: "§", shy: "­", sup1: "¹", sup2: "²", sup3: "³", szlig: "ß", thorn: "þ", times: "×", uacute: "ú", ucirc: "û", ugrave: "ù", uml: "¨", uuml: "ü", yacute: "ý", yen: "¥", yuml: "ÿ" };
});
var Vi = C((Mv, mf) => {
  mf.exports = { "0": "�", "128": "€", "130": "‚", "131": "ƒ", "132": "„", "133": "…", "134": "†", "135": "‡", "136": "ˆ", "137": "‰", "138": "Š", "139": "‹", "140": "Œ", "142": "Ž", "145": "‘", "146": "’", "147": "“", "148": "”", "149": "•", "150": "–", "151": "—", "152": "˜", "153": "™", "154": "š", "155": "›", "156": "œ", "158": "ž", "159": "Ÿ" };
});
var Le2 = C((Uv, ji) => {
  ji.exports = gf;
  function gf(e) {
    var r = typeof e == "string" ? e.charCodeAt(0) : e;
    return r >= 48 && r <= 57;
  }
});
var Gi = C((Yv, $i) => {
  $i.exports = vf;
  function vf(e) {
    var r = typeof e == "string" ? e.charCodeAt(0) : e;
    return r >= 97 && r <= 102 || r >= 65 && r <= 70 || r >= 48 && r <= 57;
  }
});
var ze = C((Vv, Hi) => {
  Hi.exports = Ff;
  function Ff(e) {
    var r = typeof e == "string" ? e.charCodeAt(0) : e;
    return r >= 97 && r <= 122 || r >= 65 && r <= 90;
  }
});
var Ki = C((jv, Wi) => {
  var Ef = ze(), Cf = Le2();
  Wi.exports = bf;
  function bf(e) {
    return Ef(e) || Cf(e);
  }
});
var Ji = C(($v, xf) => {
  xf.exports = { AEli: "Æ", AElig: "Æ", AM: "&", AMP: "&", Aacut: "Á", Aacute: "Á", Abreve: "Ă", Acir: "Â", Acirc: "Â", Acy: "А", Afr: "𝔄", Agrav: "À", Agrave: "À", Alpha: "Α", Amacr: "Ā", And: "⩓", Aogon: "Ą", Aopf: "𝔸", ApplyFunction: "⁡", Arin: "Å", Aring: "Å", Ascr: "𝒜", Assign: "≔", Atild: "Ã", Atilde: "Ã", Aum: "Ä", Auml: "Ä", Backslash: "∖", Barv: "⫧", Barwed: "⌆", Bcy: "Б", Because: "∵", Bernoullis: "ℬ", Beta: "Β", Bfr: "𝔅", Bopf: "𝔹", Breve: "˘", Bscr: "ℬ", Bumpeq: "≎", CHcy: "Ч", COP: "©", COPY: "©", Cacute: "Ć", Cap: "⋒", CapitalDifferentialD: "ⅅ", Cayleys: "ℭ", Ccaron: "Č", Ccedi: "Ç", Ccedil: "Ç", Ccirc: "Ĉ", Cconint: "∰", Cdot: "Ċ", Cedilla: "¸", CenterDot: "·", Cfr: "ℭ", Chi: "Χ", CircleDot: "⊙", CircleMinus: "⊖", CirclePlus: "⊕", CircleTimes: "⊗", ClockwiseContourIntegral: "∲", CloseCurlyDoubleQuote: "”", CloseCurlyQuote: "’", Colon: "∷", Colone: "⩴", Congruent: "≡", Conint: "∯", ContourIntegral: "∮", Copf: "ℂ", Coproduct: "∐", CounterClockwiseContourIntegral: "∳", Cross: "⨯", Cscr: "𝒞", Cup: "⋓", CupCap: "≍", DD: "ⅅ", DDotrahd: "⤑", DJcy: "Ђ", DScy: "Ѕ", DZcy: "Џ", Dagger: "‡", Darr: "↡", Dashv: "⫤", Dcaron: "Ď", Dcy: "Д", Del: "∇", Delta: "Δ", Dfr: "𝔇", DiacriticalAcute: "´", DiacriticalDot: "˙", DiacriticalDoubleAcute: "˝", DiacriticalGrave: "`", DiacriticalTilde: "˜", Diamond: "⋄", DifferentialD: "ⅆ", Dopf: "𝔻", Dot: "¨", DotDot: "⃜", DotEqual: "≐", DoubleContourIntegral: "∯", DoubleDot: "¨", DoubleDownArrow: "⇓", DoubleLeftArrow: "⇐", DoubleLeftRightArrow: "⇔", DoubleLeftTee: "⫤", DoubleLongLeftArrow: "⟸", DoubleLongLeftRightArrow: "⟺", DoubleLongRightArrow: "⟹", DoubleRightArrow: "⇒", DoubleRightTee: "⊨", DoubleUpArrow: "⇑", DoubleUpDownArrow: "⇕", DoubleVerticalBar: "∥", DownArrow: "↓", DownArrowBar: "⤓", DownArrowUpArrow: "⇵", DownBreve: "̑", DownLeftRightVector: "⥐", DownLeftTeeVector: "⥞", DownLeftVector: "↽", DownLeftVectorBar: "⥖", DownRightTeeVector: "⥟", DownRightVector: "⇁", DownRightVectorBar: "⥗", DownTee: "⊤", DownTeeArrow: "↧", Downarrow: "⇓", Dscr: "𝒟", Dstrok: "Đ", ENG: "Ŋ", ET: "Ð", ETH: "Ð", Eacut: "É", Eacute: "É", Ecaron: "Ě", Ecir: "Ê", Ecirc: "Ê", Ecy: "Э", Edot: "Ė", Efr: "𝔈", Egrav: "È", Egrave: "È", Element: "∈", Emacr: "Ē", EmptySmallSquare: "◻", EmptyVerySmallSquare: "▫", Eogon: "Ę", Eopf: "𝔼", Epsilon: "Ε", Equal: "⩵", EqualTilde: "≂", Equilibrium: "⇌", Escr: "ℰ", Esim: "⩳", Eta: "Η", Eum: "Ë", Euml: "Ë", Exists: "∃", ExponentialE: "ⅇ", Fcy: "Ф", Ffr: "𝔉", FilledSmallSquare: "◼", FilledVerySmallSquare: "▪", Fopf: "𝔽", ForAll: "∀", Fouriertrf: "ℱ", Fscr: "ℱ", GJcy: "Ѓ", G: ">", GT: ">", Gamma: "Γ", Gammad: "Ϝ", Gbreve: "Ğ", Gcedil: "Ģ", Gcirc: "Ĝ", Gcy: "Г", Gdot: "Ġ", Gfr: "𝔊", Gg: "⋙", Gopf: "𝔾", GreaterEqual: "≥", GreaterEqualLess: "⋛", GreaterFullEqual: "≧", GreaterGreater: "⪢", GreaterLess: "≷", GreaterSlantEqual: "⩾", GreaterTilde: "≳", Gscr: "𝒢", Gt: "≫", HARDcy: "Ъ", Hacek: "ˇ", Hat: "^", Hcirc: "Ĥ", Hfr: "ℌ", HilbertSpace: "ℋ", Hopf: "ℍ", HorizontalLine: "─", Hscr: "ℋ", Hstrok: "Ħ", HumpDownHump: "≎", HumpEqual: "≏", IEcy: "Е", IJlig: "Ĳ", IOcy: "Ё", Iacut: "Í", Iacute: "Í", Icir: "Î", Icirc: "Î", Icy: "И", Idot: "İ", Ifr: "ℑ", Igrav: "Ì", Igrave: "Ì", Im: "ℑ", Imacr: "Ī", ImaginaryI: "ⅈ", Implies: "⇒", Int: "∬", Integral: "∫", Intersection: "⋂", InvisibleComma: "⁣", InvisibleTimes: "⁢", Iogon: "Į", Iopf: "𝕀", Iota: "Ι", Iscr: "ℐ", Itilde: "Ĩ", Iukcy: "І", Ium: "Ï", Iuml: "Ï", Jcirc: "Ĵ", Jcy: "Й", Jfr: "𝔍", Jopf: "𝕁", Jscr: "𝒥", Jsercy: "Ј", Jukcy: "Є", KHcy: "Х", KJcy: "Ќ", Kappa: "Κ", Kcedil: "Ķ", Kcy: "К", Kfr: "𝔎", Kopf: "𝕂", Kscr: "𝒦", LJcy: "Љ", L: "<", LT: "<", Lacute: "Ĺ", Lambda: "Λ", Lang: "⟪", Laplacetrf: "ℒ", Larr: "↞", Lcaron: "Ľ", Lcedil: "Ļ", Lcy: "Л", LeftAngleBracket: "⟨", LeftArrow: "←", LeftArrowBar: "⇤", LeftArrowRightArrow: "⇆", LeftCeiling: "⌈", LeftDoubleBracket: "⟦", LeftDownTeeVector: "⥡", LeftDownVector: "⇃", LeftDownVectorBar: "⥙", LeftFloor: "⌊", LeftRightArrow: "↔", LeftRightVector: "⥎", LeftTee: "⊣", LeftTeeArrow: "↤", LeftTeeVector: "⥚", LeftTriangle: "⊲", LeftTriangleBar: "⧏", LeftTriangleEqual: "⊴", LeftUpDownVector: "⥑", LeftUpTeeVector: "⥠", LeftUpVector: "↿", LeftUpVectorBar: "⥘", LeftVector: "↼", LeftVectorBar: "⥒", Leftarrow: "⇐", Leftrightarrow: "⇔", LessEqualGreater: "⋚", LessFullEqual: "≦", LessGreater: "≶", LessLess: "⪡", LessSlantEqual: "⩽", LessTilde: "≲", Lfr: "𝔏", Ll: "⋘", Lleftarrow: "⇚", Lmidot: "Ŀ", LongLeftArrow: "⟵", LongLeftRightArrow: "⟷", LongRightArrow: "⟶", Longleftarrow: "⟸", Longleftrightarrow: "⟺", Longrightarrow: "⟹", Lopf: "𝕃", LowerLeftArrow: "↙", LowerRightArrow: "↘", Lscr: "ℒ", Lsh: "↰", Lstrok: "Ł", Lt: "≪", Map: "⤅", Mcy: "М", MediumSpace: " ", Mellintrf: "ℳ", Mfr: "𝔐", MinusPlus: "∓", Mopf: "𝕄", Mscr: "ℳ", Mu: "Μ", NJcy: "Њ", Nacute: "Ń", Ncaron: "Ň", Ncedil: "Ņ", Ncy: "Н", NegativeMediumSpace: "​", NegativeThickSpace: "​", NegativeThinSpace: "​", NegativeVeryThinSpace: "​", NestedGreaterGreater: "≫", NestedLessLess: "≪", NewLine: `
`, Nfr: "𝔑", NoBreak: "⁠", NonBreakingSpace: " ", Nopf: "ℕ", Not: "⫬", NotCongruent: "≢", NotCupCap: "≭", NotDoubleVerticalBar: "∦", NotElement: "∉", NotEqual: "≠", NotEqualTilde: "≂̸", NotExists: "∄", NotGreater: "≯", NotGreaterEqual: "≱", NotGreaterFullEqual: "≧̸", NotGreaterGreater: "≫̸", NotGreaterLess: "≹", NotGreaterSlantEqual: "⩾̸", NotGreaterTilde: "≵", NotHumpDownHump: "≎̸", NotHumpEqual: "≏̸", NotLeftTriangle: "⋪", NotLeftTriangleBar: "⧏̸", NotLeftTriangleEqual: "⋬", NotLess: "≮", NotLessEqual: "≰", NotLessGreater: "≸", NotLessLess: "≪̸", NotLessSlantEqual: "⩽̸", NotLessTilde: "≴", NotNestedGreaterGreater: "⪢̸", NotNestedLessLess: "⪡̸", NotPrecedes: "⊀", NotPrecedesEqual: "⪯̸", NotPrecedesSlantEqual: "⋠", NotReverseElement: "∌", NotRightTriangle: "⋫", NotRightTriangleBar: "⧐̸", NotRightTriangleEqual: "⋭", NotSquareSubset: "⊏̸", NotSquareSubsetEqual: "⋢", NotSquareSuperset: "⊐̸", NotSquareSupersetEqual: "⋣", NotSubset: "⊂⃒", NotSubsetEqual: "⊈", NotSucceeds: "⊁", NotSucceedsEqual: "⪰̸", NotSucceedsSlantEqual: "⋡", NotSucceedsTilde: "≿̸", NotSuperset: "⊃⃒", NotSupersetEqual: "⊉", NotTilde: "≁", NotTildeEqual: "≄", NotTildeFullEqual: "≇", NotTildeTilde: "≉", NotVerticalBar: "∤", Nscr: "𝒩", Ntild: "Ñ", Ntilde: "Ñ", Nu: "Ν", OElig: "Œ", Oacut: "Ó", Oacute: "Ó", Ocir: "Ô", Ocirc: "Ô", Ocy: "О", Odblac: "Ő", Ofr: "𝔒", Ograv: "Ò", Ograve: "Ò", Omacr: "Ō", Omega: "Ω", Omicron: "Ο", Oopf: "𝕆", OpenCurlyDoubleQuote: "“", OpenCurlyQuote: "‘", Or: "⩔", Oscr: "𝒪", Oslas: "Ø", Oslash: "Ø", Otild: "Õ", Otilde: "Õ", Otimes: "⨷", Oum: "Ö", Ouml: "Ö", OverBar: "‾", OverBrace: "⏞", OverBracket: "⎴", OverParenthesis: "⏜", PartialD: "∂", Pcy: "П", Pfr: "𝔓", Phi: "Φ", Pi: "Π", PlusMinus: "±", Poincareplane: "ℌ", Popf: "ℙ", Pr: "⪻", Precedes: "≺", PrecedesEqual: "⪯", PrecedesSlantEqual: "≼", PrecedesTilde: "≾", Prime: "″", Product: "∏", Proportion: "∷", Proportional: "∝", Pscr: "𝒫", Psi: "Ψ", QUO: '"', QUOT: '"', Qfr: "𝔔", Qopf: "ℚ", Qscr: "𝒬", RBarr: "⤐", RE: "®", REG: "®", Racute: "Ŕ", Rang: "⟫", Rarr: "↠", Rarrtl: "⤖", Rcaron: "Ř", Rcedil: "Ŗ", Rcy: "Р", Re: "ℜ", ReverseElement: "∋", ReverseEquilibrium: "⇋", ReverseUpEquilibrium: "⥯", Rfr: "ℜ", Rho: "Ρ", RightAngleBracket: "⟩", RightArrow: "→", RightArrowBar: "⇥", RightArrowLeftArrow: "⇄", RightCeiling: "⌉", RightDoubleBracket: "⟧", RightDownTeeVector: "⥝", RightDownVector: "⇂", RightDownVectorBar: "⥕", RightFloor: "⌋", RightTee: "⊢", RightTeeArrow: "↦", RightTeeVector: "⥛", RightTriangle: "⊳", RightTriangleBar: "⧐", RightTriangleEqual: "⊵", RightUpDownVector: "⥏", RightUpTeeVector: "⥜", RightUpVector: "↾", RightUpVectorBar: "⥔", RightVector: "⇀", RightVectorBar: "⥓", Rightarrow: "⇒", Ropf: "ℝ", RoundImplies: "⥰", Rrightarrow: "⇛", Rscr: "ℛ", Rsh: "↱", RuleDelayed: "⧴", SHCHcy: "Щ", SHcy: "Ш", SOFTcy: "Ь", Sacute: "Ś", Sc: "⪼", Scaron: "Š", Scedil: "Ş", Scirc: "Ŝ", Scy: "С", Sfr: "𝔖", ShortDownArrow: "↓", ShortLeftArrow: "←", ShortRightArrow: "→", ShortUpArrow: "↑", Sigma: "Σ", SmallCircle: "∘", Sopf: "𝕊", Sqrt: "√", Square: "□", SquareIntersection: "⊓", SquareSubset: "⊏", SquareSubsetEqual: "⊑", SquareSuperset: "⊐", SquareSupersetEqual: "⊒", SquareUnion: "⊔", Sscr: "𝒮", Star: "⋆", Sub: "⋐", Subset: "⋐", SubsetEqual: "⊆", Succeeds: "≻", SucceedsEqual: "⪰", SucceedsSlantEqual: "≽", SucceedsTilde: "≿", SuchThat: "∋", Sum: "∑", Sup: "⋑", Superset: "⊃", SupersetEqual: "⊇", Supset: "⋑", THOR: "Þ", THORN: "Þ", TRADE: "™", TSHcy: "Ћ", TScy: "Ц", Tab: "	", Tau: "Τ", Tcaron: "Ť", Tcedil: "Ţ", Tcy: "Т", Tfr: "𝔗", Therefore: "∴", Theta: "Θ", ThickSpace: "  ", ThinSpace: " ", Tilde: "∼", TildeEqual: "≃", TildeFullEqual: "≅", TildeTilde: "≈", Topf: "𝕋", TripleDot: "⃛", Tscr: "𝒯", Tstrok: "Ŧ", Uacut: "Ú", Uacute: "Ú", Uarr: "↟", Uarrocir: "⥉", Ubrcy: "Ў", Ubreve: "Ŭ", Ucir: "Û", Ucirc: "Û", Ucy: "У", Udblac: "Ű", Ufr: "𝔘", Ugrav: "Ù", Ugrave: "Ù", Umacr: "Ū", UnderBar: "_", UnderBrace: "⏟", UnderBracket: "⎵", UnderParenthesis: "⏝", Union: "⋃", UnionPlus: "⊎", Uogon: "Ų", Uopf: "𝕌", UpArrow: "↑", UpArrowBar: "⤒", UpArrowDownArrow: "⇅", UpDownArrow: "↕", UpEquilibrium: "⥮", UpTee: "⊥", UpTeeArrow: "↥", Uparrow: "⇑", Updownarrow: "⇕", UpperLeftArrow: "↖", UpperRightArrow: "↗", Upsi: "ϒ", Upsilon: "Υ", Uring: "Ů", Uscr: "𝒰", Utilde: "Ũ", Uum: "Ü", Uuml: "Ü", VDash: "⊫", Vbar: "⫫", Vcy: "В", Vdash: "⊩", Vdashl: "⫦", Vee: "⋁", Verbar: "‖", Vert: "‖", VerticalBar: "∣", VerticalLine: "|", VerticalSeparator: "❘", VerticalTilde: "≀", VeryThinSpace: " ", Vfr: "𝔙", Vopf: "𝕍", Vscr: "𝒱", Vvdash: "⊪", Wcirc: "Ŵ", Wedge: "⋀", Wfr: "𝔚", Wopf: "𝕎", Wscr: "𝒲", Xfr: "𝔛", Xi: "Ξ", Xopf: "𝕏", Xscr: "𝒳", YAcy: "Я", YIcy: "Ї", YUcy: "Ю", Yacut: "Ý", Yacute: "Ý", Ycirc: "Ŷ", Ycy: "Ы", Yfr: "𝔜", Yopf: "𝕐", Yscr: "𝒴", Yuml: "Ÿ", ZHcy: "Ж", Zacute: "Ź", Zcaron: "Ž", Zcy: "З", Zdot: "Ż", ZeroWidthSpace: "​", Zeta: "Ζ", Zfr: "ℨ", Zopf: "ℤ", Zscr: "𝒵", aacut: "á", aacute: "á", abreve: "ă", ac: "∾", acE: "∾̳", acd: "∿", acir: "â", acirc: "â", acut: "´", acute: "´", acy: "а", aeli: "æ", aelig: "æ", af: "⁡", afr: "𝔞", agrav: "à", agrave: "à", alefsym: "ℵ", aleph: "ℵ", alpha: "α", amacr: "ā", amalg: "⨿", am: "&", amp: "&", and: "∧", andand: "⩕", andd: "⩜", andslope: "⩘", andv: "⩚", ang: "∠", ange: "⦤", angle: "∠", angmsd: "∡", angmsdaa: "⦨", angmsdab: "⦩", angmsdac: "⦪", angmsdad: "⦫", angmsdae: "⦬", angmsdaf: "⦭", angmsdag: "⦮", angmsdah: "⦯", angrt: "∟", angrtvb: "⊾", angrtvbd: "⦝", angsph: "∢", angst: "Å", angzarr: "⍼", aogon: "ą", aopf: "𝕒", ap: "≈", apE: "⩰", apacir: "⩯", ape: "≊", apid: "≋", apos: "'", approx: "≈", approxeq: "≊", arin: "å", aring: "å", ascr: "𝒶", ast: "*", asymp: "≈", asympeq: "≍", atild: "ã", atilde: "ã", aum: "ä", auml: "ä", awconint: "∳", awint: "⨑", bNot: "⫭", backcong: "≌", backepsilon: "϶", backprime: "‵", backsim: "∽", backsimeq: "⋍", barvee: "⊽", barwed: "⌅", barwedge: "⌅", bbrk: "⎵", bbrktbrk: "⎶", bcong: "≌", bcy: "б", bdquo: "„", becaus: "∵", because: "∵", bemptyv: "⦰", bepsi: "϶", bernou: "ℬ", beta: "β", beth: "ℶ", between: "≬", bfr: "𝔟", bigcap: "⋂", bigcirc: "◯", bigcup: "⋃", bigodot: "⨀", bigoplus: "⨁", bigotimes: "⨂", bigsqcup: "⨆", bigstar: "★", bigtriangledown: "▽", bigtriangleup: "△", biguplus: "⨄", bigvee: "⋁", bigwedge: "⋀", bkarow: "⤍", blacklozenge: "⧫", blacksquare: "▪", blacktriangle: "▴", blacktriangledown: "▾", blacktriangleleft: "◂", blacktriangleright: "▸", blank: "␣", blk12: "▒", blk14: "░", blk34: "▓", block: "█", bne: "=⃥", bnequiv: "≡⃥", bnot: "⌐", bopf: "𝕓", bot: "⊥", bottom: "⊥", bowtie: "⋈", boxDL: "╗", boxDR: "╔", boxDl: "╖", boxDr: "╓", boxH: "═", boxHD: "╦", boxHU: "╩", boxHd: "╤", boxHu: "╧", boxUL: "╝", boxUR: "╚", boxUl: "╜", boxUr: "╙", boxV: "║", boxVH: "╬", boxVL: "╣", boxVR: "╠", boxVh: "╫", boxVl: "╢", boxVr: "╟", boxbox: "⧉", boxdL: "╕", boxdR: "╒", boxdl: "┐", boxdr: "┌", boxh: "─", boxhD: "╥", boxhU: "╨", boxhd: "┬", boxhu: "┴", boxminus: "⊟", boxplus: "⊞", boxtimes: "⊠", boxuL: "╛", boxuR: "╘", boxul: "┘", boxur: "└", boxv: "│", boxvH: "╪", boxvL: "╡", boxvR: "╞", boxvh: "┼", boxvl: "┤", boxvr: "├", bprime: "‵", breve: "˘", brvba: "¦", brvbar: "¦", bscr: "𝒷", bsemi: "⁏", bsim: "∽", bsime: "⋍", bsol: "\\", bsolb: "⧅", bsolhsub: "⟈", bull: "•", bullet: "•", bump: "≎", bumpE: "⪮", bumpe: "≏", bumpeq: "≏", cacute: "ć", cap: "∩", capand: "⩄", capbrcup: "⩉", capcap: "⩋", capcup: "⩇", capdot: "⩀", caps: "∩︀", caret: "⁁", caron: "ˇ", ccaps: "⩍", ccaron: "č", ccedi: "ç", ccedil: "ç", ccirc: "ĉ", ccups: "⩌", ccupssm: "⩐", cdot: "ċ", cedi: "¸", cedil: "¸", cemptyv: "⦲", cen: "¢", cent: "¢", centerdot: "·", cfr: "𝔠", chcy: "ч", check: "✓", checkmark: "✓", chi: "χ", cir: "○", cirE: "⧃", circ: "ˆ", circeq: "≗", circlearrowleft: "↺", circlearrowright: "↻", circledR: "®", circledS: "Ⓢ", circledast: "⊛", circledcirc: "⊚", circleddash: "⊝", cire: "≗", cirfnint: "⨐", cirmid: "⫯", cirscir: "⧂", clubs: "♣", clubsuit: "♣", colon: ":", colone: "≔", coloneq: "≔", comma: ",", commat: "@", comp: "∁", compfn: "∘", complement: "∁", complexes: "ℂ", cong: "≅", congdot: "⩭", conint: "∮", copf: "𝕔", coprod: "∐", cop: "©", copy: "©", copysr: "℗", crarr: "↵", cross: "✗", cscr: "𝒸", csub: "⫏", csube: "⫑", csup: "⫐", csupe: "⫒", ctdot: "⋯", cudarrl: "⤸", cudarrr: "⤵", cuepr: "⋞", cuesc: "⋟", cularr: "↶", cularrp: "⤽", cup: "∪", cupbrcap: "⩈", cupcap: "⩆", cupcup: "⩊", cupdot: "⊍", cupor: "⩅", cups: "∪︀", curarr: "↷", curarrm: "⤼", curlyeqprec: "⋞", curlyeqsucc: "⋟", curlyvee: "⋎", curlywedge: "⋏", curre: "¤", curren: "¤", curvearrowleft: "↶", curvearrowright: "↷", cuvee: "⋎", cuwed: "⋏", cwconint: "∲", cwint: "∱", cylcty: "⌭", dArr: "⇓", dHar: "⥥", dagger: "†", daleth: "ℸ", darr: "↓", dash: "‐", dashv: "⊣", dbkarow: "⤏", dblac: "˝", dcaron: "ď", dcy: "д", dd: "ⅆ", ddagger: "‡", ddarr: "⇊", ddotseq: "⩷", de: "°", deg: "°", delta: "δ", demptyv: "⦱", dfisht: "⥿", dfr: "𝔡", dharl: "⇃", dharr: "⇂", diam: "⋄", diamond: "⋄", diamondsuit: "♦", diams: "♦", die: "¨", digamma: "ϝ", disin: "⋲", div: "÷", divid: "÷", divide: "÷", divideontimes: "⋇", divonx: "⋇", djcy: "ђ", dlcorn: "⌞", dlcrop: "⌍", dollar: "$", dopf: "𝕕", dot: "˙", doteq: "≐", doteqdot: "≑", dotminus: "∸", dotplus: "∔", dotsquare: "⊡", doublebarwedge: "⌆", downarrow: "↓", downdownarrows: "⇊", downharpoonleft: "⇃", downharpoonright: "⇂", drbkarow: "⤐", drcorn: "⌟", drcrop: "⌌", dscr: "𝒹", dscy: "ѕ", dsol: "⧶", dstrok: "đ", dtdot: "⋱", dtri: "▿", dtrif: "▾", duarr: "⇵", duhar: "⥯", dwangle: "⦦", dzcy: "џ", dzigrarr: "⟿", eDDot: "⩷", eDot: "≑", eacut: "é", eacute: "é", easter: "⩮", ecaron: "ě", ecir: "ê", ecirc: "ê", ecolon: "≕", ecy: "э", edot: "ė", ee: "ⅇ", efDot: "≒", efr: "𝔢", eg: "⪚", egrav: "è", egrave: "è", egs: "⪖", egsdot: "⪘", el: "⪙", elinters: "⏧", ell: "ℓ", els: "⪕", elsdot: "⪗", emacr: "ē", empty: "∅", emptyset: "∅", emptyv: "∅", emsp13: " ", emsp14: " ", emsp: " ", eng: "ŋ", ensp: " ", eogon: "ę", eopf: "𝕖", epar: "⋕", eparsl: "⧣", eplus: "⩱", epsi: "ε", epsilon: "ε", epsiv: "ϵ", eqcirc: "≖", eqcolon: "≕", eqsim: "≂", eqslantgtr: "⪖", eqslantless: "⪕", equals: "=", equest: "≟", equiv: "≡", equivDD: "⩸", eqvparsl: "⧥", erDot: "≓", erarr: "⥱", escr: "ℯ", esdot: "≐", esim: "≂", eta: "η", et: "ð", eth: "ð", eum: "ë", euml: "ë", euro: "€", excl: "!", exist: "∃", expectation: "ℰ", exponentiale: "ⅇ", fallingdotseq: "≒", fcy: "ф", female: "♀", ffilig: "ﬃ", fflig: "ﬀ", ffllig: "ﬄ", ffr: "𝔣", filig: "ﬁ", fjlig: "fj", flat: "♭", fllig: "ﬂ", fltns: "▱", fnof: "ƒ", fopf: "𝕗", forall: "∀", fork: "⋔", forkv: "⫙", fpartint: "⨍", frac1: "¼", frac12: "½", frac13: "⅓", frac14: "¼", frac15: "⅕", frac16: "⅙", frac18: "⅛", frac23: "⅔", frac25: "⅖", frac3: "¾", frac34: "¾", frac35: "⅗", frac38: "⅜", frac45: "⅘", frac56: "⅚", frac58: "⅝", frac78: "⅞", frasl: "⁄", frown: "⌢", fscr: "𝒻", gE: "≧", gEl: "⪌", gacute: "ǵ", gamma: "γ", gammad: "ϝ", gap: "⪆", gbreve: "ğ", gcirc: "ĝ", gcy: "г", gdot: "ġ", ge: "≥", gel: "⋛", geq: "≥", geqq: "≧", geqslant: "⩾", ges: "⩾", gescc: "⪩", gesdot: "⪀", gesdoto: "⪂", gesdotol: "⪄", gesl: "⋛︀", gesles: "⪔", gfr: "𝔤", gg: "≫", ggg: "⋙", gimel: "ℷ", gjcy: "ѓ", gl: "≷", glE: "⪒", gla: "⪥", glj: "⪤", gnE: "≩", gnap: "⪊", gnapprox: "⪊", gne: "⪈", gneq: "⪈", gneqq: "≩", gnsim: "⋧", gopf: "𝕘", grave: "`", gscr: "ℊ", gsim: "≳", gsime: "⪎", gsiml: "⪐", g: ">", gt: ">", gtcc: "⪧", gtcir: "⩺", gtdot: "⋗", gtlPar: "⦕", gtquest: "⩼", gtrapprox: "⪆", gtrarr: "⥸", gtrdot: "⋗", gtreqless: "⋛", gtreqqless: "⪌", gtrless: "≷", gtrsim: "≳", gvertneqq: "≩︀", gvnE: "≩︀", hArr: "⇔", hairsp: " ", half: "½", hamilt: "ℋ", hardcy: "ъ", harr: "↔", harrcir: "⥈", harrw: "↭", hbar: "ℏ", hcirc: "ĥ", hearts: "♥", heartsuit: "♥", hellip: "…", hercon: "⊹", hfr: "𝔥", hksearow: "⤥", hkswarow: "⤦", hoarr: "⇿", homtht: "∻", hookleftarrow: "↩", hookrightarrow: "↪", hopf: "𝕙", horbar: "―", hscr: "𝒽", hslash: "ℏ", hstrok: "ħ", hybull: "⁃", hyphen: "‐", iacut: "í", iacute: "í", ic: "⁣", icir: "î", icirc: "î", icy: "и", iecy: "е", iexc: "¡", iexcl: "¡", iff: "⇔", ifr: "𝔦", igrav: "ì", igrave: "ì", ii: "ⅈ", iiiint: "⨌", iiint: "∭", iinfin: "⧜", iiota: "℩", ijlig: "ĳ", imacr: "ī", image: "ℑ", imagline: "ℐ", imagpart: "ℑ", imath: "ı", imof: "⊷", imped: "Ƶ", in: "∈", incare: "℅", infin: "∞", infintie: "⧝", inodot: "ı", int: "∫", intcal: "⊺", integers: "ℤ", intercal: "⊺", intlarhk: "⨗", intprod: "⨼", iocy: "ё", iogon: "į", iopf: "𝕚", iota: "ι", iprod: "⨼", iques: "¿", iquest: "¿", iscr: "𝒾", isin: "∈", isinE: "⋹", isindot: "⋵", isins: "⋴", isinsv: "⋳", isinv: "∈", it: "⁢", itilde: "ĩ", iukcy: "і", ium: "ï", iuml: "ï", jcirc: "ĵ", jcy: "й", jfr: "𝔧", jmath: "ȷ", jopf: "𝕛", jscr: "𝒿", jsercy: "ј", jukcy: "є", kappa: "κ", kappav: "ϰ", kcedil: "ķ", kcy: "к", kfr: "𝔨", kgreen: "ĸ", khcy: "х", kjcy: "ќ", kopf: "𝕜", kscr: "𝓀", lAarr: "⇚", lArr: "⇐", lAtail: "⤛", lBarr: "⤎", lE: "≦", lEg: "⪋", lHar: "⥢", lacute: "ĺ", laemptyv: "⦴", lagran: "ℒ", lambda: "λ", lang: "⟨", langd: "⦑", langle: "⟨", lap: "⪅", laqu: "«", laquo: "«", larr: "←", larrb: "⇤", larrbfs: "⤟", larrfs: "⤝", larrhk: "↩", larrlp: "↫", larrpl: "⤹", larrsim: "⥳", larrtl: "↢", lat: "⪫", latail: "⤙", late: "⪭", lates: "⪭︀", lbarr: "⤌", lbbrk: "❲", lbrace: "{", lbrack: "[", lbrke: "⦋", lbrksld: "⦏", lbrkslu: "⦍", lcaron: "ľ", lcedil: "ļ", lceil: "⌈", lcub: "{", lcy: "л", ldca: "⤶", ldquo: "“", ldquor: "„", ldrdhar: "⥧", ldrushar: "⥋", ldsh: "↲", le: "≤", leftarrow: "←", leftarrowtail: "↢", leftharpoondown: "↽", leftharpoonup: "↼", leftleftarrows: "⇇", leftrightarrow: "↔", leftrightarrows: "⇆", leftrightharpoons: "⇋", leftrightsquigarrow: "↭", leftthreetimes: "⋋", leg: "⋚", leq: "≤", leqq: "≦", leqslant: "⩽", les: "⩽", lescc: "⪨", lesdot: "⩿", lesdoto: "⪁", lesdotor: "⪃", lesg: "⋚︀", lesges: "⪓", lessapprox: "⪅", lessdot: "⋖", lesseqgtr: "⋚", lesseqqgtr: "⪋", lessgtr: "≶", lesssim: "≲", lfisht: "⥼", lfloor: "⌊", lfr: "𝔩", lg: "≶", lgE: "⪑", lhard: "↽", lharu: "↼", lharul: "⥪", lhblk: "▄", ljcy: "љ", ll: "≪", llarr: "⇇", llcorner: "⌞", llhard: "⥫", lltri: "◺", lmidot: "ŀ", lmoust: "⎰", lmoustache: "⎰", lnE: "≨", lnap: "⪉", lnapprox: "⪉", lne: "⪇", lneq: "⪇", lneqq: "≨", lnsim: "⋦", loang: "⟬", loarr: "⇽", lobrk: "⟦", longleftarrow: "⟵", longleftrightarrow: "⟷", longmapsto: "⟼", longrightarrow: "⟶", looparrowleft: "↫", looparrowright: "↬", lopar: "⦅", lopf: "𝕝", loplus: "⨭", lotimes: "⨴", lowast: "∗", lowbar: "_", loz: "◊", lozenge: "◊", lozf: "⧫", lpar: "(", lparlt: "⦓", lrarr: "⇆", lrcorner: "⌟", lrhar: "⇋", lrhard: "⥭", lrm: "‎", lrtri: "⊿", lsaquo: "‹", lscr: "𝓁", lsh: "↰", lsim: "≲", lsime: "⪍", lsimg: "⪏", lsqb: "[", lsquo: "‘", lsquor: "‚", lstrok: "ł", l: "<", lt: "<", ltcc: "⪦", ltcir: "⩹", ltdot: "⋖", lthree: "⋋", ltimes: "⋉", ltlarr: "⥶", ltquest: "⩻", ltrPar: "⦖", ltri: "◃", ltrie: "⊴", ltrif: "◂", lurdshar: "⥊", luruhar: "⥦", lvertneqq: "≨︀", lvnE: "≨︀", mDDot: "∺", mac: "¯", macr: "¯", male: "♂", malt: "✠", maltese: "✠", map: "↦", mapsto: "↦", mapstodown: "↧", mapstoleft: "↤", mapstoup: "↥", marker: "▮", mcomma: "⨩", mcy: "м", mdash: "—", measuredangle: "∡", mfr: "𝔪", mho: "℧", micr: "µ", micro: "µ", mid: "∣", midast: "*", midcir: "⫰", middo: "·", middot: "·", minus: "−", minusb: "⊟", minusd: "∸", minusdu: "⨪", mlcp: "⫛", mldr: "…", mnplus: "∓", models: "⊧", mopf: "𝕞", mp: "∓", mscr: "𝓂", mstpos: "∾", mu: "μ", multimap: "⊸", mumap: "⊸", nGg: "⋙̸", nGt: "≫⃒", nGtv: "≫̸", nLeftarrow: "⇍", nLeftrightarrow: "⇎", nLl: "⋘̸", nLt: "≪⃒", nLtv: "≪̸", nRightarrow: "⇏", nVDash: "⊯", nVdash: "⊮", nabla: "∇", nacute: "ń", nang: "∠⃒", nap: "≉", napE: "⩰̸", napid: "≋̸", napos: "ŉ", napprox: "≉", natur: "♮", natural: "♮", naturals: "ℕ", nbs: " ", nbsp: " ", nbump: "≎̸", nbumpe: "≏̸", ncap: "⩃", ncaron: "ň", ncedil: "ņ", ncong: "≇", ncongdot: "⩭̸", ncup: "⩂", ncy: "н", ndash: "–", ne: "≠", neArr: "⇗", nearhk: "⤤", nearr: "↗", nearrow: "↗", nedot: "≐̸", nequiv: "≢", nesear: "⤨", nesim: "≂̸", nexist: "∄", nexists: "∄", nfr: "𝔫", ngE: "≧̸", nge: "≱", ngeq: "≱", ngeqq: "≧̸", ngeqslant: "⩾̸", nges: "⩾̸", ngsim: "≵", ngt: "≯", ngtr: "≯", nhArr: "⇎", nharr: "↮", nhpar: "⫲", ni: "∋", nis: "⋼", nisd: "⋺", niv: "∋", njcy: "њ", nlArr: "⇍", nlE: "≦̸", nlarr: "↚", nldr: "‥", nle: "≰", nleftarrow: "↚", nleftrightarrow: "↮", nleq: "≰", nleqq: "≦̸", nleqslant: "⩽̸", nles: "⩽̸", nless: "≮", nlsim: "≴", nlt: "≮", nltri: "⋪", nltrie: "⋬", nmid: "∤", nopf: "𝕟", no: "¬", not: "¬", notin: "∉", notinE: "⋹̸", notindot: "⋵̸", notinva: "∉", notinvb: "⋷", notinvc: "⋶", notni: "∌", notniva: "∌", notnivb: "⋾", notnivc: "⋽", npar: "∦", nparallel: "∦", nparsl: "⫽⃥", npart: "∂̸", npolint: "⨔", npr: "⊀", nprcue: "⋠", npre: "⪯̸", nprec: "⊀", npreceq: "⪯̸", nrArr: "⇏", nrarr: "↛", nrarrc: "⤳̸", nrarrw: "↝̸", nrightarrow: "↛", nrtri: "⋫", nrtrie: "⋭", nsc: "⊁", nsccue: "⋡", nsce: "⪰̸", nscr: "𝓃", nshortmid: "∤", nshortparallel: "∦", nsim: "≁", nsime: "≄", nsimeq: "≄", nsmid: "∤", nspar: "∦", nsqsube: "⋢", nsqsupe: "⋣", nsub: "⊄", nsubE: "⫅̸", nsube: "⊈", nsubset: "⊂⃒", nsubseteq: "⊈", nsubseteqq: "⫅̸", nsucc: "⊁", nsucceq: "⪰̸", nsup: "⊅", nsupE: "⫆̸", nsupe: "⊉", nsupset: "⊃⃒", nsupseteq: "⊉", nsupseteqq: "⫆̸", ntgl: "≹", ntild: "ñ", ntilde: "ñ", ntlg: "≸", ntriangleleft: "⋪", ntrianglelefteq: "⋬", ntriangleright: "⋫", ntrianglerighteq: "⋭", nu: "ν", num: "#", numero: "№", numsp: " ", nvDash: "⊭", nvHarr: "⤄", nvap: "≍⃒", nvdash: "⊬", nvge: "≥⃒", nvgt: ">⃒", nvinfin: "⧞", nvlArr: "⤂", nvle: "≤⃒", nvlt: "<⃒", nvltrie: "⊴⃒", nvrArr: "⤃", nvrtrie: "⊵⃒", nvsim: "∼⃒", nwArr: "⇖", nwarhk: "⤣", nwarr: "↖", nwarrow: "↖", nwnear: "⤧", oS: "Ⓢ", oacut: "ó", oacute: "ó", oast: "⊛", ocir: "ô", ocirc: "ô", ocy: "о", odash: "⊝", odblac: "ő", odiv: "⨸", odot: "⊙", odsold: "⦼", oelig: "œ", ofcir: "⦿", ofr: "𝔬", ogon: "˛", ograv: "ò", ograve: "ò", ogt: "⧁", ohbar: "⦵", ohm: "Ω", oint: "∮", olarr: "↺", olcir: "⦾", olcross: "⦻", oline: "‾", olt: "⧀", omacr: "ō", omega: "ω", omicron: "ο", omid: "⦶", ominus: "⊖", oopf: "𝕠", opar: "⦷", operp: "⦹", oplus: "⊕", or: "∨", orarr: "↻", ord: "º", order: "ℴ", orderof: "ℴ", ordf: "ª", ordm: "º", origof: "⊶", oror: "⩖", orslope: "⩗", orv: "⩛", oscr: "ℴ", oslas: "ø", oslash: "ø", osol: "⊘", otild: "õ", otilde: "õ", otimes: "⊗", otimesas: "⨶", oum: "ö", ouml: "ö", ovbar: "⌽", par: "¶", para: "¶", parallel: "∥", parsim: "⫳", parsl: "⫽", part: "∂", pcy: "п", percnt: "%", period: ".", permil: "‰", perp: "⊥", pertenk: "‱", pfr: "𝔭", phi: "φ", phiv: "ϕ", phmmat: "ℳ", phone: "☎", pi: "π", pitchfork: "⋔", piv: "ϖ", planck: "ℏ", planckh: "ℎ", plankv: "ℏ", plus: "+", plusacir: "⨣", plusb: "⊞", pluscir: "⨢", plusdo: "∔", plusdu: "⨥", pluse: "⩲", plusm: "±", plusmn: "±", plussim: "⨦", plustwo: "⨧", pm: "±", pointint: "⨕", popf: "𝕡", poun: "£", pound: "£", pr: "≺", prE: "⪳", prap: "⪷", prcue: "≼", pre: "⪯", prec: "≺", precapprox: "⪷", preccurlyeq: "≼", preceq: "⪯", precnapprox: "⪹", precneqq: "⪵", precnsim: "⋨", precsim: "≾", prime: "′", primes: "ℙ", prnE: "⪵", prnap: "⪹", prnsim: "⋨", prod: "∏", profalar: "⌮", profline: "⌒", profsurf: "⌓", prop: "∝", propto: "∝", prsim: "≾", prurel: "⊰", pscr: "𝓅", psi: "ψ", puncsp: " ", qfr: "𝔮", qint: "⨌", qopf: "𝕢", qprime: "⁗", qscr: "𝓆", quaternions: "ℍ", quatint: "⨖", quest: "?", questeq: "≟", quo: '"', quot: '"', rAarr: "⇛", rArr: "⇒", rAtail: "⤜", rBarr: "⤏", rHar: "⥤", race: "∽̱", racute: "ŕ", radic: "√", raemptyv: "⦳", rang: "⟩", rangd: "⦒", range: "⦥", rangle: "⟩", raqu: "»", raquo: "»", rarr: "→", rarrap: "⥵", rarrb: "⇥", rarrbfs: "⤠", rarrc: "⤳", rarrfs: "⤞", rarrhk: "↪", rarrlp: "↬", rarrpl: "⥅", rarrsim: "⥴", rarrtl: "↣", rarrw: "↝", ratail: "⤚", ratio: "∶", rationals: "ℚ", rbarr: "⤍", rbbrk: "❳", rbrace: "}", rbrack: "]", rbrke: "⦌", rbrksld: "⦎", rbrkslu: "⦐", rcaron: "ř", rcedil: "ŗ", rceil: "⌉", rcub: "}", rcy: "р", rdca: "⤷", rdldhar: "⥩", rdquo: "”", rdquor: "”", rdsh: "↳", real: "ℜ", realine: "ℛ", realpart: "ℜ", reals: "ℝ", rect: "▭", re: "®", reg: "®", rfisht: "⥽", rfloor: "⌋", rfr: "𝔯", rhard: "⇁", rharu: "⇀", rharul: "⥬", rho: "ρ", rhov: "ϱ", rightarrow: "→", rightarrowtail: "↣", rightharpoondown: "⇁", rightharpoonup: "⇀", rightleftarrows: "⇄", rightleftharpoons: "⇌", rightrightarrows: "⇉", rightsquigarrow: "↝", rightthreetimes: "⋌", ring: "˚", risingdotseq: "≓", rlarr: "⇄", rlhar: "⇌", rlm: "‏", rmoust: "⎱", rmoustache: "⎱", rnmid: "⫮", roang: "⟭", roarr: "⇾", robrk: "⟧", ropar: "⦆", ropf: "𝕣", roplus: "⨮", rotimes: "⨵", rpar: ")", rpargt: "⦔", rppolint: "⨒", rrarr: "⇉", rsaquo: "›", rscr: "𝓇", rsh: "↱", rsqb: "]", rsquo: "’", rsquor: "’", rthree: "⋌", rtimes: "⋊", rtri: "▹", rtrie: "⊵", rtrif: "▸", rtriltri: "⧎", ruluhar: "⥨", rx: "℞", sacute: "ś", sbquo: "‚", sc: "≻", scE: "⪴", scap: "⪸", scaron: "š", sccue: "≽", sce: "⪰", scedil: "ş", scirc: "ŝ", scnE: "⪶", scnap: "⪺", scnsim: "⋩", scpolint: "⨓", scsim: "≿", scy: "с", sdot: "⋅", sdotb: "⊡", sdote: "⩦", seArr: "⇘", searhk: "⤥", searr: "↘", searrow: "↘", sec: "§", sect: "§", semi: ";", seswar: "⤩", setminus: "∖", setmn: "∖", sext: "✶", sfr: "𝔰", sfrown: "⌢", sharp: "♯", shchcy: "щ", shcy: "ш", shortmid: "∣", shortparallel: "∥", sh: "­", shy: "­", sigma: "σ", sigmaf: "ς", sigmav: "ς", sim: "∼", simdot: "⩪", sime: "≃", simeq: "≃", simg: "⪞", simgE: "⪠", siml: "⪝", simlE: "⪟", simne: "≆", simplus: "⨤", simrarr: "⥲", slarr: "←", smallsetminus: "∖", smashp: "⨳", smeparsl: "⧤", smid: "∣", smile: "⌣", smt: "⪪", smte: "⪬", smtes: "⪬︀", softcy: "ь", sol: "/", solb: "⧄", solbar: "⌿", sopf: "𝕤", spades: "♠", spadesuit: "♠", spar: "∥", sqcap: "⊓", sqcaps: "⊓︀", sqcup: "⊔", sqcups: "⊔︀", sqsub: "⊏", sqsube: "⊑", sqsubset: "⊏", sqsubseteq: "⊑", sqsup: "⊐", sqsupe: "⊒", sqsupset: "⊐", sqsupseteq: "⊒", squ: "□", square: "□", squarf: "▪", squf: "▪", srarr: "→", sscr: "𝓈", ssetmn: "∖", ssmile: "⌣", sstarf: "⋆", star: "☆", starf: "★", straightepsilon: "ϵ", straightphi: "ϕ", strns: "¯", sub: "⊂", subE: "⫅", subdot: "⪽", sube: "⊆", subedot: "⫃", submult: "⫁", subnE: "⫋", subne: "⊊", subplus: "⪿", subrarr: "⥹", subset: "⊂", subseteq: "⊆", subseteqq: "⫅", subsetneq: "⊊", subsetneqq: "⫋", subsim: "⫇", subsub: "⫕", subsup: "⫓", succ: "≻", succapprox: "⪸", succcurlyeq: "≽", succeq: "⪰", succnapprox: "⪺", succneqq: "⪶", succnsim: "⋩", succsim: "≿", sum: "∑", sung: "♪", sup: "⊃", sup1: "¹", sup2: "²", sup3: "³", supE: "⫆", supdot: "⪾", supdsub: "⫘", supe: "⊇", supedot: "⫄", suphsol: "⟉", suphsub: "⫗", suplarr: "⥻", supmult: "⫂", supnE: "⫌", supne: "⊋", supplus: "⫀", supset: "⊃", supseteq: "⊇", supseteqq: "⫆", supsetneq: "⊋", supsetneqq: "⫌", supsim: "⫈", supsub: "⫔", supsup: "⫖", swArr: "⇙", swarhk: "⤦", swarr: "↙", swarrow: "↙", swnwar: "⤪", szli: "ß", szlig: "ß", target: "⌖", tau: "τ", tbrk: "⎴", tcaron: "ť", tcedil: "ţ", tcy: "т", tdot: "⃛", telrec: "⌕", tfr: "𝔱", there4: "∴", therefore: "∴", theta: "θ", thetasym: "ϑ", thetav: "ϑ", thickapprox: "≈", thicksim: "∼", thinsp: " ", thkap: "≈", thksim: "∼", thor: "þ", thorn: "þ", tilde: "˜", time: "×", times: "×", timesb: "⊠", timesbar: "⨱", timesd: "⨰", tint: "∭", toea: "⤨", top: "⊤", topbot: "⌶", topcir: "⫱", topf: "𝕥", topfork: "⫚", tosa: "⤩", tprime: "‴", trade: "™", triangle: "▵", triangledown: "▿", triangleleft: "◃", trianglelefteq: "⊴", triangleq: "≜", triangleright: "▹", trianglerighteq: "⊵", tridot: "◬", trie: "≜", triminus: "⨺", triplus: "⨹", trisb: "⧍", tritime: "⨻", trpezium: "⏢", tscr: "𝓉", tscy: "ц", tshcy: "ћ", tstrok: "ŧ", twixt: "≬", twoheadleftarrow: "↞", twoheadrightarrow: "↠", uArr: "⇑", uHar: "⥣", uacut: "ú", uacute: "ú", uarr: "↑", ubrcy: "ў", ubreve: "ŭ", ucir: "û", ucirc: "û", ucy: "у", udarr: "⇅", udblac: "ű", udhar: "⥮", ufisht: "⥾", ufr: "𝔲", ugrav: "ù", ugrave: "ù", uharl: "↿", uharr: "↾", uhblk: "▀", ulcorn: "⌜", ulcorner: "⌜", ulcrop: "⌏", ultri: "◸", umacr: "ū", um: "¨", uml: "¨", uogon: "ų", uopf: "𝕦", uparrow: "↑", updownarrow: "↕", upharpoonleft: "↿", upharpoonright: "↾", uplus: "⊎", upsi: "υ", upsih: "ϒ", upsilon: "υ", upuparrows: "⇈", urcorn: "⌝", urcorner: "⌝", urcrop: "⌎", uring: "ů", urtri: "◹", uscr: "𝓊", utdot: "⋰", utilde: "ũ", utri: "▵", utrif: "▴", uuarr: "⇈", uum: "ü", uuml: "ü", uwangle: "⦧", vArr: "⇕", vBar: "⫨", vBarv: "⫩", vDash: "⊨", vangrt: "⦜", varepsilon: "ϵ", varkappa: "ϰ", varnothing: "∅", varphi: "ϕ", varpi: "ϖ", varpropto: "∝", varr: "↕", varrho: "ϱ", varsigma: "ς", varsubsetneq: "⊊︀", varsubsetneqq: "⫋︀", varsupsetneq: "⊋︀", varsupsetneqq: "⫌︀", vartheta: "ϑ", vartriangleleft: "⊲", vartriangleright: "⊳", vcy: "в", vdash: "⊢", vee: "∨", veebar: "⊻", veeeq: "≚", vellip: "⋮", verbar: "|", vert: "|", vfr: "𝔳", vltri: "⊲", vnsub: "⊂⃒", vnsup: "⊃⃒", vopf: "𝕧", vprop: "∝", vrtri: "⊳", vscr: "𝓋", vsubnE: "⫋︀", vsubne: "⊊︀", vsupnE: "⫌︀", vsupne: "⊋︀", vzigzag: "⦚", wcirc: "ŵ", wedbar: "⩟", wedge: "∧", wedgeq: "≙", weierp: "℘", wfr: "𝔴", wopf: "𝕨", wp: "℘", wr: "≀", wreath: "≀", wscr: "𝓌", xcap: "⋂", xcirc: "◯", xcup: "⋃", xdtri: "▽", xfr: "𝔵", xhArr: "⟺", xharr: "⟷", xi: "ξ", xlArr: "⟸", xlarr: "⟵", xmap: "⟼", xnis: "⋻", xodot: "⨀", xopf: "𝕩", xoplus: "⨁", xotime: "⨂", xrArr: "⟹", xrarr: "⟶", xscr: "𝓍", xsqcup: "⨆", xuplus: "⨄", xutri: "△", xvee: "⋁", xwedge: "⋀", yacut: "ý", yacute: "ý", yacy: "я", ycirc: "ŷ", ycy: "ы", ye: "¥", yen: "¥", yfr: "𝔶", yicy: "ї", yopf: "𝕪", yscr: "𝓎", yucy: "ю", yum: "ÿ", yuml: "ÿ", zacute: "ź", zcaron: "ž", zcy: "з", zdot: "ż", zeetrf: "ℨ", zeta: "ζ", zfr: "𝔷", zhcy: "ж", zigrarr: "⇝", zopf: "𝕫", zscr: "𝓏", zwj: "‍", zwnj: "‌" };
});
var Zi = C((Gv, Qi) => {
  var Xi = Ji();
  Qi.exports = kf;
  var yf = {}.hasOwnProperty;
  function kf(e) {
    return yf.call(Xi, e) ? Xi[e] : false;
  }
});
var lr = C((Hv, Du2) => {
  var eu2 = Yi(), ru2 = Vi(), wf = Le2(), Af = Gi(), uu2 = Ki(), Bf = Zi();
  Du2.exports = Uf;
  var qf = {}.hasOwnProperty, Me2 = String.fromCharCode, Tf = Function.prototype, tu2 = { warning: null, reference: null, text: null, warningContext: null, referenceContext: null, textContext: null, position: {}, additional: null, attribute: false, nonTerminated: true }, Sf = 9, nu2 = 10, Pf = 12, _f = 32, iu2 = 38, Lf = 59, Of = 60, If = 61, Nf = 35, Rf = 88, zf = 120, Mf = 65533, Ue2 = "named", At2 = "hexadecimal", Bt2 = "decimal", qt2 = {};
  qt2[At2] = 16;
  qt2[Bt2] = 10;
  var Nr2 = {};
  Nr2[Ue2] = uu2;
  Nr2[Bt2] = wf;
  Nr2[At2] = Af;
  var au2 = 1, ou2 = 2, su2 = 3, cu2 = 4, lu2 = 5, wt2 = 6, fu2 = 7, xe2 = {};
  xe2[au2] = "Named character references must be terminated by a semicolon";
  xe2[ou2] = "Numeric character references must be terminated by a semicolon";
  xe2[su2] = "Named character references cannot be empty";
  xe2[cu2] = "Numeric character references cannot be empty";
  xe2[lu2] = "Named character references must be known";
  xe2[wt2] = "Numeric character references cannot be disallowed";
  xe2[fu2] = "Numeric character references cannot be outside the permissible Unicode range";
  function Uf(e, r) {
    var n = {}, t, a;
    r || (r = {});
    for (a in tu2)
      t = r[a], n[a] = t ?? tu2[a];
    return (n.position.indent || n.position.start) && (n.indent = n.position.indent || [], n.position = n.position.start), Yf(e, n);
  }
  function Yf(e, r) {
    var n = r.additional, t = r.nonTerminated, a = r.text, i = r.reference, u = r.warning, o = r.textContext, s = r.referenceContext, l = r.warningContext, c = r.position, f = r.indent || [], D = e.length, h = 0, p = -1, d = c.column || 1, m = c.line || 1, g = "", x2 = [], F, B2, b2, v2, y2, k2, E, w2, A2, q2, T2, N2, _, P2, S2, L2, we2, j2, I2;
    for (typeof n == "string" && (n = n.charCodeAt(0)), L2 = J2(), w2 = u ? K2 : Tf, h--, D++; ++h < D; )
      if (y2 === nu2 && (d = f[p] || 1), y2 = e.charCodeAt(h), y2 === iu2) {
        if (E = e.charCodeAt(h + 1), E === Sf || E === nu2 || E === Pf || E === _f || E === iu2 || E === Of || E !== E || n && E === n) {
          g += Me2(y2), d++;
          continue;
        }
        for (_ = h + 1, N2 = _, I2 = _, E === Nf ? (I2 = ++N2, E = e.charCodeAt(I2), E === Rf || E === zf ? (P2 = At2, I2 = ++N2) : P2 = Bt2) : P2 = Ue2, F = "", T2 = "", v2 = "", S2 = Nr2[P2], I2--; ++I2 < D && (E = e.charCodeAt(I2), !!S2(E)); )
          v2 += Me2(E), P2 === Ue2 && qf.call(eu2, v2) && (F = v2, T2 = eu2[v2]);
        b2 = e.charCodeAt(I2) === Lf, b2 && (I2++, B2 = P2 === Ue2 ? Bf(v2) : false, B2 && (F = v2, T2 = B2)), j2 = 1 + I2 - _, !b2 && !t || (v2 ? P2 === Ue2 ? (b2 && !T2 ? w2(lu2, 1) : (F !== v2 && (I2 = N2 + F.length, j2 = 1 + I2 - N2, b2 = false), b2 || (A2 = F ? au2 : su2, r.attribute ? (E = e.charCodeAt(I2), E === If ? (w2(A2, j2), T2 = null) : uu2(E) ? T2 = null : w2(A2, j2)) : w2(A2, j2))), k2 = T2) : (b2 || w2(ou2, j2), k2 = parseInt(v2, qt2[P2]), Vf(k2) ? (w2(fu2, j2), k2 = Me2(Mf)) : k2 in ru2 ? (w2(wt2, j2), k2 = ru2[k2]) : (q2 = "", jf(k2) && w2(wt2, j2), k2 > 65535 && (k2 -= 65536, q2 += Me2(k2 >>> 10 | 55296), k2 = 56320 | k2 & 1023), k2 = q2 + Me2(k2))) : P2 !== Ue2 && w2(cu2, j2)), k2 ? (me2(), L2 = J2(), h = I2 - 1, d += I2 - _ + 1, x2.push(k2), we2 = J2(), we2.offset++, i && i.call(s, k2, { start: L2, end: we2 }, e.slice(_ - 1, I2)), L2 = we2) : (v2 = e.slice(_ - 1, I2), g += v2, d += v2.length, h = I2 - 1);
      } else
        y2 === 10 && (m++, p++, d = 0), y2 === y2 ? (g += Me2(y2), d++) : me2();
    return x2.join("");
    function J2() {
      return { line: m, column: d, offset: h + (c.offset || 0) };
    }
    function K2(ge, M2) {
      var ct2 = J2();
      ct2.column += M2, ct2.offset += M2, u.call(l, xe2[ge], ct2, ge);
    }
    function me2() {
      g && (x2.push(g), a && a.call(o, g, { start: L2, end: J2() }), g = "");
    }
  }
  function Vf(e) {
    return e >= 55296 && e <= 57343 || e > 1114111;
  }
  function jf(e) {
    return e >= 1 && e <= 8 || e === 11 || e >= 13 && e <= 31 || e >= 127 && e <= 159 || e >= 64976 && e <= 65007 || (e & 65535) === 65535 || (e & 65535) === 65534;
  }
});
var hu = C((Wv, du2) => {
  var $f = _e(), pu2 = lr();
  du2.exports = Gf;
  function Gf(e) {
    return n.raw = t, n;
    function r(i) {
      for (var u = e.offset, o = i.line, s = []; ++o && o in u; )
        s.push((u[o] || 0) + 1);
      return { start: i, indent: s };
    }
    function n(i, u, o) {
      pu2(i, { position: r(u), warning: a, text: o, reference: o, textContext: e, referenceContext: e });
    }
    function t(i, u, o) {
      return pu2(i, $f(o, { position: r(u), warning: a }));
    }
    function a(i, u, o) {
      o !== 3 && e.file.message(i, u);
    }
  }
});
var vu = C((Kv, gu) => {
  gu.exports = Hf;
  function Hf(e) {
    return r;
    function r(n, t) {
      var a = this, i = a.offset, u = [], o = a[e + "Methods"], s = a[e + "Tokenizers"], l = t.line, c = t.column, f, D, h, p, d, m;
      if (!n)
        return u;
      for (k2.now = F, k2.file = a.file, g(""); n; ) {
        for (f = -1, D = o.length, d = false; ++f < D && (p = o[f], h = s[p], !(h && (!h.onlyAtStart || a.atStart) && (!h.notInList || !a.inList) && (!h.notInBlock || !a.inBlock) && (!h.notInLink || !a.inLink) && (m = n.length, h.apply(a, [k2, n]), d = m !== n.length, d))); )
          ;
        d || a.file.fail(new Error("Infinite loop"), k2.now());
      }
      return a.eof = F(), u;
      function g(E) {
        for (var w2 = -1, A2 = E.indexOf(`
`); A2 !== -1; )
          l++, w2 = A2, A2 = E.indexOf(`
`, A2 + 1);
        w2 === -1 ? c += E.length : c = E.length - w2, l in i && (w2 !== -1 ? c += i[l] : c <= i[l] && (c = i[l] + 1));
      }
      function x2() {
        var E = [], w2 = l + 1;
        return function() {
          for (var A2 = l + 1; w2 < A2; )
            E.push((i[w2] || 0) + 1), w2++;
          return E;
        };
      }
      function F() {
        var E = { line: l, column: c };
        return E.offset = a.toOffset(E), E;
      }
      function B2(E) {
        this.start = E, this.end = F();
      }
      function b2(E) {
        n.slice(0, E.length) !== E && a.file.fail(new Error("Incorrectly eaten value: please report this warning on https://git.io/vg5Ft"), F());
      }
      function v2() {
        var E = F();
        return w2;
        function w2(A2, q2) {
          var T2 = A2.position, N2 = T2 ? T2.start : E, _ = [], P2 = T2 && T2.end.line, S2 = E.line;
          if (A2.position = new B2(N2), T2 && q2 && T2.indent) {
            if (_ = T2.indent, P2 < S2) {
              for (; ++P2 < S2; )
                _.push((i[P2] || 0) + 1);
              _.push(E.column);
            }
            q2 = _.concat(q2);
          }
          return A2.position.indent = q2 || [], A2;
        }
      }
      function y2(E, w2) {
        var A2 = w2 ? w2.children : u, q2 = A2[A2.length - 1], T2;
        return q2 && E.type === q2.type && (E.type === "text" || E.type === "blockquote") && mu2(q2) && mu2(E) && (T2 = E.type === "text" ? Wf : Kf, E = T2.call(a, q2, E)), E !== q2 && A2.push(E), a.atStart && u.length !== 0 && a.exitStart(), E;
      }
      function k2(E) {
        var w2 = x2(), A2 = v2(), q2 = F();
        return b2(E), T2.reset = N2, N2.test = _, T2.test = _, n = n.slice(E.length), g(E), w2 = w2(), T2;
        function T2(P2, S2) {
          return A2(y2(A2(P2), S2), w2);
        }
        function N2() {
          var P2 = T2.apply(null, arguments);
          return l = q2.line, c = q2.column, n = E + n, P2;
        }
        function _() {
          var P2 = A2({});
          return l = q2.line, c = q2.column, n = E + n, P2.position;
        }
      }
    }
  }
  function mu2(e) {
    var r, n;
    return e.type !== "text" || !e.position ? true : (r = e.position.start, n = e.position.end, r.line !== n.line || n.column - r.column === e.value.length);
  }
  function Wf(e, r) {
    return e.value += r.value, e;
  }
  function Kf(e, r) {
    return this.options.commonmark || this.options.gfm ? r : (e.children = e.children.concat(r.children), e);
  }
});
var Cu = C((Jv, Eu) => {
  Eu.exports = Rr;
  var Tt2 = ["\\", "`", "*", "{", "}", "[", "]", "(", ")", "#", "+", "-", ".", "!", "_", ">"], St2 = Tt2.concat(["~", "|"]), Fu2 = St2.concat([`
`, '"', "$", "%", "&", "'", ",", "/", ":", ";", "<", "=", "?", "@", "^"]);
  Rr.default = Tt2;
  Rr.gfm = St2;
  Rr.commonmark = Fu2;
  function Rr(e) {
    var r = e || {};
    return r.commonmark ? Fu2 : r.gfm ? St2 : Tt2;
  }
});
var xu = C((Xv, bu2) => {
  bu2.exports = ["address", "article", "aside", "base", "basefont", "blockquote", "body", "caption", "center", "col", "colgroup", "dd", "details", "dialog", "dir", "div", "dl", "dt", "fieldset", "figcaption", "figure", "footer", "form", "frame", "frameset", "h1", "h2", "h3", "h4", "h5", "h6", "head", "header", "hgroup", "hr", "html", "iframe", "legend", "li", "link", "main", "menu", "menuitem", "meta", "nav", "noframes", "ol", "optgroup", "option", "p", "param", "pre", "section", "source", "title", "summary", "table", "tbody", "td", "tfoot", "th", "thead", "title", "tr", "track", "ul"];
});
var Pt = C((Qv, yu) => {
  yu.exports = { position: true, gfm: true, commonmark: false, pedantic: false, blocks: xu() };
});
var wu = C((Zv, ku2) => {
  var Jf = _e(), Xf = Cu(), Qf = Pt();
  ku2.exports = Zf;
  function Zf(e) {
    var r = this, n = r.options, t, a;
    if (e == null)
      e = {};
    else if (typeof e == "object")
      e = Jf(e);
    else
      throw new Error("Invalid value `" + e + "` for setting `options`");
    for (t in Qf) {
      if (a = e[t], a == null && (a = n[t]), t !== "blocks" && typeof a != "boolean" || t === "blocks" && typeof a != "object")
        throw new Error("Invalid value `" + a + "` for setting `options." + t + "`");
      e[t] = a;
    }
    return r.options = e, r.escape = Xf(e), r;
  }
});
var qu = C((eF, Bu2) => {
  Bu2.exports = Au2;
  function Au2(e) {
    if (e == null)
      return nD;
    if (typeof e == "string")
      return tD(e);
    if (typeof e == "object")
      return "length" in e ? rD(e) : eD(e);
    if (typeof e == "function")
      return e;
    throw new Error("Expected function, string, or object as test");
  }
  function eD(e) {
    return r;
    function r(n) {
      var t;
      for (t in e)
        if (n[t] !== e[t])
          return false;
      return true;
    }
  }
  function rD(e) {
    for (var r = [], n = -1; ++n < e.length; )
      r[n] = Au2(e[n]);
    return t;
    function t() {
      for (var a = -1; ++a < r.length; )
        if (r[a].apply(this, arguments))
          return true;
      return false;
    }
  }
  function tD(e) {
    return r;
    function r(n) {
      return !!(n && n.type === e);
    }
  }
  function nD() {
    return true;
  }
});
var Su = C((rF, Tu2) => {
  Tu2.exports = iD;
  function iD(e) {
    return e;
  }
});
var Ou = C((tF, Lu2) => {
  Lu2.exports = zr2;
  var uD = qu(), aD = Su(), Pu2 = true, _u2 = "skip", _t2 = false;
  zr2.CONTINUE = Pu2;
  zr2.SKIP = _u2;
  zr2.EXIT = _t2;
  function zr2(e, r, n, t) {
    var a, i;
    typeof r == "function" && typeof n != "function" && (t = n, n = r, r = null), i = uD(r), a = t ? -1 : 1, u(e, null, [])();
    function u(o, s, l) {
      var c = typeof o == "object" && o !== null ? o : {}, f;
      return typeof c.type == "string" && (f = typeof c.tagName == "string" ? c.tagName : typeof c.name == "string" ? c.name : void 0, D.displayName = "node (" + aD(c.type + (f ? "<" + f + ">" : "")) + ")"), D;
      function D() {
        var h = l.concat(o), p = [], d, m;
        if ((!r || i(o, s, l[l.length - 1] || null)) && (p = oD(n(o, l)), p[0] === _t2))
          return p;
        if (o.children && p[0] !== _u2)
          for (m = (t ? o.children.length : -1) + a; m > -1 && m < o.children.length; ) {
            if (d = u(o.children[m], m, h)(), d[0] === _t2)
              return d;
            m = typeof d[1] == "number" ? d[1] : m + a;
          }
        return p;
      }
    }
  }
  function oD(e) {
    return e !== null && typeof e == "object" && "length" in e ? e : typeof e == "number" ? [Pu2, e] : [e];
  }
});
var Nu = C((nF, Iu2) => {
  Iu2.exports = Ur2;
  var Mr2 = Ou(), sD = Mr2.CONTINUE, cD = Mr2.SKIP, lD = Mr2.EXIT;
  Ur2.CONTINUE = sD;
  Ur2.SKIP = cD;
  Ur2.EXIT = lD;
  function Ur2(e, r, n, t) {
    typeof r == "function" && typeof n != "function" && (t = n, n = r, r = null), Mr2(e, r, a, t);
    function a(i, u) {
      var o = u[u.length - 1], s = o ? o.children.indexOf(i) : null;
      return n(i, s, o);
    }
  }
});
var zu = C((iF, Ru2) => {
  var fD = Nu();
  Ru2.exports = DD;
  function DD(e, r) {
    return fD(e, r ? pD : dD), e;
  }
  function pD(e) {
    delete e.position;
  }
  function dD(e) {
    e.position = void 0;
  }
});
var Yu = C((uF, Uu2) => {
  var Mu2 = _e(), hD = zu();
  Uu2.exports = vD;
  var mD = `
`, gD = /\r\n|\r/g;
  function vD() {
    var e = this, r = String(e.file), n = { line: 1, column: 1, offset: 0 }, t = Mu2(n), a;
    return r = r.replace(gD, mD), r.charCodeAt(0) === 65279 && (r = r.slice(1), t.column++, t.offset++), a = { type: "root", children: e.tokenizeBlock(r, t), position: { start: n, end: e.eof || Mu2(n) } }, e.options.position || hD(a, true), a;
  }
});
var ju = C((aF, Vu2) => {
  var FD = /^[ \t]*(\n|$)/;
  Vu2.exports = ED;
  function ED(e, r, n) {
    for (var t, a = "", i = 0, u = r.length; i < u && (t = FD.exec(r.slice(i)), t != null); )
      i += t[0].length, a += t[0];
    if (a !== "") {
      if (n)
        return true;
      e(a);
    }
  }
});
var Yr = C((oF, $u2) => {
  var pe2 = "", Lt2;
  $u2.exports = CD;
  function CD(e, r) {
    if (typeof e != "string")
      throw new TypeError("expected a string");
    if (r === 1)
      return e;
    if (r === 2)
      return e + e;
    var n = e.length * r;
    if (Lt2 !== e || typeof Lt2 > "u")
      Lt2 = e, pe2 = "";
    else if (pe2.length >= n)
      return pe2.substr(0, n);
    for (; n > pe2.length && r > 1; )
      r & 1 && (pe2 += e), r >>= 1, e += e;
    return pe2 += e, pe2 = pe2.substr(0, n), pe2;
  }
});
var Ot = C((sF, Gu) => {
  Gu.exports = bD;
  function bD(e) {
    return String(e).replace(/\n+$/, "");
  }
});
var Ku = C((cF, Wu2) => {
  var xD = Yr(), yD = Ot();
  Wu2.exports = AD;
  var It2 = `
`, Hu2 = "	", Nt2 = " ", kD = 4, wD = xD(Nt2, kD);
  function AD(e, r, n) {
    for (var t = -1, a = r.length, i = "", u = "", o = "", s = "", l, c, f; ++t < a; )
      if (l = r.charAt(t), f)
        if (f = false, i += o, u += s, o = "", s = "", l === It2)
          o = l, s = l;
        else
          for (i += l, u += l; ++t < a; ) {
            if (l = r.charAt(t), !l || l === It2) {
              s = l, o = l;
              break;
            }
            i += l, u += l;
          }
      else if (l === Nt2 && r.charAt(t + 1) === l && r.charAt(t + 2) === l && r.charAt(t + 3) === l)
        o += wD, t += 3, f = true;
      else if (l === Hu2)
        o += l, f = true;
      else {
        for (c = ""; l === Hu2 || l === Nt2; )
          c += l, l = r.charAt(++t);
        if (l !== It2)
          break;
        o += c + l, s += l;
      }
    if (u)
      return n ? true : e(i)({ type: "code", lang: null, meta: null, value: yD(u) });
  }
});
var Qu = C((lF, Xu2) => {
  Xu2.exports = SD;
  var Vr2 = `
`, fr2 = "	", Ye2 = " ", BD = "~", Ju2 = "`", qD = 3, TD = 4;
  function SD(e, r, n) {
    var t = this, a = t.options.gfm, i = r.length + 1, u = 0, o = "", s, l, c, f, D, h, p, d, m, g, x2, F, B2;
    if (a) {
      for (; u < i && (c = r.charAt(u), !(c !== Ye2 && c !== fr2)); )
        o += c, u++;
      if (F = u, c = r.charAt(u), !(c !== BD && c !== Ju2)) {
        for (u++, l = c, s = 1, o += c; u < i && (c = r.charAt(u), c === l); )
          o += c, s++, u++;
        if (!(s < qD)) {
          for (; u < i && (c = r.charAt(u), !(c !== Ye2 && c !== fr2)); )
            o += c, u++;
          for (f = "", p = ""; u < i && (c = r.charAt(u), !(c === Vr2 || l === Ju2 && c === l)); )
            c === Ye2 || c === fr2 ? p += c : (f += p + c, p = ""), u++;
          if (c = r.charAt(u), !(c && c !== Vr2)) {
            if (n)
              return true;
            B2 = e.now(), B2.column += o.length, B2.offset += o.length, o += f, f = t.decode.raw(t.unescape(f), B2), p && (o += p), p = "", g = "", x2 = "", d = "", m = "";
            for (var b2 = true; u < i; ) {
              if (c = r.charAt(u), d += g, m += x2, g = "", x2 = "", c !== Vr2) {
                d += c, x2 += c, u++;
                continue;
              }
              for (b2 ? (o += c, b2 = false) : (g += c, x2 += c), p = "", u++; u < i && (c = r.charAt(u), c === Ye2); )
                p += c, u++;
              if (g += p, x2 += p.slice(F), !(p.length >= TD)) {
                for (p = ""; u < i && (c = r.charAt(u), c === l); )
                  p += c, u++;
                if (g += p, x2 += p, !(p.length < s)) {
                  for (p = ""; u < i && (c = r.charAt(u), !(c !== Ye2 && c !== fr2)); )
                    g += c, x2 += c, u++;
                  if (!c || c === Vr2)
                    break;
                }
              }
            }
            for (o += d + g, u = -1, i = f.length; ++u < i; )
              if (c = f.charAt(u), c === Ye2 || c === fr2)
                D || (D = f.slice(0, u));
              else if (D) {
                h = f.slice(u);
                break;
              }
            return e(o)({ type: "code", lang: D || f || null, meta: h || null, value: m });
          }
        }
      }
    }
  }
});
var Oe = C((Ve2, Zu2) => {
  Ve2 = Zu2.exports = PD;
  function PD(e) {
    return e.trim ? e.trim() : Ve2.right(Ve2.left(e));
  }
  Ve2.left = function(e) {
    return e.trimLeft ? e.trimLeft() : e.replace(/^\s\s*/, "");
  };
  Ve2.right = function(e) {
    if (e.trimRight)
      return e.trimRight();
    for (var r = /\s/, n = e.length; r.test(e.charAt(--n)); )
      ;
    return e.slice(0, n + 1);
  };
});
var jr = C((fF, ea) => {
  ea.exports = _D;
  function _D(e, r, n, t) {
    for (var a = e.length, i = -1, u, o; ++i < a; )
      if (u = e[i], o = u[1] || {}, !(o.pedantic !== void 0 && o.pedantic !== n.options.pedantic) && !(o.commonmark !== void 0 && o.commonmark !== n.options.commonmark) && r[u[0]].apply(n, t))
        return true;
    return false;
  }
});
var ia = C((DF, na) => {
  var LD = Oe(), OD = jr();
  na.exports = ID;
  var Rt2 = `
`, ra = "	", zt2 = " ", ta = ">";
  function ID(e, r, n) {
    for (var t = this, a = t.offset, i = t.blockTokenizers, u = t.interruptBlockquote, o = e.now(), s = o.line, l = r.length, c = [], f = [], D = [], h, p = 0, d, m, g, x2, F, B2, b2, v2; p < l && (d = r.charAt(p), !(d !== zt2 && d !== ra)); )
      p++;
    if (r.charAt(p) === ta) {
      if (n)
        return true;
      for (p = 0; p < l; ) {
        for (g = r.indexOf(Rt2, p), B2 = p, b2 = false, g === -1 && (g = l); p < l && (d = r.charAt(p), !(d !== zt2 && d !== ra)); )
          p++;
        if (r.charAt(p) === ta ? (p++, b2 = true, r.charAt(p) === zt2 && p++) : p = B2, x2 = r.slice(p, g), !b2 && !LD(x2)) {
          p = B2;
          break;
        }
        if (!b2 && (m = r.slice(p), OD(u, i, t, [e, m, true])))
          break;
        F = B2 === p ? x2 : r.slice(B2, g), D.push(p - B2), c.push(F), f.push(x2), p = g + 1;
      }
      for (p = -1, l = D.length, h = e(c.join(Rt2)); ++p < l; )
        a[s] = (a[s] || 0) + D[p], s++;
      return v2 = t.enterBlock(), f = t.tokenizeBlock(f.join(Rt2), o), v2(), h({ type: "blockquote", children: f });
    }
  }
});
var oa = C((pF, aa) => {
  aa.exports = RD;
  var ua = `
`, Dr2 = "	", pr2 = " ", dr2 = "#", ND = 6;
  function RD(e, r, n) {
    for (var t = this, a = t.options.pedantic, i = r.length + 1, u = -1, o = e.now(), s = "", l = "", c, f, D; ++u < i; ) {
      if (c = r.charAt(u), c !== pr2 && c !== Dr2) {
        u--;
        break;
      }
      s += c;
    }
    for (D = 0; ++u <= i; ) {
      if (c = r.charAt(u), c !== dr2) {
        u--;
        break;
      }
      s += c, D++;
    }
    if (!(D > ND) && !(!D || !a && r.charAt(u + 1) === dr2)) {
      for (i = r.length + 1, f = ""; ++u < i; ) {
        if (c = r.charAt(u), c !== pr2 && c !== Dr2) {
          u--;
          break;
        }
        f += c;
      }
      if (!(!a && f.length === 0 && c && c !== ua)) {
        if (n)
          return true;
        for (s += f, f = "", l = ""; ++u < i && (c = r.charAt(u), !(!c || c === ua)); ) {
          if (c !== pr2 && c !== Dr2 && c !== dr2) {
            l += f + c, f = "";
            continue;
          }
          for (; c === pr2 || c === Dr2; )
            f += c, c = r.charAt(++u);
          if (!a && l && !f && c === dr2) {
            l += c;
            continue;
          }
          for (; c === dr2; )
            f += c, c = r.charAt(++u);
          for (; c === pr2 || c === Dr2; )
            f += c, c = r.charAt(++u);
          u--;
        }
        return o.column += s.length, o.offset += s.length, s += l + f, e(s)({ type: "heading", depth: D, children: t.tokenizeInline(l, o) });
      }
    }
  }
});
var la = C((dF, ca) => {
  ca.exports = $D;
  var zD = "	", MD = `
`, sa = " ", UD = "*", YD = "-", VD = "_", jD = 3;
  function $D(e, r, n) {
    for (var t = -1, a = r.length + 1, i = "", u, o, s, l; ++t < a && (u = r.charAt(t), !(u !== zD && u !== sa)); )
      i += u;
    if (!(u !== UD && u !== YD && u !== VD))
      for (o = u, i += u, s = 1, l = ""; ++t < a; )
        if (u = r.charAt(t), u === o)
          s++, i += l + o, l = "";
        else if (u === sa)
          l += u;
        else
          return s >= jD && (!u || u === MD) ? (i += l, n ? true : e(i)({ type: "thematicBreak" })) : void 0;
  }
});
var Mt = C((hF, Da) => {
  Da.exports = KD;
  var fa = "	", GD = " ", HD = 1, WD = 4;
  function KD(e) {
    for (var r = 0, n = 0, t = e.charAt(r), a = {}, i, u = 0; t === fa || t === GD; ) {
      for (i = t === fa ? WD : HD, n += i, i > 1 && (n = Math.floor(n / i) * i); u < n; )
        a[++u] = r;
      t = e.charAt(++r);
    }
    return { indent: n, stops: a };
  }
});
var ha = C((mF, da) => {
  var JD = Oe(), XD = Yr(), QD = Mt();
  da.exports = rp;
  var pa = `
`, ZD = " ", ep = "!";
  function rp(e, r) {
    var n = e.split(pa), t = n.length + 1, a = 1 / 0, i = [], u, o, s;
    for (n.unshift(XD(ZD, r) + ep); t--; )
      if (o = QD(n[t]), i[t] = o.stops, JD(n[t]).length !== 0)
        if (o.indent)
          o.indent > 0 && o.indent < a && (a = o.indent);
        else {
          a = 1 / 0;
          break;
        }
    if (a !== 1 / 0)
      for (t = n.length; t--; ) {
        for (s = i[t], u = a; u && !(u in s); )
          u--;
        n[t] = n[t].slice(s[u] + 1);
      }
    return n.shift(), n.join(pa);
  }
});
var Ca = C((gF, Ea) => {
  var tp = Oe(), np = Yr(), ma = Le2(), ip = Mt(), up = ha(), ap = jr();
  Ea.exports = dp;
  var Ut2 = "*", op = "_", ga = "+", Yt2 = "-", va = ".", de2 = " ", te2 = `
`, $r2 = "	", Fa = ")", sp = "x", ye = 4, cp = /\n\n(?!\s*$)/, lp = /^\[([ X\tx])][ \t]/, fp = /^([ \t]*)([*+-]|\d+[.)])( {1,4}(?! )| |\t|$|(?=\n))([^\n]*)/, Dp = /^([ \t]*)([*+-]|\d+[.)])([ \t]+)/, pp = /^( {1,4}|\t)?/gm;
  function dp(e, r, n) {
    for (var t = this, a = t.options.commonmark, i = t.options.pedantic, u = t.blockTokenizers, o = t.interruptList, s = 0, l = r.length, c = null, f, D, h, p, d, m, g, x2, F, B2, b2, v2, y2, k2, E, w2, A2, q2, T2, N2 = false, _, P2, S2, L2; s < l && (p = r.charAt(s), !(p !== $r2 && p !== de2)); )
      s++;
    if (p = r.charAt(s), p === Ut2 || p === ga || p === Yt2)
      d = p, h = false;
    else {
      for (h = true, D = ""; s < l && (p = r.charAt(s), !!ma(p)); )
        D += p, s++;
      if (p = r.charAt(s), !D || !(p === va || a && p === Fa) || n && D !== "1")
        return;
      c = parseInt(D, 10), d = p;
    }
    if (p = r.charAt(++s), !(p !== de2 && p !== $r2 && (i || p !== te2 && p !== ""))) {
      if (n)
        return true;
      for (s = 0, k2 = [], E = [], w2 = []; s < l; ) {
        for (m = r.indexOf(te2, s), g = s, x2 = false, L2 = false, m === -1 && (m = l), f = 0; s < l; ) {
          if (p = r.charAt(s), p === $r2)
            f += ye - f % ye;
          else if (p === de2)
            f++;
          else
            break;
          s++;
        }
        if (A2 && f >= A2.indent && (L2 = true), p = r.charAt(s), F = null, !L2) {
          if (p === Ut2 || p === ga || p === Yt2)
            F = p, s++, f++;
          else {
            for (D = ""; s < l && (p = r.charAt(s), !!ma(p)); )
              D += p, s++;
            p = r.charAt(s), s++, D && (p === va || a && p === Fa) && (F = p, f += D.length + 1);
          }
          if (F)
            if (p = r.charAt(s), p === $r2)
              f += ye - f % ye, s++;
            else if (p === de2) {
              for (S2 = s + ye; s < S2 && r.charAt(s) === de2; )
                s++, f++;
              s === S2 && r.charAt(s) === de2 && (s -= ye - 1, f -= ye - 1);
            } else
              p !== te2 && p !== "" && (F = null);
        }
        if (F) {
          if (!i && d !== F)
            break;
          x2 = true;
        } else
          !a && !L2 && r.charAt(g) === de2 ? L2 = true : a && A2 && (L2 = f >= A2.indent || f > ye), x2 = false, s = g;
        if (b2 = r.slice(g, m), B2 = g === s ? b2 : r.slice(s, m), (F === Ut2 || F === op || F === Yt2) && u.thematicBreak.call(t, e, b2, true))
          break;
        if (v2 = y2, y2 = !x2 && !tp(B2).length, L2 && A2)
          A2.value = A2.value.concat(w2, b2), E = E.concat(w2, b2), w2 = [];
        else if (x2)
          w2.length !== 0 && (N2 = true, A2.value.push(""), A2.trail = w2.concat()), A2 = { value: [b2], indent: f, trail: [] }, k2.push(A2), E = E.concat(w2, b2), w2 = [];
        else if (y2) {
          if (v2 && !a)
            break;
          w2.push(b2);
        } else {
          if (v2 || ap(o, u, t, [e, b2, true]))
            break;
          A2.value = A2.value.concat(w2, b2), E = E.concat(w2, b2), w2 = [];
        }
        s = m + 1;
      }
      for (_ = e(E.join(te2)).reset({ type: "list", ordered: h, start: c, spread: N2, children: [] }), q2 = t.enterList(), T2 = t.enterBlock(), s = -1, l = k2.length; ++s < l; )
        A2 = k2[s].value.join(te2), P2 = e.now(), e(A2)(hp(t, A2, P2), _), A2 = k2[s].trail.join(te2), s !== l - 1 && (A2 += te2), e(A2);
      return q2(), T2(), _;
    }
  }
  function hp(e, r, n) {
    var t = e.offset, a = e.options.pedantic ? mp : gp, i = null, u, o;
    return r = a.apply(null, arguments), e.options.gfm && (u = r.match(lp), u && (o = u[0].length, i = u[1].toLowerCase() === sp, t[n.line] += o, r = r.slice(o))), { type: "listItem", spread: cp.test(r), checked: i, children: e.tokenizeBlock(r, n) };
  }
  function mp(e, r, n) {
    var t = e.offset, a = n.line;
    return r = r.replace(Dp, i), a = n.line, r.replace(pp, i);
    function i(u) {
      return t[a] = (t[a] || 0) + u.length, a++, "";
    }
  }
  function gp(e, r, n) {
    var t = e.offset, a = n.line, i, u, o, s, l, c, f;
    for (r = r.replace(fp, D), s = r.split(te2), l = up(r, ip(i).indent).split(te2), l[0] = o, t[a] = (t[a] || 0) + u.length, a++, c = 0, f = s.length; ++c < f; )
      t[a] = (t[a] || 0) + s[c].length - l[c].length, a++;
    return l.join(te2);
    function D(h, p, d, m, g) {
      return u = p + d + m, o = g, Number(d) < 10 && u.length % 2 === 1 && (d = de2 + d), i = p + np(de2, d.length) + m, i + o;
    }
  }
});
var ka = C((vF, ya) => {
  ya.exports = xp;
  var Vt2 = `
`, vp = "	", ba = " ", xa = "=", Fp = "-", Ep = 3, Cp = 1, bp = 2;
  function xp(e, r, n) {
    for (var t = this, a = e.now(), i = r.length, u = -1, o = "", s, l, c, f, D; ++u < i; ) {
      if (c = r.charAt(u), c !== ba || u >= Ep) {
        u--;
        break;
      }
      o += c;
    }
    for (s = "", l = ""; ++u < i; ) {
      if (c = r.charAt(u), c === Vt2) {
        u--;
        break;
      }
      c === ba || c === vp ? l += c : (s += l + c, l = "");
    }
    if (a.column += o.length, a.offset += o.length, o += s + l, c = r.charAt(++u), f = r.charAt(++u), !(c !== Vt2 || f !== xa && f !== Fp)) {
      for (o += c, l = f, D = f === xa ? Cp : bp; ++u < i; ) {
        if (c = r.charAt(u), c !== f) {
          if (c !== Vt2)
            return;
          u--;
          break;
        }
        l += c;
      }
      return n ? true : e(o + l)({ type: "heading", depth: D, children: t.tokenizeInline(s, a) });
    }
  }
});
var $t = C((jt) => {
  var yp = "[a-zA-Z_:][a-zA-Z0-9:._-]*", kp = "[^\"'=<>`\\u0000-\\u0020]+", wp = "'[^']*'", Ap = '"[^"]*"', Bp = "(?:" + kp + "|" + wp + "|" + Ap + ")", qp = "(?:\\s+" + yp + "(?:\\s*=\\s*" + Bp + ")?)", wa = "<[A-Za-z][A-Za-z0-9\\-]*" + qp + "*\\s*\\/?>", Aa = "<\\/[A-Za-z][A-Za-z0-9\\-]*\\s*>", Tp = "<!---->|<!--(?:-?[^>-])(?:-?[^-])*-->", Sp = "<[?].*?[?]>", Pp = "<![A-Za-z]+\\s+[^>]*>", _p = "<!\\[CDATA\\[[\\s\\S]*?\\]\\]>";
  jt.openCloseTag = new RegExp("^(?:" + wa + "|" + Aa + ")");
  jt.tag = new RegExp("^(?:" + wa + "|" + Aa + "|" + Tp + "|" + Sp + "|" + Pp + "|" + _p + ")");
});
var Sa = C((EF, Ta) => {
  var Lp = $t().openCloseTag;
  Ta.exports = Kp;
  var Op = "	", Ip = " ", Ba = `
`, Np = "<", Rp = /^<(script|pre|style)(?=(\s|>|$))/i, zp = /<\/(script|pre|style)>/i, Mp = /^<!--/, Up = /-->/, Yp = /^<\?/, Vp = /\?>/, jp = /^<![A-Za-z]/, $p = />/, Gp = /^<!\[CDATA\[/, Hp = /]]>/, qa = /^$/, Wp = new RegExp(Lp.source + "\\s*$");
  function Kp(e, r, n) {
    for (var t = this, a = t.options.blocks.join("|"), i = new RegExp("^</?(" + a + ")(?=(\\s|/?>|$))", "i"), u = r.length, o = 0, s, l, c, f, D, h, p, d = [[Rp, zp, true], [Mp, Up, true], [Yp, Vp, true], [jp, $p, true], [Gp, Hp, true], [i, qa, true], [Wp, qa, false]]; o < u && (f = r.charAt(o), !(f !== Op && f !== Ip)); )
      o++;
    if (r.charAt(o) === Np) {
      for (s = r.indexOf(Ba, o + 1), s = s === -1 ? u : s, l = r.slice(o, s), c = -1, D = d.length; ++c < D; )
        if (d[c][0].test(l)) {
          h = d[c];
          break;
        }
      if (h) {
        if (n)
          return h[2];
        if (o = s, !h[1].test(l))
          for (; o < u; ) {
            if (s = r.indexOf(Ba, o + 1), s = s === -1 ? u : s, l = r.slice(o + 1, s), h[1].test(l)) {
              l && (o = s);
              break;
            }
            o = s;
          }
        return p = r.slice(0, o), e(p)({ type: "html", value: p });
      }
    }
  }
});
var ne = C((CF, Pa) => {
  Pa.exports = Qp;
  var Jp = String.fromCharCode, Xp = /\s/;
  function Qp(e) {
    return Xp.test(typeof e == "number" ? Jp(e) : e.charAt(0));
  }
});
var Gt = C((bF, _a) => {
  var Zp = kr();
  _a.exports = ed;
  function ed(e) {
    return Zp(e).toLowerCase();
  }
});
var Ma = C((xF, za) => {
  var rd = ne(), td = Gt();
  za.exports = ad;
  var La = '"', Oa = "'", nd = "\\", je2 = `
`, Gr2 = "	", Hr2 = " ", Wt2 = "[", hr2 = "]", id = "(", ud = ")", Ia = ":", Na = "<", Ra = ">";
  function ad(e, r, n) {
    for (var t = this, a = t.options.commonmark, i = 0, u = r.length, o = "", s, l, c, f, D, h, p, d; i < u && (f = r.charAt(i), !(f !== Hr2 && f !== Gr2)); )
      o += f, i++;
    if (f = r.charAt(i), f === Wt2) {
      for (i++, o += f, c = ""; i < u && (f = r.charAt(i), f !== hr2); )
        f === nd && (c += f, i++, f = r.charAt(i)), c += f, i++;
      if (!(!c || r.charAt(i) !== hr2 || r.charAt(i + 1) !== Ia)) {
        for (h = c, o += c + hr2 + Ia, i = o.length, c = ""; i < u && (f = r.charAt(i), !(f !== Gr2 && f !== Hr2 && f !== je2)); )
          o += f, i++;
        if (f = r.charAt(i), c = "", s = o, f === Na) {
          for (i++; i < u && (f = r.charAt(i), !!Ht2(f)); )
            c += f, i++;
          if (f = r.charAt(i), f === Ht2.delimiter)
            o += Na + c + f, i++;
          else {
            if (a)
              return;
            i -= c.length + 1, c = "";
          }
        }
        if (!c) {
          for (; i < u && (f = r.charAt(i), !!od(f)); )
            c += f, i++;
          o += c;
        }
        if (c) {
          for (p = c, c = ""; i < u && (f = r.charAt(i), !(f !== Gr2 && f !== Hr2 && f !== je2)); )
            c += f, i++;
          if (f = r.charAt(i), D = null, f === La ? D = La : f === Oa ? D = Oa : f === id && (D = ud), !D)
            c = "", i = o.length;
          else if (c) {
            for (o += c + f, i = o.length, c = ""; i < u && (f = r.charAt(i), f !== D); ) {
              if (f === je2) {
                if (i++, f = r.charAt(i), f === je2 || f === D)
                  return;
                c += je2;
              }
              c += f, i++;
            }
            if (f = r.charAt(i), f !== D)
              return;
            l = o, o += c + f, i++, d = c, c = "";
          } else
            return;
          for (; i < u && (f = r.charAt(i), !(f !== Gr2 && f !== Hr2)); )
            o += f, i++;
          if (f = r.charAt(i), !f || f === je2)
            return n ? true : (s = e(s).test().end, p = t.decode.raw(t.unescape(p), s, { nonTerminated: false }), d && (l = e(l).test().end, d = t.decode.raw(t.unescape(d), l)), e(o)({ type: "definition", identifier: td(h), label: h, title: d || null, url: p }));
        }
      }
    }
  }
  function Ht2(e) {
    return e !== Ra && e !== Wt2 && e !== hr2;
  }
  Ht2.delimiter = Ra;
  function od(e) {
    return e !== Wt2 && e !== hr2 && !rd(e);
  }
});
var Va = C((yF, Ya) => {
  var sd = ne();
  Ya.exports = vd;
  var cd = "	", Wr2 = `
`, ld = " ", fd = "-", Dd = ":", pd = "\\", Kt2 = "|", dd = 1, hd = 2, Ua = "left", md = "center", gd = "right";
  function vd(e, r, n) {
    var t = this, a, i, u, o, s, l, c, f, D, h, p, d, m, g, x2, F, B2, b2, v2, y2, k2, E;
    if (t.options.gfm) {
      for (a = 0, F = 0, l = r.length + 1, c = []; a < l; ) {
        if (y2 = r.indexOf(Wr2, a), k2 = r.indexOf(Kt2, a + 1), y2 === -1 && (y2 = r.length), k2 === -1 || k2 > y2) {
          if (F < hd)
            return;
          break;
        }
        c.push(r.slice(a, y2)), F++, a = y2 + 1;
      }
      for (o = c.join(Wr2), i = c.splice(1, 1)[0] || [], a = 0, l = i.length, F--, u = false, p = []; a < l; ) {
        if (D = i.charAt(a), D === Kt2) {
          if (h = null, u === false) {
            if (E === false)
              return;
          } else
            p.push(u), u = false;
          E = false;
        } else if (D === fd)
          h = true, u = u || null;
        else if (D === Dd)
          u === Ua ? u = md : h && u === null ? u = gd : u = Ua;
        else if (!sd(D))
          return;
        a++;
      }
      if (u !== false && p.push(u), !(p.length < dd)) {
        if (n)
          return true;
        for (x2 = -1, b2 = [], v2 = e(o).reset({ type: "table", align: p, children: b2 }); ++x2 < F; ) {
          for (B2 = c[x2], s = { type: "tableRow", children: [] }, x2 && e(Wr2), e(B2).reset(s, v2), l = B2.length + 1, a = 0, f = "", d = "", m = true; a < l; ) {
            if (D = B2.charAt(a), D === cd || D === ld) {
              d ? f += D : e(D), a++;
              continue;
            }
            D === "" || D === Kt2 ? m ? e(D) : ((d || D) && !m && (o = d, f.length > 1 && (D ? (o += f.slice(0, -1), f = f.charAt(f.length - 1)) : (o += f, f = "")), g = e.now(), e(o)({ type: "tableCell", children: t.tokenizeInline(d, g) }, s)), e(f + D), f = "", d = "") : (f && (d += f, f = ""), d += D, D === pd && a !== l - 2 && (d += B2.charAt(a + 1), a++)), m = false, a++;
          }
          x2 || e(Wr2 + i);
        }
        return v2;
      }
    }
  }
});
var Ga = C((kF, $a) => {
  var Fd = Oe(), Ed = Ot(), Cd = jr();
  $a.exports = yd;
  var bd = "	", mr2 = `
`, xd = " ", ja = 4;
  function yd(e, r, n) {
    for (var t = this, a = t.options, i = a.commonmark, u = t.blockTokenizers, o = t.interruptParagraph, s = r.indexOf(mr2), l = r.length, c, f, D, h, p; s < l; ) {
      if (s === -1) {
        s = l;
        break;
      }
      if (r.charAt(s + 1) === mr2)
        break;
      if (i) {
        for (h = 0, c = s + 1; c < l; ) {
          if (D = r.charAt(c), D === bd) {
            h = ja;
            break;
          } else if (D === xd)
            h++;
          else
            break;
          c++;
        }
        if (h >= ja && D !== mr2) {
          s = r.indexOf(mr2, s + 1);
          continue;
        }
      }
      if (f = r.slice(s + 1), Cd(o, u, t, [e, f, true]))
        break;
      if (c = s, s = r.indexOf(mr2, s + 1), s !== -1 && Fd(r.slice(c, s)) === "") {
        s = c;
        break;
      }
    }
    return f = r.slice(0, s), n ? true : (p = e.now(), f = Ed(f), e(f)({ type: "paragraph", children: t.tokenizeInline(f, p) }));
  }
});
var Wa = C((wF, Ha) => {
  Ha.exports = kd;
  function kd(e, r) {
    return e.indexOf("\\", r);
  }
});
var Qa = C((AF, Xa) => {
  var wd = Wa();
  Xa.exports = Ja;
  Ja.locator = wd;
  var Ad = `
`, Ka = "\\";
  function Ja(e, r, n) {
    var t = this, a, i;
    if (r.charAt(0) === Ka && (a = r.charAt(1), t.escape.indexOf(a) !== -1))
      return n ? true : (a === Ad ? i = { type: "break" } : i = { type: "text", value: a }, e(Ka + a)(i));
  }
});
var Jt = C((BF, Za) => {
  Za.exports = Bd;
  function Bd(e, r) {
    return e.indexOf("<", r);
  }
});
var io = C((qF, no) => {
  var eo2 = ne(), qd = lr(), Td = Jt();
  no.exports = en2;
  en2.locator = Td;
  en2.notInLink = true;
  var ro = "<", Xt2 = ">", to2 = "@", Qt2 = "/", Zt2 = "mailto:", Kr2 = Zt2.length;
  function en2(e, r, n) {
    var t = this, a = "", i = r.length, u = 0, o = "", s = false, l = "", c, f, D, h, p;
    if (r.charAt(0) === ro) {
      for (u++, a = ro; u < i && (c = r.charAt(u), !(eo2(c) || c === Xt2 || c === to2 || c === ":" && r.charAt(u + 1) === Qt2)); )
        o += c, u++;
      if (o) {
        if (l += o, o = "", c = r.charAt(u), l += c, u++, c === to2)
          s = true;
        else {
          if (c !== ":" || r.charAt(u + 1) !== Qt2)
            return;
          l += Qt2, u++;
        }
        for (; u < i && (c = r.charAt(u), !(eo2(c) || c === Xt2)); )
          o += c, u++;
        if (c = r.charAt(u), !(!o || c !== Xt2))
          return n ? true : (l += o, D = l, a += l + c, f = e.now(), f.column++, f.offset++, s && (l.slice(0, Kr2).toLowerCase() === Zt2 ? (D = D.slice(Kr2), f.column += Kr2, f.offset += Kr2) : l = Zt2 + l), h = t.inlineTokenizers, t.inlineTokenizers = { text: h.text }, p = t.enterLink(), D = t.tokenizeInline(D, f), t.inlineTokenizers = h, p(), e(a)({ type: "link", title: null, url: qd(l, { nonTerminated: false }), children: D }));
      }
    }
  }
});
var ao = C((TF, uo) => {
  uo.exports = Sd;
  function Sd(e, r) {
    var n = String(e), t = 0, a;
    if (typeof r != "string")
      throw new Error("Expected character");
    for (a = n.indexOf(r); a !== -1; )
      t++, a = n.indexOf(r, a + r.length);
    return t;
  }
});
var co = C((SF, so2) => {
  so2.exports = Pd;
  var oo = ["www.", "http://", "https://"];
  function Pd(e, r) {
    var n = -1, t, a, i;
    if (!this.options.gfm)
      return n;
    for (a = oo.length, t = -1; ++t < a; )
      i = e.indexOf(oo[t], r), i !== -1 && (n === -1 || i < n) && (n = i);
    return n;
  }
});
var ho = C((PF, po2) => {
  var lo2 = ao(), _d = lr(), Ld = Le2(), rn2 = ze(), Od = ne(), Id = co();
  po2.exports = nn2;
  nn2.locator = Id;
  nn2.notInLink = true;
  var Nd = 33, Rd = 38, zd = 41, Md = 42, Ud = 44, Yd = 45, tn2 = 46, Vd = 58, jd = 59, $d = 63, Gd = 60, fo2 = 95, Hd = 126, Wd = "(", Do2 = ")";
  function nn2(e, r, n) {
    var t = this, a = t.options.gfm, i = t.inlineTokenizers, u = r.length, o = -1, s = false, l, c, f, D, h, p, d, m, g, x2, F, B2, b2, v2;
    if (a) {
      if (r.slice(0, 4) === "www.")
        s = true, D = 4;
      else if (r.slice(0, 7).toLowerCase() === "http://")
        D = 7;
      else if (r.slice(0, 8).toLowerCase() === "https://")
        D = 8;
      else
        return;
      for (o = D - 1, f = D, l = []; D < u; ) {
        if (d = r.charCodeAt(D), d === tn2) {
          if (o === D - 1)
            break;
          l.push(D), o = D, D++;
          continue;
        }
        if (Ld(d) || rn2(d) || d === Yd || d === fo2) {
          D++;
          continue;
        }
        break;
      }
      if (d === tn2 && (l.pop(), D--), l[0] !== void 0 && (c = l.length < 2 ? f : l[l.length - 2] + 1, r.slice(c, D).indexOf("_") === -1)) {
        if (n)
          return true;
        for (m = D, h = D; D < u && (d = r.charCodeAt(D), !(Od(d) || d === Gd)); )
          D++, d === Nd || d === Md || d === Ud || d === tn2 || d === Vd || d === $d || d === fo2 || d === Hd || (m = D);
        if (D = m, r.charCodeAt(D - 1) === zd)
          for (p = r.slice(h, D), g = lo2(p, Wd), x2 = lo2(p, Do2); x2 > g; )
            D = h + p.lastIndexOf(Do2), p = r.slice(h, D), x2--;
        if (r.charCodeAt(D - 1) === jd && (D--, rn2(r.charCodeAt(D - 1)))) {
          for (m = D - 2; rn2(r.charCodeAt(m)); )
            m--;
          r.charCodeAt(m) === Rd && (D = m);
        }
        return F = r.slice(0, D), b2 = _d(F, { nonTerminated: false }), s && (b2 = "http://" + b2), v2 = t.enterLink(), t.inlineTokenizers = { text: i.text }, B2 = t.tokenizeInline(F, e.now()), t.inlineTokenizers = i, v2(), e(F)({ type: "link", title: null, url: b2, children: B2 });
      }
    }
  }
});
var Fo = C((_F, vo2) => {
  var Kd = Le2(), Jd = ze(), Xd = 43, Qd = 45, Zd = 46, eh = 95;
  vo2.exports = go2;
  function go2(e, r) {
    var n = this, t, a;
    if (!this.options.gfm || (t = e.indexOf("@", r), t === -1))
      return -1;
    if (a = t, a === r || !mo2(e.charCodeAt(a - 1)))
      return go2.call(n, e, t + 1);
    for (; a > r && mo2(e.charCodeAt(a - 1)); )
      a--;
    return a;
  }
  function mo2(e) {
    return Kd(e) || Jd(e) || e === Xd || e === Qd || e === Zd || e === eh;
  }
});
var xo = C((LF, bo2) => {
  var rh = lr(), Eo2 = Le2(), Co2 = ze(), th = Fo();
  bo2.exports = on2;
  on2.locator = th;
  on2.notInLink = true;
  var nh = 43, un2 = 45, Jr2 = 46, ih = 64, an2 = 95;
  function on2(e, r, n) {
    var t = this, a = t.options.gfm, i = t.inlineTokenizers, u = 0, o = r.length, s = -1, l, c, f, D;
    if (a) {
      for (l = r.charCodeAt(u); Eo2(l) || Co2(l) || l === nh || l === un2 || l === Jr2 || l === an2; )
        l = r.charCodeAt(++u);
      if (u !== 0 && l === ih) {
        for (u++; u < o; ) {
          if (l = r.charCodeAt(u), Eo2(l) || Co2(l) || l === un2 || l === Jr2 || l === an2) {
            u++, s === -1 && l === Jr2 && (s = u);
            continue;
          }
          break;
        }
        if (!(s === -1 || s === u || l === un2 || l === an2))
          return l === Jr2 && u--, c = r.slice(0, u), n ? true : (D = t.enterLink(), t.inlineTokenizers = { text: i.text }, f = t.tokenizeInline(c, e.now()), t.inlineTokenizers = i, D(), e(c)({ type: "link", title: null, url: "mailto:" + rh(c, { nonTerminated: false }), children: f }));
      }
    }
  }
});
var wo = C((OF, ko2) => {
  var uh = ze(), ah = Jt(), oh = $t().tag;
  ko2.exports = yo2;
  yo2.locator = ah;
  var sh = "<", ch = "?", lh = "!", fh = "/", Dh = /^<a /i, ph = /^<\/a>/i;
  function yo2(e, r, n) {
    var t = this, a = r.length, i, u;
    if (!(r.charAt(0) !== sh || a < 3) && (i = r.charAt(1), !(!uh(i) && i !== ch && i !== lh && i !== fh) && (u = r.match(oh), !!u)))
      return n ? true : (u = u[0], !t.inLink && Dh.test(u) ? t.inLink = true : t.inLink && ph.test(u) && (t.inLink = false), e(u)({ type: "html", value: u }));
  }
});
var sn = C((IF, Ao2) => {
  Ao2.exports = dh;
  function dh(e, r) {
    var n = e.indexOf("[", r), t = e.indexOf("![", r);
    return t === -1 || n < t ? n : t;
  }
});
var Lo = C((NF, _o2) => {
  var gr2 = ne(), hh = sn();
  _o2.exports = Po2;
  Po2.locator = hh;
  var mh = `
`, gh = "!", Bo2 = '"', qo2 = "'", $e2 = "(", vr2 = ")", cn2 = "<", ln2 = ">", To = "[", Fr2 = "\\", vh = "]", So2 = "`";
  function Po2(e, r, n) {
    var t = this, a = "", i = 0, u = r.charAt(0), o = t.options.pedantic, s = t.options.commonmark, l = t.options.gfm, c, f, D, h, p, d, m, g, x2, F, B2, b2, v2, y2, k2, E, w2, A2;
    if (u === gh && (g = true, a = u, u = r.charAt(++i)), u === To && !(!g && t.inLink)) {
      for (a += u, y2 = "", i++, B2 = r.length, E = e.now(), v2 = 0, E.column += i, E.offset += i; i < B2; ) {
        if (u = r.charAt(i), d = u, u === So2) {
          for (f = 1; r.charAt(i + 1) === So2; )
            d += u, i++, f++;
          D ? f >= D && (D = 0) : D = f;
        } else if (u === Fr2)
          i++, d += r.charAt(i);
        else if ((!D || l) && u === To)
          v2++;
        else if ((!D || l) && u === vh)
          if (v2)
            v2--;
          else {
            if (r.charAt(i + 1) !== $e2)
              return;
            d += $e2, c = true, i++;
            break;
          }
        y2 += d, d = "", i++;
      }
      if (c) {
        for (x2 = y2, a += y2 + d, i++; i < B2 && (u = r.charAt(i), !!gr2(u)); )
          a += u, i++;
        if (u = r.charAt(i), y2 = "", h = a, u === cn2) {
          for (i++, h += cn2; i < B2 && (u = r.charAt(i), u !== ln2); ) {
            if (s && u === mh)
              return;
            y2 += u, i++;
          }
          if (r.charAt(i) !== ln2)
            return;
          a += cn2 + y2 + ln2, k2 = y2, i++;
        } else {
          for (u = null, d = ""; i < B2 && (u = r.charAt(i), !(d && (u === Bo2 || u === qo2 || s && u === $e2))); ) {
            if (gr2(u)) {
              if (!o)
                break;
              d += u;
            } else {
              if (u === $e2)
                v2++;
              else if (u === vr2) {
                if (v2 === 0)
                  break;
                v2--;
              }
              y2 += d, d = "", u === Fr2 && (y2 += Fr2, u = r.charAt(++i)), y2 += u;
            }
            i++;
          }
          a += y2, k2 = y2, i = a.length;
        }
        for (y2 = ""; i < B2 && (u = r.charAt(i), !!gr2(u)); )
          y2 += u, i++;
        if (u = r.charAt(i), a += y2, y2 && (u === Bo2 || u === qo2 || s && u === $e2))
          if (i++, a += u, y2 = "", F = u === $e2 ? vr2 : u, p = a, s) {
            for (; i < B2 && (u = r.charAt(i), u !== F); )
              u === Fr2 && (y2 += Fr2, u = r.charAt(++i)), i++, y2 += u;
            if (u = r.charAt(i), u !== F)
              return;
            for (b2 = y2, a += y2 + u, i++; i < B2 && (u = r.charAt(i), !!gr2(u)); )
              a += u, i++;
          } else
            for (d = ""; i < B2; ) {
              if (u = r.charAt(i), u === F)
                m && (y2 += F + d, d = ""), m = true;
              else if (!m)
                y2 += u;
              else if (u === vr2) {
                a += y2 + F + d, b2 = y2;
                break;
              } else
                gr2(u) ? d += u : (y2 += F + d + u, d = "", m = false);
              i++;
            }
        if (r.charAt(i) === vr2)
          return n ? true : (a += vr2, k2 = t.decode.raw(t.unescape(k2), e(h).test().end, { nonTerminated: false }), b2 && (p = e(p).test().end, b2 = t.decode.raw(t.unescape(b2), p)), A2 = { type: g ? "image" : "link", title: b2 || null, url: k2 }, g ? A2.alt = t.decode.raw(t.unescape(x2), E) || null : (w2 = t.enterLink(), A2.children = t.tokenizeInline(x2, E), w2()), e(a)(A2));
      }
    }
  }
});
var No = C((RF, Io2) => {
  var Fh = ne(), Eh = sn(), Ch = Gt();
  Io2.exports = Oo2;
  Oo2.locator = Eh;
  var fn2 = "link", bh = "image", xh = "shortcut", yh = "collapsed", Dn2 = "full", kh = "!", Xr2 = "[", Qr2 = "\\", Zr2 = "]";
  function Oo2(e, r, n) {
    var t = this, a = t.options.commonmark, i = r.charAt(0), u = 0, o = r.length, s = "", l = "", c = fn2, f = xh, D, h, p, d, m, g, x2, F;
    if (i === kh && (c = bh, l = i, i = r.charAt(++u)), i === Xr2) {
      for (u++, l += i, g = "", F = 0; u < o; ) {
        if (i = r.charAt(u), i === Xr2)
          x2 = true, F++;
        else if (i === Zr2) {
          if (!F)
            break;
          F--;
        }
        i === Qr2 && (g += Qr2, i = r.charAt(++u)), g += i, u++;
      }
      if (s = g, D = g, i = r.charAt(u), i === Zr2) {
        if (u++, s += i, g = "", !a)
          for (; u < o && (i = r.charAt(u), !!Fh(i)); )
            g += i, u++;
        if (i = r.charAt(u), i === Xr2) {
          for (h = "", g += i, u++; u < o && (i = r.charAt(u), !(i === Xr2 || i === Zr2)); )
            i === Qr2 && (h += Qr2, i = r.charAt(++u)), h += i, u++;
          i = r.charAt(u), i === Zr2 ? (f = h ? Dn2 : yh, g += h + i, u++) : h = "", s += g, g = "";
        } else {
          if (!D)
            return;
          h = D;
        }
        if (!(f !== Dn2 && x2))
          return s = l + s, c === fn2 && t.inLink ? null : n ? true : (p = e.now(), p.column += l.length, p.offset += l.length, h = f === Dn2 ? h : D, d = { type: c + "Reference", identifier: Ch(h), label: h, referenceType: f }, c === fn2 ? (m = t.enterLink(), d.children = t.tokenizeInline(D, p), m()) : d.alt = t.decode.raw(t.unescape(D), p) || null, e(s)(d));
      }
    }
  }
});
var zo = C((zF, Ro2) => {
  Ro2.exports = wh;
  function wh(e, r) {
    var n = e.indexOf("**", r), t = e.indexOf("__", r);
    return t === -1 ? n : n === -1 || t < n ? t : n;
  }
});
var Vo = C((MF, Yo2) => {
  var Ah = Oe(), Mo2 = ne(), Bh = zo();
  Yo2.exports = Uo2;
  Uo2.locator = Bh;
  var qh = "\\", Th = "*", Sh = "_";
  function Uo2(e, r, n) {
    var t = this, a = 0, i = r.charAt(a), u, o, s, l, c, f, D;
    if (!(i !== Th && i !== Sh || r.charAt(++a) !== i) && (o = t.options.pedantic, s = i, c = s + s, f = r.length, a++, l = "", i = "", !(o && Mo2(r.charAt(a)))))
      for (; a < f; ) {
        if (D = i, i = r.charAt(a), i === s && r.charAt(a + 1) === s && (!o || !Mo2(D)) && (i = r.charAt(a + 2), i !== s))
          return Ah(l) ? n ? true : (u = e.now(), u.column += 2, u.offset += 2, e(c + l + c)({ type: "strong", children: t.tokenizeInline(l, u) })) : void 0;
        !o && i === qh && (l += i, i = r.charAt(++a)), l += i, a++;
      }
  }
});
var $o = C((UF, jo2) => {
  jo2.exports = Lh;
  var Ph = String.fromCharCode, _h = /\w/;
  function Lh(e) {
    return _h.test(typeof e == "number" ? Ph(e) : e.charAt(0));
  }
});
var Ho = C((YF, Go2) => {
  Go2.exports = Oh;
  function Oh(e, r) {
    var n = e.indexOf("*", r), t = e.indexOf("_", r);
    return t === -1 ? n : n === -1 || t < n ? t : n;
  }
});
var Qo = C((VF, Xo2) => {
  var Ih = Oe(), Nh = $o(), Wo2 = ne(), Rh = Ho();
  Xo2.exports = Jo2;
  Jo2.locator = Rh;
  var zh = "*", Ko2 = "_", Mh = "\\";
  function Jo2(e, r, n) {
    var t = this, a = 0, i = r.charAt(a), u, o, s, l, c, f, D;
    if (!(i !== zh && i !== Ko2) && (o = t.options.pedantic, c = i, s = i, f = r.length, a++, l = "", i = "", !(o && Wo2(r.charAt(a)))))
      for (; a < f; ) {
        if (D = i, i = r.charAt(a), i === s && (!o || !Wo2(D))) {
          if (i = r.charAt(++a), i !== s) {
            if (!Ih(l) || D === s)
              return;
            if (!o && s === Ko2 && Nh(i)) {
              l += s;
              continue;
            }
            return n ? true : (u = e.now(), u.column++, u.offset++, e(c + l + s)({ type: "emphasis", children: t.tokenizeInline(l, u) }));
          }
          l += s;
        }
        !o && i === Mh && (l += i, i = r.charAt(++a)), l += i, a++;
      }
  }
});
var es = C((jF, Zo2) => {
  Zo2.exports = Uh;
  function Uh(e, r) {
    return e.indexOf("~~", r);
  }
});
var us = C(($F, is) => {
  var rs = ne(), Yh = es();
  is.exports = ns;
  ns.locator = Yh;
  var et2 = "~", ts = "~~";
  function ns(e, r, n) {
    var t = this, a = "", i = "", u = "", o = "", s, l, c;
    if (!(!t.options.gfm || r.charAt(0) !== et2 || r.charAt(1) !== et2 || rs(r.charAt(2))))
      for (s = 1, l = r.length, c = e.now(), c.column += 2, c.offset += 2; ++s < l; ) {
        if (a = r.charAt(s), a === et2 && i === et2 && (!u || !rs(u)))
          return n ? true : e(ts + o + ts)({ type: "delete", children: t.tokenizeInline(o, c) });
        o += i, u = i, i = a;
      }
  }
});
var os = C((GF, as) => {
  as.exports = Vh;
  function Vh(e, r) {
    return e.indexOf("`", r);
  }
});
var ls = C((HF, cs) => {
  var jh = os();
  cs.exports = ss;
  ss.locator = jh;
  var pn = 10, dn = 32, hn2 = 96;
  function ss(e, r, n) {
    for (var t = r.length, a = 0, i, u, o, s, l, c; a < t && r.charCodeAt(a) === hn2; )
      a++;
    if (!(a === 0 || a === t)) {
      for (i = a, l = r.charCodeAt(a); a < t; ) {
        if (s = l, l = r.charCodeAt(a + 1), s === hn2) {
          if (u === void 0 && (u = a), o = a + 1, l !== hn2 && o - u === i) {
            c = true;
            break;
          }
        } else
          u !== void 0 && (u = void 0, o = void 0);
        a++;
      }
      if (c) {
        if (n)
          return true;
        if (a = i, t = u, s = r.charCodeAt(a), l = r.charCodeAt(t - 1), c = false, t - a > 2 && (s === dn || s === pn) && (l === dn || l === pn)) {
          for (a++, t--; a < t; ) {
            if (s = r.charCodeAt(a), s !== dn && s !== pn) {
              c = true;
              break;
            }
            a++;
          }
          c === true && (i++, u--);
        }
        return e(r.slice(0, o))({ type: "inlineCode", value: r.slice(i, u) });
      }
    }
  }
});
var Ds = C((WF, fs) => {
  fs.exports = $h;
  function $h(e, r) {
    for (var n = e.indexOf(`
`, r); n > r && e.charAt(n - 1) === " "; )
      n--;
    return n;
  }
});
var hs = C((KF, ds) => {
  var Gh = Ds();
  ds.exports = ps;
  ps.locator = Gh;
  var Hh = " ", Wh = `
`, Kh = 2;
  function ps(e, r, n) {
    for (var t = r.length, a = -1, i = "", u; ++a < t; ) {
      if (u = r.charAt(a), u === Wh)
        return a < Kh ? void 0 : n ? true : (i += u, e(i)({ type: "break" }));
      if (u !== Hh)
        return;
      i += u;
    }
  }
});
var gs = C((JF, ms) => {
  ms.exports = Jh;
  function Jh(e, r, n) {
    var t = this, a, i, u, o, s, l, c, f, D, h;
    if (n)
      return true;
    for (a = t.inlineMethods, o = a.length, i = t.inlineTokenizers, u = -1, D = r.length; ++u < o; )
      f = a[u], !(f === "text" || !i[f]) && (c = i[f].locator, c || e.file.fail("Missing locator: `" + f + "`"), l = c.call(t, r, 1), l !== -1 && l < D && (D = l));
    s = r.slice(0, D), h = e.now(), t.decode(s, h, p);
    function p(d, m, g) {
      e(g || d)({ type: "text", value: d });
    }
  }
});
var Cs = C((XF, Es) => {
  var Xh = _e(), rt2 = Ni(), Qh = zi(), Zh = Ui(), em = hu(), mn = vu();
  Es.exports = vs;
  function vs(e, r) {
    this.file = r, this.offset = {}, this.options = Xh(this.options), this.setOptions({}), this.inList = false, this.inBlock = false, this.inLink = false, this.atStart = true, this.toOffset = Qh(r).toOffset, this.unescape = Zh(this, "escape"), this.decode = em(this);
  }
  var z = vs.prototype;
  z.setOptions = wu();
  z.parse = Yu();
  z.options = Pt();
  z.exitStart = rt2("atStart", true);
  z.enterList = rt2("inList", false);
  z.enterLink = rt2("inLink", false);
  z.enterBlock = rt2("inBlock", false);
  z.interruptParagraph = [["thematicBreak"], ["list"], ["atxHeading"], ["fencedCode"], ["blockquote"], ["html"], ["setextHeading", { commonmark: false }], ["definition", { commonmark: false }]];
  z.interruptList = [["atxHeading", { pedantic: false }], ["fencedCode", { pedantic: false }], ["thematicBreak", { pedantic: false }], ["definition", { commonmark: false }]];
  z.interruptBlockquote = [["indentedCode", { commonmark: true }], ["fencedCode", { commonmark: true }], ["atxHeading", { commonmark: true }], ["setextHeading", { commonmark: true }], ["thematicBreak", { commonmark: true }], ["html", { commonmark: true }], ["list", { commonmark: true }], ["definition", { commonmark: false }]];
  z.blockTokenizers = { blankLine: ju(), indentedCode: Ku(), fencedCode: Qu(), blockquote: ia(), atxHeading: oa(), thematicBreak: la(), list: Ca(), setextHeading: ka(), html: Sa(), definition: Ma(), table: Va(), paragraph: Ga() };
  z.inlineTokenizers = { escape: Qa(), autoLink: io(), url: ho(), email: xo(), html: wo(), link: Lo(), reference: No(), strong: Vo(), emphasis: Qo(), deletion: us(), code: ls(), break: hs(), text: gs() };
  z.blockMethods = Fs(z.blockTokenizers);
  z.inlineMethods = Fs(z.inlineTokenizers);
  z.tokenizeBlock = mn("block");
  z.tokenizeInline = mn("inline");
  z.tokenizeFactory = mn;
  function Fs(e) {
    var r = [], n;
    for (n in e)
      r.push(n);
    return r;
  }
});
var ks = C((QF, ys) => {
  var rm = Oi(), tm = _e(), bs = Cs();
  ys.exports = xs;
  xs.Parser = bs;
  function xs(e) {
    var r = this.data("settings"), n = rm(bs);
    n.prototype.options = tm(n.prototype.options, r, e), this.Parser = n;
  }
});
var As = C((ZF, ws) => {
  ws.exports = nm;
  function nm(e) {
    if (e)
      throw e;
  }
});
var gn = C((eE, Bs) => {
  Bs.exports = function(r) {
    return r != null && r.constructor != null && typeof r.constructor.isBuffer == "function" && r.constructor.isBuffer(r);
  };
});
var Ns = C((rE, Is) => {
  var tt2 = Object.prototype.hasOwnProperty, Os = Object.prototype.toString, qs = Object.defineProperty, Ts = Object.getOwnPropertyDescriptor, Ss = function(r) {
    return typeof Array.isArray == "function" ? Array.isArray(r) : Os.call(r) === "[object Array]";
  }, Ps = function(r) {
    if (!r || Os.call(r) !== "[object Object]")
      return false;
    var n = tt2.call(r, "constructor"), t = r.constructor && r.constructor.prototype && tt2.call(r.constructor.prototype, "isPrototypeOf");
    if (r.constructor && !n && !t)
      return false;
    var a;
    for (a in r)
      ;
    return typeof a > "u" || tt2.call(r, a);
  }, _s = function(r, n) {
    qs && n.name === "__proto__" ? qs(r, n.name, { enumerable: true, configurable: true, value: n.newValue, writable: true }) : r[n.name] = n.newValue;
  }, Ls = function(r, n) {
    if (n === "__proto__")
      if (tt2.call(r, n)) {
        if (Ts)
          return Ts(r, n).value;
      } else
        return;
    return r[n];
  };
  Is.exports = function e() {
    var r, n, t, a, i, u, o = arguments[0], s = 1, l = arguments.length, c = false;
    for (typeof o == "boolean" && (c = o, o = arguments[1] || {}, s = 2), (o == null || typeof o != "object" && typeof o != "function") && (o = {}); s < l; ++s)
      if (r = arguments[s], r != null)
        for (n in r)
          t = Ls(o, n), a = Ls(r, n), o !== a && (c && a && (Ps(a) || (i = Ss(a))) ? (i ? (i = false, u = t && Ss(t) ? t : []) : u = t && Ps(t) ? t : {}, _s(o, { name: n, newValue: e(c, u, a) })) : typeof a < "u" && _s(o, { name: n, newValue: a }));
    return o;
  };
});
var zs = C((tE, Rs) => {
  Rs.exports = (e) => {
    if (Object.prototype.toString.call(e) !== "[object Object]")
      return false;
    let r = Object.getPrototypeOf(e);
    return r === null || r === Object.prototype;
  };
});
var Us = C((nE, Ms) => {
  var im = [].slice;
  Ms.exports = um;
  function um(e, r) {
    var n;
    return t;
    function t() {
      var u = im.call(arguments, 0), o = e.length > u.length, s;
      o && u.push(a);
      try {
        s = e.apply(null, u);
      } catch (l) {
        if (o && n)
          throw l;
        return a(l);
      }
      o || (s && typeof s.then == "function" ? s.then(i, a) : s instanceof Error ? a(s) : i(s));
    }
    function a() {
      n || (n = true, r.apply(null, arguments));
    }
    function i(u) {
      a(null, u);
    }
  }
});
var Gs = C((iE, $s) => {
  var Vs = Us();
  $s.exports = js;
  js.wrap = Vs;
  var Ys = [].slice;
  function js() {
    var e = [], r = {};
    return r.run = n, r.use = t, r;
    function n() {
      var a = -1, i = Ys.call(arguments, 0, -1), u = arguments[arguments.length - 1];
      if (typeof u != "function")
        throw new Error("Expected function as last argument, not " + u);
      o.apply(null, [null].concat(i));
      function o(s) {
        var l = e[++a], c = Ys.call(arguments, 0), f = c.slice(1), D = i.length, h = -1;
        if (s) {
          u(s);
          return;
        }
        for (; ++h < D; )
          (f[h] === null || f[h] === void 0) && (f[h] = i[h]);
        i = f, l ? Vs(l, o).apply(null, i) : u.apply(null, [null].concat(i));
      }
    }
    function t(a) {
      if (typeof a != "function")
        throw new Error("Expected `fn` to be a function, not " + a);
      return e.push(a), r;
    }
  }
});
var Js = C((uE, Ks) => {
  var Ge2 = {}.hasOwnProperty;
  Ks.exports = am;
  function am(e) {
    return !e || typeof e != "object" ? "" : Ge2.call(e, "position") || Ge2.call(e, "type") ? Hs(e.position) : Ge2.call(e, "start") || Ge2.call(e, "end") ? Hs(e) : Ge2.call(e, "line") || Ge2.call(e, "column") ? vn(e) : "";
  }
  function vn(e) {
    return (!e || typeof e != "object") && (e = {}), Ws(e.line) + ":" + Ws(e.column);
  }
  function Hs(e) {
    return (!e || typeof e != "object") && (e = {}), vn(e.start) + "-" + vn(e.end);
  }
  function Ws(e) {
    return e && typeof e == "number" ? e : 1;
  }
});
var Zs = C((aE, Qs) => {
  var om = Js();
  Qs.exports = Fn;
  function Xs() {
  }
  Xs.prototype = Error.prototype;
  Fn.prototype = new Xs();
  var ke2 = Fn.prototype;
  ke2.file = "";
  ke2.name = "";
  ke2.reason = "";
  ke2.message = "";
  ke2.stack = "";
  ke2.fatal = null;
  ke2.column = null;
  ke2.line = null;
  function Fn(e, r, n) {
    var t, a, i;
    typeof r == "string" && (n = r, r = null), t = sm(n), a = om(r) || "1:1", i = { start: { line: null, column: null }, end: { line: null, column: null } }, r && r.position && (r = r.position), r && (r.start ? (i = r, r = r.start) : i.start = r), e.stack && (this.stack = e.stack, e = e.message), this.message = e, this.name = a, this.reason = e, this.line = r ? r.line : null, this.column = r ? r.column : null, this.location = i, this.source = t[0], this.ruleId = t[1];
  }
  function sm(e) {
    var r = [null, null], n;
    return typeof e == "string" && (n = e.indexOf(":"), n === -1 ? r[1] = e : (r[0] = e.slice(0, n), r[1] = e.slice(n + 1))), r;
  }
});
var ec = C((He2) => {
  He2.basename = cm;
  He2.dirname = lm;
  He2.extname = fm;
  He2.join = Dm;
  He2.sep = "/";
  function cm(e, r) {
    var n = 0, t = -1, a, i, u, o;
    if (r !== void 0 && typeof r != "string")
      throw new TypeError('"ext" argument must be a string');
    if (Er2(e), a = e.length, r === void 0 || !r.length || r.length > e.length) {
      for (; a--; )
        if (e.charCodeAt(a) === 47) {
          if (u) {
            n = a + 1;
            break;
          }
        } else
          t < 0 && (u = true, t = a + 1);
      return t < 0 ? "" : e.slice(n, t);
    }
    if (r === e)
      return "";
    for (i = -1, o = r.length - 1; a--; )
      if (e.charCodeAt(a) === 47) {
        if (u) {
          n = a + 1;
          break;
        }
      } else
        i < 0 && (u = true, i = a + 1), o > -1 && (e.charCodeAt(a) === r.charCodeAt(o--) ? o < 0 && (t = a) : (o = -1, t = i));
    return n === t ? t = i : t < 0 && (t = e.length), e.slice(n, t);
  }
  function lm(e) {
    var r, n, t;
    if (Er2(e), !e.length)
      return ".";
    for (r = -1, t = e.length; --t; )
      if (e.charCodeAt(t) === 47) {
        if (n) {
          r = t;
          break;
        }
      } else
        n || (n = true);
    return r < 0 ? e.charCodeAt(0) === 47 ? "/" : "." : r === 1 && e.charCodeAt(0) === 47 ? "//" : e.slice(0, r);
  }
  function fm(e) {
    var r = -1, n = 0, t = -1, a = 0, i, u, o;
    for (Er2(e), o = e.length; o--; ) {
      if (u = e.charCodeAt(o), u === 47) {
        if (i) {
          n = o + 1;
          break;
        }
        continue;
      }
      t < 0 && (i = true, t = o + 1), u === 46 ? r < 0 ? r = o : a !== 1 && (a = 1) : r > -1 && (a = -1);
    }
    return r < 0 || t < 0 || a === 0 || a === 1 && r === t - 1 && r === n + 1 ? "" : e.slice(r, t);
  }
  function Dm() {
    for (var e = -1, r; ++e < arguments.length; )
      Er2(arguments[e]), arguments[e] && (r = r === void 0 ? arguments[e] : r + "/" + arguments[e]);
    return r === void 0 ? "." : pm(r);
  }
  function pm(e) {
    var r, n;
    return Er2(e), r = e.charCodeAt(0) === 47, n = dm(e, !r), !n.length && !r && (n = "."), n.length && e.charCodeAt(e.length - 1) === 47 && (n += "/"), r ? "/" + n : n;
  }
  function dm(e, r) {
    for (var n = "", t = 0, a = -1, i = 0, u = -1, o, s; ++u <= e.length; ) {
      if (u < e.length)
        o = e.charCodeAt(u);
      else {
        if (o === 47)
          break;
        o = 47;
      }
      if (o === 47) {
        if (!(a === u - 1 || i === 1))
          if (a !== u - 1 && i === 2) {
            if (n.length < 2 || t !== 2 || n.charCodeAt(n.length - 1) !== 46 || n.charCodeAt(n.length - 2) !== 46) {
              if (n.length > 2) {
                if (s = n.lastIndexOf("/"), s !== n.length - 1) {
                  s < 0 ? (n = "", t = 0) : (n = n.slice(0, s), t = n.length - 1 - n.lastIndexOf("/")), a = u, i = 0;
                  continue;
                }
              } else if (n.length) {
                n = "", t = 0, a = u, i = 0;
                continue;
              }
            }
            r && (n = n.length ? n + "/.." : "..", t = 2);
          } else
            n.length ? n += "/" + e.slice(a + 1, u) : n = e.slice(a + 1, u), t = u - a - 1;
        a = u, i = 0;
      } else
        o === 46 && i > -1 ? i++ : i = -1;
    }
    return n;
  }
  function Er2(e) {
    if (typeof e != "string")
      throw new TypeError("Path must be a string. Received " + JSON.stringify(e));
  }
});
var tc = C((rc) => {
  rc.cwd = hm;
  function hm() {
    return "/";
  }
});
var uc = C((cE, ic) => {
  var ie2 = ec(), mm = tc(), gm = gn();
  ic.exports = he2;
  var vm = {}.hasOwnProperty, En = ["history", "path", "basename", "stem", "extname", "dirname"];
  he2.prototype.toString = qm;
  Object.defineProperty(he2.prototype, "path", { get: Fm, set: Em });
  Object.defineProperty(he2.prototype, "dirname", { get: Cm, set: bm });
  Object.defineProperty(he2.prototype, "basename", { get: xm, set: ym });
  Object.defineProperty(he2.prototype, "extname", { get: km, set: wm });
  Object.defineProperty(he2.prototype, "stem", { get: Am, set: Bm });
  function he2(e) {
    var r, n;
    if (!e)
      e = {};
    else if (typeof e == "string" || gm(e))
      e = { contents: e };
    else if ("message" in e && "messages" in e)
      return e;
    if (!(this instanceof he2))
      return new he2(e);
    for (this.data = {}, this.messages = [], this.history = [], this.cwd = mm.cwd(), n = -1; ++n < En.length; )
      r = En[n], vm.call(e, r) && (this[r] = e[r]);
    for (r in e)
      En.indexOf(r) < 0 && (this[r] = e[r]);
  }
  function Fm() {
    return this.history[this.history.length - 1];
  }
  function Em(e) {
    bn2(e, "path"), this.path !== e && this.history.push(e);
  }
  function Cm() {
    return typeof this.path == "string" ? ie2.dirname(this.path) : void 0;
  }
  function bm(e) {
    nc(this.path, "dirname"), this.path = ie2.join(e || "", this.basename);
  }
  function xm() {
    return typeof this.path == "string" ? ie2.basename(this.path) : void 0;
  }
  function ym(e) {
    bn2(e, "basename"), Cn2(e, "basename"), this.path = ie2.join(this.dirname || "", e);
  }
  function km() {
    return typeof this.path == "string" ? ie2.extname(this.path) : void 0;
  }
  function wm(e) {
    if (Cn2(e, "extname"), nc(this.path, "extname"), e) {
      if (e.charCodeAt(0) !== 46)
        throw new Error("`extname` must start with `.`");
      if (e.indexOf(".", 1) > -1)
        throw new Error("`extname` cannot contain multiple dots");
    }
    this.path = ie2.join(this.dirname, this.stem + (e || ""));
  }
  function Am() {
    return typeof this.path == "string" ? ie2.basename(this.path, this.extname) : void 0;
  }
  function Bm(e) {
    bn2(e, "stem"), Cn2(e, "stem"), this.path = ie2.join(this.dirname || "", e + (this.extname || ""));
  }
  function qm(e) {
    return (this.contents || "").toString(e);
  }
  function Cn2(e, r) {
    if (e && e.indexOf(ie2.sep) > -1)
      throw new Error("`" + r + "` cannot be a path: did not expect `" + ie2.sep + "`");
  }
  function bn2(e, r) {
    if (!e)
      throw new Error("`" + r + "` cannot be empty");
  }
  function nc(e, r) {
    if (!e)
      throw new Error("Setting `" + r + "` requires `path` to be set too");
  }
});
var oc = C((lE, ac) => {
  var Tm = Zs(), nt2 = uc();
  ac.exports = nt2;
  nt2.prototype.message = Sm;
  nt2.prototype.info = _m;
  nt2.prototype.fail = Pm;
  function Sm(e, r, n) {
    var t = new Tm(e, r, n);
    return this.path && (t.name = this.path + ":" + t.name, t.file = this.path), t.fatal = false, this.messages.push(t), t;
  }
  function Pm() {
    var e = this.message.apply(this, arguments);
    throw e.fatal = true, e;
  }
  function _m() {
    var e = this.message.apply(this, arguments);
    return e.fatal = null, e;
  }
});
var cc = C((fE, sc) => {
  sc.exports = oc();
});
var vc = C((DE, gc) => {
  var lc = As(), Lm = gn(), it2 = Ns(), fc = zs(), hc = Gs(), Cr2 = cc();
  gc.exports = mc().freeze();
  var Om = [].slice, Im = {}.hasOwnProperty, Nm = hc().use(Rm).use(zm).use(Mm);
  function Rm(e, r) {
    r.tree = e.parse(r.file);
  }
  function zm(e, r, n) {
    e.run(r.tree, r.file, t);
    function t(a, i, u) {
      a ? n(a) : (r.tree = i, r.file = u, n());
    }
  }
  function Mm(e, r) {
    var n = e.stringify(r.tree, r.file);
    n == null || (typeof n == "string" || Lm(n) ? ("value" in r.file && (r.file.value = n), r.file.contents = n) : r.file.result = n);
  }
  function mc() {
    var e = [], r = hc(), n = {}, t = -1, a;
    return i.data = o, i.freeze = u, i.attachers = e, i.use = s, i.parse = c, i.stringify = h, i.run = f, i.runSync = D, i.process = p, i.processSync = d, i;
    function i() {
      for (var m = mc(), g = -1; ++g < e.length; )
        m.use.apply(null, e[g]);
      return m.data(it2(true, {}, n)), m;
    }
    function u() {
      var m, g;
      if (a)
        return i;
      for (; ++t < e.length; )
        m = e[t], m[1] !== false && (m[1] === true && (m[1] = void 0), g = m[0].apply(i, m.slice(1)), typeof g == "function" && r.use(g));
      return a = true, t = 1 / 0, i;
    }
    function o(m, g) {
      return typeof m == "string" ? arguments.length === 2 ? (kn2("data", a), n[m] = g, i) : Im.call(n, m) && n[m] || null : m ? (kn2("data", a), n = m, i) : n;
    }
    function s(m) {
      var g;
      if (kn2("use", a), m != null)
        if (typeof m == "function")
          b2.apply(null, arguments);
        else if (typeof m == "object")
          "length" in m ? B2(m) : x2(m);
        else
          throw new Error("Expected usable value, not `" + m + "`");
      return g && (n.settings = it2(n.settings || {}, g)), i;
      function x2(v2) {
        B2(v2.plugins), v2.settings && (g = it2(g || {}, v2.settings));
      }
      function F(v2) {
        if (typeof v2 == "function")
          b2(v2);
        else if (typeof v2 == "object")
          "length" in v2 ? b2.apply(null, v2) : x2(v2);
        else
          throw new Error("Expected usable value, not `" + v2 + "`");
      }
      function B2(v2) {
        var y2 = -1;
        if (v2 != null)
          if (typeof v2 == "object" && "length" in v2)
            for (; ++y2 < v2.length; )
              F(v2[y2]);
          else
            throw new Error("Expected a list of plugins, not `" + v2 + "`");
      }
      function b2(v2, y2) {
        var k2 = l(v2);
        k2 ? (fc(k2[1]) && fc(y2) && (y2 = it2(true, k2[1], y2)), k2[1] = y2) : e.push(Om.call(arguments));
      }
    }
    function l(m) {
      for (var g = -1; ++g < e.length; )
        if (e[g][0] === m)
          return e[g];
    }
    function c(m) {
      var g = Cr2(m), x2;
      return u(), x2 = i.Parser, xn2("parse", x2), Dc(x2, "parse") ? new x2(String(g), g).parse() : x2(String(g), g);
    }
    function f(m, g, x2) {
      if (pc(m), u(), !x2 && typeof g == "function" && (x2 = g, g = null), !x2)
        return new Promise(F);
      F(null, x2);
      function F(B2, b2) {
        r.run(m, Cr2(g), v2);
        function v2(y2, k2, E) {
          k2 = k2 || m, y2 ? b2(y2) : B2 ? B2(k2) : x2(null, k2, E);
        }
      }
    }
    function D(m, g) {
      var x2, F;
      return f(m, g, B2), dc("runSync", "run", F), x2;
      function B2(b2, v2) {
        F = true, x2 = v2, lc(b2);
      }
    }
    function h(m, g) {
      var x2 = Cr2(g), F;
      return u(), F = i.Compiler, yn2("stringify", F), pc(m), Dc(F, "compile") ? new F(m, x2).compile() : F(m, x2);
    }
    function p(m, g) {
      if (u(), xn2("process", i.Parser), yn2("process", i.Compiler), !g)
        return new Promise(x2);
      x2(null, g);
      function x2(F, B2) {
        var b2 = Cr2(m);
        Nm.run(i, { file: b2 }, v2);
        function v2(y2) {
          y2 ? B2(y2) : F ? F(b2) : g(null, b2);
        }
      }
    }
    function d(m) {
      var g, x2;
      return u(), xn2("processSync", i.Parser), yn2("processSync", i.Compiler), g = Cr2(m), p(g, F), dc("processSync", "process", x2), g;
      function F(B2) {
        x2 = true, lc(B2);
      }
    }
  }
  function Dc(e, r) {
    return typeof e == "function" && e.prototype && (Um(e.prototype) || r in e.prototype);
  }
  function Um(e) {
    var r;
    for (r in e)
      return true;
    return false;
  }
  function xn2(e, r) {
    if (typeof r != "function")
      throw new Error("Cannot `" + e + "` without `Parser`");
  }
  function yn2(e, r) {
    if (typeof r != "function")
      throw new Error("Cannot `" + e + "` without `Compiler`");
  }
  function kn2(e, r) {
    if (r)
      throw new Error("Cannot invoke `" + e + "` on a frozen processor.\nCreate a new processor first, by invoking it: use `processor()` instead of `processor`.");
  }
  function pc(e) {
    if (!e || typeof e.type != "string")
      throw new Error("Expected node, got `" + e + "`");
  }
  function dc(e, r, n) {
    if (!n)
      throw new Error("`" + e + "` finished async. Use `" + r + "` instead");
  }
});
var An = C((wn2) => {
  wn2.isRemarkParser = Ym;
  wn2.isRemarkCompiler = Vm;
  function Ym(e) {
    return !!(e && e.prototype && e.prototype.blockTokenizers);
  }
  function Vm(e) {
    return !!(e && e.prototype && e.prototype.visitors);
  }
});
var kc = C((dE, yc) => {
  var Fc = An();
  yc.exports = Hm;
  var Ec = 9, Cc = 32, ut2 = 36, jm = 48, $m = 57, bc = 92, Gm = ["math", "math-inline"], xc = "math-display";
  function Hm(e) {
    let r = this.Parser, n = this.Compiler;
    Fc.isRemarkParser(r) && Wm(r, e), Fc.isRemarkCompiler(n) && Km(n);
  }
  function Wm(e, r) {
    let n = e.prototype, t = n.inlineMethods;
    i.locator = a, n.inlineTokenizers.math = i, t.splice(t.indexOf("text"), 0, "math");
    function a(u, o) {
      return u.indexOf("$", o);
    }
    function i(u, o, s) {
      let l = o.length, c = false, f = false, D = 0, h, p, d, m, g, x2, F;
      if (o.charCodeAt(D) === bc && (f = true, D++), o.charCodeAt(D) === ut2) {
        if (D++, f)
          return s ? true : u(o.slice(0, D))({ type: "text", value: "$" });
        if (o.charCodeAt(D) === ut2 && (c = true, D++), d = o.charCodeAt(D), !(d === Cc || d === Ec)) {
          for (m = D; D < l; ) {
            if (p = d, d = o.charCodeAt(D + 1), p === ut2) {
              if (h = o.charCodeAt(D - 1), h !== Cc && h !== Ec && (d !== d || d < jm || d > $m) && (!c || d === ut2)) {
                g = D - 1, D++, c && D++, x2 = D;
                break;
              }
            } else
              p === bc && (D++, d = o.charCodeAt(D + 1));
            D++;
          }
          if (x2 !== void 0)
            return s ? true : (F = o.slice(m, g + 1), u(o.slice(0, x2))({ type: "inlineMath", value: F, data: { hName: "span", hProperties: { className: Gm.concat(c && r.inlineMathDouble ? [xc] : []) }, hChildren: [{ type: "text", value: F }] } }));
        }
      }
    }
  }
  function Km(e) {
    let r = e.prototype;
    r.visitors.inlineMath = n;
    function n(t) {
      let a = "$";
      return (t.data && t.data.hProperties && t.data.hProperties.className || []).includes(xc) && (a = "$$"), a + t.value + a;
    }
  }
});
var Tc = C((hE, qc) => {
  var wc = An();
  qc.exports = Zm;
  var Ac = 10, br2 = 32, Bn2 = 36, Bc = `
`, Jm = "$", Xm = 2, Qm = ["math", "math-display"];
  function Zm() {
    let e = this.Parser, r = this.Compiler;
    wc.isRemarkParser(e) && eg(e), wc.isRemarkCompiler(r) && rg(r);
  }
  function eg(e) {
    let r = e.prototype, n = r.blockMethods, t = r.interruptParagraph, a = r.interruptList, i = r.interruptBlockquote;
    r.blockTokenizers.math = u, n.splice(n.indexOf("fencedCode") + 1, 0, "math"), t.splice(t.indexOf("fencedCode") + 1, 0, ["math"]), a.splice(a.indexOf("fencedCode") + 1, 0, ["math"]), i.splice(i.indexOf("fencedCode") + 1, 0, ["math"]);
    function u(o, s, l) {
      var c = s.length, f = 0;
      let D, h, p, d, m, g, x2, F, B2, b2, v2;
      for (; f < c && s.charCodeAt(f) === br2; )
        f++;
      for (m = f; f < c && s.charCodeAt(f) === Bn2; )
        f++;
      if (g = f - m, !(g < Xm)) {
        for (; f < c && s.charCodeAt(f) === br2; )
          f++;
        for (x2 = f; f < c; ) {
          if (D = s.charCodeAt(f), D === Bn2)
            return;
          if (D === Ac)
            break;
          f++;
        }
        if (s.charCodeAt(f) === Ac) {
          if (l)
            return true;
          for (h = [], x2 !== f && h.push(s.slice(x2, f)), f++, p = s.indexOf(Bc, f + 1), p = p === -1 ? c : p; f < c; ) {
            for (F = false, b2 = f, v2 = p, d = p, B2 = 0; d > b2 && s.charCodeAt(d - 1) === br2; )
              d--;
            for (; d > b2 && s.charCodeAt(d - 1) === Bn2; )
              B2++, d--;
            for (g <= B2 && s.indexOf(Jm, b2) === d && (F = true, v2 = d); b2 <= v2 && b2 - f < m && s.charCodeAt(b2) === br2; )
              b2++;
            if (F)
              for (; v2 > b2 && s.charCodeAt(v2 - 1) === br2; )
                v2--;
            if ((!F || b2 !== v2) && h.push(s.slice(b2, v2)), F)
              break;
            f = p + 1, p = s.indexOf(Bc, f + 1), p = p === -1 ? c : p;
          }
          return h = h.join(`
`), o(s.slice(0, p))({ type: "math", value: h, data: { hName: "div", hProperties: { className: Qm.concat() }, hChildren: [{ type: "text", value: h }] } });
        }
      }
    }
  }
  function rg(e) {
    let r = e.prototype;
    r.visitors.math = n;
    function n(t) {
      return `$$
` + t.value + `
$$`;
    }
  }
});
var Pc = C((mE, Sc) => {
  var tg = kc(), ng = Tc();
  Sc.exports = ig;
  function ig(e) {
    var r = e || {};
    ng.call(this, r), tg.call(this, r);
  }
});
var Lc = C((gE, _c) => {
  _c.exports = cg;
  var xr2 = 9, at2 = 10, We2 = 32, ug = 33, ag = 58, Ke2 = 91, og = 92, qn2 = 93, yr2 = 94, ot2 = 96, st2 = 4, sg = 1024;
  function cg(e) {
    var r = this.Parser, n = this.Compiler;
    lg(r) && Dg(r, e), fg(n) && pg(n);
  }
  function lg(e) {
    return !!(e && e.prototype && e.prototype.blockTokenizers);
  }
  function fg(e) {
    return !!(e && e.prototype && e.prototype.visitors);
  }
  function Dg(e, r) {
    for (var n = r || {}, t = e.prototype, a = t.blockTokenizers, i = t.inlineTokenizers, u = t.blockMethods, o = t.inlineMethods, s = a.definition, l = i.reference, c = [], f = -1, D = u.length, h; ++f < D; )
      h = u[f], !(h === "newline" || h === "indentedCode" || h === "paragraph" || h === "footnoteDefinition") && c.push([h]);
    c.push(["footnoteDefinition"]), n.inlineNotes && (Tn2(o, "reference", "inlineNote"), i.inlineNote = m), Tn2(u, "definition", "footnoteDefinition"), Tn2(o, "reference", "footnoteCall"), a.definition = x2, a.footnoteDefinition = p, i.footnoteCall = d, i.reference = g, t.interruptFootnoteDefinition = c, g.locator = l.locator, d.locator = F, m.locator = B2;
    function p(b2, v2, y2) {
      for (var k2 = this, E = k2.interruptFootnoteDefinition, w2 = k2.offset, A2 = v2.length + 1, q2 = 0, T2 = [], N2, _, P2, S2, L2, we2, j2, I2, J2, K2, me2, ge, M2; q2 < A2 && (S2 = v2.charCodeAt(q2), !(S2 !== xr2 && S2 !== We2)); )
        q2++;
      if (v2.charCodeAt(q2++) === Ke2 && v2.charCodeAt(q2++) === yr2) {
        for (_ = q2; q2 < A2; ) {
          if (S2 = v2.charCodeAt(q2), S2 !== S2 || S2 === at2 || S2 === xr2 || S2 === We2)
            return;
          if (S2 === qn2) {
            P2 = q2, q2++;
            break;
          }
          q2++;
        }
        if (!(P2 === void 0 || _ === P2 || v2.charCodeAt(q2++) !== ag)) {
          if (y2)
            return true;
          for (N2 = v2.slice(_, P2), L2 = b2.now(), J2 = 0, K2 = 0, me2 = q2, ge = []; q2 < A2; ) {
            if (S2 = v2.charCodeAt(q2), S2 !== S2 || S2 === at2)
              M2 = { start: J2, contentStart: me2 || q2, contentEnd: q2, end: q2 }, ge.push(M2), S2 === at2 && (J2 = q2 + 1, K2 = 0, me2 = void 0, M2.end = J2);
            else if (K2 !== void 0)
              if (S2 === We2 || S2 === xr2)
                K2 += S2 === We2 ? 1 : st2 - K2 % st2, K2 > st2 && (K2 = void 0, me2 = q2);
              else {
                if (K2 < st2 && M2 && (M2.contentStart === M2.contentEnd || dg(E, a, k2, [b2, v2.slice(q2, sg), true])))
                  break;
                K2 = void 0, me2 = q2;
              }
            q2++;
          }
          for (q2 = -1, A2 = ge.length; A2 > 0 && (M2 = ge[A2 - 1], M2.contentStart === M2.contentEnd); )
            A2--;
          for (we2 = b2(v2.slice(0, M2.contentEnd)); ++q2 < A2; )
            M2 = ge[q2], w2[L2.line + q2] = (w2[L2.line + q2] || 0) + (M2.contentStart - M2.start), T2.push(v2.slice(M2.contentStart, M2.end));
          return j2 = k2.enterBlock(), I2 = k2.tokenizeBlock(T2.join(""), L2), j2(), we2({ type: "footnoteDefinition", identifier: N2.toLowerCase(), label: N2, children: I2 });
        }
      }
    }
    function d(b2, v2, y2) {
      var k2 = v2.length + 1, E = 0, w2, A2, q2, T2;
      if (v2.charCodeAt(E++) === Ke2 && v2.charCodeAt(E++) === yr2) {
        for (A2 = E; E < k2; ) {
          if (T2 = v2.charCodeAt(E), T2 !== T2 || T2 === at2 || T2 === xr2 || T2 === We2)
            return;
          if (T2 === qn2) {
            q2 = E, E++;
            break;
          }
          E++;
        }
        if (!(q2 === void 0 || A2 === q2))
          return y2 ? true : (w2 = v2.slice(A2, q2), b2(v2.slice(0, E))({ type: "footnoteReference", identifier: w2.toLowerCase(), label: w2 }));
      }
    }
    function m(b2, v2, y2) {
      var k2 = this, E = v2.length + 1, w2 = 0, A2 = 0, q2, T2, N2, _, P2, S2, L2;
      if (v2.charCodeAt(w2++) === yr2 && v2.charCodeAt(w2++) === Ke2) {
        for (N2 = w2; w2 < E; ) {
          if (T2 = v2.charCodeAt(w2), T2 !== T2)
            return;
          if (S2 === void 0)
            if (T2 === og)
              w2 += 2;
            else if (T2 === Ke2)
              A2++, w2++;
            else if (T2 === qn2)
              if (A2 === 0) {
                _ = w2, w2++;
                break;
              } else
                A2--, w2++;
            else if (T2 === ot2) {
              for (P2 = w2, S2 = 1; v2.charCodeAt(P2 + S2) === ot2; )
                S2++;
              w2 += S2;
            } else
              w2++;
          else if (T2 === ot2) {
            for (P2 = w2, L2 = 1; v2.charCodeAt(P2 + L2) === ot2; )
              L2++;
            w2 += L2, S2 === L2 && (S2 = void 0), L2 = void 0;
          } else
            w2++;
        }
        if (_ !== void 0)
          return y2 ? true : (q2 = b2.now(), q2.column += 2, q2.offset += 2, b2(v2.slice(0, w2))({ type: "footnote", children: k2.tokenizeInline(v2.slice(N2, _), q2) }));
      }
    }
    function g(b2, v2, y2) {
      var k2 = 0;
      if (v2.charCodeAt(k2) === ug && k2++, v2.charCodeAt(k2) === Ke2 && v2.charCodeAt(k2 + 1) !== yr2)
        return l.call(this, b2, v2, y2);
    }
    function x2(b2, v2, y2) {
      for (var k2 = 0, E = v2.charCodeAt(k2); E === We2 || E === xr2; )
        E = v2.charCodeAt(++k2);
      if (E === Ke2 && v2.charCodeAt(k2 + 1) !== yr2)
        return s.call(this, b2, v2, y2);
    }
    function F(b2, v2) {
      return b2.indexOf("[", v2);
    }
    function B2(b2, v2) {
      return b2.indexOf("^[", v2);
    }
  }
  function pg(e) {
    var r = e.prototype.visitors, n = "    ";
    r.footnote = t, r.footnoteReference = a, r.footnoteDefinition = i;
    function t(u) {
      return "^[" + this.all(u).join("") + "]";
    }
    function a(u) {
      return "[^" + (u.label || u.identifier) + "]";
    }
    function i(u) {
      for (var o = this.all(u).join(`

`).split(`
`), s = 0, l = o.length, c; ++s < l; )
        c = o[s], c !== "" && (o[s] = n + c);
      return "[^" + (u.label || u.identifier) + "]: " + o.join(`
`);
    }
  }
  function Tn2(e, r, n) {
    e.splice(e.indexOf(r), 0, n);
  }
  function dg(e, r, n, t) {
    for (var a = e.length, i = -1; ++i < a; )
      if (r[e[i][0]].apply(n, t))
        return true;
    return false;
  }
});
var Ln = {};
On(Ln, { languages: () => Qc, options: () => Zc, parsers: () => Pn, printers: () => wg });
var al = (e, r, n, t) => {
  if (!(e && r == null))
    return r.replaceAll ? r.replaceAll(n, t) : n.global ? r.replace(n, t) : r.split(n).join(t);
}, R = al;
var ol = (e, r, n) => {
  if (!(e && r == null))
    return Array.isArray(r) || typeof r == "string" ? r[n < 0 ? r.length + n : n] : r.at(n);
}, U = ol;
var qi = Ie(kr(), 1);
function Je(e) {
  if (typeof e != "string")
    throw new TypeError("Expected a string");
  return e.replace(/[|\\{}()[\]^$+*?.]/g, "\\$&").replace(/-/g, "\\x2d");
}
function cl(e, r) {
  let n = e.match(new RegExp(`(${Je(r)})+`, "g"));
  if (n === null)
    return 0;
  let t = /* @__PURE__ */ new Map(), a = 0;
  for (let i of n) {
    let u = i.length / r.length;
    t.set(u, true), u > a && (a = u);
  }
  for (let i = 1; i < a; i++)
    if (!t.get(i))
      return i;
  return a + 1;
}
var Nn = cl;
function ll(e, r) {
  let n = e.match(new RegExp(`(${Je(r)})+`, "g"));
  return n === null ? 0 : n.reduce((t, a) => Math.max(t, a.length / r.length), 0);
}
var wr = ll;
var Rn = () => /[#*0-9]\uFE0F?\u20E3|[\xA9\xAE\u203C\u2049\u2122\u2139\u2194-\u2199\u21A9\u21AA\u231A\u231B\u2328\u23CF\u23ED-\u23EF\u23F1\u23F2\u23F8-\u23FA\u24C2\u25AA\u25AB\u25B6\u25C0\u25FB\u25FC\u25FE\u2600-\u2604\u260E\u2611\u2614\u2615\u2618\u2620\u2622\u2623\u2626\u262A\u262E\u262F\u2638-\u263A\u2640\u2642\u2648-\u2653\u265F\u2660\u2663\u2665\u2666\u2668\u267B\u267E\u267F\u2692\u2694-\u2697\u2699\u269B\u269C\u26A0\u26A7\u26AA\u26B0\u26B1\u26BD\u26BE\u26C4\u26C8\u26CF\u26D1\u26D3\u26E9\u26F0-\u26F5\u26F7\u26F8\u26FA\u2702\u2708\u2709\u270F\u2712\u2714\u2716\u271D\u2721\u2733\u2734\u2744\u2747\u2757\u2763\u27A1\u2934\u2935\u2B05-\u2B07\u2B1B\u2B1C\u2B55\u3030\u303D\u3297\u3299]\uFE0F?|[\u261D\u270C\u270D](?:\uFE0F|\uD83C[\uDFFB-\uDFFF])?|[\u270A\u270B](?:\uD83C[\uDFFB-\uDFFF])?|[\u23E9-\u23EC\u23F0\u23F3\u25FD\u2693\u26A1\u26AB\u26C5\u26CE\u26D4\u26EA\u26FD\u2705\u2728\u274C\u274E\u2753-\u2755\u2795-\u2797\u27B0\u27BF\u2B50]|\u26F9(?:\uFE0F|\uD83C[\uDFFB-\uDFFF])?(?:\u200D[\u2640\u2642]\uFE0F?)?|\u2764\uFE0F?(?:\u200D(?:\uD83D\uDD25|\uD83E\uDE79))?|\uD83C(?:[\uDC04\uDD70\uDD71\uDD7E\uDD7F\uDE02\uDE37\uDF21\uDF24-\uDF2C\uDF36\uDF7D\uDF96\uDF97\uDF99-\uDF9B\uDF9E\uDF9F\uDFCD\uDFCE\uDFD4-\uDFDF\uDFF5\uDFF7]\uFE0F?|[\uDF85\uDFC2\uDFC7](?:\uD83C[\uDFFB-\uDFFF])?|[\uDFC3\uDFC4\uDFCA](?:\uD83C[\uDFFB-\uDFFF])?(?:\u200D[\u2640\u2642]\uFE0F?)?|[\uDFCB\uDFCC](?:\uFE0F|\uD83C[\uDFFB-\uDFFF])?(?:\u200D[\u2640\u2642]\uFE0F?)?|[\uDCCF\uDD8E\uDD91-\uDD9A\uDE01\uDE1A\uDE2F\uDE32-\uDE36\uDE38-\uDE3A\uDE50\uDE51\uDF00-\uDF20\uDF2D-\uDF35\uDF37-\uDF7C\uDF7E-\uDF84\uDF86-\uDF93\uDFA0-\uDFC1\uDFC5\uDFC6\uDFC8\uDFC9\uDFCF-\uDFD3\uDFE0-\uDFF0\uDFF8-\uDFFF]|\uDDE6\uD83C[\uDDE8-\uDDEC\uDDEE\uDDF1\uDDF2\uDDF4\uDDF6-\uDDFA\uDDFC\uDDFD\uDDFF]|\uDDE7\uD83C[\uDDE6\uDDE7\uDDE9-\uDDEF\uDDF1-\uDDF4\uDDF6-\uDDF9\uDDFB\uDDFC\uDDFE\uDDFF]|\uDDE8\uD83C[\uDDE6\uDDE8\uDDE9\uDDEB-\uDDEE\uDDF0-\uDDF5\uDDF7\uDDFA-\uDDFF]|\uDDE9\uD83C[\uDDEA\uDDEC\uDDEF\uDDF0\uDDF2\uDDF4\uDDFF]|\uDDEA\uD83C[\uDDE6\uDDE8\uDDEA\uDDEC\uDDED\uDDF7-\uDDFA]|\uDDEB\uD83C[\uDDEE-\uDDF0\uDDF2\uDDF4\uDDF7]|\uDDEC\uD83C[\uDDE6\uDDE7\uDDE9-\uDDEE\uDDF1-\uDDF3\uDDF5-\uDDFA\uDDFC\uDDFE]|\uDDED\uD83C[\uDDF0\uDDF2\uDDF3\uDDF7\uDDF9\uDDFA]|\uDDEE\uD83C[\uDDE8-\uDDEA\uDDF1-\uDDF4\uDDF6-\uDDF9]|\uDDEF\uD83C[\uDDEA\uDDF2\uDDF4\uDDF5]|\uDDF0\uD83C[\uDDEA\uDDEC-\uDDEE\uDDF2\uDDF3\uDDF5\uDDF7\uDDFC\uDDFE\uDDFF]|\uDDF1\uD83C[\uDDE6-\uDDE8\uDDEE\uDDF0\uDDF7-\uDDFB\uDDFE]|\uDDF2\uD83C[\uDDE6\uDDE8-\uDDED\uDDF0-\uDDFF]|\uDDF3\uD83C[\uDDE6\uDDE8\uDDEA-\uDDEC\uDDEE\uDDF1\uDDF4\uDDF5\uDDF7\uDDFA\uDDFF]|\uDDF4\uD83C\uDDF2|\uDDF5\uD83C[\uDDE6\uDDEA-\uDDED\uDDF0-\uDDF3\uDDF7-\uDDF9\uDDFC\uDDFE]|\uDDF6\uD83C\uDDE6|\uDDF7\uD83C[\uDDEA\uDDF4\uDDF8\uDDFA\uDDFC]|\uDDF8\uD83C[\uDDE6-\uDDEA\uDDEC-\uDDF4\uDDF7-\uDDF9\uDDFB\uDDFD-\uDDFF]|\uDDF9\uD83C[\uDDE6\uDDE8\uDDE9\uDDEB-\uDDED\uDDEF-\uDDF4\uDDF7\uDDF9\uDDFB\uDDFC\uDDFF]|\uDDFA\uD83C[\uDDE6\uDDEC\uDDF2\uDDF3\uDDF8\uDDFE\uDDFF]|\uDDFB\uD83C[\uDDE6\uDDE8\uDDEA\uDDEC\uDDEE\uDDF3\uDDFA]|\uDDFC\uD83C[\uDDEB\uDDF8]|\uDDFD\uD83C\uDDF0|\uDDFE\uD83C[\uDDEA\uDDF9]|\uDDFF\uD83C[\uDDE6\uDDF2\uDDFC]|\uDFF3\uFE0F?(?:\u200D(?:\u26A7\uFE0F?|\uD83C\uDF08))?|\uDFF4(?:\u200D\u2620\uFE0F?|\uDB40\uDC67\uDB40\uDC62\uDB40(?:\uDC65\uDB40\uDC6E\uDB40\uDC67|\uDC73\uDB40\uDC63\uDB40\uDC74|\uDC77\uDB40\uDC6C\uDB40\uDC73)\uDB40\uDC7F)?)|\uD83D(?:[\uDC08\uDC26](?:\u200D\u2B1B)?|[\uDC3F\uDCFD\uDD49\uDD4A\uDD6F\uDD70\uDD73\uDD76-\uDD79\uDD87\uDD8A-\uDD8D\uDDA5\uDDA8\uDDB1\uDDB2\uDDBC\uDDC2-\uDDC4\uDDD1-\uDDD3\uDDDC-\uDDDE\uDDE1\uDDE3\uDDE8\uDDEF\uDDF3\uDDFA\uDECB\uDECD-\uDECF\uDEE0-\uDEE5\uDEE9\uDEF0\uDEF3]\uFE0F?|[\uDC42\uDC43\uDC46-\uDC50\uDC66\uDC67\uDC6B-\uDC6D\uDC72\uDC74-\uDC76\uDC78\uDC7C\uDC83\uDC85\uDC8F\uDC91\uDCAA\uDD7A\uDD95\uDD96\uDE4C\uDE4F\uDEC0\uDECC](?:\uD83C[\uDFFB-\uDFFF])?|[\uDC6E\uDC70\uDC71\uDC73\uDC77\uDC81\uDC82\uDC86\uDC87\uDE45-\uDE47\uDE4B\uDE4D\uDE4E\uDEA3\uDEB4-\uDEB6](?:\uD83C[\uDFFB-\uDFFF])?(?:\u200D[\u2640\u2642]\uFE0F?)?|[\uDD74\uDD90](?:\uFE0F|\uD83C[\uDFFB-\uDFFF])?|[\uDC00-\uDC07\uDC09-\uDC14\uDC16-\uDC25\uDC27-\uDC3A\uDC3C-\uDC3E\uDC40\uDC44\uDC45\uDC51-\uDC65\uDC6A\uDC79-\uDC7B\uDC7D-\uDC80\uDC84\uDC88-\uDC8E\uDC90\uDC92-\uDCA9\uDCAB-\uDCFC\uDCFF-\uDD3D\uDD4B-\uDD4E\uDD50-\uDD67\uDDA4\uDDFB-\uDE2D\uDE2F-\uDE34\uDE37-\uDE44\uDE48-\uDE4A\uDE80-\uDEA2\uDEA4-\uDEB3\uDEB7-\uDEBF\uDEC1-\uDEC5\uDED0-\uDED2\uDED5-\uDED7\uDEDC-\uDEDF\uDEEB\uDEEC\uDEF4-\uDEFC\uDFE0-\uDFEB\uDFF0]|\uDC15(?:\u200D\uD83E\uDDBA)?|\uDC3B(?:\u200D\u2744\uFE0F?)?|\uDC41\uFE0F?(?:\u200D\uD83D\uDDE8\uFE0F?)?|\uDC68(?:\u200D(?:[\u2695\u2696\u2708]\uFE0F?|\u2764\uFE0F?\u200D\uD83D(?:\uDC8B\u200D\uD83D)?\uDC68|\uD83C[\uDF3E\uDF73\uDF7C\uDF93\uDFA4\uDFA8\uDFEB\uDFED]|\uD83D(?:[\uDC68\uDC69]\u200D\uD83D(?:\uDC66(?:\u200D\uD83D\uDC66)?|\uDC67(?:\u200D\uD83D[\uDC66\uDC67])?)|[\uDCBB\uDCBC\uDD27\uDD2C\uDE80\uDE92]|\uDC66(?:\u200D\uD83D\uDC66)?|\uDC67(?:\u200D\uD83D[\uDC66\uDC67])?)|\uD83E[\uDDAF-\uDDB3\uDDBC\uDDBD])|\uD83C(?:\uDFFB(?:\u200D(?:[\u2695\u2696\u2708]\uFE0F?|\u2764\uFE0F?\u200D\uD83D(?:\uDC8B\u200D\uD83D)?\uDC68\uD83C[\uDFFB-\uDFFF]|\uD83C[\uDF3E\uDF73\uDF7C\uDF93\uDFA4\uDFA8\uDFEB\uDFED]|\uD83D[\uDCBB\uDCBC\uDD27\uDD2C\uDE80\uDE92]|\uD83E(?:[\uDDAF-\uDDB3\uDDBC\uDDBD]|\uDD1D\u200D\uD83D\uDC68\uD83C[\uDFFC-\uDFFF])))?|\uDFFC(?:\u200D(?:[\u2695\u2696\u2708]\uFE0F?|\u2764\uFE0F?\u200D\uD83D(?:\uDC8B\u200D\uD83D)?\uDC68\uD83C[\uDFFB-\uDFFF]|\uD83C[\uDF3E\uDF73\uDF7C\uDF93\uDFA4\uDFA8\uDFEB\uDFED]|\uD83D[\uDCBB\uDCBC\uDD27\uDD2C\uDE80\uDE92]|\uD83E(?:[\uDDAF-\uDDB3\uDDBC\uDDBD]|\uDD1D\u200D\uD83D\uDC68\uD83C[\uDFFB\uDFFD-\uDFFF])))?|\uDFFD(?:\u200D(?:[\u2695\u2696\u2708]\uFE0F?|\u2764\uFE0F?\u200D\uD83D(?:\uDC8B\u200D\uD83D)?\uDC68\uD83C[\uDFFB-\uDFFF]|\uD83C[\uDF3E\uDF73\uDF7C\uDF93\uDFA4\uDFA8\uDFEB\uDFED]|\uD83D[\uDCBB\uDCBC\uDD27\uDD2C\uDE80\uDE92]|\uD83E(?:[\uDDAF-\uDDB3\uDDBC\uDDBD]|\uDD1D\u200D\uD83D\uDC68\uD83C[\uDFFB\uDFFC\uDFFE\uDFFF])))?|\uDFFE(?:\u200D(?:[\u2695\u2696\u2708]\uFE0F?|\u2764\uFE0F?\u200D\uD83D(?:\uDC8B\u200D\uD83D)?\uDC68\uD83C[\uDFFB-\uDFFF]|\uD83C[\uDF3E\uDF73\uDF7C\uDF93\uDFA4\uDFA8\uDFEB\uDFED]|\uD83D[\uDCBB\uDCBC\uDD27\uDD2C\uDE80\uDE92]|\uD83E(?:[\uDDAF-\uDDB3\uDDBC\uDDBD]|\uDD1D\u200D\uD83D\uDC68\uD83C[\uDFFB-\uDFFD\uDFFF])))?|\uDFFF(?:\u200D(?:[\u2695\u2696\u2708]\uFE0F?|\u2764\uFE0F?\u200D\uD83D(?:\uDC8B\u200D\uD83D)?\uDC68\uD83C[\uDFFB-\uDFFF]|\uD83C[\uDF3E\uDF73\uDF7C\uDF93\uDFA4\uDFA8\uDFEB\uDFED]|\uD83D[\uDCBB\uDCBC\uDD27\uDD2C\uDE80\uDE92]|\uD83E(?:[\uDDAF-\uDDB3\uDDBC\uDDBD]|\uDD1D\u200D\uD83D\uDC68\uD83C[\uDFFB-\uDFFE])))?))?|\uDC69(?:\u200D(?:[\u2695\u2696\u2708]\uFE0F?|\u2764\uFE0F?\u200D\uD83D(?:\uDC8B\u200D\uD83D)?[\uDC68\uDC69]|\uD83C[\uDF3E\uDF73\uDF7C\uDF93\uDFA4\uDFA8\uDFEB\uDFED]|\uD83D(?:[\uDCBB\uDCBC\uDD27\uDD2C\uDE80\uDE92]|\uDC66(?:\u200D\uD83D\uDC66)?|\uDC67(?:\u200D\uD83D[\uDC66\uDC67])?|\uDC69\u200D\uD83D(?:\uDC66(?:\u200D\uD83D\uDC66)?|\uDC67(?:\u200D\uD83D[\uDC66\uDC67])?))|\uD83E[\uDDAF-\uDDB3\uDDBC\uDDBD])|\uD83C(?:\uDFFB(?:\u200D(?:[\u2695\u2696\u2708]\uFE0F?|\u2764\uFE0F?\u200D\uD83D(?:[\uDC68\uDC69]|\uDC8B\u200D\uD83D[\uDC68\uDC69])\uD83C[\uDFFB-\uDFFF]|\uD83C[\uDF3E\uDF73\uDF7C\uDF93\uDFA4\uDFA8\uDFEB\uDFED]|\uD83D[\uDCBB\uDCBC\uDD27\uDD2C\uDE80\uDE92]|\uD83E(?:[\uDDAF-\uDDB3\uDDBC\uDDBD]|\uDD1D\u200D\uD83D[\uDC68\uDC69]\uD83C[\uDFFC-\uDFFF])))?|\uDFFC(?:\u200D(?:[\u2695\u2696\u2708]\uFE0F?|\u2764\uFE0F?\u200D\uD83D(?:[\uDC68\uDC69]|\uDC8B\u200D\uD83D[\uDC68\uDC69])\uD83C[\uDFFB-\uDFFF]|\uD83C[\uDF3E\uDF73\uDF7C\uDF93\uDFA4\uDFA8\uDFEB\uDFED]|\uD83D[\uDCBB\uDCBC\uDD27\uDD2C\uDE80\uDE92]|\uD83E(?:[\uDDAF-\uDDB3\uDDBC\uDDBD]|\uDD1D\u200D\uD83D[\uDC68\uDC69]\uD83C[\uDFFB\uDFFD-\uDFFF])))?|\uDFFD(?:\u200D(?:[\u2695\u2696\u2708]\uFE0F?|\u2764\uFE0F?\u200D\uD83D(?:[\uDC68\uDC69]|\uDC8B\u200D\uD83D[\uDC68\uDC69])\uD83C[\uDFFB-\uDFFF]|\uD83C[\uDF3E\uDF73\uDF7C\uDF93\uDFA4\uDFA8\uDFEB\uDFED]|\uD83D[\uDCBB\uDCBC\uDD27\uDD2C\uDE80\uDE92]|\uD83E(?:[\uDDAF-\uDDB3\uDDBC\uDDBD]|\uDD1D\u200D\uD83D[\uDC68\uDC69]\uD83C[\uDFFB\uDFFC\uDFFE\uDFFF])))?|\uDFFE(?:\u200D(?:[\u2695\u2696\u2708]\uFE0F?|\u2764\uFE0F?\u200D\uD83D(?:[\uDC68\uDC69]|\uDC8B\u200D\uD83D[\uDC68\uDC69])\uD83C[\uDFFB-\uDFFF]|\uD83C[\uDF3E\uDF73\uDF7C\uDF93\uDFA4\uDFA8\uDFEB\uDFED]|\uD83D[\uDCBB\uDCBC\uDD27\uDD2C\uDE80\uDE92]|\uD83E(?:[\uDDAF-\uDDB3\uDDBC\uDDBD]|\uDD1D\u200D\uD83D[\uDC68\uDC69]\uD83C[\uDFFB-\uDFFD\uDFFF])))?|\uDFFF(?:\u200D(?:[\u2695\u2696\u2708]\uFE0F?|\u2764\uFE0F?\u200D\uD83D(?:[\uDC68\uDC69]|\uDC8B\u200D\uD83D[\uDC68\uDC69])\uD83C[\uDFFB-\uDFFF]|\uD83C[\uDF3E\uDF73\uDF7C\uDF93\uDFA4\uDFA8\uDFEB\uDFED]|\uD83D[\uDCBB\uDCBC\uDD27\uDD2C\uDE80\uDE92]|\uD83E(?:[\uDDAF-\uDDB3\uDDBC\uDDBD]|\uDD1D\u200D\uD83D[\uDC68\uDC69]\uD83C[\uDFFB-\uDFFE])))?))?|\uDC6F(?:\u200D[\u2640\u2642]\uFE0F?)?|\uDD75(?:\uFE0F|\uD83C[\uDFFB-\uDFFF])?(?:\u200D[\u2640\u2642]\uFE0F?)?|\uDE2E(?:\u200D\uD83D\uDCA8)?|\uDE35(?:\u200D\uD83D\uDCAB)?|\uDE36(?:\u200D\uD83C\uDF2B\uFE0F?)?)|\uD83E(?:[\uDD0C\uDD0F\uDD18-\uDD1F\uDD30-\uDD34\uDD36\uDD77\uDDB5\uDDB6\uDDBB\uDDD2\uDDD3\uDDD5\uDEC3-\uDEC5\uDEF0\uDEF2-\uDEF8](?:\uD83C[\uDFFB-\uDFFF])?|[\uDD26\uDD35\uDD37-\uDD39\uDD3D\uDD3E\uDDB8\uDDB9\uDDCD-\uDDCF\uDDD4\uDDD6-\uDDDD](?:\uD83C[\uDFFB-\uDFFF])?(?:\u200D[\u2640\u2642]\uFE0F?)?|[\uDDDE\uDDDF](?:\u200D[\u2640\u2642]\uFE0F?)?|[\uDD0D\uDD0E\uDD10-\uDD17\uDD20-\uDD25\uDD27-\uDD2F\uDD3A\uDD3F-\uDD45\uDD47-\uDD76\uDD78-\uDDB4\uDDB7\uDDBA\uDDBC-\uDDCC\uDDD0\uDDE0-\uDDFF\uDE70-\uDE7C\uDE80-\uDE88\uDE90-\uDEBD\uDEBF-\uDEC2\uDECE-\uDEDB\uDEE0-\uDEE8]|\uDD3C(?:\u200D[\u2640\u2642]\uFE0F?|\uD83C[\uDFFB-\uDFFF])?|\uDDD1(?:\u200D(?:[\u2695\u2696\u2708]\uFE0F?|\uD83C[\uDF3E\uDF73\uDF7C\uDF84\uDF93\uDFA4\uDFA8\uDFEB\uDFED]|\uD83D[\uDCBB\uDCBC\uDD27\uDD2C\uDE80\uDE92]|\uD83E(?:[\uDDAF-\uDDB3\uDDBC\uDDBD]|\uDD1D\u200D\uD83E\uDDD1))|\uD83C(?:\uDFFB(?:\u200D(?:[\u2695\u2696\u2708]\uFE0F?|\u2764\uFE0F?\u200D(?:\uD83D\uDC8B\u200D)?\uD83E\uDDD1\uD83C[\uDFFC-\uDFFF]|\uD83C[\uDF3E\uDF73\uDF7C\uDF84\uDF93\uDFA4\uDFA8\uDFEB\uDFED]|\uD83D[\uDCBB\uDCBC\uDD27\uDD2C\uDE80\uDE92]|\uD83E(?:[\uDDAF-\uDDB3\uDDBC\uDDBD]|\uDD1D\u200D\uD83E\uDDD1\uD83C[\uDFFB-\uDFFF])))?|\uDFFC(?:\u200D(?:[\u2695\u2696\u2708]\uFE0F?|\u2764\uFE0F?\u200D(?:\uD83D\uDC8B\u200D)?\uD83E\uDDD1\uD83C[\uDFFB\uDFFD-\uDFFF]|\uD83C[\uDF3E\uDF73\uDF7C\uDF84\uDF93\uDFA4\uDFA8\uDFEB\uDFED]|\uD83D[\uDCBB\uDCBC\uDD27\uDD2C\uDE80\uDE92]|\uD83E(?:[\uDDAF-\uDDB3\uDDBC\uDDBD]|\uDD1D\u200D\uD83E\uDDD1\uD83C[\uDFFB-\uDFFF])))?|\uDFFD(?:\u200D(?:[\u2695\u2696\u2708]\uFE0F?|\u2764\uFE0F?\u200D(?:\uD83D\uDC8B\u200D)?\uD83E\uDDD1\uD83C[\uDFFB\uDFFC\uDFFE\uDFFF]|\uD83C[\uDF3E\uDF73\uDF7C\uDF84\uDF93\uDFA4\uDFA8\uDFEB\uDFED]|\uD83D[\uDCBB\uDCBC\uDD27\uDD2C\uDE80\uDE92]|\uD83E(?:[\uDDAF-\uDDB3\uDDBC\uDDBD]|\uDD1D\u200D\uD83E\uDDD1\uD83C[\uDFFB-\uDFFF])))?|\uDFFE(?:\u200D(?:[\u2695\u2696\u2708]\uFE0F?|\u2764\uFE0F?\u200D(?:\uD83D\uDC8B\u200D)?\uD83E\uDDD1\uD83C[\uDFFB-\uDFFD\uDFFF]|\uD83C[\uDF3E\uDF73\uDF7C\uDF84\uDF93\uDFA4\uDFA8\uDFEB\uDFED]|\uD83D[\uDCBB\uDCBC\uDD27\uDD2C\uDE80\uDE92]|\uD83E(?:[\uDDAF-\uDDB3\uDDBC\uDDBD]|\uDD1D\u200D\uD83E\uDDD1\uD83C[\uDFFB-\uDFFF])))?|\uDFFF(?:\u200D(?:[\u2695\u2696\u2708]\uFE0F?|\u2764\uFE0F?\u200D(?:\uD83D\uDC8B\u200D)?\uD83E\uDDD1\uD83C[\uDFFB-\uDFFE]|\uD83C[\uDF3E\uDF73\uDF7C\uDF84\uDF93\uDFA4\uDFA8\uDFEB\uDFED]|\uD83D[\uDCBB\uDCBC\uDD27\uDD2C\uDE80\uDE92]|\uD83E(?:[\uDDAF-\uDDB3\uDDBC\uDDBD]|\uDD1D\u200D\uD83E\uDDD1\uD83C[\uDFFB-\uDFFF])))?))?|\uDEF1(?:\uD83C(?:\uDFFB(?:\u200D\uD83E\uDEF2\uD83C[\uDFFC-\uDFFF])?|\uDFFC(?:\u200D\uD83E\uDEF2\uD83C[\uDFFB\uDFFD-\uDFFF])?|\uDFFD(?:\u200D\uD83E\uDEF2\uD83C[\uDFFB\uDFFC\uDFFE\uDFFF])?|\uDFFE(?:\u200D\uD83E\uDEF2\uD83C[\uDFFB-\uDFFD\uDFFF])?|\uDFFF(?:\u200D\uD83E\uDEF2\uD83C[\uDFFB-\uDFFE])?))?)/g;
var zn = { eastAsianWidth(e) {
  var r = e.charCodeAt(0), n = e.length == 2 ? e.charCodeAt(1) : 0, t = r;
  return 55296 <= r && r <= 56319 && 56320 <= n && n <= 57343 && (r &= 1023, n &= 1023, t = r << 10 | n, t += 65536), t == 12288 || 65281 <= t && t <= 65376 || 65504 <= t && t <= 65510 ? "F" : 4352 <= t && t <= 4447 || 4515 <= t && t <= 4519 || 4602 <= t && t <= 4607 || 9001 <= t && t <= 9002 || 11904 <= t && t <= 11929 || 11931 <= t && t <= 12019 || 12032 <= t && t <= 12245 || 12272 <= t && t <= 12283 || 12289 <= t && t <= 12350 || 12353 <= t && t <= 12438 || 12441 <= t && t <= 12543 || 12549 <= t && t <= 12589 || 12593 <= t && t <= 12686 || 12688 <= t && t <= 12730 || 12736 <= t && t <= 12771 || 12784 <= t && t <= 12830 || 12832 <= t && t <= 12871 || 12880 <= t && t <= 13054 || 13056 <= t && t <= 19903 || 19968 <= t && t <= 42124 || 42128 <= t && t <= 42182 || 43360 <= t && t <= 43388 || 44032 <= t && t <= 55203 || 55216 <= t && t <= 55238 || 55243 <= t && t <= 55291 || 63744 <= t && t <= 64255 || 65040 <= t && t <= 65049 || 65072 <= t && t <= 65106 || 65108 <= t && t <= 65126 || 65128 <= t && t <= 65131 || 110592 <= t && t <= 110593 || 127488 <= t && t <= 127490 || 127504 <= t && t <= 127546 || 127552 <= t && t <= 127560 || 127568 <= t && t <= 127569 || 131072 <= t && t <= 194367 || 177984 <= t && t <= 196605 || 196608 <= t && t <= 262141 ? "W" : "N";
} };
var fl = /[^\x20-\x7F]/;
function Dl(e) {
  if (!e)
    return 0;
  if (!fl.test(e))
    return e.length;
  e = e.replace(Rn(), "  ");
  let r = 0;
  for (let n of e) {
    let t = n.codePointAt(0);
    if (t <= 31 || t >= 127 && t <= 159 || t >= 768 && t <= 879)
      continue;
    let a = zn.eastAsianWidth(n);
    r += a === "F" || a === "W" ? 2 : 1;
  }
  return r;
}
var Xe = Dl;
var Ar = "'", Mn = '"';
function pl(e, r) {
  let n = r === true || r === Ar ? Ar : Mn, t = n === Ar ? Mn : Ar, a = 0, i = 0;
  for (let u of e)
    u === n ? a++ : u === t && i++;
  return a > i ? t : n;
}
var Un = pl;
var ue = "string", X = "array", ve2 = "cursor", Q = "indent", Z = "align", ae = "trim", G = "group", H = "fill", W = "if-break", oe = "indent-if-break", se = "line-suffix", ce = "line-suffix-boundary", $ = "line", le = "label", ee = "break-parent", Br = /* @__PURE__ */ new Set([ve2, Q, Z, ae, G, H, W, oe, se, ce, $, le, ee]);
function dl(e) {
  if (typeof e == "string")
    return ue;
  if (Array.isArray(e))
    return X;
  if (!e)
    return;
  let { type: r } = e;
  if (Br.has(r))
    return r;
}
var fe = dl;
var hl = (e) => new Intl.ListFormat("en-US", { type: "disjunction" }).format(e);
function ml(e) {
  let r = e === null ? "null" : typeof e;
  if (r !== "string" && r !== "object")
    return `Unexpected doc '${r}', 
Expected it to be 'string' or 'object'.`;
  if (fe(e))
    throw new Error("doc is valid.");
  let n = Object.prototype.toString.call(e);
  if (n !== "[object Object]")
    return `Unexpected doc '${n}'.`;
  let t = hl([...Br].map((a) => `'${a}'`));
  return `Unexpected doc.type '${e.type}'.
Expected it to be ${t}.`;
}
var ft = class extends Error {
  name = "InvalidDocError";
  constructor(r) {
    super(ml(r)), this.doc = r;
  }
}, Ae = ft;
var Yn = {};
function gl(e, r, n, t) {
  let a = [e];
  for (; a.length > 0; ) {
    let i = a.pop();
    if (i === Yn) {
      n(a.pop());
      continue;
    }
    n && a.push(i, Yn);
    let u = fe(i);
    if (!u)
      throw new Ae(i);
    if ((r == null ? void 0 : r(i)) !== false)
      switch (u) {
        case X:
        case H: {
          let o = u === X ? i : i.parts;
          for (let s = o.length, l = s - 1; l >= 0; --l)
            a.push(o[l]);
          break;
        }
        case W:
          a.push(i.flatContents, i.breakContents);
          break;
        case G:
          if (t && i.expandedStates)
            for (let o = i.expandedStates.length, s = o - 1; s >= 0; --s)
              a.push(i.expandedStates[s]);
          else
            a.push(i.contents);
          break;
        case Z:
        case Q:
        case oe:
        case le:
        case se:
          a.push(i.contents);
          break;
        case ue:
        case ve2:
        case ae:
        case ce:
        case $:
        case ee:
          break;
        default:
          throw new Ae(i);
      }
  }
}
var Vn = gl;
var jn = () => {
}, qr = jn;
function Qe(e) {
  return { type: Q, contents: e };
}
function Fe(e, r) {
  return { type: Z, contents: r, n: e };
}
function Ze(e, r = {}) {
  return qr(r.expandedStates), { type: G, id: r.id, contents: e, break: !!r.shouldBreak, expandedStates: r.expandedStates };
}
function qe(e) {
  return Fe({ type: "root" }, e);
}
function Tr(e) {
  return { type: H, parts: e };
}
function $n(e, r = "", n = {}) {
  return { type: W, breakContents: e, flatContents: r, groupId: n.groupId };
}
var er = { type: ee };
var Ne = { type: $, hard: true }, vl = { type: $, hard: true, literal: true }, Sr = { type: $ }, rr = { type: $, soft: true }, O = [Ne, er], tr = [vl, er];
function nr(e, r) {
  let n = [];
  for (let t = 0; t < r.length; t++)
    t !== 0 && n.push(e), n.push(r[t]);
  return n;
}
var Wn = (e) => {
  if (Array.isArray(e))
    return e;
  if (e.type !== H)
    throw new Error(`Expect doc to be 'array' or '${H}'.`);
  return e.parts;
};
function Kn(e, r) {
  if (typeof e == "string")
    return r(e);
  let n = /* @__PURE__ */ new Map();
  return t(e);
  function t(i) {
    if (n.has(i))
      return n.get(i);
    let u = a(i);
    return n.set(i, u), u;
  }
  function a(i) {
    switch (fe(i)) {
      case X:
        return r(i.map(t));
      case H:
        return r({ ...i, parts: i.parts.map(t) });
      case W:
        return r({ ...i, breakContents: t(i.breakContents), flatContents: t(i.flatContents) });
      case G: {
        let { expandedStates: u, contents: o } = i;
        return u ? (u = u.map(t), o = u[0]) : o = t(o), r({ ...i, contents: o, expandedStates: u });
      }
      case Z:
      case Q:
      case oe:
      case le:
      case se:
        return r({ ...i, contents: t(i.contents) });
      case ue:
      case ve2:
      case ae:
      case ce:
      case $:
      case ee:
        return r(i);
      default:
        throw new Ae(i);
    }
  }
}
function Gn(e) {
  if (e.length > 0) {
    let r = U(false, e, -1);
    !r.expandedStates && !r.break && (r.break = "propagated");
  }
  return null;
}
function Jn(e) {
  let r = /* @__PURE__ */ new Set(), n = [];
  function t(i) {
    if (i.type === ee && Gn(n), i.type === G) {
      if (n.push(i), r.has(i))
        return false;
      r.add(i);
    }
  }
  function a(i) {
    i.type === G && n.pop().break && Gn(n);
  }
  Vn(e, t, a, true);
}
function Hn(e) {
  let r = [], n = e.filter(Boolean);
  for (; n.length > 0; ) {
    let t = n.shift();
    if (t) {
      if (Array.isArray(t)) {
        n.unshift(...t);
        continue;
      }
      if (r.length > 0 && typeof U(false, r, -1) == "string" && typeof t == "string") {
        r[r.length - 1] += t;
        continue;
      }
      r.push(t);
    }
  }
  return r;
}
function Xn(e) {
  return Kn(e, (r) => Array.isArray(r) ? Hn(r) : r.parts ? { ...r, parts: Hn(r.parts) } : r);
}
function Ee(e, r = tr) {
  return Kn(e, (n) => typeof n == "string" ? nr(r, n.split(`
`)) : n);
}
function Qn(e) {
  switch (e) {
    case "cr":
      return "\r";
    case "crlf":
      return `\r
`;
    default:
      return `
`;
  }
}
var V = Symbol("MODE_BREAK"), re = Symbol("MODE_FLAT"), ir = Symbol("cursor");
function Zn() {
  return { value: "", length: 0, queue: [] };
}
function Fl(e, r) {
  return Dt2(e, { type: "indent" }, r);
}
function El(e, r, n) {
  return r === Number.NEGATIVE_INFINITY ? e.root || Zn() : r < 0 ? Dt2(e, { type: "dedent" }, n) : r ? r.type === "root" ? { ...e, root: e } : Dt2(e, { type: typeof r == "string" ? "stringAlign" : "numberAlign", n: r }, n) : e;
}
function Dt2(e, r, n) {
  let t = r.type === "dedent" ? e.queue.slice(0, -1) : [...e.queue, r], a = "", i = 0, u = 0, o = 0;
  for (let p of t)
    switch (p.type) {
      case "indent":
        c(), n.useTabs ? s(1) : l(n.tabWidth);
        break;
      case "stringAlign":
        c(), a += p.n, i += p.n.length;
        break;
      case "numberAlign":
        u += 1, o += p.n;
        break;
      default:
        throw new Error(`Unexpected type '${p.type}'`);
    }
  return D(), { ...e, value: a, length: i, queue: t };
  function s(p) {
    a += "	".repeat(p), i += n.tabWidth * p;
  }
  function l(p) {
    a += " ".repeat(p), i += p;
  }
  function c() {
    n.useTabs ? f() : D();
  }
  function f() {
    u > 0 && s(u), h();
  }
  function D() {
    o > 0 && l(o), h();
  }
  function h() {
    u = 0, o = 0;
  }
}
function pt(e) {
  let r = 0, n = 0, t = e.length;
  e:
    for (; t--; ) {
      let a = e[t];
      if (a === ir) {
        n++;
        continue;
      }
      for (let i = a.length - 1; i >= 0; i--) {
        let u = a[i];
        if (u === " " || u === "	")
          r++;
        else {
          e[t] = a.slice(0, i + 1);
          break e;
        }
      }
    }
  if (r > 0 || n > 0)
    for (e.length = t + 1; n-- > 0; )
      e.push(ir);
  return r;
}
function Pr(e, r, n, t, a, i) {
  if (n === Number.POSITIVE_INFINITY)
    return true;
  let u = r.length, o = [e], s = [];
  for (; n >= 0; ) {
    if (o.length === 0) {
      if (u === 0)
        return true;
      o.push(r[--u]);
      continue;
    }
    let { mode: l, doc: c } = o.pop();
    switch (fe(c)) {
      case ue:
        s.push(c), n -= Xe(c);
        break;
      case X:
      case H: {
        let f = Wn(c);
        for (let D = f.length - 1; D >= 0; D--)
          o.push({ mode: l, doc: f[D] });
        break;
      }
      case Q:
      case Z:
      case oe:
      case le:
        o.push({ mode: l, doc: c.contents });
        break;
      case ae:
        n += pt(s);
        break;
      case G: {
        if (i && c.break)
          return false;
        let f = c.break ? V : l, D = c.expandedStates && f === V ? U(false, c.expandedStates, -1) : c.contents;
        o.push({ mode: f, doc: D });
        break;
      }
      case W: {
        let D = (c.groupId ? a[c.groupId] || re : l) === V ? c.breakContents : c.flatContents;
        D && o.push({ mode: l, doc: D });
        break;
      }
      case $:
        if (l === V || c.hard)
          return true;
        c.soft || (s.push(" "), n--);
        break;
      case se:
        t = true;
        break;
      case ce:
        if (t)
          return false;
        break;
    }
  }
  return false;
}
function ei(e, r) {
  let n = {}, t = r.printWidth, a = Qn(r.endOfLine), i = 0, u = [{ ind: Zn(), mode: V, doc: e }], o = [], s = false, l = [], c = 0;
  for (Jn(e); u.length > 0; ) {
    let { ind: D, mode: h, doc: p } = u.pop();
    switch (fe(p)) {
      case ue: {
        let d = a !== `
` ? R(false, p, `
`, a) : p;
        o.push(d), u.length > 0 && (i += Xe(d));
        break;
      }
      case X:
        for (let d = p.length - 1; d >= 0; d--)
          u.push({ ind: D, mode: h, doc: p[d] });
        break;
      case ve2:
        if (c >= 2)
          throw new Error("There are too many 'cursor' in doc.");
        o.push(ir), c++;
        break;
      case Q:
        u.push({ ind: Fl(D, r), mode: h, doc: p.contents });
        break;
      case Z:
        u.push({ ind: El(D, p.n, r), mode: h, doc: p.contents });
        break;
      case ae:
        i -= pt(o);
        break;
      case G:
        switch (h) {
          case re:
            if (!s) {
              u.push({ ind: D, mode: p.break ? V : re, doc: p.contents });
              break;
            }
          case V: {
            s = false;
            let d = { ind: D, mode: re, doc: p.contents }, m = t - i, g = l.length > 0;
            if (!p.break && Pr(d, u, m, g, n))
              u.push(d);
            else if (p.expandedStates) {
              let x2 = U(false, p.expandedStates, -1);
              if (p.break) {
                u.push({ ind: D, mode: V, doc: x2 });
                break;
              } else
                for (let F = 1; F < p.expandedStates.length + 1; F++)
                  if (F >= p.expandedStates.length) {
                    u.push({ ind: D, mode: V, doc: x2 });
                    break;
                  } else {
                    let B2 = p.expandedStates[F], b2 = { ind: D, mode: re, doc: B2 };
                    if (Pr(b2, u, m, g, n)) {
                      u.push(b2);
                      break;
                    }
                  }
            } else
              u.push({ ind: D, mode: V, doc: p.contents });
            break;
          }
        }
        p.id && (n[p.id] = U(false, u, -1).mode);
        break;
      case H: {
        let d = t - i, { parts: m } = p;
        if (m.length === 0)
          break;
        let [g, x2] = m, F = { ind: D, mode: re, doc: g }, B2 = { ind: D, mode: V, doc: g }, b2 = Pr(F, [], d, l.length > 0, n, true);
        if (m.length === 1) {
          b2 ? u.push(F) : u.push(B2);
          break;
        }
        let v2 = { ind: D, mode: re, doc: x2 }, y2 = { ind: D, mode: V, doc: x2 };
        if (m.length === 2) {
          b2 ? u.push(v2, F) : u.push(y2, B2);
          break;
        }
        m.splice(0, 2);
        let k2 = { ind: D, mode: h, doc: Tr(m) }, E = m[0];
        Pr({ ind: D, mode: re, doc: [g, x2, E] }, [], d, l.length > 0, n, true) ? u.push(k2, v2, F) : b2 ? u.push(k2, y2, F) : u.push(k2, y2, B2);
        break;
      }
      case W:
      case oe: {
        let d = p.groupId ? n[p.groupId] : h;
        if (d === V) {
          let m = p.type === W ? p.breakContents : p.negate ? p.contents : Qe(p.contents);
          m && u.push({ ind: D, mode: h, doc: m });
        }
        if (d === re) {
          let m = p.type === W ? p.flatContents : p.negate ? Qe(p.contents) : p.contents;
          m && u.push({ ind: D, mode: h, doc: m });
        }
        break;
      }
      case se:
        l.push({ ind: D, mode: h, doc: p.contents });
        break;
      case ce:
        l.length > 0 && u.push({ ind: D, mode: h, doc: Ne });
        break;
      case $:
        switch (h) {
          case re:
            if (p.hard)
              s = true;
            else {
              p.soft || (o.push(" "), i += 1);
              break;
            }
          case V:
            if (l.length > 0) {
              u.push({ ind: D, mode: h, doc: p }, ...l.reverse()), l.length = 0;
              break;
            }
            p.literal ? D.root ? (o.push(a, D.root.value), i = D.root.length) : (o.push(a), i = 0) : (i -= pt(o), o.push(a + D.value), i = D.length);
            break;
        }
        break;
      case le:
        u.push({ ind: D, mode: h, doc: p.contents });
        break;
      case ee:
        break;
      default:
        throw new Ae(p);
    }
    u.length === 0 && l.length > 0 && (u.push(...l.reverse()), l.length = 0);
  }
  let f = o.indexOf(ir);
  if (f !== -1) {
    let D = o.indexOf(ir, f + 1), h = o.slice(0, f).join(""), p = o.slice(f + 1, D).join(""), d = o.slice(D + 1).join("");
    return { formatted: h + p + d, cursorNodeStart: h.length, cursorNodeText: p };
  }
  return { formatted: o.join("") };
}
var dt = class extends Error {
  name = "UnexpectedNodeError";
  constructor(r, n, t = "type") {
    super(`Unexpected ${n} node ${t}: ${JSON.stringify(r[t])}.`), this.node = r;
  }
}, ri = dt;
async function Cl(e, r) {
  if (e.lang === "yaml") {
    let n = e.value.trim(), t = n ? await r(n, { parser: "yaml" }) : "";
    return qe([e.startDelimiter, O, t, t ? O : "", e.endDelimiter]);
  }
}
var ti = Cl;
var bl = (e) => e.split(/[/\\]/).pop();
function ni(e, r) {
  if (!r)
    return;
  let n = bl(r).toLowerCase();
  return e.find((t) => {
    var a, i;
    return ((a = t.extensions) == null ? void 0 : a.some((u) => n.endsWith(u))) || ((i = t.filenames) == null ? void 0 : i.some((u) => u.toLowerCase() === n));
  });
}
function xl(e, r) {
  if (r)
    return e.find(({ name: n }) => n.toLowerCase() === r) ?? e.find(({ aliases: n }) => n == null ? void 0 : n.includes(r)) ?? e.find(({ extensions: n }) => n == null ? void 0 : n.includes(`.${r}`));
}
function yl(e, r) {
  let n = e.plugins.flatMap((a) => a.languages ?? []), t = xl(n, r.language) ?? ni(n, r.physicalFile) ?? ni(n, r.file) ?? (r.physicalFile, void 0);
  return t == null ? void 0 : t.parsers[0];
}
var ii = yl;
var kl = new Proxy(() => {
}, { get: () => kl });
function Te(e) {
  return e.position.start.offset;
}
function Se2(e) {
  return e.position.end.offset;
}
var ui = "(?:[\\u02ea-\\u02eb\\u1100-\\u11ff\\u2e80-\\u2e99\\u2e9b-\\u2ef3\\u2f00-\\u2fd5\\u2ff0-\\u303f\\u3041-\\u3096\\u3099-\\u309f\\u30a1-\\u30fa\\u30fc-\\u30ff\\u3105-\\u312f\\u3131-\\u318e\\u3190-\\u3191\\u3196-\\u31e3\\u31f0-\\u321e\\u322a-\\u3247\\u3260-\\u327e\\u328a-\\u32b0\\u32c0-\\u32cb\\u32d0-\\u3370\\u337b-\\u337f\\u33e0-\\u33fe\\u3400-\\u4dbf\\u4e00-\\u9fff\\ua700-\\ua707\\ua960-\\ua97c\\uac00-\\ud7a3\\ud7b0-\\ud7c6\\ud7cb-\\ud7fb\\uf900-\\ufa6d\\ufa70-\\ufad9\\ufe10-\\ufe1f\\ufe30-\\ufe6f\\uff00-\\uffef]|[\\ud840-\\ud868\\ud86a-\\ud86c\\ud86f-\\ud872\\ud874-\\ud879\\ud880-\\ud883\\ud885-\\ud887][\\udc00-\\udfff]|\\ud81b[\\udfe3]|\\ud82b[\\udff0-\\udff3\\udff5-\\udffb\\udffd-\\udffe]|\\ud82c[\\udc00-\\udd22\\udd32\\udd50-\\udd52\\udd55\\udd64-\\udd67]|\\ud83c[\\ude00\\ude50-\\ude51]|\\ud869[\\udc00-\\udedf\\udf00-\\udfff]|\\ud86d[\\udc00-\\udf39\\udf40-\\udfff]|\\ud86e[\\udc00-\\udc1d\\udc20-\\udfff]|\\ud873[\\udc00-\\udea1\\udeb0-\\udfff]|\\ud87a[\\udc00-\\udfe0]|\\ud87e[\\udc00-\\ude1d]|\\ud884[\\udc00-\\udf4a\\udf50-\\udfff]|\\ud888[\\udc00-\\udfaf])(?:[\\ufe00-\\ufe0f]|\\udb40[\\udd00-\\uddef])?", ai = new RegExp("[\\u1100-\\u11ff\\u3001-\\u3003\\u3008-\\u3011\\u3013-\\u301f\\u302e-\\u3030\\u3037\\u30fb\\u3131-\\u318e\\u3200-\\u321e\\u3260-\\u327e\\ua960-\\ua97c\\uac00-\\ud7a3\\ud7b0-\\ud7c6\\ud7cb-\\ud7fb\\ufe45-\\ufe46\\uff61-\\uff65\\uffa0-\\uffbe\\uffc2-\\uffc7\\uffca-\\uffcf\\uffd2-\\uffd7\\uffda-\\uffdc]", ""), ur = "[\\u0021-\\u002f\\u003a-\\u0040\\u005b-\\u0060\\u007b-\\u007e\\u00a1\\u00a7\\u00ab\\u00b6-\\u00b7\\u00bb\\u00bf\\u037e\\u0387\\u055a-\\u055f\\u0589-\\u058a\\u05be\\u05c0\\u05c3\\u05c6\\u05f3-\\u05f4\\u0609-\\u060a\\u060c-\\u060d\\u061b\\u061d-\\u061f\\u066a-\\u066d\\u06d4\\u0700-\\u070d\\u07f7-\\u07f9\\u0830-\\u083e\\u085e\\u0964-\\u0965\\u0970\\u09fd\\u0a76\\u0af0\\u0c77\\u0c84\\u0df4\\u0e4f\\u0e5a-\\u0e5b\\u0f04-\\u0f12\\u0f14\\u0f3a-\\u0f3d\\u0f85\\u0fd0-\\u0fd4\\u0fd9-\\u0fda\\u104a-\\u104f\\u10fb\\u1360-\\u1368\\u1400\\u166e\\u169b-\\u169c\\u16eb-\\u16ed\\u1735-\\u1736\\u17d4-\\u17d6\\u17d8-\\u17da\\u1800-\\u180a\\u1944-\\u1945\\u1a1e-\\u1a1f\\u1aa0-\\u1aa6\\u1aa8-\\u1aad\\u1b5a-\\u1b60\\u1b7d-\\u1b7e\\u1bfc-\\u1bff\\u1c3b-\\u1c3f\\u1c7e-\\u1c7f\\u1cc0-\\u1cc7\\u1cd3\\u2010-\\u2027\\u2030-\\u2043\\u2045-\\u2051\\u2053-\\u205e\\u207d-\\u207e\\u208d-\\u208e\\u2308-\\u230b\\u2329-\\u232a\\u2768-\\u2775\\u27c5-\\u27c6\\u27e6-\\u27ef\\u2983-\\u2998\\u29d8-\\u29db\\u29fc-\\u29fd\\u2cf9-\\u2cfc\\u2cfe-\\u2cff\\u2d70\\u2e00-\\u2e2e\\u2e30-\\u2e4f\\u2e52-\\u2e5d\\u3001-\\u3003\\u3008-\\u3011\\u3014-\\u301f\\u3030\\u303d\\u30a0\\u30fb\\ua4fe-\\ua4ff\\ua60d-\\ua60f\\ua673\\ua67e\\ua6f2-\\ua6f7\\ua874-\\ua877\\ua8ce-\\ua8cf\\ua8f8-\\ua8fa\\ua8fc\\ua92e-\\ua92f\\ua95f\\ua9c1-\\ua9cd\\ua9de-\\ua9df\\uaa5c-\\uaa5f\\uaade-\\uaadf\\uaaf0-\\uaaf1\\uabeb\\ufd3e-\\ufd3f\\ufe10-\\ufe19\\ufe30-\\ufe52\\ufe54-\\ufe61\\ufe63\\ufe68\\ufe6a-\\ufe6b\\uff01-\\uff03\\uff05-\\uff0a\\uff0c-\\uff0f\\uff1a-\\uff1b\\uff1f-\\uff20\\uff3b-\\uff3d\\uff3f\\uff5b\\uff5d\\uff5f-\\uff65]|\\ud800[\\udd00-\\udd02\\udf9f\\udfd0]|\\ud801[\\udd6f]|\\ud802[\\udc57\\udd1f\\udd3f\\ude50-\\ude58\\ude7f\\udef0-\\udef6\\udf39-\\udf3f\\udf99-\\udf9c]|\\ud803[\\udead\\udf55-\\udf59\\udf86-\\udf89]|\\ud804[\\udc47-\\udc4d\\udcbb-\\udcbc\\udcbe-\\udcc1\\udd40-\\udd43\\udd74-\\udd75\\uddc5-\\uddc8\\uddcd\\udddb\\udddd-\\udddf\\ude38-\\ude3d\\udea9]|\\ud805[\\udc4b-\\udc4f\\udc5a-\\udc5b\\udc5d\\udcc6\\uddc1-\\uddd7\\ude41-\\ude43\\ude60-\\ude6c\\udeb9\\udf3c-\\udf3e]|\\ud806[\\udc3b\\udd44-\\udd46\\udde2\\ude3f-\\ude46\\ude9a-\\ude9c\\ude9e-\\udea2\\udf00-\\udf09]|\\ud807[\\udc41-\\udc45\\udc70-\\udc71\\udef7-\\udef8\\udf43-\\udf4f\\udfff]|\\ud809[\\udc70-\\udc74]|\\ud80b[\\udff1-\\udff2]|\\ud81a[\\ude6e-\\ude6f\\udef5\\udf37-\\udf3b\\udf44]|\\ud81b[\\ude97-\\ude9a\\udfe2]|\\ud82f[\\udc9f]|\\ud836[\\ude87-\\ude8b]|\\ud83a[\\udd5e-\\udd5f]";
var mt = /* @__PURE__ */ new Set(["liquidNode", "inlineCode", "emphasis", "esComment", "strong", "delete", "wikiLink", "link", "linkReference", "image", "imageReference", "footnote", "footnoteReference", "sentence", "whitespace", "word", "break", "inlineMath"]), _r = /* @__PURE__ */ new Set([...mt, "tableCell", "paragraph", "heading"]), ht = new RegExp(ur), Pe = "non-cjk", De = "cj-letter", Ce = "k-letter", ar = "cjk-punctuation";
function Lr(e) {
  let r = [], n = e.split(/([\t\n ]+)/);
  for (let [a, i] of n.entries()) {
    if (a % 2 === 1) {
      r.push({ type: "whitespace", value: /\n/.test(i) ? `
` : " " });
      continue;
    }
    if ((a === 0 || a === n.length - 1) && i === "")
      continue;
    let u = i.split(new RegExp(`(${ui})`));
    for (let [o, s] of u.entries())
      if (!((o === 0 || o === u.length - 1) && s === "")) {
        if (o % 2 === 0) {
          s !== "" && t({ type: "word", value: s, kind: Pe, hasLeadingPunctuation: ht.test(s[0]), hasTrailingPunctuation: ht.test(U(false, s, -1)) });
          continue;
        }
        t(ht.test(s) ? { type: "word", value: s, kind: ar, hasLeadingPunctuation: true, hasTrailingPunctuation: true } : { type: "word", value: s, kind: ai.test(s) ? Ce : De, hasLeadingPunctuation: false, hasTrailingPunctuation: false });
      }
  }
  return r;
  function t(a) {
    let i = U(false, r, -1);
    (i == null ? void 0 : i.type) === "word" && !u(Pe, ar) && ![i.value, a.value].some((o) => /\u3000/.test(o)) && r.push({ type: "whitespace", value: "" }), r.push(a);
    function u(o, s) {
      return i.kind === o && a.kind === s || i.kind === s && a.kind === o;
    }
  }
}
function Re(e, r) {
  let [, n, t, a] = r.slice(e.position.start.offset, e.position.end.offset).match(/^\s*(\d+)(\.|\))(\s*)/);
  return { numberText: n, marker: t, leadingSpaces: a };
}
function oi(e, r) {
  if (!e.ordered || e.children.length < 2)
    return false;
  let n = Number(Re(e.children[0], r.originalText).numberText), t = Number(Re(e.children[1], r.originalText).numberText);
  if (n === 0 && e.children.length > 2) {
    let a = Number(Re(e.children[2], r.originalText).numberText);
    return t === 1 && a === 1;
  }
  return t === 1;
}
function Or(e, r) {
  let { value: n } = e;
  return e.position.end.offset === r.length && n.endsWith(`
`) && r.endsWith(`
`) ? n.slice(0, -1) : n;
}
function be(e, r) {
  return function n(t, a, i) {
    let u = { ...r(t, a, i) };
    return u.children && (u.children = u.children.map((o, s) => n(o, s, [u, ...i]))), u;
  }(e, null, []);
}
function gt(e) {
  if ((e == null ? void 0 : e.type) !== "link" || e.children.length !== 1)
    return false;
  let [r] = e.children;
  return Te(e) === Te(r) && Se2(e) === Se2(r);
}
function wl(e, r) {
  let { node: n } = e;
  if (n.type === "code" && n.lang !== null) {
    let t = ii(r, { language: n.lang });
    if (t)
      return async (a) => {
        let i = r.__inJsTemplate ? "~" : "`", u = i.repeat(Math.max(3, wr(n.value, i) + 1)), o = { parser: t };
        n.lang === "tsx" && (o.filepath = "dummy.tsx");
        let s = await a(Or(n, r.originalText), o);
        return qe([u, n.lang, n.meta ? " " + n.meta : "", O, Ee(s), O, u]);
      };
  }
  switch (n.type) {
    case "front-matter":
      return (t) => ti(n, t);
    case "import":
    case "export":
      return (t) => t(n.value, { parser: "babel" });
    case "jsx":
      return (t) => t(`<$>${n.value}</$>`, { parser: "__js_expression", rootMarker: "mdx" });
  }
  return null;
}
var si = wl;
var Al = new RegExp("^(?<startDelimiter>-{3}|\\+{3})(?<language>[^\\n]*)\\n(?:|(?<value>.*?)\\n)(?<endDelimiter>\\k<startDelimiter>|\\.{3})[^\\S\\n]*(?:\\n|$)", "s");
function Bl(e) {
  let r = e.match(Al);
  if (!r)
    return { content: e };
  let { startDelimiter: n, language: t, value: a = "", endDelimiter: i } = r.groups, u = t.trim() || "yaml";
  if (n === "+++" && (u = "toml"), u !== "yaml" && n !== i)
    return { content: e };
  let [o] = r;
  return { frontMatter: { type: "front-matter", lang: u, value: a, startDelimiter: n, endDelimiter: i, raw: o.replace(/\n$/, "") }, content: R(false, o, /[^\n]/g, " ") + e.slice(o.length) };
}
var or = Bl;
var ci = ["format", "prettier"];
function vt(e) {
  let r = `@(${ci.join("|")})`, n = new RegExp([`<!--\\s*${r}\\s*-->`, `{\\s*\\/\\*\\s*${r}\\s*\\*\\/\\s*}`, `<!--.*\r?
[\\s\\S]*(^|
)[^\\S
]*${r}[^\\S
]*($|
)[\\s\\S]*
.*-->`].join("|"), "m"), t = e.match(n);
  return (t == null ? void 0 : t.index) === 0;
}
var li = (e) => vt(or(e).content.trimStart()), fi = (e) => {
  let r = or(e), n = `<!-- @${ci[0]} -->`;
  return r.frontMatter ? `${r.frontMatter.raw}

${n}

${r.content}` : `${n}

${r.content}`;
};
var ql = /^.$/su;
function Tl(e, r) {
  return e = Sl(e, r), e = _l(e), e = Ol(e, r), e = Il(e, r), e = Ll(e), e;
}
function Sl(e, r) {
  return be(e, (n) => n.type !== "text" || n.value === "*" || n.value === "_" || !ql.test(n.value) || n.position.end.offset - n.position.start.offset === n.value.length ? n : { ...n, value: r.originalText.slice(n.position.start.offset, n.position.end.offset) });
}
function Pl(e, r, n) {
  return be(e, (t) => {
    if (!t.children)
      return t;
    let a = t.children.reduce((i, u) => {
      let o = U(false, i, -1);
      return o && r(o, u) ? i.splice(-1, 1, n(o, u)) : i.push(u), i;
    }, []);
    return { ...t, children: a };
  });
}
function _l(e) {
  return Pl(e, (r, n) => r.type === "text" && n.type === "text", (r, n) => ({ type: "text", value: r.value + n.value, position: { start: r.position.start, end: n.position.end } }));
}
function Ll(e) {
  return be(e, (r, n, [t]) => {
    if (r.type !== "text")
      return r;
    let { value: a } = r;
    return t.type === "paragraph" && (n === 0 && (a = a.trimStart()), n === t.children.length - 1 && (a = a.trimEnd())), { type: "sentence", position: r.position, children: Lr(a) };
  });
}
function Ol(e, r) {
  return be(e, (n, t, a) => {
    if (n.type === "code") {
      let i = /^\n?(?: {4,}|\t)/.test(r.originalText.slice(n.position.start.offset, n.position.end.offset));
      if (n.isIndented = i, i)
        for (let u = 0; u < a.length; u++) {
          let o = a[u];
          if (o.hasIndentedCodeblock)
            break;
          o.type === "list" && (o.hasIndentedCodeblock = true);
        }
    }
    return n;
  });
}
function Il(e, r) {
  return be(e, (a, i, u) => {
    if (a.type === "list" && a.children.length > 0) {
      for (let o = 0; o < u.length; o++) {
        let s = u[o];
        if (s.type === "list" && !s.isAligned)
          return a.isAligned = false, a;
      }
      a.isAligned = t(a);
    }
    return a;
  });
  function n(a) {
    return a.children.length === 0 ? -1 : a.children[0].position.start.column - 1;
  }
  function t(a) {
    if (!a.ordered)
      return true;
    let [i, u] = a.children;
    if (Re(i, r.originalText).leadingSpaces.length > 1)
      return true;
    let s = n(i);
    if (s === -1)
      return false;
    if (a.children.length === 1)
      return s % r.tabWidth === 0;
    let l = n(u);
    return s !== l ? false : s % r.tabWidth === 0 ? true : Re(u, r.originalText).leadingSpaces.length > 1;
  }
}
var Di = Tl;
var di = Ie(kr(), 1);
function Nl(e) {
  return (e == null ? void 0 : e.type) === "front-matter";
}
var pi = Nl;
var Rl = /* @__PURE__ */ new Set(["position", "raw"]);
function hi(e, r, n) {
  if ((e.type === "front-matter" || e.type === "code" || e.type === "yaml" || e.type === "import" || e.type === "export" || e.type === "jsx") && delete r.value, e.type === "list" && delete r.isAligned, (e.type === "list" || e.type === "listItem") && delete r.spread, e.type === "text" || (e.type === "inlineCode" && (r.value = R(false, e.value, `
`, " ")), e.type === "wikiLink" && (r.value = R(false, e.value.trim(), /[\t\n]+/g, " ")), (e.type === "definition" || e.type === "linkReference" || e.type === "imageReference") && (r.label = (0, di.default)(e.label)), (e.type === "definition" || e.type === "link" || e.type === "image") && e.title && (r.title = R(false, e.title, /\\(?=["')])/g, "")), (n == null ? void 0 : n.type) === "root" && n.children.length > 0 && (n.children[0] === e || pi(n.children[0]) && n.children[1] === e) && e.type === "html" && vt(e.value)))
    return null;
}
hi.ignoredProperties = Rl;
var mi = hi;
var sr = null;
function cr(e) {
  if (sr !== null && typeof sr.property) {
    let r = sr;
    return sr = cr.prototype = null, r;
  }
  return sr = cr.prototype = e ?? /* @__PURE__ */ Object.create(null), new cr();
}
var zl = 10;
for (let e = 0; e <= zl; e++)
  cr();
function Ft(e) {
  return cr(e);
}
function Ml(e, r = "type") {
  Ft(e);
  function n(t) {
    let a = t[r], i = e[a];
    if (!Array.isArray(i))
      throw Object.assign(new Error(`Missing visitor keys for '${a}'.`), { node: t });
    return i;
  }
  return n;
}
var gi = Ml;
var Ul = { "front-matter": [], root: ["children"], paragraph: ["children"], sentence: ["children"], word: [], whitespace: [], emphasis: ["children"], strong: ["children"], delete: ["children"], inlineCode: [], wikiLink: [], link: ["children"], image: [], blockquote: ["children"], heading: ["children"], code: [], html: [], list: ["children"], thematicBreak: [], linkReference: ["children"], imageReference: [], definition: [], footnote: ["children"], footnoteReference: [], footnoteDefinition: ["children"], table: ["children"], tableCell: ["children"], break: [], liquidNode: [], import: [], export: [], esComment: [], jsx: [], math: [], inlineMath: [], tableRow: ["children"], listItem: ["children"], text: [] }, vi = Ul;
var Yl = gi(vi), Fi = Yl;
var Vl = /* @__PURE__ */ new Set(["heading", "tableCell", "link", "wikiLink"]), jl = new Set(`$(£¥·'"〈《「『【〔〖〝﹙﹛＄（［｛￡￥[{‵︴︵︷︹︻︽︿﹁﹃﹏〘｟«`), $l = new Set(`!%),.:;?]}¢°·'"†‡›℃∶、。〃〆〕〗〞﹚﹜！＂％＇），．：；？］｝～–—•〉》」︰︱︲︳﹐﹑﹒﹓﹔﹕﹖﹘︶︸︺︼︾﹀﹂﹗｜､』】〙〟｠»ヽヾーァィゥェォッャュョヮヵヶぁぃぅぇぉっゃゅょゎゕゖㇰㇱㇲㇳㇴㇵㇶㇷㇸㇹㇺㇻㇼㇽㇾㇿ々〻‐゠〜～‼⁇⁈⁉・`), Ei = new Set("!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~");
function Gl({ parent: e }) {
  if (e.usesCJSpaces === void 0) {
    let r = { " ": 0, "": 0 }, { children: n } = e;
    for (let t = 1; t < n.length - 1; ++t) {
      let a = n[t];
      if (a.type === "whitespace" && (a.value === " " || a.value === "")) {
        let i = n[t - 1].kind, u = n[t + 1].kind;
        (i === De && u === Pe || i === Pe && u === De) && ++r[a.value];
      }
    }
    e.usesCJSpaces = r[" "] > r[""];
  }
  return e.usesCJSpaces;
}
function Hl(e, r) {
  if (r)
    return true;
  let { previous: n, next: t } = e;
  if (!n || !t)
    return true;
  let a = n.kind, i = t.kind;
  return bi(a) && bi(i) || a === Ce && i === De || i === Ce && a === De ? true : a === ar || i === ar || a === De && i === De ? false : Ei.has(t.value[0]) || Ei.has(U(false, n.value, -1)) ? true : n.hasTrailingPunctuation || t.hasLeadingPunctuation ? false : Gl(e);
}
function Ci(e) {
  return e === Pe || e === De || e === Ce;
}
function bi(e) {
  return e === Pe || e === Ce;
}
function Wl(e, r, n, t, a) {
  if (n !== "always" || e.hasAncestor((s) => Vl.has(s.type)))
    return false;
  if (t)
    return r !== "";
  if (r === " ")
    return true;
  let { previous: i, next: u } = e;
  return !(r === "" && ((i == null ? void 0 : i.kind) === Ce && Ci(u == null ? void 0 : u.kind) || (u == null ? void 0 : u.kind) === Ce && Ci(i == null ? void 0 : i.kind)) || !a && (u && $l.has(u.value[0]) || i && jl.has(U(false, i.value, -1))));
}
function Et(e, r, n, t) {
  if (n === "preserve" && r === `
`)
    return O;
  let a = r === " " || r === `
` && Hl(e, t);
  return Wl(e, r, n, t, a) ? a ? Sr : rr : a ? " " : "";
}
var Kl = /* @__PURE__ */ new Set(["listItem", "definition", "footnoteDefinition"]);
function Jl(e, r, n) {
  var a, i, u;
  let { node: t } = e;
  if (nf(e))
    return Lr(r.originalText.slice(t.position.start.offset, t.position.end.offset)).map((o) => o.type === "word" ? o.value : Et(e, o.value, r.proseWrap, true));
  switch (t.type) {
    case "front-matter":
      return r.originalText.slice(t.position.start.offset, t.position.end.offset);
    case "root":
      return t.children.length === 0 ? "" : [Xn(ef(e, r, n)), O];
    case "paragraph":
      return Y(e, r, n, { postprocessor: Tr });
    case "sentence":
      return Y(e, r, n);
    case "word": {
      let o = R(false, R(false, t.value, "*", "\\*"), new RegExp([`(^|${ur})(_+)`, `(_+)(${ur}|$)`].join("|"), "g"), (c, f, D, h, p) => R(false, D ? `${f}${D}` : `${h}${p}`, "_", "\\_")), s = (c, f, D) => c.type === "sentence" && D === 0, l = (c, f, D) => gt(c.children[D - 1]);
      return o !== t.value && (e.match(void 0, s, l) || e.match(void 0, s, (c, f, D) => c.type === "emphasis" && D === 0, l)) && (o = o.replace(/^(\\?[*_])+/, (c) => R(false, c, "\\", ""))), o;
    }
    case "whitespace": {
      let { next: o } = e, s = o && /^>|^(?:[*+-]|#{1,6}|\d+[).])$/.test(o.value) ? "never" : r.proseWrap;
      return Et(e, t.value, s);
    }
    case "emphasis": {
      let o;
      if (gt(t.children[0]))
        o = r.originalText[t.position.start.offset];
      else {
        let { previous: s, next: l } = e;
        o = (s == null ? void 0 : s.type) === "sentence" && ((a = U(false, s.children, -1)) == null ? void 0 : a.type) === "word" && !U(false, s.children, -1).hasTrailingPunctuation || (l == null ? void 0 : l.type) === "sentence" && ((i = l.children[0]) == null ? void 0 : i.type) === "word" && !l.children[0].hasLeadingPunctuation || e.hasAncestor((f) => f.type === "emphasis") ? "*" : "_";
      }
      return [o, Y(e, r, n), o];
    }
    case "strong":
      return ["**", Y(e, r, n), "**"];
    case "delete":
      return ["~~", Y(e, r, n), "~~"];
    case "inlineCode": {
      let o = r.proseWrap === "preserve" ? t.value : R(false, t.value, `
`, " "), s = Nn(o, "`"), l = "`".repeat(s || 1), c = o.startsWith("`") || o.endsWith("`") || /^[\n ]/.test(o) && /[\n ]$/.test(o) && /[^\n ]/.test(o) ? " " : "";
      return [l, c, o, c, l];
    }
    case "wikiLink": {
      let o = "";
      return r.proseWrap === "preserve" ? o = t.value : o = R(false, t.value, /[\t\n]+/g, " "), ["[[", o, "]]"];
    }
    case "link":
      switch (r.originalText[t.position.start.offset]) {
        case "<": {
          let o = "mailto:";
          return ["<", t.url.startsWith(o) && r.originalText.slice(t.position.start.offset + 1, t.position.start.offset + 1 + o.length) !== o ? t.url.slice(o.length) : t.url, ">"];
        }
        case "[":
          return ["[", Y(e, r, n), "](", Ct(t.url, ")"), Ir(t.title, r), ")"];
        default:
          return r.originalText.slice(t.position.start.offset, t.position.end.offset);
      }
    case "image":
      return ["![", t.alt || "", "](", Ct(t.url, ")"), Ir(t.title, r), ")"];
    case "blockquote":
      return ["> ", Fe("> ", Y(e, r, n))];
    case "heading":
      return ["#".repeat(t.depth) + " ", Y(e, r, n)];
    case "code": {
      if (t.isIndented) {
        let l = " ".repeat(4);
        return Fe(l, [l, Ee(t.value, O)]);
      }
      let o = r.__inJsTemplate ? "~" : "`", s = o.repeat(Math.max(3, wr(t.value, o) + 1));
      return [s, t.lang || "", t.meta ? " " + t.meta : "", O, Ee(Or(t, r.originalText), O), O, s];
    }
    case "html": {
      let { parent: o, isLast: s } = e, l = o.type === "root" && s ? t.value.trimEnd() : t.value, c = /^<!--.*-->$/s.test(l);
      return Ee(l, c ? O : qe(tr));
    }
    case "list": {
      let o = yi(t, e.parent), s = oi(t, r);
      return Y(e, r, n, { processor(l) {
        let c = D(), f = l.node;
        if (f.children.length === 2 && f.children[1].type === "html" && f.children[0].position.start.column !== f.children[1].position.start.column)
          return [c, xi(l, r, n, c)];
        return [c, Fe(" ".repeat(c.length), xi(l, r, n, c))];
        function D() {
          let h = t.ordered ? (l.isFirst ? t.start : s ? 1 : t.start + l.index) + (o % 2 === 0 ? ". " : ") ") : o % 2 === 0 ? "- " : "* ";
          return t.isAligned || t.hasIndentedCodeblock ? Xl(h, r) : h;
        }
      } });
    }
    case "thematicBreak": {
      let { ancestors: o } = e, s = o.findIndex((c) => c.type === "list");
      return s === -1 ? "---" : yi(o[s], o[s + 1]) % 2 === 0 ? "***" : "---";
    }
    case "linkReference":
      return ["[", Y(e, r, n), "]", t.referenceType === "full" ? bt(t) : t.referenceType === "collapsed" ? "[]" : ""];
    case "imageReference":
      switch (t.referenceType) {
        case "full":
          return ["![", t.alt || "", "]", bt(t)];
        default:
          return ["![", t.alt, "]", t.referenceType === "collapsed" ? "[]" : ""];
      }
    case "definition": {
      let o = r.proseWrap === "always" ? Sr : " ";
      return Ze([bt(t), ":", Qe([o, Ct(t.url), t.title === null ? "" : [o, Ir(t.title, r, false)]])]);
    }
    case "footnote":
      return ["[^", Y(e, r, n), "]"];
    case "footnoteReference":
      return Bi(t);
    case "footnoteDefinition": {
      let o = t.children.length === 1 && t.children[0].type === "paragraph" && (r.proseWrap === "never" || r.proseWrap === "preserve" && t.children[0].position.start.line === t.children[0].position.end.line);
      return [Bi(t), ": ", o ? Y(e, r, n) : Ze([Fe(" ".repeat(4), Y(e, r, n, { processor: ({ isFirst: s }) => s ? Ze([rr, n()]) : n() })), ((u = e.next) == null ? void 0 : u.type) === "footnoteDefinition" ? rr : ""])];
    }
    case "table":
      return Zl(e, r, n);
    case "tableCell":
      return Y(e, r, n);
    case "break":
      return /\s/.test(r.originalText[t.position.start.offset]) ? ["  ", qe(tr)] : ["\\", O];
    case "liquidNode":
      return Ee(t.value, O);
    case "import":
    case "export":
    case "jsx":
      return t.value;
    case "esComment":
      return ["{/* ", t.value, " */}"];
    case "math":
      return ["$$", O, t.value ? [Ee(t.value, O), O] : "", "$$"];
    case "inlineMath":
      return r.originalText.slice(Te(t), Se2(t));
    case "tableRow":
    case "listItem":
    case "text":
    default:
      throw new ri(t, "Markdown");
  }
}
function xi(e, r, n, t) {
  let { node: a } = e, i = a.checked === null ? "" : a.checked ? "[x] " : "[ ] ";
  return [i, Y(e, r, n, { processor({ node: u, isFirst: o }) {
    if (o && u.type !== "list")
      return Fe(" ".repeat(i.length), n());
    let s = " ".repeat(uf(r.tabWidth - t.length, 0, 3));
    return [s, Fe(s, n())];
  } })];
}
function Xl(e, r) {
  let n = t();
  return e + " ".repeat(n >= 4 ? 0 : n);
  function t() {
    let a = e.length % r.tabWidth;
    return a === 0 ? 0 : r.tabWidth - a;
  }
}
function yi(e, r) {
  return Ql(e, r, (n) => n.ordered === e.ordered);
}
function Ql(e, r, n) {
  let t = -1;
  for (let a of r.children)
    if (a.type === e.type && n(a) ? t++ : t = -1, a === e)
      return t;
}
function Zl(e, r, n) {
  let { node: t } = e, a = [], i = e.map(() => e.map(({ index: f }) => {
    let D = ei(n(), r).formatted, h = Xe(D);
    return a[f] = Math.max(a[f] || 3, h), { text: D, width: h };
  }, "children"), "children"), u = s(false);
  if (r.proseWrap !== "never")
    return [er, u];
  let o = s(true);
  return [er, Ze($n(o, u))];
  function s(f) {
    let D = [c(i[0], f), l(f)];
    return i.length > 1 && D.push(nr(Ne, i.slice(1).map((h) => c(h, f)))), nr(Ne, D);
  }
  function l(f) {
    return `| ${a.map((h, p) => {
      let d = t.align[p], m = d === "center" || d === "left" ? ":" : "-", g = d === "center" || d === "right" ? ":" : "-", x2 = f ? "-" : "-".repeat(h - 2);
      return `${m}${x2}${g}`;
    }).join(" | ")} |`;
  }
  function c(f, D) {
    return `| ${f.map(({ text: p, width: d }, m) => {
      if (D)
        return p;
      let g = a[m] - d, x2 = t.align[m], F = 0;
      x2 === "right" ? F = g : x2 === "center" && (F = Math.floor(g / 2));
      let B2 = g - F;
      return `${" ".repeat(F)}${p}${" ".repeat(B2)}`;
    }).join(" | ")} |`;
  }
}
function ef(e, r, n) {
  let t = [], a = null, { children: i } = e.node;
  for (let [u, o] of i.entries())
    switch (xt(o)) {
      case "start":
        a === null && (a = { index: u, offset: o.position.end.offset });
        break;
      case "end":
        a !== null && (t.push({ start: a, end: { index: u, offset: o.position.start.offset } }), a = null);
        break;
    }
  return Y(e, r, n, { processor({ index: u }) {
    if (t.length > 0) {
      let o = t[0];
      if (u === o.start.index)
        return [ki(i[o.start.index]), r.originalText.slice(o.start.offset, o.end.offset), ki(i[o.end.index])];
      if (o.start.index < u && u < o.end.index)
        return false;
      if (u === o.end.index)
        return t.shift(), false;
    }
    return n();
  } });
}
function Y(e, r, n, t = {}) {
  let { postprocessor: a = (o) => o, processor: i = () => n() } = t, u = [];
  return e.each(() => {
    let o = i(e);
    o !== false && (u.length > 0 && rf(e) && (u.push(O), (tf(e, r) || Ai(e)) && u.push(O), Ai(e) && u.push(O)), u.push(o));
  }, "children"), a(u);
}
function ki(e) {
  if (e.type === "html")
    return e.value;
  if (e.type === "paragraph" && Array.isArray(e.children) && e.children.length === 1 && e.children[0].type === "esComment")
    return ["{/* ", e.children[0].value, " */}"];
}
function xt(e) {
  let r;
  if (e.type === "html")
    r = e.value.match(/^<!--\s*prettier-ignore(?:-(start|end))?\s*-->$/);
  else {
    let n;
    e.type === "esComment" ? n = e : e.type === "paragraph" && e.children.length === 1 && e.children[0].type === "esComment" && (n = e.children[0]), n && (r = n.value.match(/^prettier-ignore(?:-(start|end))?$/));
  }
  return r ? r[1] || "next" : false;
}
function rf({ node: e, parent: r }) {
  let n = mt.has(e.type), t = e.type === "html" && _r.has(r.type);
  return !n && !t;
}
function wi(e, r) {
  return e.type === "listItem" && (e.spread || r.originalText.charAt(e.position.end.offset - 1) === `
`);
}
function tf({ node: e, previous: r, parent: n }, t) {
  if (wi(r, t))
    return true;
  let u = r.type === e.type && Kl.has(e.type), o = n.type === "listItem" && !wi(n, t), s = xt(r) === "next", l = e.type === "html" && r.type === "html" && r.position.end.line + 1 === e.position.start.line, c = e.type === "html" && n.type === "listItem" && r.type === "paragraph" && r.position.end.line + 1 === e.position.start.line;
  return !(u || o || s || l || c);
}
function Ai({ node: e, previous: r }) {
  let n = r.type === "list", t = e.type === "code" && e.isIndented;
  return n && t;
}
function nf(e) {
  let r = e.findAncestor((n) => n.type === "linkReference" || n.type === "imageReference");
  return r && (r.type !== "linkReference" || r.referenceType !== "full");
}
function Ct(e, r = []) {
  let n = [" ", ...Array.isArray(r) ? r : [r]];
  return new RegExp(n.map((t) => `\\${t}`).join("|")).test(e) ? `<${e}>` : e;
}
function Ir(e, r, n = true) {
  if (!e)
    return "";
  if (n)
    return " " + Ir(e, r, false);
  if (e = R(false, e, /\\(?=["')])/g, ""), e.includes('"') && e.includes("'") && !e.includes(")"))
    return `(${e})`;
  let t = Un(e, r.singleQuote);
  return e = R(false, e, "\\", "\\\\"), e = R(false, e, t, `\\${t}`), `${t}${e}${t}`;
}
function uf(e, r, n) {
  return e < r ? r : e > n ? n : e;
}
function af(e) {
  return e.index > 0 && xt(e.previous) === "next";
}
function bt(e) {
  return `[${(0, qi.default)(e.label)}]`;
}
function Bi(e) {
  return `[^${e.label}]`;
}
var of = { preprocess: Di, print: Jl, embed: si, massageAstNode: mi, hasPrettierIgnore: af, insertPragma: fi, getVisitorKeys: Fi }, Ti = of;
var Pn = {};
On(Pn, { markdown: () => xg, mdx: () => yg, remark: () => xg });
var Gc = Ie(ks(), 1), Hc = Ie(vc(), 1), Wc = Ie(Pc(), 1), Kc = Ie(Lc(), 1);
var hg = /^import\s/, mg = /^export\s/, Oc = "[a-z][a-z0-9]*(\\.[a-z][a-z0-9]*)*|", Ic = /<!---->|<!---?[^>-](?:-?[^-])*-->/, gg = /^{\s*\/\*(.*)\*\/\s*}/, vg = `

`, Nc = (e) => hg.test(e), Sn = (e) => mg.test(e), Rc = (e, r) => {
  let n = r.indexOf(vg), t = r.slice(0, n);
  if (Sn(t) || Nc(t))
    return e(t)({ type: Sn(t) ? "export" : "import", value: t });
}, zc = (e, r) => {
  let n = gg.exec(r);
  if (n)
    return e(n[0])({ type: "esComment", value: n[1].trim() });
};
Rc.locator = (e) => Sn(e) || Nc(e) ? -1 : 1;
zc.locator = (e, r) => e.indexOf("{", r);
var Mc = function() {
  let { Parser: e } = this, { blockTokenizers: r, blockMethods: n, inlineTokenizers: t, inlineMethods: a } = e.prototype;
  r.esSyntax = Rc, t.esComment = zc, n.splice(n.indexOf("paragraph"), 0, "esSyntax"), a.splice(a.indexOf("text"), 0, "esComment");
};
function Fg() {
  return (e) => be(e, (r, n, [t]) => r.type !== "html" || Ic.test(r.value) || _r.has(t.type) ? r : { ...r, type: "jsx" });
}
var Uc = Fg;
var Eg = function() {
  let e = this.Parser.prototype;
  e.blockMethods = ["frontMatter", ...e.blockMethods], e.blockTokenizers.frontMatter = r;
  function r(n, t) {
    let a = or(t);
    if (a.frontMatter)
      return n(a.frontMatter.raw)(a.frontMatter);
  }
  r.onlyAtStart = true;
}, Yc = Eg;
var Cg = function() {
  let e = this.Parser.prototype, r = e.inlineMethods;
  r.splice(r.indexOf("text"), 0, "liquid"), e.inlineTokenizers.liquid = n;
  function n(t, a) {
    let i = a.match(/^({%.*?%}|{{.*?}})/s);
    if (i)
      return t(i[0])({ type: "liquidNode", value: i[0] });
  }
  n.locator = function(t, a) {
    return t.indexOf("{", a);
  };
}, Vc = Cg;
var bg = function() {
  let e = "wikiLink", r = /^\[\[(?<linkContents>.+?)]]/s, n = this.Parser.prototype, t = n.inlineMethods;
  t.splice(t.indexOf("link"), 0, e), n.inlineTokenizers.wikiLink = a;
  function a(i, u) {
    let o = r.exec(u);
    if (o) {
      let s = o.groups.linkContents.trim();
      return i(o[0])({ type: e, value: s });
    }
  }
  a.locator = function(i, u) {
    return i.indexOf("[", u);
  };
}, jc = bg;
function Jc({ isMDX: e }) {
  return (r) => {
    let n = (0, Hc.default)().use(Gc.default, { commonmark: true, ...e && { blocks: [Oc] } }).use(Kc.default).use(Yc).use(Wc.default).use(e ? Mc : $c).use(Vc).use(e ? Uc : $c).use(jc);
    return n.run(n.parse(r));
  };
}
function $c() {
}
var Xc = { astFormat: "mdast", hasPragma: li, locStart: Te, locEnd: Se2 }, xg = { ...Xc, parse: Jc({ isMDX: false }) }, yg = { ...Xc, parse: Jc({ isMDX: true }) };
var Qc = [{ linguistLanguageId: 222, name: "Markdown", type: "prose", color: "#083fa1", aliases: ["md", "pandoc"], aceMode: "markdown", codemirrorMode: "gfm", codemirrorMimeType: "text/x-gfm", wrap: true, extensions: [".md", ".livemd", ".markdown", ".mdown", ".mdwn", ".mkd", ".mkdn", ".mkdown", ".ronn", ".scd", ".workbook"], filenames: ["contents.lr", "README"], tmScope: "text.md", parsers: ["markdown"], vscodeLanguageIds: ["markdown"] }, { linguistLanguageId: 222, name: "MDX", type: "prose", color: "#083fa1", aliases: ["md", "pandoc"], aceMode: "markdown", codemirrorMode: "gfm", codemirrorMimeType: "text/x-gfm", wrap: true, extensions: [".mdx"], filenames: [], tmScope: "text.md", parsers: ["mdx"], vscodeLanguageIds: ["mdx"] }];
var _n = { bracketSpacing: { category: "Common", type: "boolean", default: true, description: "Print spaces between brackets.", oppositeDescription: "Do not print spaces between brackets." }, singleQuote: { category: "Common", type: "boolean", default: false, description: "Use single quotes instead of double quotes." }, proseWrap: { category: "Common", type: "choice", default: "preserve", description: "How to wrap prose.", choices: [{ value: "always", description: "Wrap prose if it exceeds the print width." }, { value: "never", description: "Do not wrap prose." }, { value: "preserve", description: "Wrap prose as-is." }] }, bracketSameLine: { category: "Common", type: "boolean", default: false, description: "Put > of opening tags on the last line instead of on a new line." }, singleAttributePerLine: { category: "Common", type: "boolean", default: false, description: "Enforce single attribute per line in HTML, Vue and JSX." } };
var kg = { proseWrap: _n.proseWrap, singleQuote: _n.singleQuote }, Zc = kg;
var wg = { mdast: Ti };
var YE = Ln;
export {
  YE as Y,
  su as s
};
