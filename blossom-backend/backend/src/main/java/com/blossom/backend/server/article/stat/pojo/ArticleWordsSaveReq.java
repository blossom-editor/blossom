package com.blossom.backend.server.article.stat.pojo;

import lombok.Data;

import java.util.List;

/**
 * 新增字数请求
 *
 * @since 1.8.0
 */
@Data
public class ArticleWordsSaveReq {

    /**
     * 字数统计集合
     */
    private List<ArticleWordsRes> wordsList;
}
