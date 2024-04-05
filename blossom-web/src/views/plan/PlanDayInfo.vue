<template>
  <div class="day-info-root">
    <div class="info-form">
      <el-form ref="DayFormRef" :model="dayForm" :rules="dayFormRule" label-position="top" label-width="60px" size="small">
        <el-form-item prop="title">
          <el-input v-model="dayForm.title" placeholder="计划标题" style="width: calc(100% - 30px)">
            <template #prefix>
              <el-icon size="15">
                <Document />
              </el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item>
          <el-input type="textarea" :rows="5" v-model="dayForm.content" placeholder="计划内容" resize="none" />
        </el-form-item>

        <el-form-item label="计划日期" prop="planDate">
          <el-date-picker
            v-model="dayForm.planDate"
            type="date"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
            :editable="false" />
        </el-form-item>
        <el-form-item label="开始时间 / 结束时间" required>
          <el-form-item prop="planStartTime">
            <el-time-select
              v-model="dayForm.planStartTime"
              :max-time="dayForm.planEndTime"
              :editable="false"
              placeholder="开始日期"
              start="00:00"
              step="00:15"
              end="23:59"
              style="width: 155px; margin-right: 18px" />
          </el-form-item>
          <el-form-item prop="planEndTime">
            <el-time-select
              v-model="dayForm.planEndTime"
              :min-time="dayForm.planStartTime"
              :editable="false"
              placeholder="结束日期"
              start="00:00"
              step="00:15"
              end="23:59"
              style="width: 155px" />
          </el-form-item>
        </el-form-item>

        <el-form-item>
          <el-checkbox v-model="dayForm.allDay" label="全天" style="margin-right: 20px" @change="allDayChange" />
          <el-checkbox v-model="dayForm.repeat" label="重复" style="margin-right: 20px" @change="repeatChange" />
          <div style="margin-right: 10px">重复天数</div>
          <el-input-number v-model="dayForm.repeatDay" :min="1" :disabled="!dayForm.repeat" />
        </el-form-item>

        <bl-col>
          <bl-row style="font-size: 12px" :style="{ color: dayForm.color }">
            颜色
            <span class="iconbl bl-a-colorpalette-line" style="font-size: 13px; padding-left: 5px"></span>
          </bl-row>
          <bl-row class="color-container">
            <div v-for="color in colors" :class="['color', color]" @click="clickColor(color)"></div>
          </bl-row>
        </bl-col>
      </el-form>
    </div>
    <el-button color="#474747" @click="saveDay(DayFormRef)" style="width: 100%">
      <span class="iconbl bl-a-templateadd-line" style="margin-right: 10px" />保 存
    </el-button>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { Document } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { planAddDayApi } from '@/api/plan'

const colors = ['purple', 'red', 'yellow', 'blue', 'green', 'gray']

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
      planAddDayApi(dayForm.value).then((_resp) => {
        emits('saved')
      })
    }
  })
}

const emits = defineEmits(['saved'])
</script>

<style scoped lang="scss">
@import url('./PlanColor.scss');

.day-info-root {
  padding-top: 20px;

  .color-container {
    align-content: flex-start;
    flex-wrap: wrap;
    overflow: scroll;
    overflow-y: scroll;
    padding: 10px 10px 8px 0;

    .color {
      @include box(20px, 20px);
      @include font(11px, 300);
      color: #fff;
      margin: 0 5px;
      border-radius: 4px;
      transition: transform 0.1s;
      cursor: pointer;

      &:active {
        transform: scale(1.2);
      }
    }
  }
}
</style>
