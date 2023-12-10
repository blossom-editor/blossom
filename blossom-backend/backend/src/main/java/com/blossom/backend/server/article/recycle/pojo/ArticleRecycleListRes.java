package com.blossom.backend.server.article.recycle.pojo;

import lombok.Data;

import java.util.Date;

/**
 * 回收站列表
 *
 * @since 1.10.0
 */
@Data
public class ArticleRecycleListRes {
    /**
     * 文章ID
     */
    private Long id;
    /**
     * 文章名称
     */
    private String name;
    /**
     * 删除时间
     */
    private Date delTime;
}
