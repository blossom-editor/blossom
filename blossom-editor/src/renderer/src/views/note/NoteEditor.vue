<template>
  <div class="note-editor-root">
    <div v-if="props.send" :class="['send', sendStart ? 'send-start' : '']" @click="saveNote">
      <img src="@renderer/assets/imgs/note/plane.png" class="plant" />
    </div>
    <section class="blokken">
      <div class="textarea-placeholder">日期:{{ getDateZh() }}</div>
      <textarea @keyup.ctrl.enter="saveNote" v-model="noteContent"></textarea>
      <img
        :class="['note-img', props.alwaysShowPlant ? 'note-img-always' : '']"
        style="z-index: 40; right: 15px; bottom: 10px; width: 90px; height: 90px"
        src="@renderer/assets/imgs/plant/08.png" />
      <img :class="['note-img', props.alwaysShowPlant ? 'note-img-always' : '']" src="@renderer/assets/imgs/plant/02.png" />
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { noteAddApi } from '@renderer/api/note'
import { getDateZh } from '@renderer/assets/utils/util'
import { isBlank } from '@renderer/assets/utils/obj'
import Notify from '@renderer/scripts/notify'

const props = defineProps({
  send: {
    type: Boolean,
    default: true
  },
  alwaysShowPlant: {
    type: Boolean,
    default: false
  }
})
const noteContent = ref('')

const saveNote = () => {
  if (isBlank(noteContent.value)) {
    Notify.info('不能保存空的便签')
    return
  }
  noteAddApi({ content: noteContent.value }).then((_resp) => {
    emits('saved')
    noteContent.value = ''
  })
  if (props.send) {
    sendAnimation()
  }
}

const sendStart = ref<boolean>(false)
const sendAnimation = () => {
  sendStart.value = true
  setTimeout(() => {
    sendStart.value = false
  }, 1000)
}

const emits = defineEmits(['saved'])
</script>

<style scoped lang="scss">
.note-editor-root {
  @include box(100%, 100%);
  @include themeBrightness(100%, 80%);
  padding: 5px;
  position: relative;

  .send {
    @include absolute(10px, '', '', 15px);
    transition: 0.5s;
    z-index: 9999;
    cursor: pointer;

    .plant {
      transform: rotate(-105deg);
      width: 35px;
      z-index: 9999;
    }
  }

  .send-start {
    // 名称|运行时间|运动曲线|运动次数
    animation: send 1s ease-out 1;
  }

  @keyframes send {
    100% {
      left: -1029px;
      top: -490px;
    }
  }

  @media screen and (max-height: 1050px) {
    .blokken {
      .note-img {
        opacity: 0;
      }
    }
  }

  .blokken {
    position: relative;
    background-color: #fff;
    height: 100%;
    font-size: 1.3rem;
    background-image: linear-gradient(to bottom, #fff calc(1em - 1px), #efefef calc(1em - 1px), #efefef 1em, #fff 1em);
    background-position: 0% 1em;
    background-size: 100% 1em;
    background-repeat: repeat-y;
    box-shadow: 2px 3px 5px 1px rgba(0, 0, 0, 0.3);

    .textarea-placeholder {
      @include box(100%, 42px);
      @include absolute(0, 0);
      @include font(14px, 100);
      color: #c2c2c2;
      background-color: #ffffffdc;
      text-align: right;
      padding: 0 10px;
    }

    textarea {
      @include absolute(42px, 0);
      @include box(100%, calc(100% - 42px));
      padding: 0 10px;
      font-size: 14px;
      line-height: 20.8px;
      color: #848484;
      border: 0;
      background-color: #ffffff00;
      transition: 0.5s;
      resize: none;
      outline: none;
    }

    .note-img {
      @include box(200px, 200px);
      @include absolute('', -50px, 0, '');
      transition: 0.3s;
    }

    .note-img-always {
      opacity: 1 !important;
    }

    &:before,
    &:after {
      content: '';
      @include absolute(0, '', '', 0);
      @include box(100%, 100%);
      background-color: #fff;
      box-shadow: 3px 3px 5px 1px rgba(0, 0, 0, 0.3);
    }

    &:before {
      transform: rotate(-2deg);
      z-index: -1;
    }

    &:after {
      transform: rotate(2deg);
      z-index: -2;
    }
  }
}
</style>
