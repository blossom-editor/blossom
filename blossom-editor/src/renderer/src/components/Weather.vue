<template>
  <div class="weather-root">
    <bl-row class="location" just="flex-end">
      {{ weather.location.name }}
      <el-tooltip placement="top" effect="light" :show-after="1000" :hide-after="0" :auto-close="3000">
        <span class="iconbl bl-refresh-smile" @click="refresh"></span>
        <template #content>
          刷新天气信息
          <br />
          注意: 请勿频繁刷新, 避免超出三方免费额度
        </template>
      </el-tooltip>
    </bl-row>

    <!-- 现在 -->
    <div class="now hover-dark">
      <div class="weather-title"></div>
      <div class="weather-body">
        <img :src="weather.now.img" style="width: 190px; height: 190px" />
        <!-- <img src="@renderer/assets/imgs/weather/qing-moon.png" style="width: 190px;height: 190px;"> -->
        <div class="temp-wrapper">
          <!-- 温度 -->
          <span class="now-temp">
            {{ weather.now.temp }}
          </span>
          <!-- 温度的单位和说明 -->
          <div class="now-temp-extra">
            <span style="font-size: 25px">°C</span>
            <span style="">{{ weather.now.text }}</span>
          </div>
        </div>
      </div>

      <div class="weather-footer">
        <span>未来两小时: {{ weather.hourly[0].text }}</span>
      </div>
    </div>

    <!-- 今天 -->
    <div class="today hover-dark">
      <div class="weather-title">Today</div>
      <div class="weather-body">
        <img :src="weather.daily[0].img" style="width: 40px; height: 40px" />
        <!-- <img src="@renderer/assets/imgs/weather/feng.png" style="width: 40px;height: 40px;"> -->
        <span>{{ weather.daily[0].tempMax }}°C</span>
        ~
        <span>{{ weather.daily[0].tempMin }}°C </span>
      </div>
      <div class="weather-footer">
        <span>{{ weather.daily[0].textDay }}</span>
      </div>
    </div>

    <!-- 明天 -->
    <div class="tomorrow hover-dark">
      <div class="weather-title">Tomorrow</div>
      <div class="weather-body">
        <img :src="weather.daily[1].img" style="width: 40px; height: 40px" />
        <span>{{ weather.daily[1].tempMax }}°C</span>
        ~
        <span>{{ weather.daily[1].tempMin }}°C</span>
      </div>
      <div class="weather-footer">
        <span>{{ weather.daily[1].textDay }}</span>
      </div>
    </div>

    <!-- 后天 -->
    <div class="day-after hover-dark">
      <div class="weather-title">
        <span>Tomorrow</span>
        <span>After</span>
      </div>
      <div class="weather-body">
        <img :src="weather.daily[2].img" style="width: 40px; height: 40px" />
        <span>{{ weather.daily[2].tempMax }}°C</span>
        ~
        <span>{{ weather.daily[2].tempMin }}°C</span>
      </div>
      <div class="weather-footer">
        <span>{{ weather.daily[2].textDay }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useUserStore } from '@renderer/stores/user'
import { getAll, refreshApi } from '@renderer/api/weather'
import { useLifecycle } from '@renderer/scripts/lifecycle'
import { isBlank, isNotBlank } from '@renderer/assets/utils/obj'

useLifecycle(
  () => {
    refreshWeatherTask()
    getWeather()
  },
  () => {
    getWeather()
  }
)

const userStore = useUserStore()

const getImgUrl = (name: string) => {
  let iconValue = replacePrefix(name)
  if (isBlank(iconValue)) {
    return new URL(`../assets/imgs/weather/qing.png`, import.meta.url).href
  }
  return new URL(`../assets/imgs/weather/${iconValue}.png`, import.meta.url).href
}

const replacePrefix = (icon: string) => {
  if (icon && isNotBlank(icon)) {
    return icon.replaceAll('#wt-', '')
  }
  return ''
}

const weather = ref({
  location: {
    name: '未配置天气'
  },
  now: {
    iconValue: '#wt-qing',
    img: getImgUrl('qing'),
    temp: '0',
    text: '晴'
  },
  hourly: [{ text: '0' }],
  daily: [
    { iconValueDay: '#wt-qing', img: getImgUrl('qing-s'), tempMin: '0', tempMax: '0', textDay: '晴' },
    { iconValueDay: '#wt-qing', img: getImgUrl('qing-s'), tempMin: '0', tempMax: '0', textDay: '晴' },
    { iconValueDay: '#wt-qing', img: getImgUrl('qing-s'), tempMin: '0', tempMax: '0', textDay: '晴' }
  ]
})

const getWeather = () => {
  getAll({ location: userStore.userinfo.location }).then((resp) => {
    if (resp.data.now) {
      if (resp.data.now.iconValue === '#wt-qing') {
        let nowHours = new Date().getHours()
        if (nowHours < 7 || nowHours > 19) {
          resp.data.now.img = getImgUrl('qing-moon')
        } else {
          resp.data.now.img = getImgUrl(resp.data.now.iconValue)
        }
      } else {
        resp.data.now.img = getImgUrl(resp.data.now.iconValue)
      }
    }
    if (resp.data.daily) {
      resp.data.daily[0].img = getImgUrl(resp.data.daily[0].iconValueDay + '-s')
      resp.data.daily[1].img = getImgUrl(resp.data.daily[1].iconValueDay + '-s')
      resp.data.daily[2].img = getImgUrl(resp.data.daily[2].iconValueDay + '-s')
    }
    weather.value = { ...weather.value, ...resp.data }
  })
}
const refresh = () => {
  refreshApi().then((_) => {
    getWeather()
  })
}

const refreshWeatherTask = () => {
  // 20 分钟刷新一次日期
  setInterval(getWeather, 1000 * 60 * 20)
}
</script>

<style scoped lang="scss">
.weather-root {
  @include flex(row, center, flex-end);
  @include font(15px, 500, 'jellee');
  @include themeColor(#ffffff, #d8d8d8);
  @include themeText(2px 3px 4px rgba(107, 104, 104, 0.5), 2px 3px 4px rgba(39, 39, 39, 0.5));
  min-height: 250px;
  max-height: 250px;
  position: relative;
  border-radius: 10px;
  transition: box-shadow 0.5s;
  z-index: 99;

  .location {
    @include font(12px, 300);
    @include absolute(30px, 10px, '', '');
    color: var(--bl-text-color-light);
    text-shadow: var(--bl-text-shadow-light);

    @keyframes rotation {
      0% {
        transform: rotate(0deg);
      }

      100% {
        transform: rotate(-360deg);
      }
    }

    .bl-refresh-smile {
      cursor: pointer;
      margin-left: 3px;
      transition: color 0.3s;

      &:hover {
        animation: rotation 10s linear infinite;
        text-shadow: none;
        color: var(--el-color-primary);
      }
    }
  }

  .weather-mask {
    @include flex(column, center, center);
    @include box(100%, 100%);
    position: absolute;
    border-radius: 10px;
    top: 0;
    left: 0;
    background-color: var(--el-color-primary-light-2);

    .big-text {
      cursor: pointer;
      @include font(25px, 800);
    }

    .small-text {
      cursor: pointer;
      @include font(14px, 500);
    }

    .masking {
      width: 100%;
      text-align: center;
      cursor: pointer;
      flex-shrink: 0;
    }
  }

  .weather-title {
    @include flex(column, flex-start, flex-end);
    @include box(100%, 15%);
    padding: 0 10px;
    font-size: 11px;
  }

  .weather-body {
    @include flex(column, center, center);
    @include box(100%, 70%);
  }

  .weather-footer {
    @include flex(row, center, center);
    @include box(100%, 15%);
    @include font(11px, 500);
  }

  .now {
    @include box(140px, 200px);
    border-top-left-radius: 10px;
    border-bottom-left-radius: 10px;
    background-color: var(--el-color-primary);
    transition:
      box-shadow 0.3s,
      width 0.2s;
    margin-left: 10px;

    svg {
      @include box(200px, 200px);
      position: absolute;
      top: -30px;
      z-index: 100;
    }

    img {
      @include box(200px, 200px);
      position: absolute;
      top: -30px;
      z-index: 100;
    }

    .temp-wrapper {
      @include flex(row, center, center);
      width: 100%;
      margin-top: 70px;
    }

    &-temp {
      @include flex(row, center, center);
      @include font(60px, 900);
      height: 100%;
    }

    &-temp-extra {
      @include flex(column, center);
      @include font(16px, 700);
      height: 100%;
    }

    &:hover {
      z-index: 15;
    }
  }

  .today,
  .tomorrow,
  .day-after {
    @include box(100px, 200px);
    transition:
      box-shadow 0.3s,
      width 0.2s,
      height 0.2s;

    &:hover {
      z-index: 15;
    }

    .weather-body {
      svg {
        @include box(50px, 50px);
      }

      span {
        @include font(15px, 700);
        margin-top: 10px;
      }
    }
  }

  .today {
    background-color: var(--el-color-primary-light-3);
  }

  .tomorrow {
    background-color: var(--el-color-primary-light-5);
  }

  .day-after {
    background-color: var(--el-color-primary-light-7);
    border-top-right-radius: 10px;
    border-bottom-right-radius: 10px;
  }
}

.hover-dark {
  &:hover {
    box-shadow: 0 2px 10px 3px rgba(0, 0, 0, 0.3);
  }
}
</style>
