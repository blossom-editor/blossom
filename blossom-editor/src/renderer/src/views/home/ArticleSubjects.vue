<template>
  <div class="home-subject-root">
    <div v-if="isEmpty(subjects)" class="placeholder">无专题内容</div>
    <div
      v-for="subject in subjects"
      class="subject-item"
      :key="subject.name"
      :style="{ '--bl-subject-color1': subject.color + '70' }"
      @click="toToc(subject.tocId)">
      <div class="progress" :style="{ background: subject.color + '40', width: (subject.subjectWords / maxWords) * 100 + '%' }"></div>

      <div class="seal">{{ subject.name }}</div>

      <div class="inner"></div>
      <img
        class="menu-icon-img"
        v-if="isNotBlank(subject.icon) && (subject.icon.startsWith('http') || subject.icon.startsWith('https'))"
        :src="subject.icon" />
      <svg class="icon subject-icon" aria-hidden="true">
        <use :xlink:href="'#' + subject.icon"></use>
      </svg>

      <bl-row class="infos"> <span class="iconbl bl-pen-line"></span>{{ subject.subjectWords }} </bl-row>
      <bl-row class="infos"> <span class="iconbl bl-a-clock3-line"></span>{{ subject.subjectUpdTime }} </bl-row>
    </div>
    <div style="width: 100%; height: 5px"></div>
  </div>
</template>

<script setup lang="ts">
import router from '@renderer/router'
import { ref, onActivated } from 'vue'
import { subjectsApi } from '@renderer/api/blossom'
import { isEmpty } from 'lodash'
import { isNotBlank, isNull } from '@renderer/assets/utils/obj'
import Notify from '@renderer/scripts/notify'

onActivated(() => {
  getSubjects()
})

let maxWords = 0
const subjects = ref<any>([])

const getSubjects = () => {
  subjectsApi().then((resp) => {
    subjects.value = resp.data
    if (!isEmpty(resp.data)) {
      maxWords = resp.data
        .sort((a: any, b: any) => {
          return b.subjectWords - a.subjectWords
        })
        .slice(0, 1)[0].subjectWords
    }
  })
}

const toToc = (articleId: number) => {
  if (isNull(articleId)) {
    Notify.info('该专题无目录, 请先在专题下添加一篇含有 [TOC] 标签的文章', '提示')
    return
  }
  router.push({ path: '/articleIndex', query: { articleId: articleId } })
}
</script>

<style scoped lang="scss">
.home-subject-root {
  @include box(100%, 100%);
  @include flex(row, flex-start, flex-start);
  flex-wrap: wrap;
  align-content: flex-start;
  overflow: hidden;
  overflow-y: overlay;
}

.placeholder {
  padding: 20px 0 0 20px;
  color: var(--bl-text-color-light);
}

$width-item: 210px;

.subject-item {
  @include flex(column, flex-start, flex-start);
  @include box($width-item, 90px, $width-item, $width-item);
  @include themeShadow(0 3px 5px 0 #cacaca, 0 3px 3px #000000);
  @include themeBg(
    linear-gradient(155deg, #ffffff00 0%, #f0f0f0 80%, var(--bl-subject-color1) 100%),
    linear-gradient(155deg, var(--bl-html-color) 0%, var(--el-color-primary-light-9) 80%, var(--bl-subject-color1) 100%)
  );
  margin: 15px 10px;
  border-radius: 4px;
  transition: transform 0.3s;
  position: relative;
  overflow: hidden;
  z-index: 1;
  cursor: pointer;

  &:hover {
    transform: translateY(-5px);
    .seal {
      transform: translateY(-30px);
    }

    .inner {
      opacity: 1;
    }
  }

  .progress {
    @include box($width-item, 2px);
    @include absolute(0, '', '', 0);
    z-index: 5;
  }

  .seal {
    @include box($width-item, 40px);
    @include font(14px, 300);
    @include themeColor(#3e3e3e, #b3b3b3);
    @include themeShadow(0 3px 7px rgb(144, 144, 144), 0 3px 5px rgb(14, 14, 14));
    line-height: 40px;
    padding: 0 10px;
    border-bottom-left-radius: 20px;
    border-bottom-right-radius: 20px;
    backdrop-filter: blur(5px);
    transition: transform 0.3s;
    z-index: 4;
  }

  .inner {
    @include box(180px, 25px);
    @include absolute(1px, '', '', 15px);
    @include themeShadow(inset 0 3px 10px rgb(84, 84, 84), inset 0 3px 10px rgb(0, 0, 0));
    border-bottom-left-radius: 30px;
    border-bottom-right-radius: 30px;
    transition: opacity 1.5s;
    opacity: 0;
  }

  .subject-icon,
  .menu-icon-img {
    @include box(55px, 55px);
    @include absolute('', 2px, 8px, '');
    @include themeFilter(drop-shadow(0 0 3px rgb(62, 62, 62)), drop-shadow(0 0 3px #000000));
  }

  .infos {
    @include box(100%, 25px);
    @include font(11px, 300);
    @include themeColor(#595959, #8d8d8d);
    padding: 5px 10px;

    .iconbl {
      font-size: 12px;
      margin-right: 5px;
      transition: 0.3s;
    }

    .bl-sendmail-fill {
      font-size: 15px;
    }
  }
}
</style>
