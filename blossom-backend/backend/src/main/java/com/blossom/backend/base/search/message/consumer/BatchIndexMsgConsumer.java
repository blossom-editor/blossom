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
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        // 需要对所有用户的索引进行维护，减少文件打开次数, 优先进行分组
        Map<Long, List<IndexMsg>> userGroupMsgMap = list.stream().collect(Collectors.groupingBy(IndexMsg::getCurrentUserId));
        // 遍历map， 逐个用户进行索引重建
        for (Map.Entry<Long, List<IndexMsg>> entity : userGroupMsgMap.entrySet()){
            Long userId = entity.getKey();
            List<IndexMsg> msgList = entity.getValue();
            if (userId == null){
                continue;
            }
            try (Directory directory = FSDirectory.open(searchProperties.getUserIndexDirectory(userId));
                 IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig(new StandardAnalyzer()));
            ){
                for (IndexMsg indexMsg : msgList){
                    if (IndexMsgTypeEnum.ADD.equals(indexMsg.getType())) {
                        // 插入 or 更新索引
                        // 打开索引库 --->通过getDoc方法获取索引文档
                        Document document = indexMsg.getDoc();
                        String id = document.get("id");
                        indexWriter.updateDocument(new Term("id", id), document);
                    } else if (IndexMsgTypeEnum.DELETE.equals(indexMsg.getType())) {
                        // 删除索引
                        Long id = indexMsg.getId();
                        indexWriter.deleteDocuments(new Term("id", Convert.toStr(id)));
                    }
                }
                // 完成
                indexWriter.flush();
                indexWriter.commit();
            }

        }

    }
}

