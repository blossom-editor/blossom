<template>
  <div class="plan-root">
    <bl-row just="space-between" class="workbench" height="45px">
      <div class="month">{{ calendarDate.getMonth() + 1 }}月</div>
      <el-button-group size="small">
        <el-button @click="selectDate('prev-month')">上月</el-button>
        <el-button @click="selectDate('today')">今日</el-button>
        <el-button @click="selectDate('next-month')">下月</el-button>
      </el-button-group>
    </bl-row>
    <el-calendar class="plan-calendar" v-model="calendarDate" ref="CalendarRef">
      <template #header="{ date }"><div></div></template>
      <template #date-cell="{ data }">
        <div class="date-title">
          <span>{{ data.day.split('-').slice(2).join('-') }}</span>
        </div>
        <div class="plan-group">
          <div v-for="(plan, index) in planDays[data.day + ' 00:00:00']" :key="plan.id" @click="showUpdForm(plan)">
            <div :class="'plan-line ' + plan.color + ' ' + plan.position + ' ' + plan.hl" :style="{ top: index * 21 + 'px' }">
              <div v-if="plan.position == 'head' || plan.position == 'all'" class="plan-title">
                {{ plan.title }}
              </div>
            </div>
          </div>
        </div>
      </template>
    </el-calendar>
    <!--  -->
    <div class="module-workbench" @click="showAddForm"><span class="iconbl bl-a-addline-line"></span></div>
  </div>

  <!-- 新增页面 -->
  <el-drawer v-model="isShowAddForm" class="center-drawer" direction="btt" :with-header="true" :destroy-on-close="true" size="470px">
    <PlanDayInfo ref="PlanDayInfoRef" @saved="savedCallback"></PlanDayInfo>
  </el-drawer>

  <!-- 修改页面 -->
  <el-drawer
    v-model="isShowUpdForm"
    class="center-drawer"
    direction="btt"
    :close-on-click-modal="true"
    :with-header="true"
    :destroy-on-close="true"
    size="270px">
    <div class="detail">
      <el-input size="small" style="width: calc(100% - 30px); margin-bottom: 18px" v-model="curPlan.title" placeholder="计划 标题">
        <template #prefix>
          <el-icon size="15">
            <Document />
          </el-icon>
        </template>
      </el-input>
      <el-input size="small" type="textarea" placeholder="Todo 内容" v-model="curPlan.content" resize="none" :rows="4"></el-input>
      <div class="times">
        <bl-row style="margin-bottom: 5px"> <span class="iconbl bl-date-line"></span> {{ curPlan.planDate.substring(0, 10) }} </bl-row>
        <bl-row> <span class="iconbl bl-a-clock3-line"></span> {{ curPlan.planStartTime }} - {{ curPlan.planEndTime }} </bl-row>
      </div>
      <bl-row class="btns" just="center">
        <el-button-group style="width: 100%">
          <el-button color="#474747" @click="delPlan" style="width: 50%"><i class="iconbl bl-delete-line"></i>删 除</el-button>
          <el-button color="#474747" @click="updPlan" style="width: 50%"><i class="iconbl bl-a-texteditorsave-line"></i>保 存</el-button>
        </el-button-group>
      </bl-row>
    </div>
  </el-drawer>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { Document } from '@element-plus/icons-vue'
import { ElMessageBox, type CalendarDateType, type CalendarInstance } from 'element-plus'
import { planListDayApi, planDelApi, planUpdDayApi } from '@/api/plan'
import { getDateTimeFormat, timestampToDatetime } from '@/assets/utils/util'
import PlanDayInfo from './PlanDayInfo.vue'

onMounted(() => {
  getPlanAll(getDateTimeFormat().substring(0, 7))
})

const planDays = ref<any>({})
let lastMonth: string = ''

const calendarDate = ref<Date>(new Date())
const CalendarRef = ref<CalendarInstance>()
const selectDate = (val: CalendarDateType) => {
  if (!CalendarRef.value) return
  CalendarRef.value.selectDate(val)
}

watch(
  () => calendarDate.value,
  (data) => {
    getPlanAll(timestampToDatetime(data).substring(0, 7))
  }
)

const getPlanAll = (month: string, force: boolean = false) => {
  if (!force && month == lastMonth) {
    return
  }
  lastMonth = month
  planListDayApi({ month: month }).then((resp) => {
    planDays.value = resp.data
  })
}

//#region ----------------------------------------< 增删改 >-------------------------------------
const PlanDayInfoRef = ref()
const isShowAddForm = ref(false)
const showAddForm = () => {
  isShowAddForm.value = true
}

const savedCallback = () => {
  getPlanAll(lastMonth, true)
  isShowAddForm.value = false
}

const isShowUpdForm = ref(false)
const curPlan = ref({ groupId: '', title: '', content: '', planDate: '', planStartTime: '', planEndTime: '' })
const showUpdForm = (plan: any) => {
  isShowUpdForm.value = true
  curPlan.value = plan
}

const updPlan = () => {
  planUpdDayApi(curPlan.value).then((resp) => {
    getPlanAll(lastMonth, true)
    isShowUpdForm.value = false
  })
}

const delPlan = () => {
  ElMessageBox.confirm('是否确定删除该任务', '删除任务', {
    confirmButtonText: '我要删除',
    cancelButtonText: '取消'
  }).then(() => {
    planDelApi({ groupId: curPlan.value.groupId }).then((_resp) => {
      getPlanAll(lastMonth, true)
      isShowUpdForm.value = false
    })
  })
}
//#endregion
</script>

<style scoped lang="scss">
.plan-root {
  @include box(100%, 100%);
  @include flex(column, flex-start, center);
  overflow: hidden;
  position: relative;

  .workbench {
    padding: 0 10px 10px 10px;

    :deep(.el-button, .el-radio-button__inner) {
      padding: 8px 10px;
    }
  }

  .plan-calendar {
    --el-calendar-border: 1px solid var(--el-border-color);
    overflow: hidden;
    flex: 1;

    ::-webkit-scrollbar {
      width: 0;
      height: 0;
    }

    :deep(.el-calendar__header) {
      padding: 0;
    }

    :deep(.el-calendar__body) {
      height: 100%;
      padding: 0;

      th {
        font-size: 13px;
        padding: 3px 0;
      }

      .el-calendar-table {
        height: 100%;
        border: 0;

        thead {
          th {
            border-bottom: var(--el-calendar-border);
          }
        }

        tbody {
          @include box(100%, calc(100% - 45px));

          tr {
            td.prev,
            td.next {
              .date-title {
                filter: blur(2px);
              }

              &:hover {
                .date-title {
                  .iconbl {
                    opacity: 0 !important;
                  }
                }
              }
            }

            td.current {
              .date-title {
                color: var(--el-color-primary-light-5);
              }
            }

            td.is-today {
              background-color: var(--el-color-primary-light-8) !important;
            }

            td.is-selected {
              background: transparent;
            }

            td {
              border-right: 0;
              border-left: 0;
              border-top: 0;
              overflow-y: scroll;
              overflow-x: hidden;
              padding: 0;

              &:last-child {
                .el-calendar-day {
                  border-right: 0;
                }
              }

              .el-calendar-day {
                min-height: 100%;
                padding: 0;
                border-right: var(--el-calendar-border);

                &:hover {
                  background-color: transparent;

                  .date-title {
                    .iconbl {
                      opacity: 1;
                    }
                  }
                }

                .date-title {
                  @include box(100%, 25px);
                  @include flex(row, space-between, center);
                  padding: 3px 10px;

                  .iconbl {
                    opacity: 0;
                    transition: 0.3s;
                  }
                }

                .plan-group {
                  @include box(100%, calc(100% - 25px));
                  @include flex(column, flex-start, center);
                  position: relative;

                  .plan-line {
                    @include flex(row, flex-start, center);
                    @include box(calc(100% + 2px), 15px);
                    box-shadow: 0 2px 3px 1px rgba(104, 104, 104, 0.5);
                    position: absolute;
                    left: 0;
                    padding-left: 4px;
                    color: #fff;
                    transition: 0.3s;
                    z-index: 10;

                    .plan-title {
                      width: calc(100% - 10px);
                      font-size: 0.6rem;
                      text-align: left;
                      overflow: visible;
                      white-space: nowrap;
                    }
                  }

                  .hold {
                    box-shadow: none !important;
                  }

                  $bolder-radius: 20px;

                  .head {
                    width: calc(100% - 4px);
                    border-top-left-radius: $bolder-radius;
                    border-bottom-left-radius: $bolder-radius;
                    margin-left: 5px;
                    z-index: 100;
                    padding-right: 0;
                  }

                  .tail {
                    width: calc(100% - 5px);
                    border-top-right-radius: $bolder-radius;
                    border-bottom-right-radius: $bolder-radius;
                    margin-right: 5px;
                  }

                  .all {
                    width: calc(100% - 10px);
                    border-radius: $bolder-radius;
                    margin-left: 5px;
                    margin-right: 5px;
                  }

                  .tail,
                  .all {
                    &:hover {
                      .bl-delete-line {
                        opacity: 1;
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
@import url('./PlanColor.scss');

.detail {
  @include flex(column, flex-start, flex-start);
  height: 100%;
  padding-top: 20px;
  padding-bottom: 30px;

  .times {
    margin-top: 18px;
    margin-bottom: 18px;
    color: #949494;
    .iconbl {
      margin-right: 10px;
    }
  }

  .btns {
    .iconbl {
      margin-right: 15px;
    }
  }
}
</style>
