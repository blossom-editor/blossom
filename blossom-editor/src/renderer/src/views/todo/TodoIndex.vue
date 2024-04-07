<template>
  <div class="todo-root">
    <div class="todo-aside">
      <div class="setting doc-workbench-root">
        <div class="iconbl bl-refresh-line" @click="getTodos()"></div>
      </div>
      <div class="task-collapse">
        <el-collapse v-model="activeName" accordion>
          <el-collapse-item title="每日待办事项" name="1" class="collapse-item">
            <el-calendar ref="CalendarRef" class="task-day-calendar">
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
                  <div v-if="getCount(data.day) != 0">
                    <bl-tag>{{ getCount(data.day) }}</bl-tag>
                  </div>
                </div>
              </template>
            </el-calendar>
          </el-collapse-item>

          <el-collapse-item title="阶段性事项" name="2">
            <div v-for="phased in todoPhased" class="task-phased" @click="toTask(phased.todoId, phased.todoName, phased.todoType)">
              <el-input
                v-if="phased.updTodoName"
                v-model="phased.todoName"
                type="textarea"
                :id="'phased-name-input-' + phased.todoId"
                :rows="3"
                @blur="blurPhasedUpdHandle(phased.todoId!)"></el-input>

              <div v-else class="phase-name" @dblclick="showPhasedUpdHandle(phased.todoId!)">
                {{ phased.todoName }}
              </div>
              <div class="phased-stat">{{ phased.taskCountStat }}</div>
            </div>

            <el-input
              v-if="showPhasedAdd"
              ref="phasedAddInputRef"
              v-model="phasedAddName"
              @keyup.enter="blurPhasedAddHandle"
              @blur="blurPhasedAddHandle"></el-input>
            <div v-else class="task-phased-add" @click="showPhasedAddHandle">新增计划</div>
          </el-collapse-item>

          <el-collapse-item title="阶段性事项 已完成" name="3">
            <div v-for="phased in todoPhasedClose" class="task-phased" @click="toTask(phased.todoId, phased.todoName, phased.todoType)">
              <div class="phased-stat">{{ phased.taskCountStat }}</div>
              <div class="phase-name">{{ phased.todoName }}</div>
            </div>
          </el-collapse-item>
        </el-collapse>
      </div>
    </div>
    <div class="todo-main">
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
import { nextTick, ref } from 'vue'
import type { CalendarDateType, CalendarInstance } from 'element-plus'
import { TodoList, TodoType } from './scripts/types'
import { todosApi, addPhasedApi, updTodoNameApi } from '@renderer/api/todo'
import { isNotBlank } from '@renderer/assets/utils/obj'
import { getDateFormat } from '@renderer/assets/utils/util'
import { useLifecycle } from '@renderer/scripts/lifecycle'
import TaskProgress from './TaskProgress.vue'
import TodoStat from './TodoStat.vue'

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
const CalendarRef = ref<CalendarInstance>()
const selectDate = (val: CalendarDateType) => {
  if (!CalendarRef.value) return
  CalendarRef.value.selectDate(val)
}

//#endregion

// 获取的每日待办事项原始数据
const TaskProgressRef = ref()
const TodoStatRef = ref()
const activeName = ref('1')
// key:日期; value:当日的待办统计
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
        taskCountStat: '0|0|0',
        updTodoName: false
      })
    }
    todoPhased.value = resp.data.taskPhased
    todoPhasedClose.value = resp.data.taskPhasedClose
  })
}

/**
 * @todo 日历每一项点击时由于内部数据变更, 都会触发查询
 * @param day
 */
const getCount = (day: string): number => {
  if (!todoDayMaps.value) {
    return 0
  }
  if (!todoDayMaps.value.has(day)) {
    return 0
  }
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
          padding: 0 5px 0 15px;
          overflow-x: hidden;
          overflow-y: scroll;

          [class='dark'] & {
            box-shadow:
              inset -1px 3px 5px #0f0f0f,
              inset -1px -3px 5px #0f0f0f;
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
      --el-calendar-selected-bg-color: var(--el-color-primary-light-8);

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

          .is-today {
            .el-calendar-day {
              .cell-wrapper {
                .day {
                  font-weight: 900;
                  text-decoration: underline;
                }
              }
            }
          }
        }
      }
    }

    .task-phased {
      @include box(220px, auto);
      @include flex(row, flex-start, flex-start);
      @include font(13px, 300);
      border: 1px solid var(--el-border-color);
      min-height: 40px;
      flex-wrap: wrap;
      padding: 3px 5px;
      margin-bottom: 10px;
      color: var(--bl-text-color);
      border-radius: 3px;
      position: relative;
      overflow: hidden;
      transition:
        box-shadow 0.3s,
        border-color 0.3s;
      cursor: pointer;

      &:hover {
        @include themeShadow(0 0 9px #c0c0c0, 0 0 10px #000000);
        border-color: var(--el-color-primary);

        .phased-stat {
          background-color: var(--el-color-primary-light-8);
        }
      }

      &:first-child {
        margin-top: 15px;
      }

      .phase-name {
        line-height: 14px;
        word-break: break-all;
      }

      .phased-stat {
        font-size: 11px;
        line-height: 15px;
        padding: 2px 10px 0 10px;
        color: var(--bl-text-color-light);
        background-color: var(--bl-bg-color);
        border-top-left-radius: 3px;
        position: absolute;
        right: 0;
        bottom: 0;
        transition: background-color 0.1s;
      }

      .task-count {
        padding: 0 5px;
        margin-left: 5px;
      }
    }

    .task-phased-add {
      @include themeBorder(1px, #b4b4b4, #515151, 'around', 3px, dashed);
      width: 220px;
      padding: 1px 5px;
      margin: 10px 0;
      color: var(--bl-text-color-light);
      transition: box-shadow 0.3s;
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
