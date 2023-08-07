package com.blossom.backend.server.article.stat;

import lombok.Getter;

/**
 * 文章统计类型
 *
 * @author xzzz
 */
public enum ArticleStatTypeEnum {

    /**
     * 文章每日编辑数, 按热力图显示
     */
    ARTICLE_HEATMAP(1),

    /**
     * 截止当月文章总字数, 按折线图显示
     */
    ARTICLE_WORDS(2);

    @Getter
    private final Integer type;

    ArticleStatTypeEnum(Integer type) {
        this.type = type;
    }
}
