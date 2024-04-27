<template>
  <div class="config-root" v-loading="!userStore.isLogin" element-loading-spinner="none" element-loading-text="请登录后查看...">
    <div class="title">服务器配置</div>
    <div class="desc" style="margin-bottom: 0">服务器各项参数配置，若无内容请点击下方刷新。只有管理员用户具有操作服务器配置的权限。</div>
    <div class="desc">
      <el-button @click="refreshParam" text bg><span class="iconbl bl-refresh-line"></span>刷新参数</el-button>
    </div>

    <el-form :model="serverParamForm" label-position="right" label-width="130px" style="max-width: 800px">
      <el-form-item label="文件访问地址" :required="true">
        <el-input
          size="default"
          style="width: calc(100% - 100px)"
          v-model="serverParamForm.BLOSSOM_OBJECT_STORAGE_DOMAIN"
          @change="(cur: any) => updParam('BLOSSOM_OBJECT_STORAGE_DOMAIN', cur)">
          <template #prefix>
            <div class="iconbl bl-image--line" style="font-size: 20px"></div>
          </template>
        </el-input>
        <el-button size="default" style="width: 90px; margin-left: 10px" @click="autuUpdBlossomOSDomain">点击自动配置</el-button>
        <div class="conf-tip">
          文件访问地址。需以<code style="color: var(--el-color-danger)">/pic</code>结尾。你可以点击右上角的<span
            class="iconbl bl-caution-line"
            style="padding: 0 3px"></span
          >图标进行快捷配置。
        </div>
      </el-form-item>

      <el-form-item label="备份文件路径">
        <el-input size="default" v-model="serverParamForm.BACKUP_PATH" @change="(cur: any) => updParam('BACKUP_PATH', cur)"></el-input>
        <div class="conf-tip">如果通过 Docker 部署，推荐使用默认路径，如果你需要修改路径，请注意路径挂载。</div>
      </el-form-item>

      <el-form-item label="编辑记录保存天数">
        <bl-row>
          <el-input-number
            size="default"
            controls-position="right"
            :min="1"
            v-model="serverParamForm.ARTICLE_LOG_EXP_DAYS"
            @change="(cur: any) => updParam('ARTICLE_LOG_EXP_DAYS', cur)">
          </el-input-number>
        </bl-row>
        <div class="conf-tip">文章编辑记录的保存天数。{{ serverParamForm.ARTICLE_LOG_EXP_DAYS }}天前的编辑记录将被删除。</div>
      </el-form-item>

      <el-form-item label="文章回收站保存天数">
        <bl-row>
          <el-input-number
            size="default"
            controls-position="right"
            :min="1"
            v-model="serverParamForm.ARTICLE_RECYCLE_EXP_DAYS"
            @change="(cur: any) => updParam('ARTICLE_RECYCLE_EXP_DAYS', cur)"></el-input-number>
        </bl-row>
        <div class="conf-tip">文章回收站过期天数。{{ serverParamForm.ARTICLE_RECYCLE_EXP_DAYS }}天前的记录将被删除。</div>
      </el-form-item>

      <el-form-item label="备份文件保存天数">
        <bl-row>
          <el-input-number
            size="default"
            controls-position="right"
            :min="1"
            v-model="serverParamForm.BACKUP_EXP_DAYS"
            @change="(cur: any) => updParam('BACKUP_EXP_DAYS', cur)"></el-input-number>
        </bl-row>
        <div class="conf-tip">
          备份文件的保存天数，每日早上7点进行全量文章备份(不包含图片)，同时删除{{ serverParamForm.BACKUP_EXP_DAYS }}天前的备份文件。
        </div>
      </el-form-item>

      <el-form-item label="和风天气 Key">
        <el-input size="default" v-model="serverParamForm.HEFENG_KEY" @change="(cur: any) => updParam('HEFENG_KEY', cur)"></el-input>
        <div class="conf-tip">
          和风天气的 API KEY，申请方式请查看<a href="https://www.wangyunf.com/blossom-doc/guide/hefeng.html" target="_blank">《和风天气配置文档》</a
          >。修改后点击首页天气右上角的刷新按钮 <span class="iconbl bl-refresh-smile"></span> 获取最新天气。
        </div>
      </el-form-item>

      <el-form-item label="服务器到期时间">
        <div class="conf-tip">如果你使用云服务器或其他有时限的环境，可在此配置到期提示，其他环境可无视，(<code>yyyy-MM-dd</code>格式)。</div>
        <el-input size="default" v-model="serverParamForm.SERVER_MACHINE_EXPIRE" @change="(cur: any) => updParam('SERVER_MACHINE_EXPIRE', cur)">
          <template #append> {{ serverExpire.machine }} </template>
        </el-input>
      </el-form-item>

      <el-form-item label="数据库到期时间">
        <el-input size="default" v-model="serverParamForm.SERVER_DATABASE_EXPIRE" @change="(cur: any) => updParam('SERVER_DATABASE_EXPIRE', cur)">
          <template #append> {{ serverExpire.database }} </template>
        </el-input>
      </el-form-item>

      <el-form-item label="域名到期时间">
        <el-input size="default" v-model="serverParamForm.SERVER_DOMAIN_EXPIRE" @change="(cur: any) => updParam('SERVER_DOMAIN_EXPIRE', cur)">
          <template #append> {{ serverExpire.domain }} </template>
        </el-input>
      </el-form-item>

      <el-form-item label="证书到期时间">
        <el-input size="default" v-model="serverParamForm.SERVER_HTTPS_EXPIRE" @change="(cur: any) => updParam('SERVER_HTTPS_EXPIRE', cur)">
          <template #append> {{ serverExpire.https }} </template>
        </el-input>
      </el-form-item>
    </el-form>
    <!-- <div class="server-config">
      {{ userStore.userinfo }}
    </div> -->
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useServerStore } from '@renderer/stores/server'
import { KEY_BLOSSOM_OBJECT_STORAGE_DOMAIN, useUserStore } from '@renderer/stores/user'
import { paramListApi, paramUpdApi, paramRefreshApi } from '@renderer/api/blossom'
import { getDateTimeFormat, betweenDay } from '@renderer/assets/utils/util'
import Notify from '@renderer/scripts/notify'
import dayjs from 'dayjs'

const serverStore = useServerStore()
const userStore = useUserStore()

const serverParamForm = ref({
  WEB_ARTICLE_URL: '',
  BACKUP_PATH: '',
  ARTICLE_LOG_EXP_DAYS: 0,
  ARTICLE_RECYCLE_EXP_DAYS: 0,
  BACKUP_EXP_DAYS: 0,
  HEFENG_KEY: '',
  BLOSSOM_OBJECT_STORAGE_DOMAIN: '',
  SERVER_MACHINE_EXPIRE: '',
  SERVER_DATABASE_EXPIRE: '',
  SERVER_DOMAIN_EXPIRE: '',
  SERVER_HTTPS_EXPIRE: '',
  SERVER_VERSION: ''
})

/**
 * 计算天数
 */
const serverExpire = computed(() => {
  let now = getDateTimeFormat()
  try {
    return {
      machine: dayjs().isBefore(serverParamForm.value.SERVER_MACHINE_EXPIRE)
        ? betweenDay(now, serverParamForm.value.SERVER_MACHINE_EXPIRE) + '天后到期'
        : '已到期',
      database: dayjs().isBefore(serverParamForm.value.SERVER_DATABASE_EXPIRE)
        ? betweenDay(now, serverParamForm.value.SERVER_DATABASE_EXPIRE) + '天后到期'
        : '已到期',
      domain: dayjs().isBefore(serverParamForm.value.SERVER_DOMAIN_EXPIRE)
        ? betweenDay(now, serverParamForm.value.SERVER_DOMAIN_EXPIRE) + '天后到期'
        : '已到期',
      https: dayjs().isBefore(serverParamForm.value.SERVER_HTTPS_EXPIRE)
        ? betweenDay(now, serverParamForm.value.SERVER_HTTPS_EXPIRE) + '天后到期'
        : '已到期'
    }
  } catch {
    return {}
  }
})

/**
 * 获取参数列表
 */
const getParamList = () => {
  paramListApi().then((resp) => {
    serverParamForm.value = {
      ...resp.data,
      ...{
        ARTICLE_LOG_EXP_DAYS: Number(resp.data.ARTICLE_LOG_EXP_DAYS),
        ARTICLE_RECYCLE_EXP_DAYS: Number(resp.data.ARTICLE_RECYCLE_EXP_DAYS),
        BACKUP_EXP_DAYS: Number(resp.data.BACKUP_EXP_DAYS)
      }
    }
  })
}

const refreshParam = () => {
  paramRefreshApi().then((_) => {
    Notify.success('', '刷新成功')
    getParamList()
    userStore.getUserinfo()
  })
}

const updParam = (paramName: string, paramValue: string) => {
  paramUpdApi({ paramName: paramName, paramValue: paramValue }).then((_resp) => {
    userStore.getUserinfo()
    ElMessage.info({ message: '保存成功', grouping: true })
  })
}

/**
 * 自动配置
 */
const autuUpdBlossomOSDomain = () => {
  let url = serverStore.serverUrl + '/pic'
  paramUpdApi({ paramName: KEY_BLOSSOM_OBJECT_STORAGE_DOMAIN, paramValue: url }).then((_resp) => {
    userStore.getUserinfo()
    getParamList()
    Notify.success('配置成功', '配置成功')
  })
}

const reload = () => {
  getParamList()
}

defineExpose({ reload })
</script>

<style scoped lang="scss">
@import './styles/config-root.scss';

.server-config {
  padding: 10px;
  font-size: 12px;
  white-space: pre;
  color: var(--bl-text-color-light);
}
</style>
