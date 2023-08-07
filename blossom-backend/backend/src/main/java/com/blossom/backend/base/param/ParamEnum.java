package com.blossom.backend.base.param;

import lombok.Getter;

/**
 * 参数枚举
 *
 * @author xzzz
 */
public enum ParamEnum {

    /**
     * 文章的 web 端访问路径
     */
    WEB_ARTICLE_URL(false),

    /**
     * 文章日志过期天数
     */
    ARTICLE_LOG_EXP_DAYS(false),

    /**
     * 和风天气KEY
     */
    HEFENG_KEY(true),

    /**
     * GITEE key
     */
    GITEE_ACCESS_TOKEN(true),
    ;

    /**
     * 是否脱敏
     */
    @Getter
    private final Boolean masking;

    ParamEnum(Boolean masking) {
        this.masking = masking;
    }
}
