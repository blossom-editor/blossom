<template>
  <div class="container">
    <span class="hover-me">Hover me !</span>
    <div class="tooltip">
      <p>Heyy👋</p>
    </div>
  </div>
</template>

<script setup lang="ts">
</script>
<style scoped lang="scss">
.container {
  font-size: 18px;
  color: #333;
  position: relative;
  cursor: pointer;
  display: inline-block;
}

.hover-me {
  position: relative;
  z-index: 1;
  text-decoration: underline;
  text-underline-offset: 4px;
  text-decoration-color: #333;
}

.tooltip {
  width: 100%;
  height: 10px;
  background: #ffffff;
  padding: 0.25em;
  text-align: center;
  position: absolute;
  top: 40px;
  left: 0;
  opacity: 0;
  visibility: hidden;
  transform-origin: center top;
  box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.05);
  transition: opacity 0.3s ease-in-out;
}

.tooltip::before {
  content: "";
  position: absolute;
  top: -8px; /* 之前是bottom: -8px;，现在改为top以移动到上方 */
  left: 15%; /* 之前是left: 80%;，现在改为0%以移动到左侧 */
  transform: translateX(0%); /* 调整箭头的水平位置 */
  border-width: 0 7px 8px; /* 调整边框宽度以改变箭头的方向 */
  border-style: solid;
  border-color: transparent transparent #ffffff transparent; /* 调整边框颜色以改变箭头的方向 */
  box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.05);
}

.tooltip p {
  margin: 0;
  color: #333;
  font-weight: 600;
}

.container:hover .tooltip,
.tooltip:hover { /* 添加这条规则来保持tooltip在hover时可见 */
  top: -10px;
  opacity: 1;
  visibility: visible;
  animation: goPopup 0.5s cubic-bezier(0.68, -0.55, 0.27, 1.55) forwards;
}

.container:hover .tooltip p {
  animation: bounce 2s ease-in-out infinite;
}

@keyframes goPopup {
  0% {
    transform: translateY(0) scaleY(0);
    opacity: 0;
  }
  50% {
    transform: translateY(50%) scaleY(1.2);
    opacity: 1;
  }
  100% {
    transform: translateY(100%) scaleY(1);
    border-radius: 8px;
    opacity: 1;
    height: 40px;
  }
}

@keyframes bounce {
  0%,
  20%,
  50%,
  80%,
  100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-3px);
  }
  60% {
    transform: translateY(-2px);
  }
}
</style>