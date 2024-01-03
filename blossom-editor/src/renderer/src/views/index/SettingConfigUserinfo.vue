<template>
  <div class="config-root">
    <div class="title">修改用户信息</div>
    <div class="desc">
      天气预报使用<a target="_blank" href="https://dev.qweather.com/">和风天气API</a>，您可以在其上免费创建您的令牌以使用天气预报功能。
    </div>
    <el-form :model="userinfoForm" :rules="rules" label-position="right" label-width="130px" style="max-width: 800px" ref="UserinfoFormRef">
      <el-form-item label="ID" prop="username">
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
import { storeToRefs } from 'pinia'
import { userUpdApi } from '@renderer/api/auth'
import { useUserStore } from '@renderer/stores/user'
import { openExtenal } from '@renderer/assets/utils/electron'
import Notify from '@renderer/scripts/notify'

const userStore = useUserStore()
const { userinfo } = storeToRefs(userStore)

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
  id: userinfo.value.id,
  username: userinfo.value.username,
  nickName: userinfo.value.nickName,
  remark: userinfo.value.remark,
  location: userinfo.value.location,
  avatar: userinfo.value.avatar
})
const rules = ref<FormRules<UserinfoForm>>({
  username: [{ required: true, message: '请填写用户名', trigger: 'blur' }],
  nickName: [{ required: true, message: '请填写昵称', trigger: 'blur' }],
  remark: [{ required: true, message: '请填写备注', trigger: 'blur' }],
  avatar: [{ required: true, message: '请填写头像', trigger: 'blur' }]
})

const save = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid, _fields) => {
    if (valid) {
      userUpdApi(userinfoForm.value).then((_resp) => {
        Notify.success('个人信息修改成功')
        userStore.checkToken(
          () => {},
          () => {}
        )
      })
    }
  })
}
</script>

<style scoped lang="scss">
@import './styles/config-root.scss';
</style>
