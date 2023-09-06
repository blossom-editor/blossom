const blossom: any = {
  /**
   * 基础配置
   */
  SYS: {
    NAME: 'Blossom',
    SHORT_NAME: 'BLOSSOM-WEB',
    // 版本
    VERSION: 'v1.3.0',
    // 公网安备号
    GONG_WANG_AN_BEI: "X公网安备 XXXXXXXXXX号",
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
    PRD: 'https://www.wangyunf.com/bl/',
    // 填写你开放为博客的用户ID
    USER_ID: 1
  },
  /**
   * 可以填写你自己的网站
   * NAME: 网站名称
   * URL: 网站地址
   * LOGO: 网站LOGO, 放在 src/assets/imgs/linklogo/ 路径下
   */
  LINKS: [
    {
      NAME: 'Blossom 文档',
      URL: 'https://www.wangyunf.com/blossom-doc/index',
      LOGO: 'blossom_logo.png'
    }
  ]
}

export default blossom
