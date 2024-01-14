package com.blossom.backend.server.article.reference;

import lombok.Getter;

public enum  ArticleReferenceEnum {
    /**
     * 图片
     */
    FILE(10),
    /**
     * 内部文章
     */
    INNER(11),
    /**
     * 未知内部文章
     */
    INNER_UNKNOWN(12),

    /**
     * 外部文章
     */
    OUTSIDE(21);

    @Getter
    private final Integer type;

    ArticleReferenceEnum(Integer type) {
        this.type = type;
    }
}
