# Guardcat 后台监控

## 依赖版本
1. node: 18.12.1
2. npm: 8.19.2
3. cnpm: 9.0.1

# 相关官网
## Vue
- https://cn.vuejs.org/guide/introduction.html

## Vite
- https://cn.vitejs.dev/guide/

##  tui-editor
- https://ui.toast.com/tui-editor
- https://www.npmjs.com/package/@toast-ui/editor-plugin-color-syntax
- https://www.npmjs.com/package/@toast-ui/editor-plugin-code-syntax-highlight


# package.json
```json
{
  "name": "guardcat",
  "version": "1.0",
  "private": true,
  "scripts": {
    "dev": "vite",
    "build": "run-p type-check build-only",
    "preview": "vite preview",
    "build-only": "vite build",
    "type-check": "vue-tsc --noEmit --skipLibCheck"
  },
  "dependencies": {
    "@codemirror/lang-java": "^6.0.1",
    "@codemirror/lang-json": "^6.0.1",
    "@codemirror/lang-sql": "^6.5.0",
    "@codemirror/theme-one-dark": "^6.1.2",
    "@toast-ui/editor": "^3.2.2",
    "@toast-ui/editor-plugin-code-syntax-highlight": "^3.1.0",
    "@toast-ui/editor-plugin-color-syntax": "^3.1.0",
    "axios": "^1.2.2",
    "echarts": "^5.4.1",
    "element-plus": "^2.2.28",
    "pinia": "^2.0.28",
    "svg-sprite-loader": "^2.0.3",
    "vue": "^3.2.45",
    "vue-codemirror": "^6.1.1",
    "vue-router": "^4.1.6"
  },
  "devDependencies": {
    "@types/codemirror": "^5.60.7",
    "@types/node": "^18.11.12",
    "@vitejs/plugin-vue": "^4.0.0",
    "@vitejs/plugin-vue-jsx": "^3.0.0",
    "@vue/tsconfig": "^0.1.3",
    "npm-run-all": "^4.1.5",
    "rollup-plugin-visualizer": "^5.9.0",
    "sass": "^1.57.1",
    "typescript": "~4.7.4",
    "unplugin-auto-import": "^0.16.4",
    "unplugin-element-plus": "^0.7.1",
    "unplugin-vue-components": "^0.25.0",
    "vite": "^4.0.0",
    "vue-tsc": "^1.0.12"
  }
}


```