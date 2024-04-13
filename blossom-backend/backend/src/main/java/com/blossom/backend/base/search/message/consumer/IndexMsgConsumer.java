package com.blossom.backend.base.search.message.consumer;

import com.blossom.backend.base.search.SearchProperties;
import com.blossom.backend.base.search.message.IndexMsg;
import com.blossom.backend.base.search.message.IndexMsgTypeEnum;
import com.blossom.backend.base.search.queue.IndexMsgQueue;
import com.blossom.backend.server.article.draft.ArticleService;
import com.blossom.backend.server.article.draft.pojo.ArticleEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;

/**
 * 索引消息的消费者
 *
 * @author Andecheal
 */
@Slf4j
@Component
public class IndexMsgConsumer {

    private final SearchProperties searchProperties;

    private final ArticleService articleService;

    /**
     * 单线程处理索引文档消息
     *
     * @param searchProperties 索引配置
     * @param articleService   文章服务
     */
    IndexMsgConsumer(SearchProperties searchProperties, ArticleService articleService) {
        this.searchProperties = searchProperties;
        this.articleService = articleService;
        Executors.newSingleThreadExecutor().submit(() -> {
            while (true) {
                try {
                    IndexMsg indexMsg = IndexMsgQueue.take();
                    final Long userId = indexMsg.getUserId();
                    final Long id = indexMsg.getId();
                    if (userId == null || id == null) {
                        continue;
                    }
                    if (IndexMsgTypeEnum.ADD == indexMsg.getType()) {
                        // 插入 or 更新索引
                        // 打开索引库
                        try (Directory directory = FSDirectory.open(this.searchProperties.getUserIndexDirectory(userId));
                             IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig(new StandardAnalyzer()))) {
                            // 查询最新的消息
                            ArticleEntity article = this.articleService.selectById(id, false, true, false, userId);
                            if (null == article) {
                                continue;
                            }
                            Document document = new Document();
                            document.add(new StringField("id", String.valueOf(id), Field.Store.YES));
                            document.add(new TextField("name", article.getName(), Field.Store.YES));
                            document.add(new TextField("tags", article.getTags(), Field.Store.YES));
                            document.add(new TextField("markdown", article.getMarkdown(), Field.Store.YES));
                            indexWriter.updateDocument(new Term("id", String.valueOf(id)), document);
                            indexWriter.flush();
                            indexWriter.commit();
                        }
                    } else if (IndexMsgTypeEnum.DELETE == indexMsg.getType()) {
                        // 删除索引
                        try (Directory directory = FSDirectory.open(this.searchProperties.getUserIndexDirectory(userId));
                             IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig(new StandardAnalyzer()))) {
                            indexWriter.deleteDocuments(new Term("id", String.valueOf(id)));
                            indexWriter.flush();
                            indexWriter.commit();
                        }
                    }

                } catch (Exception e) {
                    log.error("消费失败" + e.getMessage());
                }
            }
        });
    }

}
