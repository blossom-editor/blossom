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
    FA(1,"文章文件夹"),
    /**
     * folder picture 图片文件夹
     */
    FP(2,"图片文件夹"),
    /**
     * article 文章
     */
    A(3,"文章");

    @Getter
    private final Integer type;

    @Getter
    private final String desc;

    DocTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
