<template>
  <div class="daily-info-root">

    <!-- 标题 -->
    <div class="info-title-wrapper">
      <bl-row class="info-title">
        <div class="iconbl bl-a-pagelevel-line" style="font-size: 25px;margin-right: 10px;"></div>
        <div>新增日常计划</div>
      </bl-row>
    </div>

    <div class="info-form">
      <el-form ref="DailyFormRef" :model="dailyForm" :rules="dailyFormRule" label-position="top" label-width="60px">
        <el-form-item label="开始时间 / 结束时间" required>
          <el-form-item prop="planStartTime">
            <el-time-select v-model="dailyForm.planStartTime" :max-time="dailyForm.planEndTime" placeholder="开始日期"
              start="00:00" step="00:15" end="23:59" style="width: 180px;margin-right: 18px;" />
          </el-form-item>
          <el-form-item prop="planEndTime">
            <el-time-select v-model="dailyForm.planEndTime" :min-time="dailyForm.planStartTime" placeholder="结束日期"
              start="00:00" step="00:15" end="23:59" style="width: 180px;" />
          </el-form-item>
        </el-form-item>

        <el-form-item label="计划内容">
          <el-input type="textarea" v-model="dailyForm.content" placeholder="计划内容" />
        </el-form-item>

        <el-form-item label="图片">
          <el-input v-model="dailyForm.img" placeholder="可自定义图片地址或选择内置图片">
            <template #prefix>
              <el-icon size="15">
                <Picture />
              </el-icon>
            </template>
          </el-input>
        </el-form-item>

        <bl-row height="150px">
          <bl-col align="flex-start">
            <div style="font-size: 12px;">预览</div>
            <bl-col class="daily" align="flex-start">
              <bl-row class="time">{{ dailyForm.planStartTime + ' ~ ' + dailyForm.planEndTime }}</bl-row>
              <bl-row height="calc(100% - 20px)">
                <div class="content">
                  {{ dailyForm.content }}
                </div>
                <div width="70px">
                  <img :src="imgPreview">
                </div>
              </bl-row>
            </bl-col>
          </bl-col>

          <bl-col align="flex-start">
            <div style="font-size: 12px;">内置图片</div>
            <bl-row class="img-container">
              <div v-for="img in imgs" style="" @click="dailyForm.img = img.substring(img.lastIndexOf('/') + 1)">
                <img :src="img">
              </div>
            </bl-row>
          </bl-col>
        </bl-row>
      </el-form>
    </div>

    <div class="info-footer">
      <div></div>
      <el-button size="default" type="primary" @click="saveDaily(DailyFormRef)">
        <span class="iconbl bl-a-pageadd-line" />保存
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { Picture } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { planAddDailyApi } from '@renderer/api/plan'

const getImg = (img: string) => {
  return new URL(`../../assets/imgs/plan/${img}`, import.meta.url).href
}

const imgPreview = computed(() => {
  if (dailyForm.value.img.startsWith('http')) {
    return dailyForm.value.img
  }
  return getImg(dailyForm.value.img)
})

interface DailyForm { content: string, planStartTime: string, planEndTime: string, img: string }
const DailyFormRef = ref<FormInstance>()
const dailyForm = ref<DailyForm>({
  content: '',
  planStartTime: '',
  planEndTime: '',
  img: 'base-awesome.png'
})

const dailyFormRule = reactive<FormRules<DailyForm>>({
  planStartTime: [
    { required: true, message: '请填写开始时间', trigger: 'change' }
  ],
  planEndTime: [
    { required: true, message: '请填写结束时间', trigger: 'change' }
  ]
})

const saveDaily = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid, _fields) => {
    if (valid) {
      planAddDailyApi(dailyForm.value).then(_resp => {
        emits('saved')
      })
    }
  })
}

const imgs = [
  getImg('base-awesome.png'),
  getImg('base-cool.png'),
  getImg('base-cry.png'),
  getImg('base-learning.png'),
  getImg('base-seesee.png'),
  getImg('dog.png'),
  getImg('cat-kiss.png'),
  getImg('cat-nice.png'),
  getImg('cat-shock.png'),
  getImg('cat-smile.png'),
  getImg('cat.png'),
  getImg('computer.png'),
  getImg('coffee.png'),
  getImg('juice.png'),
  getImg('beer.png'),
  getImg('ball.png'),
  getImg('bike.png'),
  getImg('crossbones.png'),
  getImg('hand-ok.png'),
  getImg('hand-write.png'),
  getImg('love.png'),
  getImg('sleep.png')
]

const emits = defineEmits(['saved'])
</script>

<style scoped lang="scss">
.daily-info-root {
  $height-title: 50px;
  $height-footer: 50px;
  $height-form: calc(100% - #{$height-title} - #{$height-footer});

  .info-title-wrapper {
    @include box(100%, $height-title);
    @include flex(row, flex-start, center);
    border-bottom: 1px solid var(--el-border-color);

    .info-title {
      @include font(16px);
      width: calc(100% - 50px - 50px);
      height: 100%;
      color: var(--el-color-primary);
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
      padding-left: 10px;
    }

    .info-title-close {
      width: 50px;
      font-size: 40px;
      color: var(--el-border-color);
      text-align: center;
    }
  }

  .info-form {
    @include box(100%, $height-form);
    padding: 10px;

    :deep(.el-form--inline .el-form-item) {
      margin-right: 20px;
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

  .daily {
    @include box(220px, 130px);
    margin: 10px 5px 10px 5px;
    transition: 0.3s;
    border-radius: 6px;
    padding: 5px;
    overflow: hidden;
  }

  .daily {
    @include box(205px, 100px, 205px, 205px, 100px, 100px);
    @include themeShadow(2px 2px 8px 1px #DADADA, 2px 2px 8px 1px #121212);
    position: relative;

    .time {
      @include font(12px, 500);
      height: 20px;
      padding: 0 5px;
    }

    .content {
      @include box(135px, 70px);
      @include font(13px, 300);
      padding-left: 5px;
      color: #8A8A8A;
      text-align: left;
      white-space: normal;
      word-wrap: break-word;
      overflow: scroll;
      overflow-y: overlay;
    }

    img {
      height: 23px;
      width: 23px;
      height: 70px;
      width: 70px;
      border-radius: 5px;
    }
  }

  .img-container {
    padding: 5px;
    align-content: flex-start;
    flex-wrap: wrap;
    overflow: scroll;
    overflow-y: overlay;

    div {
      margin: 1px;
      cursor: pointer;

      img {
        height: 23px;
        width: 23px;
      }
    }

  }
}
</style>