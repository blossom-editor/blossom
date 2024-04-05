<template>
  <div class="web-collect-root">
    <div class="save-form" :style="saveFormStyle">
      <bl-row height="50px" just="space-between" class="save-form-title">
        <div>{{ webForm.operator }}</div>
        <div class="iconbl bl-a-closeline-line" @click="closeForm"></div>
      </bl-row>
      <bl-row>
        <el-form label-width="70px" v-model="webForm" style="width: calc(100% - 90px)">
          <el-form-item label="网站名称">
            <el-input v-model="webForm.name" style="width: 100%" />
          </el-form-item>
          <el-form-item label="网站地址">
            <el-input v-model="webForm.url" placeholder="http://..." />
          </el-form-item>
          <el-form-item label="网站图标">
            <el-input v-model="webForm.icon" placeholder="wl-">
              <template #append>
                <el-tooltip content="查看所有图标" effect="light" placement="top" :hide-after="0">
                  <div style="cursor: pointer; font-size: 20px" @click="openNewIconWindow()">
                    <svg class="icon" aria-hidden="true">
                      <use xlink:href="#wl-yanfa"></use>
                    </svg>
                  </div>
                </el-tooltip>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item label="图标地址">
            <el-input v-model="webForm.img" placeholder="http://..." />
          </el-form-item>
          <el-form-item label="网站类型">
            <el-select v-model="webForm.type" class="m-2" placeholder="选择类型" style="width: 140px">
              <el-option label="日常 - Daily" value="daily" />
              <el-option label="工作 - Work" value="work" />
              <el-option label="其他 - Other" value="other" />
            </el-select>
            <el-input-number v-model="webForm.sort" :min="1" style="width: 80px; margin-left: 10px" />
          </el-form-item>
        </el-form>
        <div class="web-item-card hover">
          <img v-if="isNotBlank(webForm.img)" :src="webForm.img" />
          <svg v-else style="width: 40px; height: 40px" aria-hidden="true">
            <use :xlink:href="'#' + webForm.icon"></use>
          </svg>
          <div class="web-name">{{ webForm.name }}</div>
        </div>
      </bl-row>
      <bl-row just="space-between" style="margin-bottom: 10px; padding: 0 20px">
        <el-button type="danger" @click="delWeb" :disabled="!(webForm.id > 0)">删除</el-button>
        <el-button type="primary" @click="saveWeb">保存</el-button>
      </bl-row>
    </div>

    <bl-row just="flex-end" class="web-collect-title">
      <span class="title-remind" style="">右键点击卡片修改</span>
      <span v-if="viewStyle.isWebCollectCard" class="iconbl bl-array-line" @click="showWebCollectCard(false)"></span>
      <span v-else class="iconbl bl-article-line container-operator" @click="showWebCollectCard(true)" />
      <span class="iconbl bl-add-line" @click="showForm($event)"></span>
      <span class="iconbl bl-refresh-smile" @click="getWebAll"></span>
      Quick Access
    </bl-row>

    <div class="web-item-container">
      <div v-if="isEmpty(data)" class="placeholder">
        无收藏网址，点击上方 <span class="iconbl bl-add-line" style="padding-right: 10px"></span>添加
      </div>
      <div v-for="(collect, index) in data" @click="closeForm">
        <bl-row just="flex-end" class="web-collect-group" :style="index == 0 ? 'marginTop:0' : ''">
          {{ collect.title }}
        </bl-row>
        <div class="web-collect-content">
          <div
            v-for="web in collect.webs"
            :key="web.name"
            :class="[
              'web-item',
              viewStyle.isWebCollectCard ? 'web-item-card' : 'web-item-list',
              viewStyle.isGlobalShadow ? 'web-item-heavy' : 'web-item-light'
            ]"
            @click="openExtenal(web.url)"
            @contextmenu="showForm($event, web)">
            <img v-if="isNotBlank(web.img)" :src="web.img" />
            <svg v-else aria-hidden="true">
              <use :xlink:href="'#' + web.icon"></use>
            </svg>
            <div class="web-name">{{ web.name }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessageBox } from 'element-plus'
import { webAllApi, webSaveApi, webDelApi } from '@renderer/api/web'
import { isNotBlank, isNotNull } from '@renderer/assets/utils/obj'
import { openExtenal, openNewIconWindow } from '@renderer/assets/utils/electron'
import { useLifecycle } from '@renderer/scripts/lifecycle'
import { ViewStyle, useConfigStore } from '@renderer/stores/config'
import { isEmpty } from 'lodash'

const configStore = useConfigStore()
const viewStyle = ref<ViewStyle>(configStore.viewStyle)

useLifecycle(
  () => getWebAll(),
  () => getWebAll()
)

const data = ref<any>([])
const getWebAll = () => {
  webAllApi().then((resp) => {
    if (isEmpty(resp.data.daily) && isEmpty(resp.data.work) && isEmpty(resp.data.other)) {
      data.value = []
      return
    }
    let webs = [
      { title: 'Daily', webs: resp.data.daily },
      { title: 'Work', webs: resp.data.work },
      { title: 'Other', webs: resp.data.other }
    ]
    data.value = webs
  })
}

const webForm = ref<any>({})
const saveFormStyle = ref<any>({})
const showForm = (event: MouseEvent, updWeb?: any) => {
  event.preventDefault()
  saveFormStyle.value = { display: 'block' }
  if (isNotNull(updWeb)) {
    Object.assign(webForm.value, updWeb)
    webForm.value.operator = '修改网站'
  } else {
    webForm.value = { operator: '新增网站', name: '', url: '', icon: '', img: '', type: '', sort: 1 }
  }
}
const closeForm = () => {
  saveFormStyle.value = { display: 'none' }
}
const saveWeb = () => {
  webSaveApi(webForm.value).then((_resp) => {
    getWebAll()
    closeForm()
  })
}
const delWeb = () => {
  ElMessageBox.confirm(`删除后将不可恢复, 是否确定删除[${webForm.value.name}]?`, {
    confirmButtonText: '确定删除',
    cancelButtonText: '我再想想',
    type: 'info',
    draggable: true
  }).then(() => {
    webDelApi(webForm.value).then((_resp) => {
      getWebAll()
      closeForm()
    })
  })
}

const showWebCollectCard = (card: boolean) => {
  viewStyle.value.isWebCollectCard = card
  configStore.setViewStyle(viewStyle.value)
}
</script>

<style scoped lang="scss">
.web-collect-root {
  @include box(100%, 100%);
  @include themeColor(#bdbdbd, #a3a6ad);

  .iconbl {
    transition: color 0.3s;
    cursor: pointer;

    &:hover {
      color: var(--el-color-primary);
    }
  }

  .web-collect-title {
    @include box(100%, 31px);
    @include font(14px, 500);
    flex-wrap: wrap-reverse;
    align-content: flex-end;
    text-shadow: var(--bl-text-shadow);
    padding: 5px 10px;

    .iconbl {
      font-size: 17px;
      margin-right: 10px;
      cursor: pointer;
    }

    .title-remind {
      color: var(--el-color-primary);
      font-size: 11px;
      padding: 0 10px;
      opacity: 0;
      transition: 0.3s;
    }

    &:hover {
      .title-remind {
        opacity: 1;
      }
    }
    .bl-refresh-smile {
      &:hover {
        animation: rotation 10s linear infinite;
        text-shadow: none;
      }
    }

    @keyframes rotation {
      0% {
        transform: rotate(0deg);
      }

      100% {
        transform: rotate(-360deg);
      }
    }
  }

  .web-collect-group {
    @include themeColor(#bdbdbd, #a3a6ad);
    @include font(14px, 300);
    text-shadow: var(--bl-text-shadow);
    padding: 5px 20px;
  }

  .web-collect-content {
    @include flex(row, flex-start, flex-start);
    align-content: flex-start;
    flex-wrap: wrap;
    padding-left: 8px;
  }

  .web-item-container {
    @include box(100%, calc(100% - 31px));
    overflow-y: scroll;

    .placeholder {
      @include font(15px, 300);
      padding: 20px 0 0 20px;
      color: var(--bl-text-color-light);
    }

    &::-webkit-scrollbar {
      width: 4px;
      height: 3px;
    }

    &::-webkit-scrollbar-thumb {
      background-color: var(--bl-scroll-color);
      width: 4px;
      height: 3px;
      transition: background-color 0.3s;
      &:hover {
        background-color: var(--bl-scroll-color-hover);
      }
    }
  }

  .web-item {
    transition:
      transform 0.2s,
      box-shadow 0.2s;
    cursor: pointer;
    img,
    svg {
      object-fit: contain;
      filter: var(--bl-drop-shadow-star);
    }
  }

  .web-item-card {
    @include flex(column, space-between, center);
    @include box(80px, 110px);
    @include themeBrightness(100%, 80%);
    padding: 15px 10px 10px 10px;
    border-radius: 10px;

    img,
    svg {
      width: 40px;
      height: 40px;
    }

    .web-name {
      @include box(100%, 32px);
      @include themeColor(#a5a5a5, #929292);
      text-shadow: var(--bl-text-shadow);
      text-align: center;
      font-size: 11px;
      overflow: hidden;
      white-space: normal;
    }
  }

  .web-item-list {
    @include flex(row, flex-start, center);
    @include box(200px, auto);
    border-radius: 4px;
    padding: 3px 3px 3px 5px;
    margin-bottom: 5px;
    color: var(--bl-text-color-light);

    img,
    svg {
      width: 18px;
      height: 18px;
    }

    .web-name {
      @include ellipsis();
      width: 175px;
      font-size: 13px;
      padding-left: 5px;
    }
  }

  .web-item-heavy {
    &:hover {
      @include themeShadow(0 3px 5px 0 rgb(190, 190, 190), 0 3px 5px 0 rgba(0, 0, 0, 1));
      transform: translateY(-5px);
    }
  }

  .web-item-light {
    transition: background-color 0.2s;
    &:hover {
      @include themeBg(#f5f5f5, #171717);
    }
  }

  .web-item-card.hover {
    @include themeShadow(0 3px 5px 0 rgb(190, 190, 190), 0 3px 5px 0 rgba(0, 0, 0, 1));
    transform: translateY(-5px);
  }

  .save-form {
    @include box(100%, 270px);
    @include absolute(0, 0);
    @include themeBg(#fffffff1, #000000dd);
    @include themeShadow(3px 3px 10px 1px #cccccc, 3px 3px 10px 1px #000000);
    z-index: 99999999;
    border-top-left-radius: 10px;
    border-bottom-left-radius: 10px;
    display: none;

    .save-form-title {
      border-bottom: 1px solid var(--el-border-color);
      padding: 10px;
    }

    .el-form {
      padding: 10px;
      :deep(.el-form-item--small) {
        margin-bottom: 8px;
      }
    }
  }
}
</style>
