<template>
  <div class="userinfo-setting-root">
    <div class="info-title">
      <div class="info" style="width: 350px; font-size: 17px">{{ curUser?.nickName }}</div>
    </div>

    <div class="content">
      <div :class="curUser.delTime !== '0' ? 'warn-bg-zebra' : ''">
        <bl-row class="info-group">
          <bl-col width="80px">
            <img v-if="curUser.avatar != ''" class="avatar" :src="curUser.avatar" />
            <img v-else class="avatar" src="@renderer/assets/imgs/default_user_avatar.jpg" />
          </bl-col>
          <bl-col width="300px" height="100px" just="space-around" align="flex-start" style="padding-left: 20px">
            <div class="info">ID {{ curUser.id }}</div>
            <div class="info">昵称 {{ curUser.nickName }}</div>
            <div class="info">用户名 {{ curUser.username }}</div>
            <bl-row just="space-between">
              <div>创建于 {{ curUser.creTime }}</div>
              <div :class="['type', curUser.type === 1 ? 'admin' : curUser.type === 2 ? 'normal' : 'read']">
                <span v-if="curUser.type === 1" class="iconbl bl-a-Securitypermissions-line"></span
                >{{ curUser.type === 1 ? '管理员' : curUser.type === 2 ? '普通' : '只读' }}
              </div>
            </bl-row>
          </bl-col>
        </bl-row>

        <bl-row class="info-group stat-group" just="space-around">
          <div class="iconbl bl-a-texteditorhighlightcolor-line"></div>
          <bl-col align="flex-end" width="30%">
            <div style="margin-bottom: 5px">{{ formartNumber(articleStat.articleCount) }} 篇文章</div>
            <div>{{ formartNumber(articleStat.articleWords) }} 字</div>
          </bl-col>
          <div class="iconbl bl-image--line"></div>
          <bl-col align="flex-end" width="30%">
            <div style="margin-bottom: 5px">{{ formartNumber(pictureStat.pictureCount) }} 个文件</div>
            <div>{{ formatFileSize(pictureStat.pictureSize) }}</div>
          </bl-col>
        </bl-row>
      </div>

      <el-divider style="margin: 0"></el-divider>

      <div class="info-group">
        <div class="row">用户类型</div>
        <el-select class="row" size="default" v-model="curUser.type" style="width: 100%">
          <el-option :value="1" label="管理员">
            <span style="float: left">管理员</span>
            <span style="float: right; color: var(--bl-text-color-light)">允许管理用户，修改服务器配置</span>
          </el-option>
          <el-option :value="2" label="普通用户">
            <span style="float: left">普通用户</span>
            <span style="float: right; color: var(--bl-text-color-light)">除管理员特权外的所有功能</span>
          </el-option>
          <el-option :value="3" label="只读用户">
            <span style="float: left">只读用户</span>
            <span style="float: right; color: var(--bl-text-color-light)">只有查看权限，无法修改与删除</span>
          </el-option>
        </el-select>

        <div class="row">和风天气地区</div>
        <el-input class="row" size="default" v-model="curUser.location">
          <template #append>
            <el-button @click="openExtenal('https://github.com/qwd/LocationList/blob/master/China-City-List-latest.csv')">查看城市代码</el-button>
          </template>
        </el-input>

        <div class="row">博客地址</div>
        <el-input type="textarea" class="row" size="default" :row="2" resize="none" v-model="curUser.userParams.WEB_ARTICLE_URL">
          <template #prefix>
            <div class="iconbl bl-blog"></div>
          </template>
        </el-input>
      </div>

      <el-divider style="margin: 0"></el-divider>

      <bl-row just="space-between" class="info-group">
        <el-button-group>
          <el-button size="default" text bg type="danger" @click="del">删除</el-button>
          <el-button size="default" text bg type="warning" @click="disabled">
            {{ curUser.delTime === '0' ? '禁用' : '启用' }}
          </el-button>
        </el-button-group>
        <el-button size="default" type="primary" @click="upd">保存</el-button>
      </bl-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { userinfoAdminApi, userDisabledApi, userUpdAdminApi, userDelReq } from '@renderer/api/auth'
import { articleWordsUserApi, pictureStatUserApi, userParamUpdAdminApi } from '@renderer/api/blossom'
import { DEFAULT_USER_INFO } from '@renderer/stores/user'
import type { Userinfo } from '@renderer/stores/user'
import { openExtenal } from '@renderer/assets/utils/electron'
import { formartNumber, formatFileSize } from '@renderer/assets/utils/util'
import { ElMessage, ElMessageBox } from 'element-plus'

onMounted(() => {
  getUserInfo()
})

//#region ----------------------------------------< 用户列表 >--------------------------------------

const props = defineProps({
  id: { type: String, default: '' }
})

const articleStat = ref({ articleCount: 0, articleWords: 0 })
const pictureStat = ref({ pictureCount: 0, pictureSize: 0 })
const curUser = ref<Userinfo>(DEFAULT_USER_INFO)
const getUserInfo = () => {
  userinfoAdminApi({ id: props.id }).then((resp) => {
    curUser.value = resp.data
  })
  articleWordsUserApi({ id: props.id }).then((resp) => {
    articleStat.value = resp.data
  })
  pictureStatUserApi({ id: props.id }).then((resp) => {
    pictureStat.value = resp.data
  })
}

/**
 * 禁用启用用户
 */
const disabled = () => {
  if (curUser.value.delTime === '0') {
    ElMessageBox.confirm(
      `禁用后该用户将无法再次登录, 是否禁用?<br/>
    <div style="width:100%;text-align:center;font-size:14px">${curUser.value.nickName} (${curUser.value.id})</div>`,
      {
        confirmButtonText: '确定禁用',
        cancelButtonText: '取消',
        type: 'warning',
        dangerouslyUseHTMLString: true,
        draggable: true
      }
    ).then(() => {
      userDisabledApi({ id: props.id, delTime: Date.now() }).then((_resp) => {
        emits('saved')
      })
    })
  } else {
    userDisabledApi({ id: props.id, delTime: 0 }).then((_resp) => {
      emits('saved')
    })
  }
}

/**
 * 修改用户信息
 */
const upd = () => {
  ElMessageBox.confirm(
    `修改后各项配置会在用户重新登录后生效, 是否保存用户信息?<br/>
    <div style="width:100%;text-align:center;font-size:14px">${curUser.value.nickName} (${curUser.value.id})</div>`,
    {
      confirmButtonText: '确定保存',
      cancelButtonText: '我再想想',
      type: 'warning',
      dangerouslyUseHTMLString: true,
      draggable: true
    }
  ).then(() => {
    userUpdAdminApi({
      id: props.id,
      type: curUser.value.type,
      location: curUser.value.location
    }).then((_resp) => {
      userParamUpdAdminApi({
        userId: props.id,
        paramName: 'WEB_ARTICLE_URL',
        paramValue: curUser.value.userParams.WEB_ARTICLE_URL
      }).then((_resp) => {
        emits('saved')
        ElMessage.info('用户信息已更新')
      })
    })
  })
}

const del = () => {
  ElMessageBox.confirm(
    `删除后该用户的<span style="color:var(--el-color-danger)">所有数据都将无法找回</span>, 是否确认删除?<br/>
    <div style="width:100%;text-align:center;font-size:14px">${curUser.value.nickName} (${curUser.value.id})</div>`,
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '我再想想',
      type: 'warning',
      dangerouslyUseHTMLString: true,
      draggable: true
    }
  ).then(() => {
    userDelReq({ id: props.id }).then((_resp) => {
      emits('saved')
      ElMessage.info('用户已删除')
    })
  })
}

const emits = defineEmits(['saved'])

//#endregion
</script>

<style lang="scss" scoped>
@import '@renderer/assets/styles/bl-dialog-info';
@import '@renderer/views/index/styles/user-setting';
.userinfo-setting-root {
  @include box(100%, 100%);
  @include font(13px, 300);

  .info {
    @include ellipsis();
    width: 100%;
  }

  .label {
    margin-top: 10px;
  }

  .content {
    @include box(100%, calc(100% - 50px));
    position: relative;

    .info-group {
      padding: 10px;

      .row {
        margin-bottom: 10px;
      }
    }

    .stat-group {
      font-size: 12px;
      .bl-a-texteditorhighlightcolor-line,
      .bl-image--line {
        @include themeText(2px 4px 5px rgba(134, 134, 134, 0.3), 2px 4px 7px #222222);
        @include themeColor(#8a8a8a, #656565);
        position: absolute;
        font-size: 30px;
      }
      .bl-a-texteditorhighlightcolor-line {
        left: 30px;
      }

      .bl-image--line {
        left: 230px;
      }
    }

    .avatar {
      @include box(80px, 80px);
      @include themeBrightness();
      @include themeShadow(2px 4px 7px 2px rgba(134, 134, 134, 0.3), 2px 4px 7px 2px #000000);
      @include themeBorder(2px, #a8abb2, #707070);
      object-fit: cover;
      border-radius: 6px;
      margin-left: 10px;
    }
  }
}
</style>
