<template>
  <div class="wave-footer-root">
    <div class="footer">
      <div class="inner-footer flex"></div>
      <div>
        <svg class="waves" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink"
          viewBox="0 24 150 28" preserveAspectRatio="none" shape-rendering="auto">
          <defs>
            <path id="gentle-wave" d="M-160 44c30 0 58-18 88-18s 58 18 88 18 58-18 88-18 58 18 88 18 v44h-352z" />
          </defs>
          <g class="parallax" v-if="isDark">
            <use xlink:href="#gentle-wave" x="48" y="0" fill="#77841133" />
            <use xlink:href="#gentle-wave" x="48" y="3" fill="#77841133" />
            <use xlink:href="#gentle-wave" x="48" y="5" fill="#77841133" />
            <use xlink:href="#gentle-wave" x="48" y="7" fill="#77841133" />
          </g>
          <g class="parallax" v-else>
            <use xlink:href="#gentle-wave" x="48" y="0" fill="#FFFFFFCF" />
            <use xlink:href="#gentle-wave" x="48" y="3" fill="#E0D2FF45" />
            <use xlink:href="#gentle-wave" x="48" y="5" fill="#ffffff41" />
            <use xlink:href="#gentle-wave" x="48" y="7" fill="#E0D2FF83" />
          </g>
        </svg>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useDark } from '@vueuse/core'
const isDark = useDark();

</script>

<style scoped lang="scss">
.wave-footer-root {
  @include box(100%, 100%);
  color: #FFFFFF;
  color: rgba(255, 255, 255, 1);

  .footer {
    position: relative;
    text-align: center;
    color: white;
  }

  .inner-footer {
    width: 100%;
    margin: 0;
    padding: 0;
  }

  .flex {
    @include flex(row, center, center);
    text-align: center;
  }

  .waves {
    position: relative;
    width: 100%;
    height: 15vh;
    margin-bottom: -7px;
    min-height: 100px;
    max-height: 150px;
  }

  .parallax>use {
    /* 使use元素执行move-forever动画 */
    animation: move-forever 25s cubic-bezier(.55, .5, .45, .5) infinite;
  }

  .parallax>use:nth-child(1) {
    /* 延迟2秒启动动画  */
    animation-delay: -2s;
    /* 设置动画持续时间为7秒 */
    animation-duration: 7s;
  }

  .parallax>use:nth-child(2) {
    animation-delay: -3s;
    animation-duration: 10s;
  }

  .parallax>use:nth-child(3) {
    animation-delay: -4s;
    animation-duration: 13s;
  }

  .parallax>use:nth-child(4) {
    animation-delay: -5s;
    animation-duration: 20s;
  }

  @keyframes move-forever {
    0% {
      transform: translate3d(-90px, 0, 0);
    }

    100% {
      transform: translate3d(85px, 0, 0);
    }
  }

  @media (max-width: 768px) {
    .waves {
      height: 40px;
      min-height: 40px;
    }
  }
}
</style>