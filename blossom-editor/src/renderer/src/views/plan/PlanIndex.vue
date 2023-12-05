<template>
  <div class="plan-index-root">
    <div class="plan-daily">
      <PlanDaily></PlanDaily>
    </div>
    <el-calendar class="bl-calendar" v-model="selectDay">
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
                <div
                  :class="'plan-line ' + plan.color + ' ' + plan.position + ' ' + plan.hl"
                  :style="{ top: index * 28 + 'px' }"
                  @mouseenter="handleMouseenter(data.day, plan.groupId)"
                  @mouseleave="handleMouseleave(data.day, plan.groupId)">
                  <div v-if="plan.position == 'head' || plan.position == 'all'" class="plan-title">
                    {{ plan.title }}
                  </div>
                  <div v-if="plan.position == 'tail' || plan.position == 'all'" class="iconbl bl-delete-line" @click="delDay(plan.groupId)"></div>
                </div>
              </template>

              <!-- 弹出框内容 -->
              <bl-col class="plan-popover-inner">
                <div :class="['plan-popover-title', plan.color]">
                  <el-input
                    v-if="plan.updTitle"
                    type="textarea"
                    v-model="plan.title"
                    :id="'plan-title-input-' + plan.id"
                    :rows="2"
                    @blur="blurPlanTitleInput(plan)"></el-input>
                  <div v-else @dblclick="showPlanTitleInput(plan)">{{ plan.title }}</div>
                </div>
                <div class="plan-popover-time">
                  <div><span class="iconbl bl-date-line"></span> {{ data.day }}</div>
                  <span class="iconbl bl-a-clock3-line"></span> {{ plan.planStartTime }} - {{ plan.planEndTime }}
                </div>
                <div class="plan-popover-content">
                  <el-input
                    v-if="plan.updContent"
                    type="textarea"
                    v-model="plan.content"
                    :id="'plan-content-input-' + plan.id"
                    :rows="5"
                    @blur="blurPlanContentInput(plan)"></el-input>
                  <div v-else :class="[isBlank(plan.content) ? 'content-placeholder' : '']" @dblclick="showPlanContentInput(plan)">
                    {{ plan.content }}
                  </div>
                </div>
              </bl-col>
            </el-popover>
          </div>
        </div>
      </template>
    </el-calendar>
  </div>

  <el-dialog
    v-model="isShowPlanAddDialog"
    width="400"
    top="60px"
    style="margin-left: 300px"
    :append-to-body="false"
    :destroy-on-close="true"
    :close-on-click-modal="true"
    draggable>
    <PlanDayInfo ref="PlanDayInfoRef" @saved="savedCallback"></PlanDayInfo>
  </el-dialog>
</template>

<script setup lang="ts">
import { nextTick, onMounted, ref, watch } from 'vue'
import { planListDayApi, planUpdDayApi, planDelApi } from '@renderer/api/plan'
import { getDateTimeFormat, getNextDay, timestampToDatetime } from '@renderer/assets/utils/util'
import { isNull, isBlank } from '@renderer/assets/utils/obj'
import PlanDaily from './PlanDaily.vue'
import PlanDayInfo from './PlanDayInfo.vue'

onMounted(() => {
  getPlanAll(getDateTimeFormat().substring(0, 7))
})

const PlanDayInfoRef = ref()
// 当前选择的日期
const selectDay = ref()
// 计划列表
const planDays = ref({})
// 上次点击选择的月份, 不同月份时才查询接口
let lastMonth: string = ''

watch(
  () => selectDay.value,
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

const handleMouseenter = (date: string, groupId: number) => {
  handleHlByGroupId(date, groupId, 1, 'hl')
  handleHlByGroupId(date, groupId, -1, 'hl')
}

const handleMouseleave = (date: string, groupId: number) => {
  handleHlByGroupId(date, groupId, 1, '')
  handleHlByGroupId(date, groupId, -1, '')
}

const handleHlByGroupId = (date: string, groupId: number, next: number = 1 | -1, hlClassName: string) => {
  let nextDate = date
  while (true) {
    let hasNext = false
    let thisDayPlans = planDays.value[nextDate + ' 00:00:00']
    if (isNull(thisDayPlans)) {
      break
    }
    for (let i = 0; i < thisDayPlans.length; i++) {
      let plan = thisDayPlans[i]
      if (plan.groupId == groupId) {
        plan.hl = hlClassName
        hasNext = true
        break
      }
    }
    if (!hasNext) {
      break
    }
    nextDate = getNextDay(nextDate, next)
  }
}

//#endregion

//#region ----------------------------------------< 修改 >-------------------------------------
const blurPlanTitleInput = (plan: any) => {
  planUpdDayApi({ groupId: plan.groupId, title: plan.title, content: plan.content }).then((_resp) => {
    getPlanAll(lastMonth, true)
    plan.updTitle = false
  })
}

const showPlanTitleInput = (plan: any) => {
  plan.updTitle = true
  nextTick(() => {
    let ele = document.getElementById('plan-title-input-' + plan.id)
    if (ele) ele.focus()
  })
}

const blurPlanContentInput = (plan: any) => {
  planUpdDayApi({ groupId: plan.groupId, title: plan.title, content: plan.content }).then((_resp) => {
    getPlanAll(lastMonth, true)
    plan.updContent = false
  })
}

const showPlanContentInput = (plan: any) => {
  plan.updContent = true
  nextTick(() => {
    let ele = document.getElementById('plan-content-input-' + plan.id)
    if (ele) ele.focus()
  })
}
//#endregion
</script>

<style scoped lang="scss">
.plan-index-root {
  @include box(100%, 100%);
  @include flex(row, center, center);
  padding: 20px 20px 20px 0;

  .plan-daily {
    @include box(250px, 100%);
    padding-right: 5px;
  }

  .bl-calendar {
    @include box(calc(100% - 250px), 100%);
    @include themeShadow(0 0 5px #d7d7d7, 0 0 5px #000000);
    --el-calendar-border: 1px solid var(--el-border-color);
    // border: var(--el-calendar-border);
    z-index: 1;
    border-radius: 8px;
    overflow: hidden;
    border: 0;

    ::-webkit-scrollbar {
      width: 0;
      height: 0;
    }

    :deep(.el-calendar__header) {
      @include box(100%, 50px);
      @include font(13px, 300);
      color: #8a8a8a;
      border: 0;
    }

    :deep(.el-calendar__body) {
      @include box(100%, calc(100% - 50px));
      padding: 0;

      .el-calendar-table {
        @include box(100%, 100%);
        border: 0;

        thead {
          @include box(100%, 45px);
          background-color: var(--bl-html-color);

          th {
            border-bottom: var(--el-calendar-border);

            &:last-child {
            }
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
                    @include box(calc(100% + 2px), 22px);
                    @include themeShadow(0 3px 5px 1px rgba(104, 104, 104, 0.5), 0 3px 5px 1px rgba(0, 0, 0, 1));
                    @include themeBrightness();
                    position: absolute;
                    left: 0;
                    padding: 0 5px;
                    margin: 3px 0 3px -1px;
                    color: #fff;
                    transition: 0.3s;
                    z-index: 9999;

                    .plan-title {
                      @include ellipsis();
                      width: calc(100% - 10px);
                      font-size: 11px;
                      text-align: left;
                    }
                  }

                  .hold {
                    box-shadow: none !important;
                  }

                  $bolder-radius: 20px;

                  .head {
                    width: calc(100% - 9px);
                    border-top-left-radius: $bolder-radius;
                    border-bottom-left-radius: $bolder-radius;
                    margin-left: 10px;
                  }

                  .tail {
                    width: calc(100% - 9px);
                    border-top-right-radius: $bolder-radius;
                    border-bottom-right-radius: $bolder-radius;
                    margin-right: 10px;
                  }

                  .all {
                    width: calc(100% - 20px);
                    border-radius: $bolder-radius;
                    margin-left: 10px;
                    margin-right: 10px;
                  }

                  .tail,
                  .all {
                    &:hover {
                      .bl-delete-line {
                        opacity: 1;
                      }
                    }
                  }

                  .bl-delete-line {
                    @include font(13px, 300);
                    @include absolute('', 0);
                    width: 25px;
                    border-radius: $bolder-radius;
                    color: #fff;
                    padding: 5px;
                    opacity: 0;
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
  width: 250px !important;

  .plan-popover-inner {
    @include themeBrightness();

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
      .el-textarea {
        --el-input-bg-color: var(--bl-html-color) !important;
      }
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
      transition: color 0.3s;
      cursor: cell;
      &:hover {
        @include themeColor(#000, #fff);
      }

      .content-placeholder {
        height: 10px;
        transition: background 0.3s;
        border-radius: 5px;
        cursor: cell;
        &:hover {
          @include themeBg(#f5f5f5, #1a1a1a);
        }
      }
    }
  }
}
</style>
