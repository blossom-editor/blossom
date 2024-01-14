<template>
  <div class="article-search-root">
    <!-- 标题 -->
    <div class="info-title">
      <el-input
        v-model="searchKeyword"
        ref="SearchInputRef"
        size="large"
        placeholder="输入关键字"
        class="search-input"
        @input="handleInput"
        @keyup.enter="search">
        <template #prefix>
          <el-icon size="25" @click="search"><Search /></el-icon>
        </template>
        <template #suffix>
          <el-tooltip effect="light" placement="top" :show-after="0" :hide-after="0" :auto-close="2000" :offset="3">
            <template #content>
              <div>匹配全部关键字</div>
              <div class="keyboard">{{ keymaps.fullSearchOperatorAnd }}</div>
            </template>
            <div :class="['iconbl bl-and', isOperator ? 'and' : 'or']" @click="handleOperator"></div>
          </el-tooltip>
          <el-icon class="clear-btn" size="25" @click="clear"><CircleClose /></el-icon>
        </template>
      </el-input>
    </div>

    <div class="content">
      <div class="results" ref="ResultsRef">
        <div v-if="noResult" class="placeholder">
          <div>未找到相关结果 "{{ searchKeyword }}"</div>
          <div class="iconbl bl-wuneirong"></div>
        </div>
        <div
          v-else
          v-for="(article, index) in result"
          :id="'result-item' + article.id"
          :key="article.id"
          :class="['result-item', article.id === currentId ? 'current' : '']"
          @click="openArticle"
          @mouseenter="hover(article)">
          <div class="index">{{ index + 1 }}</div>
          <bl-row just="space-between" class="infos">
            <div class="name" v-html="article.name"></div>
            <bl-row class="tags" height="100%">
              <bl-tag v-for="tag in article.tags" :key="tag"><span v-html="tag"></span></bl-tag>
            </bl-row>
          </bl-row>
          <div v-if="isNotBlank(article.markdown)" class="markdown" v-html="article.markdown"></div>
          <div class="workbench">
            <bl-row>
              <div class="iconbl bl-correlation-line" @click.stop="copyUrlLink(article)"></div>
              <div class="iconbl bl-a-computerend-line" @click.stop="openArticleWindow(article)"></div>
            </bl-row>
          </div>
        </div>
      </div>
    </div>
    <div class="info-footer">
      <bl-row class="keys" just="space-between">
        <bl-row width="250px">
          <kbd class="keyboard small iconbl bl-enter"></kbd>
          <div>打开</div>
          <kbd class="keyboard small">↑</kbd>
          <kbd class="keyboard small">↓</kbd>
          <div>切换</div>
          <kbd class="keyboard small">ESC</kbd>
          <div>关闭</div>
        </bl-row>
        <div class="totalhit">搜索到 {{ totalHit }} 项结果</div>
      </bl-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { nextTick, onMounted, onUnmounted, ref } from 'vue'
import { Search, CircleClose } from '@element-plus/icons-vue'
import { articleSearchApi } from '@renderer/api/blossom'
import { isBlank, isNotBlank, isNull } from '@renderer/assets/utils/obj'
import { getPrimaryColor } from '@renderer/scripts/global-theme'
import { rgbaToHex, rgbToHex } from '@renderer/assets/utils/color'
import { openNewArticleWindow } from '@renderer/assets/utils/electron'
import { keymaps } from './scripts/editor-tools'
import hotkeys from 'hotkeys-js'

onMounted(() => {
  document.addEventListener('keydown', handleKeyDown)
  document.addEventListener('mousemove', enabledMouse)
  bindKeys()
  nextTick(() => {
    SearchInputRef.value.focus()
  })
})

onUnmounted(() => {
  document.removeEventListener('keydown', handleKeyDown)
  document.removeEventListener('mousemove', enabledMouse)
  unbindKeys()
})

type Article = { id: string; name: string; originName: string; markdown: string; tags: string[]; currnet: boolean }
const SearchInputRef = ref()
const searchKeyword = ref('')
const ResultsRef = ref<HTMLElement>()
const result = ref<Article[]>([])
const totalHit = ref(0)
const isOperator = ref(false)
const noResult = ref(false)
const currentId = ref('')

/**
 * 输入后清空
 */
const handleInput = () => {
  noResult.value = false
  currentId.value = ''
}

/**
 * 切换搜索 operator
 */
const handleOperator = () => {
  isOperator.value = !isOperator.value
  search()
}

/**
 * 搜索文章
 */
const search = () => {
  articleSearchApi({ keyword: searchKeyword.value, hlColor: getColor(), debug: false, operator: isOperator.value }).then((resp) => {
    result.value = resp.data.hits
    totalHit.value = resp.data.totalHit
    if (isNull(resp.data.hits)) {
      noResult.value = true
    } else {
      noResult.value = false
    }
    SearchInputRef.value.blur()
    ResultsRef.value!.focus()
  })
}

/**
 * 将颜色转换为 16 进制, 用于后台分隔 tag
 */
const getColor = () => {
  let color = getPrimaryColor().color.trim()
  if (isBlank(color)) {
    return '#E3A300'
  }
  if (color.startsWith('rgba(')) {
    return rgbaToHex(getPrimaryColor().color)
  }
  if (color.startsWith('rgb(')) {
    return rgbToHex(getPrimaryColor().color)
  }
  if (color.startsWith('#')) {
    return color
  }
  return '#E3A300'
}

const clear = () => {
  searchKeyword.value = ''
  result.value = []
}

/**
 * 打开搜索的文章
 */
const openArticle = () => {
  let curArticle = result.value.find((item) => item.id === currentId.value)
  if (isNull(curArticle)) {
    return
  }
  let tree: DocTree = {
    i: curArticle!.id,
    p: '0',
    n: curArticle!.originName,
    o: 0,
    t: [],
    s: 0,
    icon: '',
    ty: 3,
    star: 0
  }
  emits('openArticle', tree)
}

/**
 * 复制搜索文章的双链链接
 * @param article 文章信息
 */
const copyUrlLink = (article: Article) => {
  emits('createLink', article.originName, article.id)
}

/**
 * 新窗口中打开搜索的文章
 * @param article
 */
const openArticleWindow = (article: Article) => {
  openNewArticleWindow(article.name, article.id)
}

/**
 * 记录当前选中的文章
 * @param id
 */
const setCurrentId = (id: string) => {
  currentId.value = id
}

const emits = defineEmits(['openArticle', 'createLink'])

//#region

//#region ----------------------------------------< 键盘选择 >--------------------------------------
// 禁用鼠标
let disabledMouse = false

/**
 * 鼠标移入的事件
 * @param article
 */
const hover = (article: Article) => {
  if (disabledMouse) {
    return
  }
  article.currnet = true
  setCurrentId(article.id)
}

/**
 * 监听键盘, 监听 ↑|↓|Enter 键
 * @param event
 */
const handleKeyDown = (event: KeyboardEvent) => {
  let keyCode = event.code
  if (keyCode === 'ArrowDown') {
    event.preventDefault()
    down()
  } else if (keyCode === 'ArrowUp') {
    event.preventDefault()
    up()
  } else if (keyCode === 'Enter') {
    let target = event.target
    if (target && (target as HTMLElement).nodeName !== 'INPUT' && isNotBlank(currentId.value)) {
      event.preventDefault()
      openArticle()
    }
  } else {
    SearchInputRef.value.focus()
  }
}

/**
 * 选择上一个
 */
const up = () => {
  if (isNull(result.value)) {
    return
  }
  disabledMouse = true
  let lenght = result.value.length
  for (let i = 0; i < lenght; i++) {
    const article = result.value[i]
    if (article.id === currentId.value && i > 0) {
      setCurrentId(result.value[i - 1].id)
      scrollIntoView(currentId.value, 'up')
      break
    }
  }
}

/**
 * 选择下一个
 */
const down = () => {
  if (isNull(result.value)) {
    return
  }
  disabledMouse = true
  if (isBlank(currentId.value)) {
    currentId.value = result.value[0].id
    return
  }
  let lenght = result.value.length
  for (let i = 0; i < lenght; i++) {
    const article = result.value[i]
    if (article.id === currentId.value && i < lenght - 1) {
      setCurrentId(result.value[i + 1].id)
      scrollIntoView(currentId.value, 'down')
      break
    }
  }
}

/**
 * 显示选中元素
 * @param id   元素ID
 * @param type 选中的是上一个, 或是下一个
 */
const scrollIntoView = (id: string, type: 'up' | 'down') => {
  let item = document.getElementById('result-item' + id)
  if (!item) {
    return
  }
  if (type === 'up') {
    // console.log(ResultsRef.value?.scrollTop)
    // console.log(item.offsetTop)
    let top = item.offsetTop - ResultsRef.value!.scrollTop
    if (top <= 90) {
      item.scrollIntoView({ block: 'start' })
      ResultsRef.value?.scrollTo({ top: ResultsRef.value?.scrollTop - 10 })
    }
  } else {
    // console.log(ResultsRef.value?.scrollTop)
    // console.log(item.offsetTop)
    let top = item.offsetTop - ResultsRef.value!.scrollTop
    if (top >= 500) {
      item.scrollIntoView({ block: 'end' })
      ResultsRef.value?.scrollTo({ top: ResultsRef.value?.scrollTop + 10 })
    }
  }
}

const enabledMouse = () => {
  disabledMouse = false
}

//#endregion

//#region
const bindKeys = () => {
  hotkeys('alt+g, command+g', () => {
    handleOperator()
    return false
  })
}

const unbindKeys = () => {
  hotkeys.unbind('alt+g, command+g')
}
//#endregion
</script>

<style scoped lang="scss">
@import '@renderer/assets/styles/bl-dialog-info';

.article-search-root {
  border-radius: 10px;
  @include box(100%, 100%);

  .info-title {
    height: 60px;
    padding: 0;
    padding-top: 3px;
  }

  .content {
    @include box(100%, calc(100% - 110px));
    padding-right: 4px;
  }

  .search-input {
    --el-input-bg-color: #00000000;
    .bl-and {
      cursor: pointer;
      transition: color 0.3s;
    }
    .bl-and.and {
      color: var(--el-color-primary);
    }
    .clear-btn {
      cursor: pointer;
    }

    :deep(.el-input__wrapper) {
      box-shadow: none;
    }

    :deep(.el-input__wrapper.is-focus) {
      box-shadow: none;
    }
    :deep(.el-input__inner) {
      font-size: 16px;
    }
  }

  .results {
    height: 100%;
    overflow-y: scroll;
    padding: 0 16px 0 20px;
    padding-bottom: 0;
    padding-top: 10px;
    .placeholder {
      padding: 40px 0;
      text-align: center;
      color: var(--bl-text-color-light);

      .iconbl {
        @include themeColor(#f1f1f1, #272727);
        margin-top: 80px;
        font-size: 150px;
      }
    }

    .result-item {
      @include themeShadow(0 0 2px #d4d4d4, 0 0 3px #000000);
      max-height: 100px;
      margin-bottom: 15px;
      border-radius: 4px;
      background-color: var(--bl-html-color);
      border: 1px solid var(--el-border-color-light);
      position: relative;
      box-sizing: border-box;
      cursor: pointer;

      .index {
        @include font(11px, 100);
        width: 13px;
        color: var(--bl-text-color-light);
        font-style: italic;
        position: absolute;
        top: 0px;
        left: -20px;
        text-align: right;
      }

      .infos {
        height: 26px;
        padding: 0 4px;
        .name {
          @include ellipsis();
          color: var(--el-color-primary);
          min-width: 60%;
        }

        .tags {
          @include ellipsis();
          width: auto !important;
          max-width: 40%;
        }
      }

      .markdown {
        height: 69px;
        font-size: 12px;
        padding: 0 3px 2px 3px;
        border-top: 1px solid var(--el-border-color);
        overflow-y: scroll;
        color: var(--bl-text-color-light);
      }

      .workbench {
        height: 20px;
        font-size: 12px;
        background-color: var(--bl-bg-color);
        border-top-left-radius: 4px;
        border-bottom-right-radius: 4px;
        position: absolute;
        right: 0;
        bottom: 0;
        display: none;

        .iconbl {
          padding: 0 5px;
          line-height: 20px;
          height: 100%;
          cursor: pointer;
          &:hover {
            background-color: var(--el-color-primary-light-8);
          }
        }
      }
    }

    .result-item.current {
      border: 1px solid var(--el-color-primary);
      background-color: var(--el-color-primary-light-9);

      .workbench {
        border-top: 1px solid var(--el-color-primary);
        border-left: 1px solid var(--el-color-primary);
        display: block;
      }

      .markdown {
        color: var(--bl-text-color);
      }
    }

    &::-webkit-scrollbar {
      width: 10px;
    }
    &::-webkit-scrollbar-thumb {
      border-radius: 0px;
    }
  }

  .keys {
    @include font(13px, 100);

    kbd {
      margin: 0 3px;
    }

    div {
      margin-right: 10px;
    }
  }

  .totalhit {
    @include font(13px, 100);
    color: var(--bl-text-color-light);
  }

  .keyboard {
    height: 21px;
    margin-bottom: 6px;
    font-size: 13px;
  }
}
</style>
