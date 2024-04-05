<template>
  <div class="article-info-root">
    <!-- 标题 -->
    <div class="info-title">
      <div class="iconbl bl-a-labellist-line"></div>
      {{ taskSaveFormTitle }}
    </div>

    <div v-loading="formLoading" class="info-form">
      <el-form :inline="true" :model="taskSaveForm" :rules="taskSaveFormRule" label-width="52px" ref="TaskSaveFormRef">
        <el-form-item label="标题" prop="taskName">
          <el-input v-model="taskSaveForm.taskName">
            <template #append>
              <el-tooltip content="查看 Emoji" effect="light" placement="top" :hide-after="0">
                <div class="emoji-link" @click="openExtenal('https://www.emojiall.com/zh-hans')">😉</div>
              </el-tooltip>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="内容">
          <el-input type="textarea" :rows="4" v-model="taskSaveForm.taskContent"></el-input>
        </el-form-item>
        <el-form-item label="标签">
          <div class="info-tags-container">
            <el-popover placement="top-start" :width="262" trigger="click" :show-after="0" :hide-after="0" :popper-style="{ marginRight: '10px' }">
              <template #reference>
                <el-button><span class="iconbl bl-tally-line"></span></el-button>
              </template>
              <div class="quick-tags-container">
                <span v-if="quickTags.size === 0" class="quick-tags-placeholder">无标签</span>
                <span
                  v-else
                  v-for="quickTag in quickTags.values()"
                  :key="quickTag.name"
                  :class="['quick-tag', quickTag.selected ? 'selected' : '']"
                  @click="handleQuickTagClick(quickTag)">
                  {{ quickTag.name }}
                </span>
              </div>
            </el-popover>
            <el-input
              v-if="isShowTagInput"
              ref="TagInputRef"
              style="width: 75px"
              v-model="tagInputValue"
              @keyup.enter="blurTagInput"
              @blur="blurTagInput" />
            <el-button v-else style="width: 75px" @click="showInput"> + 标签 </el-button>
            <el-tag v-for="tag in taskSaveForm?.taskTags" :key="tag" :disable-transitions="false" @close="handleTagClose(tag)" closable>
              {{ tag }}
            </el-tag>
          </div>
        </el-form-item>
        <el-form-item label="截止至">
          <el-input v-model="taskSaveForm.deadLine" placeholder="如下午3点会议之间"></el-input>
        </el-form-item>
        <el-form-item label="开始于" v-if="infoType == 'upd'">
          <el-date-picker
            v-model="taskSaveForm.startTime"
            type="datetime"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束于" v-if="infoType == 'upd'">
          <el-date-picker
            v-model="taskSaveForm.endTime"
            type="datetime"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%" />
        </el-form-item>
        <el-form-item label="颜色" style="width: auto">
          <el-input v-model="taskSaveForm.color" style="width: 200px; margin-right: 5px" placeholder="推荐半透明色,更兼容暗黑模式"></el-input>
          <el-color-picker
            show-alpha
            v-model="taskSaveForm.color"
            :predefine="[
              'rgba(68, 173, 56, 0.7)',
              'rgba(186, 196, 44, 0.7)',
              'rgba(235, 205, 72, 0.7)',
              'rgba(232, 144, 144, 0.7)',
              'rgba(112, 145, 188, 0.7)',
              'rgba(157, 129, 216, 0.7)',
              'rgba(0, 0, 0, 0.65)'
            ]" />

          <el-tooltip content="颜色搭配参考" effect="light" placement="top" :hide-after="0">
            <a
              href="https://colorhunt.co/"
              target="_blank"
              class="color-hunt iconbl bl-a-colorpalette-line"
              :style="{ color: taskSaveForm.color }"></a>
          </el-tooltip>
        </el-form-item>
        <el-form-item label="进度" v-if="infoType == 'upd'" style="width: auto">
          <el-input-number v-model="taskSaveForm.process" :min="0" :max="100" :step="5"></el-input-number>
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
import { nextTick, ref } from 'vue'
import { ElInput, ElMessageBox, FormInstance, FormRules } from 'element-plus'
import { isBlank, isNotBlank, isNull } from '@renderer/assets/utils/obj'
import { taskInfoApi, addTaskApi, updTaskApi, delTaskApi, countTaskApi, taskTagsApi } from '@renderer/api/todo'
import { TaskInfo, TodoType } from './scripts/types'
import { openExtenal } from '@renderer/assets/utils/electron'

//#region --------------------------------------------------< 修改任务 >--------------------------------------------------
const infoType = ref<'upd' | 'add'>('add')
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
})

const taskSaveFormRule = ref<FormRules<TaskInfo>>({
  todoId: [{ required: true, message: '事项ID为必填项', trigger: 'blur' }],
  todoType: [{ required: true, message: '任务为必填项', trigger: 'blur' }],
  taskName: [{ required: true, message: '', trigger: 'blur' }]
})

/**
 * 保存表单,内部决定是否存还是新增
 * @param formEl
 */
const save = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid, _fields) => {
    if (taskSaveForm.value.color == null) {
      taskSaveForm.value.color = ''
    }
    if (valid) {
      saveLoading.value = true
      if (isNotBlank(taskSaveForm.value.id)) {
        let datas = { ...taskSaveForm.value, ...{ returnTasks: true } }
        updTaskApi(datas)
          .then((resp) => {
            emits('saved', resp.data)
          })
          .catch(() => (saveLoading.value = false))
      } else {
        addTaskApi(taskSaveForm.value)
          .then((resp) => {
            emits('saved', resp.data)
          })
          .catch(() => (saveLoading.value = false))
      }
    }
  })
}

/**
 * 删除节点前的检查, 会从服务端获取当前事项是否最后一个任务
 */
const delTaskBefore = () => {
  if (taskSaveForm.value.todoType === 20) {
    countTaskApi({ todoId: taskSaveForm.value.todoId }).then((resp) => {
      if (resp.data == 1) {
        ElMessageBox.confirm(`当删除阶段性事项的最后一个任务时, 该事项也将一并删除, 是否确定删除?`, {
          confirmButtonText: '确定删除',
          cancelButtonText: '我再想想',
          type: 'info',
          draggable: true
        }).then(() => {
          delTask()
        })
      } else {
        delTask()
      }
    })
  } else {
    delTask()
  }
}

const delTask = () => {
  delTaskApi(taskSaveForm.value).then((resp) => emits('saved', resp.data))
}

/**
 * 回显数据
 * @param dialogType 弹框的类型
 * @param taskId 任务的ID
 * @param todoId 事项的ID [仅新增]
 * @param todoType 事项的类型 [仅新增]
 */
const reload = (dialogType: 'upd' | 'add', taskId?: string, todoId?: string, todoType?: TodoType) => {
  infoType.value = dialogType
  if (dialogType == 'upd') {
    taskSaveFormTitle.value = '修改任务'
    taskInfoApi({ id: taskId }).then((resp) => {
      taskSaveForm.value = resp.data
      initQuickTags(resp.data.todoType, resp.data.todoId)
    })
  } else {
    taskSaveFormTitle.value = '新增任务'
    taskSaveForm.value.todoId = todoId!
    taskSaveForm.value.todoType = todoType!
    initQuickTags(todoType!, todoId!)
  }
}

//#endregion

//#region --------------------------------------------------< 标签 >--------------------------------------------------
const TagInputRef = ref<InstanceType<typeof ElInput>>()
const tagInputValue = ref('')
const isShowTagInput = ref(false)
const quickTags = ref<Map<string, QuickTag>>(new Map())

/**
 * 初始化标签集合
 * @param todoType 事项的类型
 * @param todoId   事项的ID, 为阶段性事项[20]时必填
 */
const initQuickTags = (todoType: TodoType, todoId: string) => {
  taskTagsApi({ todoType: todoType, todoId: todoId }).then((resp) => {
    if (isNull(resp.data)) {
      return
    }
    for (let i = 0; i < resp.data.length; i++) {
      const tag = resp.data[i]
      quickTags.value.set(tag, {
        name: tag,
        selected: taskSaveForm.value.taskTags.includes(tag)
      })
    }
  })
}

const handleQuickTagClick = (tag: QuickTag) => {
  if (tag.selected) {
    taskSaveForm.value?.taskTags.splice(taskSaveForm.value.taskTags.indexOf(tag.name), 1)
    quickTags.value.set(tag.name, { name: tag.name, selected: false })
  } else {
    taskSaveForm.value.taskTags.push(tag.name)
    quickTags.value.set(tag.name, { name: tag.name, selected: true })
  }
}

const handleTagClose = (tag: string) => {
  taskSaveForm.value.taskTags.splice(taskSaveForm.value.taskTags.indexOf(tag), 1)
  quickTags.value.set(tag, { name: tag, selected: false })
}

const showInput = () => {
  isShowTagInput.value = true
  nextTick(() => {
    TagInputRef.value!.input!.focus()
  })
}

const blurTagInput = () => {
  if (isBlank(tagInputValue.value)) {
    return
  }
  if (taskSaveForm.value.taskTags.indexOf(tagInputValue.value) == -1) {
    taskSaveForm.value.taskTags.push(tagInputValue.value)
  }
  isShowTagInput.value = false
  tagInputValue.value = ''
}

//#endregion

defineExpose({ reload })
const emits = defineEmits(['saved'])
</script>

<style scoped lang="scss">
@import '@renderer/assets/styles/bl-dialog-info';

.article-info-root {
  border-radius: 10px;

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

.info-tags-container {
  text-align: left;
  overflow-y: scroll;

  & > span,
  button {
    margin: 3px 3px;
  }

  .el-input {
    margin: 3px 3px;
  }
}
</style>
