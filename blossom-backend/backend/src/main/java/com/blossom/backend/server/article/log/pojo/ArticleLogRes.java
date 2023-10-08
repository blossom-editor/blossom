package com.blossom.backend.server.article.log.pojo;

import lombok.Data;

import java.util.Date;

/**
 * 文章记录
 */
@Data
public class ArticleLogRes {
    /**
     * ID
     */
    private Long i;
    /**
     * 日期
     */
    private Date dt;
}
