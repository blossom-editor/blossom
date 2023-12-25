package com.blossom.backend.base.search.message.consumer;

import com.blossom.backend.base.search.SearchProperties;
import com.blossom.backend.base.search.message.IndexMsg;
import com.blossom.backend.base.search.message.IndexMsgTypeEnum;
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
import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class BatchIndexMsgConsumer {

    private SearchProperties searchProperties;

    BatchIndexMsgConsumer(SearchProperties searchProperties) {
        this.searchProperties = searchProperties;
        if (!StringUtils.hasText(searchProperties.getPath())) {
            log.info("未配置索引库地址, 关闭全文搜索功能支持");
        }
    }

    public void batchReload(List<IndexMsg> list) throws IOException {
        try (Directory directory = FSDirectory.open(new File(searchProperties.getPath()).toPath());
             IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig(new IKAnalyzer()));
        ){
            for (IndexMsg indexMsg : list){
                if (IndexMsgTypeEnum.ADD.equals(indexMsg.getType())) {
                    // 插入 or 更新索引
                    // 打开索引库
                    Document document = indexMsg.getData();
                    String id = document.get("id");
                    indexWriter.updateDocument(new Term("id", id), document);
                } else if (IndexMsgTypeEnum.DELETE.equals(indexMsg.getType())) {
                    // 删除索引
                        Document document = indexMsg.getData();
                        String id = document.get("id");
                        indexWriter.deleteDocuments(new Term("id", id));
                }
            }
            // 完成
            indexWriter.flush();
            indexWriter.commit();
        }
    }
}

