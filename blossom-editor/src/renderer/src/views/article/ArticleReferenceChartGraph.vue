<template>
  <div class="header">
    <AppHeader simple></AppHeader>
  </div>
  <div class="article-reference-root">
    <div class="setting">
      <bl-row>
        <el-button-group class="ml-4">
          <el-button type="primary" @click="getArticleRefList(true)">仅内部文章</el-button>
          <el-button type="primary" @click="getArticleRefList(false)">含外网文章</el-button>
        </el-button-group>
      </bl-row>
      <bl-row style="margin-top: 10px">
        <el-checkbox v-model="showOutsideName" border @change="getArticleRefList(false)">显示外网文章名称</el-checkbox>
      </bl-row>
      <bl-row class="title" just="center"> 文章引用网络 </bl-row>
      <bl-row just="center">
        <bl-col class="symbol" just="center">
          <div class="inside"></div>
          内部文章<br /><span>({{ stat.inside }}篇)</span>
        </bl-col>
        <bl-col class="symbol" just="center">
          <div class="outside"></div>
          外网文章<br /><span>({{ stat.outside }}篇)</span>
        </bl-col>
      </bl-row>
    </div>
    <div class="desc">
      <div style="margin-bottom: 0">说明:</div>
      <ol>
        <li>如果文章没有任何引用, 则不会出现在引用网络中.</li>
        <li>文章名称必须唯一, 相同链接如果有不同的名称, 则会以其中一条为准.</li>
        <li>使用简短的链接名称, 有助于在知识网络中显示.</li>
        <li>点击查看详情.</li>
      </ol>
    </div>
    <div class="app-relation-graph-chart" ref="ChartGraphRef"></div>
  </div>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'
import { ref, onMounted, onUnmounted } from 'vue'
import { useDark } from '@vueuse/core'
import { articleRefListApi } from '@renderer/api/blossom'
import { useUserStore } from '@renderer/stores/user'

// echarts
import * as echarts from 'echarts/core'
import { TitleComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import { GraphChart } from 'echarts/charts'
import { CanvasRenderer } from 'echarts/renderers'
import { isNotBlank, isNotNull } from '@renderer/assets/utils/obj'
import { getPrimaryColor } from '@renderer/scripts/global-theme'
import AppHeader from '@renderer/components/AppHeader.vue'

echarts.use([TitleComponent, TooltipComponent, LegendComponent, GraphChart, CanvasRenderer])

const isDark = useDark()

const route = useRoute()
const userStore = useUserStore()

// -------------------- data
const showOutsideName = ref(false)
const ChartGraphRef = ref<any>(null)
let articleId = ''
let chartGraph: any
let nodes: any = [{}]
let links: any = [{}]
let stat = ref({
  inside: 0,
  outside: 0
})

let inside: any = { itemStyle: {}, label: {} }
let insideUnknown: any = { itemStyle: {}, label: {} }
let outside: any = { itemStyle: {}, label: {} }
const changeStyle = () => {
  let primaryColor = getPrimaryColor()
  // 节点数量统计
  stat.value = { inside: 0, outside: 0 }
  inside = {
    itemStyle: {
      color: primaryColor.color
    },
    label: {
      fontSize: 14,
      color: isDark.value ? '#BABABA' : '#000000',
      textBorderColor: isDark.value ? '#3B3B3B' : '#E7E7E7',
      textBorderWidth: 2
    }
  }
  insideUnknown = {
    itemStyle: {
      color: isDark.value ? '#7B0000' : '#EB6969'
    },
    label: {
      fontSize: 13,
      color: isDark.value ? '#BABABA' : '#030303',
      textBorderColor: isDark.value ? '#7B0000' : '#EB6969',
      textBorderWidth: 1
    }
  }
  outside = {
    itemStyle: {
      color: isDark.value ? '#7B5E00' : '#FDC81A87'
    },
    label: {
      fontSize: 12,
      show: showOutsideName.value,
      color: isDark.value ? '#959595' : '#B5B5B5'
    }
  }
}

/**
 * 获取文章内容
 * @param onlyInner
 * @param articleId
 */
const getArticleRefList = (onlyInner: boolean) => {
  changeStyle()
  let param: any
  if (isNotNull(articleId) && isNotBlank(articleId)) {
    param = { onlyInner: onlyInner, articleId: articleId }
  } else {
    param = { onlyInner: onlyInner }
  }
  articleRefListApi(param).then((resp) => {
    nodes = resp.data.nodes.map((node: any) => {
      if (node.artType == 11) {
        node.itemStyle = inside.itemStyle
        node.label = inside.label
        stat.value.inside += 1
      } else if (node.artType === 12) {
        node.itemStyle = insideUnknown.itemStyle
        node.label = insideUnknown.label
      } else if (node.artType == 21) {
        node.itemStyle = outside.itemStyle
        node.label = outside.label
        stat.value.outside += 1
      }
      node.symbolSize = getLinkCount(node.name, resp.data.links)
      return node
    })
    links = resp.data.links
    renderChart()
  })
}

// 节点被链接数量的累计基数, 越大则节点 symbolSize 越大
const ascending = 1
/**
 * 统计节点被被链接的数量, 用于计算 symbolSize
 * @param name  节点名称
 * @param links 全部节点关系
 */
const getLinkCount = (name: string, links: any[]): number => {
  let count: number = 20
  for (let i = 0; i < links.length; i++) {
    if (count >= 100) {
      break
    }
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
      // position: [20, 50],
      triggerOn: 'click',
      enterable: true,
      alwaysShowContent: false,
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
          url = `<div>地址: <a target="_blank" href="${userStore.userinfo.userParams.WEB_ARTICLE_URL + params.data.artId}">${
            userStore.userinfo.userParams.WEB_ARTICLE_URL + params.data.artId
          }</a></div>`
        }
        let type = ''
        if (params.data.artType === 11) {
          type = `<div>类型: 内部文章</div>`
        } else if (params.data.artType === 12) {
          type = `<div style="color:${insideUnknown.itemStyle.color}">类型: 未知文章, 可能是文章ID错误或已被删除</div>`
        } else if (params.data.artType === 21) {
          type = `<div>类型: 外网文章</div>`
        }
        return `<div class="chart-graph-article-ref-tooltip" style="border:1px solid ${params.data.itemStyle.color}">
          <div class="title">${params.data.name}</div>
          <div class="content">
            ${type}
            ${url}
          </div>
          </div>`
      }
    },
    series: [
      {
        type: 'graph',
        layout: 'force',
        top: 100,
        bottom: 100,
        draggable: false,
        symbolSize: 15,
        animation: true,
        animationThreshold: 1000,
        animationDuration: 1,
        zoom: 0.5,
        roam: true,
        label: {
          show: true,
          formatter: (param: any) => {
            let len = param.name.length
            if (len < 20) {
              return param.name
            }
            return (param.name as string).substring(0, 15) + '...'
          }
        },
        labelLayout: {
          // 标签重叠时进行遮盖
          hideOverlap: true
        },
        // itemStyle: {
        //   shadowColor: '#000000',
        //   shadowBlur: 10,
        //   shadowOffsetX: 2,
        //   shadowOffsetY: 3
        // },
        // autoCurveness: true,
        lineStyle: {
          color: isDark.value ? '#5E5E5E' : '#B3B3B3',
          width: 2,
          // 直线或曲线
          curveness: 0.1
        },
        force: {
          layoutAnimation: true,
          repulsion: 500, // 节点之间的斥力因子。
          // 这个参数能减缓节点的移动速度. 取值范围 0 到 1, 越大越快, 值越大时, 节点之间会更加内聚, 否则会混在一起
          friction: 0.2,
          // 节点受到的向中心的引力因子. 该值越大, 所有节点越往中心点靠拢.
          gravity: 0.05
        },
        // 箭头的开始, 结束图形
        edgeSymbol: ['circle', 'arrow'],
        // 箭头的开始, 结束图形大小
        edgeSymbolSize: [0, 5],
        // edgeLabel: {
        //   show: false,
        // fontSize: 10,
        // width: 30,
        // overflow: 'truncate'
        // },
        emphasis: {
          // 聚焦关系图中的邻接点和边的图形。
          focus: 'adjacency',
          // blurScope: 'series',
          lineStyle: {
            width: 5
          }
          // label: { show: true },
          // edgeLabel: { show: false },
        },
        blur: {
          // itemStyle: { opacity: 0.1 },
          lineStyle: { opacity: 0.1 },
          label: { show: false },
          edgeLabel: { show: false }
        },
        data: nodes,
        links: links,
        categories:
          nodes.length > 0
            ? nodes.map((item: any) => {
                return item.name
              })
            : ''
      }
    ]
  })
}

const init = () => {
  chartGraph = echarts.init(ChartGraphRef.value)
}

/**
 * 防抖
 */
let debounceTimeout: NodeJS.Timeout | undefined
function debounce(fn: () => void, time = 500) {
  if (debounceTimeout != undefined) {
    clearTimeout(debounceTimeout)
  }
  debounceTimeout = setTimeout(fn, time)
}

const windowResize = () => {
  debounce(() => {
    chartGraph.resize()
  }, 300)
}

onMounted(() => {
  document.title = 'Blossom 双链图表'
  init()
  windowResize()
  articleId = route.query.articleId as string
  getArticleRefList(true)
  window.addEventListener('resize', windowResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', windowResize)
})
</script>

<style scoped lang="scss">
.header {
  @include box(100%, 30px);
}
.article-reference-root {
  @include box(100%, calc(100% - 30px));
  position: relative;

  .setting {
    @include absolute(0, '', '', 20px);
    border: 1px solid var(--el-border-color);
    padding: 10px;
    border-radius: 10px;

    .title {
      color: var(--el-color-primary);
      text-shadow: var(--bl-text-shadow);
      font-weight: bold;
      height: 40px;
    }

    .symbol {
      font-size: 12px;
      color: var(--el-color-primary);
      margin-bottom: 10px;
      margin-right: 10px;
    }

    .inside,
    .outside {
      @include box(20px, 20px);
      box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.441);
      border-radius: 50%;
      margin-bottom: 10px;
    }

    .inside {
      background-color: var(--el-color-primary);
    }

    .outside {
      background-color: #fdc81a;
    }
  }

  .desc {
    @include font(12px, 300);
    @include absolute('', '', 20px, 20px);
    color: var(--bl-text-color-light);
    border: 1px dashed var(--bl-text-color-light);
    border-radius: 5px;
    padding: 10px;

    ol {
      margin: 0;
      padding-left: 30px;
    }
  }

  .setting,
  .desc {
    backdrop-filter: blur(4px);
    z-index: 99;
  }

  .app-relation-graph-chart {
    @include box(100%, 100%);
  }
}
</style>

<style lang="scss">
.chart-graph-article-ref-tooltip {
  max-width: 400px;
  word-break: break-all;
  white-space: normal;
  background-color: var(--bl-html-color);
  border-radius: 4px;
  color: var(--bl-text-color);

  .title {
    @include font(15px, 500);
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
