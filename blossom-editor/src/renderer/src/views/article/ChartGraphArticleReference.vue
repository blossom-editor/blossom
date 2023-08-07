<template>
  <div class="article-reference-root">
    <div class="setting">
      <bl-row>
        <el-button-group class="ml-4">
          <el-button type="primary" @click="getArticleRefList(true)">仅内部文章</el-button>
          <el-button type="primary" @click="getArticleRefList(false)">含外网文章</el-button>
        </el-button-group>
      </bl-row>
      <bl-row class="title">
        文章引用网络
      </bl-row>
      <bl-row>
        <bl-col class="symbol" justify-content="center">
          <div class="inner"></div> 内部文章
        </bl-col>
        <bl-col class="symbol" justify-content="center">
          <div class="outside"></div> 外网文章
        </bl-col>
      </bl-row>
      <bl-col class="desc">
        <bl-row style="margin-bottom: 0;">说明:</bl-row>
        <ol>
          <li>如果文章没有任何引用, 则不会出现在引用网络中.</li>
          <li>文章名称必须唯一, 相同链接如果有不同的名称, 则会以其中一条为准.</li>
          <li>使用简短的链接名称, 有助于在知识网络中显示.</li>
          <li>点击查看详情.</li>
        </ol>
      </bl-col>
    </div>
    <div class="app-relation-graph-chart" ref="ChartGraphRef"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from "vue"
import { useDark } from '@vueuse/core'
import { articleRefListApi } from "@renderer/api/blossom"
import { useUserStore } from '@renderer/stores/user'

// echarts
import * as echarts from 'echarts/core'
import { TitleComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import { GraphChart } from 'echarts/charts'
import { CanvasRenderer } from 'echarts/renderers'
echarts.use([TitleComponent, TooltipComponent, LegendComponent, GraphChart, CanvasRenderer])

const isDark = useDark()

const userStore = useUserStore()

// -------------------- data
const ChartGraphRef = ref<any>(null)
let chartGraph: any
let nodes: any = [{}]
let links: any = [{}]

const getArticleRefList = (onlyInner: boolean) => {
  articleRefListApi({ onlyInner: onlyInner }).then(resp => {
    nodes = resp.data.nodes.map((node: any) => {
      if (node.artType == 11) {
        node.itemStyle = { color: isDark.value ? '#614E8A' : '#ad8cf2' }
      }
      if (node.artType == 21) {
        node.itemStyle = { color: isDark.value ? '#937100' : '#fdc81a' }
      }
      node.symbolSize = getCount(node.name, resp.data.links)
      return node
    })
    links = resp.data.links
    renderChart()
  })
}
const ascending = 1
const getCount = (name: string, links: any[]): number => {
  let count: number = 15
  for (let i = 0; i < links.length; i++) {
    let link = links[i]
    if (link.source == name) {
      count += ascending
    }
    if (link.target == name) {
      count += ascending
    }
  }
  return count
}

const renderChart = () => {
  chartGraph.setOption({
    tooltip: {
      position: [20, 250],
      triggerOn: 'click',
      enterable: true,
      alwaysShowContent: true,
      borderWidth: 0,
      borderColor: 'none',
      padding: 0,
      formatter: (params: any) => {
        if (params.dataType === 'edge') {
          return
        }
        let url = ''
        if (!params.data.inner) {
          url = `<div>地址: <a target="_blank" href="${params.data.artUrl}">${params.data.artUrl}</a></div>`
        } else {
          url = `<div>地址: <a target="_blank" href="${userStore.userinfo.params.WEB_ARTICLE_URL + params.data.artId}">${userStore.userinfo.params.WEB_ARTICLE_URL + params.data.artId}</a></div>`
        }
        // console.log(params);
        return `<div class="chart-graph-article-ref-tooltip">
          <div class="title">${params.data.name}</div>
          <div class="content">
            <div>类型: ${params.data.inner ? '内部文章' : '外网文章'}</div>
            ${url}
          </div>
          </div>`
      }
    },
    series: [
      {
        type: 'graph',
        layout: 'force',
        top: 100, bottom: 100,
        force: {
          repulsion: 150, // 节点之间的斥力因子。
          friction: 0.1 // 这个参数能减缓节点的移动速度。取值范围 0 到 1。
        },
        draggable: false,
        symbolSize: 15,
        animation: false,
        animationThreshold: 1000,
        animationDuration: 1000,
        zoom: 1,
        roam: true,
        label: {
          show: true,
          fontSize: 10,
          color: isDark.value ? '#DCDCDC' : '#000000',
          formatter: '{b}'
        },
        labelLayout: {
          hideOverlap: true
        },
        itemStyle: {
        },
        // autoCurveness: true,
        lineStyle: {
          curveness: 0.1
        },
        // 箭头的开始, 结束图形
        edgeSymbol: ['circle', 'arrow'],
        // 箭头的开始, 结束图形大小
        edgeSymbolSize: [0, 10],
        edgeLabel: { fontSize: 10 },
        emphasis: {
          focus: 'adjacency',
          lineStyle: {
            width: 5
          }
        },
        blur: {
          label: { show: false }, edgeLabel: { show: false },
        },
        data: nodes,
        links: links,
        categories: nodes.length > 0 ? nodes.map((item: any) => {
          return item.name
        }) : ''
      }
    ]
  });
}

const init = () => {
  chartGraph = echarts.init(ChartGraphRef.value);
}

/**
 * 防抖
 */
let debounceTimeout: NodeJS.Timeout | undefined;
function debounce(fn: () => void, time = 500) {
  if (debounceTimeout != undefined) {
    clearTimeout(debounceTimeout);
  }
  debounceTimeout = setTimeout(fn, time);
}

const windowResize = () => {
  debounce(() => {
    chartGraph.resize()
  }, 300)
}

onMounted(() => {
  init()
  windowResize()
  getArticleRefList(true)
  window.addEventListener("resize", windowResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', windowResize)
})


</script>

<style scoped lang="scss">
.article-reference-root {
  @include box(100%, 100%);
  position: relative;

  .setting {
    position: absolute;
    z-index: 99;
    left: 20px;

    .title {
      color: var(--el-color-primary);
      text-shadow: var(--xz-text-shadow);
      font-weight: bold;
      height: 40px;
    }

    .desc {
      @include font(12px, 300);
      color: var(--el-text-color-secondary);
      border: 1px dashed #AAAAAA;
      border-radius: 5px;
      padding: 10px;

      ol {
        margin: 0;
        padding-left: 30px;
      }
    }

    .symbol {
      font-size: 12px;
      color: var(--el-color-primary);
      margin-bottom: 10px;
      margin-right: 10px;
    }

    .inner,
    .outside {
      @include box(20px, 20px);
      box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.441);
      border-radius: 50%;
      margin-bottom: 10px;
    }

    .inner {
      background-color: #ad8cf2;
    }


    .outside {
      background-color: #fdc81a;
    }
  }

  .app-relation-graph-chart {
    @include box(100%, 100%);
  }

}
</style>

<style lang=scss>
.chart-graph-article-ref-tooltip {
  max-width: 400px;
  word-break: break-all;
  white-space: normal;
  background-color: var(--xz-html-color);
  border-radius: 4px;
  border: 1px solid var(--el-color-primary-light-5);

  .title {
    @include font(15px, 700);
    border-bottom: 1px solid var(--el-color-primary-light-5);
    padding: 10px;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }

  .content {
    font-size: 12px;
    padding: 5px 10px;
  }
}
</style>