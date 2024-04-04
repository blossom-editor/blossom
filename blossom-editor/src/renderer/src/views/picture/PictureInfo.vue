<template>
  <div class="fora-info-root">
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
        <el-form :inline="true" :model="docForm" :rules="docFormRule" label-width="60px">
          <el-form-item label="上级菜单">
            <el-tree-select
              v-model="docForm.pid"
              style="width: 432px"
              :data="docTreeData"
              node-key="i"
              :props="{ label: 'n', disabled: checkSelectTreeIsDisabled }"
              :indent="10"
              :clearable="true"
              :accordion="true"
              :show-checkbox="true"
              :check-strictly="true"
              placeholder="请选择上级菜单"
              @clear="clearPid">
            </el-tree-select>
          </el-form-item>

          <!--  -->
          <el-form-item label="名称" prop="name">
            <el-input v-model="docForm.name" style="width: 432px" placeholder="文件夹名称">
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

          <el-form-item label="排序">
            <el-input-number :min="1" v-model="docForm.sort" style="width: 176px" />
          </el-form-item>

          <el-form-item label="图标">
            <el-input v-model="docForm.icon" style="width: 176px" placeholder="图标或图片地址">
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
          <el-form-item label="图片上传目录" prop="storePath" style="margin-bottom: 0">
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
        <div>
          <el-button size="default" type="primary" :disabled="saveLoading" @click="saveDoc">
            <span class="iconbl bl-a-filechoose-line" />保存
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, inject, computed, watch, Ref } from 'vue'
import { ElInput } from 'element-plus'
import type { FormRules } from 'element-plus'
import { Document } from '@element-plus/icons-vue'
import { getCDocsByPid, provideKeyDocTree, getDocById } from '@renderer/views/doc/doc'
import { useUserStore } from '@renderer/stores/user'
import { folderInfoApi, folderAddApi, folderUpdApi } from '@renderer/api/blossom'
import { isNotBlank } from '@renderer/assets/utils/obj'
import { openExtenal, openNewIconWindow } from '@renderer/assets/utils/electron'
import Notify from '@renderer/scripts/notify'

//#region --------------------------------------------------< 基本信息 >--------------------------------------------------
const userStore = useUserStore()
// storePath 拼接服务器配置的根目录
const storePath = computed(() => {
  return userStore.userinfo.osRes.defaultPath + '/U' + userStore.userinfo.id + docForm.value.storePath
})

// 当前表单的类型, 新增(add), 修改(upd), 详情(info)
let curDocDialogType: DocDialogType
// 表单加载项
const isLoading = ref(false)
// 当前菜单, 用作上级菜单的树状下拉列表
const docTreeData = inject<Ref<DocTree[]>>(provideKeyDocTree)
// 表单
const docForm = ref<DocInfo>({
  id: '0',
  pid: '0',
  name: '',
  icon: 'wl-folder',
  tags: [],
  sort: 0,
  cover: '',
  describes: '',
  openStatus: 0,
  starStatus: 0,
  storePath: '/',
  type: 2
})
const docFormRule = ref<FormRules<DocInfo>>({
  storePath: [{ required: true, message: '上传目录为必填项', trigger: 'blur' }],
  name: [{ required: true, message: '名称为必填项', trigger: 'blur' }]
})

/**
 * 加载文档信息
 * @param dialogType 页面类型, 'add' | 'upd' | 'info'
 * @param id         文件夹或文章ID
 * @param pid        初始化父级文件夹
 */
const reload = (dialogType: DocDialogType, id?: number, pid?: string) => {
  curDocDialogType = dialogType
  docForm.value.type = 2
  // 只有修改时才查询数据, 新增时不查询
  if (dialogType == 'upd') {
    const handleResp = (resp: any) => (docForm.value = resp.data)
    const handleFinally = () => setTimeout(() => (isLoading.value = false), 100)
    isLoading.value = true
    folderInfoApi({ id: id })
      .then((resp) => handleResp(resp))
      .finally(handleFinally)
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
  return data.ty !== 2
}

/**
 * 清空上级菜单选项, 清空后的上级菜单为0
 */
const clearPid = () => {
  docForm.value.pid = ''
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

//#endregion

//#region --------------------------------------------------< 保存表单 >--------------------------------------------------
/**
 * 保存表单
 */
const saveLoading = ref<boolean>(false)
const saveDoc = () => {
  saveLoading.value = true
  // then 回调
  const handleResp = (_: any) => {
    Notify.success(curDocDialogType === 'upd' ? `修改《${docForm.value.name}》成功` : `新增《${docForm.value.name}》成功`)
    emits('saved', curDocDialogType, docForm.value)
  }
  const handleFinally = () => setTimeout(() => (saveLoading.value = false), 300)
  if (curDocDialogType == 'add')
    // 新增文件夹
    folderAddApi(docForm.value)
      .then((resp) => handleResp(resp))
      .finally(handleFinally)
  if (curDocDialogType == 'upd')
    // 修改文件夹
    folderUpdApi(docForm.value)
      .then((resp) => handleResp(resp))
      .finally(handleFinally)
}
//#endregion

//#region --------------------------------------------------<新增标签相关>--------------------------------------------------
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
  if (inputValue.value && docForm.value.tags.indexOf(inputValue.value) == -1) {
    docForm.value?.tags.push(inputValue.value)
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

.fora-info-root {
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

    .info-title-close {
      width: 50px;
      font-size: 40px;
      color: var(--el-border-color);
      text-align: center;
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
      border-radius: 5px;
      box-shadow: 0 0 10px #505050;
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
        @include themeText(2px 3px 5px rgba(107, 104, 104, 0.4), 2px 3px 5px rgba(183, 183, 183, 0.8));
        padding: 5px;
        font-size: 60px;
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
      @include themeText(2px 3px 5px rgba(107, 104, 104, 0.5), 2px 3px 5px rgba(183, 183, 183, 0.8));

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
        cursor: not-allowed;
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
    @include flex(row, space-between, center);
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
