package com.blossom.backend.server.article.draft.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * 文章统计对象
 *
 * @author xzzz
 */
@Data
public class ArticleStatRes implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文章数
     */
    private Integer articleCount;
    /**
     * 字数
     */
    private Integer articleWords;
}
