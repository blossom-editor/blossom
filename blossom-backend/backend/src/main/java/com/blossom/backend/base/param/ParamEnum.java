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
    WEB_ARTICLE_URL(false, 0,""),

    /**
     * 文章日志过期天数
     */
    ARTICLE_LOG_EXP_DAYS(false, 0,""),

    /**
     * 文章回收站过期天数
     */
    ARTICLE_RECYCLE_EXP_DAYS(false, 0,""),

    /**
     * 和风天气KEY
     */
    HEFENG_KEY(true, 20,""),

    /**
     * GITEE key
     */
    GITEE_ACCESS_TOKEN(true, 20,""),

    /**
     * 备份路径
     */
    BACKUP_PATH(false, 0,""),

    /**
     * 备份过期天数
     */
    BACKUP_EXP_DAYS(false, 0,""),

    /**
     * BLOSSOM 对象存储地址
     */
    BLOSSOM_OBJECT_STORAGE_DOMAIN(false, 0,"http://www.google.com/"),

    /**
     * 服务器JWT加密字符串
     */
    SERVER_JWT_SECRET(true, 9999,""),

    /**
     * 过期时间 - 服务器
     */
    SERVER_MACHINE_EXPIRE(false, 0,""),

    /**
     * 过期时间 - 域名
     */
    SERVER_DOMAIN_EXPIRE(false, 0,""),

    /**
     * 过期时间 - HTTPS 证书
     */
    SERVER_HTTPS_EXPIRE(false, 0,""),

    /**
     * 过期时间 - 数据库
     */
    SERVER_DATABASE_EXPIRE(false, 0,""),
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

    /**
     * 默认值
     */
    @Getter
    private final String defaultValue;

    ParamEnum(Boolean masking, Integer maskingLength, String defaultValue) {
        this.masking = masking;
        this.maskingLength = maskingLength;
        this.defaultValue = defaultValue;
    }
}
