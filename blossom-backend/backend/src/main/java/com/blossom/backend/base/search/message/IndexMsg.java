package com.blossom.backend.base.search.message;

import org.apache.lucene.document.Document;

/**
 * 索引消息接口
 */
public interface IndexMsg {

    /**
     * 消息操作类型
     */
    IndexMsgTypeEnum getType();

    /**
     * 主键 ID
     */
    Long getId();

    /**
     * 为批量 reload 提供的提前构造数据的接口, 避免多次查询数据库
     */
    Document getDoc();

    /**
     * 消息对应用户 ID
     */
    Long getUserId();

}
