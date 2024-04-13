<template>
  <div class="article-info-root">
    <!-- 标题 -->
    <div class="info-title-wrapper">
      <div class="info-icon">
        <img
          style="height: 40px; width: 40px"
          v-if="docForm != undefined && isNotBlank(docForm.icon) && (docForm.icon!.startsWith('http') || docForm.icon!.startsWith('https'))"
          :src="docForm.icon" />
        <svg v-else-if="docForm != undefined && isNotBlank(docForm.icon)" style="height: 40px; width: 40px" aria-hidden="true">
          <use :xlink:href="'#' + docForm.icon"></use>
        </svg>
      </div>
      <div class="info-title">{{ docForm.name }}</div>
    </div>

    <div v-loading="isLoading">
      <!-- 封面 -->
      <!-- <div class="info-img">
        <div v-if="!docForm.cover" class="info-img-placeholder">
          <span>添加封面地址以查看封面</span>
          <span>封面比例为 [10:1] 时显示效果最佳, 建议 [1900 * 190]</span>
        </div>
        <img v-if="docForm.cover" :src="docForm.cover" />
      </div> -->

      <!-- 状态栏 -->
      <div class="article-stat-container" style="margin-top: 10px">
        <!-- 统计信息 -->
        <div class="stat-details">
          <div class="stat-main-left">
            <bl-tag>ID: {{ docForm.id }} {{ curIsOpen ? '(已公开)' : '' }}</bl-tag>
            <bl-tag>创建时间: {{ docForm.creTime }}</bl-tag>
            <bl-tag>修改时间: {{ docForm.updTime }}</bl-tag>
            <bl-tag>公开时间: {{ curIsOpen ? docForm.openTime : '未公开' }}</bl-tag>
          </div>
          <div class="stat-main-right">
            <bl-tag>版本: {{ docForm.version === undefined ? '无' : docForm.version }}</bl-tag>
            <bl-tag>字数: {{ docForm.words === undefined ? '无' : docForm.words }}</bl-tag>
            <bl-tag>PV: {{ docForm.pv === undefined ? '无' : docForm.pv }}</bl-tag>
            <bl-tag>UV: {{ docForm.uv === undefined ? '无' : docForm.uv }}</bl-tag>
          </div>
        </div>

        <!-- 按钮 -->
        <div class="stat-buttons">
          <bl-row just="space-around">
            <el-tooltip content="公开访问 ↑" effect="light" placement="top" :hide-after="0">
              <div :class="['iconbl bl-a-cloudupload-line', curDocDialogType == 'add' || curIsOpen ? 'disabled' : '']" @click="open(1)"></div>
            </el-tooltip>
            <el-tooltip content="取消公开 ↓" effect="light" placement="top" :hide-after="0">
              <div :class="['iconbl bl-a-clouddownload-line', curDocDialogType == 'add' || !curIsOpen ? 'disabled' : '']" @click="open(0)"></div>
            </el-tooltip>
            <el-tooltip effect="light" placement="top" :hide-after="0">
              <template #content>
                <div style="margin: 3px 0">{{ curIsFolder ? '文件夹无法同步版本' : '公开文章版本同步' }}</div>
                <div style="border-top: 2px solid #fff" v-if="diffVersion > 0">
                  版本差异: {{ diffVersion }} (本地 {{ docForm.version }} / 公开 {{ docForm.openVersion }})<br />
                  同步时间: {{ docForm.syncTime }}<br />
                  修改时间: {{ docForm.updTime }}
                </div>
              </template>
              <el-badge :value="diffVersion" :is-dot="true" :hidden="diffVersion == 0">
                <div :class="['iconbl bl-a-cloudrefresh-line', diffVersion == 0 ? 'disabled' : '']" @click="sync"></div>
              </el-badge>
            </el-tooltip>
          </bl-row>
        </div>

        <!-- 星标 -->
        <div class="stat-star">
          <div v-if="!curIsStar" :class="['iconbl bl-star-line', curDocDialogType == 'add']" @click="star(1)"></div>
          <div v-else class="iconbl bl-star-fill" @click="star(0)"></div>
        </div>
      </div>

      <div class="info-tags-container">
        <el-popover placement="top-start" :width="490" trigger="click" :show-after="0" :hide-after="0">
          <template #reference>
            <el-button><span class="iconbl bl-tally-line"></span></el-button>
          </template>
          <div class="quick-tags-container">
            <span v-if="quickTags.size === 0" class="quick-tags-placeholder">所在目录下无标签</span>
            <span
              v-else
              v-for="quickTag in quickTags.values()"
              :key="quickTag.name"
              :class="['quick-tag', quickTag.selected ? 'selected' : '']"
              @click="handleQuickTagClick(quickTag)">
              {{ quickTag.name }}
            </span>
          </div>
        </el-popover>
        <el-input
          v-if="inputVisible"
          ref="InputRef"
          style="width: 75px"
          v-model="inputValue"
          @keyup.enter="handleInputConfirm"
          @blur="handleInputConfirm" />
        <el-button v-else style="width: 75px" @click="showInput"> + 标签 </el-button>
        <el-tag v-for="tag in docForm?.tags" :key="tag" :disable-transitions="false" @close="handleTagClose(tag)" size="default" closable>
          {{ tag }}
        </el-tag>
      </div>

      <div class="info-form">
        <el-form :inline="true" :model="docForm" :rules="docFormRule" label-width="60px" ref="DocFormRef">
          <el-form-item label="上级菜单">
            <el-tree-select
              v-model="docForm.pid"
              style="width: 432px"
              node-key="i"
              :data="docTreeData"
              :props="{ label: 'n', disabled: checkSelectTreeIsDisabled }"
              :indent="10"
              :clearable="true"
              :accordion="true"
              :show-checkbox="true"
              :check-strictly="true"
              placeholder="请选择上级菜单"
              @clear="pidClear">
            </el-tree-select>
          </el-form-item>

          <!--  -->
          <el-form-item label="名称" prop="name">
            <el-input v-model="docForm.name" style="width: 432px" placeholder="文件夹或文章的名称">
              <template #prefix>
                <el-icon size="15">
                  <Document />
                </el-icon>
              </template>
              <template #append>
                <el-tooltip content="查看 Emoji" effect="light" placement="top" :hide-after="0">
                  <div style="cursor: pointer; font-size: 15px" @click="openExtenal('https://www.emojiall.com/zh-hans')">😉</div>
                </el-tooltip>
              </template>
            </el-input>
          </el-form-item>

          <!--  -->
          <el-form-item label="类型">
            <el-radio-group v-model="docForm.type" style="width: 110px" :disabled="curDocDialogType == 'upd'">
              <el-radio-button :value="1">文件夹</el-radio-button>
              <el-radio-button :value="3">文章</el-radio-button>
            </el-radio-group>
          </el-form-item>

          <!--  -->
          <el-form-item label="主色调">
            <el-input
              v-model="docForm.color"
              :style="{ width: '242px', '--el-input-text-color': '#000000', '--el-input-bg-color': docForm.color }"
              placeholder="主色调 #FFFFFF">
            </el-input>
          </el-form-item>

          <el-form-item label="排序">
            <el-input-number v-model="docForm.sort" style="width: 110px" />
          </el-form-item>
          <!--  -->
          <!-- <el-form-item label="封面">
            <el-input v-model="docForm.cover" style="width: 176px" placeholder="封面图片访问地址"> </el-input>
          </el-form-item> -->

          <el-form-item label="图标">
            <el-input v-model="docForm.icon" style="width: 242px" placeholder="图标/图片http地址">
              <template #append>
                <el-tooltip content="查看所有图标" effect="light" placement="top" :hide-after="0">
                  <div style="cursor: pointer; font-size: 20px" @click="openNewIconWindow()">
                    <svg class="icon" aria-hidden="true">
                      <use xlink:href="#wl-yanfa"></use>
                    </svg>
                  </div>
                </el-tooltip>
              </template>
            </el-input>
          </el-form-item>

          <!--  -->
          <el-form-item label="描述">
            <el-input
              type="textarea"
              v-model="docForm.describes"
              style="width: 432px"
              :autosize="{ minRows: 3, maxRows: 3 }"
              resize="none"
              placeholder="描述下文件夹或文章吧"></el-input>
          </el-form-item>

          <!--  -->
          <el-form-item label="图片保存路径" prop="storePath" v-if="docForm.type != 3" style="width: 100%">
            <el-input v-model="docForm.storePath" style="width: 432px" placeholder="图片的保存路径, 需在头尾增加 / " @change="formatStorePath">
              <template #append>
                <div style="cursor: pointer" @click="fillStorePath(docForm.pid)">填充路径</div>
              </template>
            </el-input>
            <bl-row width="100%" style="font-size: 12px; overflow-x: scroll; flex-wrap: wrap">
              <span>图片将保存至: </span><span class="bl-tag">{{ storePath }}</span>
              <blockquote v-if="showStorePathWarning" class="blockquote-yellow">
                若路径中包含 Emoji、特殊字符、中文或空格时，建议您在保存后上传图片进行测试，以确保路径有效。
              </blockquote>
            </bl-row>
          </el-form-item>
        </el-form>
      </div>

      <div class="info-footer">
        <el-button size="default" type="primary" :disabled="saveLoading" @click="saveDoc(DocFormRef)">
          <span class="iconbl bl-a-filechoose-line" /> 保存
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, inject, computed, watch, Ref } from 'vue'
import { ElInput, ElMessageBox, FormInstance } from 'element-plus'
import type { FormRules } from 'element-plus'
import { Document } from '@element-plus/icons-vue'
import { provideKeyDocTree, getCDocsByPid, getDocById } from '@renderer/views/doc/doc'
import { useUserStore } from '@renderer/stores/user'
import {
  folderStarApi,
  folderInfoApi,
  folderAddApi,
  folderUpdApi,
  folderOpenApi,
  articleInfoApi,
  articleAddApi,
  articleUpdApi,
  articleOpenApi,
  articleSyncApi,
  articleStarApi
} from '@renderer/api/blossom'
import { isNotBlank, isNull } from '@renderer/assets/utils/obj'
import { openExtenal, openNewIconWindow } from '@renderer/assets/utils/electron'
import Notify from '@renderer/scripts/notify'

//#region --------------------------------------------------< 基本信息 >--------------------------------------------------
const userStore = useUserStore()
// storePath 拼接服务器配置的根目录
const storePath = computed(() => {
  return userStore.userinfo.osRes.defaultPath + '/U' + userStore.userinfo.id + docForm.value.storePath
})

// 当前表单的类型, 新增(add), 修改(upd), 详情(info)
const curDocDialogType = ref<DocDialogType>()
// 表单加载项
const isLoading = ref(false)
// 当前菜单, 用作上级菜单的树状下拉列表
const docTreeData = inject<Ref<DocTree[]>>(provideKeyDocTree)
const DocFormRef = ref<FormInstance>()
// 表单
const docForm = ref<DocInfo>({
  id: '0',
  pid: '0',
  name: '',
  icon: '',
  tags: [],
  sort: 1,
  cover: '',
  describes: '',
  openStatus: 0,
  starStatus: 0,
  storePath: '/',
  type: 3
})
const docFormRule = ref<FormRules<DocInfo>>({
  storePath: [{ required: true, message: '上传目录为必填项', trigger: 'blur' }],
  name: [
    {
      required: true,
      trigger: 'blur',
      validator: (_rule: any, value: any, callback: any) => {
        if (value === '') {
          callback(new Error('文档名称为必填项'))
        }
        const reg = /^\.|[\\\\/:*?\"<>|]/gim
        if (reg.test(value)) {
          callback(new Error(`文档名不得包含以下字符 . * " : \\ / ? < > |`))
        } else {
          callback()
        }
      }
    }
  ]
})

watch(
  () => docForm.value.type,
  (val) => {
    if (val === 1 && docForm.value.icon === '') {
      docForm.value.icon = 'wl-folder'
    }
    if (val === 3 && docForm.value.icon === 'wl-folder') {
      docForm.value.icon = ''
    }
  }
)

/**
 * 计算本地与公开文章的版本差异
 */
const diffVersion = computed<number>(() => {
  if (docForm.value.openStatus == 0) return 0
  if (docForm.value.version == undefined || docForm.value.openVersion == undefined) return 0
  return docForm.value.version - docForm.value.openVersion
})

/**
 * 加载文档信息
 * @param dialogType 页面类型, 'add' | 'upd' | 'info'
 * @param id         文件夹或文章ID
 * @param docType    文档类型: 1:文件夹; 2:文档;
 * @param pid        初始化父级文件夹
 */
const reload = (dialogType: DocDialogType, id?: number, docType?: DocType, pid?: string) => {
  curDocDialogType.value = dialogType
  docForm.value.type = docType == undefined ? 3 : docType
  // 只有修改时才查询数据, 新增时不查询
  if (dialogType == 'upd') {
    const handleResp = (resp: any) => {
      docForm.value = resp.data
    }
    const handleFinally = () => {
      setTimeout(() => {
        isLoading.value = false
      }, 100)
    }
    isLoading.value = true
    if (docType == 1) {
      folderInfoApi({ id: id })
        .then((resp) => handleResp(resp))
        .finally(handleFinally)
    }
    if (docType == 3) {
      articleInfoApi({ id: id, showToc: false, showMarkdown: false, showHtml: false })
        .then((resp) => handleResp(resp))
        .finally(handleFinally)
    }
  }

  if (dialogType == 'add') {
    if (pid != undefined) {
      docForm.value.pid = pid
    }
  }
}

/**
 * 检查树状下拉列表中的选项是否被禁用, 文章将被禁用, 即上级菜单不能是文章, 只能是文件夹
 * @param data
 */
const checkSelectTreeIsDisabled = (data: any) => {
  return data.ty === 3
}

/**
 * 清空上级菜单选项, 清空后的上级菜单为0
 */
const pidClear = () => {
  docForm.value.pid = '0'
}

/**
 * 存储地址会影响图片保存路径及文章的备份地址, 为保证正确需要进行较为严格的格式校验
 */
const formatStorePath = () => {
  if (docForm.value.storePath == undefined || docForm.value.storePath?.length == 0) {
    docForm.value.storePath = '/'
  }
  let path: string = docForm.value.storePath
  if (!path.startsWith('/')) {
    path = '/' + path
  }
  if (!path.endsWith('/')) {
    path = path + '/'
  }
  while (path.includes('//')) {
    path = path.replaceAll('//', '/')
  }
  docForm.value.storePath = path
}

const showStorePathWarning = ref(false)

/**
 * 填充文件夹路径
 */
const fillStorePath = (id: string, path: string = ''): void => {
  let doc = getDocById(id, docTreeData!.value)
  if (!doc) {
    return
  }
  path = doc.n + '/' + path
  if (doc.p !== '0') {
    fillStorePath(doc.p, path)
  } else {
    let docName = ''
    if (isNotBlank(docForm.value.name)) {
      docName = docForm.value.name + '/'
    }
    docForm.value.storePath = '/' + path + docName
    showStorePathWarning.value = true
  }
}

/**
 * 当前文档是否是文件夹类型(folder/1)
 */
const curIsFolder = computed<boolean>(() => {
  if (isNull(docForm)) {
    return false
  }
  if (isNull(docForm.value)) {
    return false
  }
  if (isNull(docForm.value?.type) || docForm.value?.type != 1) {
    return false
  }
  return true
})

/**
 * 当前文档是否是文章类型(article/2)
 */
const curIsArticle = computed<boolean>(() => {
  if (isNull(docForm)) {
    return false
  }
  if (isNull(docForm.value)) {
    return false
  }
  if (isNull(docForm.value?.type) || docForm.value?.type != 3) {
    return false
  }
  return true
})

/**
 * 是否star: true:star(1); false:No Star(0)
 */
const curIsStar = computed<boolean>(() => {
  if (isNull(docForm)) {
    return false
  }
  if (isNull(docForm.value)) {
    return false
  }
  if (isNull(docForm.value?.starStatus) || docForm.value?.starStatus === 0) {
    return false
  }
  return true
})

/**
 * 是否公开: true:公开(1); false:未公开(0)
 */
const curIsOpen = computed<boolean>(() => {
  if (isNull(docForm)) {
    return false
  }
  if (isNull(docForm.value)) {
    return false
  }
  if (isNull(docForm.value?.openStatus) || docForm.value?.openStatus === 0) {
    return false
  }
  return true
})

//#endregion

//#region --------------------------------------------------< star >--------------------------------------------------

/**
 * star 文章
 * @param changeStarStatus
 */
const star = (changeStarStatus: number) => {
  if (curIsArticle.value) {
    docForm.value.starStatus = changeStarStatus
    articleStarApi({ id: docForm.value.id, starStatus: docForm.value.starStatus }).then(() => {
      Notify.success(docForm.value.starStatus === 0 ? '取消 Star 成功' : 'Star 成功')
    })
  }
  if (curIsFolder.value) {
    docForm.value.starStatus = changeStarStatus
    folderStarApi({ id: docForm.value.id, starStatus: docForm.value.starStatus }).then(() => {
      Notify.success(docForm.value.starStatus === 0 ? '取消 Star 成功' : 'Star 成功')
    })
  }
}

//#endregion

//#region --------------------------------------------------< 公开 >--------------------------------------------------
/**
 * 公开文档
 * @param changeStarStatus
 */
const open = (changeOpenStatus: number) => {
  if (curIsOpen.value && changeOpenStatus == 1) return
  if (!curIsOpen.value && changeOpenStatus == 0) return
  ElMessageBox.confirm(
    changeOpenStatus === 0 ? '取消后文章的PV,UV数据不会删除, 是否确认取消公开访问?' : '是否确认允许公开访问?',
    changeOpenStatus === 0 ? '取消公开' : '文档公开',
    { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning' }
  ).then(() => {
    if (curIsArticle.value) {
      articleOpenApi({ id: docForm.value.id, openStatus: changeOpenStatus }).then((_) => {
        docForm.value.openStatus = changeOpenStatus
        Notify.success(docForm.value.openStatus === 0 ? '取消公开成功' : '公开成功')
      })
    } else if (curIsFolder.value) {
      folderOpenApi({ id: docForm.value.id, openStatus: changeOpenStatus }).then((_) => {
        docForm.value.openStatus = changeOpenStatus
        Notify.success(docForm.value.openStatus === 0 ? '取消公开成功' : '公开成功')
      })
    }
  })
}

const sync = () => {
  if (!curIsOpen.value || curIsFolder.value) return
  articleSyncApi({ id: docForm.value.id }).then((resp) => {
    docForm.value.openVersion = resp.data.openVersion
    docForm.value.syncTime = resp.data.syncTime
    Notify.success('同步成功')
  })
}
//#endregion

//#region --------------------------------------------------< 保存表单 >--------------------------------------------------
/**
 * 保存表单
 */
const saveLoading = ref<boolean>(false)
const saveDoc = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid, _fields) => {
    if (valid) {
      saveLoading.value = true
      const handleResp = (_: any) => {
        Notify.success(curDocDialogType.value === 'upd' ? `修改《${docForm.value.name}》成功` : `新增《${docForm.value.name}》成功`)
        emits('saved', curDocDialogType.value, docForm.value)
      }
      const handleFinally = () => setTimeout(() => (saveLoading.value = false), 300)
      // 新增文件夹
      if (docForm.value.type == 1) {
        if (curDocDialogType.value == 'add')
          folderAddApi(docForm.value)
            .then((resp) => handleResp(resp))
            .finally(handleFinally)
        if (curDocDialogType.value == 'upd')
          // 修改文件夹
          folderUpdApi(docForm.value)
            .then((resp) => handleResp(resp))
            .finally(handleFinally)
      }
      if (docForm.value.type == 3) {
        if (curDocDialogType.value == 'add')
          // 新增文章
          articleAddApi(docForm.value)
            .then((resp) => handleResp(resp))
            .finally(handleFinally)
        if (curDocDialogType.value == 'upd')
          // 修改文章
          articleUpdApi(docForm.value)
            .then((resp) => handleResp(resp))
            .finally(handleFinally)
      }
    }
  })
}
//#endregion

//#region --------------------------------------------------< 标签 >--------------------------------------------------
const inputValue = ref('')
const inputVisible = ref(false)
const InputRef = ref<InstanceType<typeof ElInput>>()
const quickTags = ref<Map<string, QuickTag>>(new Map())

watch(
  () => docForm.value?.pid,
  (newVal: string) => {
    if (newVal === undefined) {
      docForm.value.pid = '0'
    }
    if (newVal != undefined) {
      initQuickTags(newVal)
    }
  }
)

const initQuickTags = (pid: string) => {
  let docs = getCDocsByPid(pid, docTreeData!.value)
  let tags = new Set()
  for (let i = 0; i < docs.length; i++) {
    const doc = docs[i]
    for (let j = 0; j < doc.tags.length; j++) {
      const tag: string = doc.tags[j]
      if (!tags.has(tag)) {
        tags.add(tag)
        quickTags.value.set(tag, { name: tag, selected: docForm.value.tags.includes(tag) })
      }
    }
  }
}

const handleQuickTagClick = (tag: QuickTag) => {
  if (tag.selected) {
    docForm.value?.tags.splice(docForm.value.tags.indexOf(tag.name), 1)
    quickTags.value.set(tag.name, { name: tag.name, selected: false })
  } else {
    docForm.value.tags.push(tag.name)
    quickTags.value.set(tag.name, { name: tag.name, selected: true })
  }
}

const handleTagClose = (tag: string) => {
  docForm.value?.tags.splice(docForm.value.tags.indexOf(tag), 1)
  quickTags.value.set(tag, { name: tag, selected: false })
}

const showInput = () => {
  inputVisible.value = true
  nextTick(() => {
    InputRef.value!.input!.focus()
  })
}

const handleInputConfirm = () => {
  if (inputValue.value && inputValue.value.toLocaleLowerCase() === 'subject') {
    inputValue.value = 'subject'
  }
  if (inputValue.value && inputValue.value.toLocaleLowerCase() === 'toc') {
    inputValue.value = 'toc'
  }
  // 有便签且未在标签池中
  if (inputValue.value && docForm.value.tags.indexOf(inputValue.value) == -1) {
    docForm.value.tags.push(inputValue.value)
    quickTags.value.set(inputValue.value, { name: inputValue.value, selected: true })
  }
  inputVisible.value = false
  inputValue.value = ''
}
//#endregion

defineExpose({
  reload
})
const emits = defineEmits(['saved'])
</script>

<style scoped lang="scss">
$height-title: 50px;
$height-img: 70px;
$height-stat: 70px;
$height-tags: 83px;
$height-footer: 50px;
$height-form: calc(100% - #{$height-title} - #{$height-img} - #{$height-stat} - #{$height-tags} - #{$height-footer});

.article-info-root {
  border-radius: 10px;

  .info-title-wrapper {
    @include box(100%, $height-title);
    @include flex(row, flex-start, center);
    border-bottom: 1px solid var(--el-border-color);

    .info-icon {
      @include box(50px, 100%);
      text-align: center;
    }

    .info-title {
      @include font(16px);
      width: calc(100% - 50px - 50px);
      height: 100%;
      padding-top: 10px;
      color: var(--bl-text-title-color);
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
      padding-left: 10px;
    }
  }

  .info-img {
    @include box(100%, $height-img);
    padding: 10px;

    .info-img-placeholder {
      @include box(515px, 51px);
      @include flex(column, center, center);
      @include font(12px);
      @include themeColor(#cacaca, #494949);
      @include themeBorder(1px, #b0b0b0, #494949, 'around', 5px, dashed);
    }

    img {
      @include box(515px, 51px);
      @include themeShadow(0 0 10px #505050, 0 0 10px #000000);
      border-radius: 5px;
    }
  }

  .article-stat-container {
    @include box(100%, $height-stat);
    @include flex(row, flex-start, center);

    .stat-details {
      @include box(calc(100% - 80px - 150px), 100%);
      @include flex(row, space-around, center);
      font-size: 12px;

      .tag-root {
        margin: 2px 3px;
      }

      .stat-main-left {
        @include box(65%, 100%);
      }

      .stat-main-right {
        @include box(35%, 100%);
      }

      & > div {
        padding-left: 10px;
        @include flex(column, space-around, flex-start);
      }
    }

    .stat-star {
      @include box(80px, 100%);
      color: rgb(206, 175, 0);

      .iconbl {
        padding: 5px;
        font-size: 60px;
        text-shadow: var(--bl-text-shadow);
        transition: 0.3s;
        cursor: pointer;

        &:hover {
          color: rgb(237, 204, 11);
        }
      }
    }

    .stat-buttons {
      @include box(150px, 100%);
      padding: 5px;
      color: var(--el-color-primary-light-5);
      text-shadow: var(--bl-text-shadow);

      .iconbl {
        cursor: pointer;
        transition: 0.3s;
        font-size: 30px;

        &:hover {
          color: var(--el-color-primary);
        }
      }
    }

    .stat-star,
    .stat-buttons {
      .disabled {
        pointer-events: none;
        color: var(--el-color-info-light-5);

        &:hover {
          color: var(--el-color-info-light-5);
        }
      }
    }
  }

  .info-tags-container {
    @include box(calc(100% - 20px), $height-tags);
    @include themeShadow(inset 0 0 4px 1px rgb(222, 222, 222), inset 0 0 4px 1px rgb(0, 0, 0));
    padding: 10px;
    margin: 10px;
    border-radius: 10px;
    text-align: left;
    overflow-y: scroll;

    .info-tags-quick-choise {
      display: inline-block;
      border: 1px solid var(--el-border-color);
      padding: 3px 11px;
      margin: 3px 3px;
      font-size: 12px;
    }

    & > span,
    button {
      margin: 3px 3px;
    }

    .el-input {
      margin: 3px 3px;
    }
  }

  .info-form {
    @include box(100%, $height-form);
    padding: 10px;

    :deep(.el-form--inline .el-form-item) {
      margin-right: 20px;
    }
  }

  .info-footer {
    @include box(100%, $height-footer);
    @include flex(row, flex-end, center);
    border-top: 1px solid var(--el-border-color);
    padding: 10px;
    text-align: right;

    .iconbl {
      font-size: 18px;
      margin-right: 5px;
    }
  }

  .emoji-link {
    font-size: 25px;
    margin-left: 5px;
    cursor: pointer;
  }
}
</style>
