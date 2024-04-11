<template>
  <div class="table-resource-root">
    <div class="table-container">
      <div class="table-operator">
        <bl-row class="component-title">资源列表</bl-row>
        <bl-row style="margin-top: 10px">
          <el-input v-model="dataSearch" placeholder="搜索资源名" />
          <el-button type="primary" :icon="Refresh" @click="getResource()" style="margin-left: 10px"></el-button>
        </bl-row>
      </div>
      <div class="table">
        <el-table border height="100%" tooltip-effect="dark" v-loading="tableLoading" :data="filterTableDatas" @row-click="rowClick">
          <el-table-column sortable prop="resource" min-width="170" :show-overflow-tooltip="true" label="资源名称(24小时内)" />
          <el-table-column sortable prop="success" width="80" align="right" label="请求数" />
          <el-table-column sortable prop="avgRt" width="90" align="right" label="avgRT">
            <template #default="scope">
              <el-tag class="fail-border fail-bg" effect="dark" v-if="scope.row.avgRt > 1000">
                {{ scope.row.avgRt }} <span v-if="scope.row.avgRt > 1000" style="font-weight: 700"></span>
              </el-tag>
              <span v-else>{{ scope.row.avgRt }}</span>
            </template>
          </el-table-column>
          <el-table-column sortable prop="maxRt" width="80" align="right" label="maxRT">
            <template #default="scope">
              <el-tag class="fail-border fail-bg" effect="dark" v-if="scope.row.maxRt > 1000">
                {{ scope.row.maxRt }} <span v-if="scope.row.maxRt > 1000" style="font-weight: 700"></span>
              </el-tag>
              <span v-else>{{ scope.row.maxRt }}</span>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <div class="chart-line">
      <bl-row class="component-title" just="space-between">
        <div class="title">
          流量折线图：
          <span style="font-size: 12px"
            >查询最近{{ lineSearchParam.interval }}的流量信息，按每{{ lineSearchParam.customInterval }}分钟聚合，资源:
            {{ lineSearchParam.resource }}</span
          >
        </div>
        <el-button-group style="width: 200px">
          <el-button round @click="intervalClick('5m', 1)">5M</el-button>
          <el-button round @click="intervalClick('10m', 1)">10M</el-button>
          <el-button round @click="intervalClick('1h', 1)">1H</el-button>
          <el-button round @click="intervalClick('6h', 1)">6H</el-button>
          <el-button round @click="intervalClick('1d', 15)">1D</el-button>
        </el-button-group>
      </bl-row>
      <SentinelChartLine ref="SentinelChartLineRef" :show-title="true"></SentinelChartLine>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import { resourcesApi } from '@renderer/api/sentinel'
import { isNull } from '@renderer/assets/utils/obj'
import SentinelChartLine from './SentinelChartLine.vue'

onMounted(() => {
  getResource()
})

// ----------------------------------------< 资源列表 >----------------------------------------
interface ResourceRow {
  resource: string
  success: number
  avgRt: number
  maxRt: number
  minRt: number
}
const SentinelChartLineRef = ref()
const tableLoading = ref(false)
const tableDatas = ref<ResourceRow[]>([])
const dataSearch = ref('')
const lineSearchParam = ref({
  resource: '',
  interval: '1d',
  customInterval: 30
})

/**
 * 过滤数据
 */
const filterTableDatas = computed<ResourceRow[]>(() => {
  return tableDatas.value.filter((metric: any) => {
    if (isNull(dataSearch.value)) {
      return true
    }
    return metric.resource.toLowerCase().includes(dataSearch.value.toLowerCase())
  })
})

const getResource = () => {
  resourcesApi({ interval: '1d' }).then((resp) => {
    tableDatas.value = resp.data
    lineSearch()
  })
}

const lineSearch = () => {
  SentinelChartLineRef.value.reload(lineSearchParam.value.resource, lineSearchParam.value.interval, lineSearchParam.value.customInterval)
  SentinelChartLineRef.value.windowResize()
}

const intervalClick = (interval: string = '1d', customInterval: number) => {
  lineSearchParam.value.interval = interval
  lineSearchParam.value.customInterval = customInterval
  lineSearch()
}

const rowClick = (row: ResourceRow) => {
  lineSearchParam.value.resource = row.resource
  lineSearch()
}
</script>

<style scoped lang="scss">
.table-resource-root {
  @include box(100%, 100%);
  @include flex(row, flex-start, flex-start);
  padding: 0 30px 30px 0;

  .component-title {
    width: 100%;
    color: var(--bl-text-color);
    border: 1px solid var(--el-border-color);
    background-color: var(--bl-bg-color);
    border-radius: 4px;
    padding: 5px 10px;
    height: 36px;

    .title {
      width: calc(100% - 200px);
      overflow: hidden;
      @include ellipsis();
    }
  }

  .table-container {
    @include box(420px, 100%);

    .table-operator {
      @include box(100%, 80px);
      @include flex(column, flex-start, flex-start);
    }

    .table {
      @include box(100%, calc(100% - 80px));

      :deep(.el-tag--small) {
        padding: 0 3px;
      }
    }

    .to-high {
      @include themeColor(#e35959, #811111);
      border: 1px solid red;

      &:before {
        content: '↑';
        font-size: 15px;
      }
    }
  }

  .chart-line {
    @include box(calc(100% - 420px), 100%);
    max-height: 500px;
    margin-left: 15px;
  }
}
</style>
