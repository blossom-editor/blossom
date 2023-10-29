package com.blossom.backend.server.article.stat.pojo;

import lombok.Data;

/**
 * 新增字数响应
 *
 * @since 1.8.0
 */
@Data
public class ArticleWordsRes {
    /**
     * 日期
     */
    private String date;
    /**
     * 统计值
     */
    private Integer value;
}
