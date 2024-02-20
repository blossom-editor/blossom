package com.blossom.backend.server.article.interact.pojo;

import lombok.Getter;

/**
 * 互动相关
 *
 * @author xingxing
 */
@Getter
public enum LikeActionType {
    Like("Like"),
    Dislike("Dislike"),
    CancelLike("CancelLike"),
    CancelDislike("CancelDislike"),
    LikeToDislike("LikeToDislike"),
    DislikeToLike("DislikeToLike");

    // 获取枚举值的方法
    private final String action;

    // 构造函数
    LikeActionType(String action) {
        this.action = action;
    }

    // 根据字符串值查找枚举值的静态方法
    public static LikeActionType fromString(String text) {
        for (LikeActionType b : LikeActionType.values()) {
            if (b.action.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}

