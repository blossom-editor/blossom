<template>
  <span>{{ dateNow }} {{ timeNow }}</span>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';

const dateNow = ref('');
const timeNow = ref('');

const now = () => {
  let dateSingle = new Date();
  dateNow.value = dateSingle.toLocaleDateString()
  timeNow.value = dateSingle.toLocaleTimeString()
}

const refresh = () => {
  setInterval(now, 1000);
}

onMounted(() => {
  refresh();
})
</script>

<style scoped></style>