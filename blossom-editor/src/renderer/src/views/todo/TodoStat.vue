<template>
  <div class="todo-stat-root">
    <div class="stat-global">
      <div class="title">近30日完成率%</div>
      <div class="sub-title">Last 30 days completion rate</div>
      <div class="chart-line-completed">
        <TodoChartCompleted ref="TodoChartCompletedRef"></TodoChartCompleted>
      </div>

      <div class="title" style="margin-top: 20px;">所有任务</div>
      <div class="sub-title">All Tasks Statistic</div>

      <bl-row class="item-row" just="space-around">
        <div class="stat-item waiting">
          <div class="label">未开始</div>
          <div class="value">{{ taskStat.waiting }}</div>
          <span class="icon-shadow iconbl bl-flag-line"></span>
        </div>
        <div class="stat-item progress">
          <div class="label">进行中</div>
          <div class="value">{{ taskStat.processing }}</div>
          <span class="icon-shadow iconbl bl-a-flowchart1-line"></span>
        </div>
      </bl-row>

      <bl-row class="item-row" just="space-around">
        <div class="stat-item completed">
          <div class="label">完成数</div>
          <div class="value">{{ taskStat.completed }}</div>
          <span class="icon-shadow iconbl bl-a-boxchoice-line"></span>
        </div>
        <div class="stat-item total">
          <div class="label">任务总数</div>
          <div class="value">{{ taskStat.total }}</div>
          <span class="icon-shadow iconbl bl-a-boxdownload-line"></span>
        </div>
      </bl-row>

    </div>

    <div class="phared-details">
      <div class="title">{{ todoStat.todoName }}</div>
      <div class="sub-title">Phased Planning</div>
      <div class="row">创建日期: {{ todoStat.firstCreTime }}</div>
      <div class="row">最早开始: {{ todoStat.firstStartTime }}</div>
      <div class="row">最后完成: {{ todoStat.lastEndTime }}</div>

      <bl-row v-if="todoStat.todoType == 20" class="row" just="space-between" style="margin-top: 10px;">
        <el-button v-if="todoStat.todoStatus == 2" text bg @click="openTodo">重启事项</el-button>
        <el-button v-if="todoStat.todoStatus == 1" type="success" text bg @click="completedTodo">完成事项</el-button>
      </bl-row>
    </div>

  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { todoStatApi, taskStatApi, completedTodoApi, openTodoApi } from '@renderer/api/todo'
import TodoChartCompleted from './TodoChartCompleted.vue'

const TodoChartCompletedRef = ref()
const taskStat = ref({
  waiting: 0,
  processing: 0,
  completed: 0,
  total: 0,
})

onMounted(() => {
  taskStatApi().then(resp => {
    taskStat.value = {
      waiting: resp.data.waiting,
      processing: resp.data.processing,
      completed: resp.data.completed,
      total: resp.data.total
    }
    TodoChartCompletedRef.value.reload(resp.data.dates, resp.data.rates)
  })
})

const openTodo = () => {
  openTodoApi({ todoId: todoStat.value.todoId }).then(_resp => {
    emits('refreshTodo')
  })
}

const completedTodo = () => {
  completedTodoApi({ todoId: todoStat.value.todoId }).then(_resp => {
    emits('refreshTodo')
  })
}

const todoStat = ref({
  todoId: '',
  todoName: '',
  todoType: 10,
  todoStatus: 1,
  firstCreTime: '',
  firstStartTime: '',
  lastEndTime: ''
})

const reload = (todoId: string) => {
  todoStatApi({ todoId: todoId }).then(resp => {
    todoStat.value = resp.data
  })
}

defineExpose({ reload })
const emits = defineEmits(['refreshTodo'])
</script>

<style scoped lang="scss">
.todo-stat-root {
  @include box(100%, 100%);
  padding: 10px 15px 20px 15px;

  .stat-global,
  .phared-details {
    @include themeShadow(0 0 5px #D7D7D7, 0 0 5px #000000);
    @include themeBg(#FAFAFA, #0F0F0F);
    border-radius: 6px;
    margin-bottom: 20px;
    padding: 10px 0;

    .title {
      @include font(17px, 300);
      padding: 5px 10px;
      color: var(--bl-text-color);
    }

    .sub-title {
      @include font(12px, 300);
      padding: 0 15px;
      color: var(--bl-text-color-light);
    }

  }

  .stat-global {
    @include themeShadow(0 0 5px #D7D7D7, 0 0 5px #000000);
    @include themeBg(#FAFAFA, #0F0F0F);
    border-radius: 6px;
    margin-bottom: 20px;

    .chart-line-completed {
      @include box(100%, 230px);
    }

    .item-row {
      padding: 10px;

      &>div:first-child {
        margin-left: 10px;
      }

      &>div:last-child {
        margin-right: 10px;
      }

      .stat-item {
        width: 150px;
        color: var(--bl-text-color);
        border-radius: 6px;
        padding: 10px 15px;
        position: relative;
        overflow: hidden;

        &:hover {
          .icon-shadow {
            transform: scale(1.4);
          }
        }

        .label {
          @include font(17px, 300);
          padding-bottom: 10px;
        }

        .value {
          @include font(24px, 500);
          position: relative;
          padding-left: 15px;
          line-height: 20px;

          &::before {
            content: "";
            height: 100%;
            width: 6px;
            display: block;
            background-color: #000000;
            position: absolute;
            left: -0px;
            border-radius: 4px;
            filter: blur(1px);
          }
        }

        .icon-shadow {
          @include font(60px, 500);
          position: absolute;
          top: 5px;
          right: 5px;
          z-index: 1;
          filter: blur(3px);
          transition: all 0.3s;
        }
      }


      .stat-item.waiting {
        background-color: #FE63702D;

        .value {
          &::before {
            background-color: #FF626F;
          }
        }

        .icon-shadow {
          @include themeColor(#FF8590, #BC454F);
        }
      }

      .stat-item.progress {
        background-color: #FBA85F2D;

        .value {
          &::before {
            background-color: #FBA85F;
          }
        }

        .icon-shadow {
          @include themeColor(#F9AC69, #BA804E);
        }
      }

      .stat-item.completed {
        background-color: #9974FE2D;

        .value {
          &::before {
            background-color: #8E65FF;
          }
        }

        .icon-shadow {
          @include themeColor(#9777EE, #7251CC);
        }
      }

      .stat-item.total {
        background-color: #6289FF2D;

        .value {
          &::before {
            background-color: #7896EE;
          }
        }

        .icon-shadow {
          @include themeColor(#7896EE, #3C5AB3);
        }
      }


    }

  }

  .phared-details {

    .row {
      @include font(12px, 300);
      padding: 5px 15px 0 15px;
      color: var(--bl-text-color);
    }
  }
}
</style>
