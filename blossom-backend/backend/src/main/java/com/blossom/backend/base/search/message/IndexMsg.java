package com.blossom.backend.base.search.message;

import org.apache.lucene.document.Document;

/**
 * 索引消息接口
 */
public interface IndexMsg {

    IndexMsgTypeEnum getType();

    Document getData();

}
