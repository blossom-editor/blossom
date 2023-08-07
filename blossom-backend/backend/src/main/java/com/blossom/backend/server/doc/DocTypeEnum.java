package com.blossom.backend.server.doc;

import lombok.Getter;

/**
 * 文档的类型
 *
 * @author xzzz
 */
public enum DocTypeEnum {

    /**
     * folder article 文章文件夹
     */
    FA(1),

    /**
     * folder picture 图片文件夹
     */
    FP(2),

    /**
     * article 文章
     */
    A(3);

    @Getter
    private final Integer type;

    DocTypeEnum(Integer type) {
        this.type = type;
    }
}
