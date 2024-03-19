package com.blossom.backend.server.article.backup.pojo;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.blossom.backend.server.article.backup.BackupTypeEnum;
import com.blossom.common.base.enums.YesNo;
import com.blossom.common.base.util.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
public class BackupFile {

    private static final String SEPARATOR = "_";
    private static Set<String> prefixs = new HashSet<String>() {{
        this.add("BML");
        this.add("BMN");
        this.add("BHL");
        this.add("BHN");
    }};

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

    public BackupFile() {
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

    public void build(String filename) {
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
     * 检查文件格式
     */
    public boolean checkPrefix() {
        String[] tags = filename.split(SEPARATOR);
        if (tags.length != 5) {
            return false;
        }
        String prefix = tags[0];
        // 不是固定文件前缀则失败
        if (!prefixs.contains(prefix)) {
            return false;
        }
        return true;
    }

    /**
     * 获取备份文件的路径, 由备份路径 + 本次备份名称构成
     */
    public String getRootPath() {
        return this.path + "/" + this.filename;
    }

}
