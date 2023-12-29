package com.blossom.backend.base.search.message.consumer;

import cn.hutool.core.convert.Convert;
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
import org.springframework.util.StringUtils;

import java.util.concurrent.Executors;

/**
 * 索引消息的消费者
 *
 * @author Andecheal
 */
@Component
@Slf4j
public class IndexMsgConsumer {

    private SearchProperties searchProperties;

    private ArticleService articleService;

    IndexMsgConsumer(SearchProperties searchProperties, ArticleService articleService) {
        this.searchProperties = searchProperties;
        this.articleService = articleService;
        if (!StringUtils.hasText(searchProperties.getPath())) {
            log.info("未配置索引库地址, 关闭全文搜索功能支持");
            return;
        }
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        IndexMsg indexMsg = IndexMsgQueue.take();
                        // 首先获取消息中的userId， 根据userId 打开对应的索引库
                        Long userId = indexMsg.getCurrentUserId();
                        if (userId == null){
                            // 记录异常并继续消费
                            log.error("消费异常. 获取用户id为空");
                            continue;
                        }
                        if (IndexMsgTypeEnum.ADD.equals(indexMsg.getType())) {
                            // 插入 or 更新索引
                            // 打开索引库
                            try (Directory directory = FSDirectory.open(searchProperties.getUserIndexDirectory(userId));
                                 IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig(new StandardAnalyzer()));
                            ) {
                                // 查询doc数据 ---> 避免service部分功能未查询全部字段数据导致的索引field丢失
                                Long id = indexMsg.getId();
                                ArticleEntity article = articleService.getById(id);
                                Document document = new Document();
                                String title = article.getName();
                                String markdownContent = article.getMarkdown();
                                String tags = article.getTags();
                                // 存储文章的id, content
                                document.add(new StringField("id", Convert.toStr(id), Field.Store.YES));
                                if (StringUtils.hasText(title)){
                                    document.add(new TextField("title", article.getName(), Field.Store.YES));
                                }
                                if (StringUtils.hasText(markdownContent)){
                                    document.add(new TextField("content", markdownContent, Field.Store.YES));
                                }
                                if (StringUtils.hasText(tags)){
                                    document.add(new TextField("tags", tags, Field.Store.YES));
                                }

                                indexWriter.updateDocument(new Term("id", Convert.toStr(id)), document);
                                indexWriter.flush();
                                indexWriter.commit();
                            }
                        } else if (IndexMsgTypeEnum.DELETE.equals(indexMsg.getType())) {
                            // 删除索引
                            try (Directory directory = FSDirectory.open(searchProperties.getUserIndexDirectory(userId));
                                 IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig(new StandardAnalyzer()));

                            ) {
                                Long id = indexMsg.getId();
                                indexWriter.deleteDocuments(new Term("id", Convert.toStr(id)));
                                indexWriter.flush();
                                indexWriter.commit();
                            }
                        }

                    } catch (Exception e) {
                        log.error("消费失败" + e.getMessage());
                    }
                }
            }
        });
    }

}
