package com.blossom.backend.base.search;

import com.blossom.backend.base.search.message.ArticleIndexMsg;
import com.blossom.backend.base.search.message.IndexMsg;
import com.blossom.backend.base.search.message.IndexMsgTypeEnum;
import com.blossom.backend.base.search.message.consumer.BatchIndexMsgConsumer;
import com.blossom.backend.server.article.draft.ArticleService;
import com.blossom.backend.server.article.draft.pojo.ArticleEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 对既有索引进行监控与维护
 */

@Component
@Slf4j
public class IndexObserver {

    private SearchProperties searchProperties;
    private ArticleService articleService;
    private BatchIndexMsgConsumer batchIndexMsgConsumer;


    IndexObserver(SearchProperties searchProperties, ArticleService articleService, BatchIndexMsgConsumer batchIndexMsgConsumer){
        this.searchProperties = searchProperties;
        this.articleService = articleService;
        this.batchIndexMsgConsumer = batchIndexMsgConsumer;
    }

    /**
     * 进行索引的维护
     */
    @Scheduled(cron = "0 0 04 * * ?")
    public void reloadIndex() throws IOException {
        if (StringUtils.hasText(searchProperties.getPath())){
            List<ArticleEntity> allArticleWithContent = articleService.listAllArticleWithContent();
            List<IndexMsg> batchReloadMsgs = new ArrayList<>();
            allArticleWithContent.forEach(article ->{
                ArticleIndexMsg articleIndexMsg = new ArticleIndexMsg(IndexMsgTypeEnum.ADD,article.getId(),article.getName(),article.getTags(),article.getMarkdown(),article.getUserId());
                batchReloadMsgs.add(articleIndexMsg);
            });

            batchIndexMsgConsumer.batchReload(batchReloadMsgs);
        }
    }


}
