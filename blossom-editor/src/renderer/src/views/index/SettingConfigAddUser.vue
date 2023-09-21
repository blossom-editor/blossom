<template>
  <el-form :model="addUserForm" :rules="rules" label-position="right" label-width="90px" style="width: 580px;"
    ref="AddUserFormRef">
    <el-form-item label="用户名" prop="username">
      <el-input v-model="addUserForm.username" size="default">
        <template #prefix>
          <div class="iconbl bl-user-line" style="font-size: 20px;"></div>
        </template>
      </el-input>
    </el-form-item>
    <el-form-item label="密码" prop="password">
      <el-input v-model="addUserForm.password" size="default" show-password>
        <template #prefix>
          <div class="iconbl bl-a-Securitypermissions-line" style="font-size: 20px;"></div>
        </template>
      </el-input>
    </el-form-item>
    <el-form-item label="用户权限" prop="type">
      <el-radio-group v-model="addUserForm.type" size="default">
        <el-radio-button label="1">管理员</el-radio-button>
        <el-radio-button label="2">普通用户</el-radio-button>
        <el-radio-button label="3">只读用户</el-radio-button>
      </el-radio-group>
      <bl-row style="color: var(--bl-text-color-light);">
        {{ typeDesc }}
      </bl-row>
    </el-form-item>
    <el-form-item>
      <bl-row just="flex-end" align="flex-start">
        <el-button size="default" type="primary" @click="save(AddUserFormRef)">保存</el-button>
      </bl-row>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { userAddApi } from '@renderer/api/auth'
import Notify from '@renderer/scripts/notify'

interface AddUserForm { username: string, password: string, type: number }
const AddUserFormRef = ref<FormInstance>()
const addUserForm = ref<AddUserForm>({
  username: '',
  password: '',
  type: 2
})
const rules = ref<FormRules<AddUserForm>>({
  username: [{ required: true, message: '请填写用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请填写密码', trigger: 'blur' }],
  type: [{ required: true, message: '请选择用户权限', trigger: 'blur' }],
})

const typeDesc = computed(() => {
  if (addUserForm.value.type == 1) {
    return '只有管理员可以添加用户.'
  } else if (addUserForm.value.type == 2) {
    return '普通用户拥有除添加使用账号之外的所有权限.'
  } else {
    return '只读用户只又有查看权限, 无法进行任何数据操作, 例如新建文件夹, 编辑文章等...';
  }
})

const save = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid, _fields) => {
    if (valid) {
      userAddApi(addUserForm.value).then(_resp => {
        Notify.success('保存成功')
      })
    }
  })
}
</script>