<template>
  <div class="todo-stat-root">
    <div class="stat-global">
      <div class="title">近30日完成率% <span class="iconbl bl-refresh-smile" style="cursor: pointer" @click="getTaskStat"></span></div>
      <div class="sub-title">Last 30 days completion rate</div>
      <div class="chart-line-completed">
        <TodoChartCompleted ref="TodoChartCompletedRef"></TodoChartCompleted>
      </div>

      <div class="title" style="margin-top: 20px">所有任务</div>
      <div class="sub-title">All Tasks Statistic</div>

      <bl-row class="item-row" just="space-around">
        <div class="stat-item waiting">
          <div class="label">未开始</div>
          <div class="value">{{ taskStat.waiting }}</div>
          <span class="icon-shadow iconbl bl-a-boxsubtract-line"></span>
        </div>
        <div class="stat-item progress">
          <div class="label">进行中</div>
          <div class="value">{{ taskStat.processing }}</div>
          <span class="icon-shadow iconbl bl-a-boxaddition-line"></span>
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

      <bl-row v-if="todoStat.todoType == 20" class="row" just="space-between" style="margin-top: 10px">
        <el-button v-if="todoStat.todoStatus == 2" text bg @click="openTodo">重启事项</el-button>
        <el-button v-if="todoStat.todoStatus == 1" type="success" text bg @click="completedTodo">完成事项</el-button>
      </bl-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { todoStatApi, taskStatApi, completedTodoApi, openTodoApi } from '@renderer/api/todo'
import TodoChartCompleted from './TodoChartCompleted.vue'
import { useLifecycle } from '@renderer/scripts/lifecycle'

useLifecycle(
  () => getTaskStat(),
  () => getTaskStat()
)

const getTaskStat = () => {
  taskStatApi().then((resp) => {
    taskStat.value = {
      waiting: resp.data.waiting,
      processing: resp.data.processing,
      completed: resp.data.completed,
      total: resp.data.total
    }
    TodoChartCompletedRef.value.reload(resp.data.dates, resp.data.rates)
  })
}

const TodoChartCompletedRef = ref()
const taskStat = ref({
  waiting: 0,
  processing: 0,
  completed: 0,
  total: 0
})

const openTodo = () => {
  openTodoApi({ todoId: todoStat.value.todoId }).then((_resp) => {
    emits('refreshTodo')
  })
}

const completedTodo = () => {
  completedTodoApi({ todoId: todoStat.value.todoId }).then((_resp) => {
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
  todoStatApi({ todoId: todoId }).then((resp) => {
    todoStat.value = resp.data
  })
}

defineExpose({ reload })
const emits = defineEmits(['refreshTodo'])
</script>

<style scoped lang="scss">
@import './styles/todo-stat.scss';
</style>
