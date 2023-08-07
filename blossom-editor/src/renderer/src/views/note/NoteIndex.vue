<template>
  <div class="note-index-root">
    <div class="editor">
      <bl-row height="200px">
        <bl-col>
          <img src="@renderer/assets/imgs/note/note.png" class="note-logo">
        </bl-col>
        <bl-col alignItems="flex-start" class=""
          style="padding: 10px;font-size: 13px;letter-spacing:1px;color: var(--el-text-color-placeholder);">
          <ol>
            <li>便签只支持文本格式。</li>
            <li style="margin-top: 5px;">使用 Ctrl+Enter 保存便签。</li>
            <li style="margin-top: 5px;">点击便签上的磁盘图标, 可以将便签内容设置为文章功能的临时内容。</li>
            <li style="margin-top: 5px;">点击便签上的垃圾桶图标, 可将便签删除。</li>
            <li style="margin-top: 5px;">点击便签上的图钉图标, 可将便签置顶。</li>
          </ol>
        </bl-col>
      </bl-row>
      <bl-row height="calc(100% - 200px)">
        <NoteEditor :alwaysShowPlant="true" @saved="getNoteList()"></NoteEditor>
      </bl-row>
    </div>
    <div class="note-container">
      <section :class="['note', note.top == 1 ? 'note-top' : '']" v-for="note in notes" :key="note.id">
        <div class="cd" @click="saveToStorage(note.content)">
          <img src="@renderer/assets/imgs/note/cd.png">
        </div>
        <div class="del" @click="del(note.id)">
          <img src="@renderer/assets/imgs/note/dustbin.png">
        </div>
        <div class="pin" @click="top(note.id, note.top)">
          <img :class="[note.top != 1 ? 'img-hidden' : '']" src="@renderer/assets/imgs/note/pin.png">
        </div>
        <div class="note-workbench">
          {{ note.creTime }}
        </div>
        <div class="content">
          {{ note.content }}
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
//@ts-ignore
import { onActivated, onMounted, ref } from 'vue'
import { noteAllApi, noteDelApi, noteTopApi } from '@renderer/api/note'
import NoteEditor from '@renderer/views/note/NoteEditor.vue'
import { TempTextareaKey } from '@renderer/views/article/article'
import { Local } from '@renderer/assets/utils/storage'

onMounted(() => {
})

onActivated(() => {
  getNoteList()
})

const getNoteList = () => {
  noteAllApi().then(resp => {
    notes.value = resp.data
  })
}

const saveToStorage = (content: string) => {
  console.log(TempTextareaKey);
  Local.set(TempTextareaKey, content)
}

const top = (id: number, top: number) => {
  let param = { id: id, top: Math.abs(top - 1) }
  noteTopApi(param).then(_resp => {
    getNoteList()
  })
}

const del = (id: number) => {
  noteDelApi({ id: id }).then(_resp => {
    getNoteList()
  })
}

const notes = ref<any>([
])
</script>

<style scoped lang="scss">
.note-index-root {
  @include box(100%, 100%);
  @include flex(row, flex-start, flex-start);
  padding: 20px 20px 20px 30px;

  .editor {
    @include box(500px, 100%);
    z-index: 3;

    .note-logo {
      @include box(160px, 160px);
      @include themeBrightness();
    }
  }

  .note-container {
    @include box(calc(100% - 520px), 100%);
    @include flex(row, flex-start, flex-start);
    @include themeBrightness();
    align-content: flex-start;
    flex-wrap: wrap;
    margin-left: 20px;
    padding-left: 40px;
    z-index: 3;
    overflow: auto;
    overflow-y: overlay;

    .note {
      position: relative;
      @include box(250px, 354px);
      font-size: 1.3rem;
      color: #C2C2C2;
      background-image: linear-gradient(to bottom, #fff calc(1em - 1px), #efefef calc(1em - 1px), #efefef 1em, #fff 1em);
      background-position: 0% 1em;
      background-size: 100% 1em;
      background-repeat: repeat-y;
      box-shadow: 2px 3px 5px 1px rgba(0, 0, 0, 0.3);
      margin: 20px 20px;
      cursor: pointer;

      &:before {
        content: "";
        @include absolute(0, '', '', 0);
        @include box(100%, 100%);
        background-color: #FEF6DF;
        box-shadow: 3px 3px 5px 1px rgba(0, 0, 0, 0.3);
      }

      &:before {
        transform: rotate(-7deg);
        z-index: -1;
      }

      &:hover {

        .del,
        .cd,
        .pin img {
          opacity: 1;
        }
      }

      .pin {
        @include box(30px, 30px);
        @include absolute(-10px, -5px);
        cursor: pointer;

        img {
          @include box(100%, 100%);
          transition: 0.5s;
        }

        .img-hidden {
          opacity: 0;
        }
      }

      .del,
      .cd {
        @include box(28px, 28px);
        @include absolute(-10px, 20px);
        margin: 0 5px 5px 0;
        opacity: 0;
        transition: 0.5s;
        cursor: pointer;

        img {
          @include box(100%, 100%);
        }
      }

      .cd {
        @include absolute(-10px, 53px);
      }

      .note-workbench {
        @include box(100%, 42px);
        @include font(13px, 700);
        color: #D1D1D1;
        background-color: #FFFFFFDC;
        padding: 3px 5px;
      }

      .content {
        @include box(100%, calc(100% - 42px));
        max-height: calc(100% - 21px);
        font-size: 14px;
        line-height: 20.8px;
        padding: 0 5px;
        z-index: 3;
        word-wrap: break-word;
        overflow: auto;
        overflow-y: overlay;
      }
    }

    .note-top {
      background-image: linear-gradient(to bottom, #fff6d7 calc(1em - 1px), #DADADA calc(1em - 1px), #efefef 1em, #fff6d7 1em) !important;

      &:before {
        background-color: #efd8b1;
      }

      .note-workbench {
        background-color: #FCE0AF;
        color: #898989;
      }

      .content {
        color: #898989;
      }
    }


    ::-webkit-scrollbar {
      width: 0;
    }
  }
}
</style>