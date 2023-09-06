package com.blossom.backend.server.article.backup;

import com.blossom.backend.base.user.UserService;
import com.blossom.backend.base.user.pojo.UserEntity;
import com.blossom.common.base.enums.YesNo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class ArticleBackupJob {

    private final UserService userService;
    private final ArticleBackupService backupService;

    /**
     * 每天早7点进行备份
     */
    @Scheduled(cron = "0 0 07 * * ?")
    public void backup() {
        List<UserEntity> users = userService.listAll();
        for (UserEntity user : users) {
            backupService.backup(user.getId(), BackupTypeEnum.MARKDOWN, YesNo.NO,null);
        }
    }
}
