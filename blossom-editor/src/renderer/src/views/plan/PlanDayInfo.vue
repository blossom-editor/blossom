<template>
  <div class="day-info-root">
    <!-- 标题 -->
    <div class="info-title">
      <div class="iconbl bl-a-templatelist2-line"></div>
      <div>新增计划</div>
    </div>

    <div class="info-form">
      <el-form ref="DayFormRef" :model="dayForm" :rules="dayFormRule" label-position="top" label-width="60px">
        <el-form-item label="计划日期" prop="planDate">
          <el-date-picker v-model="dayForm.planDate" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>

        <el-form-item label="开始时间 / 结束时间" required>
          <el-form-item prop="planStartTime">
            <el-time-select
              v-model="dayForm.planStartTime"
              :max-time="dayForm.planEndTime"
              placeholder="开始时间"
              start="00:00"
              step="00:15"
              end="23:59"
              style="width: 180px; margin-right: 18px" />
          </el-form-item>
          <el-form-item prop="planEndTime">
            <el-time-select
              v-model="dayForm.planEndTime"
              :min-time="dayForm.planStartTime"
              placeholder="结束时间"
              start="00:00"
              step="00:15"
              end="23:59"
              style="width: 180px" />
          </el-form-item>
        </el-form-item>

        <el-form-item>
          <el-checkbox v-model="dayForm.allDay" label="全天" style="margin-right: 50px" @change="allDayChange" />
          <el-checkbox v-model="dayForm.repeat" label="重复" style="margin-right: 60px" @change="repeatChange" />
          <div style="margin-right: 10px">重复天数</div>
          <el-input-number v-model="dayForm.repeatDay" :min="1" :disabled="!dayForm.repeat" />
        </el-form-item>

        <el-form-item label="计划标题" prop="title">
          <el-input v-model="dayForm.title" placeholder="计划标题">
            <template #prefix>
              <el-icon size="15">
                <Document />
              </el-icon>
            </template>
            <template #append>
              <el-tooltip content="查看 Emoji" effect="light" placement="top" :hide-after="0">
                <div style="cursor: pointer; font-size: 15px" @click="openExtenal('https://www.emojiall.com/zh-hans')">😉</div>
              </el-tooltip>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="计划内容">
          <el-input type="textarea" :rows="5" v-model="dayForm.content" placeholder="计划内容" />
        </el-form-item>

        <bl-col>
          <bl-row style="font-size: 12px">
            颜色
            <span class="iconbl bl-a-colorpalette-line" style="font-size: 13px; padding-left: 5px"></span>
          </bl-row>
          <bl-row class="color-container">
            <bl-row
              v-for="color in colors"
              width="50px"
              height="25px"
              just="center"
              :class="['color', color, dayForm.color == color ? 'selected' : '']"
              @click="clickColor(color)">
              <span>{{ color }}</span>
            </bl-row>
          </bl-row>
        </bl-col>
      </el-form>
    </div>

    <div class="info-footer">
      <div></div>
      <el-button size="default" type="primary" :disabled="saveLoading" @click="saveDay(DayFormRef)">
        <span class="iconbl bl-a-templateadd-line" />保存
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { Document } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { planAddDayApi } from '@renderer/api/plan'
import { openExtenal } from '@renderer/assets/utils/electron'

const colors = ['purple', 'red', 'yellow', 'blue', 'green', 'gray']

const saveLoading = ref(false)
interface DayForm {
  title: string
  content: string
  planDate: string
  planStartTime: string
  planEndTime: string
  allDay: boolean
  repeat: false
  repeatDay: number
  color: string
}
const DayFormRef = ref<FormInstance>()
const dayForm = ref<DayForm>({
  title: '',
  content: '',
  planDate: '',
  planStartTime: '',
  planEndTime: '',
  allDay: false,
  repeat: false,
  repeatDay: 1,
  color: 'purple'
})

const dayFormRule = reactive<FormRules<DayForm>>({
  planDate: [{ required: true, message: '请填写日期', trigger: 'change' }],
  planStartTime: [{ required: true, message: '请填写开始时间', trigger: 'change' }],
  planEndTime: [{ required: true, message: '请填写结束时间', trigger: 'change' }],
  title: [{ required: true, message: '请填写计划标题', trigger: 'change' }]
})

const allDayChange = (allDay: boolean) => {
  if (allDay) {
    dayForm.value.planStartTime = '00:00'
    dayForm.value.planEndTime = '23:59'
  } else {
    dayForm.value.planStartTime = ''
    dayForm.value.planEndTime = ''
  }
}

const repeatChange = (repeat: boolean) => {
  if (!repeat) {
    dayForm.value.repeatDay = 1
  }
}

const clickColor = (color: string) => {
  dayForm.value.color = color
}

const saveDay = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid, _fields) => {
    if (valid) {
      saveLoading.value = true
      planAddDayApi(dayForm.value)
        .then((_resp) => {
          emits('saved')
        })
        .catch(() => (saveLoading.value = false))
    }
  })
}

const emits = defineEmits(['saved'])

const setPlanDate = (ymd: string) => {
  dayForm.value.planDate = ymd + ' 00:00:00'
}

defineExpose({
  setPlanDate
})
</script>

<style scoped lang="scss">
@import '@renderer/assets/styles/bl-dialog-info';

.day-info-root {
  .color-container {
    align-content: flex-start;
    flex-wrap: wrap;
    overflow: scroll;
    overflow-y: scroll;
    padding: 10px;

    .color {
      @include font(11px, 300);
      color: #fff;
      margin: 0 3px;
      border-radius: 5px;
      transition: box-shadow 0.2s;
      @include themeBrightness(100%, 80%);
      cursor: pointer;
    }

    .selected {
      @include themeShadow(0 0 5px 1px #454545, 0 0 8px 2px #000000);
      @include themeBrightness(100%, 100%);
    }
  }
}
</style>
