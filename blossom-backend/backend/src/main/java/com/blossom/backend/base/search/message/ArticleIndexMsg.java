package com.blossom.backend.base.search.message;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

/**
 * 文章索引消息的实现
 */
@Getter
public class ArticleIndexMsg implements IndexMsg {

    private final IndexMsgTypeEnum type;

    private final Long id;

    private Document doc;

    private final Long userId;

    public ArticleIndexMsg(IndexMsgTypeEnum indexMsgType, Long id, Long userId) {
        this.type = indexMsgType;
        this.id = id;
        this.userId = userId;
    }

    /**
     * 创建文章索引消息
     *
     * @param indexMsgType 操作类型
     * @param id           唯一ID
     * @param name         标题
     * @param tags         标签
     * @param markdown     正文内容
     * @param userId       用户ID
     */
    public ArticleIndexMsg(IndexMsgTypeEnum indexMsgType, Long id, String name, String tags, String markdown, Long userId) {
        this.type = indexMsgType;
        this.id = id;
        this.userId = userId;
        Document document = new Document();
        // 存储文章的id, markdown
        document.add(new StringField("id", Convert.toStr(id), Field.Store.YES));
        if (StrUtil.isNotBlank(name)) {
            document.add(new TextField("name", name, Field.Store.YES));
        }
        if (StrUtil.isNotBlank(markdown)) {
            document.add(new TextField("markdown", markdown, Field.Store.YES));
        }
        if (StrUtil.isNotBlank(tags)) {
            document.add(new TextField("tags", tags, Field.Store.YES));
        }
        this.doc = document;
    }
}
