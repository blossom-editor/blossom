function max(values, valueof) {
  let max2;
  if (valueof === void 0) {
    for (const value2 of values) {
      if (value2 != null && (max2 < value2 || max2 === void 0 && value2 >= value2)) {
        max2 = value2;
      }
    }
  } else {
    let index = -1;
    for (let value2 of values) {
      if ((value2 = valueof(value2, ++index, values)) != null && (max2 < value2 || max2 === void 0 && value2 >= value2)) {
        max2 = value2;
      }
    }
  }
  return max2;
}
function min(values, valueof) {
  let min2;
  if (valueof === void 0) {
    for (const value2 of values) {
      if (value2 != null && (min2 > value2 || min2 === void 0 && value2 >= value2)) {
        min2 = value2;
      }
    }
  } else {
    let index = -1;
    for (let value2 of values) {
      if ((value2 = valueof(value2, ++index, values)) != null && (min2 > value2 || min2 === void 0 && value2 >= value2)) {
        min2 = value2;
      }
    }
  }
  return min2;
}
function sum(values, valueof) {
  let sum2 = 0;
  if (valueof === void 0) {
    for (let value2 of values) {
      if (value2 = +value2) {
        sum2 += value2;
      }
    }
  } else {
    let index = -1;
    for (let value2 of values) {
      if (value2 = +valueof(value2, ++index, values)) {
        sum2 += value2;
      }
    }
  }
  return sum2;
}
function targetDepth(d) {
  return d.target.depth;
}
function left(node) {
  return node.depth;
}
function right(node, n) {
  return n - 1 - node.height;
}
function justify(node, n) {
  return node.sourceLinks.length ? node.depth : n - 1;
}
function center(node) {
  return node.targetLinks.length ? node.depth : node.sourceLinks.length ? min(node.sourceLinks, targetDepth) - 1 : 0;
}
function constant$1(x2) {
  return function() {
    return x2;
  };
}
function ascendingSourceBreadth(a, b) {
  return ascendingBreadth(a.source, b.source) || a.index - b.index;
}
function ascendingTargetBreadth(a, b) {
  return ascendingBreadth(a.target, b.target) || a.index - b.index;
}
function ascendingBreadth(a, b) {
  return a.y0 - b.y0;
}
function value(d) {
  return d.value;
}
function defaultId(d) {
  return d.index;
}
function defaultNodes(graph) {
  return graph.nodes;
}
function defaultLinks(graph) {
  return graph.links;
}
function find(nodeById, id) {
  const node = nodeById.get(id);
  if (!node)
    throw new Error("missing: " + id);
  return node;
}
function computeLinkBreadths({ nodes }) {
  for (const node of nodes) {
    let y0 = node.y0;
    let y1 = y0;
    for (const link2 of node.sourceLinks) {
      link2.y0 = y0 + link2.width / 2;
      y0 += link2.width;
    }
    for (const link2 of node.targetLinks) {
      link2.y1 = y1 + link2.width / 2;
      y1 += link2.width;
    }
  }
}
function Sankey() {
  let x0 = 0, y0 = 0, x1 = 1, y1 = 1;
  let dx = 24;
  let dy = 8, py;
  let id = defaultId;
  let align = justify;
  let sort;
  let linkSort;
  let nodes = defaultNodes;
  let links = defaultLinks;
  let iterations = 6;
  function sankey() {
    const graph = { nodes: nodes.apply(null, arguments), links: links.apply(null, arguments) };
    computeNodeLinks(graph);
    computeNodeValues(graph);
    computeNodeDepths(graph);
    computeNodeHeights(graph);
    computeNodeBreadths(graph);
    computeLinkBreadths(graph);
    return graph;
  }
  sankey.update = function(graph) {
    computeLinkBreadths(graph);
    return graph;
  };
  sankey.nodeId = function(_) {
    return arguments.length ? (id = typeof _ === "function" ? _ : constant$1(_), sankey) : id;
  };
  sankey.nodeAlign = function(_) {
    return arguments.length ? (align = typeof _ === "function" ? _ : constant$1(_), sankey) : align;
  };
  sankey.nodeSort = function(_) {
    return arguments.length ? (sort = _, sankey) : sort;
  };
  sankey.nodeWidth = function(_) {
    return arguments.length ? (dx = +_, sankey) : dx;
  };
  sankey.nodePadding = function(_) {
    return arguments.length ? (dy = py = +_, sankey) : dy;
  };
  sankey.nodes = function(_) {
    return arguments.length ? (nodes = typeof _ === "function" ? _ : constant$1(_), sankey) : nodes;
  };
  sankey.links = function(_) {
    return arguments.length ? (links = typeof _ === "function" ? _ : constant$1(_), sankey) : links;
  };
  sankey.linkSort = function(_) {
    return arguments.length ? (linkSort = _, sankey) : linkSort;
  };
  sankey.size = function(_) {
    return arguments.length ? (x0 = y0 = 0, x1 = +_[0], y1 = +_[1], sankey) : [x1 - x0, y1 - y0];
  };
  sankey.extent = function(_) {
    return arguments.length ? (x0 = +_[0][0], x1 = +_[1][0], y0 = +_[0][1], y1 = +_[1][1], sankey) : [[x0, y0], [x1, y1]];
  };
  sankey.iterations = function(_) {
    return arguments.length ? (iterations = +_, sankey) : iterations;
  };
  function computeNodeLinks({ nodes: nodes2, links: links2 }) {
    for (const [i, node] of nodes2.entries()) {
      node.index = i;
      node.sourceLinks = [];
      node.targetLinks = [];
    }
    const nodeById = new Map(nodes2.map((d, i) => [id(d, i, nodes2), d]));
    for (const [i, link2] of links2.entries()) {
      link2.index = i;
      let { source, target } = link2;
      if (typeof source !== "object")
        source = link2.source = find(nodeById, source);
      if (typeof target !== "object")
        target = link2.target = find(nodeById, target);
      source.sourceLinks.push(link2);
      target.targetLinks.push(link2);
    }
    if (linkSort != null) {
      for (const { sourceLinks, targetLinks } of nodes2) {
        sourceLinks.sort(linkSort);
        targetLinks.sort(linkSort);
      }
    }
  }
  function computeNodeValues({ nodes: nodes2 }) {
    for (const node of nodes2) {
      node.value = node.fixedValue === void 0 ? Math.max(sum(node.sourceLinks, value), sum(node.targetLinks, value)) : node.fixedValue;
    }
  }
  function computeNodeDepths({ nodes: nodes2 }) {
    const n = nodes2.length;
    let current = new Set(nodes2);
    let next = /* @__PURE__ */ new Set();
    let x2 = 0;
    while (current.size) {
      for (const node of current) {
        node.depth = x2;
        for (const { target } of node.sourceLinks) {
          next.add(target);
        }
      }
      if (++x2 > n)
        throw new Error("circular link");
      current = next;
      next = /* @__PURE__ */ new Set();
    }
  }
  function computeNodeHeights({ nodes: nodes2 }) {
    const n = nodes2.length;
    let current = new Set(nodes2);
    let next = /* @__PURE__ */ new Set();
    let x2 = 0;
    while (current.size) {
      for (const node of current) {
        node.height = x2;
        for (const { source } of node.targetLinks) {
          next.add(source);
        }
      }
      if (++x2 > n)
        throw new Error("circular link");
      current = next;
      next = /* @__PURE__ */ new Set();
    }
  }
  function computeNodeLayers({ nodes: nodes2 }) {
    const x2 = max(nodes2, (d) => d.depth) + 1;
    const kx = (x1 - x0 - dx) / (x2 - 1);
    const columns = new Array(x2);
    for (const node of nodes2) {
      const i = Math.max(0, Math.min(x2 - 1, Math.floor(align.call(null, node, x2))));
      node.layer = i;
      node.x0 = x0 + i * kx;
      node.x1 = node.x0 + dx;
      if (columns[i])
        columns[i].push(node);
      else
        columns[i] = [node];
    }
    if (sort)
      for (const column of columns) {
        column.sort(sort);
      }
    return columns;
  }
  function initializeNodeBreadths(columns) {
    const ky = min(columns, (c) => (y1 - y0 - (c.length - 1) * py) / sum(c, value));
    for (const nodes2 of columns) {
      let y2 = y0;
      for (const node of nodes2) {
        node.y0 = y2;
        node.y1 = y2 + node.value * ky;
        y2 = node.y1 + py;
        for (const link2 of node.sourceLinks) {
          link2.width = link2.value * ky;
        }
      }
      y2 = (y1 - y2 + py) / (nodes2.length + 1);
      for (let i = 0; i < nodes2.length; ++i) {
        const node = nodes2[i];
        node.y0 += y2 * (i + 1);
        node.y1 += y2 * (i + 1);
      }
      reorderLinks(nodes2);
    }
  }
  function computeNodeBreadths(graph) {
    const columns = computeNodeLayers(graph);
    py = Math.min(dy, (y1 - y0) / (max(columns, (c) => c.length) - 1));
    initializeNodeBreadths(columns);
    for (let i = 0; i < iterations; ++i) {
      const alpha = Math.pow(0.99, i);
      const beta = Math.max(1 - alpha, (i + 1) / iterations);
      relaxRightToLeft(columns, alpha, beta);
      relaxLeftToRight(columns, alpha, beta);
    }
  }
  function relaxLeftToRight(columns, alpha, beta) {
    for (let i = 1, n = columns.length; i < n; ++i) {
      const column = columns[i];
      for (const target of column) {
        let y2 = 0;
        let w = 0;
        for (const { source, value: value2 } of target.targetLinks) {
          let v = value2 * (target.layer - source.layer);
          y2 += targetTop(source, target) * v;
          w += v;
        }
        if (!(w > 0))
          continue;
        let dy2 = (y2 / w - target.y0) * alpha;
        target.y0 += dy2;
        target.y1 += dy2;
        reorderNodeLinks(target);
      }
      if (sort === void 0)
        column.sort(ascendingBreadth);
      resolveCollisions(column, beta);
    }
  }
  function relaxRightToLeft(columns, alpha, beta) {
    for (let n = columns.length, i = n - 2; i >= 0; --i) {
      const column = columns[i];
      for (const source of column) {
        let y2 = 0;
        let w = 0;
        for (const { target, value: value2 } of source.sourceLinks) {
          let v = value2 * (target.layer - source.layer);
          y2 += sourceTop(source, target) * v;
          w += v;
        }
        if (!(w > 0))
          continue;
        let dy2 = (y2 / w - source.y0) * alpha;
        source.y0 += dy2;
        source.y1 += dy2;
        reorderNodeLinks(source);
      }
      if (sort === void 0)
        column.sort(ascendingBreadth);
      resolveCollisions(column, beta);
    }
  }
  function resolveCollisions(nodes2, alpha) {
    const i = nodes2.length >> 1;
    const subject = nodes2[i];
    resolveCollisionsBottomToTop(nodes2, subject.y0 - py, i - 1, alpha);
    resolveCollisionsTopToBottom(nodes2, subject.y1 + py, i + 1, alpha);
    resolveCollisionsBottomToTop(nodes2, y1, nodes2.length - 1, alpha);
    resolveCollisionsTopToBottom(nodes2, y0, 0, alpha);
  }
  function resolveCollisionsTopToBottom(nodes2, y2, i, alpha) {
    for (; i < nodes2.length; ++i) {
      const node = nodes2[i];
      const dy2 = (y2 - node.y0) * alpha;
      if (dy2 > 1e-6)
        node.y0 += dy2, node.y1 += dy2;
      y2 = node.y1 + py;
    }
  }
  function resolveCollisionsBottomToTop(nodes2, y2, i, alpha) {
    for (; i >= 0; --i) {
      const node = nodes2[i];
      const dy2 = (node.y1 - y2) * alpha;
      if (dy2 > 1e-6)
        node.y0 -= dy2, node.y1 -= dy2;
      y2 = node.y0 - py;
    }
  }
  function reorderNodeLinks({ sourceLinks, targetLinks }) {
    if (linkSort === void 0) {
      for (const { source: { sourceLinks: sourceLinks2 } } of targetLinks) {
        sourceLinks2.sort(ascendingTargetBreadth);
      }
      for (const { target: { targetLinks: targetLinks2 } } of sourceLinks) {
        targetLinks2.sort(ascendingSourceBreadth);
      }
    }
  }
  function reorderLinks(nodes2) {
    if (linkSort === void 0) {
      for (const { sourceLinks, targetLinks } of nodes2) {
        sourceLinks.sort(ascendingTargetBreadth);
        targetLinks.sort(ascendingSourceBreadth);
      }
    }
  }
  function targetTop(source, target) {
    let y2 = source.y0 - (source.sourceLinks.length - 1) * py / 2;
    for (const { target: node, width } of source.sourceLinks) {
      if (node === target)
        break;
      y2 += width + py;
    }
    for (const { source: node, width } of target.targetLinks) {
      if (node === source)
        break;
      y2 -= width;
    }
    return y2;
  }
  function sourceTop(source, target) {
    let y2 = target.y0 - (target.targetLinks.length - 1) * py / 2;
    for (const { source: node, width } of target.targetLinks) {
      if (node === source)
        break;
      y2 += width + py;
    }
    for (const { target: node, width } of source.sourceLinks) {
      if (node === target)
        break;
      y2 -= width;
    }
    return y2;
  }
  return sankey;
}
var pi = Math.PI, tau = 2 * pi, epsilon = 1e-6, tauEpsilon = tau - epsilon;
function Path() {
  this._x0 = this._y0 = // start of current subpath
  this._x1 = this._y1 = null;
  this._ = "";
}
function path() {
  return new Path();
}
Path.prototype = path.prototype = {
  constructor: Path,
  moveTo: function(x2, y2) {
    this._ += "M" + (this._x0 = this._x1 = +x2) + "," + (this._y0 = this._y1 = +y2);
  },
  closePath: function() {
    if (this._x1 !== null) {
      this._x1 = this._x0, this._y1 = this._y0;
      this._ += "Z";
    }
  },
  lineTo: function(x2, y2) {
    this._ += "L" + (this._x1 = +x2) + "," + (this._y1 = +y2);
  },
  quadraticCurveTo: function(x1, y1, x2, y2) {
    this._ += "Q" + +x1 + "," + +y1 + "," + (this._x1 = +x2) + "," + (this._y1 = +y2);
  },
  bezierCurveTo: function(x1, y1, x2, y2, x3, y3) {
    this._ += "C" + +x1 + "," + +y1 + "," + +x2 + "," + +y2 + "," + (this._x1 = +x3) + "," + (this._y1 = +y3);
  },
  arcTo: function(x1, y1, x2, y2, r) {
    x1 = +x1, y1 = +y1, x2 = +x2, y2 = +y2, r = +r;
    var x0 = this._x1, y0 = this._y1, x21 = x2 - x1, y21 = y2 - y1, x01 = x0 - x1, y01 = y0 - y1, l01_2 = x01 * x01 + y01 * y01;
    if (r < 0)
      throw new Error("negative radius: " + r);
    if (this._x1 === null) {
      this._ += "M" + (this._x1 = x1) + "," + (this._y1 = y1);
    } else if (!(l01_2 > epsilon))
      ;
    else if (!(Math.abs(y01 * x21 - y21 * x01) > epsilon) || !r) {
      this._ += "L" + (this._x1 = x1) + "," + (this._y1 = y1);
    } else {
      var x20 = x2 - x0, y20 = y2 - y0, l21_2 = x21 * x21 + y21 * y21, l20_2 = x20 * x20 + y20 * y20, l21 = Math.sqrt(l21_2), l01 = Math.sqrt(l01_2), l = r * Math.tan((pi - Math.acos((l21_2 + l01_2 - l20_2) / (2 * l21 * l01))) / 2), t01 = l / l01, t21 = l / l21;
      if (Math.abs(t01 - 1) > epsilon) {
        this._ += "L" + (x1 + t01 * x01) + "," + (y1 + t01 * y01);
      }
      this._ += "A" + r + "," + r + ",0,0," + +(y01 * x20 > x01 * y20) + "," + (this._x1 = x1 + t21 * x21) + "," + (this._y1 = y1 + t21 * y21);
    }
  },
  arc: function(x2, y2, r, a0, a1, ccw) {
    x2 = +x2, y2 = +y2, r = +r, ccw = !!ccw;
    var dx = r * Math.cos(a0), dy = r * Math.sin(a0), x0 = x2 + dx, y0 = y2 + dy, cw = 1 ^ ccw, da = ccw ? a0 - a1 : a1 - a0;
    if (r < 0)
      throw new Error("negative radius: " + r);
    if (this._x1 === null) {
      this._ += "M" + x0 + "," + y0;
    } else if (Math.abs(this._x1 - x0) > epsilon || Math.abs(this._y1 - y0) > epsilon) {
      this._ += "L" + x0 + "," + y0;
    }
    if (!r)
      return;
    if (da < 0)
      da = da % tau + tau;
    if (da > tauEpsilon) {
      this._ += "A" + r + "," + r + ",0,1," + cw + "," + (x2 - dx) + "," + (y2 - dy) + "A" + r + "," + r + ",0,1," + cw + "," + (this._x1 = x0) + "," + (this._y1 = y0);
    } else if (da > epsilon) {
      this._ += "A" + r + "," + r + ",0," + +(da >= pi) + "," + cw + "," + (this._x1 = x2 + r * Math.cos(a1)) + "," + (this._y1 = y2 + r * Math.sin(a1));
    }
  },
  rect: function(x2, y2, w, h) {
    this._ += "M" + (this._x0 = this._x1 = +x2) + "," + (this._y0 = this._y1 = +y2) + "h" + +w + "v" + +h + "h" + -w + "Z";
  },
  toString: function() {
    return this._;
  }
};
function constant(x2) {
  return function constant2() {
    return x2;
  };
}
function x(p) {
  return p[0];
}
function y(p) {
  return p[1];
}
var slice = Array.prototype.slice;
function linkSource(d) {
  return d.source;
}
function linkTarget(d) {
  return d.target;
}
function link(curve) {
  var source = linkSource, target = linkTarget, x$1 = x, y$1 = y, context = null;
  function link2() {
    var buffer, argv = slice.call(arguments), s = source.apply(this, argv), t = target.apply(this, argv);
    if (!context)
      context = buffer = path();
    curve(context, +x$1.apply(this, (argv[0] = s, argv)), +y$1.apply(this, argv), +x$1.apply(this, (argv[0] = t, argv)), +y$1.apply(this, argv));
    if (buffer)
      return context = null, buffer + "" || null;
  }
  link2.source = function(_) {
    return arguments.length ? (source = _, link2) : source;
  };
  link2.target = function(_) {
    return arguments.length ? (target = _, link2) : target;
  };
  link2.x = function(_) {
    return arguments.length ? (x$1 = typeof _ === "function" ? _ : constant(+_), link2) : x$1;
  };
  link2.y = function(_) {
    return arguments.length ? (y$1 = typeof _ === "function" ? _ : constant(+_), link2) : y$1;
  };
  link2.context = function(_) {
    return arguments.length ? (context = _ == null ? null : _, link2) : context;
  };
  return link2;
}
function curveHorizontal(context, x0, y0, x1, y1) {
  context.moveTo(x0, y0);
  context.bezierCurveTo(x0 = (x0 + x1) / 2, y0, x0, y1, x1, y1);
}
function linkHorizontal() {
  return link(curveHorizontal);
}
function horizontalSource(d) {
  return [d.source.x1, d.y0];
}
function horizontalTarget(d) {
  return [d.target.x0, d.y1];
}
function sankeyLinkHorizontal() {
  return linkHorizontal().source(horizontalSource).target(horizontalTarget);
}
export {
  Sankey as S,
  center as c,
  justify as j,
  left as l,
  right as r,
  sankeyLinkHorizontal as s
};
