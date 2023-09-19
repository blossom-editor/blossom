<template>
  <div class="article-info-root">

    <!-- 标题 -->
    <div class="info-title-wrapper">
      <div class="info-title">{{ taskSaveFormTitle }}</div>
    </div>

    <div v-loading="formLoading" class="info-form">
      <el-form :inline="true" :model="taskSaveForm" :rules="taskSaveFormRule" label-width="52px" ref="TaskSaveFormRef">
        <!-- <el-form-item label="todoId">
          <el-input v-model="taskSaveForm.todoId" style="width: 90px;" disabled></el-input>
          <el-input v-model="taskSaveForm.todoName" style="width: 90px;" disabled></el-input>
          <el-input v-model="taskSaveForm.todoType" style="width: 30px;" disabled></el-input>
        </el-form-item> -->
        <el-form-item label="标题" prop="taskName">
          <el-input v-model="taskSaveForm.taskName">
            <template #append>
              <el-tooltip content="查看 Emoji" effect="blossomt" placement="top" :hide-after="0">
                <div class="emoji-link" @click="openExtenal('https://www.emojiall.com/zh-hans')">😉</div>
              </el-tooltip>
            </template></el-input>
        </el-form-item>
        <el-form-item label="内容">
          <el-input type="textarea" :rows="4" v-model="taskSaveForm.taskContent"></el-input>
        </el-form-item>
        <el-form-item label="截止至">
          <el-input v-model="taskSaveForm.deadLine" placeholder="如下午3点会议之间"></el-input>
        </el-form-item>
        <el-form-item label="开始于">
          <el-date-picker v-model="taskSaveForm.startTime" type="datetime" format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束于">
          <el-date-picker v-model="taskSaveForm.endTime" type="datetime" format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="颜色" style="width: auto;">
          <el-input v-model="taskSaveForm.color" style="width:200px;margin-right: 5px;"
            placeholder="推荐半透明色,更兼容暗黑模式"></el-input>
          <el-color-picker show-alpha v-model="taskSaveForm.color" :predefine="[
            'rgba(68, 173, 56, 0.7)',
            'rgba(186, 196, 44, 0.7)',
            'rgba(235, 205, 72, 0.7)',
            'rgba(232, 144, 144, 0.7)',
            'rgba(112, 145, 188, 0.7)',
            'rgba(157, 129, 216, 0.7)',
            'rgba(0, 0, 0, 0.65)',
          ]" />

          <el-tooltip content="颜色搭配参考" effect="blossomt" placement="top" :hide-after="0">
            <a href="https://colorhunt.co/" target="_blank" class="color-hunt iconbl bl-a-colorpalette-line"
              :style="{ color: taskSaveForm.color }"></a>
          </el-tooltip>
        </el-form-item>
        <el-form-item label="进度" style="width: auto;">
          <el-input-number v-model="taskSaveForm.process" :min="0" :max="100" style="width: 100%;"></el-input-number>
        </el-form-item>
      </el-form>
    </div>
    <div class="info-footer">
      <el-button type="danger" plain @click="delTaskBefore()">删除</el-button>
      <div>
        <el-button type="primary" :disabled="saveLoading" @click="save(TaskSaveFormRef)">
          <span class="iconbl bl-a-filechoose-line" /> 保存
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessageBox, FormInstance, FormRules } from 'element-plus'
import { isNotBlank } from '@renderer/assets/utils/obj'
import { taskInfoApi, addTaskApi, updTaskApi, delTaskApi, countTaskApi } from '@renderer/api/todo'
import { TaskInfo, TodoType } from './scripts/types'
import { openExtenal } from '@renderer/assets/utils/electron'

//#region ---------------------------------------- 修改任务 ----------------------------------------
const formLoading = ref(false)
const saveLoading = ref(false)
const TaskSaveFormRef = ref()
const taskSaveFormTitle = ref('')
const taskSaveForm = ref<TaskInfo>({
  id: '',
  todoId: '',
  todoName: '',
  todoType: 10,
  taskName: '',
  taskContent: '',
  deadLine: '',
  creTime: '',
  startTime: '',
  endTime: '',
  process: 0,
  color: '',
  taskStatus: 'WAITING',
  updTaskName: false,
  updTaskContent: false
})

const taskSaveFormRule = ref<FormRules<TaskInfo>>({
  todoId: [{ required: true, message: '事项ID为必填项', trigger: 'blur' }],
  todoType: [{ required: true, message: '任务为必填项', trigger: 'blur' }],
  taskName: [{ required: true, message: '任务标题为必填项', trigger: 'blur' }]
})

/**
 * 保存表单,内部决定是否存还是新增
 * @param formEl 
 */
const save = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid, _fields) => {
    if (valid) {
      if (isNotBlank(taskSaveForm.value.id)) {
        updTask(taskSaveForm.value)
      } else {
        addTaskApi(taskSaveForm.value).then(resp => {
          emits('saved', resp.data)
        })
      }
    }
  })
}

/**
 * 修改任务
 * @param data 
 */
const updTask = (data: any) => {
  let datas = { ...data, ...{ returnTasks: true } }
  updTaskApi(datas).then(resp => {
    emits('saved', resp.data)
  })
}

/**
 * 删除节点
 */
const delTaskBefore = () => {
  if (taskSaveForm.value.todoType === 20) {
    countTaskApi({ todoId: taskSaveForm.value.todoId }).then(resp => {
      if (resp.data == 1) {
        ElMessageBox.confirm(`当删除阶段性事项的最后一个任务时, 该事项也将一并删除, 是否确定删除?`, {
          confirmButtonText: '确定删除', cancelButtonText: '我再想想', type: 'info', draggable: true,
        }).then(() => {
          delTask()
        })
      } else {
        delTask()
      }
    })
  }
  else {
    delTask()
  }
}

const delTask = () => {
  delTaskApi(taskSaveForm.value).then(resp => emits('saved', resp.data))
}

/**
 * 回显数据
 * @param dialogType 
 * @param taskId 
 */
const reload = (dialogType: 'upd' | 'add', taskId?: string, todoId?: string, todoType?: TodoType) => {
  if (dialogType == 'upd') {
    taskSaveFormTitle.value = '修改任务'
    taskInfoApi({ id: taskId }).then(resp => {
      taskSaveForm.value = resp.data
    })
  } else {
    taskSaveFormTitle.value = '新增任务'
    taskSaveForm.value.todoId = todoId!
    taskSaveForm.value.todoType = todoType!
  }
}

defineExpose({ reload })
const emits = defineEmits(['saved'])
</script>

<style scoped lang="scss">
$height-title: 50px;
$height-footer: 50px;
$height-form: calc(100% - #{$height-title} - #{$height-footer});

.article-info-root {
  border-radius: 10px;

  .info-title-wrapper {
    @include box(100%, $height-title);
    @include flex(row, flex-start, center);
    border-bottom: 1px solid var(--el-border-color);

    .info-icon {
      @include box(50px, 100%);
      padding: 5px 0;
      text-align: center;
    }

    .info-title {
      @include font(16px);
      width: calc(100% - 50px - 50px);
      height: 100%;
      padding-top: 10px;
      color: var(--el-color-primary);
      padding-left: 10px;
    }
  }

  .info-form {
    @include box(100%, $height-form);
    padding: 10px 10px 0;

    :deep(.el-form--inline .el-form-item) {
      width: calc(100% - 10px);
      margin-right: 10px;
      margin-bottom: 10px;
    }

    .color-hunt {
      @include box(24px, 24px);
      line-height: 20px;
      font-size: 18px;
      padding: 1px 3px;
      margin-left: 5px;
      border: 1px solid var(--el-border-color);
      border-radius: 4px;
      text-decoration: none;

      &:hover {
        border: 1px solid var(--el-border-color-hover);
      }
    }
  }

  .info-footer {
    @include box(100%, $height-footer);
    @include flex(row, space-between, center);
    border-top: 1px solid var(--el-border-color);
    padding: 10px;
    text-align: right;

    .iconbl {
      font-size: 18px;
      margin-right: 5px;
    }
  }

  .emoji-link {
    cursor: pointer;
  }
}
</style>