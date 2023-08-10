<template>
  <div class="index-article-root">

    <!-- folder menu -->
    <div class="doc-container" :style="{ width: docEditorStyle.docs }" v-show="docsExpand" v-loading="docTreeLoading"
      :default-active="docTreeDefaultActive" element-loading-text="One moment please...">
      <!-- 文件夹操作 -->
      <div class="doc-workbench">
        <ArticleTreeWorkbench @refresh-doc-tree="getDocTree" @show-sort="handleShowSort"></ArticleTreeWorkbench>
      </div>
      <!-- 文件夹 -->
      <el-menu v-if="!isEmpty(docTreeData)" class="doc-trees" :style="tempTextareaStyle.docTree"
        :default-active="curActiveDoc?.id">

        <!-- ================================================ L1 ================================================ -->
        <div v-for="L1 in docTreeData" :key="L1.i">

          <!-- L1无下级 -->
          <el-menu-item v-if="isEmpty(L1.children)" :index="L1.i">
            <template #title>
              <ArticleTreeTitle :trees="L1" @click-doc="clickCurDoc" :size="15" />
            </template>
          </el-menu-item>

          <!-- L1有下级 -->
          <el-sub-menu v-else :expand-open-icon="ArrowDownBold" :expand-close-icon="ArrowRightBold" :index="L1.i">
            <template #title>
              <ArticleTreeTitle :trees="L1" @click-doc="clickCurDoc" :size="15" />
            </template>

            <!-- ================================================ L2 ================================================ -->
            <div v-for="L2 in L1.children" :key="L2.i">
              <!-- L2无下级 -->
              <el-menu-item v-if="isEmpty(L2.children)" :index="L2.i">
                <template #title>
                  <ArticleTreeTitle :trees="L2" @click-doc="clickCurDoc" />
                </template>
              </el-menu-item>

              <!-- L2有下级 -->
              <el-sub-menu v-else :expand-open-icon="ArrowDownBold" :expand-close-icon="ArrowRightBold" :index="L2.i">
                <template #title>
                  <ArticleTreeTitle :trees="L2" @click-doc="clickCurDoc" />
                </template>

                <!-- ================================================ L3 ================================================ -->
                <div v-for="L3 in L2.children" :key="L3.i">
                  <!-- L3无下级 -->
                  <el-menu-item v-if="isEmpty(L3.children)" :index="L3.i">
                    <template #title>
                      <ArticleTreeTitle :trees="L3" @click-doc="clickCurDoc" />
                    </template>
                  </el-menu-item>

                  <!-- L3有下级 -->
                  <el-sub-menu v-else :expand-open-icon="ArrowDownBold" :expand-close-icon="ArrowRightBold" :index="L3.i">
                    <template #title>
                      <ArticleTreeTitle :trees="L3" @click-doc="clickCurDoc" />
                    </template>

                    <!-- ================================================ L4 ================================================ -->
                    <div v-for="L4 in L3.children" :key="L4.i">
                      <!-- L4 不允许有下级, 只允许4级 -->
                      <el-menu-item v-if="isEmpty(L4.children)" :index="L4.i">
                        <template #title>
                          <ArticleTreeTitle :trees="L4" @click-doc="clickCurDoc" />
                        </template>
                      </el-menu-item>
                    </div>
                  </el-sub-menu>
                </div>

              </el-sub-menu>
            </div>
          </el-sub-menu>
        </div>
      </el-menu>

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
      element-loading-spinner="1" element-loading-text="正在读取内容...">
      <div class="editor-tools">
        <EditorTools @save="saveCurArticleContent()" @preview-full-screen="alt_3()" @editor-full-screen="alt_4()"
          @bold="commandInlineBold(getEditor())" @italic="commandInlineItalic(getEditor())"
          @strike="commandInlineStrike(getEditor())" @sub="commandInlineSub(getEditor())"
          @sup="commandInlineSup(getEditor())" @separator="commandBlockSeparator(getEditor())"
          @blockquote="commandBlockquote(getEditor())" @blockquote-block="commandBlockquoteBlack(getEditor())"
          @blockquote-green="commandBlockquoteGreen(getEditor())"
          @blockquote-yellow="commandBlockquoteYellow(getEditor())" @blockquote-red="commandBlockquoteRed(getEditor())"
          @blockquote-blue="commandBlockquoteBlue(getEditor())" @blockquote-purple="commandBlockquotePurple(getEditor())"
          @code="commandInlineCode(getEditor())" @pre="commandBlockPre(getEditor())"
          @checkbox="commandBlockCheckBox(getEditor())" @unordered="commandBlockUnordered(getEditor())"
          @ordered="commandBlockOrdered(getEditor())" @table="commandBlockTable(getEditor())"
          @image="commandBlockImg(getEditor())" @link="commandBlockLink(getEditor())">
        </EditorTools>
      </div>
      <div class="editor-preview" @click.right="handleEditorClickRight" :style="{ fontFamily: editorConfig.fontFamily }">
        <div class="gutter-holder" :style="editorPreviewStyle.gutter"></div>
        <div class="editor-codemirror" ref="EditorRef" :style="editorPreviewStyle.editor"></div>
        <div class="preview-marked bl-preview" ref="PreviewRef" :style="editorPreviewStyle.preview" v-html="articleHtml">
        </div>
      </div>
      <!-- editor status -->
      <div class="editor-status">
        <EditorStatus :render-interval="renderInterval"></EditorStatus>
      </div>

      <!-- the toc -->
      <div :class="['bl-preview-toc-absolute', (tocsExpand) ? 'is-expand-open' : 'is-expand-close']">
        <div class="toc-title">目录 <span style="font-size: 10px;">(Alt+2 可隐藏)</span></div>
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

    <Teleport to=" body">
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
          <div class="menu-item" @click="openExtenal('https://www.emojiall.com/zh-hans')">
            <span style="margin-right: 4px;padding: 2px 0;">😉</span>Emoji网站
          </div>
          <div class="menu-item" @click="openExtenal('https://www.xuhuhu.com/markdown-formatter.html')">
            <span class="iconbl bl-transcript-fill"></span>在线格式化工具
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
// vue
import { ref, computed, provide, onMounted, onBeforeUnmount, onActivated, onDeactivated, shallowRef } from "vue"
import { ArrowDownBold, ArrowRightBold, Picture } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import type { UploadProps } from 'element-plus'
import { useRoute } from 'vue-router'
import { useUserStore } from '@renderer/stores/user'
import { useServerStore } from '@renderer/stores/server'
import { useConfigStore } from '@renderer/stores/config'
// api
import { docTreeApi, articleInfoApi, articleUpdContentApi, uploadFileApiUrl } from '@renderer/api/blossom'
// ts
import { treeToInfo, provideKeyDocTree, provideKeyDocInfo, provideKeyCurArticleInfo } from '@renderer/views/doc/doc'
import {
  TempTextareaKey,
  codemirrorTheme, insertBlockCommand,
  commandInlineBold, commandInlineItalic, commandInlineStrike, commandInlineSub, commandInlineSup,
  commandBlockSeparator, commandBlockquote, commandBlockquoteBlack, commandBlockquoteGreen, commandBlockquoteYellow, commandBlockquoteRed, commandBlockquoteBlue, commandBlockquotePurple,
  commandInlineCode, commandBlockPre, commandBlockCheckBox,
  commandBlockUnordered, commandBlockOrdered, commandBlockTable, commandBlockImg, commandBlockLink
} from '@renderer/views/article/article'
import { beforeUpload, onError } from '@renderer/views/picture/picture'

// utils
import { isEmpty } from 'lodash'
import { isBlank, isNotNull, isNull } from '@renderer/assets/utils/obj'
import { openExtenal } from '@renderer/assets/utils/electron'
import { formartMarkdownTable } from '@renderer/assets/utils/formatTable'

// component
import ArticleTreeTitle from '@renderer/views/article/ArticleTreeTitle.vue'
import ArticleTreeWorkbench from "@renderer/views/article/ArticleTreeWorkbench.vue"
import EditorTools from './EditorTools.vue'
import EditorStatus from "./EditorStatus.vue"
import Notify from '@renderer/components/Notify'

// codemirror
import { EditorState } from "@codemirror/state"
import { EditorView, basicSetup } from "codemirror"
import { ViewUpdate, keymap, BlockInfo } from "@codemirror/view"
import { insertTab, indentLess } from "@codemirror/commands"
import { markdown as cmmd } from '@codemirror/lang-markdown'
import { languages } from "@codemirror/language-data"

// marked
import { marked } from 'marked'

// 快捷键注册
import type { shortcutFunc } from '@renderer/assets/utils/ShortcutRegister'
import ShortcutRegistrant from '@renderer/assets/utils/ShortcutRegister'
import { Local } from "@renderer/assets/utils/storage"
import { storeToRefs } from "pinia"

onMounted(() => {
  initEditor()
  addListenerScroll()
  initAutoSaveInterval()
})
onBeforeUnmount(() => {
  removeListenerShortcutMap()
  removeListenerRightMenu()
  removeListenerScroll()
  distoryAutoSaveInterval()
})
onActivated(() => {
  enterView()
  getRouteQueryParams()
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
interface DocEditorStyle { docs: string; editor: string }
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
interface EditorPreviewStyle { gutter: any, editor: any, preview: any }
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
      docTree: {
        height: 'calc(100% - 90px - 178px)'
      },
      tempTextarea: {
        height: '150px',
        padding: '10px'
      }
    }
  }
  return {
    docTree: {
      height: 'calc(100% - 90px - 28px)'
    },
    tempTextarea: {
      height: '0',
      padding: ''
    }
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
  getDocTree(false, false, false)
  initTempTextarea()
}
/**
 * 退出页面时, 保存文章
 */
const exitView = () => {
  autoSave()
}
const route = useRoute()
/**
 * 获取路由参数
 */
const getRouteQueryParams = () => {
  let articleId = route.query.articleId
  if (isNotNull(articleId)) {
    docTreeDefaultActive.value = articleId as string
    let treeParam: any = { ty: 3, i: articleId }
    clickCurDoc(treeParam)
  }
}
//#endregion

//#region ----------------------------------------< 文档列表与当前文章 >----------------------------
const docTreeLoading = ref(true)        // 文档菜单的加载动画
const showSort = ref(false)             // 是否显示文章排序
const docTreeDefaultActive = ref('')    // 文档的默认选中项, 用于外部跳转后选中菜单
const docTreeData = ref<DocTree[]>([])  // 文档菜单
const curDoc = ref<DocInfo>()           // 当前选中的文档, 包含文件夹和文章, 如果选中是文件夹, 则不会重置编辑器中的文章
const curArticle = ref<DocInfo>()       // 当前选中的文章, 用于在编辑器中展示
const curActiveDoc = ref<DocInfo>()     // 当前激活的文档的 index, 防止在刷新列表时重置选中, 导致需要再次从文档菜单中逐个点击
// 非绑定数据
let articleIsChange: boolean = false    // 编辑器内容是否有变更, 防止在没有变更时频繁保存导致请求接口和版本号的无意义变更
let lastSaveTime: number = new Date().valueOf()// 上次保存时间
let autoSaveInterval: NodeJS.Timer
const authSaveMs = 5 * 60 * 1000

// 注入的相关信息
provide(provideKeyDocTree, docTreeData)
provide(provideKeyDocInfo, curDoc)
provide(provideKeyCurArticleInfo, curArticle)

/**
 * 获取文档树状列表
 * 1. 初始化是全否调用
 * 2. 在 workbench 中点击按钮调用, 每个按钮是单选的
 */
const getDocTree = (isOnlyOpen: boolean, isOnlySubject: boolean, isOnlyStar: boolean) => {
  docTreeLoading.value = true
  docTreeApi({ onlyPicture: false, onlyOpen: isOnlyOpen, onlySubject: isOnlySubject, onlyStar: isOnlyStar }).then(resp => {
    docTreeData.value = resp.data
    concatSort(docTreeData.value)
  }).finally(() => {
    docTreeLoading.value = false
  })
}

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
    markdown: getEditor().state.doc.toString(),
    html: getArticleHtml(),
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
  console.log('开启自动保存');
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
  console.log('关闭自动保存');
  clearInterval(autoSaveInterval)
}
/**
 * 自动保存, 该种方式不会有保存成功的提示
 */
const autoSave = () => {
  saveCurArticleContent(true)
}
/**
 * 在名称中显式排序
 * @param trees 
 */
const concatSort = (trees: DocTree[]) => {
  for (let i = 0; i < trees.length; i++) {
    if (!isEmpty(trees[i].children)) {
      concatSort(trees[i].children as DocTree[])
    }
    if (showSort.value) {
      trees[i].n = trees[i].s + '〉' + trees[i].n
    } else {
      trees[i].n = trees[i].n.substring(trees[i].n.indexOf('〉') + 1)
    }
  }
}
/**
 * 是否显示
 */
const handleShowSort = () => {
  showSort.value = !showSort.value
  concatSort(docTreeData.value)
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
    insertBlockCommand(getEditor(), `\n![${file.name}](${resp.data})\n`)
  } else {
    Notify.error(resp.msg, '上传失败')
  }
}
/**
 * 递归从文档树状列表中获取指定ID的文章信息
 * @param articleId 文档DI, 通常是文章ID, 也兼容文件夹ID的获取
 * @param trees 文档树状列表
 */
const getDocInfoFromTrees = (articleId: number, trees: DocTree[]): DocTree | undefined => {
  let target: DocTree | undefined
  for (let i = 0; i < trees.length; i++) {
    let tree = trees[i]
    // console.log(articleId, tree.i, tree.i == articleId);
    if (tree.i == articleId) {
      target = tree
    } else if (!isEmpty(tree.children)) {
      target = getDocInfoFromTrees(articleId, tree.children!)
    }
    if (target != undefined) {
      break
    }
  }
  return target
}

//#endregion

//#region ----------------------------------------< codemirror/editor >----------------------------
// editor dom
const EditorRef = ref()
const editorLoading = ref(false)
const editor = shallowRef<EditorView>()
const createEditorState = (doc?: string): EditorState => {
  return EditorState.create({
    doc: doc,
    extensions: [
      basicSetup, cmmd({ codeLanguages: languages }), EditorView.theme(codemirrorTheme),
      keymap.of([
        { key: 'Tab', run: insertTab, },
        { key: 'Shift-Tab', run: indentLess },
        { key: 'Ctrl-s', run(_view: EditorView) { saveCurArticleContent(); return true } },
        { key: 'Alt-b', run(view: EditorView) { commandInlineBold(view); return true } },
        { key: 'Alt-i', run(view: EditorView) { commandInlineItalic(view); return true } },
        { key: 'Alt-s', run(view: EditorView) { commandInlineStrike(view); return true } },
        { key: 'Alt-t', run(view: EditorView) { commandBlockTable(view); return true } },
        { key: 'Alt-e', run(view: EditorView) { commandInlineCode(view); return true } },
        { key: 'Alt-m', run(view: EditorView) { commandBlockImg(view); return true } },
        { key: 'Alt-k', run(view: EditorView) { commandBlockLink(view); return true } },
        { key: 'Ctrl-alt-c', run(view: EditorView) { commandBlockCheckBox(view); return true } },
        { key: 'Ctrl-alt-p', run(view: EditorView) { commandInlineSup(view); return true } },
        { key: 'Ctrl-alt-b', run(view: EditorView) { commandInlineSub(view); return true } },
        { key: 'Ctrl-alt-e', run(view: EditorView) { commandBlockPre(view); return true } },
        { key: 'Ctrl-alt-s', run(view: EditorView) { commandBlockSeparator(view); return true } },
      ]),
      EditorView.updateListener.of((viewUpd: ViewUpdate) => {
        if (viewUpd.docChanged) debounce(parse, 300)
      })
    ]
  })
}
/**
 * 初始化编辑器
 */
const initEditor = (doc?: string) => {
  editor.value = new EditorView({
    state: createEditorState(doc),
    parent: EditorRef.value
  })
}

const getEditor = (): EditorView => {
  if (editor.value === undefined) {
    throw new Error("editor:EditorView 未初始化")
  }
  editor.value.setState
  return editor.value
}

/**
 * 将 markdown 原文设置到编辑器中
 * @param md markdown
 */
const setDoc = (md: string): void => {
  getEditor().setState(createEditorState(md))
  parse()
}

const addListenerScroll = () => {
  EditorRef.value?.addEventListener('scroll', scroll)
}

const removeListenerScroll = () => {
  EditorRef.value?.removeEventListener('scroll', scroll)
}
//#endregion

//#region ----------------------------------------< marked/preview >-------------------------------
// 解析用时
const renderInterval = ref<number>(0)
// 显式 html 的 dom
const PreviewRef = ref()
// 解析后的 html 内容
const articleHtml = ref<string>('')
// 解析 markdown 时, 是否将图片和标题解析成列表对象
let parseTocAndReferences: boolean = true
let isDebounce: boolean = false

/**
 * 自定义解析
 */
const renderer = {
  /**
   * 标题解析为 TOC 集合, 增加锚点跳转
   * @param text  标题内容
   * @param level 标题级别
   */
  heading(text: any, level: number) {
    const realLevel = level
    if (parseTocAndReferences) {
      articleToc.value.push({
        level: realLevel,
        clazz: 'toc-' + realLevel,
        index: articleToc.value.length,
        content: text
      })
    }
    return `<h${realLevel} id="${realLevel}-${text}">
              ${text}
            </h${realLevel}>`;
  },
  /**
   * a 标签
   * @param href  链接地址
   * @param title 链接标题 <a title="title">, 语法拓展内容在title中
   * @param text  链接的名称
   */
  link(href: string | null, title: string | null, text: string) {
    let aTag: string
    let linkReference: ArticleReference = {
      targetId: 0,
      targetName: text,
      targetUrl: href as string,
      type: 21
    }
    // 没有标题, 普通链接
    if (isBlank(title)) {
      aTag = `<a target="_blank" href=${href} target="_blank">${text}</a>`
    } else {
      let arr = title!.match(/(?<=\$\$).*?(?=\$\$)/)
      let isInnerArticle: boolean = arr != null && arr.length > 0 && !isBlank(arr[0])
      if (isInnerArticle) {
        let articleId = Number(arr![0])
        // 如果ID不是数字
        if (isNaN(articleId)) {
          aTag = `<a target="_blank" href=${href} title=${title}>${text}</a>`
        }

        let article = getDocInfoFromTrees(articleId, docTreeData.value)
        if (article != undefined) {
          linkReference.targetId = article.i
          linkReference.targetName = article.n
          linkReference.type = 11
        }

        aTag = `<a target="_blank" href=${href} class="inner-link bl-tip bl-tip-bottom" data-tip="双链引用: 《${text}》">${text}</a>`
      } else {
        aTag = `<a target="_blank" href=${href} title=${title} >${text}</a>`
      }
    }

    if (parseTocAndReferences) {
      articleLink.value.push(linkReference)
    }
    return aTag
  },
  /**
   * 表格 header/body
   */
  table(header: string, body: string) {
    let arr = header.match(/(?<=\$\$).*?(?=\$\$)/)
    let isContainer: boolean = arr != null && arr[0] === 'container'
    if (isContainer) {
      return `<table class="bl-table-container"><thead>${header}</thead><tbody>${body}</tbody></table>`
    }
    return `<table><thead>${header}</thead><tbody>${body}</tbody></table>`
  },
  /**
   * 引用拓展, 为引用指定颜色
   * @param quote 引用内部文字的内容
   */
  blockquote(quote: string) {
    let finalQuote = quote
    let clazz = 'bl-blockquote-'
    let colors = ['green', 'yellow', 'red', 'blue', 'purple', 'black']
    for (let i = 0; i < colors.length; i++) {
      let color = colors[i]
      let target = '<p>$$' + color + '$$'
      if (quote.startsWith(target)) {
        clazz = 'bl-blockquote-' + color
        finalQuote = quote.replaceAll(target, '<p>')
        break
      }
    }
    return `<blockquote class="${clazz}">${finalQuote}</blockquote>`
  },
  /**
   * 自定义代码块内容解析:
   * 1. bilibili
   * 格式为: ```bilibili$$bvid$$w100$$h100
   * 
   * 官方使用文档: https://player.bilibili.com/
   * 
   * @param code      解析后的 HTML 代码
   * @param language  语言
   * @param isEscaped 
   */
  code(code: string, language: string | undefined, _isEscaped: boolean) {
    if (language == undefined) {
      language = 'text'
    }

    if (language.startsWith('bilibili')) {
      let bvid = ''
      let width = '100%'
      let height = '300px'
      let tags: string[] = language.split('$$')
      if (tags.length > 1) {
        if (tags.length >= 2) {
          bvid = tags[1]
        }
        if (tags.length >= 3) {
          for (let i = 2; i < tags.length; i++) {
            let tag = tags[i]
            if (tag.startsWith('w')) {
              width = tags[i].substring(1)
              if (!width.endsWith('%')) {
                width += 'px'
              }
            }
            if (tag.startsWith('h')) {
              height = tags[i].substring(1)
              if (!height.endsWith('%')) {
                width += 'px'
              }
            }
          }
        }
      }

      if (isBlank(bvid)) {
        return `<div style="width:100%;padding:40px;background-color:#000000;color:#ffffff;">未获取到BVID，请检查你的配置</div>`
      }

      return `<iframe width="${width}" height="${height}" style="margin: 10px 0"
      scrolling="no" border="0" frameborder="no" framespacing="0"
      src="https://player.bilibili.com/player.html?bvid=${bvid}&page=1&autoplay=0" ></iframe>`
    }
    return `<pre><code class="hljs language-${language}">${code}</code></pre>`
  },
  /**
   * 拓展图片设置
   * ![照片A$$shadow$$w100]()
   * 上面格式解析为
   *  - 图片名称为 照片A
   *  - 图片包含阴影
   *  - 图片宽度为100px
   * 
   * @param href   图片路径
   * @param _title null
   * @param text   图片的名称
   */
  image(href: string | null, _title: string | null, text: string) {
    if (parseTocAndReferences) {
      articleImg.value.push({
        targetId: 0,
        targetName: text,
        targetUrl: href as string,
        type: 10
      })
    }
    let width = 'auto';
    let style = ''
    let tags: string[] = text.split('$$')
    if (tags.length > 1) {
      for (let i = 0; i < tags.length; i++) {
        let tag = tags[i]
        if (tag === 'shadow') {
          style += 'box-shadow: var(--bl-preview-img-box-shadow);'
        }
        if (tag.startsWith('w')) {
          width = tags[i].substring(1)
          if (!width.endsWith('%')) {
            width += 'px'
            style += `min-width:${width};max-width:${width};`
          }
        }
      }
    }
    return `<p>
      <img width="${width}" style="${style}" src="${href}" alt="${text}">
      </p>`
  }
};

marked.use({ renderer })

/**
 * 解析 markdown 为 html, 并将 html 赋值给 articleHtml
 */
const parse = () => {
  renderInterval.value = Date.now()
  articleIsChange = true
  isDebounce = true
  let mdContent = getEditor().state.doc.toString();
  clearTocAndImg()
  parseTocAndReferences = true
  //@ts-ignore
  marked.parse(mdContent).then((content: string) => {
    articleHtml.value = content
    renderInterval.value = Date.now() - renderInterval.value
  })
}

/**
 * 获取文章的 html 格式内容
 */
const getArticleHtml = (): string => {
  return articleHtml.value
}

/**
 * 防抖, 防止频繁渲染造成的卡顿
 */
let debounceTimeout: NodeJS.Timeout | undefined;
function debounce(fn: () => void, time = 500) {
  if (debounceTimeout != undefined) {
    clearTimeout(debounceTimeout);
  }
  if (isDebounce) {
    debounceTimeout = setTimeout(fn, time);
  } else {
    fn()
  }
}

//#endregion

//#region ----------------------------------------< TOC >------------------------------------------
interface ArticleReference { targetId: number, targetName: string, targetUrl: string, type: number }
const articleToc = ref<any[]>([])
// 文章对图片引用
const articleImg = ref<ArticleReference[]>([])
// 文章对链接的引用
const articleLink = ref<ArticleReference[]>([])

/**
 * 跳转至指定ID位置,ID为 标题级别-标题内容
 * @param level 标题级别
 * @param content 标题内容
 */
const toScroll = (level: number, content: string) => {
  let id = level + '-' + content
  let elm: HTMLElement = document.getElementById(id) as HTMLElement
  (elm.parentNode as Element).scrollTop = elm.offsetTop
  // let elm = document.getElementById(id)
  // elm?.scrollIntoView(true)
}
// 清空当前目录内容
const clearTocAndImg = () => {
  articleToc.value = []
  articleImg.value = []
  articleLink.value = []
}

//#endregion

//#region ----------------------------------------< 双屏滚动  >----------------------------------------
const marginTop = 48.66666793823242;
const matchHtmlTags = 'p, h1, h2, h3, h4, h5, h6, li, pre, blockquote, hr, table, iframe'
const scroll = (_event: Event | string, _source?: string, _lineno?: number, _colno?: number, _error?: Error): any => {
  if (EditorRef.value == undefined) {
    return;
  }
  // console.log(EditorRef.value?.scrollHeight,
  // EditorRef.value?.clientHeight,
  // EditorRef.value?.scrollTop);

  // 如果在头部附近
  if (EditorRef.value?.scrollTop < 20) {
    PreviewRef.value.firstChild.scrollIntoView()
  }
  // 如果在尾部附近
  else if (EditorRef.value?.clientHeight + EditorRef.value?.scrollTop > EditorRef.value?.scrollHeight - 20) {
    PreviewRef.value.scrollTop = PreviewRef.value.scrollHeight
  } else {
    parseTocAndReferences = false;
    // 文档头部, 距离整个浏览器的距离
    const top = getEditor().documentTop;

    // 获取可见位置最顶部的内容
    const topBlock: BlockInfo = getEditor().elementAtHeight(Math.abs(top) + marginTop)

    // 从0开始获取全部不可见的内容的 markdown 原文档
    const invisibleMarkdown: string = getEditor().state.sliceDoc(0, topBlock.from)

    // 将不可见的内容全部转换为 html
    // @ts-ignore
    marked.parse(invisibleMarkdown).then((html: string) => {
      const invisibleHtml = html
      // 将不可见的的 html 转换为 dom 对象, 是一个从 <html> 标签开始的 dom 对象
      const invisibleDomAll = new DOMParser().parseFromString(invisibleHtml, 'text/html')
      // body 下的内容才是由 markdown 转换而来的, 不可见内容转换的 dom 集合
      const editorDoms = invisibleDomAll.body.querySelectorAll(matchHtmlTags)

      // 预览页面的 dom 集合
      const previewDoms = PreviewRef.value.querySelectorAll(matchHtmlTags)
      let targetIndex = editorDoms.length;
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

const removeListenerRightMenu = () => {
  document.body.removeEventListener('click', closeEditorRightMenu)
}

const closeEditorRightMenu = () => {
  removeListenerRightMenu()
  editorRightMenu.value.show = false
}

const handleEditorClickRight = (event: Event) => {
  editorRightMenu.value = { show: false, clientX: 0, clientY: 0 }
  //@ts-ignore
  editorRightMenu.value = { show: true, clientX: event.clientX, clientY: event.clientY }
  setTimeout(() => {
    document.body.addEventListener('click', closeEditorRightMenu)
  }, 100);
}

/**
 * 右键复制功能
 */
const rightMenuCopy = () => {
  let ranges = getEditor().state.selection.ranges;
  let text = ''
  if (ranges.length > 0) {
    for (let i = 0; i < ranges.length; i++) {
      let range = ranges[i]
      if (range != undefined) {
        let rangeText = getEditor().state.sliceDoc(range.from, range.to)
        if (isBlank(rangeText)) {
          continue;
        }
        if (i != 0) {
          text += '\n';
        }
        text += rangeText;
      }
    }
  }
  //@ts-ignore
  window.electronAPI.writeText(text, 'clipboard')
}

/**
 * 右键黏贴功能
 */
const rightMenuPaste = () => {
  //@ts-ignore
  let text = window.electronAPI.readText()
  insertBlockCommand(getEditor(), text)
}

/**
 * 右键格式化表格功能
 */
const formatTable = () => {
  let ranges = getEditor().state.selection.ranges;
  if (ranges.length < 1) {
    Notify.error('未选中内容')
    return;
  }
  if (ranges.length > 1) {
    Notify.error('选中内容过多')
    return;
  }

  let text = getEditor().state.sliceDoc(ranges[0].from, ranges[0].to)
  if (isBlank(text)) {
    return
  }
  let newText = formartMarkdownTable(text)
  insertBlockCommand(getEditor(), newText)
}
//#endregion

//#region ----------------------------------------< 快捷键注册 >-------------------------------------
const shortcutRegistrant: ShortcutRegistrant = new ShortcutRegistrant().setDebug(false)
const alt_1: shortcutFunc = (): void => { docsExpand.value = !docsExpand.value; }
const alt_2: shortcutFunc = (): void => { tocsExpand.value = !tocsExpand.value; }
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
@import './ArticleIndex.scss';

:deep(.el-loading-spinner) {
  @extend .bl-loading-spinner;
}
</style>