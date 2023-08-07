<template>
  <div class="home-subject-root">
    <div v-if="isEmpty(subjects)" class="placeholder">
      无专题内容
    </div>
    <div class="subject-item" v-for="subject in  subjects " :key="subject.name"
      :style="{ '--xz-subject-color1': subject.color }" @click="toToc(subject.tocId)">

      <!-- 进度条 -->
      <div class="progress"
        :style="{ 'background': subject.color + '50', width: subject.subjectWords / maxWords * 100 + '%' }"></div>

      <!-- 名称 -->
      <div class="seal">
        <span>{{ subject.name }}</span>
      </div>

      <div class="inner"></div>

      <!-- 图标 -->
      <svg class="icon subject-icon" aria-hidden="true">
        <use :xlink:href="'#' + subject.icon"></use>
      </svg>

      <!-- 字数 -->
      <bl-row class="infos" justifyContent="space-between">
        <div>
          <span class="iconbl bl-pen-line"></span>{{ subject.subjectWords }}
        </div>
      </bl-row>

      <!-- 日期 -->
      <bl-row class="infos" justifyContent="space-between">
        <div>
          <span class="iconbl bl-a-clock3-line"></span>{{ subject.subjectUpdTime }}
        </div>
      </bl-row>

    </div>
  </div>
</template>

<script setup lang="ts">
import router from "@renderer/router";
import { ref, onActivated } from "vue"
import { subjectsApi } from "@renderer/api/blossom"
import { isEmpty } from 'lodash'
import { isNull } from "@renderer/assets/utils/obj"
import Notify from '@renderer/components/Notify'

onActivated(() => {
  getSubjects()
})

let maxWords = 0
const subjects = ref<any>([])

const getSubjects = () => {
  subjectsApi().then(resp => {
    subjects.value = resp.data
    if (!isEmpty(resp.data)) {
      maxWords = resp.data.sort((a: any, b: any) => {
        return b.subjectWords - a.subjectWords;
      }).slice(0, 1)[0].subjectWords
    }
  })
}

const toToc = (articleId: number) => {
  if (isNull(articleId)) {
    Notify.info('该专题无目录, 请先在专题下添加一篇含有 [TOC] 标签的文章', '提示')
    return;
  }
  router.push({ path: '/articleIndex', query: { articleId: articleId } })
}

</script>

<style scoped lang="scss">
.home-subject-root {
  // @include box(900px, 100%);
  @include box(100%, 100%);
  @include flex(row, flex-start, flex-start);
  flex-wrap: wrap;
  align-content: flex-start;
  overflow: auto;
  overflow-y: overlay;

  .placeholder {
    padding: 20px 0 0 20px;
    color: var(--el-text-color-disabled);
  }

  $width-item: 210px;

  .subject-item {
    @include flex(column, flex-start, flex-start);
    @include box($width-item, 90px, $width-item, $width-item);
    @include themeShadow(3px 3px 5px 1px rgba(88, 88, 88, 0.3), 3px 3px 5px 1px rgba(0, 0, 0, 1));
    @include themeBg(linear-gradient(155deg, #ffffff00 0%, #F0F0F0 60%, var(--xz-subject-color1) 100%),
      linear-gradient(155deg, var(--xz-html-color) 0%, var(--el-color-primary-light-9) 60%, var(--xz-subject-color1) 100%));
    border-radius: 5px;
    margin: 15px;
    transition: 0.3s;
    position: relative;
    overflow: hidden;
    cursor: pointer;
    z-index: 1;

    &:hover {
      transform: translateY(-5px);
      @include themeShadow(3px 3px 5px 1px rgb(116, 116, 116), 3px 3px 5px 1px rgba(0, 0, 0, 1));

      .seal {
        text-shadow: 3px 3px 5px rgba(0, 0, 0, 1);
        transform: translateY(-30px);
        @include themeShadow(0 3px 15px 1px rgb(116, 116, 116), 3px 3px 5px 1px rgba(0, 0, 0, 1));

        [class="dark"] & {
          text-shadow: 5px 5px 15px #000, -3px -3px 10px rgba(255, 255, 255, .5);
        }
      }

      .inner {
        opacity: 1;
      }
    }

    .progress {
      @include box($width-item, 2px);
      @include absolute(0, '', '', 0);
      z-index: 3;
    }

    .seal {
      @include flex(row, space-between, center);
      @include box($width-item, 40px);
      @include font(14px, 500);
      @include themeColor(#636363, #CDCDCD);
      @include themeShadow(0 3px 10px rgb(144, 144, 144), 0 3px 5px rgb(37, 37, 37));
      padding: 5px 10px;
      border-bottom-left-radius: 30px;
      border-bottom-right-radius: 30px;
      text-shadow: 2px 2px 5px rgb(54, 54, 54);
      backdrop-filter: blur(5px);
      transition: 0.3s;
      z-index: 2;
    }

    .inner {
      @include box(calc(#{$width-item} - 30px), 25px);
      @include absolute(1px, '', '', 15px);
      border-bottom-left-radius: 30px;
      border-bottom-right-radius: 30px;
      @include themeShadow(inset 0 3px 10px rgb(84, 84, 84), inset 0 3px 10px rgb(0, 0, 0));
      transition: 1.5s;
      opacity: 0;
    }

    .subject-icon {
      @include box(55px, 55px);
      @include absolute('', 2px, 8px, '');
    }

    .infos {
      @include box(100%, 25px);
      @include font(11px, 300);
      @include themeColor(#595959, #8D8D8D);
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


}
</style>