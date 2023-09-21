<template>
  <div class="task-progress-simple-root">
    <div v-if="countWait > 0" class="task-workbench">
      <bl-row class="bars">
        <div v-if="countWait != 0" class="waiting" :style="{ width: `calc(${(countWait / countTotal) * 100}% - 6px)` }">
        </div>
        <div v-if="countProc != 0" class="processing"
          :style="{ width: `calc(${(countProc / countTotal) * 100}% - 6px)` }">
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
    </div>

    <div v-if="countWait == 0" class="placeholder">
      无待办事项
    </div>

    <div class="progress-container">
      <div class="waiting">
        <!-- <div class="tasks-title"><span>待办</span></div>
        <div class="tasks-sub-title" align="flex-start"><span>Waiting</span><span>{{ countWait }}</span></div> -->
        <div class="tasks-container">

          <div v-for="t in taskWait" :key="t.id" class="task-item">
            <div v-if="t.todoType == 99" class="divider"></div>
            <div v-else>
              <bl-row class="task-title" just="space-between">
                <div>{{ t.taskName }}</div>
              </bl-row>
              <div class="task-content">{{ t.taskContent }}</div>
              <el-progress v-if="t.process > 0" :percentage="t.process" :color="t.color" />
            </div>
          </div>
        </div>
      </div>


      <div class="processing">
        <!-- <div class="tasks-title"><span>进行中</span></div>
        <div class="tasks-sub-title"><span>Processing</span><span>{{ countProc }}</span></div> -->
        <div class="tasks-container">
          <!--  -->
          <div v-for="t in taskProc" :key="t.id" class="task-item">
            <div v-if="t.todoType == 99" class="divider">中午 12:00</div>
            <div v-else>
              <bl-row class="task-title" just="space-between">
                <div>{{ t.taskName }}</div>
              </bl-row>
              <bl-row class="task-tags" v-if="!isEmpty(t.taskTags)">
                <bl-tag v-for="tag in t.taskTags" :key="tag" :size="11">{{ tag }}</bl-tag>
              </bl-row>
              <div class="task-content">{{ t.taskContent }}</div>
              <el-progress v-if="t.process > 0" :percentage="t.process" :color="t.color" />
            </div>
          </div>
        </div>
      </div>

      <!--  -->
      <div class="completed">
        <!-- <div class="tasks-title">完成</div>
        <div class="tasks-sub-title"><span>Completed</span><span>{{ countComp }}</span></div> -->
        <div class="tasks-container">
          <!--  -->
          <div v-for="t in taskComp" :key="t.id" class="task-item">
            <div v-if="t.todoType == 99" class="divider">中午 12:00</div>
            <div v-else>
              <bl-row class="task-title" just="space-between">
                <div>{{ t.taskName }}</div>
              </bl-row>
              <bl-row class="task-tags" v-if="!isEmpty(t.taskTags)">
                <bl-tag v-for="tag in t.taskTags" :key="tag" :size="11">{{ tag }}</bl-tag>
              </bl-row>
              <div class="task-content">{{ t.taskContent }}</div>
              <el-progress v-if="t.process > 0" :percentage="t.process" :color="t.color" />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onActivated, onMounted, ref } from 'vue'
import { tasksApi } from '@renderer/api/todo'
import { TaskInfo } from './scripts/types'
import { isEmpty } from 'lodash'
import { getDateFormat } from '@renderer/assets/utils/util'

onMounted(() => {
  getTasks(getDateFormat())
})

onActivated(() => {
  getTasks(getDateFormat())
})

//#region --------------------------------------------------< 列表/弹框显示 >--------------------------------------------------
const curTodo = ref({ todoId: '', todoName: '', todoType: 10 })
const taskWait = ref<TaskInfo[]>([])
const taskProc = ref<TaskInfo[]>([])
const taskComp = ref<TaskInfo[]>([])
const countWait = computed<number>(() => taskWait.value.length)
const countProc = computed<number>(() => taskProc.value.filter(t => t.todoType != 99).length)
const countComp = computed<number>(() => taskComp.value.filter(t => t.todoType != 99).length)
const countTotal = computed(() => countWait.value + countProc.value + countComp.value)

const getTasks = (todoId: string) => {
  tasksApi({ todoId: todoId }).then(resp => {
    taskWait.value = resp.data.waiting
    taskProc.value = resp.data.processing
    taskComp.value = resp.data.completed
  })
}
</script>

<style scoped lang="scss">
@import './styles/task-progress.scss';
@import './styles/task-item.scss';

.task-progress-simple-root {
  @include box(100%, 100%);

  .placeholder {
    padding: 20px 0 0 20px;
    color: var(--bl-text-color-light);
  }

  .task-workbench {
    display: block;
    height: 60px;
  }

  .progress-container {
    height: calc(100% - 60px);

    .waiting,
    .processing,
    .completed {
      width: 240px;
      min-width: 240px;
      max-width: 240px;
      padding: 0;

      .tasks-container {
        height: 100%;
        padding-top: 0;
        overflow-x: hidden;

        .task-item {
          @include themeBg(linear-gradient(155deg, #ffffff00 0%, #FFFFFF84 60%, var(--el-color-primary-light-9) 100%),
            linear-gradient(155deg, var(--bl-html-color) 0%, #00000015 60%, var(--el-color-primary-light-9) 100%));
          width: 210px;
          min-width: 210px;
          max-width: 210px;
          margin: 10px 20px;

          &:has(.divider) {
            background: transparent !important;
          }
        }
      }
    }
  }

  @media screen and (max-height:1100px) {
    .task-workbench {
      display: none;
    }

    .progress-container {
      height: 100%;
      padding-top: 0;
    }
  }
}

::-webkit-scrollbar {
  width: 0px;
  height: 3px;
}
</style>