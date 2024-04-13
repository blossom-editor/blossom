<template>
  <div class="config-root" v-loading="!userStore.isLogin" element-loading-spinner="none" element-loading-text="请登录后查看...">
    <div class="title">修改登录密码</div>
    <div class="desc">修改后下次登录将使用新密码。</div>
    
    <el-form :model="updPwdForm" :rules="rules" label-position="right" label-width="130px" ref="UpdPwdFormRef">
      <el-form-item label="旧密码" prop="password">
        <el-input v-model="updPwdForm.password" size="default" show-password>
          <template #prefix>
            <div class="iconbl bl-a-Securitypermissions-line" style="font-size: 20px"></div>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input v-model="updPwdForm.newPassword" size="default" show-password>
          <template #prefix>
            <div class="iconbl bl-a-Securitypermissions-line" style="font-size: 20px"></div>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input v-model="updPwdForm.confirmPassword" size="default" show-password>
          <template #prefix>
            <div class="iconbl bl-a-Securitypermissions-line" style="font-size: 20px"></div>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item>
        <bl-row just="flex-end" align="flex-start">
          <el-button size="default" type="primary" @click="save(UpdPwdFormRef)">保存</el-button>
        </bl-row>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useUserStore } from '@renderer/stores/user'
import type { FormInstance, FormRules } from 'element-plus'
import { userUpdPwdApi } from '@renderer/api/auth'
import Notify from '@renderer/scripts/notify'

const userStore = useUserStore()

interface UpdPwdForm {
  password: string
  newPassword: string
  confirmPassword: string
}
const UpdPwdFormRef = ref<FormInstance>()
const updPwdForm = ref<UpdPwdForm>({
  password: '',
  newPassword: '',
  confirmPassword: ''
})
const rules = ref<FormRules<UpdPwdForm>>({
  password: [{ required: true, message: '请填写旧密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请填写新密码', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请填写确认密码', trigger: 'blur' }]
})

const save = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid, _fields) => {
    if (valid) {
      userUpdPwdApi(updPwdForm.value).then((_resp) => {
        Notify.success('密码修改成功')
      })
    }
  })
}
</script>

<style scoped lang="scss">
@import './styles/config-root.scss';
</style>
