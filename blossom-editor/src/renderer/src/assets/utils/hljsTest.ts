import hljs from 'highlight.js'

hljs.addPlugin({
  'after:highlight': (el) => {
    console.log(el);
    
    let result = '<ol>'
    let snsArr: string[] = el.value.split(/[\n\r]+/)
    snsArr.forEach((item: string) => {
      result += `<li>${item}</li>`
    })
    el.value = result += '</ol>'
  }
})

const html = hljs.highlight(
  `public void main () {
  li
  神经
}`,
  { language: 'java' }
).value

console.log(html)
