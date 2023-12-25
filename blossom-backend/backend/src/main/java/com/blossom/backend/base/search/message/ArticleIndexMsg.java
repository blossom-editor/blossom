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

    private Document data;

    public ArticleIndexMsg(IndexMsgTypeEnum indexMsgTypeEnum, Long id, String title) {
        this.type = indexMsgTypeEnum;
        Document document = new Document();
        document.add(new StringField("id", Convert.toStr(id), Field.Store.YES));
        document.add(new StringField("title", title, Field.Store.YES));
        this.data = document;
    }

    public ArticleIndexMsg(IndexMsgTypeEnum indexMsgTypeEnum, Long id) {
        this.type = indexMsgTypeEnum;
        Document document = new Document();
        document.add(new StringField("id", Convert.toStr(id), Field.Store.YES));
        this.data = document;
    }

    public ArticleIndexMsg(IndexMsgTypeEnum indexMsgTypeEnum, Long id, String title, String markdownContent) {
        this.type = indexMsgTypeEnum;
        Document document = new Document();
        // 存储文章的id, content
        document.add(new StringField("id", Convert.toStr(id), Field.Store.YES));
        if (StringUtils.hasText(title)){
            document.add(new TextField("title", title, Field.Store.YES));
        }
        if (StringUtils.hasText(markdownContent)){
            document.add(new TextField("content", markdownContent, Field.Store.YES));
        }
        this.data = document;
    }

    @Override
    public IndexMsgTypeEnum getType() {
        return this.type;
    }

    @Override
    public Document getData() {
        return this.data;
    }
}
