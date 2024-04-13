<template>
  <bl-row class="container-name">专题收藏</bl-row>
  <bl-row class="container-sub-name" just="space-between">
    <span>Subject Star</span>
    <span v-if="configViewStyle.isHomeSubjectCard" class="iconbl bl-array-line container-operator" @click="showSubjectCard(false)" />
    <span v-else class="iconbl bl-article-line container-operator" @click="showSubjectCard(true)" />
  </bl-row>
  <div class="home-subject-root">
    <div v-if="isEmpty(subjects)" class="placeholder">无专题收藏</div>
    <div
      v-for="subject in subjects"
      :class="[configViewStyle.isHomeSubjectCard ? 'subject-card' : 'subject-list', configViewStyle.webCollectExpand ? '' : 'close']"
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
      <svg v-else class="icon subject-icon" aria-hidden="true">
        <use :xlink:href="'#' + subject.icon"></use>
      </svg>

      <bl-row class="infos words"> <span class="iconbl bl-pen-line"></span>{{ subject.subjectWords }} </bl-row>
      <bl-row class="infos time"> <span class="iconbl bl-a-clock3-line"></span>{{ subject.subjectUpdTime }} </bl-row>
    </div>
    <div style="width: 100%; height: 5px"></div>
  </div>
</template>

<script setup lang="ts">
import router from '@renderer/router'
import { ref } from 'vue'
import { useConfigStore } from '@renderer/stores/config'
import type { ViewStyle } from '@renderer/stores/config'
import { subjectsApi } from '@renderer/api/blossom'
import { isEmpty } from 'lodash'
import { isNotBlank, isNull } from '@renderer/assets/utils/obj'
import Notify from '@renderer/scripts/notify'
import { useLifecycle } from '@renderer/scripts/lifecycle'

const configStore = useConfigStore()
const configViewStyle = ref<ViewStyle>(configStore.viewStyle)

useLifecycle(
  () => getSubjects(),
  () => getSubjects()
)

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

const showSubjectCard = (isCard: boolean) => {
  configViewStyle.value.isHomeSubjectCard = isCard
  configStore.setViewStyle(configViewStyle.value)
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
@import './styles/container.scss';
.home-subject-root {
  @include box(100%, 100%);
  @include flex(row, flex-start, flex-start);
  flex-wrap: wrap;
  align-content: flex-start;
  overflow: hidden;
  overflow-y: scroll;
}

.placeholder {
  @include font(15px, 300);
  padding: 20px 0 0 20px;
  color: var(--bl-text-color-light);
}

$width-item: 210px;

.subject-card {
  @include flex(column, flex-end, flex-start);
  @include box($width-item, 90px, $width-item, $width-item);
  @include themeBg(
    linear-gradient(155deg, #ffffff00 0%, #f0f0f0 80%, var(--bl-subject-color1) 100%),
    linear-gradient(155deg, var(--bl-html-color) 0%, var(--el-color-primary-light-9) 80%, var(--bl-subject-color1) 100%)
  );
  box-shadow: var(--bl-box-shadow-subject);
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
    @include themeBg(#ffffffe1, #151515d2);
    @include themeShadow(0 3px 7px rgb(198, 198, 198), 0 3px 5px rgb(14, 14, 14));
    @include ellipsis();
    line-height: 40px;
    padding: 0 10px;
    border-bottom-left-radius: 20px;
    border-bottom-right-radius: 20px;
    transition: transform 0.3s;
    z-index: 4;
  }

  .inner {
    @include box(180px, 25px);
    @include absolute(1px, '', '', 15px);
    @include themeShadow(inset 0 3px 10px rgb(84, 84, 84), inset 0 3px 10px rgb(0, 0, 0));
    border-bottom-left-radius: 30px;
    border-bottom-right-radius: 30px;
    transition: opacity 1s;
    opacity: 0;
  }

  .subject-icon,
  .menu-icon-img {
    @include box(50px, 50px);
    @include absolute('', 5px, 8px, '');
    @include themeFilter(drop-shadow(0 0 3px rgb(62, 62, 62)), drop-shadow(0 0 3px #000000));
    border-radius: 4px;
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

.subject-card.close {
  margin: 15px 5px;
}

.subject-list {
  @include flex(row, flex-start, flex-start);
  width: $width-item;
  flex-wrap: wrap;
  align-content: flex-start;
  position: relative;
  padding: 3px;
  margin: 15px 10px 0 10px;
  border-radius: 6px;
  transition: background-color 0.2s;
  cursor: pointer;

  &:hover {
    @include themeBg(#f5f5f5, #171717);

    .content {
      .name {
        color: var(--bl-text-color);
      }
    }
  }

  .seal {
    @include ellipsis();
    width: 100%;
    padding-left: 40px;
    font-size: 13px;
    color: var(--bl-text-color-light);
  }

  .words {
    width: 100%;
    font-size: 10px;
    padding-left: 40px;
    color: var(--bl-text-color-light);

    .iconbl {
      font-size: 12px;
      padding-right: 3px;
    }
  }

  .subject-icon,
  .menu-icon-img {
    @include box(25px, 25px);
    @include absolute(6px, '', '', 10px);
    @include themeFilter(drop-shadow(0 0 2px rgb(135, 135, 135)), drop-shadow(0 0 2px #000000));
  }
  .menu-icon-img {
    border-radius: 4px;
  }

  .time,
  .progress,
  .inner {
    display: none;
  }
}

.subject-list.close {
  margin: 15px 5px 0 5px;
}
</style>
