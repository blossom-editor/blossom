import { p as provideKeyCurArticleInfo, o as openNewArticleReferenceWindow, a as openNewArticleLogWindow, _ as _export_sfc } from "./index-c0c27055.js";
import { E as defineComponent, z as inject, ah as resolveComponent, o as openBlock, c as createElementBlock, O as createVNode, R as withCtx, a as createBaseVNode, W as toDisplayString, u as unref, X as createCommentVNode, V as createTextVNode, ab as toRaw, ay as pushScopeId, az as popScopeId } from "./@vue-364b3ba4.js";
import "./element-plus-e2d52cdd.js";
import "./lodash-es-42b2b1f3.js";
import "./async-validator-3256c125.js";
import "./@vueuse-19bc569b.js";
import "./@element-plus-0eaea524.js";
import "./dayjs-6d657463.js";
import "./@braintree-12043bbe.js";
import "./@ctrl-ab5a38b7.js";
import "./@popperjs-8eb851c6.js";
import "./escape-html-4392a897.js";
import "./normalize-wheel-es-a50d6d2e.js";
import "./vue-router-f8cb7117.js";
import "./axios-52a074ad.js";
import "./pinia-12acb2c6.js";
import "./lodash-a31254c0.js";
import "./echarts-81ee5d1a.js";
import "./zrender-7e0bbddb.js";
import "./@codemirror-1642cad3.js";
import "./@lezer-f512215d.js";
import "./crelt-e4f1dcc3.js";
import "./style-mod-9b511968.js";
import "./w3c-keyname-2f5b428c.js";
import "./marked-5af8cb72.js";
import "./marked-katex-extension-3867ecbe.js";
import "./katex-80f36097.js";
import "./marked-highlight-2c456749.js";
import "./highlight.js-e85a0efa.js";
import "./mermaid-00332b60.js";
import "./d3-transition-fc8016c4.js";
import "./d3-dispatch-9936551e.js";
import "./d3-timer-98fd67ae.js";
import "./d3-interpolate-44ef0aea.js";
import "./d3-color-65ecfa0f.js";
import "./d3-selection-ec178582.js";
import "./d3-ease-921d15ba.js";
import "./d3-zoom-f0aa3407.js";
import "./d3-drag-e94a8d41.js";
import "./dompurify-4ba20628.js";
import "./dagre-d3-es-921003d2.js";
import "./d3-shape-a0afbb89.js";
import "./d3-path-5f8d6c00.js";
import "./d3-fetch-bbc298d1.js";
import "./khroma-36c1e783.js";
import "./uuid-3874c49c.js";
import "./d3-scale-8ddd7af4.js";
import "./internmap-0ab9411c.js";
import "./d3-array-511ddb60.js";
import "./d3-format-6c965616.js";
import "./d3-time-format-b4bed30c.js";
import "./d3-time-a52c0c86.js";
import "./d3-axis-c99728e0.js";
import "./elkjs-f7b2278c.js";
import "./ts-dedent-eeefe080.js";
import "./mdast-util-from-markdown-64cdcdcb.js";
import "./micromark-f6c6e66c.js";
import "./micromark-util-combine-extensions-25166568.js";
import "./micromark-util-chunked-b50d5f7c.js";
import "./micromark-factory-space-6db398ce.js";
import "./micromark-util-character-89d8b557.js";
import "./micromark-core-commonmark-e993cb99.js";
import "./micromark-util-classify-character-5d623242.js";
import "./micromark-util-resolve-all-e737a054.js";
import "./decode-named-character-reference-3508650d.js";
import "./micromark-util-subtokenize-b5a12a10.js";
import "./micromark-factory-destination-9634da8c.js";
import "./micromark-factory-label-685637c9.js";
import "./micromark-factory-title-6caff633.js";
import "./micromark-factory-whitespace-25e35b8b.js";
import "./micromark-util-normalize-identifier-4f823864.js";
import "./micromark-util-html-tag-name-b67bc85d.js";
import "./micromark-util-decode-numeric-character-reference-334bc6ac.js";
import "./micromark-util-decode-string-c5a05620.js";
import "./unist-util-stringify-position-f4d19e8d.js";
import "./mdast-util-to-string-e503658e.js";
import "./cytoscape-4694d5ae.js";
import "./cytoscape-cose-bilkent-2ffcadaf.js";
import "./cose-base-d024680b.js";
import "./layout-base-c9290116.js";
import "./stylis-bacbcf2a.js";
import "./d3-sankey-57c9b703.js";
import "./d3-scale-chromatic-fa461606.js";
import "./markmap-lib-4e7f7dd6.js";
import "./@babel-d9a14db7.js";
import "./remarkable-9ef756fc.js";
import "./markmap-common-e19b15e2.js";
import "./remarkable-katex-9c6f6947.js";
import "./js-yaml-54b37f1b.js";
import "./markmap-view-ae516497.js";
import "./hotkeys-js-926ea2e6.js";
import "./codemirror-1bcbe02b.js";
import "./prettier-e922c0c2.js";
const _withScopeId = (n) => (pushScopeId("data-v-14c9d11d"), n = n(), popScopeId(), n);
const _hoisted_1 = { class: "editor-status-root" };
const _hoisted_2 = { key: 0 };
const _hoisted_3 = /* @__PURE__ */ _withScopeId(() => /* @__PURE__ */ createBaseVNode("span", { class: "iconbl bl-a-filehistory-line" }, null, -1));
const _hoisted_4 = /* @__PURE__ */ _withScopeId(() => /* @__PURE__ */ createBaseVNode("span", { class: "iconbl bl-correlation-line" }, null, -1));
const _sfc_main = /* @__PURE__ */ defineComponent({
  __name: "EditorStatus",
  props: {
    renderInterval: {
      type: Number,
      default: 0
    }
  },
  setup(__props) {
    const props = __props;
    const curDoc = inject(provideKeyCurArticleInfo);
    const openArticleReferenceWindow = () => {
      if (curDoc && curDoc.value) {
        openNewArticleReferenceWindow(toRaw(curDoc.value));
      }
    };
    const openArticleLogWindow = () => {
      if (curDoc && curDoc.value) {
        openNewArticleLogWindow(toRaw(curDoc.value));
      }
    };
    return (_ctx, _cache) => {
      const _component_bl_row = resolveComponent("bl-row");
      const _component_bl_col = resolveComponent("bl-col");
      return openBlock(), createElementBlock("div", _hoisted_1, [
        createVNode(_component_bl_row, {
          width: "calc(100% - 240px)",
          height: "100%",
          class: "status-item-container"
        }, {
          default: withCtx(() => [
            createBaseVNode("div", null, " 《" + toDisplayString(unref(curDoc)?.name) + "》 ", 1),
            createBaseVNode("div", null, " 版本:" + toDisplayString(unref(curDoc)?.version), 1),
            createBaseVNode("div", null, " 字数:" + toDisplayString(unref(curDoc)?.words), 1),
            createBaseVNode("div", null, " 最近修改:" + toDisplayString(unref(curDoc)?.updTime), 1),
            unref(curDoc)?.openTime ? (openBlock(), createElementBlock("div", _hoisted_2, " 发布:" + toDisplayString(unref(curDoc)?.openTime), 1)) : createCommentVNode("", true)
          ]),
          _: 1
        }),
        createVNode(_component_bl_row, {
          just: "flex-end",
          width: "240px",
          height: "100%",
          class: "status-item-container"
        }, {
          default: withCtx(() => [
            createBaseVNode("div", { onClick: openArticleLogWindow }, [
              _hoisted_3,
              createTextVNode(" 编辑记录 ")
            ]),
            createBaseVNode("div", { onClick: openArticleReferenceWindow }, [
              _hoisted_4,
              createTextVNode(" 引用网络 ")
            ]),
            createVNode(_component_bl_col, {
              width: "100px",
              just: "center"
            }, {
              default: withCtx(() => [
                createTextVNode(" 渲染用时: " + toDisplayString(props.renderInterval) + "ms ", 1)
              ]),
              _: 1
            })
          ]),
          _: 1
        })
      ]);
    };
  }
});
const EditorStatus_vue_vue_type_style_index_0_scoped_14c9d11d_lang = "";
const EditorStatus = /* @__PURE__ */ _export_sfc(_sfc_main, [["__scopeId", "data-v-14c9d11d"]]);
export {
  EditorStatus as default
};
