import hljs from 'highlight.js'

hljs.addPlugin({
  'after:highlight': (el) => {
    // console.log(el.value);
    // let result = '<ol>'
    let lines: any[] = el.value.split(/\n|\r\n?|\n\n+/g)
    // snsArr.forEach((item: string) => {
    //   result += `<li>${item}</li>`
    // })
    // el.value = result += '</ol>'
    let result = '<ol>'
    for (let i = 0; i < lines.length; i++) {
      // let line = lines[i]
      result += `<li>${i + 1}</li>`
    }
    el.value = el.value + result + '</ol>'
  }
})

const html = hljs.highlight(
  `/**
 * 注释
 */




public void main () {
  li
  神经
}`,
  { language: 'java' }
).value

console.log(html)
