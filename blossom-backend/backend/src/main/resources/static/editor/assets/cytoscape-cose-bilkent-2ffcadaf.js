import { c as commonjsGlobal, g as getDefaultExportFromCjs } from "./@braintree-12043bbe.js";
import { r as requireCoseBase } from "./cose-base-d024680b.js";
var cytoscapeCoseBilkent = { exports: {} };
(function(module, exports) {
  (function webpackUniversalModuleDefinition(root, factory) {
    module.exports = factory(requireCoseBase());
  })(commonjsGlobal, function(__WEBPACK_EXTERNAL_MODULE_0__) {
    return (
      /******/
      function(modules) {
        var installedModules = {};
        function __webpack_require__(moduleId) {
          if (installedModules[moduleId]) {
            return installedModules[moduleId].exports;
          }
          var module2 = installedModules[moduleId] = {
            /******/
            i: moduleId,
            /******/
            l: false,
            /******/
            exports: {}
            /******/
          };
          modules[moduleId].call(module2.exports, module2, module2.exports, __webpack_require__);
          module2.l = true;
          return module2.exports;
        }
        __webpack_require__.m = modules;
        __webpack_require__.c = installedModules;
        __webpack_require__.i = function(value) {
          return value;
        };
        __webpack_require__.d = function(exports2, name, getter) {
          if (!__webpack_require__.o(exports2, name)) {
            Object.defineProperty(exports2, name, {
              /******/
              configurable: false,
              /******/
              enumerable: true,
              /******/
              get: getter
              /******/
            });
          }
        };
        __webpack_require__.n = function(module2) {
          var getter = module2 && module2.__esModule ? (
            /******/
            function getDefault() {
              return module2["default"];
            }
          ) : (
            /******/
            function getModuleExports() {
              return module2;
            }
          );
          __webpack_require__.d(getter, "a", getter);
          return getter;
        };
        __webpack_require__.o = function(object, property) {
          return Object.prototype.hasOwnProperty.call(object, property);
        };
        __webpack_require__.p = "";
        return __webpack_require__(__webpack_require__.s = 1);
      }([
        /* 0 */
        /***/
        function(module2, exports2) {
          module2.exports = __WEBPACK_EXTERNAL_MODULE_0__;
        },
        /* 1 */
        /***/
        function(module2, exports2, __webpack_require__) {
          var LayoutConstants = __webpack_require__(0).layoutBase.LayoutConstants;
          var FDLayoutConstants = __webpack_require__(0).layoutBase.FDLayoutConstants;
          var CoSEConstants = __webpack_require__(0).CoSEConstants;
          var CoSELayout = __webpack_require__(0).CoSELayout;
          var CoSENode = __webpack_require__(0).CoSENode;
          var PointD = __webpack_require__(0).layoutBase.PointD;
          var DimensionD = __webpack_require__(0).layoutBase.DimensionD;
          var defaults = {
            // Called on `layoutready`
            ready: function ready() {
            },
            // Called on `layoutstop`
            stop: function stop() {
            },
            // 'draft', 'default' or 'proof" 
            // - 'draft' fast cooling rate 
            // - 'default' moderate cooling rate 
            // - "proof" slow cooling rate
            quality: "default",
            // include labels in node dimensions
            nodeDimensionsIncludeLabels: false,
            // number of ticks per frame; higher is faster but more jerky
            refresh: 30,
            // Whether to fit the network view after when done
            fit: true,
            // Padding on fit
            padding: 10,
            // Whether to enable incremental mode
            randomize: true,
            // Node repulsion (non overlapping) multiplier
            nodeRepulsion: 4500,
            // Ideal edge (non nested) length
            idealEdgeLength: 50,
            // Divisor to compute edge forces
            edgeElasticity: 0.45,
            // Nesting factor (multiplier) to compute ideal edge length for nested edges
            nestingFactor: 0.1,
            // Gravity force (constant)
            gravity: 0.25,
            // Maximum number of iterations to perform
            numIter: 2500,
            // For enabling tiling
            tile: true,
            // Type of layout animation. The option set is {'during', 'end', false}
            animate: "end",
            // Duration for animate:end
            animationDuration: 500,
            // Represents the amount of the vertical space to put between the zero degree members during the tiling operation(can also be a function)
            tilingPaddingVertical: 10,
            // Represents the amount of the horizontal space to put between the zero degree members during the tiling operation(can also be a function)
            tilingPaddingHorizontal: 10,
            // Gravity range (constant) for compounds
            gravityRangeCompound: 1.5,
            // Gravity force (constant) for compounds
            gravityCompound: 1,
            // Gravity range (constant)
            gravityRange: 3.8,
            // Initial cooling factor for incremental layout
            initialEnergyOnIncremental: 0.5
          };
          function extend(defaults2, options) {
            var obj = {};
            for (var i in defaults2) {
              obj[i] = defaults2[i];
            }
            for (var i in options) {
              obj[i] = options[i];
            }
            return obj;
          }
          function _CoSELayout(_options) {
            this.options = extend(defaults, _options);
            getUserOptions(this.options);
          }
          var getUserOptions = function getUserOptions2(options) {
            if (options.nodeRepulsion != null)
              CoSEConstants.DEFAULT_REPULSION_STRENGTH = FDLayoutConstants.DEFAULT_REPULSION_STRENGTH = options.nodeRepulsion;
            if (options.idealEdgeLength != null)
              CoSEConstants.DEFAULT_EDGE_LENGTH = FDLayoutConstants.DEFAULT_EDGE_LENGTH = options.idealEdgeLength;
            if (options.edgeElasticity != null)
              CoSEConstants.DEFAULT_SPRING_STRENGTH = FDLayoutConstants.DEFAULT_SPRING_STRENGTH = options.edgeElasticity;
            if (options.nestingFactor != null)
              CoSEConstants.PER_LEVEL_IDEAL_EDGE_LENGTH_FACTOR = FDLayoutConstants.PER_LEVEL_IDEAL_EDGE_LENGTH_FACTOR = options.nestingFactor;
            if (options.gravity != null)
              CoSEConstants.DEFAULT_GRAVITY_STRENGTH = FDLayoutConstants.DEFAULT_GRAVITY_STRENGTH = options.gravity;
            if (options.numIter != null)
              CoSEConstants.MAX_ITERATIONS = FDLayoutConstants.MAX_ITERATIONS = options.numIter;
            if (options.gravityRange != null)
              CoSEConstants.DEFAULT_GRAVITY_RANGE_FACTOR = FDLayoutConstants.DEFAULT_GRAVITY_RANGE_FACTOR = options.gravityRange;
            if (options.gravityCompound != null)
              CoSEConstants.DEFAULT_COMPOUND_GRAVITY_STRENGTH = FDLayoutConstants.DEFAULT_COMPOUND_GRAVITY_STRENGTH = options.gravityCompound;
            if (options.gravityRangeCompound != null)
              CoSEConstants.DEFAULT_COMPOUND_GRAVITY_RANGE_FACTOR = FDLayoutConstants.DEFAULT_COMPOUND_GRAVITY_RANGE_FACTOR = options.gravityRangeCompound;
            if (options.initialEnergyOnIncremental != null)
              CoSEConstants.DEFAULT_COOLING_FACTOR_INCREMENTAL = FDLayoutConstants.DEFAULT_COOLING_FACTOR_INCREMENTAL = options.initialEnergyOnIncremental;
            if (options.quality == "draft")
              LayoutConstants.QUALITY = 0;
            else if (options.quality == "proof")
              LayoutConstants.QUALITY = 2;
            else
              LayoutConstants.QUALITY = 1;
            CoSEConstants.NODE_DIMENSIONS_INCLUDE_LABELS = FDLayoutConstants.NODE_DIMENSIONS_INCLUDE_LABELS = LayoutConstants.NODE_DIMENSIONS_INCLUDE_LABELS = options.nodeDimensionsIncludeLabels;
            CoSEConstants.DEFAULT_INCREMENTAL = FDLayoutConstants.DEFAULT_INCREMENTAL = LayoutConstants.DEFAULT_INCREMENTAL = !options.randomize;
            CoSEConstants.ANIMATE = FDLayoutConstants.ANIMATE = LayoutConstants.ANIMATE = options.animate;
            CoSEConstants.TILE = options.tile;
            CoSEConstants.TILING_PADDING_VERTICAL = typeof options.tilingPaddingVertical === "function" ? options.tilingPaddingVertical.call() : options.tilingPaddingVertical;
            CoSEConstants.TILING_PADDING_HORIZONTAL = typeof options.tilingPaddingHorizontal === "function" ? options.tilingPaddingHorizontal.call() : options.tilingPaddingHorizontal;
          };
          _CoSELayout.prototype.run = function() {
            var ready;
            var frameId;
            var options = this.options;
            this.idToLNode = {};
            var layout = this.layout = new CoSELayout();
            var self = this;
            self.stopped = false;
            this.cy = this.options.cy;
            this.cy.trigger({ type: "layoutstart", layout: this });
            var gm = layout.newGraphManager();
            this.gm = gm;
            var nodes = this.options.eles.nodes();
            var edges = this.options.eles.edges();
            this.root = gm.addRoot();
            this.processChildrenList(this.root, this.getTopMostNodes(nodes), layout);
            for (var i = 0; i < edges.length; i++) {
              var edge = edges[i];
              var sourceNode = this.idToLNode[edge.data("source")];
              var targetNode = this.idToLNode[edge.data("target")];
              if (sourceNode !== targetNode && sourceNode.getEdgesBetween(targetNode).length == 0) {
                var e1 = gm.add(layout.newEdge(), sourceNode, targetNode);
                e1.id = edge.id();
              }
            }
            var getPositions = function getPositions2(ele, i2) {
              if (typeof ele === "number") {
                ele = i2;
              }
              var theId = ele.data("id");
              var lNode = self.idToLNode[theId];
              return {
                x: lNode.getRect().getCenterX(),
                y: lNode.getRect().getCenterY()
              };
            };
            var iterateAnimated = function iterateAnimated2() {
              var afterReposition = function afterReposition2() {
                if (options.fit) {
                  options.cy.fit(options.eles, options.padding);
                }
                if (!ready) {
                  ready = true;
                  self.cy.one("layoutready", options.ready);
                  self.cy.trigger({ type: "layoutready", layout: self });
                }
              };
              var ticksPerFrame = self.options.refresh;
              var isDone;
              for (var i2 = 0; i2 < ticksPerFrame && !isDone; i2++) {
                isDone = self.stopped || self.layout.tick();
              }
              if (isDone) {
                if (layout.checkLayoutSuccess() && !layout.isSubLayout) {
                  layout.doPostLayout();
                }
                if (layout.tilingPostLayout) {
                  layout.tilingPostLayout();
                }
                layout.isLayoutFinished = true;
                self.options.eles.nodes().positions(getPositions);
                afterReposition();
                self.cy.one("layoutstop", self.options.stop);
                self.cy.trigger({ type: "layoutstop", layout: self });
                if (frameId) {
                  cancelAnimationFrame(frameId);
                }
                ready = false;
                return;
              }
              var animationData = self.layout.getPositionsData();
              options.eles.nodes().positions(function(ele, i3) {
                if (typeof ele === "number") {
                  ele = i3;
                }
                if (!ele.isParent()) {
                  var theId = ele.id();
                  var pNode = animationData[theId];
                  var temp = ele;
                  while (pNode == null) {
                    pNode = animationData[temp.data("parent")] || animationData["DummyCompound_" + temp.data("parent")];
                    animationData[theId] = pNode;
                    temp = temp.parent()[0];
                    if (temp == void 0) {
                      break;
                    }
                  }
                  if (pNode != null) {
                    return {
                      x: pNode.x,
                      y: pNode.y
                    };
                  } else {
                    return {
                      x: ele.position("x"),
                      y: ele.position("y")
                    };
                  }
                }
              });
              afterReposition();
              frameId = requestAnimationFrame(iterateAnimated2);
            };
            layout.addListener("layoutstarted", function() {
              if (self.options.animate === "during") {
                frameId = requestAnimationFrame(iterateAnimated);
              }
            });
            layout.runLayout();
            if (this.options.animate !== "during") {
              self.options.eles.nodes().not(":parent").layoutPositions(self, self.options, getPositions);
              ready = false;
            }
            return this;
          };
          _CoSELayout.prototype.getTopMostNodes = function(nodes) {
            var nodesMap = {};
            for (var i = 0; i < nodes.length; i++) {
              nodesMap[nodes[i].id()] = true;
            }
            var roots = nodes.filter(function(ele, i2) {
              if (typeof ele === "number") {
                ele = i2;
              }
              var parent = ele.parent()[0];
              while (parent != null) {
                if (nodesMap[parent.id()]) {
                  return false;
                }
                parent = parent.parent()[0];
              }
              return true;
            });
            return roots;
          };
          _CoSELayout.prototype.processChildrenList = function(parent, children, layout) {
            var size = children.length;
            for (var i = 0; i < size; i++) {
              var theChild = children[i];
              var children_of_children = theChild.children();
              var theNode;
              var dimensions = theChild.layoutDimensions({
                nodeDimensionsIncludeLabels: this.options.nodeDimensionsIncludeLabels
              });
              if (theChild.outerWidth() != null && theChild.outerHeight() != null) {
                theNode = parent.add(new CoSENode(layout.graphManager, new PointD(theChild.position("x") - dimensions.w / 2, theChild.position("y") - dimensions.h / 2), new DimensionD(parseFloat(dimensions.w), parseFloat(dimensions.h))));
              } else {
                theNode = parent.add(new CoSENode(this.graphManager));
              }
              theNode.id = theChild.data("id");
              theNode.paddingLeft = parseInt(theChild.css("padding"));
              theNode.paddingTop = parseInt(theChild.css("padding"));
              theNode.paddingRight = parseInt(theChild.css("padding"));
              theNode.paddingBottom = parseInt(theChild.css("padding"));
              if (this.options.nodeDimensionsIncludeLabels) {
                if (theChild.isParent()) {
                  var labelWidth = theChild.boundingBox({ includeLabels: true, includeNodes: false }).w;
                  var labelHeight = theChild.boundingBox({ includeLabels: true, includeNodes: false }).h;
                  var labelPos = theChild.css("text-halign");
                  theNode.labelWidth = labelWidth;
                  theNode.labelHeight = labelHeight;
                  theNode.labelPos = labelPos;
                }
              }
              this.idToLNode[theChild.data("id")] = theNode;
              if (isNaN(theNode.rect.x)) {
                theNode.rect.x = 0;
              }
              if (isNaN(theNode.rect.y)) {
                theNode.rect.y = 0;
              }
              if (children_of_children != null && children_of_children.length > 0) {
                var theNewGraph;
                theNewGraph = layout.getGraphManager().add(layout.newGraph(), theNode);
                this.processChildrenList(theNewGraph, children_of_children, layout);
              }
            }
          };
          _CoSELayout.prototype.stop = function() {
            this.stopped = true;
            return this;
          };
          var register = function register2(cytoscape2) {
            cytoscape2("layout", "cose-bilkent", _CoSELayout);
          };
          if (typeof cytoscape !== "undefined") {
            register(cytoscape);
          }
          module2.exports = register;
        }
        /******/
      ])
    );
  });
})(cytoscapeCoseBilkent);
var cytoscapeCoseBilkentExports = cytoscapeCoseBilkent.exports;
const coseBilkent = /* @__PURE__ */ getDefaultExportFromCjs(cytoscapeCoseBilkentExports);
export {
  coseBilkent as c
};
