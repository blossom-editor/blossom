<template>
  <div class="plan-daily-root">
    <bl-col class="daily-container">
      <bl-row just="flex-end" class="title">日常安排</bl-row>
      <bl-col :class="['daily ', daily.current ? 'current' : '']" v-for="daily in dailys" align="flex-start">
        <bl-row height="20px" class="time" style="">{{ daily.planStartTime + ' ~ ' + daily.planEndTime }}</bl-row>
        <bl-row height="calc(100% - 20px)">
          <bl-col align="flex-start" width="135px" class="content">
            <span>
              {{ daily.content }}
            </span>
          </bl-col>
          <bl-col width="70px">
            <img :src="daily.img" />
          </bl-col>
          <div class="del"></div>
          <span class="iconbl bl-a-closeline-line" @click="delDaily(daily.id)"></span>
        </bl-row>
      </bl-col>

      <bl-col class="add-daily" just="center" @click="handleShowPlanAddDialog">
        <span class="iconbl bl-a-pageadd-line"></span>
      </bl-col>
    </bl-col>
  </div>

  <el-dialog
    v-model="isShowPlanAddDialog"
    width="400"
    top="60px"
    style="margin-left: 300px"
    :append-to-body="true"
    :destroy-on-close="true"
    :close-on-click-modal="true"
    draggable>
    <PlanDailyInfo @saved="savedCallback"></PlanDailyInfo>
  </el-dialog>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { planListDailyApi, planDelApi } from '@renderer/api/plan'
import { isNull } from '@renderer/assets/utils/obj'
import PlanDailyInfo from './PlanDailyInfo.vue'

onMounted(() => {
  getPlanDaily()
})

interface PlanDaily {
  id: number
  planStartTime: string
  planEndTime: string
  content: string
  img: string
  current: boolean
}
const dailys = ref<PlanDaily[]>([])

const getPlanDaily = () => {
  planListDailyApi().then((resp) => {
    dailys.value = resp.data.map((plan) => {
      if (isNull(plan.img)) {
        plan.img = getImg('cat-smile.png')
      } else if (!(plan.img as string).startsWith('http')) {
        plan.img = getImg(plan.img)
      }
      return plan
    })
  })
}

const getImg = (img: string) => {
  return new URL(`../../assets/imgs/plan/${img}`, import.meta.url).href
}

//#region ----------------------------------------< 新增删除 >-------------------------------------
const isShowPlanAddDialog = ref(false)

const handleShowPlanAddDialog = () => {
  isShowPlanAddDialog.value = true
}

const savedCallback = () => {
  getPlanDaily()
  isShowPlanAddDialog.value = false
}

const delDaily = (id: number) => {
  planDelApi({ id: id }).then((_resp) => {
    getPlanDaily()
  })
}
//#endregion
</script>

<style scoped lang="scss">
.plan-daily-root {
  @include box(100%, 100%);
  @include themeBrightness();

  .daily-container {
    @include box(100%, 100%);
    overflow: scroll;
    overflow-y: scroll;

    .title {
      width: 100%;
      font-weight: 300;
      padding-right: 20px;
      text-align: right;
      color: #8a8a8a;
    }

    .daily,
    .add-daily {
      margin: 10px 15px 10px 20px;
      transition: 0.3s;
      border-radius: 6px;
      padding: 5px;
      overflow: hidden;

      &:hover {
        @include themeShadow(3px 3px 8px 1px #9e9e9e, 3px 3px 8px 1px #000000);
      }
    }

    .daily {
      @include box(205px, 100px, 205px, 205px, 100px, 100px);
      @include themeShadow(2px 2px 8px 1px #dadada, 2px 2px 8px 1px #121212);
      position: relative;

      &:hover {
        .del,
        .bl-a-closeline-line {
          opacity: 1;
        }
      }

      .time {
        @include font(12px, 500);
        padding: 0 5px;
      }

      .content {
        @include font(13px, 300);
        padding-left: 5px;
        color: #8a8a8a;
        text-align: left;
        white-space: normal;
        word-wrap: break-word;
        overflow: scroll;
        overflow-y: scroll;
      }

      .del {
        @include absolute(0, 0);
        border-top: 30px solid var(--el-color-primary);
        border-left: 30px solid transparent;
        transition: 0.3s;
        opacity: 0;
      }

      .bl-a-closeline-line {
        @include font(11px, 500);
        @include absolute(3px, 3px);
        color: #fff;
        transition: 0.3s;
        opacity: 0;
        cursor: pointer;
      }

      img {
        height: 70px;
        width: 70px;
        border-radius: 5px;
      }

      ::-webkit-scrollbar {
        width: 0;
        height: 0;
      }
    }

    .current {
      @include themeShadow(2px 2px 8px 1px #9e9e9e, 2px 2px 8px 1px #000000);
      background-color: var(--el-color-primary);

      .time,
      .content {
        color: #fff;
      }
    }

    .add-daily {
      @include box(205px, 50px, 205px, 205px, 50px, 50px);
      @include themeShadow(2px 2px 8px 1px #ececec, 2px 2px 8px 1px #121212);
      border: 1px dashed #9e9e9e;
      color: #9e9e9e;
      cursor: pointer;
    }
  }
}
</style>
