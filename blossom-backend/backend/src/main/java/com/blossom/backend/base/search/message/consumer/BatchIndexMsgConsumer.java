package com.blossom.backend.base.search.message.consumer;

import cn.hutool.core.convert.Convert;
import com.blossom.backend.base.search.SearchProperties;
import com.blossom.backend.base.search.message.IndexMsg;
import com.blossom.backend.base.search.message.IndexMsgTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 批量构建索引
 */
@Slf4j
@Component
public class BatchIndexMsgConsumer {

    private final SearchProperties searchProperties;

    BatchIndexMsgConsumer(SearchProperties searchProperties) {
        this.searchProperties = searchProperties;
    }

    /**
     * 批量构建所有用户的索引
     *
     * @param articles 全部文章
     */
    public void batchReload(List<IndexMsg> articles) throws IOException {
        // 需要对所有用户的索引进行维护, 减少文件打开次数, 优先进行分组
        Map<Long, List<IndexMsg>> userGroupMsgMap = articles.stream().collect(Collectors.groupingBy(IndexMsg::getUserId));
        // 遍历 Map, 逐个用户进行索引重建
        for (Map.Entry<Long, List<IndexMsg>> entity : userGroupMsgMap.entrySet()) {
            Long userId = entity.getKey();
            List<IndexMsg> msgList = entity.getValue();
            if (userId == null) {
                continue;
            }
            try (Directory directory = FSDirectory.open(searchProperties.getUserIndexDirectory(userId));
                 IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig(new StandardAnalyzer()))) {
                for (IndexMsg indexMsg : msgList) {
                    /*
                     * 插入或更新索引, 通过 getDoc 方法获取索引文档
                     */
                    if (IndexMsgTypeEnum.ADD == indexMsg.getType()) {
                        Document document = indexMsg.getDoc();
                        String id = document.get("id");
                        indexWriter.updateDocument(new Term("id", id), document);
                    }
                    /*
                     * 删除索引内容, 批量处理时不会进行删除, 所有的删除都由用户主动操作
                     */
                    else if (IndexMsgTypeEnum.DELETE == indexMsg.getType()) {
                        Long id = indexMsg.getId();
                        indexWriter.deleteDocuments(new Term("id", Convert.toStr(id)));
                    }
                }
                indexWriter.flush();
                indexWriter.commit();
            }
        }
    }
}

