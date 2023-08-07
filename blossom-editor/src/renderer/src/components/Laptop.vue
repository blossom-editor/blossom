<template>
  <div class="laptop-root">
    <!-- 电脑 -->
    <img class="laptop" v-if="isDark" src="@renderer/assets/imgs/pe/laptop-dark.png">
    <img class="laptop" v-else src="@renderer/assets/imgs/pe/laptop.png">

    <!-- 电脑摆件 -->
    <div v-if="isDawning()">
      <img class="furniture" v-if="isDark" style="left: -20px;bottom: -35px;"
        src="@renderer/assets/imgs/pe/headset-dark.png">
      <img class="furniture" v-else style="left: -20px;bottom: -35px;" src="@renderer/assets/imgs/pe/headset.png">
    </div>

    <img v-else-if="isMorning()" class="furniture" style="left: 10px;bottom: 10px;"
      src="@renderer/assets/imgs/plant/cactus.png">

    <img v-else-if="isAfternoon()" class="furniture flip" style="height: 40px;left: 15px;bottom: 5px;"
      src="@renderer/assets/imgs/plan/coffee.png">

    <div v-else-if="isEvening() && randomBoolean()">
      <img v-if="isDark" class="furniture flip" style="left: -25px;bottom: -20px;"
        src="@renderer/assets/imgs/pe/watch-dark.png">
      <img v-else class="furniture flip" style="left: -25px;bottom: -20px;" src="@renderer/assets/imgs/pe/watch.png">
    </div>

    <div v-else-if="isEvening()">
      <img v-if="isDark" class="furniture" style="left: -20px;bottom: 00px;"
        src="@renderer/assets/imgs/pe/sound-dark.png">
      <img v-else class="furniture flip" style="left: -10px;bottom: -30px;" src="@renderer/assets/imgs/pe/phone.png">
    </div>
  </div>
</template>

<script setup lang="ts">
import { useDark } from '@vueuse/core'
import { nowWhen, randomBoolean } from '@renderer/assets/utils/util'
import { onActivated } from 'vue';

let now: string = nowWhen()
const isDark = useDark()

onActivated(() => {
  now = nowWhen()
})

const isDawning = () => {
  return now == 'Dawning'
}

const isMorning = () => {
  return now == 'Morning'
}

const isAfternoon = () => {
  return now == 'Afternoon'
}

const isEvening = () => {
  return now == 'Evening'
}
</script>

<style scoped lang="scss">
.laptop-root {
  @include box(200px, 200px);
  position: relative;

  .laptop {
    @include absolute(0px, '', '', -20px);
    height: 240px;
  }

  .furniture {
    height: 100px;
    position: absolute;
    z-index: 10;
  }

  .furniture.flip {
    transform: scaleX(-1);
    filter: FlipH;
  }
}
</style>