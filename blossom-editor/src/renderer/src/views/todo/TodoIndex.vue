<template>
  <div class="todo-root">
    <div class="todo-aside">
      <div class="setting doc-workbench-root">
        <div class="iconbl bl-a-cloudrefresh-line" @click="getTodos()"></div>
      </div>
      <div class="task-collapse">
        <el-collapse v-model="activeName" accordion>
          <el-collapse-item title="每日待办事项" name="1" class="collapse-item">
            <el-calendar ref="CalendarRef" class="task-day-calendar" v-model="selectDay">
              <template #header="{ date }">
                <bl-row just="space-between" class="header" style="margin: 8px 10px">
                  <div class="month">{{ date.split(' ')[2] }}{{ date.split(' ')[3] }}</div>
                  <el-button-group>
                    <el-button text bg @click="selectDate('prev-month')">上月</el-button>
                    <el-button text bg @click="selectDate('today')">今日</el-button>
                    <el-button text bg @click="selectDate('next-month')">下月</el-button>
                  </el-button-group>
                </bl-row>
              </template>
              <template #date-cell="{ data }">
                <div class="cell-wrapper" @click="toTask(data.day, data.day, 10)">
                  <div class="day">{{ data.day.split('-')[2] }}</div>
                  <div v-if="getCount(data.day) > 0">
                    <bl-tag>{{ getCount(data.day) }}</bl-tag>
                  </div>
                </div>
              </template>
            </el-calendar>
          </el-collapse-item>

          <!--  -->
          <el-collapse-item title="阶段性事项" name="2">
            <div v-for="phased in todoPhased" class="task-phased" @click="toTask(phased.todoId, phased.todoName, phased.todoType)">
              <!-- update name -->
              <el-input
                v-if="phased.updTodoName"
                :id="'phased-name-input-' + phased.todoId"
                v-model="phased.todoName"
                type="textarea"
                :rows="3"
                @blur="blurPhasedUpdHandle(phased.todoId!)"></el-input>
              <div v-else @dblclick="showPhasedUpdHandle(phased.todoId!)">{{ phased.todoName }}</div>
              <bl-tag v-if="phased.taskCount > 0">{{ phased.taskCount }}</bl-tag>
            </div>

            <!-- add phased -->
            <el-input
              v-if="showPhasedAdd"
              ref="phasedAddInputRef"
              v-model="phasedAddName"
              @blur="blurPhasedAddHandle"
              style="margin-top: 10px"></el-input>
            <div v-else class="task-phased-add" @click="showPhasedAddHandle">新增计划</div>
          </el-collapse-item>

          <!--  -->
          <el-collapse-item title="阶段性事项 已完成" name="3">
            <div v-for="phased in todoPhasedClose" class="task-phased" @click="toTask(phased.todoId, phased.todoName, phased.todoType)">
              {{ phased.todoName }}
              <bl-tag v-if="phased.taskCount > 0">{{ phased.taskCount }}</bl-tag>
            </div>
          </el-collapse-item>
        </el-collapse>
      </div>
    </div>
    <div class="todo-main gradient-linear">
      <div class="todo-tasks" :style="{ width: viewStyle.todoStatExpand ? 'calc(100% - 450px)' : '100%' }">
        <TaskProgress ref="TaskProgressRef" @refresh-todo="getTodos"></TaskProgress>
      </div>
      <div v-if="viewStyle.todoStatExpand" class="todo-stat">
        <TodoStat ref="TodoStatRef" @refresh-todo="getTodos"></TodoStat>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useConfigStore } from '@renderer/stores/config'
import { nextTick, ref, watch } from 'vue'
import type { CalendarDateType, CalendarInstance } from 'element-plus'
import { TodoList, TodoType } from './scripts/types'
import { todosApi, addPhasedApi, updTodoNameApi } from '@renderer/api/todo'
import { isNotBlank } from '@renderer/assets/utils/obj'
import { getDateFormat } from '@renderer/assets/utils/util'
import TaskProgress from './TaskProgress.vue'
import TodoStat from './TodoStat.vue'
import { useLifecycle } from '@renderer/scripts/lifecycle'

const { viewStyle } = useConfigStore()

useLifecycle(
  () => {
    getTodos()
    let today = getDateFormat()
    toTask(today, today, 10)
  },
  () => {
    getTodos()
  }
)

//#region ----------------------------------------< 日历 >--------------------------------------

const selectDay = ref()
const CalendarRef = ref<CalendarInstance>()
const selectDate = (val: CalendarDateType) => {
  if (!CalendarRef.value) return
  CalendarRef.value.selectDate(val)
}

watch(
  () => selectDay.value,
  (_date) => {
    // getPlanAll(timestampToDatetime(data).substring(0, 7))
  }
)

//#endregion

// 获取的每日待办事项原始数据
const TaskProgressRef = ref()
const TodoStatRef = ref()
const activeName = ref('1')
const todoDayMaps = ref<Map<string, TodoList>>(new Map())
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
    todoPhasedClose.value = resp.data.taskPhasedClose
  })
}

const getCount = (day: string): number => {
  if (!todoDayMaps.value) return 0
  if (!todoDayMaps.value.get(day)) return 0
  return todoDayMaps.value.get(day)!.taskCount
}

/**
 * 查看指定待办事项
 * @param todoId 待办事项ID
 * @param todoName 待办事项名称
 * @param todoType 待办事项类型
 */
const toTask = (todoId: string, todoName: string, todoType: TodoType) => {
  TaskProgressRef.value.reload(todoId, todoName, todoType)
  if (viewStyle.todoStatExpand) {
    TodoStatRef.value.reload(todoId)
  }
}

//#region --------------------------------------------------< 阶段性事项 >--------------------------------------------------
const todoPhased = ref<TodoList[]>([])
const todoPhasedClose = ref<TodoList[]>([])
const showPhasedAdd = ref(false)
const phasedAddInputRef = ref()
const phasedAddName = ref('')

const showPhasedAddHandle = () => {
  showPhasedAdd.value = true
  nextTick(() => {
    phasedAddInputRef.value.focus()
  })
}

const blurPhasedAddHandle = () => {
  showPhasedAdd.value = false
  if (isNotBlank(phasedAddName.value)) {
    addPhasedApi({ todoName: phasedAddName.value }).then((_) => {
      getTodos()
    })
  }
  phasedAddName.value = ''
}

const showPhasedUpdHandle = (todoId: string) => {
  for (let index = 0; index < todoPhased.value.length; index++) {
    let task = todoPhased.value[index]
    if (task.todoId === todoId) {
      task.updTodoName = true
      nextTick(() => {
        let ele = document.getElementById('phased-name-input-' + todoId)
        if (ele) {
          ele.focus()
        }
      })
      break
    }
  }
}

const blurPhasedUpdHandle = (todoId: string) => {
  for (let index = 0; index < todoPhased.value.length; index++) {
    let todo = todoPhased.value[index]
    if (todo.todoId === todoId) {
      todo.updTodoName = false
      updTodoNameApi({ todoId: todoId, todoName: todo.todoName })
      break
    }
  }
}

//#endregion
</script>

<style scoped lang="scss">
@import '../doc/tree-workbench.scss';

.todo-root {
  @include box(100%, 100%);
  @include flex(row, center, center);

  .todo-aside {
    @include box(250px, 100%, 250px, 250px);

    .setting {
      @include box(100%, 90px);
      @include flex(row, flex-end, flex-end);
      @include font(18px, 300);
      color: var(--bl-text-color);
      border-bottom: 1px solid var(--el-border-color);
    }

    .task-collapse {
      height: calc(100% - 90px);

      .el-collapse {
        height: 100%;
        $item-height: 48px;
        border-top: none;
        border-bottom: none;
        --el-transition-duration: 0s;

        :deep(.el-collapse-item) {
          max-height: calc(100% - #{$item-height * 2});
          overflow: hidden;
        }

        :deep(.el-collapse-item.is-active) {
          height: calc(100% - #{$item-height * 2});
          overflow: hidden;
        }

        :deep(.el-collapse-item__header) {
          padding-left: 20px;
        }

        :deep(.el-collapse-item__wrap) {
          height: 100%;
          max-height: 100%;
        }

        :deep(.el-collapse-item__content) {
          box-shadow:
            inset -1px 3px 5px #dfdfdf,
            inset -1px -3px 5px #dfdfdf;
          height: calc(100% - #{$item-height});
          overflow-y: auto;
          padding: 0 10px 0 20px;

          [class='dark'] & {
            box-shadow:
              inset -1px 3px 5px #000,
              inset -1px -3px 5px #000;
          }
        }
        .collapse-item {
          :deep(.el-collapse-item__content) {
            padding: 0;
          }
        }
      }
    }

    .task-day-calendar {
      background-color: transparent;
      :deep(.el-calendar__header) {
        padding: 0;
      }

      :deep(.el-calendar__body) {
        padding: 0;
        th {
          padding: 6px 0;
        }
        .el-calendar-table__row {
          .prev,
          .current,
          .next {
            .el-calendar-day {
              padding: 0;
              height: 50px;
              .cell-wrapper {
                @include box(100%, 100%);
                .day {
                  @include font(14px, 300);
                  text-align: center;
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

    .task-day,
    .task-phased {
      @include flex(row, flex-start, center);
      @include font(13px, 300);
      padding: 1px 3px;
      border-radius: 4px;
      color: var(--bl-text-color);
      cursor: pointer;

      &:hover {
        background-color: var(--el-color-info-light-9);
      }

      &:first-child {
        margin-top: 5px;
      }

      &:last-child {
        margin-bottom: 5px;
      }
    }

    .task-phased {
      padding: 5px 3px;
      @include flex(row, flex-start, flex-start);

      .task-count {
        padding: 0 5px;
        margin-left: 5px;
      }
    }

    .task-phased-add {
      @include themeBorder(1px, #b4b4b4, #515151, 'around', 6px, dashed);
      padding: 1px 5px;
      margin: 10px 0;
      color: var(--bl-text-color-light);
      transition: 0.3s;
      text-align: center;
      cursor: pointer;

      &:hover {
        @include themeShadow(0 0 5px #9e9e9e, 0 0 8px 1px #000000);
      }
    }
  }

  .todo-main {
    @include box(calc(100% - 250px), 100%);
    @include flex(row, center, center);
    border-left: 1px solid var(--el-border-color);

    .todo-tasks {
      height: 100%;
      overflow: hidden;
    }

    .todo-stat {
      @include box(450px, 100%);
      border-left: 1px solid var(--el-border-color);
    }
  }

  .gradient-linear {
    --color1: #ffffff;
    --color2: #9a9a9a05;

    [class='dark'] & {
      --color1: #1e1e1e;
      --color2: #a5a5a503;
    }

    background: linear-gradient(135deg, var(--color1) 25%, var(--color2) 0, var(--color2) 50%, var(--color1) 0, var(--color1) 75%, var(--color2) 0);
    background-size: 60px 60px;
    animation: alwaysToLeftBottom 320s linear infinite;
  }

  @keyframes alwaysToLeftBottom {
    to {
      background-position: 300%;
    }
  }

  @media screen and (max-width: 1350px) {
    .todo-main {
      .todo-tasks {
        width: 100% !important;
        height: 100%;
      }

      .todo-stat {
        display: none;
      }
    }
  }
}
</style>
