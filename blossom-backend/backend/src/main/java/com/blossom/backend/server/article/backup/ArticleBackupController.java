package com.blossom.backend.server.article.backup;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.blossom.backend.base.auth.AuthContext;
import com.blossom.backend.base.param.ParamEnum;
import com.blossom.backend.base.param.ParamService;
import com.blossom.backend.base.param.pojo.ParamEntity;
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
import java.io.*;
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
     */
    @GetMapping
    public R<ArticleBackupService.BackupFile> backup() {
        return R.ok(backupService.backup(AuthContext.getUserId()));
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
            OutputStream os = response.getOutputStream();
            // 设置强制下载不打开
            response.setContentType("application/force-download");
            // 将请求头暴露给前端
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.addHeader("Content-Disposition", "attachment;filename=" + filename);
            byte[] buffer = new byte[1024];
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bis.read(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
