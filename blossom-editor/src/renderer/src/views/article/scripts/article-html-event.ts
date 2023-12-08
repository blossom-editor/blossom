import { writeText } from '@renderer/assets/utils/electron'
import { Ref, nextTick, onMounted, ref } from 'vue'
import { articleInfoApi } from '@renderer/api/blossom'

type ArticleHtmlEvent = 'copyPreCode' | 'showArticleReferenceView'

const articleViewWidth = 550
const articleViewHeight = 370

export function useArticleHtmlEvent(articleViewRef: Ref<HTMLElement>) {
  const articleReferenceView = ref({
    show: false,
    html: '',
    articleId: '0',
    name: '',
    style: {
      top: '0',
      left: '0',
      width: '100px',
      height: '100px'
    }
  })

  function onHtmlEventDispatch(_t: any, _ty: any, event: any, type: ArticleHtmlEvent, data: any) {
    // console.log(type)
    // console.log(t)
    // console.log(ty)
    // console.log(e)
    /*
     复制代码块内容
     */
    if (type === 'copyPreCode') {
      let code = document.getElementById(data)
      if (code) {
        writeText(code.innerText)
      }
      return
    }

    /*
     打开文章预览
     */
    if (type === 'showArticleReferenceView') {
      event.preventDefault()
      let rect = event.target.getBoundingClientRect()
      let top = rect.top + rect.height + 10
      if (document.body.clientHeight - top < articleViewHeight) {
        top = rect.top - articleViewHeight - 10
      }
      articleReferenceView.value.style = {
        left: rect.left + 'px',
        top: top + 'px',
        width: `${articleViewWidth}px`,
        height: `${articleViewHeight}px`
      }

      articleReferenceView.value.show = true
      articleReferenceView.value.articleId = data
      articleReferenceView.value.html = `<p style="color:var(--bl-text-color-light)">正在加载文章...</p>`

      function closeView() {
        if (articleViewRef.value) {
          articleViewRef.value.removeEventListener('mouseleave', closeView)
        }
        articleReferenceView.value.show = false
      }

      nextTick(() => {
        setTimeout(() => articleViewRef.value.addEventListener('mouseleave', closeView), 100)
        articleInfoApi({ id: data, showToc: false, showMarkdown: false, showHtml: true }).then((resp) => {
          articleReferenceView.value.html = resp.data.html
          articleReferenceView.value.name = resp.data.name
        })
      })
    }
  }

  onMounted(() => {
    window.onHtmlEventDispatch = onHtmlEventDispatch
  })

  // onBeforeUnmount(() => {
  //   if (articleViewRef.value) {
  //     articleViewRef.value.removeEventListener('mouseleave', closeView)
  //   }
  //   document.body.removeEventListener('click', closeView)
  // })

  return { articleReferenceView }
}
