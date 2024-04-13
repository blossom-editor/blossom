<template>
  <div class="task-workbench">
    <bl-row class="bars">
      <div
        v-if="countWait != 0"
        :class="['waiting', viewStyle.isGlobalShadow ? '' : 'bar-light']"
        :style="{ width: `calc(${(countWait / countTotal) * 100}% - 6px)` }"></div>
      <div
        v-if="countProc != 0"
        :class="['processing', viewStyle.isGlobalShadow ? '' : 'bar-light']"
        :style="{ width: `calc(${(countProc / countTotal) * 100}% - 6px)` }"></div>
      <div
        v-if="countComp != 0"
        :class="['completed', viewStyle.isGlobalShadow ? '' : 'bar-light']"
        :style="{ width: `calc(${(countComp / countTotal) * 100}% - 6px)` }"></div>
      <div v-if="countTotal == 0" :class="['completed', viewStyle.isGlobalShadow ? '' : 'bar-light']" style="width: calc(100% - 6px)"></div>
    </bl-row>
    <bl-row height="24px" just="space-between" align="center" style="padding: 0 10px">
      <div class="task-name">{{ curTodo.todoName }}</div>
      <bl-row width="200px" class="bars-legend">
        <div class="waiting"></div>
        待办
        <div class="processing"></div>
        进行中
        <div class="completed"></div>
        完成
      </bl-row>
    </bl-row>
    <bl-row just="space-between" style="padding: 0 10px 0 10px; margin-top: 5px">
      <div>
        <el-button v-if="curTodo.todoType == 10" type="primary" @click="handleShowBenchwork" plain>转移事项</el-button>
      </div>
      <div>
        <el-select class="tag-select" v-model="queryTags" multiple collapse-tags collapse-tags-tooltip placeholder="根据标签筛选">
          <el-option v-for="tag in queryTagOptions" :key="tag" :label="tag" :value="tag"></el-option>
        </el-select>
        <el-checkbox v-model="showAnyTime" label="显示时间" border style="margin-left: 10px; margin-right: 10px" />
        <el-button @click="showExportDialog">导出任务</el-button>
        <el-checkbox v-model="viewStyle.todoStatExpand" label="显示统计" border style="margin-left: 10px" @change="statExpandChange" />
      </div>
    </bl-row>
  </div>

  <bl-row class="task-workbench-child" :style="workbencChildStyle.workbench">
    <span>将</span>
    <bl-tag>{{ taskIdMap.size }}</bl-tag>
    <span style="padding-right: 10px">项待办转移至</span>
    <el-date-picker
      v-model="transferForm.todoId"
      style="width: 125px"
      type="date"
      format="YYYY-MM-DD"
      value-format="YYYY-MM-DD"
      :disabled-date="disabledDate" />
    <el-checkbox v-model="transferForm.delSource" label="删除原事项" border style="margin-left: 10px" />
    <el-button type="primary" @click="transfer" plain style="margin-left: 10px">确认转移</el-button>
  </bl-row>

  <div class="progress-container" :style="workbencChildStyle.progress">
    <div
      class="waiting"
      @dragenter="onDragenter(WaitDragRef, $event)"
      @drop="onDrop('WAITING')"
      @dragleave="onDragleave(WaitDragRef, $event)"
      ref="WaitRef">
      <div class="tasks-title">
        <span>待办</span>
        <div>
          <span class="add-icon iconbl bl-a-addline-line" @click="quickAdd()"></span>
          <span class="add-icon iconbl bl-a-pageadd-line" @click="showTaskInfoDialog()"></span>
        </div>
      </div>

      <div class="tasks-sub-title" align="flex-start">
        <span>Waiting</span><span>{{ countWait }}</span>
      </div>
      <div class="tasks-container">
        <div class="drag-container" ref="WaitDragRef">
          <div>将任务设置为未开始</div>
          <div class="iconbl bl-a-boxsubtract-line"></div>
        </div>

        <div v-if="countTotal == 0" class="task-tip">
          在待办列表右侧点击<span class="add-icon iconbl bl-a-addline-line" @click="showTaskInfoDialog()"></span>添加任务
        </div>

        <div
          v-for="t in taskWaitComputed"
          :key="t.id"
          draggable="true"
          class="task-item task-item-normal"
          @dragstart="dragStart([ProcDragRef, CompDragRef], $event)"
          @dragend="dragendWait(t, $event)">
          <div v-if="t.todoType == 99" class="divider"></div>
          <div v-else>
            <bl-row just="center" v-if="isShowWorkbenchChild" class="selection" @click="transferClick(t.id as string)">
              <div class="iconbl bl-check" v-if="taskIdMap.has(t.id as string)"></div>
            </bl-row>
            <bl-row class="task-title" just="space-between" :style="{ backgroundColor: getColor(t.color) }">
              <el-input v-if="t.updTaskName" v-model="t.taskName" :id="'task-name-input-' + t.id" @blur="blurTaskNameInput(t)"></el-input>
              <div v-else @dblclick="showTaskNameInput(t)">{{ t.taskName }}</div>
              <el-button class="iconbl bl-pen" @click="showTaskInfoDialog(t.id)" text></el-button>
            </bl-row>

            <div v-if="showAnyTime" class="task-times">创建于: {{ t.creTime }}</div>
            <div v-if="isNotBlank(t.deadLine)" class="dead-line task-times">截止至: {{ t.deadLine }}</div>
            <bl-row class="task-tags" v-if="!isEmpty(t.taskTags)">
              <bl-tag v-for="tag in t.taskTags" :key="tag" :size="11">{{ tag }}</bl-tag>
            </bl-row>
            <el-input
              class="task-content"
              type="textarea"
              v-if="t.updTaskContent"
              v-model="t.taskContent"
              @blur="blurTaskContentInput(t)"
              :id="'task-content-input-' + t.id"></el-input>
            <div v-else :class="[isBlank(t.taskContent) ? 'task-content-placeholder' : 'task-content']" @dblclick="showTaskContentInput(t)">
              {{ t.taskContent }}
            </div>
            <el-progress v-if="t.process > 0" :percentage="t.process" :color="t.color" />
          </div>
        </div>
      </div>
    </div>

    <div
      class="processing"
      ref="ProcRef"
      @dragenter="onDragenter(ProcDragRef, $event)"
      @drop="onDrop('PROCESSING')"
      @dragleave="onDragleave(ProcDragRef, $event)">
      <div class="tasks-title"><span>进行中</span></div>
      <div class="tasks-sub-title">
        <span>Processing</span><span>{{ countProc }}</span>
      </div>
      <div class="tasks-container">
        <div class="drag-container" ref="ProcDragRef">
          <div>将任务设置为进行中</div>
          <div class="iconbl bl-a-boxaddition-line"></div>
        </div>
        <!--  -->
        <div
          v-for="t in taskProcComputed"
          :key="t.id"
          class="task-item task-item-normal"
          draggable="true"
          @dragstart="dragStart([WaitDragRef, CompDragRef], $event)"
          @dragend="dragendProc(t, $event)">
          <div v-if="t.todoType == 99" class="divider">中午 12:00</div>
          <div v-else>
            <bl-row just="center" v-if="isShowWorkbenchChild" class="selection" @click="transferClick(t.id as string)">
              <div class="iconbl bl-check" v-if="taskIdMap.has(t.id as string)"></div>
            </bl-row>
            <bl-row class="task-title" just="space-between" :style="{ backgroundColor: getColor(t.color) }">
              <el-input v-if="t.updTaskName" v-model="t.taskName" :id="'task-name-input-' + t.id" @blur="blurTaskNameInput(t)"></el-input>
              <div v-else @dblclick="showTaskNameInput(t)">{{ t.taskName }}</div>
              <el-button class="iconbl bl-pen" @click="showTaskInfoDialog(t.id)" text></el-button>
            </bl-row>

            <div v-if="showAnyTime" class="task-times">创建于: {{ t.creTime }}</div>
            <div v-if="showAnyTime" class="task-times">开始于: {{ t.startTime }}</div>
            <div v-if="isNotBlank(t.deadLine)" class="dead-line task-times">截止至: {{ t.deadLine }}</div>
            <bl-row class="task-tags" v-if="!isEmpty(t.taskTags)">
              <bl-tag v-for="tag in t.taskTags" :key="tag" :size="11">{{ tag }}</bl-tag>
            </bl-row>
            <el-input
              class="task-content"
              type="textarea"
              v-if="t.updTaskContent"
              v-model="t.taskContent"
              @blur="blurTaskContentInput(t)"
              :id="'task-content-input-' + t.id"></el-input>
            <div v-else :class="[isBlank(t.taskContent) ? 'task-content-placeholder' : 'task-content']" @dblclick="showTaskContentInput(t)">
              {{ t.taskContent }}
            </div>
            <el-progress v-if="t.process > 0" :percentage="t.process" :color="t.color" />
          </div>
        </div>
      </div>
    </div>

    <!--  -->
    <div
      class="completed"
      ref="CompRef"
      @dragenter="onDragenter(CompDragRef, $event)"
      @drop="onDrop('COMPLETED')"
      @dragleave="onDragleave(CompDragRef, $event)">
      <div class="tasks-title">完成</div>
      <div class="tasks-sub-title">
        <span>Completed</span><span>{{ countComp }}</span>
      </div>
      <div class="tasks-container">
        <div class="drag-container" ref="CompDragRef">
          <div>将任务设置为完成</div>
          <div class="iconbl bl-a-boxchoice-line"></div>
        </div>
        <!--  -->
        <div
          v-for="t in taskCompComputed"
          :key="t.id"
          draggable="true"
          class="task-item task-item-normal"
          @dragstart="dragStart([WaitDragRef, ProcDragRef], $event)"
          @dragend="dragendComp(t, $event)">
          <div v-if="t.todoType == 99" class="divider">中午 12:00</div>
          <div v-else>
            <bl-row just="center" v-if="isShowWorkbenchChild" class="selection" @click="transferClick(t.id as string)">
              <div class="iconbl bl-check" v-if="taskIdMap.has(t.id as string)"></div>
            </bl-row>
            <bl-row class="task-title" just="space-between" :style="{ backgroundColor: getColor(t.color) }">
              <el-input v-if="t.updTaskName" v-model="t.taskName" :id="'task-name-input-' + t.id" @blur="blurTaskNameInput(t)"></el-input>
              <div v-else @dblclick="showTaskNameInput(t)">{{ t.taskName }}</div>
              <el-button class="iconbl bl-pen" @click="showTaskInfoDialog(t.id)" text></el-button>
            </bl-row>

            <div v-if="showAnyTime" class="task-times">创建于: {{ t.creTime }}</div>
            <div v-if="showAnyTime" class="task-times">开始于: {{ t.startTime }}</div>
            <div v-if="showAnyTime" class="task-times">结束于: {{ t.endTime }}</div>
            <div v-if="isNotBlank(t.deadLine)" class="dead-line task-times">截止至: {{ t.deadLine }}</div>
            <bl-row class="task-tags" v-if="!isEmpty(t.taskTags)">
              <bl-tag v-for="tag in t.taskTags" :key="tag" :size="11">{{ tag }}</bl-tag>
            </bl-row>
            <el-input
              class="task-content"
              type="textarea"
              v-if="t.updTaskContent"
              v-model="t.taskContent"
              @blur="blurTaskContentInput(t)"
              :id="'task-content-input-' + t.id"></el-input>
            <div v-else :class="[isBlank(t.taskContent) ? 'task-content-placeholder' : 'task-content']" @dblclick="showTaskContentInput(t)">
              {{ t.taskContent }}
            </div>
            <el-progress v-if="t.process > 0" :percentage="t.process" :color="t.color" />
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- 导出 dialog -->
  <el-dialog
    draggable
    v-model="isShowExportDialog"
    :align-center="true"
    :append-to-body="true"
    :destroy-on-close="true"
    :close-on-click-modal="false"
    width="800px"
    style="--el-dialog-padding-primary: 10px !important">
    <div style="padding: 15px">
      <bl-row align="center" height="20px">
        <bl-row width="250px" v-if="curTodo.todoType == 20"> 导出 {{ curTodo.todoName }} </bl-row>
        <bl-row width="250px" v-if="curTodo.todoType == 10">
          导出时间范围：
          <el-date-picker
            style="width: 150px"
            v-model="exportForm.exportDays"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="MM-DD"
            value-format="YYYY-MM-DD" />
        </bl-row>
        <el-checkbox v-model="exportForm.exportDate" label="导出时间" border style="margin: 10px" />
        <el-checkbox v-model="exportForm.exportProcess" label="导出进度" border />
        <el-button style="margin-left: 10px" @click="exportTodo">预览</el-button>
        <el-button @click="download">下载 Markdown</el-button>
      </bl-row>
      <el-divider style="margin: 10px 0"></el-divider>
      <bl-row>
        <el-input type="textarea" :rows="27" v-model="exportContent"></el-input>
      </bl-row>
    </div>
  </el-dialog>

  <!-- 详情 dialog -->
  <el-dialog
    draggable
    v-model="isShowTaskInfoDialog"
    :align-center="true"
    :modal="false"
    :lock-scroll="false"
    :append-to-body="false"
    :destroy-on-close="true"
    :close-on-click-modal="false"
    width="350">
    <TaskInfoComponent ref="TaskInfoRef" @saved="savedCallback"></TaskInfoComponent>
  </el-dialog>
</template>

<script setup lang="ts">
import { useConfigStore } from '@renderer/stores/config'
import { computed, nextTick, onMounted, onUnmounted, Ref, ref } from 'vue'
import { isBlank, isNotBlank } from '@renderer/assets/utils/obj'
import { tasksApi, addTaskApi, updTaskApi, toWaitingApi, toProcessingApi, toCompletedApi, exportTodoApi, taskTransferApi } from '@renderer/api/todo'
import { TaskInfo, TaskStatus, TodoType } from './scripts/types'
import { getDateFormat } from '@renderer/assets/utils/util'
import { isEmpty } from 'lodash'
import Notify from '@renderer/scripts/notify'
import TaskInfoComponent from './TaskInfo.vue'
import { dayjs, ElMessageBox } from 'element-plus'

onMounted(() => {
  document.addEventListener('dragover', preventDefaultDragover, false)
})

onUnmounted(() => {
  document.removeEventListener('dragover', preventDefaultDragover)
})

const getColor = (color: string) => {
  return `${color}`
}

//#region --------------------------------------------------< 列表/弹框显示 >--------------------------------------------------
const curTodo = ref({ todoId: '', todoName: '', todoType: 10 })
const taskWait = ref<TaskInfo[]>([])
const taskProc = ref<TaskInfo[]>([])
const taskComp = ref<TaskInfo[]>([])
const countWait = computed<number>(() => taskWaitComputed.value.length)
const countProc = computed<number>(() => taskProcComputed.value.filter((t) => t.todoType != 99).length)
const countComp = computed<number>(() => taskCompComputed.value.filter((t) => t.todoType != 99).length)
const countTotal = computed(() => countWait.value + countProc.value + countComp.value)

const taskWaitComputed = computed<TaskInfo[]>(() => {
  if (queryTags.value.length == 0) {
    return taskWait.value
  }
  return includeQueryTags(taskWait)
})

const taskProcComputed = computed<TaskInfo[]>(() => {
  if (queryTags.value.length == 0) {
    return taskProc.value
  }
  return includeQueryTags(taskProc)
})

const taskCompComputed = computed<TaskInfo[]>(() => {
  if (queryTags.value.length == 0) {
    return taskComp.value
  }
  return includeQueryTags(taskComp)
})

const getTasks = (todoId: string) => {
  tasksApi({ todoId: todoId }).then((resp) => {
    taskWait.value = resp.data.waiting
    taskProc.value = resp.data.processing
    taskComp.value = resp.data.completed
  })
}

const TaskInfoRef = ref()
const isShowTaskInfoDialog = ref(false)

/**
 * 显示任务详情
 * @param id 任务ID
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

const quickAdd = () => {
  addTaskApi({ todoId: curTodo.value.todoId, taskName: '新待办事项' }).then((_resp) => {
    getTasks(curTodo.value.todoId)
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

//#region --------------------------------------------------< 文本框修改内容 >--------------------------------------------------
const showTaskNameInput = (task: TaskInfo) => {
  task.updTaskName = true
  nextTick(() => {
    let ele = document.getElementById('task-name-input-' + task.id)
    if (ele) {
      ele.focus()
    }
  })
}

/**
 * 任务名称失去焦点, 保存数据
 */
const blurTaskNameInput = (task: TaskInfo) => {
  updTaskApi({ id: task.id, todoId: task.todoId, taskName: task.taskName }).then(() => {
    task.updTaskName = false
  })
}

const showTaskContentInput = (task: TaskInfo) => {
  task.updTaskContent = true
  nextTick(() => {
    let ele = document.getElementById('task-content-input-' + task.id)
    if (ele) {
      ele.focus()
    }
  })
}

/**
 * 任务内容失去焦点, 保存数据
 */
const blurTaskContentInput = (task: TaskInfo) => {
  updTaskApi({ id: task.id, todoId: task.todoId, taskName: task.taskName, taskContent: task.taskContent }).then(() => {
    task.updTaskContent = false
  })
}

/**
 * 菜单点击调用该方法
 *
 * @param todoId
 * @param todoName
 * @param todoType
 */
const reload = (todoId: string, todoName: string, todoType: TodoType) => {
  taskIdMap.value.clear()
  if (todoType === 20) {
    isShowWorkbenchChild.value = false
    handleBenchworkStyle(false)
  }

  curTodo.value = { todoId: todoId, todoName: todoName, todoType: todoType }
  queryTags.value = []
  getTasks(todoId)
}

//#endregion

//#region --------------------------------------------------< 顶部操作台 >--------------------------------------------------

const configStore = useConfigStore()
const { viewStyle } = configStore

const queryTagOptions = computed(() => {
  let tags = new Set()
  taskWait.value.forEach((task) => {
    task.taskTags.forEach((tag) => {
      tags.add(tag)
    })
  })
  taskProc.value.forEach((task) => {
    task.taskTags.forEach((tag) => {
      tags.add(tag)
    })
  })
  taskComp.value.forEach((task) => {
    task.taskTags.forEach((tag) => {
      tags.add(tag)
    })
  })
  return Array.from(tags).sort()
})
const queryTags = ref<string[]>([])
const showAnyTime = ref(false)

const isInclude = (taskTags: any[], queryTags: any[]) => queryTags.every((val) => taskTags.includes(val))
/**
 * 检查传入的任务列表中, 是否包含 queryTags 中的全部值
 */
const includeQueryTags = (tasks: Ref<TaskInfo[]>) => {
  let result: TaskInfo[] = []
  for (let index = 0; index < tasks.value.length; index++) {
    const task: TaskInfo = tasks.value[index]
    const taskTags = task.taskTags
    if (!taskTags) {
      continue
    }
    if (isInclude(taskTags, queryTags.value)) {
      result.push(task)
    }
  }
  return result
}

const isShowExportDialog = ref(false)
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
  exportTodoApi(params).then((resp) => {
    exportContent.value = resp.data
  })
}

const download = () => {
  if (curTodo.value.todoType == 10) {
  }
  let filename = '任务导出.md'
  let a = document.createElement('a')
  let blob = new Blob([exportContent.value], { type: 'text/plain' })
  let objectUrl = URL.createObjectURL(blob)
  a.setAttribute('href', objectUrl)
  a.setAttribute('download', filename)
  a.click()
  URL.revokeObjectURL(a.href)
  a.remove()
}

const statExpandChange = () => {
  configStore.setViewStyle(viewStyle)
}
//#endregion

//#region --------------------------------------------------< 二级操作台 >--------------------------------------------------
const workbencChildStyle = ref({ workbench: { height: '0', visibility: 'hidden', opacity: 0 }, progress: { height: 'calc(100% - 100px)' } })
const isShowWorkbenchChild = ref(false)
const transferForm = ref({ todoId: '', delSource: false })
const taskIdMap = ref(new Map<string, any>())

const handleShowBenchwork = () => {
  isShowWorkbenchChild.value = !isShowWorkbenchChild.value
  handleBenchworkStyle(isShowWorkbenchChild.value)
}

const handleBenchworkStyle = (expand: boolean) => {
  if (expand) {
    workbencChildStyle.value = { workbench: { height: '48px', visibility: 'visible', opacity: 1 }, progress: { height: 'calc(100% - 100px - 48px)' } }
  } else {
    workbencChildStyle.value = { workbench: { height: '0', visibility: 'hidden', opacity: 0 }, progress: { height: 'calc(100% - 100px)' } }
  }
}

/**
 * 判断可转移的日期区间
 * @param time 日期
 */
const disabledDate = (time: Date) => {
  return dayjs(dayjs(time).format('YYYY-MM-DD')).diff(curTodo.value.todoId, 'day') === 0
}

/**
 * 选中或取消选中
 * @param taskId 任务ID
 */
const transferClick = (taskId: string) => {
  if (taskIdMap.value.has(taskId)) {
    taskIdMap.value.delete(taskId)
  } else {
    taskIdMap.value.set(taskId, '')
  }
}

/**
 * 转移任务
 */
const transfer = () => {
  if (isBlank(transferForm.value.todoId)) {
    Notify.warning('请先选择将任务转移至哪一天', '无法转移')
    return
  }
  if (taskIdMap.value.size === 0) {
    Notify.warning('请先选中需要转移的事项', '无法转移')
    return
  }
  if (transferForm.value.todoId === curTodo.value.todoId) {
    Notify.warning(`转移日期与原日期不能相同`, '无法转移')
    return
  }
  ElMessageBox.confirm(
    `是否确认将 ${taskIdMap.value.size} 个任务转移至 ${transferForm.value.todoId} ? ` +
      (transferForm.value.delSource === true ? '原任务将被删除!' : '原任务仍会保留。'),
    '是否转移',
    {
      confirmButtonText: '我要转移',
      cancelButtonText: '我再想想',
      type: 'warning'
    }
  ).then(() => {
    taskTransferApi({ ...transferForm.value, ...{ curTodoId: curTodo.value.todoId, taskIds: Array.from(taskIdMap.value.keys()) } }).then((resp) => {
      savedCallback(resp.data)
      Notify.success(`所选待办事项已转移至 [${transferForm.value.todoId}]`, '转移事项成功')
    })
  })
}
//#endregion

//#region --------------------------------------------------< 组件拖动 >--------------------------------------------------
/**
 * 在 mac 中, dragend 事件会在鼠标松开后半秒左右触发, 导致样式出现问题
 * 禁用掉 dragover 的默认行为后就正常了, 虽然不知道是为什么
 * https://stackoverflow.com/a/65910078/22700578
 */
const preventDefaultDragover = (event: DragEvent) => {
  event.preventDefault()
}

const WaitRef = ref()
const WaitDragRef = ref()

const ProcRef = ref()
const ProcDragRef = ref()

const CompRef = ref()
const CompDragRef = ref()

let toStage: TaskStatus | ''


const img = new Image()
img.src = new URL(`../../assets/imgs/transparent.png`, import.meta.url).href

/**
 * 开始拖动
 * @param doms 可以拖到哪两个范围内
 * @param e
 */
const dragStart = (doms: any, e: DragEvent) => {
  if (isShowWorkbenchChild.value) {
    e.preventDefault()
    return
  }
  e.dataTransfer!.setDragImage(img, 0, 0)
  let ele = e!.target as Element
  let cloneNode = ele.cloneNode(true) as HTMLElement
  const targetRect = ele.getBoundingClientRect()
  const distLeft = e.clientX - targetRect.left
  const distTop = e.clientY - targetRect.top

  cloneNode.style.position = 'fixed'
  cloneNode.style.left = `${targetRect.left}px`
  cloneNode.style.top = `${targetRect.top}px`
  cloneNode.style.zIndex = '999'
  cloneNode.style.pointerEvents = 'none'
  document.body.appendChild(cloneNode)

  // 拖拽事件
  const onDrag = (e: any) => {
    if (cloneNode) {
      cloneNode.style.transform = `translate(${e.clientX - distLeft - targetRect.left}px, ${e.clientY - distTop - targetRect.top}px)`
    }
  }

  // 松开事件
  const onDragend = () => {
    console.log('onDragend')
    ele.removeEventListener('drag', onDrag)
    ele.removeEventListener('dragend', onDragend)
    ele.classList.remove('moving')
    cloneNode.remove()
  }

  ele.addEventListener('drag', onDrag)
  ele.addEventListener('dragend', onDragend)
  
  ele.classList.add('moving') // 原始元素隐藏

  for (let i = 0; i < doms.length; i++) {
    doms[i].classList.remove('enter')
    doms[i].style.display = 'block'
  }

  toStage = ''
}

/**
 * waiting 中的元素在拖拽完成后执行
 */
const dragendWait = (task: TaskInfo, _e: DragEvent) => {
  ProcDragRef.value.style.display = 'none'
  CompDragRef.value.style.display = 'none'
  // 如果没有拖入任何元素
  if (toStage == '' || toStage === 'WAITING') return
  // 如果拖入到其他阶段
  removeTask(taskWait, task) // 1. 从本阶段删除
  toStageHandle(task) // 2. 数据条件到其他阶段
}

/**
 * processing 中的元素在拖拽完成后执行
 */
const dragendProc = (task: TaskInfo, _e: DragEvent) => {
  WaitDragRef.value.style.display = 'none'
  CompDragRef.value.style.display = 'none'
  if (toStage == '' || toStage === 'PROCESSING') return
  removeTask(taskProc, task)
  toStageHandle(task)
}

/**
 * 从 completed 中拖出
 */
const dragendComp = (task: TaskInfo, _e: DragEvent) => {
  WaitDragRef.value.style.display = 'none'
  ProcDragRef.value.style.display = 'none'
  if (toStage == '' || toStage === 'COMPLETED') return
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
    toWaitingApi({ id: task.id, todoId: task.todoId }).then((resp) => savedCallback(resp.data))
    return true
  } else if (toStage === 'PROCESSING') {
    toProcessingApi({ id: task.id, todoId: task.todoId }).then((resp) => savedCallback(resp.data))
    return true
  } else if (toStage === 'COMPLETED') {
    toCompletedApi({ id: task.id, todoId: task.todoId }).then((resp) => savedCallback(resp.data))
    return true
  }
  return false
}

/**
 * 当拖拽的元素进入 dom 阶段, 增该阶段增加 enter 样式
 * @param dom 进入到的阶段
 */
const onDragenter = (dom: any, e: DragEvent) => {
  if (e.target !== dom) return
  dom.classList.add('enter')
}

/**
 * 元素拖拽到 type 阶段
 */
const onDrop = (type: TaskStatus) => {
  toStage = type
}

/**
 * 当拖拽的元素离开 dom 该段, 增该阶段删除 enter 样式
 * @param dom 离开的阶段
 */
const onDragleave = (dom: any, e: DragEvent) => {
  if (e.target !== dom) return
  dom.classList.remove('enter')
}

//#endregion

defineExpose({ reload })
const emits = defineEmits(['refreshTodo'])
</script>

<style scoped lang="scss">
@import './styles/task-progress.scss';
</style>

<style lang="scss">
@import './styles/task-item.scss';
</style>
