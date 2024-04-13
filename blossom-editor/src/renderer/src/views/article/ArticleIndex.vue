<template>
  <div class="index-article-root">
    <!-- folder menu -->
    <div class="doc-container" ref="DocsRef" v-show="docsExpand">
      <div class="doc-tree-menu-container" :style="tempTextareaStyle.docTree">
        <ArticleTreeDocs @click-doc="clickCurDoc" ref="ArticleTreeDocsRef"></ArticleTreeDocs>
      </div>

      <div class="doc-temp-textarea">
        <bl-row just="space-between" height="28px" class="doc-temp-textarea-workbench">
          <bl-row><img src="@renderer/assets/imgs/note/cd.png" />临时内容(可从便签快速设置)</bl-row>
          <div class="iconbl bl-subtract-line" @click="tempTextareaExpand = !tempTextareaExpand"></div>
        </bl-row>
        <bl-row class="doc-temp-textarea-input" :style="tempTextareaStyle.tempTextarea">
          <el-input v-model="tempTextarea" type="textarea" resize="none" @input="tempInput"></el-input>
        </bl-row>
      </div>
    </div>
    <div class="resize-divider-vertical" ref="ResizeDocsDividerRef"></div>
    <!-- editor -->
    <div class="editor-container" ref="EditorContainerRef" v-loading="editorLoading" element-loading-text="正在读取文章内容...">
      <div class="editor-tools">
        <EditorTools
          @save="saveCurArticleContent()"
          @preview-full-screen="alt_3()"
          @editor-full-screen="alt_4()"
          @bold="cmw.commandBold()"
          @italic="cmw.commandItalic()"
          @strike="cmw.commandStrike()"
          @sub="cmw.commandSub()"
          @sup="cmw.commandSup()"
          @separator="cmw.commandSeparator()"
          @blockquote="cmw.commandQuote()"
          @blockquote-block="cmw.commandQuoteBlack()"
          @blockquote-green="cmw.commandQuoteGreen()"
          @blockquote-yellow="cmw.commandQuoteYellow()"
          @blockquote-red="cmw.commandQuoteRed()"
          @blockquote-blue="cmw.commandQuoteBlue()"
          @blockquote-purple="cmw.commandQuotePurple()"
          @code="cmw.commandCode()"
          @pre="cmw.commandPre()"
          @checkbox="cmw.commandCheckBox()"
          @unordered="cmw.commandUnordered()"
          @ordered="cmw.commandOrdered()"
          @table="cmw.commandTable()"
          @image="cmw.commandImg()"
          @link="cmw.commandLink()">
        </EditorTools>
      </div>

      <!-- 编辑器与预览 -->
      <div class="editor-preview" :style="editorStyle">
        <div v-if="!curArticle" class="ep-placeholder">
          <ArticleIndexPlaceholder></ArticleIndexPlaceholder>
        </div>
        <div class="operator" ref="EditorOperatorRef">
          <el-tooltip
            :content="'同步滚动:' + (editorOperator.sycnScroll ? '开启' : '关闭')"
            popper-class="is-small"
            effect="light"
            placement="right"
            transition="none"
            :show-after="500"
            :hide-after="0"
            :show-arrow="false">
            <div
              class="iconbl bl-scroll"
              :style="{ color: editorOperator.sycnScroll ? 'var(--el-color-primary-light-3)' : '' }"
              @click="handleSyncScroll"></div>
          </el-tooltip>
          <el-tooltip
            content="前往顶部"
            popper-class="is-small"
            effect="light"
            placement="right"
            transition="none"
            :show-after="500"
            :hide-after="0"
            :show-arrow="false">
            <div class="iconbl bl-a-doubleonline-line" @click="scrollTop"></div>
          </el-tooltip>
          <el-tooltip
            content="前往底部"
            popper-class="is-small"
            effect="light"
            placement="right"
            transition="none"
            :show-after="500"
            :hide-after="0"
            :show-arrow="false">
            <div class="iconbl bl-a-doubleunderline-line" @click="scrollBottom"></div>
          </el-tooltip>
        </div>
        <div class="gutter-holder" ref="GutterHolderRef"></div>
        <div class="editor-codemirror" ref="EditorRef" @click.right="handleEditorClickRight"></div>
        <div class="resize-divider-vertical editor-resize-divider" ref="ResizeEditorDividerRef"></div>
        <div class="preview-marked bl-preview" ref="PreviewRef" v-html="articleHtml"></div>
      </div>

      <!-- status -->
      <div class="editor-status">
        <EditorStatus :render-interval="renderInterval"></EditorStatus>
      </div>

      <!-- toc -->
      <div :class="['bl-preview-toc-absolute', tocsExpand ? 'is-expand-open' : 'is-expand-close']" ref="TocRef">
        <div class="toc-title" ref="TocTitleRef">
          目录
          <span v-show="tocsExpand" style="font-size: 10px">({{ keymaps.hideToc }} 可隐藏)</span>
        </div>
        <div class="toc-content" v-show="tocsExpand">
          <div v-for="toc in articleToc" :key="toc.id" :class="[toc.clazz]" @click="toScroll(toc.id)" v-html="toc.content"></div>
        </div>
        <div class="img-title">
          引用图片
          <el-tooltip effect="light" placement="right" :hide-after="0">
            <template #content> 重复上传图片后<br />如果图片无变化可刷新缓存 </template>
            <span class="iconbl bl-refresh-line" @click="refreshCache"></span>
          </el-tooltip>
        </div>
        <div class="img-content">
          <div class="img-wrapper" v-for="image in articleImg" :key="image.targetUrl" @click="showPicInfo(image.targetUrl)">
            <img :src="picCacheWrapper(image.targetUrl)" />
          </div>
        </div>
      </div>
    </div>

    <PictureViewerInfo ref="PictureViewerInfoRef" @saved="refreshCache"></PictureViewerInfo>

    <Teleport to="body">
      <div
        v-show="editorRightMenu.show"
        class="editor-right-menu"
        :style="{ left: editorRightMenu.clientX + 'px', top: editorRightMenu.clientY + 'px' }">
        <div class="menu-content">
          <div v-if="isElectron()" class="menu-item" @click="rightMenuCopy"><span class="iconbl bl-copy-line"></span>复制</div>
          <div v-if="isElectron()" class="menu-item" @click="rightMenuPaste"><span class="iconbl bl-a-texteditorpastetext-line"></span>黏贴</div>
          <div class="menu-item">
            <el-upload
              name="file"
              :action="serverStore.serverUrl + uploadFileApiUrl"
              :data="(f: UploadRawFile) => uploadDate(f, curArticle!.pid)"
              :headers="{ Authorization: 'Bearer ' + userStore.auth.token }"
              :show-file-list="false"
              :before-upload="beforeUpload"
              :on-success="onUploadSeccess"
              :on-error="onError">
              <bl-row><span class="iconbl bl-image--line"></span>插入图片</bl-row>
            </el-upload>
          </div>
          <div class="menu-item" @click="upper"><span class="iconbl bl-daxie"></span>大写</div>
          <div class="menu-item" @click="lower"><span class="iconbl bl-xiaoxie"></span>小写</div>
          <div class="menu-item" @click="formatTable"><span class="iconbl bl-transcript-line"></span>格式化选中表格</div>
          <div class="menu-item" @click="openExtenal('https://katex.org/#demo')">
            <span class="iconbl bl-a-texteditorsuperscript-line"></span>Katex 在线校验
          </div>
          <div class="menu-item" @click="openExtenal('https://mermaid.live/edit')">
            <span class="iconbl bl-a-statisticalviewpiechart3-line"></span>Mermaid 在线校验
          </div>
          <div class="menu-item" @click="openExtenal('https://www.emojiall.com/zh-hans')">
            <span style="margin-right: 4px; padding: 2px 0">😉</span>Emoji网站
          </div>
        </div>
      </div>
    </Teleport>

    <Teleport to="body">
      <div v-if="articleReferenceView.show" ref="ArticleViewRef" class="article-view-absolute bl-preview" :style="articleReferenceView.style">
        <div class="content-view bl-preview" v-html="articleReferenceView.html" :style="editorStyle"></div>
        <bl-row class="workbench" just="space-between">
          <div class="btns">
            <div @click="openArticleWindow(articleReferenceView.articleId)">新窗口打开</div>
          </div>
          <div class="infos">{{ articleReferenceView.name }}</div>
        </bl-row>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
// vue
import { ref, computed, provide, onMounted, onBeforeUnmount, onActivated, onDeactivated, defineAsyncComponent, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import type { UploadProps, UploadRawFile } from 'element-plus'
import { useUserStore } from '@renderer/stores/user'
import { useServerStore } from '@renderer/stores/server'
import { useConfigStore } from '@renderer/stores/config'
import { articleInfoApi, articleUpdContentApi, uploadFileApiUrl } from '@renderer/api/blossom'
// utils
import { Local } from '@renderer/assets/utils/storage'
import { isBlank, isNull } from '@renderer/assets/utils/obj'
import { sleep, isElectron, isBase64Img } from '@renderer/assets/utils/util'
import { openExtenal, writeText, readText, openNewArticleWindow } from '@renderer/assets/utils/electron'
import { formartMarkdownTable } from '@renderer/assets/utils/format-table'
// component
import ArticleTreeDocs from './ArticleTreeDocs.vue'
import ArticleIndexPlaceholder from './ArticleIndexPlaceholder.vue'
import EditorTools from './EditorTools.vue'
// ts
import hotkeys from 'hotkeys-js'
import Notify from '@renderer/scripts/notify'
import { useDraggable } from '@renderer/scripts/draggable'
import type { shortcutFunc } from '@renderer/scripts/shortcut-register'
import { treeToInfo, provideKeyDocInfo, provideKeyCurArticleInfo, isArticle } from '@renderer/views/doc/doc'
import { TempTextareaKey, ArticleReference, parseTocAsync } from './scripts/article'
import type { Toc } from './scripts/article'
import { beforeUpload, onError, picCacheWrapper, picCacheRefresh, uploadForm, uploadDate } from '@renderer/views/picture/scripts/picture'
import { useResizeVertical } from '@renderer/scripts/resize-devider-vertical'
// codemirror
import { CmWrapper } from './scripts/codemirror'
// marked
import marked, { renderBlockquote, renderCode, renderCodespan, renderHeading, renderImage, renderTable, renderLink } from './scripts/markedjs'
import { EPScroll } from './scripts/editor-preview-scroll'
import { useArticleHtmlEvent } from './scripts/article-html-event'
import { shallowRef } from 'vue'
import { keymaps } from './scripts/editor-tools'

//#region -- mounted

const PictureViewerInfo = defineAsyncComponent(() => import('@renderer/views/picture/PictureViewerInfo.vue'))
// const EditorTools = defineAsyncComponent(() => import('./EditorTools.vue'))
const EditorStatus = defineAsyncComponent(() => import('./EditorStatus.vue'))
let isMounted = false

onMounted(() => {
  initEditor()
  initScroll()
  addListenerScroll()
  initAutoSaveInterval()
  if (!isMounted) {
    enterView()
    bindKeys()
  }
})
onBeforeUnmount(() => {
  unbindKeys()
  removeListenerEditorRightMenu()
  removeListenerScroll()
  distoryAutoSaveInterval()
})
onActivated(() => {
  if (isMounted) {
    enterView()
    bindKeys()
  }
  isMounted = true
})
onDeactivated(() => {
  exitView()
  unbindKeys()
})

const userStore = useUserStore()
const serverStore = useServerStore()
const { editorStyle } = useConfigStore()

watch(
  () => userStore.userinfo.id,
  (_newId: string, _oldId: string) => {
    curDoc.value = undefined
    curArticle.value = undefined
    setNewState('')
  }
)

//#endregion

//#region ----------------------------------------< 公共参数和页面动态样式 >--------------------------------------
const DocsRef = ref()
const EditorContainerRef = ref()
const ResizeDocsDividerRef = ref()
const GutterHolderRef = ref() // editor gutter holder
const EditorRef = ref() // editor
const ResizeEditorDividerRef = ref() // editor&preview resize dom
const EditorOperatorRef = ref()
const PreviewRef = ref() // html 预览
const editorOperator = ref({
  syncParse: true,
  sycnScroll: true
})
/**
 * 文档列表的展开和收起
 */
const docsExpand = ref<boolean>(true)
const tocsExpand = ref<boolean>(true)

/**
 * 编辑器和预览的展开收起
 */
let previewFullScreen = false // 是否全屏展开预览
let editorFullScreen = false // 是否全屏展开编辑
const changeEditorPreviewStyle = () => {
  if (previewFullScreen) {
    GutterHolderRef.value.style.width = '0px'
    EditorRef.value.style.width = '0px'
    PreviewRef.value.style.width = '100%'
    PreviewRef.value.style.padding = '10px 20px 0 30px'
    EditorOperatorRef.value.style.display = 'none'
    return
  }
  if (editorFullScreen) {
    GutterHolderRef.value.style.width = '50px'
    EditorRef.value.style.width = 'calc(100% - 6px)'
    PreviewRef.value.style.width = '0'
    PreviewRef.value.style.padding = '0'
    EditorOperatorRef.value.style.display = 'none'
    return
  }
  GutterHolderRef.value.style.width = '50px'
  EditorRef.value.style.width = '50%'
  PreviewRef.value.style.width = '50%'
  PreviewRef.value.style.padding = '10px 20px 0 30px'
  EditorOperatorRef.value.style.display = 'block'
  EditorOperatorRef.value.style.left = 'calc(50% - 0.5px)'
}
/**
 * 临时文本框
 */
const tempTextarea = ref('')
const tempTextareaExpand = ref(true)
const tempTextareaStyle = computed<any>(() => {
  if (tempTextareaExpand.value) {
    return {
      docTree: { height: 'calc(100% - 178px)' },
      tempTextarea: { height: '150px', padding: '10px' }
    }
  }
  return {
    docTree: { height: 'calc(100% - 28px)' },
    tempTextarea: { height: '0', padding: '' }
  }
})
const initTempTextarea = () => {
  tempTextarea.value = Local.get('editor_temp_textarea_value')
}
const tempInput = (value: string) => {
  Local.set(TempTextareaKey, value)
}

/**
 * 进入页面时, 保存文章
 */
const enterView = () => {
  autoSave()
  initTempTextarea()
  scrollTopLast()
}
/**
 * 退出页面时, 保存文章
 */
const exitView = () => {
  autoSave()
}

const { hideOne, resotreOne } = useResizeVertical(DocsRef, EditorContainerRef, ResizeDocsDividerRef, undefined, {
  persistent: true,
  keyOne: 'article_docs_width',
  keyTwo: 'article_editor_preview_width',
  defaultOne: '250px',
  defaultTwo: 'calc(100% - 250px)',
  maxOne: 700,
  minOne: 250
})
useResizeVertical(EditorRef, PreviewRef, ResizeEditorDividerRef, EditorOperatorRef)
//#endregion

//#region ----------------------------------------< 图片管理 >--------------------------------------
const PictureViewerInfoRef = ref()
const showPicInfo = (url: string) => {
  PictureViewerInfoRef.value.showPicInfo(url)
}
const refreshCache = () => {
  picCacheRefresh()
  parse()
}

/**
 * 右键菜单的上传回调
 * @param resp
 * @param file
 */
const onUploadSeccess: UploadProps['onSuccess'] = (resp, file) => {
  if (resp.code === '20000') {
    cmw.insertBlockCommand(`\n![${file.name}](${resp.data})\n`)
  } else {
    Notify.error(resp.msg, '上传失败')
  }
}

/**
 * 拖拽和黏贴上传
 * @param file 文件
 */
const uploadFile = (file: File) => {
  uploadForm(file, curArticle.value!.pid, (url: string) => {
    cmw.insertBlockCommand(`\n![${file.name}](${url})\n`)
  })
}

/**
 * 文件上传回调
 * @param event DragEvent | ClipboardEvent
 */
const uploadFileCallback = async (event: DragEvent | ClipboardEvent) => {
  if (!isArticle(curArticle.value)) return

  /**
   * 拖拽上传
   */
  if (event instanceof DragEvent) {
    let data: DataTransfer | null = event.dataTransfer
    if (data && data.files.length && data.files.length > 0) {
      for (const file of data.files) {
        uploadFile(file)
      }
    }
  }

  /**
   * 黏贴上传
   */
  if (event instanceof ClipboardEvent) {
    if (!event.clipboardData) return
    if (event.clipboardData.items.length === 0) return
    for (let i = 0; i < event.clipboardData.items.length; i++) {
      const file: File | null = event.clipboardData.items[i].getAsFile()
      if (file == null) {
        return
      }
      uploadFile(file)
    }
  }
}
//#endregion

//#region ----------------------------------------< html 事件监听 >----------------------------
const ArticleViewRef = ref()
const { articleReferenceView } = useArticleHtmlEvent(ArticleViewRef)

const openArticleWindow = (id: string) => {
  openNewArticleWindow('article_window_' + id, id)
}
//#endregion

//#region ----------------------------------------< 文档列表与当前文章 >----------------------------
const editorLoading = ref(false) // eidtor loading
const ArticleTreeDocsRef = ref()
const curDoc = ref<DocInfo>() // 当前选中的文档, 包含文件夹和文章, 如果选中是文件夹, 则不会重置编辑器中的文章
const curArticle = ref<DocInfo>() // 当前选中的文章, 用于在编辑器中展示
// 自定保存间隔, 5分钟不编辑则自动保存
const authSaveMs = 5 * 60 * 1000
// 非绑定数据
// 文章是否在解析时, 为 true 则正在解析, 为 false 则解析完成
let articleParseing = false
// 编辑器内容是否有变更, 防止在没有变更时频繁保存导致请求接口和版本号的无意义变更, 如果为 true, 则文章允许保存, 为 false 时跳过保存
let articleChanged = false
// 上次保存时间
let lastSaveTime: number = new Date().getTime()
// 自动保存定时器
let autoSaveInterval: NodeJS.Timeout
// 文章加载延迟遮罩
let editorLoadingTimeout: NodeJS.Timeout

provide(provideKeyDocInfo, curDoc)
provide(provideKeyCurArticleInfo, curArticle)

/**
 * 点击 doc title 的回调, 用于选中某个文档
 * 选中分为两种
 * 1:选中的是文件夹
 * 2:选中的是文章, 则查询文章内容, 变
 *
 * @param tree
 */
const clickCurDoc = async (tree: DocTree) => {
  let doc: DocInfo = treeToInfo(tree)
  curDoc.value = doc
  // 如果选中的是文章, 则查询文章详情, 用于在编辑器中显示以及注入
  if (doc.type == 3) {
    // 重复点击同一个, 不会多次查询
    if (isArticle(curArticle.value) && curArticle.value!.id == doc.id) {
      return
    }
    editorLoadingTimeout = setTimeout(() => (editorLoading.value = true), 100)
    await saveCurArticleContent(true)
    clearTocAndImg()
    await articleInfoApi({ id: doc.id, showToc: false, showMarkdown: true, showHtml: false })
      .then((resp) => {
        if (isNull(resp.data)) {
          return
        }
        curArticle.value = resp.data
        // 初次加载时立即渲染
        immediateParse = true
        if (isBlank(resp.data.markdown)) {
          setNewState('')
        } else {
          setNewState(resp.data.markdown)
        }
      })
      .finally(() => {
        if (editorLoadingTimeout) clearTimeout(editorLoadingTimeout)
        editorLoading.value = false
        articleChanged = false
      })
    nextTick(() => {
      scrollTopReset()
    })
  }
}

/**
 * 保存文章的正文, 并更新编辑器状态栏中的版本, 字数, 修改时间等信息.
 *
 * @param auto 是否为自动保存, 如果是自动保存, 则不弹出保存成功的提示框, 避免在非用户主动操作下弹框
 */
const saveCurArticleContent = async (auto: boolean = false) => {
  if (!isArticle(curArticle.value)) {
    return
  }
  const saveCallback = () => {
    if (!auto) {
      ElMessage.success({ message: '保存成功', duration: 1000, offset: 70, grouping: true })
    }
  }
  // 如果文档发生变动才保存
  if (!articleChanged) {
    console.info('%c文档内容无变化, 无需保存', 'background:#AD8CF2;color:#fff;padding-top:2px')
    saveCallback()
    return
  }
  // 如果文档正在解析中, 则等待解析完成
  while (articleParseing) {
    console.info('%c检测到正在解析, 等待解析完成', 'background:#AD7736;color:#fff;padding-top:2px')
    await sleep(100)
  }
  articleChanged = false
  let data = {
    id: curArticle.value!.id,
    name: curArticle.value!.name,
    markdown: cmw.getDocString(),
    html: PreviewRef.value.innerHTML,
    references: articleImg.value.concat(articleLink.value).map((item) => {
      let refer: ArticleReference = { targetId: '', targetName: '', targetUrl: '', type: 10 }
      Object.assign(refer, item)
      if (isBase64Img(refer.targetUrl)) {
        refer.targetUrl = ''
      }
      return refer
    })
  }
  await articleUpdContentApi(data)
    .then((resp) => {
      lastSaveTime = new Date().getTime()
      curArticle.value!.words = resp.data.words as number
      curArticle.value!.updTime = resp.data.updTime as string
      if (curArticle.value!.version != undefined) {
        curArticle.value!.version = curArticle.value!.version + 1
      } else {
        curArticle.value!.version = 1
      }
      saveCallback()
    })
    .catch(() => {
      articleChanged = true
    })
}
/**
 * 初始化自动保存定时器
 * 如果 authSaveMs 时间没有保存, 则自动保存.
 */
const initAutoSaveInterval = () => {
  autoSaveInterval = setInterval(() => {
    let current = new Date().getTime()
    if (current - lastSaveTime > authSaveMs) {
      autoSave()
    }
  }, 30 * 1000)
}
/**
 * 销毁自动保存定时器
 */
const distoryAutoSaveInterval = () => {
  clearInterval(autoSaveInterval)
}
/**
 * 自动保存, 该种方式不会有保存成功的提示
 */
const autoSave = () => {
  saveCurArticleContent(true)
}

//#endregion

//#region ----------------------------------------< codemirror/editor >----------------------------
let cmw: CmWrapper // codemirror editor wrapper

/**
 * 初始化编辑器, 创建编辑器封装器, 并在编辑器底部增加一个空白页
 */
const initEditor = (_doc?: string) => {
  cmw = new CmWrapper(
    CmWrapper.newEditor(
      // 创建 state
      CmWrapper.newState(
        () => {
          articleParseing = true
          debounceParse(parse, 300)
        },
        saveCurArticleContent,
        uploadFileCallback
      ),
      EditorRef.value
    )
  )
  appendEditorHolder()
}
/**
 * 将 markdown 原文设置到编辑器中, 并且会重置编辑器状态
 * @param md markdown
 */
const setNewState = (md: string): void => {
  cmw.setState(
    CmWrapper.newState(
      () => {
        articleChanged = true
        articleParseing = true
        allwaysBottom()
        debounceParse(parse, 300)
      },
      saveCurArticleContent,
      uploadFileCallback,
      md
    )
  )
  parse()
}

/**
 * 编辑器底部增加空白占位元素, 点击占位元素会时会聚焦在编辑器
 */
const appendEditorHolder = () => {
  // 创建元素
  let editorHeightHolder = document.createElement('div')
  editorHeightHolder.style.height = '65vh'
  editorHeightHolder.style.position = 'relative'
  editorHeightHolder.addEventListener('click', () => {
    let length = cmw.getDocLength()
    cmw.editor.focus()
    cmw.insert(length, length, '', length, length)
  })
  EditorRef.value.appendChild(editorHeightHolder)
}

/**
 * 编辑器滚动条永远置底
 */
const allwaysBottom = async () => {
  const clientHeight = EditorRef.value.clientHeight
  const scrollTop = EditorRef.value.scrollTop
  const scrollHeight = EditorRef.value.scrollHeight
  let a = clientHeight + scrollTop
  if (a >= scrollHeight - 100) {
    scrollWrapper.toBottom()
  }
}
//#endregion

//#region ----------------------------------------< marked/preview >-------------------------------
const renderInterval = ref(0) // 解析用时
const articleHtml = ref('') // 解析后的 html 内容
const renderAsync = ref({
  need: 0,
  done: 0
})
let immediateParse = false // 是否立即渲染, 文档初次加载时立即渲染, 内容变更时防抖渲染
/**
 * 自定义渲染
 */
const renderer = {
  table(header: string, body: string): string {
    return renderTable(header, body)
  },
  blockquote(quote: string): string {
    return renderBlockquote(quote)
  },
  codespan(src: string): string {
    return renderCodespan(src)
  },
  code(code: string, language: string | undefined, _isEscaped: boolean): string {
    return renderCode(code, language, _isEscaped, renderAsync.value)
  },
  heading(text: string, level: number, raw: string): string {
    return renderHeading(text, level, raw)
  },
  image(href: string | null, _title: string | null, text: string): string {
    articleImg.value.push({ targetId: '0', targetName: text, targetUrl: href as string, type: 10 })
    return renderImage(href, _title, text)
  },
  link(href: string, title: string | null | undefined, text: string): string {
    let { link, ref } = renderLink(href, title, text, ArticleTreeDocsRef.value.getDocTreeData())
    articleLink.value.push(ref)
    return link
  }
}

marked.use({ renderer: renderer })

/**
 * 解析 markdown 为 html, 并将 html 赋值给 articleHtml
 */
const parse = () => {
  const begin = Date.now()
  immediateParse = false
  let mdContent = cmw.getDocString()
  clearTocAndImg()
  renderAsync.value = {
    need: 0,
    done: 0
  }
  marked
    .parse(mdContent, { async: true })
    .then((content: string) => {
      articleHtml.value = content
      renderInterval.value = Date.now() - begin
      articleParseing = false
    })
    .then(() => {
      nextTick(() => {
        parseToc()
      }).then(() => {
        const clientHeight = EditorRef.value.clientHeight
        const scrollTop = EditorRef.value.scrollTop
        const scrollHeight = EditorRef.value.scrollHeight
        let a = clientHeight + scrollTop
        if (a >= scrollHeight - 150) {
          setTimeout(() => {
            PreviewRef.value.scrollTop = PreviewRef.value.scrollHeight
          }, 7)
        }
      })
    })
}

/**
 * 防抖, 防止频繁渲染造成的卡顿
 */
let debounceTimeout: NodeJS.Timeout | undefined
function debounceParse(parseFn: () => void, time = 500) {
  if (debounceTimeout != undefined) {
    clearTimeout(debounceTimeout)
  }
  if (immediateParse) {
    parseFn()
  } else {
    debounceTimeout = setTimeout(parseFn, time)
  }
}

//#endregion

//#region ----------------------------------------< TOC >------------------------------------------
const articleToc = shallowRef<Toc[]>([])
const articleImg = shallowRef<ArticleReference[]>([]) // 文章对图片引用
const articleLink = shallowRef<ArticleReference[]>([]) // 文章对链接的引用
const TocRef = ref()
const TocTitleRef = ref()
/**
 * 跳转至指定ID位置,ID为 标题级别-标题内容
 * @param level 标题级别
 * @param content 标题内容
 */
const toScroll = (id: string) => {
  let elm: HTMLElement = document.getElementById(id) as HTMLElement
  ;(elm.parentNode as Element).scrollTop = elm.offsetTop
}
// 清空当前目录内容
const clearTocAndImg = () => {
  articleImg.value = []
  articleLink.value = []
}

const parseToc = async () => {
  parseTocAsync(PreviewRef.value).then((tocs) => (articleToc.value = tocs))
}

useDraggable(TocRef, TocTitleRef)

//#endregion

//#region ----------------------------------------< 双屏滚动  >----------------------------------------
let scrollWrapper: EPScroll
const initScroll = async () => {
  scrollWrapper = new EPScroll(EditorRef.value, PreviewRef.value, cmw)
}
const scroll = (event: Event | string, source?: string, lineno?: number, colno?: number, error?: Error) => {
  scrollWrapper.sycnScroll(event, source, lineno, colno, error)
}
const scrollTopReset = () => scrollWrapper.scrollTopReset()
const scrollTopLast = () => scrollWrapper.scrollTopLast()
const addListenerScroll = () => EditorRef.value.addEventListener('scroll', scroll)
const removeListenerScroll = () => EditorRef.value.removeEventListener('scroll', scroll)
const scrollTop = () => scrollWrapper.toTop()
const scrollBottom = () => scrollWrapper.toBottom()
const handleSyncScroll = () => {
  editorOperator.value.sycnScroll = scrollWrapper.open()
}

//#endregion

//#region ----------------------------------------< 编辑器右键 >----------------------------------------
const editorRightMenu = ref<RightMenu>({ show: false, clientX: 0, clientY: 0 })
const rightMenuHeight = isElectron() ? 270 : 220

const handleEditorClickRight = (event: MouseEvent) => {
  event.preventDefault()
  editorRightMenu.value = { show: false, clientX: 0, clientY: 0 }
  let y = event.clientY
  if (document.body.clientHeight - event.clientY < rightMenuHeight) {
    y = event.clientY - rightMenuHeight
  }
  editorRightMenu.value = { show: true, clientX: event.clientX, clientY: y }
  setTimeout(() => {
    document.body.addEventListener('click', closeEditorRightMenu)
  }, 100)
}

const closeEditorRightMenu = () => {
  removeListenerEditorRightMenu()
  editorRightMenu.value.show = false
}

const removeListenerEditorRightMenu = () => {
  document.body.removeEventListener('click', closeEditorRightMenu)
}

/** 复制当前选中内容 */
const rightMenuCopy = () => {
  writeText(cmw.getSelectionRangesText())
}
/** 右键黏贴功能 */
const rightMenuPaste = () => {
  cmw.insertBlockCommand(readText())
}

/** 转大写功能 */
const upper = () => {
  cmw.toUpper()
}

/** 转小写功能 */
const lower = () => {
  cmw.toLower()
}

/**
 * 右键格式化表格功能
 */
const formatTable = () => {
  let ranges = cmw.getSlelctionRangesArr()
  if (ranges.length < 1) {
    Notify.error('未选中内容')
    return
  }
  if (ranges.length > 1) {
    Notify.error('选中内容过多')
    return
  }
  let text = cmw.sliceDoc(ranges[0].from, ranges[0].to)
  if (isBlank(text)) {
    return
  }
  cmw.insertBlockCommand(formartMarkdownTable(text))
}
//#endregion

//#region ----------------------------------------< 快捷键注册 >-------------------------------------
const alt_1: shortcutFunc = (): void => {
  docsExpand.value = !docsExpand.value
  if (!docsExpand.value) {
    hideOne()
  } else {
    resotreOne()
  }
}
const alt_2: shortcutFunc = (): void => {
  tocsExpand.value = !tocsExpand.value
}
// 全屏预览
const alt_3: shortcutFunc = (): void => {
  previewFullScreen = !previewFullScreen
  if (previewFullScreen) {
    editorFullScreen = false
  }
  changeEditorPreviewStyle()
}
// 全屏编辑
const alt_4: shortcutFunc = (): void => {
  editorFullScreen = !editorFullScreen
  if (previewFullScreen) {
    previewFullScreen = false
  }
  changeEditorPreviewStyle()
}

hotkeys.filter = function (_event) {
  return true
}

const bindKeys = () => {
  hotkeys('alt+1, command+1', () => {
    alt_1()
    return false
  })
  hotkeys('alt+2, command+2', () => {
    alt_2()
    return false
  })
  hotkeys('alt+3, command+3', () => {
    alt_3()
    return false
  })
  hotkeys('alt+4, command+4', () => {
    alt_4()
    return false
  })
}

const unbindKeys = () => {
  hotkeys.unbind('alt+1, command+1')
  hotkeys.unbind('alt+2, command+2')
  hotkeys.unbind('alt+3, command+3')
  hotkeys.unbind('alt+4, command+4')
}

//#endregion
</script>

<style scoped lang="scss">
@import '@renderer/assets/styles/bl-resize-divider.scss';
@import '@renderer/assets/styles/bl-loading-spinner.scss';
@import './styles/article-index.scss';
@import './styles/article-view-absolute.scss';
@import './styles/editor-right-menu.scss';
@import './styles/bl-preview-toc.scss';
@import './styles/article-backtop.scss';
</style>
