package com.blossom.backend.server.article.backup;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.blossom.backend.base.param.ParamEnum;
import com.blossom.backend.base.param.ParamService;
import com.blossom.backend.base.param.pojo.ParamEntity;
import com.blossom.backend.server.article.draft.ArticleService;
import com.blossom.backend.server.article.draft.pojo.ArticleEntity;
import com.blossom.backend.server.doc.DocService;
import com.blossom.backend.server.doc.DocTypeEnum;
import com.blossom.backend.server.doc.pojo.DocTreeReq;
import com.blossom.backend.server.doc.pojo.DocTreeRes;
import com.blossom.common.base.exception.XzException500;
import com.blossom.common.base.util.DateUtils;
import com.blossom.common.base.util.SortUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
    private ParamService paramService;

    @Autowired
    @Qualifier("taskExecutor")
    private Executor executor;

    private static final String ERROR_MSG = String.format("[文章备份] 备份失败, 未配置备份路径 [%s]", ParamEnum.BACKUP_PATH.name());
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
     * 备份某个用户的文章
     *
     * @param userId 用户ID
     * @return 备份编号
     */
    public BackupFile backup(Long userId) {
        final BackupFile backupFile = new BackupFile(userId);
        final ParamEntity param = paramService.getValue(ParamEnum.BACKUP_PATH);
        XzException500.throwBy(ObjUtil.isNull(param), ERROR_MSG);
        final String rootPath = param.getParamValue();
        XzException500.throwBy(StrUtil.isBlank(rootPath), ERROR_MSG);
        backupFile.setPath(rootPath);

        final File backLogFile = new File(backupFile.getRootPath() + "/" + "log.txt");
        final List<String> backLogs = new ArrayList<>();
        log.info("[文章备份] 开始备份, 本次备份文件名称 [{}], 用户ID [{}]", backupFile.getFilename(), userId);
        backLogs.add("[文章备份] Blossom 备份日志");
        backLogs.add("[文章备份] 开始备份");
        backLogs.add("[文章备份] 备份用户: " + userId);
        backLogs.add("[文章备份] 备份日期: " + DateUtils.now());
        expire(rootPath, backLogs);
        executor.execute(() -> {
            final long start = System.currentTimeMillis();
            List<DocTreeRes> articles = getArticles(userId);
            if (CollUtil.isEmpty(articles)) {
                FileUtil.writeLines(backLogs, backLogFile, StandardCharsets.UTF_8);
                ZipUtil.zip(backupFile.getRootPath());
                deleteFolder(backupFile.getRootPath());
            }

            String logCount = String.format("[文章备份] 文章篇数: %s 篇", articles.size());
            log.info(logCount);
            backLogs.add(logCount);

            List<Long> ids = articles.stream().map(DocTreeRes::getI).collect(Collectors.toList());
            List<ArticleEntity> allContents = articleService.listAllContent(ids);
            Map<Long, ArticleEntity> markdowns = allContents.stream().collect(Collectors.toMap(ArticleEntity::getId, art -> art));

            int index = 1;
            int indexLen = String.valueOf(articles.size()).length();
            int versionLen = String.valueOf(allContents.stream()
                    .map(ArticleEntity::getVersion).max(SortUtil.intSort).orElse(1)).length();
            int idLen = String.valueOf(allContents.stream()
                    .map(ArticleEntity::getId).max(SortUtil.longSort).orElse((1L))).length();

            backLogs.add("============================== ↓↓ 文本文章列表 ↓↓ ==============================");
            backLogs.add("排序 [ID] [版本] [时间] 文章路径");
            backLogs.add("-----------------------------------------------------------------------------");
            for (DocTreeRes article : articles) {
                File file = new File(backupFile.getRootPath() + "/" + clearPath(article.getN()) + ".md");
                ArticleEntity articleDetail = markdowns.get(article.getI());
                if (articleDetail == null) {
                    continue;
                }
                // 文章 markdown 内容
                String content = StrUtil.isBlank(articleDetail.getMarkdown()) ? "" : articleDetail.getMarkdown();
                String id = String.valueOf(articleDetail.getId());
                String version = String.valueOf(articleDetail.getVersion());

                FileUtil.writeBytes(content.getBytes(StandardCharsets.UTF_8), file);

                // 排序 [id] [version] [时间] 路径
                String backArticleLog = String.format("%s [%s] [%s] [%s] %s",
                        StrUtil.fillBefore(String.valueOf(index), ' ', indexLen),
                        StrUtil.fillBefore(String.valueOf(id), ' ', idLen),
                        StrUtil.fillBefore(String.valueOf(version), ' ', versionLen),
                        DateUtils.toYMDHMS(articleDetail.getUpdTime()),
                        article.getN()
                );

                backLogs.add(backArticleLog);
                index++;
            }
            backLogs.add("============================== ↑↑ 文本文章列表 ↑↑ ==============================");

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
            Date today = DateUtils.date();
            Date bakDate = DateUtils.parse(backupFile.getDate(), DatePattern.PURE_DATE_FORMAT);
            long gap = DateUtils.betweenDay(bakDate, today, true);
            if (gap > expireDay) {
                String logStr = String.format("[文章备份] 备份文件: %s 已过期, 即将删除", backupFile.getFilename());
                backLog.add(logStr);
                log.info(logStr);
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

    private List<DocTreeRes> getArticles(Long userId) {
        DocTreeReq req = new DocTreeReq();
        req.setUserId(userId);
        List<DocTreeRes> docs = docService.listTree(req);
        List<DocTreeRes> articles = new ArrayList<>();
        findArticle("", docs, articles);
        return articles;
    }

    private void findArticle(String prefix, List<DocTreeRes> docs, List<DocTreeRes> articles) {
        if (CollUtil.isEmpty(docs)) {
            return;
        }
        for (DocTreeRes doc : docs) {
            doc.setN(prefix + "/" + doc.getN());
            if (CollUtil.isNotEmpty(doc.getChildren())) {
                findArticle(doc.getN(), doc.getChildren(), articles);
            }
            if (doc.getTy().equals(DocTypeEnum.A.getType())) {
                articles.add(doc);
            }
        }
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
        private String userId;
        private String date;
        private String time;

        private String filename;
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
        public BackupFile(Long userId) {
            String filename = String.format("B_%s_%s", userId, DateUtils.toYMDHMS_SSS(System.currentTimeMillis()));
            filename = filename.replaceAll(" ", SEPARATOR)
                    .replaceAll("-", "")
                    .replaceAll(":", "")
                    .replaceAll("\\.", SEPARATOR);
            build(filename);
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
        }

        /**
         * 获取备份文件的路径, 由备份路径 + 本次备份名称构成
         */
        public String getRootPath() {
            return this.path + "/" + this.filename;
        }

    }
}
