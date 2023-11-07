const blossom: any = {
  /**
   * 基础配置
   */
  SYS: {
    // 修改该值可以改变网页左上角名称, 你可以改为你的名称
    NAME: 'Blossom',
    SHORT_NAME: 'BLOSSOM-WEB',
    // 版本
    VERSION: 'v1.8.0',
    // 公网安备号
    GONG_WANG_AN_BEI: 'X公网安备 XXXXXXXXXX号',
    // ICP 备案号
    ICP_BEI_AN_HAO: '京ICP备123123123号',
    // 邮箱
    EMAIL: '491548320@qq.com'
  },
  /**
   * 填写服务器的地址
   */
  DOMAIN: {
    LOC: 'http://127.0.0.1:9999/',
    // 将该值填写为你的后台访问地址, 与 blossom 客户端登录页面填写的地址相同
    PRD: 'https://www.wangyunf.com/bl/',
    // 将该值填写你开放为博客的用户ID
    USER_ID: 1
  },
  /**
   * 可以填写你自己的网站，该信息回展示顶部的【更多】按钮中，以及首页的【我的所有文章】下
   * NAME: 网站名称
   * URL: 网站地址
   * LOGO: 网站LOGO, 放在 src/assets/imgs/linklogo/ 路径下
   */
  LINKS: [
    // 下方是一个示例
    // {
    //   NAME: '我的个人主页',
    //   URL: 'https://www.wangyunf.com',
    //   // 请将 logo 放入到 src/assets/imgs/linklogo/
    //   LOGO: 'luban.png'
    // }
  ]
}

export default blossom
