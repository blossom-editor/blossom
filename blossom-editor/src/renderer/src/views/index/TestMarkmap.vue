<template>
  <div class="tem-root">
    <el-button @click="mark">渲染</el-button>
    <svg id="target" style="width: 100%; height: 100%"></svg>
  </div>
</template>

<script setup lang="ts">
import { Transformer } from 'markmap-lib'
import { Markmap } from 'markmap-view'

const markdown = `
# a
## bb
## 123123213123
### ccc
### qwewqe
### 1231232131
### 1232132
`

const transformer = new Transformer()

const mark = () => {
  // 1. transform Markdown
  const { root } = transformer.transform(markdown)

  // // 2. get assets
  // // either get assets required by used features
  // const { styles, scripts } = transformer.getUsedAssets(features)

  // // // or get all possible assets that could be used later
  // const { styles, scripts } = transformer.getAssets(features)

  // // 1. load assets
  // if (styles) loadCSS(styles)
  // if (scripts) loadJS(scripts, { getMarkmap: () => Markmap })

  // // 2. create markmap
  // // `options` is optional, i.e. `undefined` can be passed here
  Markmap.create('#target', {}, root) // -> returns a Markmap instance

  // const svgEl = document.querySelector('#target')
  // Markmap.create(svgEl, {}) // -> returns a Markmap instance
}
</script>

<style lang="scss">
.tem-root {
  @include box(100%, 100%);
}
</style>
