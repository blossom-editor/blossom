-- Copy from /blossom/script/sql/blossom.sql

-- ----------------------------
-- Table structure for base_sys_param
-- ----------------------------
CREATE TABLE IF NOT EXISTS `base_sys_param`  (
                                   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '参数ID',
                                   `param_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '参数名称',
                                   `param_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '参数值',
                                   `param_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '参数说明',
                                   `open_state` int NOT NULL COMMENT '开放状态 [YesNo]',
                                   `cre_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   `upd_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                   PRIMARY KEY (`id`) USING BTREE,
                                   UNIQUE INDEX `unq_param_name`(`param_name`) USING BTREE COMMENT '参数名称唯一'
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统参数' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of base_sys_param
-- ----------------------------
-- INSERT INTO `base_sys_param` VALUES (1, 'WEB_ARTICLE_URL', 'https://www.domain.com/blossom/#/articles?articleId=', '博客端文章地址,用于PC端直接调往WEB端阅读文章', 1, '2023-04-04 08:20:57', '2023-08-06 22:19:07');
-- INSERT INTO `base_sys_param` VALUES (3, 'ARTICLE_LOG_EXP_DAYS', '30', '文章修改记录保存天数, 超过该天数将被删除', 1, '2023-08-02 17:46:58', '2023-08-02 18:03:43');
-- INSERT INTO `base_sys_param` VALUES (11, 'HEFENG_KEY', 'ABC', '和风天气的KEY', 1, '2023-07-31 19:28:54', '2023-08-06 22:19:11');
-- INSERT INTO `base_sys_param` VALUES (21, 'GITEE_ACCESS_TOKEN', 'ABC', '[过时配置]GITEE API 的访问 token', 1, '2023-07-31 20:12:05', '2023-08-06 22:20:12');
-- INSERT INTO `base_sys_param` VALUES (31, 'BACKUP_PATH', '/home/bl/backup/', '备份路径, 参考格式: /home/bl/backup/', 1, '2023-08-26 16:59:11', '2023-08-26 19:32:38');
-- INSERT INTO `base_sys_param` VALUES (32, 'BACKUP_EXP_DAYS', '7', '备份过期天数', 1, '2023-08-26 19:21:43', '2023-08-26 19:21:43');
-- INSERT INTO `base_sys_param` VALUES (900, 'SERVER_JWT_SECRET', '96cae35ce8a9b0244178bf28e4966c2ce1b8385723a96a6b838858cdd6ca0a1e', 'JWT加密字符串', 1, '2023-08-31 18:39:11', '2023-08-31 18:52:50');
-- INSERT INTO `base_sys_param` VALUES (901, 'SERVER_MACHINE_EXPIRE', '2024-01-01', '过期时间 - 服务器', 1, '2023-08-31 17:29:55', '2023-08-31 17:35:10');
-- INSERT INTO `base_sys_param` VALUES (902, 'SERVER_DOMAIN_EXPIRE', '2024-01-01', '过期时间 - 域名', 1, '2023-08-31 17:30:45', '2023-08-31 17:35:12');
-- INSERT INTO `base_sys_param` VALUES (903, 'SERVER_HTTPS_EXPIRE', '2024-01-01', '过期时间 - HTTPS 证书', 1, '2023-08-31 17:31:06', '2023-08-31 17:35:15');
-- INSERT INTO `base_sys_param` VALUES (904, 'SERVER_DATABASE_EXPIRE', '2024-01-01', '过期时间 - 数据库', 1, '2023-08-31 17:31:33', '2023-08-31 17:35:17');

INSERT INTO base_sys_param (id, param_name, param_value, param_desc, open_state, cre_time, upd_time)
SELECT 1,
       'WEB_ARTICLE_URL',
       'https://www.domain.com/blossom/#/articles?articleId=',
       '博客端文章地址,用于PC端直接调往WEB端阅读文章',
       1,
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1
                  FROM base_sys_param
                  WHERE id = 1);
INSERT INTO base_sys_param (id, param_name, param_value, param_desc, open_state, cre_time, upd_time)
SELECT 3,
       'ARTICLE_LOG_EXP_DAYS',
       '30',
       '文章修改记录保存天数, 超过该天数将被删除',
       1,
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1
                  FROM base_sys_param
                  WHERE id = 3);
INSERT INTO base_sys_param (id, param_name, param_value, param_desc, open_state, cre_time, upd_time)
SELECT 11,
       'HEFENG_KEY',
       'ABC',
       '和风天气的KEY',
       1,
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1
                  FROM base_sys_param
                  WHERE id = 11);
INSERT INTO base_sys_param (id, param_name, param_value, param_desc, open_state, cre_time, upd_time)
SELECT 21,
       'GITEE_ACCESS_TOKEN',
       'ABC',
       '[过时配置]GITEE API 的访问 token',
       1,
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1
                  FROM base_sys_param
                  WHERE id = 21);
INSERT INTO base_sys_param (id, param_name, param_value, param_desc, open_state, cre_time, upd_time)
SELECT 31,
       'BACKUP_PATH',
       '/home/bl/backup/',
       '备份路径, 参考格式: /home/bl/backup/',
       1,
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1
                  FROM base_sys_param
                  WHERE id = 31);
INSERT INTO base_sys_param (id, param_name, param_value, param_desc, open_state, cre_time, upd_time)
SELECT 32,
       'BACKUP_EXP_DAYS',
       '7',
       '备份过期天数',
       1,
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1
                  FROM base_sys_param
                  WHERE id = 32);
INSERT INTO base_sys_param (id, param_name, param_value, param_desc, open_state, cre_time, upd_time)
SELECT 900,
       'SERVER_JWT_SECRET',
       '96cae35ce8a9b0244178bf28e4966c2ce1b8385723a96a6b838858cdd6ca0a1e',
       'JWT加密字符串',
       1,
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1
                  FROM base_sys_param
                  WHERE id = 900);
INSERT INTO base_sys_param (id, param_name, param_value, param_desc, open_state, cre_time, upd_time)
SELECT 901,
       'SERVER_MACHINE_EXPIRE',
       '2024-01-01',
       '过期时间 - 服务器',
       1,
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1
                  FROM base_sys_param
                  WHERE id = 901);
INSERT INTO base_sys_param (id, param_name, param_value, param_desc, open_state, cre_time, upd_time)
SELECT 902,
       'SERVER_DOMAIN_EXPIRE',
       '2024-01-01',
       '过期时间 - 域名',
       1,
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1
                  FROM base_sys_param
                  WHERE id = 902);
INSERT INTO base_sys_param (id, param_name, param_value, param_desc, open_state, cre_time, upd_time)
SELECT 903,
       'SERVER_HTTPS_EXPIRE',
       '2024-01-01',
       '过期时间 - HTTPS 证书',
       1,
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1
                  FROM base_sys_param
                  WHERE id = 903);
INSERT INTO base_sys_param (id, param_name, param_value, param_desc, open_state, cre_time, upd_time)
SELECT 904,
       'SERVER_DATABASE_EXPIRE',
       '2024-01-01',
       '过期时间 - 数据库',
       1,
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1
                  FROM base_sys_param
                  WHERE id = 904);


-- ----------------------------
-- Table structure for base_user
-- ----------------------------
CREATE TABLE IF NOT EXISTS `base_user`  (
                              `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                              `type` tinyint(1) NOT NULL DEFAULT 2 COMMENT '用户类型: 1:管理员; 2:普通用户; 3:只读用户;',
                              `username` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '用户名',
                              `phone` varchar(13) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '用户手机号',
                              `password` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '用户密码',
                              `salt` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '密码加盐',
                              `nick_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '昵称',
                              `real_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '真实姓名',
                              `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '用户头像',
                              `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '备注',
                              `cre_by` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '0,SYS' COMMENT '创建人ID，名称',
                              `cre_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `upd_by` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '0,SYS' COMMENT '修改人ID，名称',
                              `upd_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                              `del_by` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '0,SYS' COMMENT '删除人ID，名称',
                              `del_time` bigint NOT NULL DEFAULT 0 COMMENT '删除时间',
                              `location` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '101100101' COMMENT '和风天气的位置, 官方文档:https://github.com/qwd/LocationList/blob/master/China-City-List-latest.csv',
                              PRIMARY KEY (`id`) USING BTREE,
                              UNIQUE INDEX `unq_user_username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10002 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '用户' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of base_user
-- ----------------------------
-- INSERT INTO `base_user` VALUES (1, 1, 'blos', '', '$2a$10$SgMx8T/06595PEq3EA9US.ja1oHxpIDG/XnERmBXS.wYS8qbxAGDa', 'UVeESP5NgXwb8JmjCHUK', '用户', 'blos', '', '预设管理员账号, 用户名密码都是 blos', '0,SYS', '2023-08-04 16:48:28', '0,SYS', '2023-08-06 22:17:35', '0', 0, '101100101');
INSERT INTO base_user (id, type, username, phone, password, salt, nick_name, real_name, remark, cre_by, cre_time, upd_by, upd_time, location)
SELECT 1,
       1,
       'blos',
       '',
       '$2a$10$SgMx8T/06595PEq3EA9US.ja1oHxpIDG/XnERmBXS.wYS8qbxAGDa',
       'UVeESP5NgXwb8JmjCHUK',
       '用户',
       'blos',
       '预设管理员账号, 用户名密码都是 blos',
       '0,SYS',
       CURRENT_TIMESTAMP,
       '0,SYS',
       CURRENT_TIMESTAMP,
       '101100101'
    WHERE NOT EXISTS (SELECT 1
                  FROM base_user
                  WHERE id = 1);
-- ----------------------------
-- Table structure for blossom_article
-- ----------------------------
CREATE TABLE IF NOT EXISTS `blossom_article`  (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                    `pid` bigint NOT NULL COMMENT '文件夹ID',
                                    `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '文章名称',
                                    `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '文章图标',
                                    `tags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '标签集合',
                                    `sort` int NOT NULL DEFAULT 1 COMMENT '排序',
                                    `cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '封面',
                                    `describes` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '描述',
                                    `star_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'star状态',
                                    `open_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '公开状态',
                                    `open_version` int NOT NULL DEFAULT 0 COMMENT '公开版本',
                                    `pv` int NOT NULL DEFAULT 0 COMMENT '页面的查看数',
                                    `uv` int NOT NULL DEFAULT 0 COMMENT '独立的访问次数,每日IP重置',
                                    `likes` int NOT NULL DEFAULT 0 COMMENT '点赞数',
                                    `words` int NOT NULL DEFAULT 0 COMMENT '文章字数',
                                    `version` int NOT NULL DEFAULT 0 COMMENT '版本',
                                    `color` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '颜色',
                                    `toc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '目录解析',
                                    `markdown` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT 'Markdown 内容',
                                    `html` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT 'Html内容',
                                    `cre_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `upd_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                    `user_id` bigint NOT NULL DEFAULT 1 COMMENT '用户ID',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20153 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '文章，Article' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blossom_article
-- ----------------------------

-- ----------------------------
-- Table structure for blossom_article_log
-- ----------------------------
CREATE TABLE IF NOT EXISTS `blossom_article_log`  (
                                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                        `article_id` bigint NOT NULL COMMENT '文章ID',
                                        `version` int NOT NULL DEFAULT 0 COMMENT '版本',
                                        `markdown` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '文章内容',
                                        `cre_time` datetime NOT NULL COMMENT '修改日期',
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 146 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '文章记录，ArticleLog' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blossom_article_log
-- ----------------------------

-- ----------------------------
-- Table structure for blossom_article_open
-- ----------------------------
CREATE TABLE IF NOT EXISTS `blossom_article_open`  (
                                         `id` bigint NOT NULL COMMENT '文章ID',
                                         `pid` bigint NOT NULL COMMENT '文件夹ID',
                                         `words` int NOT NULL COMMENT '字数',
                                         `open_version` int NOT NULL DEFAULT 1 COMMENT '版本',
                                         `open_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '公开时间',
                                         `sync_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '同步时间',
                                         `toc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '目录',
                                         `markdown` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT 'Markdown 内容',
                                         `html` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT 'Html内容',
                                         `user_id` bigint NOT NULL DEFAULT 1 COMMENT '用户ID',
                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '公开文章，ArticleOpen' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blossom_article_open
-- ----------------------------

-- ----------------------------
-- Table structure for blossom_article_reference
-- ----------------------------
CREATE TABLE IF NOT EXISTS `blossom_article_reference` (
                                             `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                             `source_id` bigint NOT NULL COMMENT '文章ID',
                                             `source_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '文章名称',
                                             `target_Id` bigint NOT NULL COMMENT '引用文章ID',
                                             `target_name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '引用名称',
                                             `target_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '图片链接',
                                             `type` tinyint NOT NULL COMMENT '引用类型: 10:图片; 11:文章; 21:外部文章',
                                             `user_id` bigint NOT NULL DEFAULT '1' COMMENT '用户ID',
                                             PRIMARY KEY (`id`) USING BTREE,
                                             KEY `idx_article_ref_sourceid` (`source_id`) USING BTREE COMMENT 'source id',
                                             KEY `idx_article_ref_targetid` (`target_Id`) USING BTREE COMMENT 'target id'
) ENGINE=InnoDB AUTO_INCREMENT=100000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of blossom_article_reference
-- ----------------------------

-- ----------------------------
-- Table structure for blossom_article_view
-- ----------------------------
CREATE TABLE IF NOT EXISTS `blossom_article_view` (
                                        `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                        `article_id` bigint NOT NULL COMMENT '文章ID',
                                        `type` char(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '事件类型 1:uv; 2:like',
                                        `ip` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '地址,IPV4',
                                        `user_agent` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '设备',
                                        `country` varchar(50) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '国家',
                                        `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '省',
                                        `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '市',
                                        `cre_day` date NOT NULL COMMENT '日期 yyyy-MM-dd',
                                        `cre_time` datetime NOT NULL COMMENT '日期',
                                        PRIMARY KEY (`id`) USING BTREE,
                                        KEY `idx_view_articleid` (`article_id`) USING BTREE COMMENT '文章ID',
                                        KEY `idx_view_ip` (`ip`) USING BTREE COMMENT 'IP',
                                        KEY `idx_view_creday` (`cre_day`) USING BTREE COMMENT '日期'
) ENGINE=InnoDB AUTO_INCREMENT=465 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='文章访问记录，ArticleView';

-- ----------------------------
-- Records of blossom_article_view
-- ----------------------------

-- ----------------------------
-- Table structure for blossom_folder
-- ----------------------------
CREATE TABLE IF NOT EXISTS `blossom_folder`  (
                                   `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
                                   `pid` bigint UNSIGNED NOT NULL COMMENT '父id',
                                   `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '文件夹名称',
                                   `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '图标',
                                   `tags` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '标签',
                                   `open_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '开放状态',
                                   `sort` int UNSIGNED NOT NULL DEFAULT 1 COMMENT '排序',
                                   `cover` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '封面图片',
                                   `color` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '颜色',
                                   `describes` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '备注',
                                   `store_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '/' COMMENT '存储地址',
                                   `subject_words` int NOT NULL DEFAULT 0 COMMENT '专题字数',
                                   `subject_upd_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '专题的最后修改时间',
                                   `type` tinyint(1) NOT NULL DEFAULT 1 COMMENT '1:文章;2:图片',
                                   `cre_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `upd_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                   `user_id` bigint NOT NULL DEFAULT 1 COMMENT '用户ID',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12035 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '文件夹，Folder' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blossom_folder
-- ----------------------------

-- ----------------------------
-- Table structure for blossom_note
-- ----------------------------
CREATE TABLE IF NOT EXISTS `blossom_note`  (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `content` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '内容',
                                 `top` tinyint(1) NOT NULL DEFAULT 0 COMMENT '置顶',
                                 `top_time` datetime NULL DEFAULT NULL COMMENT '置顶时间',
                                 `cre_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `user_id` bigint NOT NULL DEFAULT 1 COMMENT '用户ID',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blossom_note
-- ----------------------------

-- ----------------------------
-- Table structure for blossom_picture
-- ----------------------------
CREATE TABLE IF NOT EXISTS `blossom_picture`  (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                    `pid` bigint NOT NULL DEFAULT -1 COMMENT '文件夹ID',
                                    `source_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '原文件名',
                                    `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '文件名',
                                    `path_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '文件路径',
                                    `url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '文件访问url',
                                    `rate` tinyint NOT NULL DEFAULT 0 COMMENT '评分 {0,1,2,3,4,5}',
                                    `star_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '收藏 0:否,1:是',
                                    `suffix` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '文件后缀',
                                    `size` bigint NOT NULL DEFAULT 0 COMMENT '文件大小',
                                    `cre_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建日期',
                                    `user_id` bigint NOT NULL DEFAULT 1 COMMENT '用户ID',
                                    PRIMARY KEY (`id`) USING BTREE,
                                    UNIQUE INDEX `unq_pic_url`(`url`) USING BTREE COMMENT '链接唯一',
                                    UNIQUE INDEX `unq_pic_pathname`(`path_name`) USING BTREE COMMENT '路径唯一'
) ENGINE = InnoDB AUTO_INCREMENT = 305774931235323969 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '图片，Picture' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blossom_picture
-- ----------------------------

-- ----------------------------
-- Table structure for blossom_plan
-- ----------------------------
CREATE TABLE IF NOT EXISTS `blossom_plan`  (
                                 `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                 `group_id` bigint NOT NULL COMMENT '分组ID',
                                 `type` tinyint(1) NOT NULL COMMENT '计划类型: daily, day',
                                 `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '标题',
                                 `content` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '内容',
                                 `plan_month` varchar(7) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '日期所在月份',
                                 `plan_date` date NULL DEFAULT NULL COMMENT '日期: day',
                                 `plan_start_time` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '开始时间: daily, day',
                                 `plan_end_time` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '结束时间',
                                 `color` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '颜色',
                                 `position` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '该计划在该组计划的位置 head, tail, all',
                                 `img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '图片名称, 或图片地址',
                                 `cre_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
                                 `user_id` bigint NOT NULL DEFAULT 1 COMMENT '用户ID',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 142 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '计划，Plan' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blossom_plan
-- ----------------------------

-- ----------------------------
-- Table structure for blossom_stat
-- ----------------------------
CREATE TABLE IF NOT EXISTS `blossom_stat`  (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `type` tinyint(1) NOT NULL COMMENT '统计类型: 1:每日编辑文章数; 2:每月总字数;',
                                 `stat_date` date NOT NULL COMMENT '统计日期',
                                 `stat_value` int NOT NULL DEFAULT 0 COMMENT '统计数值',
                                 `user_id` bigint NOT NULL DEFAULT 1 COMMENT '用户ID',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 218 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blossom_stat
-- ----------------------------

-- ----------------------------
-- Table structure for blossom_web
-- ----------------------------
CREATE TABLE IF NOT EXISTS `blossom_web`  (
                                `id` int NOT NULL AUTO_INCREMENT,
                                `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '网页名称',
                                `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '网页url',
                                `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '图标',
                                `img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '图片, 图片的优先级高于图标',
                                `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '类型 ',
                                `sort` int NOT NULL DEFAULT 1 COMMENT '排序',
                                `cre_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `user_id` bigint NOT NULL DEFAULT 1 COMMENT '用户ID',
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 292 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '[FS] 网站收藏' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blossom_web
-- ----------------------------

-- ----------------------------
-- Table structure for blossom_todo
-- ----------------------------
CREATE TABLE IF NOT EXISTS `blossom_todo` (
                                `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                `todo_id` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '事项ID',
                                `todo_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '事项名称',
                                `todo_status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '事项状态 1:未完成 | 2:完成 | 9:作废',
                                `todo_type` tinyint(1) NOT NULL DEFAULT '10' COMMENT '事项类型 10:每日待办事项 | 20:阶段性事项',
                                `task_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '任务名称',
                                `task_content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '任务内容',
                                `task_tags` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '便签',
                                `task_status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT 'WAITING' COMMENT '任务状态 WAIT | PROC | DONE',
                                `dead_line` varchar(100) COLLATE utf8mb4_bin DEFAULT '' COMMENT '截止日期',
                                `start_time` datetime DEFAULT NULL COMMENT '开始日期',
                                `end_time` datetime DEFAULT NULL COMMENT '结束日期',
                                `color` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '颜色',
                                `process` tinyint(1) NOT NULL DEFAULT '0' COMMENT '进度 0 ~ 100',
                                `user_id` bigint DEFAULT NULL COMMENT '用户ID',
                                `cre_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                PRIMARY KEY (`id`),
                                KEY `idx_todo_todoid` (`todo_id`) COMMENT 'todoid 索引',
                                KEY `idx_todo_userid` (`user_id`) USING BTREE COMMENT 'userid 索引',
                                KEY `idx_todo_cretime` (`cre_time`) USING BTREE COMMENT '创建时间'
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='待办事项，Todo';
SET FOREIGN_KEY_CHECKS = 1;

