package com.blossom.common.cache.pojo;

import lombok.Data;

/**
 * redis 信息
 *
 * @author xzzz
 */
@Data
public class RedisInfoRes {

    /**
     * redis 键
     */
    private String key;
    /**
     * redis 值
     */
    private String value;
    /**
     * redis 说明
     */
    private String desc;
}
