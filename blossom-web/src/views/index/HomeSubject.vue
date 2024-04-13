<template>
  <div class="home-subject-root">
    <div class="subject-item" v-for="subject in subjects" :key="subject.name">
      <div class="name" :style="{ fontSize: getFontSize(subject.name) }">
        {{ subject.name }}
      </div>
      <div class="words"><span class="iconbl bl-pen-line"></span>{{ subject.subjectWords }}</div>
      <div class="upd-time"><span class="iconbl bl-a-clock3-line"></span>{{ subject.subjectUpdTime }}</div>
      <div class="cover-name" :style="{ background: subject.color }">
        {{ subject.name }}
      </div>
      <div class="describes">
        {{ subject.describes }}
      </div>
      <div class="button" @click="toToc(subject.tocId)">
        <span class="iconbl bl-sendmail-fill"></span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import router from '@/router'
import { ref, onMounted } from 'vue'
import { subjectsApi } from '@/api/blossom'

onMounted(() => {
  subjects.value = []
  subjectsApi({ starStatus: 1 }).then((resp) => {
    subjects.value = resp.data
  })
})

const subjects = ref<any>([])

const getFontSize = (name: string) => {
  if (name.length >= 18) {
    return '18px'
  }
  return '20px'
}

const toToc = (articleId: number) => {
  router.push({ path: '/articles', query: { articleId: articleId } })
}
</script>

<style scoped lang="scss">
.home-subject-root {
  @include box(100%, 100%);
  @include flex(row, flex-start, flex-start);
  font-family: 'Jetbrains Mono';
  flex-wrap: wrap;
  align-content: flex-start;
  padding: 20px;
  overflow: scroll;

  &::-webkit-scrollbar-thumb {
    background-color: #414141;
  }

  .subject-item {
    @include box(260px, 150px);
    position: relative;
    border-radius: 5px;
    padding: 5px 10px;
    margin: 15px 16px;
    transition: border 0.3s, box-shadow 0.3s;
    box-shadow: 0 0 12px rgba(0, 0, 0, 0.1);
    overflow: hidden;

    .iconbl {
      font-size: 12px;
      margin-right: 5px;
    }

    &:hover {
      border: 0;
      box-shadow: 7px 7px 12px rgba(0, 0, 0, 0.5), -3px -3px 10px rgba(255, 255, 255, 0.1);

      .name {
        text-shadow: 5px 5px 15px #000, -3px -3px 10px rgba(255, 255, 255, 0.5);
      }

      .cover-name {
        opacity: 1;
      }

      .button {
        opacity: 1;
        animation-delay: 0s;
        animation-name: shock;
        animation-duration: 0.1s;
        animation-iteration-count: 3;
        animation-direction: normal;
        animation-timing-function: linear;
      }
    }

    .name {
      height: 35px;
      font-weight: 700;
      color: #cdcdcd;
      transition: text-shadow 0.3s;
    }

    .words {
      @include font(12px, 300);
      height: 20px;
      color: #7a7a7a;
    }

    .upd-time {
      @include font(12px, 300);
      height: 20px;
      color: #7a7a7a;
    }

    .cover-name {
      @include box(150px, 17px);
      @include font(12px, 700);
      line-height: 17px;
      position: absolute;
      top: 15px;
      right: -50px;
      opacity: 0;
      color: #fff;
      box-shadow: 0 0 10px 1px #181818;
      text-align: center;
      transform: scale(0.8) rotate(45deg);
      transition: opacity 0.3s;
    }

    .describes {
      font-size: 12px;
      color: #7a7a7a;
      margin-top: 5px;
    }

    .button {
      position: absolute;
      right: 5px;
      bottom: 8px;
      color: #cdcdcd;
      opacity: 0;
      cursor: pointer;

      .icon-sendmail-fill {
        font-size: 20px;
      }
    }
  }

  @keyframes shock {
    0% {
      margin-left: 0px;
      margin-right: 3px;
      margin-top: 0px;
    }

    100% {
      margin-left: 3px;
      margin-right: 0px;
      margin-top: 3px;
    }
  }
}
</style>
