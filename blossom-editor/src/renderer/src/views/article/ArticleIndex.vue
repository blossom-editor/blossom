<template>
  <div class="index-article-root">

    <!-- folder menu -->
    <div class="doc-container" :style="{ width: docEditorStyle.docs }" v-show="docsExpand">

      <div class="doc-tree-menu-container" :style="tempTextareaStyle.docTree">
        <ArticleTreeDocs @click-doc="clickCurDoc" ref="ArticleTreeDocsRef"></ArticleTreeDocs>
      </div>

      <div class="doc-temp-textarea">
        <bl-row just="space-between" height="28px" class="doc-temp-textarea-workbench">
          <bl-row style="font-size: 10px;">
            <img src="@renderer/assets/imgs/note/cd.png" style="width: 15px;margin-right: 5px;">
            临时内容(可从便签快速设置)
          </bl-row>
          <div class="iconbl bl-subtract-line" @click="tempTextareaExpand = !tempTextareaExpand"></div>
        </bl-row>
        <bl-row class="doc-temp-textarea-input" :style="tempTextareaStyle.tempTextarea">
          <el-input v-model="tempTextarea" type="textarea" resize="none" @input="tempInput"></el-input>
        </bl-row>
      </div>
    </div>

    <!-- editor -->
    <div class="editor-container" :style="{ width: docEditorStyle.editor }" v-loading="editorLoading"
      element-loading-spinner="1" element-loading-text="正在读取文章内容...">
      <div class="editor-tools">
        <EditorTools @save="saveCurArticleContent()" @preview-full-screen="alt_3()" @editor-full-screen="alt_4()"
          @bold="cmw.commandBold()" @italic="cmw.commandItalic()" @strike="cmw.commandStrike()" @sub="cmw.commandSub()"
          @sup="cmw.commandSup()" @separator="cmw.commandSeparator()" @blockquote="cmw.commandQuote()"
          @blockquote-block="cmw.commandQuoteBlack()" @blockquote-green="cmw.commandQuoteGreen()"
          @blockquote-yellow="cmw.commandQuoteYellow()" @blockquote-red="cmw.commandQuoteRed()"
          @blockquote-blue="cmw.commandQuoteBlue()" @blockquote-purple="cmw.commandQuotePurple()"
          @code="cmw.commandCode()" @pre="cmw.commandPre()" @checkbox="cmw.commandCheckBox()"
          @unordered="cmw.commandUnordered()" @ordered="cmw.commandOrdered()" @table="cmw.commandTable()"
          @image="cmw.commandImg()" @link="cmw.commandLink()">
        </EditorTools>
      </div>
      <div class="editor-preview" :style="{ fontFamily: editorConfig.fontFamily }">
        <div class="gutter-holder" :style="editorPreviewStyle.gutter"></div>
        <div class="editor-codemirror" ref="EditorRef" :style="editorPreviewStyle.editor"
          @click.right="handleEditorClickRight"></div>
        <div class="preview-marked bl-preview" ref="PreviewRef" :style="editorPreviewStyle.preview" v-html="articleHtml">
        </div>
      </div>

      <!-- status -->
      <div class="editor-status">
        <EditorStatus :render-interval="renderInterval"></EditorStatus>
      </div>

      <!-- toc -->
      <div :class="['bl-preview-toc-absolute', (tocsExpand) ? 'is-expand-open' : 'is-expand-close']" ref="TocRef">
        <div class="toc-title" ref="TocTitleRef">目录 <span style="font-size: 10px;">(Alt+2 可隐藏)</span></div>
        <div class="toc-content" v-show="(tocsExpand)">
          <div v-for="toc in articleToc" :key="toc.index" :class="[toc.clazz]" @click="toScroll(toc.level, toc.content)">
            {{ toc.content }}
          </div>
        </div>
        <div class="img-title">引用图片</div>
        <div class="img-content">
          <div v-for="image in articleImg" :key="image.targetUrl">
            <el-image class="elimg" :src="image.targetUrl" fit="cover" :preview-src-list="[image.targetUrl]"
              :preview-teleported="true">
              <template #error>
                <div class="image-slot">
                  <el-icon :size="25">
                    <Picture />
                  </el-icon>
                </div>
              </template>
            </el-image>
          </div>
        </div>
      </div>

    </div>

    <Teleport to="body">
      <div v-show="editorRightMenu.show" class="editor-right-menu"
        :style="{ left: editorRightMenu.clientX + 'px', top: editorRightMenu.clientY + 'px' }">
        <div class="menu-content">
          <div class="menu-item" @click="rightMenuCopy">
            <span class="iconbl bl-copy-line"></span>复制
          </div>
          <div class="menu-item" @click="rightMenuPaste">
            <span class="iconbl bl-a-texteditorpastetext-line"></span>黏贴
          </div>
          <div class="menu-item">
            <el-upload :action="serverStore.serverUrl + uploadFileApiUrl" name="file" :data="{ pid: curArticle?.pid }"
              :headers="{ 'Authorization': 'Bearer ' + userStore.auth.token }" :show-file-list="false"
              :before-upload="beforeUpload" :on-success="onUploadSeccess" :on-error="onError">
              <span class="iconbl bl-image--line"></span>插入图片
            </el-upload>
          </div>
          <div class="menu-item" @click="formatTable">
            <span class="iconbl bl-transcript-line"></span>格式化选中表格
          </div>
          <div class="menu-item" @click="openExtenal('https://katex.org/#demo')">
            <span class="iconbl bl-a-texteditorsuperscript-line"></span>Katex 在线校验
          </div>
          <div class="menu-item" @click="openExtenal('https://mermaid.live/edit')">
            <span class="iconbl bl-a-statisticalviewpiechart3-line"></span>Mermaid 在线校验
          </div>
          <div class="menu-item" @click="openExtenal('https://www.emojiall.com/zh-hans')">
            <span style="margin-right: 4px;padding: 2px 0;">😉</span>Emoji网站
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
// vue
import { ref, computed, provide, onMounted, onBeforeUnmount, onActivated, onDeactivated } from "vue"
import { Picture } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import type { UploadProps } from 'element-plus'
import { storeToRefs } from "pinia"
import { useUserStore } from '@renderer/stores/user'
import { useServerStore } from '@renderer/stores/server'
import { useConfigStore } from '@renderer/stores/config'
import { articleInfoApi, articleUpdContentApi, uploadFileApiUrl } from '@renderer/api/blossom'
// ts
import { treeToInfo, provideKeyDocInfo, provideKeyCurArticleInfo } from '@renderer/views/doc/doc'
import { TempTextareaKey, ArticleReference, DocEditorStyle, EditorPreviewStyle } from '@renderer/views/article/article'
import { beforeUpload, onError } from '@renderer/views/picture/picture'
// utils
import { Local } from "@renderer/assets/utils/storage"
import { isBlank, isNull } from '@renderer/assets/utils/obj'
import { openExtenal, writeText, readText } from '@renderer/assets/utils/electron'
import { formartMarkdownTable } from '@renderer/assets/utils/formatTable'
// component
import { useDraggable } from '@renderer/components/Draggable'
import ArticleTreeDocs from "./ArticleTreeDocs.vue"
import EditorTools from './EditorTools.vue'
import EditorStatus from "./EditorStatus.vue"
import Notify from '@renderer/components/Notify'
// codemirror
import { CmWrapper } from './codemirror'
// marked
import marked, { simpleMarked, renderBlockquote, renderCode, renderCodespan, renderHeading, renderImage, renderTable, tokenizerCodespan, renderLink } from './markedjs'

// 快捷键注册
import type { shortcutFunc } from '@renderer/assets/utils/ShortcutRegister'
import ShortcutRegistrant from '@renderer/assets/utils/ShortcutRegister'

onMounted(() => {
  initEditor()
  addListenerScroll()
  initAutoSaveInterval()
})
onBeforeUnmount(() => {
  removeListenerShortcutMap()
  removeListenerEditorRightMenu()
  removeListenerScroll()
  distoryAutoSaveInterval()
})
onActivated(() => {
  enterView()
  addListererShortcutMap()
})
onDeactivated(() => {
  exitView()
  removeListenerShortcutMap()
})

//#region ----------------------------------------< panin store >--------------------------------------
const userStore = useUserStore()
const serverStore = useServerStore()
const configStore = useConfigStore()
const { editorConfig } = storeToRefs(configStore)
//#endregion

//#region ----------------------------------------< 公共参数和页面动态样式 >--------------------------------------
/**
 * 文档列表的展开和收起
 */
const docsExpand = ref<boolean>(true)
const tocsExpand = ref<boolean>(true)
const docEditorStyle = computed<DocEditorStyle>(() => {
  if (!docsExpand.value) {
    return { docs: '0px', editor: '100%' }
  }
  return { docs: '250px', editor: 'calc(100% - 250px)' }
})
/**
 * 编辑器和预览的展开收起
 */
const previewFullScreen = ref<boolean>(false) // 是否全屏展开预览
const editorFullScreen = ref<boolean>(false)  // 是否全屏展开编辑
const editorPreviewStyle = computed<EditorPreviewStyle>(() => {
  if (previewFullScreen.value) {
    return {
      gutter: { width: '0' },
      editor: { width: '0' },
      preview: { width: '100%' }
    }
  }
  if (editorFullScreen.value) {
    return {
      gutter: { width: '50px' },
      editor: { width: '100%' },
      preview: { width: '0', padding: '0' }
    }
  }
  return {
    gutter: { width: '50px' },
    editor: { width: '50%' },
    preview: { width: '50%' }
  }
})
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
}
/**
 * 退出页面时, 保存文章
 */
const exitView = () => {
  autoSave()
}
//#endregion

//#region ----------------------------------------< 文档列表与当前文章 >----------------------------
const ArticleTreeDocsRef = ref()
const curDoc = ref<DocInfo>()           // 当前选中的文档, 包含文件夹和文章, 如果选中是文件夹, 则不会重置编辑器中的文章
const curArticle = ref<DocInfo>()       // 当前选中的文章, 用于在编辑器中展示
const curActiveDoc = ref<DocInfo>()     // 当前激活的文档的 index, 防止在刷新列表时重置选中, 导致需要再次从文档菜单中逐个点击
// 非绑定数据
let articleIsChange: boolean = false    // 编辑器内容是否有变更, 防止在没有变更时频繁保存导致请求接口和版本号的无意义变更
let lastSaveTime: number = new Date().valueOf()// 上次保存时间
let autoSaveInterval: NodeJS.Timer
const authSaveMs = 5 * 60 * 1000

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
  curActiveDoc.value = doc // 设置激活的菜单, 用于在刷新后仍然能选中当前的选项
  // 如果选中的是文章, 则查询文章详情, 用于在编辑器中显示以及注入
  if (doc.type == 3) {
    // 重复点击同一个, 不会多次查询
    if (curIsArticle() && curArticle.value!.id == doc.id) {
      return
    }
    editorLoading.value = true
    await saveCurArticleContent(true)
    clearTocAndImg()
    await articleInfoApi({ id: doc.id, showToc: false, showMarkdown: true, showHtml: false }).then(resp => {
      if (isNull(resp.data)) {
        return
      }
      curArticle.value = resp.data
      // 初次加载, 不需要防抖解析 markdown 内容
      isDebounce = false
      if (isBlank(resp.data.markdown)) {
        setDoc('')
      } else {
        setDoc(resp.data.markdown)
      }
    }).finally(() => {
      editorLoading.value = false
      articleIsChange = false
    })
  }
}

/**
 * 保存文章的正文, 并更新编辑器状态栏中的版本, 字数, 修改时间等信息.
 * 
 * @param auto 是否为自动保存, 如果是自动保存, 则不弹出保存成功的提示框, 避免在非用户主动操作下弹框
 */
const saveCurArticleContent = async (auto: boolean = false) => {
  if (!curIsArticle()) {
    return
  }
  const saveCallback = () => {
    if (!auto) {
      ElMessage.success({ message: '保存成功', duration: 1000, offset: 50, grouping: true })
    }
  }
  // 如果文档发生变动才保存
  if (!articleIsChange) {
    console.info('文档内容无变化, 无需保存')
    saveCallback()
    return
  }
  articleIsChange = false
  let data = {
    id: curArticle.value!.id,
    name: curArticle.value!.name,
    markdown: cmw.getDocString(),
    html: articleHtml.value,
    toc: JSON.stringify(articleToc.value),
    references: articleImg.value.concat(articleLink.value)
  }
  await articleUpdContentApi(data).then(resp => {
    lastSaveTime = new Date().valueOf()
    curArticle.value!.words = resp.data.words as number
    curArticle.value!.updTime = resp.data.updTime as string
    if (curArticle.value!.version != undefined) {
      curArticle.value!.version = curArticle.value!.version + 1
    } else {
      curArticle.value!.version = 1
    }
    saveCallback()
  })
}
/**
 * 初始化自动保存定时器
 * 如果 authSaveMs 时间没有保存, 则自动保存.
 */
const initAutoSaveInterval = () => {
  autoSaveInterval = setInterval(() => {
    let current = new Date().valueOf()
    if ((current - lastSaveTime) > authSaveMs) {
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
/**
 * 判断当前选中的是否是文章
 */
const curIsArticle = (): boolean => {
  if (isNull(curArticle)) { return false }
  if (isNull(curArticle.value)) { return false }
  if (isNull(curArticle.value?.type) || curArticle.value?.type != 3) { return false }
  return true
}
/**
 * 文件上传成功
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

//#endregion

//#region ----------------------------------------< codemirror/editor >----------------------------
const EditorRef = ref()           // editor dom
const editorLoading = ref(false)  // eidtor loading
let cmw: CmWrapper                // codemirror editor wrapper
/** 
 * 初始化编辑器 
 */
const initEditor = (_doc?: string) => {
  cmw = new CmWrapper(CmWrapper.newEditor(CmWrapper.newState(() => { debounce(parse, 300) }, saveCurArticleContent), EditorRef.value))
}
/**
 * 将 markdown 原文设置到编辑器中, 并且会重置编辑器状态
 * @param md markdown
 */
const setDoc = (md: string): void => {
  cmw.setState(CmWrapper.newState(() => { debounce(parse, 300) }, saveCurArticleContent, md))
  parse()
}

//#endregion

//#region ----------------------------------------< marked/preview >-------------------------------
const renderInterval = ref<number>(0)     // 解析用时
const PreviewRef = ref()                  // 显式 html 的 dom
const articleHtml = ref<string>('')       // 解析后的 html 内容
let isDebounce: boolean = false           // 是否在渲染时设置防抖, 切换文档时不用防抖渲染
/**
 * 自定义渲染
 */
const renderer = {
  table(header: string, body: string): string { return renderTable(header, body) },
  blockquote(quote: string): string { return renderBlockquote(quote) },
  codespan(src: string): string { return renderCodespan(src) },
  code(code: string, language: string | undefined, _isEscaped: boolean): string {
    return renderCode(code, language, _isEscaped, (eleid: string, svg: string) => {
      articleHtml.value = articleHtml.value.replaceAll(`>${eleid}<`, `>${svg}<`)
    })
  },
  heading(text: any, level: number): string {
    articleToc.value.push({ level: level, clazz: 'toc-' + level, index: articleToc.value.length, content: text })
    return renderHeading(text, level)
  },
  image(href: string | null, _title: string | null, text: string): string {
    articleImg.value.push({ targetId: 0, targetName: text, targetUrl: href as string, type: 10 })
    return renderImage(href, _title, text)
  },
  link(href: string | null, title: string | null, text: string): string {
    let { link, ref } = renderLink(href, title, text, ArticleTreeDocsRef.value.getDocTreeData())
    articleLink.value.push(ref)
    return link
  }
}

/**
 * 自定义解析
 */
const tokenizer = {
  codespan(src: string): any { return tokenizerCodespan(src) }
}

//@ts-ignore
marked.use({ tokenizer: tokenizer, renderer: renderer })

/**
 * 解析 markdown 为 html, 并将 html 赋值给 articleHtml
 */
const parse = () => {
  renderInterval.value = Date.now()
  articleIsChange = true
  isDebounce = true
  let mdContent = cmw.getDocString()
  clearTocAndImg()
  marked.parse(mdContent, { async: true }).then((content: string) => {
    articleHtml.value = content
    renderInterval.value = Date.now() - renderInterval.value
  })
}

/**
 * 防抖, 防止频繁渲染造成的卡顿
 */
let debounceTimeout: NodeJS.Timeout | undefined
function debounce(fn: () => void, time = 500) {
  if (debounceTimeout != undefined) {
    clearTimeout(debounceTimeout)
  }
  if (isDebounce) {
    debounceTimeout = setTimeout(fn, time)
  } else {
    fn()
  }
}

//#endregion

//#region ----------------------------------------< TOC >------------------------------------------
const articleToc = ref<any[]>([])
const articleImg = ref<ArticleReference[]>([])  // 文章对图片引用
const articleLink = ref<ArticleReference[]>([]) // 文章对链接的引用
const TocRef = ref()
const TocTitleRef = ref()
/**
 * 跳转至指定ID位置,ID为 标题级别-标题内容
 * @param level 标题级别
 * @param content 标题内容
 */
const toScroll = (level: number, content: string) => {
  let id = level + '-' + content
  let elm: HTMLElement = document.getElementById(id) as HTMLElement
  (elm.parentNode as Element).scrollTop = elm.offsetTop
}
// 清空当前目录内容
const clearTocAndImg = () => {
  articleToc.value = []
  articleImg.value = []
  articleLink.value = []
}

useDraggable(TocRef, TocTitleRef)

//#endregion

//#region ----------------------------------------< 双屏滚动  >----------------------------------------

const addListenerScroll = () => {
  EditorRef.value?.addEventListener('scroll', sycnScroll)
}

const removeListenerScroll = () => {
  EditorRef.value?.removeEventListener('scroll', sycnScroll)
}

const marginTop = 75
const matchHtmlTags = 'p, h1, h2, h3, h4, h5, h6, ul, ol, li, pre, blockquote, hr, table, tr, iframe, span'
const sycnScroll = (_event: Event | string, _source?: string, _lineno?: number, _colno?: number, _error?: Error): any => {
  if (EditorRef.value == undefined) {
    return
  }
  // console.log(EditorRef.value?.scrollHeight,
  // EditorRef.value?.clientHeight,
  // EditorRef.value?.scrollTop)

  // 如果在头部附近
  if (EditorRef.value?.scrollTop < 20) {
    PreviewRef.value.firstChild.scrollIntoView()
  }
  // 如果在尾部附近
  else if (EditorRef.value?.clientHeight + EditorRef.value?.scrollTop > EditorRef.value?.scrollHeight - 20) {
    PreviewRef.value.scrollTop = PreviewRef.value.scrollHeight
  }
  // 其他
  else {
    // 文档头部, 距离整个浏览器的距离
    const top = cmw.getDocumentTop()

    // 获取可见位置最顶部的内容
    const topBlock = cmw.getElementAtHeight(Math.abs(top) + marginTop)
    // 从0开始获取全部不可见的内容的 markdown 原文档
    const invisibleMarkdown: string = cmw.sliceDoc(0, topBlock.from)


    // 将不可见的内容全部转换为 html
    //@ts-ignore
    simpleMarked!.parse(invisibleMarkdown, { async: true }).then((html: string) => {
      const invisibleHtml = html
      // 将不可见的的 html 转换为 dom 对象, 是一个从 <html> 标签开始的 dom 对象
      const invisibleDomAll = new DOMParser().parseFromString(invisibleHtml, 'text/html')
      // body 下的内容才是由 markdown 转换而来的, 不可见内容转换的 dom 集合
      const editorDoms = invisibleDomAll.body.querySelectorAll(matchHtmlTags)
      // 预览页面的 dom 集合
      const previewDoms = PreviewRef.value.querySelectorAll(matchHtmlTags)
      let targetIndex = editorDoms.length
      // 预览页面的 dom 数小于 markdown 转换的 dom 数, 处理数组边界
      if (targetIndex > previewDoms.length) {
        targetIndex = previewDoms.length
      }
      const tagetDom = previewDoms[targetIndex]
      tagetDom.scrollIntoView({ behavior: "smooth", block: "start", inline: "nearest" })
    })
  }
}

//#endregion

//#region ----------------------------------------< 编辑器右键 >----------------------------------------
const editorRightMenu = ref<RightMenu>({ show: false, clientX: 0, clientY: 0 })
const rightMenuHeight = 215


const handleEditorClickRight = (event: MouseEvent) => {
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
const rightMenuCopy = () => { writeText(cmw.getSelectionRangesText()) }
/** 右键黏贴功能 */
const rightMenuPaste = () => { cmw.insertBlockCommand(readText()) }

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
const shortcutRegistrant: ShortcutRegistrant = new ShortcutRegistrant().setDebug(false)
const alt_1: shortcutFunc = (): void => { docsExpand.value = !docsExpand.value }
const alt_2: shortcutFunc = (): void => { tocsExpand.value = !tocsExpand.value }
const alt_3: shortcutFunc = (): void => {
  previewFullScreen.value = !previewFullScreen.value
  if (previewFullScreen.value) {
    editorFullScreen.value = false
  }
}
const alt_4: shortcutFunc = (): void => {
  editorFullScreen.value = !editorFullScreen.value
  if (previewFullScreen.value) {
    previewFullScreen.value = false
  }
}

const keydown = (evnet: KeyboardEvent) => { shortcutRegistrant.keydown(evnet) }
const keyup = (evnet: KeyboardEvent) => { shortcutRegistrant.keyup(evnet) }

/** 注册快捷键 */
const addListererShortcutMap = () => {
  // Alt + 1: 隐藏菜单
  // Alt + 2: 隐藏目录
  // Alt + v: 隐藏编辑器
  let altAnd: Map<string, shortcutFunc> = new Map()
  altAnd.set("Digit1", alt_1)
  altAnd.set("Digit2", alt_2)
  altAnd.set("Digit3", alt_3)
  altAnd.set("Digit4", alt_4)
  shortcutRegistrant.register("AltLeft", altAnd)

  window.addEventListener("keydown", keydown)
  window.addEventListener("keyup", keyup)
  window.onblur = () => {
    shortcutRegistrant.clearDownCodes()
  }
}

/** 删除快捷键 */
const removeListenerShortcutMap = () => {
  window.removeEventListener('keydown', keydown)
  window.removeEventListener('keyup', keyup)
}

//#endregion

</script>

<style scoped lang="scss">
@import './style/article-index.scss';
@import './style/bl-preview-toc.scss';

.bl-preview {
  h1 {
    font-size: 30px;
  }
}

:deep(.el-loading-spinner) {
  @extend .bl-loading-spinner;
}
</style>