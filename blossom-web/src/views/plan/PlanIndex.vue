<template>
  <div class="plan-index-root">
    <div class="header">
      <IndexHeader :bg="true"></IndexHeader>
    </div>

    <bl-row just="space-between" class="workbench" height="45px">
      <div class="month">{{ calendarDate.getMonth() + 1 }}月</div>
      <el-button-group size="small">
        <el-button @click="selectDate('prev-month')">上月</el-button>
        <el-button @click="selectDate('today')">今日</el-button>
        <el-button @click="selectDate('next-month')">下月</el-button>
      </el-button-group>
    </bl-row>
    <el-calendar class="bl-calendar" v-model="calendarDate" ref="CalendarRef">
      <template #header="{ date }"><div></div></template>
      <template #date-cell="{ data }">
        <div class="date-title">
          <span>{{ data.day.split('-').slice(2).join('-') }}</span>
          <span class="iconbl bl-a-addline-line" @click="handleShowPlanAddDialog(data.day)"></span>
        </div>
        <div class="plan-group">
          <div v-for="(plan, index) in planDays[data.day + ' 00:00:00']" :key="plan.id">
            <el-popover
              placement="right"
              popper-class="plan-popover"
              :width="200"
              trigger="click"
              :hide-after="0"
              :disabled="plan.id < 0"
              :persistent="false">
              <!-- 触发元素 -->
              <template #reference>
                <div :class="'plan-line ' + plan.color + ' ' + plan.position + ' ' + plan.hl" :style="{ top: index * 21 + 'px' }">
                  <div v-if="plan.position == 'head' || plan.position == 'all'" class="plan-title">
                    {{ plan.title }}
                  </div>
                </div>
              </template>

              <!-- 弹出框内容 -->
              <bl-col class="plan-popover-inner">
                <div :class="['plan-popover-title', plan.color]">
                  {{ plan.title }}
                </div>
                <div class="plan-popover-time">
                  <div><span class="iconbl bl-date-line"></span> {{ data.day }}</div>
                  <span class="iconbl bl-a-clock3-line"></span> {{ plan.planStartTime }} - {{ plan.planEndTime }}
                </div>
                <div class="plan-popover-content">
                  {{ plan.content }}
                </div>
              </bl-col>
            </el-popover>
          </div>
        </div>
      </template>
    </el-calendar>
  </div>
</template>

<script setup lang="ts">
import { nextTick, onMounted, ref, watch } from 'vue'
import type { CalendarDateType, CalendarInstance } from 'element-plus'
import { planListDayApi, planDelApi } from '@/api/plan'
import { getDateTimeFormat, getNextDay, timestampToDatetime } from '@/assets/utils/util'
import IndexHeader from '@/views/index/IndexHeader.vue'

onMounted(() => {
  getPlanAll(getDateTimeFormat().substring(0, 7))
})

const PlanDayInfoRef = ref()
// 计划列表
const planDays = ref<any>({})
// 上次点击选择的月份, 不同月份时才查询接口
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

//#region ----------------------------------------< 新增删除 >-------------------------------------
const isShowPlanAddDialog = ref(false)

const handleShowPlanAddDialog = (ymd: string) => {
  isShowPlanAddDialog.value = true
  nextTick(() => {
    PlanDayInfoRef.value.setPlanDate(ymd)
  })
}

const savedCallback = () => {
  getPlanAll(lastMonth, true)
  isShowPlanAddDialog.value = false
}

const delDay = (groupId: number) => {
  planDelApi({ groupId: groupId }).then((_resp) => {
    getPlanAll(lastMonth, true)
  })
}
//#endregion
</script>

<style scoped lang="scss">
.plan-index-root {
  @include box(100%, 100%);
  @include flex(column, flex-start, center);
  overflow: hidden;

  .header {
    @include box(100%, 60px);
  }
  .workbench,
  .bl-calendar {
    max-width: 900px;
  }

  .workbench {
    padding: 10px 10px;

    :deep(.el-button, .el-radio-button__inner) {
      padding: 8px 10px;
    }
  }

  .bl-calendar {
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
              overflow: scroll;
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
</style>

<style lang="scss">
@import url('./PlanColor.scss');

.plan-popover {
  --el-popover-padding: 0 !important;
  border: 0 !important;
  width: 170px !important;

  .plan-popover-inner {
    .iconbl {
      font-size: 13px;
    }

    .plan-popover-title {
      @include font(15px, 500);
      width: 100%;
      padding: 10px;
      border-top-left-radius: 4px;
      border-top-right-radius: 4px;
      color: #fff;
    }

    .plan-popover-time {
      width: 100%;
      padding: 5px 10px;
      text-align: left;
    }

    .plan-popover-content {
      width: 100%;
      text-align: left;
      padding: 0 10px 10px;
      white-space: pre-wrap;
    }
  }
}
</style>
