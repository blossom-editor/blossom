package com.blossom.backend.server.article.backup;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.blossom.backend.base.auth.AuthContext;
import com.blossom.backend.base.param.ParamEnum;
import com.blossom.backend.base.param.ParamService;
import com.blossom.backend.base.param.pojo.ParamEntity;
import com.blossom.backend.server.utils.DownloadUtil;
import com.blossom.common.base.enums.YesNo;
import com.blossom.common.base.exception.XzException404;
import com.blossom.common.base.exception.XzException500;
import com.blossom.common.base.pojo.R;
import com.blossom.common.base.util.SortUtil;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文章 [Article]
 *
 * @author xzzz
 */
@RestController
@AllArgsConstructor
@RequestMapping("/article/backup")
public class ArticleBackupController {

    private final ParamService paramService;
    private final ArticleBackupService backupService;

    /**
     * 执行备份
     *
     * @param type    导出类型 MARKDOWN/HTML
     * @param toLocal 是否导出为本地图片映射 YES/NO
     */
    @GetMapping
    public R<ArticleBackupService.BackupFile> backup(
            @RequestParam("type") String type,
            @RequestParam("toLocal") String toLocal,
            @RequestParam(value = "articleId", required = false) Long articleId) {
        BackupTypeEnum typeEnum;
        if (StrUtil.isBlank(type)) {
            typeEnum = BackupTypeEnum.MARKDOWN;
        } else {
            typeEnum = BackupTypeEnum.valueOf(type.toUpperCase());
        }

        YesNo toLocalEnum;
        if (StrUtil.isBlank(toLocal)) {
            toLocalEnum = YesNo.NO;
        } else {
            toLocalEnum = YesNo.valueOf(toLocal.toUpperCase());
        }

        return R.ok(backupService.backup(AuthContext.getUserId(), typeEnum, toLocalEnum, articleId));
    }

    /**
     * 备份记录
     */
    @GetMapping("/list")
    public R<List<ArticleBackupService.BackupFile>> list() {
        return R.ok(backupService.listAll(AuthContext.getUserId())
                .stream()
                .sorted((b1, b2) -> SortUtil.dateSort.compare(b1.getDatetime(), b2.getDatetime()))
                .collect(Collectors.toList())
        );
    }

    /**
     * 下载压缩包
     *
     * @param filename 文件名称
     */
    @GetMapping("/download")
    public void download(@RequestParam("filename") String filename, HttpServletResponse response) {
        final ParamEntity param = paramService.getValue(ParamEnum.BACKUP_PATH);
        XzException500.throwBy(ObjUtil.isNull(param), ArticleBackupService.ERROR_MSG);
        final String rootPath = param.getParamValue();
        XzException500.throwBy(StrUtil.isBlank(rootPath), ArticleBackupService.ERROR_MSG);
        File file = new File(rootPath + "/" + filename);
        XzException404.throwBy(!file.exists(), "备份文件[" + filename + "]不存在");

        try (InputStream is = FileUtil.getInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(is)) {
            DownloadUtil.forceDownload(response, bis, filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
