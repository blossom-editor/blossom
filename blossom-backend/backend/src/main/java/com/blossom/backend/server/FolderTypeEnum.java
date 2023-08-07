package com.blossom.backend.server;

import lombok.Getter;

/**
 * 文件夹类型, 不同的文件夹具有不同的字段要素, 例如图片文件夹不需要公开
 *
 * @author xzzz
 */
public enum FolderTypeEnum {
    /**
     * 文章文件夹
     */
    ARTICLE(1),

    /**
     * 图片文件夹
     */
    PICTURE(2);

    @Getter
    private final Integer type;

    FolderTypeEnum(Integer type) {
        this.type = type;
    }
}
