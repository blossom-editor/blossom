<template>
  <div class="userlist-setting-root">
    <div class="info-title">
      <div class="iconbl bl-user-line"></div>
      用户管理
    </div>

    <div class="content">
      <bl-row class="stat" just="space-between" align="space-between">
        <div class="all">
          <div class="label">用户总数</div>
          <div class="count">{{ users.length }}</div>
        </div>
        <div class="admin">
          <div class="label">管理员</div>
          <div class="count">{{ userStatComputed.admin }}</div>
        </div>
        <div class="normal">
          <div class="label">普通用户</div>
          <div class="count">{{ userStatComputed.normal }}</div>
        </div>
        <div class="read">
          <div class="label">只读用户</div>
          <div class="count">{{ userStatComputed.read }}</div>
        </div>
      </bl-row>
      <bl-row class="search" just="space-between">
        <div>
          <el-input size="default" placeholder="搜索用户名或昵称" style="width: 335px" v-model="userSearch"></el-input>
          <el-select size="default" placeholder="用户名类型" style="width: 158px; margin-left: 19px" v-model="userTypeSearch" clearable>
            <el-option :value="1" label="管理员" />
            <el-option :value="2" label="普通用户" />
            <el-option :value="3" label="只读用户" />
          </el-select>
        </div>
        <el-button size="default" class="iconbl bl-refresh-line" text @click="getUserList"></el-button>
      </bl-row>
      <div class="user-container">
        <div :class="['user-item', user.delTime !== '0' ? 'warn-bg-zebra' : '']" v-for="user in usersComputed" @click="showDetailDialog(user.id)">
          <img v-if="user.avatar != ''" class="avatar" :src="user.avatar" />
          <img v-else class="avatar" src="@renderer/assets/imgs/default_user_avatar.jpg" />
          <div class="nickname">{{ user.nickName }}</div>
          <div class="username">{{ user.username }}</div>
          <bl-row class="time" just="space-between">{{ user.creTime }}</bl-row>
          <div class="id">{{ user.id }}</div>
          <div :class="['type', user.type === 1 ? 'admin' : user.type === 2 ? 'normal' : 'read']">
            <span v-if="user.type === 1" class="iconbl bl-a-Securitypermissions-line"></span
            >{{ user.type === 1 ? '管理员' : user.type === 2 ? '普通' : '只读' }}
          </div>
        </div>
      </div>
    </div>
  </div>

  <el-dialog
    v-model="isShowDetailDialog"
    class="bl-dialog-fixed-body"
    width="400"
    style="height: 535px"
    :align-center="true"
    :append-to-body="true"
    :destroy-on-close="true"
    :close-on-click-modal="true">
    <UserInfoSetting :id="curUserId" @saved="infoSaved"></UserInfoSetting>
  </el-dialog>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { userListApi } from '@renderer/api/auth'
import { isNotNull, isNull } from '@renderer/assets/utils/obj'
import { computed } from 'vue'
import UserInfoSetting from './UserInfoSetting.vue'

onMounted(() => {
  getUserList()
})

//#region ----------------------------------------< 用户列表 >--------------------------------------

type UserList = {
  id: string
  avatar: string
  nickName: string
  username: string
  creTime: string
  delTime: string
  type: number
}
const userSearch = ref()
const userTypeSearch = ref()
const users = ref<UserList[]>([])

/**
 * 用户搜索
 */
const usersComputed = computed(() => {
  return users.value
    .filter((user: UserList) => {
      if (isNull(userSearch.value) && isNull(userTypeSearch.value)) {
        return true
      }

      let isName: boolean = true
      if (isNotNull(userSearch.value)) {
        isName =
          user.username.toLowerCase().includes(userSearch.value.toLowerCase()) || user.nickName.toLowerCase().includes(userSearch.value.toLowerCase())
      }

      let isType: boolean = true
      if (isNotNull(userTypeSearch.value)) {
        isType = user.type === userTypeSearch.value
      }

      if (isName && isType) {
        return true
      }
      return false
    })
    .reverse()
})

/**
 * 统计用户数
 */
const userStatComputed = computed(() => {
  let stat = {
    admin: 0,
    normal: 0,
    read: 0
  }
  for (let i = 0; i < users.value.length; i++) {
    const u = users.value[i]
    if (u.type === 1) {
      stat.admin++
    }
    if (u.type === 2) {
      stat.normal++
    }
    if (u.type === 3) {
      stat.read++
    }
  }
  return stat
})

const getUserList = () => {
  userListApi().then((resp) => {
    users.value = resp.data
  })
}

//#endregion

//#region ----------------------------------------< 用户列表 >--------------------------------------
const isShowDetailDialog = ref(false)
const curUserId = ref<string>()
const showDetailDialog = (id: string) => {
  isShowDetailDialog.value = true
  curUserId.value = id
}
const infoSaved = () => {
  getUserList()
  isShowDetailDialog.value = false
}
//#endregion
</script>

<style lang="scss" scoped>
@import '@renderer/assets/styles/bl-dialog-info';
@import '@renderer/views/index/styles/user-setting';
.userlist-setting-root {
  @include box(100%, 100%);

  .content {
    @include box(100%, calc(100% - 50px));
    padding: 10px 10px 20px;

    .stat {
      height: 34px;
      font-size: 13px;

      .all,
      .admin,
      .normal,
      .read {
        @include flex(row, space-between, center);
        width: 158px;
        height: 100%;
        padding: 0 20px;
        border: 1px solid var(--el-border-color);
        border-radius: 4px;
        transition: transform 0.3s;
        cursor: pointer;
        .label {
          @include font(13px, 300);
        }

        .count {
          font-size: 24px;
          font-style: italic;
          color: var(--bl-text-color-light);
        }

        &:hover {
          transform: translateY(-3px);
        }
      }

      .all {
        background: linear-gradient(155deg, $admin-bg 0%, $normal-bg 70%);
        color: $admin-color;
        --bl-text-color-light: $admin-color;
      }

      .admin {
        background-color: $admin-bg;
        color: $admin-color;
        --bl-text-color-light: $admin-color;
      }

      .normal {
        background-color: $normal-bg;
        color: $normal-color;
        --bl-text-color-light: $normal-color;
      }

      .read {
        background-color: $read-bg;
        color: $read-color;
        --bl-text-color-light: $read-color;
      }
    }

    .search {
      height: 30px;
      margin: 10px 0 10px 0;
      .iconbl {
        font-size: 15px;
      }
    }

    .user-container {
      @include box(100%, calc(100% - 84px));
      @include flex(row, flex-start, flex-start);
      align-content: flex-start;
      flex-wrap: wrap;
      overflow-y: scroll;
      padding: 10px 5px 10px 10px;

      .user-item {
        @include font(13px, 300);
        @include themeBg(#f5f5f5, #252525);
        width: 150px;
        margin-bottom: 25px;
        margin-right: 15px;
        padding: 8px;
        border-radius: 4px;
        transition:
          box-shadow 0.2s,
          transform 0.3s;
        position: relative;
        cursor: pointer;

        &:hover {
          @include themeShadow(0 3px 5px 0 rgb(190, 190, 190), 0 3px 5px 0 rgb(20, 20, 20));
          transform: translateY(-5px);

          .download-btn {
            opacity: 1;
          }
        }

        .avatar {
          @include box(50px, 50px);
          @include themeBrightness();
          object-fit: cover;
          border-radius: 6px;
          margin-bottom: 10px;
        }

        .nickname {
          @include font(15px, 300);
          @include ellipsis();
        }

        .username {
          @include font(12px, 300);
          @include ellipsis();
          color: var(--bl-text-color-light);
        }

        .time {
          @include font(11px, 300);
          color: var(--bl-text-color-light);
        }

        .id {
          @include font(11px, 300);
          color: var(--bl-text-color-light);
          position: absolute;
          top: 35px;
          right: 10px;
        }

        .type {
          position: absolute;
          top: 10px;
          right: 10px;
        }
      }
    }
  }
}
</style>
