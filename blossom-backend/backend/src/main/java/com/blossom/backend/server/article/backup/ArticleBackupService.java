package com.blossom.backend.server.article.backup;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.blossom.backend.base.param.ParamEnum;
import com.blossom.backend.base.param.ParamService;
import com.blossom.backend.base.param.pojo.ParamEntity;
import com.blossom.backend.base.user.UserService;
import com.blossom.backend.base.user.pojo.UserEntity;
import com.blossom.backend.server.article.draft.ArticleService;
import com.blossom.backend.server.article.draft.pojo.ArticleEntity;
import com.blossom.backend.server.article.reference.ArticleReferenceService;
import com.blossom.backend.server.article.reference.pojo.ArticleReferenceEntity;
import com.blossom.backend.server.doc.DocService;
import com.blossom.backend.server.doc.DocTypeEnum;
import com.blossom.backend.server.doc.pojo.DocTreeReq;
import com.blossom.backend.server.doc.pojo.DocTreeRes;
import com.blossom.backend.server.picture.PictureService;
import com.blossom.backend.server.picture.pojo.PictureEntity;
import com.blossom.backend.server.utils.ArticleUtil;
import com.blossom.common.base.enums.YesNo;
import com.blossom.common.base.exception.XzException500;
import com.blossom.common.base.util.DateUtils;
import com.blossom.common.base.util.PrimaryKeyUtil;
import com.blossom.common.base.util.SortUtil;
import com.blossom.common.iaas.IaasProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ArticleBackupService {

    @Autowired
    private DocService docService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleReferenceService referenceService;

    @Autowired
    private ParamService paramService;

    @Autowired
    private UserService userService;

    @Autowired
    private PictureService pictureService;

    @Autowired
    private IaasProperties iaasProperties;

    @Autowired
    @Qualifier("taskExecutor")
    private Executor executor;

    public static final String ERROR_MSG = String.format("[文章备份] 备份失败, 未配置备份路径 [%s]", ParamEnum.BACKUP_PATH.name());
    private static final String SEPARATOR = "_";

    /**
     * 查看记录
     *
     * @param userId 用户ID
     * @return 备份记录列表
     */
    public List<BackupFile> listAll(Long userId) {
        final ParamEntity param = paramService.getValue(ParamEnum.BACKUP_PATH);
        XzException500.throwBy(ObjUtil.isNull(param), ERROR_MSG);
        final String rootPath = param.getParamValue();
        XzException500.throwBy(StrUtil.isBlank(rootPath), ERROR_MSG);
        return listAll(rootPath).stream().filter(b -> String.valueOf(userId).equals(b.getUserId())).collect(Collectors.toList());
    }

    /**
     * 将过期的备份文件删除
     *
     * @param rootPath 获取备份文件列表
     */
    public List<BackupFile> listAll(String rootPath) {
        File path = new File(rootPath);
        if (!path.exists()) {
            FileUtil.mkdir(path);
        }
        File[] files = FileUtil.ls(rootPath);
        List<BackupFile> backupFiles = new ArrayList<>();
        for (File file : files) {
            BackupFile backupFile = new BackupFile(file);
            backupFile.setPath(rootPath);
            backupFiles.add(backupFile);
        }
        return backupFiles;
    }

    /**
     * 备份文章的入口
     *
     * @param userId    用户ID
     * @param type      备份文章的类型: MARKDOWN/HTML
     * @param toLocal   是否将图片替换成本地路径, 如果为 YES, 则会替换路径, 并将图片一并放入压缩包
     * @param articleId 文章ID, 指定导出的文章
     * @return 备份编号
     */
    public BackupFile backup(Long userId, final BackupTypeEnum type, final YesNo toLocal, Long articleId) {
        final BackupFile backupFile = new BackupFile(userId, type, toLocal);
        final ParamEntity param = paramService.getValue(ParamEnum.BACKUP_PATH);
        XzException500.throwBy(ObjUtil.isNull(param), ERROR_MSG);
        // 备份文件的地址
        final String rootPath = param.getParamValue();
        XzException500.throwBy(StrUtil.isBlank(rootPath), ERROR_MSG);
        backupFile.setPath(rootPath);

        // 用户信息
        UserEntity user = userService.selectById(userId);

        final File backLogFile = new File(backupFile.getRootPath() + "/" + "log.txt");
        final List<String> backLogs = new ArrayList<>();
        log.info("[文章备份] 开始备份, 本次备份文件名称 [{}], 用户ID [{}]", backupFile.getFilename(), userId);
        backLogs.add("[文章备份] Blossom 备份日志");
        backLogs.add("[文章备份] 开始备份");
        backLogs.add("[文章备份] 备份用户: " + user.getNickName() + "(" + userId + ")");
        backLogs.add("[文章备份] 备份日期: " + DateUtils.now());
        expire(rootPath, backLogs);

        executor.execute(() -> {
            final long start = System.currentTimeMillis();
            List<DocTreeRes> articles = getArticles(userId, articleId);
            if (CollUtil.isEmpty(articles)) {
                FileUtil.writeLines(backLogs, backLogFile, StandardCharsets.UTF_8);
                ZipUtil.zip(backupFile.getRootPath());
                deleteFolder(backupFile.getRootPath());
            }

            String logCount = String.format("[文章备份] 文章篇数: %s 篇", articles.size());
            log.info(logCount);
            backLogs.add(logCount);

            List<Long> ids = articles.stream().map(DocTreeRes::getI).collect(Collectors.toList());
            List<ArticleEntity> allContents = articleService.listAllContent(ids, type.name());
            Map<Long, ArticleEntity> markdowns = allContents.stream().collect(Collectors.toMap(ArticleEntity::getId, art -> art));

            int index = 1;
            int indexLen = String.valueOf(articles.size()).length();
            int versionLen = String.valueOf(allContents.stream()
                    .map(ArticleEntity::getVersion).max(SortUtil.intSort).orElse(1)).length();
            int idLen = String.valueOf(allContents.stream()
                    .map(ArticleEntity::getId).max(SortUtil.longSort).orElse((1L))).length();

            // 记录已经保存的文件
            Set<String> exists = new HashSet<>();

            backLogs.add("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ ↓↓ 文章列表 ↓↓ ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            backLogs.add("┃ 排序 [ID] [版本] [时间] 文章路径");
            backLogs.add("┠────────────────────────────────────────────────────────────────────────────");
            for (DocTreeRes article : articles) {
                String name = clearPath(article.getN());
                // 处理文章或文件夹重名的情况
                if (exists.contains(name)) {
                    name = name + "_" + article.getI();
                    if (exists.contains(name)) {
                        name = name + "_" + PrimaryKeyUtil.nextId();
                    }
                }
                exists.add(name);
                // 创建文章 file
                File file = new File(backupFile.getRootPath() + "/" + name + getArticleSuffix(type));
                // 导出的文章正文
                ArticleEntity articleDetail = markdowns.get(article.getI());
                if (articleDetail == null) {
                    continue;
                }
                // 查正文不查名称, 名称从树状列表中获取
                articleDetail.setName(article.getN());

                // 文章 markdown 内容
                String content = getContentByType(articleDetail, type, user);
                content = formatContent(content, toLocal, article.getI(), article.getN());
                String id = String.valueOf(articleDetail.getId());
                String version = String.valueOf(articleDetail.getVersion());

                FileUtil.writeBytes(content.getBytes(StandardCharsets.UTF_8), file);

                // 排序 [id] [version] [时间] 路径
                String backArticleLog = String.format("┃ %s [%s] [%s] [%s] %s",
                        StrUtil.fillBefore(String.valueOf(index), ' ', indexLen),
                        StrUtil.fillBefore(String.valueOf(id), ' ', idLen),
                        StrUtil.fillBefore(String.valueOf(version), ' ', versionLen),
                        DateUtils.toYMDHMS(articleDetail.getUpdTime()),
                        article.getN()
                );

                backLogs.add(backArticleLog);
                index++;
            }
            backLogs.add("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ ↑↑ 文章列表 ↑↑ ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            if (toLocal == YesNo.YES) {
                backLogs.add("");
                if (articleId != null) {
                    List<ArticleReferenceEntity> refs = referenceService.listPics(articleId);
                    PictureEntity where = new PictureEntity();
                    where.setUrls(refs.stream().map(ArticleReferenceEntity::getTargetUrl).collect(Collectors.toList()));
                    List<PictureEntity> pics = pictureService.listAll(where);
                    backLogs.add("[图片备份] 图片个数: " + pics.size());
                    backLogs.add("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ ↓↓ 图片列表 ↓↓ ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                    for (PictureEntity pic : pics) {
                        backLogs.add("┃ " + pic.getPathName());
                        FileUtil.copy(
                                pic.getPathName(),
                                backupFile.getRootPath() + "/" + pic.getPathName(),
                                true);
                    }
                }
                // 备份全部图片
                else {
                    List<File> files = FileUtil.loopFiles(FileUtil.newFile(iaasProperties.getBlos().getDefaultPath() + "/U" + userId), null);
                    backLogs.add("[图片备份] 图片个数: " + files.size());
                    backLogs.add("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ ↓↓ 图片列表 ↓↓ ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                    for (File file : files) {
                        backLogs.add(file.getPath());
                    }
                    FileUtil.copy(
                            iaasProperties.getBlos().getDefaultPath() + "/U" + userId,
                            backupFile.getRootPath() + "/" + iaasProperties.getBlos().getDefaultPath(),
                            true);

                }
                backLogs.add("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ ↑↑ 图片列表 ↑↑ ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

            }

            String logEnd = String.format("[文章备份] 备份结束, 本次备份用时:%s", System.currentTimeMillis() - start + " ms");
            log.info(logEnd);
            backLogs.add(logEnd);


            FileUtil.writeLines(backLogs, backLogFile, StandardCharsets.UTF_8);
            ZipUtil.zip(backupFile.getRootPath());
            deleteFolder(backupFile.getRootPath());
        });
        return backupFile;
    }

    /**
     * 将过期的备份文件删除
     *
     * @param rootPath 备份根路径
     */
    private void expire(String rootPath, List<String> backLog) {
        ParamEntity param = paramService.getValue(ParamEnum.BACKUP_EXP_DAYS);
        if (param == null) {
            return;
        }
        final long expireDay;
        try {
            expireDay = Long.parseLong(param.getParamValue());
        } catch (NumberFormatException e) {
            throw new XzException500(String.format("文章过期日期 [%s] 配置错误", ParamEnum.BACKUP_EXP_DAYS.name()));
        }
        List<BackupFile> backupFiles = listAll(rootPath);
        for (BackupFile backupFile : backupFiles) {
            if (StrUtil.isBlank(backupFile.getDate())) {
                continue;
            }
            Date today = DateUtils.date();
            Date bakDate = DateUtils.parse(backupFile.getDate(), DatePattern.PURE_DATE_FORMAT);
            long gap = DateUtils.betweenDay(bakDate, today, true);
            if (gap > expireDay) {
                String logStr = String.format("[文章备份] 备份文件: %s 已过期, 即将删除", backupFile.getFilename());
                backLog.add(logStr);
                log.info(logStr);
                if (backupFile.getFile() != null) {
                    backupFile.getFile().delete();
                }
            }
        }
    }

    /**
     * 备份后删除文件夹, 只保留压缩包
     *
     * @param path 文件夹路径
     */
    private void deleteFolder(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            FileUtil.del(file);
        }
    }

    /**
     * 获取所有文章
     *
     * @param userId 用户ID
     * @return 文章列表, 虽然是 DocTreeRes 对象, 但不是树状结构
     */
    private List<DocTreeRes> getArticles(Long userId, Long articleId) {
        DocTreeReq req = new DocTreeReq();
        req.setUserId(userId);
        req.setArticleId(articleId);
        List<DocTreeRes> docs = docService.listTree(req);
        List<DocTreeRes> articles = new ArrayList<>();
        Set<String> exists = new HashSet<>();
        findArticle("", docs, articles, exists);
        return articles;
    }

    /**
     * 递归拼接文章的名称, 文章的名称会被拼接成文章的所有上级文件夹 + 文章名称
     *
     * @param prefix   上级文件夹名称
     * @param docs     文章树状列表
     * @param articles 拼接结果列表, 虽然是 DocTreeRes 对象, 但不是树状结构
     * @param exists   判断文件夹是否重名
     */
    private void findArticle(String prefix, List<DocTreeRes> docs, List<DocTreeRes> articles, Set<String> exists) {
        if (CollUtil.isEmpty(docs)) {
            return;
        }
        for (DocTreeRes doc : docs) {
            if (exists.contains(doc.getN())) {
                doc.setN(doc.getN() + "_" + doc.getI());
            }
            exists.add(doc.getN());
            doc.setN(prefix + "/" + doc.getN());
            if (CollUtil.isNotEmpty(doc.getChildren())) {
                findArticle(doc.getN(), doc.getChildren(), articles, exists);
            }
            if (doc.getTy().equals(DocTypeEnum.A.getType())) {
                articles.add(doc);
            }
        }
    }

    /**
     * 根据类型判断是获取 markdown 内容还是 html 内容
     *
     * @param article 文章
     * @param type    类型
     * @return 对应的内容
     */
    private String getContentByType(ArticleEntity article, BackupTypeEnum type, UserEntity user) {
        if (type == BackupTypeEnum.MARKDOWN) {
            return StrUtil.isBlank(article.getMarkdown()) ? "文章无内容" : article.getMarkdown();
        } else if (type == BackupTypeEnum.HTML) {
            ArticleEntity export = new ArticleEntity();
            export.setName(article.getName().substring(article.getName().lastIndexOf("/")));
            export.setHtml(article.getHtml());
            return ArticleUtil.toHtml(article, user);
        }
        return "";
    }

    /**
     * 格式化文章正文，主要将图片路径替换为本地路径
     *
     * @param content     文章正文
     * @param toLocal     是否映射为本地路径
     * @param articleId   文章ID
     * @param articleName 文章名称，包含递归后的所有上级菜单名称，用于计算文章向上级文件夹查找的次数
     * @return 返回替换后的正文内容
     */
    private String formatContent(String content, YesNo toLocal, Long articleId, String articleName) {
        if (toLocal == YesNo.NO) {
            return content;
        }

        List<ArticleReferenceEntity> refs = referenceService.listPics(articleId);
        final String domain = iaasProperties.getBlos().getDomain();

        // 计算字符出现的次数
        int separatorCount = countChar(articleName, '/');

        StringBuilder parent = new StringBuilder();

        // 起始不为0, 需要排除掉备份目录
        for (int i = 1; i < separatorCount; i++) {
            parent.append("../");
        }

        // 将 domain 替换成本地映射
        for (ArticleReferenceEntity ref : refs) {
            if (!ref.getTargetUrl().startsWith(domain)) {
                continue;
            }
            String localPath = parent.substring(0, parent.length() > 0 ? parent.length() - 1 : 0) + ref.getTargetUrl().replace(domain, "");
            content = content.replaceAll(ref.getTargetUrl(), localPath);
            System.out.println(localPath);
        }
        return content;

    }

    private static int countChar(String str, char target) {
        int times = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == target) {
                times++;
            }
        }
        return times;
    }

    /**
     * 获取文章的后缀名
     *
     * @param type 类型
     */
    private String getArticleSuffix(BackupTypeEnum type) {
        if (type == BackupTypeEnum.MARKDOWN) {
            return ".md";
        } else if (type == BackupTypeEnum.HTML) {
            return ".html";
        }
        return ".txt";
    }

    private static String clearPath(String path) {
        return path.replaceAll(" ", "")
                .replaceAll("-", "-")
                .replaceAll(":", "")
                .replaceAll("\\.", "")
                .replaceAll("\\*", "")
                .replaceAll("\\?", "")
                .replaceAll("\"", "")
                .replaceAll("<", "")
                .replaceAll(">", "")
                .replaceAll("\\|", "")
                ;
    }

    /**
     * 备份文件
     */
    @Data
    public static class BackupFile {
        /**
         * 用户ID
         */
        private String userId;
        /**
         * 备份日期 YYYYMMDD
         *
         * @mock 20230101
         */
        private String date;
        /**
         * 备份时间 HHMMSS
         *
         * @mock 123001
         */
        private String time;
        /**
         * 备份的日期和时间, yyyy-MM-dd HH:mm:ss
         */
        private Date datetime;
        /**
         * 备份包的名称
         */
        private String filename;
        /**
         * 备份包路径
         */
        private String path;
        /**
         * 本地文件
         */
        @JsonIgnore
        private File file;
        /**
         * 文件大小
         */
        private Long fileLength;

        /**
         * 通过本地备份文件初始化
         *
         * @param file 本地备份文件
         */
        public BackupFile(File file) {
            build(FileUtil.getPrefix(file.getName()));
            this.file = file;
            this.fileLength = file.length();
        }

        /**
         * 指定用户的开始备份
         *
         * @param userId 用户ID
         */
        public BackupFile(Long userId, BackupTypeEnum type, YesNo toLocal) {
            String filename = String.format("%s_%s_%s", buildFilePrefix(type, toLocal), userId, DateUtils.toYMDHMS_SSS(System.currentTimeMillis()));
            filename = filename.replaceAll(" ", SEPARATOR)
                    .replaceAll("-", "")
                    .replaceAll(":", "")
                    .replaceAll("\\.", SEPARATOR);
            build(filename);
        }

        private static String buildFilePrefix(BackupTypeEnum type, YesNo toLocal) {
            String prefix = "B";
            if (type == BackupTypeEnum.MARKDOWN) {
                prefix += "M";
            } else if (type == BackupTypeEnum.HTML) {
                prefix += "H";
            }

            if (toLocal == YesNo.YES) {
                prefix += "L";
            } else if (toLocal == YesNo.NO) {
                prefix += "N";
            }

            return prefix;
        }

        private void build(String filename) {
            this.filename = filename;
            String[] tags = filename.split(SEPARATOR);
            if (tags.length < 5) {
                return;
            }
            this.userId = tags[1];
            this.date = tags[2];
            this.time = tags[3];
            this.datetime = DateUtil.parse(this.date + this.time);
        }

        /**
         * 获取备份文件的路径, 由备份路径 + 本次备份名称构成
         */
        public String getRootPath() {
            return this.path + "/" + this.filename;
        }

    }

}
