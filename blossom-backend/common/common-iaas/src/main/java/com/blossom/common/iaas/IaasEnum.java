package com.blossom.common.iaas;

import lombok.Getter;

@Getter
@SuppressWarnings("all")
public enum IaasEnum {

    ALIBABA("alibaba"),
    HUAWEI("huawei"),
    TENCENT("tencent"),
    QINIU("qiniu"),
    BLOSSOM("blossom"),
    MINIO("minio");

    private String type;

    IaasEnum(String type) {
        this.type = type;
    }
}
