package com.blossom.backend.base.sys.os;

import lombok.Data;

/**
 * 对象存储配置
 *
 * @author xzzz
 */
@Data
public class OSRes {

    /**
     * 对象存储类型
     */
    private String osType;
    /**
     * bucket 名称
     */
    private String bucketName;
    /**
     * 请求域名
     */
    private String domain;
    /**
     * 保存路径
     */
    private String defaultPath;
}
