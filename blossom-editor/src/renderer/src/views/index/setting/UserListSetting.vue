<template>
  <div class="userlist-setting-root">
    <div class="info-title">
      <div class="iconbl bl-user-line"></div>
      用户管理
    </div>

    <div class="content">
      <bl-row class="stat"
        >共
        <div>{{ users.length }}</div>
        名用户，管理员
        <div class="admin">{{ userStatComputed.admin }}</div>
        名，普通用户
        <div class="normal">{{ userStatComputed.normal }}</div>
        名，只读用户
        <div class="read">{{ userStatComputed.read }}</div>
        名。</bl-row
      >
      <bl-row class="search" just="space-between">
        <div>
          <el-input size="default" placeholder="搜索用户名或昵称" style="width: 200px" v-model="userSearch"></el-input>
          <el-select size="default" placeholder="用户名类型" style="width: 120px; margin-left: 10px" v-model="userTypeSearch" clearable>
            <el-option :value="1" label="管理员" />
            <el-option :value="2" label="普通用户" />
            <el-option :value="3" label="只读用户" />
          </el-select>
        </div>
        <el-button size="default" class="iconbl bl-refresh-line" @click="getUserList"></el-button>
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
      height: 25px;
      font-size: 13px;

      div {
        padding: 0 3px;
        margin: 0 2px;
        font-size: 13px;
        transition: all 0.3s;
        border-radius: 4px;
        cursor: pointer;
      }
      .admin:hover {
        background-color: $admin-bg;
        color: $admin-color;
      }

      .normal:hover {
        background-color: $normal-bg;
        color: $normal-color;
      }

      .read:hover {
        background-color: $read-bg;
        color: $read-color;
      }
    }

    .search {
      height: 30px;
      margin: 10px 0 10px 0;
    }

    .user-container {
      @include box(100%, calc(100% - 75px));
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
