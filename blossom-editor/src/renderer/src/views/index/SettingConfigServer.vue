<template>
  <bl-col align="flex-start">

    <bl-row class="param-row">
      <el-button @click="refreshParam" size="default">刷新服务器参数缓存</el-button>
    </bl-row>

    <bl-row class="param-row">
      <div class="param-title">服务器到期时间: </div>
      <div class="param-value">{{ userinfo.params.SERVER_MACHINE_EXPIRE }}</div>
      <el-tag>{{ serverExpire.machine }} 天后到期</el-tag>
    </bl-row>

    <bl-row class="param-row">
      <div class="param-title">数据库到期时间: </div>
      <div class="param-value">{{ userinfo.params.SERVER_DATABASE_EXPIRE }}</div>
      <el-tag>{{ serverExpire.database }} 天后到期</el-tag>
    </bl-row>

    <bl-row class="param-row">
      <div class="param-title">域名到期时间: </div>
      <div class="param-value">{{ userinfo.params.SERVER_DOMAIN_EXPIRE }}</div>
      <el-tag>{{ serverExpire.domain }} 天后到期</el-tag>
    </bl-row>

    <bl-row class="param-row">
      <div class="param-title">证书到期时间: </div>
      <div class="param-value">{{ userinfo.params.SERVER_HTTPS_EXPIRE }}</div>
      <el-tag>{{ serverExpire.https }} 天后到期</el-tag>
    </bl-row>
  </bl-col>
  <el-input type="textarea" v-model="userinfoJson" :rows="30" resize="none" disabled></el-input>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { storeToRefs } from 'pinia'
import { useUserStore } from '@renderer/stores/user'
import { paramRefreshApi } from '@renderer/api/blossom'
import { formatJson, getDateTimeFormat, betweenDay } from '@renderer/assets/utils/util'
import Notify from '@renderer/scripts/notify'

const userStore = useUserStore()
const { userinfo } = storeToRefs(userStore)

const serverExpire = computed(() => {
  let now = getDateTimeFormat()
  let params = userinfo.value.params
  return {
    machine: betweenDay(now, params.SERVER_MACHINE_EXPIRE),
    database: betweenDay(now, params.SERVER_DATABASE_EXPIRE),
    domain: betweenDay(now, params.SERVER_DOMAIN_EXPIRE),
    https: betweenDay(now, params.SERVER_HTTPS_EXPIRE),
  }
})

const refreshParam = () => {
  paramRefreshApi().then(_ => {
    Notify.success('刷新参数成功, 请重新登录后查看', '刷新成功')
  })
}


const userinfoJson = computed(() => {
  return formatJson(userinfo.value)
})
</script>

<style scoped lang="scss">
.param-row {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;

  .param-title {
    width: 110px;
    text-align: right;
  }

  .param-value {
    padding: 0 10px;
  }
}
</style>