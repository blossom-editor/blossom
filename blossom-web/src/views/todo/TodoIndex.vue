<template>
  <div class="todo-root">
    <div class="header">
      <IndexHeader :bg="true"></IndexHeader>
    </div>

    <bl-row just="space-between" class="workbench" height="45px">
      <el-radio-group v-model="todoType" size="small">
        <el-radio-button :label="10">每日</el-radio-button>
        <el-radio-button :label="20">阶段性</el-radio-button>
      </el-radio-group>
      <div class="month" v-show="todoType === 10">{{ calendarDate.getMonth() + 1 }}月</div>
      <el-button-group v-show="todoType === 10" size="small">
        <el-button @click="selectDate('prev-month')">上月</el-button>
        <el-button @click="selectDate('today')">今日</el-button>
        <el-button @click="selectDate('next-month')">下月</el-button>
      </el-button-group>
    </bl-row>

    <!-- 日历 -->
    <el-calendar v-show="todoType === 10" v-model="calendarDate" class="task-day-calendar" ref="CalendarRef">
      <template #header="{ date }"><div></div></template>
      <template #date-cell="{ data }">
        <bl-col just="space-around" class="cell-wrapper" @click="toTask(data.day)">
          <div class="day">{{ data.day.split('-')[2] }}</div>
          <div v-if="getCount(data.day) > 0">
            <bl-tag :size="14">{{ getCount(data.day) }}</bl-tag>
          </div>
        </bl-col>
      </template>
    </el-calendar>

    <div v-show="todoType === 20" class="phaseds">
      <bl-row v-for="phased in todoPhased" class="phased" just="space-between" width="90%" @click="toTask(phased.todoId)">
        <span style="padding-left: 5px">{{ phased.todoName }}</span>
        <bl-tag v-if="phased.taskCount > 0">{{ phased.taskCount }}</bl-tag>
      </bl-row>
    </div>

    <div class="segmented-control" @change="updatePillPosition">
      <span class="selection"></span>
      <div class="option">
        <input type="radio" id="WAITING" name="sample" value="WAITING" v-model="taskShowStatus" checked />
        <label for="WAITING"><span>待 办</span></label>
        <span v-show="countWait > 0" class="count">{{ countWait }}</span>
      </div>
      <div class="option">
        <input type="radio" id="PROCESSING" name="sample" value="PROCESSING" v-model="taskShowStatus" />
        <label for="PROCESSING"><span>进行中</span></label>
        <span v-show="countProc > 0" class="count">{{ countProc }}</span>
      </div>
      <div class="option">
        <input type="radio" id="COMPLETED" name="sample" value="COMPLETED" v-model="taskShowStatus" />
        <label for="COMPLETED"><span>完 成</span></label>
        <span v-show="countComp > 0" class="count">{{ countComp }}</span>
      </div>
    </div>

    <!--  -->
    <bl-col class="tasks" just="flex-start" align="center">
      <div v-for="task in taskShow" class="task" @click="showDetail(task)">
        <bl-row just="space-between">
          <div class="title">{{ task.taskName }}</div>
          <div class="title">{{ task.deadLine }}</div>
        </bl-row>
        <div class="content">{{ task.taskContent }}</div>
        <div class="cornor" :style="{ borderTop: `20px solid ${isBlank(task.color) ? '#E5E5E5' : task.color}` }"></div>
      </div>
    </bl-col>
  </div>

  <!--  -->
  <el-drawer v-model="isShowDetail" direction="btt" :with-header="true" size="345px">
    <template #header="{ close, titleId, titleClass }">
      <div class="drawer-header">
        <el-input style="--el-input-border-color: #ffffff00" v-model="curTask.taskName"></el-input>
      </div>
    </template>
    <div class="detail">
      <el-input type="textarea" placeholder="无内容" v-model="curTask.taskContent" resize="none" :rows="4"></el-input>
      <bl-row class="tags">
        <bl-tag v-for="tag in curTask.taskTags" :bg-color="isBlank(curTask.color) ? '#000' : curTask.color" :size="13" style="min-height: 20px">
          {{ tag }}
        </bl-tag>
      </bl-row>
      <bl-col class="times">
        <bl-row style="color: red">截止至：{{ curTask.deadLine }}</bl-row>
        <!-- <bl-row>创建于：{{ curTask.creTime }} </bl-row>
        <bl-row>开始于：{{ curTask.startTime }} </bl-row>
        <bl-row>完成于：{{ curTask.endTime }} </bl-row> -->
      </bl-col>
      <bl-row>
        <el-slider v-model="curTask.process" :show-tooltip="false" size="small" />
        <div class="process">{{ curTask.process }}%</div>
      </bl-row>
      <bl-row class="btns" just="space-between">
        <el-button-group>
          <el-button @click="updTask"><i class="iconbl bl-a-texteditorsave-line"></i></el-button>
          <el-button @click="updTask">123</el-button>
        </el-button-group>
        <el-button-group>
          <el-button color="#000" v-if="curTask.taskStatus === 'WAITING'" @click="toProcessing">
            <span class="iconbl bl-a-boxaddition-line"></span>开 始
          </el-button>
          <el-button color="#000" v-if="curTask.taskStatus === 'WAITING' || curTask.taskStatus === 'PROCESSING'" @click="toCompleted">
            <span class="iconbl bl-a-boxchoice-line"></span>完 成
          </el-button>
        </el-button-group>
      </bl-row>
      <div class="status">
        {{ curTask.taskStatus }}
      </div>
    </div>
  </el-drawer>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import type { CalendarDateType, CalendarInstance } from 'element-plus'
import { tasksApi, todosApi, toProcessingApi, toCompletedApi, updTaskApi } from '@/api/todo'
import type { TodoList, TaskInfo, TodoType, TaskStatus } from './scripts/types'
import { isBlank } from '@/assets/utils/obj'
import IndexHeader from '@/views/index/IndexHeader.vue'

onMounted(() => {
  getTodos()
})

//#region ----------------------------------------< 待办事项 >--------------------------------------

const calendarDate = ref<Date>(new Date())
const CalendarRef = ref<CalendarInstance>()
const selectDate = (val: CalendarDateType) => {
  if (!CalendarRef.value) return
  CalendarRef.value.selectDate(val)
}

const todoType = ref<TodoType>(10)
const todoDayMaps = ref<Map<string, TodoList>>(new Map())
const todoPhased = ref<TodoList[]>([])
const getTodos = () => {
  todosApi().then((resp) => {
    todoDayMaps.value.clear()
    for (let key in resp.data.todoDays) {
      let todo = resp.data.todoDays[key] as TodoList
      todoDayMaps.value.set(key, {
        todoId: todo.todoId,
        todoName: todo.todoName,
        todoStatus: 1,
        todoType: 10,
        today: false,
        taskCount: todo.taskCount > 0 ? todo.taskCount : 0,
        updTodoName: false
      })
    }
    todoPhased.value = resp.data.taskPhased
  })
}

const getCount = (day: string): number => {
  if (!todoDayMaps.value) return 0
  if (!todoDayMaps.value.get(day)) return 0
  return todoDayMaps.value.get(day)!.taskCount
}

//#endregion

//#region ----------------------------------------< 任务 >--------------------------------------
// 当前待办事项
const curTask = ref<TaskInfo>({
  id: '',
  todoId: '',
  todoName: '',
  todoType: 10,
  taskName: '',
  taskContent: 'content 张三李四',
  taskTags: ['张三', '李四', 'Article'],
  deadLine: '今天下午两点30分',
  creTime: '2023-01-01 12:21:32',
  startTime: '2023-01-01 12:21:32',
  endTime: '2023-01-01 12:21:32',
  process: 0,
  color: '#000',
  taskStatus: 'WAITING',
  updTaskName: false,
  updTaskContent: false
})
const taskWait = ref<TaskInfo[]>([])
const taskProc = ref<TaskInfo[]>([])
const taskComp = ref<TaskInfo[]>([])
const countWait = computed<number>(() => taskWait.value.length)
const countProc = computed<number>(() => taskProc.value.filter((t: { todoType: number }) => t.todoType != 99).length)
const countComp = computed<number>(() => taskComp.value.filter((t: { todoType: number }) => t.todoType != 99).length)
const taskShowStatus = ref<TaskStatus>('WAITING')
const taskShow = computed(() => {
  if (taskShowStatus.value === 'WAITING') {
    return taskWait.value
  }
  if (taskShowStatus.value === 'PROCESSING') {
    return taskProc.value.filter((t: { todoType: number }) => t.todoType != 99)
  }
  if (taskShowStatus.value === 'COMPLETED') {
    return taskComp.value.filter((t: { todoType: number }) => t.todoType != 99)
  }
})

const updatePillPosition = () => {
  Array.from(document.querySelectorAll('.segmented-control .option input')).forEach((elem: Element, index) => {
    let input = elem as HTMLInputElement
    if (input.checked) {
      let selection = document.querySelector('.segmented-control .selection')
      if (selection) {
        ;(selection as HTMLElement).style.transform = 'translateX(' + input.offsetWidth * index + 'px)'
      }
    }
  })
}

/**
 * 查看指定待办事项
 * @param todoId 待办事项ID
 * @param todoName 待办事项名称
 * @param todoType 待办事项类型
 */
const toTask = (todoId: string) => {
  tasksApi({ todoId: todoId }).then((resp) => {
    taskWait.value = resp.data.waiting
    taskProc.value = resp.data.processing
    taskComp.value = resp.data.completed
  })
}

const savedCallback = (data: any) => {
  taskWait.value = data.waiting
  taskProc.value = data.processing
  taskComp.value = data.completed
}

const toProcessing = () => {
  toProcessingApi({ id: curTask.value.id!, todoId: curTask.value.todoId }).then((resp) => {
    ElMessage('任务已开始')
    savedCallback(resp.data)
    isShowDetail.value = false
  })
}

const toCompleted = () => {
  toCompletedApi({ id: curTask.value.id!, todoId: curTask.value.todoId }).then((resp) => {
    ElMessage('任务已完成')
    savedCallback(resp.data)
    isShowDetail.value = false
  })
}

const updTask = () => {
  updTaskApi(curTask.value).then((resp) => {
    ElMessage('修改成功')
    isShowDetail.value = false
  })
}

const isShowDetail = ref(false)
const showDetail = (task: TaskInfo) => {
  isShowDetail.value = true
  curTask.value = task
}

//#region 类型
</script>

<style scoped lang="scss">
.todo-root {
  @include box(100%, 100%);
  @include flex(column, flex-start, center);
  overflow: hidden;
  --primary-light: #8abdff;
  --primary: #6d5dfc;
  --primary-dark: #5b0eeb;

  --white: #ffffff;
  --greyLight-1: #e4ebf5;
  --greyLight-2: #c8d0e7;
  --greyLight-3: #bec8e4;
  --greyDark: #9baacf;

  .header {
    @include box(100%, 60px);
  }

  .workbench,
  .task-day-calendar,
  .types,
  .tasks,
  .phaseds {
    max-width: 900px;
  }

  .workbench {
    padding: 10px 10px;

    :deep(.el-button, .el-radio-button__inner) {
      padding: 8px 10px;
    }
  }

  .task-day-calendar {
    min-height: 230px;
    background-color: transparent;
    --el-calendar-cell-width: 40px;

    :deep(.el-calendar__header) {
      padding: 0;
    }

    :deep(.el-calendar__body) {
      padding: 0;
      th {
        font-size: 13px;
        padding: 3px 0;
      }

      .el-calendar-table {
        tr:first-child td {
          border-left: none;
        }

        td:first-child {
          border-left: none;
        }
      }

      .el-calendar-table__row {
        .prev,
        .current,
        .next {
          .el-calendar-day {
            padding: 0;
            .cell-wrapper {
              @include box(100%, 100%);
              .day {
                @include font(14px, 300);
                text-align: center;
              }
              .tag-root {
                font-size: 14px;
              }
            }
          }
          &:last-child {
            border-right: none;
          }
        }
      }
    }
  }

  .phaseds {
    @include box(100%, 230px);
    @include flex(row, center, flex-start);
    align-content: flex-start;
    flex-wrap: wrap;
    padding-left: 10px;
    overflow: scroll;
    .phased {
      line-height: 33px;
      font-size: 14px;
      margin: 0 10px 10px 0;
      border-radius: 4px;
      padding: 3px 5px;
      background-color: #eeeeee;
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
      transition: background-color 0.3s;

      &:active {
        background-color: #d8d8d8;
      }
    }
  }

  .segmented-control {
    --background: rgba(239, 239, 240, 1);
    background: var(--background);
    border-radius: 5px;
    margin-top: 10px;
    padding: 2px;
    border: none;
    outline: none;
    display: grid;
    grid-auto-flow: column;
    grid-auto-columns: 1fr;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;

    .selection {
      background: rgba(255, 255, 255, 1);
      border: 0.5px solid rgba(0, 0, 0, 0.04);
      box-shadow: 0 3px 8px 0 rgba(0, 0, 0, 0.12), 0 3px 1px 0 rgba(0, 0, 0, 0.04);
      border-radius: 5px;
      grid-column: 1;
      grid-row: 1;
      z-index: 2;
      will-change: transform;
      -webkit-transition: transform 0.2s ease;
      transition: transform 0.2s ease;
    }

    .option {
      position: relative;
      cursor: pointer;

      &:first-of-type {
        grid-column: 1;
        grid-row: 1;
        box-shadow: none;

        label::before {
          opacity: 0;
        }
      }

      input {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        width: 100%;
        height: 100%;
        padding: 0;
        margin: 0;
        -webkit-appearance: none;
        -moz-appearance: none;
        appearance: none;
        outline: none;
        border: none;
        opacity: 0;

        &:checked + label::before,
        &:checked + label::after {
          z-index: 1;
        }

        &::checked + label {
          cursor: default;
        }
      }

      label {
        position: relative;
        display: block;
        text-align: center;
        padding: 3px 20px;
        background: rgba(255, 255, 255, 0);
        font-weight: 500;
        color: rgb(105, 105, 105);
        font-size: 14px;
        transition: transform 0.3s;
        cursor: pointer;

        &:active {
          color: #000;
        }

        &::after {
          right: 0;
          transform: translateX(0.5px);
        }

        span {
          display: block;
          position: relative;
          z-index: 2;
          -webkit-transition: all 0.2s ease;
          transition: all 0.2s ease;
          will-change: transform;
          padding: 5px;
          line-height: 18px;
          pointer-events: none;
        }
      }

      .count {
        font-size: 12px;
        font-style: italic;
        color: #bebebe;
        position: absolute;
        top: 1px;
        right: 5px;
        z-index: 3;
        text-align: right;
      }
    }
  }

  .tasks {
    width: 95%;
    max-width: 650px;
    padding: 0 10px 10px 10px;
    margin-top: 10px;
    border-radius: 10px;
    overflow-y: auto;
    flex: 1;

    .task {
      width: 100%;
      font-size: 14px;
      padding: 10px;
      margin-bottom: 10px;
      border-radius: 4px;
      background-color: #f3f3f3;
      position: relative;
      transition: background-color 0.3s;

      &:active {
        background-color: #d8d8d8;
      }

      .title {
        width: 70%;
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
        &:last-child {
          width: 30%;
          text-align: right;
        }
      }

      .content {
        font-size: 0.8rem;
        margin-top: 3px;
        color: #a3a3a3;
        word-break: break-all;
        overflow: hidden;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
      }

      .cornor {
        // @include box(20px, 20px);
        // transform: rotate(45deg);
        // border-top-left-radius: 10px;

        position: absolute;
        top: 0;
        right: 0;

        border-left: 20px solid transparent;
        border-top-right-radius: 4px;
      }
    }
  }
}

.drawer-header {
  --el-font-size-base: 15px;
}

.detail {
  @include flex(column, space-between, flex-start);
  --el-font-size-base: 15px;
  .el-textarea {
    --el-input-border-color: #ffffff00 !important;
  }

  .tags {
    height: 44px;
    padding: 10px 0 3px 0;
    overflow: scroll;
  }

  .times {
    margin-top: 10px;
    font-size: 13px;
    .bl-row-root {
      margin-bottom: 5px;
      white-space: nowrap;
    }
  }

  .process {
    width: 50px;
    font-style: italic;
    color: #bebebe;
    text-align: right;
  }

  .btns {
    margin-top: 10px;

    .iconbl {
      margin-right: 5px;
    }
  }
  .status {
    font-size: 12px;
    font-style: italic;
    color: #bebebe;
    position: absolute;
    right: 30px;
    bottom: 10px;
  }
}
</style>
