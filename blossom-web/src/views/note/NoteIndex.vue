<template>
  <div class="note-root">
    <div class="notes">
      <div class="note-add" @click="showDetail('add')">
        <div class="iconbl bl-a-addline-line"></div>
      </div>
      <div :class="[note.top ? 'note-top' : '', 'note']" v-for="note in notes" @click="showDetail('upd', note)">
        <img v-if="note.top" class="pin" src="@/assets/imgs/note/pin.png" />
        <div class="time">{{ note.creTime.substring(5, 16) }}</div>
        <div class="content">
          {{ note.content }}
        </div>
      </div>
    </div>
  </div>

  <!--  -->
  <el-drawer v-model="isShowDetail" class="center-drawer" direction="btt" :with-header="true" size="330px">
    <div class="detail">
      <div class="detail-title">
        {{ detaileType === 'add' ? '添加便签' : '编辑便签' }}
      </div>
      <el-input type="textarea" placeholder="无内容" v-model="curNote.content" resize="none" :rows="10"></el-input>
      <bl-row just="space-between" class="btns">
        <div>
          <el-button text bg @click="del"><img src="@/assets/imgs/note/dustbin.png" /></el-button>
          <el-button text bg @click="top"><img src="@/assets/imgs/note/pin.png" /></el-button>
        </div>
        <el-button color="#474747" style="width: 100px" @click="save">保 存</el-button>
      </bl-row>
    </div>
  </el-drawer>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { noteAllApi, noteAddApi, noteUpdApi, noteDelApi, noteTopApi } from '@/api/note'
import { ElMessageBox } from 'element-plus'

onMounted(() => {
  getNoteAll()
})

const notes = ref()
const getNoteAll = () => {
  noteAllApi().then((resp) => {
    notes.value = resp.data
  })
}

const isShowDetail = ref(false)
const detaileType = ref<'add' | 'upd'>('add')
const curNote = ref({
  id: '',
  content: '',
  top: 0
})

const showDetail = (type: 'add' | 'upd', note?: any) => {
  isShowDetail.value = true
  detaileType.value = type
  if (type === 'add') {
    curNote.value.content = ''
  }
  if (type === 'upd') {
    curNote.value = note
  }
}

const save = () => {
  if (detaileType.value === 'add') {
    noteAddApi({ content: curNote.value.content }).then(() => {
      getNoteAll()
      isShowDetail.value = false
    })
  } else if (detaileType.value === 'upd') {
    noteUpdApi(curNote.value).then(() => {
      getNoteAll()
      isShowDetail.value = false
    })
  }
}

const del = () => {
  ElMessageBox.confirm('是否确定删除该便签', '删除任务', {
    confirmButtonText: '我要删除',
    cancelButtonText: '取消'
  }).then(() => {
    noteDelApi({ id: curNote.value.id }).then(() => {
      getNoteAll()
      isShowDetail.value = false
    })
  })
}

const top = () => {
  noteTopApi({ id: curNote.value.id, top: Math.abs(curNote.value.top - 1) }).then((_resp) => {
    getNoteAll()
    isShowDetail.value = false
  })
}
</script>

<style scoped lang="scss">
.note-root {
  @include box(100%, 100%);
  @include flex(column, flex-start, center);
  overflow: hidden;
  .header {
    @include box(100%, 60px);
  }

  .notes {
    @include flex(row, flex-start, flex-start);
    max-width: 900px;
    align-content: flex-start;
    flex-wrap: wrap;
    flex: 1;
    width: 100%;
    padding-top: 10px;
    padding-left: 10px;
    overflow-y: scroll;

    ::-webkit-scrollbar {
      width: 2px;
    }

    .note-add,
    .note {
      width: calc((100% - 70px) / 3);
      height: 135px;
      border-radius: 8px;
      margin: 0 10px 20px 10px;
      transition: box-shadow 0.3s;

      &:hover {
        box-shadow: 3px 3px 5px #e3e3e3;
      }
    }

    .note-add {
      @include flex(row, center, center);
      background-color: #f3f3f3;
      cursor: pointer;
      .iconbl {
        font-size: 35px;
        color: #e3e3e3;
      }
    }

    .note {
      background-color: #fdf5de;
      font-size: 0.8rem;
      padding: 5px 5px 10px 10px;
      position: relative;

      .pin {
        @include box(1rem, 1rem);
        position: absolute;
        top: -5px;
        right: -2px;
      }

      .content {
        @include box(100%, calc(100% - 16px));
        color: #878787;
        overflow-y: scroll;
        white-space: pre-line;
        word-wrap: break-word;
        overflow: auto;
        overflow-y: scroll;
        user-select: text;
      }
      .time {
        @include box(100%, 16px);
        line-height: 16px;
        font-size: 0.7rem;
        color: #b5b5b5;
      }
    }

    .note-top {
      background-color: #fef1c6;
    }
  }
}

.detail {
  padding-top: 20px;

  .detail-title {
    color: #878787;
    margin-bottom: 10px;
  }

  .pin {
    @include box(1rem, 1rem);
  }

  .btns {
    margin-top: 10px;
    img {
      @include box(1.3rem, 1.3rem);
    }
  }
}
</style>
