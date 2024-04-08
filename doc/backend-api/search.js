let api = [];
const apiDocListSize = 6
api.push({
    name: '通用功能',
    order: '1',
    list: []
})
api[0].list.push({
    alias: 'AuthController',
    order: '1',
    link: '登录授权_[auth]',
    desc: '登录授权 [Auth]',
    list: []
})
api[0].list[0].list.push({
    order: '1',
    deprecated: 'false',
    url: 'http://127.0.0.1/login',
    desc: '登录[OP]',
});
api[0].list[0].list.push({
    order: '2',
    deprecated: 'false',
    url: 'http://127.0.0.1/logout',
    desc: '用户退出',
});
api[0].list[0].list.push({
    order: '3',
    deprecated: 'false',
    url: 'http://127.0.0.1/kickout',
    desc: '踢出用户',
});
api[0].list[0].list.push({
    order: '4',
    deprecated: 'false',
    url: 'http://127.0.0.1/check',
    desc: '检查 Token 状态',
});
api[0].list.push({
    alias: 'UserParamController',
    order: '2',
    link: '用户参数配置',
    desc: '用户参数配置',
    list: []
})
api[0].list[1].list.push({
    order: '1',
    deprecated: 'false',
    url: 'http://127.0.0.1/user/param/list',
    desc: '用户参数列表',
});
api[0].list[1].list.push({
    order: '2',
    deprecated: 'false',
    url: 'http://127.0.0.1/user/param/upd',
    desc: '修改用户参数',
});
api[0].list[1].list.push({
    order: '3',
    deprecated: 'false',
    url: 'http://127.0.0.1/user/param/upd/admin',
    desc: '修改用户参数',
});
api[0].list[1].list.push({
    order: '4',
    deprecated: 'false',
    url: 'http://127.0.0.1/user/param/refresh',
    desc: '刷新用户配置',
});
api[0].list.push({
    alias: 'SearchController',
    order: '3',
    link: '搜索接口',
    desc: '搜索接口',
    list: []
})
api[0].list[2].list.push({
    order: '1',
    deprecated: 'false',
    url: 'http://127.0.0.1/search',
    desc: '搜索',
});
api[0].list.push({
    alias: 'SysController',
    order: '4',
    link: '系统功能_[sys]',
    desc: '系统功能 [Sys]',
    list: []
})
api[0].list[3].list.push({
    order: '1',
    deprecated: 'false',
    url: 'http://127.0.0.1/sys/alive',
    desc: '服务在线检查 [OP]',
});
api[0].list[3].list.push({
    order: '2',
    deprecated: 'false',
    url: 'http://127.0.0.1/sys/osconfig',
    desc: '对象存储配置',
});
api[0].list[3].list.push({
    order: '3',
    deprecated: 'false',
    url: 'http://127.0.0.1/sys/param/list',
    desc: '系统参数列表',
});
api[0].list[3].list.push({
    order: '4',
    deprecated: 'false',
    url: 'http://127.0.0.1/sys/param/upd',
    desc: '修改系统参数',
});
api[0].list[3].list.push({
    order: '5',
    deprecated: 'false',
    url: 'http://127.0.0.1/sys/param/refresh',
    desc: '刷新系统配置',
});
api[0].list.push({
    alias: 'UserController',
    order: '5',
    link: '用户_[user]',
    desc: '用户 [User]',
    list: []
})
api[0].list[4].list.push({
    order: '1',
    deprecated: 'false',
    url: 'http://127.0.0.1/user/list',
    desc: '获取用户列表',
});
api[0].list[4].list.push({
    order: '2',
    deprecated: 'false',
    url: 'http://127.0.0.1/user/info/admin',
    desc: '用户信息',
});
api[0].list[4].list.push({
    order: '3',
    deprecated: 'false',
    url: 'http://127.0.0.1/user/add',
    desc: '新增用户',
});
api[0].list[4].list.push({
    order: '4',
    deprecated: 'false',
    url: 'http://127.0.0.1/user/upd/admin',
    desc: '修改用户',
});
api[0].list[4].list.push({
    order: '5',
    deprecated: 'false',
    url: 'http://127.0.0.1/user/disabled',
    desc: '禁用启用',
});
api[0].list[4].list.push({
    order: '6',
    deprecated: 'false',
    url: 'http://127.0.0.1/user/info',
    desc: '用户信息',
});
api[0].list[4].list.push({
    order: '7',
    deprecated: 'false',
    url: 'http://127.0.0.1/user/info/open',
    desc: '用户信息 [OP]',
});
api[0].list[4].list.push({
    order: '8',
    deprecated: 'false',
    url: 'http://127.0.0.1/user/upd',
    desc: '修改用户',
});
api[0].list[4].list.push({
    order: '9',
    deprecated: 'false',
    url: 'http://127.0.0.1/user/upd/pwd',
    desc: '修改密码',
});
api[0].list[4].list.push({
    order: '10',
    deprecated: 'false',
    url: 'http://127.0.0.1/user/del',
    desc: '删除用户',
});
api.push({
    name: '业务功能',
    order: '2',
    list: []
})
api[1].list.push({
    alias: 'DocController',
    order: '1',
    link: '文档_[doc]',
    desc: '文档 [Doc]',
    list: []
})
api[1].list[0].list.push({
    order: '1',
    deprecated: 'false',
    url: 'http://127.0.0.1/doc/trees',
    desc: '文档列表',
});
api[1].list[0].list.push({
    order: '2',
    deprecated: 'false',
    url: 'http://127.0.0.1/doc/trees/open',
    desc: '文档列表 [OP]',
});
api[1].list[0].list.push({
    order: '3',
    deprecated: 'false',
    url: 'http://127.0.0.1/doc/upd/sort',
    desc: '修改排序',
});
api[1].list.push({
    alias: 'FolderController',
    order: '2',
    link: '文件夹_[folder]',
    desc: '文件夹 [Folder]',
    list: []
})
api[1].list[1].list.push({
    order: '1',
    deprecated: 'false',
    url: 'http://127.0.0.1/folder/subjects/open',
    desc: '查询专题列表 [OP]',
});
api[1].list[1].list.push({
    order: '2',
    deprecated: 'false',
    url: 'http://127.0.0.1/folder/subjects',
    desc: '查询专题列表',
});
api[1].list[1].list.push({
    order: '3',
    deprecated: 'false',
    url: 'http://127.0.0.1/folder/star',
    desc: '星标文件夹',
});
api[1].list[1].list.push({
    order: '4',
    deprecated: 'false',
    url: 'http://127.0.0.1/folder/info',
    desc: '通过ID查询文件夹',
});
api[1].list[1].list.push({
    order: '5',
    deprecated: 'false',
    url: 'http://127.0.0.1/folder/add',
    desc: '新增或修改文件夹',
});
api[1].list[1].list.push({
    order: '6',
    deprecated: 'false',
    url: 'http://127.0.0.1/folder/upd',
    desc: '修改文件夹',
});
api[1].list[1].list.push({
    order: '7',
    deprecated: 'false',
    url: 'http://127.0.0.1/folder/upd/name',
    desc: '修改文件夹',
});
api[1].list[1].list.push({
    order: '8',
    deprecated: 'false',
    url: 'http://127.0.0.1/folder/upd/tag',
    desc: '快速增加/删除标签',
});
api[1].list[1].list.push({
    order: '9',
    deprecated: 'false',
    url: 'http://127.0.0.1/folder/open',
    desc: '公开文件夹',
});
api[1].list[1].list.push({
    order: '10',
    deprecated: 'false',
    url: 'http://127.0.0.1/folder/del',
    desc: '删除文件夹',
});
api[1].list.push({
    alias: 'ArticleController',
    order: '3',
    link: '文章_[article]',
    desc: '文章 [Article]',
    list: []
})
api[1].list[2].list.push({
    order: '1',
    deprecated: 'false',
    url: 'http://127.0.0.1/article/list',
    desc: '查询列表',
});
api[1].list[2].list.push({
    order: '2',
    deprecated: 'false',
    url: 'http://127.0.0.1/article/info',
    desc: '文章详情 [ID]',
});
api[1].list[2].list.push({
    order: '3',
    deprecated: 'false',
    url: 'http://127.0.0.1/article/add',
    desc: '新增文章',
});
api[1].list[2].list.push({
    order: '4',
    deprecated: 'false',
    url: 'http://127.0.0.1/article/upd',
    desc: '修改文章基础信息',
});
api[1].list[2].list.push({
    order: '5',
    deprecated: 'false',
    url: 'http://127.0.0.1/article/upd/content',
    desc: '保存正文内容',
});
api[1].list[2].list.push({
    order: '6',
    deprecated: 'false',
    url: 'http://127.0.0.1/article/upd/name',
    desc: '修改文章名称',
});
api[1].list[2].list.push({
    order: '7',
    deprecated: 'false',
    url: 'http://127.0.0.1/article/upd/tag',
    desc: '快速增加/删除标签',
});
api[1].list[2].list.push({
    order: '8',
    deprecated: 'false',
    url: 'http://127.0.0.1/article/del',
    desc: '删除文章',
});
api[1].list[2].list.push({
    order: '9',
    deprecated: 'false',
    url: 'http://127.0.0.1/article/star',
    desc: '星标文章',
});
api[1].list[2].list.push({
    order: '10',
    deprecated: 'false',
    url: 'http://127.0.0.1/article/download',
    desc: '下载文章',
});
api[1].list[2].list.push({
    order: '11',
    deprecated: 'false',
    url: 'http://127.0.0.1/article/download/html',
    desc: '下载文章 Html',
});
api[1].list[2].list.push({
    order: '12',
    deprecated: 'false',
    url: 'http://127.0.0.1/article/import',
    desc: '文章导入',
});
api[1].list[2].list.push({
    order: '13',
    deprecated: 'false',
    url: 'http://127.0.0.1/article/temp/key',
    desc: '创建文章的临时访问缓存',
});
api[1].list[2].list.push({
    order: '14',
    deprecated: 'false',
    url: 'http://127.0.0.1/article/temp/h',
    desc: '临时查看文章',
});
api[1].list.push({
    alias: 'ArticleOpenController',
    order: '4',
    link: '文章公开_[a#open]',
    desc: '文章公开 [A#Open]',
    list: []
})
api[1].list[3].list.push({
    order: '1',
    deprecated: 'false',
    url: 'http://127.0.0.1/article/open/info',
    desc: '查询公开文章 [OP]',
});
api[1].list[3].list.push({
    order: '2',
    deprecated: 'false',
    url: 'http://127.0.0.1/article/open',
    desc: '公开文章',
});
api[1].list[3].list.push({
    order: '3',
    deprecated: 'false',
    url: 'http://127.0.0.1/article/open/batch',
    desc: '批量公开文章',
});
api[1].list[3].list.push({
    order: '4',
    deprecated: 'false',
    url: 'http://127.0.0.1/article/open/sync',
    desc: '同步公开文章',
});
api[1].list[3].list.push({
    order: '5',
    deprecated: 'false',
    url: 'http://127.0.0.1/article/open/qrcode',
    desc: '生成公开文章二维码',
});
api[1].list.push({
    alias: 'ArticleReferenceController',
    order: '5',
    link: '文章引用_[a#reference]',
    desc: '文章引用 [A#Reference]',
    list: []
})
api[1].list[4].list.push({
    order: '1',
    deprecated: 'false',
    url: 'http://127.0.0.1/article/ref/list',
    desc: '文章引用关系',
});
api[1].list.push({
    alias: 'ArticleLogController',
    order: '6',
    link: '文章记录_[a#log]',
    desc: '文章记录 [A#Log]',
    list: []
})
api[1].list[5].list.push({
    order: '1',
    deprecated: 'false',
    url: 'http://127.0.0.1/article/log',
    desc: '文章编辑记录',
});
api[1].list[5].list.push({
    order: '2',
    deprecated: 'false',
    url: 'http://127.0.0.1/article/log/content',
    desc: '查文章记录内容',
});
api[1].list.push({
    alias: 'ArticleRecycleController',
    order: '7',
    link: '文章回收站_[a#recycle]',
    desc: '文章回收站 [A#Recycle]',
    list: []
})
api[1].list[6].list.push({
    order: '1',
    deprecated: 'false',
    url: 'http://127.0.0.1/article/recycle/list',
    desc: '查询列表',
});
api[1].list[6].list.push({
    order: '2',
    deprecated: 'false',
    url: 'http://127.0.0.1/article/recycle/restore',
    desc: '还原文章',
});
api[1].list[6].list.push({
    order: '3',
    deprecated: 'false',
    url: 'http://127.0.0.1/article/recycle/download',
    desc: '下载文章',
});
api[1].list.push({
    alias: 'ArticleBackupController',
    order: '8',
    link: '文章备份_[a#backup]',
    desc: '文章备份 [A#Backup]',
    list: []
})
api[1].list[7].list.push({
    order: '1',
    deprecated: 'false',
    url: 'http://127.0.0.1/article/backup',
    desc: '执行备份',
});
api[1].list[7].list.push({
    order: '2',
    deprecated: 'false',
    url: 'http://127.0.0.1/article/backup/list',
    desc: '备份记录',
});
api[1].list[7].list.push({
    order: '3',
    deprecated: 'true',
    url: 'http://127.0.0.1/article/backup/download',
    desc: '下载压缩包',
});
api[1].list[7].list.push({
    order: '4',
    deprecated: 'false',
    url: 'http://127.0.0.1/article/backup/download/fragment',
    desc: 'head 请求获取分片信息 [OP]',
});
api[1].list[7].list.push({
    order: '5',
    deprecated: 'false',
    url: 'http://127.0.0.1/article/backup/download/fragment',
    desc: '分片下载 [OP]',
});
api[1].list.push({
    alias: 'ArticleStatController',
    order: '9',
    link: '文章统计_[a#stat]',
    desc: '文章统计 [A#Stat]',
    list: []
})
api[1].list[8].list.push({
    order: '1',
    deprecated: 'false',
    url: 'http://127.0.0.1/article/stat/heatmap/open',
    desc: '每日编辑热力图 [OP]',
});
api[1].list[8].list.push({
    order: '2',
    deprecated: 'false',
    url: 'http://127.0.0.1/article/stat/heatmap',
    desc: '每日编辑热力图',
});
api[1].list[8].list.push({
    order: '3',
    deprecated: 'false',
    url: 'http://127.0.0.1/article/stat/words/open',
    desc: '文章数和文章字数 [OP]',
});
api[1].list[8].list.push({
    order: '4',
    deprecated: 'false',
    url: 'http://127.0.0.1/article/stat/words',
    desc: '文章数和文章字数',
});
api[1].list[8].list.push({
    order: '5',
    deprecated: 'false',
    url: 'http://127.0.0.1/article/stat/words/user',
    desc: '文章数和文章字数',
});
api[1].list[8].list.push({
    order: '6',
    deprecated: 'false',
    url: 'http://127.0.0.1/article/stat/words/list',
    desc: '字数统计列表',
});
api[1].list[8].list.push({
    order: '7',
    deprecated: 'false',
    url: 'http://127.0.0.1/article/stat/words/save',
    desc: '保存字数统计信息',
});
api[1].list[8].list.push({
    order: '8',
    deprecated: 'false',
    url: 'http://127.0.0.1/article/stat/line/open',
    desc: '文章字数折线图 [OP]',
});
api[1].list[8].list.push({
    order: '9',
    deprecated: 'false',
    url: 'http://127.0.0.1/article/stat/line',
    desc: '近36月字数折线图',
});
api[1].list.push({
    alias: 'PictureController',
    order: '10',
    link: '图片_[picture]',
    desc: '图片 [Picture]',
    list: []
})
api[1].list[9].list.push({
    order: '1',
    deprecated: 'false',
    url: 'http://127.0.0.1/picture/page',
    desc: '分页列表',
});
api[1].list[9].list.push({
    order: '2',
    deprecated: 'false',
    url: 'http://127.0.0.1/picture/info',
    desc: '查询图片信息',
});
api[1].list[9].list.push({
    order: '3',
    deprecated: 'false',
    url: 'http://127.0.0.1/picture/del',
    desc: '删除图片',
});
api[1].list[9].list.push({
    order: '4',
    deprecated: 'false',
    url: 'http://127.0.0.1/picture/del/batch',
    desc: '批量删除文件',
});
api[1].list[9].list.push({
    order: '5',
    deprecated: 'false',
    url: 'http://127.0.0.1/picture/transfer',
    desc: '文件转移',
});
api[1].list[9].list.push({
    order: '6',
    deprecated: 'false',
    url: 'http://127.0.0.1/picture/star',
    desc: '星标图片',
});
api[1].list[9].list.push({
    order: '7',
    deprecated: 'false',
    url: 'http://127.0.0.1/picture/stat',
    desc: '统计图片 [OP]',
});
api[1].list[9].list.push({
    order: '8',
    deprecated: 'false',
    url: 'http://127.0.0.1/picture/stat/user',
    desc: '查询用户的图片统计',
});
api[1].list.push({
    alias: 'PictureBlosController',
    order: '11',
    link: '图片上传查看_[p#blos]',
    desc: '图片上传查看 [P#Blos]',
    list: []
})
api[1].list[10].list.push({
    order: '1',
    deprecated: 'false',
    url: 'http://127.0.0.1/picture/file/upload',
    desc: '上传文件',
});
api[1].list[10].list.push({
    order: '2',
    deprecated: 'false',
    url: 'http://127.0.0.1/pic/{filename}/**',
    desc: '查看图片 [OP]',
});
api[1].list.push({
    alias: 'PlanController',
    order: '12',
    link: '计划_[plan]',
    desc: '计划 [Plan]',
    list: []
})
api[1].list[11].list.push({
    order: '1',
    deprecated: 'false',
    url: 'http://127.0.0.1/plan/list/day',
    desc: '每日计划',
});
api[1].list[11].list.push({
    order: '2',
    deprecated: 'false',
    url: 'http://127.0.0.1/plan/list/daily',
    desc: '日常计划',
});
api[1].list[11].list.push({
    order: '3',
    deprecated: 'false',
    url: 'http://127.0.0.1/plan/add/day',
    desc: '新增每日计划',
});
api[1].list[11].list.push({
    order: '4',
    deprecated: 'false',
    url: 'http://127.0.0.1/plan/upd/day',
    desc: '修改每日计划',
});
api[1].list[11].list.push({
    order: '5',
    deprecated: 'false',
    url: 'http://127.0.0.1/plan/add/daily',
    desc: '新增日常计划',
});
api[1].list[11].list.push({
    order: '6',
    deprecated: 'false',
    url: 'http://127.0.0.1/plan/del',
    desc: '删除计划',
});
api[1].list.push({
    alias: 'TodoController',
    order: '13',
    link: '待办事项_[todo]',
    desc: '待办事项 [Todo]',
    list: []
})
api[1].list[12].list.push({
    order: '1',
    deprecated: 'false',
    url: 'http://127.0.0.1/todo/list',
    desc: '待办事项列表',
});
api[1].list[12].list.push({
    order: '2',
    deprecated: 'false',
    url: 'http://127.0.0.1/todo/add/phased',
    desc: '新增阶段性事项',
});
api[1].list[12].list.push({
    order: '3',
    deprecated: 'false',
    url: 'http://127.0.0.1/todo/upd/name',
    desc: '修改阶段性事项名称',
});
api[1].list[12].list.push({
    order: '4',
    deprecated: 'false',
    url: 'http://127.0.0.1/todo/open',
    desc: '开启阶段性事项',
});
api[1].list[12].list.push({
    order: '5',
    deprecated: 'false',
    url: 'http://127.0.0.1/todo/completed',
    desc: '完成阶段性事项',
});
api[1].list[12].list.push({
    order: '6',
    deprecated: 'false',
    url: 'http://127.0.0.1/todo/stat',
    desc: '待办事项列表',
});
api[1].list[12].list.push({
    order: '7',
    deprecated: 'false',
    url: 'http://127.0.0.1/todo/export',
    desc: '任务导出',
});
api[1].list.push({
    alias: 'TaskController',
    order: '14',
    link: '待办事项_[todo#task]',
    desc: '待办事项 [Todo#Task]',
    list: []
})
api[1].list[13].list.push({
    order: '1',
    deprecated: 'false',
    url: 'http://127.0.0.1/todo/task/list',
    desc: '任务列表',
});
api[1].list[13].list.push({
    order: '2',
    deprecated: 'false',
    url: 'http://127.0.0.1/todo/task/info',
    desc: '任务详情',
});
api[1].list[13].list.push({
    order: '3',
    deprecated: 'false',
    url: 'http://127.0.0.1/todo/task/count',
    desc: '任务数量',
});
api[1].list[13].list.push({
    order: '4',
    deprecated: 'false',
    url: 'http://127.0.0.1/todo/task/tags',
    desc: '标签列表',
});
api[1].list[13].list.push({
    order: '5',
    deprecated: 'false',
    url: 'http://127.0.0.1/todo/task/stat',
    desc: '统计',
});
api[1].list[13].list.push({
    order: '6',
    deprecated: 'false',
    url: 'http://127.0.0.1/todo/task/add',
    desc: '新增任务',
});
api[1].list[13].list.push({
    order: '7',
    deprecated: 'false',
    url: 'http://127.0.0.1/todo/task/upd',
    desc: '修改任务',
});
api[1].list[13].list.push({
    order: '8',
    deprecated: 'false',
    url: 'http://127.0.0.1/todo/task/waiting',
    desc: '事项移动到待办',
});
api[1].list[13].list.push({
    order: '9',
    deprecated: 'false',
    url: 'http://127.0.0.1/todo/task/processing',
    desc: '事项移动到进行中',
});
api[1].list[13].list.push({
    order: '10',
    deprecated: 'false',
    url: 'http://127.0.0.1/todo/task/completed',
    desc: '事项移动到完成',
});
api[1].list[13].list.push({
    order: '11',
    deprecated: 'false',
    url: 'http://127.0.0.1/todo/task/del',
    desc: '删除事项',
});
api[1].list[13].list.push({
    order: '12',
    deprecated: 'false',
    url: 'http://127.0.0.1/todo/task/transfer',
    desc: '转移事项',
});
api[1].list.push({
    alias: 'NoteController',
    order: '15',
    link: '便签_[note]',
    desc: '便签 [Note]',
    list: []
})
api[1].list[14].list.push({
    order: '1',
    deprecated: 'false',
    url: 'http://127.0.0.1/note/list',
    desc: '全部列表',
});
api[1].list[14].list.push({
    order: '2',
    deprecated: 'false',
    url: 'http://127.0.0.1/note/add',
    desc: '新增',
});
api[1].list[14].list.push({
    order: '3',
    deprecated: 'false',
    url: 'http://127.0.0.1/note/upd',
    desc: '修改',
});
api[1].list[14].list.push({
    order: '4',
    deprecated: 'false',
    url: 'http://127.0.0.1/note/del',
    desc: '删除',
});
api[1].list[14].list.push({
    order: '5',
    deprecated: 'false',
    url: 'http://127.0.0.1/note/top',
    desc: '置顶/取消置顶',
});
api[1].list.push({
    alias: 'WebController',
    order: '16',
    link: '网站收藏_[web]',
    desc: '网站收藏 [Web]',
    list: []
})
api[1].list[15].list.push({
    order: '1',
    deprecated: 'false',
    url: 'http://127.0.0.1/web/list',
    desc: '网站列表 [OP]',
});
api[1].list[15].list.push({
    order: '2',
    deprecated: 'false',
    url: 'http://127.0.0.1/web/save',
    desc: '保存',
});
api[1].list[15].list.push({
    order: '3',
    deprecated: 'false',
    url: 'http://127.0.0.1/web/del',
    desc: '删除',
});
api.push({
    name: '三方接口',
    order: '3',
    list: []
})
api[2].list.push({
    alias: 'WeatherController',
    order: '1',
    link: '和风天气',
    desc: '和风天气',
    list: []
})
api[2].list[0].list.push({
    order: '1',
    deprecated: 'false',
    url: 'http://127.0.0.1/weather',
    desc: '获取天气信息',
});
api[2].list.push({
    alias: 'ThirdPartyScheduled',
    order: '2',
    link: '三方接口定时任务',
    desc: '三方接口定时任务',
    list: []
})
api[2].list[1].list.push({
    order: '1',
    deprecated: 'false',
    url: 'http://127.0.0.1/thirdparty/scheduled/weather',
    desc: '刷新天气缓存',
});
api.push({
    name: '拓展功能',
    order: '4',
    list: []
})
api[3].list.push({
    alias: 'SentinelMetricController',
    order: '1',
    link: '流量监控【本地日志】',
    desc: '流量监控【本地日志】',
    list: []
})
api[3].list[0].list.push({
    order: '1',
    deprecated: 'false',
    url: 'http://127.0.0.1/sentinel/clusterNode',
    desc: '资源列表',
});
api[3].list[0].list.push({
    order: '2',
    deprecated: 'false',
    url: 'http://127.0.0.1/sentinel/resources',
    desc: '一天内被请求的资源列表',
});
api[3].list[0].list.push({
    order: '3',
    deprecated: 'false',
    url: 'http://127.0.0.1/sentinel/metric',
    desc: '资源的监控信息',
});
api[3].list[0].list.push({
    order: '4',
    deprecated: 'false',
    url: 'http://127.0.0.1/sentinel/metric/line',
    desc: '资源折线图',
});
api[3].list[0].list.push({
    order: '5',
    deprecated: 'false',
    url: 'http://127.0.0.1/sentinel/metric/app',
    desc: '集群过去24小时的总体信息',
});
api[3].list[0].list.push({
    order: '6',
    deprecated: 'false',
    url: 'http://127.0.0.1/sentinel/cnode',
    desc: '资源的秒级, 分钟级指标信息',
});
api.push({
    name: '错误码列表',
    order: '5',
    list: []
})
api.push({
    name: '数据字典',
    order: '5',
    list: []
})
api[5].list.push({
    alias: '登录方式 [GrantTypeEnum]',
    order: '1',
    link: '登录方式_[granttypeenum]',
    desc: '登录方式 [GrantTypeEnum]',
    list: []
})
api[5].list.push({
    alias: '是否,真假字典 [YesNo]',
    order: '2',
    link: '是否,真假字典_[yesno]',
    desc: '是否,真假字典 [YesNo]',
    list: []
})
api[5].list.push({
    alias: '用户类型 [UserTypeEnum]',
    order: '3',
    link: '用户类型_[usertypeenum]',
    desc: '用户类型 [UserTypeEnum]',
    list: []
})
api[5].list.push({
    alias: '文档类型 [DocTypeEnum]',
    order: '4',
    link: '文档类型_[doctypeenum]',
    desc: '文档类型 [DocTypeEnum]',
    list: []
})
api[5].list.push({
    alias: '文件夹类型 [FolderTypeEnum]',
    order: '5',
    link: '文件夹类型_[foldertypeenum]',
    desc: '文件夹类型 [FolderTypeEnum]',
    list: []
})
api[5].list.push({
    alias: '系统参数表配置内容',
    order: '100',
    link: '系统参数表配置内容',
    desc: '系统参数表配置内容',
    list: []
})
document.onkeydown = keyDownSearch;
function keyDownSearch(e) {
    const theEvent = e;
    const code = theEvent.keyCode || theEvent.which || theEvent.charCode;
    if (code === 13) {
        const search = document.getElementById('search');
        const searchValue = search.value.toLocaleLowerCase();

        let searchGroup = [];
        for (let i = 0; i < api.length; i++) {

            let apiGroup = api[i];

            let searchArr = [];
            for (let i = 0; i < apiGroup.list.length; i++) {
                let apiData = apiGroup.list[i];
                const desc = apiData.desc;
                if (desc.toLocaleLowerCase().indexOf(searchValue) > -1) {
                    searchArr.push({
                        order: apiData.order,
                        desc: apiData.desc,
                        link: apiData.link,
                        list: apiData.list
                    });
                } else {
                    let methodList = apiData.list || [];
                    let methodListTemp = [];
                    for (let j = 0; j < methodList.length; j++) {
                        const methodData = methodList[j];
                        const methodDesc = methodData.desc;
                        if (methodDesc.toLocaleLowerCase().indexOf(searchValue) > -1) {
                            methodListTemp.push(methodData);
                            break;
                        }
                    }
                    if (methodListTemp.length > 0) {
                        const data = {
                            order: apiData.order,
                            desc: apiData.desc,
                            link: apiData.link,
                            list: methodListTemp
                        };
                        searchArr.push(data);
                    }
                }
            }
            if (apiGroup.name.toLocaleLowerCase().indexOf(searchValue) > -1) {
                searchGroup.push({
                    name: apiGroup.name,
                    order: apiGroup.order,
                    list: searchArr
                });
                continue;
            }
            if (searchArr.length === 0) {
                continue;
            }
            searchGroup.push({
                name: apiGroup.name,
                order: apiGroup.order,
                list: searchArr
            });
        }
        let html;
        if (searchValue === '') {
            const liClass = "";
            const display = "display: none";
            html = buildAccordion(api,liClass,display);
            document.getElementById('accordion').innerHTML = html;
        } else {
            const liClass = "open";
            const display = "display: block";
            html = buildAccordion(searchGroup,liClass,display);
            document.getElementById('accordion').innerHTML = html;
        }
        const Accordion = function (el, multiple) {
            this.el = el || {};
            this.multiple = multiple || false;
            const links = this.el.find('.dd');
            links.on('click', {el: this.el, multiple: this.multiple}, this.dropdown);
        };
        Accordion.prototype.dropdown = function (e) {
            const $el = e.data.el;
            let $this = $(this), $next = $this.next();
            $next.slideToggle();
            $this.parent().toggleClass('open');
            if (!e.data.multiple) {
                $el.find('.submenu').not($next).slideUp("20").parent().removeClass('open');
            }
        };
        new Accordion($('#accordion'), false);
    }
}

function buildAccordion(apiGroups, liClass, display) {
    let html = "";
    if (apiGroups.length > 0) {
        if (apiDocListSize === 1) {
            let apiData = apiGroups[0].list;
            let order = apiGroups[0].order;
            for (let j = 0; j < apiData.length; j++) {
                html += '<li class="'+liClass+'">';
                html += '<a class="dd" href="#_'+order+'_'+apiData[j].order+'_' + apiData[j].link + '">' + apiData[j].order + '.&nbsp;' + apiData[j].desc + '</a>';
                html += '<ul class="sectlevel2" style="'+display+'">';
                let doc = apiData[j].list;
                for (let m = 0; m < doc.length; m++) {
                    let spanString;
                    if (doc[m].deprecated === 'true') {
                        spanString='<span class="line-through">';
                    } else {
                        spanString='<span>';
                    }
                    html += '<li><a href="#_'+order+'_' + apiData[j].order + '_' + doc[m].order + '_' + doc[m].desc + '">' + apiData[j].order + '.' + doc[m].order + '.&nbsp;' + spanString + doc[m].desc + '<span></a> </li>';
                }
                html += '</ul>';
                html += '</li>';
            }
        } else {
            for (let i = 0; i < apiGroups.length; i++) {
                let apiGroup = apiGroups[i];
                html += '<li class="'+liClass+'">';
                html += '<a class="dd" href="#_'+apiGroup.order+'_' + apiGroup.name + '">' + apiGroup.order + '.&nbsp;' + apiGroup.name + '</a>';
                html += '<ul class="sectlevel1">';

                let apiData = apiGroup.list;
                for (let j = 0; j < apiData.length; j++) {
                    html += '<li class="'+liClass+'">';
                    html += '<a class="dd" href="#_'+apiGroup.order+'_'+ apiData[j].order + '_'+ apiData[j].link + '">' +apiGroup.order+'.'+ apiData[j].order + '.&nbsp;' + apiData[j].desc + '</a>';
                    html += '<ul class="sectlevel2" style="'+display+'">';
                    let doc = apiData[j].list;
                    for (let m = 0; m < doc.length; m++) {
                       let spanString;
                       if (doc[m].deprecated === 'true') {
                           spanString='<span class="line-through">';
                       } else {
                           spanString='<span>';
                       }
                       html += '<li><a href="#_'+apiGroup.order+'_' + apiData[j].order + '_' + doc[m].order + '_' + doc[m].desc + '">'+apiGroup.order+'.' + apiData[j].order + '.' + doc[m].order + '.&nbsp;' + spanString + doc[m].desc + '<span></a> </li>';
                   }
                    html += '</ul>';
                    html += '</li>';
                }

                html += '</ul>';
                html += '</li>';
            }
        }
    }
    return html;
}