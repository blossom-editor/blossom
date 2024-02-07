<template>
  <div class="todo-root">
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
        <bl-col just="flex-start" class="cell-wrapper" @click="toTask(data.day)">
          <div class="day">{{ data.day.split('-')[2] }}</div>
          <bl-row just="flex-end" class="count" v-if="getCount(data.day) > 0">
            <bl-tag :size="14">{{ getCount(data.day) }}</bl-tag>
          </bl-row>
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
      <div v-for="task in taskShow" class="task" @click="showDetail('upd', task)">
        <bl-row
          class="process"
          height="3px"
          :width="Math.max(task.process, 0) + '%'"
          :style="{ backgroundColor: `${isBlank(task.color) ? '#E5E5E5' : task.color}`, borderTopRightRadius: task.process >= 100 ? '4px' : '' }">
        </bl-row>
        <bl-row style="padding: 0 10px" just="space-between">
          <div class="title">{{ task.taskName }}</div>
          <div class="title">{{ task.deadLine }}</div>
        </bl-row>
        <div class="content">{{ task.taskContent }}</div>
      </div>
    </bl-col>
    <!--  -->
    <div class="module-workbench" @click="showDetail('add')"><span class="iconbl bl-a-addline-line"></span></div>
  </div>

  <!--  -->
  <el-drawer v-model="isShowDetail" class="center-drawer" direction="btt" :with-header="true" size="400px">
    <div class="detail">
      <el-input class="task-name" size="small" style="width: calc(100% - 30px)" v-model="curTask.taskName" placeholder="Todo 标题">
        <template #prefix>
          <el-icon size="15">
            <Document />
          </el-icon>
        </template>
      </el-input>
      <el-input size="small" type="textarea" placeholder="Todo 内容" v-model="curTask.taskContent" resize="none" :rows="4"></el-input>
      <!--  -->
      <bl-row class="times">
        <div class="label">截止：</div>
        <el-input size="small" style="width: calc(100% - 50px)" v-model="curTask.deadLine" placeholder="..."></el-input>
      </bl-row>
      <!--  -->
      <bl-row class="colors">
        <div class="label" :style="{ color: curTask.color }">颜色：</div>
        <div
          v-for="color in colors"
          class="color"
          :key="color"
          :style="{ backgroundColor: color }"
          @click="
            () => {
              curTask.color = color
            }
          "></div>
      </bl-row>
      <!--  -->
      <bl-row class="tags">
        <el-button @click="showTagDialog" size="small"> + 标签 </el-button>
        <el-tag
          v-for="tag in curTask.taskTags"
          :key="tag"
          :disable-transitions="false"
          :color="isBlank(curTask.color) ? '#474747' : curTask.color"
          @close="handleTagClose(tag)"
          closable>
          {{ tag }}
        </el-tag>
      </bl-row>
      <!--  -->
      <bl-row>
        <el-slider
          v-model="curTask.process"
          :show-tooltip="false"
          size="small"
          :style="{ '--el-slider-main-bg-color': isBlank(curTask.color) ? '#474747' : curTask.color }" />
        <div class="process">{{ curTask.process }}%</div>
      </bl-row>
      <bl-row class="btns" just="space-between">
        <el-button-group>
          <el-button v-if="detailType === 'upd'" color="#474747" @click="delTask"><i class="iconbl bl-delete-line"></i></el-button>
          <el-button v-if="detailType === 'upd'" color="#474747" @click="updTask"><i class="iconbl bl-a-texteditorsave-line"></i></el-button>
        </el-button-group>
        <el-button v-if="detailType === 'add'" color="#474747" style="width: 100%" @click="addTask">保 存</el-button>
        <el-button-group>
          <el-button v-if="detailType === 'upd' && curTask.taskStatus !== 'WAITING'" color="#E2EBD0" @click="toWaiting">
            <span class="iconbl bl-a-boxsubtract-line"></span>待 办
          </el-button>
          <el-button v-if="detailType === 'upd' && curTask.taskStatus !== 'PROCESSING'" color="#E2EBD0" @click="toProcessing">
            <span class="iconbl bl-a-boxaddition-line"></span>开 始
          </el-button>
          <el-button v-if="detailType === 'upd' && curTask.taskStatus !== 'COMPLETED'" color="#E2EBD0" @click="toCompleted">
            <span class="iconbl bl-a-boxchoice-line"></span>完 成
          </el-button>
        </el-button-group>
      </bl-row>
      <div class="status">
        {{ taskStatus }}
      </div>
    </div>
  </el-drawer>

  <el-dialog v-model="isShowTagDialog" :append-to-body="true" :destroy-on-close="true" :close-on-click-modal="false" title="添加标签">
    <el-input v-model="tagInput" placeholder="标签内容..."></el-input>
    <el-button style="width: 100%; margin-top: 10px" color="#474747" @click="addTag">添加</el-button>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { Document } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { CalendarDateType, CalendarInstance } from 'element-plus'
import { tasksApi, todosApi, toWaitingApi, toProcessingApi, toCompletedApi, addTaskApi, updTaskApi, delTaskApi } from '@/api/todo'
import type { TodoList, TaskInfo, TodoType, TaskStatus } from './scripts/types'
import { isBlank } from '@/assets/utils/obj'

onMounted(() => {
  getTodos()
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

const colors = ref([
  'rgba(68, 173, 56, 0.7)',
  'rgba(186, 196, 44, 0.7)',
  'rgba(235, 205, 72, 0.7)',
  'rgba(232, 144, 144, 0.7)',
  'rgba(112, 145, 188, 0.7)',
  'rgba(157, 129, 216, 0.7)',
  'rgba(0, 0, 0, 0.65)'
])

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
const initTask = (): TaskInfo => {
  return {
    id: '',
    todoId: '',
    todoName: '',
    todoType: 10,
    taskName: '',
    taskContent: '',
    taskTags: [],
    deadLine: '',
    creTime: '',
    startTime: '',
    endTime: '',
    process: 0,
    color: '',
    taskStatus: 'WAITING',
    updTaskName: false,
    updTaskContent: false
  }
}
// 当前待办事项
const curTodoId = ref<string>('')
const curTask = ref<TaskInfo>(initTask())
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
const taskStatus = computed(() => {
  if (curTask.value.taskStatus === 'WAITING') {
    return '未开始'
  }
  if (curTask.value.taskStatus === 'PROCESSING') {
    return '进行中'
  }
  if (curTask.value.taskStatus === 'COMPLETED') {
    return '已完成'
  }
})
/**
 * 查看指定待办事项的所有任务
 * @param todoId 待办事项ID
 */
const toTask = (todoId: string) => {
  curTodoId.value = todoId
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

const toWaiting = () => {
  toWaitingApi({ id: curTask.value.id!, todoId: curTask.value.todoId }).then((resp) => {
    ElMessage('已移至待办')
    savedCallback(resp.data)
    isShowDetail.value = false
  })
}

const toProcessing = () => {
  toProcessingApi({ id: curTask.value.id!, todoId: curTask.value.todoId }).then((resp) => {
    ElMessage('已移至进行中')
    savedCallback(resp.data)
    isShowDetail.value = false
  })
}

const toCompleted = () => {
  toCompletedApi({ id: curTask.value.id!, todoId: curTask.value.todoId }).then((resp) => {
    ElMessage('已移至完成')
    savedCallback(resp.data)
    isShowDetail.value = false
  })
}

const addTask = () => {
  if (isBlank(curTask.value.todoId)) {
    ElMessage('请先选择要添加任务的日期或阶段性事项')
    return
  }
  if (isBlank(curTask.value.taskName)) {
    ElMessage('请填写 Todo 标题')
    return
  }
  addTaskApi(curTask.value).then((resp) => {
    ElMessage('新增成功')
    isShowDetail.value = false
    savedCallback(resp.data)
    getTodos()
  })
}

const updTask = () => {
  updTaskApi(curTask.value).then((resp) => {
    ElMessage('修改成功')
    isShowDetail.value = false
  })
}

const delTask = () => {
  ElMessageBox.confirm('是否确定删除该任务', '删除任务', {
    confirmButtonText: '我要删除',
    cancelButtonText: '取消'
  }).then(() => {
    delTaskApi({ id: curTask.value.id!, todoId: curTask.value.todoId }).then((resp) => {
      ElMessage('删除成功')
      savedCallback(resp.data)
      isShowDetail.value = false
    })
  })
}

const detailType = ref('upd')
const isShowDetail = ref(false)
/**
 * 显式任务详情
 * @param type 新增/修改
 * @param task 修改的任务
 */
const showDetail = (type: 'add' | 'upd', task?: TaskInfo) => {
  isShowDetail.value = true
  detailType.value = type
  if (type === 'upd') {
    curTask.value = task!
  } else {
    curTask.value = initTask()
    curTask.value.todoType = todoType.value
    curTask.value.todoId = curTodoId.value
  }
}

//#endregion

//#region ----------------------------------------< 标签 >--------------------------------------
const isShowTagDialog = ref(false)
const tagInput = ref('')
const showTagDialog = () => {
  isShowTagDialog.value = true
}
const handleTagClose = (tag: string) => {
  curTask.value.taskTags.splice(curTask.value.taskTags.indexOf(tag), 1)
}
const addTag = () => {
  if (isBlank(tagInput.value)) {
    ElMessage('请填写标签内容')
    return
  }
  curTask.value.taskTags.push(tagInput.value)
  isShowTagDialog.value = false
  tagInput.value = ''
}

//#endregion
</script>

<style scoped lang="scss">
.todo-root {
  @include box(100%, 100%);
  @include flex(column, flex-start, center);
  overflow: hidden;
  position: relative;

  .workbench,
  .task-day-calendar,
  .types,
  .tasks,
  .phaseds {
    max-width: 900px;
  }

  // 顶部操作台
  .workbench {
    padding: 0 10px 10px 10px;

    :deep(.el-button, .el-radio-button__inner) {
      padding: 8px 10px;
    }
  }

  // 任务日历
  .task-day-calendar {
    min-height: 203px;
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
            height: 35px;
            padding: 0;
            .cell-wrapper {
              @include box(100%, 100%);
              position: relative;
              .day {
                width: 100%;
                @include font(14px, 300);
                padding: 1px 0 0 3px;
              }
              .count {
                text-align: right;
                position: absolute;
                right: 5px;
                bottom: 3px;
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

  // 阶段性事项列表
  .phaseds {
    @include box(100%, 203px);
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

  // 三阶段控制按钮
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

  // 任务列表
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
      margin-bottom: 10px;
      border-radius: 4px;
      background-color: #f3f3f3;
      position: relative;
      transition: background-color 0.3s;
      padding-bottom: 10px;

      &:active {
        background-color: #d8d8d8;
      }

      .process {
        margin-bottom: 7px;
        border-top-left-radius: 4px;
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
        padding: 0 10px;
        margin-top: 3px;
        color: #a3a3a3;
        word-break: break-all;
        overflow: hidden;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
      }
    }
  }
}

.drawer-header {
  --el-font-size-base: 15px;
  position: relative;
}

// 抽屉的内容
.detail {
  @include flex(column, flex-start, flex-start);
  height: 100%;
  padding-top: 20px;
  padding-bottom: 30px;
  --el-font-size-base: 14px;

  & > div {
    margin-bottom: 18px;
  }

  .tags {
    height: 44px;
    overflow: scroll;
    margin-bottom: 0;

    .el-button {
      width: 55px;
      margin-right: 5px;
    }
    .el-tag {
      --el-tag-text-color: #fff;
      margin-right: 5px;
    }
  }

  .times {
    width: 100%;
    font-size: 13px;
    color: var(--el-text-color-regular);
    .bl-row-root {
      white-space: nowrap;
    }
    .label {
      width: 50px;
    }
  }

  .colors {
    height: 24px;
    font-size: 13px;
    color: var(--el-text-color-regular);
    .color {
      @include box(20px, 20px);
      margin: 0 15px 0 0;
      border-radius: 4px;
      transition: transform 0.1s;
      &:active {
        transform: scale(1.2);
      }
    }
    .label {
      width: 50px;
    }
  }

  .process {
    width: 50px;
    font-style: italic;
    color: #bebebe;
    text-align: right;
  }

  .btns {
    margin-bottom: 0;
    .bl-delete-line {
      font-size: 20px;
    }
    .bl-a-texteditorsave-line {
      font-size: 17px;
    }

    .bl-a-boxsubtract-line,
    .bl-a-boxaddition-line,
    .bl-a-boxchoice-line {
      margin-right: 5px;
    }
  }

  .status {
    font-size: 12px;
    font-style: italic;
    margin-bottom: 0;
    color: #bebebe;
    position: absolute;
    left: 30px;
    bottom: 10px;
  }
}
</style>
