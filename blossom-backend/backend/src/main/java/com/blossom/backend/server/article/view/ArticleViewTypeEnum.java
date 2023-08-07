package com.blossom.backend.server.article.view;

import lombok.Getter;

/**
 * 文章记录的类型
 *
 * @author xzzz
 */
public enum ArticleViewTypeEnum {

    /**
     * UV, 每个IP,每天,每个文章唯一
     */
    UV(1),
    /**
     * 点赞, 每个IP,每个文章唯一
     */
    LIKE(2);

    @Getter
    private Integer type;

    ArticleViewTypeEnum(Integer type) {
        this.type = type;
    }
}
