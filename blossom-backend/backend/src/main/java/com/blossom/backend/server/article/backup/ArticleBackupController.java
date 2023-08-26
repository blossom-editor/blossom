package com.blossom.backend.server.article.backup;

import com.blossom.backend.base.auth.AuthContext;
import com.blossom.common.base.pojo.R;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 文章 [Article]
 *
 * @author xzzz
 */
@RestController
@AllArgsConstructor
@RequestMapping("/article/backup")
public class ArticleBackupController {

    private final ArticleBackupService backupService;

    /**
     * 备份
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
        return R.ok(backupService.listAll(AuthContext.getUserId()));
    }
}
