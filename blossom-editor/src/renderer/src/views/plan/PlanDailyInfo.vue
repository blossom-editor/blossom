<template>
  <div class="daily-info-root">
    <!-- 标题 -->
    <div class="info-title">
      <div class="iconbl bl-a-pagelevel-line"></div>
      <div>新增日常计划</div>
    </div>

    <div class="info-form">
      <el-form ref="DailyFormRef" :model="dailyForm" :rules="dailyFormRule" label-position="top" label-width="60px">
        <el-form-item label="开始时间 / 结束时间" required>
          <el-form-item prop="planStartTime">
            <el-time-select
              v-model="dailyForm.planStartTime"
              :max-time="dailyForm.planEndTime"
              placeholder="开始日期"
              start="00:00"
              step="00:15"
              end="23:59"
              style="width: 180px; margin-right: 18px" />
          </el-form-item>
          <el-form-item prop="planEndTime">
            <el-time-select
              v-model="dailyForm.planEndTime"
              :min-time="dailyForm.planStartTime"
              placeholder="结束日期"
              start="00:00"
              step="00:15"
              end="23:59"
              style="width: 180px" />
          </el-form-item>
        </el-form-item>

        <el-form-item label="计划内容">
          <el-input type="textarea" v-model="dailyForm.content" placeholder="计划内容" />
        </el-form-item>

        <el-form-item label="图片">
          <el-input v-model="dailyForm.img" placeholder="可自定义图片地址或选择内置图片" @input="useOutsideImg">
            <template #prefix>
              <el-icon size="15">
                <Picture />
              </el-icon>
            </template>
          </el-input>
        </el-form-item>

        <bl-row height="150px">
          <bl-col align="flex-start">
            <div style="font-size: 12px">预览</div>
            <bl-col class="daily" align="flex-start">
              <bl-row class="time">{{ dailyForm.planStartTime + ' ~ ' + dailyForm.planEndTime }}</bl-row>
              <bl-row height="calc(100% - 20px)">
                <div class="content">
                  {{ dailyForm.content }}
                </div>
                <div width="70px">
                  <img :src="dailyForm.imgPreview" />
                </div>
              </bl-row>
            </bl-col>
          </bl-col>

          <bl-col align="flex-start">
            <div style="font-size: 12px">内置图片</div>
            <bl-row class="img-container">
              <div v-for="img in imgs" style="" @click="useInnerImg(img.name, img.url)">
                <img :src="img.url" />
              </div>
            </bl-row>
          </bl-col>
        </bl-row>
      </el-form>
    </div>

    <div class="info-footer">
      <div></div>
      <el-button size="default" type="primary" :disabled="saveLoading" @click="saveDaily(DailyFormRef)">
        <span class="iconbl bl-a-pageadd-line" />保存
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { Picture } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { planAddDailyApi } from '@renderer/api/plan'

const getImg = (img: string): string => {
  return new URL(`../../assets/imgs/plan/${img}`, import.meta.url).href
}

interface DailyForm {
  content: string
  planStartTime: string
  planEndTime: string
  img: string
  imgPreview: string
}
const saveLoading = ref(false)
const DailyFormRef = ref<FormInstance>()
const dailyForm = ref<DailyForm>({
  content: '',
  planStartTime: '',
  planEndTime: '',
  img: 'base-awesome.png',
  imgPreview: getImg('base-awesome.png')
})

const dailyFormRule = reactive<FormRules<DailyForm>>({
  planStartTime: [{ required: true, message: '请填写开始时间', trigger: 'change' }],
  planEndTime: [{ required: true, message: '请填写结束时间', trigger: 'change' }]
})

const saveDaily = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid, _fields) => {
    if (valid) {
      saveLoading.value = true
      planAddDailyApi(dailyForm.value)
        .then((_resp) => {
          emits('saved')
        })
        .catch(() => (saveLoading.value = false))
    }
  })
}

const imgs = [
  { name: 'base-awesome.png', url: getImg('base-awesome.png') },
  { name: 'base-celebrate.png', url: getImg('base-celebrate.png') },
  { name: 'base-cool.png', url: getImg('base-cool.png') },
  { name: 'base-learning.png', url: getImg('base-learning.png') },
  { name: 'cat-kiss.png', url: getImg('cat-kiss.png') },
  { name: 'cat-nice.png', url: getImg('cat-nice.png') },
  { name: 'cat-smile.png', url: getImg('cat-smile.png') },
  { name: 'cat.png', url: getImg('cat.png') },
  { name: 'coffee.png', url: getImg('coffee.png') },
  { name: 'juice.png', url: getImg('juice.png') },
  { name: 'beer.png', url: getImg('beer.png') }
]

/**
 * 将图片
 * @param img
 */
const useInnerImg = (imgName: string, imgUrl: string) => {
  dailyForm.value.img = imgName
  dailyForm.value.imgPreview = imgUrl
}

const useOutsideImg = (img: string) => {
  dailyForm.value.imgPreview = img
}

const emits = defineEmits(['saved'])
</script>

<style scoped lang="scss">
@import '@renderer/assets/styles/bl-dialog-info';

.daily-info-root {
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
    @include themeShadow(2px 2px 8px 1px #dadada, 2px 2px 8px 1px #121212);
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
      color: #8a8a8a;
      text-align: left;
      white-space: normal;
      word-wrap: break-word;
      overflow: scroll;
      overflow-y: scroll
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
    overflow-y: scroll;

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
