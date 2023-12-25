package com.blossom.backend.base.search.message;

import org.apache.lucene.document.Document;

/**
 * 索引消息接口
 */
public interface IndexMsg {

    /**
     * 消息类型
     * @return
     */
    IndexMsgTypeEnum getType();

    /**
     * 主键id
     * @return
     */
    Long getId();

    /**
     * 为批量reload提供的提前构造数据的接口，避免多次查询数据库
     * @return
     */
    Document getDoc();

    /**
     * 消息对应用户id
     * @return
     */
    Long getCurrentUserId();

}
