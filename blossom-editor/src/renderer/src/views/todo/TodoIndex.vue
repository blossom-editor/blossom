<template>
  <div class="todo-root">
    <div class="todo-aside">
      <div class="setting doc-workbench-root">
        <div class="iconbl bl-a-cloudrefresh-line" @click="getTodos()"></div>
      </div>
      <div class="task-collapse">
        <el-collapse v-model="activeName" accordion>
          <el-collapse-item title="每日待办事项" name="1">
            <div v-for="taskday in todoDays" class="task-day" @click="toTask(taskday.todoId, taskday.todoName, taskday.todoType)">
              {{ taskday.todoName }}
              <bl-tag v-if="taskday.taskCount > 0">{{ taskday.taskCount }}</bl-tag>
              <bl-tag v-if="taskday.today">今日</bl-tag>
            </div>
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

import { nextTick, onActivated, onMounted, ref } from 'vue'
import { TodoList, TodoType } from './scripts/types'
import { todosApi, addPhasedApi, updTodoNameApi } from '@renderer/api/todo'
import { isNotBlank } from '@renderer/assets/utils/obj'
import { getNextDay, getDateFormat } from '@renderer/assets/utils/util'
import TaskProgress from './TaskProgress.vue'
import TodoStat from './TodoStat.vue'

const { viewStyle } = useConfigStore()

onMounted(() => {
  getTodos()
  let today = getDateFormat()
  toTask(today, today, 10)
})

onActivated(() => {
  getTodos()
})

let todoDaysResource: any
const getTodos = () => {
  todosApi().then((resp) => {
    todoDaysResource = resp.data.todoDays
    todoPhased.value = resp.data.taskPhased
    todoPhasedClose.value = resp.data.taskPhasedClose
    initTaskDays()
  })
}

const toTask = (todoId: string, todoName: string, todoType: TodoType) => {
  TaskProgressRef.value.reload(todoId, todoName, todoType)
  if (viewStyle.todoStatExpand) {
    TodoStatRef.value.reload(todoId)
  }
}

const TaskProgressRef = ref()
const TodoStatRef = ref()
const activeName = ref('1')
const today = ref(getDateFormat())
const nextWeek = ref(getNextDay(getDateFormat(), 7))
const todoDays = ref<TodoList[]>([])

/**
 * 获取前31天及后7天的日期
 */
const initTaskDays = () => {
  today.value = getDateFormat()
  nextWeek.value = getNextDay(getDateFormat(), 7)
  let days: TodoList[] = []

  let addDay = (day: string) => {
    let resource = todoDaysResource[day]
    days.push({
      todoId: day,
      todoName: day,
      todoStatus: 1,
      todoType: 10,
      today: day === today.value,
      taskCount: resource ? resource.taskCount : 0,
      updTodoName: false
    })
  }
  // 往后7天
  for (let index = 0; index < 7; index++) {
    addDay(getNextDay(nextWeek.value, index * -1))
  }

  // // 往前1个月
  for (let index = 0; index < 31; index++) {
    addDay(getNextDay(today.value, index * -1))
  }
  todoDays.value = days
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
          overflow-y: scroll;
          padding: 0 10px 0 20px;

          [class='dark'] & {
            box-shadow:
              inset -1px 3px 5px #000,
              inset -1px -3px 5px #000;
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
      //@include box(calc(100% - 450px), 100%);
      overflow: hidden;
    }

    .todo-stat {
      @include box(450px, 100%);
      // height: 100%;
      border-left: 1px solid var(--el-border-color);
    }
  }

  .gradient-linear {
    --color1: #ffffff;
    --color2: #9a9a9a05;

    [class='dark'] & {
      --color1: #1e1e1e;
      --color2: #4a4a4a03;
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

  @media screen and (max-width: 1120px) {
    .todo-main {
      .todo-tasks {
        @include box(100%, 100%);
      }

      .todo-stat {
        display: none;
      }
    }
  }
}
</style>
