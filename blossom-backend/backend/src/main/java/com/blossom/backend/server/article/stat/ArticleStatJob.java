package com.blossom.backend.server.article.stat;

import cn.hutool.core.collection.CollUtil;
import com.blossom.backend.base.user.UserService;
import com.blossom.backend.base.user.pojo.UserEntity;
import com.blossom.backend.server.article.draft.pojo.ArticleStatRes;
import com.blossom.common.base.util.DateUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 文章统计任务
 *
 * @author xzzz
 */
@Slf4j
@Component
@AllArgsConstructor
public class ArticleStatJob {

    private final ArticleStatService statService;
    private final UserService userService;

    /**
     * 每10分钟刷新
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void update() {
        log.debug("[BLOSSOM] 刷新文章统计");
        // 修改本日编辑文章数
        String toDay = DateUtils.today();
        String toDayBegin = toDay + " 00:00:00";
        String toDayEnd = toDay + " 23:59:59";

        List<UserEntity> users = userService.listAll();
        if (CollUtil.isEmpty(users)) {
            return;
        }

        for (UserEntity user : users) {
            ArticleStatRes statCount = statService.statUpdArticleCount(toDayBegin, toDayEnd, user.getId());
            statService.updByDate(ArticleStatTypeEnum.ARTICLE_HEATMAP, toDay, statCount.getArticleCount(), user.getId());

            String toMouth = DateUtils.format(DateUtils.beginOfMonth(DateUtils.date()), DateUtils.PATTERN_YYYYMMDD);
            ArticleStatRes statWord = statService.statCount(null, null, user.getId());
            statService.updByDate(ArticleStatTypeEnum.ARTICLE_WORDS, toMouth, statWord.getArticleWords(), user.getId());
        }
    }
}
