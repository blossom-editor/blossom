package com.blossom.backend.server.folder;

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
    ARTICLE(1, "文章文件夹"),

    /**
     * 图片文件夹
     */
    PICTURE(2, "图片文件夹");

    @Getter
    private final Integer type;

    @Getter
    private final String desc;

    FolderTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static FolderTypeEnum getType(Integer type) {
        for (FolderTypeEnum value : FolderTypeEnum.values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return null;
    }

}
