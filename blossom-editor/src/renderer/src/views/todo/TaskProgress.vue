<template>
  <div class="task-workbench">

    <bl-row class="bars">
      <div v-if="countWait != 0" class="waiting" :style="{ width: `calc(${(countWait / countTotal) * 100}% - 6px)` }">
      </div>
      <div v-if="countProc != 0" class="processing" :style="{ width: `calc(${(countProc / countTotal) * 100}% - 6px)` }">
      </div>
      <div v-if="countComp != 0" class="completed" :style="{ width: `calc(${(countComp / countTotal) * 100}% - 6px)` }">
      </div>
      <div v-if="countTotal == 0" class="completed" style="width:calc(100% - 6px)"></div>
    </bl-row>
    <bl-row height="24px" just="space-between" align="center" style="padding: 0 10px;">
      <div class="task-name">{{ curTodo.todoName }}</div>
      <bl-row width="200px" class="bars-legend">
        <div class="waiting"></div>待办
        <div class="processing"></div>进行中
        <div class="completed"></div>完成
      </bl-row>
    </bl-row>
    <bl-row just="flex-end" style="padding: 0 10px 0 0;margin-top: 5px;">
      <el-checkbox v-model="showAnyTime" label="显示时间" border />
      <el-button style="margin-left: 10px;" @click="showExportDialog">导出任务</el-button>
    </bl-row>
  </div>

  <div class="task-progress-root">
    <div class="waiting" @dragenter="onDragenter(WaitDragRef, 'WAITING', $event)"
      @dragleave="onDragleave(WaitDragRef, $event)" ref="WaitRef">
      <div class="tasks-title">
        <span>待办</span>
        <span class="iconbl bl-a-addline-line" @click="showTaskInfoDialog()"></span>
      </div>

      <div class="tasks-sub-title" align="flex-start"><span>Waiting</span><span>{{ taskWait.length }}</span></div>
      <div class="tasks-container">
        <div class="drag-container" ref="WaitDragRef">将任务设置为未开始</div>

        <div v-for="t in taskWait" :key="t.id" draggable="true" class="task-item"
          @dragstart="dragstartWait([ProcDragRef, CompDragRef], $event)" @dragend="dragendWait(t, $event)">

          <div v-if="t.todoType == 99" class="middle"></div>
          <div v-else>
            <bl-row class="task-title" just="space-between" :style="{ backgroundImage: getColor(t.color) }">
              <el-input v-if="t.updTaskName" v-model="t.taskName" :id="'task-name-input-' + t.id"
                @blur="blurTaskNameInput(t)"></el-input>
              <div v-else @dblclick="showTaskNameInput(t)">{{ t.taskName }}</div>
              <el-button class="iconbl bl-a-fileedit-line" @click="showTaskInfoDialog(t.id)" :color="t.color"></el-button>
            </bl-row>

            <div v-if="showAnyTime" class="task-times">创建于: {{ t.creTime }}</div>
            <div v-if="isNotBlank(t.deadLine)" class="dead-line task-times">截止至: {{ t.deadLine }}</div>

            <el-input class="task-content" type="textarea" v-if="t.updTaskContent" v-model="t.taskContent"
              @blur="blurTaskContentInput(t)" :id="'task-content-input-' + t.id"></el-input>
            <div class="task-content" v-else @dblclick="showTaskContentInput(t)">{{ t.taskContent }}</div>
            <el-progress v-if="t.process > 0" :percentage="t.process" :color="t.color" />
          </div>
        </div>
      </div>
    </div>


    <div class="processing" ref="ProcRef" @dragenter="onDragenter(ProcDragRef, 'PROCESSING', $event)"
      @dragleave="onDragleave(ProcDragRef, $event)">
      <div class="tasks-title"><span>进行中</span></div>
      <div class="tasks-sub-title"><span>Processing</span><span>{{ taskProc.length }}</span></div>
      <div class="tasks-container">
        <div class="drag-container" ref="ProcDragRef">将任务设置为进行中</div>
        <!--  -->
        <div v-for="t in taskProc" :key="t.id" class="task-item" draggable="true"
          @dragstart="dragstartWait([WaitDragRef, CompDragRef], $event)" @dragend="dragendProc(t, $event)">
          <div v-if="t.todoType == 99" class="middle">中午 12:00</div>
          <div v-else>
            <bl-row class="task-title" just="space-between" :style="{ backgroundImage: getColor(t.color) }">
              <el-input v-if="t.updTaskName" v-model="t.taskName" :id="'task-name-input-' + t.id"
                @blur="blurTaskNameInput(t)"></el-input>
              <div v-else @dblclick="showTaskNameInput(t)">{{ t.taskName }}</div>
              <el-button class="iconbl bl-a-fileedit-line" @click="showTaskInfoDialog(t.id)" :color="t.color"></el-button>
            </bl-row>

            <div v-if="showAnyTime" class="task-times">创建于: {{ t.creTime }}</div>
            <div v-if="showAnyTime" class="task-times">开始于: {{ t.startTime }}</div>
            <div v-if="isNotBlank(t.deadLine)" class="dead-line task-times">截止至: {{ t.deadLine }}</div>

            <el-input class="task-content" type="textarea" v-if="t.updTaskContent" v-model="t.taskContent"
              @blur="blurTaskContentInput(t)" :id="'task-content-input-' + t.id"></el-input>
            <div class="task-content" v-else @dblclick="showTaskContentInput(t)">{{ t.taskContent }}</div>
            <el-progress v-if="t.process > 0" :percentage="t.process" :color="t.color" />
          </div>
        </div>
      </div>
    </div>

    <!--  -->
    <div class="completed" ref="CompRef" @dragenter="onDragenter(CompDragRef, 'COMPLETED', $event)"
      @dragleave="onDragleave(CompDragRef, $event)">
      <div class="tasks-title">完成</div>
      <div class="tasks-sub-title">
        <span>Completed</span>
        <span>{{ taskComp.length }}</span>
      </div>
      <div class="tasks-container">
        <div class="drag-container" ref="CompDragRef">将任务设置为完成</div>

        <!--  -->
        <div v-for="t in taskComp" :key="t.id" draggable="true" class="task-item"
          @dragstart="dragstartWait([WaitDragRef, ProcDragRef], $event)" @dragend="dragendComp(t, $event)">
          <div v-if="t.todoType == 99" class="middle">中午 12:00</div>
          <div v-else>
            <bl-row class="task-title" just="space-between" :style="{ backgroundImage: getColor(t.color) }">
              <el-input v-if="t.updTaskName" v-model="t.taskName" :id="'task-name-input-' + t.id"
                @blur="blurTaskNameInput(t)"></el-input>
              <div v-else @dblclick="showTaskNameInput(t)">{{ t.taskName }}</div>
              <el-button class="iconbl bl-a-fileedit-line" @click="showTaskInfoDialog(t.id)" :color="t.color"></el-button>
            </bl-row>

            <div v-if="showAnyTime" class="task-times">创建于: {{ t.creTime }}</div>
            <div v-if="showAnyTime" class="task-times">开始于: {{ t.startTime }}</div>
            <div v-if="showAnyTime" class="task-times">结束于: {{ t.endTime }}</div>
            <div v-if="isNotBlank(t.deadLine)" class="dead-line task-times">截止至: {{ t.deadLine }}</div>

            <el-input class="task-content" type="textarea" v-if="t.updTaskContent" v-model="t.taskContent"
              @blur="blurTaskContentInput(t)" :id="'task-content-input-' + t.id"></el-input>
            <div class="task-content" v-else @dblclick="showTaskContentInput(t)">{{ t.taskContent }}</div>
            <el-progress v-if="t.process > 0" :percentage="t.process" :color="t.color" />
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- 导出 dialog -->
  <el-dialog draggable title="导出任务" v-model="isShowExportDialog" :align-center="true" :append-to-body="true"
    :destroy-on-close="true" :close-on-click-modal="false" width="800px" style="--el-dialog-border-radius:10px;
    --el-dialog-padding-primary: 10px;
    --el-dialog-box-shadow:var(--bl-box-shadow);
    --el-dialog-bg-color:var(--bl-html-color);">
    <bl-row align="center" height="20px">
      <bl-row width="250px" v-if="curTodo.todoType == 20">
        导出 {{ curTodo.todoName }}
      </bl-row>
      <bl-row width="250px" v-if="curTodo.todoType == 10">
        导出时间范围：
        <el-date-picker style="width: 150px;" v-model="exportForm.exportDays" type="daterange" range-separator="至"
          start-placeholder="开始日期" end-placeholder="结束日期" format="MM-DD" value-format="YYYY-MM-DD" />
      </bl-row>
      <el-checkbox v-model="exportForm.exportDate" label="导出时间" border style="margin: 10px;" />
      <el-checkbox v-model="exportForm.exportProcess" label="导出进度" border />
      <el-button style="margin-left: 10px;" @click="exportTodo">预览</el-button>
      <el-button @click="download">下载 Markdown</el-button>
    </bl-row>
    <el-divider style="margin: 10px 0;"></el-divider>
    <bl-row>
      <el-input type="textarea" :rows="27" v-model="exportContent"></el-input>
    </bl-row>
  </el-dialog>

  <el-dialog draggable v-model="isShowTaskInfoDialog" :align-center="true" :modal="false" :lock-scroll="false"
    :append-to-body="false" :destroy-on-close="true" :close-on-click-modal="false" width="350" style="--el-dialog-border-radius:10px;
    --el-dialog-padding-primary:0;
    --el-dialog-box-shadow:var(--bl-box-shadow);
    --el-dialog-bg-color:var(--bl-html-color);">
    <TaskInfoComponent ref="TaskInfoRef" @saved="savedCallback"></TaskInfoComponent>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, nextTick, Ref, ref } from 'vue'
import { isBlank, isNotBlank } from '@renderer/assets/utils/obj'
import { tasksApi, updTaskApi, toWaitingApi, toProcessingApi, toCompletedApi, exportTodoApi } from '@renderer/api/todo'
import { TaskInfo, TaskStatus, TodoType } from './scripts/types'
import TaskInfoComponent from './TaskInfo.vue'
import { getDateFormat } from '@renderer/assets/utils/util'
import Notify from '@renderer/scripts/notify'

const getColor = (color: string) => {
  return `linear-gradient(270deg, ${color}, var(--bl-html-color))`
}

//#region ---------------------------------------- 列表弹框显示 ----------------------------------------
const curTodo = ref({ todoId: '', todoName: '', todoType: 10 })
const taskWait = ref<TaskInfo[]>([])
const taskProc = ref<TaskInfo[]>([])
const taskComp = ref<TaskInfo[]>([])
const countWait = computed<number>(() => taskWait.value.length)
const countProc = computed<number>(() => taskProc.value.filter(t => t.todoType != 99).length)
const countComp = computed<number>(() => taskComp.value.filter(t => t.todoType != 99).length)
const countTotal = computed(() => countWait.value + countProc.value + countComp.value)

const getTasksApi = (todoId: string) => {
  tasksApi({ todoId: todoId }).then(resp => {
    taskWait.value = resp.data.waiting
    taskProc.value = resp.data.processing
    taskComp.value = resp.data.completed
  })
}

const TaskInfoRef = ref()
const isShowTaskInfoDialog = ref(false)

/**
 * 显示
 * @param id 
 */
const showTaskInfoDialog = (id?: string) => {
  isShowTaskInfoDialog.value = true
  nextTick(() => {
    if (id) {
      TaskInfoRef.value.reload('upd', id)
    } else {
      TaskInfoRef.value.reload('add', undefined, curTodo.value.todoId, curTodo.value.todoType)
    }
  })
}

/**
 * 保存数据后的回调
 * @param data 
 */
const savedCallback = (data: any) => {
  taskWait.value = data.waiting
  taskProc.value = data.processing
  taskComp.value = data.completed
  isShowTaskInfoDialog.value = false
  emits('refreshTodo')
}

//#endregion

//#region ---------------------------------------- 文本框修改内容 ----------------------------------------
const showTaskNameInput = (task: TaskInfo) => {
  task.updTaskName = true
  nextTick(() => {
    let ele = document.getElementById('task-name-input-' + task.id)
    if (ele) { ele.focus() }
  })
}

/**
 * 任务名称失去焦点, 保存数据
 */
const blurTaskNameInput = (task: TaskInfo) => {
  updTaskApi({ id: task.id, todoId: task.todoId, taskName: task.taskName })
  task.updTaskName = false
}

const showTaskContentInput = (task: TaskInfo) => {
  task.updTaskContent = true
  nextTick(() => {
    let ele = document.getElementById('task-content-input-' + task.id)
    if (ele) { ele.focus() }
  })
}

/**
 * 任务内容失去焦点, 保存数据
 */
const blurTaskContentInput = (task: TaskInfo) => {
  updTaskApi({ id: task.id, todoId: task.todoId, taskContent: task.taskContent })
  task.updTaskContent = false
}

/**
 * 菜单点击调用该方法
 * 
 * @param todoId 
 * @param todoName
 * @param todoType 
 */
const reload = (todoId: string, todoName: string, todoType: TodoType) => {
  curTodo.value = { todoId: todoId, todoName: todoName, todoType: todoType }
  getTasksApi(todoId)
}

//

//#endregion

//#region ---------------------------------------- 顶部操作台 ----------------------------------------
const isShowExportDialog = ref(false)
const showAnyTime = ref(false)
const exportContent = ref('')
const exportForm = ref({
  todoId: '',
  exportDays: [getDateFormat(), getDateFormat()],
  exportProcess: false,
  exportDate: false
})

const showExportDialog = () => {
  isShowExportDialog.value = true
}

const exportTodo = () => {
  console.log(curTodo.value.todoId);
  if (isBlank(curTodo.value.todoId)) {
    Notify.error('请先选择待办事项再使用导出')
    return
  }
  let params = {
    todoId: curTodo.value.todoType == 20 ? curTodo.value.todoId : '',
    beginTodoId: exportForm.value.exportDays[0],
    endTodoId: exportForm.value.exportDays[1],
    exportProcess: exportForm.value.exportProcess,
    exportDate: exportForm.value.exportDate
  }
  exportTodoApi(params).then(resp => {
    exportContent.value = resp.data
    console.log(resp);
  })
}

const download = () => {
  if (curTodo.value.todoType == 10) {
    
  }
  let filename = '任务导出.md'
  let a = document.createElement('a')
  let blob = new Blob([exportContent.value], { type: "text/plain" })
  let objectUrl = URL.createObjectURL(blob)
  a.setAttribute("href", objectUrl)
  a.setAttribute("download", filename)
  a.click()
  URL.revokeObjectURL(a.href)
  a.remove()
}
//#endregion

//#region ---------------------------------------- 组件拖动 ----------------------------------------
const WaitRef = ref()
const WaitDragRef = ref()

const ProcRef = ref()
const ProcDragRef = ref()

const CompRef = ref()
const CompDragRef = ref()

let toStage: TaskStatus | ''

const dragstartWait = (doms: any, e: DragEvent) => {
  let img = new Image()
  img.src = "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' %3E%3Cpath /%3E%3C/svg%3E"
  e.dataTransfer!.setDragImage(img, 0, 0)
  let ele = (e!.target as Element)
  let cloneNode = ele.cloneNode(true) as HTMLElement
  const targetRect = ele.getBoundingClientRect()
  const distLeft = e.clientX - targetRect.left
  const distTop = e.clientY - targetRect.top

  cloneNode.style.position = 'fixed'
  cloneNode.style.left = '0'
  cloneNode.style.top = '0'
  cloneNode.style.zIndex = '999'
  cloneNode.style.pointerEvents = 'none'
  cloneNode.style.transform = `translate(${e.clientX - distLeft}px,${e.clientY - distTop}px)`
  document.body.appendChild(cloneNode)
  const onDrag = (de: any) => {
    if (cloneNode) {
      cloneNode.style.transform = `translate(${de.clientX - distLeft}px,${de.clientY - distTop}px)`
    }
  }
  const onDragend = () => {
    document.body.removeChild(cloneNode);
  }
  ele.addEventListener('drag', onDrag)
  ele.addEventListener('dragend', onDragend)
  ele.classList.add('moving')
  for (let i = 0; i < doms.length; i++) {
    doms[i].style.display = 'block'
  }
  toStage = ''
}

/**
 * 从 waiting 中拖出
 */
const dragendWait = (task: TaskInfo, e: DragEvent) => {
  (e!.target as Element).classList.remove('moving')
  ProcDragRef.value.style.display = 'none'
  CompDragRef.value.style.display = 'none'
  if (toStage == '') return
  removeTask(taskWait, task)
  toStageHandle(task)
}

/**
 * 从 processing 中拖出
 */
const dragendProc = (task: TaskInfo, e: DragEvent) => {
  (e!.target as Element).classList.remove('moving')
  WaitDragRef.value.style.display = 'none'
  CompDragRef.value.style.display = 'none'
  if (toStage == '') return
  removeTask(taskProc, task)
  toStageHandle(task)
}

/**
 * 从 completed 中拖出
 */
const dragendComp = (task: TaskInfo, e: DragEvent) => {
  (e!.target as Element).classList.remove('moving')
  WaitDragRef.value.style.display = 'none'
  ProcDragRef.value.style.display = 'none'
  if (toStage == '') return
  removeTask(taskComp, task)
  toStageHandle(task)
}

/**
 * 从原始数组中删除, 通过 id 判断
 * @params tasks 数组
 * @params task 删除对象
 */
const removeTask = (tasks: Ref<TaskInfo[]>, task: TaskInfo) => {
  for (let index = 0; index < tasks.value.length; index++) {
    const ele = tasks.value[index]
    if (task.id == ele.id) {
      console.log(index);
      tasks.value.splice(index, 1)
      break
    }
  }
}

/**
 * 将数据移动到指定阶段
 */
const toStageHandle = (task: TaskInfo): boolean => {
  if (toStage === 'WAITING') {
    toWaitingApi({ id: task.id, todoId: task.todoId }).then(resp => savedCallback(resp.data))
    return true
  } else if (toStage === 'PROCESSING') {
    toProcessingApi({ id: task.id, todoId: task.todoId }).then(resp => savedCallback(resp.data))
    return true
  } else if (toStage === 'COMPLETED') {
    toCompletedApi({ id: task.id, todoId: task.todoId }).then(resp => savedCallback(resp.data))
    return true
  }
  return false
}

let debounceTimeout: NodeJS.Timeout | undefined
const onDragenter = (dom: any, type: TaskStatus, e: DragEvent) => {
  if (e.target !== dom) {
    return
  }
  if (debounceTimeout != undefined) {
    clearTimeout(debounceTimeout)
  }
  toStage = type
  dom.classList.add('enter')
}

const onDragleave = (dom: any, e: DragEvent) => {
  if (e.target !== dom) {
    return
  }
  debounceTimeout = setTimeout(() => {
    toStage = ''
  }, 200);
  dom.classList.remove('enter')
}

//#endregion

defineExpose({ reload })
const emits = defineEmits(['refreshTodo'])
</script>

<style scoped lang="scss">
.task-workbench {
  @include box(100%, 90px);
  font-weight: 300;
  color: var(--bl-text-color);
  border-bottom: 1px solid var(--el-border-color);

  .task-name {
    @include font(16px, 300);
    text-shadow: var(--bl-text-shadow);
  }

  .waiting {
    background-color: #FE63706B;
  }

  .processing {
    background-color: #FBA85F6B;
  }

  .completed {
    background-color: #9974FE6B;
  }

  .bars {
    padding: 10px 10px 5px;

    div {
      // @include themeShadow(0 0 4px #C0C0C0, 0 0 6px #000000);
      @include font(13px, 300);
      height: 13px;
      margin: 0 3px;
      border-radius: 4px;
      padding: 3px 10px;
      text-align: center;
      transition: width 1s;
      box-shadow:
        inset 0 3px #FFFFFF5D,
        inset 0 -2px #46464680,
        0 2px 4px #C0C0C0;


      [class="dark"] & {
        box-shadow:
          inset 0 3px #FFFFFF48,
          inset 0 -3px #2F2F2FA9,
          0 2px 4px #000000;
      }

    }

  }

  .bars-legend {
    font-size: 14px;
    color: var(--bl-text-color-light);

    &>div {
      @include box(7px, 7px);
      border-radius: 50%;
      margin: 0 5px 0 20px;
    }

  }
}

.task-progress-root {
  @include box(100%, calc(100% - 100px));
  @include flex(row, flex-start, center);
  padding-top: 20px;
  overflow-x: scroll;

  .waiting,
  .processing,
  .completed {
    @include flex(column, flex-start, center);
    @include box(350px, 100%, 350px, 350px);
    color: var(--bl-text-color);
    position: relative;
    padding: 0 10px;


    .tasks-title {
      @include flex(row, space-between, center);
      @include box(270px, 30px);
      @include font(28px, 700);
      text-shadow: var(--bl-text-shadow);

      .iconbl {
        @include font(13px, 300);
        padding-right: 3px;
        color: var(--bl-text-color-light);
        cursor: pointer;

        &:hover {
          color: var(--el-color-primary);
        }
      }
    }

    .tasks-sub-title {
      @include flex(row, space-between, center);
      @include box(270px, 30px);
      @include font(13px, 300);
      padding: 0 5px;
      color: var(--bl-text-color-light);
      border-bottom: 1px solid var(--el-border-color);
    }

    .tasks-container {
      @include box(100%, calc(100% - 60px));
      @include flex(column, flex-start, center);
      position: relative;
      overflow-y: overlay;
      padding: 20px 0;

      .drag-container {
        @include box(295px, 100%);
        position: absolute;
        top: 0;
        left: calc(50% - 140px);
        padding: 30px 0;
        color: transparent;
        text-align: center;
        display: none;
        z-index: 2;

      }

      .drag-container.enter {
        @include themeBg(#F5F5F5E8, #000000E0);
        border-radius: 10px;
        border: 3px solid var(--el-color-primary);
        color: var(--bl-text-color);
      }
    }
  }

}

.task-popover {
  background-color: var(--bl-bg-color) !important;
  color: red;

  &>div {
    padding: 5px 5px 5px 5px;
    margin: 0 5px 5px 5px;
    border-radius: 5px;
    text-align: left;
    cursor: pointer;

    &:hover {
      background: var(--el-color-primary-light-8);
    }

    .el-input-number--small {
      margin-left: 10px;
      width: 80px;
    }
  }
}
</style>

<style lang="scss">
.task-item {
  @include themeShadow(0 0 5px #C0C0C0, 0 0 5px #000000);
  @include themeBg(#F9F9F9, #0F0F0F);
  width: 270px;
  min-width: 270px;
  height: auto;
  border-radius: 4px;
  margin-bottom: 15px;
  transition: box-shadow 0.3s;


  .middle {
    @include themeColor(#CFCFCF, #545454E0);
    @include themeBorder(2px, #CFCFCF, #545454E0, 'bottom', 0, dashed);
    width: 100%;
    font-size: 12px;
    top: 500px;
  }

  &:has(.middle) {
    width: 100%;
    background-color: transparent;
    box-shadow: none;
    pointer-events: none;

    &:hover {
      box-shadow: none;
    }
  }

  &:hover {
    @include themeShadow(0 0 9px #C0C0C0, 0 0 15px #000000);

    .task-title {

      .iconbl {
        opacity: 1;
      }
    }
  }

  .task-title {
    @include themeBrightness(100%, 80%);
    font-size: 14px;
    padding: 5px 10px;
    margin-bottom: 5px;
    border-top-left-radius: 4px;
    border-top-right-radius: 4px;
    // border-bottom: 1px solid var(--el-border-color);
    cursor: move;


    &>div {
      transition: color 0.2s;

      &:hover {
        @include themeColor(#000000, #ECECEC);
      }
    }

    // 任务的操作按钮
    .iconbl {
      opacity: 0;
    }
  }


  .task-times {
    height: 20px;
    font-size: 13px;
    padding: 0 10px;
    color: var(--bl-text-color-light);
  }

  .dead-line {
    @include themeColor(#D5002E, #740019);
    // @include themeBg(#D5002E, #740019);
    // @include themeColor(#ffffff, #CCB6BB);
    // @include themeShadow(inset 0 0 5px #AEAEAE, inset 0 0 5px #000000);
    // font-size: 12px;
    // margin: 10px 10px 5px 10px;
    // padding: 4px 5px;
    // border-radius: 4px;
  }

  .task-content {
    margin-top: 5px;
    padding: 5px 10px;
    font-size: 13px;
    color: var(--bl-text-color-light);
    // border-top: 1px solid var(--el-border-color);
    white-space: pre-line;
    word-wrap: break-word;
    overflow: auto;
    transition: color 0.2s;

    &:hover {
      @include themeColor(#555555, #A5A5A5);
    }
  }

  .el-progress {
    padding: 0 10px;
    margin-bottom: 10px;

    .el-progress__text {
      font-size: 12px !important;
      min-width: auto;
      color: var(--bl-text-color-light);
    }
  }

}

.task-item.moving {
  border: 1px dashed var(--el-border-color);
  box-shadow: none;
  cursor: move;

  * {
    visibility: hidden;
  }
}
</style>