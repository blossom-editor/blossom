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
    /**
     * 更多链接 JSON
     */
    WEB_BLOG_LINKS(false, 0, ""),
    /**
     * 博客端专题特殊形式
     * 0:否;1:是
     *
     * @since 1.13.0
     */
    WEB_BLOG_SUBJECT_TITLE(false, 0, "0"),
    /**
     * 是否在文章内容的顶部显示文章的标题
     * 0:否;1:是
     *
     * @since 1.15.0
     */
    WEB_BLOG_SHOW_ARTICLE_NAME(false, 0, "1"),
    /**
     * 博客主题色
     *
     * @since 1.15.0
     */
    WEB_BLOG_COLOR(false, 0, "rgb(104, 104, 104)"),
    // ----------< 博客水印 >----------
    /**
     * 启用博客水印
     * 0:否;1:是
     *
     * @since 1.15.0
     */
    WEB_BLOG_WATERMARK_ENABLED(false, 0, "0"),
    /**
     * 水印内容
     *
     * @since 1.15.0
     */
    WEB_BLOG_WATERMARK_CONTENT(false, 0, ""),
    /**
     * 水印字体大小
     *
     * @since 1.15.0
     */
    WEB_BLOG_WATERMARK_FONTSIZE(false, 0, "15"),
    /**
     * 水印颜色
     *
     * @since 1.15.0
     */
    WEB_BLOG_WATERMARK_COLOR(false, 0, "rgba(157, 157, 157, 0.2)"),
    /**
     * 水印密集度
     *
     * @since 1.15.0
     */
    WEB_BLOG_WATERMARK_GAP(false, 0, "100"),
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
