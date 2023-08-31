<template>
  <div class="icon-list-root">
    <div class="icon-desc">
      <h2>Blossom 图标</h2>
      {{ iconDesc }}
      <div v-show="activeTab === 'weblogo'" style="padding: 10px 0;">
        <el-input v-model="iconSearch" size="large" style="width: 300px;" placeholder="查询图标"></el-input>
      </div>
    </div>

    <el-tabs class="icon-tabs" v-model="activeTab">
      <el-tab-pane label="网站及LOGO" name="weblogo">
        <div class="icon-container">
          <div v-for="icon in filterWebLogo" class="icon-item" :key="icon.icon_id">
            <svg style="width: 40px;height: 40px;" aria-hidden="true">
              <use :xlink:href="'#wl-' + icon.font_class"></use>
            </svg>
            <div class="icon-name">{{ 'wl-' + icon.font_class }}</div>
          </div>
        </div>
      </el-tab-pane>
      <el-tab-pane label="单色图标" name="blossom">
        <div class="icon-container">
          <div v-for="icon in blossom" :class="['iconbl icon-item', 'bl-' + icon.font_class]" :key="icon.icon_id">
            <div class="icon-name">{{ 'bl-' + icon.font_class }}</div>
          </div>
        </div>
      </el-tab-pane>
      <el-tab-pane label="系统图片" name="sysimg">
        <div class="icon-container">
          <div v-for="img in imgs" class="icon-item" :key="img.icon_id">
            <img style="width: 40px;height: 40px;object-fit: contain;" :src="img" />
            <div class="icon-name">{{ img.substring(img.lastIndexOf('/') + 1) }}</div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, shallowRef } from 'vue'
import blossomIcons from '@renderer/assets/iconfont/blossom/iconfont.json'
import weblogIcons from '@renderer/assets/iconfont/weblogo/iconfont.json'
import { isNull } from '@renderer/assets/utils/obj'

const getImg = (img: string) => {
  return new URL(`../assets/imgs/${img}`, import.meta.url).href
}

const activeTab = ref('weblogo')

const blossom = shallowRef<any[]>([])
const weblogo = shallowRef<any[]>([])
const imgs = shallowRef<any[]>([
  getImg('plan/base-awesome.png'),
  getImg('plan/base-cool.png'),
  getImg('plan/base-cry.png'),
  getImg('plan/base-learning.png'),
  getImg('plan/base-seesee.png'),
  getImg('plan/dog.png'),
  getImg('plan/cat-kiss.png'),
  getImg('plan/cat-nice.png'),
  getImg('plan/cat-shock.png'),
  getImg('plan/cat-smile.png'),
  getImg('plan/cat.png'),
  getImg('plan/computer.png'),
  getImg('plan/coffee.png'),
  getImg('plan/juice.png'),
  getImg('plan/beer.png'),
  getImg('plan/ball.png'),
  getImg('plan/bike.png'),
  getImg('plan/crossbones.png'),
  getImg('plan/hand-ok.png'),
  getImg('plan/hand-write.png'),
  getImg('plan/love.png'),
  getImg('plan/sleep.png'),
  // note
  getImg('note/cd.png'),
  getImg('note/dustbin.png'),
  getImg('note/note.png'),
  getImg('note/pin.png'),
  getImg('note/plane.png'),
  // pe
  getImg('pe/headset-dark.png'),
  getImg('pe/headset.png'),
  getImg('pe/laptop-dark.png'),
  getImg('pe/laptop.png'),
  getImg('pe/phone.png'),
  getImg('pe/sound-dark.png'),
  getImg('pe/watch-dark.png'),
  getImg('pe/watch.png'),
  // plant
  getImg('plant/02.png'),
  getImg('plant/08.png'),
  getImg('plant/cactus.png'),
  // weather
  getImg('weather/daxue-s.png'),
  getImg('weather/daxue-s.png'),
  getImg('weather/daxue.png'),
  getImg('weather/duoyun-s.png'),
  getImg('weather/duoyun.png'),
  getImg('weather/feng-s.png'),
  getImg('weather/feng.png'),
  getImg('weather/leijiayu-s.png'),
  getImg('weather/leijiayu.png'),
  getImg('weather/qing-hot-s.png'),
  getImg('weather/qing-hot.png'),
  getImg('weather/qing-s.png'),
  getImg('weather/qing.png'),
  getImg('weather/qingduoyun-s.png'),
  getImg('weather/qingduoyun.png'),
  getImg('weather/taiyangyu-s.png'),
  getImg('weather/taiyangyu.png'),
  getImg('weather/wu-s.png'),
  getImg('weather/wu.png'),
  getImg('weather/xue-s.png'),
  getImg('weather/xue.png'),
  getImg('weather/yin-s.png'),
  getImg('weather/yin.png'),
  getImg('weather/yu-s.png'),
  getImg('weather/yu.png'),
  getImg('weather/yun-s.png'),
  getImg('weather/yun.png'),
  getImg('weather/zhongyu-s.png'),
  getImg('weather/zhongyu.png'),

])

const iconSearch = ref('')

const filterWebLogo = computed(() => {
  return weblogo.value.filter((icon: any) => {
    if (isNull(iconSearch.value)) {
      return true;
    }
    return icon.font_class.toLowerCase().includes(iconSearch.value.toLowerCase())
  })
})

onMounted(() => {
  blossom.value = blossomIcons.glyphs
  weblogo.value = weblogIcons.glyphs.sort((w1, w2) => {
    return w1.font_class.localeCompare(w2.font_class)
  })
})

const iconDesc = computed(() => {
  if (activeTab.value === 'blossom') {
    return 'Blossom 单色图标, 页面组件使用'
  }
  else if (activeTab.value === 'weblogo') {
    return '网站或品牌 LOGO, 常用在文档图标, 网站收藏图标中'
  }
  else if (activeTab.value === 'sysimg') {
    return 'Blossom 使用的全部图片.'
  }
  return ''
})
</script>

<style lang="scss">
.icon-list-root {
  @include box(100%, 100%);
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
      overflow: scroll;
      overflow-y: overlay;

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