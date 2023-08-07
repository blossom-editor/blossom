<template>
  <el-form :model="updPwdForm" :rules="rules" label-position="right" label-width="90px" style="width: 580px;"
    ref="UpdPwdFormRef">
    <el-form-item label="旧密码" prop="password">
      <el-input v-model="updPwdForm.password" size="default" show-password>
        <template #prefix>
          <div class="iconbl bl-a-Securitypermissions-line" style="font-size: 20px;"></div>
        </template>
      </el-input>
    </el-form-item>
    <el-form-item label="新密码" prop="newPassword">
      <el-input v-model="updPwdForm.newPassword" size="default" show-password>
        <template #prefix>
          <div class="iconbl bl-a-Securitypermissions-line" style="font-size: 20px;"></div>
        </template>
      </el-input>
    </el-form-item>
    <el-form-item label="确认密码" prop="confirmPassword">
      <el-input v-model="updPwdForm.confirmPassword" size="default" show-password>
        <template #prefix>
          <div class="iconbl bl-a-Securitypermissions-line" style="font-size: 20px;"></div>
        </template>
      </el-input>
    </el-form-item>
    <el-form-item>
      <bl-row justify-content="flex-end" align-items="flex-start">
        <el-button size="default" type="primary" @click="save(UpdPwdFormRef)">保存</el-button>
      </bl-row>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { userUpdPwdApi } from '@renderer/api/auth'
import Notify from '@renderer/components/Notify'

interface UpdPwdForm { password: string, newPassword: string, confirmPassword: string }
const UpdPwdFormRef = ref<FormInstance>()
const updPwdForm = ref<UpdPwdForm>({
  password: '',
  newPassword: '',
  confirmPassword: ''
})
const rules = ref<FormRules<UpdPwdForm>>({
  password: [{ required: true, message: '请填写旧密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请填写信密码', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请填写确认密码', trigger: 'blur' }],
})

const save = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid, _fields) => {
    if (valid) {
      userUpdPwdApi(updPwdForm.value).then(_resp => {
        Notify.success('保存成功')
      })
    }
  })
}
</script>