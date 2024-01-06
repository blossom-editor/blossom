package com.blossom.backend.base.search;

import com.blossom.backend.base.search.message.ArticleIndexMsg;
import com.blossom.backend.base.search.message.IndexMsg;
import com.blossom.backend.base.search.message.IndexMsgTypeEnum;
import com.blossom.backend.base.search.message.consumer.BatchIndexMsgConsumer;
import com.blossom.backend.server.article.draft.ArticleService;
import com.blossom.backend.server.article.draft.pojo.ArticleEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 对既有索引进行监控与维护
 */
@Slf4j
@Component
public class IndexObserver {

    private final SearchProperties searchProperties;
    private final ArticleService articleService;
    private final BatchIndexMsgConsumer batchIndexMsgConsumer;

    IndexObserver(SearchProperties searchProperties, ArticleService articleService, BatchIndexMsgConsumer batchIndexMsgConsumer) {
        this.searchProperties = searchProperties;
        this.articleService = articleService;
        this.batchIndexMsgConsumer = batchIndexMsgConsumer;
    }

    /**
     * 启动时维护索引
     */
    @EventListener(ApplicationStartedEvent.class)
    public void refresh() {
        try {
            log.info("[  SEARCH] 重建全部用户索引开始");
            long start = System.currentTimeMillis();
            this.reloadIndex();
            log.info("[  SEARCH] 重建全部用户索引完成, 用时: {} ms", (System.currentTimeMillis() - start));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 进行索引的维护
     */
    @Scheduled(cron = "0 0 04 * * ?")
    public void reloadIndex() throws IOException {
        List<ArticleEntity> allArticleWithContent = articleService.listAllIndexField();
        List<IndexMsg> batchReloadMsgs = new ArrayList<>();
        allArticleWithContent.forEach(article -> {
            ArticleIndexMsg articleIndexMsg = new ArticleIndexMsg(IndexMsgTypeEnum.ADD, article.getId(), article.getName(), article.getTags(), article.getMarkdown(), article.getUserId());
            batchReloadMsgs.add(articleIndexMsg);
        });
        batchIndexMsgConsumer.batchReload(batchReloadMsgs);
    }


}
