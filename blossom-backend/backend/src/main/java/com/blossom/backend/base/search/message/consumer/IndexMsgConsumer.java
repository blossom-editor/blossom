package com.blossom.backend.base.search.message.consumer;

import com.blossom.backend.base.search.SearchProperties;
import com.blossom.backend.base.search.message.IndexMsg;
import com.blossom.backend.base.search.message.IndexMsgTypeEnum;
import com.blossom.backend.base.search.queue.IndexMsgQueue;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
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

    IndexMsgConsumer(SearchProperties searchProperties) {
        this.searchProperties = searchProperties;
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
                        if (IndexMsgTypeEnum.ADD.equals(indexMsg.getType())) {
                            // 插入 or 更新索引
                            // 打开索引库
                            try (Directory directory = FSDirectory.open(new File(searchProperties.getPath()).toPath());
                                 IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig(new IKAnalyzer()));

                            ) {
                                Document document = indexMsg.getData();
                                String id = document.get("id");
                                indexWriter.updateDocument(new Term("id", id), document);
                                indexWriter.flush();
                                indexWriter.commit();
                            }
                        } else if (IndexMsgTypeEnum.DELETE.equals(indexMsg.getType())) {
                            // 删除索引
                            try (Directory directory = FSDirectory.open(new File(searchProperties.getPath()).toPath());
                                 IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig(new IKAnalyzer()));

                            ) {
                                Document document = indexMsg.getData();
                                String id = document.get("id");
                                indexWriter.deleteDocuments(new Term("id", id));
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
