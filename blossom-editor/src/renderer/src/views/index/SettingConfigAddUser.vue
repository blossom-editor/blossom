<template>
  <div class="config-root">
    <div class="title">添加使用账号</div>
    <div class="desc" style="margin-bottom: 0">您可以添加用户与您共用同一个后台服务，添加账号后，可登录新账号修改个人信息。</div>
    <div class="desc">
      <el-button @click="showUserListDialog" text bg><span class="iconbl bl-user-line"></span>用户管理</el-button>
    </div>

    <el-form :model="addUserForm" :rules="rules" label-position="right" label-width="130px" ref="AddUserFormRef">
      <el-form-item label="用户名" prop="username">
        <el-input v-model="addUserForm.username" size="default">
          <template #prefix>
            <div class="iconbl bl-user-line" style="font-size: 20px"></div>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input v-model="addUserForm.password" size="default" show-password>
          <template #prefix>
            <div class="iconbl bl-a-Securitypermissions-line" style="font-size: 20px"></div>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="用户权限" prop="type">
        <el-radio-group v-model="addUserForm.type" size="default">
          <el-radio-button value="1">管理员</el-radio-button>
          <el-radio-button value="2">普通用户</el-radio-button>
          <el-radio-button value="3">只读用户</el-radio-button>
        </el-radio-group>
        <bl-row class="conf-tip">
          {{ typeDesc }}
        </bl-row>
      </el-form-item>
      <el-form-item>
        <bl-row just="flex-end" align="flex-start">
          <el-button size="default" type="primary" @click="save(AddUserFormRef)">保存</el-button>
        </bl-row>
      </el-form-item>
    </el-form>
  </div>

  <!-- 自定义临时访问链接 -->
  <el-dialog
    v-model="isShowUserListDialog"
    class="bl-dialog-fixed-body"
    width="710"
    style="height: 70%"
    :align-center="true"
    :append-to-body="true"
    :destroy-on-close="true"
    :close-on-click-modal="true">
    <UserListSetting></UserListSetting>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { userAddApi } from '@renderer/api/auth'
import Notify from '@renderer/scripts/notify'
import UserListSetting from './setting/UserListSetting.vue'

interface AddUserForm {
  username: string
  password: string
  type: number
}
const AddUserFormRef = ref<FormInstance>()
const addUserForm = ref<AddUserForm>({
  username: '',
  password: '',
  type: 2
})
const rules = ref<FormRules<AddUserForm>>({
  username: [{ required: true, message: '请填写用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请填写密码', trigger: 'blur' }],
  type: [{ required: true, message: '请选择用户权限', trigger: 'blur' }]
})

const typeDesc = computed(() => {
  if (addUserForm.value.type == 1) {
    return '只有管理员可以添加用户。'
  } else if (addUserForm.value.type == 2) {
    return '普通用户拥有除添加使用账号之外的所有权限。'
  } else {
    return '只读用户只又有查看权限, 无法进行任何数据操作, 例如新建文件夹, 编辑文章等。'
  }
})

const save = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid, _fields) => {
    if (valid) {
      userAddApi(addUserForm.value).then((_resp) => {
        Notify.success('添加用户成功')
      })
    }
  })
}

//#region ----------------------------------------< 菜单 >--------------------------------------
const isShowUserListDialog = ref(false)
const showUserListDialog = () => {
  isShowUserListDialog.value = true
}
//#endregion
</script>

<style scoped lang="scss">
@import './styles/config-root.scss';
</style>
