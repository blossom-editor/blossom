import "./d3-transition-fc8016c4.js";
import "./d3-zoom-f0aa3407.js";
import { h as has, c as constant, i as isFunction, k as keys, f as filter, a as forEach, b as isUndefined, u as union, v as values, r as reduce, d as isEmpty, e as flatten, m as map, g as range, j as uniqueId, n as now, l as min, o as max, p as minBy, q as isArray, s as find, z as zipObject, t as sortBy, w as pick, x as last, y as cloneDeep, A as merge$1, B as mapValues, C as forIn, D as forOwn, E as defaults, F as isPlainObject, G as clone } from "./lodash-es-42b2b1f3.js";
import { s as select } from "./d3-selection-ec178582.js";
import { l as line, c as curveLinear } from "./d3-shape-a0afbb89.js";
import { s as svg } from "./d3-fetch-bbc298d1.js";
var DEFAULT_EDGE_NAME = "\0";
var GRAPH_NODE = "\0";
var EDGE_KEY_DELIM = "";
class Graph {
  constructor(opts = {}) {
    this._isDirected = has(opts, "directed") ? opts.directed : true;
    this._isMultigraph = has(opts, "multigraph") ? opts.multigraph : false;
    this._isCompound = has(opts, "compound") ? opts.compound : false;
    this._label = void 0;
    this._defaultNodeLabelFn = constant(void 0);
    this._defaultEdgeLabelFn = constant(void 0);
    this._nodes = {};
    if (this._isCompound) {
      this._parent = {};
      this._children = {};
      this._children[GRAPH_NODE] = {};
    }
    this._in = {};
    this._preds = {};
    this._out = {};
    this._sucs = {};
    this._edgeObjs = {};
    this._edgeLabels = {};
  }
  /* === Graph functions ========= */
  isDirected() {
    return this._isDirected;
  }
  isMultigraph() {
    return this._isMultigraph;
  }
  isCompound() {
    return this._isCompound;
  }
  setGraph(label) {
    this._label = label;
    return this;
  }
  graph() {
    return this._label;
  }
  /* === Node functions ========== */
  setDefaultNodeLabel(newDefault) {
    if (!isFunction(newDefault)) {
      newDefault = constant(newDefault);
    }
    this._defaultNodeLabelFn = newDefault;
    return this;
  }
  nodeCount() {
    return this._nodeCount;
  }
  nodes() {
    return keys(this._nodes);
  }
  sources() {
    var self = this;
    return filter(this.nodes(), function(v) {
      return isEmpty(self._in[v]);
    });
  }
  sinks() {
    var self = this;
    return filter(this.nodes(), function(v) {
      return isEmpty(self._out[v]);
    });
  }
  setNodes(vs, value) {
    var args = arguments;
    var self = this;
    forEach(vs, function(v) {
      if (args.length > 1) {
        self.setNode(v, value);
      } else {
        self.setNode(v);
      }
    });
    return this;
  }
  setNode(v, value) {
    if (has(this._nodes, v)) {
      if (arguments.length > 1) {
        this._nodes[v] = value;
      }
      return this;
    }
    this._nodes[v] = arguments.length > 1 ? value : this._defaultNodeLabelFn(v);
    if (this._isCompound) {
      this._parent[v] = GRAPH_NODE;
      this._children[v] = {};
      this._children[GRAPH_NODE][v] = true;
    }
    this._in[v] = {};
    this._preds[v] = {};
    this._out[v] = {};
    this._sucs[v] = {};
    ++this._nodeCount;
    return this;
  }
  node(v) {
    return this._nodes[v];
  }
  hasNode(v) {
    return has(this._nodes, v);
  }
  removeNode(v) {
    var self = this;
    if (has(this._nodes, v)) {
      var removeEdge = function(e) {
        self.removeEdge(self._edgeObjs[e]);
      };
      delete this._nodes[v];
      if (this._isCompound) {
        this._removeFromParentsChildList(v);
        delete this._parent[v];
        forEach(this.children(v), function(child) {
          self.setParent(child);
        });
        delete this._children[v];
      }
      forEach(keys(this._in[v]), removeEdge);
      delete this._in[v];
      delete this._preds[v];
      forEach(keys(this._out[v]), removeEdge);
      delete this._out[v];
      delete this._sucs[v];
      --this._nodeCount;
    }
    return this;
  }
  setParent(v, parent) {
    if (!this._isCompound) {
      throw new Error("Cannot set parent in a non-compound graph");
    }
    if (isUndefined(parent)) {
      parent = GRAPH_NODE;
    } else {
      parent += "";
      for (var ancestor = parent; !isUndefined(ancestor); ancestor = this.parent(ancestor)) {
        if (ancestor === v) {
          throw new Error("Setting " + parent + " as parent of " + v + " would create a cycle");
        }
      }
      this.setNode(parent);
    }
    this.setNode(v);
    this._removeFromParentsChildList(v);
    this._parent[v] = parent;
    this._children[parent][v] = true;
    return this;
  }
  _removeFromParentsChildList(v) {
    delete this._children[this._parent[v]][v];
  }
  parent(v) {
    if (this._isCompound) {
      var parent = this._parent[v];
      if (parent !== GRAPH_NODE) {
        return parent;
      }
    }
  }
  children(v) {
    if (isUndefined(v)) {
      v = GRAPH_NODE;
    }
    if (this._isCompound) {
      var children = this._children[v];
      if (children) {
        return keys(children);
      }
    } else if (v === GRAPH_NODE) {
      return this.nodes();
    } else if (this.hasNode(v)) {
      return [];
    }
  }
  predecessors(v) {
    var predsV = this._preds[v];
    if (predsV) {
      return keys(predsV);
    }
  }
  successors(v) {
    var sucsV = this._sucs[v];
    if (sucsV) {
      return keys(sucsV);
    }
  }
  neighbors(v) {
    var preds = this.predecessors(v);
    if (preds) {
      return union(preds, this.successors(v));
    }
  }
  isLeaf(v) {
    var neighbors;
    if (this.isDirected()) {
      neighbors = this.successors(v);
    } else {
      neighbors = this.neighbors(v);
    }
    return neighbors.length === 0;
  }
  filterNodes(filter2) {
    var copy = new this.constructor({
      directed: this._isDirected,
      multigraph: this._isMultigraph,
      compound: this._isCompound
    });
    copy.setGraph(this.graph());
    var self = this;
    forEach(this._nodes, function(value, v) {
      if (filter2(v)) {
        copy.setNode(v, value);
      }
    });
    forEach(this._edgeObjs, function(e) {
      if (copy.hasNode(e.v) && copy.hasNode(e.w)) {
        copy.setEdge(e, self.edge(e));
      }
    });
    var parents = {};
    function findParent(v) {
      var parent = self.parent(v);
      if (parent === void 0 || copy.hasNode(parent)) {
        parents[v] = parent;
        return parent;
      } else if (parent in parents) {
        return parents[parent];
      } else {
        return findParent(parent);
      }
    }
    if (this._isCompound) {
      forEach(copy.nodes(), function(v) {
        copy.setParent(v, findParent(v));
      });
    }
    return copy;
  }
  /* === Edge functions ========== */
  setDefaultEdgeLabel(newDefault) {
    if (!isFunction(newDefault)) {
      newDefault = constant(newDefault);
    }
    this._defaultEdgeLabelFn = newDefault;
    return this;
  }
  edgeCount() {
    return this._edgeCount;
  }
  edges() {
    return values(this._edgeObjs);
  }
  setPath(vs, value) {
    var self = this;
    var args = arguments;
    reduce(vs, function(v, w) {
      if (args.length > 1) {
        self.setEdge(v, w, value);
      } else {
        self.setEdge(v, w);
      }
      return w;
    });
    return this;
  }
  /*
   * setEdge(v, w, [value, [name]])
   * setEdge({ v, w, [name] }, [value])
   */
  setEdge() {
    var v, w, name, value;
    var valueSpecified = false;
    var arg0 = arguments[0];
    if (typeof arg0 === "object" && arg0 !== null && "v" in arg0) {
      v = arg0.v;
      w = arg0.w;
      name = arg0.name;
      if (arguments.length === 2) {
        value = arguments[1];
        valueSpecified = true;
      }
    } else {
      v = arg0;
      w = arguments[1];
      name = arguments[3];
      if (arguments.length > 2) {
        value = arguments[2];
        valueSpecified = true;
      }
    }
    v = "" + v;
    w = "" + w;
    if (!isUndefined(name)) {
      name = "" + name;
    }
    var e = edgeArgsToId(this._isDirected, v, w, name);
    if (has(this._edgeLabels, e)) {
      if (valueSpecified) {
        this._edgeLabels[e] = value;
      }
      return this;
    }
    if (!isUndefined(name) && !this._isMultigraph) {
      throw new Error("Cannot set a named edge when isMultigraph = false");
    }
    this.setNode(v);
    this.setNode(w);
    this._edgeLabels[e] = valueSpecified ? value : this._defaultEdgeLabelFn(v, w, name);
    var edgeObj = edgeArgsToObj(this._isDirected, v, w, name);
    v = edgeObj.v;
    w = edgeObj.w;
    Object.freeze(edgeObj);
    this._edgeObjs[e] = edgeObj;
    incrementOrInitEntry(this._preds[w], v);
    incrementOrInitEntry(this._sucs[v], w);
    this._in[w][e] = edgeObj;
    this._out[v][e] = edgeObj;
    this._edgeCount++;
    return this;
  }
  edge(v, w, name) {
    var e = arguments.length === 1 ? edgeObjToId(this._isDirected, arguments[0]) : edgeArgsToId(this._isDirected, v, w, name);
    return this._edgeLabels[e];
  }
  hasEdge(v, w, name) {
    var e = arguments.length === 1 ? edgeObjToId(this._isDirected, arguments[0]) : edgeArgsToId(this._isDirected, v, w, name);
    return has(this._edgeLabels, e);
  }
  removeEdge(v, w, name) {
    var e = arguments.length === 1 ? edgeObjToId(this._isDirected, arguments[0]) : edgeArgsToId(this._isDirected, v, w, name);
    var edge = this._edgeObjs[e];
    if (edge) {
      v = edge.v;
      w = edge.w;
      delete this._edgeLabels[e];
      delete this._edgeObjs[e];
      decrementOrRemoveEntry(this._preds[w], v);
      decrementOrRemoveEntry(this._sucs[v], w);
      delete this._in[w][e];
      delete this._out[v][e];
      this._edgeCount--;
    }
    return this;
  }
  inEdges(v, u) {
    var inV = this._in[v];
    if (inV) {
      var edges = values(inV);
      if (!u) {
        return edges;
      }
      return filter(edges, function(edge) {
        return edge.v === u;
      });
    }
  }
  outEdges(v, w) {
    var outV = this._out[v];
    if (outV) {
      var edges = values(outV);
      if (!w) {
        return edges;
      }
      return filter(edges, function(edge) {
        return edge.w === w;
      });
    }
  }
  nodeEdges(v, w) {
    var inEdges = this.inEdges(v, w);
    if (inEdges) {
      return inEdges.concat(this.outEdges(v, w));
    }
  }
}
Graph.prototype._nodeCount = 0;
Graph.prototype._edgeCount = 0;
function incrementOrInitEntry(map2, k) {
  if (map2[k]) {
    map2[k]++;
  } else {
    map2[k] = 1;
  }
}
function decrementOrRemoveEntry(map2, k) {
  if (!--map2[k]) {
    delete map2[k];
  }
}
function edgeArgsToId(isDirected, v_, w_, name) {
  var v = "" + v_;
  var w = "" + w_;
  if (!isDirected && v > w) {
    var tmp = v;
    v = w;
    w = tmp;
  }
  return v + EDGE_KEY_DELIM + w + EDGE_KEY_DELIM + (isUndefined(name) ? DEFAULT_EDGE_NAME : name);
}
function edgeArgsToObj(isDirected, v_, w_, name) {
  var v = "" + v_;
  var w = "" + w_;
  if (!isDirected && v > w) {
    var tmp = v;
    v = w;
    w = tmp;
  }
  var edgeObj = { v, w };
  if (name) {
    edgeObj.name = name;
  }
  return edgeObj;
}
function edgeObjToId(isDirected, edgeObj) {
  return edgeArgsToId(isDirected, edgeObj.v, edgeObj.w, edgeObj.name);
}
class List {
  constructor() {
    var sentinel = {};
    sentinel._next = sentinel._prev = sentinel;
    this._sentinel = sentinel;
  }
  dequeue() {
    var sentinel = this._sentinel;
    var entry = sentinel._prev;
    if (entry !== sentinel) {
      unlink(entry);
      return entry;
    }
  }
  enqueue(entry) {
    var sentinel = this._sentinel;
    if (entry._prev && entry._next) {
      unlink(entry);
    }
    entry._next = sentinel._next;
    sentinel._next._prev = entry;
    sentinel._next = entry;
    entry._prev = sentinel;
  }
  toString() {
    var strs = [];
    var sentinel = this._sentinel;
    var curr = sentinel._prev;
    while (curr !== sentinel) {
      strs.push(JSON.stringify(curr, filterOutLinks));
      curr = curr._prev;
    }
    return "[" + strs.join(", ") + "]";
  }
}
function unlink(entry) {
  entry._prev._next = entry._next;
  entry._next._prev = entry._prev;
  delete entry._next;
  delete entry._prev;
}
function filterOutLinks(k, v) {
  if (k !== "_next" && k !== "_prev") {
    return v;
  }
}
var DEFAULT_WEIGHT_FN = constant(1);
function greedyFAS(g, weightFn) {
  if (g.nodeCount() <= 1) {
    return [];
  }
  var state = buildState(g, weightFn || DEFAULT_WEIGHT_FN);
  var results = doGreedyFAS(state.graph, state.buckets, state.zeroIdx);
  return flatten(
    map(results, function(e) {
      return g.outEdges(e.v, e.w);
    })
  );
}
function doGreedyFAS(g, buckets, zeroIdx) {
  var results = [];
  var sources = buckets[buckets.length - 1];
  var sinks = buckets[0];
  var entry;
  while (g.nodeCount()) {
    while (entry = sinks.dequeue()) {
      removeNode(g, buckets, zeroIdx, entry);
    }
    while (entry = sources.dequeue()) {
      removeNode(g, buckets, zeroIdx, entry);
    }
    if (g.nodeCount()) {
      for (var i = buckets.length - 2; i > 0; --i) {
        entry = buckets[i].dequeue();
        if (entry) {
          results = results.concat(removeNode(g, buckets, zeroIdx, entry, true));
          break;
        }
      }
    }
  }
  return results;
}
function removeNode(g, buckets, zeroIdx, entry, collectPredecessors) {
  var results = collectPredecessors ? [] : void 0;
  forEach(g.inEdges(entry.v), function(edge) {
    var weight = g.edge(edge);
    var uEntry = g.node(edge.v);
    if (collectPredecessors) {
      results.push({ v: edge.v, w: edge.w });
    }
    uEntry.out -= weight;
    assignBucket(buckets, zeroIdx, uEntry);
  });
  forEach(g.outEdges(entry.v), function(edge) {
    var weight = g.edge(edge);
    var w = edge.w;
    var wEntry = g.node(w);
    wEntry["in"] -= weight;
    assignBucket(buckets, zeroIdx, wEntry);
  });
  g.removeNode(entry.v);
  return results;
}
function buildState(g, weightFn) {
  var fasGraph = new Graph();
  var maxIn = 0;
  var maxOut = 0;
  forEach(g.nodes(), function(v) {
    fasGraph.setNode(v, { v, in: 0, out: 0 });
  });
  forEach(g.edges(), function(e) {
    var prevWeight = fasGraph.edge(e.v, e.w) || 0;
    var weight = weightFn(e);
    var edgeWeight = prevWeight + weight;
    fasGraph.setEdge(e.v, e.w, edgeWeight);
    maxOut = Math.max(maxOut, fasGraph.node(e.v).out += weight);
    maxIn = Math.max(maxIn, fasGraph.node(e.w)["in"] += weight);
  });
  var buckets = range(maxOut + maxIn + 3).map(function() {
    return new List();
  });
  var zeroIdx = maxIn + 1;
  forEach(fasGraph.nodes(), function(v) {
    assignBucket(buckets, zeroIdx, fasGraph.node(v));
  });
  return { graph: fasGraph, buckets, zeroIdx };
}
function assignBucket(buckets, zeroIdx, entry) {
  if (!entry.out) {
    buckets[0].enqueue(entry);
  } else if (!entry["in"]) {
    buckets[buckets.length - 1].enqueue(entry);
  } else {
    buckets[entry.out - entry["in"] + zeroIdx].enqueue(entry);
  }
}
function run$2(g) {
  var fas = g.graph().acyclicer === "greedy" ? greedyFAS(g, weightFn(g)) : dfsFAS(g);
  forEach(fas, function(e) {
    var label = g.edge(e);
    g.removeEdge(e);
    label.forwardName = e.name;
    label.reversed = true;
    g.setEdge(e.w, e.v, label, uniqueId("rev"));
  });
  function weightFn(g2) {
    return function(e) {
      return g2.edge(e).weight;
    };
  }
}
function dfsFAS(g) {
  var fas = [];
  var stack = {};
  var visited = {};
  function dfs2(v) {
    if (has(visited, v)) {
      return;
    }
    visited[v] = true;
    stack[v] = true;
    forEach(g.outEdges(v), function(e) {
      if (has(stack, e.w)) {
        fas.push(e);
      } else {
        dfs2(e.w);
      }
    });
    delete stack[v];
  }
  forEach(g.nodes(), dfs2);
  return fas;
}
function undo$2(g) {
  forEach(g.edges(), function(e) {
    var label = g.edge(e);
    if (label.reversed) {
      g.removeEdge(e);
      var forwardName = label.forwardName;
      delete label.reversed;
      delete label.forwardName;
      g.setEdge(e.w, e.v, label, forwardName);
    }
  });
}
function addDummyNode(g, type, attrs, name) {
  var v;
  do {
    v = uniqueId(name);
  } while (g.hasNode(v));
  attrs.dummy = type;
  g.setNode(v, attrs);
  return v;
}
function simplify(g) {
  var simplified = new Graph().setGraph(g.graph());
  forEach(g.nodes(), function(v) {
    simplified.setNode(v, g.node(v));
  });
  forEach(g.edges(), function(e) {
    var simpleLabel = simplified.edge(e.v, e.w) || { weight: 0, minlen: 1 };
    var label = g.edge(e);
    simplified.setEdge(e.v, e.w, {
      weight: simpleLabel.weight + label.weight,
      minlen: Math.max(simpleLabel.minlen, label.minlen)
    });
  });
  return simplified;
}
function asNonCompoundGraph(g) {
  var simplified = new Graph({ multigraph: g.isMultigraph() }).setGraph(g.graph());
  forEach(g.nodes(), function(v) {
    if (!g.children(v).length) {
      simplified.setNode(v, g.node(v));
    }
  });
  forEach(g.edges(), function(e) {
    simplified.setEdge(e, g.edge(e));
  });
  return simplified;
}
function intersectRect$1(rect2, point) {
  var x = rect2.x;
  var y = rect2.y;
  var dx = point.x - x;
  var dy = point.y - y;
  var w = rect2.width / 2;
  var h = rect2.height / 2;
  if (!dx && !dy) {
    throw new Error("Not possible to find intersection inside of the rectangle");
  }
  var sx, sy;
  if (Math.abs(dy) * w > Math.abs(dx) * h) {
    if (dy < 0) {
      h = -h;
    }
    sx = h * dx / dy;
    sy = h;
  } else {
    if (dx < 0) {
      w = -w;
    }
    sx = w;
    sy = w * dy / dx;
  }
  return { x: x + sx, y: y + sy };
}
function buildLayerMatrix(g) {
  var layering = map(range(maxRank(g) + 1), function() {
    return [];
  });
  forEach(g.nodes(), function(v) {
    var node = g.node(v);
    var rank2 = node.rank;
    if (!isUndefined(rank2)) {
      layering[rank2][node.order] = v;
    }
  });
  return layering;
}
function normalizeRanks(g) {
  var min$1 = min(
    map(g.nodes(), function(v) {
      return g.node(v).rank;
    })
  );
  forEach(g.nodes(), function(v) {
    var node = g.node(v);
    if (has(node, "rank")) {
      node.rank -= min$1;
    }
  });
}
function removeEmptyRanks(g) {
  var offset = min(
    map(g.nodes(), function(v) {
      return g.node(v).rank;
    })
  );
  var layers = [];
  forEach(g.nodes(), function(v) {
    var rank2 = g.node(v).rank - offset;
    if (!layers[rank2]) {
      layers[rank2] = [];
    }
    layers[rank2].push(v);
  });
  var delta = 0;
  var nodeRankFactor = g.graph().nodeRankFactor;
  forEach(layers, function(vs, i) {
    if (isUndefined(vs) && i % nodeRankFactor !== 0) {
      --delta;
    } else if (delta) {
      forEach(vs, function(v) {
        g.node(v).rank += delta;
      });
    }
  });
}
function addBorderNode$1(g, prefix, rank2, order2) {
  var node = {
    width: 0,
    height: 0
  };
  if (arguments.length >= 4) {
    node.rank = rank2;
    node.order = order2;
  }
  return addDummyNode(g, "border", node, prefix);
}
function maxRank(g) {
  return max(
    map(g.nodes(), function(v) {
      var rank2 = g.node(v).rank;
      if (!isUndefined(rank2)) {
        return rank2;
      }
    })
  );
}
function partition(collection, fn) {
  var result = { lhs: [], rhs: [] };
  forEach(collection, function(value) {
    if (fn(value)) {
      result.lhs.push(value);
    } else {
      result.rhs.push(value);
    }
  });
  return result;
}
function time(name, fn) {
  var start = now();
  try {
    return fn();
  } finally {
    console.log(name + " time: " + (now() - start) + "ms");
  }
}
function notime(name, fn) {
  return fn();
}
function addBorderSegments(g) {
  function dfs2(v) {
    var children = g.children(v);
    var node = g.node(v);
    if (children.length) {
      forEach(children, dfs2);
    }
    if (has(node, "minRank")) {
      node.borderLeft = [];
      node.borderRight = [];
      for (var rank2 = node.minRank, maxRank2 = node.maxRank + 1; rank2 < maxRank2; ++rank2) {
        addBorderNode(g, "borderLeft", "_bl", v, node, rank2);
        addBorderNode(g, "borderRight", "_br", v, node, rank2);
      }
    }
  }
  forEach(g.children(), dfs2);
}
function addBorderNode(g, prop, prefix, sg, sgNode, rank2) {
  var label = { width: 0, height: 0, rank: rank2, borderType: prop };
  var prev = sgNode[prop][rank2 - 1];
  var curr = addDummyNode(g, "border", label, prefix);
  sgNode[prop][rank2] = curr;
  g.setParent(curr, sg);
  if (prev) {
    g.setEdge(prev, curr, { weight: 1 });
  }
}
function adjust(g) {
  var rankDir = g.graph().rankdir.toLowerCase();
  if (rankDir === "lr" || rankDir === "rl") {
    swapWidthHeight(g);
  }
}
function undo$1(g) {
  var rankDir = g.graph().rankdir.toLowerCase();
  if (rankDir === "bt" || rankDir === "rl") {
    reverseY(g);
  }
  if (rankDir === "lr" || rankDir === "rl") {
    swapXY(g);
    swapWidthHeight(g);
  }
}
function swapWidthHeight(g) {
  forEach(g.nodes(), function(v) {
    swapWidthHeightOne(g.node(v));
  });
  forEach(g.edges(), function(e) {
    swapWidthHeightOne(g.edge(e));
  });
}
function swapWidthHeightOne(attrs) {
  var w = attrs.width;
  attrs.width = attrs.height;
  attrs.height = w;
}
function reverseY(g) {
  forEach(g.nodes(), function(v) {
    reverseYOne(g.node(v));
  });
  forEach(g.edges(), function(e) {
    var edge = g.edge(e);
    forEach(edge.points, reverseYOne);
    if (has(edge, "y")) {
      reverseYOne(edge);
    }
  });
}
function reverseYOne(attrs) {
  attrs.y = -attrs.y;
}
function swapXY(g) {
  forEach(g.nodes(), function(v) {
    swapXYOne(g.node(v));
  });
  forEach(g.edges(), function(e) {
    var edge = g.edge(e);
    forEach(edge.points, swapXYOne);
    if (has(edge, "x")) {
      swapXYOne(edge);
    }
  });
}
function swapXYOne(attrs) {
  var x = attrs.x;
  attrs.x = attrs.y;
  attrs.y = x;
}
function run$1(g) {
  g.graph().dummyChains = [];
  forEach(g.edges(), function(edge) {
    normalizeEdge(g, edge);
  });
}
function normalizeEdge(g, e) {
  var v = e.v;
  var vRank = g.node(v).rank;
  var w = e.w;
  var wRank = g.node(w).rank;
  var name = e.name;
  var edgeLabel = g.edge(e);
  var labelRank = edgeLabel.labelRank;
  if (wRank === vRank + 1)
    return;
  g.removeEdge(e);
  var dummy, attrs, i;
  for (i = 0, ++vRank; vRank < wRank; ++i, ++vRank) {
    edgeLabel.points = [];
    attrs = {
      width: 0,
      height: 0,
      edgeLabel,
      edgeObj: e,
      rank: vRank
    };
    dummy = addDummyNode(g, "edge", attrs, "_d");
    if (vRank === labelRank) {
      attrs.width = edgeLabel.width;
      attrs.height = edgeLabel.height;
      attrs.dummy = "edge-label";
      attrs.labelpos = edgeLabel.labelpos;
    }
    g.setEdge(v, dummy, { weight: edgeLabel.weight }, name);
    if (i === 0) {
      g.graph().dummyChains.push(dummy);
    }
    v = dummy;
  }
  g.setEdge(v, w, { weight: edgeLabel.weight }, name);
}
function undo(g) {
  forEach(g.graph().dummyChains, function(v) {
    var node = g.node(v);
    var origLabel = node.edgeLabel;
    var w;
    g.setEdge(node.edgeObj, origLabel);
    while (node.dummy) {
      w = g.successors(v)[0];
      g.removeNode(v);
      origLabel.points.push({ x: node.x, y: node.y });
      if (node.dummy === "edge-label") {
        origLabel.x = node.x;
        origLabel.y = node.y;
        origLabel.width = node.width;
        origLabel.height = node.height;
      }
      v = w;
      node = g.node(v);
    }
  });
}
function longestPath(g) {
  var visited = {};
  function dfs2(v) {
    var label = g.node(v);
    if (has(visited, v)) {
      return label.rank;
    }
    visited[v] = true;
    var rank2 = min(
      map(g.outEdges(v), function(e) {
        return dfs2(e.w) - g.edge(e).minlen;
      })
    );
    if (rank2 === Number.POSITIVE_INFINITY || // return value of _.map([]) for Lodash 3
    rank2 === void 0 || // return value of _.map([]) for Lodash 4
    rank2 === null) {
      rank2 = 0;
    }
    return label.rank = rank2;
  }
  forEach(g.sources(), dfs2);
}
function slack(g, e) {
  return g.node(e.w).rank - g.node(e.v).rank - g.edge(e).minlen;
}
function feasibleTree(g) {
  var t = new Graph({ directed: false });
  var start = g.nodes()[0];
  var size = g.nodeCount();
  t.setNode(start, {});
  var edge, delta;
  while (tightTree(t, g) < size) {
    edge = findMinSlackEdge(t, g);
    delta = t.hasNode(edge.v) ? slack(g, edge) : -slack(g, edge);
    shiftRanks(t, g, delta);
  }
  return t;
}
function tightTree(t, g) {
  function dfs2(v) {
    forEach(g.nodeEdges(v), function(e) {
      var edgeV = e.v, w = v === edgeV ? e.w : edgeV;
      if (!t.hasNode(w) && !slack(g, e)) {
        t.setNode(w, {});
        t.setEdge(v, w, {});
        dfs2(w);
      }
    });
  }
  forEach(t.nodes(), dfs2);
  return t.nodeCount();
}
function findMinSlackEdge(t, g) {
  return minBy(g.edges(), function(e) {
    if (t.hasNode(e.v) !== t.hasNode(e.w)) {
      return slack(g, e);
    }
  });
}
function shiftRanks(t, g, delta) {
  forEach(t.nodes(), function(v) {
    g.node(v).rank += delta;
  });
}
function CycleException() {
}
CycleException.prototype = new Error();
function dfs$1(g, vs, order2) {
  if (!isArray(vs)) {
    vs = [vs];
  }
  var navigation = (g.isDirected() ? g.successors : g.neighbors).bind(g);
  var acc = [];
  var visited = {};
  forEach(vs, function(v) {
    if (!g.hasNode(v)) {
      throw new Error("Graph does not have node: " + v);
    }
    doDfs(g, v, order2 === "post", visited, navigation, acc);
  });
  return acc;
}
function doDfs(g, v, postorder2, visited, navigation, acc) {
  if (!has(visited, v)) {
    visited[v] = true;
    if (!postorder2) {
      acc.push(v);
    }
    forEach(navigation(v), function(w) {
      doDfs(g, w, postorder2, visited, navigation, acc);
    });
    if (postorder2) {
      acc.push(v);
    }
  }
}
function postorder$1(g, vs) {
  return dfs$1(g, vs, "post");
}
function preorder(g, vs) {
  return dfs$1(g, vs, "pre");
}
networkSimplex.initLowLimValues = initLowLimValues;
networkSimplex.initCutValues = initCutValues;
networkSimplex.calcCutValue = calcCutValue;
networkSimplex.leaveEdge = leaveEdge;
networkSimplex.enterEdge = enterEdge;
networkSimplex.exchangeEdges = exchangeEdges;
function networkSimplex(g) {
  g = simplify(g);
  longestPath(g);
  var t = feasibleTree(g);
  initLowLimValues(t);
  initCutValues(t, g);
  var e, f;
  while (e = leaveEdge(t)) {
    f = enterEdge(t, g, e);
    exchangeEdges(t, g, e, f);
  }
}
function initCutValues(t, g) {
  var vs = postorder$1(t, t.nodes());
  vs = vs.slice(0, vs.length - 1);
  forEach(vs, function(v) {
    assignCutValue(t, g, v);
  });
}
function assignCutValue(t, g, child) {
  var childLab = t.node(child);
  var parent = childLab.parent;
  t.edge(child, parent).cutvalue = calcCutValue(t, g, child);
}
function calcCutValue(t, g, child) {
  var childLab = t.node(child);
  var parent = childLab.parent;
  var childIsTail = true;
  var graphEdge = g.edge(child, parent);
  var cutValue = 0;
  if (!graphEdge) {
    childIsTail = false;
    graphEdge = g.edge(parent, child);
  }
  cutValue = graphEdge.weight;
  forEach(g.nodeEdges(child), function(e) {
    var isOutEdge = e.v === child, other = isOutEdge ? e.w : e.v;
    if (other !== parent) {
      var pointsToHead = isOutEdge === childIsTail, otherWeight = g.edge(e).weight;
      cutValue += pointsToHead ? otherWeight : -otherWeight;
      if (isTreeEdge(t, child, other)) {
        var otherCutValue = t.edge(child, other).cutvalue;
        cutValue += pointsToHead ? -otherCutValue : otherCutValue;
      }
    }
  });
  return cutValue;
}
function initLowLimValues(tree, root) {
  if (arguments.length < 2) {
    root = tree.nodes()[0];
  }
  dfsAssignLowLim(tree, {}, 1, root);
}
function dfsAssignLowLim(tree, visited, nextLim, v, parent) {
  var low = nextLim;
  var label = tree.node(v);
  visited[v] = true;
  forEach(tree.neighbors(v), function(w) {
    if (!has(visited, w)) {
      nextLim = dfsAssignLowLim(tree, visited, nextLim, w, v);
    }
  });
  label.low = low;
  label.lim = nextLim++;
  if (parent) {
    label.parent = parent;
  } else {
    delete label.parent;
  }
  return nextLim;
}
function leaveEdge(tree) {
  return find(tree.edges(), function(e) {
    return tree.edge(e).cutvalue < 0;
  });
}
function enterEdge(t, g, edge) {
  var v = edge.v;
  var w = edge.w;
  if (!g.hasEdge(v, w)) {
    v = edge.w;
    w = edge.v;
  }
  var vLabel = t.node(v);
  var wLabel = t.node(w);
  var tailLabel = vLabel;
  var flip = false;
  if (vLabel.lim > wLabel.lim) {
    tailLabel = wLabel;
    flip = true;
  }
  var candidates = filter(g.edges(), function(edge2) {
    return flip === isDescendant(t, t.node(edge2.v), tailLabel) && flip !== isDescendant(t, t.node(edge2.w), tailLabel);
  });
  return minBy(candidates, function(edge2) {
    return slack(g, edge2);
  });
}
function exchangeEdges(t, g, e, f) {
  var v = e.v;
  var w = e.w;
  t.removeEdge(v, w);
  t.setEdge(f.v, f.w, {});
  initLowLimValues(t);
  initCutValues(t, g);
  updateRanks(t, g);
}
function updateRanks(t, g) {
  var root = find(t.nodes(), function(v) {
    return !g.node(v).parent;
  });
  var vs = preorder(t, root);
  vs = vs.slice(1);
  forEach(vs, function(v) {
    var parent = t.node(v).parent, edge = g.edge(v, parent), flipped = false;
    if (!edge) {
      edge = g.edge(parent, v);
      flipped = true;
    }
    g.node(v).rank = g.node(parent).rank + (flipped ? edge.minlen : -edge.minlen);
  });
}
function isTreeEdge(tree, u, v) {
  return tree.hasEdge(u, v);
}
function isDescendant(tree, vLabel, rootLabel) {
  return rootLabel.low <= vLabel.lim && vLabel.lim <= rootLabel.lim;
}
function rank(g) {
  switch (g.graph().ranker) {
    case "network-simplex":
      networkSimplexRanker(g);
      break;
    case "tight-tree":
      tightTreeRanker(g);
      break;
    case "longest-path":
      longestPathRanker(g);
      break;
    default:
      networkSimplexRanker(g);
  }
}
var longestPathRanker = longestPath;
function tightTreeRanker(g) {
  longestPath(g);
  feasibleTree(g);
}
function networkSimplexRanker(g) {
  networkSimplex(g);
}
function run(g) {
  var root = addDummyNode(g, "root", {}, "_root");
  var depths = treeDepths(g);
  var height = max(values(depths)) - 1;
  var nodeSep = 2 * height + 1;
  g.graph().nestingRoot = root;
  forEach(g.edges(), function(e) {
    g.edge(e).minlen *= nodeSep;
  });
  var weight = sumWeights(g) + 1;
  forEach(g.children(), function(child) {
    dfs(g, root, nodeSep, weight, height, depths, child);
  });
  g.graph().nodeRankFactor = nodeSep;
}
function dfs(g, root, nodeSep, weight, height, depths, v) {
  var children = g.children(v);
  if (!children.length) {
    if (v !== root) {
      g.setEdge(root, v, { weight: 0, minlen: nodeSep });
    }
    return;
  }
  var top = addBorderNode$1(g, "_bt");
  var bottom = addBorderNode$1(g, "_bb");
  var label = g.node(v);
  g.setParent(top, v);
  label.borderTop = top;
  g.setParent(bottom, v);
  label.borderBottom = bottom;
  forEach(children, function(child) {
    dfs(g, root, nodeSep, weight, height, depths, child);
    var childNode = g.node(child);
    var childTop = childNode.borderTop ? childNode.borderTop : child;
    var childBottom = childNode.borderBottom ? childNode.borderBottom : child;
    var thisWeight = childNode.borderTop ? weight : 2 * weight;
    var minlen = childTop !== childBottom ? 1 : height - depths[v] + 1;
    g.setEdge(top, childTop, {
      weight: thisWeight,
      minlen,
      nestingEdge: true
    });
    g.setEdge(childBottom, bottom, {
      weight: thisWeight,
      minlen,
      nestingEdge: true
    });
  });
  if (!g.parent(v)) {
    g.setEdge(root, top, { weight: 0, minlen: height + depths[v] });
  }
}
function treeDepths(g) {
  var depths = {};
  function dfs2(v, depth) {
    var children = g.children(v);
    if (children && children.length) {
      forEach(children, function(child) {
        dfs2(child, depth + 1);
      });
    }
    depths[v] = depth;
  }
  forEach(g.children(), function(v) {
    dfs2(v, 1);
  });
  return depths;
}
function sumWeights(g) {
  return reduce(
    g.edges(),
    function(acc, e) {
      return acc + g.edge(e).weight;
    },
    0
  );
}
function cleanup(g) {
  var graphLabel = g.graph();
  g.removeNode(graphLabel.nestingRoot);
  delete graphLabel.nestingRoot;
  forEach(g.edges(), function(e) {
    var edge = g.edge(e);
    if (edge.nestingEdge) {
      g.removeEdge(e);
    }
  });
}
function addSubgraphConstraints(g, cg, vs) {
  var prev = {}, rootPrev;
  forEach(vs, function(v) {
    var child = g.parent(v), parent, prevChild;
    while (child) {
      parent = g.parent(child);
      if (parent) {
        prevChild = prev[parent];
        prev[parent] = child;
      } else {
        prevChild = rootPrev;
        rootPrev = child;
      }
      if (prevChild && prevChild !== child) {
        cg.setEdge(prevChild, child);
        return;
      }
      child = parent;
    }
  });
}
function buildLayerGraph(g, rank2, relationship) {
  var root = createRootNode(g), result = new Graph({ compound: true }).setGraph({ root }).setDefaultNodeLabel(function(v) {
    return g.node(v);
  });
  forEach(g.nodes(), function(v) {
    var node = g.node(v), parent = g.parent(v);
    if (node.rank === rank2 || node.minRank <= rank2 && rank2 <= node.maxRank) {
      result.setNode(v);
      result.setParent(v, parent || root);
      forEach(g[relationship](v), function(e) {
        var u = e.v === v ? e.w : e.v, edge = result.edge(u, v), weight = !isUndefined(edge) ? edge.weight : 0;
        result.setEdge(u, v, { weight: g.edge(e).weight + weight });
      });
      if (has(node, "minRank")) {
        result.setNode(v, {
          borderLeft: node.borderLeft[rank2],
          borderRight: node.borderRight[rank2]
        });
      }
    }
  });
  return result;
}
function createRootNode(g) {
  var v;
  while (g.hasNode(v = uniqueId("_root")))
    ;
  return v;
}
function crossCount(g, layering) {
  var cc = 0;
  for (var i = 1; i < layering.length; ++i) {
    cc += twoLayerCrossCount(g, layering[i - 1], layering[i]);
  }
  return cc;
}
function twoLayerCrossCount(g, northLayer, southLayer) {
  var southPos = zipObject(
    southLayer,
    map(southLayer, function(v, i) {
      return i;
    })
  );
  var southEntries = flatten(
    map(northLayer, function(v) {
      return sortBy(
        map(g.outEdges(v), function(e) {
          return { pos: southPos[e.w], weight: g.edge(e).weight };
        }),
        "pos"
      );
    })
  );
  var firstIndex = 1;
  while (firstIndex < southLayer.length)
    firstIndex <<= 1;
  var treeSize = 2 * firstIndex - 1;
  firstIndex -= 1;
  var tree = map(new Array(treeSize), function() {
    return 0;
  });
  var cc = 0;
  forEach(
    // @ts-expect-error
    southEntries.forEach(function(entry) {
      var index = entry.pos + firstIndex;
      tree[index] += entry.weight;
      var weightSum = 0;
      while (index > 0) {
        if (index % 2) {
          weightSum += tree[index + 1];
        }
        index = index - 1 >> 1;
        tree[index] += entry.weight;
      }
      cc += entry.weight * weightSum;
    })
  );
  return cc;
}
function initOrder(g) {
  var visited = {};
  var simpleNodes = filter(g.nodes(), function(v) {
    return !g.children(v).length;
  });
  var maxRank2 = max(
    map(simpleNodes, function(v) {
      return g.node(v).rank;
    })
  );
  var layers = map(range(maxRank2 + 1), function() {
    return [];
  });
  function dfs2(v) {
    if (has(visited, v))
      return;
    visited[v] = true;
    var node = g.node(v);
    layers[node.rank].push(v);
    forEach(g.successors(v), dfs2);
  }
  var orderedVs = sortBy(simpleNodes, function(v) {
    return g.node(v).rank;
  });
  forEach(orderedVs, dfs2);
  return layers;
}
function barycenter(g, movable) {
  return map(movable, function(v) {
    var inV = g.inEdges(v);
    if (!inV.length) {
      return { v };
    } else {
      var result = reduce(
        inV,
        function(acc, e) {
          var edge = g.edge(e), nodeU = g.node(e.v);
          return {
            sum: acc.sum + edge.weight * nodeU.order,
            weight: acc.weight + edge.weight
          };
        },
        { sum: 0, weight: 0 }
      );
      return {
        v,
        barycenter: result.sum / result.weight,
        weight: result.weight
      };
    }
  });
}
function resolveConflicts(entries, cg) {
  var mappedEntries = {};
  forEach(entries, function(entry, i) {
    var tmp = mappedEntries[entry.v] = {
      indegree: 0,
      in: [],
      out: [],
      vs: [entry.v],
      i
    };
    if (!isUndefined(entry.barycenter)) {
      tmp.barycenter = entry.barycenter;
      tmp.weight = entry.weight;
    }
  });
  forEach(cg.edges(), function(e) {
    var entryV = mappedEntries[e.v];
    var entryW = mappedEntries[e.w];
    if (!isUndefined(entryV) && !isUndefined(entryW)) {
      entryW.indegree++;
      entryV.out.push(mappedEntries[e.w]);
    }
  });
  var sourceSet = filter(mappedEntries, function(entry) {
    return !entry.indegree;
  });
  return doResolveConflicts(sourceSet);
}
function doResolveConflicts(sourceSet) {
  var entries = [];
  function handleIn(vEntry) {
    return function(uEntry) {
      if (uEntry.merged) {
        return;
      }
      if (isUndefined(uEntry.barycenter) || isUndefined(vEntry.barycenter) || uEntry.barycenter >= vEntry.barycenter) {
        mergeEntries(vEntry, uEntry);
      }
    };
  }
  function handleOut(vEntry) {
    return function(wEntry) {
      wEntry["in"].push(vEntry);
      if (--wEntry.indegree === 0) {
        sourceSet.push(wEntry);
      }
    };
  }
  while (sourceSet.length) {
    var entry = sourceSet.pop();
    entries.push(entry);
    forEach(entry["in"].reverse(), handleIn(entry));
    forEach(entry.out, handleOut(entry));
  }
  return map(
    filter(entries, function(entry2) {
      return !entry2.merged;
    }),
    function(entry2) {
      return pick(entry2, ["vs", "i", "barycenter", "weight"]);
    }
  );
}
function mergeEntries(target, source) {
  var sum = 0;
  var weight = 0;
  if (target.weight) {
    sum += target.barycenter * target.weight;
    weight += target.weight;
  }
  if (source.weight) {
    sum += source.barycenter * source.weight;
    weight += source.weight;
  }
  target.vs = source.vs.concat(target.vs);
  target.barycenter = sum / weight;
  target.weight = weight;
  target.i = Math.min(source.i, target.i);
  source.merged = true;
}
function sort(entries, biasRight) {
  var parts = partition(entries, function(entry) {
    return has(entry, "barycenter");
  });
  var sortable = parts.lhs, unsortable = sortBy(parts.rhs, function(entry) {
    return -entry.i;
  }), vs = [], sum = 0, weight = 0, vsIndex = 0;
  sortable.sort(compareWithBias(!!biasRight));
  vsIndex = consumeUnsortable(vs, unsortable, vsIndex);
  forEach(sortable, function(entry) {
    vsIndex += entry.vs.length;
    vs.push(entry.vs);
    sum += entry.barycenter * entry.weight;
    weight += entry.weight;
    vsIndex = consumeUnsortable(vs, unsortable, vsIndex);
  });
  var result = { vs: flatten(vs) };
  if (weight) {
    result.barycenter = sum / weight;
    result.weight = weight;
  }
  return result;
}
function consumeUnsortable(vs, unsortable, index) {
  var last$1;
  while (unsortable.length && (last$1 = last(unsortable)).i <= index) {
    unsortable.pop();
    vs.push(last$1.vs);
    index++;
  }
  return index;
}
function compareWithBias(bias) {
  return function(entryV, entryW) {
    if (entryV.barycenter < entryW.barycenter) {
      return -1;
    } else if (entryV.barycenter > entryW.barycenter) {
      return 1;
    }
    return !bias ? entryV.i - entryW.i : entryW.i - entryV.i;
  };
}
function sortSubgraph(g, v, cg, biasRight) {
  var movable = g.children(v);
  var node = g.node(v);
  var bl = node ? node.borderLeft : void 0;
  var br = node ? node.borderRight : void 0;
  var subgraphs = {};
  if (bl) {
    movable = filter(movable, function(w) {
      return w !== bl && w !== br;
    });
  }
  var barycenters = barycenter(g, movable);
  forEach(barycenters, function(entry) {
    if (g.children(entry.v).length) {
      var subgraphResult = sortSubgraph(g, entry.v, cg, biasRight);
      subgraphs[entry.v] = subgraphResult;
      if (has(subgraphResult, "barycenter")) {
        mergeBarycenters(entry, subgraphResult);
      }
    }
  });
  var entries = resolveConflicts(barycenters, cg);
  expandSubgraphs(entries, subgraphs);
  var result = sort(entries, biasRight);
  if (bl) {
    result.vs = flatten([bl, result.vs, br]);
    if (g.predecessors(bl).length) {
      var blPred = g.node(g.predecessors(bl)[0]), brPred = g.node(g.predecessors(br)[0]);
      if (!has(result, "barycenter")) {
        result.barycenter = 0;
        result.weight = 0;
      }
      result.barycenter = (result.barycenter * result.weight + blPred.order + brPred.order) / (result.weight + 2);
      result.weight += 2;
    }
  }
  return result;
}
function expandSubgraphs(entries, subgraphs) {
  forEach(entries, function(entry) {
    entry.vs = flatten(
      entry.vs.map(function(v) {
        if (subgraphs[v]) {
          return subgraphs[v].vs;
        }
        return v;
      })
    );
  });
}
function mergeBarycenters(target, other) {
  if (!isUndefined(target.barycenter)) {
    target.barycenter = (target.barycenter * target.weight + other.barycenter * other.weight) / (target.weight + other.weight);
    target.weight += other.weight;
  } else {
    target.barycenter = other.barycenter;
    target.weight = other.weight;
  }
}
function order(g) {
  var maxRank$1 = maxRank(g), downLayerGraphs = buildLayerGraphs(g, range(1, maxRank$1 + 1), "inEdges"), upLayerGraphs = buildLayerGraphs(g, range(maxRank$1 - 1, -1, -1), "outEdges");
  var layering = initOrder(g);
  assignOrder(g, layering);
  var bestCC = Number.POSITIVE_INFINITY, best;
  for (var i = 0, lastBest = 0; lastBest < 4; ++i, ++lastBest) {
    sweepLayerGraphs(i % 2 ? downLayerGraphs : upLayerGraphs, i % 4 >= 2);
    layering = buildLayerMatrix(g);
    var cc = crossCount(g, layering);
    if (cc < bestCC) {
      lastBest = 0;
      best = cloneDeep(layering);
      bestCC = cc;
    }
  }
  assignOrder(g, best);
}
function buildLayerGraphs(g, ranks, relationship) {
  return map(ranks, function(rank2) {
    return buildLayerGraph(g, rank2, relationship);
  });
}
function sweepLayerGraphs(layerGraphs, biasRight) {
  var cg = new Graph();
  forEach(layerGraphs, function(lg) {
    var root = lg.graph().root;
    var sorted = sortSubgraph(lg, root, cg, biasRight);
    forEach(sorted.vs, function(v, i) {
      lg.node(v).order = i;
    });
    addSubgraphConstraints(lg, cg, sorted.vs);
  });
}
function assignOrder(g, layering) {
  forEach(layering, function(layer) {
    forEach(layer, function(v, i) {
      g.node(v).order = i;
    });
  });
}
function parentDummyChains(g) {
  var postorderNums = postorder(g);
  forEach(g.graph().dummyChains, function(v) {
    var node = g.node(v);
    var edgeObj = node.edgeObj;
    var pathData = findPath(g, postorderNums, edgeObj.v, edgeObj.w);
    var path = pathData.path;
    var lca = pathData.lca;
    var pathIdx = 0;
    var pathV = path[pathIdx];
    var ascending = true;
    while (v !== edgeObj.w) {
      node = g.node(v);
      if (ascending) {
        while ((pathV = path[pathIdx]) !== lca && g.node(pathV).maxRank < node.rank) {
          pathIdx++;
        }
        if (pathV === lca) {
          ascending = false;
        }
      }
      if (!ascending) {
        while (pathIdx < path.length - 1 && g.node(pathV = path[pathIdx + 1]).minRank <= node.rank) {
          pathIdx++;
        }
        pathV = path[pathIdx];
      }
      g.setParent(v, pathV);
      v = g.successors(v)[0];
    }
  });
}
function findPath(g, postorderNums, v, w) {
  var vPath = [];
  var wPath = [];
  var low = Math.min(postorderNums[v].low, postorderNums[w].low);
  var lim = Math.max(postorderNums[v].lim, postorderNums[w].lim);
  var parent;
  var lca;
  parent = v;
  do {
    parent = g.parent(parent);
    vPath.push(parent);
  } while (parent && (postorderNums[parent].low > low || lim > postorderNums[parent].lim));
  lca = parent;
  parent = w;
  while ((parent = g.parent(parent)) !== lca) {
    wPath.push(parent);
  }
  return { path: vPath.concat(wPath.reverse()), lca };
}
function postorder(g) {
  var result = {};
  var lim = 0;
  function dfs2(v) {
    var low = lim;
    forEach(g.children(v), dfs2);
    result[v] = { low, lim: lim++ };
  }
  forEach(g.children(), dfs2);
  return result;
}
function findType1Conflicts(g, layering) {
  var conflicts = {};
  function visitLayer(prevLayer, layer) {
    var k0 = 0, scanPos = 0, prevLayerLength = prevLayer.length, lastNode = last(layer);
    forEach(layer, function(v, i) {
      var w = findOtherInnerSegmentNode(g, v), k1 = w ? g.node(w).order : prevLayerLength;
      if (w || v === lastNode) {
        forEach(layer.slice(scanPos, i + 1), function(scanNode) {
          forEach(g.predecessors(scanNode), function(u) {
            var uLabel = g.node(u), uPos = uLabel.order;
            if ((uPos < k0 || k1 < uPos) && !(uLabel.dummy && g.node(scanNode).dummy)) {
              addConflict(conflicts, u, scanNode);
            }
          });
        });
        scanPos = i + 1;
        k0 = k1;
      }
    });
    return layer;
  }
  reduce(layering, visitLayer);
  return conflicts;
}
function findType2Conflicts(g, layering) {
  var conflicts = {};
  function scan(south, southPos, southEnd, prevNorthBorder, nextNorthBorder) {
    var v;
    forEach(range(southPos, southEnd), function(i) {
      v = south[i];
      if (g.node(v).dummy) {
        forEach(g.predecessors(v), function(u) {
          var uNode = g.node(u);
          if (uNode.dummy && (uNode.order < prevNorthBorder || uNode.order > nextNorthBorder)) {
            addConflict(conflicts, u, v);
          }
        });
      }
    });
  }
  function visitLayer(north, south) {
    var prevNorthPos = -1, nextNorthPos, southPos = 0;
    forEach(south, function(v, southLookahead) {
      if (g.node(v).dummy === "border") {
        var predecessors = g.predecessors(v);
        if (predecessors.length) {
          nextNorthPos = g.node(predecessors[0]).order;
          scan(south, southPos, southLookahead, prevNorthPos, nextNorthPos);
          southPos = southLookahead;
          prevNorthPos = nextNorthPos;
        }
      }
      scan(south, southPos, south.length, nextNorthPos, north.length);
    });
    return south;
  }
  reduce(layering, visitLayer);
  return conflicts;
}
function findOtherInnerSegmentNode(g, v) {
  if (g.node(v).dummy) {
    return find(g.predecessors(v), function(u) {
      return g.node(u).dummy;
    });
  }
}
function addConflict(conflicts, v, w) {
  if (v > w) {
    var tmp = v;
    v = w;
    w = tmp;
  }
  var conflictsV = conflicts[v];
  if (!conflictsV) {
    conflicts[v] = conflictsV = {};
  }
  conflictsV[w] = true;
}
function hasConflict(conflicts, v, w) {
  if (v > w) {
    var tmp = v;
    v = w;
    w = tmp;
  }
  return has(conflicts[v], w);
}
function verticalAlignment(g, layering, conflicts, neighborFn) {
  var root = {}, align = {}, pos = {};
  forEach(layering, function(layer) {
    forEach(layer, function(v, order2) {
      root[v] = v;
      align[v] = v;
      pos[v] = order2;
    });
  });
  forEach(layering, function(layer) {
    var prevIdx = -1;
    forEach(layer, function(v) {
      var ws = neighborFn(v);
      if (ws.length) {
        ws = sortBy(ws, function(w2) {
          return pos[w2];
        });
        var mp = (ws.length - 1) / 2;
        for (var i = Math.floor(mp), il = Math.ceil(mp); i <= il; ++i) {
          var w = ws[i];
          if (align[v] === v && prevIdx < pos[w] && !hasConflict(conflicts, v, w)) {
            align[w] = v;
            align[v] = root[v] = root[w];
            prevIdx = pos[w];
          }
        }
      }
    });
  });
  return { root, align };
}
function horizontalCompaction(g, layering, root, align, reverseSep) {
  var xs = {}, blockG = buildBlockGraph(g, layering, root, reverseSep), borderType = reverseSep ? "borderLeft" : "borderRight";
  function iterate(setXsFunc, nextNodesFunc) {
    var stack = blockG.nodes();
    var elem = stack.pop();
    var visited = {};
    while (elem) {
      if (visited[elem]) {
        setXsFunc(elem);
      } else {
        visited[elem] = true;
        stack.push(elem);
        stack = stack.concat(nextNodesFunc(elem));
      }
      elem = stack.pop();
    }
  }
  function pass1(elem) {
    xs[elem] = blockG.inEdges(elem).reduce(function(acc, e) {
      return Math.max(acc, xs[e.v] + blockG.edge(e));
    }, 0);
  }
  function pass2(elem) {
    var min2 = blockG.outEdges(elem).reduce(function(acc, e) {
      return Math.min(acc, xs[e.w] - blockG.edge(e));
    }, Number.POSITIVE_INFINITY);
    var node = g.node(elem);
    if (min2 !== Number.POSITIVE_INFINITY && node.borderType !== borderType) {
      xs[elem] = Math.max(xs[elem], min2);
    }
  }
  iterate(pass1, blockG.predecessors.bind(blockG));
  iterate(pass2, blockG.successors.bind(blockG));
  forEach(align, function(v) {
    xs[v] = xs[root[v]];
  });
  return xs;
}
function buildBlockGraph(g, layering, root, reverseSep) {
  var blockGraph = new Graph(), graphLabel = g.graph(), sepFn = sep(graphLabel.nodesep, graphLabel.edgesep, reverseSep);
  forEach(layering, function(layer) {
    var u;
    forEach(layer, function(v) {
      var vRoot = root[v];
      blockGraph.setNode(vRoot);
      if (u) {
        var uRoot = root[u], prevMax = blockGraph.edge(uRoot, vRoot);
        blockGraph.setEdge(uRoot, vRoot, Math.max(sepFn(g, v, u), prevMax || 0));
      }
      u = v;
    });
  });
  return blockGraph;
}
function findSmallestWidthAlignment(g, xss) {
  return minBy(values(xss), function(xs) {
    var max2 = Number.NEGATIVE_INFINITY;
    var min2 = Number.POSITIVE_INFINITY;
    forIn(xs, function(x, v) {
      var halfWidth = width(g, v) / 2;
      max2 = Math.max(x + halfWidth, max2);
      min2 = Math.min(x - halfWidth, min2);
    });
    return max2 - min2;
  });
}
function alignCoordinates(xss, alignTo) {
  var alignToVals = values(alignTo), alignToMin = min(alignToVals), alignToMax = max(alignToVals);
  forEach(["u", "d"], function(vert) {
    forEach(["l", "r"], function(horiz) {
      var alignment = vert + horiz, xs = xss[alignment], delta;
      if (xs === alignTo)
        return;
      var xsVals = values(xs);
      delta = horiz === "l" ? alignToMin - min(xsVals) : alignToMax - max(xsVals);
      if (delta) {
        xss[alignment] = mapValues(xs, function(x) {
          return x + delta;
        });
      }
    });
  });
}
function balance(xss, align) {
  return mapValues(xss.ul, function(ignore, v) {
    if (align) {
      return xss[align.toLowerCase()][v];
    } else {
      var xs = sortBy(map(xss, v));
      return (xs[1] + xs[2]) / 2;
    }
  });
}
function positionX(g) {
  var layering = buildLayerMatrix(g);
  var conflicts = merge$1(findType1Conflicts(g, layering), findType2Conflicts(g, layering));
  var xss = {};
  var adjustedLayering;
  forEach(["u", "d"], function(vert) {
    adjustedLayering = vert === "u" ? layering : values(layering).reverse();
    forEach(["l", "r"], function(horiz) {
      if (horiz === "r") {
        adjustedLayering = map(adjustedLayering, function(inner) {
          return values(inner).reverse();
        });
      }
      var neighborFn = (vert === "u" ? g.predecessors : g.successors).bind(g);
      var align = verticalAlignment(g, adjustedLayering, conflicts, neighborFn);
      var xs = horizontalCompaction(g, adjustedLayering, align.root, align.align, horiz === "r");
      if (horiz === "r") {
        xs = mapValues(xs, function(x) {
          return -x;
        });
      }
      xss[vert + horiz] = xs;
    });
  });
  var smallestWidth = findSmallestWidthAlignment(g, xss);
  alignCoordinates(xss, smallestWidth);
  return balance(xss, g.graph().align);
}
function sep(nodeSep, edgeSep, reverseSep) {
  return function(g, v, w) {
    var vLabel = g.node(v);
    var wLabel = g.node(w);
    var sum = 0;
    var delta;
    sum += vLabel.width / 2;
    if (has(vLabel, "labelpos")) {
      switch (vLabel.labelpos.toLowerCase()) {
        case "l":
          delta = -vLabel.width / 2;
          break;
        case "r":
          delta = vLabel.width / 2;
          break;
      }
    }
    if (delta) {
      sum += reverseSep ? delta : -delta;
    }
    delta = 0;
    sum += (vLabel.dummy ? edgeSep : nodeSep) / 2;
    sum += (wLabel.dummy ? edgeSep : nodeSep) / 2;
    sum += wLabel.width / 2;
    if (has(wLabel, "labelpos")) {
      switch (wLabel.labelpos.toLowerCase()) {
        case "l":
          delta = wLabel.width / 2;
          break;
        case "r":
          delta = -wLabel.width / 2;
          break;
      }
    }
    if (delta) {
      sum += reverseSep ? delta : -delta;
    }
    delta = 0;
    return sum;
  };
}
function width(g, v) {
  return g.node(v).width;
}
function position(g) {
  g = asNonCompoundGraph(g);
  positionY(g);
  forOwn(positionX(g), function(x, v) {
    g.node(v).x = x;
  });
}
function positionY(g) {
  var layering = buildLayerMatrix(g);
  var rankSep = g.graph().ranksep;
  var prevY = 0;
  forEach(layering, function(layer) {
    var maxHeight = max(
      map(layer, function(v) {
        return g.node(v).height;
      })
    );
    forEach(layer, function(v) {
      g.node(v).y = prevY + maxHeight / 2;
    });
    prevY += maxHeight + rankSep;
  });
}
function layout(g, opts) {
  var time$1 = opts && opts.debugTiming ? time : notime;
  time$1("layout", function() {
    var layoutGraph = time$1("  buildLayoutGraph", function() {
      return buildLayoutGraph(g);
    });
    time$1("  runLayout", function() {
      runLayout(layoutGraph, time$1);
    });
    time$1("  updateInputGraph", function() {
      updateInputGraph(g, layoutGraph);
    });
  });
}
function runLayout(g, time2) {
  time2("    makeSpaceForEdgeLabels", function() {
    makeSpaceForEdgeLabels(g);
  });
  time2("    removeSelfEdges", function() {
    removeSelfEdges(g);
  });
  time2("    acyclic", function() {
    run$2(g);
  });
  time2("    nestingGraph.run", function() {
    run(g);
  });
  time2("    rank", function() {
    rank(asNonCompoundGraph(g));
  });
  time2("    injectEdgeLabelProxies", function() {
    injectEdgeLabelProxies(g);
  });
  time2("    removeEmptyRanks", function() {
    removeEmptyRanks(g);
  });
  time2("    nestingGraph.cleanup", function() {
    cleanup(g);
  });
  time2("    normalizeRanks", function() {
    normalizeRanks(g);
  });
  time2("    assignRankMinMax", function() {
    assignRankMinMax(g);
  });
  time2("    removeEdgeLabelProxies", function() {
    removeEdgeLabelProxies(g);
  });
  time2("    normalize.run", function() {
    run$1(g);
  });
  time2("    parentDummyChains", function() {
    parentDummyChains(g);
  });
  time2("    addBorderSegments", function() {
    addBorderSegments(g);
  });
  time2("    order", function() {
    order(g);
  });
  time2("    insertSelfEdges", function() {
    insertSelfEdges(g);
  });
  time2("    adjustCoordinateSystem", function() {
    adjust(g);
  });
  time2("    position", function() {
    position(g);
  });
  time2("    positionSelfEdges", function() {
    positionSelfEdges(g);
  });
  time2("    removeBorderNodes", function() {
    removeBorderNodes(g);
  });
  time2("    normalize.undo", function() {
    undo(g);
  });
  time2("    fixupEdgeLabelCoords", function() {
    fixupEdgeLabelCoords(g);
  });
  time2("    undoCoordinateSystem", function() {
    undo$1(g);
  });
  time2("    translateGraph", function() {
    translateGraph(g);
  });
  time2("    assignNodeIntersects", function() {
    assignNodeIntersects(g);
  });
  time2("    reversePoints", function() {
    reversePointsForReversedEdges(g);
  });
  time2("    acyclic.undo", function() {
    undo$2(g);
  });
}
function updateInputGraph(inputGraph, layoutGraph) {
  forEach(inputGraph.nodes(), function(v) {
    var inputLabel = inputGraph.node(v);
    var layoutLabel = layoutGraph.node(v);
    if (inputLabel) {
      inputLabel.x = layoutLabel.x;
      inputLabel.y = layoutLabel.y;
      if (layoutGraph.children(v).length) {
        inputLabel.width = layoutLabel.width;
        inputLabel.height = layoutLabel.height;
      }
    }
  });
  forEach(inputGraph.edges(), function(e) {
    var inputLabel = inputGraph.edge(e);
    var layoutLabel = layoutGraph.edge(e);
    inputLabel.points = layoutLabel.points;
    if (has(layoutLabel, "x")) {
      inputLabel.x = layoutLabel.x;
      inputLabel.y = layoutLabel.y;
    }
  });
  inputGraph.graph().width = layoutGraph.graph().width;
  inputGraph.graph().height = layoutGraph.graph().height;
}
var graphNumAttrs = ["nodesep", "edgesep", "ranksep", "marginx", "marginy"];
var graphDefaults = { ranksep: 50, edgesep: 20, nodesep: 50, rankdir: "tb" };
var graphAttrs = ["acyclicer", "ranker", "rankdir", "align"];
var nodeNumAttrs = ["width", "height"];
var nodeDefaults = { width: 0, height: 0 };
var edgeNumAttrs = ["minlen", "weight", "width", "height", "labeloffset"];
var edgeDefaults = {
  minlen: 1,
  weight: 1,
  width: 0,
  height: 0,
  labeloffset: 10,
  labelpos: "r"
};
var edgeAttrs = ["labelpos"];
function buildLayoutGraph(inputGraph) {
  var g = new Graph({ multigraph: true, compound: true });
  var graph = canonicalize(inputGraph.graph());
  g.setGraph(
    merge$1({}, graphDefaults, selectNumberAttrs(graph, graphNumAttrs), pick(graph, graphAttrs))
  );
  forEach(inputGraph.nodes(), function(v) {
    var node = canonicalize(inputGraph.node(v));
    g.setNode(v, defaults(selectNumberAttrs(node, nodeNumAttrs), nodeDefaults));
    g.setParent(v, inputGraph.parent(v));
  });
  forEach(inputGraph.edges(), function(e) {
    var edge = canonicalize(inputGraph.edge(e));
    g.setEdge(
      e,
      merge$1({}, edgeDefaults, selectNumberAttrs(edge, edgeNumAttrs), pick(edge, edgeAttrs))
    );
  });
  return g;
}
function makeSpaceForEdgeLabels(g) {
  var graph = g.graph();
  graph.ranksep /= 2;
  forEach(g.edges(), function(e) {
    var edge = g.edge(e);
    edge.minlen *= 2;
    if (edge.labelpos.toLowerCase() !== "c") {
      if (graph.rankdir === "TB" || graph.rankdir === "BT") {
        edge.width += edge.labeloffset;
      } else {
        edge.height += edge.labeloffset;
      }
    }
  });
}
function injectEdgeLabelProxies(g) {
  forEach(g.edges(), function(e) {
    var edge = g.edge(e);
    if (edge.width && edge.height) {
      var v = g.node(e.v);
      var w = g.node(e.w);
      var label = { rank: (w.rank - v.rank) / 2 + v.rank, e };
      addDummyNode(g, "edge-proxy", label, "_ep");
    }
  });
}
function assignRankMinMax(g) {
  var maxRank2 = 0;
  forEach(g.nodes(), function(v) {
    var node = g.node(v);
    if (node.borderTop) {
      node.minRank = g.node(node.borderTop).rank;
      node.maxRank = g.node(node.borderBottom).rank;
      maxRank2 = max(maxRank2, node.maxRank);
    }
  });
  g.graph().maxRank = maxRank2;
}
function removeEdgeLabelProxies(g) {
  forEach(g.nodes(), function(v) {
    var node = g.node(v);
    if (node.dummy === "edge-proxy") {
      g.edge(node.e).labelRank = node.rank;
      g.removeNode(v);
    }
  });
}
function translateGraph(g) {
  var minX = Number.POSITIVE_INFINITY;
  var maxX = 0;
  var minY = Number.POSITIVE_INFINITY;
  var maxY = 0;
  var graphLabel = g.graph();
  var marginX = graphLabel.marginx || 0;
  var marginY = graphLabel.marginy || 0;
  function getExtremes(attrs) {
    var x = attrs.x;
    var y = attrs.y;
    var w = attrs.width;
    var h = attrs.height;
    minX = Math.min(minX, x - w / 2);
    maxX = Math.max(maxX, x + w / 2);
    minY = Math.min(minY, y - h / 2);
    maxY = Math.max(maxY, y + h / 2);
  }
  forEach(g.nodes(), function(v) {
    getExtremes(g.node(v));
  });
  forEach(g.edges(), function(e) {
    var edge = g.edge(e);
    if (has(edge, "x")) {
      getExtremes(edge);
    }
  });
  minX -= marginX;
  minY -= marginY;
  forEach(g.nodes(), function(v) {
    var node = g.node(v);
    node.x -= minX;
    node.y -= minY;
  });
  forEach(g.edges(), function(e) {
    var edge = g.edge(e);
    forEach(edge.points, function(p) {
      p.x -= minX;
      p.y -= minY;
    });
    if (has(edge, "x")) {
      edge.x -= minX;
    }
    if (has(edge, "y")) {
      edge.y -= minY;
    }
  });
  graphLabel.width = maxX - minX + marginX;
  graphLabel.height = maxY - minY + marginY;
}
function assignNodeIntersects(g) {
  forEach(g.edges(), function(e) {
    var edge = g.edge(e);
    var nodeV = g.node(e.v);
    var nodeW = g.node(e.w);
    var p1, p2;
    if (!edge.points) {
      edge.points = [];
      p1 = nodeW;
      p2 = nodeV;
    } else {
      p1 = edge.points[0];
      p2 = edge.points[edge.points.length - 1];
    }
    edge.points.unshift(intersectRect$1(nodeV, p1));
    edge.points.push(intersectRect$1(nodeW, p2));
  });
}
function fixupEdgeLabelCoords(g) {
  forEach(g.edges(), function(e) {
    var edge = g.edge(e);
    if (has(edge, "x")) {
      if (edge.labelpos === "l" || edge.labelpos === "r") {
        edge.width -= edge.labeloffset;
      }
      switch (edge.labelpos) {
        case "l":
          edge.x -= edge.width / 2 + edge.labeloffset;
          break;
        case "r":
          edge.x += edge.width / 2 + edge.labeloffset;
          break;
      }
    }
  });
}
function reversePointsForReversedEdges(g) {
  forEach(g.edges(), function(e) {
    var edge = g.edge(e);
    if (edge.reversed) {
      edge.points.reverse();
    }
  });
}
function removeBorderNodes(g) {
  forEach(g.nodes(), function(v) {
    if (g.children(v).length) {
      var node = g.node(v);
      var t = g.node(node.borderTop);
      var b = g.node(node.borderBottom);
      var l = g.node(last(node.borderLeft));
      var r = g.node(last(node.borderRight));
      node.width = Math.abs(r.x - l.x);
      node.height = Math.abs(b.y - t.y);
      node.x = l.x + node.width / 2;
      node.y = t.y + node.height / 2;
    }
  });
  forEach(g.nodes(), function(v) {
    if (g.node(v).dummy === "border") {
      g.removeNode(v);
    }
  });
}
function removeSelfEdges(g) {
  forEach(g.edges(), function(e) {
    if (e.v === e.w) {
      var node = g.node(e.v);
      if (!node.selfEdges) {
        node.selfEdges = [];
      }
      node.selfEdges.push({ e, label: g.edge(e) });
      g.removeEdge(e);
    }
  });
}
function insertSelfEdges(g) {
  var layers = buildLayerMatrix(g);
  forEach(layers, function(layer) {
    var orderShift = 0;
    forEach(layer, function(v, i) {
      var node = g.node(v);
      node.order = i + orderShift;
      forEach(node.selfEdges, function(selfEdge) {
        addDummyNode(
          g,
          "selfedge",
          {
            width: selfEdge.label.width,
            height: selfEdge.label.height,
            rank: node.rank,
            order: i + ++orderShift,
            e: selfEdge.e,
            label: selfEdge.label
          },
          "_se"
        );
      });
      delete node.selfEdges;
    });
  });
}
function positionSelfEdges(g) {
  forEach(g.nodes(), function(v) {
    var node = g.node(v);
    if (node.dummy === "selfedge") {
      var selfNode = g.node(node.e.v);
      var x = selfNode.x + selfNode.width / 2;
      var y = selfNode.y;
      var dx = node.x - x;
      var dy = selfNode.height / 2;
      g.setEdge(node.e, node.label);
      g.removeNode(v);
      node.label.points = [
        { x: x + 2 * dx / 3, y: y - dy },
        { x: x + 5 * dx / 6, y: y - dy },
        { x: x + dx, y },
        { x: x + 5 * dx / 6, y: y + dy },
        { x: x + 2 * dx / 3, y: y + dy }
      ];
      node.label.x = node.x;
      node.label.y = node.y;
    }
  });
}
function selectNumberAttrs(obj, attrs) {
  return mapValues(pick(obj, attrs), Number);
}
function canonicalize(attrs) {
  var newAttrs = {};
  forEach(attrs, function(v, k) {
    newAttrs[k.toLowerCase()] = v;
  });
  return newAttrs;
}
function isSubgraph(g, v) {
  return !!g.children(v).length;
}
function edgeToId(e) {
  return escapeId(e.v) + ":" + escapeId(e.w) + ":" + escapeId(e.name);
}
var ID_DELIM = /:/g;
function escapeId(str) {
  return str ? String(str).replace(ID_DELIM, "\\:") : "";
}
function applyStyle(dom, styleFn) {
  if (styleFn) {
    dom.attr("style", styleFn);
  }
}
function applyClass(dom, classFn, otherClasses) {
  if (classFn) {
    dom.attr("class", classFn).attr("class", otherClasses + " " + dom.attr("class"));
  }
}
function applyTransition(selection, g) {
  var graph = g.graph();
  if (isPlainObject(graph)) {
    var transition = graph.transition;
    if (isFunction(transition)) {
      return transition(selection);
    }
  }
  return selection;
}
var arrows = {
  normal,
  vee,
  undirected
};
function setArrows(value) {
  arrows = value;
}
function normal(parent, id, edge, type) {
  var marker = parent.append("marker").attr("id", id).attr("viewBox", "0 0 10 10").attr("refX", 9).attr("refY", 5).attr("markerUnits", "strokeWidth").attr("markerWidth", 8).attr("markerHeight", 6).attr("orient", "auto");
  var path = marker.append("path").attr("d", "M 0 0 L 10 5 L 0 10 z").style("stroke-width", 1).style("stroke-dasharray", "1,0");
  applyStyle(path, edge[type + "Style"]);
  if (edge[type + "Class"]) {
    path.attr("class", edge[type + "Class"]);
  }
}
function vee(parent, id, edge, type) {
  var marker = parent.append("marker").attr("id", id).attr("viewBox", "0 0 10 10").attr("refX", 9).attr("refY", 5).attr("markerUnits", "strokeWidth").attr("markerWidth", 8).attr("markerHeight", 6).attr("orient", "auto");
  var path = marker.append("path").attr("d", "M 0 0 L 10 5 L 0 10 L 4 5 z").style("stroke-width", 1).style("stroke-dasharray", "1,0");
  applyStyle(path, edge[type + "Style"]);
  if (edge[type + "Class"]) {
    path.attr("class", edge[type + "Class"]);
  }
}
function undirected(parent, id, edge, type) {
  var marker = parent.append("marker").attr("id", id).attr("viewBox", "0 0 10 10").attr("refX", 9).attr("refY", 5).attr("markerUnits", "strokeWidth").attr("markerWidth", 8).attr("markerHeight", 6).attr("orient", "auto");
  var path = marker.append("path").attr("d", "M 0 5 L 10 5").style("stroke-width", 1).style("stroke-dasharray", "1,0");
  applyStyle(path, edge[type + "Style"]);
  if (edge[type + "Class"]) {
    path.attr("class", edge[type + "Class"]);
  }
}
function addHtmlLabel(root, node) {
  var fo = root.append("foreignObject").attr("width", "100000");
  var div = fo.append("xhtml:div");
  div.attr("xmlns", "http://www.w3.org/1999/xhtml");
  var label = node.label;
  switch (typeof label) {
    case "function":
      div.insert(label);
      break;
    case "object":
      div.insert(function() {
        return label;
      });
      break;
    default:
      div.html(label);
  }
  applyStyle(div, node.labelStyle);
  div.style("display", "inline-block");
  div.style("white-space", "nowrap");
  var client = div.node().getBoundingClientRect();
  fo.attr("width", client.width).attr("height", client.height);
  return fo;
}
function addSVGLabel(root, node) {
  var domNode = root;
  domNode.node().appendChild(node.label);
  applyStyle(domNode, node.labelStyle);
  return domNode;
}
function addTextLabel(root, node) {
  var domNode = root.append("text");
  var lines = processEscapeSequences(node.label).split("\n");
  for (var i = 0; i < lines.length; i++) {
    domNode.append("tspan").attr("xml:space", "preserve").attr("dy", "1em").attr("x", "1").text(lines[i]);
  }
  applyStyle(domNode, node.labelStyle);
  return domNode;
}
function processEscapeSequences(text) {
  var newText = "";
  var escaped = false;
  var ch;
  for (var i = 0; i < text.length; ++i) {
    ch = text[i];
    if (escaped) {
      switch (ch) {
        case "n":
          newText += "\n";
          break;
        default:
          newText += ch;
      }
      escaped = false;
    } else if (ch === "\\") {
      escaped = true;
    } else {
      newText += ch;
    }
  }
  return newText;
}
function addLabel(root, node, location2) {
  var label = node.label;
  var labelSvg = root.append("g");
  if (node.labelType === "svg") {
    addSVGLabel(labelSvg, node);
  } else if (typeof label !== "string" || node.labelType === "html") {
    addHtmlLabel(labelSvg, node);
  } else {
    addTextLabel(labelSvg, node);
  }
  var labelBBox = labelSvg.node().getBBox();
  var y;
  switch (location2) {
    case "top":
      y = -node.height / 2;
      break;
    case "bottom":
      y = node.height / 2 - labelBBox.height;
      break;
    default:
      y = -labelBBox.height / 2;
  }
  labelSvg.attr("transform", "translate(" + -labelBBox.width / 2 + "," + y + ")");
  return labelSvg;
}
var createClusters = function(selection, g) {
  var clusters = g.nodes().filter(function(v) {
    return isSubgraph(g, v);
  });
  var svgClusters = selection.selectAll("g.cluster").data(clusters, function(v) {
    return v;
  });
  applyTransition(svgClusters.exit(), g).style("opacity", 0).remove();
  var enterSelection = svgClusters.enter().append("g").attr("class", "cluster").attr("id", function(v) {
    var node = g.node(v);
    return node.id;
  }).style("opacity", 0).each(function(v) {
    var node = g.node(v);
    var thisGroup = select(this);
    select(this).append("rect");
    var labelGroup = thisGroup.append("g").attr("class", "label");
    addLabel(labelGroup, node, node.clusterLabelPos);
  });
  svgClusters = svgClusters.merge(enterSelection);
  svgClusters = applyTransition(svgClusters, g).style("opacity", 1);
  svgClusters.selectAll("rect").each(function(c) {
    var node = g.node(c);
    var domCluster = select(this);
    applyStyle(domCluster, node.style);
  });
  return svgClusters;
};
function setCreateClusters(value) {
  createClusters = value;
}
let createEdgeLabels = function(selection, g) {
  var svgEdgeLabels = selection.selectAll("g.edgeLabel").data(g.edges(), function(e) {
    return edgeToId(e);
  }).classed("update", true);
  svgEdgeLabels.exit().remove();
  svgEdgeLabels.enter().append("g").classed("edgeLabel", true).style("opacity", 0);
  svgEdgeLabels = selection.selectAll("g.edgeLabel");
  svgEdgeLabels.each(function(e) {
    var root = select(this);
    root.select(".label").remove();
    var edge = g.edge(e);
    var label = addLabel(root, g.edge(e), 0).classed("label", true);
    var bbox = label.node().getBBox();
    if (edge.labelId) {
      label.attr("id", edge.labelId);
    }
    if (!has(edge, "width")) {
      edge.width = bbox.width;
    }
    if (!has(edge, "height")) {
      edge.height = bbox.height;
    }
  });
  var exitSelection;
  if (svgEdgeLabels.exit) {
    exitSelection = svgEdgeLabels.exit();
  } else {
    exitSelection = svgEdgeLabels.selectAll(null);
  }
  applyTransition(exitSelection, g).style("opacity", 0).remove();
  return svgEdgeLabels;
};
function setCreateEdgeLabels(value) {
  createEdgeLabels = value;
}
function intersectNode(node, point) {
  return node.intersect(point);
}
var createEdgePaths = function(selection, g, arrows2) {
  var previousPaths = selection.selectAll("g.edgePath").data(g.edges(), function(e) {
    return edgeToId(e);
  }).classed("update", true);
  var newPaths = enter(previousPaths, g);
  exit(previousPaths, g);
  var svgPaths = previousPaths.merge !== void 0 ? previousPaths.merge(newPaths) : previousPaths;
  applyTransition(svgPaths, g).style("opacity", 1);
  svgPaths.each(function(e) {
    var domEdge = select(this);
    var edge = g.edge(e);
    edge.elem = this;
    if (edge.id) {
      domEdge.attr("id", edge.id);
    }
    applyClass(
      domEdge,
      edge["class"],
      (domEdge.classed("update") ? "update " : "") + "edgePath"
    );
  });
  svgPaths.selectAll("path.path").each(function(e) {
    var edge = g.edge(e);
    edge.arrowheadId = uniqueId("arrowhead");
    var domEdge = select(this).attr("marker-end", function() {
      return "url(" + makeFragmentRef(location.href, edge.arrowheadId) + ")";
    }).style("fill", "none");
    applyTransition(domEdge, g).attr("d", function(e2) {
      return calcPoints(g, e2);
    });
    applyStyle(domEdge, edge.style);
  });
  svgPaths.selectAll("defs *").remove();
  svgPaths.selectAll("defs").each(function(e) {
    var edge = g.edge(e);
    var arrowhead = arrows2[edge.arrowhead];
    arrowhead(select(this), edge.arrowheadId, edge, "arrowhead");
  });
  return svgPaths;
};
function setCreateEdgePaths(value) {
  createEdgePaths = value;
}
function makeFragmentRef(url, fragmentId) {
  var baseUrl = url.split("#")[0];
  return baseUrl + "#" + fragmentId;
}
function calcPoints(g, e) {
  var edge = g.edge(e);
  var tail = g.node(e.v);
  var head = g.node(e.w);
  var points = edge.points.slice(1, edge.points.length - 1);
  points.unshift(intersectNode(tail, points[0]));
  points.push(intersectNode(head, points[points.length - 1]));
  return createLine(edge, points);
}
function createLine(edge, points) {
  var line$1 = (line || svg.line)().x(function(d) {
    return d.x;
  }).y(function(d) {
    return d.y;
  });
  (line$1.curve || line$1.interpolate)(edge.curve);
  return line$1(points);
}
function getCoords(elem) {
  var bbox = elem.getBBox();
  var matrix = elem.ownerSVGElement.getScreenCTM().inverse().multiply(elem.getScreenCTM()).translate(bbox.width / 2, bbox.height / 2);
  return { x: matrix.e, y: matrix.f };
}
function enter(svgPaths, g) {
  var svgPathsEnter = svgPaths.enter().append("g").attr("class", "edgePath").style("opacity", 0);
  svgPathsEnter.append("path").attr("class", "path").attr("d", function(e) {
    var edge = g.edge(e);
    var sourceElem = g.node(e.v).elem;
    var points = range(edge.points.length).map(function() {
      return getCoords(sourceElem);
    });
    return createLine(edge, points);
  });
  svgPathsEnter.append("defs");
  return svgPathsEnter;
}
function exit(svgPaths, g) {
  var svgPathExit = svgPaths.exit();
  applyTransition(svgPathExit, g).style("opacity", 0).remove();
}
var createNodes = function(selection, g, shapes2) {
  var simpleNodes = g.nodes().filter(function(v) {
    return !isSubgraph(g, v);
  });
  var svgNodes = selection.selectAll("g.node").data(simpleNodes, function(v) {
    return v;
  }).classed("update", true);
  svgNodes.exit().remove();
  svgNodes.enter().append("g").attr("class", "node").style("opacity", 0);
  svgNodes = selection.selectAll("g.node");
  svgNodes.each(function(v) {
    var node = g.node(v);
    var thisGroup = select(this);
    applyClass(
      thisGroup,
      node["class"],
      (thisGroup.classed("update") ? "update " : "") + "node"
    );
    thisGroup.select("g.label").remove();
    var labelGroup = thisGroup.append("g").attr("class", "label");
    var labelDom = addLabel(labelGroup, node);
    var shape = shapes2[node.shape];
    var bbox = pick(labelDom.node().getBBox(), "width", "height");
    node.elem = this;
    if (node.id) {
      thisGroup.attr("id", node.id);
    }
    if (node.labelId) {
      labelGroup.attr("id", node.labelId);
    }
    if (has(node, "width")) {
      bbox.width = node.width;
    }
    if (has(node, "height")) {
      bbox.height = node.height;
    }
    bbox.width += node.paddingLeft + node.paddingRight;
    bbox.height += node.paddingTop + node.paddingBottom;
    labelGroup.attr(
      "transform",
      "translate(" + (node.paddingLeft - node.paddingRight) / 2 + "," + (node.paddingTop - node.paddingBottom) / 2 + ")"
    );
    var root = select(this);
    root.select(".label-container").remove();
    var shapeSvg = shape(root, bbox, node).classed("label-container", true);
    applyStyle(shapeSvg, node.style);
    var shapeBBox = shapeSvg.node().getBBox();
    node.width = shapeBBox.width;
    node.height = shapeBBox.height;
  });
  var exitSelection;
  if (svgNodes.exit) {
    exitSelection = svgNodes.exit();
  } else {
    exitSelection = svgNodes.selectAll(null);
  }
  applyTransition(exitSelection, g).style("opacity", 0).remove();
  return svgNodes;
};
function setCreateNodes(value) {
  createNodes = value;
}
function positionClusters(selection, g) {
  var created = selection.filter(function() {
    return !select(this).classed("update");
  });
  function translate(v) {
    var node = g.node(v);
    return "translate(" + node.x + "," + node.y + ")";
  }
  created.attr("transform", translate);
  applyTransition(selection, g).style("opacity", 1).attr("transform", translate);
  applyTransition(created.selectAll("rect"), g).attr("width", function(v) {
    return g.node(v).width;
  }).attr("height", function(v) {
    return g.node(v).height;
  }).attr("x", function(v) {
    var node = g.node(v);
    return -node.width / 2;
  }).attr("y", function(v) {
    var node = g.node(v);
    return -node.height / 2;
  });
}
function positionEdgeLabels(selection, g) {
  var created = selection.filter(function() {
    return !select(this).classed("update");
  });
  function translate(e) {
    var edge = g.edge(e);
    return has(edge, "x") ? "translate(" + edge.x + "," + edge.y + ")" : "";
  }
  created.attr("transform", translate);
  applyTransition(selection, g).style("opacity", 1).attr("transform", translate);
}
function positionNodes(selection, g) {
  var created = selection.filter(function() {
    return !select(this).classed("update");
  });
  function translate(v) {
    var node = g.node(v);
    return "translate(" + node.x + "," + node.y + ")";
  }
  created.attr("transform", translate);
  applyTransition(selection, g).style("opacity", 1).attr("transform", translate);
}
function intersectEllipse(node, rx, ry, point) {
  var cx = node.x;
  var cy = node.y;
  var px = cx - point.x;
  var py = cy - point.y;
  var det = Math.sqrt(rx * rx * py * py + ry * ry * px * px);
  var dx = Math.abs(rx * ry * px / det);
  if (point.x < cx) {
    dx = -dx;
  }
  var dy = Math.abs(rx * ry * py / det);
  if (point.y < cy) {
    dy = -dy;
  }
  return { x: cx + dx, y: cy + dy };
}
function intersectCircle(node, rx, point) {
  return intersectEllipse(node, rx, rx, point);
}
function intersectLine(p1, p2, q1, q2) {
  var a1, a2, b1, b2, c1, c2;
  var r1, r2, r3, r4;
  var denom, offset, num;
  var x, y;
  a1 = p2.y - p1.y;
  b1 = p1.x - p2.x;
  c1 = p2.x * p1.y - p1.x * p2.y;
  r3 = a1 * q1.x + b1 * q1.y + c1;
  r4 = a1 * q2.x + b1 * q2.y + c1;
  if (r3 !== 0 && r4 !== 0 && sameSign(r3, r4)) {
    return;
  }
  a2 = q2.y - q1.y;
  b2 = q1.x - q2.x;
  c2 = q2.x * q1.y - q1.x * q2.y;
  r1 = a2 * p1.x + b2 * p1.y + c2;
  r2 = a2 * p2.x + b2 * p2.y + c2;
  if (r1 !== 0 && r2 !== 0 && sameSign(r1, r2)) {
    return;
  }
  denom = a1 * b2 - a2 * b1;
  if (denom === 0) {
    return;
  }
  offset = Math.abs(denom / 2);
  num = b1 * c2 - b2 * c1;
  x = num < 0 ? (num - offset) / denom : (num + offset) / denom;
  num = a2 * c1 - a1 * c2;
  y = num < 0 ? (num - offset) / denom : (num + offset) / denom;
  return { x, y };
}
function sameSign(r1, r2) {
  return r1 * r2 > 0;
}
function intersectPolygon(node, polyPoints, point) {
  var x1 = node.x;
  var y1 = node.y;
  var intersections = [];
  var minX = Number.POSITIVE_INFINITY;
  var minY = Number.POSITIVE_INFINITY;
  polyPoints.forEach(function(entry) {
    minX = Math.min(minX, entry.x);
    minY = Math.min(minY, entry.y);
  });
  var left = x1 - node.width / 2 - minX;
  var top = y1 - node.height / 2 - minY;
  for (var i = 0; i < polyPoints.length; i++) {
    var p1 = polyPoints[i];
    var p2 = polyPoints[i < polyPoints.length - 1 ? i + 1 : 0];
    var intersect = intersectLine(
      node,
      point,
      { x: left + p1.x, y: top + p1.y },
      { x: left + p2.x, y: top + p2.y }
    );
    if (intersect) {
      intersections.push(intersect);
    }
  }
  if (!intersections.length) {
    console.log("NO INTERSECTION FOUND, RETURN NODE CENTER", node);
    return node;
  }
  if (intersections.length > 1) {
    intersections.sort(function(p, q) {
      var pdx = p.x - point.x;
      var pdy = p.y - point.y;
      var distp = Math.sqrt(pdx * pdx + pdy * pdy);
      var qdx = q.x - point.x;
      var qdy = q.y - point.y;
      var distq = Math.sqrt(qdx * qdx + qdy * qdy);
      return distp < distq ? -1 : distp === distq ? 0 : 1;
    });
  }
  return intersections[0];
}
function intersectRect(node, point) {
  var x = node.x;
  var y = node.y;
  var dx = point.x - x;
  var dy = point.y - y;
  var w = node.width / 2;
  var h = node.height / 2;
  var sx, sy;
  if (Math.abs(dy) * w > Math.abs(dx) * h) {
    if (dy < 0) {
      h = -h;
    }
    sx = dy === 0 ? 0 : h * dx / dy;
    sy = h;
  } else {
    if (dx < 0) {
      w = -w;
    }
    sx = w;
    sy = dx === 0 ? 0 : w * dy / dx;
  }
  return { x: x + sx, y: y + sy };
}
var shapes = {
  rect,
  ellipse,
  circle,
  diamond
};
function setShapes(value) {
  shapes = value;
}
function rect(parent, bbox, node) {
  var shapeSvg = parent.insert("rect", ":first-child").attr("rx", node.rx).attr("ry", node.ry).attr("x", -bbox.width / 2).attr("y", -bbox.height / 2).attr("width", bbox.width).attr("height", bbox.height);
  node.intersect = function(point) {
    return intersectRect(node, point);
  };
  return shapeSvg;
}
function ellipse(parent, bbox, node) {
  var rx = bbox.width / 2;
  var ry = bbox.height / 2;
  var shapeSvg = parent.insert("ellipse", ":first-child").attr("x", -bbox.width / 2).attr("y", -bbox.height / 2).attr("rx", rx).attr("ry", ry);
  node.intersect = function(point) {
    return intersectEllipse(node, rx, ry, point);
  };
  return shapeSvg;
}
function circle(parent, bbox, node) {
  var r = Math.max(bbox.width, bbox.height) / 2;
  var shapeSvg = parent.insert("circle", ":first-child").attr("x", -bbox.width / 2).attr("y", -bbox.height / 2).attr("r", r);
  node.intersect = function(point) {
    return intersectCircle(node, r, point);
  };
  return shapeSvg;
}
function diamond(parent, bbox, node) {
  var w = bbox.width * Math.SQRT2 / 2;
  var h = bbox.height * Math.SQRT2 / 2;
  var points = [
    { x: 0, y: -h },
    { x: -w, y: 0 },
    { x: 0, y: h },
    { x: w, y: 0 }
  ];
  var shapeSvg = parent.insert("polygon", ":first-child").attr(
    "points",
    points.map(function(p) {
      return p.x + "," + p.y;
    }).join(" ")
  );
  node.intersect = function(p) {
    return intersectPolygon(node, points, p);
  };
  return shapeSvg;
}
function render() {
  var fn = function(svg2, g) {
    preProcessGraph(g);
    var outputGroup = createOrSelectGroup(svg2, "output");
    var clustersGroup = createOrSelectGroup(outputGroup, "clusters");
    var edgePathsGroup = createOrSelectGroup(outputGroup, "edgePaths");
    var edgeLabels = createEdgeLabels(createOrSelectGroup(outputGroup, "edgeLabels"), g);
    var nodes = createNodes(createOrSelectGroup(outputGroup, "nodes"), g, shapes);
    layout(g);
    positionNodes(nodes, g);
    positionEdgeLabels(edgeLabels, g);
    createEdgePaths(edgePathsGroup, g, arrows);
    var clusters = createClusters(clustersGroup, g);
    positionClusters(clusters, g);
    postProcessGraph(g);
  };
  fn.createNodes = function(value) {
    if (!arguments.length)
      return createNodes;
    setCreateNodes(value);
    return fn;
  };
  fn.createClusters = function(value) {
    if (!arguments.length)
      return createClusters;
    setCreateClusters(value);
    return fn;
  };
  fn.createEdgeLabels = function(value) {
    if (!arguments.length)
      return createEdgeLabels;
    setCreateEdgeLabels(value);
    return fn;
  };
  fn.createEdgePaths = function(value) {
    if (!arguments.length)
      return createEdgePaths;
    setCreateEdgePaths(value);
    return fn;
  };
  fn.shapes = function(value) {
    if (!arguments.length)
      return shapes;
    setShapes(value);
    return fn;
  };
  fn.arrows = function(value) {
    if (!arguments.length)
      return arrows;
    setArrows(value);
    return fn;
  };
  return fn;
}
var NODE_DEFAULT_ATTRS = {
  paddingLeft: 10,
  paddingRight: 10,
  paddingTop: 10,
  paddingBottom: 10,
  rx: 0,
  ry: 0,
  shape: "rect"
};
var EDGE_DEFAULT_ATTRS = {
  arrowhead: "normal",
  curve: curveLinear
};
function preProcessGraph(g) {
  g.nodes().forEach(function(v) {
    var node = g.node(v);
    if (!has(node, "label") && !g.children(v).length) {
      node.label = v;
    }
    if (has(node, "paddingX")) {
      defaults(node, {
        paddingLeft: node.paddingX,
        paddingRight: node.paddingX
      });
    }
    if (has(node, "paddingY")) {
      defaults(node, {
        paddingTop: node.paddingY,
        paddingBottom: node.paddingY
      });
    }
    if (has(node, "padding")) {
      defaults(node, {
        paddingLeft: node.padding,
        paddingRight: node.padding,
        paddingTop: node.padding,
        paddingBottom: node.padding
      });
    }
    defaults(node, NODE_DEFAULT_ATTRS);
    forEach(["paddingLeft", "paddingRight", "paddingTop", "paddingBottom"], function(k) {
      node[k] = Number(node[k]);
    });
    if (has(node, "width")) {
      node._prevWidth = node.width;
    }
    if (has(node, "height")) {
      node._prevHeight = node.height;
    }
  });
  g.edges().forEach(function(e) {
    var edge = g.edge(e);
    if (!has(edge, "label")) {
      edge.label = "";
    }
    defaults(edge, EDGE_DEFAULT_ATTRS);
  });
}
function postProcessGraph(g) {
  forEach(g.nodes(), function(v) {
    var node = g.node(v);
    if (has(node, "_prevWidth")) {
      node.width = node._prevWidth;
    } else {
      delete node.width;
    }
    if (has(node, "_prevHeight")) {
      node.height = node._prevHeight;
    } else {
      delete node.height;
    }
    delete node._prevWidth;
    delete node._prevHeight;
  });
}
function createOrSelectGroup(root, name) {
  var selection = root.select("g." + name);
  if (selection.empty()) {
    selection = root.append("g").attr("class", name);
  }
  return selection;
}
function write(g) {
  var json = {
    options: {
      directed: g.isDirected(),
      multigraph: g.isMultigraph(),
      compound: g.isCompound()
    },
    nodes: writeNodes(g),
    edges: writeEdges(g)
  };
  if (!isUndefined(g.graph())) {
    json.value = clone(g.graph());
  }
  return json;
}
function writeNodes(g) {
  return map(g.nodes(), function(v) {
    var nodeValue = g.node(v);
    var parent = g.parent(v);
    var node = { v };
    if (!isUndefined(nodeValue)) {
      node.value = nodeValue;
    }
    if (!isUndefined(parent)) {
      node.parent = parent;
    }
    return node;
  });
}
function writeEdges(g) {
  return map(g.edges(), function(e) {
    var edgeValue = g.edge(e);
    var edge = { v: e.v, w: e.w };
    if (!isUndefined(e.name)) {
      edge.name = e.name;
    }
    if (!isUndefined(edgeValue)) {
      edge.value = edgeValue;
    }
    return edge;
  });
}
export {
  Graph as G,
  addHtmlLabel as a,
  applyStyle as b,
  intersectRect as c,
  intersectPolygon as i,
  layout as l,
  render as r,
  write as w
};
