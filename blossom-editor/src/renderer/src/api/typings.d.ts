declare namespace BlossomAPI {
  type AccessTokenResponse = {
    /** 登录令牌 */
    token?: string;
    /** 到期时间 */
    expire?: number;
    /** 授权方式 */
    grantType?: string;
    /** 登录平台 */
    clientId?: string;
    /** 请求是否刷新Token */
    requestRefresh?: boolean;
    /** 用户 Token 唯一:
<p>为 false  则每次登录返回的 token 是一样的;
<p>为 true   则每次登录 token 会替换前一个 token */
    multiPlaceLogin?: boolean;
    /** 授权时长, 单位为秒 */
    duration?: number;
    /** 用户ID */
    userId?: number;
    /** 登录日期 */
    loginTime?: string;
    /** 权限列表 (类: String) */
    permissions?: string[];
    /** 用户元信息, 由用户自定义各类信息(map data) */
    metadata?: Record<string, any>;
  };

  type ArticleAddReqRequest = {
    /** 文章所在的文件夹ID */
    pid: number;
    /** 文章名称 */
    name: string;
    /** 文章图标 */
    icon?: string;
    /** 标签集合 (类: String) */
    tags?: string[];
    /** 排序 */
    sort?: number;
    /** 封面 */
    cover?: string;
    /** 描述 */
    describes?: string;
    /** 颜色 */
    color?: string;
    /** 是否新增到尾部, 将忽略传入的 sort, 使用最大 sort */
    addToLast?: boolean;
  };

  type articleBackupDownloadFragmentGETParams = {
    /** 文件名 */
    filename: number;
  };

  type articleBackupDownloadGETParams = {
    /** 文件名称 */
    filename: number;
  };

  type articleBackupGETParams = {
    /**      导出类型 MARKDOWN / HTML, 默认为 MARKDOWN {@link BackupTypeEnum} */
    type: number;
    /**   是否导出为本地图片映射 YES / NO, 默认为 NO {@link YesNo} */
    toLocal: number;
    /** 备份指定的文章 */
    articleId?: number;
  };

  type ArticleBackupServiceBackupFileResponse = {
    /** 用户ID */
    userId?: string;
    /** 备份日期 YYYYMMDD */
    date?: string;
    /** 备份时间 HHMMSS */
    time?: string;
    /** 备份的日期和时间, yyyy-MM-dd HH:mm:ss */
    datetime?: string;
    /** 备份包的名称 */
    filename?: string;
    /** 备份包路径 */
    path?: string;
    /** 文件大小 */
    fileLength?: number;
  };

  type articleDownloadGETParams = {
    /**       文章ID */
    id: number;
  };

  type articleDownloadHtmlGETParams = {
    /**       文章ID */
    id: number;
  };

  type ArticleEntityResponse = {
    /** ID */
    id?: number;
    /** 文件夹ID */
    pid?: number;
    /** 文章名称 */
    name?: string;
    /** 文章图标 */
    icon?: string;
    /** 标签集合 */
    tags?: string;
    /** 排序 */
    sort?: number;
    /** 封面 */
    cover?: string;
    /** 描述 */
    describes?: string;
    /** star状态 */
    starStatus?: number;
    /** 公开状态 */
    openStatus?: number;
    /** 公开版本 */
    openVersion?: number;
    /** 页面的查看数 */
    pv?: number;
    /** 独立的访问次数,每日IP重置 */
    uv?: number;
    /** 点赞数 */
    likes?: number;
    /** 文章字数 */
    words?: number;
    /** 文章字数 */
    version?: number;
    /** 颜色 */
    color?: string;
    /** 目录 */
    toc?: string;
    /** Markdown 内容 */
    markdown?: string;
    /** Html 内容 */
    html?: string;
    /** 版本 */
    creTime?: string;
    /** 修改时间 */
    updTime?: string;
    /** 用户ID */
    userId?: number;
    /** 引用集合 (类: ArticleReferenceReq) */
    references?: ArticleReferenceReqResponse[];
    /** 父ID集合 (类: Long) */
    pids?: string[];
  };

  type ArticleHeatmapResResponse = {
    /** [
[日期,值],
[日期,值]
] (类: Object) */
    data?: ObjectResponse[];
    /** 最大修改数据 */
    maxStatValues?: number;
    /** 开始时间 */
    dateBegin?: string;
    /** 结束时间 */
    dateEnd?: string;
  };

  type articleImportPOSTParams = {
    /** 文件 */
    file: string;
    /**  上级菜单 */
    pid: number;
  };

  type articleInfoGETParams = {
    /**           文章ID */
    id: number;
    /**      是否返回目录 */
    showToc: number;
    /** 是否返回 markdown 内容 */
    showMarkdown: number;
    /**     是否返回 html 内容 */
    showHtml: number;
  };

  type ArticleInfoResResponse = {
    /** ID */
    id?: number;
    /** 文件夹ID */
    pid?: number;
    /** 文章名称 */
    name?: string;
    /** 文章图标 */
    icon?: string;
    /** 标签集合 (类: String) */
    tags?: string[];
    /** 排序 */
    sort?: number;
    /** 封面 */
    cover?: string;
    /** 描述 */
    describes?: string;
    /** star状态 */
    starStatus?: number;
    /** 页面的查看数 */
    pv?: number;
    /** 独立的访问次数,每日IP重置 */
    uv?: number;
    /** 点赞数 */
    likes?: number;
    /** 文章字数 */
    words?: number;
    /** 版本 */
    version?: number;
    /** Markdown 内容 */
    markdown?: string;
    /** Html 内容 */
    html?: string;
    /** 版本 */
    creTime?: string;
    /** 修改时间 */
    updTime?: string;
    /** 颜色 */
    color?: string;
    /** 目录 */
    toc?: string;
    /** 类型 */
    type?: number;
    /** 公开状态 */
    openStatus?: number;
    /** 公开同步时间 */
    syncTime?: string;
    /** 公开时间 */
    openTime?: string;
    /** 公开版本 */
    openVersion?: number;
  };

  type ArticleInfoSimpleResResponse = {
    /** ID */
    id?: number;
    /** 文件夹ID */
    pid?: number;
    /** 文章名称 */
    name?: string;
    /** 文章图标 */
    icon?: string;
    /** 标签集合 (类: String) */
    tags?: string[];
    /** 封面 */
    cover?: string;
    /** 描述 */
    describes?: string;
    /** star状态 */
    starStatus?: number;
    /** 公开状态 */
    openStatus?: number;
    /** 页面的查看数 */
    pv?: number;
    /** 独立的访问次数,每日IP重置 */
    uv?: number;
    /** 点赞数 */
    likes?: number;
    /** 文章字数 */
    words?: number;
    /** 版本 */
    version?: number;
    /** 颜色 */
    color?: string;
  };

  type ArticleLineResResponse = {
    /** 日期集合 (类: String) */
    statDates?: string[];
    /** 对应日期的统计值 (类: Integer) */
    statValues?: string[];
  };

  type articleListGETParams = {
    /** [分页参数] 页码, 0与1都表示第一页, 超过最大页时只显示最后一页 */
    pageNum?: number;
    /** [分页参数] 每页结果数, 不传则为10, 最大为200, 超过200会自动替换为200 */
    pageSize?: number;
    /** [分页参数] 排序字段, 需将驼峰类型字段改为下换线分隔字段,如 [userId > user_id] */
    sortField?: number;
    /** [分页参数] 排序方式 asc(升序) 或 desc(降序) */
    order?: number;
    /** 文章ID */
    id?: number;
    /** 上级ID集合 (类: Long) */
    pids?: Record<string, any>;
    /** star状态 {@link YesNo} */
    starStatus?: number;
    /** 公开状态  {@link YesNo} */
    openStatus?: number;
    /** 用户ID */
    userId?: number;
  };

  type articleLogContentGETParams = {
    /** 记录ID */
    id: number;
  };

  type articleLogGETParams = {
    /** 文章ID */
    articleId: number;
  };

  type ArticleLogResResponse = {
    /** ID */
    i?: number;
    /** 日期 */
    dt?: string;
  };

  type articleOpenInfoGETParams = {
    /** 文章ID */
    id: number;
  };

  type articleOpenQrcodeGETParams = {
    /** 文章ID */
    id: number;
  };

  type ArticleOpenReqRequest = {
    /** 文章ID */
    id: number;
    /** 公开状态 {@link YesNo}<br/>[Enum values:<br/>YES(1, "是", true)<br/>NO(0, "否", false)<br/>] */
    openStatus: string;
    /** 用户ID */
    userId?: number;
  };

  type ArticleOpenResResponse = {
    /** ID */
    id?: number;
    /** 文件夹ID */
    pid?: number;
    /** 字数 */
    words?: number;
    /** 公开文章版本 */
    openVersion?: number;
    /** 初次公开时间 */
    openTime?: string;
    /** 同步时间 */
    syncTime?: string;
    /** markdown 正文 */
    markdown?: string;
    /** html 正文 */
    html?: string;
  };

  type ArticleOpenSyncReqRequest = {
    /** 文章ID */
    id: number;
  };

  type articleRecycleDownloadGETParams = {
    /**       文章ID */
    id: number;
  };

  type ArticleRecycleListResResponse = {
    /** 文章ID */
    id?: number;
    /** 文章名称 */
    name?: string;
    /** 删除时间 */
    delTime?: string;
  };

  type ArticleRecycleRestoreReqRequest = {
    /** 文章ID */
    id: number;
  };

  type ArticleReferenceReqRequest = {
    /** 引用ID */
    targetId?: number;
    /** 引用名称, 链接名称或图片名称 */
    targetName?: string;
    /** 引用地址, 链接的地址或图片地址 */
    targetUrl?: string;
    /** 类型 [暂无] */
    type?: number;
  };

  type ArticleReferenceReqResponse = {
    /** 引用ID */
    targetId?: number;
    /** 引用名称, 链接名称或图片名称 */
    targetName?: string;
    /** 引用地址, 链接的地址或图片地址 */
    targetUrl?: string;
    /** 类型 [暂无] */
    type?: number;
  };

  type articleRefListGETParams = {
    /** 是否只查询内部文章之间的引用 */
    onlyInner: number;
    /** 查询指定文章的引用关系 */
    articleId?: number;
  };

  type ArticleStarReqRequest = {
    /** 文章ID */
    id: number;
    /** star 状态 {@link YesNo}<br/>[Enum values:<br/>YES(1, "是", true)<br/>NO(0, "否", false)<br/>] */
    starStatus: string;
  };

  type articleStatHeatmapGETParams = {
    /** 向前查询的月数, 填写负数, 默认为 -2 */
    offsetMonth?: number;
  };

  type articleStatHeatmapOpenGETParams = {
    /** 向前查询的月数, 填写负数, 默认为 -2 */
    offsetMonth?: number;
  };

  type ArticleStatResResponse = {
    /** 文章数 */
    articleCount?: number;
    /** 字数 */
    articleWords?: number;
  };

  type articleStatWordsUserGETParams = {
    /** No comments found. */
    id: number;
  };

  type articleTempHGETParams = {
    /** 临时访问 Key */
    k: number;
  };

  type articleTempKeyGETParams = {
    /**       文章ID */
    id: number;
    /** 临时访问的过期时间 */
    duration?: number;
  };

  type ArticleUpdContentReqRequest = {
    /** ID */
    id: number;
    /** 名称, 用于引用关系表中的名称冗余 */
    name: string;
    /** markdown 内容 */
    markdown?: string;
    /** html 内容 */
    html?: string;
    /** 目录 */
    toc?: string;
    /** 引用链接集合 (类: ArticleReferenceReq) */
    references?: ArticleReferenceReqRequest[];
  };

  type ArticleUpdContentResResponse = {
    /** 文章ID */
    id?: number;
    /** 文章版本 */
    version?: number;
    /** 字数 */
    words?: number;
    /** 修改时间 */
    updTime?: string;
  };

  type ArticleUpdNameReqRequest = {
    /** ID */
    id: number;
    /** 名称 */
    name: string;
  };

  type ArticleUpdReqRequest = {
    /** ID */
    id: number;
    /** 文件夹ID */
    pid: number;
    /** 文章名称 */
    name: string;
    /** 文章图标 */
    icon?: string;
    /** 标签集合 (类: String) */
    tags?: string[];
    /** 排序 */
    sort?: number;
    /** 封面 */
    cover?: string;
    /** 描述 */
    describes?: string;
    /** 颜色 */
    color?: string;
  };

  type ArticleUpdTagReqRequest = {
    /** ID */
    id: number;
    /** 标签, toc 标签因为具有特殊意义, 必须小写 */
    tag: string;
  };

  type ArticleWordsResRequest = {
    /** 日期 */
    date?: string;
    /** 统计值 */
    value?: number;
  };

  type ArticleWordsResResponse = {
    /** 日期 */
    date?: string;
    /** 统计值 */
    value?: number;
  };

  type ArticleWordsSaveReqRequest = {
    /** 字数统计集合 (类: ArticleWordsRes) */
    wordsList?: ArticleWordsResRequest[];
  };

  type BlossomUserResResponse = {
    /** 用户ID */
    id?: number;
    /** 用户类型 */
    type?: number;
    /** 用户名 */
    username?: string;
    /** 昵称 */
    nickName?: string;
    /** 用户头像 */
    avatar?: string;
    /** 备注 */
    remark?: string;
    /** 位置 */
    location?: string;
    /** 文章数 */
    articleCount?: number;
    /** 文章字数 */
    articleWords?: number;
    /** 创建日期 */
    creTime?: string;
    /** 逻辑删除, 目前用于禁用用户, 而不是删除 */
    delTime?: number;
    /** 对象存储信息, 非登录状态不返回该字段(object) */
    osRes?: OSResResponse;
    /** 系统参数, paramName: paramValue(map data) */
    params?: Record<string, any>;
    /** 用户参数, paramName: paramValue(map data) */
    userParams?: Record<string, any>;
  };

  type CityResLocationResponse = {
    /** No comments found. */
    name?: string;
    /** No comments found. */
    id?: string;
  };

  type DailyResDailyResponse = {
    /** 预报日期2013-12-30 */
    fxDate?: string;
    /** 预报当天最高温度 */
    tempMax?: string;
    /** 预报当天最低温度 */
    tempMin?: string;
    /** No comments found. */
    iconDay?: string;
    /** No comments found. */
    iconValueDay?: string;
    /** No comments found. */
    textDay?: string;
    /** No comments found. */
    iconNight?: string;
    /** No comments found. */
    iconValueNight?: string;
    /** No comments found. */
    textNight?: string;
  };

  type DelReqRequest = {
    /** ID */
    id: number;
  };

  type DocTreeResResponse = {
    /** id */
    i?: number;
    /** 父id */
    p?: number;
    /** 名称, 文件夹名称或文章名称 */
    n?: string;
    /** open, 公开状态 */
    o?: number;
    /** 版本有差异 version different */
    vd?: number;
    /** 图标 */
    icon?: string;
    /** 标签, 字符串数组 (类: String) */
    t?: string[];
    /** 排序 */
    s?: number;
    /** 是否收藏 {@link YesNo}<br/>[Enum values:<br/>YES(1, "是", true)<br/>NO(0, "否", false)<br/>] */
    star?: string;
    /** 存储地址 storage path */
    sp?: string;
    /** 类型 {@link DocTypeEnum}<br/>[Enum values:<br/>FA(1,"文章文件夹")<br/>FP(2,"图片文件夹")<br/>A(3,"文章")<br/>] */
    ty?: string;
    /** 子菜单 (类: DocTreeRes) */
    children?: string[];
  };

  type docTreesGETParams = {
    /** [Folder] 只查询文件夹, 包含文章文件夹和图片文件夹 */
    onlyFolder?: number;
    /** [Article] 只查询公开的文章和文件夹 */
    onlyOpen?: number;
    /** [Article] 只查询专题文章和文件夹 */
    onlySubject?: number;
    /** [Article] 只查询 star 文章和文件夹 */
    onlyStar?: number;
    /** [Article] 文章ID, 指查询指定文章 */
    articleId?: number;
    /** [Picture + Article] 只查询图片文件夹, 以及含有图片的文章文件夹 */
    onlyPicture?: number;
    /** 用户ID */
    userId?: number;
  };

  type DownloadReqRequest = {
    filename: string;
  };

  type FolderAddReqRequest = {
    /** 父ID */
    pid: number;
    /** 文件夹名称 */
    name: string;
    /** 图标 */
    icon?: string;
    /** 标签 (类: String) */
    tags?: string[];
    /** 排序 */
    sort: number;
    /** 封面图片 */
    cover?: string;
    /** 备注 */
    describes?: string;
    /** 存储地址 */
    storePath: string;
    /** 颜色 */
    color?: string;
    /** 文件夹类型 {@link FolderTypeEnum}
<p>文件夹类型确认后无法修改<br/>[Enum values:<br/>ARTICLE(1, "文章文件夹")<br/>PICTURE(2, "图片文件夹")<br/>] */
    type: string;
    /** 是否新增到尾部, 将忽略传入的 sort, 使用最大 sort */
    addToLast?: boolean;
  };

  type FolderEntityResponse = {
    /** id */
    id?: number;
    /** 父id */
    pid?: number;
    /** 文件夹名称 */
    name?: string;
    /** 图标 */
    icon?: string;
    /** 标签 */
    tags?: string;
    /** 开放状态 */
    openStatus?: number;
    /** 排序 */
    sort?: number;
    /** 封面图片 */
    cover?: string;
    /** 颜色 */
    color?: string;
    /** 备注 */
    describes?: string;
    /** 存储地址, 以 / 开头, 以 / 结尾, 保存时会进行格式校验 */
    storePath?: string;
    /** 专题字数 */
    subjectWords?: number;
    /** 专题的最后修改时间 */
    subjectUpdTime?: string;
    /** 文件夹类型, 1:文章文件夹; 2:图片文件夹; */
    type?: number;
    /** 创建时间 */
    creTime?: string;
    /** 修改时间 */
    updTime?: string;
    /** 用户ID */
    userId?: number;
    /** ID 集合 (类: Long) */
    ids?: string[];
  };

  type folderInfoGETParams = {
    /** 文件夹ID */
    id: number;
  };

  type FolderOpenCloseReqRequest = {
    /** id */
    id: number;
    /** open 状态 {@link YesNo}<br/>[Enum values:<br/>YES(1, "是", true)<br/>NO(0, "否", false)<br/>] */
    openStatus: string;
  };

  type FolderResResponse = {
    /** id */
    id?: number;
    /** 父id */
    pid?: number;
    /** 文件夹名称 */
    name?: string;
    /** 图标 */
    icon?: string;
    /** 标签 (类: String) */
    tags?: string[];
    /** 是否公开文件夹 [0:未公开，1:公开] */
    openStatus?: number;
    /** 排序 */
    sort?: number;
    /** 封面图片 */
    cover?: string;
    /** 颜色 */
    color?: string;
    /** 备注 */
    describes?: string;
    /** 存储地址 */
    storePath?: string;
    /** 专题字数 */
    subjectWords?: number;
    /** 专题的最后修改时间 */
    subjectUpdTime?: string;
    /** 文件夹类型 {@link FolderTypeEnum} */
    type?: number;
    /** 创建时间 */
    creTime?: string;
    /** 修改时间 */
    updTime?: string;
  };

  type FolderSubjectResResponse = {
    /** ID */
    id?: number;
    /** 目录文章的ID */
    tocId?: number;
    /** 文件夹名称 */
    name?: string;
    /** 颜色 */
    color?: string;
    /** 封面 */
    cover?: string;
    /** 图标 */
    icon?: string;
    /** 备注 */
    describes?: string;
    /** 专题字数 */
    subjectWords?: number;
    /** 专题的最后修改时间 */
    subjectUpdTime?: string;
  };

  type FolderUpdNameReqRequest = {
    /** ID */
    id: number;
    /** 名称 */
    name: string;
  };

  type FolderUpdReqRequest = {
    /** id */
    id: number;
    /** 上级菜单ID */
    pid: number;
    /** 文件夹名称 */
    name: string;
    /** 图标 */
    icon?: string;
    /** 标签 (类: String) */
    tags?: string[];
    /** 排序 */
    sort: number;
    /** 封面图片 */
    cover?: string;
    /** 备注 */
    describes?: string;
    /** 存储地址 */
    storePath: string;
    /** 颜色 */
    color?: string;
  };

  type HourlyResHourlyResponse = {
    /** 逐小时预报时间 */
    fxTime?: string;
    /** 逐小时预报温度 */
    temp?: string;
    /** 逐小时预报天气状况图标代码 */
    icon?: string;
    /** 图标 */
    iconValue?: string;
    /** 逐小时预报天气状况文字描述 */
    text?: string;
  };

  type JsonNodeResponse = {
    /** No comments found. */
    array?: boolean;
    /** No comments found. */
    valueNode?: boolean;
    /** No comments found. */
    containerNode?: boolean;
    /** No comments found. */
    missingNode?: boolean;
    /** No comments found. */
    object?: boolean;
  };

  type KickOutReqRequest = {
    /** 用户ID */
    userId: number;
  };

  type ListResponse = {
    /** A map key. */
    mapKey?: WebEntityResponse[];
  };

  type LoginReqRequest = {
    /** 登录方式，可见数据字典中[GrantType]部分, 必须为小写 */
    grantType: string;
    /** 客户端ID */
    clientId: string;
    /** 用户名, grantType = password 时必填 */
    username?: string;
    /** 密码, grantType = password 时必填 */
    password?: string;
  };

  type MetricLineResResponse = {
    /** 资源名称 */
    resource?: string;
    /** 标题 */
    title?: string;
    /** 副标题 */
    subTitle?: string;
    /** No comments found. */
    x?: string[];
    /** No comments found. */
    s?: string[];
    /** No comments found. */
    e?: string[];
    /** No comments found. */
    p?: string[];
    /** No comments found. */
    b?: string[];
    /** No comments found. */
    minRt?: string[];
    /** No comments found. */
    maxRt?: string[];
    /** No comments found. */
    avgRt?: string[];
  };

  type MetricResResponse = {
    /** 指标时间 */
    datetime?: string;
    /** 最大QPS */
    maxQps?: number;
    /** 平均QPS */
    avgQps?: number;
    /** 成功的请求数，success = pass + block */
    success?: number;
    /** 异常的请求数 */
    exception?: number;
    /** 通过的请求数 */
    pass?: number;
    /** 阻塞的请求数 */
    block?: number;
    /** 最小响应时间 ms */
    minRt?: number;
    /** 最大响应时间 ms */
    maxRt?: number;
    /** 平均响应时间 ms */
    avgRt?: number;
  };

  type NoteEntityResponse = {
    /** ID */
    id?: number;
    /** 内容 */
    content?: string;
    /** 置顶 */
    top?: number;
    /** 置顶时间 */
    topTime?: string;
    /** 创建时间 */
    creTime?: string;
    /** 用户ID */
    userId?: number;
  };

  type NoteSaveReqRequest = {
    /** 便签内容 */
    content: string;
  };

  type NoteTopReqRequest = {
    /** ID */
    id: number;
    /** 是否置顶 {@link YesNo} */
    top: number;
  };

  type NoteUpdReqRequest = {
    /** ID */
    id: number;
    /** 用户ID */
    userId?: number;
    /** 便签内容 */
    content: string;
  };

  type NowResNowResponse = {
    /** 实况观测时间 */
    obsTime?: string;
    /** 实况温度，默认单位：摄氏度 */
    temp?: string;
    /** 实况体感温度，默认单位：摄氏度 */
    feelsLike?: string;
    /** 当前天气状况和图标的代码 */
    icon?: string;
    /** No comments found. */
    iconValue?: string;
    /** 实况天气状况的文字描述，包括阴晴雨雪等天气状态的描述 */
    text?: string;
    /** 实况风向360角度 */
    wind360?: string;
    /** 实况风向 */
    windDir?: string;
    /** 实况风力等级 */
    windScale?: string;
    /** 实况风速，公里/小时 */
    windSpeed?: string;
    /** 实况相对湿度，百分比数值 */
    humidity?: string;
    /** 实况降水量，默认单位：毫米 */
    precip?: string;
    /** 实况大气压强，默认单位：百帕 */
    pressure?: string;
    /** 实况能见度，默认单位：公里 */
    vis?: string;
    /** 实况云量，百分比数值 */
    cloud?: string;
    /** 实况露点温度 */
    dew?: string;
  };

  type ObjectResponse = {
    /** any object.(object) */
    'any object'?: Record<string, any>;
  };

  type OSResResponse = {
    /** 对象存储类型 */
    osType?: string;
    /** bucket 名称 */
    bucketName?: string;
    /** 请求域名 */
    domain?: string;
    /** 保存路径 */
    defaultPath?: string;
  };

  type PageResPictureResResponse = {
    /** 页码，0或1均表示第一页 */
    pageNum?: number;
    /** 每页结果数 */
    pageSize?: number;
    /** 总页数 */
    totalPage?: number;
    /** 总行数 */
    total?: number;
    /** 是否有下一页 */
    hasNextPage?: boolean;
    /** 分页数据 (类: T) */
    datas?: PictureResResponse[];
  };

  type ParamUpdReqRequest = {
    /** 参数名称 */
    paramName: string;
    /** 参数值 */
    paramValue: string;
  };

  type picfilenameGETParams = {
    /** 文件名, 注意: 如果使用 Blossom 保存图片, 那么生成的图片地址就是访问该接口的地址, 无需再拼接图片地址 */
    filename: number;
  };

  type PictureDelBatchReqRequest = {
    /** ID (类: Long) */
    ids: string[];
    /** 忽略图片引用检查, 强制进行删除 */
    ignoreCheck?: boolean;
  };

  type PictureDelBatchResResponse = {
    /** 删除成功的图片数量 */
    success?: number;
    /** 成功删除的图片ID (类: Long) */
    successIds?: string[];
    /** 删除失败的图片数量 */
    fault?: number;
    /** 使用中的图片数量 */
    inuse?: number;
  };

  type PictureDelReqRequest = {
    /** ID */
    id: number;
    /** 忽略图片引用检查, 强制进行删除 */
    ignoreCheck?: boolean;
  };

  type pictureFileUploadPOSTParams = {
    /**         文件 */
    file: string;
    /**     文件名 */
    filename?: number;
    /**          图片上级ID */
    pid?: number;
    /** 是否允许重复上传 @since 1.6.0 */
    repeatUpload?: number;
  };

  type pictureInfoGETParams = {
    /** 图片URL */
    url: number;
  };

  type picturePageGETParams = {
    /** [分页参数] 页码, 0与1都表示第一页, 超过最大页时只显示最后一页 */
    pageNum?: number;
    /** [分页参数] 每页结果数, 不传则为10, 最大为200, 超过200会自动替换为200 */
    pageSize?: number;
    /** [分页参数] 排序字段, 需将驼峰类型字段改为下换线分隔字段,如 [userId > user_id] */
    sortField?: number;
    /** [分页参数] 排序方式 asc(升序) 或 desc(降序) */
    order?: number;
    /** 文件夹ID */
    pid?: number;
    /** 原文件名 */
    sourceName?: number;
    /** 文件名 */
    name?: number;
    /** 文件路径 */
    pathName?: number;
    /** 是否收藏 {@link YesNo} */
    starStatus?: number;
  };

  type PictureResResponse = {
    /** ID */
    id?: number;
    /** 文件夹ID */
    pid?: number;
    /** 文件名 */
    name?: string;
    /** 文件路径 */
    pathName?: string;
    /** 文件访问url */
    url?: string;
    /** 收藏  {@link YesNo} */
    starStatus?: number;
    /** 文件大小, byte */
    size?: number;
    /** 创建日期 */
    creTime?: string;
    /** 使用了该图片的文章名称, 逗号分隔 */
    articleNames?: string;
  };

  type PictureStarReqRequest = {
    /** 文章ID */
    id: number;
    /** 是否收藏 {@link YesNo} */
    starStatus: number;
  };

  type pictureStatGETParams = {
    /** 文件夹ID */
    pid?: number;
  };

  type PictureStatResResponse = {
    /** 图片个数 */
    pictureCount?: number;
    /** 图片大小 byte */
    pictureSize?: number;
  };

  type pictureStatUserGETParams = {
    /** 用户ID */
    id: number;
  };

  type PictureTransferReqRequest = {
    /** ID (类: Long) */
    ids: string[];
    /** 文件夹ID */
    pid: number;
  };

  type PlanDailyAddReqRequest = {
    /** 内容 */
    content?: string;
    /** 开始时间 */
    planStartTime: string;
    /** 结束日期 */
    planEndTime: string;
    /** 图片 */
    img?: string;
    /** 用户ID */
    userId?: number;
  };

  type PlanDailyResResponse = {
    /** ID */
    id?: number;
    /** 内容 */
    content?: string;
    /** 开始时间 */
    planStartTime?: string;
    /** 结束日期 */
    planEndTime?: string;
    /** 图片 */
    img?: string;
    /** 是否当前计划 */
    current?: boolean;
  };

  type PlanDayAddReqRequest = {
    /** 标题 */
    title: string;
    /** 内容 */
    content?: string;
    /** 日期 */
    planDate: string;
    /** 开始时间 */
    planStartTime: string;
    /** 结束 */
    planEndTime: string;
    /** 颜色 */
    color?: string;
    /** 是否全天 */
    allDay?: boolean;
    /** 是否重复 */
    repeat?: boolean;
    /** 重复天数 */
    repeatDay?: number;
    /** 用户ID */
    userId?: number;
  };

  type PlanDayResResponse = {
    /** ID */
    id?: number;
    /** 分组ID */
    groupId?: number;
    /** 标题 */
    title?: string;
    /** 内容 */
    content?: string;
    /** 日期 */
    planDate?: string;
    /** 计划开始时间 */
    planStartTime?: string;
    /** 计划结束时间 */
    planEndTime?: string;
    /** 颜色 */
    color?: string;
    /** 位置 */
    position?: string;
    /** 图片 */
    img?: string;
    /** 排序 */
    sort?: number;
  };

  type PlanDayUpdReqRequest = {
    /** 分组ID */
    groupId: number;
    /** 标题 */
    title: string;
    /** 内容 */
    content?: string;
    /** 用户ID */
    userId?: number;
  };

  type PlanDelReqRequest = {
    /** 计划ID */
    id?: number;
    /** 计划组ID */
    groupId?: number;
  };

  type planListDayGETParams = {
    /** 查询的月份, 会查询该月的所有每日计划 */
    month?: number;
  };

  type RAccessTokenResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: AccessToken)(object) */
    data?: AccessTokenResponse;
  };

  type RArticleBackupServiceBackupFileResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: BackupFile)(object) */
    data?: ArticleBackupServiceBackupFileResponse;
  };

  type RArticleEntityResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: ArticleEntity)(object) */
    data?: ArticleEntityResponse;
  };

  type RArticleHeatmapResResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: ArticleHeatmapRes)(object) */
    data?: ArticleHeatmapResResponse;
  };

  type RArticleInfoResResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: ArticleInfoRes)(object) */
    data?: ArticleInfoResResponse;
  };

  type RArticleLineResResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: ArticleLineRes)(object) */
    data?: ArticleLineResResponse;
  };

  type RArticleOpenResResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: ArticleOpenRes)(object) */
    data?: ArticleOpenResResponse;
  };

  type RArticleStatResResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: ArticleStatRes)(object) */
    data?: ArticleStatResResponse;
  };

  type RArticleUpdContentResResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: ArticleUpdContentRes)(object) */
    data?: ArticleUpdContentResResponse;
  };

  type RBlossomUserResResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: BlossomUserRes)(object) */
    data?: BlossomUserResResponse;
  };

  type ResourceRegionResponse = {
    /** No comments found.(object) */
    resource?: ResourceResponse;
    /** No comments found. */
    position?: number;
    /** No comments found. */
    count?: number;
  };

  type ResourceResponse = {
    /** No comments found. */
    open?: boolean;
    /** No comments found.(object) */
    uRL?: URLResponse;
    /** No comments found. */
    file?: boolean;
    /** No comments found. */
    description?: string;
    /** No comments found.(object) */
    uRI?: URIResponse;
    /** No comments found. */
    readable?: boolean;
    /** No comments found. */
    filename?: string;
    /** No comments found.(object) */
    inputStream?: Record<string, any>;
  };

  type ResourcesResResponse = {
    /** 资源名 */
    resource?: string;
    /** 统计该资源所用的文档数，相当于多少秒的指标信息 */
    docCount?: number;
    /** 最小响应时间 毫秒 */
    minRt?: number;
    /** 最大响应时间 毫秒 */
    maxRt?: number;
    /** 平均响应时间 毫秒 */
    avgRt?: number;
    /** 请求成功数 */
    success?: number;
  };

  type RFolderEntityResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: FolderEntity)(object) */
    data?: FolderEntityResponse;
  };

  type RFolderResResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: FolderRes)(object) */
    data?: FolderResResponse;
  };

  type RIntegerResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: Integer) */
    data?: number;
  };

  type RJsonNodeResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: JsonNode)(object) */
    data?: JsonNodeResponse;
  };

  type RListArticleBackupServiceBackupFileResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: List) */
    data?: ArticleBackupServiceBackupFileResponse[];
  };

  type RListArticleInfoSimpleResResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: List) */
    data?: ArticleInfoSimpleResResponse[];
  };

  type RListArticleLogResResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: List) */
    data?: ArticleLogResResponse[];
  };

  type RListArticleRecycleListResResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: List) */
    data?: ArticleRecycleListResResponse[];
  };

  type RListArticleWordsResResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: List) */
    data?: ArticleWordsResResponse[];
  };

  type RListDocTreeResResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: List) */
    data?: DocTreeResResponse[];
  };

  type RListFolderSubjectResResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: List) */
    data?: FolderSubjectResResponse[];
  };

  type RListNoteEntityResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: List) */
    data?: NoteEntityResponse[];
  };

  type RListPlanDailyResResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: List) */
    data?: PlanDailyResResponse[];
  };

  type RListResourcesResResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: List) */
    data?: ResourcesResResponse[];
  };

  type RListStringResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: List) */
    data?: string[];
  };

  type RListUserListResResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: List) */
    data?: UserListResResponse[];
  };

  type RLongResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: Long) */
    data?: number;
  };

  type RMapStringListPlanDayResResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: Map)(object) */
    data?: ListResponse;
  };

  type RMapStringListWebEntityResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: Map)(object) */
    data?: ListResponse;
  };

  type RMapStringObjectResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: Map)(object) */
    data?: ObjectResponse;
  };

  type RMapStringStringResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: Map)(object) */
    data?: StringResponse;
  };

  type RMetricLineResResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: MetricLineRes)(object) */
    data?: MetricLineResResponse;
  };

  type RMetricResResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: MetricRes)(object) */
    data?: MetricResResponse;
  };

  type ROSResResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: OSRes)(object) */
    data?: OSResResponse;
  };

  type RPageResPictureResResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: PageRes)(object) */
    data?: PageResPictureResResponse;
  };

  type RPictureDelBatchResResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: PictureDelBatchRes)(object) */
    data?: PictureDelBatchResResponse;
  };

  type RPictureResResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: PictureRes)(object) */
    data?: PictureResResponse;
  };

  type RPictureStatResResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: PictureStatRes)(object) */
    data?: PictureStatResResponse;
  };

  type RResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: ?)(object) */
    data?: Record<string, any>;
  };

  type RSearchResResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: SearchRes)(object) */
    data?: SearchResResponse;
  };

  type RSetStringResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: Set) */
    data?: string[];
  };

  type RStringResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: String) */
    data?: string;
  };

  type RTaskInfoResResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: TaskInfoRes)(object) */
    data?: TaskInfoResResponse;
  };

  type RTaskResResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: TaskRes)(object) */
    data?: TaskResResponse;
  };

  type RTaskStatisticResResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: TaskStatisticRes)(object) */
    data?: TaskStatisticResResponse;
  };

  type RTodoGroupResResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: TodoGroupRes)(object) */
    data?: TodoGroupResResponse;
  };

  type RTodoStatisticResResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: TodoStatisticRes)(object) */
    data?: TodoStatisticResResponse;
  };

  type RWeatherResResponse = {
    /** [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表 */
    code?: string;
    /** [公共响应] 说明, 正常时返回 "成功" */
    msg?: string;
    /** [公共响应] 异常信息, 无后台错误时无该字段 */
    ex?: string;
    /** [公共响应] 响应内容, 响应类如果为[?]则代表无 data (类: WeatherRes)(object) */
    data?: WeatherResResponse;
  };

  type searchGETParams = {
    /**  搜索关键字 */
    keyword: number;
    /**  高亮颜色 */
    hlColor: number;
    /** 是否全部匹配 */
    operator: number;
    /**    是否DEBUG, 为 true 时高亮前后缀为【】 */
    debug: number;
  };

  type SearchResHitResponse = {
    /** 主键 */
    id?: number;
    /** 文章名称 */
    name?: string;
    /** 源文章名称 */
    originName?: string;
    /** 标签 (类: String) */
    tags?: string[];
    /** 正文 */
    markdown?: string;
    /** 命中分数 */
    score?: number;
  };

  type SearchResResponse = {
    /** 命中总数 */
    totalHit?: number;
    /** 命中数据 (类: Hit) */
    hits?: SearchResHitResponse[];
  };

  type sentinelClusterNodeGETParams = {
    /** 资源名称 */
    id?: number;
  };

  type sentinelCnodeGETParams = {
    /** 资源名称 */
    id?: number;
  };

  type sentinelMetricAppGETParams = {
    /** No comments found. */
    appName?: number;
  };

  type sentinelMetricGETParams = {
    /**  资源名称, 如果不传入资源名称, 则搜索全部资源 */
    resource?: number;
    /** 开始时间 */
    startTime?: number;
    /**   结束时间, 如果不传入结束时间, 则按日志最大行数搜索 */
    endTime?: number;
    /**  文件中查询最大行数, sentinel 本地查询最大 12000 行 */
    maxLines?: number;
  };

  type sentinelMetricLineGETParams = {
    /**           资源名称 */
    resource?: number;
    /**          开始时间 */
    startTime?: number;
    /**            结束时间 */
    endTime?: number;
    /**           快捷区间, 用于约定时间范围, 会自定生成开始和结束时间 */
    interval?: number;
    /**     自定义单位聚合区间, 如 1m, 10m */
    customInterval?: number;
    /** NANOSECONDS(null)<br/>MICROSECONDS(null)<br/>MILLISECONDS(null)<br/>SECONDS(null)<br/>MINUTES(null)<br/>HOURS(null)<br/>DAYS(null)<br/> */
    customIntervalUnit?: number;
  };

  type sentinelResourcesGETParams = {
    /** 开始时间 */
    startTime?: number;
    /**   结束时间 */
    endTime?: number;
    /**  快捷区间, 优先级比日期高 */
    interval?: number;
  };

  type string = string;

  type StringResponse = {
    /** A map key. */
    mapKey?: string;
  };

  type TaskAddReqRequest = {
    /** todoId */
    todoId: string;
    /** 任务名称 */
    taskName?: string;
    /** 任务内容 */
    taskContent?: string;
    /** 截止日期 */
    deadLine?: string;
    /** 颜色 */
    color?: string;
    /** 标签集合 (类: String) */
    taskTags?: string[];
  };

  type TaskDelReqRequest = {
    /** ID */
    id: number;
    /** todoId */
    todoId: string;
  };

  type TaskInfoResResponse = {
    /** 任务ID */
    id?: number;
    /** 事项ID */
    todoId?: string;
    /** 待办类型 */
    todoType?: number;
    /** 任务名称 */
    taskName?: string;
    /** 任务内容 */
    taskContent?: string;
    /** 标签集合 (类: String) */
    taskTags?: string[];
    /** 任务状态 WAITING | PROCESSING | COMPLETED */
    taskStatus?: string;
    /** 截止至, 可填写任意内容 */
    deadLine?: string;
    /** 开始日期 */
    startTime?: string;
    /** 结束日期 */
    endTime?: string;
    /** 进度 0 ~ 100 */
    process?: number;
    /** 颜色 */
    color?: string;
    /** 创建时间 */
    creTime?: string;
  };

  type TaskResResponse = {
    /** 代办事项 (类: TaskInfoRes) */
    waiting?: TaskInfoResResponse[];
    /** 进行中事项 (类: TaskInfoRes) */
    processing?: TaskInfoResResponse[];
    /** 已完成事项 (类: TaskInfoRes) */
    completed?: TaskInfoResResponse[];
  };

  type TaskStatisticResResponse = {
    /** 日期 (类: String) */
    dates?: string[];
    /** 对应日期的完成率 (类: Long) */
    rates?: string[];
    /** 待完成数 */
    waiting?: number;
    /** 进行中数 */
    processing?: number;
    /** 完成数 */
    completed?: number;
    /** 总数 */
    total?: number;
  };

  type TaskTransferReqRequest = {
    /** 任务ID集合 (类: Long) */
    taskIds?: string[];
    /** 被转移的各项任务所在的待办事项ID */
    curTodoId: string;
    /** 转移到的事项ID */
    todoId: string;
    /** 是否删除原事项 */
    delSource?: boolean;
  };

  type TaskUpdReqRequest = {
    /** ID */
    id: number;
    /** todoId */
    todoId: string;
    /** 任务名称 */
    taskName: string;
    /** 任务内容 */
    taskContent?: string;
    /** 标签集合 (类: String) */
    taskTags?: string[];
    /** 截止日期 */
    deadLine?: string;
    /** 开始日期 */
    startTime?: string;
    /** 结束日期 */
    endTime?: string;
    /** 进度 0 ~ 100 */
    process?: number;
    /** 颜色 */
    color?: string;
    /** 接口是否返回列表 */
    returnTasks?: boolean;
  };

  type TaskUpdStatusReqRequest = {
    /** ID */
    id: number;
    /** todoId */
    todoId: string;
  };

  type todoExportGETParams = {
    /** 阶段性事项ID */
    todoId?: number;
    /** 每日事项开始日期 */
    beginTodoId?: number;
    /** 每日事项结束日期 */
    endTodoId?: number;
    /** 是否导出日期 */
    exportDate?: number;
    /** 是否导出进度 */
    exportProcess?: number;
  };

  type TodoGroupResResponse = {
    /** 每日待办事项(map data) */
    todoDays?: Record<string, any>;
    /** 开启的阶段性事项 (类: TodoGroup) */
    taskPhased?: TodoGroupResTodoGroupResponse[];
    /** 关闭的阶段性事项 (类: TodoGroup) */
    taskPhasedClose?: TodoGroupResTodoGroupResponse[];
  };

  type TodoGroupResTodoGroupResponse = {
    /** 事项ID */
    todoId?: string;
    /** 事项名称 */
    todoName?: string;
    /** 事项类型 10:每日待办事项 | 20:阶段性事项 */
    todoType?: number;
    /** 任务数量 */
    taskCount?: number;
    /** 任务数量说明 */
    taskCountStat?: string;
    /** 事项状态 1:完成 | 2:未完成 */
    todoStatus?: number;
  };

  type TodoPhasedAddReqRequest = {
    /** 阶段性事项名称 */
    todoName: string;
  };

  type TodoPhasedCompletedReqRequest = {
    /** 阶段性事项ID */
    todoId: string;
  };

  type TodoPhasedUpdReqRequest = {
    /** 阶段性事项ID */
    todoId: string;
    /** 阶段性事项名称 */
    todoName: string;
  };

  type todoStatGETParams = {
    /** todoId 待办事项ID */
    todoId: number;
  };

  type TodoStatisticResResponse = {
    /** No comments found. */
    todoId?: string;
    /** 事项名称 */
    todoName?: string;
    /** 事项类型 */
    todoType?: number;
    /** 事项状态 */
    todoStatus?: number;
    /** 最早创建日期 */
    firstCreTime?: string;
    /** 最早开始日期 */
    firstStartTime?: string;
    /** 最后完成日期 */
    lastEndTime?: string;
  };

  type todoTaskCountGETParams = {
    /** todoId */
    todoId: number;
  };

  type todoTaskInfoGETParams = {
    /** 任务ID */
    id: number;
  };

  type todoTaskListGETParams = {
    /** todoId */
    todoId: number;
  };

  type todoTaskTagsGETParams = {
    /** 待办事项类型 {@link TodoTypeEnum} */
    todoType: number;
    /**   待办事项ID, 当待办事项为阶段性事项时传入, 如未传入, 则返回空集合 */
    todoId?: number;
  };

  type URIResponse = {
    /** No comments found. */
    string?: string;
  };

  type URLResponse = {
    /** No comments found. */
    protocol?: string;
    /** No comments found. */
    host?: string;
    /** No comments found. */
    port?: number;
    /** No comments found. */
    file?: string;
    /** No comments found. */
    authority?: string;
    /** No comments found. */
    ref?: string;
    /** No comments found. */
    hashCode?: number;
  };

  type UserAddReqRequest = {
    /** 用户名 */
    username: string;
    /** 密码 */
    password: string;
    /** 用户类型 {@link UserTypeEnum} */
    type: number;
  };

  type UserDeleteReqRequest = {
    /** 用户ID */
    id: number;
  };

  type UserDisabledReqRequest = {
    /** 用户ID */
    id: number;
    /** 禁用状态 */
    delTime?: number;
  };

  type userInfoAdminGETParams = {
    /** 用户ID */
    id: number;
  };

  type UserListResResponse = {
    /** 用户ID */
    id?: number;
    /** 用户头像 */
    avatar?: string;
    /** 用户名 */
    username?: string;
    /** 昵称 */
    nickName?: string;
    /** 用户类型 */
    type?: number;
    /** 创建日期 */
    creTime?: string;
    /** 逻辑删除, 目前用于禁用用户, 而不是删除 */
    delTime?: number;
  };

  type UserParamUpdReqRequest = {
    /** 参数名称 */
    paramName: string;
    /** 参数值 */
    paramValue?: string;
    /** 用户ID */
    userId?: number;
  };

  type UserUpdAdminReqRequest = {
    /** 创建名称,创建人ID */
    creBy?: string;
    /** 创建时间 */
    creTime?: string;
    /** 修改人名称,修改人ID */
    updBy?: string;
    /** 修改时间 */
    updTime?: string;
    /** 删除人名称,删除人ID */
    delBy?: string;
    /** 删除时间，0 为未删除，非 0 为已删除 */
    delTime?: number;
    /** 用户ID */
    id?: number;
    /** 和风天气的位置 */
    location?: string;
    /** 类型 */
    type?: number;
  };

  type UserUpdPwdReqRequest = {
    /** 非必填, 自动填充 */
    userId?: number;
    /** 旧密码 */
    password: string;
    /** 新密码 */
    newPassword: string;
    /** 确认密码 */
    confirmPassword: string;
  };

  type UserUpdReqRequest = {
    /** 创建名称,创建人ID */
    creBy?: string;
    /** 创建时间 */
    creTime?: string;
    /** 修改人名称,修改人ID */
    updBy?: string;
    /** 修改时间 */
    updTime?: string;
    /** 删除人名称,删除人ID */
    delBy?: string;
    /** 删除时间，0 为未删除，非 0 为已删除 */
    delTime?: number;
    /** 用户名 */
    username: string;
    /** 昵称 */
    nickName: string;
    /** 说明 */
    remark: string;
    /** 和风天气的位置 */
    location?: string;
    /** 用户头像 */
    avatar?: string;
  };

  type weatherGETParams = {
    /** 用户位置 */
    location?: number;
  };

  type WeatherResResponse = {
    /** No comments found.(object) */
    location?: CityResLocationResponse;
    /** No comments found.(object) */
    now?: NowResNowResponse;
    /** No comments found. (类: Hourly) */
    hourly?: HourlyResHourlyResponse[];
    /** No comments found. (类: Daily) */
    daily?: DailyResDailyResponse[];
  };

  type WebEntityResponse = {
    /** ID */
    id?: number;
    /** 网站名称 */
    name?: string;
    /** 网站链接 */
    url?: string;
    /** 图标 */
    icon?: string;
    /** 图片, 图片的优先级比图标高 */
    img?: string;
    /** 类型 */
    type?: string;
    /** 排序 */
    sort?: number;
    /** 创建时间 */
    creTime?: string;
    /** No comments found. */
    userId?: number;
  };

  type WebSaveReqRequest = {
    /** ID 新增非必填, 修改必填 */
    id?: number;
    /** 网站名称 */
    name: string;
    /** 网站链接 */
    url: string;
    /** 网站图标 */
    icon?: string;
    /** 网站图片 */
    img?: string;
    /** 网站类型 */
    type: string;
    /** 网站排序 */
    sort: number;
    /** 用户ID */
    userId?: number;
  };
}
