package com.blossom.backend.base.search.message;

import cn.hutool.core.convert.Convert;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.springframework.util.StringUtils;

/**
 * 文章索引消息的实现
 */

public class ArticleIndexMsg implements IndexMsg {

    private IndexMsgTypeEnum type;

    private Long data;

    private Document document;

    private Long userId;



    public ArticleIndexMsg(IndexMsgTypeEnum indexMsgType, Long id, Long userId) {
        this.type = indexMsgType;
        this.data = id;
        this.userId = userId;
    }

    public ArticleIndexMsg(IndexMsgTypeEnum indexMsgType, Long id, String title, String tags, String content, Long userId){
        this.type = indexMsgType;
        this.data = id;
        this.userId = userId;
        Document document = new Document();
        // 存储文章的id, content
        document.add(new StringField("id", Convert.toStr(id), Field.Store.YES));
        if (StringUtils.hasText(title)){
            document.add(new TextField("title", title, Field.Store.YES));
        }
        if (StringUtils.hasText(content)){
            document.add(new TextField("content", content, Field.Store.YES));
        }
        if (StringUtils.hasText(tags)){
            document.add(new TextField("tags", tags, Field.Store.YES));
        }
        this.document = document;
    }

    @Override
    public IndexMsgTypeEnum getType() {
        return this.type;
    }

    @Override
    public Long getId() {
        return this.data;
    }

    @Override
    public Document getDoc() {
        return this.document;
    }

    @Override
    public Long getCurrentUserId() {
        return this.userId;
    }
}
