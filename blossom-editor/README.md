# blossom

`Blossom` MD 文档管理与编辑


## 启动安装
```bash
# Install
$ npm install

# Development
$ npm run dev

# Build
# For windows
$ npm run build:win
# For macOS
$ npm run build:mac
# For Linux
$ npm run build:linux

```

# 文档 

## 相关文档

1. vue: https://cn.vuejs.org/guide/introduction.html
2. electron-vite: https://cn-evite.netlify.app/guide/
3. element-plus: https://element-plus.gitee.io/zh-CN/component/button.html
4. iconfont: https://www.iconfont.cn/manage/index?manage_type=myprojects&projectId=1823192
5. tui-editor: https://nhn.github.io/tui.editor/latest/ToastUIEditorCore
6. electron: https://www.electronjs.org/zh/docs/latest/api/app

```java
public String flowCheck () throws InterruptedException {
    FlowRule rule = new FlowRule();
    rule.setLimitApp("default");
    // 资源名
    rule.setResource("getTest");
    // 系统预热限流方式
    rule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_WARM_UP);
    // 阈值类型,QPS,系统预热其实只能针对QPS进行判断
    rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
    // 阈值个数,系统进入稳定期时最大的放行速度,QPS
    rule.setCount(50);
    // 系统进入稳定期需要的时长,单位秒,实际并不是精准的在这个时间进入稳定期的
    rule.setWarmUpPeriodSec(60);
    try (Entry ignored = SphU.entry("getTest")) {
        System.out.println("SUCC");
    } catch (BlockException e) {
        System.out.println("> FAIL");
    }
}
```


# 文档结构

文档分为两种:
1. 文件夹 Folder
2. 文章 Article

认为文件夹也是一种文档，只不过不具有正文内容`content`.

| Tree | field            | Folder           | Article    | 说明                                           |
| ---- | ---------------- | ---------------- | ---------- | ---------------------------------------------- |
|      | type             | folder           | article    | 类型                                           |
| i    | id               | id               | id         | 唯一ID                                         |
| p    | pid              | pid              | pid        | 所属的父级内容，Article 的父级通常是个 Folder  |
| n    | name             | name             | name       | 名称                                           |
| icon | icon             | icon             | icon       | 图标                                           |
| t    | tags             | tags             | tags       | 标签集合                                       |
| s    | sort             | sort             | sort       | 排序                                           |
|      | cover            | cover            | cover      | 封面图                                         |
|      | describes        | describes        | describes  | 描述                                           |
| o    | openStatus       | openStatus       | openStatus | 公开状态 0:不公开；1:公开                      |
|      | openTime         |                  | openTime   | 发布时间                                       |
| star | starStatus       |                  | starStatus | 收藏状态 0:不收藏；1:收藏                      |
|      | pv               |                  | pv         | 页面的查看数                                   |
|      | uv               |                  | uv         | 独立的访问次数,每日IP重置                      |
|      | likes            |                  | likes      | 点赞数                                         |
|      | words            |                  | words      | 文章字数                                       |
|      | version          |                  | version    | 文章字数                                       |
|      | sp               | storePath        |            | 图片的存储路径,图片会存储在对应路径下,方便整理 |
|      | subject_words    | subject_words    |            | 专题字数                                       |
|      | subject_upd_time | subject_upd_time |            | 专题最后修改时间                               |
|      | creTime          | creTime          | creTime    | 创建时间                                       |
|      | updTime          | updTime          | updTime    | 修改时间                                       |

# 功能列表

全局 WebSocket 实现文章的保存

