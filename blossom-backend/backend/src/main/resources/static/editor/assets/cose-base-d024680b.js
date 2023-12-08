import { c as commonjsGlobal } from "./@braintree-12043bbe.js";
import { r as requireLayoutBase } from "./layout-base-c9290116.js";
var coseBase = { exports: {} };
var hasRequiredCoseBase;
function requireCoseBase() {
  if (hasRequiredCoseBase)
    return coseBase.exports;
  hasRequiredCoseBase = 1;
  (function(module, exports) {
    (function webpackUniversalModuleDefinition(root, factory) {
      module.exports = factory(requireLayoutBase());
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
          return __webpack_require__(__webpack_require__.s = 7);
        }([
          /* 0 */
          /***/
          function(module2, exports2) {
            module2.exports = __WEBPACK_EXTERNAL_MODULE_0__;
          },
          /* 1 */
          /***/
          function(module2, exports2, __webpack_require__) {
            var FDLayoutConstants = __webpack_require__(0).FDLayoutConstants;
            function CoSEConstants() {
            }
            for (var prop in FDLayoutConstants) {
              CoSEConstants[prop] = FDLayoutConstants[prop];
            }
            CoSEConstants.DEFAULT_USE_MULTI_LEVEL_SCALING = false;
            CoSEConstants.DEFAULT_RADIAL_SEPARATION = FDLayoutConstants.DEFAULT_EDGE_LENGTH;
            CoSEConstants.DEFAULT_COMPONENT_SEPERATION = 60;
            CoSEConstants.TILE = true;
            CoSEConstants.TILING_PADDING_VERTICAL = 10;
            CoSEConstants.TILING_PADDING_HORIZONTAL = 10;
            CoSEConstants.TREE_REDUCTION_ON_INCREMENTAL = false;
            module2.exports = CoSEConstants;
          },
          /* 2 */
          /***/
          function(module2, exports2, __webpack_require__) {
            var FDLayoutEdge = __webpack_require__(0).FDLayoutEdge;
            function CoSEEdge(source, target, vEdge) {
              FDLayoutEdge.call(this, source, target, vEdge);
            }
            CoSEEdge.prototype = Object.create(FDLayoutEdge.prototype);
            for (var prop in FDLayoutEdge) {
              CoSEEdge[prop] = FDLayoutEdge[prop];
            }
            module2.exports = CoSEEdge;
          },
          /* 3 */
          /***/
          function(module2, exports2, __webpack_require__) {
            var LGraph = __webpack_require__(0).LGraph;
            function CoSEGraph(parent, graphMgr, vGraph) {
              LGraph.call(this, parent, graphMgr, vGraph);
            }
            CoSEGraph.prototype = Object.create(LGraph.prototype);
            for (var prop in LGraph) {
              CoSEGraph[prop] = LGraph[prop];
            }
            module2.exports = CoSEGraph;
          },
          /* 4 */
          /***/
          function(module2, exports2, __webpack_require__) {
            var LGraphManager = __webpack_require__(0).LGraphManager;
            function CoSEGraphManager(layout) {
              LGraphManager.call(this, layout);
            }
            CoSEGraphManager.prototype = Object.create(LGraphManager.prototype);
            for (var prop in LGraphManager) {
              CoSEGraphManager[prop] = LGraphManager[prop];
            }
            module2.exports = CoSEGraphManager;
          },
          /* 5 */
          /***/
          function(module2, exports2, __webpack_require__) {
            var FDLayoutNode = __webpack_require__(0).FDLayoutNode;
            var IMath = __webpack_require__(0).IMath;
            function CoSENode(gm, loc, size, vNode) {
              FDLayoutNode.call(this, gm, loc, size, vNode);
            }
            CoSENode.prototype = Object.create(FDLayoutNode.prototype);
            for (var prop in FDLayoutNode) {
              CoSENode[prop] = FDLayoutNode[prop];
            }
            CoSENode.prototype.move = function() {
              var layout = this.graphManager.getLayout();
              this.displacementX = layout.coolingFactor * (this.springForceX + this.repulsionForceX + this.gravitationForceX) / this.noOfChildren;
              this.displacementY = layout.coolingFactor * (this.springForceY + this.repulsionForceY + this.gravitationForceY) / this.noOfChildren;
              if (Math.abs(this.displacementX) > layout.coolingFactor * layout.maxNodeDisplacement) {
                this.displacementX = layout.coolingFactor * layout.maxNodeDisplacement * IMath.sign(this.displacementX);
              }
              if (Math.abs(this.displacementY) > layout.coolingFactor * layout.maxNodeDisplacement) {
                this.displacementY = layout.coolingFactor * layout.maxNodeDisplacement * IMath.sign(this.displacementY);
              }
              if (this.child == null) {
                this.moveBy(this.displacementX, this.displacementY);
              } else if (this.child.getNodes().length == 0) {
                this.moveBy(this.displacementX, this.displacementY);
              } else {
                this.propogateDisplacementToChildren(this.displacementX, this.displacementY);
              }
              layout.totalDisplacement += Math.abs(this.displacementX) + Math.abs(this.displacementY);
              this.springForceX = 0;
              this.springForceY = 0;
              this.repulsionForceX = 0;
              this.repulsionForceY = 0;
              this.gravitationForceX = 0;
              this.gravitationForceY = 0;
              this.displacementX = 0;
              this.displacementY = 0;
            };
            CoSENode.prototype.propogateDisplacementToChildren = function(dX, dY) {
              var nodes = this.getChild().getNodes();
              var node;
              for (var i = 0; i < nodes.length; i++) {
                node = nodes[i];
                if (node.getChild() == null) {
                  node.moveBy(dX, dY);
                  node.displacementX += dX;
                  node.displacementY += dY;
                } else {
                  node.propogateDisplacementToChildren(dX, dY);
                }
              }
            };
            CoSENode.prototype.setPred1 = function(pred12) {
              this.pred1 = pred12;
            };
            CoSENode.prototype.getPred1 = function() {
              return pred1;
            };
            CoSENode.prototype.getPred2 = function() {
              return pred2;
            };
            CoSENode.prototype.setNext = function(next2) {
              this.next = next2;
            };
            CoSENode.prototype.getNext = function() {
              return next;
            };
            CoSENode.prototype.setProcessed = function(processed2) {
              this.processed = processed2;
            };
            CoSENode.prototype.isProcessed = function() {
              return processed;
            };
            module2.exports = CoSENode;
          },
          /* 6 */
          /***/
          function(module2, exports2, __webpack_require__) {
            var FDLayout = __webpack_require__(0).FDLayout;
            var CoSEGraphManager = __webpack_require__(4);
            var CoSEGraph = __webpack_require__(3);
            var CoSENode = __webpack_require__(5);
            var CoSEEdge = __webpack_require__(2);
            var CoSEConstants = __webpack_require__(1);
            var FDLayoutConstants = __webpack_require__(0).FDLayoutConstants;
            var LayoutConstants = __webpack_require__(0).LayoutConstants;
            var Point = __webpack_require__(0).Point;
            var PointD = __webpack_require__(0).PointD;
            var Layout = __webpack_require__(0).Layout;
            var Integer = __webpack_require__(0).Integer;
            var IGeometry = __webpack_require__(0).IGeometry;
            var LGraph = __webpack_require__(0).LGraph;
            var Transform = __webpack_require__(0).Transform;
            function CoSELayout() {
              FDLayout.call(this);
              this.toBeTiled = {};
            }
            CoSELayout.prototype = Object.create(FDLayout.prototype);
            for (var prop in FDLayout) {
              CoSELayout[prop] = FDLayout[prop];
            }
            CoSELayout.prototype.newGraphManager = function() {
              var gm = new CoSEGraphManager(this);
              this.graphManager = gm;
              return gm;
            };
            CoSELayout.prototype.newGraph = function(vGraph) {
              return new CoSEGraph(null, this.graphManager, vGraph);
            };
            CoSELayout.prototype.newNode = function(vNode) {
              return new CoSENode(this.graphManager, vNode);
            };
            CoSELayout.prototype.newEdge = function(vEdge) {
              return new CoSEEdge(null, null, vEdge);
            };
            CoSELayout.prototype.initParameters = function() {
              FDLayout.prototype.initParameters.call(this, arguments);
              if (!this.isSubLayout) {
                if (CoSEConstants.DEFAULT_EDGE_LENGTH < 10) {
                  this.idealEdgeLength = 10;
                } else {
                  this.idealEdgeLength = CoSEConstants.DEFAULT_EDGE_LENGTH;
                }
                this.useSmartIdealEdgeLengthCalculation = CoSEConstants.DEFAULT_USE_SMART_IDEAL_EDGE_LENGTH_CALCULATION;
                this.springConstant = FDLayoutConstants.DEFAULT_SPRING_STRENGTH;
                this.repulsionConstant = FDLayoutConstants.DEFAULT_REPULSION_STRENGTH;
                this.gravityConstant = FDLayoutConstants.DEFAULT_GRAVITY_STRENGTH;
                this.compoundGravityConstant = FDLayoutConstants.DEFAULT_COMPOUND_GRAVITY_STRENGTH;
                this.gravityRangeFactor = FDLayoutConstants.DEFAULT_GRAVITY_RANGE_FACTOR;
                this.compoundGravityRangeFactor = FDLayoutConstants.DEFAULT_COMPOUND_GRAVITY_RANGE_FACTOR;
                this.prunedNodesAll = [];
                this.growTreeIterations = 0;
                this.afterGrowthIterations = 0;
                this.isTreeGrowing = false;
                this.isGrowthFinished = false;
                this.coolingCycle = 0;
                this.maxCoolingCycle = this.maxIterations / FDLayoutConstants.CONVERGENCE_CHECK_PERIOD;
                this.finalTemperature = FDLayoutConstants.CONVERGENCE_CHECK_PERIOD / this.maxIterations;
                this.coolingAdjuster = 1;
              }
            };
            CoSELayout.prototype.layout = function() {
              var createBendsAsNeeded = LayoutConstants.DEFAULT_CREATE_BENDS_AS_NEEDED;
              if (createBendsAsNeeded) {
                this.createBendpoints();
                this.graphManager.resetAllEdges();
              }
              this.level = 0;
              return this.classicLayout();
            };
            CoSELayout.prototype.classicLayout = function() {
              this.nodesWithGravity = this.calculateNodesToApplyGravitationTo();
              this.graphManager.setAllNodesToApplyGravitation(this.nodesWithGravity);
              this.calcNoOfChildrenForAllNodes();
              this.graphManager.calcLowestCommonAncestors();
              this.graphManager.calcInclusionTreeDepths();
              this.graphManager.getRoot().calcEstimatedSize();
              this.calcIdealEdgeLengths();
              if (!this.incremental) {
                var forest = this.getFlatForest();
                if (forest.length > 0) {
                  this.positionNodesRadially(forest);
                } else {
                  this.reduceTrees();
                  this.graphManager.resetAllNodesToApplyGravitation();
                  var allNodes = new Set(this.getAllNodes());
                  var intersection = this.nodesWithGravity.filter(function(x) {
                    return allNodes.has(x);
                  });
                  this.graphManager.setAllNodesToApplyGravitation(intersection);
                  this.positionNodesRandomly();
                }
              } else {
                if (CoSEConstants.TREE_REDUCTION_ON_INCREMENTAL) {
                  this.reduceTrees();
                  this.graphManager.resetAllNodesToApplyGravitation();
                  var allNodes = new Set(this.getAllNodes());
                  var intersection = this.nodesWithGravity.filter(function(x) {
                    return allNodes.has(x);
                  });
                  this.graphManager.setAllNodesToApplyGravitation(intersection);
                }
              }
              this.initSpringEmbedder();
              this.runSpringEmbedder();
              return true;
            };
            CoSELayout.prototype.tick = function() {
              this.totalIterations++;
              if (this.totalIterations === this.maxIterations && !this.isTreeGrowing && !this.isGrowthFinished) {
                if (this.prunedNodesAll.length > 0) {
                  this.isTreeGrowing = true;
                } else {
                  return true;
                }
              }
              if (this.totalIterations % FDLayoutConstants.CONVERGENCE_CHECK_PERIOD == 0 && !this.isTreeGrowing && !this.isGrowthFinished) {
                if (this.isConverged()) {
                  if (this.prunedNodesAll.length > 0) {
                    this.isTreeGrowing = true;
                  } else {
                    return true;
                  }
                }
                this.coolingCycle++;
                if (this.layoutQuality == 0) {
                  this.coolingAdjuster = this.coolingCycle;
                } else if (this.layoutQuality == 1) {
                  this.coolingAdjuster = this.coolingCycle / 3;
                }
                this.coolingFactor = Math.max(this.initialCoolingFactor - Math.pow(this.coolingCycle, Math.log(100 * (this.initialCoolingFactor - this.finalTemperature)) / Math.log(this.maxCoolingCycle)) / 100 * this.coolingAdjuster, this.finalTemperature);
                this.animationPeriod = Math.ceil(this.initialAnimationPeriod * Math.sqrt(this.coolingFactor));
              }
              if (this.isTreeGrowing) {
                if (this.growTreeIterations % 10 == 0) {
                  if (this.prunedNodesAll.length > 0) {
                    this.graphManager.updateBounds();
                    this.updateGrid();
                    this.growTree(this.prunedNodesAll);
                    this.graphManager.resetAllNodesToApplyGravitation();
                    var allNodes = new Set(this.getAllNodes());
                    var intersection = this.nodesWithGravity.filter(function(x) {
                      return allNodes.has(x);
                    });
                    this.graphManager.setAllNodesToApplyGravitation(intersection);
                    this.graphManager.updateBounds();
                    this.updateGrid();
                    this.coolingFactor = FDLayoutConstants.DEFAULT_COOLING_FACTOR_INCREMENTAL;
                  } else {
                    this.isTreeGrowing = false;
                    this.isGrowthFinished = true;
                  }
                }
                this.growTreeIterations++;
              }
              if (this.isGrowthFinished) {
                if (this.isConverged()) {
                  return true;
                }
                if (this.afterGrowthIterations % 10 == 0) {
                  this.graphManager.updateBounds();
                  this.updateGrid();
                }
                this.coolingFactor = FDLayoutConstants.DEFAULT_COOLING_FACTOR_INCREMENTAL * ((100 - this.afterGrowthIterations) / 100);
                this.afterGrowthIterations++;
              }
              var gridUpdateAllowed = !this.isTreeGrowing && !this.isGrowthFinished;
              var forceToNodeSurroundingUpdate = this.growTreeIterations % 10 == 1 && this.isTreeGrowing || this.afterGrowthIterations % 10 == 1 && this.isGrowthFinished;
              this.totalDisplacement = 0;
              this.graphManager.updateBounds();
              this.calcSpringForces();
              this.calcRepulsionForces(gridUpdateAllowed, forceToNodeSurroundingUpdate);
              this.calcGravitationalForces();
              this.moveNodes();
              this.animate();
              return false;
            };
            CoSELayout.prototype.getPositionsData = function() {
              var allNodes = this.graphManager.getAllNodes();
              var pData = {};
              for (var i = 0; i < allNodes.length; i++) {
                var rect = allNodes[i].rect;
                var id = allNodes[i].id;
                pData[id] = {
                  id,
                  x: rect.getCenterX(),
                  y: rect.getCenterY(),
                  w: rect.width,
                  h: rect.height
                };
              }
              return pData;
            };
            CoSELayout.prototype.runSpringEmbedder = function() {
              this.initialAnimationPeriod = 25;
              this.animationPeriod = this.initialAnimationPeriod;
              var layoutEnded = false;
              if (FDLayoutConstants.ANIMATE === "during") {
                this.emit("layoutstarted");
              } else {
                while (!layoutEnded) {
                  layoutEnded = this.tick();
                }
                this.graphManager.updateBounds();
              }
            };
            CoSELayout.prototype.calculateNodesToApplyGravitationTo = function() {
              var nodeList = [];
              var graph;
              var graphs = this.graphManager.getGraphs();
              var size = graphs.length;
              var i;
              for (i = 0; i < size; i++) {
                graph = graphs[i];
                graph.updateConnected();
                if (!graph.isConnected) {
                  nodeList = nodeList.concat(graph.getNodes());
                }
              }
              return nodeList;
            };
            CoSELayout.prototype.createBendpoints = function() {
              var edges = [];
              edges = edges.concat(this.graphManager.getAllEdges());
              var visited = /* @__PURE__ */ new Set();
              var i;
              for (i = 0; i < edges.length; i++) {
                var edge = edges[i];
                if (!visited.has(edge)) {
                  var source = edge.getSource();
                  var target = edge.getTarget();
                  if (source == target) {
                    edge.getBendpoints().push(new PointD());
                    edge.getBendpoints().push(new PointD());
                    this.createDummyNodesForBendpoints(edge);
                    visited.add(edge);
                  } else {
                    var edgeList = [];
                    edgeList = edgeList.concat(source.getEdgeListToNode(target));
                    edgeList = edgeList.concat(target.getEdgeListToNode(source));
                    if (!visited.has(edgeList[0])) {
                      if (edgeList.length > 1) {
                        var k;
                        for (k = 0; k < edgeList.length; k++) {
                          var multiEdge = edgeList[k];
                          multiEdge.getBendpoints().push(new PointD());
                          this.createDummyNodesForBendpoints(multiEdge);
                        }
                      }
                      edgeList.forEach(function(edge2) {
                        visited.add(edge2);
                      });
                    }
                  }
                }
                if (visited.size == edges.length) {
                  break;
                }
              }
            };
            CoSELayout.prototype.positionNodesRadially = function(forest) {
              var currentStartingPoint = new Point(0, 0);
              var numberOfColumns = Math.ceil(Math.sqrt(forest.length));
              var height = 0;
              var currentY = 0;
              var currentX = 0;
              var point = new PointD(0, 0);
              for (var i = 0; i < forest.length; i++) {
                if (i % numberOfColumns == 0) {
                  currentX = 0;
                  currentY = height;
                  if (i != 0) {
                    currentY += CoSEConstants.DEFAULT_COMPONENT_SEPERATION;
                  }
                  height = 0;
                }
                var tree = forest[i];
                var centerNode = Layout.findCenterOfTree(tree);
                currentStartingPoint.x = currentX;
                currentStartingPoint.y = currentY;
                point = CoSELayout.radialLayout(tree, centerNode, currentStartingPoint);
                if (point.y > height) {
                  height = Math.floor(point.y);
                }
                currentX = Math.floor(point.x + CoSEConstants.DEFAULT_COMPONENT_SEPERATION);
              }
              this.transform(new PointD(LayoutConstants.WORLD_CENTER_X - point.x / 2, LayoutConstants.WORLD_CENTER_Y - point.y / 2));
            };
            CoSELayout.radialLayout = function(tree, centerNode, startingPoint) {
              var radialSep = Math.max(this.maxDiagonalInTree(tree), CoSEConstants.DEFAULT_RADIAL_SEPARATION);
              CoSELayout.branchRadialLayout(centerNode, null, 0, 359, 0, radialSep);
              var bounds = LGraph.calculateBounds(tree);
              var transform = new Transform();
              transform.setDeviceOrgX(bounds.getMinX());
              transform.setDeviceOrgY(bounds.getMinY());
              transform.setWorldOrgX(startingPoint.x);
              transform.setWorldOrgY(startingPoint.y);
              for (var i = 0; i < tree.length; i++) {
                var node = tree[i];
                node.transform(transform);
              }
              var bottomRight = new PointD(bounds.getMaxX(), bounds.getMaxY());
              return transform.inverseTransformPoint(bottomRight);
            };
            CoSELayout.branchRadialLayout = function(node, parentOfNode, startAngle, endAngle, distance, radialSeparation) {
              var halfInterval = (endAngle - startAngle + 1) / 2;
              if (halfInterval < 0) {
                halfInterval += 180;
              }
              var nodeAngle = (halfInterval + startAngle) % 360;
              var teta = nodeAngle * IGeometry.TWO_PI / 360;
              var x_ = distance * Math.cos(teta);
              var y_ = distance * Math.sin(teta);
              node.setCenter(x_, y_);
              var neighborEdges = [];
              neighborEdges = neighborEdges.concat(node.getEdges());
              var childCount = neighborEdges.length;
              if (parentOfNode != null) {
                childCount--;
              }
              var branchCount = 0;
              var incEdgesCount = neighborEdges.length;
              var startIndex;
              var edges = node.getEdgesBetween(parentOfNode);
              while (edges.length > 1) {
                var temp = edges[0];
                edges.splice(0, 1);
                var index = neighborEdges.indexOf(temp);
                if (index >= 0) {
                  neighborEdges.splice(index, 1);
                }
                incEdgesCount--;
                childCount--;
              }
              if (parentOfNode != null) {
                startIndex = (neighborEdges.indexOf(edges[0]) + 1) % incEdgesCount;
              } else {
                startIndex = 0;
              }
              var stepAngle = Math.abs(endAngle - startAngle) / childCount;
              for (var i = startIndex; branchCount != childCount; i = ++i % incEdgesCount) {
                var currentNeighbor = neighborEdges[i].getOtherEnd(node);
                if (currentNeighbor == parentOfNode) {
                  continue;
                }
                var childStartAngle = (startAngle + branchCount * stepAngle) % 360;
                var childEndAngle = (childStartAngle + stepAngle) % 360;
                CoSELayout.branchRadialLayout(currentNeighbor, node, childStartAngle, childEndAngle, distance + radialSeparation, radialSeparation);
                branchCount++;
              }
            };
            CoSELayout.maxDiagonalInTree = function(tree) {
              var maxDiagonal = Integer.MIN_VALUE;
              for (var i = 0; i < tree.length; i++) {
                var node = tree[i];
                var diagonal = node.getDiagonal();
                if (diagonal > maxDiagonal) {
                  maxDiagonal = diagonal;
                }
              }
              return maxDiagonal;
            };
            CoSELayout.prototype.calcRepulsionRange = function() {
              return 2 * (this.level + 1) * this.idealEdgeLength;
            };
            CoSELayout.prototype.groupZeroDegreeMembers = function() {
              var self = this;
              var tempMemberGroups = {};
              this.memberGroups = {};
              this.idToDummyNode = {};
              var zeroDegree = [];
              var allNodes = this.graphManager.getAllNodes();
              for (var i = 0; i < allNodes.length; i++) {
                var node = allNodes[i];
                var parent = node.getParent();
                if (this.getNodeDegreeWithChildren(node) === 0 && (parent.id == void 0 || !this.getToBeTiled(parent))) {
                  zeroDegree.push(node);
                }
              }
              for (var i = 0; i < zeroDegree.length; i++) {
                var node = zeroDegree[i];
                var p_id = node.getParent().id;
                if (typeof tempMemberGroups[p_id] === "undefined")
                  tempMemberGroups[p_id] = [];
                tempMemberGroups[p_id] = tempMemberGroups[p_id].concat(node);
              }
              Object.keys(tempMemberGroups).forEach(function(p_id2) {
                if (tempMemberGroups[p_id2].length > 1) {
                  var dummyCompoundId = "DummyCompound_" + p_id2;
                  self.memberGroups[dummyCompoundId] = tempMemberGroups[p_id2];
                  var parent2 = tempMemberGroups[p_id2][0].getParent();
                  var dummyCompound = new CoSENode(self.graphManager);
                  dummyCompound.id = dummyCompoundId;
                  dummyCompound.paddingLeft = parent2.paddingLeft || 0;
                  dummyCompound.paddingRight = parent2.paddingRight || 0;
                  dummyCompound.paddingBottom = parent2.paddingBottom || 0;
                  dummyCompound.paddingTop = parent2.paddingTop || 0;
                  self.idToDummyNode[dummyCompoundId] = dummyCompound;
                  var dummyParentGraph = self.getGraphManager().add(self.newGraph(), dummyCompound);
                  var parentGraph = parent2.getChild();
                  parentGraph.add(dummyCompound);
                  for (var i2 = 0; i2 < tempMemberGroups[p_id2].length; i2++) {
                    var node2 = tempMemberGroups[p_id2][i2];
                    parentGraph.remove(node2);
                    dummyParentGraph.add(node2);
                  }
                }
              });
            };
            CoSELayout.prototype.clearCompounds = function() {
              var childGraphMap = {};
              var idToNode = {};
              this.performDFSOnCompounds();
              for (var i = 0; i < this.compoundOrder.length; i++) {
                idToNode[this.compoundOrder[i].id] = this.compoundOrder[i];
                childGraphMap[this.compoundOrder[i].id] = [].concat(this.compoundOrder[i].getChild().getNodes());
                this.graphManager.remove(this.compoundOrder[i].getChild());
                this.compoundOrder[i].child = null;
              }
              this.graphManager.resetAllNodes();
              this.tileCompoundMembers(childGraphMap, idToNode);
            };
            CoSELayout.prototype.clearZeroDegreeMembers = function() {
              var self = this;
              var tiledZeroDegreePack = this.tiledZeroDegreePack = [];
              Object.keys(this.memberGroups).forEach(function(id) {
                var compoundNode = self.idToDummyNode[id];
                tiledZeroDegreePack[id] = self.tileNodes(self.memberGroups[id], compoundNode.paddingLeft + compoundNode.paddingRight);
                compoundNode.rect.width = tiledZeroDegreePack[id].width;
                compoundNode.rect.height = tiledZeroDegreePack[id].height;
              });
            };
            CoSELayout.prototype.repopulateCompounds = function() {
              for (var i = this.compoundOrder.length - 1; i >= 0; i--) {
                var lCompoundNode = this.compoundOrder[i];
                var id = lCompoundNode.id;
                var horizontalMargin = lCompoundNode.paddingLeft;
                var verticalMargin = lCompoundNode.paddingTop;
                this.adjustLocations(this.tiledMemberPack[id], lCompoundNode.rect.x, lCompoundNode.rect.y, horizontalMargin, verticalMargin);
              }
            };
            CoSELayout.prototype.repopulateZeroDegreeMembers = function() {
              var self = this;
              var tiledPack = this.tiledZeroDegreePack;
              Object.keys(tiledPack).forEach(function(id) {
                var compoundNode = self.idToDummyNode[id];
                var horizontalMargin = compoundNode.paddingLeft;
                var verticalMargin = compoundNode.paddingTop;
                self.adjustLocations(tiledPack[id], compoundNode.rect.x, compoundNode.rect.y, horizontalMargin, verticalMargin);
              });
            };
            CoSELayout.prototype.getToBeTiled = function(node) {
              var id = node.id;
              if (this.toBeTiled[id] != null) {
                return this.toBeTiled[id];
              }
              var childGraph = node.getChild();
              if (childGraph == null) {
                this.toBeTiled[id] = false;
                return false;
              }
              var children = childGraph.getNodes();
              for (var i = 0; i < children.length; i++) {
                var theChild = children[i];
                if (this.getNodeDegree(theChild) > 0) {
                  this.toBeTiled[id] = false;
                  return false;
                }
                if (theChild.getChild() == null) {
                  this.toBeTiled[theChild.id] = false;
                  continue;
                }
                if (!this.getToBeTiled(theChild)) {
                  this.toBeTiled[id] = false;
                  return false;
                }
              }
              this.toBeTiled[id] = true;
              return true;
            };
            CoSELayout.prototype.getNodeDegree = function(node) {
              node.id;
              var edges = node.getEdges();
              var degree = 0;
              for (var i = 0; i < edges.length; i++) {
                var edge = edges[i];
                if (edge.getSource().id !== edge.getTarget().id) {
                  degree = degree + 1;
                }
              }
              return degree;
            };
            CoSELayout.prototype.getNodeDegreeWithChildren = function(node) {
              var degree = this.getNodeDegree(node);
              if (node.getChild() == null) {
                return degree;
              }
              var children = node.getChild().getNodes();
              for (var i = 0; i < children.length; i++) {
                var child = children[i];
                degree += this.getNodeDegreeWithChildren(child);
              }
              return degree;
            };
            CoSELayout.prototype.performDFSOnCompounds = function() {
              this.compoundOrder = [];
              this.fillCompexOrderByDFS(this.graphManager.getRoot().getNodes());
            };
            CoSELayout.prototype.fillCompexOrderByDFS = function(children) {
              for (var i = 0; i < children.length; i++) {
                var child = children[i];
                if (child.getChild() != null) {
                  this.fillCompexOrderByDFS(child.getChild().getNodes());
                }
                if (this.getToBeTiled(child)) {
                  this.compoundOrder.push(child);
                }
              }
            };
            CoSELayout.prototype.adjustLocations = function(organization, x, y, compoundHorizontalMargin, compoundVerticalMargin) {
              x += compoundHorizontalMargin;
              y += compoundVerticalMargin;
              var left = x;
              for (var i = 0; i < organization.rows.length; i++) {
                var row = organization.rows[i];
                x = left;
                var maxHeight = 0;
                for (var j = 0; j < row.length; j++) {
                  var lnode = row[j];
                  lnode.rect.x = x;
                  lnode.rect.y = y;
                  x += lnode.rect.width + organization.horizontalPadding;
                  if (lnode.rect.height > maxHeight)
                    maxHeight = lnode.rect.height;
                }
                y += maxHeight + organization.verticalPadding;
              }
            };
            CoSELayout.prototype.tileCompoundMembers = function(childGraphMap, idToNode) {
              var self = this;
              this.tiledMemberPack = [];
              Object.keys(childGraphMap).forEach(function(id) {
                var compoundNode = idToNode[id];
                self.tiledMemberPack[id] = self.tileNodes(childGraphMap[id], compoundNode.paddingLeft + compoundNode.paddingRight);
                compoundNode.rect.width = self.tiledMemberPack[id].width;
                compoundNode.rect.height = self.tiledMemberPack[id].height;
              });
            };
            CoSELayout.prototype.tileNodes = function(nodes, minWidth) {
              var verticalPadding = CoSEConstants.TILING_PADDING_VERTICAL;
              var horizontalPadding = CoSEConstants.TILING_PADDING_HORIZONTAL;
              var organization = {
                rows: [],
                rowWidth: [],
                rowHeight: [],
                width: 0,
                height: minWidth,
                // assume minHeight equals to minWidth
                verticalPadding,
                horizontalPadding
              };
              nodes.sort(function(n1, n2) {
                if (n1.rect.width * n1.rect.height > n2.rect.width * n2.rect.height)
                  return -1;
                if (n1.rect.width * n1.rect.height < n2.rect.width * n2.rect.height)
                  return 1;
                return 0;
              });
              for (var i = 0; i < nodes.length; i++) {
                var lNode = nodes[i];
                if (organization.rows.length == 0) {
                  this.insertNodeToRow(organization, lNode, 0, minWidth);
                } else if (this.canAddHorizontal(organization, lNode.rect.width, lNode.rect.height)) {
                  this.insertNodeToRow(organization, lNode, this.getShortestRowIndex(organization), minWidth);
                } else {
                  this.insertNodeToRow(organization, lNode, organization.rows.length, minWidth);
                }
                this.shiftToLastRow(organization);
              }
              return organization;
            };
            CoSELayout.prototype.insertNodeToRow = function(organization, node, rowIndex, minWidth) {
              var minCompoundSize = minWidth;
              if (rowIndex == organization.rows.length) {
                var secondDimension = [];
                organization.rows.push(secondDimension);
                organization.rowWidth.push(minCompoundSize);
                organization.rowHeight.push(0);
              }
              var w = organization.rowWidth[rowIndex] + node.rect.width;
              if (organization.rows[rowIndex].length > 0) {
                w += organization.horizontalPadding;
              }
              organization.rowWidth[rowIndex] = w;
              if (organization.width < w) {
                organization.width = w;
              }
              var h = node.rect.height;
              if (rowIndex > 0)
                h += organization.verticalPadding;
              var extraHeight = 0;
              if (h > organization.rowHeight[rowIndex]) {
                extraHeight = organization.rowHeight[rowIndex];
                organization.rowHeight[rowIndex] = h;
                extraHeight = organization.rowHeight[rowIndex] - extraHeight;
              }
              organization.height += extraHeight;
              organization.rows[rowIndex].push(node);
            };
            CoSELayout.prototype.getShortestRowIndex = function(organization) {
              var r = -1;
              var min = Number.MAX_VALUE;
              for (var i = 0; i < organization.rows.length; i++) {
                if (organization.rowWidth[i] < min) {
                  r = i;
                  min = organization.rowWidth[i];
                }
              }
              return r;
            };
            CoSELayout.prototype.getLongestRowIndex = function(organization) {
              var r = -1;
              var max = Number.MIN_VALUE;
              for (var i = 0; i < organization.rows.length; i++) {
                if (organization.rowWidth[i] > max) {
                  r = i;
                  max = organization.rowWidth[i];
                }
              }
              return r;
            };
            CoSELayout.prototype.canAddHorizontal = function(organization, extraWidth, extraHeight) {
              var sri = this.getShortestRowIndex(organization);
              if (sri < 0) {
                return true;
              }
              var min = organization.rowWidth[sri];
              if (min + organization.horizontalPadding + extraWidth <= organization.width)
                return true;
              var hDiff = 0;
              if (organization.rowHeight[sri] < extraHeight) {
                if (sri > 0)
                  hDiff = extraHeight + organization.verticalPadding - organization.rowHeight[sri];
              }
              var add_to_row_ratio;
              if (organization.width - min >= extraWidth + organization.horizontalPadding) {
                add_to_row_ratio = (organization.height + hDiff) / (min + extraWidth + organization.horizontalPadding);
              } else {
                add_to_row_ratio = (organization.height + hDiff) / organization.width;
              }
              hDiff = extraHeight + organization.verticalPadding;
              var add_new_row_ratio;
              if (organization.width < extraWidth) {
                add_new_row_ratio = (organization.height + hDiff) / extraWidth;
              } else {
                add_new_row_ratio = (organization.height + hDiff) / organization.width;
              }
              if (add_new_row_ratio < 1)
                add_new_row_ratio = 1 / add_new_row_ratio;
              if (add_to_row_ratio < 1)
                add_to_row_ratio = 1 / add_to_row_ratio;
              return add_to_row_ratio < add_new_row_ratio;
            };
            CoSELayout.prototype.shiftToLastRow = function(organization) {
              var longest = this.getLongestRowIndex(organization);
              var last = organization.rowWidth.length - 1;
              var row = organization.rows[longest];
              var node = row[row.length - 1];
              var diff = node.width + organization.horizontalPadding;
              if (organization.width - organization.rowWidth[last] > diff && longest != last) {
                row.splice(-1, 1);
                organization.rows[last].push(node);
                organization.rowWidth[longest] = organization.rowWidth[longest] - diff;
                organization.rowWidth[last] = organization.rowWidth[last] + diff;
                organization.width = organization.rowWidth[instance.getLongestRowIndex(organization)];
                var maxHeight = Number.MIN_VALUE;
                for (var i = 0; i < row.length; i++) {
                  if (row[i].height > maxHeight)
                    maxHeight = row[i].height;
                }
                if (longest > 0)
                  maxHeight += organization.verticalPadding;
                var prevTotal = organization.rowHeight[longest] + organization.rowHeight[last];
                organization.rowHeight[longest] = maxHeight;
                if (organization.rowHeight[last] < node.height + organization.verticalPadding)
                  organization.rowHeight[last] = node.height + organization.verticalPadding;
                var finalTotal = organization.rowHeight[longest] + organization.rowHeight[last];
                organization.height += finalTotal - prevTotal;
                this.shiftToLastRow(organization);
              }
            };
            CoSELayout.prototype.tilingPreLayout = function() {
              if (CoSEConstants.TILE) {
                this.groupZeroDegreeMembers();
                this.clearCompounds();
                this.clearZeroDegreeMembers();
              }
            };
            CoSELayout.prototype.tilingPostLayout = function() {
              if (CoSEConstants.TILE) {
                this.repopulateZeroDegreeMembers();
                this.repopulateCompounds();
              }
            };
            CoSELayout.prototype.reduceTrees = function() {
              var prunedNodesAll = [];
              var containsLeaf = true;
              var node;
              while (containsLeaf) {
                var allNodes = this.graphManager.getAllNodes();
                var prunedNodesInStepTemp = [];
                containsLeaf = false;
                for (var i = 0; i < allNodes.length; i++) {
                  node = allNodes[i];
                  if (node.getEdges().length == 1 && !node.getEdges()[0].isInterGraph && node.getChild() == null) {
                    prunedNodesInStepTemp.push([node, node.getEdges()[0], node.getOwner()]);
                    containsLeaf = true;
                  }
                }
                if (containsLeaf == true) {
                  var prunedNodesInStep = [];
                  for (var j = 0; j < prunedNodesInStepTemp.length; j++) {
                    if (prunedNodesInStepTemp[j][0].getEdges().length == 1) {
                      prunedNodesInStep.push(prunedNodesInStepTemp[j]);
                      prunedNodesInStepTemp[j][0].getOwner().remove(prunedNodesInStepTemp[j][0]);
                    }
                  }
                  prunedNodesAll.push(prunedNodesInStep);
                  this.graphManager.resetAllNodes();
                  this.graphManager.resetAllEdges();
                }
              }
              this.prunedNodesAll = prunedNodesAll;
            };
            CoSELayout.prototype.growTree = function(prunedNodesAll) {
              var lengthOfPrunedNodesInStep = prunedNodesAll.length;
              var prunedNodesInStep = prunedNodesAll[lengthOfPrunedNodesInStep - 1];
              var nodeData;
              for (var i = 0; i < prunedNodesInStep.length; i++) {
                nodeData = prunedNodesInStep[i];
                this.findPlaceforPrunedNode(nodeData);
                nodeData[2].add(nodeData[0]);
                nodeData[2].add(nodeData[1], nodeData[1].source, nodeData[1].target);
              }
              prunedNodesAll.splice(prunedNodesAll.length - 1, 1);
              this.graphManager.resetAllNodes();
              this.graphManager.resetAllEdges();
            };
            CoSELayout.prototype.findPlaceforPrunedNode = function(nodeData) {
              var gridForPrunedNode;
              var nodeToConnect;
              var prunedNode = nodeData[0];
              if (prunedNode == nodeData[1].source) {
                nodeToConnect = nodeData[1].target;
              } else {
                nodeToConnect = nodeData[1].source;
              }
              var startGridX = nodeToConnect.startX;
              var finishGridX = nodeToConnect.finishX;
              var startGridY = nodeToConnect.startY;
              var finishGridY = nodeToConnect.finishY;
              var upNodeCount = 0;
              var downNodeCount = 0;
              var rightNodeCount = 0;
              var leftNodeCount = 0;
              var controlRegions = [upNodeCount, rightNodeCount, downNodeCount, leftNodeCount];
              if (startGridY > 0) {
                for (var i = startGridX; i <= finishGridX; i++) {
                  controlRegions[0] += this.grid[i][startGridY - 1].length + this.grid[i][startGridY].length - 1;
                }
              }
              if (finishGridX < this.grid.length - 1) {
                for (var i = startGridY; i <= finishGridY; i++) {
                  controlRegions[1] += this.grid[finishGridX + 1][i].length + this.grid[finishGridX][i].length - 1;
                }
              }
              if (finishGridY < this.grid[0].length - 1) {
                for (var i = startGridX; i <= finishGridX; i++) {
                  controlRegions[2] += this.grid[i][finishGridY + 1].length + this.grid[i][finishGridY].length - 1;
                }
              }
              if (startGridX > 0) {
                for (var i = startGridY; i <= finishGridY; i++) {
                  controlRegions[3] += this.grid[startGridX - 1][i].length + this.grid[startGridX][i].length - 1;
                }
              }
              var min = Integer.MAX_VALUE;
              var minCount;
              var minIndex;
              for (var j = 0; j < controlRegions.length; j++) {
                if (controlRegions[j] < min) {
                  min = controlRegions[j];
                  minCount = 1;
                  minIndex = j;
                } else if (controlRegions[j] == min) {
                  minCount++;
                }
              }
              if (minCount == 3 && min == 0) {
                if (controlRegions[0] == 0 && controlRegions[1] == 0 && controlRegions[2] == 0) {
                  gridForPrunedNode = 1;
                } else if (controlRegions[0] == 0 && controlRegions[1] == 0 && controlRegions[3] == 0) {
                  gridForPrunedNode = 0;
                } else if (controlRegions[0] == 0 && controlRegions[2] == 0 && controlRegions[3] == 0) {
                  gridForPrunedNode = 3;
                } else if (controlRegions[1] == 0 && controlRegions[2] == 0 && controlRegions[3] == 0) {
                  gridForPrunedNode = 2;
                }
              } else if (minCount == 2 && min == 0) {
                var random = Math.floor(Math.random() * 2);
                if (controlRegions[0] == 0 && controlRegions[1] == 0) {
                  if (random == 0) {
                    gridForPrunedNode = 0;
                  } else {
                    gridForPrunedNode = 1;
                  }
                } else if (controlRegions[0] == 0 && controlRegions[2] == 0) {
                  if (random == 0) {
                    gridForPrunedNode = 0;
                  } else {
                    gridForPrunedNode = 2;
                  }
                } else if (controlRegions[0] == 0 && controlRegions[3] == 0) {
                  if (random == 0) {
                    gridForPrunedNode = 0;
                  } else {
                    gridForPrunedNode = 3;
                  }
                } else if (controlRegions[1] == 0 && controlRegions[2] == 0) {
                  if (random == 0) {
                    gridForPrunedNode = 1;
                  } else {
                    gridForPrunedNode = 2;
                  }
                } else if (controlRegions[1] == 0 && controlRegions[3] == 0) {
                  if (random == 0) {
                    gridForPrunedNode = 1;
                  } else {
                    gridForPrunedNode = 3;
                  }
                } else {
                  if (random == 0) {
                    gridForPrunedNode = 2;
                  } else {
                    gridForPrunedNode = 3;
                  }
                }
              } else if (minCount == 4 && min == 0) {
                var random = Math.floor(Math.random() * 4);
                gridForPrunedNode = random;
              } else {
                gridForPrunedNode = minIndex;
              }
              if (gridForPrunedNode == 0) {
                prunedNode.setCenter(nodeToConnect.getCenterX(), nodeToConnect.getCenterY() - nodeToConnect.getHeight() / 2 - FDLayoutConstants.DEFAULT_EDGE_LENGTH - prunedNode.getHeight() / 2);
              } else if (gridForPrunedNode == 1) {
                prunedNode.setCenter(nodeToConnect.getCenterX() + nodeToConnect.getWidth() / 2 + FDLayoutConstants.DEFAULT_EDGE_LENGTH + prunedNode.getWidth() / 2, nodeToConnect.getCenterY());
              } else if (gridForPrunedNode == 2) {
                prunedNode.setCenter(nodeToConnect.getCenterX(), nodeToConnect.getCenterY() + nodeToConnect.getHeight() / 2 + FDLayoutConstants.DEFAULT_EDGE_LENGTH + prunedNode.getHeight() / 2);
              } else {
                prunedNode.setCenter(nodeToConnect.getCenterX() - nodeToConnect.getWidth() / 2 - FDLayoutConstants.DEFAULT_EDGE_LENGTH - prunedNode.getWidth() / 2, nodeToConnect.getCenterY());
              }
            };
            module2.exports = CoSELayout;
          },
          /* 7 */
          /***/
          function(module2, exports2, __webpack_require__) {
            var coseBase2 = {};
            coseBase2.layoutBase = __webpack_require__(0);
            coseBase2.CoSEConstants = __webpack_require__(1);
            coseBase2.CoSEEdge = __webpack_require__(2);
            coseBase2.CoSEGraph = __webpack_require__(3);
            coseBase2.CoSEGraphManager = __webpack_require__(4);
            coseBase2.CoSELayout = __webpack_require__(6);
            coseBase2.CoSENode = __webpack_require__(5);
            module2.exports = coseBase2;
          }
          /******/
        ])
      );
    });
  })(coseBase);
  return coseBase.exports;
}
export {
  requireCoseBase as r
};
