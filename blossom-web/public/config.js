window.blconfig = {
  /**
   * 基础配置
   */
  SYS: {
    // 修改该值可以改变网页左上角名称, 你可以改为你的名称
    NAME: 'Blossom',
    // 公网安备号
    GONG_WANG_AN_BEI: '',
    // ICP 备案号
    ICP_BEI_AN_HAO: '',
    // 邮箱
    EMAIL: ''
  },
  /**
   * 博客样式，当前可设置样式如下：
   * 1. 左上角 LOGO 样式
   * 2. 专题文件夹的特殊样式显示
   */
  THEME: {
    LOGO_STYLE: {
      // 左上角 LOGO 的圆角设置
      'border-radius': '50%'
    },
    // 是否以特殊样式显示专题文件夹
    SUBJECT_TITLE: true
  },
  /**
   * 服务器的地址
   */
  DOMAIN: {
    // 将该值填写为你的后台访问地址, 与 blossom 客户端登录   页面填写的地址相同
    PRD: 'http://192.168.31.6:9999',
    // 将该值填写你开放为博客的用户ID
    USER_ID: 1
  },
  /**
   * 可以填写你自己的网站，该信息会展示在右上角的【更多】按钮中，以及首页的【所有文章】下
   * NAME: 网站名称
   * URL: 网站地址
   * LOGO: 网站LOGO地址
   */
  LINKS: [
    // 下方是一个示例
    // {
    //   NAME: 'Blossom 双链笔记软件',
    //   URL: 'https://www.wangyunf.com/blossom-doc/index.html',
    //   LOGO: 'https://www.wangyunf.com/bl/pic/home/bl/img/U1/head/blossom_logo.png'
    // }
  ]
}
