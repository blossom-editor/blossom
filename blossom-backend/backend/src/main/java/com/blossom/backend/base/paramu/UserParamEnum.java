package com.blossom.backend.base.paramu;

import lombok.Getter;

/**
 * 参数枚举
 *
 * @author xzzz
 * @since 1.12.0
 */
public enum UserParamEnum {

    /**
     * 文章的 web 端访问路径
     */
    WEB_ARTICLE_URL(false, 0, "https://www.domain.com/blossom/#/articles?articleId="),
    /**
     * 博客 LOGO 地址
     */
    WEB_LOGO_URL(false, 0, ""),
    /**
     * 博客名称
     */
    WEB_LOGO_NAME(false, 0, ""),
    /**
     * 博客的公网安备号
     */
    WEB_GONG_WANG_AN_BEI(false, 0, ""),
    /**
     * 博客备案号
     */
    WEB_IPC_BEI_AN_HAO(false, 0, ""),
    /**
     * 是否提示博客地址配置有误
     */
    WEB_BLOG_URL_ERROR_TIP_SHOW(false, 0, ""),

    ;

    /**
     * 是否脱敏
     */
    @Getter
    private final Boolean masking;

    /**
     * 脱敏长度
     */
    @Getter
    private final Integer maskingLength;

    @Getter
    private final String defaultValue;

    UserParamEnum(Boolean masking, Integer maskingLength, String defaultValue) {
        this.masking = masking;
        this.maskingLength = maskingLength;
        this.defaultValue = defaultValue;
    }
}
