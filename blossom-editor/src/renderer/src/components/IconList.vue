<template>
  <div class="header">
    <AppHeader v-if="props.window" simple></AppHeader>
  </div>
  <div class="icon-list-root">
    <div class="icon-desc">
      <h2>Blossom 图标</h2>
      {{ iconDesc }}
      <div v-show="activeTab === 'weblogo' || activeTab === 'blossom'" style="padding: 10px 0">
        <el-input v-model="iconSearch" size="large" style="width: 300px" placeholder="查询图标"></el-input>
      </div>
    </div>

    <el-tabs class="icon-tabs" v-model="activeTab">
      <el-tab-pane label="网站及LOGO" name="weblogo">
        <div class="icon-container">
          <div v-for="icon in filterWebLogo" class="icon-item" :key="icon.icon_id" @click="copyIcon('wl-' + icon.font_class)">
            <svg style="width: 40px; height: 40px" aria-hidden="true">
              <use :xlink:href="'#wl-' + icon.font_class"></use>
            </svg>
            <div class="icon-name">{{ 'wl-' + icon.font_class }}</div>
          </div>
        </div>
      </el-tab-pane>
      <el-tab-pane label="单色图标" name="blossom">
        <div class="icon-container">
          <div
            v-for="icon in filterBlossomIcon"
            :class="['iconbl icon-item', 'bl-' + icon.font_class]"
            :key="icon.icon_id"
            @click="copyIcon('bl-' + icon.font_class)">
            <div class="icon-name">{{ 'bl-' + icon.font_class }}</div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, shallowRef } from 'vue'
import { ElMessage } from 'element-plus'
import { isNull } from '@renderer/assets/utils/obj'
import { writeText } from '@renderer/assets/utils/electron'
import AppHeader from '@renderer/components/AppHeader.vue'
//
import blossomIcons from '@renderer/assets/iconfont/blossom/iconfont.json'
import weblogIcons from '@renderer/assets/iconfont/weblogo/iconfont.json'

onMounted(() => {
  document.title = 'Blossom 图标库'
  blossom.value = blossomIcons.glyphs
  weblogo.value = weblogIcons.glyphs.sort((w1, w2) => {
    return w1.font_class.localeCompare(w2.font_class)
  })
})

const props = defineProps({
  window: {
    type: Boolean,
    default: true
  }
})

const activeTab = ref('weblogo')
const blossom = shallowRef<any[]>([])
const weblogo = shallowRef<any[]>([])
const iconSearch = ref('')

const filterWebLogo = computed(() => {
  return weblogo.value.filter((icon: any) => {
    if (isNull(iconSearch.value)) {
      return true
    }
    return icon.font_class.toLowerCase().includes(iconSearch.value.toLowerCase())
  })
})

const filterBlossomIcon = computed(() => {
  return blossom.value.filter((icon: any) => {
    if (isNull(iconSearch.value)) {
      return true
    }
    return icon.font_class.toLowerCase().includes(iconSearch.value.toLowerCase())
  })
})

const iconDesc = computed(() => {
  if (activeTab.value === 'blossom') {
    return 'Blossom 单色图标, 页面组件使用'
  } else if (activeTab.value === 'weblogo') {
    return '网站或品牌 LOGO, 常用在文档图标, 网站收藏图标中'
  } else if (activeTab.value === 'sysimg') {
    return 'Blossom 使用的全部图片. 图片无法使用在网站收藏和文档列表中'
  }
  return ''
})

const copyIcon = (icon: string) => {
  writeText(icon)
  ElMessage.info({ message: `已复制: ${icon}`, duration: 1000, offset: 70, grouping: true })
}
</script>

<style lang="scss">
.header {
  @include box(100%, 30px);
}

.icon-list-root {
  @include box(100%, calc(100% - 30px));
  padding: 20px;
  color: var(--bl-text-color-light);

  .icon-desc {
    @include box(100%, 150px);
  }

  .icon-tabs {
    @include box(100%, calc(100% - 150px));
    overflow: hidden;

    .icon-container {
      @include box(100%, 100%);
      @include flex(row, flex-start, flex-start);
      align-content: flex-start;
      flex-wrap: wrap;
      background-color: var(--bl-html-color);
      overflow-y: scroll;

      .icon-item {
        @include flex(column, space-between, center);
        @include box(143px, 110px);
        padding: 15px 10px 10px 10px;
        font-size: 35px;
        border-right: 1px solid var(--el-border-color);
        border-bottom: 1px solid var(--el-border-color);
        transition: 0.3s;
        z-index: 1;
        cursor: pointer;

        &:hover {
          @include themeBg(#f1f1f1, #000);
          @include themeShadow(0 3px 5px 0 rgb(190, 190, 190), 0 3px 5px 0 rgba(0, 0, 0, 1));
          transform: translateY(-5px);
          z-index: 2;
        }

        .icon-name {
          @include flex(column, center, center);
          @include box(100%, 42px);
          color: var(--bl-text-color-light);
          text-align: center;
          font-size: 11px;
          overflow: hidden;
          white-space: normal;
          word-wrap: break-word;
          user-select: text;
        }
      }
    }

    .el-tabs__content {
      height: calc(100% - 55px);
      overflow: scroll;
      border: 1px solid var(--el-border-color);
    }
  }
}
</style>
