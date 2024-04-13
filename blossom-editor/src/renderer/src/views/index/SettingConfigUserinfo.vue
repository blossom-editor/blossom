<template>
  <div class="config-root" v-loading="!userStore.isLogin" element-loading-spinner="none" element-loading-text="请登录后查看...">
    <div class="title">修改用户信息</div>
    <div class="desc" style="margin-bottom: 0">用户的个人信息，若无内容请点击右侧刷新。</div>
    <div class="desc">
      <el-button @click="refreshUserinfo" text bg><span class="iconbl bl-refresh-line"></span>刷新信息</el-button>
    </div>

    <el-form :model="userinfoForm" :rules="rules" label-position="right" label-width="130px" style="max-width: 800px" ref="UserinfoFormRef">
      <el-form-item label="ID" prop="id">
        <el-input v-model="userinfoForm.id" size="default" disabled>
          <template #prefix>
            <div class="iconbl bl-a-Securitypermissions-line" style="font-size: 20px"></div>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="用户名" prop="username">
        <el-input v-model="userinfoForm.username" size="default">
          <template #prefix>
            <div class="iconbl bl-user-line" style="font-size: 20px"></div>
          </template>
        </el-input>
        <div class="conf-tip">用于登录。</div>
      </el-form-item>
      <el-form-item label="昵称" prop="nickName">
        <el-input v-model="userinfoForm.nickName" size="default">
          <template #prefix>
            <div class="iconbl bl-a-IDcardfront-line" style="font-size: 20px"></div>
          </template>
        </el-input>
        <div class="conf-tip">昵称会在博客首页显示。</div>
      </el-form-item>
      <el-form-item label="说明" prop="remark">
        <el-input v-model="userinfoForm.remark" size="default">
          <template #prefix>
            <div class="iconbl bl-books-line" style="font-size: 20px"></div>
          </template>
        </el-input>
        <div class="conf-tip">说明会在博客首页显示。</div>
      </el-form-item>
      <el-form-item label="天气预报城市">
        <el-input v-model="userinfoForm.location" size="default">
          <template #prefix>
            <div class="iconbl bl-cloud-line" style="font-size: 20px"></div>
          </template>
          <template #append>
            <el-button @click="openExtenal('https://github.com/qwd/LocationList/blob/master/China-City-List-latest.csv')">查看城市代码</el-button>
          </template>
        </el-input>
        <div class="conf-tip">填写和风天气的城市代码。</div>
      </el-form-item>
      <el-form-item label="头像" prop="avatar">
        <el-input v-model="userinfoForm.avatar" size="default" placeholder="请输入图片地址, http://...">
          <template #prefix>
            <div class="iconbl bl-image--line" style="font-size: 20px"></div>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item>
        <bl-row just="space-between" align="flex-start">
          <el-image :src="userinfoForm.avatar" style="height: 100px; border-radius: 4px" fit="contain">
            <template #error>
              <div></div>
            </template>
          </el-image>
          <bl-col just="flex-end">
            <el-button size="default" type="primary" @click="save(UserinfoFormRef)">保存</el-button>
          </bl-col>
        </bl-row>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { userUpdApi, userinfoApi } from '@renderer/api/auth'
import { useUserStore } from '@renderer/stores/user'
import { openExtenal } from '@renderer/assets/utils/electron'
import Notify from '@renderer/scripts/notify'

const userStore = useUserStore()

interface UserinfoForm {
  id: string
  username: string
  nickName: string
  remark: string
  location: string
  avatar: string
}
const UserinfoFormRef = ref<FormInstance>()
const userinfoForm = ref<UserinfoForm>({
  id: '',
  username: '',
  nickName: '',
  remark: '',
  location: '',
  avatar: ''
})
const rules = ref<FormRules<UserinfoForm>>({
  id: [{ required: true, message: '请填写用户ID', trigger: 'blur' }],
  username: [{ required: true, message: '请填写用户名', trigger: 'blur' }],
  nickName: [{ required: true, message: '请填写昵称', trigger: 'blur' }],
  remark: [{ required: true, message: '请填写备注', trigger: 'blur' }]
})

const getUserinfo = () => {
  userinfoApi().then((resp) => {
    userinfoForm.value = resp.data
  })
}

const refreshUserinfo = () => {
  userinfoApi().then((resp) => {
    userinfoForm.value = resp.data
    Notify.success('', '刷新成功')
  })
}

const save = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid, _fields) => {
    if (valid) {
      userUpdApi(userinfoForm.value).then((_resp) => {
        Notify.success('您的个人信息已变更', '修改成功')
        userStore.checkToken(
          () => {},
          () => {}
        )
      })
    }
  })
}

const reload = () => {
  getUserinfo()
}

defineExpose({ reload })
</script>

<style scoped lang="scss">
@import './styles/config-root.scss';
</style>
